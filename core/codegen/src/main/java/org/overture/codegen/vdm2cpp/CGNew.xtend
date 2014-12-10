package org.overture.codegen.vdm2cpp

import org.overture.codegen.cgast.declarations.AClassDeclCG
import org.overture.codegen.cgast.declarations.AFuncDeclCG
import org.overture.codegen.cgast.declarations.AMethodDeclCG
import org.overture.codegen.cgast.declarations.ANamedTypeDeclCG

class CGNew extends XtendBaseDepthFirstCG {
	
	CppExpressionVisitor exps;
	new() {
		exps = new CppExpressionVisitor(this);
	}
	
	override caseAClassDeclCG(AClassDeclCG node)'''
	class «node.name» «IF node.superName != null» : public «node.superName»«ENDIF»
	{
	public:
		«FOR method : node.methods.filter[access == "public"]»
		«method.expand»
		«ENDFOR»
		
		«FOR inst : node.fields.filter[access == "public"]»
		«inst.type.expand» «inst.name»;
		«ENDFOR»
	protected:
		«FOR method : node.methods.filter[access == "protected"]»
		«method.expand»
		«ENDFOR»
		
		«FOR inst : node.fields.filter[access == "protected"]»
		«inst.type.expand» «inst.name»;
		«ENDFOR»
	private:
		«FOR method : node.methods.filter[access == "private"]»
		«method.expand»
		«ENDFOR»
		
		«FOR inst : node.fields.filter[access == "private"]»
		«inst.type.expand» «inst.name»;
		«ENDFOR»
	}
	'''
	
	override caseAMethodDeclCG(AMethodDeclCG node)'''	
	«IF node.static != null && node?.static == true»static«ENDIF»«node.methodType.expand» «node.name»(«FOR p : node.formalParams SEPARATOR ','» «p.type.expand» «p.pattern.expand» «ENDFOR» );
	'''
	
	override caseAFuncDeclCG(AFuncDeclCG node)'''
	static «node.name»(«FOR p : node.formalParams SEPARATOR ','»«p.type.expand»«p.pattern.expand»«ENDFOR»);
	'''
	
	override caseANamedTypeDeclCG(ANamedTypeDeclCG node)'''
	typedef «node.type.expand» «node.name»
	'''
	
	
	
	
}