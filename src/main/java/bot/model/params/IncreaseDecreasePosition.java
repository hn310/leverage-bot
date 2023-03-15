package bot.model.params;

import org.web3j.abi.datatypes.generated.Uint256;

import bot.model.params.sub.Flags;

public class IncreaseDecreasePosition {
    private String key;
    private String collateralToken;
    private String indexToken;
    private Uint256 collateralDelta;
    private Uint256 sizeDelta;
    private boolean isLong;
    private Uint256 price;
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

    public Uint256 getCollateralDelta() {
        return collateralDelta;
    }

    public void setCollateralDelta(Uint256 collateralDelta) {
        this.collateralDelta = collateralDelta;
    }

    public Uint256 getSizeDelta() {
        return sizeDelta;
    }

    public void setSizeDelta(Uint256 sizeDelta) {
        this.sizeDelta = sizeDelta;
    }

    public boolean isLong() {
        return isLong;
    }

    public void setLong(boolean isLong) {
        this.isLong = isLong;
    }

    public Uint256 getPrice() {
        return price;
    }

    public void setPrice(Uint256 price) {
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
