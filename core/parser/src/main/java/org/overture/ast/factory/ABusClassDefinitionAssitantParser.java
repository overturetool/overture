package org.overture.ast.factory;

import java.util.List;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.parser.lex.LexException;
import org.overture.parser.lex.LexTokenReader;
import org.overture.parser.syntax.DefinitionReader;
import org.overture.parser.syntax.ParserException;

public class ABusClassDefinitionAssitantParser {

	private static String defs =
			"operations " +
			"public BUS:(<FCFS>|<CSMACD>) * real * set of CPU ==> BUS " +
			"	BUS(policy, speed, cpus) == is not yet specified;";
	
	public static List<PDefinition> operationDefs()
			throws ParserException, LexException
		{
			LexTokenReader ltr = new LexTokenReader(defs, Dialect.VDM_PP);
			DefinitionReader dr = new DefinitionReader(ltr);
			dr.setCurrentModule("BUS");
			return dr.readDefinitions();
		}
}
