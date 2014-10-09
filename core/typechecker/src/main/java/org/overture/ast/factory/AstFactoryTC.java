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
package org.overture.ast.factory;

import org.overture.ast.definitions.ABusClassDefinition;
import org.overture.ast.definitions.ACpuClassDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.lex.LexNameToken;
import org.overture.parser.lex.LexException;
import org.overture.parser.syntax.ParserException;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AstFactoryTC extends AstFactory
{

	public static SClassDefinition newACpuClassDefinition(
			ITypeCheckerAssistantFactory assistantFactory)
			throws ParserException, LexException
	{
		ACpuClassDefinition result = new ACpuClassDefinition();
		initClassDefinition(result, new LexNameToken("CLASS", "CPU", new LexLocation()), new LexNameList(), assistantFactory.createACpuClassDefinitionAssistant().operationDefs());

		return result;
	}

	public static SClassDefinition newABusClassDefinition(
			ITypeCheckerAssistantFactory assistantFactory)
			throws ParserException, LexException
	{
		ABusClassDefinition result = new ABusClassDefinition();
		initClassDefinition(result, new LexNameToken("CLASS", "BUS", new LexLocation()), new LexNameList(), assistantFactory.createABusClassDefinitionAssistant().operationDefs());

		result.setInstance(result);

		return result;
	}

}
