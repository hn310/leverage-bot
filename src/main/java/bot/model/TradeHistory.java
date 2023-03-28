package bot.model;

public class TradeHistory {
    private String id;
    private TradeHistoryData data;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TradeHistoryData getTradeHistoryData() {
        return data;
    }

    public void setTradeHistoryData(TradeHistoryData tradeHistoryData) {
        this.data = tradeHistoryData;
    }
}
