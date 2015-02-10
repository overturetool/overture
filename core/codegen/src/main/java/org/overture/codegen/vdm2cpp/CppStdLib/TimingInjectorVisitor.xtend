package org.overture.codegen.vdm2cpp.CppStdLib

import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor
import org.overture.codegen.cgast.declarations.AMethodDeclCG
import org.overture.codegen.cgast.analysis.AnalysisException
import org.overture.codegen.cgast.statements.ABlockStmCG
import org.overture.codegen.cgast.statements.ALocalAssignmentStmCG
import org.overture.codegen.cgast.statements.AAssignmentStmCG
import org.overture.codegen.cgast.expressions.ANullExpCG
import org.overture.codegen.cgast.expressions.AMethodInstantiationExpCG
import org.overture.codegen.cgast.statements.AStackDeclStmCG
import org.overture.codegen.cgast.types.AClassTypeCG
import org.overture.codegen.cgast.expressions.AIntLiteralExpCG
import org.overture.codegen.cgast.types.AIntNumericBasicTypeCG
import java.util.HashMap
import org.overture.codegen.cgast.declarations.AClassDeclCG

class TimingInjectorVisitor extends DepthFirstAnalysisAdaptor {
	
	var id = 0L;
	
	var registered_methods = new HashMap<Long,String>()
	
	new() {
		
	}
	
	
	def getRegisteredMethods()
	{
		return registered_methods
	}
	
	override caseAMethodDeclCG(AMethodDeclCG node) {
		val b = node.body
		if(b instanceof ABlockStmCG)
		{
			var cl = node.getAncestor(AClassDeclCG)
			registered_methods.put(id,cl.name + "::" + node.name)
			var timeScopetype = new AClassTypeCG();
			timeScopetype.setName("TimedScope");
			timeScopetype.tag = new String("internal")
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