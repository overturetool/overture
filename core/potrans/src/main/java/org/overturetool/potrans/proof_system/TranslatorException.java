package org.overturetool.potrans.proof_system;

public class TranslatorException extends Exception {
	
	private static final long serialVersionUID = 676203544114801697L;

	public TranslatorException() {
	}

	public TranslatorException(String message) {
		super(message);
	}

	public TranslatorException(Throwable cause) {
		super(cause);
	}

	public TranslatorException(String message, Throwable cause) {
		super(message, cause);
	}

}
