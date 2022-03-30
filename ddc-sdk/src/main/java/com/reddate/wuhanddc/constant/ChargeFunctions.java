package com.reddate.wuhanddc.constant;

/**
 * Recharge contract function
 */
public class ChargeFunctions {
	public static final String RECHARGE = "recharge";

	public static final String BALANCE_OF = "balanceOf";

	public static final String QUERY_FEE = "queryFee";

	public static final String SELF_RECHARGE = "selfRecharge";

	public static final String SET_FEE = "setFee";

	public static final String DELETE_FEE = "delFee";

	public static final String DELETE_DDC = "delDDC";


	public static final String RECHARGE_EVENT = "Recharge(address,address,uint256)";
	public static final String PAY_EVENT = "Pay(address,address,bytes4,uint32,uint256)";
	public static final String SET_FEE_EVENT = "SetFee(address,byte4,uint)";
	public static final String DELETE_FEE_EVENT = "DelFee(address,bytes4)";
	public static final String DELETE_DDC_EVENT = "DelDDC(address)";

}
