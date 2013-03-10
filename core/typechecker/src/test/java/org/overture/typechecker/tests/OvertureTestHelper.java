package org.overture.typechecker.tests;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import junit.framework.Assert;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.parser.lex.LexException;
import org.overture.parser.messages.VDMMessage;
import org.overture.parser.syntax.ParserException;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;
import org.overture.test.framework.results.IMessage;
import org.overture.test.framework.results.Message;
import org.overture.test.framework.results.Result;

public class OvertureTestHelper
{
	@SuppressWarnings("unchecked")
	private static Result<Boolean> convert(
			@SuppressWarnings("rawtypes") TypeCheckResult result)
	{
		if (result.result == null)
		{
			return new Result<Boolean>(false, convert(result.parserResult.warnings), convert(result.parserResult.errors));
		}
		return new Result<Boolean>(true, convert(result.warnings), convert(result.errors));
	}

	public Result<Boolean> typeCheckSl(File file)
	{
		TypeCheckResult<List<AModuleModules>> result = TypeCheckerUtil.typeCheckSl(file);

		checkTypes(result.result);

		return convert(result);
	}

	public Result<Boolean> typeCheckPp(File file)
	{
		TypeCheckResult<List<SClassDefinition>> result = TypeCheckerUtil.typeCheckPp(file);

		checkTypes(result.result);

		return convert(result);
	}

	public Result<Boolean> typeCheckRt(File file) throws ParserException,
			LexException
	{
		TypeCheckResult<List<SClassDefinition>> result = TypeCheckerUtil.typeCheckRt(file);

		checkTypes(result.result);

		return convert(result);
	}

	public static List<IMessage> convert(List<? extends VDMMessage> messages)
	{
		List<IMessage> testMessages = new Vector<IMessage>();

		for (VDMMessage msg : messages)
		{
			testMessages.add(new Message(msg.location.file.getName(), msg.number, msg.location.startLine, msg.location.startPos, msg.message));
		}

		return testMessages;
	}

	static boolean enableCompleteTypeFieldCheck = false;

	public static void checkTypes(Collection<? extends INode> c)
	{
		if (!enableCompleteTypeFieldCheck)
		{
			return;
		}
		try
		{
			if (c != null)
			{
				for (INode element : c)
				{
					if (element != null)
					{
						element.apply(new TypeSetAnalysis());
					}
				}
			}
		} catch (AnalysisException e)
		{
			Assert.fail(e.getMessage());
		}
	}
}
