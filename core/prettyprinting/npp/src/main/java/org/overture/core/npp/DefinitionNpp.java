package org.overture.core.npp;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.node.INode;

public class DefinitionNpp extends QuestionAnswerAdaptor<IndentTracker, String> implements
		IPrettyPrinter
{
	ISymbolTable mytable;
	IPrettyPrinter rootNpp;

	private static String DEFINITION_NOT_FOUND = "ERROR: definition Node not found";
	private static String space = " ";
	
	@Override
	public void setInsTable(ISymbolTable it)
	{
		mytable = it;
		
	}
	
	public DefinitionNpp(NewPrettyPrinter root, ISymbolTable nst)
	{
		rootNpp = root;
		mytable = nst;
	}
	
	
	@Override
	public String caseAValueDefinition(AValueDefinition node,
			IndentTracker question) throws AnalysisException
	{
		return node.toString();
	}

	@Override
	public String createNewReturnValue(INode node, IndentTracker question)
			throws AnalysisException
	{
		return DEFINITION_NOT_FOUND;
	}

	@Override
	public String createNewReturnValue(Object node, IndentTracker question)
			throws AnalysisException
	{
		return DEFINITION_NOT_FOUND;
	}

}
