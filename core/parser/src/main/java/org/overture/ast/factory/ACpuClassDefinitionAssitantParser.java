package org.overture.ast.factory;

import java.util.List;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.parser.lex.LexException;
import org.overture.parser.lex.LexTokenReader;
import org.overture.parser.syntax.DefinitionReader;
import org.overture.parser.syntax.ParserException;

public class ACpuClassDefinitionAssitantParser {

	private static String defs =
			"operations " +
			"public CPU:(<FP>|<FCFS>) * real ==> CPU " +
			"	CPU(policy, speed) == is not yet specified; " +
			"public deploy: ? ==> () " +
			"	deploy(obj) == is not yet specified; " +
			"public deploy: ? * seq of char ==> () " +
			"	deploy(obj, name) == is not yet specified; " +
			"public setPriority: ? * nat ==> () " +
			"	setPriority(opname, priority) == is not yet specified;";
	
	
	public static List<PDefinition> operationDefs() throws ParserException, LexException {
		LexTokenReader ltr = new LexTokenReader(defs, Dialect.VDM_PP);
		DefinitionReader dr = new DefinitionReader(ltr);
		dr.setCurrentModule("CPU");
		return dr.readDefinitions();
	}

}
