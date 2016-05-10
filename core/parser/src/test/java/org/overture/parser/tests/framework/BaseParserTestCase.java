/*
 * #%~
 * The VDM parser
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
package org.overture.parser.tests.framework;

import java.io.File;
import java.util.List;
import java.util.Vector;

import org.overture.ast.lex.Dialect;
import org.overture.ast.node.INode;
import org.overture.ast.util.test.OutlineCompatabilityChecker;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.parser.lex.LexException;
import org.overture.parser.lex.LexTokenReader;
import org.overture.parser.messages.VDMError;
import org.overture.parser.messages.VDMWarning;
import org.overture.parser.syntax.ParserException;
import org.overture.parser.syntax.SyntaxReader;
import org.overture.test.framework.ResultTestCase;
import org.overture.test.framework.results.IMessage;
import org.overture.test.framework.results.Message;
import org.overture.test.framework.results.Result;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class BaseParserTestCase<T extends SyntaxReader, R> extends
		ResultTestCase<R>
{

	public final static boolean DEBUG = true;

	public BaseParserTestCase()
	{
		super();
	}

	public BaseParserTestCase(File file)
	{
		super(file);
	}

	public BaseParserTestCase(File rootSource, String name, String content)
	{
		super(rootSource, name, content);
	}

	public void test() throws ParserException, LexException
	{
		if (file != null)
		{
			internal(new LexTokenReader(file, Settings.dialect));
		} else if (content != null)
		{
			internal(new LexTokenReader(content, Settings.dialect));
		}
	}

	protected abstract T getReader(LexTokenReader ltr);

	protected abstract R read(T reader) throws ParserException, LexException;

	protected abstract String getReaderTypeName();

	@Override
	protected void setUp() throws Exception
	{
		Settings.dialect = Dialect.VDM_PP;
		Settings.release = Release.VDM_10;
	}

	protected void internal(LexTokenReader ltr) throws ParserException,
			LexException
	{
		T reader = null;
		R result = null;
		try
		{
			reader = getReader(ltr);
			result = read(reader);

			System.out.println();

			List<IMessage> warnings = new Vector<>();
			List<IMessage> errors = new Vector<>();

			collectParserErrorsAndWarnings(reader, errors, warnings);
			Result<R> resultFinal = new Result<>(result, warnings, errors);

			if (result instanceof List || result instanceof INode)
			{
				OutlineCompatabilityChecker outlineCompatabilityChecker = new OutlineCompatabilityChecker();
				if (!outlineCompatabilityChecker.check(resultFinal.result))
				{
					fail("Does not support outline failed on classes: "
							+ outlineCompatabilityChecker.getInvalidNodesDescription());
				}
			}

			compareResults(resultFinal, file.getAbsolutePath());

		} finally
		{
			if (!hasRunBefore())
			{
				setHasRunBefore(true);
				if (DEBUG)
				{
					System.out.println("============================================================================================================");

					System.out.println("|");
					System.out.println("|\t\t" + getReaderTypeName() + "s");
					System.out.println("|___________________________________________________________________________________________________________");

					System.out.println(pad("Parsed " + getReaderTypeName(), 20)
							+ " - " + pad(getReturnName(result), 35) + ": "
							+ pad(result + "", 35).replace('\n', ' ')
							+ " from \"" + (content + "").replace('\n', ' ')
							+ "\"");
					System.out.flush();
				}
			}
		}
	}

	protected abstract void setHasRunBefore(boolean b);

	protected abstract boolean hasRunBefore();

	@SuppressWarnings("rawtypes")
	private String getReturnName(Object result)
	{
		if (result == null)
		{
			return "null";
		}
		String name = result.getClass().getSimpleName();
		if (result instanceof List)
		{
			try
			{
				name += "<" + ((List) result).get(0).getClass().getSimpleName()
						+ ">";
			} catch (Exception e)
			{
			}
		}
		return name;
	}

	public static String pad(String text, int length)
	{
		if (text == null)
		{
			text = "null";
		}
		while (text.length() < length)
		{
			text += " ";
		}
		return text;
	}

	public void skip()
	{
	};

	@Override
	protected File createResultFile(String filename)
	{
		if (mode == ContentModed.String)
		{
			String tmp = getName().substring(name.indexOf('_') + 1);
			tmp = File.separatorChar + "" + tmp.substring(0, tmp.indexOf('_'));
			return new File(filename + "_results" + tmp + ".result");
		}
		return new File(filename + ".result");
	}

	@Override
	protected File getResultFile(String filename)
	{
		if (mode == ContentModed.String)
		{
			String tmp = getName().substring(name.indexOf('_') + 1);
			tmp = File.separatorChar + "" + tmp.substring(0, tmp.indexOf('_'));
			return new File(filename + "_results" + tmp + ".result");
		}
		return new File(filename + ".result");
	}

	private static void collectParserErrorsAndWarnings(SyntaxReader reader,
			List<IMessage> errors, List<IMessage> warnings)
	{
		if (reader != null && reader.getErrorCount() > 0)
		{
			for (VDMError msg : reader.getErrors())
			{
				errors.add(new Message(msg.location.getFile().getName(), msg.number, msg.location.getStartLine(), msg.location.getStartPos(), msg.message));
			}
		}

		if (reader != null && reader.getWarningCount() > 0)
		{
			for (VDMWarning msg : reader.getWarnings())
			{
				warnings.add(new Message(msg.location.getFile().getName(), msg.number, msg.location.getStartLine(), msg.location.getStartPos(), msg.message));
			}
		}
	}

	public void encodeResult(Object result, Document doc, Element resultElement)
	{

	}

	public R decodeResult(Node node)
	{
		return null;
	}

	@Override
	protected boolean assertEqualResults(R expected, R actual)
	{
		return true;
	}
}
