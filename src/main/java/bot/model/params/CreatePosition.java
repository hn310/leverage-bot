package bot.model.params;

import org.web3j.abi.datatypes.generated.Uint256;

public class CreatePosition {
    private String indexToken;
    private boolean isLong;
    private Uint256 collateralDelta;
    private Uint256 sizeDelta;
    private Uint256 acceptablePrice;

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

    public Uint256 getAcceptablePrice() {
        return acceptablePrice;
    }

    public void setAcceptablePrice(Uint256 acceptablePrice) {
        this.acceptablePrice = acceptablePrice;
    }

}
