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
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.SSetType;
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
public class SetTypeFinder extends TypeUnwrapper<SSetType>
{

	protected ITypeCheckerAssistantFactory af;

	public SetTypeFinder(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public SSetType defaultSSetType(SSetType type) throws AnalysisException
	{
		return type;
	}

	@Override
	public SSetType defaultSInvariantType(SInvariantType type)
			throws AnalysisException
	{
		if (type instanceof ANamedInvariantType)
		{

			return ((ANamedInvariantType) type).getType().apply(THIS);
		} else
		{
			return null;
		}
	}

	@Override
	public SSetType caseAUnionType(AUnionType type) throws AnalysisException
	{
		ILexLocation location = type.getLocation();

		if (!type.getSetDone())
		{
			type.setSetDone(true); // Mark early to avoid recursion.
			// type.setSetType(PTypeAssistantTC.getSet(AstFactory.newAUnknownType(location)));
			type.setSetType(af.createPTypeAssistant().getSet(AstFactory.newAUnknownType(location)));
			PTypeSet set = new PTypeSet(af);

			for (PType t : type.getTypes())
			{
				if (af.createPTypeAssistant().isSet(t))
				{
					// set.add(PTypeAssistantTC.getSet(t).getSetof());
					set.add(t.apply(THIS).getSetof());
				}
			}

			type.setSetType(set.isEmpty() ? null
					: AstFactory.newASetSetType(location, set.getType(location)));
		}

		return type.getSetType();
	}

	@Override
	public SSetType caseAUnknownType(AUnknownType type)
			throws AnalysisException
	{
		return AstFactory.newASetSetType(type.getLocation()); // empty
	}

	@Override
	public SSetType defaultPType(PType type) throws AnalysisException
	{
		assert false : "Can't getSet of a non-set";
		return null;
	}
}
