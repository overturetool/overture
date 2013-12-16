package org.overture.typechecker.utilities.type;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.node.INode;
import org.overture.ast.types.ABracketType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.AVoidType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SInvariantType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;

/**
 * Checks if a type extending class element is of a specific PType.
 * 
 * @author gkanos
 */
public class PTypeExtendedChecker extends
		QuestionAnswerAdaptor<Class<? extends PType>, Boolean>
{
	protected ITypeCheckerAssistantFactory af;

	public PTypeExtendedChecker(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public Boolean caseABracketType(ABracketType type,
			Class<? extends PType> typeclass) throws AnalysisException
	{
		return type.getType().apply(THIS, typeclass);
	}

	@Override
	public Boolean caseANamedInvariantType(ANamedInvariantType type,
			Class<? extends PType> typeclass) throws AnalysisException
	{

		if (type.getOpaque())
		{
			return false;
		}
		return type.getType().apply(THIS, typeclass);
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
		if (typeclass.equals(AVoidType.class))
		{
			return false; // Optionals are never void
		}

		return type.getType().apply(THIS, typeclass);
	}

	@Override
	public Boolean caseAUnknownType(AUnknownType type,
			Class<? extends PType> typeclass) throws AnalysisException
	{
		return true;
	}

	@Override
	public Boolean caseAUnionType(AUnionType type,
			Class<? extends PType> typeclass) throws AnalysisException
	{
		for (PType t : type.getTypes())
		{
			if (PTypeAssistantTC.isType(t, typeclass))
			{
				return true;
			}
		}

		return false;
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
		return false;
	}

	@Override
	public Boolean createNewReturnValue(Object node,
			Class<? extends PType> question) throws AnalysisException
	{
		// TODO Auto-generated method stub
		return false;
	}
}
