package org.overturetool.ast.factory;

import java.util.List;

import org.overture.ast.definitions.PDefinition;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.syntax.DefinitionReader;
import org.overturetool.vdmj.syntax.ParserException;

public class ABusClassDefinitionAssitantPS {

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
