package bot.utils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.exceptions.TransactionException;

import bot.constant.AccConstant;
import bot.constant.GMXConstant;

public class SmartContractAction {

    public double getBalanceInEth(Web3j web3j) throws InterruptedException, ExecutionException {
        EthGetBalance ethGetBalance = web3j.ethGetBalance(AccConstant.ADDRESS, DefaultBlockParameterName.LATEST)
                .sendAsync().get();

        BigInteger wei = ethGetBalance.getBalance();
        return wei.doubleValue() / Math.pow(10, 18);
    }

    @SuppressWarnings("rawtypes")
    public double getGLPPrice(Web3j web3j) throws InterruptedException, ExecutionException {
        List<TypeReference<?>> outputs = new ArrayList<TypeReference<?>>();
        TypeReference<Uint256> size = new TypeReference<Uint256>() {
        };
        outputs.add(size);

        Function function = new Function("getPrice", // Function name
                Collections.singletonList(new Bool(true)), outputs); // Function returned parameters

        String encodedFunction = FunctionEncoder.encode(function);
        EthCall encodedResponse = web3j.ethCall(Transaction.createEthCallTransaction(AccConstant.GOD_KEY,
                GMXConstant.GLP_MANAGER_ADDRESS, encodedFunction), DefaultBlockParameterName.LATEST).sendAsync().get();

        List<Type> response = FunctionReturnDecoder.decode(encodedResponse.getValue(), function.getOutputParameters());
        double convertedPrice = convertToUsd(new BigInteger(response.get(0).getValue().toString()),
                GMXConstant.USD_PRICE_PRECISION);
        System.out.println(convertedPrice);
        return convertedPrice;
    }

    @SuppressWarnings({ "rawtypes", "deprecation", "unchecked" })
    public void getPositions(Web3j web3j)
            throws IOException, TransactionException, InterruptedException, ExecutionException {
        int collateralTokensNumber = 0;

        List<Type> inputs = new ArrayList<Type>();
        // vault contract address
        inputs.add(new Address(GMXConstant.VAULT_ADDRESS));
        // account of the user
        inputs.add(new Address(AccConstant.GOD_KEY));
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
        EthCall encodedResponse = web3j.ethCall(
                Transaction.createEthCallTransaction(AccConstant.GOD_KEY, GMXConstant.READER_ADDRESS, encodedFunction),
                DefaultBlockParameterName.LATEST).sendAsync().get();

        List<Type> response = FunctionReturnDecoder.decode(encodedResponse.getValue(), function.getOutputParameters());
        for (int i = 0; i < response.size(); i++) {
            System.out.println(response.get(i).getValue());
            if (i % 9 == 8) {
                System.out.println("\n");
            }
        }
    }

    private double convertToUsd(BigInteger price, int decimals) {
        BigDecimal convertedPrice = new BigDecimal(price.toString());
        return convertedPrice.doubleValue() / Math.pow(10, decimals);
    }
}
