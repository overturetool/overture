package org.overture.typechecker.utilities.type;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.node.INode;
import org.overture.ast.types.ABracketType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SInvariantType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.type.ABracketTypeAssistantTC;
import org.overture.typechecker.assistant.type.ANamedInvariantTypeAssistantTC;
import org.overture.typechecker.assistant.type.AOptionalTypeAssistantTC;
import org.overture.typechecker.assistant.type.AUnionTypeAssistantTC;
import org.overture.typechecker.assistant.type.AUnknownTypeAssistantTC;

/**
 *Don't know yet what it does.
 * 
 * @author kel
 */
public class PTypeExtendedChecker extends QuestionAnswerAdaptor<Class<? extends PType>, Boolean>
{
	private static final long serialVersionUID = 1L;
	protected ITypeCheckerAssistantFactory af;
	
	public PTypeExtendedChecker(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	
	@Override
	public Boolean caseABracketType(ABracketType type,
			Class<? extends PType> typeclass) throws AnalysisException
	{
		return ABracketTypeAssistantTC.isType(type, typeclass);
	}
	
	@Override
	public Boolean caseANamedInvariantType(ANamedInvariantType type,
			Class<? extends PType> typeclass) throws AnalysisException
	{
		return ANamedInvariantTypeAssistantTC.isType(type, typeclass);
	}
	
	@Override
	public Boolean defaultSInvariantType(SInvariantType type,
			Class<? extends PType> typeclass) throws AnalysisException
	{
		return typeclass.isInstance(type);
	}
	
	@Override
	public Boolean caseAOptionalType(AOptionalType type,
			Class<? extends PType> typeclass) throws AnalysisException
	{
		return AOptionalTypeAssistantTC.isType(type, typeclass);
	}
	
	@Override
	public Boolean caseAUnknownType(AUnknownType type,
			Class<? extends PType> typeclass) throws AnalysisException
	{
		return AUnknownTypeAssistantTC.isType(type, typeclass);
	}
	
	@Override
	public Boolean caseAUnionType(AUnionType type,
			Class<? extends PType> typeclass) throws AnalysisException
	{
		return AUnionTypeAssistantTC.isType(type, typeclass);
	}
	
	@Override
	public Boolean defaultPType(PType type, Class<? extends PType> typeclass)
			throws AnalysisException
	{
		return typeclass.isInstance(type);
	}

	@Override
	public Boolean createNewReturnValue(INode node,
			Class<? extends PType> question) throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean createNewReturnValue(Object node,
			Class<? extends PType> question) throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}
}
