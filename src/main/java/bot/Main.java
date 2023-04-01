package bot;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;

import bot.constant.AccConstant;
import bot.constant.RPCConstant;
import bot.utils.ApiAction;
import bot.utils.Trade;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args)
            throws InterruptedException, ExecutionException, IOException, TransactionException {

        // Connect to Ethereum client using web3j
        Web3j web3j = Web3j.build(new HttpService(RPCConstant.ARBITRUM_ONE_RPC));

        // Load the credentials for the sender account
        Credentials credentials = Credentials.create(AccConstant.PRIVATE_KEY);

        // start with the latest block whenever start program to avoid old trades
        BigInteger latestBlock = web3j.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, false).send().getBlock()
                .getNumber();
        // TODO uncomment this
        new ApiAction().writeLastBlockNo(latestBlock.intValueExact());
        logger.info("Start program!! Latest block: " + latestBlock.intValueExact());

        Trade trade = new Trade();

        // TODO delete this when release to prod
//        trade.startTrade(web3j, credentials);

        // TODO uncomment this
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    trade.startTrade(web3j, credentials);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 120000);
    }
}
