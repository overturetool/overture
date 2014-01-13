package org.overture.typechecker.utilities.type;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.node.INode;
import org.overture.ast.types.ABracketType;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.AQuoteType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.AUndefinedType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.AUnresolvedType;
import org.overture.ast.types.AVoidReturnType;
import org.overture.ast.types.AVoidType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SInvariantType;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SSeqType;
import org.overture.ast.util.PTypeSet;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;

/**
 * This class checks if 2 objects of a Type are equal.
 * 
 * @author kel
 */
public class TypeEqualityChecker extends QuestionAnswerAdaptor<Object, Boolean>
{

	protected ITypeCheckerAssistantFactory af;

	public TypeEqualityChecker(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public Boolean caseABracketType(ABracketType type, Object other)
			throws AnalysisException
	{

		return type.getType().apply(this, other);// FIXME: The use of THIS doesn't seems to work with
													// QuestionAnswerAdaptor.

	}

	@Override
	public Boolean caseAClassType(AClassType type, Object other)
			throws AnalysisException
	{
		other = deBracket((PType) other);

		if (other instanceof AClassType)
		{
			AClassType oc = (AClassType) other;
			return type.getName().equals(oc.getName()); // NB. name only
		}

		return false;
	}

	@Override
	public Boolean caseAFunctionType(AFunctionType type, Object other)
			throws AnalysisException
	{
		other = deBracket((PType) other);

		if (!(other instanceof AFunctionType))
		{
			return false;
		}

		AFunctionType fo = (AFunctionType) other;
		return type.getPartial() == fo.getPartial() && // FIXME:The Below statement doesn't work correct. I cannot Apply
														// with this syntax.
				type.getResult().apply(this, fo.getResult()) && // type.getParameters().apply(this,fo.getParameters()));
				// PTypeAssistantTC.equals(type.getResult(),fo.getResult()) &&
				PTypeAssistantTC.equals(type.getParameters(), fo.getParameters());

	}

	@Override
	public Boolean defaultSInvariantType(SInvariantType type, Object other)
			throws AnalysisException
	{
		if (type instanceof ANamedInvariantType)
		{

			other = deBracket((PType) other);

			if (other instanceof ANamedInvariantType)
			{
				ANamedInvariantType nother = (ANamedInvariantType) other;
				return ((ANamedInvariantType) type).getName().equals(nother.getName());
			}

			return false;
		} else if (type instanceof ARecordInvariantType)
		{

			other = PTypeAssistantTC.deBracket(other);

			if (other instanceof ARecordInvariantType)
			{
				ARecordInvariantType rother = (ARecordInvariantType) other;
				return ((ARecordInvariantType) type).getName().equals(rother.getName()); // NB. identical
			}

			return false;
		} else
		{
			other = deBracket((PType) other);
			return type.getClass() == other.getClass();
		}
	}

	@Override
	public Boolean defaultSMapType(SMapType type, Object other)
			throws AnalysisException
	{
		// return SMapTypeAssistantTC.equals(type, other);
		other = PTypeAssistantTC.deBracket(other);

		if (other.getClass() == type.getClass()) // inmaps too
		{
			SMapType mt = (SMapType) other;
			// return PTypeAssistantTC.equals(type.getFrom(),mt.getFrom()) && PTypeAssistantTC.equals(type.getTo(),
			// mt.getTo());
			return type.getFrom().apply(this, mt.getFrom())
					&& type.getTo().apply(this, mt.getTo()); // FIXME:The same problem here. THIS doesn't seem to work.
		}

		return false;
	}

	@Override
	public Boolean caseAOperationType(AOperationType type, Object other)
			throws AnalysisException
	{
		other = PTypeAssistantTC.deBracket(other);

		if (!(other instanceof AOperationType))
		{
			return false;
		}

		AOperationType oother = (AOperationType) other;
		return type.getResult().apply(this, oother.getResult())
				&& PTypeAssistantTC.equals(type.getParameters(), oother.getParameters());
		// FIXME:The above statement cannot be changed to apply form.
		// PTypeAssistantTC.equals(type.getResult(),oother.getResult()) &&
		// PTypeAssistantTC.equals(type.getParameters(), oother.getParameters()));
	}

	@Override
	public Boolean caseAOptionalType(AOptionalType type, Object other)
			throws AnalysisException
	{
		if (other instanceof AOptionalType)
		{
			AOptionalType oo = (AOptionalType) other;
			return type.getType().apply(this, oo.getType());

		}

		return false;
	}

	@Override
	public Boolean caseAProductType(AProductType type, Object other)
			throws AnalysisException
	{
		other = deBracket((PType) other);

		if (other instanceof AProductType)
		{
			AProductType pother = (AProductType) other;
			return PTypeAssistantTC.equals(type.getTypes(), pother.getTypes());
			// FIXME: apply method is not applicable here.
		}

		return false;
	}

	@Override
	public Boolean caseAQuoteType(AQuoteType type, Object other)
			throws AnalysisException
	{
		other = deBracket((PType) other);

		if (other instanceof AQuoteType)
		{
			AQuoteType qother = (AQuoteType) other;
			return type.getValue().getValue().equals(qother.getValue().getValue());
		}

		return false;
	}

	@Override
	public Boolean defaultSSeqType(SSeqType type, Object other)
			throws AnalysisException
	{
		other = deBracket((PType) other);

		if (other instanceof SSeqType)
		{
			SSeqType os = (SSeqType) other;
			// NB. Empty sequence is the same type as any sequence
			return type.getEmpty() || os.getEmpty()
					|| PTypeAssistantTC.equals(type.getSeqof(), os.getSeqof());
		}

		return false;
	}

	@Override
	public Boolean caseASetType(ASetType type, Object other)
			throws AnalysisException
	{
		// return ASetTypeAssistantTC.equals(type, other);
		other = deBracket((PType) other);

		if (other instanceof ASetType)
		{
			ASetType os = (ASetType) other;
			// NB empty set same type as any set
			return type.getEmpty() || os.getEmpty()
					|| type.getSetof().apply(this, os.getSetof());
		}

		return false;
	}

	@Override
	public Boolean caseAUndefinedType(AUndefinedType type, Object other)
			throws AnalysisException
	{
		other = deBracket((PType) other);

		return other instanceof AUndefinedType;
	}

	@Override
	public Boolean caseAUnionType(AUnionType type, Object other)
			throws AnalysisException
	{

		other = deBracket((PType) other);
		PTypeSet types = new PTypeSet(type.getTypes());

		if (other instanceof AUnionType)
		{
			AUnionType uother = (AUnionType) other;

			for (PType t : uother.getTypes())
			{
				if (!types.contains(t))
				{
					return false;
				}
			}

			return true;
		}

		return types.contains(other);
	}

	@Override
	public Boolean caseAUnknownType(AUnknownType type, Object other)
			throws AnalysisException
	{
		return true;
	}

	@Override
	public Boolean caseAUnresolvedType(AUnresolvedType type, Object other)
			throws AnalysisException
	{
		other = deBracket((PType) other);

		if (other instanceof AUnresolvedType)
		{
			AUnresolvedType nother = (AUnresolvedType) other;
			return type.getName().equals(nother.getName());
		}

		if (other instanceof ANamedInvariantType)
		{
			ANamedInvariantType nother = (ANamedInvariantType) other;
			return type.getName().equals(nother.getName());
		}

		return false;
	}

	@Override
	public Boolean caseAVoidType(AVoidType type, Object other)
			throws AnalysisException
	{
		// return AVoidTypeAssistantTC.equals(type, other);
		other = deBracket((PType) other);

		return other instanceof AVoidType;
	}

	@Override
	public Boolean caseAVoidReturnType(AVoidReturnType type, Object other)
			throws AnalysisException
	{
		other = deBracket((PType) other);

		return other instanceof AVoidReturnType;
	}

	@Override
	public Boolean defaultPType(PType type, Object other)
			throws AnalysisException
	{
		other = deBracket((PType) other);
		return type.getClass() == other.getClass();
	}

	public static PType deBracket(PType other)
	{

		while (other instanceof ABracketType)
		{
			other = ((ABracketType) other).getType();
		}

		return other;
	}

	@Override
	public Boolean createNewReturnValue(INode node, Object question)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean createNewReturnValue(Object node, Object question)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}
}
