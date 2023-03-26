package bot.model;

import bot.model.params.IncreaseDecreasePosition;

public class TradeHistoryData {
    private int blockNumber;
    private String action;
    private IncreaseDecreasePosition params;
    private String account;
    private String timestamp;
    private String txhash;

    public int getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(int blockNumber) {
        this.blockNumber = blockNumber;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public IncreaseDecreasePosition getParams() {
        return params;
    }

    public void setParams(IncreaseDecreasePosition params) {
        this.params = params;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTxhash() {
        return txhash;
    }

    public void setTxhash(String txhash) {
        this.txhash = txhash;
    }

}
