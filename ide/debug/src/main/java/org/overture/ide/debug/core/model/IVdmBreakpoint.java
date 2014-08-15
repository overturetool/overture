/*
 * #%~
 * org.overture.ide.debug
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.ide.debug.core.model;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.overture.ide.debug.core.dbgp.IDbgpSession;
import org.overture.ide.debug.core.dbgp.breakpoints.IDbgpBreakpoint;

public interface IVdmBreakpoint extends IBreakpoint
{

	int HIT_CONDITION_GREATER_OR_EQUAL = IDbgpBreakpoint.HIT_CONDITION_GREATER_OR_EQUAL;

	int HIT_CONDITION_EQUAL = IDbgpBreakpoint.HIT_CONDITION_EQUAL;

	int HIT_CONDITION_MULTIPLE = IDbgpBreakpoint.HIT_CONDITION_MULTIPLE;

	/**
	 * @return
	 * @throws CoreException
	 * @deprecated
	 */

	String getIdentifier() throws CoreException;

	/**
	 * @param id
	 * @throws CoreException
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
	 * Returns actual hit count during debugging or -1 if not available.
	 */

	int getHitCount() throws CoreException;

	/**
	 * Sets actual hit count during debugging or -1 if not available.
	 * 
	 * @param value
	 * @throws CoreException
	 */

	void setHitCount(int value) throws CoreException;

	int getHitCount(IDbgpSession session) throws CoreException;

	void setHitCount(IDbgpSession session, int value) throws CoreException;

	/**
	 * Returns the hit count condition
	 **/

	int getHitValue() throws CoreException;

	/**
	 * Sets the hit count condition
	 * 
	 * @param count
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
	 * If expression state is true, then this is not line breakpoint, but conditional breakpoint.
	 */

	boolean getExpressionState() throws CoreException;

	void setExpressionState(boolean state) throws CoreException;

	String[] getUpdatableAttributes();

	void clearSessionInfo();

}
