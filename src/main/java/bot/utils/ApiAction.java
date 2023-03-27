package bot.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import bot.constant.AccConstant;
import bot.constant.ActionsConstant;
import bot.constant.GMXConstant;
import bot.constant.MiscConstant;
import bot.model.TradeHistory;
import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ApiAction {

    public List<TradeHistory> getGodTradeHistories(int lastBlockNo) throws IOException {
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
            if (th.getTradeHistoryData().getBlockNumber() > lastBlockNo) {
                // open/close only success if below actions are called -> ignore other actions
                if (th.getTradeHistoryData().getAction().startsWith(ActionsConstant.INCREASE_POSITION)
                        || th.getTradeHistoryData().getAction().startsWith(ActionsConstant.DECREASE_POSITION)) {
                    filteredTradeHistories.add(th);
                }
            }
        }
        return filteredTradeHistories;
    }

    public int readLastBlockNo() throws IOException {
        int lastBlockNo = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(MiscConstant.BLOCK_NO_FILE))) {
            lastBlockNo = Integer.parseInt(br.readLine());
        }
        return lastBlockNo;
    }

    public void writeLastBlockNo(int lastBlockNo) throws IOException {
        String oldContent = Files.readString(Path.of(MiscConstant.BLOCK_NO_FILE), Charset.defaultCharset());
        FileWriter fw = new FileWriter(MiscConstant.BLOCK_NO_FILE, false); // the true will append the new data
        fw.write(lastBlockNo + System.getProperty("line.separator") + oldContent); // appends the string to the file
        fw.close();
    }
}
