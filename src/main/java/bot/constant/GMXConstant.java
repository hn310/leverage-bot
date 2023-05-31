package bot.constant;

import java.math.BigInteger;

import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.utils.Numeric;

public class GMXConstant {
    public static String GMX_ACTIONS_URL = "https://api.gmx.io/actions";
    
    // smart contracts address
    public static String POSITION_ROUTER_ADDRESS = "0xb87a436B93fFE9D75c5cFA7bAcFff96430b09868";
    public static String READER_ADDRESS = "0x22199a49A999c351eF7927602CFB187ec3cae489";
    public static String VAULT_ADDRESS = "0x489ee077994B6658eAfA855C308275EAd8097C4A";
    public static String GLP_MANAGER_ADDRESS = "0x3963FfC9dff443c2A94f21b129D429891E32ec18";
    
    // token address
    public static String WBTC_ADDRESS = "0x2f2a2543B76A4166549F7aaB2e75Bef0aefC5B0f";
    public static String WETH_ADDRESS = "0x82aF49447D8a07e3bd95BD0d56f35241523fBab1";
    public static String USDC_ADDRESS = "0xFF970A61A04b1cA14834A43f5dE4533eBDDB5CC8";
    public static String LINK_ADDRESS = "0xf97f4df75117a78c1A5a0DBb814Af92458539FB4";
    public static String UNI_ADDRESS = "0xFa7F8980b0f1E64A2062791cc3b0871572f1F7f0";
    
    // long, short position
    public static Bool IS_LONG = new Bool(true);
    public static Bool IS_SHORT = new Bool(false);
    
    public static int USD_PRICE_PRECISION = 30;
    public static int USD_DECIMALS = 6;
    
    // trade related params
    public static Uint256 AMOUNT_IN = new Uint256(new BigInteger("100000000")); // 05/20: 120 USD, 05/31: 100 USD
    public static Uint256 MIN_OUT = new Uint256(new BigInteger("0"));
    public static Uint256 EXECUTION_FEE = new Uint256(new BigInteger("200000000000000")); // 0.0000000002 ETH
    public static Bytes32 REFERRAL_CODE = new Bytes32(
            Numeric.hexStringToByteArray("0x0000000000000000000000000000000000000000000000000000000000000000")) ;
    public static Address CALLBACK_TARGET = new Address("0x0000000000000000000000000000000000000000");
    public static double SLIPPAGE = 1.005; // 0.5%
}
