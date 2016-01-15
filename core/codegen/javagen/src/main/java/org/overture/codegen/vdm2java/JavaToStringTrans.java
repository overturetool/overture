package org.overture.codegen.vdm2java;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.ADefaultClassDeclCG;
import org.overture.codegen.ir.IRInfo;

public class JavaToStringTrans extends DepthFirstAnalysisAdaptor
{
	private JavaClassCreator creator;
	
	public JavaToStringTrans(IRInfo info)
	{
		this.creator = new JavaClassCreator(info);
	}
	
	@Override
	public void caseADefaultClassDeclCG(ADefaultClassDeclCG node) throws AnalysisException
	{
		node.getMethods().add(creator.generateToString(node));
	}
}
