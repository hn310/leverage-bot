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
    
    private static final String[] send_errors = {"max fee per gas","insufficient funds for gas","intrinsic gas too low"};
    
    public BigInteger getBalanceInUsdc(Web3j web3j, Credentials credentials) throws IOException {
    	Function balanceOfFunction = new Function(
                "balanceOf",
                Collections.singletonList(new org.web3j.abi.datatypes.Address(credentials.getAddress())),
                Collections.singletonList(new TypeReference<Uint256>() {
                })
        );
    	
    	String encodedFunction = FunctionEncoder.encode(balanceOfFunction);
        org.web3j.protocol.core.methods.response.EthCall ethCall = web3j.ethCall(
                org.web3j.protocol.core.methods.request.Transaction.createEthCallTransaction(
                		credentials.getAddress(), GMXConstant.USDC_ADDRESS, encodedFunction),
                DefaultBlockParameterName.LATEST)
                .send();

        List<TypeReference<Type>> outputParameters = balanceOfFunction.getOutputParameters();
        List<Type> values = FunctionReturnDecoder.decode(ethCall.getValue(), outputParameters);
        BigInteger balance = (BigInteger) values.get(0).getValue();
        return balance;
    }

    @SuppressWarnings({ "rawtypes", "deprecation", "unchecked" })
    public List<PositionResponse> getPositions(Web3j web3j, Credentials credentials) throws IOException, TransactionException, InterruptedException, ExecutionException {
    	List<PositionResponse> psList = new ArrayList<PositionResponse>();
    	int collateralTokensNumber = 0;

        List<Type> inputs = new ArrayList<Type>();
        // vault contract address
        inputs.add(new Address(GMXConstant.VAULT_ADDRESS));
        // account of the user
        inputs.add(new Address(credentials.getAddress()));
        // array of collateralTokens
        List<Address> collateralTokens = new ArrayList<Address>();
        collateralTokens.add(new Address(GMXConstant.USDC_ADDRESS));
        collateralTokens.add(new Address(GMXConstant.USDC_ADDRESS));
        collateralTokens.add(new Address(GMXConstant.USDC_ADDRESS));
        collateralTokens.add(new Address(GMXConstant.USDC_ADDRESS));
        collateralTokens.add(new Address(GMXConstant.USDC_ADDRESS));
        collateralTokens.add(new Address(GMXConstant.USDC_ADDRESS));
        collateralTokens.add(new Address(GMXConstant.USDC_ADDRESS));
        collateralTokens.add(new Address(GMXConstant.USDC_ADDRESS));
        inputs.add(new DynamicArray(collateralTokens));
        collateralTokensNumber = collateralTokens.size();
        // array of indexTokens
        List<Address> indexTokens = new ArrayList<Address>();
        indexTokens.add(new Address(GMXConstant.WBTC_ADDRESS));
        indexTokens.add(new Address(GMXConstant.WBTC_ADDRESS));
        indexTokens.add(new Address(GMXConstant.WETH_ADDRESS));
        indexTokens.add(new Address(GMXConstant.WETH_ADDRESS));
        indexTokens.add(new Address(GMXConstant.LINK_ADDRESS));
        indexTokens.add(new Address(GMXConstant.LINK_ADDRESS));
        indexTokens.add(new Address(GMXConstant.UNI_ADDRESS));
        indexTokens.add(new Address(GMXConstant.UNI_ADDRESS));
        inputs.add(new DynamicArray(indexTokens));
        // array of whether the position is a long position
        List<Bool> isLongArr = new ArrayList<Bool>();
        isLongArr.add(GMXConstant.IS_LONG);
        isLongArr.add(GMXConstant.IS_SHORT);
        isLongArr.add(GMXConstant.IS_LONG);
        isLongArr.add(GMXConstant.IS_SHORT);
        isLongArr.add(GMXConstant.IS_LONG);
        isLongArr.add(GMXConstant.IS_SHORT);
        isLongArr.add(GMXConstant.IS_LONG);
        isLongArr.add(GMXConstant.IS_SHORT);
        inputs.add(new DynamicArray(isLongArr));
        
        // set neccessary info for PositionResponse to use in calculating % in loss
        for (int i = 0; i < collateralTokensNumber; i++) {
        	PositionResponse ps = new PositionResponse();
        	psList.add(ps);
        }
        // WBTC
        psList.get(0).setIndexToken(new Address(GMXConstant.WBTC_ADDRESS));
        psList.get(0).setIsLong(GMXConstant.IS_LONG);
        psList.get(1).setIndexToken(new Address(GMXConstant.WBTC_ADDRESS));
        psList.get(1).setIsLong(GMXConstant.IS_SHORT);
        // ETH
        psList.get(2).setIndexToken(new Address(GMXConstant.WETH_ADDRESS));
        psList.get(2).setIsLong(GMXConstant.IS_LONG);
        psList.get(3).setIndexToken(new Address(GMXConstant.WETH_ADDRESS));
        psList.get(3).setIsLong(GMXConstant.IS_SHORT);
        // LINK
        psList.get(4).setIndexToken(new Address(GMXConstant.LINK_ADDRESS));
        psList.get(4).setIsLong(GMXConstant.IS_LONG);
        psList.get(5).setIndexToken(new Address(GMXConstant.LINK_ADDRESS));
        psList.get(5).setIsLong(GMXConstant.IS_SHORT);
        // UNI
        psList.get(6).setIndexToken(new Address(GMXConstant.UNI_ADDRESS));
        psList.get(6).setIsLong(GMXConstant.IS_LONG);
        psList.get(7).setIndexToken(new Address(GMXConstant.UNI_ADDRESS));
        psList.get(7).setIsLong(GMXConstant.IS_SHORT);

        List<TypeReference<?>> outputs = new ArrayList<TypeReference<?>>();
        // have to add this for the function to work
        outputs.add(new TypeReference<Uint256>() {}); // have no fucking clue why this return 32
        outputs.add(new TypeReference<Uint256>() {}); // have no fucking clue why this return collateralTokensNumber * 9
        
        for (int i = 0; i < collateralTokensNumber; i++) {
            // size
            outputs.add(new TypeReference<Uint256>() {});
            // collateral
			outputs.add(new TypeReference<Uint256>() {});
            // averagePrice
			outputs.add(new TypeReference<Uint256>() {});
            // entryFundingRate
			outputs.add(new TypeReference<Uint256>() {});
            // hasRealisedProfit
			outputs.add(new TypeReference<Uint256>() {});
            // realisedPnl
            outputs.add(new TypeReference<Uint256>() {});
            // lastIncreasedTime
            outputs.add(new TypeReference<Uint256>() {});
            // hasProfit (in getPositions. In getPosition it is Bool)
            outputs.add(new TypeReference<Uint256>() {});
            // delta
            outputs.add(new TypeReference<Uint256>() {});
            
        }

        Function function = new Function("getPositions", // Function name
                inputs, outputs); // Function returned parameters

        String encodedFunction = FunctionEncoder.encode(function);
        EthCall encodedResponse = web3j.ethCall(Transaction.createEthCallTransaction(null, GMXConstant.READER_ADDRESS, encodedFunction), DefaultBlockParameterName.LATEST).send();
        List<Type> response = FunctionReturnDecoder.decode(encodedResponse.getValue(), function.getOutputParameters());
        for (int i = 0; i < response.size(); i++) {
			if (i % 9 == 2) { // skip the first 2 elements in array (32 & indexTokens*9)
				psList.get(i / 9).setSize((Uint256) response.get(i));
				psList.get(i / 9).setCollateral((Uint256) response.get(i + 1));
				psList.get(i / 9).setAveragePrice((Uint256) response.get(i + 2));
				psList.get(i / 9).setEntryFundingRate((Uint256) response.get(i + 3));
				psList.get(i / 9).setHasRealisedProfit((Uint256) response.get(i + 4));
				psList.get(i / 9).setRealisedPnl((Uint256) response.get(i + 5));
				psList.get(i / 9).setLastIncreasedTime((Uint256) response.get(i + 6));
				psList.get(i / 9).setHasProfitInGetPositions((Uint256) response.get(i + 7));
				psList.get(i / 9).setDelta((Uint256) response.get(i + 8));
			}
		}
        return psList;
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
            ps.setSize((Uint256) response.get(0));
            ps.setCollateral((Uint256) response.get(1));
            ps.setAveragePrice((Uint256) response.get(2));
            ps.setEntryFundingRate((Uint256) response.get(3));
            ps.setReserveAmount((Uint256) response.get(4));
            ps.setRealisedPnl((Uint256) response.get(5));
            ps.setHasProfit((Bool) response.get(6));
            ps.setLastIncreasedTime((Uint256) response.get(7));
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
    	// if not enough balance then return
    	if (isNotEnoughBalance(web3j, credentials, openPositionRequest.getAmountIn())) {
    		return;
    	}
    	
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
				if (Arrays.stream(send_errors).anyMatch(ethSendTransaction.getError().getMessage()::contains)) {
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
				if (Arrays.stream(send_errors).anyMatch(ethSendTransaction.getError().getMessage()::contains)) {
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
    
    private boolean isNotEnoughBalance(Web3j web3j, Credentials credentials, Uint256 amountIn) throws IOException {
		BigInteger balanceInUsdc = getBalanceInUsdc(web3j, credentials);
		if (balanceInUsdc.compareTo(amountIn.getValue()) < 0) {
			logger.info("currentBalance is not enough: " + balanceInUsdc);
			return true;
		} else {
			return false;
		}
    }
}
