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
package org.overture.ide.debug.core.model;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IVariable;

/**
 * {@link IScriptVariable}s could implement this interface to support value
 * change check.
 */
public interface IRefreshableVdmVariable {

	/**
	 * Refreshes the value of the variable
	 * 
	 * @param newVariable
	 * @return <code>this</code> if value was successfully refreshed or
	 *         <code>newVariable</code> if it was not possible to refresh value.
	 * @throws DebugException
	 */
	IVariable refreshVariable(IVariable newVariable) throws DebugException;

}
