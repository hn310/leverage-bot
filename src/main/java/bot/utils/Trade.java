package bot.utils;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;

import bot.constant.AccConstant;
import bot.constant.ActionsConstant;
import bot.constant.GMXConstant;
import bot.model.PositionResponse;
import bot.model.TradeHistory;
import bot.model.TradeModel;

public class Trade {
    private static final Logger logger = LogManager.getLogger(Trade.class);

    ApiAction apiAction = new ApiAction();
    SmartContractAction scAction = new SmartContractAction();

    public void startTrade(Web3j web3j, Credentials credentials) throws IOException, InterruptedException, ExecutionException {
        // STEP 0: Get last processed block No
        int lastBlockNo = this.apiAction.readLastBlockNo();

        // STEP 1: Detect IncreasePosition(open) or DecreasePosition(close) by call API
        // actions (compare with block number from step 4)
        List<TradeHistory> tradeHistories = this.apiAction.getGodTradeHistories(lastBlockNo);
        if (tradeHistories.size() == 0) {
            logger.info("No new positions found!");
            return;
        } else {
            logger.info("Number of new positions found: " + tradeHistories.size());
        }

        // loop through each trade history to open a similar one
        for (TradeHistory th : tradeHistories) {
            // STEP 2: Get info about position
            // token used to long/short (BTC, ETH)
            String collateralToken = th.getTradeHistoryData().getParams().getCollateralToken();
            String indexToken = th.getTradeHistoryData().getParams().getIndexToken();
            String collateralDelta = th.getTradeHistoryData().getParams().getCollateralDelta(); // original USD
            String sizeDelta = th.getTradeHistoryData().getParams().getSizeDelta(); // multiplied (after margin) USD
            boolean isLong = th.getTradeHistoryData().getParams().isLong();
            // the USD value of the max (for longs) or min (for shorts) index price
            // acceptable when executing the request
            String acceptablePrice = th.getTradeHistoryData().getParams().getPrice();

            // STEP 3: Create similar position
            TradeModel tradeModel = new TradeModel();
            List<Address> _path = new ArrayList<Address>();
            if (th.getTradeHistoryData().getParams().isLong()) {
                if (th.getTradeHistoryData().getAction().startsWith(ActionsConstant.INCREASE_POSITION)) {
                    // increase long: path[USDC, BTC]
                    _path.add(new Address(GMXConstant.USDC_ADDRESS));
                    _path.add(new Address(indexToken));
                } else {
                    // decrease long: path[BTC]
                    _path.add(new Address(indexToken));
                }
            } else {
                // increase/decrease short: path[USDC] only
                _path.add(new Address(GMXConstant.USDC_ADDRESS));
            }

            Uint256 _collateralDelta = new Uint256(0);
            Uint256 _sizeDelta = new Uint256(0);
            // If increase position: collateralDelta = 10 USD, sizeDelta = calculate
            if (th.getTradeHistoryData().getAction().startsWith(ActionsConstant.INCREASE_POSITION)) {
                _collateralDelta = GMXConstant.AMOUNT_IN;
                _sizeDelta = this.scAction.calculateSizeDelta(collateralDelta, sizeDelta, GMXConstant.AMOUNT_IN);
            }
            // If decrease position: collateralDelta = 0, sizeDelta = getFromSmartContract
            else {
                _collateralDelta = new Uint256(0);
                // TODO change to own private key
                PositionResponse ps = this.scAction.getPosition(web3j, new Address(AccConstant.GOD_KEY),
                        new Address(collateralToken), new Address(indexToken), new Bool(isLong));
                _sizeDelta = ps.getSize();
            }
            tradeModel.setPath(_path);
            tradeModel.setIndexToken(new Address(indexToken));
            tradeModel.setAmountIn(_collateralDelta);
            tradeModel.setSizeDelta(_sizeDelta);
            tradeModel.setAcceptablePrice(new Uint256(new BigInteger(acceptablePrice)));
            tradeModel.setIsLong(new Bool(isLong));
            tradeModel.setMinOut(GMXConstant.MIN_OUT);
            tradeModel.setExecutionFee(GMXConstant.EXECUTION_FEE);
            tradeModel.setReferralCode(GMXConstant.REFERRAL_CODE);
            tradeModel.setCallbackTarget(GMXConstant.CALLBACK_TARGET);
            logger.info(tradeModel.toString());
        }
        // STEP 4: Write newest last block number to file
        lastBlockNo = tradeHistories.get(0).getTradeHistoryData().getBlockNumber(); // get newest last blockNo
        logger.info("STEP 4 (lastBlockNo): " + lastBlockNo);
        // TODO uncomment this
//        this.apiAction.writeLastBlockNo(lastBlockNo); // write latest block number to file
    }
}
