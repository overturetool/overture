package org.overture.codegen.vdm2cpp

import org.overture.codegen.cgast.INode
import org.overture.codegen.cgast.SDeclCG
import org.overture.codegen.cgast.SExpCG
import org.overture.codegen.cgast.SStmCG
import org.overture.codegen.cgast.STypeCG
import org.overture.codegen.cgast.analysis.AnalysisException
import org.overture.codegen.cgast.declarations.AClassDeclCG
import org.overture.codegen.cgast.name.ATypeNameCG
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG
import org.overture.codegen.cgast.statements.AIdentifierObjectDesignatorCG
import org.overture.codegen.cgast.statements.AIdentifierStateDesignatorCG
import org.overture.codegen.vdm2cpp.stdlib.CppDeclarationsVisitor
import org.overture.codegen.vdm2cpp.stdlib.CppExpressionVisitor
import org.overture.codegen.vdm2cpp.stdlib.CppStatementVisitor
import org.overture.codegen.vdm2cpp.stdlib.CppTypeVisitor
import org.overture.codegen.vdm2cpp.stdlib.DependencyAnalyser
import org.overture.codegen.cgast.expressions.AUndefinedExpCG

class CGcppstdlib extends XtendAnswerStringVisitor {
	CppExpressionVisitor exps;
	CppStatementVisitor stms;
	CppTypeVisitor typs;
	CppDeclarationsVisitor decls;
	
	TypeHierarchyAnalyser type_info;

	new()
	{
		type_info = null;
		exps = new CppExpressionVisitor(this);
		stms = new CppStatementVisitor(this);
		typs = new CppTypeVisitor(this);
		decls = new CppDeclarationsVisitor(this);
	}
	
	new(TypeHierarchyAnalyser tan) {
		type_info = tan;
		exps = new CppExpressionVisitor(this);
		stms = new CppStatementVisitor(this);
		typs = new CppTypeVisitor(this);
		decls = new CppDeclarationsVisitor(this);
		
	}
	
	def expand(INode node)
	{
		return node.apply(this);
	}
	
	override defaultINode(INode node) throws AnalysisException {
		return '''/*case not handled «node.class»*/'''
	}
	
	override defaultSTypeCG(STypeCG node) throws AnalysisException {
		return node.apply(typs);
	}
	
	override defaultSExpCG(SExpCG node) throws AnalysisException {
		return node.apply(exps);
	}
	
	override defaultSStmCG(SStmCG node) throws AnalysisException {
		return node.apply(stms);
	}
	
	override defaultSDeclCG(SDeclCG node) throws AnalysisException {
		return node.apply(decls);
	}

	
	override caseATypeNameCG(ATypeNameCG node)
	{
		if(node.definingClass != node.getAncestor(AClassDeclCG).name)
		{
			if(node.definingClass != null)
			{
				return '''«node.definingClass»::«node.name»'''
			}
			else
			{
				return '''«node.name»'''
			}
		}
		else
		{
			return '''«node.name»'''	
		}
		
	}
	
	override caseAIdentifierPatternCG(AIdentifierPatternCG node)
	'''«node.name»'''
	
	override caseAIdentifierStateDesignatorCG(AIdentifierStateDesignatorCG node)
	'''«node.name»'''
	
	override caseAIdentifierObjectDesignatorCG(AIdentifierObjectDesignatorCG node)
	'''«node.exp.expand»'''
	
	override caseAClassDeclCG(AClassDeclCG node)'''
	#ifndef VDMCPP«node.name.toUpperCase»_HPP
	#define VDMCPP«node.name.toUpperCase»_HPP
	«node.generateIncludes»
	
	class «node.name»;
	
	typedef std::shared_ptr<«node.name»> «node.name»Ptr;
	
	class «node.name» «IF node.superName != null» : public «node.superName»«ENDIF»
	{		
	public:
	
		«FOR rec : node.typeDecls.filter[access== "public"]»
		«rec.expand»
		«ENDFOR»
		«FOR method : node.methods.filter[access == "public"]»
		«method.expand»
		«ENDFOR»
		
		virtual ~«node.name»(){};
		
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
		«inst.type.expand» «inst.name» «IF inst.initial instanceof AUndefinedExpCG»;«ELSE» = «inst.initial.expand»;«ENDIF»
		«ENDFOR»
	};
	
	#endif /*VDMCPP«node.name.toUpperCase»_HPP*/
	'''
	
	def generateIncludes(AClassDeclCG cg) 
	{
		var dep_man = new DependencyManager(cg.name);
		cg.apply(new DependencyAnalyser(),dep_man);
		
		return '''
		«FOR d : dep_man.dependeciesVDM»
		#include "«d».hpp"
		«ENDFOR»
		«FOR d : dep_man.dependenciesTargetLanguage»
		#include <«d.include»>
		«ENDFOR»
		«FOR p : type_info.getSuperType(cg)»
		#include "«p.name».hpp"
		«ENDFOR»
		'''
	}
	
	
}