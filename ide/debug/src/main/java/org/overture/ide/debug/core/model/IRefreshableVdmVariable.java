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

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IVariable;

/**
 * {@link IScriptVariable}s could implement this interface to support value change check.
 */
public interface IRefreshableVdmVariable
{

	/**
	 * Refreshes the value of the variable
	 * 
	 * @param newVariable
	 * @return <code>this</code> if value was successfully refreshed or <code>newVariable</code> if it was not possible
	 *         to refresh value.
	 * @throws DebugException
	 */
	IVariable refreshVariable(IVariable newVariable) throws DebugException;

}
