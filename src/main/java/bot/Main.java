package bot;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;

import bot.constant.AccConstant;
import bot.constant.RPCConstant;
import bot.model.TradeHistory;
import bot.utils.AccUtils;
import bot.utils.SmartContractUtils;

public class Main {
    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException, TransactionException {
        // Connect to Ethereum client using web3j
        Web3j web3j = Web3j.build(new HttpService(RPCConstant.ARBITRUM_ONE_RPC));

        // Load the credentials for the sender account
        Credentials credentials = Credentials.create(AccConstant.PRIVATE_KEY);

        double balanceInEth = AccUtils.getBalanceInEth(web3j);
//        System.out.println(balanceInEth);
//        SmartContractUtils.getGLPPrice(web3j);
        
        
        SmartContractUtils.getPositions(web3j);
//        List<TradeHistory> tradeHistories = AccUtils.getGodTradeHistories();
//        System.out.println(tradeHistories.get(0).getTradeHistoryData().getParams().getSizeDelta());
        
        //TODO get position: check xem user dang open bao nhieu position, khi close cung can call de biet rut ra bao nhieu tien
        
        
    }
}
