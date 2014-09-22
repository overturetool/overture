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
package org.overture.ide.debug.core.model.internal;

import org.eclipse.core.runtime.CoreException;
import org.overture.ide.debug.core.model.IVdmBreakpoint;

public class VdmBreakpointUtils
{

	/**
	 * Checks that {@link IScriptBreakpoint#getExpressionState()} is true and {@link IScriptBreakpoint#getExpression()}
	 * is not empty
	 * 
	 * @return
	 * @throws CoreException
	 */
	public static boolean isConditional(IVdmBreakpoint bp) throws CoreException
	{
		return isConditional(bp.getExpressionState(), bp.getExpression());
	}

	/**
	 * Checks that {@link expressionState} is true and {@link expression} is not empty
	 * 
	 * @return
	 * @throws CoreException
	 */
	public static boolean isConditional(boolean expressionState,
			String expression)
	{
		return expressionState && !StrUtils.isBlank(expression);
	}

}
