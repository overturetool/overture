/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.  
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html  
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Andrei Sobolev)
 *******************************************************************************/
package org.overture.ide.debug.core.model;

import org.eclipse.core.runtime.IProgressMonitor;
import org.overture.ide.debug.core.model.internal.VdmThread;
import org.overture.ide.debug.core.model.internal.operations.DbgpDebugger;

/**
 * This class called to configure advanced thread parameters. It could be
 * registered from debugger runner to ScriptDebugTarget. One instance per
 * target.
 */
public interface IVdmDebugThreadConfigurator {
	void configureThread(DbgpDebugger engine, VdmThread scriptThread);

	void initializeBreakpoints(IVdmThread thread, IProgressMonitor monitor);
}
