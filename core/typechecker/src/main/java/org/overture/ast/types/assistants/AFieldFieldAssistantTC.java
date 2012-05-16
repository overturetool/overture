package org.overture.ast.types.assistants;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.PType;
import org.overture.typecheck.TypeCheckInfo;

public class AFieldFieldAssistantTC {

	public static void typeResolve(AFieldField f, ATypeDefinition root,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {
		
		// Recursion defence done by the type
		f.setType(PTypeAssistantTC.typeResolve(f.getType(), root, rootVisitor, question));

		if (question.env.isVDMPP())
		{
			if (f.getType() instanceof AFunctionType)
			{
    			f.getTagname().setTypeQualifier(((AFunctionType)f.getType()).getParameters());
			}
			else if (f.getType() instanceof AOperationType)
    		{
				f.getTagname().setTypeQualifier(((AOperationType)f.getType()).getParameters());
    		}
		}
		
	}

	public static void unResolve(AFieldField f) {
		PTypeAssistantTC.unResolve(f.getType());
		
	}

}
