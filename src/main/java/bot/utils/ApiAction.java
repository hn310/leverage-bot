package bot.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
	private static final Logger logger = LogManager.getLogger(ApiAction.class);

    public List<TradeHistory> getGodTradeHistories(int lastBlockNo, String godAccount) throws IOException, InterruptedException {
        OkHttpClient client = new OkHttpClient();
        
		List<TradeHistory> filteredTradeHistories = new ArrayList<TradeHistory>();

		HttpUrl.Builder urlBuilder = HttpUrl.parse(GMXConstant.GMX_ACTIONS_URL).newBuilder();
		urlBuilder.addQueryParameter("account", godAccount);
		String url = urlBuilder.build().toString();

		Request request = new Request.Builder().url(url).build();

		Call call = client.newCall(request);
		Response response = call.execute();

		String resStr = response.body().string();

		Gson gson = new Gson();
		String sanitizedRes = resStr.replaceAll("\"\\{", "{").replaceAll("}\"", "}").replaceAll("\\\\", "");
		List<TradeHistory> godTradeHistories = new ArrayList<TradeHistory>();
		try {
			godTradeHistories = gson.fromJson(sanitizedRes, new TypeToken<List<TradeHistory>>() {
			}.getType());
		} catch (Exception e) {
			logger.error(e);
			logger.error("resStr: " + resStr);
			logger.error("sanitizedRes: " + sanitizedRes);
		}

		for (TradeHistory th : godTradeHistories) {
			if (th.getTradeHistoryData().getBlockNumber() > lastBlockNo) {
				// open/close only success if below actions are called -> ignore other actions
				if (th.getTradeHistoryData().getAction().startsWith(ActionsConstant.INCREASE_POSITION)
						|| th.getTradeHistoryData().getAction().startsWith(ActionsConstant.DECREASE_POSITION)) {
					filteredTradeHistories.add(th);
				}
			}
		}
        
        // remove duplicated trade histories due to call API sometimes return duplicated trade histories
        Set<String> seenTradeIds = new HashSet<>();
        List<TradeHistory> uniqueTradeHistories = new ArrayList<>();
        for (TradeHistory th : filteredTradeHistories) {
            if (seenTradeIds.add(th.getId())) {
            	uniqueTradeHistories.add(th);
            }
        }

		return uniqueTradeHistories;
    }
    
    public Map<String, String> getCurrentPrices() throws IOException {
    	OkHttpClient client = new OkHttpClient();
    	HttpUrl.Builder urlBuilder = HttpUrl.parse(GMXConstant.GMX_PRICES_URL).newBuilder();
		String url = urlBuilder.build().toString();

		Request request = new Request.Builder().url(url).build();

		Call call = client.newCall(request);
		Response response = call.execute();

		String resStr = response.body().string();

		Gson gson = new Gson();
		Map<String, String> map = new HashMap<String, String>();
		try {
			map = gson.fromJson(resStr, Map.class);
		} catch (Exception e) {
			logger.error(e);
			logger.error("resStr: " + resStr);
		}
    	return map;
    }

    public int readLastBlockNo() throws IOException {
        int lastBlockNo = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(MiscConstant.BLOCK_NO_FILE))) {
            lastBlockNo = Integer.parseInt(br.readLine());
        }
        return lastBlockNo;
    }

    public void writeLastBlockNo(int lastBlockNo) throws IOException {
        if (!Files.exists(Paths.get(MiscConstant.BLOCK_NO_FILE), LinkOption.NOFOLLOW_LINKS)) {
            Files.createFile(Paths.get(MiscConstant.BLOCK_NO_FILE));
        }
        String oldContent = Files.readString(Path.of(MiscConstant.BLOCK_NO_FILE), Charset.defaultCharset());
        FileWriter fw = new FileWriter(MiscConstant.BLOCK_NO_FILE, false); // the true will append the new data
        fw.write(lastBlockNo + System.getProperty("line.separator") + oldContent); // appends the string to the file
        fw.close();
    }
}
