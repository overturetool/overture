/*
 * #%~
 * Integration of the ProB Solver for the VDM Interpreter
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
package org.overture.interpreter.tests;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.overture.interpreter.runtime.ContextException;
import org.overture.interpreter.runtime.ICollectedRuntimeExceptions;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.values.SeqValue;
import org.overture.interpreter.values.Value;
import org.overture.parser.messages.LocatedException;
import org.overture.test.framework.results.IMessage;
import org.overture.test.framework.results.Message;
import org.overture.test.framework.results.Result;

public class ExecutionToResultTranslator
{
	public static Result<Value> wrapValue(Exception e)
	{
		Result<String> result = wrap(e);
		return new Result<Value>(new SeqValue(result.getStringResult()), result.warnings, result.errors);
	}

	public static Result<String> wrap(Exception e)
	{
		List<IMessage> errors = new Vector<IMessage>();
		String message = e.getMessage();
		if (e instanceof ICollectedRuntimeExceptions)
		{
			List<String> messages = new Vector<String>();
			for (Exception err : ((ICollectedRuntimeExceptions) e).getCollectedExceptions())
			{
				if (err instanceof ContextException)
				{
					ContextException ce = (ContextException) err;
					errors.add(new Message(ce.location.getFile().getName(), ce.number, ce.location.getStartLine(), ce.location.getStartPos(), ce.getMessage()));
				} else if (err instanceof ValueException)
				{
					ValueException ve = (ValueException) err;
					errors.add(new Message("?", ve.number, 0, 0, ve.getMessage()));
				} else if (err instanceof LocatedException)
				{
					LocatedException le = (LocatedException) err;
					errors.add(new Message(le.location.getFile().getName(), le.number, le.location.getStartLine(), le.location.getStartPos(), le.getMessage()));
				} else
				{
					messages.add(err.getMessage());
				}
			}
			Collections.sort(messages);
			message = messages.toString();
		}
		return new Result<String>(message, new Vector<IMessage>(), errors);
	}
}
