/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overture.ide.debug.core.model.internal.operations;

import org.overture.ide.debug.core.dbgp.exceptions.DbgpException;
import org.overture.ide.debug.core.model.IVdmThread;

public class DbgpSuspendOperation extends DbgpOperation {
	private static final String JOB_NAME = "Suspend Operation";

	public DbgpSuspendOperation(IVdmThread thread, IResultHandler finish) {
		super(thread, JOB_NAME, finish);
	}

	protected void process() throws DbgpException {
		/* boolean success = */getExtended().makeBreak();
		// TODO: think about what to do with this status
	}
}
