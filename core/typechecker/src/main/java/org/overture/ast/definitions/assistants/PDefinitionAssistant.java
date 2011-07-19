package org.overture.ast.definitions.assistants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.overture.ast.analysis.QuestionAnswerAdaptor;

import org.overture.ast.definitions.AAssignmentDefinition;
import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.AClassInvariantDefinition;
import org.overture.ast.definitions.AEqualsDefinition;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AExternalDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.AImportedDefinition;
import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.ALocalDefinition;
import org.overture.ast.definitions.AMultiBindListDefinition;
import org.overture.ast.definitions.AMutexSyncDefinition;
import org.overture.ast.definitions.ANamedTraceDefinition;
import org.overture.ast.definitions.APerSyncDefinition;
import org.overture.ast.definitions.ARenamedDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.AThreadDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.AUntypedDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.PAccessSpecifier;
import org.overture.ast.types.PType;
import org.overture.ast.types.assistants.PTypeAssistant;
import org.overture.runtime.Environment;
import org.overture.runtime.TypeChecker;
import org.overture.typecheck.TypeCheckInfo;
import org.overture.typecheck.TypeCheckerErrors;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.NameScope;

public class PDefinitionAssistant {

	public static boolean hasSupertype(SClassDefinition aClassDefDefinition,
			PType other) {

		if (PTypeAssistant.equals(aClassDefDefinition.getType(), other)) {
			return true;
		} else {
			for (PType type : aClassDefDefinition.getSupertypes()) {
				AClassType sclass = (AClassType) type;

				if (PTypeAssistant.hasSupertype(sclass, other)) {
					return true;
				}
			}
		}
		return false;

	}

	public static boolean isFunctionOrOperation(PDefinition possible) {
		switch (possible.kindPDefinition()) {
		case EXPLICITFUNCTION:
		case IMPLICITFUNCTION:
		case EXPLICITOPERATION:
		case IMPLICITOPERATION:
			return true;
		default:
			return false;
		}
	}

	public static PDefinition findType(List<PDefinition> definitions,
			LexNameToken name, String fromModule) {

		for (PDefinition d : definitions) {
			PDefinition def = findType(d, name, fromModule);

			if (def != null) {
				return def;
			}
		}

		return null;

	}

	public static PDefinition findType(PDefinition d, LexNameToken sought,
			String fromModule) {
		switch (d.kindPDefinition()) {

		case CLASS:
			return SClassDefinitionAssistant.findType((SClassDefinition) d,
					sought, fromModule);
		case IMPORTED:
			return AImportedDefinitionAssistant.findType(
					(AImportedDefinition) d, sought, fromModule);
		case INHERITED:
			return AInheritedDefinitionAssistant.findType(
					(AInheritedDefinition) d, sought, fromModule);
		case RENAMED:
			return ARenamedDefinitionAssistant.findType((ARenamedDefinition) d,
					sought, fromModule);
		case STATE:
			return AStateDefinitionAssistant.findType((AStateDefinition) d,
					sought, fromModule);
		case TYPE:
			return ATypeDefinitionAssistant.findType((ATypeDefinition) d,
					sought, fromModule);
		default:
			return null;
		}
	}

	public static PDefinition findName(PDefinition d, LexNameToken sought,
			NameScope scope) {
		switch (d.kindPDefinition()) {
		//case ASSIGNMENT:
		case CLASS:
			return SClassDefinitionAssistant.findName((SClassDefinition)d, sought, scope);
		case EQUALS:
			return AEqualsDefinitionAssistant.findName((AEqualsDefinition)d,sought,scope);
		case EXPLICITFUNCTION:
			return AExplicitFunctionDefinitionAssistant.findName((AExplicitFunctionDefinition)d,sought,scope);
		case EXPLICITOPERATION:
			return AExplicitOperationDefinitionAssistant.findName((AExplicitOperationDefinition)d,sought,scope);
		case EXTERNAL:
			return AExternalDefinitionAssistant.findName((AExternalDefinition)d,sought,scope);
		case IMPLICITFUNCTION:
			return AImplicitFunctionDefinitionAssistant.findName((AImplicitFunctionDefinition)d,sought,scope);
		case IMPLICITOPERATION:
			return AImplicitOperationDefinitionAssistant.findName((AImplicitOperationDefinition)d,sought,scope);
		case IMPORTED:
			return AImportedDefinitionAssistant.findName((AImportedDefinition)d,sought,scope);
		case INHERITED:
			return AInheritedDefinitionAssistant.findName((AInheritedDefinition)d,sought,scope);
		case INSTANCEVARIABLE:
			return AInstanceVariableDefinitionAssistant.findName((AInstanceVariableDefinition)d,sought,scope);
		case MULTIBINDLIST:
			return AMultiBindListDefinitionAssistant.findName((AMultiBindListDefinition)d,sought,scope);
		case MUTEXSYNC:
			return AMutexSyncDefinitionAssistant.findName((AMutexSyncDefinition)d,sought,scope);
		case NAMEDTRACE:
			return ANamedTraceDefinitionAssistant.findName((ANamedTraceDefinition)d,sought,scope);
		case PERSYNC:
			return APerSyncDefinitionAssistant.findName((APerSyncDefinition)d,sought,scope);
		case RENAMED:
			return ARenamedDefinitionAssistant.findName((ARenamedDefinition)d,sought,scope);
		case STATE:
			return AStateDefinitionAssistant.findName((AStateDefinition)d,sought,scope);
		case THREAD:
			return AThreadDefinitionAssistant.findName((AThreadDefinition)d,sought,scope);
		case TYPE:
			return ATypeDefinitionAssistant.findName((ATypeDefinition)d,sought,scope);		
		case VALUE:
			return AValueDefinitionAssistant.findName((AValueDefinition)d,sought,scope);
		default:
			return findNameBaseCase(d,sought,scope);	
		}
		
		
	}	
	
	public static PDefinition findNameBaseCase(PDefinition d, LexNameToken sought,
			NameScope scope) {
		if (d.getName().equals(sought)) {
			if ((d.getNameScope() == NameScope.STATE && !scope
					.matches(NameScope.STATE))
					|| (d.getNameScope() == NameScope.OLDSTATE && !scope
							.matches(NameScope.OLDSTATE))) {
				TypeChecker.report(3302, "State variable '" + sought.getName()
						+ "' cannot be accessed from this context",
						sought.getLocation());
			}

			markUsed(d);
			return d;
		}

		return null;
		
	}

	public static void markUsed(PDefinition d) {
		switch (d.kindPDefinition()) {				
		case EXTERNAL:
			AExternalDefinitionAssistant.markUsed((AExternalDefinition)d);
			break;		
		case IMPORTED:
			AImportedDefinitionAssistant.markUsed((AImportedDefinition)d);
			break;
		case INHERITED:
			AInheritedDefinitionAssistant.markUsed((AInheritedDefinition)d);		
			break;
		case RENAMED:
			ARenamedDefinitionAssistant.markUsed((ARenamedDefinition)d);	
		default:
			d.setUsed(true);
			break;
		}				
	}

	

	


	
	public static void unusedCheck(PDefinition d) {
		switch (d.kindPDefinition()) {		
		case EQUALS:
			AEqualsDefinitionAssistant.unusedCheck((AEqualsDefinition)d);
			break;		
		case MULTIBINDLIST:
			AMultiBindListDefinitionAssistant.unusedCheck((AMultiBindListDefinition)d);
			break;
		case STATE:
			AStateDefinitionAssistant.unusedCheck((AStateDefinition)d);
			break;
		case VALUE:
			AValueDefinitionAssistant.unusedCheck((AValueDefinition)d);
			break;
		default:
			unusedCheckBaseCase(d);
			break;
		}
	}
	

	public static void unusedCheckBaseCase(PDefinition d) {
		if (!d.getUsed()) {
			TypeCheckerErrors.warning(5000, "Definition '" + d.getName()
					+ "' not used", d.getLocation(), d);
			markUsed(d); // To avoid multiple warnings
		}

	}
	
	

	

	

	public static List<PDefinition> getDefinitions(
			PDefinition d) {
		
		switch (d.kindPDefinition()) {
		
		case ASSIGNMENT:
			return AAssignmentDefinitionAssistant.getDefinitions((AAssignmentDefinition)d);
		case CLASS:
			return SClassDefinitionAssistant.getDefinitions((SClassDefinition)d);
		case CLASSINVARIANT:
			return AClassInvariantDefinitionAssistant.getDefinitions((AClassInvariantDefinition)d);
		case EQUALS:
			return AEqualsDefinitionAssistant.getDefinitions((AEqualsDefinition)d);
		case EXPLICITFUNCTION:
			return AExplicitFunctionDefinitionAssistant.getDefinitions((AExplicitFunctionDefinition)d);
		case EXPLICITOPERATION:
			return AExplicitOperationDefinitionAssistant.getDefinitions((AExplicitOperationDefinition)d);
		case EXTERNAL:
			return AExternalDefinitionAssistant.getDefinitions((AExternalDefinition)d);
		case IMPLICITFUNCTION:
			return AImplicitFunctionDefinitionAssistant.getDefinitions((AImplicitFunctionDefinition)d);
		case IMPLICITOPERATION:
			return AImplicitOperationDefinitionAssistant.getDefinitions((AImplicitOperationDefinition)d);
		case IMPORTED:
			return AImportedDefinitionAssistant.getDefinitions((AImportedDefinition)d);
		case INHERITED:
			return AInheritedDefinitionAssistant.getDefinitions((AInheritedDefinition)d);
		case INSTANCEVARIABLE:
			return AInstanceVariableDefinitionAssistant.getDefinitions((AInstanceVariableDefinition)d);
		case LOCAL:
			return ALocalDefinitionAssistant.getDefinitions((ALocalDefinition)d);
		case MULTIBINDLIST:
			return AMultiBindListDefinitionAssistant.getDefinitions((AMultiBindListDefinition)d);
		case MUTEXSYNC:
			return AMutexSyncDefinitionAssistant.getDefinitions((AMutexSyncDefinition)d);
		case NAMEDTRACE:
			return ANamedTraceDefinitionAssistant.getDefinitions((ANamedTraceDefinition)d);
		case PERSYNC:
			return APerSyncDefinitionAssistant.getDefinitions((APerSyncDefinition)d);
		case RENAMED:
			return ARenamedDefinitionAssistant.getDefinitions((ARenamedDefinition)d);
		case STATE:
			return AStateDefinitionAssistant.getDefinitions((AStateDefinition)d);
		case THREAD:
			return AThreadDefinitionAssistant.getDefinitions((AThreadDefinition)d);
		case TYPE:
			return ATypeDefinitionAssistant.getDefinitions((ATypeDefinition)d);
		case UNTYPED:
			return AUntypedDefinitionAssistant.getDefinitions((AUntypedDefinition)d);
		case VALUE:
			return AValueDefinitionAssistant.getDefinitions((AValueDefinition)d);
		default:
			assert false : "getDefinitions should never hit the default case";
			return null;			
		}
		
		
	}

	

	public static PDefinition getSelfDefinition(PDefinition d) {
		switch (d.kindPDefinition()) {
		case CLASS:
			SClassDefinitionAssistant.getSelfDefinition((SClassDefinition)d);
		default:
			return getSelfDefinition(d.getClassDefinition());
		}
		
	}
	

	public static LexNameList getVariableNames(
			PDefinition d) {
		
//		List<LexNameToken> result = new Vector<LexNameToken>();
//		result.add(d.getName());
//		return result;
		switch (d.kindPDefinition()) {
		case ASSIGNMENT:
			return AAssignmentDefinitionAssistant.getVariableNames((AAssignmentDefinition)d);
		case CLASS:
			return SClassDefinitionAssistant.getVariableNames((SClassDefinition)d);
		case CLASSINVARIANT:
			return AClassInvariantDefinitionAssistant.getVariableNames((AClassInvariantDefinition)d);
		case EQUALS:
			return AEqualsDefinitionAssistant.getVariableNames((AEqualsDefinition)d);
		case EXPLICITFUNCTION:
			return AExplicitFunctionDefinitionAssistant.getVariableNames((AExplicitFunctionDefinition)d);
		case EXPLICITOPERATION:
			return AExplicitOperationDefinitionAssistant.getVariableNames((AExplicitOperationDefinition)d);
		case EXTERNAL:
			return AExternalDefinitionAssistant.getVariableNames((AExternalDefinition)d);
		case IMPLICITFUNCTION:
			return AImplicitFunctionDefinitionAssistant.getVariableNames((AImplicitFunctionDefinition)d);			 
		case IMPLICITOPERATION:
			return AImplicitOperationDefinitionAssistant.getVariableNames((AImplicitOperationDefinition)d);
		case IMPORTED:
			return AImportedDefinitionAssistant.getVariableNames((AImportedDefinition)d);
		case INHERITED:
			return AInheritedDefinitionAssistant.getVariableNames((AInheritedDefinition)d);
		case INSTANCEVARIABLE:
			return AInstanceVariableDefinitionAssistant.getVariableNames((AInstanceVariableDefinition)d);
		case LOCAL:
			return ALocalDefinitionAssistant.getVariableNames((ALocalDefinition)d);
		case MULTIBINDLIST:
			return AMultiBindListDefinitionAssistant.getVariableNames((AMultiBindListDefinition)d);
		case MUTEXSYNC:
			return AMutexSyncDefinitionAssistant.getVariableNames((AMutexSyncDefinition)d);
		case NAMEDTRACE:
			return ANamedTraceDefinitionAssistant.getVariableNames((ANamedTraceDefinition)d);
		case PERSYNC:
			return APerSyncDefinitionAssistant.getVariableNames((APerSyncDefinition)d);
		case RENAMED:
			return ARenamedDefinitionAssistant.getVariableNames((ARenamedDefinition)d);
		case STATE:
			return AStateDefinitionAssistant.getVariableNames((AStateDefinition)d);
		case THREAD:
			return AThreadDefinitionAssistant.getVariableNames((AThreadDefinition)d);
		case TYPE:
			return ATypeDefinitionAssistant.getVariableNames((ATypeDefinition)d);
		case UNTYPED:
			return AUntypedDefinitionAssistant.getVariableNames((AUntypedDefinition)d);
		case VALUE:
			return AValueDefinitionAssistant.getVariableNames((AValueDefinition)d);
		default:
			assert false : "default case should never happen in getVariableNames";
			return null;
		}

		
	}

	public static boolean isStatic(PDefinition fdef) {
		return PAccessSpecifierAssistant.isStatic(fdef.getAccess());
	}

	public static PDefinition deref(PDefinition def) {
		switch (def.kindPDefinition()) {
		case IMPORTED:
			if (def instanceof AImportedDefinition) {
				return deref(((AImportedDefinition) def).getDef());
			}
			break;
		case INHERITED:
			if (def instanceof AInheritedDefinition) {
				return deref(((AInheritedDefinition) def).getSuperdef());
			}
			break;
		case RENAMED:
			if (def instanceof ARenamedDefinition) {
				return deref(((ARenamedDefinition) def).getDef());
			}
			break;
		}
		return def;

	}

	public static boolean isCallableOperation(PDefinition def) {
		Boolean result = false;
		switch (def.kindPDefinition()) {

		}

		return result;
	}

	public static boolean isUsed(PDefinition u) {
		return u.getUsed();
	}

	public static void implicitDefinitions(PDefinition d, Environment env) {
		switch(d.kindPDefinition())
		{
		case CLASS:
			SClassDefinitionAssistant.implicitDefinitions((SClassDefinition)d,env);
			break;
		case CLASSINVARIANT:
			break;
		case EQUALS:
			break;
		case EXPLICITFUNCTION:
			break;
		case EXPLICITOPERATION:
			break;
		case EXTERNAL:
			break;
		case IMPLICITFUNCTION:
			break;
		case IMPLICITOPERATION:
			break;
		case IMPORTED:
			break;
		case INHERITED:
			break;
		case INSTANCEVARIABLE:
			break;
		case LOCAL:
			break;
		case MULTIBINDLIST:
			break;
		case MUTEXSYNC:
			break;
		case NAMEDTRACE:
			break;
		case PERSYNC:
			break;
		case RENAMED:
			break;
		case STATE:
			break;
		case THREAD:
			break;
		case TYPE:
			break;
		case UNTYPED:
			break;
		case VALUE:
			break;
		default:
			return;
		}
		
	}

	public static void typeResolve(PDefinition d,QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor, TypeCheckInfo question) {
		switch (d.kindPDefinition()) {				
		case CLASS:
			SClassDefinitionAssistant.typeResolve((SClassDefinition)d,rootVisitor,question );
			break;
		case EXPLICITFUNCTION:
			AExplicitFunctionDefinitionAssistant.typeResolve((AExplicitFunctionDefinition)d,rootVisitor,question);
			break;
		case EXPLICITOPERATION:
			AExplicitOperationDefinitionAssistant.typeResolve((AExplicitOperationDefinition)d,rootVisitor,question);
			break;		
		case IMPLICITFUNCTION:
			AImplicitFunctionDefinitionAssistant.typeResolve((AImplicitFunctionDefinition)d,rootVisitor,question);
			break;
		case IMPLICITOPERATION:
			AImplicitOperationDefinitionAssistant.typeResolve((AImplicitOperationDefinition)d,rootVisitor,question);
			break;
		case INSTANCEVARIABLE:
			AInstanceVariableDefinitionAssistant.typeResolve((AInstanceVariableDefinition)d,rootVisitor,question);
			break;
		case LOCAL:
			ALocalDefinitionAssistant.typeResolve((ALocalDefinition)d,rootVisitor,question);
			break;		
		case RENAMED:
			ARenamedDefinitionAssistant.typeResolve((ARenamedDefinition)d,rootVisitor,question);
			break;
		case STATE:
			AStateDefinitionAssistant.typeResolve((AStateDefinition)d,rootVisitor,question);
			break;
		case TYPE:
			ATypeDefinitionAssistant.typeResolve((ATypeDefinition)d,rootVisitor,question);
			break;
		case VALUE:
			AValueDefinitionAssistant.typeResolve((AValueDefinition)d,rootVisitor,question);
		default:
			return;
			
		}
		
	}

	

}
