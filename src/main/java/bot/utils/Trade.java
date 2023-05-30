package bot.utils;

import java.io.IOException;
import java.math.BigDecimal;
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
import org.web3j.protocol.exceptions.TransactionException;

import bot.constant.AccConstant;
import bot.constant.ActionsConstant;
import bot.constant.GMXConstant;
import bot.model.ClosePositionRequest;
import bot.model.OpenPositionRequest;
import bot.model.PositionResponse;
import bot.model.TradeHistory;

public class Trade {
    private static final Logger logger = LogManager.getLogger(Trade.class);

    ApiAction apiAction = new ApiAction();
    SmartContractAction scAction = new SmartContractAction();

    public void startTrade(Web3j web3j, Credentials credentials, String godAccount) throws IOException, InterruptedException, ExecutionException {
        // STEP 0: Get last processed block No
        int lastBlockNo = this.apiAction.readLastBlockNo();

        // STEP 1: Detect IncreasePosition(open) or DecreasePosition(close) by call API
        // actions (compare with block number from step 4)
        List<TradeHistory> tradeHistories = this.apiAction.getGodTradeHistories(lastBlockNo, godAccount);
        if (tradeHistories.size() == 0) {
            logger.info("No new positions found!");
            return;
        } else {
            logger.info("Number of new positions found: " + tradeHistories.size());
        }

        // loop through each trade history to open a similar one
        for (TradeHistory th : tradeHistories) {
        	logger.info("god_account: " + th.getTradeHistoryData().getAccount());
            // STEP 2: Get info about position
            // token used to long/short (BTC, ETH)
            String collateralToken = th.getTradeHistoryData().getParams().getCollateralToken();
            String indexToken = th.getTradeHistoryData().getParams().getIndexToken();
            String collateralDelta = th.getTradeHistoryData().getParams().getCollateralDelta(); // original USD
            String sizeDelta = th.getTradeHistoryData().getParams().getSizeDelta(); // multiplied (after margin) USD
            boolean isLong = th.getTradeHistoryData().getParams().isLong();

            // convert current BTC/ETH price to acceptable price
            BigInteger acceptablePrice = calculateAcceptablePrice(th);
            
            // STEP 3: Create similar position
            List<Address> _path = new ArrayList<Address>();
            if (th.getTradeHistoryData().getParams().isLong()) {
                if (th.getTradeHistoryData().getAction().startsWith(ActionsConstant.INCREASE_POSITION)) {
                    // increase long: path[USDC, BTC]
                    _path.add(new Address(GMXConstant.USDC_ADDRESS));
                    _path.add(new Address(indexToken));
                } else {
                    // decrease long: path[BTC, USDC]
                    _path.add(new Address(indexToken));
                    _path.add(new Address(GMXConstant.USDC_ADDRESS));
                }
            } else {
                // increase/decrease short: path[USDC] only
                _path.add(new Address(GMXConstant.USDC_ADDRESS));
            }

            Uint256 _collateralDelta = new Uint256(0);
            Uint256 _sizeDelta = new Uint256(0);
            // If increase position: collateralDelta = 10 USD, sizeDelta = calculate
            if (th.getTradeHistoryData().getAction().startsWith(ActionsConstant.INCREASE_POSITION)) {
            	// TODO fix this in the future, when I'm a whale
				if (godAccount.equals(AccConstant.WHALE_KEY)) {
					_collateralDelta = GMXConstant.AMOUNT_IN;
				} else if (godAccount.equals(AccConstant.DOLPHIN_KEY)) {
					// collateralDelta: x*10^30, amountIn (USDC) = x*10^6 => amountIn = collateralDelta/10^24
//					_collateralDelta = new Uint256(new BigInteger(collateralDelta).divide(new BigInteger("10").pow(24).multiply(new BigInteger("100"))));
					_collateralDelta = GMXConstant.AMOUNT_IN;
				}
                _sizeDelta = this.scAction.calculateSizeDelta(collateralDelta, sizeDelta, GMXConstant.AMOUNT_IN);
            }
            // If decrease position: collateralDelta = 0, sizeDelta = getFromSmartContract
            else {
                _collateralDelta = new Uint256(0);
                PositionResponse ps = this.scAction.getPosition(web3j, new Address(credentials.getAddress()),
                        new Address(collateralToken), new Address(indexToken), new Bool(isLong));
                _sizeDelta = ps.getSize();
            }
            
            if (th.getTradeHistoryData().getAction().startsWith(ActionsConstant.INCREASE_POSITION)) {
                OpenPositionRequest openPositionRequest = new OpenPositionRequest();
                openPositionRequest.setPath(_path);
                openPositionRequest.setIndexToken(new Address(indexToken));
                openPositionRequest.setAmountIn(_collateralDelta);
                openPositionRequest.setSizeDelta(_sizeDelta);
                openPositionRequest.setAcceptablePrice(new Uint256(acceptablePrice));
                openPositionRequest.setIsLong(new Bool(isLong));
                openPositionRequest.setMinOut(GMXConstant.MIN_OUT);
                openPositionRequest.setExecutionFee(new SmartContractAction().getMinExecutionFee(web3j));
                openPositionRequest.setReferralCode(GMXConstant.REFERRAL_CODE);
                openPositionRequest.setCallbackTarget(GMXConstant.CALLBACK_TARGET);
                logger.info("price: " + th.getTradeHistoryData().getParams().getPrice() + " | open position: " + openPositionRequest.toString());
                
                // create similar trade
                // TODO uncomment this after test
                scAction.createIncreasePosition(web3j, credentials, openPositionRequest);
            } else {
                ClosePositionRequest closePositionRequest = new ClosePositionRequest();
                closePositionRequest.setPath(_path);
                closePositionRequest.setIndexToken(new Address(indexToken));
                closePositionRequest.setCollateralDelta(_collateralDelta);
                closePositionRequest.setSizeDelta(_sizeDelta);
                closePositionRequest.setIsLong(new Bool(isLong));
                closePositionRequest.setReceiver(new Address(credentials.getAddress()));
                closePositionRequest.setAcceptablePrice(new Uint256(acceptablePrice));
                closePositionRequest.setMinOut(GMXConstant.MIN_OUT);
                closePositionRequest.setExecutionFee(new SmartContractAction().getMinExecutionFee(web3j));
                closePositionRequest.setWithdrawETH(new Bool(false));
                closePositionRequest.setCallbackTarget(GMXConstant.CALLBACK_TARGET);
                logger.info("price: " + th.getTradeHistoryData().getParams().getPrice() + " | close position: " + closePositionRequest.toString());
                
				// create similar trade
                // TODO uncomment this after test
				scAction.createDecreasePosition(web3j, credentials, closePositionRequest);
            }
        }
        // STEP 4: Write newest last block number to file
        lastBlockNo = tradeHistories.get(0).getTradeHistoryData().getBlockNumber(); // get newest last blockNo
        logger.info("STEP 4 (lastBlockNo): " + lastBlockNo);
        // TODO uncomment this
        this.apiAction.writeLastBlockNo(lastBlockNo); // write latest block number to file
    }
    
	private BigInteger calculateAcceptablePrice(TradeHistory th) {
		BigInteger acceptablePrice = new BigInteger("0");
		BigInteger price = new BigInteger(th.getTradeHistoryData().getParams().getPrice());
		if (th.getTradeHistoryData().getParams().isLong()) {
			if (th.getTradeHistoryData().getAction().startsWith(ActionsConstant.INCREASE_POSITION)) {
				// Increase long: acceptablePrice = price * 1.005 (Acceptable Price < 1,840.41)
				double tmp = price.doubleValue() * GMXConstant.SLIPPAGE;
				acceptablePrice = BigDecimal.valueOf(tmp).toBigInteger();
			} else {
				// Decrease long: acceptablePrice = price / 1.005 (Acceptable Price > 1,860.48)
				double tmp = price.doubleValue() / GMXConstant.SLIPPAGE;
				acceptablePrice = BigDecimal.valueOf(tmp).toBigInteger();
			}
		} else {
			if (th.getTradeHistoryData().getAction().startsWith(ActionsConstant.INCREASE_POSITION)) {
				// Increase short: acceptablePrice = price / 1.005 (Acceptable Price >1,860.48)
				double tmp = price.doubleValue() / GMXConstant.SLIPPAGE;
				acceptablePrice = BigDecimal.valueOf(tmp).toBigInteger();
			} else {
				// Decrease short: acceptablePrice = price * 1.005 (Acceptable Price < 1,840.41)
				double tmp = price.doubleValue() * GMXConstant.SLIPPAGE;
				acceptablePrice = BigDecimal.valueOf(tmp).toBigInteger();
			}
		}
		return acceptablePrice;
	}
	
	public boolean isAnyPositionInDanger(Web3j web3j, Credentials credentials) throws IOException, TransactionException, InterruptedException, ExecutionException {
		List<PositionResponse> psList = scAction.getPositions(web3j, credentials);
		for (PositionResponse ps: psList) {
			// check if hasProfit = 0 (in loss)
			if (ps.getHasProfitInGetPositions().getValue().intValue() == 0) {
				System.out.println(ps.getSize().getValue());
			}
		}
		return false;
	}
}
