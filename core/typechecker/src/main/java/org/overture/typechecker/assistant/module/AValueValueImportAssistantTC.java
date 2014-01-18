package org.overture.typechecker.assistant.module;

import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.modules.AValueValueImport;
import org.overture.ast.typechecker.NameScope;
import org.overture.typechecker.TypeCheckerErrors;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AValueValueImportAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AValueValueImportAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public static List<PDefinition> getDefinitions(AValueValueImport imp,
			AModuleModules module)
	{

		List<PDefinition> list = new Vector<PDefinition>();
		imp.setFrom(module);
		ILexNameToken name = imp.getName();

		PDefinition expdef = af.createPDefinitionListAssistant().findName(module.getExportdefs(), name, NameScope.NAMES);

		if (expdef == null)
		{
			TypeCheckerErrors.report(3193, "No export declared for import of value "
					+ name + " from " + module.getName(), imp.getLocation(), imp);
		} else
		{
			if (imp.getRenamed() != null)
			{
				expdef = AstFactory.newARenamedDefinition(imp.getRenamed(), expdef);
			} else
			{
				expdef = AstFactory.newAImportedDefinition(imp.getName().getLocation(), expdef);
			}

			list.add(expdef);
		}

		return list;
	}

}
