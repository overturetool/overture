package org.overture.typechecker.assistant.type;

import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.PType;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AFieldFieldAssistantTC {

	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AFieldFieldAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public static void typeResolve(AFieldField f, ATypeDefinition root,
			IQuestionAnswer<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {
		
		// Recursion defence done by the type
		f.setType(af.createPTypeAssistant().typeResolve(f.getType(), root, rootVisitor, question));

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
