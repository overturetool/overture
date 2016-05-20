/***************************************************************************
 *
 *	Copyright (c) 2009 IHA
 *
 *	Author: Kenneth Lausdahl and Augusto Ribeiro
 *
 *	This file is part of VDMJ.
 *
 *	VDMJ is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	VDMJ is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with VDMJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 **************************************************************************/

package org.overture.interpreter.runtime;

import java.io.PrintWriter;

import org.overture.ast.definitions.ASystemClassDefinition;
import org.overture.config.Settings;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.messages.Console;
import org.overture.interpreter.messages.rtlog.RTMessage.MessageType;
import org.overture.interpreter.runtime.validation.BasicRuntimeValidator;
import org.overture.interpreter.runtime.validation.IRuntimeValidatior;
import org.overture.interpreter.scheduler.AsyncThread;
import org.overture.interpreter.values.OperationValue;

public class RuntimeValidator
{

	public static IRuntimeValidatior validator;
	private static PrintWriter logfile = null;

	private RuntimeValidator() {
	}

	public static void init(ClassInterpreter classInterpreter)
	{
		if (Settings.timingInvChecks)
		{
			validator = new BasicRuntimeValidator();
			validator.init(classInterpreter);
		}
	}

	public static void bindSystemVariables(
			ASystemClassDefinition systemDefinition,
			IInterpreterAssistantFactory af)
	{
		if (Settings.timingInvChecks)
		{
			if (validator != null)
			{
				validator.bindSystemVariables(systemDefinition, af);
			}

		}
	}

	public static void validate(OperationValue operationValue, MessageType type)
	{
		if (Settings.timingInvChecks)
		{
			if (validator != null)
			{
				validator.validate(operationValue, type);
			}
		}
	}

	public static void validateAsync(OperationValue operationValue,
			AsyncThread t)
	{
		if (Settings.timingInvChecks)
		{
			if (validator != null)
			{
				validator.validateAsync(operationValue, t);
			}
		}
	}

	public static void stop()
	{
		if (Settings.timingInvChecks)
		{
			if (validator != null)
			{
				String res = validator.stop();
				if (logfile != null)
				{
					logfile.write(res);
					logfile.flush();
					logfile.close();
				} else
				{
					Console.out.print(res);
				}

			}
		}
	}

	public static void setLogFile(PrintWriter out)
	{
		logfile = out;
	}

}
