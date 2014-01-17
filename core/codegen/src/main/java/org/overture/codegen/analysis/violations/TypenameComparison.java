package org.overture.codegen.analysis.violations;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.typechecker.assistant.definition.PDefinitionAssistantTC;

public class TypenameComparison extends NamingComparison
{

	public TypenameComparison(String[] names)
	{
		super(names);
	}

	@Override
	public boolean isInvalid(ILexNameToken nameToken)
	{

		if (this.getNames().contains(nameToken.getName()))
		{
			PDefinition def = (PDefinition) nameToken.getAncestor(PDefinition.class);

			if (def == null)
				return false;

			SClassDefinition enclosingClass = nameToken.getAncestor(SClassDefinition.class);

			if (enclosingClass == null)
				return false;

			enclosingClass.getName().getModule();
			PDefinition typeDef = PDefinitionAssistantTC.findType(def, nameToken, enclosingClass.getName().getModule());

			return typeDef != null;
		}

		return false;
	}

}
