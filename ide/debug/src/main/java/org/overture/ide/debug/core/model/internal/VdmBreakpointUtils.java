/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.overture.ide.debug.core.model.internal;

import org.eclipse.core.runtime.CoreException;
import org.overture.ide.debug.core.model.IVdmBreakpoint;

public class VdmBreakpointUtils {

	/**
	 * Checks that {@link IScriptBreakpoint#getExpressionState()} is true and
	 * {@link IScriptBreakpoint#getExpression()} is not empty
	 * 
	 * @return
	 * @throws CoreException
	 */
	public static boolean isConditional(IVdmBreakpoint bp)
			throws CoreException {
		return isConditional(bp.getExpressionState(), bp.getExpression());
	}

	/**
	 * Checks that {@link expressionState} is true and {@link expression} is not
	 * empty
	 * 
	 * @return
	 * @throws CoreException
	 */
	public static boolean isConditional(boolean expressionState,
			String expression) {
		return expressionState && !StrUtils.isBlank(expression);
	}

}
