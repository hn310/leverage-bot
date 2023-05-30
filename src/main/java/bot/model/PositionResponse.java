package bot.model;

import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.generated.Uint256;

public class PositionResponse {
    private Uint256 size;
    private Uint256 collateral;
    private Uint256 averagePrice;
    private Uint256 entryFundingRate;
    private Uint256 reserveAmount;
    private Uint256 realisedPnl;
    private Bool hasProfit;
    private Uint256 hasProfitInGetPositions;
    private Uint256 lastIncreasedTime;
    private Uint256 delta;
    private Uint256 hasRealisedProfit;
    private Address indexToken;
    private Bool isLong;

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

    public Uint256 getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(Uint256 averagePrice) {
        this.averagePrice = averagePrice;
    }

    public Uint256 getEntryFundingRate() {
        return entryFundingRate;
    }

    public void setEntryFundingRate(Uint256 entryFundingRate) {
        this.entryFundingRate = entryFundingRate;
    }

    public Uint256 getReserveAmount() {
        return reserveAmount;
    }

    public void setReserveAmount(Uint256 reserveAmount) {
        this.reserveAmount = reserveAmount;
    }

    public Uint256 getRealisedPnl() {
        return realisedPnl;
    }

    public void setRealisedPnl(Uint256 realisedPnl) {
        this.realisedPnl = realisedPnl;
    }

    public Bool getHasProfit() {
        return hasProfit;
    }

    public void setHasProfit(Bool hasProfit) {
        this.hasProfit = hasProfit;
    }

    public Uint256 getLastIncreasedTime() {
        return lastIncreasedTime;
    }

    public void setLastIncreasedTime(Uint256 lastIncreasedTime) {
        this.lastIncreasedTime = lastIncreasedTime;
    }

	public Uint256 getDelta() {
		return delta;
	}

	public void setDelta(Uint256 delta) {
		this.delta = delta;
	}

	public Uint256 getHasProfitInGetPositions() {
		return hasProfitInGetPositions;
	}

	public void setHasProfitInGetPositions(Uint256 hasProfitInGetPositions) {
		this.hasProfitInGetPositions = hasProfitInGetPositions;
	}

	public Uint256 getHasRealisedProfit() {
		return hasRealisedProfit;
	}

	public void setHasRealisedProfit(Uint256 hasRealisedProfit) {
		this.hasRealisedProfit = hasRealisedProfit;
	}

	public Address getIndexToken() {
		return indexToken;
	}

	public void setIndexToken(Address indexToken) {
		this.indexToken = indexToken;
	}

	public Bool getIsLong() {
		return isLong;
	}

	public void setIsLong(Bool isLong) {
		this.isLong = isLong;
	}
}
