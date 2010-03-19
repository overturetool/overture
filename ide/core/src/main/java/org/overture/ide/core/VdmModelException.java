package org.overture.ide.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;

public class VdmModelException extends CoreException
{

	public VdmModelException(IStatus status) {
		super(status);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public boolean isDoesNotExist() {
		// TODO Auto-generated method stub
		return false;
	}

	

}
