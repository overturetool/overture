package org.overturetool.cgisa;

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.AnswerAdaptor;

public class GetTypeNameVisitor extends AnswerAdaptor<String>
{

	@Override
	public String createNewReturnValue(INode node) throws AnalysisException
	{
		return "";
	}

	@Override
	public String createNewReturnValue(Object node) throws AnalysisException
	{
		return "";
	}
	
	

}
