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

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IWatchExpressionResult;
import org.overture.ide.debug.utils.CharOperation;

public class NoWatchExpressionResult implements IWatchExpressionResult
{

	private final String expressionText;

	/**
	 * @param expressionText
	 */
	public NoWatchExpressionResult(String expressionText)
	{
		this.expressionText = expressionText;
	}

	public String[] getErrorMessages()
	{
		return CharOperation.NO_STRINGS;
	}

	public DebugException getException()
	{
		return null;
	}

	public String getExpressionText()
	{
		return expressionText;
	}

	public IValue getValue()
	{
		return null;
	}

	public boolean hasErrors()
	{
		return false;
	}

}
