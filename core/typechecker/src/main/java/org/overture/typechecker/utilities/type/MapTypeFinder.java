package org.overture.typechecker.utilities.type;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SMapType;
import org.overture.ast.util.PTypeSet;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;

/**
 * Used to get a map type from a type
 * 
 * @author kel
 */
public class MapTypeFinder extends TypeUnwrapper<SMapType>
{

	protected ITypeCheckerAssistantFactory af;

	public MapTypeFinder(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public SMapType defaultSMapType(SMapType type) throws AnalysisException
	{

		return type;
	}

	@Override
	public SMapType caseANamedInvariantType(ANamedInvariantType type)
			throws AnalysisException
	{
		return type.getType().apply(THIS);
	}

	@Override
	public SMapType caseAUnionType(AUnionType type) throws AnalysisException
	{
		ILexLocation location = type.getLocation();

		if (!type.getMapDone())
		{
			type.setMapDone(true); // Mark early to avoid recursion.
			// type.setMapType(PTypeAssistantTC.getMap(AstFactory.newAUnknownType(location)));
			// Rewritten in an none static form.
			type.setMapType(af.createPTypeAssistant().getMap(AstFactory.newAUnknownType(location)));
			PTypeSet from = new PTypeSet();
			PTypeSet to = new PTypeSet();

			for (PType t : type.getTypes())
			{
				if (PTypeAssistantTC.isMap(t))
				{
					// from.add(PTypeAssistantTC.getMap(t).getFrom()); //Original Code
					from.add(t.apply(THIS).getFrom()); // My change George.
					// to.add(PTypeAssistantTC.getMap(t).getTo());//Original code.
					to.add(t.apply(THIS).getTo());// My change George.
				}
			}

			type.setMapType(from.isEmpty() ? null
					: AstFactory.newAMapMapType(location, from.getType(location), to.getType(location)));
		}

		return type.getMapType();
	}

	@Override
	public SMapType caseAUnknownType(AUnknownType type)
			throws AnalysisException
	{
		return AstFactory.newAMapMapType(type.getLocation()); // Unknown |-> Unknown
	}

	@Override
	public SMapType defaultPType(PType node) throws AnalysisException
	{
		assert false : "Can't getMap of a non-map";
		return null;
	}
}