package com.reddate.ddc.listener;

public interface SignEventListener {
	
	/**
	 * 交易签名事件，SDK将调用此事件的具体实现来签名所有发送到链上的交易
	 * 
	 * 
	 * @param event 签名事件参数
	 * @return 返回签名交易串
	 */
	String signEvent(SignEvent event);
}
