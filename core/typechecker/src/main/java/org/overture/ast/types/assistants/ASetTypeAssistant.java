package org.overture.ast.types.assistants;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.PType;
import org.overture.typecheck.TypeCheckException;
import org.overture.typecheck.TypeCheckInfo;

public class ASetTypeAssistant {

	public static PType typeResolve(ASetType type, ATypeDefinition root,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {
		
		if (type.getResolved()) return type; else { type.setResolved(true); }

		try
		{
			type.setSetof(PTypeAssistant.typeResolve(type.getSetof(), root, rootVisitor, question));
			if (root != null) root.setInfinite(false);	// Could be empty
			return type;
		}
		catch (TypeCheckException e)
		{
			unResolve(type);
			throw e;
		}
	}

	public static void unResolve(ASetType type) {
		if (!type.getResolved()) return; else { type.setResolved(false); }
		PTypeAssistant.unResolve(type.getSetof()) ;
		
	}

	public static String toDisplay(ASetType exptype) {
		return exptype.getEmpty() ? "{}" : "set of (" + exptype.getSetof() + ")";
	}

	public static boolean equals(ASetType type, PType other) {
		other = PTypeAssistant.deBracket(other);

		if (other instanceof ASetType)
		{
			ASetType os = (ASetType)other;
			// NB empty set same type as any set
			return type.getEmpty() || os.getEmpty() || PTypeAssistant.equals(type.getSetof(), os.getSetof());
		}

		return false;
	}

}
