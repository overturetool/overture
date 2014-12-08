package org.overture.codegen.vdm2cpp

import java.io.StringWriter
import java.util.ArrayList
import java.util.LinkedList
import java.util.List
import org.overture.codegen.cgast.INode
import org.overture.codegen.cgast.SExpCG
import org.overture.codegen.cgast.SStmCG
import org.overture.codegen.cgast.STypeCG
import org.overture.codegen.cgast.analysis.AnalysisException
import org.overture.codegen.cgast.declarations.AClassDeclCG
import org.overture.codegen.cgast.declarations.AFieldDeclCG
import org.overture.codegen.cgast.declarations.AFormalParamLocalParamCG
import org.overture.codegen.cgast.declarations.AMethodDeclCG
import org.overture.codegen.cgast.declarations.ARecordDeclCG
import org.overture.codegen.cgast.declarations.AVarDeclCG
import org.overture.codegen.cgast.name.ATypeNameCG
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG
import org.overture.codegen.cgast.statements.AForLoopStmCG
import org.overture.codegen.cgast.statements.AIdentifierObjectDesignatorCG
import org.overture.codegen.cgast.statements.AIdentifierStateDesignatorCG
import org.overture.codegen.cgast.types.AClassTypeCG
import org.overture.codegen.merging.MergeVisitor
import org.overture.codegen.merging.TemplateCallable
import org.overture.codegen.merging.TemplateStructure

class vdm2cppGen extends MergeVisitor
{
	
	
	public String class_name;
	private TypeHierachyAnalyser type_info = null;
	private CppTypeVisitor type_visitor;
	private CppExpVisitor exp_visitor;
	private CppStmVisitor stm_visitor;
	
	
	new(TemplateStructure templateStructure, TemplateCallable[] templateCallables, TypeHierachyAnalyser tag) {
		super(templateStructure, templateCallables)
		type_info = tag;
		type_visitor = new CppTypeVisitor(this,templateStructure,templateCallables)
		exp_visitor = new CppExpVisitor(this,templateStructure,templateCallables)
		stm_visitor = new CppStmVisitor(this,templateStructure,templateCallables)
	}
	
	def getArgumentMutation(List<AFieldDeclCG> list)
	{
		var gen_list = new ArrayList<List<AFieldDeclCG>>();
		var  i = 1;
		for(f : list)
		{
			var listx = list.subList(0,i);
			gen_list.add(listx )
			i = i +1;
		}
		
		return gen_list	
	}
	
			
	def String getGetStaticCall(STypeCG cg)
	{
		if(cg instanceof AClassTypeCG)
		{
			 return (cg as AClassTypeCG).name
		}
		else
		{
			return "udef"
		}
	}

	
	def generateMethodPrototype(AClassDeclCG cg, String ac)
	{
		var q = new StringWriter();
		for(method : cg.methods.filter[!isConstructor && access==ac])
		{
			
			q.append(
				''' //fixme
				«method.methodType.expand» «method.name»(«FOR arg : method.formalParams SEPARATOR ','»«arg.type.expand» «arg.tag»«ENDFOR»);
				'''
			)
		}
		return q.toString;
	}
	
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
	
	def generateValueInstanceResetMethod(AClassDeclCG cg)
	'''
	void __reset__(void)
	{
	«FOR field : cg.fields»
	«IF field.initial != null»	«field.name» = «field.initial.expand»;«ENDIF»
	«ENDFOR»
	};
	'''
	
	def generateVariables(AClassDeclCG cg, String ac)
	{	
		var q = new StringWriter();
		for (field : cg.fields.filter[access == ac])
		{
			field.apply(this,q)
		}
		return q.toString;
	}
	
	def generateMethods(AClassDeclCG cg, String ac)
	{	
		var q = new StringWriter();
		for (method : cg.methods.filter[access==ac && isConstructor == false])
		{
			method.apply(this,q)
		}
		return q.toString;
		
		
	}	
	
	def generateRecords(AClassDeclCG cg, String ac)
	{
		var typedep = new TypeDependency();
		var q = new StringWriter();
		
		var recs = typedep.orderedRecords;
		for( rec : recs)
		{
			rec.apply(this,q);
			q.append('''
			
			''');
		}
		
		return q.toString;
	}
	
	def generateDestructor(AClassDeclCG cg)'''virtual ~«cg.name»(){};'''
	
	def generateConstructor(AClassDeclCG cg) 
	{
		var q = new StringWriter();
		for(cons : cg.methods.filter[isConstructor == true])
		{
			q.append('''
			«cg.name»( «FOR p : cons.formalParams SEPARATOR ','» «p.expand» «ENDFOR» )
			{
				__reset__();
				«cons.body.expand»
			};
			''')
		}
		return q
	}
	
	def expand(INode node)
	{
		var str = new StringWriter()
		node.apply(this,str)
		return str.toString()
	}
	
	override defaultINode(INode node, StringWriter question)
			throws AnalysisException
	{
		System.out.println("Not generating for" + node.getClass.toString())
		question.append("/*Not generating for" + node.getClass.toString() + "*/")
	}
	
	override defaultSTypeCG(STypeCG node, StringWriter question) throws AnalysisException {
		node.apply(type_visitor,question);
	}
	
	override defaultSExpCG(SExpCG node, StringWriter question) throws AnalysisException {
		node.apply(exp_visitor,question)
	}

	override defaultSStmCG(SStmCG node, StringWriter question) throws AnalysisException {
		node.apply(stm_visitor,question)
	}
	
	override caseAFieldDeclCG(AFieldDeclCG node, StringWriter question) throws AnalysisException
	{
		question.append(
		'''
		«node.type.expand» «node.name»;
		''')
	}
	
	override caseAClassDeclCG(AClassDeclCG node, StringWriter question) throws AnalysisException 
	{
		class_name = node.name
		
		
		// the template
		question.append(
		'''
		#ifndef «node.name.toUpperCase»_HPP
		#define «node.name.toUpperCase»_HPP
		«node.generateIncludes»
		class «node.name» «IF node.superName != null» : public «node.superName»«ENDIF»
		{
			
		public: // public records
			«generateRecords(node,"public")»
		protected: 
			«generateRecords(node,"protected")»
		private: // private records
			«generateRecords(node,"private")»
			
		public: // public variables
			«generateVariables(node,"public")»
		protected: 
			«generateVariables(node,"protected")»
		private: // private variables
			«generateVariables(node,"private")»
			
		public:
			«generateConstructor(node)»
			
			«generateDestructor(node)»
			
			«generateMethods(node,"public")»
			
		protected:
		    «generateMethods(node,"protected")»
		    
		private:
		
			«generateMethods(node,"private")»
			
			«generateValueInstanceResetMethod(node)»
			
		};
		#endif /*«node.name.toUpperCase»_HPP*/
		'''
		)
	}
	
	override caseAMethodDeclCG(AMethodDeclCG node, StringWriter question) throws AnalysisException 
	{
		question.append(
		'''
		virtual «node.methodType.expand» «node.name»(«FOR p:node.formalParams SEPARATOR ','» «p.expand»«ENDFOR»)
		{
			«node.body?.expand»
		};
		'''
		)
	}
	
	override caseAIdentifierStateDesignatorCG(AIdentifierStateDesignatorCG node, StringWriter question) throws AnalysisException {
		question.append('''«node.name»''')
	}
	
	override caseATypeNameCG(ATypeNameCG node, StringWriter question) throws AnalysisException {
		if(node.definingClass != this.class_name)
		{
			if(node.definingClass != null)
			{
				question.append('''«node.definingClass»::«node.name»''')
			}
			else
			{
				question.append('''«node.name»''')
			}
		}
		else
		{
			question.append('''«node.name»''')	
		}
		
	}
	
	override caseARecordDeclCG(ARecordDeclCG node, StringWriter question) throws AnalysisException {
		question.append('''
		typedef struct «node.name»
		{
			«FOR fields: node.fields.argumentMutation»
			«node.name»(«FOR field: fields SEPARATOR ','»«field.type.expand» «field.name»«ENDFOR»)
			{
				«IF node.fields.exists[initial != null]»__reset__();«ENDIF»
				«FOR field: fields»
					this->«field.name» = «field.name»;
				«ENDFOR»
			};
			
			«ENDFOR»
			«IF node.fields.exists[initial != null]»
			void __reset__(void)
			{
				«FOR field: node.fields.filter[initial != null]»
				«field.name» = «field.initial.expand»;
				«ENDFOR»
			}
			«ENDIF»
			«FOR t : node.fields»«t.expand»«ENDFOR»
		}«node.name»;
		typedef shared_ptr<«node.name»> «node.name»Ptr;
		'''
		)
	}
	
	override caseAVarDeclCG(AVarDeclCG node, StringWriter question) throws AnalysisException {
		if(node.parent instanceof AForLoopStmCG)
		{
			// TODO: hack
			question.append('''«node.type.expand»::iterator «node.pattern.expand» = «node.exp.expand»''')
		}
		else
		{
			question.append('''«node.type.expand» «node.pattern.expand» = «node.exp.expand»;''')
		}
	}
	
	override caseAIdentifierPatternCG(AIdentifierPatternCG node, StringWriter question) throws AnalysisException {
		question.append(''' «node.name» ''')
	}

	override caseAFormalParamLocalParamCG(AFormalParamLocalParamCG node, StringWriter question)
	{
		question.append(''' «node.type.expand» «node.pattern.expand» ''')
	}
	
	override caseAIdentifierObjectDesignatorCG(AIdentifierObjectDesignatorCG node, StringWriter question) throws AnalysisException {
		question.append(
			'''«node.exp.expand»'''
		)
	}
	
}