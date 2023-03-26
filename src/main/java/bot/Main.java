package bot;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;

import bot.constant.AccConstant;
import bot.constant.RPCConstant;
import bot.model.TradeHistory;
import bot.utils.AccUtils;

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
        
        
//        SmartContractUtils.getPositions(web3j);
        List<TradeHistory> tradeHistories = AccUtils.getGodTradeHistories(73655821);
        System.out.println(tradeHistories.size());
        System.out.println(tradeHistories.get(0).getTradeHistoryData().getTxhash());
        
        logger.info(tradeHistories.get(0).getTradeHistoryData().getTxhash());
        
        //TODO get position: check xem user dang open bao nhieu position, khi close cung can call de biet rut ra bao nhieu tien
        // khi close hoan toan -> collateralDelta = 0
        
        // STEP 0: Get last processed block No
        
        // STEP 1: Detect IncreasePosition(open) or DecreasePosition(close) by call API actions (compare with block number from step 4)
        
        // STEP 2: Get info about position
        
        // STEP 3: Create similar position
        
        // STEP 4: Write latest block number to file
        
    }
}
