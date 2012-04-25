package org.overturetool.proofsupport.external_tools.hol;

public class HolInterpreterException extends Exception {

	private static final long serialVersionUID = -7064648945655075327L;

	public HolInterpreterException(String message) {
		super(message);
	}


	public HolInterpreterException(String message, Throwable cause) {
		super(message, cause);
	}

}
