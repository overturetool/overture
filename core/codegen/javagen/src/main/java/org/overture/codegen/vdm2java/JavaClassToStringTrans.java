package org.overture.codegen.vdm2java;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.ir.IRInfo;

public class JavaClassToStringTrans extends DepthFirstAnalysisAdaptor
{
	private JavaClassCreator creator;
	
	public JavaClassToStringTrans(IRInfo info)
	{
		this.creator = new JavaClassCreator(info);
	}
	
	@Override
	public void caseAClassDeclCG(AClassDeclCG node) throws AnalysisException
	{
		node.getMethods().add(creator.generateToString(node));
	}
}
