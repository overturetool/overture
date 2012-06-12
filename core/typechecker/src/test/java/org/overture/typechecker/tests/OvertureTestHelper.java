package org.overture.typechecker.tests;

import java.io.File;
import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.modules.AModuleModules;
import org.overture.parser.lex.LexException;
import org.overture.parser.messages.VDMMessage;
import org.overture.parser.syntax.ParserException;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;
import org.overturetool.test.framework.results.IMessage;
import org.overturetool.test.framework.results.Message;
import org.overturetool.test.framework.results.Result;

public class OvertureTestHelper
{
	@SuppressWarnings("unchecked")
	private static Result<Boolean> convert(@SuppressWarnings("rawtypes") TypeCheckResult result)
	{
		if(result.result==null)
		{
			return new Result<Boolean>(false, convert(result.parserResult.warnings), convert(result.parserResult.errors));
		}
		return new Result<Boolean>(true, convert(result.warnings), convert(result.errors));
	}
	public Result<Boolean> typeCheckSl(File file)
	{
		TypeCheckResult<List<AModuleModules>> result = TypeCheckerUtil.typeCheckSl(file);
		return convert(result);
	}

	public Result<Boolean> typeCheckPp(File file)
	{
		TypeCheckResult<List<SClassDefinition>> result = TypeCheckerUtil.typeCheckPp(file);
		return convert(result);
	}

	public Result<Boolean> typeCheckRt(File file) throws ParserException, LexException
	{
		TypeCheckResult<List<SClassDefinition>> result = TypeCheckerUtil.typeCheckRt(file);
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

}
