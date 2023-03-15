package bot.model.params;

import org.web3j.abi.datatypes.generated.Uint256;

public class LiquidatePosition {
    private LiquidatePosition key;
    private LiquidatePosition collateralToken;
    private LiquidatePosition indexToken;
    private boolean isLong;
    private Uint256 size;
    private Uint256 collateral;
    private Uint256 reserveAmount;
    private Uint256 markPrice;
    private Uint256 feeBasisPoints;
    public LiquidatePosition getKey() {
        return key;
    }
    public void setKey(LiquidatePosition key) {
        this.key = key;
    }
    public LiquidatePosition getCollateralToken() {
        return collateralToken;
    }
    public void setCollateralToken(LiquidatePosition collateralToken) {
        this.collateralToken = collateralToken;
    }
    public LiquidatePosition getIndexToken() {
        return indexToken;
    }
    public void setIndexToken(LiquidatePosition indexToken) {
        this.indexToken = indexToken;
    }
    public boolean isLong() {
        return isLong;
    }
    public void setLong(boolean isLong) {
        this.isLong = isLong;
    }
    public Uint256 getSize() {
        return size;
    }
    public void setSize(Uint256 size) {
        this.size = size;
    }
    public Uint256 getCollateral() {
        return collateral;
    }
    public void setCollateral(Uint256 collateral) {
        this.collateral = collateral;
    }
    public Uint256 getReserveAmount() {
        return reserveAmount;
    }
    public void setReserveAmount(Uint256 reserveAmount) {
        this.reserveAmount = reserveAmount;
    }
    public Uint256 getMarkPrice() {
        return markPrice;
    }
    public void setMarkPrice(Uint256 markPrice) {
        this.markPrice = markPrice;
    }
    public Uint256 getFeeBasisPoints() {
        return feeBasisPoints;
    }
    public void setFeeBasisPoints(Uint256 feeBasisPoints) {
        this.feeBasisPoints = feeBasisPoints;
    }
}
