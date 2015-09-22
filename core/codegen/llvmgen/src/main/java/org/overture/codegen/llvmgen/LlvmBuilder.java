package org.overture.codegen.llvmgen;

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.AnswerAdaptor;
import org.overture.codegen.cgast.declarations.ADefaultClassDeclCG;
import org.overture.codegen.cgast.declarations.ASystemClassDeclCG;

public class LlvmBuilder extends AnswerAdaptor<LlvmNode>
{

	@Override
	public LlvmNode caseADefaultClassDeclCG(ADefaultClassDeclCG node) throws AnalysisException
	{
		return new LlvmNode(node.getName());
	}
	
	@Override
	public LlvmNode caseASystemClassDeclCG(ASystemClassDeclCG node) throws AnalysisException
	{
		return new LlvmNode(node.getName());
	}
	
	@Override
	public LlvmNode createNewReturnValue(INode node) throws AnalysisException
	{
		return null;
	}

	@Override
	public LlvmNode createNewReturnValue(Object node) throws AnalysisException
	{
		return null;
	}
}