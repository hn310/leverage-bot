package bot.model.params;

public class CreatePosition {
    private String indexToken;
    private boolean isLong;
    private String collateralDelta;
    private String sizeDelta;
    private String acceptablePrice;

    public String getIndexToken() {
        return indexToken;
    }

    public void setIndexToken(String indexToken) {
        this.indexToken = indexToken;
    }

    public boolean isLong() {
        return isLong;
    }

    public void setLong(boolean isLong) {
        this.isLong = isLong;
    }

    public String getCollateralDelta() {
        return collateralDelta;
    }

    public void setCollateralDelta(String collateralDelta) {
        this.collateralDelta = collateralDelta;
    }

    public String getSizeDelta() {
        return sizeDelta;
    }

    public void setSizeDelta(String sizeDelta) {
        this.sizeDelta = sizeDelta;
    }

    public String getAcceptablePrice() {
        return acceptablePrice;
    }

    public void setAcceptablePrice(String acceptablePrice) {
        this.acceptablePrice = acceptablePrice;
    }
}
