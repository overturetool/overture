package org.overture.typechecker.assistant.definition;

import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.types.PType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AValueDefinitionAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AValueDefinitionAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	// public static void updateDefs(AValueDefinition node, TypeCheckInfo question)
	// {
	// PType type = node.getType();
	// PPattern pattern = node.getPattern();
	//
	// List<PDefinition> newdefs = PPatternAssistantTC.getDefinitions(pattern, type, node.getNameScope());
	//
	// // The untyped definitions may have had "used" markers, so we copy
	// // those into the new typed definitions, lest we get warnings. We
	// // also mark the local definitions as "ValueDefintions" (proxies),
	// // so that classes can be constructed correctly (values are statics).
	//
	// for (PDefinition d : newdefs)
	// {
	// for (PDefinition u : node.getDefs())
	// {
	// if (u.getName().equals(d.getName()))
	// {
	// if (PDefinitionAssistantTC.isUsed(u))
	// {
	// PDefinitionAssistantTC.markUsed(d);
	// }
	//
	// break;
	// }
	// }
	//
	// ALocalDefinition ld = (ALocalDefinition) d;
	// ALocalDefinitionAssistantTC.setValueDefinition(ld);
	// }
	//
	// node.setDefs(newdefs);
	// List<PDefinition> defs = node.getDefs();
	// PDefinitionListAssistantTC.setAccessibility(defs, node.getAccess().clone());
	// PDefinitionListAssistantTC.setClassDefinition(defs, node.getClassDefinition());
	// }


//	public static PType getType(AValueDefinition def)
//	{
//		return def.getType() != null ? def.getType()
//				: (def.getExpType() != null ? def.getExpType()
//						: AstFactory.newAUnknownType(def.getLocation()));
//	}

	public static PType getType(AValueDefinition def)
	{
		return def.getType() != null ? def.getType()
				: def.getExpType() != null ? def.getExpType()
						: AstFactory.newAUnknownType(def.getLocation());
	}


}
