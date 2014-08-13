package org.overture.pog.visitors;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.node.INode;

public class VdmLocaleExtractor extends AnswerAdaptor<String> implements
		ILocaleExtractVisitor
{

	@Override
	public String createNewReturnValue(INode node) throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createNewReturnValue(Object node) throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}

}
