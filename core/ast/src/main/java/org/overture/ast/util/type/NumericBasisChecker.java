package org.overture.ast.util.type;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.assistant.IAstAssistantFactory;
import org.overture.ast.factory.AstFactory;
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
 * Used to check if a given type is a Numeric type.
 * 
 * @author gkanos
 */
public class NumericBasisChecker extends AnswerAdaptor<SNumericBasicType>
{
	protected static IAstAssistantFactory af;

	@SuppressWarnings("static-access")
	public NumericBasisChecker(IAstAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public SNumericBasicType defaultSNumericBasicType(SNumericBasicType type)
			throws AnalysisException
	{
		return type;
	}

	@Override
	public SNumericBasicType defaultSBasicType(SBasicType type)
			throws AnalysisException
	{
		return null;
	}

	@Override
	public SNumericBasicType caseABracketType(ABracketType type)
			throws AnalysisException
	{
		return type.getType().apply(THIS);
	}

	@Override
	public SNumericBasicType caseANamedInvariantType(ANamedInvariantType type)
			throws AnalysisException
	{
		return type.getType().apply(THIS);
	}

	@Override
	public SNumericBasicType defaultSInvariantType(SInvariantType type)
			throws AnalysisException
	{
		return null;
	}

	@Override
	public SNumericBasicType caseAOptionalType(AOptionalType type)
			throws AnalysisException
	{
		return type.getType().apply(THIS);
	}

	@Override
	public SNumericBasicType caseAUnionType(AUnionType type)
			throws AnalysisException
	{
		if (!type.getNumDone())
		{
			type.setNumDone(true);
			type.setNumType(AstFactory.newANatNumericBasicType(type.getLocation())); // lightest default
			boolean found = false;

			for (PType t : type.getTypes())
			{
				if (af.createPTypeAssistant().isNumeric(t))
				{
					SNumericBasicType nt = af.createPTypeAssistant().getNumeric(t);

					if (af.createSNumericBasicTypeAssistant().getWeight(nt) > af.createSNumericBasicTypeAssistant().getWeight(type.getNumType()))
					{
						type.setNumType(nt);
					}

					found = true;
				}
			}

			if (!found)
			{
				type.setNumType(null);
			}
		}

		return type.getNumType();
	}

	@Override
	public SNumericBasicType caseAUnknownType(AUnknownType type)
			throws AnalysisException
	{
		return AstFactory.newARealNumericBasicType(type.getLocation());
	}

	@Override
	public SNumericBasicType defaultPType(PType type) throws AnalysisException
	{
		assert false : "Can't getNumeric of a non-numeric";
		return null;
	}

	@Override
	public SNumericBasicType createNewReturnValue(INode type)
			throws AnalysisException
	{
		assert false : "Can't getNumeric of a non-numeric";
		return null;
	}

	@Override
	public SNumericBasicType createNewReturnValue(Object type)
			throws AnalysisException
	{
		assert false : "Can't getNumeric of a non-numeric";
		return null;
	}

}
