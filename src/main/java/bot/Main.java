package bot;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;

import bot.constant.AccConstant;
import bot.constant.GMXConstant;
import bot.constant.RPCConstant;
import bot.model.ClosePositionRequest;
import bot.model.OpenPositionRequest;
import bot.utils.ApiAction;
import bot.utils.SmartContractAction;
import bot.utils.Trade;

public class Main {
	private static final Logger logger = LogManager.getLogger(Main.class);
	
	private static Credentials credentials = null;
	private static String godAccount = null;

	public static void main(String[] args)
			throws InterruptedException, ExecutionException, IOException, TransactionException {

		// Connect to Ethereum client using web3j
		Web3j web3j = Web3j.build(new HttpService(RPCConstant.ARBITRUM_ONE_RPC));
		
		if ("1".equals(args[0])) {
			 credentials = Credentials.create(AccConstant.GMX_1_KEY);
			 godAccount = AccConstant.WHALE_KEY;
			 // hard code 100$ per trade
		} else if ("2".equals(args[0])) {
			 credentials = Credentials.create(AccConstant.GMX_2_KEY);
			 godAccount = AccConstant.DOLPHIN_KEY;
			 // divide by 2
		} else if ("3".equals(args[0])) {
			 credentials = Credentials.create(AccConstant.GMX_3_KEY);
			 godAccount = AccConstant.SHRIMP_KEY;
		}

		// start with the latest block whenever start program to avoid old trades
		BigInteger latestBlock = web3j.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, false).send().getBlock()
				.getNumber();
		// TODO uncomment this
		new ApiAction().writeLastBlockNo(latestBlock.intValueExact());
		logger.info("Start program!! Latest block: " + latestBlock.intValueExact());
		logger.info("godAccount: " + godAccount);
		logger.info("ver: 2023/05/02 17:53");

		Trade trade = new Trade();
		
		// TODO uncomment this
		Timer t = new Timer();
		t.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					trade.startTrade(web3j, credentials, godAccount);
				} catch (Exception e) {
					logger.error(e);
				}
			}
		}, 0, 3000); // pooling 3s
	}

	public void test(Web3j web3j, Credentials credentials)
			throws IOException, InterruptedException, ExecutionException {
		OpenPositionRequest openPositionRequest = new OpenPositionRequest();
		openPositionRequest.setAcceptablePrice(new Uint256(new BigInteger("27599689000000000000000000000000000")));
		openPositionRequest.setAmountIn(GMXConstant.AMOUNT_IN);
		openPositionRequest.setCallbackTarget(GMXConstant.CALLBACK_TARGET);
		Uint256 executionFee = new SmartContractAction().getMinExecutionFee(web3j);
		openPositionRequest.setExecutionFee(executionFee);
		openPositionRequest.setIndexToken(new Address(GMXConstant.WBTC_ADDRESS));
		openPositionRequest.setIsLong(new Bool(false));
		openPositionRequest.setMinOut(GMXConstant.MIN_OUT);
		List<Address> path = new ArrayList<Address>();
		path.add(new Address(GMXConstant.USDC_ADDRESS));
		path.add(new Address(GMXConstant.WBTC_ADDRESS));
		openPositionRequest.setPath(path);
		openPositionRequest.setReferralCode(GMXConstant.REFERRAL_CODE);
		openPositionRequest.setSizeDelta(new Uint256(new BigInteger("13728571428571428560452558100000")));
		new SmartContractAction().createIncreasePosition(web3j, credentials, openPositionRequest);

		ClosePositionRequest closePositionRequest = new ClosePositionRequest();
		List<Address> pathS = new ArrayList<Address>();
		pathS.add(new Address(GMXConstant.WBTC_ADDRESS));
		closePositionRequest.setPath(pathS);
		closePositionRequest.setIndexToken(new Address(GMXConstant.WBTC_ADDRESS));
		closePositionRequest.setCollateralDelta(GMXConstant.MIN_OUT);
		closePositionRequest.setSizeDelta(new Uint256(new BigInteger("13728571428571428560452558100000")));
		closePositionRequest.setIsLong(new Bool(true));
		closePositionRequest.setReceiver(new Address(credentials.getAddress()));
		closePositionRequest.setAcceptablePrice(new Uint256(new BigInteger("27819689000000000000000000000000000")));
		closePositionRequest.setMinOut(GMXConstant.MIN_OUT);
		closePositionRequest.setExecutionFee(executionFee);
		closePositionRequest.setWithdrawETH(new Bool(false));
		closePositionRequest.setCallbackTarget(GMXConstant.CALLBACK_TARGET);
		new SmartContractAction().createDecreasePosition(web3j, credentials, closePositionRequest);
	}
}
