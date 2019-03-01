/*
 * #%~
 * The VDM Type Checker
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.typechecker.utilities.type;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.ASeq1SeqType;
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
			boolean allSeq1 = true;

			for (PType t : type.getTypes())
			{
				if (af.createPTypeAssistant().isSeq(t))
				{
					SSeqType st = t.apply(THIS);
					set.add(st.getSeqof());
					allSeq1 = allSeq1 && (st instanceof ASeq1SeqType);
				}
			}

			type.setSeqType(set.isEmpty() ? null :
					allSeq1 ?
						AstFactory.newASeq1SeqType(type.getLocation(), set.getType(type.getLocation())) :		
						AstFactory.newASeqSeqType(type.getLocation(), set.getType(type.getLocation())));
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
