/*
 * #%~
 * The Overture Abstract Syntax Tree
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
package org.overture.ast.util.type;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.assistant.IAstAssistantFactory;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.node.INode;
import org.overture.ast.types.ABracketType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SBasicType;
import org.overture.ast.types.SInvariantType;
import org.overture.ast.types.SNumericBasicType;

/**
 * Used to check if a given type is a Numeric type.
 * 
 * @author gkanos
 */
public class NumericBasisChecker extends AnswerAdaptor<SNumericBasicType>
{
	protected final IAstAssistantFactory af;
	protected final String fromModule;

	public NumericBasisChecker(IAstAssistantFactory af, String fromModule)
	{
		this.af = af;
		this.fromModule = fromModule;
	}

	@Override
	public SNumericBasicType defaultSNumericBasicType(SNumericBasicType type)
			throws AnalysisException
	{
		return type;
	}

	@Override
	public SNumericBasicType defaultSBasicType(SBasicType type)
			throws AnalysisException
	{
		return null;
	}

	@Override
	public SNumericBasicType caseABracketType(ABracketType type)
			throws AnalysisException
	{
		return type.getType().apply(THIS);
	}

	@Override
	public SNumericBasicType caseANamedInvariantType(ANamedInvariantType type)
			throws AnalysisException
	{
		return type.getType().apply(THIS);
	}

	@Override
	public SNumericBasicType defaultSInvariantType(SInvariantType type)
			throws AnalysisException
	{
		return null;
	}

	@Override
	public SNumericBasicType caseAOptionalType(AOptionalType type)
			throws AnalysisException
	{
		return type.getType().apply(THIS);
	}

	@Override
	public SNumericBasicType caseAUnionType(AUnionType type)
			throws AnalysisException
	{
		if (!type.getNumDone())
		{
			type.setNumDone(true);
			type.setNumType(AstFactory.newANatNumericBasicType(type.getLocation())); // lightest default
			boolean found = false;

			for (PType t : type.getTypes())
			{
				if (af.createPTypeAssistant().isNumeric(t, fromModule))
				{
					SNumericBasicType nt = af.createPTypeAssistant().getNumeric(t, fromModule);

					if (af.createSNumericBasicTypeAssistant().getWeight(nt) > af.createSNumericBasicTypeAssistant().getWeight(type.getNumType()))
					{
						type.setNumType(nt);
					}

					found = true;
				}
			}

			if (!found)
			{
				type.setNumType(null);
			}
		}

		return type.getNumType();
	}

	@Override
	public SNumericBasicType caseAUnknownType(AUnknownType type)
			throws AnalysisException
	{
		return AstFactory.newARealNumericBasicType(type.getLocation());
	}

	@Override
	public SNumericBasicType defaultPType(PType type) throws AnalysisException
	{
		assert false : "Can't getNumeric of a non-numeric";
		return null;
	}

	@Override
	public SNumericBasicType createNewReturnValue(INode type)
			throws AnalysisException
	{
		assert false : "Can't getNumeric of a non-numeric";
		return null;
	}

	@Override
	public SNumericBasicType createNewReturnValue(Object type)
			throws AnalysisException
	{
		assert false : "Can't getNumeric of a non-numeric";
		return null;
	}

}
