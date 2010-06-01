/*******************************************************************************

 * Copyright (c) 2005, 2007 IBM Corporation and others.

 * All rights reserved. This program and the accompanying materials

 * are made available under the terms of the Eclipse Public License v1.0

 * which accompanies this distribution, and is available at

 * http://www.eclipse.org/legal/epl-v10.html

 *

 

 *******************************************************************************/

package org.overture.ide.debug.core.model;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.overture.ide.debug.core.dbgp.IDbgpSession;
import org.overture.ide.debug.core.dbgp.breakpoints.IDbgpBreakpoint;

public interface IVdmBreakpoint extends IBreakpoint {

	int HIT_CONDITION_GREATER_OR_EQUAL = IDbgpBreakpoint.HIT_CONDITION_GREATER_OR_EQUAL;

	int HIT_CONDITION_EQUAL = IDbgpBreakpoint.HIT_CONDITION_EQUAL;

	int HIT_CONDITION_MULTIPLE = IDbgpBreakpoint.HIT_CONDITION_MULTIPLE;

	/**
	 * 
	 * @return
	 * 
	 * @throws CoreException
	 * 
	 * @deprecated
	 */

	String getIdentifier() throws CoreException;

	/**
	 * 
	 * @param id
	 * 
	 * @throws CoreException
	 * 
	 * @deprecated
	 */

	void setIdentifier(String id) throws CoreException;

	String[] getIdentifiers();

	String getId(IDbgpSession session);

	void setId(IDbgpSession session, String identifier);

	String removeId(IDbgpSession session);

	// Message

	String getMessage() throws CoreException;

	void setMessage(String message) throws CoreException;

	/**
	 * 
	 * Returns actual hit count during debugging or -1 if not available.
	 */

	int getHitCount() throws CoreException;

	/**
	 * 
	 * Sets actual hit count during debugging or -1 if not available.
	 * 
	 * 
	 * 
	 * @param value
	 * 
	 * @throws CoreException
	 * 
	 * @deprecated
	 */

	void setHitCount(int value) throws CoreException;

	int getHitCount(IDbgpSession session) throws CoreException;

	void setHitCount(IDbgpSession session, int value) throws CoreException;

	/**
	 * 
	 * Returns the hit count condition
	 **/

	int getHitValue() throws CoreException;

	/**
	 * 
	 * Sets the hit count condition
	 * 
	 * 
	 * 
	 * @param count
	 * 
	 * @throws CoreException
	 */

	void setHitValue(int count) throws CoreException;

	// Hit condition

	int getHitCondition() throws CoreException;

	void setHitCondition(int condition) throws CoreException;

	// Resource name

	String getResourceName() throws CoreException;

	// Expressions

	String getExpression() throws CoreException;

	void setExpression(String expression) throws CoreException;

	/**
	 * 
	 * If expression state is true, then this is not line breakpoint, but
	 * 
	 * conditional breakpoint.
	 */

	boolean getExpressionState() throws CoreException;

	void setExpressionState(boolean state) throws CoreException;

	String[] getUpdatableAttributes();

	void clearSessionInfo();

}
