package org.overture.typechecker.utilities.pattern;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.ASetMultipleBind;
import org.overture.ast.patterns.ATypeMultipleBind;
import org.overture.ast.types.PType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.pattern.PPatternListAssistantTC;

public class PossibleBindTypeFinder extends AnswerAdaptor<PType>
{
	protected ITypeCheckerAssistantFactory af;

	public PossibleBindTypeFinder(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public PType caseASetMultipleBind(ASetMultipleBind mb)
			throws AnalysisException
	{
		return af.createPPatternListAssistant().getPossibleType(mb.getPlist(), mb.getLocation());
	}

	@Override
	public PType caseATypeMultipleBind(ATypeMultipleBind mb)
			throws AnalysisException
	{
		return af.createPPatternListAssistant().getPossibleType(mb.getPlist(), mb.getLocation());
	}

	@Override
	public PType createNewReturnValue(INode node) throws AnalysisException
	{
		assert false : "Should not happen";
		return null;
	}

	@Override
	public PType createNewReturnValue(Object node) throws AnalysisException
	{
		assert false : "Should not happen";
		return null;
	}
}
