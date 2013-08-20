package org.overture.typechecker.assistant.definition;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import org.overture.typechecker.TypeComparator;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;
import org.overture.typechecker.util.HelpLexNameToken;

public class PDefinitionAssistantTC extends PDefinitionAssistant {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public PDefinitionAssistantTC(ITypeCheckerAssistantFactory af)
	{
		super(af);
		this.af = af;
	}
	
	public boolean equals(PDefinition d, Object other)		// Used for sets of definitions.
	{
		if (d instanceof AEqualsDefinition) {
			return af.createAEqualsDefinitionAssistant().equals((AEqualsDefinition)d, other);
		} else if (d instanceof AMultiBindListDefinition) {
			return af.createAMultiBindListDefinitionAssistant().equals((AMultiBindListDefinition)d,other);
		} else if (d instanceof AMutexSyncDefinition) {
			return af.createAMutexSyncDefinitionAssistant().equals((AMutexSyncDefinition)d,other);
		} else if (d instanceof AThreadDefinition) {
			return af.createAThreadDefinitionAssistant().equals((AThreadDefinition)d,other);
		} else if (d instanceof AValueDefinition) {
			return af.createAValueDefinitionAssistant().equals((AValueDefinition)d,other);
		} else {
			return equalsBaseCase(d, other);
		}
	}
	
	private  boolean equalsBaseCase(PDefinition def, Object other)		// Used for sets of definitions.
	{
		if (other instanceof PDefinition)
		{
			PDefinition odef = (PDefinition)other;
			return def.getName() != null && odef.getName() != null && def.getName().equals(odef.getName());
		}
		return false;
	}
	
	public static boolean hasSupertype( SClassDefinition aClassDefDefinition,
			PType other) {

		if (PTypeAssistantTC.equals(
				af.createPDefinitionAssistant().getType(aClassDefDefinition), other)) {
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

	public static boolean isFunctionOrOperation( PDefinition possible) {
		return isFunction(possible) || isOperation(possible);
	}

	public static PDefinition findType( List<PDefinition> definitions,
			ILexNameToken name, String fromModule) {

		for (PDefinition d : definitions) {
			PDefinition def = findType(d, name, fromModule);

			if (def != null) {
				return def;
			}
		}

		return null;

	}

	public static PDefinition findType( PDefinition d, ILexNameToken sought,
			String fromModule) {
		if (d instanceof SClassDefinition) {
			return SClassDefinitionAssistantTC.findType((SClassDefinition) d,
					sought, fromModule);
		} else if (d instanceof AImportedDefinition) {
			return AImportedDefinitionAssistantTC.findType(
					(AImportedDefinition) d, sought, fromModule);
		} else if (d instanceof AInheritedDefinition) {
			return AInheritedDefinitionAssistantTC.findType(
					(AInheritedDefinition) d, sought, fromModule);
		} else if (d instanceof ARenamedDefinition) {
			return ARenamedDefinitionAssistantTC.findType((ARenamedDefinition) d,
					sought, fromModule);
		} else if (d instanceof AStateDefinition) {
			return AStateDefinitionAssistantTC.findType((AStateDefinition) d,
					sought, fromModule);
		} else if (d instanceof ATypeDefinition) {
			return ATypeDefinitionAssistantTC.findType((ATypeDefinition) d,
					sought, fromModule);
		} else {
			return null;
		}
	}

	public static PDefinition findName( PDefinition d, ILexNameToken sought,
			NameScope scope) {
		if (d instanceof SClassDefinition) {
			return SClassDefinitionAssistantTC.findName((SClassDefinition) d,
					sought, scope);
		} else if (d instanceof AEqualsDefinition) {
			return AEqualsDefinitionAssistantTC.findName((AEqualsDefinition) d,
					sought, scope);
		} else if (d instanceof AExplicitFunctionDefinition) {
			return AExplicitFunctionDefinitionAssistantTC.findName(
					(AExplicitFunctionDefinition) d, sought, scope);
		} else if (d instanceof AExplicitOperationDefinition) {
			return AExplicitOperationDefinitionAssistantTC.findName(
					(AExplicitOperationDefinition) d, sought, scope);
		} else if (d instanceof AExternalDefinition) {
			return AExternalDefinitionAssistantTC.findName(
					(AExternalDefinition) d, sought, scope);
		} else if (d instanceof AImplicitFunctionDefinition) {
			return AImplicitFunctionDefinitionAssistantTC.findName(
					(AImplicitFunctionDefinition) d, sought, scope);
		} else if (d instanceof AImplicitOperationDefinition) {
			return AImplicitOperationDefinitionAssistantTC.findName(
					(AImplicitOperationDefinition) d, sought, scope);
		} else if (d instanceof AImportedDefinition) {
			return AImportedDefinitionAssistantTC.findName(
					(AImportedDefinition) d, sought, scope);
		} else if (d instanceof AInheritedDefinition) {
			return AInheritedDefinitionAssistantTC.findName(
					(AInheritedDefinition) d, sought, scope);
		} else if (d instanceof AInstanceVariableDefinition) {
			return AInstanceVariableDefinitionAssistantTC.findName(
					(AInstanceVariableDefinition) d, sought, scope);
		} else if (d instanceof AMultiBindListDefinition) {
			return AMultiBindListDefinitionAssistantTC.findName(
					(AMultiBindListDefinition) d, sought, scope);
		} else if (d instanceof AMutexSyncDefinition) {
			return AMutexSyncDefinitionAssistantTC.findName(
					(AMutexSyncDefinition) d, sought, scope);
		} else if (d instanceof ANamedTraceDefinition) {
			return ANamedTraceDefinitionAssistantTC.findName(
					(ANamedTraceDefinition) d, sought, scope);
		} else if (d instanceof APerSyncDefinition) {
			return APerSyncDefinitionAssistantTC.findName((APerSyncDefinition) d,
					sought, scope);
		} else if (d instanceof ARenamedDefinition) {
			return ARenamedDefinitionAssistantTC.findName((ARenamedDefinition) d,
					sought, scope);
		} else if (d instanceof AStateDefinition) {
			return AStateDefinitionAssistantTC.findName((AStateDefinition) d,
					sought, scope);
		} else if (d instanceof AThreadDefinition) {
			return AThreadDefinitionAssistantTC.findName((AThreadDefinition) d,
					sought, scope);
		} else if (d instanceof ATypeDefinition) {
			return ATypeDefinitionAssistantTC.findName((ATypeDefinition) d,
					sought, scope);
		} else if (d instanceof AValueDefinition) {
			return AValueDefinitionAssistantTC.findName((AValueDefinition) d,
					sought, scope);
		} else {
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
		if (d instanceof AExternalDefinition) {
			AExternalDefinitionAssistantTC.markUsed((AExternalDefinition) d);
		} else if (d instanceof AImportedDefinition) {
			AImportedDefinitionAssistantTC.markUsed((AImportedDefinition) d);
		} else if (d instanceof AInheritedDefinition) {
			AInheritedDefinitionAssistantTC.markUsed((AInheritedDefinition) d);
		} else if (d instanceof ARenamedDefinition) {
			ARenamedDefinitionAssistantTC.markUsed((ARenamedDefinition) d);
			d.setUsed(true);
		} else {
			d.setUsed(true);
		}
	}

	public static void unusedCheck(PDefinition d) {
		if (d instanceof AEqualsDefinition) {
			AEqualsDefinitionAssistantTC.unusedCheck((AEqualsDefinition) d);
		} else if (d instanceof AMultiBindListDefinition) {
			AMultiBindListDefinitionAssistantTC
					.unusedCheck((AMultiBindListDefinition) d);
		} else if (d instanceof AStateDefinition) {
			AStateDefinitionAssistantTC.unusedCheck((AStateDefinition) d);
		} else if (d instanceof AValueDefinition) {
			AValueDefinitionAssistantTC.unusedCheck((AValueDefinition) d);
		} else {
			unusedCheckBaseCase(d);
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
		if (d instanceof AAssignmentDefinition) {
			return AAssignmentDefinitionAssistantTC
					.getDefinitions((AAssignmentDefinition) d);
		} else if (d instanceof SClassDefinition) {
			return SClassDefinitionAssistantTC
					.getDefinitions((SClassDefinition) d);
		} else if (d instanceof AClassInvariantDefinition) {
			return AClassInvariantDefinitionAssistantTC
					.getDefinitions((AClassInvariantDefinition) d);
		} else if (d instanceof AEqualsDefinition) {
			return AEqualsDefinitionAssistantTC
					.getDefinitions((AEqualsDefinition) d);
		} else if (d instanceof AExplicitFunctionDefinition) {
			return AExplicitFunctionDefinitionAssistantTC
					.getDefinitions((AExplicitFunctionDefinition) d);
		} else if (d instanceof AExplicitOperationDefinition) {
			return AExplicitOperationDefinitionAssistantTC
					.getDefinitions((AExplicitOperationDefinition) d);
		} else if (d instanceof AExternalDefinition) {
			return AExternalDefinitionAssistantTC
					.getDefinitions((AExternalDefinition) d);
		} else if (d instanceof AImplicitFunctionDefinition) {
			return AImplicitFunctionDefinitionAssistantTC
					.getDefinitions((AImplicitFunctionDefinition) d);
		} else if (d instanceof AImplicitOperationDefinition) {
			return AImplicitOperationDefinitionAssistantTC
					.getDefinitions((AImplicitOperationDefinition) d);
		} else if (d instanceof AImportedDefinition) {
			return AImportedDefinitionAssistantTC
					.getDefinitions((AImportedDefinition) d);
		} else if (d instanceof AInheritedDefinition) {
			return AInheritedDefinitionAssistantTC
					.getDefinitions((AInheritedDefinition) d);
		} else if (d instanceof AInstanceVariableDefinition) {
			return AInstanceVariableDefinitionAssistantTC
					.getDefinitions((AInstanceVariableDefinition) d);
		} else if (d instanceof ALocalDefinition) {
			return ALocalDefinitionAssistantTC
					.getDefinitions((ALocalDefinition) d);
		} else if (d instanceof AMultiBindListDefinition) {
			return AMultiBindListDefinitionAssistantTC
					.getDefinitions((AMultiBindListDefinition) d);
		} else if (d instanceof AMutexSyncDefinition) {
			return AMutexSyncDefinitionAssistantTC
					.getDefinitions((AMutexSyncDefinition) d);
		} else if (d instanceof ANamedTraceDefinition) {
			return ANamedTraceDefinitionAssistantTC
					.getDefinitions((ANamedTraceDefinition) d);
		} else if (d instanceof APerSyncDefinition) {
			return APerSyncDefinitionAssistantTC
					.getDefinitions((APerSyncDefinition) d);
		} else if (d instanceof ARenamedDefinition) {
			return ARenamedDefinitionAssistantTC
					.getDefinitions((ARenamedDefinition) d);
		} else if (d instanceof AStateDefinition) {
			return AStateDefinitionAssistantTC
					.getDefinitions((AStateDefinition) d);
		} else if (d instanceof AThreadDefinition) {
			return AThreadDefinitionAssistantTC
					.getDefinitions((AThreadDefinition) d);
		} else if (d instanceof ATypeDefinition) {
			return ATypeDefinitionAssistantTC.getDefinitions((ATypeDefinition) d);
		} else if (d instanceof AUntypedDefinition) {
			return AUntypedDefinitionAssistantTC
					.getDefinitions((AUntypedDefinition) d);
		} else if (d instanceof AValueDefinition) {
			return AValueDefinitionAssistantTC
					.getDefinitions((AValueDefinition) d);
		} else {
			assert false : "getDefinitions should never hit the default case";
			return null;
		}

	}

	public static PDefinition getSelfDefinition( PDefinition d) {
		if (d instanceof SClassDefinition) {
			return SClassDefinitionAssistantTC
					.getSelfDefinition((SClassDefinition) d);
		} else {
			return getSelfDefinition(d.getClassDefinition());
		}

	}

	public static LexNameList getVariableNames( PDefinition d) {
		if (d instanceof AAssignmentDefinition) {
			return AAssignmentDefinitionAssistantTC
					.getVariableNames((AAssignmentDefinition) d);
		} else if (d instanceof SClassDefinition) {
			return SClassDefinitionAssistantTC
					.getVariableNames((SClassDefinition) d);
		} else if (d instanceof AClassInvariantDefinition) {
			return AClassInvariantDefinitionAssistantTC
					.getVariableNames((AClassInvariantDefinition) d);
		} else if (d instanceof AEqualsDefinition) {
			return AEqualsDefinitionAssistantTC
					.getVariableNames((AEqualsDefinition) d);
		} else if (d instanceof AExplicitFunctionDefinition) {
			return AExplicitFunctionDefinitionAssistantTC
					.getVariableNames((AExplicitFunctionDefinition) d);
		} else if (d instanceof AExplicitOperationDefinition) {
			return AExplicitOperationDefinitionAssistantTC
					.getVariableNames((AExplicitOperationDefinition) d);
		} else if (d instanceof AExternalDefinition) {
			return AExternalDefinitionAssistantTC
					.getVariableNames((AExternalDefinition) d);
		} else if (d instanceof AImplicitFunctionDefinition) {
			return AImplicitFunctionDefinitionAssistantTC
					.getVariableNames((AImplicitFunctionDefinition) d);
		} else if (d instanceof AImplicitOperationDefinition) {
			return AImplicitOperationDefinitionAssistantTC
					.getVariableNames((AImplicitOperationDefinition) d);
		} else if (d instanceof AImportedDefinition) {
			return AImportedDefinitionAssistantTC
					.getVariableNames((AImportedDefinition) d);
		} else if (d instanceof AInheritedDefinition) {
			return AInheritedDefinitionAssistantTC
					.getVariableNames((AInheritedDefinition) d);
		} else if (d instanceof AInstanceVariableDefinition) {
			return AInstanceVariableDefinitionAssistantTC
					.getVariableNames((AInstanceVariableDefinition) d);
		} else if (d instanceof ALocalDefinition) {
			return ALocalDefinitionAssistantTC
					.getVariableNames((ALocalDefinition) d);
		} else if (d instanceof AMultiBindListDefinition) {
			return AMultiBindListDefinitionAssistantTC
					.getVariableNames((AMultiBindListDefinition) d);
		} else if (d instanceof AMutexSyncDefinition) {
			return AMutexSyncDefinitionAssistantTC
					.getVariableNames((AMutexSyncDefinition) d);
		} else if (d instanceof ANamedTraceDefinition) {
			return ANamedTraceDefinitionAssistantTC
					.getVariableNames((ANamedTraceDefinition) d);
		} else if (d instanceof APerSyncDefinition) {
			return APerSyncDefinitionAssistantTC
					.getVariableNames((APerSyncDefinition) d);
		} else if (d instanceof ARenamedDefinition) {
			return ARenamedDefinitionAssistantTC
					.getVariableNames((ARenamedDefinition) d);
		} else if (d instanceof AStateDefinition) {
			return AStateDefinitionAssistantTC
					.getVariableNames((AStateDefinition) d);
		} else if (d instanceof AThreadDefinition) {
			return AThreadDefinitionAssistantTC
					.getVariableNames((AThreadDefinition) d);
		} else if (d instanceof ATypeDefinition) {
			return ATypeDefinitionAssistantTC
					.getVariableNames((ATypeDefinition) d);
		} else if (d instanceof AUntypedDefinition) {
			return AUntypedDefinitionAssistantTC
					.getVariableNames((AUntypedDefinition) d);
		} else if (d instanceof AValueDefinition) {
			return AValueDefinitionAssistantTC
					.getVariableNames((AValueDefinition) d);
		} else {
			assert false : "default case should never happen in getVariableNames";
			return null;
		}

	}

	public static boolean isStatic(PDefinition fdef) {
		return PAccessSpecifierAssistantTC.isStatic(fdef.getAccess());
	}

	public static PDefinition deref(PDefinition d) {
		if (d instanceof AImportedDefinition) {
			if (d instanceof AImportedDefinition) {
				return deref(((AImportedDefinition) d).getDef());
			}
		} else if (d instanceof AInheritedDefinition) {
			if (d instanceof AInheritedDefinition) {
				return deref(((AInheritedDefinition) d).getSuperdef());
			}
		} else if (d instanceof ARenamedDefinition) {
			if (d instanceof ARenamedDefinition) {
				return deref(((ARenamedDefinition) d).getDef());
			}
		}
		return d;

	}

	public static boolean isCallableOperation(PDefinition d) {
		if (d instanceof AExplicitOperationDefinition) {
			return true;
		} else if (d instanceof AImplicitOperationDefinition) {
			return ((AImplicitOperationDefinition)d).getBody() != null;
		} else if (d instanceof AImportedDefinition) {
			return isCallableOperation(((AImportedDefinition)d).getDef());
		} else if (d instanceof AInheritedDefinition) {
			return isCallableOperation(((AInheritedDefinition)d).getSuperdef());
		} else if (d instanceof ARenamedDefinition) {
			return isCallableOperation(((ARenamedDefinition)d).getDef());
		} else {
			return false;
		}		
	}

	public static boolean isUsed(PDefinition d) {
		if (d instanceof AExternalDefinition) {
			return AExternalDefinitionAssistantTC.isUsed((AExternalDefinition) d);
		} else if (d instanceof AInheritedDefinition) {
			return AInheritedDefinitionAssistantTC
					.isUsed((AInheritedDefinition) d);
		} else {
			return d.getUsed();
		}

	}

	public static void implicitDefinitions( PDefinition d, Environment env) {
		if (d instanceof SClassDefinition) {
			SClassDefinitionAssistantTC.implicitDefinitions((SClassDefinition) d,
					env);
		} else if (d instanceof AClassInvariantDefinition) {
		} else if (d instanceof AEqualsDefinition) {
		} else if (d instanceof AExplicitFunctionDefinition) {
			AExplicitFunctionDefinitionAssistantTC.implicitDefinitions(
					(AExplicitFunctionDefinition) d, env);
		} else if (d instanceof AExplicitOperationDefinition) {
			AExplicitOperationDefinitionAssistantTC.implicitDefinitions(
					(AExplicitOperationDefinition) d, env);
		} else if (d instanceof AImplicitFunctionDefinition) {
			AImplicitFunctionDefinitionAssistantTC.implicitDefinitions(
					(AImplicitFunctionDefinition) d, env);
		} else if (d instanceof AImplicitOperationDefinition) {
			AImplicitOperationDefinitionAssistantTC.implicitDefinitions(
					(AImplicitOperationDefinition) d, env);
		} else if (d instanceof AStateDefinition) {
			AStateDefinitionAssistantTC.implicitDefinitions((AStateDefinition) d,
					env);
		} else if (d instanceof AThreadDefinition) {
			AThreadDefinitionAssistantTC.implicitDefinitions(
					(AThreadDefinition) d, env);
		} else if (d instanceof ATypeDefinition) {
			ATypeDefinitionAssistantTC.implicitDefinitions((ATypeDefinition) d,
					env);
		} else {
			return;
		}

	}

	public static void typeResolve(PDefinition d,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) throws AnalysisException {
		if (d instanceof SClassDefinition) {
			SClassDefinitionAssistantTC.typeResolve((SClassDefinition) d,
					rootVisitor, question);
		} else if (d instanceof AExplicitFunctionDefinition) {
			AExplicitFunctionDefinitionAssistantTC.typeResolve(
					(AExplicitFunctionDefinition) d, rootVisitor, question);
		} else if (d instanceof AExplicitOperationDefinition) {
			AExplicitOperationDefinitionAssistantTC.typeResolve(
					(AExplicitOperationDefinition) d, rootVisitor, question);
		} else if (d instanceof AImplicitFunctionDefinition) {
			AImplicitFunctionDefinitionAssistantTC.typeResolve(
					(AImplicitFunctionDefinition) d, rootVisitor, question);
		} else if (d instanceof AImplicitOperationDefinition) {
			AImplicitOperationDefinitionAssistantTC.typeResolve(
					(AImplicitOperationDefinition) d, rootVisitor, question);
		} else if (d instanceof AInstanceVariableDefinition) {
			AInstanceVariableDefinitionAssistantTC.typeResolve(
					(AInstanceVariableDefinition) d, rootVisitor, question);
		} else if (d instanceof ALocalDefinition) {
			ALocalDefinitionAssistantTC.typeResolve((ALocalDefinition) d,
					rootVisitor, question);
		} else if (d instanceof ARenamedDefinition) {
			ARenamedDefinitionAssistantTC.typeResolve((ARenamedDefinition) d,
					rootVisitor, question);
		} else if (d instanceof AStateDefinition) {
			AStateDefinitionAssistantTC.typeResolve((AStateDefinition) d,
					rootVisitor, question);
		} else if (d instanceof ATypeDefinition) {
			ATypeDefinitionAssistantTC.typeResolve((ATypeDefinition) d,
					rootVisitor, question);
		} else if (d instanceof AValueDefinition) {
			AValueDefinitionAssistantTC.typeResolve((AValueDefinition) d,
					rootVisitor, question);
		} else {
			return;
		}

	}

	public  PType getType( PDefinition d) {
		if (d instanceof AAssignmentDefinition) {
			return d.getType();
		} else if (d instanceof SClassDefinition) {
			return SClassDefinitionAssistantTC.getType((SClassDefinition) d);
		} else if (d instanceof AClassInvariantDefinition) {
			return AstFactory.newABooleanBasicType(d.getLocation());
		} else if (d instanceof AEqualsDefinition) {
			return AEqualsDefinitionAssistantTC.getType((AEqualsDefinition) d);
		} else if (d instanceof AExplicitFunctionDefinition) {
			return d.getType();
		} else if (d instanceof AExplicitOperationDefinition) {
			return d.getType();
		} else if (d instanceof AExternalDefinition) {
			return AExternalDefinitionAssistantTC
					.getType((AExternalDefinition) d);
		} else if (d instanceof AImplicitFunctionDefinition) {
			return d.getType();
		} else if (d instanceof AImplicitOperationDefinition) {
			return d.getType();
		} else if (d instanceof AImportedDefinition) {
			return getType(((AImportedDefinition) d).getDef());
		} else if (d instanceof AInheritedDefinition) {
			return AInheritedDefinitionAssistantTC
					.getType((AInheritedDefinition) d);
		} else if (d instanceof AInstanceVariableDefinition) {
			return d.getType();
		} else if (d instanceof ALocalDefinition) {
			return d.getType() == null ? AstFactory.newAUnknownType(d.getLocation()) : d.getType();
		} else if (d instanceof AMultiBindListDefinition) {
			return AMultiBindListDefinitionAssistantTC
					.getType((AMultiBindListDefinition) d);
		} else if (d instanceof AMutexSyncDefinition) {
			return AstFactory.newAUnknownType(d.getLocation());
		} else if (d instanceof ANamedTraceDefinition) {
			return AstFactory.newAOperationType(d.getLocation(), new Vector<PType>(), AstFactory.newAVoidType(d.getLocation()));
		} else if (d instanceof APerSyncDefinition) {
			return AstFactory.newABooleanBasicType(d.getLocation());
		} else if (d instanceof ARenamedDefinition) {
			return getType(((ARenamedDefinition) d).getDef());
		} else if (d instanceof AStateDefinition) {
			return ((AStateDefinition) d).getRecordType();
		} else if (d instanceof AThreadDefinition) {
			return AstFactory.newAUnknownType(d.getLocation());
		} else if (d instanceof ATypeDefinition) {
			return ((ATypeDefinition) d).getInvType();
		} else if (d instanceof AUntypedDefinition) {
			return  AstFactory.newAUnknownType(d.getLocation());
		} else if (d instanceof AValueDefinition) {
			return AValueDefinitionAssistantTC.getType((AValueDefinition) d);
		} else {
			assert false : "should never go in this case";
			return null;
		}

	}

	public static boolean isUpdatable(PDefinition d) {
		if (d instanceof AAssignmentDefinition
				|| d instanceof AInstanceVariableDefinition
				|| d instanceof AExternalDefinition) {
			return true;
		} else if (d instanceof AImportedDefinition) {
			return PDefinitionAssistantTC.isUpdatable(((AImportedDefinition) d)
					.getDef());
		} else if (d instanceof AInheritedDefinition) {
			return PDefinitionAssistantTC
					.isUpdatable(((AInheritedDefinition) d).getSuperdef());
		} else if (d instanceof ALocalDefinition) {
			return ((ALocalDefinition) d).getNameScope().matches(
					NameScope.STATE) || PTypeAssistantTC.isClass(af.createPDefinitionAssistant().getType(d));
		} else if (d instanceof ARenamedDefinition) {
			return PDefinitionAssistantTC.isUpdatable(((ARenamedDefinition) d)
					.getDef());
		} else {
			return false;
		}
	}
	

	public static String kind(PDefinition d) {
		if (d instanceof AAssignmentDefinition) {
			return "assignable variable";
		} else if (d instanceof SClassDefinition) {
			return "class";
		} else if (d instanceof AClassInvariantDefinition) {
			return "invariant";
		} else if (d instanceof AEqualsDefinition) {
			return "equals";
		} else if (d instanceof AExplicitFunctionDefinition) {
			return "explicit function";
		} else if (d instanceof AExplicitOperationDefinition) {
			return "explicit operation";
		} else if (d instanceof AExternalDefinition) {
			return "external";
		} else if (d instanceof AImplicitFunctionDefinition) {
			return "implicit function";
		} else if (d instanceof AImplicitOperationDefinition) {
			return "implicit operation";
		} else if (d instanceof AImportedDefinition) {
			return "import";
		} else if (d instanceof AInheritedDefinition) {
			return kind(((AInheritedDefinition) d).getSuperdef());
		} else if (d instanceof AInstanceVariableDefinition) {
			return "instance variable";
		} else if (d instanceof ALocalDefinition) {
			return "local";
		} else if (d instanceof AMultiBindListDefinition) {
			return "bind";
		} else if (d instanceof AMutexSyncDefinition) {
			return "mutex predicate";
		} else if (d instanceof ANamedTraceDefinition) {
			return "trace";
		} else if (d instanceof APerSyncDefinition) {
			return "permission predicate";
		} else if (d instanceof ARenamedDefinition) {
			return kind(((ARenamedDefinition) d).getDef());
		} else if (d instanceof AStateDefinition) {
			return "state";
		} else if (d instanceof AThreadDefinition) {
			return "thread";
		} else if (d instanceof ATypeDefinition) {
			return "type";
		} else if (d instanceof AUntypedDefinition) {
			return "untyped";
		} else if (d instanceof AValueDefinition) {
			return "value";
		} else {
			return null;
		}

	}

	public static boolean isFunction( PDefinition d) {
		if (d instanceof AExplicitFunctionDefinition
				|| d instanceof AImplicitFunctionDefinition) {
			return true;
		} else if (d instanceof AImportedDefinition) {
			return isFunction(((AImportedDefinition) d).getDef());
		} else if (d instanceof AInheritedDefinition) {
			return isFunction(((AInheritedDefinition) d).getSuperdef());
		} else if (d instanceof ALocalDefinition) {
			return ALocalDefinitionAssistantTC.isFunction((ALocalDefinition) d);
		} else if (d instanceof ARenamedDefinition) {
			return isFunction(((ARenamedDefinition) d).getDef());
		} else {
			return false;
		}
	}

	public static boolean isOperation(PDefinition d) {
		if (d instanceof AExplicitOperationDefinition
				|| d instanceof AImplicitOperationDefinition
				|| d instanceof ANamedTraceDefinition
				|| d instanceof AThreadDefinition) {
			return true;
		} else if (d instanceof AImportedDefinition) {
			return isOperation(((AImportedDefinition) d).getDef());
		} else if (d instanceof AInheritedDefinition) {
			return isOperation(((AInheritedDefinition) d).getSuperdef());
		} else if (d instanceof ARenamedDefinition) {
			return isOperation(((ARenamedDefinition) d).getDef());
		} else {
			return false;
		}
	}
	
	public static LexNameList  getOldNames(PDefinition d)
	{
		if (d instanceof AEqualsDefinition) {
			return AEqualsDefinitionAssistantTC.getOldNames((AEqualsDefinition)d);
		} else if (d instanceof AValueDefinition) {
			return AValueDefinitionAssistantTC.getOldNames((AValueDefinition) d);
		} else {
			return new LexNameList();
		}
		
		
	}


	/**
	 * Check a DefinitionList for incompatible duplicate pattern definitions.
	 */
	public static List<PDefinition> checkDuplicatePatterns(PDefinition d, List<PDefinition> defs)
	{
		Set<PDefinition> noDuplicates = new HashSet<PDefinition>();
		
		for (PDefinition d1: defs)
		{
			for (PDefinition d2: defs)
			{
				if (d1 != d2 && d1.getName() != null && d2.getName() != null && d1.getName().equals(d2.getName()))
				{
					if (!TypeComparator.compatible(d1.getType(), d2.getType()))
					{
						TypeCheckerErrors.report(3322, "Duplicate patterns bind to different types", d.getLocation(), d);
						TypeCheckerErrors.detail2(d1.getName().getName(), d1.getType(), d2.getName().getName(), d2.getType());
					}
				}
			}
			
			noDuplicates.add(d1);
		}

		return new Vector<PDefinition>(noDuplicates);
	}
	
}
