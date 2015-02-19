package org.overturetool.cgisa.helpers;

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.AnswerAdaptor;
import org.overture.codegen.cgast.types.ASeqSeqTypeCG;

public class IsSeqOfCharTypeVisitor extends AnswerAdaptor<Boolean>
{

	@Override
	public Boolean createNewReturnValue(INode node) throws AnalysisException
	{
		return false;
	}

	@Override
	public Boolean createNewReturnValue(Object node) throws AnalysisException
	{
		return false;
	}
	
	@Override
	public Boolean caseASeqSeqTypeCG(ASeqSeqTypeCG node)
			throws AnalysisException
	{
		return node.getSeqOf().apply(new IsCharTypeVisitor());
	}
}