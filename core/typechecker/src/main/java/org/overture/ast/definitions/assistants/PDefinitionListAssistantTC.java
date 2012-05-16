package org.overture.ast.definitions.assistants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.types.AAccessSpecifierAccessSpecifier;
import org.overture.ast.types.PType;
import org.overture.typecheck.Environment;
import org.overture.typecheck.TypeCheckInfo;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.NameScope;

public class PDefinitionListAssistantTC {

	public static void implicitDefinitions(List<PDefinition> paramDefinitions,
			Environment env) {
		for (PDefinition d: paramDefinitions)
		{
			PDefinitionAssistantTC.implicitDefinitions(d,env);
		//	System.out.println();
		}
		
	}
	
	public static PDefinition findName(List<PDefinition> definitions,
			LexNameToken name, NameScope scope) {
		for (PDefinition d : definitions) {
			PDefinition def = PDefinitionAssistantTC.findName(d, name, scope);

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
			PDefinitionAssistantTC.unusedCheck(d);
		}

	}
	
	public static Set<PDefinition> findMatches(List<PDefinition> definitions,
			LexNameToken name) {

		Set<PDefinition> set = new HashSet<PDefinition>();

		for (PDefinition d : singleDefinitions(definitions)) {
			if (PDefinitionAssistantTC.isFunctionOrOperation(d) && d.getName().matches(name)) {
				set.add(d);
			}
		}

		return set;
	}
	
	public static List<PDefinition> singleDefinitions(
			List<PDefinition> definitions) {
		List<PDefinition> all = new ArrayList<PDefinition>();

		for (PDefinition d : definitions) {
			all.addAll(PDefinitionAssistantTC.getDefinitions(d));
		}

		return all;
	}
	
	public static void markUsed(List<PDefinition> definitions) {
		for (PDefinition d : definitions) {
			PDefinitionAssistantTC.markUsed(d);
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
			variableNames.addAll(PDefinitionAssistantTC.getVariableNames(d));
		}

		return variableNames;
	}
	
	public static void setAccessibility(List<PDefinition> defs,
			AAccessSpecifierAccessSpecifier access) {
		for (PDefinition d : defs) {
			d.setAccess(access);
		}

	}

	

	public static void typeResolve(List<PDefinition> definitions, QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor, TypeCheckInfo question) {
		for (PDefinition definition : definitions) {
			PDefinitionAssistantTC.typeResolve(definition, rootVisitor, question);
		}
		
	}

	public static PDefinition findType(LinkedList<PDefinition> actualDefs,
			LexNameToken name, String fromModule)
	{
		for (PDefinition d: actualDefs)
		{
			PDefinition def =  PDefinitionAssistantTC.findType(d,name, fromModule);

			if (def != null)
			{
				return def;
			}
		}

		return null;
	}

	public static void initializedCheck(LinkedList<PDefinition> definitions) {
		for (PDefinition d: definitions)
		{
			if (d instanceof AInstanceVariableDefinition)
			{
				AInstanceVariableDefinition ivd = (AInstanceVariableDefinition)d;
				AInstanceVariableDefinitionAssistantTC.initializedCheck(ivd);
			}
		}
	}
	
	public static LexNameList getOldNames(LinkedList<PDefinition> definitions)
	{
		LexNameList list = new LexNameList();

		for (PDefinition d: definitions)
		{
			list.addAll(PDefinitionAssistantTC.getOldNames(d));
		}

		return list;
	}

	public static void setClassDefinition(List<PDefinition> defs,
			SClassDefinition classDefinition) {
		PDefinitionAssistant.setClassDefinition(defs, classDefinition);
		
	}
}
