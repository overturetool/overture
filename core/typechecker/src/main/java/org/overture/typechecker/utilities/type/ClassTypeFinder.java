package org.overture.typechecker.utilities.type;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SInvariantType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.type.ANamedInvariantTypeAssistantTC;
import org.overture.typechecker.assistant.type.AUnknownTypeAssistantTC;

public class ClassTypeFinder extends TypeUnwrapper<AClassType>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected ITypeCheckerAssistantFactory af;

	public ClassTypeFinder(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	
	@Override
	public AClassType caseAClassType(AClassType type) throws AnalysisException
	{
		return type;
	}
	@Override
	public AClassType defaultSInvariantType(SInvariantType type)
			throws AnalysisException
	{
		if (type instanceof ANamedInvariantType)
		{
			return ANamedInvariantTypeAssistantTC.getClassType((ANamedInvariantType) type);
		}
		else
		{
			return null;
		}
	}
	@Override
	public AClassType caseAUnionType(AUnionType type) throws AnalysisException
	{
		//return AUnionTypeAssistantTC.getClassType(type);
		return af.createAUnionTypeAssistant().getClassType(type);
	}

	@Override
	public AClassType caseAUnknownType(AUnknownType type)
			throws AnalysisException
	{
		return AUnknownTypeAssistantTC.getClassType(type);
	}
	
	@Override
	public AClassType defaultPType(PType tyoe) throws AnalysisException
	{
		assert false : "Can't getClass of a non-class";
		return null;
	}
}
