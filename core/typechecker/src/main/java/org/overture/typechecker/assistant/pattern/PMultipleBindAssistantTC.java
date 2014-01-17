package org.overture.typechecker.assistant.pattern;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.PType;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class PMultipleBindAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public PMultipleBindAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public static Collection<? extends PDefinition> getDefinitions(
			PMultipleBind mb, PType type, TypeCheckInfo question)
	{

		List<PDefinition> defs = new Vector<PDefinition>();

		for (PPattern p : mb.getPlist())
		{
			defs.addAll(af.createPPatternAssistant().getDefinitions(p, type, question.scope));
		}

		return defs;
	}

	public static List<PMultipleBind> getMultipleBindList(PMultipleBind bind)
	{
		List<PMultipleBind> list = new Vector<PMultipleBind>();
		list.add(bind);
		return list;
	}

	public static PType getPossibleType(PMultipleBind mb)
	{
		try
		{
			return mb.apply(af.getPossibleBindTypeFinder());
		} catch (AnalysisException e)
		{
			return null;
		}

	}

}
