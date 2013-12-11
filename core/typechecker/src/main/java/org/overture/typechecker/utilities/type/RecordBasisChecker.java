package org.overture.typechecker.utilities.type;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SInvariantType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * Used to check if a given type is a Record type.
 * 
 * @author kel
 */
public class RecordBasisChecker extends TypeUnwrapper<Boolean>
{

	protected ITypeCheckerAssistantFactory af;

	public RecordBasisChecker(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
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
			return ((ANamedInvariantType) type).getType().apply(THIS);
		} else if (type instanceof ARecordInvariantType)
		{
			if (type.getOpaque())
			{
				return false;
			}
			return true;
		} else
		{
			return false;
		}
	}

	@Override
	public Boolean caseAUnionType(AUnionType type) throws AnalysisException
	{
		return af.createAUnionTypeAssistant().getRecord(type) != null;
	}

	@Override
	public Boolean defaultPType(PType node) throws AnalysisException
	{
		return false;
	}

}
