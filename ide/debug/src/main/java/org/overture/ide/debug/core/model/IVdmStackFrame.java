/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overture.ide.debug.core.model;

import java.net.URI;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IStackFrame;

public interface IVdmStackFrame extends IStackFrame {
	IVdmStack getStack();

	IVdmThread getVdmThread();

	int getLevel();

	String getSourceLine();

	/**
	 * Return line number of the command start or -1 if not available
	 * 
	 * @return
	 */
	int getBeginLine();

	/**
	 * Return column number of the command start or -1 if not available
	 * 
	 * @return
	 */
	int getBeginColumn();

	/**
	 * Return line number of the command end or -1 if not available
	 * 
	 * @return
	 */
	int getEndLine();

	/**
	 * Return column number of the command end or -1 if not available
	 * 
	 * @return
	 */
	int getEndColumn();

	URI getSourceURI();

	IVdmVariable findVariable(String varName) throws DebugException;

	String getWhere();
}
