package org.overture.typechecker.utilities.type;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SInvariantType;
import org.overture.ast.types.SSeqType;
import org.overture.ast.util.PTypeSet;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * Used to get a seq type from a type
 * 
 * @author kel
 */

public class SeqTypeFinder extends TypeUnwrapper<SSeqType>
{

	protected ITypeCheckerAssistantFactory af;

	public SeqTypeFinder(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public SSeqType defaultSSeqType(SSeqType type) throws AnalysisException
	{
		return type;
	}

	@Override
	public SSeqType defaultSInvariantType(SInvariantType type)
			throws AnalysisException
	{
		if (type instanceof ANamedInvariantType)
		{
			// return PTypeAssistantTC.getSeq(type.getType());
			return ((ANamedInvariantType) type).getType().apply(THIS);
		} else
		{
			return null;
		}
	}

	@Override
	public SSeqType caseAUnionType(AUnionType type) throws AnalysisException
	{
		// return AUnionTypeAssistantTC.getSeq(type);
		if (!type.getSeqDone())
		{
			type.setSeqDone(true); // Mark early to avoid recursion.
			// type.setSeqType(PTypeAssistantTC.getSeq(AstFactory.newAUnknownType(type.getLocation())));
			type.setSeqType(af.createPTypeAssistant().getSeq(AstFactory.newAUnknownType(type.getLocation())));
			PTypeSet set = new PTypeSet(af);

			for (PType t : type.getTypes())
			{
				if (af.createPTypeAssistant().isSeq(t))
				{
					set.add(t.apply(THIS).getSeqof());
				}
			}

			type.setSeqType(set.isEmpty() ? null
					: AstFactory.newASeqSeqType(type.getLocation(), set.getType(type.getLocation())));
		}

		return type.getSeqType();
	}

	@Override
	public SSeqType caseAUnknownType(AUnknownType type)
			throws AnalysisException
	{
		return AstFactory.newASeqSeqType(type.getLocation()); // empty
	}

	@Override
	public SSeqType defaultPType(PType type) throws AnalysisException
	{
		assert false : "cannot getSeq from non-seq";
		return null;
	}

}
