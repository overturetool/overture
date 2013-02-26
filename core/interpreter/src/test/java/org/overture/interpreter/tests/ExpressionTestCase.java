package org.overture.interpreter.tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Vector;

import org.overture.ast.expressions.PExp;
import org.overture.ast.lex.Dialect;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.interpreter.util.InterpreterUtil;
import org.overture.interpreter.values.Value;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;
import org.overturetool.test.framework.results.IMessage;
import org.overturetool.test.framework.results.Result;

public class ExpressionTestCase extends InterpreterBaseTestCase
{
	public ExpressionTestCase()
	{
		super();

	}

	public ExpressionTestCase(File file)
	{
		super(file);
	}

	public ExpressionTestCase(File rootSource, String name, String content)
	{
		super(rootSource, name, content);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		Settings.dialect = Dialect.VDM_SL;
		Settings.release = Release.VDM_10;
	}
	
	@Override
	protected File createResultFile(String filename)
	{
		return new File(filename + ".result");
	}

	@Override
	protected File getResultFile(String filename)
	{
		return new File(filename + ".result");
	}

	@Override
	public void test() throws Exception
	{
		if(content==null && file == null)
		{
			return;
		}
		
		Result<Value> result = null;
		String input = null;
		if (mode == ContentModed.String)
		{
			input = content;
		} else if (mode == ContentModed.File)
		{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String data = "";
			String text = null;
			while ((text = reader.readLine()) != null)
			{
				data += text;
			}
			reader.close();

			input = data;
		}

		TypeCheckResult<PExp> tcResult = TypeCheckerUtil.typeCheckExpression(input);
		if (!tcResult.parserResult.errors.isEmpty()
				|| !tcResult.errors.isEmpty())
		{		
			fail("Expression did not pass type check!.");
		}
		Value val = InterpreterUtil.interpret(input);
		result = new Result<Value>(val, new Vector<IMessage>(), new Vector<IMessage>());

		compareResults(result, file.getAbsolutePath());
	}

}
