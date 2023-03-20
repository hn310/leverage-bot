package bot.utils;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import bot.constant.AccConstant;
import bot.constant.ActionsConstant;
import bot.constant.GMXConstant;
import bot.model.TradeHistory;
import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AccUtils {
    public static double getBalanceInEth(Web3j web3j) throws InterruptedException, ExecutionException {
        EthGetBalance ethGetBalance = web3j.ethGetBalance(AccConstant.ADDRESS, DefaultBlockParameterName.LATEST)
                .sendAsync().get();

        BigInteger wei = ethGetBalance.getBalance();
        return wei.doubleValue() / Math.pow(10, 18);
    }

    public static List<TradeHistory> getGodTradeHistories() throws IOException {
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(GMXConstant.GMX_ACTIONS_URL).newBuilder();
        urlBuilder.addQueryParameter("account", AccConstant.GOD_KEY);

        String url = urlBuilder.build().toString();

        Request request = new Request.Builder().url(url).build();

        Call call = client.newCall(request);
        Response response = call.execute();

        String resStr = response.body().string();
        Gson gson = new Gson();
        String sanitizedRes = resStr.replaceAll("\"\\{", "{").replaceAll("}\"", "}").replaceAll("\\\\", "");
        List<TradeHistory> godTradeHistories = gson.fromJson(sanitizedRes, new TypeToken<List<TradeHistory>>() {
        }.getType());
        List<TradeHistory> filteredTradeHistories = new ArrayList<TradeHistory>();
        for (TradeHistory th : godTradeHistories) {
            // other actions are self-invoked by smart contracts -> ignored
            if (th.getTradeHistoryData().getAction().equals(ActionsConstant.CREATE_DECREASE_POSITION)
                    || th.getTradeHistoryData().getAction().equals(ActionsConstant.CREATE_INCREASE_POSITION)) {
                filteredTradeHistories.add(th);
            }
        }
        return filteredTradeHistories;
    }
}
