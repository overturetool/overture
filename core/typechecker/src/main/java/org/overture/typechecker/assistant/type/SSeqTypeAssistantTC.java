package org.overture.typechecker.assistant.type;

import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.types.AAccessSpecifierAccessSpecifier;
import org.overture.ast.types.PType;
import org.overture.ast.types.SSeqType;
import org.overture.typechecker.TypeCheckException;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class SSeqTypeAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public SSeqTypeAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	public static void unResolve(SSeqType type) {
		if (!type.getResolved()) return; else { type.setResolved(false); }
		PTypeAssistantTC.unResolve(type.getSeqof());
		
	}

	public static PType typeResolve(SSeqType type, ATypeDefinition root,
			IQuestionAnswer<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {
		if (type.getResolved()) return type; else { type.setResolved(true); }

		try
		{
			type.setSeqof(af.createPTypeAssistant().typeResolve(type.getSeqof(), root, rootVisitor, question));
			if (root != null) root.setInfinite(false);	// Could be empty
			return type;
		}
		catch (TypeCheckException e)
		{
			unResolve(type);
			throw e;
		}
	}

//	public static boolean equals(SSeqType type, Object other) {
//		other = PTypeAssistantTC.deBracket(other);
//
//		if (other instanceof SSeqType)
//		{
//			SSeqType os = (SSeqType)other;
//			// NB. Empty sequence is the same type as any sequence
//			return type.getEmpty() || os.getEmpty() ||	PTypeAssistantTC.equals(type.getSeqof(), os.getSeqof());
//		}
//
//		return false;
//	}

//	public static boolean narrowerThan(SSeqType type,
//			AAccessSpecifierAccessSpecifier accessSpecifier) {
//		return PTypeAssistantTC.narrowerThan(type.getSeqof(),accessSpecifier);
//	}

	public static PType polymorph(SSeqType type, ILexNameToken pname,
			PType actualType) {
		return AstFactory.newASeqSeqType(type.getLocation(), PTypeAssistantTC.polymorph(type.getSeqof(), pname, actualType));
	}

}
