package org.overture.typechecker.assistant.pattern;

import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.PType;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class PatternListTC extends Vector<PPattern>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8197456560367128159L;

	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public PatternListTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public void typeResolve(
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) throws AnalysisException
	{

		for (PPattern pPattern : this)
		{
			PPatternAssistantTC.typeResolve(pPattern, rootVisitor, question);
		}
	}

	public void unResolve()
	{

		for (PPattern pPattern : this)
		{
			PPatternAssistantTC.unResolve(pPattern);
		}
	}

}
