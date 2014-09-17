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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SInvariantType;
import org.overture.ast.util.PTypeSet;
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
		// return af.createAUnionTypeAssistant().getRecord(type) != null;
		if (!type.getRecDone())
		{
			type.setRecDone(true); // Mark early to avoid recursion.
			type.setRecType(af.createPTypeAssistant().getRecord(AstFactory.newAUnknownType(type.getLocation())));

			// Build a record type with the common fields of the contained
			// record types, making the field types the union of the original
			// fields' types...

			Map<String, PTypeSet> common = new HashMap<String, PTypeSet>();

			for (PType t : type.getTypes())
			{
				if (af.createPTypeAssistant().isRecord(t))
				{
					for (AFieldField f : af.createPTypeAssistant().getRecord(t).getFields())
					{
						PTypeSet current = common.get(f.getTag());

						if (current == null)
						{
							common.put(f.getTag(), new PTypeSet(f.getType(), af));
						} else
						{
							current.add(f.getType());
						}
					}
				}
			}

			List<AFieldField> fields = new Vector<AFieldField>();

			for (String tag : common.keySet())
			{
				LexNameToken tagname = new LexNameToken("?", tag, type.getLocation());
				fields.add(AstFactory.newAFieldField(tagname, tag, common.get(tag).getType(type.getLocation()), false));
			}

			type.setRecType(fields.isEmpty() ? null
					: AstFactory.newARecordInvariantType(type.getLocation(), fields));
		}

		return type.getRecType() != null;
	}

	@Override
	public Boolean defaultPType(PType node) throws AnalysisException
	{
		return false;
	}

}
