package org.overture.typechecker.assistant.definition;

import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.assistant.definition.PDefinitionAssistant;
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
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.PType;
import org.overture.typechecker.Environment;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.TypeChecker;
import org.overture.typechecker.TypeCheckerErrors;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;
import org.overture.typechecker.util.HelpLexNameToken;

public class PDefinitionAssistantTC extends PDefinitionAssistant {

	
	public static boolean equals(PDefinition def, Object other)		// Used for sets of definitions.
	{
		switch (def.kindPDefinition()) {		
		case AEqualsDefinition.kindPDefinition:
			return AEqualsDefinitionAssistantTC.equals((AEqualsDefinition)def, other);		
		case AMultiBindListDefinition.kindPDefinition:
			return AMultiBindListDefinitionAssistantTC.equals((AMultiBindListDefinition)def,other);
		case AMutexSyncDefinition.kindPDefinition:
			return AMutexSyncDefinitionAssistantTC.equals((AMutexSyncDefinition)def,other);		
		case AThreadDefinition.kindPDefinition:
			return AThreadDefinitionAssistantTC.equals((AThreadDefinition)def,other);
		case AValueDefinition.kindPDefinition:
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
			ILexNameToken name, String fromModule) {

		for (PDefinition d : definitions) {
			PDefinition def = findType(d, name, fromModule);

			if (def != null) {
				return def;
			}
		}

		return null;

	}

	public static PDefinition findType(PDefinition d, ILexNameToken sought,
			String fromModule) {
		switch (d.kindPDefinition()) {

		case SClassDefinition.kindPDefinition:
			return SClassDefinitionAssistantTC.findType((SClassDefinition) d,
					sought, fromModule);
		case AImportedDefinition.kindPDefinition:
			return AImportedDefinitionAssistantTC.findType(
					(AImportedDefinition) d, sought, fromModule);
		case AInheritedDefinition.kindPDefinition:
			return AInheritedDefinitionAssistantTC.findType(
					(AInheritedDefinition) d, sought, fromModule);
		case ARenamedDefinition.kindPDefinition:
			return ARenamedDefinitionAssistantTC.findType((ARenamedDefinition) d,
					sought, fromModule);
		case AStateDefinition.kindPDefinition:
			return AStateDefinitionAssistantTC.findType((AStateDefinition) d,
					sought, fromModule);
		case ATypeDefinition.kindPDefinition:
			return ATypeDefinitionAssistantTC.findType((ATypeDefinition) d,
					sought, fromModule);
		default:
			return null;
		}
	}

	public static PDefinition findName(PDefinition d, ILexNameToken sought,
			NameScope scope) {
		switch (d.kindPDefinition()) {
		// case AAssignmentDefinition.kindPDefinition:
		case SClassDefinition.kindPDefinition:
			return SClassDefinitionAssistantTC.findName((SClassDefinition) d,
					sought, scope);
		case AEqualsDefinition.kindPDefinition:
			return AEqualsDefinitionAssistantTC.findName((AEqualsDefinition) d,
					sought, scope);
		case AExplicitFunctionDefinition.kindPDefinition:
			return AExplicitFunctionDefinitionAssistantTC.findName(
					(AExplicitFunctionDefinition) d, sought, scope);
		case AExplicitOperationDefinition.kindPDefinition:
			return AExplicitOperationDefinitionAssistantTC.findName(
					(AExplicitOperationDefinition) d, sought, scope);
		case AExternalDefinition.kindPDefinition:
			return AExternalDefinitionAssistantTC.findName(
					(AExternalDefinition) d, sought, scope);
		case AImplicitFunctionDefinition.kindPDefinition:
			return AImplicitFunctionDefinitionAssistantTC.findName(
					(AImplicitFunctionDefinition) d, sought, scope);
		case AImplicitOperationDefinition.kindPDefinition:
			return AImplicitOperationDefinitionAssistantTC.findName(
					(AImplicitOperationDefinition) d, sought, scope);
		case AImportedDefinition.kindPDefinition:
			return AImportedDefinitionAssistantTC.findName(
					(AImportedDefinition) d, sought, scope);
		case AInheritedDefinition.kindPDefinition:
			return AInheritedDefinitionAssistantTC.findName(
					(AInheritedDefinition) d, sought, scope);
		case AInstanceVariableDefinition.kindPDefinition:
			return AInstanceVariableDefinitionAssistantTC.findName(
					(AInstanceVariableDefinition) d, sought, scope);
		case AMultiBindListDefinition.kindPDefinition:
			return AMultiBindListDefinitionAssistantTC.findName(
					(AMultiBindListDefinition) d, sought, scope);
		case AMutexSyncDefinition.kindPDefinition:
			return AMutexSyncDefinitionAssistantTC.findName(
					(AMutexSyncDefinition) d, sought, scope);
		case ANamedTraceDefinition.kindPDefinition:
			return ANamedTraceDefinitionAssistantTC.findName(
					(ANamedTraceDefinition) d, sought, scope);
		case APerSyncDefinition.kindPDefinition:
			return APerSyncDefinitionAssistantTC.findName((APerSyncDefinition) d,
					sought, scope);
		case ARenamedDefinition.kindPDefinition:
			return ARenamedDefinitionAssistantTC.findName((ARenamedDefinition) d,
					sought, scope);
		case AStateDefinition.kindPDefinition:
			return AStateDefinitionAssistantTC.findName((AStateDefinition) d,
					sought, scope);
		case AThreadDefinition.kindPDefinition:
			return AThreadDefinitionAssistantTC.findName((AThreadDefinition) d,
					sought, scope);
		case ATypeDefinition.kindPDefinition:
			return ATypeDefinitionAssistantTC.findName((ATypeDefinition) d,
					sought, scope);
		case AValueDefinition.kindPDefinition:
			return AValueDefinitionAssistantTC.findName((AValueDefinition) d,
					sought, scope);
		default:
			return findNameBaseCase(d, sought, scope);
		}

	}

	public static PDefinition findNameBaseCase(PDefinition d,
			ILexNameToken sought, NameScope scope) {
		if (HelpLexNameToken.isEqual(d.getName(), sought)) {
			if ((d.getNameScope() == NameScope.STATE && !scope
					.matches(NameScope.STATE))
					|| (d.getNameScope() == NameScope.OLDSTATE && !scope
							.matches(NameScope.OLDSTATE))) {

				TypeChecker.report(3302, "State variable '" + sought.getFullName()
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
		case AExternalDefinition.kindPDefinition:
			AExternalDefinitionAssistantTC.markUsed((AExternalDefinition) d);
			break;
		case AImportedDefinition.kindPDefinition:
			AImportedDefinitionAssistantTC.markUsed((AImportedDefinition) d);
			break;
		case AInheritedDefinition.kindPDefinition:
			AInheritedDefinitionAssistantTC.markUsed((AInheritedDefinition) d);
			break;
		case ARenamedDefinition.kindPDefinition:
			ARenamedDefinitionAssistantTC.markUsed((ARenamedDefinition) d);
		default:
			d.setUsed(true);
			break;
		}
	}

	public static void unusedCheck(PDefinition d) {
		switch (d.kindPDefinition()) {
		case AEqualsDefinition.kindPDefinition:
			AEqualsDefinitionAssistantTC.unusedCheck((AEqualsDefinition) d);
			break;
		case AMultiBindListDefinition.kindPDefinition:
			AMultiBindListDefinitionAssistantTC
					.unusedCheck((AMultiBindListDefinition) d);
			break;
		case AStateDefinition.kindPDefinition:
			AStateDefinitionAssistantTC.unusedCheck((AStateDefinition) d);
			break;
		case AValueDefinition.kindPDefinition:
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

		case AAssignmentDefinition.kindPDefinition:
			return AAssignmentDefinitionAssistantTC
					.getDefinitions((AAssignmentDefinition) d);
		case SClassDefinition.kindPDefinition:
			return SClassDefinitionAssistantTC
					.getDefinitions((SClassDefinition) d);
		case AClassInvariantDefinition.kindPDefinition:
			return AClassInvariantDefinitionAssistantTC
					.getDefinitions((AClassInvariantDefinition) d);
		case AEqualsDefinition.kindPDefinition:
			return AEqualsDefinitionAssistantTC
					.getDefinitions((AEqualsDefinition) d);
		case AExplicitFunctionDefinition.kindPDefinition:
			return AExplicitFunctionDefinitionAssistantTC
					.getDefinitions((AExplicitFunctionDefinition) d);
		case AExplicitOperationDefinition.kindPDefinition:
			return AExplicitOperationDefinitionAssistantTC
					.getDefinitions((AExplicitOperationDefinition) d);
		case AExternalDefinition.kindPDefinition:
			return AExternalDefinitionAssistantTC
					.getDefinitions((AExternalDefinition) d);
		case AImplicitFunctionDefinition.kindPDefinition:
			return AImplicitFunctionDefinitionAssistantTC
					.getDefinitions((AImplicitFunctionDefinition) d);
		case AImplicitOperationDefinition.kindPDefinition:
			return AImplicitOperationDefinitionAssistantTC
					.getDefinitions((AImplicitOperationDefinition) d);
		case AImportedDefinition.kindPDefinition:
			return AImportedDefinitionAssistantTC
					.getDefinitions((AImportedDefinition) d);
		case AInheritedDefinition.kindPDefinition:
			return AInheritedDefinitionAssistantTC
					.getDefinitions((AInheritedDefinition) d);
		case AInstanceVariableDefinition.kindPDefinition:
			return AInstanceVariableDefinitionAssistantTC
					.getDefinitions((AInstanceVariableDefinition) d);
		case ALocalDefinition.kindPDefinition:
			return ALocalDefinitionAssistantTC
					.getDefinitions((ALocalDefinition) d);
		case AMultiBindListDefinition.kindPDefinition:
			return AMultiBindListDefinitionAssistantTC
					.getDefinitions((AMultiBindListDefinition) d);
		case AMutexSyncDefinition.kindPDefinition:
			return AMutexSyncDefinitionAssistantTC
					.getDefinitions((AMutexSyncDefinition) d);
		case ANamedTraceDefinition.kindPDefinition:
			return ANamedTraceDefinitionAssistantTC
					.getDefinitions((ANamedTraceDefinition) d);
		case APerSyncDefinition.kindPDefinition:
			return APerSyncDefinitionAssistantTC
					.getDefinitions((APerSyncDefinition) d);
		case ARenamedDefinition.kindPDefinition:
			return ARenamedDefinitionAssistantTC
					.getDefinitions((ARenamedDefinition) d);
		case AStateDefinition.kindPDefinition:
			return AStateDefinitionAssistantTC
					.getDefinitions((AStateDefinition) d);
		case AThreadDefinition.kindPDefinition:
			return AThreadDefinitionAssistantTC
					.getDefinitions((AThreadDefinition) d);
		case ATypeDefinition.kindPDefinition:
			return ATypeDefinitionAssistantTC.getDefinitions((ATypeDefinition) d);
		case AUntypedDefinition.kindPDefinition:
			return AUntypedDefinitionAssistantTC
					.getDefinitions((AUntypedDefinition) d);
		case AValueDefinition.kindPDefinition:
			return AValueDefinitionAssistantTC
					.getDefinitions((AValueDefinition) d);
		default:
			assert false : "getDefinitions should never hit the default case";
			return null;
		}

	}

	public static PDefinition getSelfDefinition(PDefinition d) {
		switch (d.kindPDefinition()) {
		case SClassDefinition.kindPDefinition:
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
		case AAssignmentDefinition.kindPDefinition:
			return AAssignmentDefinitionAssistantTC
					.getVariableNames((AAssignmentDefinition) d);
		case SClassDefinition.kindPDefinition:
			return SClassDefinitionAssistantTC
					.getVariableNames((SClassDefinition) d);
		case AClassInvariantDefinition.kindPDefinition:
			return AClassInvariantDefinitionAssistantTC
					.getVariableNames((AClassInvariantDefinition) d);
		case AEqualsDefinition.kindPDefinition:
			return AEqualsDefinitionAssistantTC
					.getVariableNames((AEqualsDefinition) d);
		case AExplicitFunctionDefinition.kindPDefinition:
			return AExplicitFunctionDefinitionAssistantTC
					.getVariableNames((AExplicitFunctionDefinition) d);
		case AExplicitOperationDefinition.kindPDefinition:
			return AExplicitOperationDefinitionAssistantTC
					.getVariableNames((AExplicitOperationDefinition) d);
		case AExternalDefinition.kindPDefinition:
			return AExternalDefinitionAssistantTC
					.getVariableNames((AExternalDefinition) d);
		case AImplicitFunctionDefinition.kindPDefinition:
			return AImplicitFunctionDefinitionAssistantTC
					.getVariableNames((AImplicitFunctionDefinition) d);
		case AImplicitOperationDefinition.kindPDefinition:
			return AImplicitOperationDefinitionAssistantTC
					.getVariableNames((AImplicitOperationDefinition) d);
		case AImportedDefinition.kindPDefinition:
			return AImportedDefinitionAssistantTC
					.getVariableNames((AImportedDefinition) d);
		case AInheritedDefinition.kindPDefinition:
			return AInheritedDefinitionAssistantTC
					.getVariableNames((AInheritedDefinition) d);
		case AInstanceVariableDefinition.kindPDefinition:
			return AInstanceVariableDefinitionAssistantTC
					.getVariableNames((AInstanceVariableDefinition) d);
		case ALocalDefinition.kindPDefinition:
			return ALocalDefinitionAssistantTC
					.getVariableNames((ALocalDefinition) d);
		case AMultiBindListDefinition.kindPDefinition:
			return AMultiBindListDefinitionAssistantTC
					.getVariableNames((AMultiBindListDefinition) d);
		case AMutexSyncDefinition.kindPDefinition:
			return AMutexSyncDefinitionAssistantTC
					.getVariableNames((AMutexSyncDefinition) d);
		case ANamedTraceDefinition.kindPDefinition:
			return ANamedTraceDefinitionAssistantTC
					.getVariableNames((ANamedTraceDefinition) d);
		case APerSyncDefinition.kindPDefinition:
			return APerSyncDefinitionAssistantTC
					.getVariableNames((APerSyncDefinition) d);
		case ARenamedDefinition.kindPDefinition:
			return ARenamedDefinitionAssistantTC
					.getVariableNames((ARenamedDefinition) d);
		case AStateDefinition.kindPDefinition:
			return AStateDefinitionAssistantTC
					.getVariableNames((AStateDefinition) d);
		case AThreadDefinition.kindPDefinition:
			return AThreadDefinitionAssistantTC
					.getVariableNames((AThreadDefinition) d);
		case ATypeDefinition.kindPDefinition:
			return ATypeDefinitionAssistantTC
					.getVariableNames((ATypeDefinition) d);
		case AUntypedDefinition.kindPDefinition:
			return AUntypedDefinitionAssistantTC
					.getVariableNames((AUntypedDefinition) d);
		case AValueDefinition.kindPDefinition:
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
		case AImportedDefinition.kindPDefinition:
			if (def instanceof AImportedDefinition) {
				return deref(((AImportedDefinition) def).getDef());
			}
			break;
		case AInheritedDefinition.kindPDefinition:
			if (def instanceof AInheritedDefinition) {
				return deref(((AInheritedDefinition) def).getSuperdef());
			}
			break;
		case ARenamedDefinition.kindPDefinition:
			if (def instanceof ARenamedDefinition) {
				return deref(((ARenamedDefinition) def).getDef());
			}
			break;
		}
		return def;

	}

	public static boolean isCallableOperation(PDefinition def) {
		
		switch (def.kindPDefinition()) {
		case AExplicitOperationDefinition.kindPDefinition:
			return true;		
		case AImplicitOperationDefinition.kindPDefinition:
			return ((AImplicitOperationDefinition)def).getBody() != null;
		case AImportedDefinition.kindPDefinition:
			return isCallableOperation(((AImportedDefinition)def).getDef()); 
		case AInheritedDefinition.kindPDefinition:
			return isCallableOperation(((AInheritedDefinition)def).getSuperdef()); 		
		case ARenamedDefinition.kindPDefinition:
			return isCallableOperation(((ARenamedDefinition)def).getDef());
		default:
			return false;
		}		
	}

	public static boolean isUsed(PDefinition u) {
		switch (u.kindPDefinition()) {
		case AExternalDefinition.kindPDefinition:
			return AExternalDefinitionAssistantTC.isUsed((AExternalDefinition) u);
//		case AImportedDefinition.kindPDefinition:
//			return AImportedDefinitionAssistant.isUsed((AImportedDefinition) u);
		case AInheritedDefinition.kindPDefinition:
			return AInheritedDefinitionAssistantTC
					.isUsed((AInheritedDefinition) u);
//		case ARenamedDefinition.kindPDefinition:
//			return ARenamedDefinitionAssistant.isUsed((ARenamedDefinition) u);
		default:
			return u.getUsed();
		}

	}

	public static void implicitDefinitions(PDefinition d, Environment env) {
		switch (d.kindPDefinition()) {
		case SClassDefinition.kindPDefinition:
			SClassDefinitionAssistantTC.implicitDefinitions((SClassDefinition) d,
					env);
			break;
		case AClassInvariantDefinition.kindPDefinition:
			break;
		case AEqualsDefinition.kindPDefinition:
			break;
		case AExplicitFunctionDefinition.kindPDefinition:
			AExplicitFunctionDefinitionAssistantTC.implicitDefinitions(
					(AExplicitFunctionDefinition) d, env);
			break;
		case AExplicitOperationDefinition.kindPDefinition:
			AExplicitOperationDefinitionAssistantTC.implicitDefinitions(
					(AExplicitOperationDefinition) d, env);
			break;
		case AImplicitFunctionDefinition.kindPDefinition:
			AImplicitFunctionDefinitionAssistantTC.implicitDefinitions(
					(AImplicitFunctionDefinition) d, env);
			break;
		case AImplicitOperationDefinition.kindPDefinition:
			AImplicitOperationDefinitionAssistantTC.implicitDefinitions(
					(AImplicitOperationDefinition) d, env);
			break;
		case AStateDefinition.kindPDefinition:
			AStateDefinitionAssistantTC.implicitDefinitions((AStateDefinition) d,
					env);
			break;
		case AThreadDefinition.kindPDefinition:
			AThreadDefinitionAssistantTC.implicitDefinitions(
					(AThreadDefinition) d, env);
			break;
		case ATypeDefinition.kindPDefinition:
			ATypeDefinitionAssistantTC.implicitDefinitions((ATypeDefinition) d,
					env);
			break;
		default:
			return;
		}

	}

	public static void typeResolve(PDefinition d,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) throws AnalysisException {
		switch (d.kindPDefinition()) {
		case SClassDefinition.kindPDefinition:
			SClassDefinitionAssistantTC.typeResolve((SClassDefinition) d,
					rootVisitor, question);
			break;
		case AExplicitFunctionDefinition.kindPDefinition:
			AExplicitFunctionDefinitionAssistantTC.typeResolve(
					(AExplicitFunctionDefinition) d, rootVisitor, question);
			break;
		case AExplicitOperationDefinition.kindPDefinition:
			AExplicitOperationDefinitionAssistantTC.typeResolve(
					(AExplicitOperationDefinition) d, rootVisitor, question);
			break;
		case AImplicitFunctionDefinition.kindPDefinition:
			AImplicitFunctionDefinitionAssistantTC.typeResolve(
					(AImplicitFunctionDefinition) d, rootVisitor, question);
			break;
		case AImplicitOperationDefinition.kindPDefinition:
			AImplicitOperationDefinitionAssistantTC.typeResolve(
					(AImplicitOperationDefinition) d, rootVisitor, question);
			break;
		case AInstanceVariableDefinition.kindPDefinition:
			AInstanceVariableDefinitionAssistantTC.typeResolve(
					(AInstanceVariableDefinition) d, rootVisitor, question);
			break;
		case ALocalDefinition.kindPDefinition:
			ALocalDefinitionAssistantTC.typeResolve((ALocalDefinition) d,
					rootVisitor, question);
			break;
		case ARenamedDefinition.kindPDefinition:
			ARenamedDefinitionAssistantTC.typeResolve((ARenamedDefinition) d,
					rootVisitor, question);
			break;
		case AStateDefinition.kindPDefinition:
			AStateDefinitionAssistantTC.typeResolve((AStateDefinition) d,
					rootVisitor, question);
			break;
		case ATypeDefinition.kindPDefinition:
			ATypeDefinitionAssistantTC.typeResolve((ATypeDefinition) d,
					rootVisitor, question);
			break;
		case AValueDefinition.kindPDefinition:
			AValueDefinitionAssistantTC.typeResolve((AValueDefinition) d,
					rootVisitor, question);
		default:
			return;

		}

	}

	public static PType getType(PDefinition def) {
		switch (def.kindPDefinition()) {
		case AAssignmentDefinition.kindPDefinition:
			return def.getType();
		case SClassDefinition.kindPDefinition:
			return SClassDefinitionAssistantTC.getType((SClassDefinition) def);
		case AClassInvariantDefinition.kindPDefinition:
			return AstFactory.newABooleanBasicType(def.getLocation());
		case AEqualsDefinition.kindPDefinition:
			return AEqualsDefinitionAssistantTC.getType((AEqualsDefinition) def);
		case AExplicitFunctionDefinition.kindPDefinition:
			return def.getType();
		case AExplicitOperationDefinition.kindPDefinition:
			return def.getType();
		case AExternalDefinition.kindPDefinition:
			return AExternalDefinitionAssistantTC
					.getType((AExternalDefinition) def);
		case AImplicitFunctionDefinition.kindPDefinition:
			return def.getType();
		case AImplicitOperationDefinition.kindPDefinition:
			return def.getType();
		case AImportedDefinition.kindPDefinition:
			return getType(((AImportedDefinition) def).getDef());
		case AInheritedDefinition.kindPDefinition:
			return AInheritedDefinitionAssistantTC
					.getType((AInheritedDefinition) def);
		case AInstanceVariableDefinition.kindPDefinition:
			return def.getType();
		case ALocalDefinition.kindPDefinition:
			return def.getType() == null ? AstFactory.newAUnknownType(def.getLocation()) : def.getType();
		case AMultiBindListDefinition.kindPDefinition:
			return AMultiBindListDefinitionAssistantTC
					.getType((AMultiBindListDefinition) def);
		case AMutexSyncDefinition.kindPDefinition:
			return AstFactory.newAUnknownType(def.getLocation());
		case ANamedTraceDefinition.kindPDefinition:
			return AstFactory.newAOperationType(def.getLocation(), new Vector<PType>(), AstFactory.newAVoidType(def.getLocation()));
		case APerSyncDefinition.kindPDefinition:
			return AstFactory.newABooleanBasicType(def.getLocation());
		case ARenamedDefinition.kindPDefinition:
			return getType(((ARenamedDefinition) def).getDef());
		case AStateDefinition.kindPDefinition:
			return ((AStateDefinition) def).getRecordType();
		case AThreadDefinition.kindPDefinition:
			return AstFactory.newAUnknownType(def.getLocation());
		case ATypeDefinition.kindPDefinition:
			return ((ATypeDefinition) def).getInvType();
		case AUntypedDefinition.kindPDefinition:
			return  AstFactory.newAUnknownType(def.getLocation());
		case AValueDefinition.kindPDefinition:
			return AValueDefinitionAssistantTC.getType((AValueDefinition) def);
		default:
			assert false : "should never go in this case";
			return null;
		}

	}

	public static boolean isUpdatable(PDefinition d) {
		switch (d.kindPDefinition()) {
		case AAssignmentDefinition.kindPDefinition:
		case AInstanceVariableDefinition.kindPDefinition:
		case AExternalDefinition.kindPDefinition:
			return true;
		case AImportedDefinition.kindPDefinition:
			return PDefinitionAssistantTC.isUpdatable(((AImportedDefinition) d)
					.getDef());
		case AInheritedDefinition.kindPDefinition:
			return PDefinitionAssistantTC
					.isUpdatable(((AInheritedDefinition) d).getSuperdef());
		case ALocalDefinition.kindPDefinition:
			return ((ALocalDefinition) d).getNameScope().matches(
					NameScope.STATE);
		case ARenamedDefinition.kindPDefinition:
			return PDefinitionAssistantTC.isUpdatable(((ARenamedDefinition) d)
					.getDef());
		default:
			return false;
		}
	}
	

	public static String kind(PDefinition d) {
		switch (d.kindPDefinition()) {
		case AAssignmentDefinition.kindPDefinition:
			return "assignable variable";
		case SClassDefinition.kindPDefinition:
			return "class";
		case AClassInvariantDefinition.kindPDefinition:
			return "invariant";
		case AEqualsDefinition.kindPDefinition:
			return "equals";
		case AExplicitFunctionDefinition.kindPDefinition:
			return "explicit function";
		case AExplicitOperationDefinition.kindPDefinition:
			return "explicit operation";
		case AExternalDefinition.kindPDefinition:
			return "external";
		case AImplicitFunctionDefinition.kindPDefinition:
			return "implicit function";
		case AImplicitOperationDefinition.kindPDefinition:
			return "implicit operation";
		case AImportedDefinition.kindPDefinition:
			return "import";
		case AInheritedDefinition.kindPDefinition:
			return kind(((AInheritedDefinition) d).getSuperdef());
		case AInstanceVariableDefinition.kindPDefinition:
			return "instance variable";
		case ALocalDefinition.kindPDefinition:
			return "local";
		case AMultiBindListDefinition.kindPDefinition:
			return "bind";
		case AMutexSyncDefinition.kindPDefinition:
			return "mutex predicate";
		case ANamedTraceDefinition.kindPDefinition:
			return "trace";
		case APerSyncDefinition.kindPDefinition:
			return "permission predicate";
		case ARenamedDefinition.kindPDefinition:
			return kind(((ARenamedDefinition) d).getDef());
		case AStateDefinition.kindPDefinition:
			return "state";
		case AThreadDefinition.kindPDefinition:
			return "thread";
		case ATypeDefinition.kindPDefinition:
			return "type";
		case AUntypedDefinition.kindPDefinition:
			return "untyped";
		case AValueDefinition.kindPDefinition:
			return "value";
		default:
			return null;
		}

	}

	public static boolean isFunction(PDefinition def) {
		switch (def.kindPDefinition()) {
		case AExplicitFunctionDefinition.kindPDefinition:
		case AImplicitFunctionDefinition.kindPDefinition:
			return true;
		case AImportedDefinition.kindPDefinition:
			return isFunction(((AImportedDefinition) def).getDef());
		case AInheritedDefinition.kindPDefinition:
			return isFunction(((AInheritedDefinition) def).getSuperdef());
		case ALocalDefinition.kindPDefinition:
			return ALocalDefinitionAssistantTC.isFunction((ALocalDefinition) def);
		case ARenamedDefinition.kindPDefinition:
			return isFunction(((ARenamedDefinition) def).getDef());
		default:
			return false;
		}
	}

	public static boolean isOperation(PDefinition def) {
		switch (def.kindPDefinition()) {
		case AExplicitOperationDefinition.kindPDefinition:
		case AImplicitOperationDefinition.kindPDefinition:
		case ANamedTraceDefinition.kindPDefinition:
		case AThreadDefinition.kindPDefinition:
			return true;
		case AImportedDefinition.kindPDefinition:
			return isOperation(((AImportedDefinition) def).getDef());
		case AInheritedDefinition.kindPDefinition:
			return isOperation(((AInheritedDefinition) def).getSuperdef());
		case ARenamedDefinition.kindPDefinition:
			return isOperation(((ARenamedDefinition) def).getDef());
		default:
			return false;
		}
	}
	
	public static LexNameList  getOldNames(PDefinition def)
	{
		switch (def.kindPDefinition()) {		
		case AEqualsDefinition.kindPDefinition:
			return AEqualsDefinitionAssistantTC.getOldNames((AEqualsDefinition)def);
		case AValueDefinition.kindPDefinition:
			return AValueDefinitionAssistantTC.getOldNames((AValueDefinition) def);
		default:
			return new LexNameList();
		}
		
		
	}

}
