package bot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.web3j.abi.datatypes.Address;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;

import bot.constant.AccConstant;
import bot.constant.GMXConstant;
import bot.constant.RPCConstant;
import bot.model.TradeHistory;
import bot.utils.ApiAction;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    
    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException, TransactionException {
        
        // Connect to Ethereum client using web3j
        Web3j web3j = Web3j.build(new HttpService(RPCConstant.ARBITRUM_ONE_RPC));

        // Load the credentials for the sender account
        Credentials credentials = Credentials.create(AccConstant.PRIVATE_KEY);

//        double balanceInEth = AccUtils.getBalanceInEth(web3j);
//        System.out.println(balanceInEth);
//        SmartContractUtils.getGLPPrice(web3j);
        
        ApiAction apiAction = new ApiAction();
        
//        SmartContractUtils.getPositions(web3j);

        //TODO get position: check xem user dang open bao nhieu position, khi close cung can call de biet rut ra bao nhieu tien
        // khi close hoan toan -> collateralDelta = 0 -> lam ntn de lay duoc sizeDelta
        
        // STEP 0: Get last processed block No
        int lastBlockNo = apiAction.readLastBlockNo();
        logger.info("STEP 0 (lastBlockNo): " + lastBlockNo);
        
        // STEP 1: Detect IncreasePosition(open) or DecreasePosition(close) by call API actions (compare with block number from step 4)
        List<TradeHistory> tradeHistories = apiAction.getGodTradeHistories(lastBlockNo);
        if (tradeHistories.size() == 0) {
            logger.info("No new positions found!");
            return;
        } else {
            logger.info("Number of new positions found: " + tradeHistories.size());
        }
        // START LOOP
        for (TradeHistory th : tradeHistories) {
            // STEP 2: Get info about position
            String indexToken = th.getTradeHistoryData().getParams().getIndexToken(); // token used to long/short (BTC, ETH)
            String collateralDelta = th.getTradeHistoryData().getParams().getCollateralDelta(); // original USD
            String sizeDelta = th.getTradeHistoryData().getParams().getSizeDelta(); // multiplied (after margin) USD
            boolean isLong = th.getTradeHistoryData().getParams().isLong();
            String price = th.getTradeHistoryData().getParams().getPrice(); // price of BTC/ETH to open/close position
            int feeBasisPoints = 10; // hard-coded, 0.1% fee
            
            // STEP 3: Create similar position
            List<Address> collateralTokens = new ArrayList<Address>();
            collateralTokens.add(new Address(GMXConstant.USDC_ADDRESS)); // use USDC to trade
            collateralTokens.add(new Address(indexToken));
        }
        // END LOOP
        // STEP 4: Write newest last block number to file
        lastBlockNo = tradeHistories.get(0).getTradeHistoryData().getBlockNumber(); // get newest last blockNo
        logger.info("STEP 4 (lastBlockNo): " + lastBlockNo);
//        apiAction.writeLastBlockNo(lastBlockNo); // write latest block number to file
    }
}
