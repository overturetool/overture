package org.overture.ast.definitions.assistants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.overture.ast.analysis.QuestionAnswerAdaptor;

import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AImportedDefinition;
import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.ast.definitions.ALocalDefinition;
import org.overture.ast.definitions.ARenamedDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.node.NodeList;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.PType;
import org.overture.ast.types.assistants.PTypeAssistant;
import org.overture.runtime.TypeChecker;
import org.overture.typecheck.TypeCheckInfo;
import org.overture.typecheck.TypeCheckerErrors;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.NameScope;


public class PDefinitionAssistant {

	public static boolean hasSupertype(SClassDefinition aClassDefDefinition, PType other) {
		
		if (PTypeAssistant.equals(aClassDefDefinition.getType(),other))
		{
			return true;
		}
		else
		{
			for (PType type: aClassDefDefinition.getSupertypes())
			{
				AClassType sclass = (AClassType)type;

				if (PTypeAssistant.hasSupertype(sclass, other))
				{
					return true;
				}
			}
		}
		return false;
		
	}

	public static boolean isFunctionOrOperation(PDefinition possible) {
		switch(possible.kindPDefinition())
		{
			//TODO: missing operations
			case EXPLICITFUNCTION:
			case IMPLICITFUNCTION: 
				return true;
			default:
				return false;
		}
	}

	public static PDefinition findType(List<PDefinition> definitions,
			LexNameToken name, String fromModule) {
		
		for (PDefinition d: definitions)
		{
			PDefinition def = findType(d,name, fromModule);

			if (def != null)
			{
				return def;
			}
		}

		return null;
		
	}

	public static PDefinition findType(PDefinition d, LexNameToken name,
			String fromModule) {
		switch(d.kindPDefinition())
		{
			case STATE:
				if (findName(d,name, NameScope.STATE) != null)
				{
					return d;
				}

				return null;
			case TYPE:
				return null;
				
			default:
				return null;
		}
	}

	public static PDefinition findName(PDefinition d, LexNameToken sought,
			NameScope scope) {
		if (d.getName().equals(sought))
		{
			if ((d.getNameScope()== NameScope.STATE && !scope.matches(NameScope.STATE)) ||
				(d.getNameScope() == NameScope.OLDSTATE && !scope.matches(NameScope.OLDSTATE)))
			{
				TypeChecker.report(3302,
					"State variable '" + sought.getName() + "' cannot be accessed from this context",sought.getLocation());
			}

			markUsed(d);
			return d;
		}

		return null;
	}

	public static void markUsed(PDefinition d)
	{
		d.setUsed(true);
	}

	public static PDefinition findName(List<PDefinition> definitions,
			LexNameToken name, NameScope scope) {
		for (PDefinition d: definitions)
		{
			PDefinition def = findName(d,name, scope);

			if (def != null)
			{
				return def;
			}
		}

		return null;
	}

	public static AStateDefinition findStateDefinition(
			List<PDefinition> definitions) {
		for (PDefinition d: definitions)
		{
			if (d instanceof AStateDefinition)
			{
				return (AStateDefinition)d;
			}
		}

   		return null;
	}

	public static void unusedCheck(List<PDefinition> definitions) {
		for (PDefinition d: definitions)
		{
			unusedCheck(d);
		}
		
	}

	private static void unusedCheck(PDefinition d) {
		if (!d.getUsed())
		{
			TypeCheckerErrors.warning(d, 5000, "Definition '" + d.getName() + "' not used");
			markUsed(d);		// To avoid multiple warnings
		}
		
	}
	


	public static Set<PDefinition> findMatches(List<PDefinition> definitions,
			LexNameToken name) {
		
		Set<PDefinition> set = new HashSet<PDefinition>();

		for (PDefinition d: singleDefinitions(definitions))
		{
			if (isFunctionOrOperation(d) && d.getName().matches(name))
			{
				set.add(d);
			}
		}

		return set;
	}

	private static List<PDefinition> singleDefinitions(List<PDefinition> definitions) {
		List<PDefinition> all = new ArrayList<PDefinition>();

		for (PDefinition d: definitions)
		{
			all.addAll(getDefinitions(d));
		}

		return all;
	}

	private static Collection<? extends PDefinition> getDefinitions(
			PDefinition d) {
		System.out.println("HelperDefinition : getDefinitions(PDefinitions d) not implemented");
		return null;
	}

	public static void markUsed(List<PDefinition> definitions) {
		for (PDefinition d: definitions)
		{
			markUsed(d);
		}
		
	}

	public static void typeCheck(NodeList<PDefinition> defs,
			  QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor, TypeCheckInfo question) {
		for (PDefinition d: defs)
		{			
			d.apply(rootVisitor, question );
		}
		
	}

	public static PDefinition getSelfDefinition(AExplicitFunctionDefinition node) {
		return getSelfDefinition(node.getClassDefinition());
	}

	private static PDefinition getSelfDefinition(
			SClassDefinition classDefinition) {
		
		PDefinition def = new ALocalDefinition(classDefinition.getLocation(),
				classDefinition.getName().getSelfName(), NameScope.LOCAL,false, null , null, classDefinition.getType());
			markUsed(def);
			return def;
	}
	
	public static LexNameList getVariableName(List<PDefinition> list) {
		LexNameList variableNames = new LexNameList();

		for (PDefinition d : list) {
			variableNames.addAll(getVariableNames(d));
		}

		return variableNames;
	}

	private static Collection<? extends LexNameToken> getVariableNames(
			PDefinition d) {
		List<LexNameToken> result = new ArrayList<LexNameToken>();

		switch (d.kindPDefinition()) {
		case EXPLICITFUNCTION:
			if(d instanceof AExplicitFunctionDefinition)
			{
				AExplicitFunctionDefinition efd = (AExplicitFunctionDefinition) d;
				result.addAll(AExplicitFunctionDefinitionAssistant.getVariableNames(efd));
			}
			break;
		case LOCAL:
			if(d instanceof ALocalDefinition)
			{	
				ALocalDefinition ld = (ALocalDefinition) d;
				result.addAll(ALocalDefinitionAssistant.getVariableNames(ld));
			}
			break;
		default:
			System.out
					.println("DefinitionHelper : getVariableName(PDefinition)");
			break;
		}

		return result;
	}

	public static boolean isStatic(PDefinition fdef) {
		return PAccessSpecifierAssistant.isStatic(fdef.getAccess());
	}

	public static PDefinition deref(PDefinition def) {
		switch (def.kindPDefinition()) {
		case IMPORTED:
			if( def instanceof AImportedDefinition)
			{
				return deref(((AImportedDefinition) def).getDef());
			}
			break;
		case INHERITED:
			if(def instanceof AInheritedDefinition)
			{
				return deref(((AInheritedDefinition) def).getSuperdef());
			}		
			break;
		case RENAMED: 
			if(def instanceof ARenamedDefinition)
			{
				return deref(((ARenamedDefinition) def).getDef());
			}
			break;			
		}
		return def;
		
		
	}
	
	public static boolean isCallableOperation(PDefinition def)
	{
		Boolean result = false;
		switch (def.kindPDefinition()) {
					
		}
		
		return result;
	}
	
}
