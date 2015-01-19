package org.overture.codegen.vdm2cpp.visitors

import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor
import org.overture.codegen.cgast.declarations.AMethodDeclCG
import org.overture.codegen.cgast.analysis.AnalysisException
import org.overture.codegen.cgast.declarations.AClassDeclCG
import org.overture.codegen.cgast.statements.ACallObjectStmCG
import org.overture.codegen.cgast.statements.ACallObjectExpStmCG
import org.overture.codegen.cgast.statements.APlainCallStmCG
import org.overture.codegen.cgast.statements.ABlockStmCG

class ConstructorVdmLibInit extends DepthFirstAnalysisAdaptor {
	
	override caseAMethodDeclCG(AMethodDeclCG node) throws AnalysisException {
		if(node.isConstructor)
		{
			 
			val class_name = node.getAncestor(AClassDeclCG).name;
			
			//construct call to vdm_init_CLASSNAME()
			
			var a = new APlainCallStmCG();
			a.name = "vdm_init_"+class_name;
			System.out.println(  node.body )
			if(node.body instanceof ABlockStmCG)
			{
				var body = node.body as ABlockStmCG
				body.statements.add(a)
			}
			
		}
		
		super.caseAMethodDeclCG(node)
	}
	
}