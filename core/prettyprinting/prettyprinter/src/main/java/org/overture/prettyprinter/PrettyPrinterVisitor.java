package org.overture.prettyprinter;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.node.INode;

public class PrettyPrinterVisitor extends
		QuestionAnswerAdaptor<PrettyPrinterEnv, String>
{

	private QuestionAnswerAdaptor<PrettyPrinterEnv, String> ppDefinition = new PrettyPrinterVisitorDefinitions();

	@Override
	public String defaultPDefinition(PDefinition node, PrettyPrinterEnv question)
			throws AnalysisException
	{
		return node.apply(ppDefinition, question);
	}

	@Override
	public String createNewReturnValue(INode arg0, PrettyPrinterEnv arg1)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createNewReturnValue(Object arg0, PrettyPrinterEnv arg1)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
