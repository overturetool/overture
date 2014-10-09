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
 * Represents a 'hash' script type
 */
public class HashVdmType extends CollectionVdmType
{

	private static String HASH = "map"; //$NON-NLS-1$

	public HashVdmType()
	{
		super(HASH);
	}

	protected String buildDetailString(IVariable variable)
			throws DebugException
	{
		StringBuffer sb = new StringBuffer();

		sb.append(getVariableName(variable));
		sb.append("|->"); //$NON-NLS-1$
		sb.append(variable.getValue().getValueString());

		return sb.toString();
	}

	protected char getCloseBrace()
	{
		return '}';
	}

	protected char getOpenBrace()
	{
		return '{';
	}

	/**
	 * Returns the variable name (key) for the hash element.
	 * <p>
	 * Subclasses may override this method if they need to process the variable name before it is displayed.
	 * </p>
	 */
	protected String getVariableName(IVariable variable) throws DebugException
	{
		return variable.getName();
	}

	@Override
	public String formatValue(IVdmValue value)
	{
		return super.formatValue(value);
	}
}
