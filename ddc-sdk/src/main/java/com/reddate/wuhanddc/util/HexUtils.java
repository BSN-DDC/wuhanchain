package com.reddate.wuhanddc.util;

import org.fisco.bcos.web3j.utils.Numeric;

import java.math.BigInteger;

public class HexUtils {

	public static boolean isValid4ByteHash(String str) {
		String strNoPrefix = Numeric.cleanHexPrefix(str);
	    boolean valid = strNoPrefix.length() == 8;
	    if(valid) {
	    	try {
				new BigInteger(strNoPrefix,16);
			} catch (Exception e) {
				valid = false;
			}
	    }
		return valid;
	}
	
}
