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
package org.overture.ast.assistant;

import org.overture.ast.analysis.intf.IAnswer;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.assistant.definition.PAccessSpecifierAssistant;
import org.overture.ast.assistant.definition.PDefinitionAssistant;
import org.overture.ast.assistant.pattern.PPatternAssistant;
import org.overture.ast.assistant.type.AUnionTypeAssistant;
import org.overture.ast.assistant.type.PTypeAssistant;
import org.overture.ast.assistant.type.SNumericBasicTypeAssistant;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.types.SNumericBasicType;
import org.overture.ast.util.pattern.AllVariableNameLocator;
import org.overture.ast.util.type.HashChecker;
import org.overture.ast.util.type.NumericBasisChecker;
import org.overture.ast.util.type.NumericFinder;

//TODO Add assistant Javadoc
/**
 * This is the main AST assistant factory. everyone ultimately inherits from here.
 * 
 * @author ldc
 */

public class AstAssistantFactory implements IAstAssistantFactory
{
	@Override
	public PAccessSpecifierAssistant createPAccessSpecifierAssistant()
	{
		return new PAccessSpecifierAssistant(this);
	}

	@Override
	public PDefinitionAssistant createPDefinitionAssistant()
	{
		return new PDefinitionAssistant(this);
	}

	@Override
	public PPatternAssistant createPPatternAssistant(String fromModule)
	{
		return new PPatternAssistant(this, fromModule);
	}

	// @Override
	// public ABracketTypeAssistant createABracketTypeAssistant()
	// {
	// return new ABracketTypeAssistant(this);
	// }

	// @Override
	// public ANamedInvariantTypeAssistant createANamedInvariantTypeAssistant()
	// {
	// return new ANamedInvariantTypeAssistant(this);
	// }

	// @Override
	// public AOptionalTypeAssistant createAOptionalTypeAssistant()
	// {
	// return new AOptionalTypeAssistant(this);
	// }

	// @Override
	// public AParameterTypeAssistant createAParameterTypeAssistant()
	// {
	// return new AParameterTypeAssistant(this);
	// }

	@Override
	public AUnionTypeAssistant createAUnionTypeAssistant()
	{
		return new AUnionTypeAssistant(this);
	}

	// @Override
	// public AUnknownTypeAssistant createAUnknownTypeAssistant()
	// {
	// return new AUnknownTypeAssistant(this);
	// }

	@Override
	public PTypeAssistant createPTypeAssistant()
	{
		return new PTypeAssistant(this);
	}

	@Override
	public SNumericBasicTypeAssistant createSNumericBasicTypeAssistant()
	{
		return new SNumericBasicTypeAssistant(this);
	}

	// visitors

	@Override
	public IAnswer<LexNameList> getAllVariableNameLocator(String fromModule)
	{
		return new AllVariableNameLocator(this, fromModule);
	}

	@Override
	public IQuestionAnswer<String, Boolean> getNumericFinder()
	{
		return new NumericFinder(this);
	}

	@Override
	public IQuestionAnswer<String, SNumericBasicType> getNumericBasisChecker()
	{
		return new NumericBasisChecker(this);
	}

	@Override
	public IAnswer<Integer> getHashChecker()
	{
		return new HashChecker(this);
	}

}
