package org.overture.ast.types.assistants;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.PType;
import org.overture.typecheck.TypeCheckInfo;

public class AFieldFieldAssistant {

	public static void typeResolve(AFieldField f, Object object,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {
		
		// Recursion defence done by the type
		f.setType(f.getType().apply(rootVisitor, question));

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
		PTypeAssistant.unResolve(f.getType());
		
	}

}
