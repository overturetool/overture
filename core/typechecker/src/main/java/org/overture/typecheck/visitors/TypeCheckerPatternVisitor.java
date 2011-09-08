package org.overture.typecheck.visitors;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.assistants.PMultipleBindAssistant;
import org.overture.ast.patterns.ASetMultipleBind;
import org.overture.ast.patterns.ATypeMultipleBind;
import org.overture.ast.patterns.assistants.PPatternListAssistant;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overture.ast.types.assistants.PTypeAssistant;
import org.overture.typecheck.TypeCheckInfo;
import org.overture.typecheck.TypeCheckerErrors;
import org.overture.typecheck.TypeComparator;

public class TypeCheckerPatternVisitor extends
		QuestionAnswerAdaptor<TypeCheckInfo, PType> {
	
	final private QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor;
	
	
	public TypeCheckerPatternVisitor(TypeCheckVisitor typeCheckVisitor) {
		this.rootVisitor = typeCheckVisitor;
	}
	
	@Override
	public PType caseASetMultipleBind(ASetMultipleBind node,
			TypeCheckInfo question) {
		
		
		PPatternListAssistant.typeResolve(node.getPlist(), rootVisitor, question);
		question.qualifiers = null;
		PType type = node.getSet().apply(rootVisitor, question);
		PType result = new AUnknownType(node.getLocation(),false);

		if (!PTypeAssistant.isSet(type))
		{
			TypeCheckerErrors.report(3197, "Expression matching set bind is not a set",node.getSet().getLocation(),node.getSet());
			TypeCheckerErrors.detail("Actual type", type);
		}
		else
		{
			ASetType st = PTypeAssistant.getSet(type);

			if (!st.getEmpty())
			{
				result = st.getSetof();
				PType ptype = PMultipleBindAssistant.getPossibleType(node);

				if (!TypeComparator.compatible(ptype, result))
				{
					TypeCheckerErrors.report(3264, "At least one bind cannot match set",node.getSet().getLocation(),node.getSet());
					TypeCheckerErrors.detail2("Binds", ptype, "Set of", st);
				}
			}
			else
			{
				TypeCheckerErrors.warning(3264, "Empty set used in bind",node.getSet().getLocation(),node.getSet());
			}
		}

		return result;
	}
	
	@Override
	public PType caseATypeMultipleBind(ATypeMultipleBind node,
			TypeCheckInfo question) {
		
		PPatternListAssistant.typeResolve(node.getPlist(), rootVisitor, question);
		PType type = PTypeAssistant.typeResolve(node.getType(),null,rootVisitor,question);
		PType ptype = PPatternListAssistant.getPossibleType(node.getPlist(), node.getLocation());

		if (!TypeComparator.compatible(ptype, type))
		{
			TypeCheckerErrors.report(3265, "At least one bind cannot match this type",type.getLocation(),type);
			TypeCheckerErrors.detail2("Binds", ptype, "Type", type);
		}

		node.setType(type);
		return type;
	}
}
