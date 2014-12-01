package org.overture.codegen.vdm2cpp

import java.io.StringWriter
import java.util.ArrayList
import java.util.List
import org.overture.ast.types.AVoidType
import org.overture.codegen.cgast.INode
import org.overture.codegen.cgast.analysis.AnalysisException
import org.overture.codegen.cgast.declarations.AClassDeclCG
import org.overture.codegen.cgast.declarations.AFieldDeclCG
import org.overture.codegen.cgast.declarations.AMethodDeclCG
import org.overture.codegen.cgast.declarations.ARecordDeclCG
import org.overture.codegen.cgast.expressions.AAndBoolBinaryExpCG
import org.overture.codegen.cgast.expressions.AApplyExpCG
import org.overture.codegen.cgast.expressions.ABoolLiteralExpCG
import org.overture.codegen.cgast.expressions.ACastUnaryExpCG
import org.overture.codegen.cgast.expressions.ACompMapExpCG
import org.overture.codegen.cgast.expressions.ACompSeqExpCG
import org.overture.codegen.cgast.expressions.ADeRefExpCG
import org.overture.codegen.cgast.expressions.ADivideNumericBinaryExpCG
import org.overture.codegen.cgast.expressions.AElemsUnaryExpCG
import org.overture.codegen.cgast.expressions.AEnumSeqExpCG
import org.overture.codegen.cgast.expressions.AEnumSetExpCG
import org.overture.codegen.cgast.expressions.AEqualsBinaryExpCG
import org.overture.codegen.cgast.expressions.AExplicitVarExpCG
import org.overture.codegen.cgast.expressions.AFieldExpCG
import org.overture.codegen.cgast.expressions.AForAllQuantifierExpCG
import org.overture.codegen.cgast.expressions.AGreaterEqualNumericBinaryExpCG
import org.overture.codegen.cgast.expressions.AGreaterNumericBinaryExpCG
import org.overture.codegen.cgast.expressions.AHeadUnaryExpCG
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG
import org.overture.codegen.cgast.expressions.AIntLiteralExpCG
import org.overture.codegen.cgast.expressions.ANewExpCG
import org.overture.codegen.cgast.expressions.ANotEqualsBinaryExpCG
import org.overture.codegen.cgast.expressions.ANotUnaryExpCG
import org.overture.codegen.cgast.expressions.ANullExpCG
import org.overture.codegen.cgast.expressions.APlusNumericBinaryExpCG
import org.overture.codegen.cgast.expressions.APostIncExpCG
import org.overture.codegen.cgast.expressions.ARealLiteralExpCG
import org.overture.codegen.cgast.expressions.ASeqConcatBinaryExpCG
import org.overture.codegen.cgast.expressions.AStringLiteralExpCG
import org.overture.codegen.cgast.expressions.ASubtractNumericBinaryExpCG
import org.overture.codegen.cgast.expressions.ATailUnaryExpCG
import org.overture.codegen.cgast.expressions.ATimesNumericBinaryExpCG
import org.overture.codegen.cgast.name.ATypeNameCG
import org.overture.codegen.cgast.statements.AAssignmentStmCG
import org.overture.codegen.cgast.statements.ABlockStmCG
import org.overture.codegen.cgast.statements.ACallObjectStmCG
import org.overture.codegen.cgast.statements.AForAllStmCG
import org.overture.codegen.cgast.statements.AForIndexStmCG
import org.overture.codegen.cgast.statements.AForLoopStmCG
import org.overture.codegen.cgast.statements.AIdentifierObjectDesignatorCG
import org.overture.codegen.cgast.statements.AIdentifierStateDesignatorCG
import org.overture.codegen.cgast.statements.AIfStmCG
import org.overture.codegen.cgast.statements.AReturnStmCG
import org.overture.codegen.cgast.statements.ASkipStmCG
import org.overture.codegen.cgast.types.ABoolBasicTypeCG
import org.overture.codegen.cgast.types.ACharBasicTypeCG
import org.overture.codegen.cgast.types.AClassTypeCG
import org.overture.codegen.cgast.types.AIntNumericBasicTypeCG
import org.overture.codegen.cgast.types.AMapMapTypeCG
import org.overture.codegen.cgast.types.AMethodTypeCG
import org.overture.codegen.cgast.types.ARealNumericBasicTypeCG
import org.overture.codegen.cgast.types.ARecordTypeCG
import org.overture.codegen.cgast.types.ASeqSeqTypeCG
import org.overture.codegen.cgast.types.ASetSetTypeCG
import org.overture.codegen.cgast.types.AStringTypeCG
import org.overture.codegen.cgast.types.AVoidTypeCG
import org.overture.codegen.cgast.expressions.ASetUnionBinaryExpCG
import org.overture.codegen.cgast.STypeCG
import org.overture.codegen.merging.MergeVisitor
import org.overture.codegen.merging.TemplateStructure
import org.overture.codegen.merging.TemplateCallable
import org.overture.codegen.cgast.declarations.AVarDeclCG
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG
import org.overture.codegen.cgast.statements.APlainCallStmCG
import org.overture.codegen.cgast.declarations.AFormalParamLocalParamCG
import org.overture.codegen.cgast.expressions.ALenUnaryExpCG
import org.overture.codegen.cgast.types.ANatNumericBasicTypeCG
import org.overture.codegen.cgast.statements.ALocalAssignmentStmCG
import org.overture.codegen.cgast.expressions.ALessNumericBinaryExpCG
import org.overture.codegen.cgast.expressions.ALessEqualNumericBinaryExpCG
import org.overture.codegen.cgast.statements.AWhileStmCG
import org.overture.codegen.cgast.statements.ABreakStmCG
import org.overture.codegen.cgast.expressions.AMinusUnaryExpCG
import org.apache.commons.lang.StringEscapeUtils
import org.overture.codegen.cgast.expressions.AAbsUnaryExpCG
import org.overture.codegen.cgast.types.AUnionTypeCG
import java.util.LinkedList
import java.util.Set
import org.overture.codegen.cgast.types.ANat1NumericBasicTypeCG

class vdm2cppGen extends MergeVisitor//QuestionAdaptor<StringWriter>
{
	
	
	private String class_name;
	//private LinkedList<AClassDeclCG> super_classes;
	private TypeHierachyAnalyser type_info = null;
	private LinkedList<AMethodDeclCG> virtual_candidates;
	
	boolean ignore_ctx
	
	
	new(TemplateStructure templateStructure, TemplateCallable[] templateCallables, TypeHierachyAnalyser tag) {
		super(templateStructure, templateCallables)
		type_info = tag;
		virtual_candidates = new LinkedList<AMethodDeclCG>()
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
	
	def generateInitalMethod(AClassDeclCG cg)
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
		question.append("/*Not generating for" + node.getClass.toString() + "*/")
	}
	
	override caseASeqSeqTypeCG(ASeqSeqTypeCG node, StringWriter question) throws AnalysisException {
		question.append('''vdm::sequence<«node.seqOf.expand»>''')
	}
	
	override caseAClassDeclCG(AClassDeclCG node, StringWriter question) throws AnalysisException 
	{
		class_name = node.name
		// find all methods which are also in super classes
		// if they are present in either they must be declared virtual such that the correct method is
		// invoked if the method is called on parent class. 
		if(type_info != null)
		{
			//System.out.println("Getting super for " + node.name)
			var supers = type_info.getSuperType(node)
			for( s: supers)
			{
				var virtual_methods = s.methods.filter[access == "public" && isConstructor == false]
				virtual_methods = virtual_methods.filter[node.methods.contains(it)]
				virtual_candidates.addAll(virtual_methods)
				System.out.println("Class " + node.name + " Added the following methods to virtual list")
				System.out.println(virtual_methods)
			}
		}
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
			
			«generateInitalMethod(node)»
			
		};
		#endif /*«node.name.toUpperCase»_HPP*/
		'''
		)
	}
	
	
	
	override caseAFieldDeclCG(AFieldDeclCG node, StringWriter question) throws AnalysisException
	{
		question.append(
		'''
		«node.type.expand» «node.name»;
		''')
	}
	
	override caseARecordTypeCG(ARecordTypeCG node, StringWriter question) throws AnalysisException {
		question.append('''«node.name.expand»''')
	}
	
	override caseASetSetTypeCG(ASetSetTypeCG node, StringWriter question) throws AnalysisException 
	{
			question.append(''' vdm::set<«node.setOf.expand»> ''')
	}

	override caseAMapMapTypeCG(AMapMapTypeCG node, StringWriter question) throws AnalysisException {
		question.append(
		'''vdm::set<«node.from.expand», «node.to.expand»>'''
		)			
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
	
	override caseAUnionTypeCG(AUnionTypeCG node, StringWriter question)
	{
		question.append('''«node.types.first.expand»''');
	}
		
	override caseACharBasicTypeCG(ACharBasicTypeCG node, StringWriter question) throws AnalysisException
	{
		question.append('''char''')
	} 
	
	override caseARealNumericBasicTypeCG(ARealNumericBasicTypeCG node, StringWriter question) throws AnalysisException {
		question.append('''double''')
	}
	
	override caseAIntNumericBasicTypeCG(AIntNumericBasicTypeCG node, StringWriter question) throws AnalysisException {
		question.append('''int''')
	}
	
	override caseABoolBasicTypeCG(ABoolBasicTypeCG node, StringWriter question) throws AnalysisException {
		question.append('''bool''')
	}
	
	override caseAStringTypeCG(AStringTypeCG node, StringWriter question) throws AnalysisException {
		question.append('''std::string''')
	}
	
	override caseANat1NumericBasicTypeCG(ANat1NumericBasicTypeCG node, StringWriter question)
	{
		question.append('''double''');
	}
	
	override caseAMethodDeclCG(AMethodDeclCG node, StringWriter question) throws AnalysisException 
	{
		question.append(
		'''
		«IF virtual_candidates.contains(node)»virtual «ENDIF»«node.methodType.expand» «node.name»(«FOR p:node.formalParams SEPARATOR ','» «p.expand»«ENDFOR»)
		{
			«node.body?.expand»
		};
		'''
		)
	}
	
	override caseAPostIncExpCG(APostIncExpCG node, StringWriter question) throws AnalysisException {
		question.append('''«node.exp.expand»++''');
	}
	
	override caseADeRefExpCG(ADeRefExpCG node, StringWriter question) throws AnalysisException {
		question.append('''*«node.exp.expand»''');
	}
	
	override caseAIdentifierVarExpCG(AIdentifierVarExpCG node, StringWriter question) throws AnalysisException {
		question.append('''«node.original»''')
	}
	
	override caseAMethodTypeCG(AMethodTypeCG node, StringWriter question) throws AnalysisException {
		question.append('''«node.result.expand»''')
	}
	
	override caseAIdentifierStateDesignatorCG(AIdentifierStateDesignatorCG node, StringWriter question) throws AnalysisException {
		question.append('''«node.name»''')
	}
	
	override caseAAssignmentStmCG(AAssignmentStmCG node, StringWriter question) throws AnalysisException {
		question.append('''«node.target.expand» = «node.exp.expand»; 
		''')
	}
	
	override caseALocalAssignmentStmCG(ALocalAssignmentStmCG node, StringWriter question)
	{
		question.append('''«node.target.expand» = «node.exp.expand»;
		''')
	}
	
	override caseAClassTypeCG(AClassTypeCG node, StringWriter question) throws AnalysisException {
			question.append('''std::shared_ptr<«node.name»>''')
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
	
	
	
	override caseABlockStmCG(ABlockStmCG node, StringWriter question) throws AnalysisException {
		
		question.append('''
		«FOR local_def : node.localDefs»
		«local_def.expand»
		«ENDFOR»
		«FOR stm : node.statements»«stm.expand»«ENDFOR»''')
	}

	override caseAReturnStmCG(AReturnStmCG node, StringWriter question) throws AnalysisException {
		question.append(
		'''
		return «node.exp.expand»;
		'''
		)
	}
	
	override caseAStringLiteralExpCG(AStringLiteralExpCG node, StringWriter question) throws AnalysisException {
		question.append('''"«StringEscapeUtils.escapeJava( node.value)»"''')
	}
	
	override caseAIfStmCG(AIfStmCG node, StringWriter question) throws AnalysisException {
		question.append(
		'''
		if( «node.ifExp.expand» )
		{
			«node.thenStm.expand»
		}
		«FOR elif : node.elseIf»else if( «elif.elseIf.expand» )
		{
			«elif.thenStm.expand»
		}
		«ENDFOR»
		«IF node.elseStm != null»
		else
		{
			«node.elseStm.expand»
		}
		«ENDIF»
		'''
		)
	}
	
	override caseAEqualsBinaryExpCG(AEqualsBinaryExpCG node, StringWriter question) throws AnalysisException {
		question.append('''«node.left» == «node.right»''')
	}
	
	override caseAIntLiteralExpCG(AIntLiteralExpCG node, StringWriter question) throws AnalysisException {
		question.append('''«node.value»''')
	}
	
	override caseANewExpCG(ANewExpCG node, StringWriter question) throws AnalysisException 
	{
		if(node.type instanceof ARecordTypeCG)
		{
			question.append('''«node.name.expand»(«FOR a : node.args SEPARATOR ','»«a.expand»«ENDFOR») ''')
		}
		else
		{
			question.append('''new «node.name.expand»(«FOR a : node.args SEPARATOR ','»«a.expand»«ENDFOR») ''')
		}
	}
	
	override caseAEnumSeqExpCG(AEnumSeqExpCG node, StringWriter question) throws AnalysisException {
		question.append(
			'''new «node.type.expand» {«FOR v: node.members SEPARATOR ','»«v.expand»«ENDFOR»}//ee'''
		)
	}
	
	
	
	override caseASkipStmCG(ASkipStmCG node, StringWriter question) throws AnalysisException {
		question.append("//skip")
	}
	
	override caseAVarDeclCG(AVarDeclCG node, StringWriter question) throws AnalysisException {
		if(node.parent instanceof AForLoopStmCG)
		{
			// TODO: hack
			question.append('''«node.type.getIteratorType»::iterator «node.pattern.expand» = «node.exp.expand»''')
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
	
	override caseAPlainCallStmCG(APlainCallStmCG node, StringWriter question)
	{
		
		question.append('''«IF node.classType != null»«node.classType.getStaticCall»::«ENDIF»«node.name»( «FOR arg: node.args SEPARATOR ','» «arg.expand» «ENDFOR»); 
		''');
	}
	

	
	override caseALenUnaryExpCG(ALenUnaryExpCG node, StringWriter question)
	{
		question.append(''' («node.exp»).size() ''')
	}
	
	override caseAMinusUnaryExpCG(AMinusUnaryExpCG node, StringWriter question)
	{
		question.append('''-(«node.exp.expand»)''')
	}
	
	
	override caseAForLoopStmCG(AForLoopStmCG node, StringWriter question) throws AnalysisException {
		question.append(
		'''
		for («node.init.expand»; «IF node.cond != null»«node.cond.expand»«ENDIF»; «IF node.inc != null»«node.inc.expand»«ENDIF» )
		{
			«node.body.expand»
		}
		'''
		)
	}
	
	override caseABreakStmCG(ABreakStmCG node, StringWriter question)
	{
		question.append('''break;''')
	}
	
	override caseAWhileStmCG(AWhileStmCG node, StringWriter question)
	{
		question.append('''while(«node.exp.expand»)
		{
			«node?.body.expand»
		}
		''')
	}
	
	override caseAForAllStmCG(AForAllStmCG node, StringWriter question) throws AnalysisException {
		question.append(
		''' // FIXME
		for( «node.exp.type.expand» «node.pattern» : «node.exp.expand»)
		{
			«node.body.expand»
		}
		'''
		)
	}
	
	override caseAFieldExpCG(AFieldExpCG node, StringWriter question) throws AnalysisException {
		if(node.object.type instanceof AClassTypeCG)
		{
			question.append('''«node.object»->«node.memberName»/*ed*/''');
		}
		else
		{
			question.append('''«node.object».«node.memberName»''');
		}
	}
	
	override caseAAbsUnaryExpCG(AAbsUnaryExpCG node, StringWriter question)
	{
		question.append('''fabs(«node.exp.expand»)''')
	}
	
	override caseAEnumSetExpCG(AEnumSetExpCG node, StringWriter question) throws AnalysisException {
		question.append('''«node.type.getIteratorType»::from_list( {«FOR member : node.members SEPARATOR ','» «member»«ENDFOR»})''')
	}
	
	override caseABoolLiteralExpCG(ABoolLiteralExpCG node, StringWriter question) throws AnalysisException {
		question.append('''«node.value»/*lit*/''')
	}
	
	override caseAApplyExpCG(AApplyExpCG node, StringWriter question) throws AnalysisException {
		
		question.append('''/*apply*/«node.root.expand»(«FOR n : node.args SEPARATOR ','»«n.expand»«ENDFOR»)/*exp*/''')	
	}
	
	override caseAAndBoolBinaryExpCG(AAndBoolBinaryExpCG node, StringWriter question) throws AnalysisException {
		question.append('''«node.left.expand» && «node.right.expand»''')
	}
	
	override caseALessNumericBinaryExpCG(ALessNumericBinaryExpCG node, StringWriter question)
	{
		question.append('''(«node.left.expand» < «node.right.expand»)''')
	}
	
	override caseACastUnaryExpCG(ACastUnaryExpCG node, StringWriter question) throws AnalysisException {
		question.append('''(«node.type.expand») «node.exp.expand»''')
		
	}
	
	override caseAGreaterEqualNumericBinaryExpCG(AGreaterEqualNumericBinaryExpCG node, StringWriter question) throws AnalysisException {
		question.append('''«node.left.expand» >= «node.right.expand»''')
	}
	
	override caseALessEqualNumericBinaryExpCG(ALessEqualNumericBinaryExpCG node, StringWriter question)
	{
		question.append('''( «node.left.expand» <= «node.right.expand» )''')
	}
	
	override caseAGreaterNumericBinaryExpCG(AGreaterNumericBinaryExpCG node, StringWriter question) throws AnalysisException {
		question.append('''«node.left.expand» > «node.right.expand»''')
	}
	
	override caseAPlusNumericBinaryExpCG(APlusNumericBinaryExpCG node, StringWriter question) throws AnalysisException {
		question.append('''«node.left.expand» + «node.right.expand»''')
	}
	
	override caseAForAllQuantifierExpCG(AForAllQuantifierExpCG node, StringWriter question) throws AnalysisException {
		question.append('''«IF node.bindList.empty»true«ELSE»//HELP MEEE«ENDIF»''')
	}
	
	override caseANotEqualsBinaryExpCG(ANotEqualsBinaryExpCG node, StringWriter question) throws AnalysisException {
		question.append('''(«node.left.expand») != («node.right.expand»)''')
	}
	
	
	override caseACallObjectStmCG(ACallObjectStmCG node, StringWriter question) throws AnalysisException {
		question.append('''«IF node.type.isVoid»return«ENDIF»«node.designator.expand»->«node.fieldName»(«FOR arg : node.args SEPARATOR ','»«arg.expand»«ENDFOR»); //dd
		''')
	}
	
	override caseATimesNumericBinaryExpCG(ATimesNumericBinaryExpCG node, StringWriter question) throws AnalysisException {
		question.append('''«node.left.expand» * «node.right.expand»''');
	}
	
	override caseAIdentifierObjectDesignatorCG(AIdentifierObjectDesignatorCG node, StringWriter question) throws AnalysisException {
		question.append(
			'''«node.exp.expand»'''
		)
	}
	
	override caseAElemsUnaryExpCG(AElemsUnaryExpCG node, StringWriter question) throws AnalysisException {
		question.append('''«node.exp.expand»''')
	}
	
	override caseAExplicitVarExpCG(AExplicitVarExpCG node, StringWriter question) throws AnalysisException {
		question.append('''«IF node.classType != null»«node.classType.getStaticCall»::«ENDIF»«node.name»/*«node.classType»*/''')
	}
	
	override caseAForIndexStmCG(AForIndexStmCG node, StringWriter question) throws AnalysisException {
		question.append('''
		for(«node.^var» = «node.from.expand» ; «node.^var» <= «node.to.expand» ; «node.^var»++ )
		{
			«node.body?.expand»
		}
		''')
	}
	
	override caseACompSeqExpCG(ACompSeqExpCG node, StringWriter question) throws AnalysisException {
		question.append('''HELP ME caseACompSeqExpCG''')
	}
	
	override caseACompMapExpCG(ACompMapExpCG node, StringWriter question) throws AnalysisException {
		question.append('''HELP ME caseACompMapExpCG''')
	}
	
	def boolean getIsVoid(STypeCG cg)
	{
		return cg.class == AVoidType;
	}
	
	override caseANatNumericBasicTypeCG(ANatNumericBasicTypeCG node, StringWriter question)
	{
		question.append('''double''')
	}
	
	override caseARealLiteralExpCG(ARealLiteralExpCG node, StringWriter question) throws AnalysisException {
		question.append('''«node.value.toString»''')
	}
	
	override caseAVoidTypeCG(AVoidTypeCG node, StringWriter question) throws AnalysisException {
		question.append('''void''')
	}
	
	override caseANotUnaryExpCG(ANotUnaryExpCG node, StringWriter question) throws AnalysisException {
		question.append('''!(«node.exp.expand»)''')
	}
	
	override caseADivideNumericBinaryExpCG(ADivideNumericBinaryExpCG node, StringWriter question) throws AnalysisException {
		question.append('''(«node.left.expand») / («node.right.expand»)''')
	}
	
	override caseASubtractNumericBinaryExpCG(ASubtractNumericBinaryExpCG node, StringWriter question) throws AnalysisException {
		question.append('''(«node.left.expand») - («node.right.expand»)''')
	}
	
	override caseAHeadUnaryExpCG(AHeadUnaryExpCG node, StringWriter question) throws AnalysisException {
		question.append('''«node.exp.expand»->front()''')
	}
	
	
	override caseATailUnaryExpCG(ATailUnaryExpCG node, StringWriter question) throws AnalysisException {
		question.append('''«node.exp.expand»->pop_front()''')
	}
	
	override caseANullExpCG(ANullExpCG node, StringWriter question) throws AnalysisException {
		question.append('''null''')	
	}
	
	override caseASeqConcatBinaryExpCG(ASeqConcatBinaryExpCG node, StringWriter question) throws AnalysisException {
		question.append('''«node.left.expand»->insert(«node.left.expand»->end(), «node.right.expand»)''')
	}
	
	
	override caseASetUnionBinaryExpCG(ASetUnionBinaryExpCG node, StringWriter question) throws AnalysisException {
		question.append('''«node.type.getIteratorType»::set_union(«node.left.expand», «node.right.expand»)''')
	}
	
	def getIteratorType(STypeCG type)
	{
		ignore_ctx = true;
		var st = type.expand
		ignore_ctx = false;
		return st;
		
		
	}
	
}