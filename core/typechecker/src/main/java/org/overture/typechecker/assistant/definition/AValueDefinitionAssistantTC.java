package org.overture.typechecker.assistant.definition;

import java.util.List;

import org.overture.ast.definitions.ALocalDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.PType;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.pattern.PPatternAssistantTC;

public class AValueDefinitionAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AValueDefinitionAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

//	public static void unusedCheck(AValueDefinition d)
//	{
//		if (d.getUsed()) // Indicates all definitions exported (used)
//		{
//			return;
//		}
//
//		if (d.getDefs() != null)
//		{
//			for (PDefinition def : d.getDefs())
//			{
//				PDefinitionAssistantTC.unusedCheck(def);
//			}
//		}
//
//	}

//	public static List<PDefinition> getDefinitions(AValueDefinition d)
//	{
//		return d.getDefs();
//	}

//	public static void typeResolve(AValueDefinition d,
//			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
//			TypeCheckInfo question) throws AnalysisException
//	{
//
//		// d.setType(getType(d));
//		if (d.getType() != null)
//		{
//			d.setType(PTypeAssistantTC.typeResolve(d.getType(), null, rootVisitor, question));
//			PPatternAssistantTC.typeResolve(d.getPattern(), rootVisitor, question);
//			updateDefs(d, question);
//		}
//
//	}

	public static void updateDefs(AValueDefinition node, TypeCheckInfo question)
	{
		PType type = node.getType();
		PPattern pattern = node.getPattern();

		List<PDefinition> newdefs = PPatternAssistantTC.getDefinitions(pattern, type, node.getNameScope());

		// The untyped definitions may have had "used" markers, so we copy
		// those into the new typed definitions, lest we get warnings. We
		// also mark the local definitions as "ValueDefintions" (proxies),
		// so that classes can be constructed correctly (values are statics).

		for (PDefinition d : newdefs)
		{
			for (PDefinition u : node.getDefs())
			{
				if (u.getName().equals(d.getName()))
				{
					if (PDefinitionAssistantTC.isUsed(u))
					{
						PDefinitionAssistantTC.markUsed(d);
					}

					break;
				}
			}

			ALocalDefinition ld = (ALocalDefinition) d;
			ALocalDefinitionAssistantTC.setValueDefinition(ld);
		}

		node.setDefs(newdefs);
		List<PDefinition> defs = node.getDefs();
		PDefinitionListAssistantTC.setAccessibility(defs, node.getAccess().clone());
		PDefinitionListAssistantTC.setClassDefinition(defs, node.getClassDefinition());
	}

	public static PType getType(AValueDefinition def)
	{
		return def.getType() != null ? def.getType()
				: (def.getExpType() != null ? def.getExpType()
						: AstFactory.newAUnknownType(def.getLocation()));
	}

	// public static LexNameList getOldNames(AValueDefinition def)
	// {
	// return PExpAssistantTC.getOldNames(def.getExpression());
	// }

}
