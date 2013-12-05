package org.overture.typechecker.utilities.type;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SInvariantType;
import org.overture.ast.util.PTypeSet;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * Used to get a Set type from a type
 * 
 * @author kel
 */
public class SetTypeFinder extends TypeUnwrapper<ASetType>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected ITypeCheckerAssistantFactory af;

	public SetTypeFinder(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	
	@Override
	public ASetType caseASetType(ASetType type) throws AnalysisException
	{
		return type;
	}
	
	@Override
	public ASetType defaultSInvariantType(SInvariantType type)
			throws AnalysisException
	{
		if (type instanceof ANamedInvariantType)
		{
			
			return ((ANamedInvariantType) type).getType().apply(THIS); 
		}
		else
		{
			return null;
		}
	}
	
	@Override
	public ASetType caseAUnionType(AUnionType type) throws AnalysisException
	{
		ILexLocation location = type.getLocation();

		if (!type.getSetDone())
		{
			type.setSetDone(true); // Mark early to avoid recursion.
			//type.setSetType(PTypeAssistantTC.getSet(AstFactory.newAUnknownType(location)));
			type.setSetType(af.createPTypeAssistant().getSet(AstFactory.newAUnknownType(location)));
			PTypeSet set = new PTypeSet();

			for (PType t : type.getTypes())
			{
				if (af.createPTypeAssistant().isSet(t))
				{
					//set.add(PTypeAssistantTC.getSet(t).getSetof());
					set.add(t.apply(THIS).getSetof());
				}
			}

			type.setSetType(set.isEmpty() ? null
					: AstFactory.newASetType(location, set.getType(location)));
		}

		return type.getSetType();
	}
	
	@Override
	public ASetType caseAUnknownType(AUnknownType type)
			throws AnalysisException
	{
		return AstFactory.newASetType(type.getLocation()); // empty
	}
	
	@Override
	public ASetType defaultPType(PType type) throws AnalysisException
	{
		assert false : "Can't getSet of a non-set";
		return null;
	}
}
