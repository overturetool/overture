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
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SMapType;
import org.overture.ast.util.PTypeSet;
import org.overture.typechecker.TypeChecker;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * Used to get a map type from a type
 * 
 * @author kel
 */
public class MapTypeFinder extends TypeUnwrapper<String, SMapType>
{

	protected ITypeCheckerAssistantFactory af;

	public MapTypeFinder(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public SMapType defaultSMapType(SMapType type, String fromModule) throws AnalysisException
	{
		return type;
	}

	@Override
	public SMapType caseANamedInvariantType(ANamedInvariantType type, String fromModule)
			throws AnalysisException
	{
		if (TypeChecker.isOpaque(type, fromModule)) return null;
		return type.getType().apply(THIS, fromModule);
	}

	@Override
	public SMapType caseAUnionType(AUnionType type, String fromModule) throws AnalysisException
	{
		ILexLocation location = type.getLocation();

		if (!type.getMapDone())
		{
			type.setMapDone(true); // Mark early to avoid recursion.
			// type.setMapType(PTypeAssistantTC.getMap(AstFactory.newAUnknownType(location)));
			// Rewritten in an none static form.
			type.setMapType(af.createPTypeAssistant().getMap(AstFactory.newAUnknownType(location), fromModule));
			PTypeSet from = new PTypeSet(af);
			PTypeSet to = new PTypeSet(af);

			for (PType t : type.getTypes())
			{
				if (af.createPTypeAssistant().isMap(t, fromModule))
				{
					from.add(t.apply(THIS, fromModule).getFrom());
					to.add(t.apply(THIS, fromModule).getTo());
				}
			}

			type.setMapType(from.isEmpty() ? null
					: AstFactory.newAMapMapType(location, from.getType(location), to.getType(location)));
		}

		return type.getMapType();
	}

	@Override
	public SMapType caseAUnknownType(AUnknownType type, String fromModule)
			throws AnalysisException
	{
		return AstFactory.newAMapMapType(type.getLocation()); // Unknown |-> Unknown
	}

	@Override
	public SMapType defaultPType(PType node, String fromModule) throws AnalysisException
	{
		assert false : "Can't getMap of a non-map";
		return null;
	}
}
