package bot.utils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.utils.Numeric;

import bot.constant.GMXConstant;
import bot.model.ClosePositionRequest;
import bot.model.OpenPositionRequest;
import bot.model.PositionResponse;

public class SmartContractAction {
    private static final Logger logger = LogManager.getLogger(SmartContractAction.class);
    
    public double getBalanceInEth(Web3j web3j, Credentials credentials) throws InterruptedException, ExecutionException, IOException {
        EthGetBalance ethGetBalance = web3j.ethGetBalance(credentials.getAddress(), DefaultBlockParameterName.LATEST).send();

        BigInteger wei = ethGetBalance.getBalance();
        return wei.doubleValue() / Math.pow(10, 18);
    }

    @SuppressWarnings("rawtypes")
    public double getGLPPrice(Web3j web3j) throws InterruptedException, ExecutionException, IOException {
        List<TypeReference<?>> outputs = new ArrayList<TypeReference<?>>();
        TypeReference<Uint256> size = new TypeReference<Uint256>() {
        };
        outputs.add(size);

        Function function = new Function("getPrice", // Function name
                Collections.singletonList(new Bool(true)), outputs); // Function returned parameters

        String encodedFunction = FunctionEncoder.encode(function);
        EthCall encodedResponse = web3j.ethCall(Transaction.createEthCallTransaction(null, GMXConstant.GLP_MANAGER_ADDRESS, encodedFunction), DefaultBlockParameterName.LATEST)
                .send();

        List<Type> response = FunctionReturnDecoder.decode(encodedResponse.getValue(), function.getOutputParameters());
        double convertedPrice = convertToUsd(new BigInteger(response.get(0).getValue().toString()), GMXConstant.USD_PRICE_PRECISION);
        System.out.println(convertedPrice);
        return convertedPrice;
    }

    @SuppressWarnings({ "rawtypes", "deprecation", "unchecked" })
    public List<Type> getPositions(Web3j web3j, String godAccount) throws IOException, TransactionException, InterruptedException, ExecutionException {
        int collateralTokensNumber = 0;

        List<Type> inputs = new ArrayList<Type>();
        // vault contract address
        inputs.add(new Address(GMXConstant.VAULT_ADDRESS));
        // account of the user
        inputs.add(new Address(godAccount));
        // array of collateralTokens
        List<Address> collateralTokens = new ArrayList<Address>();
        collateralTokens.add(new Address(GMXConstant.WBTC_ADDRESS));
        collateralTokens.add(new Address(GMXConstant.WBTC_ADDRESS));
        collateralTokens.add(new Address(GMXConstant.WETH_ADDRESS));
        collateralTokens.add(new Address(GMXConstant.WETH_ADDRESS));
        inputs.add(new DynamicArray(collateralTokens));
        collateralTokensNumber = collateralTokens.size();
        // array of indexTokens
        List<Address> indexTokens = new ArrayList<Address>();
        indexTokens.add(new Address(GMXConstant.WBTC_ADDRESS));
        indexTokens.add(new Address(GMXConstant.WBTC_ADDRESS));
        indexTokens.add(new Address(GMXConstant.WETH_ADDRESS));
        indexTokens.add(new Address(GMXConstant.WETH_ADDRESS));
        inputs.add(new DynamicArray(indexTokens));
        // array of whether the position is a long position
        List<Bool> isLongArr = new ArrayList<Bool>();
        isLongArr.add(GMXConstant.IS_LONG);
        isLongArr.add(GMXConstant.IS_SHORT);
        isLongArr.add(GMXConstant.IS_LONG);
        isLongArr.add(GMXConstant.IS_SHORT);
        inputs.add(new DynamicArray(isLongArr));

        List<TypeReference<?>> outputs = new ArrayList<TypeReference<?>>();
        for (int i = 0; i < collateralTokensNumber; i++) {
            // size
            TypeReference<Uint256> size = new TypeReference<Uint256>() {
            };
            outputs.add(size);
            // collateral
            TypeReference<Uint256> collateral = new TypeReference<Uint256>() {
            };
            outputs.add(collateral);
            // averagePrice
            TypeReference<Uint256> averagePrice = new TypeReference<Uint256>() {
            };
            outputs.add(averagePrice);
            // entryFundingRate
            TypeReference<Uint256> entryFundingRate = new TypeReference<Uint256>() {
            };
            outputs.add(entryFundingRate);
            // hasRealisedProfit
            TypeReference<Bool> hasRealisedProfit = new TypeReference<Bool>() {
            };
            outputs.add(hasRealisedProfit);
            // realisedPnl
            TypeReference<Uint256> realisedPnl = new TypeReference<Uint256>() {
            };
            outputs.add(realisedPnl);
            // lastIncreasedTime
            TypeReference<Uint256> lastIncreasedTime = new TypeReference<Uint256>() {
            };
            outputs.add(lastIncreasedTime);
            // hasProfit
            TypeReference<Bool> hasProfit = new TypeReference<Bool>() {
            };
            outputs.add(hasProfit);
            // delta
            TypeReference<Uint256> delta = new TypeReference<Uint256>() {
            };
            outputs.add(delta);
        }

        Function function = new Function("getPositions", // Function name
                inputs, outputs); // Function returned parameters

        String encodedFunction = FunctionEncoder.encode(function);
        EthCall encodedResponse = web3j.ethCall(Transaction.createEthCallTransaction(null, GMXConstant.READER_ADDRESS, encodedFunction), DefaultBlockParameterName.LATEST).send();

        List<Type> response = FunctionReturnDecoder.decode(encodedResponse.getValue(), function.getOutputParameters());
        return response;
    }

    @SuppressWarnings("rawtypes")
    public PositionResponse getPosition(Web3j web3j, Address _account, Address _collateralToken, Address _indexToken, Bool _isLong) throws InterruptedException, ExecutionException, IOException {
        PositionResponse ps = new PositionResponse();
        List<Type> inputs = new ArrayList<Type>();
        List<TypeReference<?>> outputs = new ArrayList<TypeReference<?>>();

        // set inputs
        inputs.add(_account);
        inputs.add(_collateralToken);
        inputs.add(_indexToken);
        inputs.add(_isLong);

        // set outputs
        outputs.add(new TypeReference<Uint256>() {}); // size
        outputs.add(new TypeReference<Uint256>() {}); // collateral
        outputs.add(new TypeReference<Uint256>() {}); // average price
        outputs.add(new TypeReference<Uint256>() {}); // entry funding rate
        outputs.add(new TypeReference<Uint256>() {}); // reserve amount
        outputs.add(new TypeReference<Uint256>() {}); // realised pnl
        outputs.add(new TypeReference<Bool>() {}); // has profit
        outputs.add(new TypeReference<Uint256>() {}); // last increased time

        // call function
        Function function = new Function("getPosition", // Function name
                inputs, outputs); // Function returned parameters

        String encodedFunction = FunctionEncoder.encode(function);
        EthCall encodedResponse = web3j.ethCall(Transaction.createEthCallTransaction(null, GMXConstant.VAULT_ADDRESS, encodedFunction), DefaultBlockParameterName.LATEST).send();

        List<Type> response = FunctionReturnDecoder.decode(encodedResponse.getValue(), function.getOutputParameters());
        if (response.size() > 0) {
            ps.setSize(new Uint256((BigInteger) response.get(0).getValue()));
            ps.setCollateral(new Uint256((BigInteger) response.get(1).getValue()));
            ps.setAveragePrice(new Uint256((BigInteger) response.get(2).getValue()));
            ps.setEntryFundingRate(new Uint256((BigInteger) response.get(3).getValue()));
            ps.setReserveAmount(new Uint256((BigInteger) response.get(4).getValue()));
            ps.setRealisedPnl(new Uint256((BigInteger) response.get(5).getValue()));
            ps.setHasProfit(new Bool((Boolean) response.get(6).getValue()));
            ps.setLastIncreasedTime(new Uint256((BigInteger) response.get(7).getValue()));
        }
        // sizeDelta only have value with open position.
        // for closed position, sizeDelta = 0
        logger.info("sizeDelta to close position: " + ps.getSize().getValue());
        return ps;
    }

    public double convertToUsd(BigInteger price, int decimals) {
        BigDecimal convertedPrice = new BigDecimal(price.toString());
        return convertedPrice.doubleValue() / Math.pow(10, decimals);
    }

    public Uint256 calculateSizeDelta(String collateralDelta, String sizeDelta, Uint256 amountIn) {
        BigDecimal _collateralDelta = new BigDecimal(collateralDelta);
        BigDecimal _sizeDelta = new BigDecimal(sizeDelta);
        double leverage = _sizeDelta.doubleValue() / _collateralDelta.doubleValue();
        logger.info("leverage: " + leverage);
        BigDecimal _amountIn = new BigDecimal(amountIn.getValue());
		BigInteger temp = BigDecimal.valueOf(_amountIn.doubleValue() * leverage).toBigInteger();
		Uint256 calculatedSizeDelta = new Uint256(temp.multiply(new BigInteger("10").pow(24))); // sizeDelta: 30 decimals, collateralDelta: 6 decimals => 30-6 = 24
		logger.info("calculatedSizeDelta: " + calculatedSizeDelta.getValue().toString());
		return calculatedSizeDelta;
    }

    @SuppressWarnings({ "rawtypes", "deprecation" })
	public void createIncreasePosition(Web3j web3j, Credentials credentials, OpenPositionRequest openPositionRequest) throws InterruptedException, ExecutionException, IOException {
    	// require(msg.value == _executionFee, "val");
    	BigInteger msg_value = new SmartContractAction().getMinExecutionFee(web3j).getValue(); 
    	
    	// inputs
    	List<Type> inputs = new ArrayList<Type>();
    	List<Address> path = openPositionRequest.getPath();
    	inputs.add(new DynamicArray(path));
    	inputs.add(openPositionRequest.getIndexToken());
    	inputs.add(openPositionRequest.getAmountIn());
    	inputs.add(openPositionRequest.getMinOut());
    	inputs.add(openPositionRequest.getSizeDelta());
    	inputs.add(openPositionRequest.getIsLong());
    	inputs.add(openPositionRequest.getAcceptablePrice());
    	inputs.add(openPositionRequest.getExecutionFee());
    	inputs.add(openPositionRequest.getReferralCode());
    	inputs.add(openPositionRequest.getCallbackTarget());
    	
    	// outputs
    	List<TypeReference<?>> outputs = new ArrayList<TypeReference<?>>();
    	outputs.add(new TypeReference<Bytes32>() {}); // requestKey
    	
		while (true) { // retry to avoid "32000 max fee per gas less than block base fee"
			// function call
			Function function = new Function("createIncreasePosition", inputs, outputs);

			String encodedFunction = FunctionEncoder.encode(function);
			BigInteger nonce = web3j.ethGetTransactionCount(credentials.getAddress(), DefaultBlockParameterName.LATEST)
					.send().getTransactionCount();
			RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, getCurrentGasPrice(web3j),
					getCurrentGasLimit(web3j), GMXConstant.POSITION_ROUTER_ADDRESS, msg_value, encodedFunction);
			// Sign and send the transaction
			byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
			String hexValue = Numeric.toHexString(signedMessage);
			EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).send();
			if (ethSendTransaction.hasError()) {
				logger.error(ethSendTransaction.getError().getCode() + " " + ethSendTransaction.getError().getMessage());
				if (ethSendTransaction.getError().getMessage().contains("max fee per gas")
						|| ethSendTransaction.getError().getMessage().contains("insufficient funds for gas")) {
					Thread.sleep(1000); // wait 1s before retry
				} else {
					break;
				}
			} else {
				// otherwise when error, will be looped forever
				String transactionHash = ethSendTransaction.getTransactionHash();
				logger.info("transactionHash: " + transactionHash);

				// transactionHash exists even if transaction is not yet confirmed so we need to
				// wait for response
				TransactionReceipt txReceipt = null;
				while (txReceipt == null) {
					EthGetTransactionReceipt ethGetReceipt = web3j.ethGetTransactionReceipt(transactionHash).sendAsync()
							.get();
					if (ethGetReceipt.getResult() != null) {
						txReceipt = ethGetReceipt.getTransactionReceipt().get();
					}
					Thread.sleep(1000); // wait for 1 second before checking again
				}
				logger.info("createIncreasePosition status: " + txReceipt.getStatus());
				break;
			}
		}
    }
    
    @SuppressWarnings("rawtypes")
	public void createDecreasePosition(Web3j web3j, Credentials credentials, ClosePositionRequest closePositionRequest) throws InterruptedException, ExecutionException, IOException {
		if (closePositionRequest.getSizeDelta().getValue().intValue() == 0) {
			return; // case when start program, user have already open a position
		}
    	
    	// require(msg.value == _executionFee, "val");
    	BigInteger msg_value = new SmartContractAction().getMinExecutionFee(web3j).getValue(); 

		// inputs
		List<Type> inputs = new ArrayList<Type>();
		List<Address> path = closePositionRequest.getPath();
		inputs.add(new DynamicArray(path));
		inputs.add(closePositionRequest.getIndexToken());
		inputs.add(closePositionRequest.getCollateralDelta());
		inputs.add(closePositionRequest.getSizeDelta());
		inputs.add(closePositionRequest.getIsLong());
		inputs.add(closePositionRequest.getReceiver());
		inputs.add(closePositionRequest.getAcceptablePrice());
		inputs.add(closePositionRequest.getMinOut());
		inputs.add(closePositionRequest.getExecutionFee());
		inputs.add(closePositionRequest.getWithdrawETH());
		inputs.add(closePositionRequest.getCallbackTarget());
    	
    	// outputs
    	List<TypeReference<?>> outputs = new ArrayList<TypeReference<?>>();
    	outputs.add(new TypeReference<Bytes32>() {}); // requestKey
    	
		while (true) { // retry to avoid "32000 max fee per gas less than block base fee"
			// function call
			Function function = new Function("createDecreasePosition", inputs, outputs);

			String encodedFunction = FunctionEncoder.encode(function);
			BigInteger nonce = web3j.ethGetTransactionCount(credentials.getAddress(), DefaultBlockParameterName.LATEST)
					.send().getTransactionCount();
			RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, getCurrentGasPrice(web3j),
					getCurrentGasLimit(web3j), GMXConstant.POSITION_ROUTER_ADDRESS, msg_value, encodedFunction);
			// Sign and send the transaction
			byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
			String hexValue = Numeric.toHexString(signedMessage);
			EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).send();
			if (ethSendTransaction.hasError()) {
				logger.error(ethSendTransaction.getError().getCode() + " " + ethSendTransaction.getError().getMessage());
				if (ethSendTransaction.getError().getMessage().contains("max fee per gas")
						|| ethSendTransaction.getError().getMessage().contains("insufficient funds for gas")) {
					Thread.sleep(1000); // wait 1s before retry
				} else {
					break;
				}
			} else {
				// otherwise when error, will be looped forever
				String transactionHash = ethSendTransaction.getTransactionHash();
				logger.info("transactionHash: " + transactionHash);

				// transactionHash exists even if transaction is not yet confirmed so we need to
				// wait for response
				TransactionReceipt txReceipt = null;
				while (txReceipt == null) {
					EthGetTransactionReceipt ethGetReceipt = web3j.ethGetTransactionReceipt(transactionHash).sendAsync()
							.get();
					if (ethGetReceipt.getResult() != null) {
						txReceipt = ethGetReceipt.getTransactionReceipt().get();
					}
					Thread.sleep(1000); // wait for 1 second before checking again
				}
				logger.info("createDecreasePosition status: " + txReceipt.getStatus());
				break;
			}
		}
	}
    
	public Uint256 getMinExecutionFee(Web3j web3j) throws IOException {
		List<TypeReference<?>> outputs = new ArrayList<TypeReference<?>>();
		TypeReference<Uint256> size = new TypeReference<Uint256>() {
		};
		outputs.add(size);

		Function function = new Function("minExecutionFee", // Function name
				Arrays.asList(), outputs); // Function returned parameters

		String encodedFunction = FunctionEncoder.encode(function);
		EthCall encodedResponse = web3j.ethCall(Transaction.createEthCallTransaction(null,
				GMXConstant.POSITION_ROUTER_ADDRESS, encodedFunction), DefaultBlockParameterName.LATEST).send();

		List<Type> response = FunctionReturnDecoder.decode(encodedResponse.getValue(), function.getOutputParameters());
		if (response.size() > 0) {
			Uint256 minExecutionFee = new Uint256(new BigInteger(response.get(0).getValue().toString()));
			return minExecutionFee;
		} else {
			return GMXConstant.EXECUTION_FEE; // fallback
		}
	}

    private BigInteger getCurrentGasPrice(Web3j web3j) throws InterruptedException, ExecutionException, IOException {
        EthGasPrice ethGasPrice = web3j.ethGasPrice().send();
        BigInteger currentGasPrice = ethGasPrice.getGasPrice();
        return currentGasPrice;
    }

    private BigInteger getCurrentGasLimit(Web3j web3j) throws IOException {
        return new BigInteger("6000000"); // 2023/05/01: 40xxx is not enough, raise to 6000000
    }
}
