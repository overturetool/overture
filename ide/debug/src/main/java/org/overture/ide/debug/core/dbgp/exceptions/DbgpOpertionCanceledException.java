package org.overture.ide.debug.core.dbgp.exceptions;

public class DbgpOpertionCanceledException extends DbgpException {

	private static final long serialVersionUID = 1L;

	public DbgpOpertionCanceledException() {
		super();
	}

	public DbgpOpertionCanceledException(String message, Throwable cause) {
		super(message, cause);
	}

	public DbgpOpertionCanceledException(String message) {
		super(message);
	}

	public DbgpOpertionCanceledException(Throwable cause) {
		super(cause);
	}
}
