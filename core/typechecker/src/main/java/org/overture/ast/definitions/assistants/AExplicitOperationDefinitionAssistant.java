package org.overture.ast.definitions.assistants;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.patterns.assistants.PPatternAssistant;
import org.overture.ast.types.PType;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.NameScope;


public class AExplicitOperationDefinitionAssistant {

	public static List<? extends PDefinition> getParamDefinitions(
			AExplicitOperationDefinition node) {
		
		Set<PDefinition> defs = new HashSet<PDefinition>();
		Iterator<PType> titer = node.getOperationType().getParameters().iterator();

		for (PPattern p:  node.getParameterPatterns())
		{
   			defs.addAll(PPatternAssistant.getDefinitions(p,titer.next(), NameScope.LOCAL));
		}

		return new Vector<PDefinition>(defs);
	}

	public static PDefinition findName(AExplicitOperationDefinition d,
			LexNameToken sought, NameScope scope) {
		if (PDefinitionAssistant.findNameBaseCase(d, sought, scope) != null)
		{
			return d;
		}

		PDefinition predef = d.getPredef();
		if (predef != null && PDefinitionAssistant.findName(predef, sought, scope) != null)
		{
			return predef;
		}

		PDefinition postdef = d.getPostdef();
		if (postdef != null && PDefinitionAssistant.findName(postdef,sought, scope) != null)
		{
			return postdef;
		}

		return null;
	}

}
