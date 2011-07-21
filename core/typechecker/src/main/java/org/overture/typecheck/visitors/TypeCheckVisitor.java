package org.overture.typecheck.visitors;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.statements.PStm;
import org.overture.ast.types.PType;
import org.overture.typecheck.TypeCheckInfo;
import org.overture.typecheck.TypeCheckerErrors;




public class TypeCheckVisitor  extends QuestionAnswerAdaptor<TypeCheckInfo, PType> {

//	static private QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor = null;
	
	private QuestionAnswerAdaptor<TypeCheckInfo, PType> tcStm = new TypeCheckerStmVisitor(this);
	private QuestionAnswerAdaptor<TypeCheckInfo, PType> tcExp = new TypeCheckerExpVisitor(this);
	private QuestionAnswerAdaptor<TypeCheckInfo, PType> tcDefinition = new TypeCheckerDefinitionVisitor(this);
	private QuestionAnswerAdaptor<TypeCheckInfo, PType> patternDefinition = new TypeCheckerPatternVisitor(this);
	
	
	public TypeCheckerErrors tcErrors = new TypeCheckerErrors();
	
	
//	public synchronized static QuestionAnswerAdaptor<TypeCheckInfo, PType> getInstance()
//	{
//		if(rootVisitor == null)
//		{
//			rootVisitor = new TypeCheckVisitor();
//		}
//		
//		return rootVisitor;
//	}
	
	
//	public TypeCheckVisitor() {		
//	}
	
	

	@Override
	public PType defaultPStm(PStm node, TypeCheckInfo question) {
		return node.apply(tcStm, question);
	}
	
	@Override
	public PType defaultPExp(PExp node, TypeCheckInfo question) {
		return node.apply(tcExp, question);
	}
	
	
	@Override
	public PType defaultPDefinition(PDefinition node, TypeCheckInfo question) {
		return node.apply(tcDefinition, question);
	}
	
	@Override
	public PType caseAModuleModules(AModuleModules node, TypeCheckInfo question) {
		System.out.println("Visiting Module: "+ node.getName());
		for (PDefinition def : node.getDefs()) {
			def.apply(this, question);
		}
		
		return null;
	}
	
	@Override
		public PType defaultPMultipleBind(PMultipleBind node, TypeCheckInfo question) {
		return node.apply(patternDefinition, question);
		}
			
	
	
	
	



	
	
}


