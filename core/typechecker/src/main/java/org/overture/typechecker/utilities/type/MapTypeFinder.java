package org.overture.typechecker.utilities.type;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SMapType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.type.ANamedInvariantTypeAssistantTC;
import org.overture.typechecker.assistant.type.AUnionTypeAssistantTC;
import org.overture.typechecker.assistant.type.AUnknownTypeAssistantTC;
import org.overture.typechecker.assistant.type.SMapTypeAssistantTC;

/**
 * Used to get a map type from a type
 * 
 * @author kel
 */
public class MapTypeFinder extends TypeUnwrapper<SMapType>
{
	/**
	 * Generated serial version
	 */
	private static final long serialVersionUID = 1L;

	protected ITypeCheckerAssistantFactory af;

	public MapTypeFinder(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public SMapType defaultSMapType(SMapType type) throws AnalysisException
	{
		return SMapTypeAssistantTC.getMap((SMapType) type);
	}

	@Override
	public SMapType caseANamedInvariantType(ANamedInvariantType type)
			throws AnalysisException
	{
		return ANamedInvariantTypeAssistantTC.getMap((ANamedInvariantType) type);
	}

	@Override
	public SMapType caseAUnionType(AUnionType type) throws AnalysisException
	{
		return AUnionTypeAssistantTC.getMap((AUnionType) type);
	}

	@Override
	public SMapType caseAUnknownType(AUnknownType type)
			throws AnalysisException
	{
		return AUnknownTypeAssistantTC.getMap((AUnknownType) type);
	}

	@Override
	public SMapType defaultPType(PType node) throws AnalysisException
	{
		assert false : "Can't getMap of a non-map";
		return null;
	}
}