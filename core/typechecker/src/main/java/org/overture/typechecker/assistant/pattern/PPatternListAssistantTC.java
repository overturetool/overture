package org.overture.typechecker.assistant.pattern;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.PType;
import org.overture.ast.util.PTypeSet;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class PPatternListAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public PPatternListAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public static void typeResolve(List<PPattern> pp,
			IQuestionAnswer<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) throws AnalysisException
	{

		for (PPattern pattern : pp)
		{
			PPatternAssistantTC.typeResolve(pattern, rootVisitor, question);
		}

	}

	public static void unResolve(List<PPattern> pp)
	{

		for (PPattern pPattern : pp)
		{
			af.createPPatternAssistant().unResolve(pPattern);
		}
	}

	public static PType getPossibleType(LinkedList<PPattern> plist,
			ILexLocation location)
	{

		switch (plist.size())
		{
			case 0:
				return AstFactory.newAUnknownType(location);

			case 1:
				return af.createPPatternAssistant().getPossibleType(plist.get(0));

			default:
				PTypeSet list = new PTypeSet();

				for (PPattern p : plist)
				{
					list.add(af.createPPatternAssistant().getPossibleType(p));
				}

				return list.getType(location); // NB. a union of types
		}
	}

	public static List<PExp> getMatchingExpressionList(List<PPattern> pl)
	{

		List<PExp> list = new ArrayList<PExp>();

		for (PPattern p : pl)
		{
			list.add(af.createPPatternAssistant().getMatchingExpression(p));
		}

		return list;
	}

	public static boolean isSimple(LinkedList<PPattern> p)
	{
		for (PPattern pattern : p)
		{
			if (!af.createPPatternAssistant().isSimple(pattern))
				return false; // NB. AND
		}

		return true;
	}

	public static boolean alwaysMatches(List<PPattern> pl)
	{
		for (PPattern p : pl)
		{
			if (!af.createPPatternAssistant().alwaysMatches(p))
				return false; // NB. AND
		}

		return true;
	}

}
