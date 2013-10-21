package org.overture.typechecker.utilities.type;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SMapType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * Used to determine if the of the a type is a map type
 * 
 * @author kel
 */
public class MapBasisChecker extends TypeUnwrapper<Boolean>
{
	/**
	 * Generated serial version
	 */
	private static final long serialVersionUID = 1L;

	protected ITypeCheckerAssistantFactory af;

	public MapBasisChecker(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public Boolean defaultSMapType(SMapType type) throws AnalysisException
	{
		return true;
	}

	@Override
	public Boolean caseANamedInvariantType(ANamedInvariantType type)
			throws AnalysisException
	{
		if (type.getOpaque()) return false;
		return type.getType().apply(THIS);
	}

	@Override
	public Boolean caseAUnionType(AUnionType type) throws AnalysisException
	{
		//return AUnionTypeAssistantTC.getMap(type) != null; //static call
		return af.createAUnionTypeAssistant().getMap(type) != null;//non static call
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