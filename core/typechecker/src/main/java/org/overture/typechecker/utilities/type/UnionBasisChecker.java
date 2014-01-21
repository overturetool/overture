package org.overture.typechecker.utilities.type;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.node.INode;
import org.overture.ast.types.ABracketType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.SInvariantType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * Used to determine if a type is a Union type
 * 
 * @author kel
 */
public class UnionBasisChecker extends AnswerAdaptor<Boolean>
{

	protected ITypeCheckerAssistantFactory af;

	public UnionBasisChecker(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public Boolean caseABracketType(ABracketType type) throws AnalysisException
	{
		return type.getType().apply(THIS);
	}

	@Override
	public Boolean caseANamedInvariantType(ANamedInvariantType type)
			throws AnalysisException
	{
		if (type.getOpaque())
		{
			return false;
		}
		return type.getType().apply(THIS);
	}

	@Override
	public Boolean defaultSInvariantType(SInvariantType node)
			throws AnalysisException
	{
		return false;
	}

	@Override
	public Boolean caseAUnionType(AUnionType type) throws AnalysisException
	{
		return true;
	}

	@Override
	public Boolean createNewReturnValue(INode node) throws AnalysisException
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Boolean createNewReturnValue(Object node) throws AnalysisException
	{
		// TODO Auto-generated method stub
		return false;
	}

}
