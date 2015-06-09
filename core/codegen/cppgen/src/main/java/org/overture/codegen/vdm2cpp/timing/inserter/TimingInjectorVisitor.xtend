package org.overture.codegen.vdm2cpp.timing.inserter

import java.util.HashMap
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor
import org.overture.codegen.cgast.declarations.AClassDeclCG
import org.overture.codegen.cgast.declarations.AMethodDeclCG
import org.overture.codegen.cgast.expressions.AIntLiteralExpCG
import org.overture.codegen.cgast.statements.ABlockStmCG
import org.overture.codegen.cgast.statements.AStackDeclStmCG
import org.overture.codegen.cgast.types.AExternalTypeCG
import org.overture.codegen.cgast.types.AIntNumericBasicTypeCG
import java.util.LinkedList
import org.overture.codegen.cgast.declarations.AFormalParamLocalParamCG
import java.util.ArrayList

class TimingInjectorVisitor extends DepthFirstAnalysisAdaptor {
	
	var id = 0L;
	
	var registered_methods = new HashMap<Long,String>()
	
	
	def getRegisteredMethods()
	{
		return registered_methods
	}
	
	override caseAMethodDeclCG(AMethodDeclCG node) {
		
		val b = node.body
		if(b instanceof ABlockStmCG)
		{
			var cl = node.getAncestor(AClassDeclCG)
			if(cl.name.equals("RANSAC") || cl.name.equals("RowDetection"))
			{
			
				registered_methods.put(id,cl.name + "::" + node.name+ ":" + (node.methodType))
				var timeScopetype = new AExternalTypeCG();
				timeScopetype.setName("TimedScope");
				var zero = new AIntLiteralExpCG();
				zero.setType(new AIntNumericBasicTypeCG());
				zero.value =  id
				id = id +1;
				
				var timeScopeDecl = new AStackDeclStmCG();
				timeScopeDecl.setType(timeScopetype);
				timeScopeDecl.setName("sc_");
				timeScopeDecl.getArgs().add(zero);
				
				b.statements.addFirst(timeScopeDecl)
			}
			
		}
	}
	
	def toIden(LinkedList<AFormalParamLocalParamCG> cgs)
	'''«FOR a: cgs SEPARATOR ','»«a.type»«ENDFOR»'''
	
	
}