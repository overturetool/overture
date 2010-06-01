/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overture.ide.debug.core.model.internal.operations;

import org.overture.ide.debug.core.dbgp.IDbgpStatus;
import org.overture.ide.debug.core.dbgp.exceptions.DbgpException;

public interface IDbgpDebuggerFeedback {
	void endStepInto(DbgpException e, IDbgpStatus status);

	void endStepOver(DbgpException e, IDbgpStatus status);

	void endStepReturn(DbgpException e, IDbgpStatus status);

	void endSuspend(DbgpException e, IDbgpStatus status);

	void endResume(DbgpException e, IDbgpStatus status);

	void endTerminate(DbgpException e, IDbgpStatus status);
}
