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

package org.overture.interpreter.runtime.validation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.overture.ast.definitions.ASystemClassDefinition;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.messages.Console;
import org.overture.interpreter.messages.rtlog.RTMessage.MessageType;
import org.overture.interpreter.runtime.ClassInterpreter;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.scheduler.AsyncThread;
import org.overture.interpreter.scheduler.BasicSchedulableThread;
import org.overture.interpreter.scheduler.ISchedulableThread;
import org.overture.interpreter.scheduler.SystemClock;
import org.overture.interpreter.values.NameValuePairMap;
import org.overture.interpreter.values.ObjectValue;
import org.overture.interpreter.values.OperationValue;
import org.overture.interpreter.values.Value;

public class BasicRuntimeValidator implements IRuntimeValidatior
{

	final List<ConjectureDefinition> conjectures = new ArrayList<ConjectureDefinition>();
	final List<String[]> variables = new ArrayList<String[]>();

	public void init(ClassInterpreter classInterpreter)
	{
		TimingInvariantsParser parser = new TimingInvariantsParser();

		for (File file : classInterpreter.getSourceFiles())
		{
			try
			{
				conjectures.addAll(parser.parse(file));
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for (ConjectureDefinition cd : conjectures)
		{
			Console.out.println(cd.toString());
		}

	}

	public void validate(OperationValue operationValue, MessageType type)
	{
		if (operationValue.isStatic)
		{
			return;
		}

		if (!conjectures.isEmpty())
		{
			ISchedulableThread ct = BasicSchedulableThread.getThread(Thread.currentThread());

			for (ConjectureDefinition conj : conjectures)
			{

				conj.process(operationValue.name.getName(), operationValue.classdef.getName().getName(), type, SystemClock.getWallTime(), ct.getId(), operationValue.getSelf().objectReference);
			}
		}

	}

	public void bindSystemVariables(ASystemClassDefinition systemDefinition,
			IInterpreterAssistantFactory af)
	{

		List<String[]> variablesTemp = filterVariablesInSystem(systemDefinition.getName().getName(), variables);
		Context ctxt = af.createSClassDefinitionAssistant().getStatics(systemDefinition);

		for (String[] strings : variablesTemp)
		{
			Value v = digInCtxt(strings, ctxt);
			for (ConjectureDefinition definition : conjectures)
			{
				definition.associateVariable(strings, v);
			}

		}

	}

	private Value digInCtxt(String[] strings, Context ctxt)
	{

		List<String> rest = new ArrayList<String>();
		for (int i = 1; i < strings.length; i++)
		{
			rest.add(strings[i]);
		}

		for (ILexNameToken name : ctxt.keySet())
		{
			if (name.getName().equals(rest.get(0)))
			{
				Value v = ctxt.get(name);
				if (rest.size() > 1)
				{
					return digInVariable(v, rest.subList(1, rest.size()), ctxt);
				} else
				{
					return v;
				}
			}
		}

		return null;

	}

	private Value digInVariable(Value value, List<String> rest, Context ctxt)
	{

		try
		{
			ObjectValue ov = value.objectValue(ctxt);
			NameValuePairMap nvpm = ov.members;

			for (ILexNameToken name : nvpm.keySet())
			{
				if (name.getName().equals(rest.get(0)))
				{
					Value v = nvpm.get(name);

					if (rest.size() > 1)
					{
						return digInVariable(v, rest.subList(1, rest.size()), ctxt);
					} else
					{
						return v;
					}
				}
			}

		} catch (ValueException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	private List<String[]> filterVariablesInSystem(String name,
			List<String[]> variables)
	{
		for (int i = 0; i < variables.size(); i++)
		{

			if (!variables.get(i)[0].equals(name))
			{
				variables.remove(i);
				i--;
			}

		}
		return variables;
	}

	public void validateAsync(OperationValue op, AsyncThread t)
	{
		if (!conjectures.isEmpty())
		{

			for (ConjectureDefinition conj : conjectures)
			{
				conj.process(op.name.getName(), op.classdef.getName().getName(), MessageType.Request, SystemClock.getWallTime(), t.getId(), t.getObject().objectReference);
			}
		}

	}

	public String stop()
	{

		StringBuffer sb = new StringBuffer();

		for (ConjectureDefinition cj : conjectures)
		{
			sb.append(cj.getLogFormat());
		}
		return sb.toString();
	}

	public List<ConjectureDefinition> getConjectures()
	{
		return this.conjectures;
	}

}
