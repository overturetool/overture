package org.overture.codegen.vdm2jml;

import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.expressions.AFieldExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.trans.assistants.TransAssistantCG;
import org.overture.codegen.vdm2java.JavaCodeGen;

public class RecInvTransformation extends DepthFirstAnalysisAdaptor
{
	private final String paramName;
	private JavaCodeGen javaGen;
	
	public RecInvTransformation(JavaCodeGen javaGen, String paramName)
	{
		this.javaGen = javaGen;
		this.paramName = paramName;
	}

	@Override
	public void caseAFieldExpCG(AFieldExpCG node)
			throws org.overture.codegen.cgast.analysis.AnalysisException
	{
		if (node.getObject() instanceof AFieldExpCG)
		{
			node.getObject().apply(this);
		} else
		{
			if (node.getObject() instanceof AIdentifierVarExpCG)
			{
				AIdentifierVarExpCG obj = (AIdentifierVarExpCG) node.getObject();

				if (obj.getName().equals(paramName))
				{
					TransAssistantCG assistant = javaGen.getTransformationAssistant();
					AIdentifierVarExpCG field = assistant.consIdentifierVar(node.getMemberName(), node.getType().clone());
					assistant.replaceNodeWith(node, field);
				}
			}
		}
	}
}
