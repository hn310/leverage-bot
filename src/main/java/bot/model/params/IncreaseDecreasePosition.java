package bot.model.params;

import bot.model.params.sub.Flags;

public class IncreaseDecreasePosition {
    private String key;
    private String collateralToken;
    private String indexToken;
    private String collateralDelta;
    private String sizeDelta;
    private boolean isLong;
    private String price;
    private Flags flags;
    private int feeBasisPoints;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCollateralToken() {
        return collateralToken;
    }

    public void setCollateralToken(String collateralToken) {
        this.collateralToken = collateralToken;
    }

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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Flags getFlags() {
        return flags;
    }

    public void setFlags(Flags flags) {
        this.flags = flags;
    }

    public int getFeeBasisPoints() {
        return feeBasisPoints;
    }

    public void setFeeBasisPoints(int feeBasisPoints) {
        this.feeBasisPoints = feeBasisPoints;
    }

}
