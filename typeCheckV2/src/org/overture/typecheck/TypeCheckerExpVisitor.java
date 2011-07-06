package org.overture.typecheck;

import java.util.HashSet;
import java.util.Set;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.expressions.ABooleanConstExp;
import org.overture.ast.expressions.AElseIfExp;
import org.overture.ast.expressions.AIfExp;
import org.overture.ast.expressions.AIntConstExp;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.AIntNumericBasicType;
import org.overture.ast.types.ANatNumericBasicType;
import org.overture.ast.types.ANatOneNumericBasicType;
import org.overture.ast.types.PType;
import org.overture.ast.types.assistants.PTypeAssistant;
import org.overture.runtime.TypeChecker;

public class TypeCheckerExpVisitor extends
		QuestionAnswerAdaptor<TypeCheckInfo, PType> {
	
	private QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor;

	public TypeCheckerExpVisitor(TypeCheckVisitor typeCheckVisitor) {
		this.rootVisitor = typeCheckVisitor;
	}

	@Override
	public PType caseAIfExp(AIfExp node, TypeCheckInfo question) {
		
		if (!PTypeAssistant.isType(node.getTest().apply(this, question),ABooleanBasicType.class))
		{
			TypeChecker.report(3108, "If expression is not a boolean",node.getLocation());
		}

		Set<PType> rtypes = new HashSet<PType>();
		rtypes.add(node.getThen().apply(rootVisitor, question));

		for (AElseIfExp eie: node.getElseList())
		{
			rtypes.add(eie.apply(rootVisitor, question));
		}

		rtypes.add(node.getElse().apply(rootVisitor, question));

		node.setType(PTypeAssistant.getType(rtypes,node.getLocation()));
		return node.getType();
	}
	
	
	@Override
	public PType caseAIntConstExp(AIntConstExp node, TypeCheckInfo question) {
		if (node.getValue().value < 0)
		{
			node.setType(new AIntNumericBasicType(node.getLocation()));
		}
		else if (node.getValue().value == 0)
		{
			node.setType(new ANatNumericBasicType(node.getLocation()));
		}
		else
		{
			node.setType(new ANatOneNumericBasicType(node.getLocation()));
		}
		
		return node.getType();
	}
	
	
	@Override
	public PType caseABooleanConstExp(ABooleanConstExp node,
			TypeCheckInfo question) {
		node.setType(new ABooleanBasicType(node.getLocation(),null)); 
		return node.getType();
	}
	
}
