package org.overturetool.vdmj.runtime;

import org.overture.ast.expressions.PExp;

import org.overture.interpreter.ast.expressions.ABreakpointExpInterpreter;
import org.overture.interpreter.ast.expressions.PExpInterpreter;
import org.overture.interpreter.ast.types.ABooleanBasicTypeInterpreter;
import org.overturetool.util.PostProcessingCopyAdaptor;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.lex.LexIntegerToken;
import org.overturetool.vdmj.lex.LexToken;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.lex.VDMToken;
import org.overturetool.vdmj.syntax.ExpressionReader;
import org.overturetool.vdmj.syntax.ParserException;

public class BreakpointParser
{
	public static PExpInterpreter parse(String trace, org.overturetool.interpreter.vdmj.lex.LexLocation location) throws LexException, ParserException
	{
		LexTokenReader ltr = new LexTokenReader(trace, Dialect.VDM_SL);

		ltr.push();
		LexToken tok = ltr.nextToken();

		PExpInterpreter parsed= null;
		switch (tok.type)
		{
			case EQUALS:
				parsed = readHitCondition(ltr, BreakpointCondition.EQ,location);
				break;

			case GT:
				parsed = readHitCondition(ltr, BreakpointCondition.GT,location);
				break;

			case GE:
				parsed = readHitCondition(ltr, BreakpointCondition.GE,location);
				break;

			case MOD:
				parsed = readHitCondition(ltr, BreakpointCondition.MOD,location);
				break;

			default:
				ltr.pop();
				ExpressionReader reader = new ExpressionReader(ltr);
    			reader.setCurrentModule(location.module);
    			PExp exp = reader.readExpression();
    			if(exp!=null)
    			{
    				parsed = (PExpInterpreter) exp.apply(new PostProcessingCopyAdaptor());
    			}
    			break;
		}
		
		return null;
	}
	
	private static ABreakpointExpInterpreter readHitCondition(
			LexTokenReader ltr, BreakpointCondition cond,org.overturetool.interpreter.vdmj.lex.LexLocation location)
			throws ParserException, LexException
		{
			LexToken arg = ltr.nextToken();

			if (arg.isNot(VDMToken.NUMBER))
			{
				throw new ParserException(2279, "Invalid breakpoint hit condition", location, 0);
			}

			LexIntegerToken num = (LexIntegerToken)arg;
			return new ABreakpointExpInterpreter(new ABooleanBasicTypeInterpreter(location,false),location,null, cond, num.value);
		}
}
