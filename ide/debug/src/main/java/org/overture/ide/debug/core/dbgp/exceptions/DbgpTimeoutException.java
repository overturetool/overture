package org.overture.ide.debug.core.dbgp.exceptions;

import org.overture.ide.debug.core.dbgp.DbgpRequest;

public class DbgpTimeoutException extends DbgpException {

	private static final long serialVersionUID = 1L;
	
	private DbgpRequest request;
	public DbgpTimeoutException(DbgpRequest request) {
		this.request = request;
	}

	@Override
	public String getMessage() {		
		return "Timeout on: "+request.getPacketAsString();
	}

}
