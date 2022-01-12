package com.reddate.ddc.listener;

import org.web3j.crypto.RawTransaction;

public class SignEvent {

	public RawTransaction getRawTransaction() {
		return rawTransaction;
	}

	public void setRawTransaction(RawTransaction rawTransaction) {
		this.rawTransaction = rawTransaction;
	}

	private RawTransaction rawTransaction;


}
