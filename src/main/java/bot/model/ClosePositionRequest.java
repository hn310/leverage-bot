package bot.model;

import java.util.List;

import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.generated.Uint256;

public class ClosePositionRequest {
    private List<Address> path;
    private Address indexToken;
    private Uint256 collateralDelta;
    private Uint256 sizeDelta;
    private Bool isLong;
    private Address receiver;
    private Uint256 acceptablePrice;
    private Uint256 minOut;
    private Uint256 executionFee;
    private Bool withdrawETH;
    private Address callbackTarget;

    public List<Address> getPath() {
        return path;
    }

    public void setPath(List<Address> path) {
        this.path = path;
    }

    public Address getIndexToken() {
        return indexToken;
    }

    public void setIndexToken(Address indexToken) {
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

    public Bool getIsLong() {
        return isLong;
    }

    public void setIsLong(Bool isLong) {
        this.isLong = isLong;
    }

    public Address getReceiver() {
        return receiver;
    }

    public void setReceiver(Address receiver) {
        this.receiver = receiver;
    }

    public Uint256 getAcceptablePrice() {
        return acceptablePrice;
    }

    public void setAcceptablePrice(Uint256 acceptablePrice) {
        this.acceptablePrice = acceptablePrice;
    }

    public Uint256 getMinOut() {
        return minOut;
    }

    public void setMinOut(Uint256 minOut) {
        this.minOut = minOut;
    }

    public Uint256 getExecutionFee() {
        return executionFee;
    }

    public void setExecutionFee(Uint256 executionFee) {
        this.executionFee = executionFee;
    }

    public Bool getWithdrawETH() {
        return withdrawETH;
    }

    public void setWithdrawETH(Bool withdrawETH) {
        this.withdrawETH = withdrawETH;
    }

    public Address getCallbackTarget() {
        return callbackTarget;
    }

    public void setCallbackTarget(Address callbackTarget) {
        this.callbackTarget = callbackTarget;
    }

    @Override
    public String toString() {
        StringBuffer str = new StringBuffer();
        str.append("indexToken: " + this.indexToken.getValue() + ", ");
        str.append("collateralDelta: " + this.collateralDelta.getValue() + ", ");
        str.append("sizeDelta: " + this.sizeDelta.getValue() + ", ");
        str.append("isLong: " + this.isLong.getValue() + ", ");
        str.append("receiver: " + this.receiver.getValue() + ", ");
        str.append("acceptablePrice: " + this.acceptablePrice.getValue());
        return str.toString();
    }
}
