package com.reddate.ddc.constant;

/**
 * Recharge contract function
 */
public class ChargeFunctions {
	public static final String RECHARGE = "recharge";

	public static final String BALANCE_OF = "balanceOf";

	public static final String QUERY_FEE = "queryFee";

	public static final String SELF_RECHARGE = "selfRecharge";

	public static final String SET_FEE = "setFee";

	public static final String DELETE_FEE = "deleteFee";

	public static final String DELETE_DDC = "deleteDDC";


	public static final String RECHARGE_EVENT = "Recharge(address,address,uint256)";
	public static final String PAY_EVENT = "Pay(address,address,bytes4,uint32)";
	public static final String SET_FEE_EVENT = "SetFee(address,byte4,uint)";
	public static final String DELETE_FEE_EVENT = "DeleteFee(address,bytes4)";
	public static final String DELETE_DDC_EVENT = "DeleteDDC(address)";

}
