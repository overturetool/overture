package org.overture.prettyprinter;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.PDefinition;

public class PrettyPrinterVisitor extends QuestionAnswerAdaptor<PrettyPrinterEnv,String>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9082823353484822934L;

	
	private QuestionAnswerAdaptor<PrettyPrinterEnv,String> ppDefinition = new PrettyPrinterVisitorDefinitions(
			this);
	
	
	@Override
	public String defaultPDefinition(PDefinition node, PrettyPrinterEnv question) throws AnalysisException {
		return node.apply(ppDefinition, question);
	}
}
