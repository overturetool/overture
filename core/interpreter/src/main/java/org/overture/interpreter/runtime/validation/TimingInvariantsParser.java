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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.overture.interpreter.messages.rtlog.RTMessage.MessageType;
import org.overture.interpreter.runtime.validation.ValueValidationExpression.BinaryOps;

public class TimingInvariantsParser
{
	int counter = 1;

	public List<ConjectureDefinition> parse(File file) throws IOException
	{
		StringBuffer contents = new StringBuffer();
		BufferedReader reader = null;
		boolean enabled = false;

		try
		{
			reader = new BufferedReader(new FileReader(file));
			String text;

			// repeat until all lines is read
			while ((text = reader.readLine()) != null)
			{
				if (text.replaceAll("\\s", " ").startsWith("/* timing invariants"))
				{
					enabled = true;
					continue;
				}

				if (text.replaceAll("\\s", "").startsWith("*/"))
				{
					enabled = false;
				}

				if (enabled && text.trim().length() > 0)
				{
					contents.append("\n" + text);
				}
			}
			return parse(contents.toString());

		} finally
		{
			try
			{
				if (reader != null)
				{
					reader.close();
				}
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}

	}

	private List<ConjectureDefinition> parse(String contents)
	{
		List<ConjectureDefinition> defs = new Vector<ConjectureDefinition>();

		try
		{
			int indexOfLParn;

			while ((indexOfLParn = contents.indexOf('(')) != -1)
			{
				String propName = contents.substring(0, indexOfLParn).trim();
				contents = contents.substring(indexOfLParn + 1);
				int end = contents.indexOf(';') - 1;
				String propBody = contents.substring(0, end);
				contents = contents.substring(end + 2);

				String[] elements = propBody.split(",");
				List<String> elemsFirst = Arrays.asList(elements);
				List<String> elems;
				if (elemsFirst.size() > 3)
				{
					elems = new ArrayList<String>();
					elems.add(elemsFirst.get(0) + "," + elemsFirst.get(1));
					elems.add(elemsFirst.get(2));
					elems.add(elemsFirst.get(3));
				} else
				{
					elems = elemsFirst;
				}

				List<IValidationExpression> args = decodeArg(elems.get(0));

				OperationValidationExpression initOp;
				ValueValidationExpression initValue = null;
				OperationValidationExpression endOp;
				int interval;

				initOp = (OperationValidationExpression) args.get(0);
				if (args.size() > 1)
				{
					initValue = (ValueValidationExpression) args.get(1);
				}

				args = decodeArg(elems.get(1));

				endOp = (OperationValidationExpression) args.get(0);

				args = decodeArg(elems.get(2));

				interval = ((IntegerContainer) args.get(0)).getValue();

				if (propName.equals("deadlineMet"))
				{

					defs.add(new DeadlineMet("C" + counter++, initOp, initValue, endOp, (int) (interval * 1E6)));
				} else if (propName.equals("separate"))
				{
					defs.add(new Separate("C" + counter++, initOp, initValue, endOp, (int) (interval * 1E6)));
				}

			}
		} catch (IndexOutOfBoundsException e)
		{
			e.printStackTrace();
		}

		return defs;
	}

	private List<IValidationExpression> decodeArg(String string)
	{
		List<IValidationExpression> res = new ArrayList<IValidationExpression>();
		string = string.trim();
		if (string.startsWith("("))
		{
			string = string.substring(1, string.length() - 1);
			String[] dividedString = string.split(",");
			for (String s : dividedString)
			{
				res.addAll(decodeArg(s));
			}

		} else if (string.startsWith("#"))
		{
			MessageType type = MessageType.Request;

			if (string.startsWith("#req"))
			{
				type = MessageType.Request;
			} else if (string.startsWith("#act"))
			{
				type = MessageType.Activate;
			} else if (string.startsWith("#fin"))
			{
				type = MessageType.Completed;
			}

			string = string.substring(string.indexOf("(") + 1, string.length() - 1);

			String[] name = string.split("`");

			res.add(new OperationValidationExpression(name[1], name[0], type));

		} else if (string.matches("\\d+\\W\\w\\w"))
		{
			res.add(new IntegerContainer(Integer.valueOf(string.substring(0, string.indexOf(' ')))));
		} else if (string.matches("\\w+`\\w+[.\\w+]*\\W+\\w+`\\w+"))
		{
			ValueValidationExpression.BinaryOps binop = BinaryOps.EQ;

			if (string.contains(BinaryOps.EQ.syntax))
			{
				binop = BinaryOps.EQ;
			} else if (string.contains(BinaryOps.GREATER.syntax))
			{
				binop = BinaryOps.GREATER;
			} else if (string.contains(BinaryOps.GREATEREQ.syntax))
			{
				binop = BinaryOps.GREATEREQ;
			} else if (string.contains(BinaryOps.LESS.syntax))
			{
				binop = BinaryOps.LESS;
			} else if (string.contains(BinaryOps.LESSEQ.syntax))
			{
				binop = BinaryOps.LESSEQ;
			}

			String left = string.substring(0, string.indexOf(binop.syntax));
			String right = string.substring(string.indexOf(binop.syntax)
					+ binop.syntax.length());

			String[] leftName;
			if (left.contains("`"))
			{
				leftName = left.split("`");
			} else
			{
				leftName = left.split(".");
			}

			String[] rightName;
			if (right.contains("`"))
			{
				rightName = right.split("`");
			} else
			{
				rightName = right.split(".");
			}
			res.add(new ValueValidationExpression(leftName, binop, rightName));
		}
		return res;

	}

}
