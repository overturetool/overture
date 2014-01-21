package org.overture.typechecker.assistant.type;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.patterns.APatternListTypePair;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.typechecker.NameScope;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class APatternListTypePairAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public APatternListTypePairAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public Collection<? extends PDefinition> getDefinitions(
			APatternListTypePair pltp, NameScope scope)
	{
		List<PDefinition> list = new Vector<PDefinition>();

		for (PPattern p : pltp.getPatterns())
		{
			list.addAll(af.createPPatternAssistant().getDefinitions(p, pltp.getType(), scope));
		}

		return list;
	}

}
