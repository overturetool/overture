package org.overture.codegen.vdm2java;

import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;

public class JavaToStringTrans extends DepthFirstAnalysisAdaptor
{
	private JavaClassCreator creator;

	public JavaToStringTrans(IRInfo info)
	{
		this.creator = new JavaClassCreator(info);
	}

	@Override
	public void caseADefaultClassDeclIR(ADefaultClassDeclIR node)
			throws AnalysisException
	{
		node.getMethods().add(creator.generateToString(node));
	}
}
