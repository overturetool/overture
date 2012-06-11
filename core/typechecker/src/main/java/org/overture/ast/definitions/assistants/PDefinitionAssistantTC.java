package org.overture.ast.definitions.assistants;

import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AAssignmentDefinition;
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
import org.overture.ast.factory.AstFactory;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.PType;
import org.overture.ast.types.assistants.PTypeAssistantTC;
import org.overture.typecheck.Environment;
import org.overture.typecheck.TypeCheckInfo;
import org.overture.typecheck.TypeChecker;
import org.overture.typecheck.TypeCheckerErrors;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.util.HelpLexNameToken;

public class PDefinitionAssistantTC extends PDefinitionAssistant {

	
	public static boolean equals(PDefinition def, Object other)		// Used for sets of definitions.
	{
		switch (def.kindPDefinition()) {		
		case EQUALS:
			return AEqualsDefinitionAssistantTC.equals((AEqualsDefinition)def, other);		
		case MULTIBINDLIST:
			return AMultiBindListDefinitionAssistantTC.equals((AMultiBindListDefinition)def,other);
		case MUTEXSYNC:
			return AMutexSyncDefinitionAssistantTC.equals((AMutexSyncDefinition)def,other);		
		case THREAD:
			return AThreadDefinitionAssistantTC.equals((AThreadDefinition)def,other);
		case VALUE:
			return AValueDefinitionAssistantTC.equals((AValueDefinition)def,other);
		default:
			return equalsBaseCase(def, other);
		}
	}
	
	private static boolean equalsBaseCase(PDefinition def, Object other)		// Used for sets of definitions.
	{
		if (other instanceof PDefinition)
		{
			PDefinition odef = (PDefinition)other;
			return def.getName() != null && odef.getName() != null && def.getName().equals(odef.getName());
		}
		return false;
	}
	
	public static boolean hasSupertype(SClassDefinition aClassDefDefinition,
			PType other) {

		if (PTypeAssistantTC.equals(
				PDefinitionAssistantTC.getType(aClassDefDefinition), other)) {
			return true;
		} else {
			for (PType type : aClassDefDefinition.getSupertypes()) {
				AClassType sclass = (AClassType) type;

				if (PTypeAssistantTC.hasSupertype(sclass, other)) {
					return true;
				}
			}
		}
		return false;

	}

	public static boolean isFunctionOrOperation(PDefinition possible) {
		return isFunction(possible) || isOperation(possible);
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
			return SClassDefinitionAssistantTC.findType((SClassDefinition) d,
					sought, fromModule);
		case IMPORTED:
			return AImportedDefinitionAssistantTC.findType(
					(AImportedDefinition) d, sought, fromModule);
		case INHERITED:
			return AInheritedDefinitionAssistantTC.findType(
					(AInheritedDefinition) d, sought, fromModule);
		case RENAMED:
			return ARenamedDefinitionAssistantTC.findType((ARenamedDefinition) d,
					sought, fromModule);
		case STATE:
			return AStateDefinitionAssistantTC.findType((AStateDefinition) d,
					sought, fromModule);
		case TYPE:
			return ATypeDefinitionAssistantTC.findType((ATypeDefinition) d,
					sought, fromModule);
		default:
			return null;
		}
	}

	public static PDefinition findName(PDefinition d, LexNameToken sought,
			NameScope scope) {
		switch (d.kindPDefinition()) {
		// case ASSIGNMENT:
		case CLASS:
			return SClassDefinitionAssistantTC.findName((SClassDefinition) d,
					sought, scope);
		case EQUALS:
			return AEqualsDefinitionAssistantTC.findName((AEqualsDefinition) d,
					sought, scope);
		case EXPLICITFUNCTION:
			return AExplicitFunctionDefinitionAssistantTC.findName(
					(AExplicitFunctionDefinition) d, sought, scope);
		case EXPLICITOPERATION:
			return AExplicitOperationDefinitionAssistantTC.findName(
					(AExplicitOperationDefinition) d, sought, scope);
		case EXTERNAL:
			return AExternalDefinitionAssistantTC.findName(
					(AExternalDefinition) d, sought, scope);
		case IMPLICITFUNCTION:
			return AImplicitFunctionDefinitionAssistantTC.findName(
					(AImplicitFunctionDefinition) d, sought, scope);
		case IMPLICITOPERATION:
			return AImplicitOperationDefinitionAssistantTC.findName(
					(AImplicitOperationDefinition) d, sought, scope);
		case IMPORTED:
			return AImportedDefinitionAssistantTC.findName(
					(AImportedDefinition) d, sought, scope);
		case INHERITED:
			return AInheritedDefinitionAssistantTC.findName(
					(AInheritedDefinition) d, sought, scope);
		case INSTANCEVARIABLE:
			return AInstanceVariableDefinitionAssistantTC.findName(
					(AInstanceVariableDefinition) d, sought, scope);
		case MULTIBINDLIST:
			return AMultiBindListDefinitionAssistantTC.findName(
					(AMultiBindListDefinition) d, sought, scope);
		case MUTEXSYNC:
			return AMutexSyncDefinitionAssistantTC.findName(
					(AMutexSyncDefinition) d, sought, scope);
		case NAMEDTRACE:
			return ANamedTraceDefinitionAssistantTC.findName(
					(ANamedTraceDefinition) d, sought, scope);
		case PERSYNC:
			return APerSyncDefinitionAssistantTC.findName((APerSyncDefinition) d,
					sought, scope);
		case RENAMED:
			return ARenamedDefinitionAssistantTC.findName((ARenamedDefinition) d,
					sought, scope);
		case STATE:
			return AStateDefinitionAssistantTC.findName((AStateDefinition) d,
					sought, scope);
		case THREAD:
			return AThreadDefinitionAssistantTC.findName((AThreadDefinition) d,
					sought, scope);
		case TYPE:
			return ATypeDefinitionAssistantTC.findName((ATypeDefinition) d,
					sought, scope);
		case VALUE:
			return AValueDefinitionAssistantTC.findName((AValueDefinition) d,
					sought, scope);
		default:
			return findNameBaseCase(d, sought, scope);
		}

	}

	public static PDefinition findNameBaseCase(PDefinition d,
			LexNameToken sought, NameScope scope) {
		if (HelpLexNameToken.isEqual(d.getName(), sought)) {
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
			AExternalDefinitionAssistantTC.markUsed((AExternalDefinition) d);
			break;
		case IMPORTED:
			AImportedDefinitionAssistantTC.markUsed((AImportedDefinition) d);
			break;
		case INHERITED:
			AInheritedDefinitionAssistantTC.markUsed((AInheritedDefinition) d);
			break;
		case RENAMED:
			ARenamedDefinitionAssistantTC.markUsed((ARenamedDefinition) d);
		default:
			d.setUsed(true);
			break;
		}
	}

	public static void unusedCheck(PDefinition d) {
		switch (d.kindPDefinition()) {
		case EQUALS:
			AEqualsDefinitionAssistantTC.unusedCheck((AEqualsDefinition) d);
			break;
		case MULTIBINDLIST:
			AMultiBindListDefinitionAssistantTC
					.unusedCheck((AMultiBindListDefinition) d);
			break;
		case STATE:
			AStateDefinitionAssistantTC.unusedCheck((AStateDefinition) d);
			break;
		case VALUE:
			AValueDefinitionAssistantTC.unusedCheck((AValueDefinition) d);
			break;
		default:
			unusedCheckBaseCase(d);
			break;
		}
	}

	public static void unusedCheckBaseCase(PDefinition d) {
		if (!PDefinitionAssistantTC.isUsed(d)) {
			TypeCheckerErrors.warning(5000, "Definition '" + d.getName()
					+ "' not used", d.getLocation(), d);
			markUsed(d); // To avoid multiple warnings
		}

	}

	public static List<PDefinition> getDefinitions(PDefinition d) {

		switch (d.kindPDefinition()) {

		case ASSIGNMENT:
			return AAssignmentDefinitionAssistantTC
					.getDefinitions((AAssignmentDefinition) d);
		case CLASS:
			return SClassDefinitionAssistantTC
					.getDefinitions((SClassDefinition) d);
		case CLASSINVARIANT:
			return AClassInvariantDefinitionAssistantTC
					.getDefinitions((AClassInvariantDefinition) d);
		case EQUALS:
			return AEqualsDefinitionAssistantTC
					.getDefinitions((AEqualsDefinition) d);
		case EXPLICITFUNCTION:
			return AExplicitFunctionDefinitionAssistantTC
					.getDefinitions((AExplicitFunctionDefinition) d);
		case EXPLICITOPERATION:
			return AExplicitOperationDefinitionAssistantTC
					.getDefinitions((AExplicitOperationDefinition) d);
		case EXTERNAL:
			return AExternalDefinitionAssistantTC
					.getDefinitions((AExternalDefinition) d);
		case IMPLICITFUNCTION:
			return AImplicitFunctionDefinitionAssistantTC
					.getDefinitions((AImplicitFunctionDefinition) d);
		case IMPLICITOPERATION:
			return AImplicitOperationDefinitionAssistantTC
					.getDefinitions((AImplicitOperationDefinition) d);
		case IMPORTED:
			return AImportedDefinitionAssistantTC
					.getDefinitions((AImportedDefinition) d);
		case INHERITED:
			return AInheritedDefinitionAssistantTC
					.getDefinitions((AInheritedDefinition) d);
		case INSTANCEVARIABLE:
			return AInstanceVariableDefinitionAssistantTC
					.getDefinitions((AInstanceVariableDefinition) d);
		case LOCAL:
			return ALocalDefinitionAssistantTC
					.getDefinitions((ALocalDefinition) d);
		case MULTIBINDLIST:
			return AMultiBindListDefinitionAssistantTC
					.getDefinitions((AMultiBindListDefinition) d);
		case MUTEXSYNC:
			return AMutexSyncDefinitionAssistantTC
					.getDefinitions((AMutexSyncDefinition) d);
		case NAMEDTRACE:
			return ANamedTraceDefinitionAssistantTC
					.getDefinitions((ANamedTraceDefinition) d);
		case PERSYNC:
			return APerSyncDefinitionAssistantTC
					.getDefinitions((APerSyncDefinition) d);
		case RENAMED:
			return ARenamedDefinitionAssistantTC
					.getDefinitions((ARenamedDefinition) d);
		case STATE:
			return AStateDefinitionAssistantTC
					.getDefinitions((AStateDefinition) d);
		case THREAD:
			return AThreadDefinitionAssistantTC
					.getDefinitions((AThreadDefinition) d);
		case TYPE:
			return ATypeDefinitionAssistantTC.getDefinitions((ATypeDefinition) d);
		case UNTYPED:
			return AUntypedDefinitionAssistantTC
					.getDefinitions((AUntypedDefinition) d);
		case VALUE:
			return AValueDefinitionAssistantTC
					.getDefinitions((AValueDefinition) d);
		default:
			assert false : "getDefinitions should never hit the default case";
			return null;
		}

	}

	public static PDefinition getSelfDefinition(PDefinition d) {
		switch (d.kindPDefinition()) {
		case CLASS:
			return SClassDefinitionAssistantTC
					.getSelfDefinition((SClassDefinition) d);

		default:
			return getSelfDefinition(d.getClassDefinition());
		}

	}

	public static LexNameList getVariableNames(PDefinition d) {

		// List<LexNameToken> result = new Vector<LexNameToken>();
		// result.add(d.getName());
		// return result;
		switch (d.kindPDefinition()) {
		case ASSIGNMENT:
			return AAssignmentDefinitionAssistantTC
					.getVariableNames((AAssignmentDefinition) d);
		case CLASS:
			return SClassDefinitionAssistantTC
					.getVariableNames((SClassDefinition) d);
		case CLASSINVARIANT:
			return AClassInvariantDefinitionAssistantTC
					.getVariableNames((AClassInvariantDefinition) d);
		case EQUALS:
			return AEqualsDefinitionAssistantTC
					.getVariableNames((AEqualsDefinition) d);
		case EXPLICITFUNCTION:
			return AExplicitFunctionDefinitionAssistantTC
					.getVariableNames((AExplicitFunctionDefinition) d);
		case EXPLICITOPERATION:
			return AExplicitOperationDefinitionAssistantTC
					.getVariableNames((AExplicitOperationDefinition) d);
		case EXTERNAL:
			return AExternalDefinitionAssistantTC
					.getVariableNames((AExternalDefinition) d);
		case IMPLICITFUNCTION:
			return AImplicitFunctionDefinitionAssistantTC
					.getVariableNames((AImplicitFunctionDefinition) d);
		case IMPLICITOPERATION:
			return AImplicitOperationDefinitionAssistantTC
					.getVariableNames((AImplicitOperationDefinition) d);
		case IMPORTED:
			return AImportedDefinitionAssistantTC
					.getVariableNames((AImportedDefinition) d);
		case INHERITED:
			return AInheritedDefinitionAssistantTC
					.getVariableNames((AInheritedDefinition) d);
		case INSTANCEVARIABLE:
			return AInstanceVariableDefinitionAssistantTC
					.getVariableNames((AInstanceVariableDefinition) d);
		case LOCAL:
			return ALocalDefinitionAssistantTC
					.getVariableNames((ALocalDefinition) d);
		case MULTIBINDLIST:
			return AMultiBindListDefinitionAssistantTC
					.getVariableNames((AMultiBindListDefinition) d);
		case MUTEXSYNC:
			return AMutexSyncDefinitionAssistantTC
					.getVariableNames((AMutexSyncDefinition) d);
		case NAMEDTRACE:
			return ANamedTraceDefinitionAssistantTC
					.getVariableNames((ANamedTraceDefinition) d);
		case PERSYNC:
			return APerSyncDefinitionAssistantTC
					.getVariableNames((APerSyncDefinition) d);
		case RENAMED:
			return ARenamedDefinitionAssistantTC
					.getVariableNames((ARenamedDefinition) d);
		case STATE:
			return AStateDefinitionAssistantTC
					.getVariableNames((AStateDefinition) d);
		case THREAD:
			return AThreadDefinitionAssistantTC
					.getVariableNames((AThreadDefinition) d);
		case TYPE:
			return ATypeDefinitionAssistantTC
					.getVariableNames((ATypeDefinition) d);
		case UNTYPED:
			return AUntypedDefinitionAssistantTC
					.getVariableNames((AUntypedDefinition) d);
		case VALUE:
			return AValueDefinitionAssistantTC
					.getVariableNames((AValueDefinition) d);
		default:
			assert false : "default case should never happen in getVariableNames";
			return null;
		}

	}

	public static boolean isStatic(PDefinition fdef) {
		return PAccessSpecifierAssistantTC.isStatic(fdef.getAccess());
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
		
		switch (def.kindPDefinition()) {
		case EXPLICITOPERATION:
			return true;		
		case IMPLICITOPERATION:
			return ((AImplicitOperationDefinition)def).getBody() != null;
		case IMPORTED:
			return isCallableOperation(((AImportedDefinition)def).getDef()); 
		case INHERITED:
			return isCallableOperation(((AInheritedDefinition)def).getSuperdef()); 		
		case RENAMED:
			return isCallableOperation(((ARenamedDefinition)def).getDef());
		default:
			return false;
		}		
	}

	public static boolean isUsed(PDefinition u) {
		switch (u.kindPDefinition()) {
		case EXTERNAL:
			return AExternalDefinitionAssistantTC.isUsed((AExternalDefinition) u);
//		case IMPORTED:
//			return AImportedDefinitionAssistant.isUsed((AImportedDefinition) u);
		case INHERITED:
			return AInheritedDefinitionAssistantTC
					.isUsed((AInheritedDefinition) u);
//		case RENAMED:
//			return ARenamedDefinitionAssistant.isUsed((ARenamedDefinition) u);
		default:
			return u.getUsed();
		}

	}

	public static void implicitDefinitions(PDefinition d, Environment env) {
		switch (d.kindPDefinition()) {
		case CLASS:
			SClassDefinitionAssistantTC.implicitDefinitions((SClassDefinition) d,
					env);
			break;
		case CLASSINVARIANT:
			break;
		case EQUALS:
			break;
		case EXPLICITFUNCTION:
			AExplicitFunctionDefinitionAssistantTC.implicitDefinitions(
					(AExplicitFunctionDefinition) d, env);
			break;
		case EXPLICITOPERATION:
			AExplicitOperationDefinitionAssistantTC.implicitDefinitions(
					(AExplicitOperationDefinition) d, env);
			break;
		case IMPLICITFUNCTION:
			AImplicitFunctionDefinitionAssistantTC.implicitDefinitions(
					(AImplicitFunctionDefinition) d, env);
			break;
		case IMPLICITOPERATION:
			AImplicitOperationDefinitionAssistantTC.implicitDefinitions(
					(AImplicitOperationDefinition) d, env);
			break;
		case STATE:
			AStateDefinitionAssistantTC.implicitDefinitions((AStateDefinition) d,
					env);
			break;
		case THREAD:
			AThreadDefinitionAssistantTC.implicitDefinitions(
					(AThreadDefinition) d, env);
			break;
		case TYPE:
			ATypeDefinitionAssistantTC.implicitDefinitions((ATypeDefinition) d,
					env);
			break;
		default:
			return;
		}

	}

	public static void typeResolve(PDefinition d,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {
		switch (d.kindPDefinition()) {
		case CLASS:
			SClassDefinitionAssistantTC.typeResolve((SClassDefinition) d,
					rootVisitor, question);
			break;
		case EXPLICITFUNCTION:
			AExplicitFunctionDefinitionAssistantTC.typeResolve(
					(AExplicitFunctionDefinition) d, rootVisitor, question);
			break;
		case EXPLICITOPERATION:
			AExplicitOperationDefinitionAssistantTC.typeResolve(
					(AExplicitOperationDefinition) d, rootVisitor, question);
			break;
		case IMPLICITFUNCTION:
			AImplicitFunctionDefinitionAssistantTC.typeResolve(
					(AImplicitFunctionDefinition) d, rootVisitor, question);
			break;
		case IMPLICITOPERATION:
			AImplicitOperationDefinitionAssistantTC.typeResolve(
					(AImplicitOperationDefinition) d, rootVisitor, question);
			break;
		case INSTANCEVARIABLE:
			AInstanceVariableDefinitionAssistantTC.typeResolve(
					(AInstanceVariableDefinition) d, rootVisitor, question);
			break;
		case LOCAL:
			ALocalDefinitionAssistantTC.typeResolve((ALocalDefinition) d,
					rootVisitor, question);
			break;
		case RENAMED:
			ARenamedDefinitionAssistantTC.typeResolve((ARenamedDefinition) d,
					rootVisitor, question);
			break;
		case STATE:
			AStateDefinitionAssistantTC.typeResolve((AStateDefinition) d,
					rootVisitor, question);
			break;
		case TYPE:
			ATypeDefinitionAssistantTC.typeResolve((ATypeDefinition) d,
					rootVisitor, question);
			break;
		case VALUE:
			AValueDefinitionAssistantTC.typeResolve((AValueDefinition) d,
					rootVisitor, question);
		default:
			return;

		}

	}

	public static PType getType(PDefinition def) {
		switch (def.kindPDefinition()) {
		case ASSIGNMENT:
			return def.getType();
		case CLASS:
			return SClassDefinitionAssistantTC.getType((SClassDefinition) def);
		case CLASSINVARIANT:
			return AstFactory.newABooleanBasicType(def.getLocation());
		case EQUALS:
			return AEqualsDefinitionAssistantTC.getType((AEqualsDefinition) def);
		case EXPLICITFUNCTION:
			return def.getType();
		case EXPLICITOPERATION:
			return def.getType();
		case EXTERNAL:
			return AExternalDefinitionAssistantTC
					.getType((AExternalDefinition) def);
		case IMPLICITFUNCTION:
			return def.getType();
		case IMPLICITOPERATION:
			return def.getType();
		case IMPORTED:
			return getType(((AImportedDefinition) def).getDef());
		case INHERITED:
			return AInheritedDefinitionAssistantTC
					.getType((AInheritedDefinition) def);
		case INSTANCEVARIABLE:
			return def.getType();
		case LOCAL:
			return def.getType() == null ? AstFactory.newAUnknownType(def.getLocation()) : def.getType();
		case MULTIBINDLIST:
			return AMultiBindListDefinitionAssistantTC
					.getType((AMultiBindListDefinition) def);
		case MUTEXSYNC:
			return AstFactory.newAUnknownType(def.getLocation());
		case NAMEDTRACE:
			return AstFactory.newAOperationType(def.getLocation(), new Vector<PType>(), AstFactory.newAVoidType(def.getLocation()));
		case PERSYNC:
			return AstFactory.newABooleanBasicType(def.getLocation());
		case RENAMED:
			return getType(((ARenamedDefinition) def).getDef());
		case STATE:
			return ((AStateDefinition) def).getRecordType();
		case THREAD:
			return AstFactory.newAUnknownType(def.getLocation());
		case TYPE:
			return ((ATypeDefinition) def).getInvType();
		case UNTYPED:
			return  AstFactory.newAUnknownType(def.getLocation());
		case VALUE:
			return AValueDefinitionAssistantTC.getType((AValueDefinition) def);
		default:
			assert false : "should never go in this case";
			return null;
		}

	}

	public static boolean isUpdatable(PDefinition d) {
		switch (d.kindPDefinition()) {
		case ASSIGNMENT:
		case INSTANCEVARIABLE:
		case EXTERNAL:
			return true;
		case IMPORTED:
			return PDefinitionAssistantTC.isUpdatable(((AImportedDefinition) d)
					.getDef());
		case INHERITED:
			return PDefinitionAssistantTC
					.isUpdatable(((AInheritedDefinition) d).getSuperdef());
		case LOCAL:
			return ((ALocalDefinition) d).getNameScope().matches(
					NameScope.STATE);
		case RENAMED:
			return PDefinitionAssistantTC.isUpdatable(((ARenamedDefinition) d)
					.getDef());
		default:
			return false;
		}
	}
	

	public static String kind(PDefinition d) {
		switch (d.kindPDefinition()) {
		case ASSIGNMENT:
			return "assignable variable";
		case CLASS:
			return "class";
		case CLASSINVARIANT:
			return "invariant";
		case EQUALS:
			return "equals";
		case EXPLICITFUNCTION:
			return "explicit function";
		case EXPLICITOPERATION:
			return "explicit operation";
		case EXTERNAL:
			return "external";
		case IMPLICITFUNCTION:
			return "implicit function";
		case IMPLICITOPERATION:
			return "implicit operation";
		case IMPORTED:
			return "import";
		case INHERITED:
			return kind(((AInheritedDefinition) d).getSuperdef());
		case INSTANCEVARIABLE:
			return "instance variable";
		case LOCAL:
			return "local";
		case MULTIBINDLIST:
			return "bind";
		case MUTEXSYNC:
			return "mutex predicate";
		case NAMEDTRACE:
			return "trace";
		case PERSYNC:
			return "permission predicate";
		case RENAMED:
			return kind(((ARenamedDefinition) d).getDef());
		case STATE:
			return "state";
		case THREAD:
			return "thread";
		case TYPE:
			return "type";
		case UNTYPED:
			return "untyped";
		case VALUE:
			return "value";
		default:
			return null;
		}

	}

	public static boolean isFunction(PDefinition def) {
		switch (def.kindPDefinition()) {
		case EXPLICITFUNCTION:
		case IMPLICITFUNCTION:
			return true;
		case IMPORTED:
			return isFunction(((AImportedDefinition) def).getDef());
		case INHERITED:
			return isFunction(((AInheritedDefinition) def).getSuperdef());
		case LOCAL:
			return ALocalDefinitionAssistantTC.isFunction((ALocalDefinition) def);
		case RENAMED:
			return isFunction(((ARenamedDefinition) def).getDef());
		default:
			return false;
		}
	}

	public static boolean isOperation(PDefinition def) {
		switch (def.kindPDefinition()) {
		case EXPLICITOPERATION:
		case IMPLICITOPERATION:
		case NAMEDTRACE:
		case THREAD:
			return true;
		case IMPORTED:
			return isOperation(((AImportedDefinition) def).getDef());
		case INHERITED:
			return isOperation(((AInheritedDefinition) def).getSuperdef());
		case RENAMED:
			return isOperation(((ARenamedDefinition) def).getDef());
		default:
			return false;
		}
	}
	
	public static LexNameList  getOldNames(PDefinition def)
	{
		switch (def.kindPDefinition()) {		
		case EQUALS:
			return AEqualsDefinitionAssistantTC.getOldNames((AEqualsDefinition)def);
		case VALUE:
			return AValueDefinitionAssistantTC.getOldNames((AValueDefinition) def);
		default:
			return new LexNameList();
		}
		
		
	}

}
