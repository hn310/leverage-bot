package bot;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import bot.constant.AccConstant;
import bot.constant.RPCConstant;
import bot.model.TradeHistory;
import bot.utils.AccUtils;

public class Main {
    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
        // Connect to Ethereum client using web3j
        Web3j web3j = Web3j.build(new HttpService(RPCConstant.SEPOLIA_RPC));

        // Load the credentials for the sender account
        Credentials credentials = Credentials.create(AccConstant.PRIVATE_KEY, AccConstant.PUBLIC_KEY);

        double balanceInEth = AccUtils.getBalanceInEth(web3j);
//        System.out.println(balanceInEth);
        
        Set<String> s = new HashSet<String>();
        
        for (TradeHistory th : AccUtils.getTradeHistories()) {
            s.add(th.getTradeHistoryData().getAction());
        }
        
        for (String tempS : s) {
            System.out.println(tempS);
        }
    }
}
