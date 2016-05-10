package org.overture.interpreter.tests.utils;

import java.util.ArrayList;
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
	private ExecutionToResultTranslator() {
	}

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
			List<Exception> collectedExceptions = new ArrayList<Exception>(((ICollectedRuntimeExceptions) e).getCollectedExceptions());
			for (Exception err : collectedExceptions)
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
					err.printStackTrace();
				}
			}
			Collections.sort(messages);
			message = messages.toString();
		}
		return new Result<String>(message, new Vector<IMessage>(), errors);
	}
}
