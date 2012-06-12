package org.overture.typechecker.assistant.type;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.types.AAccessSpecifierAccessSpecifier;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.PType;
import org.overture.typechecker.TypeCheckException;
import org.overture.typechecker.TypeCheckInfo;

public class ASetTypeAssistantTC {

	public static PType typeResolve(ASetType type, ATypeDefinition root,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {
		
		if (type.getResolved()) return type; else { type.setResolved(true); }

		try
		{
			type.setSetof(PTypeAssistantTC.typeResolve(type.getSetof(), root, rootVisitor, question));
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
		PTypeAssistantTC.unResolve(type.getSetof()) ;
		
	}

	public static String toDisplay(ASetType exptype) {
		return exptype.getEmpty() ? "{}" : "set of (" + exptype.getSetof() + ")";
	}

	public static boolean equals(ASetType type, Object other) {
		other = PTypeAssistantTC.deBracket(other);

		if (other instanceof ASetType)
		{
			ASetType os = (ASetType)other;
			// NB empty set same type as any set
			return type.getEmpty() || os.getEmpty() || PTypeAssistantTC.equals(type.getSetof(), os.getSetof());
		}

		return false;
	}

	public static boolean isSet(ASetType type) {
		return true;
	}

	public static ASetType getSet(ASetType type) {
		return type;
	}

	public static boolean narrowerThan(ASetType type,
			AAccessSpecifierAccessSpecifier accessSpecifier) {
		return PTypeAssistantTC.narrowerThan(type.getSetof(),accessSpecifier);
	}

	public static PType polymorph(ASetType type, LexNameToken pname,
			PType actualType) {
		return AstFactory.newASetType(type.getLocation(), PTypeAssistantTC.polymorph(type.getSetof(), pname, actualType));
	}

}
