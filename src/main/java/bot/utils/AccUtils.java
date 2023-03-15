package bot.utils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import bot.constant.AccConstant;
import bot.model.TradeHistory;
import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AccUtils {
    private static String GMX_ACTIONS_URL = "https://api.gmx.io/actions";

    public static double getBalanceInEth(Web3j web3j) throws InterruptedException, ExecutionException {
        EthGetBalance ethGetBalance = web3j.ethGetBalance(AccConstant.PUBLIC_KEY, DefaultBlockParameterName.LATEST)
                .sendAsync().get();

        BigInteger wei = ethGetBalance.getBalance();

        BigInteger decimal = BigDecimal.valueOf(Math.pow(10, 18)).toBigInteger();
        return wei.doubleValue() / decimal.doubleValue();
    }

    public static List<TradeHistory> getTradeHistories() throws IOException {
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(GMX_ACTIONS_URL).newBuilder();
        urlBuilder.addQueryParameter("account", AccConstant.GOD_KEY);

        String url = urlBuilder.build().toString();

        Request request = new Request.Builder().url(url).build();

        Call call = client.newCall(request);
        Response response = call.execute();

        String resStr = response.body().string();
        Gson gson = new Gson();
        return gson.fromJson(resStr,new TypeToken<List<TradeHistory>>(){}.getType());
    }
}
