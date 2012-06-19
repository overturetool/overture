package org.overture.interpreter.tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Vector;

import org.overture.interpreter.util.InterpreterUtil;
import org.overture.interpreter.values.Value;
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
	public void test() throws Exception
	{
		Result<Value> result = null;
		if (mode == ContentModed.String)
		{
			// TypeCheckResult<PExp> exp =TypeCheckerUtil.typeCheckExpression(content);

			Value val = InterpreterUtil.interpret(content);
			result = new Result<Value>(val, new Vector<IMessage>(), new Vector<IMessage>());

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

			Value val = InterpreterUtil.interpret(data);
			result = new Result<Value>(val, new Vector<IMessage>(), new Vector<IMessage>());
		}

		compareResults(result, file.getAbsolutePath());
	}

}
