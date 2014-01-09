package org.overture.ast.util.type;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.assistant.IAstAssistantFactory;
import org.overture.ast.node.INode;
import org.overture.ast.types.ABracketType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SBasicType;
import org.overture.ast.types.SInvariantType;
import org.overture.ast.types.SNumericBasicType;

/**
 * Used to find if a type is Numeric.
 * 
 * @author gkanos
 */

public class NumericFinder extends AnswerAdaptor<Boolean>
{
	protected static IAstAssistantFactory af;

	@SuppressWarnings("static-access")
	public NumericFinder(IAstAssistantFactory af)
	{
		this.af = af;
	}
	
	@Override
	public Boolean defaultSBasicType(SBasicType type) throws AnalysisException
	{
		SBasicType bType = type;
		return bType instanceof SNumericBasicType;
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
			return false;
		return type.getType().apply(THIS);
	}
	@Override
	public Boolean defaultSInvariantType(SInvariantType type)
			throws AnalysisException
	{
		return false;
	}
	
	@Override
	public Boolean caseAOptionalType(AOptionalType type)
			throws AnalysisException
	{
		return type.getType().apply(THIS);
	}
	
	@Override
	public Boolean caseAUnionType(AUnionType type) throws AnalysisException
	{
		return af.createPTypeAssistant().getNumeric(type) != null;
	}
	@Override
	public Boolean caseAUnknownType(AUnknownType type) throws AnalysisException
	{
		return true;
	}
	
	@Override
	public Boolean defaultPType(PType type) throws AnalysisException
	{
		return false;
	}

	@Override
	public Boolean createNewReturnValue(INode node) throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean createNewReturnValue(Object node) throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}

}
