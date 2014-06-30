package org.overture.pog.tests.newtests;

import java.util.Collection;

import org.junit.runners.Parameterized.Parameters;
import org.overture.core.tests.AllExamplesHelper;
import org.overture.core.tests.AllExamplesHelper.ExampleAstData;
import org.overture.parser.lex.LexException;
import org.overture.parser.syntax.ParserException;
import org.overture.pog.tests.InputsProvider;

public class PogAllExamplesTest
{
	

	@Parameters(name = "{index} : {0}")
	public static Collection<Object[]> testData() throws ParserException, LexException {
		Collection<ExampleAstData> examples = AllExamplesHelper.getCorrectExampleAsts();
		
		
		
		return InputsProvider.allExamples();
	}

}
