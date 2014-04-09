package org.overture.typechecker.utilities.type;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SInvariantType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.type.AUnionTypeAssistantTC;

/**
 * Used to check if a given type is a Record type (ie. a tagged type).
 * 
 * @author ncb
 */
public class TagBasisChecker extends TypeUnwrapper<Boolean>
{
	protected ITypeCheckerAssistantFactory af;

	public TagBasisChecker(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public Boolean defaultSInvariantType(SInvariantType type)
			throws AnalysisException
	{
		if (type instanceof ARecordInvariantType)
		{
			if (type.getOpaque())
			{
				return false;
			}
			
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public Boolean caseAUnionType(AUnionType type) throws AnalysisException
	{
		return false;
	}

	@Override
	public Boolean defaultPType(PType node) throws AnalysisException
	{
		return false;
	}
}
