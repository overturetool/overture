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
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.node.INode;
import org.overture.ast.types.*;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * Checks if a type is smaller than a specifier type.
 * 
 * @author kel
 */
public class NarrowerThanComparator extends
		QuestionAnswerAdaptor<AAccessSpecifierAccessSpecifier, Boolean>
{

	protected ITypeCheckerAssistantFactory af;

	public NarrowerThanComparator(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public Boolean caseABracketType(ABracketType type,
			AAccessSpecifierAccessSpecifier accessSpecifier)
			throws AnalysisException
	{
		return type.getType().apply(this, accessSpecifier);
	}

	@Override
	public Boolean caseAProductType(AProductType type,
									AAccessSpecifierAccessSpecifier accessSpecifier)
			throws AnalysisException {
		for (PType t : type.getTypes()) {
			if (t.apply(this, accessSpecifier)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public Boolean caseAFunctionType(AFunctionType type,
			AAccessSpecifierAccessSpecifier accessSpecifier)
			throws AnalysisException
	{

		for (PType t : type.getParameters())
		{
			if (t.apply(this, accessSpecifier)) // (PTypeAssistantTC.narrowerThan(t, accessSpecifier))
			{
				return true;
			}
		}

		return type.getResult().apply(this, accessSpecifier); // PTypeAssistantTC.narrowerThan(type.getResult(),accessSpecifier);
	}

	@Override
	public Boolean caseAOperationType(AOperationType type,
			AAccessSpecifierAccessSpecifier accessSpecifier)
			throws AnalysisException
	{
		for (PType t : type.getParameters())
		{
			if (t.apply(this, accessSpecifier))// (PTypeAssistantTC.narrowerThan(t, accessSpecifier))
			{
				return true;
			}
		}

		return type.getResult().apply(this, accessSpecifier); // PTypeAssistantTC.narrowerThan(type.getResult(),accessSpecifier);
	}

	@Override
	public Boolean caseAOptionalType(AOptionalType type,
			AAccessSpecifierAccessSpecifier accessSpecifier)
			throws AnalysisException
	{
		// return AOptionalTypeAssistantTC.narrowerThan(type, accessSpecifier);
		return type.getType().apply(this, accessSpecifier);
	}

	@Override
	public Boolean defaultSSeqType(SSeqType type,
			AAccessSpecifierAccessSpecifier accessSpecifier)
			throws AnalysisException
	{
		return type.getSeqof().apply(this, accessSpecifier);
	}

	@Override
	public Boolean defaultSSetType(SSetType type,
			AAccessSpecifierAccessSpecifier accessSpecifier)
			throws AnalysisException
	{
		return type.getSetof().apply(this, accessSpecifier);
	}

	@Override
	public Boolean caseAUnionType(AUnionType type,
			AAccessSpecifierAccessSpecifier accessSpecifier)
			throws AnalysisException
	{
		for (PType t : type.getTypes())
		{
			if (t.apply(this, accessSpecifier)) // (PTypeAssistantTC.narrowerThan(t, accessSpecifier))
			{
				return true;
			}
		}

		return false;
	}

	@Override
	public Boolean caseAUnknownType(AUnknownType type,
			AAccessSpecifierAccessSpecifier accessSpecifier)
			throws AnalysisException
	{
		return false;
	}

	@Override
	public Boolean caseANamedInvariantType(ANamedInvariantType type,
			AAccessSpecifierAccessSpecifier accessSpecifier)
			throws AnalysisException
	{
		if (type.getInNarrower())
		{
			return false;
		}

		type.setInNarrower(true);
		boolean result = false;

		if (type.getDefinitions().size() > 0)
		{
			for (PDefinition d : type.getDefinitions())
			{
				if (af.createPAccessSpecifierAssistant().narrowerThan(d.getAccess(), accessSpecifier))
				{
					result = true;
					break;
				}
			}
		} else if (type.getType().getDefinitions().size() == 0)
		{
			result = type.apply(this, accessSpecifier)
					|| af.createPTypeAssistant().narrowerThanBaseCase(type, accessSpecifier);// PTypeAssistantTC.narrowerThan(type,
																								// accessSpecifier)
		} else
		{
			for (PDefinition d : type.getType().getDefinitions())
			{
				if (af.createPAccessSpecifierAssistant().narrowerThan(d.getAccess(), accessSpecifier))

				{
					result = true;
					break;
				}
			}

		}

		type.setInNarrower(false);
		return result;
	}

	@Override
	public Boolean caseARecordInvariantType(ARecordInvariantType type,
			AAccessSpecifierAccessSpecifier accessSpecifier)
			throws AnalysisException
	{
		if (type.getInNarrower())
		{
			return false;
		} else
		{
			type.setInNarrower(true);
		}

		boolean result = false;

		if (type.getDefinitions().size() > 0)
		{
			for (PDefinition d : type.getDefinitions())
			{
				if (af.createPAccessSpecifierAssistant().narrowerThan(d.getAccess(), accessSpecifier))
				{
					result = true;
					break;
				}
			}
		} else
		{
			for (AFieldField field : type.getFields())
			{
				if (field.getType().apply(this, accessSpecifier))// (PTypeAssistantTC.narrowerThan(field.getType(),
																	// accessSpecifier))
				{
					result = true;
					break;
				}
			}
		}

		type.setInNarrower(false);
		return result;
	}

	@Override
	public Boolean defaultSInvariantType(SInvariantType type,
			AAccessSpecifierAccessSpecifier accessSpecifier)
			throws AnalysisException
	{
		return af.createPTypeAssistant().narrowerThanBaseCase(type, accessSpecifier);
	}

	@Override
	public Boolean defaultPType(PType type,
			AAccessSpecifierAccessSpecifier accessSpecifier)
			throws AnalysisException
	{
		return af.createPTypeAssistant().narrowerThanBaseCase(type, accessSpecifier);
	}

	@Override
	public Boolean createNewReturnValue(INode node,
			AAccessSpecifierAccessSpecifier question) throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean createNewReturnValue(Object node,
			AAccessSpecifierAccessSpecifier question) throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}

}
