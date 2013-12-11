package org.overture.typechecker.utilities.type;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SInvariantType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * Used to determine if a type is a Class type
 * 
 * @author kel
 */
public class ClassBasisChecker extends TypeUnwrapper<Boolean>
{

	protected ITypeCheckerAssistantFactory af;

	public ClassBasisChecker(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public Boolean caseAClassType(AClassType type) throws AnalysisException
	{
		return true;
	}

	@Override
	public Boolean defaultSInvariantType(SInvariantType type)
			throws AnalysisException
	{
		if (type instanceof ANamedInvariantType)
		{
			if (type.getOpaque())
			{
				return false;
			}
			return ((ANamedInvariantType) type).getType().apply(THIS);// PTypeAssistantTC.isClass(type.getType());
		} else
		{
			return false;
		}
	}

	@Override
	public Boolean caseAUnionType(AUnionType type) throws AnalysisException
	{
		return af.createAUnionTypeAssistant().getClassType(type) != null;
	}

	@Override
	public Boolean caseAUnknownType(AUnknownType type) throws AnalysisException
	{

		return true;
	}

	@Override
	public Boolean defaultPType(PType node) throws AnalysisException
	{
		return false;
	}

}
