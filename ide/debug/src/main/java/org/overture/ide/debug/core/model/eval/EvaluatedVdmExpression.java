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
package org.overture.ide.debug.core.model.eval;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IErrorReportingExpression;
import org.eclipse.debug.core.model.IValue;

public class EvaluatedVdmExpression extends PlatformObject implements
		IErrorReportingExpression
{
	public static String[] getErrorMessages(IVdmEvaluationResult result)
	{
		if (result == null)
		{
			return new String[0];
		}
		String messages[] = result.getErrorMessages();
		if (messages.length > 0)
		{
			return messages;
		}
		DebugException exception = result.getException();
		if (exception != null)
		{
			return new String[] { exception.getMessage() };
		}
		return new String[0];
	}

	private final IVdmEvaluationResult result;

	public EvaluatedVdmExpression(IVdmEvaluationResult result)
	{
		if (result == null)
		{
			throw new IllegalArgumentException();
		}

		this.result = result;
	}

	public String[] getErrorMessages()
	{
		return getErrorMessages(result);
	}

	public boolean hasErrors()
	{
		return result.hasErrors();
	}

	public IDebugTarget getDebugTarget()
	{
		return result.getThread().getDebugTarget();
	}

	public String getExpressionText()
	{
		return result.getSnippet();
	}

	public IValue getValue()
	{
		return result.getValue();
	}

	public ILaunch getLaunch()
	{
		return getDebugTarget().getLaunch();
	}

	public String getModelIdentifier()
	{
		return getDebugTarget().getModelIdentifier();
	}

	public void dispose()
	{

	}
}
