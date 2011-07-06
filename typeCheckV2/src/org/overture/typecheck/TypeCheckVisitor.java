package org.overture.typecheck;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.assistants.AExplicitFunctionDefinitionAssistant;
import org.overture.ast.definitions.assistants.DefinitionAssistant;
import org.overture.ast.expressions.ABooleanConstExp;
import org.overture.ast.expressions.AElseIfExp;
import org.overture.ast.expressions.AIfExp;
import org.overture.ast.expressions.AIntConstExp;
import org.overture.ast.expressions.ANotYetSpecifiedExp;
import org.overture.ast.expressions.ASubclassResponsibilityExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.Node;
import org.overture.ast.node.NodeList;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.patterns.assistants.PPatternAssistant;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AIntNumericBasicType;
import org.overture.ast.types.ANatNumericBasicType;
import org.overture.ast.types.ANatOneNumericBasicType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.EType;
import org.overture.ast.types.PType;
import org.overture.ast.types.assistants.PTypeAssistant;
import org.overture.runtime.FlatCheckedEnvironment;
import org.overture.runtime.TypeChecker;
import org.overture.runtime.TypeComparator;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.NameScope;




public class TypeCheckVisitor  extends QuestionAnswerAdaptor<TypeCheckInfo, PType> {

	QuestionAnswerAdaptor<TypeCheckInfo, PType> tcType = new TypeCheckerTypeVisitor(this);
	QuestionAnswerAdaptor<TypeCheckInfo, PType> tcExp = new TypeCheckerExpVisitor(this);
	QuestionAnswerAdaptor<TypeCheckInfo, PType> tcDefinition = new TypeCheckerDefinitionVisitor(this);
	
	@Override
	public PType defaultPType(PType node, TypeCheckInfo question) {
		
		return node.apply(tcType, question);
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
			
	
	
	
	



	
	
}


