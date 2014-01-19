package org.overture.typechecker.utilities.type;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.node.INode;
import org.overture.ast.types.ABracketType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.AUnresolvedType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SInvariantType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;

/**
 * This class implements a way to find type of the general PType
 * 
 * @author kel
 */
public class PTypeFinder extends QuestionAnswerAdaptor<String, PType>
{

	protected ITypeCheckerAssistantFactory af;

	public PTypeFinder(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public PType caseABracketType(ABracketType type, String typename)
			throws AnalysisException
	{
		return type.getType().apply(THIS, typename);
	}

	@Override
	public PType caseANamedInvariantType(ANamedInvariantType type,
			String typename) throws AnalysisException
	{

		if (type.getOpaque())
		{
			return null;
		}
		return type.getType().apply(THIS, typename);
	}

	@Override
	public PType caseARecordInvariantType(ARecordInvariantType type,
			String typename) throws AnalysisException
	{
		if (type.getOpaque())
		{
			return null;
		}

		if (typename.indexOf('`') > 0)
		{
			return type.getName().getFullName().equals(typename) ? type : null;
		} else
		{
			// Local typenames aren't qualified with the local module name
			return type.getName().getName().equals(typename) ? type : null;
		}
	}

	@Override
	public PType defaultSInvariantType(SInvariantType type, String typename)
			throws AnalysisException
	{
		return null;
	}

	@Override
	public PType caseAOptionalType(AOptionalType type, String typename)
			throws AnalysisException
	{
		return type.getType().apply(THIS, typename);
	}

	@Override
	public PType caseAUnionType(AUnionType type, String typename)
			throws AnalysisException
	{
		for (PType t : type.getTypes())
		{
			PType rt = af.createPTypeAssistant().isType(t, typename);

			if (rt != null)
			{
				return rt;
			}
		}

		return null;
	}

	@Override
	public PType caseAUnknownType(AUnknownType type, String typename)
			throws AnalysisException
	{
		return null;// Isn't any particular type? comment from original source
	}

	@Override
	public PType caseAUnresolvedType(AUnresolvedType type, String typename)
			throws AnalysisException
	{
		return type.getName().getFullName().equals(typename) ? type : null;
	}

	@Override
	public PType defaultPType(PType type, String typename)
			throws AnalysisException
	{
		return (af.createPTypeAssistant().toDisplay(type).equals(typename)) ? type
				: null;

	}

	@Override
	public PType createNewReturnValue(INode node, String question)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PType createNewReturnValue(Object node, String question)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}
}
