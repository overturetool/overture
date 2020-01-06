/**
 * 
 */
package org.overture.typechecker.assistant.expression;

import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.assistant.IAstAssistant;
import org.overture.ast.expressions.PExp;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.util.PTypeSet;
import org.overture.typechecker.Environment;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class PExpAssistantTC implements IAstAssistant
{
	protected final ITypeCheckerAssistantFactory af;

	public PExpAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public PTypeSet exitCheck(List<PExp> args, Environment env) throws AnalysisException
	{
		PTypeSet result = new PTypeSet(af);
		IQuestionAnswer<Environment, PTypeSet> checker = af.getExitTypeCollector();

		for (PExp exp: args)
		{
			result.addAll(exp.apply(checker, env));
		}

		return result;
	}

	public PTypeSet exitCheckMultipleBindList(List<PMultipleBind> bindList, Environment env) throws AnalysisException
	{
		PTypeSet result = new PTypeSet(af);
		IQuestionAnswer<Environment, PTypeSet> checker = af.getExitTypeCollector();

		for (PMultipleBind mbind: bindList)
		{
			result.addAll(mbind.apply(checker, env));
		}

		return result;
	}
}
