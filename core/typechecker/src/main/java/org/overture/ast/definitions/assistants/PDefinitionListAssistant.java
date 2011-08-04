package org.overture.ast.definitions.assistants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.types.AAccessSpecifierAccessSpecifier;
import org.overture.ast.types.PAccessSpecifier;
import org.overture.ast.types.PType;
import org.overture.typecheck.Environment;
import org.overture.typecheck.TypeCheckInfo;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.NameScope;

public class PDefinitionListAssistant {

	public static void implicitDefinitions(List<PDefinition> paramDefinitions,
			Environment env) {
		for (PDefinition d: paramDefinitions)
		{
			PDefinitionAssistant.implicitDefinitions(d,env);
			System.out.println();
		}
		
	}
	
	public static PDefinition findName(List<PDefinition> definitions,
			LexNameToken name, NameScope scope) {
		for (PDefinition d : definitions) {
			PDefinition def = PDefinitionAssistant.findName(d, name, scope);

			if (def != null) {
				return def;
			}
		}

		return null;
	}
	
	public static AStateDefinition findStateDefinition(
			List<PDefinition> definitions) {
		for (PDefinition d : definitions) {
			if (d instanceof AStateDefinition) {
				return (AStateDefinition) d;
			}
		}

		return null;
	}
	
	
	public static void unusedCheck(List<PDefinition> definitions) {
		for (PDefinition d : definitions) {
			PDefinitionAssistant.unusedCheck(d);
		}

	}
	
	public static Set<PDefinition> findMatches(List<PDefinition> definitions,
			LexNameToken name) {

		Set<PDefinition> set = new HashSet<PDefinition>();

		for (PDefinition d : singleDefinitions(definitions)) {
			if (PDefinitionAssistant.isFunctionOrOperation(d) && d.getName().matches(name)) {
				set.add(d);
			}
		}

		return set;
	}
	
	public static List<PDefinition> singleDefinitions(
			List<PDefinition> definitions) {
		List<PDefinition> all = new ArrayList<PDefinition>();

		for (PDefinition d : definitions) {
			all.addAll(PDefinitionAssistant.getDefinitions(d));
		}

		return all;
	}
	
	public static void markUsed(List<PDefinition> definitions) {
		for (PDefinition d : definitions) {
			PDefinitionAssistant.markUsed(d);
		}

	}

	public static void typeCheck(List<PDefinition> defs,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {
		for (PDefinition d : defs) {
			d.apply(rootVisitor, question);
		}
	}
	
	public static LexNameList getVariableNames(List<PDefinition> list) {		
		
		LexNameList variableNames = new LexNameList();

		for (PDefinition d : list) {
			variableNames.addAll(PDefinitionAssistant.getVariableNames(d));
		}

		return variableNames;
	}
	
	public static void setAccessibility(List<PDefinition> defs,
			AAccessSpecifierAccessSpecifier access) {
		for (PDefinition d : defs) {
			d.setAccess(access);
		}

	}

	public static void setClassDefinition(List<PDefinition> defs,
			SClassDefinition classDefinition) {
		for (PDefinition d : defs) {
			d.setClassDefinition(classDefinition);
		}

	}

	public static void typeResolve(List<PDefinition> definitions, QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor, TypeCheckInfo question) {
		for (PDefinition definition : definitions) {
			PDefinitionAssistant.typeResolve(definition, rootVisitor, question);
		}
		
	}

	public static PDefinition findType(LinkedList<PDefinition> actualDefs,
			LexNameToken name, String fromModule)
	{
		for (PDefinition d: actualDefs)
		{
			PDefinition def =  PDefinitionAssistant.findType(d,name, fromModule);

			if (def != null)
			{
				return def;
			}
		}

		return null;
	}
}
