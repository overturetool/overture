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

import java.util.List;

import org.overture.ast.definitions.ABusClassDefinition;
import org.overture.ast.definitions.ACpuClassDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.lex.LexNameToken;
import org.overture.parser.lex.LexException;
import org.overture.parser.lex.LexTokenReader;
import org.overture.parser.syntax.DefinitionReader;
import org.overture.parser.syntax.ParserException;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AstFactoryTC extends AstFactory
{

	public static SClassDefinition newACpuClassDefinition(
			ITypeCheckerAssistantFactory assistantFactory)
			throws ParserException, LexException
	{
		ACpuClassDefinition result = new ACpuClassDefinition();
		initClassDefinition(result, new LexNameToken("CLASS", "CPU", new LexLocation()), new LexNameList(), new AstFactoryTC().operationDefs());

		return result;
	}

	public static SClassDefinition newABusClassDefinition(
			ITypeCheckerAssistantFactory assistantFactory)
			throws ParserException, LexException
	{
		ABusClassDefinition result = new ABusClassDefinition();
		initClassDefinition(result, new LexNameToken("CLASS", "BUS", new LexLocation()), new LexNameList(), new AstFactoryTC().operationDefsBus());

		result.setInstance(result);

		return result;
	}
	
	public static final long CPU_MAX_FREQUENCY = 1000000000; // 1GHz
	private String defs = "operations "
			+ "public CPU:(<FP>|<FCFS>) * real ==> CPU "
			+ "	CPU(policy, speed) == is not yet specified; "
			+ "public deploy: ? ==> () "
			+ "	deploy(obj) == is not yet specified; "
			+ "public deploy: ? * seq of char ==> () "
			+ "	deploy(obj, name) == is not yet specified; "
			+ "public setPriority: ? * nat ==> () "
			+ "	setPriority(opname, priority) == is not yet specified;";

	public List<PDefinition> operationDefs() throws ParserException,
			LexException
	{
		LexTokenReader ltr = new LexTokenReader(defs, Dialect.VDM_PP);
		DefinitionReader dr = new DefinitionReader(ltr);
		dr.setCurrentModule("CPU");
		return dr.readDefinitions();
	}
	
	
	private String defsBus = "operations "
			+ "public BUS:(<FCFS>|<CSMACD>) * real * set of CPU ==> BUS "
			+ "	BUS(policy, speed, cpus) == is not yet specified;";

	public List<PDefinition> operationDefsBus() throws ParserException,
			LexException
	{
		LexTokenReader ltr = new LexTokenReader(defsBus, Dialect.VDM_PP);
		DefinitionReader dr = new DefinitionReader(ltr);
		dr.setCurrentModule("BUS");
		return dr.readDefinitions();
	}

}
