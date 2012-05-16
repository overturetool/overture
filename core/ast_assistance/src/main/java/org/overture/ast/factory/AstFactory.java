package org.overture.ast.factory;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

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
import org.overture.ast.definitions.ASystemClassDefinition;
import org.overture.ast.definitions.AThreadDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.AUntypedDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.definitions.assistants.AUnionTypeAssistant;
import org.overture.ast.definitions.assistants.PAccessSpecifierAssistant;
import org.overture.ast.definitions.assistants.PDefinitionAssistant;
import org.overture.ast.definitions.traces.AApplyExpressionTraceCoreDefinition;
import org.overture.ast.definitions.traces.ABracketedExpressionTraceCoreDefinition;
import org.overture.ast.definitions.traces.AConcurrentExpressionTraceCoreDefinition;
import org.overture.ast.definitions.traces.ALetBeStBindingTraceDefinition;
import org.overture.ast.definitions.traces.ALetDefBindingTraceDefinition;
import org.overture.ast.definitions.traces.ARepeatTraceDefinition;
import org.overture.ast.definitions.traces.ATraceDefinitionTerm;
import org.overture.ast.definitions.traces.PTraceCoreDefinition;
import org.overture.ast.definitions.traces.PTraceDefinition;
import org.overture.ast.expressions.*;
import org.overture.ast.modules.AAllExport;
import org.overture.ast.modules.AAllImport;
import org.overture.ast.modules.AFromModuleImports;
import org.overture.ast.modules.AFunctionExport;
import org.overture.ast.modules.AFunctionValueImport;
import org.overture.ast.modules.AModuleExports;
import org.overture.ast.modules.AModuleImports;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.modules.AOperationExport;
import org.overture.ast.modules.AOperationValueImport;
import org.overture.ast.modules.ATypeExport;
import org.overture.ast.modules.ATypeImport;
import org.overture.ast.modules.AValueExport;
import org.overture.ast.modules.AValueValueImport;
import org.overture.ast.modules.PExport;
import org.overture.ast.modules.PImport;
import org.overture.ast.patterns.ABooleanPattern;
import org.overture.ast.patterns.ACharacterPattern;
import org.overture.ast.patterns.AConcatenationPattern;
import org.overture.ast.patterns.ADefPatternBind;
import org.overture.ast.patterns.AExpressionPattern;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.AIgnorePattern;
import org.overture.ast.patterns.AIntegerPattern;
import org.overture.ast.patterns.ANilPattern;
import org.overture.ast.patterns.APatternListTypePair;
import org.overture.ast.patterns.APatternTypePair;
import org.overture.ast.patterns.AQuotePattern;
import org.overture.ast.patterns.ARealPattern;
import org.overture.ast.patterns.ARecordPattern;
import org.overture.ast.patterns.ASeqPattern;
import org.overture.ast.patterns.ASetBind;
import org.overture.ast.patterns.ASetMultipleBind;
import org.overture.ast.patterns.ASetPattern;
import org.overture.ast.patterns.AStringPattern;
import org.overture.ast.patterns.ATuplePattern;
import org.overture.ast.patterns.ATypeBind;
import org.overture.ast.patterns.ATypeMultipleBind;
import org.overture.ast.patterns.AUnionPattern;
import org.overture.ast.patterns.PBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.patterns.assistants.PPatternAssistant;
import org.overture.ast.patterns.assistants.PTypeList;
import org.overture.ast.statements.AAlwaysStm;
import org.overture.ast.statements.AApplyObjectDesignator;
import org.overture.ast.statements.AAssignmentStm;
import org.overture.ast.statements.AAtomicStm;
import org.overture.ast.statements.ABlockSimpleBlockStm;
import org.overture.ast.statements.ACallObjectStm;
import org.overture.ast.statements.ACallStm;
import org.overture.ast.statements.ACaseAlternativeStm;
import org.overture.ast.statements.ACasesStm;
import org.overture.ast.statements.AClassInvariantStm;
import org.overture.ast.statements.ACyclesStm;
import org.overture.ast.statements.ADefLetDefStm;
import org.overture.ast.statements.ADurationStm;
import org.overture.ast.statements.AElseIfStm;
import org.overture.ast.statements.AErrorCase;
import org.overture.ast.statements.AErrorStm;
import org.overture.ast.statements.AExitStm;
import org.overture.ast.statements.AExternalClause;
import org.overture.ast.statements.AFieldObjectDesignator;
import org.overture.ast.statements.AFieldStateDesignator;
import org.overture.ast.statements.AForAllStm;
import org.overture.ast.statements.AForIndexStm;
import org.overture.ast.statements.AForPatternBindStm;
import org.overture.ast.statements.AIdentifierObjectDesignator;
import org.overture.ast.statements.AIdentifierStateDesignator;
import org.overture.ast.statements.AIfStm;
import org.overture.ast.statements.ALetBeStStm;
import org.overture.ast.statements.AMapSeqStateDesignator;
import org.overture.ast.statements.ANewObjectDesignator;
import org.overture.ast.statements.ANonDeterministicSimpleBlockStm;
import org.overture.ast.statements.ANotYetSpecifiedStm;
import org.overture.ast.statements.APeriodicStm;
import org.overture.ast.statements.AReturnStm;
import org.overture.ast.statements.ASelfObjectDesignator;
import org.overture.ast.statements.ASkipStm;
import org.overture.ast.statements.ASpecificationStm;
import org.overture.ast.statements.AStartStm;
import org.overture.ast.statements.ASubclassResponsibilityStm;
import org.overture.ast.statements.ATixeStm;
import org.overture.ast.statements.ATixeStmtAlternative;
import org.overture.ast.statements.ATrapStm;
import org.overture.ast.statements.AWhileStm;
import org.overture.ast.statements.PObjectDesignator;
import org.overture.ast.statements.PStateDesignator;
import org.overture.ast.statements.PStm;
import org.overture.ast.types.AAccessSpecifierAccessSpecifier;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.ABracketType;
import org.overture.ast.types.ACharBasicType;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AInMapMapType;
import org.overture.ast.types.AIntNumericBasicType;
import org.overture.ast.types.AMapMapType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.ANatNumericBasicType;
import org.overture.ast.types.ANatOneNumericBasicType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AParameterType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.AQuoteType;
import org.overture.ast.types.ARationalNumericBasicType;
import org.overture.ast.types.ARealNumericBasicType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.ASeq1SeqType;
import org.overture.ast.types.ASeqSeqType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.ATokenBasicType;
import org.overture.ast.types.AUndefinedType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.AUnresolvedType;
import org.overture.ast.types.AVoidReturnType;
import org.overture.ast.types.AVoidType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SBasicType;
import org.overture.ast.types.SInvariantType;
import org.overturetool.util.ClonableFile;
import org.overturetool.util.ClonableString;
import org.overturetool.vdmj.lex.LexBooleanToken;
import org.overturetool.vdmj.lex.LexCharacterToken;
import org.overturetool.vdmj.lex.LexIdentifierToken;
import org.overturetool.vdmj.lex.LexIntegerToken;
import org.overturetool.vdmj.lex.LexKeywordToken;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.lex.LexQuoteToken;
import org.overturetool.vdmj.lex.LexRealToken;
import org.overturetool.vdmj.lex.LexStringToken;
import org.overturetool.vdmj.lex.LexToken;
import org.overturetool.vdmj.lex.VDMToken;
import org.overturetool.vdmj.messages.InternalException;
import org.overturetool.vdmj.typechecker.Access;
import org.overturetool.vdmj.typechecker.ClassDefinitionSettings;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.typechecker.Pass;
import org.overturetool.vdmj.util.Utils;


public class AstFactory {

	public static ADefPatternBind newADefPatternBind(LexLocation location,
			Object patternOrBind) {
		
		ADefPatternBind result = new ADefPatternBind();
		result.setLocation(location);
		
		if (patternOrBind instanceof PPattern)
		{
			result.setPattern((PPattern)patternOrBind);
			result.setBind(null);
		}
		else if (patternOrBind instanceof PBind)
		{
			result.setPattern(null);
			result.setBind((PBind) patternOrBind);
		}
		else
		{
			throw new InternalException(
				3, "PatternBind passed " + patternOrBind.getClass().getName());
		}
		
		return result;
	}

	public static ASetBind newASetBind(PPattern pattern, PExp readExpression) {
		ASetBind result = new ASetBind();
		
		result.setLocation(pattern.getLocation());
		result.setPattern(pattern);
		result.setSet(readExpression);
		
		return result;
	}

	public static ATypeBind newATypeBind(PPattern pattern, PType readType) {
		ATypeBind result = new ATypeBind();
		
		result.setLocation(pattern.getLocation());
		result.setPattern(pattern);
		result.setType(readType);
		
		return result;
	}

	public static ASetMultipleBind newASetMultipleBind(List<PPattern> plist,
			PExp readExpression) {
		ASetMultipleBind result = new ASetMultipleBind();
		
		result.setLocation(plist.get(0).getLocation());
		result.setPlist(plist);
		result.setSet(readExpression);
				
		return result;
	}

	public static ATypeMultipleBind newATypeMultipleBind(List<PPattern> plist,
			PType readType) {
		ATypeMultipleBind result = new ATypeMultipleBind();
		
		result.setLocation(plist.get(0).getLocation());
		result.setPlist(plist);
		result.setType(readType);
		
		return result;
	}

	public static AClassClassDefinition newAClassClassDefinition(
			LexNameToken className, LexNameList superclasses,
			List<PDefinition> members) {
		 
		AClassClassDefinition result = new AClassClassDefinition();
		initDefinition(result, Pass.DEFS, className.location, className, NameScope.CLASSNAME);

		result.setAccess(PAccessSpecifierAssistant.getPublic());
		result.setUsed(true);
		result.setSupernames(superclasses);
		result.setSuperDefs(new ArrayList<SClassDefinition>());
		result.setSupertypes(new ArrayList<PType>());
		result.setSuperInheritedDefinitions(new ArrayList<PDefinition>());
		result.setLocalInheritedDefinitions(new ArrayList<PDefinition>());
		result.setAllInheritedDefinitions(new ArrayList<PDefinition>());

		//this.delegate = new Delegate(name.name, definitions);
		result.setDefinitions(members);
		
		// Classes are all effectively public types
		PDefinitionAssistant.setClassDefinition(result.getDefinitions(),result);
		
		//others
		result.setSettingHierarchy(ClassDefinitionSettings.UNSET);
		
		return result;
	}

	public static ASystemClassDefinition newASystemClassDefinition(
			LexNameToken className, List<PDefinition> members) {
		ASystemClassDefinition result = new ASystemClassDefinition();
		initDefinition(result, Pass.DEFS, className.location, className, NameScope.CLASSNAME);
		
		result.setAccess(PAccessSpecifierAssistant.getPublic());
		result.setUsed(true);
		result.setSuperDefs(new ArrayList<SClassDefinition>());
		result.setSupertypes(new ArrayList<PType>());
		result.setSuperInheritedDefinitions(new ArrayList<PDefinition>());
		result.setLocalInheritedDefinitions(new ArrayList<PDefinition>());
		result.setAllInheritedDefinitions(new ArrayList<PDefinition>());

		//this.delegate = new Delegate(name.name, definitions);
		result.setDefinitions(members);
		
		// Classes are all effectively public types
		PDefinitionAssistant.setClassDefinition(result,result);

		//others
		result.setSettingHierarchy(ClassDefinitionSettings.UNSET);
		
		return result;
	}

	public static ANamedInvariantType newANamedInvariantType(
			LexNameToken typeName, PType type) {

		ANamedInvariantType result = new ANamedInvariantType();
		
		result.setLocation(typeName.location);
		result.setName(typeName);
		result.setType(type);
		
		return result;
	}

	public static ARecordInvariantType newARecordInvariantType(
			LexNameToken name, List<AFieldField> fields) {
		
		ARecordInvariantType result = new ARecordInvariantType();
		
		result.setLocation(name.location);
		result.setName(name);
		result.setFields(fields);
		
		return result;
	}

	public static ATypeDefinition newATypeDefinition(LexNameToken name,
			SInvariantType type, PPattern invPattern, PExp invExpression) {
		
		ATypeDefinition result = new ATypeDefinition();
		initDefinition(result, Pass.TYPES, name.location, name, NameScope.TYPENAME);
		
		result.setType(type);
		result.setInvPattern(invPattern);
		result.setInvExpression(invExpression);
		
		return result;
		
	}

	public static AExplicitFunctionDefinition newAExplicitFunctionDefinition(LexNameToken name,
			NameScope scope, List<LexNameToken> typeParams, AFunctionType type,
			List<List<PPattern>> parameters, PExp body, PExp precondition,
			PExp postcondition, boolean typeInvariant, LexNameToken measure) {
		
		AExplicitFunctionDefinition result = new AExplicitFunctionDefinition();
		
		//Definition initialization
		initDefinition(result, Pass.DEFS, name.location, name, scope);

		
		//AExplicitFunctionDefinition initialization
		result.setTypeParams(typeParams);
		result.setType(type);
		result.setParamPatternList(parameters);
		result.setPrecondition(precondition);
		result.setPostcondition(postcondition);
		result.setBody(body);
		result.setIsTypeInvariant(typeInvariant);
		result.setMeasure(measure);
		result.setIsCurried(parameters.size() > 1);
		
		List<PDefinition> defsList = new LinkedList<PDefinition>();
		defsList.add(result);
		type.getDefinitions().add(result);
		
		return result;
	}

	public static AImplicitFunctionDefinition newAImplicitFunctionDefinition(
			LexNameToken name, NameScope scope,
			List<LexNameToken> typeParams,
			List<APatternListTypePair> parameterPatterns,
			APatternTypePair resultPattern, PExp body, PExp precondition,
			PExp postcondition, LexNameToken measure) {
		
		AImplicitFunctionDefinition result = new AImplicitFunctionDefinition();

		
		// Definition initialization
		initDefinition(result, Pass.DEFS, name.location, name, scope);
		
		//AImplicitFunctionDefinition initialization
		result.setTypeParams(typeParams);
		result.setParamPatterns(parameterPatterns);
		result.setResult(resultPattern);
		result.setBody(body);
		result.setPrecondition(precondition);
		result.setPostcondition(postcondition);
		result.setMeasure(measure);
		
		List<PType> ptypes = new LinkedList<PType>();

		for (APatternListTypePair ptp : parameterPatterns)
		{			
			ptypes.addAll(getTypeList(ptp));
		}
		
		// NB: implicit functions are always +> total, apparently
		AFunctionType type = AstFactory.newAFunctionType(result.getLocation(), false, ptypes, resultPattern.getType());// AFunctionType(funcName.location, false, null, false, ptypes, (PType) resultPattern.getType());
		
		List<PDefinition> defs = new Vector<PDefinition>();
		defs.add(result);
		type.setDefinitions(defs);
		
		return result;
	}
	
	public static AFunctionType newAFunctionType(LexLocation location,
			boolean partial, List<PType> parameters, PType resultType) {
		AFunctionType result = new AFunctionType();
		
		result.setLocation(location);
		result.setParameters(parameters);
		result.setResult(resultType);
		result.setPartial(partial);
		
		return result;
	}

	private static List<PType> getTypeList(APatternListTypePair node)
	{
		List<PType> list = new Vector<PType>();

		for (int i = 0; i < node.getPatterns().size(); i++)
		{
			PType type = (PType) node.getType();// .clone();//Use clone since we don't want to make a switch for all
												// types.
			// type.parent(null);//new new type not in the tree yet.
			list.add(type);
		}

		return list;
	}

	public static AValueDefinition newAValueDefinition(PPattern p,
			NameScope scope, PType type, PExp readExpression) {
		
		AValueDefinition result = new AValueDefinition();
		
		// Definition initialization
		initDefinition(result, Pass.VALUES, p.getLocation(), null, scope);
		
		List<PDefinition> defs = new Vector<PDefinition>();

		for (LexNameToken var : PPatternAssistant.getVariableNames(p))
		{
			defs.add(AstFactory.newAUntypedDefinition(result.getLocation(), var, scope));
		}
		
		result.setDefs(defs);
		
		return result;
	}

	public static PDefinition newAUntypedDefinition(LexLocation location,
			LexNameToken name, NameScope scope) {

		AUntypedDefinition result = new AUntypedDefinition();
		// Definition initialization
		initDefinition(result, Pass.DEFS, location, name, scope);
		
		return result;
	}

	public static AStateDefinition newAStateDefinition(LexNameToken name,
			List<AFieldField> fields, PPattern invPattern,
			PExp invExpression, PPattern initPattern, PExp initExpression) {
		
		AStateDefinition result = new AStateDefinition();
		// Definition initialization
		initDefinition(result, Pass.TYPES, name.location, name, NameScope.STATE);
		
		//AStateDefinition init
		result.setFields(fields);
		result.setInvPattern(invPattern);
		result.setInvExpression(invExpression);
		result.setInitPattern(initPattern);
		result.setInitExpression(initExpression);
		
		List<PDefinition> stateDefs = new Vector<PDefinition>();
		
		result.setStateDefs(stateDefs);
		
		for (AFieldField f : fields)
		{
			stateDefs.add(AstFactory.newALocalDefinition(f.getTagname().location, f.getTagname(), NameScope.STATE, f.getType()));
			ALocalDefinition ld = AstFactory.newALocalDefinition(f.getTagname().location,
					f.getTagname().getOldName(), NameScope.OLDSTATE, f.getType()); 

			ld.setUsed(true);  // Else we moan about unused ~x names
			stateDefs.add(ld);
		}
		
		result.setRecordType(AstFactory.newARecordInvariantType(name, fields));
		
		ALocalDefinition recordDefinition = null;
		
		recordDefinition = AstFactory.newALocalDefinition(result.getLocation(), name, NameScope.STATE, result.getRecordType());
		recordDefinition.setUsed(true);  // Can't be exported anyway
		stateDefs.add(recordDefinition);

		recordDefinition = AstFactory.newALocalDefinition(result.getLocation(), name.getOldName(), NameScope.OLDSTATE, result.getRecordType());
		recordDefinition.setUsed(true); // Can't be exported anyway
		stateDefs.add(recordDefinition);
		
		return result;
	}

	public static ALocalDefinition newALocalDefinition(LexLocation location,
			LexNameToken name, NameScope state, PType type) {
		
		ALocalDefinition result = new ALocalDefinition();
		// Definition initialization
		initDefinition(result, Pass.DEFS, name.location, name, NameScope.STATE);
		
		result.setType(type);
		
		return result;
	}

	public static AExplicitOperationDefinition newAExplicitOperationDefinition(
			LexNameToken name, AOperationType type,
			List<PPattern> parameters, PExp precondition, PExp postcondition,
			PStm body) {
		
		AExplicitOperationDefinition result = new AExplicitOperationDefinition();
		// Definition initialization
		initDefinition(result, Pass.DEFS, name.location, name, NameScope.GLOBAL);
		
		result.setType(type);
		result.setParameterPatterns(parameters);
		result.setPrecondition(precondition);
		result.setPostcondition(postcondition);
		result.setBody(body);
		
		return result;
	}

	public static AImplicitOperationDefinition newAImplicitOperationDefinition(
			LexNameToken name,
			List<APatternListTypePair> parameterPatterns,
			APatternTypePair resultPattern, PStm body, ASpecificationStm spec) {
		
		AImplicitOperationDefinition result = new AImplicitOperationDefinition();
		// Definition initialization
		initDefinition(result, Pass.DEFS, name.location, name, NameScope.GLOBAL);
		
		result.setParameterPatterns(parameterPatterns);
		result.setResult(resultPattern);
		result.setBody(body);
		result.setExternals(spec.getExternals());
		result.setPrecondition(spec.getPrecondition());
		result.setPostcondition(spec.getPostcondition());
		result.setErrors(spec.getErrors());
		
		List<PType> ptypes = new Vector<PType>();
		
		for (APatternListTypePair ptp : parameterPatterns)
		{
			ptypes.addAll(getTypeList(ptp));
		}
		AOperationType operationType = AstFactory.newAOperationType(result.getLocation(), ptypes,
				(result == null ? AstFactory.newAVoidType(name.location) : result.getResult().getType())); 
		result.setType(operationType);
		
		return result;
	}

	public static AOperationType newAOperationType(LexLocation location,
			List<PType> parameters, PType resultType) {
		AOperationType result = new AOperationType();
		
		result.setLocation(location);
		result.setParameters(parameters);
		result.setResult(resultType);
		
		return result;
	}

	public static AVoidType newAVoidType(LexLocation location) {
		AVoidType result = new AVoidType();
		result.setLocation(location);
		return result;
	}

	public static ASpecificationStm newASpecificationStm(
			LexLocation location, List<AExternalClause> externals,
			PExp precondition, PExp postcondition, List<AErrorCase> errors) {
		
		ASpecificationStm result = new ASpecificationStm();
		initStatement(result, location);

		result.setExternals(externals);
		result.setPrecondition(precondition);
		result.setPostcondition(postcondition);
		result.setErrors(errors);
		
		return result;
	}

	public static AExternalClause newAExternalClause(LexToken mode,
			LexNameList names, PType type) {
		
		AExternalClause result = new AExternalClause();
		result.setMode(mode);
		result.setIdentifiers(names);
		result.setType((type == null) ? AstFactory.newAUnknownType(names.get(0).location) : type);
		
		return result;
	}

	public static PType newAUnknownType(LexLocation location) {
		AUnknownType result = new AUnknownType();
		result.setLocation(location);
		return result;
	}

	private static AEqualsDefinition newAEqualsDefinition(LexLocation location) {
		AEqualsDefinition result = new AEqualsDefinition();
		// Definition initialization
		initDefinition(result, Pass.DEFS, location, null, NameScope.LOCAL);
		return result;
	}
	
	public static AEqualsDefinition newAEqualsDefinition(LexLocation location,
			PPattern pattern, PExp test) {
		AEqualsDefinition result = AstFactory.newAEqualsDefinition(location);
		
		result.setPattern(pattern);
		result.setTypebind(null);
		result.setSetbind(null);
		result.setTest(test);
		
		return result;
	}

	public static AEqualsDefinition newAEqualsDefinition(LexLocation location,
			ATypeBind typebind, PExp test) {
		AEqualsDefinition result = AstFactory.newAEqualsDefinition(location);
				
		result.setPattern(null);
		result.setTypebind(typebind);
		result.setSetbind(null);
		result.setTest(test);
		
		return result;
	}

	public static AEqualsDefinition newAEqualsDefinition(LexLocation location,
			ASetBind setbind, PExp test) {
		AEqualsDefinition result = AstFactory.newAEqualsDefinition(location);
				
		result.setPattern(null);
		result.setTypebind(null);
		result.setSetbind(setbind);
		result.setTest(test);
		
		return result;
	}

	public static AClassInvariantDefinition newAClassInvariantDefinition(
			LexNameToken name, PExp expression) {
		AClassInvariantDefinition result = new AClassInvariantDefinition();
		// Definition initialization
		initDefinition(result, Pass.DEFS, name.location, name, NameScope.GLOBAL);
		
		result.setExpression(expression);
		
		return result;
	}

	public static AInstanceVariableDefinition newAInstanceVariableDefinition(
			LexNameToken name, PType type, PExp expression) {
		AInstanceVariableDefinition result = new AInstanceVariableDefinition();
		
		// Definition initialization
		initDefinition(result, Pass.VALUES, name.location, name, NameScope.STATE);

		result.setType(type);
		result.setExpression(expression);
		result.getLocation().executable(false);
		result.setOldname(name.getOldName());
		result.setInitialized(!(expression instanceof AUndefinedExp));
		
		return result;
	}

	public static AThreadDefinition newAThreadDefinition(PStm statement) {
		AThreadDefinition result = new AThreadDefinition();

		// Definition initialization
		initDefinition(result, Pass.DEFS, statement.getLocation(), null, NameScope.GLOBAL);

		result.setStatement(statement);
		result.setOperationName(LexNameToken.getThreadName(statement.getLocation()));
		result.setAccess(PAccessSpecifierAssistant.getProtected());
		
		return result;
	}
	
	public static AThreadDefinition newAThreadDefinition(LexNameToken opname,
			List<PExp> args) {
		
		APeriodicStm periodicStatement = AstFactory.newAPeriodicStm(opname,args);
		return newAThreadDefinition(periodicStatement);
	}

	private static APeriodicStm newAPeriodicStm(LexNameToken opname,
			List<PExp> args) {
		APeriodicStm result = new APeriodicStm();

		//Statement initialization
		initStatement(result, opname.location);

		result.setOpname(opname);
		result.setArgs(args);
		
		return result;
	}

	public static APerSyncDefinition newAPerSyncDefinition(LexLocation location,
			LexNameToken opname, PExp guard) {
		APerSyncDefinition result = new APerSyncDefinition();
		// Definition initialization
		initDefinition(result, Pass.DEFS, location, opname.getPerName(location), NameScope.GLOBAL);
		
		result.setOpname(opname);
		result.setGuard(guard);
		
		return result;
	}

	public static AMutexSyncDefinition newAMutexSyncDefinition(LexLocation location,
			LexNameList operations) {
		AMutexSyncDefinition result = new AMutexSyncDefinition();
		// Definition initialization
		initDefinition(result, Pass.DEFS, location, null, NameScope.GLOBAL);

		result.setOperations(operations);
		
		return result;
	}

	public static ANamedTraceDefinition newANamedTraceDefinition(LexLocation location,
			List<String> pathname, List<List<PTraceDefinition>> terms) {
		ANamedTraceDefinition result = new ANamedTraceDefinition();
		// Definition initialization
		initDefinition(result, Pass.DEFS, location, new LexNameToken(
				location.module, Utils.listToString(pathname, "_"), location), NameScope.GLOBAL);

		List<ClonableString> namesClonable = new Vector<ClonableString>();
		for (String string : pathname)
		{
			namesClonable.add( new ClonableString(string));
		}
		
		List<ATraceDefinitionTerm> tracesTerms = new Vector<ATraceDefinitionTerm>();
		for (List<PTraceDefinition> list : terms)
		{
			tracesTerms.add( new ATraceDefinitionTerm(list));
		}
		
		result.setPathname(namesClonable);
		result.setTerms(tracesTerms);
		result.setAccess(PAccessSpecifierAssistant.getPublic());
		
		return result;
	}

	public static ARepeatTraceDefinition newARepeatTraceDefinition(
			LexLocation location, PTraceCoreDefinition core, long from, long to) {
		return new ARepeatTraceDefinition(location, core, from, to);
	}

	public static ALetDefBindingTraceDefinition newALetDefBindingTraceDefinition(
			LexLocation location, List<AValueDefinition> localDefs,
			PTraceDefinition body) {
		return new ALetDefBindingTraceDefinition(location, localDefs, body);
	}

	public static ALetBeStBindingTraceDefinition newALetBeStBindingTraceDefinition(
			LexLocation location, PMultipleBind bind, PExp stexp,
			PTraceDefinition body) {
		return new ALetBeStBindingTraceDefinition(location, bind, stexp, body, null);
	}

	public static AConcurrentExpressionTraceCoreDefinition newAConcurrentExpressionTraceCoreDefinition(
			LexLocation location, List<PTraceDefinition> defs) {
		return new AConcurrentExpressionTraceCoreDefinition(location,defs);
	}

	public static AAccessSpecifierAccessSpecifier newAAccessSpecifierAccessSpecifier(
			Access access, boolean isStatic, boolean isAsync) {
		return new AAccessSpecifierAccessSpecifier(access,isStatic,isAsync);
	}

	public static APatternListTypePair newAPatternListTypePair(
			List<PPattern> patterns, PType type) {
		APatternListTypePair result = new APatternListTypePair();
		result.setPatterns(patterns);
		result.setType(type);
		
		return result;
	}

	public static AIdentifierPattern newAIdentifierPattern(LexNameToken token) {
		AIdentifierPattern result = new AIdentifierPattern();
		result.setLocation(token.location);
		result.setName(token);
		return result;
	}

	public static ATuplePattern newATuplePattern(LexLocation location,
			List<PPattern> list) {
		ATuplePattern result = new ATuplePattern();
		result.setLocation(location);
		result.setPlist(list);
		return result;
	}

	public static AProductType newAProductType(LexLocation location,
			List<PType> types) {
		AProductType result = new AProductType();
		result.setLocation(location);
		result.setTypes(types);
		return result;
	}

	public static APatternTypePair newAPatternTypePair(
			PPattern pattern, PType type) {
		APatternTypePair result = new APatternTypePair();
		result.setPattern(pattern);
		result.setType(type);
		return result;
	}

	public static AErrorCase newAErrorCase(LexIdentifierToken name, PExp left,
			PExp right) {
		return new AErrorCase(name, left, right);
	}

	public static AApplyExpressionTraceCoreDefinition newAApplyExpressionTraceCoreDefinition(
			PStm stmt, String currentModule) {
		return new AApplyExpressionTraceCoreDefinition(stmt.getLocation(),stmt,currentModule);
	}

	public static ABracketedExpressionTraceCoreDefinition newABracketedExpressionTraceCoreDefinition(
			LexLocation location, List<List<PTraceDefinition>> list) {
		return new ABracketedExpressionTraceCoreDefinition(location, list);
	}

	public static AEquivalentBooleanBinaryExp newAEquivalentBooleanBinaryExp(PExp left, LexToken op,
			PExp right) {
		AEquivalentBooleanBinaryExp result = new AEquivalentBooleanBinaryExp();
		//Binary Expression init
		result.setLocation(op.location);
		result.setLeft(left);
		result.setRight(right);
		result.setOp(op);
		
		return result;
		
	}

	public static AImpliesBooleanBinaryExp newAImpliesBooleanBinaryExp(PExp left, LexToken op,
			PExp right) {
		AImpliesBooleanBinaryExp result = new AImpliesBooleanBinaryExp();
		//Binary Expression init
		result.setLocation(op.location);
		result.setLeft(left);
		result.setRight(right);
		result.setOp(op);
		
		return result;
	}

	public static AOrBooleanBinaryExp newAOrBooleanBinaryExp(PExp left, LexToken op,
			PExp right) {
		AOrBooleanBinaryExp result = new AOrBooleanBinaryExp();
		//Binary Expression init
		result.setLocation(op.location);
		result.setLeft(left);
		result.setRight(right);
		result.setOp(op);
		
		return result;
	}

	public static AAndBooleanBinaryExp newAAndBooleanBinaryExp(PExp left, LexToken op,
			PExp right) {
		AAndBooleanBinaryExp result = new AAndBooleanBinaryExp();
		//Binary Expression init
		result.setLocation(op.location);
		result.setLeft(left);
		result.setRight(right);
		result.setOp(op);
		
		return result;
	}

	public static ANotUnaryExp newANotUnaryExp(LexLocation location,
			PExp readNotExpression) {
		return new ANotUnaryExp(location, readNotExpression);
	}

	public static AEqualsBinaryExp newAEqualsBinaryExp(PExp exp,
			LexToken token, PExp readEvaluatorP1Expression) {
		return new AEqualsBinaryExp(token.location, exp, token, readEvaluatorP1Expression);
	}

	public static ALessNumericBinaryExp newALessNumericBinaryExp(PExp exp, LexToken token,
			PExp readNotExpression) {
		return new ALessNumericBinaryExp(token.location,exp,token,readNotExpression);
	}

	public static ALessEqualNumericBinaryExp newALessEqualNumericBinaryExp(PExp exp, LexToken token,
			PExp readNotExpression) {
		return new ALessEqualNumericBinaryExp(token.location,exp,token,readNotExpression);
	}

	public static AGreaterNumericBinaryExp newAGreaterNumericBinaryExp(PExp exp, LexToken token,
			PExp readNotExpression) {
		return new AGreaterNumericBinaryExp(token.location,exp,token,readNotExpression);
	}

	public static AGreaterEqualNumericBinaryExp newAGreaterEqualNumericBinaryExp(PExp exp,
			LexToken token, PExp readNotExpression) {
		return new AGreaterEqualNumericBinaryExp(token.location,exp,token,readNotExpression);
	}

	public static ANotEqualBinaryExp newANotEqualBinaryExp(PExp exp, LexToken token,
			PExp readNotExpression) {
		return new ANotEqualBinaryExp(token.location,exp,token,readNotExpression);
	}

	public static ASubsetBinaryExp newASubsetBinaryExp(PExp exp, LexToken token,
			PExp readNotExpression) {
		return new ASubsetBinaryExp(token.location,exp,token,readNotExpression);
	}

	public static AProperSubsetBinaryExp newAProperSubsetBinaryExp(PExp exp, LexToken token,
			PExp readNotExpression) {
		return new AProperSubsetBinaryExp(token.location,exp,token,readNotExpression);
	}

	public static AInSetBinaryExp newAInSetBinaryExp(PExp exp, LexToken token,
			PExp readNotExpression) {
		return new AInSetBinaryExp(token.location,exp,token,readNotExpression);
	}

	public static ANotInSetBinaryExp newANotInSetBinaryExp(PExp exp, LexToken token,
			PExp readNotExpression) {
		return new ANotInSetBinaryExp(token.location,exp,token,readNotExpression);
	}

	public static APlusNumericBinaryExp newAPlusNumericBinaryExp(PExp exp, LexToken token,
			PExp readEvaluatorP2Expression) {
		return new APlusNumericBinaryExp(token.location,exp,token,readEvaluatorP2Expression);
	}

	public static ASubstractNumericBinaryExp newASubstractNumericBinaryExp(PExp exp, LexToken token,
			PExp readEvaluatorP2Expression) {
		return new ASubstractNumericBinaryExp(token.location,exp,token,readEvaluatorP2Expression);
	}

	public static ASetUnionBinaryExp newASetUnionBinaryExp(PExp exp, LexToken token,
			PExp readEvaluatorP2Expression) {
		return new ASetUnionBinaryExp(token.location,exp,token,readEvaluatorP2Expression);
	}

	public static ASetDifferenceBinaryExp newASetDifferenceBinaryExp(PExp exp, LexToken token,
			PExp readEvaluatorP2Expression) {
		return new ASetDifferenceBinaryExp(token.location,exp,token,readEvaluatorP2Expression);
	}

	public static AMapUnionBinaryExp newAMapUnionBinaryExp(PExp exp, LexToken token,
			PExp readEvaluatorP2Expression) {
		return new AMapUnionBinaryExp(token.location,exp,token,readEvaluatorP2Expression);
	}

	public static APlusPlusBinaryExp newAPlusPlusBinaryExp(PExp exp, LexToken token,
			PExp readEvaluatorP2Expression) {
		return new APlusPlusBinaryExp(token.location,exp,token,readEvaluatorP2Expression);
	}

	public static ASeqConcatBinaryExp newASeqConcatBinaryExp(PExp exp, LexToken token,
			PExp readEvaluatorP2Expression) {
		return new ASeqConcatBinaryExp(token.location,exp,token,readEvaluatorP2Expression);
	}

	public static ATimesNumericBinaryExp newATimesNumericBinaryExp(PExp exp, LexToken token,
			PExp readEvaluatorP3Expression) {
		return new ATimesNumericBinaryExp(token.location,exp,token,readEvaluatorP3Expression);
	}

	public static ADivideNumericBinaryExp newADivideNumericBinaryExp(PExp exp, LexToken token,
			PExp readEvaluatorP3Expression) {
		return new ADivideNumericBinaryExp(token.location,exp,token,readEvaluatorP3Expression);
	}

	public static ARemNumericBinaryExp newARemNumericBinaryExp(PExp exp, LexToken token,
			PExp readEvaluatorP3Expression) {
		return new ARemNumericBinaryExp(token.location,exp,token,readEvaluatorP3Expression);
	}

	public static AModNumericBinaryExp newAModNumericBinaryExp(PExp exp, LexToken token,
			PExp readEvaluatorP3Expression) {
		return new AModNumericBinaryExp(token.location,exp,token,readEvaluatorP3Expression);
	}

	public static ADivNumericBinaryExp newADivNumericBinaryExp(PExp exp, LexToken token,
			PExp readEvaluatorP3Expression) {
		return new ADivNumericBinaryExp(token.location,exp,token,readEvaluatorP3Expression);
	}

	public static ASetIntersectBinaryExp newASetIntersectBinaryExp(PExp exp, LexToken token,
			PExp readEvaluatorP3Expression) {
		return new ASetIntersectBinaryExp(token.location,exp,token,readEvaluatorP3Expression);
	}

	public static AMapInverseUnaryExp newAMapInverseUnaryExp(LexLocation location,
			PExp readEvaluatorP3Expression) {
		return new AMapInverseUnaryExp(location, readEvaluatorP3Expression);
	}

	public static ADomainResToBinaryExp newADomainResToBinaryExp(PExp exp, LexToken token,
			PExp readEvaluatorP5Expression) {
		return new ADomainResToBinaryExp(token.location,exp,token,readEvaluatorP5Expression);
	}

	public static ADomainResByBinaryExp newADomainResByBinaryExp(PExp exp, LexToken token,
			PExp readEvaluatorP5Expression) {
		return new ADomainResByBinaryExp(token.location,exp,token,readEvaluatorP5Expression);
	}

	public static ARangeResToBinaryExp newARangeResToBinaryExp(PExp exp, LexToken token,
			PExp readEvaluatorP6Expression) {
		return new ARangeResToBinaryExp(token.location,exp,token,readEvaluatorP6Expression);
	}

	public static ARangeResByBinaryExp newARangeResByBinaryExp(PExp exp, LexToken token,
			PExp readEvaluatorP6Expression) {
		return new ARangeResByBinaryExp(token.location,exp,token,readEvaluatorP6Expression);
	}

	public static AApplyExp newAApplyExp(PExp root) {
		AApplyExp result = new AApplyExp();
		result.setLocation(root.getLocation());
		result.setRoot(root);
		result.setArgs(new Vector<PExp>());
		return result;
	}

	public static ASubseqExp newASubseqExp(PExp seq, PExp from, PExp to) {
		ASubseqExp result = new ASubseqExp();
		result.setLocation(seq.getLocation());
		result.setSeq(seq);
		result.setFrom(from);
		result.setTo(to);
		
		return result;
	}

	public static PExp newAApplyExp(PExp root, List<PExp> args) {
		AApplyExp result = new AApplyExp();
		result.setLocation(root.getLocation());
		result.setRoot(root);
		result.setArgs(args);
		return result;
	}

	public static AFuncInstatiationExp newAFuncInstatiationExp(PExp function, List<PType> types) {
		AFuncInstatiationExp result = new AFuncInstatiationExp();
		result.setLocation(function.getLocation());
		result.setFunction(function);
		result.setActualTypes(types);
		return result;
	}

	public static AFieldExp newAFieldExp(PExp object, LexNameToken field) {
		AFieldExp result = new AFieldExp();
		result.setLocation(object.getLocation());
		result.setObject(object);
		result.setField(new LexIdentifierToken(field.name, field.old, field.location));
		result.setMemberName(field);
		result.getField().getLocation().executable(true);
		return result;
	}

	public static PExp newAFieldExp(PExp object, LexIdentifierToken field) {
		AFieldExp result = new AFieldExp();
		result.setLocation(object.getLocation());
		result.setObject(object);
		result.setField(field);
		result.getField().getLocation().executable(true);
		return result;
	}

	public static PExp newAFieldNumberExp(PExp tuple, LexIntegerToken field) {
		AFieldNumberExp result = new AFieldNumberExp();
		result.setLocation(tuple.getLocation());
		result.setTuple(tuple);
		result.setField(field);
		result.getField().location.executable(true);
		
		return result;
	}

	public static ACompBinaryExp newACompBinaryExp(PExp left, LexToken op,
			PExp right) {
		return new ACompBinaryExp(op.location,left,op,right);
	}

	public static AStarStarBinaryExp newAStarStarBinaryExp(PExp left, LexToken op,
			PExp right) {
		return new AStarStarBinaryExp(op.location,left,op,right);
	}

	public static AIntLiteralExp newAIntLiteralExp(LexIntegerToken value) {
		return new AIntLiteralExp(value.location,value);
	}

	public static ARealLiteralExp newARealLiteralExp(LexRealToken value) {
		return new ARealLiteralExp(value.location, value);
	}

	public static AVariableExp newAVariableExp(LexNameToken name) {
		return new AVariableExp(name.location,name,name.getName());
	}

	public static AStringLiteralExp newAStringLiteralExp(LexStringToken token) {
		return new AStringLiteralExp(token.location, token);
	}

	public static ACharLiteralExp newACharLiteralExp(LexCharacterToken token) {
		return new ACharLiteralExp(token.location,token);
	}

	public static AQuoteLiteralExp newAQuoteLiteralExp(LexQuoteToken token) {
		return new AQuoteLiteralExp(token.location, token);
	}

	public static ABooleanConstExp newABooleanConstExp(LexBooleanToken token) {
		return new ABooleanConstExp(token.location, token);
	}

	public static AUndefinedExp newAUndefinedExp(LexLocation location) {
		return new AUndefinedExp(location);
	}

	public static ANilExp newANilExp(LexLocation location) {
		return new ANilExp(location);
	}

	public static AThreadIdExp newAThreadIdExp(LexLocation location) {
		return new AThreadIdExp(location);
	}

	public static ASelfExp newASelfExp(LexLocation location) {
		return new ASelfExp(location, new LexNameToken(location.module, "self",location));
	}

	public static ANotYetSpecifiedExp newANotYetSpecifiedExp(LexLocation location) {
		ANotYetSpecifiedExp result = new ANotYetSpecifiedExp(location);
		result.getLocation().executable(false); // ie. ignore coverage for these
		return result;
	}

	public static ASubclassResponsibilityExp newASubclassResponsibilityExp(LexLocation location) {
		ASubclassResponsibilityExp result = new ASubclassResponsibilityExp(location);
		location.hit();
		return result;
	}

	public static ATimeExp newATimeExp(LexLocation location) {
		return new ATimeExp(location);
	}

	public static AMuExp newAMuExp(LexLocation location, PExp record,
			List<ARecordModifier> args) {
		return new AMuExp(location, record, args);
	}

	public static ARecordModifier newARecordModifier(LexIdentifierToken tag,
			PExp value) {
		return new ARecordModifier(tag, value);
	}

	public static ATupleExp newATupleExp(LexLocation location, List<PExp> args) {
		return new ATupleExp(location, args);
	}

	public static ABooleanBasicType newABooleanBasicType(LexLocation location) {
		ABooleanBasicType result = new ABooleanBasicType();
		result.setLocation(location);
		return result;
	}

	public static AMkBasicExp newAMkBasicExp(SBasicType type,
			PExp arg) {
		AMkBasicExp result = new AMkBasicExp();
		result.setLocation(type.getLocation());
		result.setType(type);
		result.setArg(arg);
		return result;
	}

	public static ANatNumericBasicType newANatNumericBasicType(LexLocation location) {
		ANatNumericBasicType result = new ANatNumericBasicType();
		result.setLocation(location);
		return result;
	}

	public static ANatOneNumericBasicType newANatOneNumericBasicType(LexLocation location) {
		ANatOneNumericBasicType result = new ANatOneNumericBasicType();
		result.setLocation(location);
		return result;
	}

	public static AIntNumericBasicType newAIntNumericBasicType(LexLocation location) {
		AIntNumericBasicType result = new AIntNumericBasicType();
		result.setLocation(location);
		return result;
	}

	public static ARationalNumericBasicType newARationalNumericBasicType(LexLocation location) {
		ARationalNumericBasicType result = new ARationalNumericBasicType();
		result.setLocation(location);
		return result;
	}

	public static ARealNumericBasicType newARealNumericBasicType(LexLocation location) {
		ARealNumericBasicType result = new ARealNumericBasicType();
		result.setLocation(location);
		return result;
	}

	public static ACharBasicType newACharBasicType(LexLocation location) {
		ACharBasicType result = new ACharBasicType();
		result.setLocation(location);
		return result;
	}

	public static ATokenBasicType newATokenBasicType(LexLocation location) {
		ATokenBasicType result = new ATokenBasicType();
		result.setLocation(location);
		return result;
	}

	public static AMkTypeExp newAMkTypeExp(LexNameToken typename, List<PExp> args) {
		AMkTypeExp result = new AMkTypeExp();
		result.setLocation(typename.getLocation());
		result.setTypeName(typename);
		result.setArgs(args);
		return result;
	}

	public static AIsExp newAIsExp(LexLocation location,
			LexNameToken name, PExp test) {
		AIsExp result = new AIsExp();
		location.executable(true);
		
		result.setLocation(location);
		result.setBasicType(null);
		result.setTypeName(name);
		result.setTest(test);
		
		return result;
	}

	public static AIsExp newAIsExp(LexLocation location, PType type, PExp test) {
		AIsExp result = new AIsExp();
		location.executable(true);
		
		result.setLocation(location);
		result.setBasicType(type);
		result.setTypeName(null);
		result.setTest(test);
		
		return result;
	}

	public static APreExp newAPreExp(LexLocation location, PExp function,
			List<PExp> args) {
		APreExp result = new APreExp();
		result.setLocation(location);
		result.setFunction(function);
		result.setArgs(args);
		return result;
	}

	public static ASetEnumSetExp newASetEnumSetExp(LexLocation start) {
		ASetEnumSetExp result = new ASetEnumSetExp();
		result.setLocation(start);
		result.setMembers(new Vector<PExp>());
		return result;
	}

	public static AMapEnumMapExp newAMapEnumMapExp(LexLocation start) {
		AMapEnumMapExp result = new AMapEnumMapExp();
		result.setLocation(start);
		result.getLocation().executable(true);
		result.setMembers(new Vector<AMapletExp>());
		return result;
	}

	public static AMapletExp newAMapletExp(PExp left, LexToken op,
			PExp right) {
		AMapletExp result = new AMapletExp(op.location, left, right);
		return result;
	}

	public static ASetCompSetExp newASetCompSetExp(LexLocation start, PExp first,
			List<PMultipleBind> bindings, PExp predicate) {
		ASetCompSetExp result = new ASetCompSetExp(start, first, bindings, predicate);
		start.executable(true);
		return result;
	}

	public static ASetRangeSetExp newASetRangeSetExp(LexLocation start, PExp first,
			PExp last) {
		ASetRangeSetExp result = new ASetRangeSetExp(start, first, last);
		start.executable(true);
		return result;
	}

	public static AMapCompMapExp newAMapCompMapExp(LexLocation start,
			AMapletExp first, List<PMultipleBind> bindings, PExp predicate) {
		AMapCompMapExp result = new AMapCompMapExp(start, first, bindings, predicate);
		start.executable(true);
		return result;
	}

	public static AMapEnumMapExp newAMapEnumMapExp(LexLocation start,
			List<AMapletExp> members) {
		AMapEnumMapExp result = new AMapEnumMapExp();
		result.setLocation(start);
		result.getLocation().executable(true);
		result.setMembers(members);
		return result;
	}

	public static ASeqEnumSeqExp newASeqEnumSeqExp(LexLocation start) {
		ASeqEnumSeqExp result = new ASeqEnumSeqExp(start, new Vector<PExp>());
		start.executable(true);
		return result;
	}

	public static ASeqCompSeqExp newASeqCompSeqExp(LexLocation start, PExp first,
			ASetBind setbind, PExp predicate) {
		ASeqCompSeqExp result = new ASeqCompSeqExp(start,first,setbind,predicate);
		start.executable(true);
		return result;
	}

	public static ASeqEnumSeqExp newASeqEnumSeqExp(LexLocation start,
			List<PExp> members) {
		ASeqEnumSeqExp result = new ASeqEnumSeqExp(start, members);
		start.executable(true);
		return result;
	}

	public static AIfExp newAIfExp(LexLocation start, PExp test, PExp thenExp,
			List<AElseIfExp> elseList, PExp elseExp) {
		
		AIfExp result = new AIfExp(start, test, thenExp, elseList, elseExp);
		start.executable(true);
		return result;
	}

	public static AElseIfExp newAElseIfExp(LexLocation start, PExp elseIfExp,
			PExp thenExp) {
		AElseIfExp result = new AElseIfExp(start, elseIfExp, thenExp);
		start.executable(true);
		return result;
	}

	public static ACasesExp newACasesExp(LexLocation start, PExp exp,
			List<ACaseAlternative> cases, PExp others) {
		ACasesExp result = new ACasesExp(start, exp, cases, others);
		start.executable(true);
		return result;
	}

	public static ACaseAlternative newACaseAlternative(PExp cexp,
			PPattern pattern, PExp resultExp) {
		ACaseAlternative result = new ACaseAlternative();
		result.setLocation(pattern.getLocation());
		result.setCexp(cexp);
		result.setPattern(pattern);
		result.setResult(resultExp);
		return result;
	}

	public static ALetDefExp newALetDefExp(LexLocation start,
			List<PDefinition> localDefs, PExp readConnectiveExpression) {
		ALetDefExp result = new ALetDefExp(start, localDefs, readConnectiveExpression);
		start.executable(true);
		return result;
	}

	public static ALetBeStExp newALetBeStExp(LexLocation start,
			PMultipleBind bind, PExp suchThat, PExp value) {
		ALetBeStExp result = new ALetBeStExp();
		result.setLocation(start);
		start.executable(true);
		result.setBind(bind);
		result.setSuchThat(suchThat);
		result.setValue(value);
		
		return result;
	}

	public static AForAllExp newAForAllExp(LexLocation start,
			List<PMultipleBind> bindList, PExp predicate) {
		AForAllExp result = new AForAllExp(start, bindList, predicate);
		start.executable(true);
		return result;
	}

	public static AExistsExp newAExistsExp(LexLocation start,
			List<PMultipleBind> bindList, PExp predicate) {
		AExistsExp result = new AExistsExp(start, bindList, predicate);
		start.executable(true);
		return result;
	}

	public static AExists1Exp newAExists1Exp(LexLocation start, PBind bind,
			PExp predicate) {
		AExists1Exp result = new AExists1Exp();
		result.setLocation(start);
		start.executable(true);
		result.setBind(bind);
		result.setPredicate(predicate);
		return result;
	}

	public static AIotaExp newAIotaExp(LexLocation start, PBind bind,
			PExp predicate) {
		AIotaExp result = new AIotaExp();
		result.setLocation(start);
		start.executable(true);
		result.setBind(bind);
		result.setPredicate(predicate);
		return result;
	}

	public static ALambdaExp newALambdaExp(LexLocation start,
			List<ATypeBind> bindList, PExp expression) {
		ALambdaExp result = new ALambdaExp();
		result.setLocation(start);
		start.executable(true);
		result.setBindList(bindList);
		result.setExpression(expression);
		return result;
	}

	public static ADefExp newADefExp(LexLocation start,
			List<PDefinition> equalsDefs, PExp expression) {
		ADefExp result = new ADefExp();
		result.setLocation(start);
		start.executable(true);
		result.setLocalDefs(equalsDefs);
		result.setExpression(expression);
		return result;
	}

	public static ANewExp newANewExp(LexLocation start,
			LexIdentifierToken classname, List<PExp> args) {
		ANewExp result = new ANewExp();
		result.setLocation(start);
		start.executable(true);
		result.setClassName(classname);
		result.setArgs(args);
		classname.location.executable(true);
		return result;
	}

	public static AIsOfBaseClassExp newAIsOfBaseClassExp(LexLocation start,
			LexNameToken classname, PExp pExp) {
		AIsOfBaseClassExp result = new AIsOfBaseClassExp();
		result.setLocation(start);
		start.executable(true);
		result.setBaseClass(classname.getExplicit(false));
		result.setExp(pExp);
		return result;
	}

	public static AIsOfClassExp newAIsOfClassExp(LexLocation start,
			LexNameToken classname, PExp pExp) {
		AIsOfClassExp result = new AIsOfClassExp();
		result.setLocation(start);
		start.executable(true);
		result.setClassName(classname.getExplicit(false));
		result.setExp(pExp);
		return result;
	}

	public static ASameBaseClassExp newASameBaseClassExp(LexLocation start,
			List<PExp> args) {
		ASameBaseClassExp result = new ASameBaseClassExp();
		result.setLocation(start);
		start.executable(true);
		result.setLeft(args.get(0));
		result.setRight(args.get(1));
		return result;
	}

	public static ASameClassExp newASameClassExp(LexLocation start,
			List<PExp> args) {
		ASameClassExp result = new ASameClassExp();
		result.setLocation(start);
		start.executable(true);
		result.setLeft(args.get(0));
		result.setRight(args.get(1));
		return result;
	}

	public static AHistoryExp newAHistoryExp(LexLocation location, LexToken op,
			LexNameList opnames) {
		AHistoryExp result = new AHistoryExp();
		result.setLocation(location);
		location.executable(true);
		result.setHop(op);
		result.setOpnames(opnames);
		
		return result;
	}

	public static AAllImport newAAllImport(LexNameToken name) {
		AAllImport result = new AAllImport();
		result.setLocation(name.location);
		result.setName(name);
		result.setRenamed(null);
		return result;
	}

	public static AFromModuleImports newAFromModuleImports(
			LexIdentifierToken name, List<List<PImport>> signatures) {
		return new AFromModuleImports(name, signatures);
	}

	public static AModuleModules newAModuleModules(File file,
			List<PDefinition> definitions) {
		
		AModuleModules result = new AModuleModules();

		if (definitions.isEmpty())
		{
			result.setName(defaultName(new LexLocation()));
		} else
		{
			result.setName(defaultName(definitions.get(0).getLocation()));
		}

		
		result.setImports(null);
		result.setExports(null);
		result.setDefs(definitions);
		
		List<ClonableFile> files = new Vector<ClonableFile>();
		if (file != null)
		{
			files.add(new ClonableFile(file));
		}
		result.setFiles(files);
		
		result.setExportdefs(new Vector<PDefinition>()); // Export nothing
		result.setImportdefs(new Vector<PDefinition>()); // and import nothing
		
		result.setIsFlat(true);
		return result;
	}

	/**
	 * Generate the default module name.
	 * 
	 * @param location
	 *            The textual location of the name
	 * @return The default module name.
	 */

	private static LexIdentifierToken defaultName(LexLocation location)
	{
		return new LexIdentifierToken("DEFAULT", false, location);
	}

	public static AModuleModules newAModuleModules(LexIdentifierToken name,
			AModuleImports imports, AModuleExports exports,
			List<PDefinition> defs) {
		AModuleModules result = new AModuleModules();
		
		result.setName(name);
		result.setImports(imports);
		result.setExports(exports);
		result.setDefs(defs);
		
		List<ClonableFile> files = new Vector<ClonableFile>();
		files.add(new ClonableFile(name.location.file));
		result.setFiles(files);
		
		result.setExportdefs(new Vector<PDefinition>()); // By default, export nothing
		result.setImportdefs(new Vector<PDefinition>()); // and import nothing
	
		return result;
	}

	public static AModuleExports newAModuleExports(
			List<List<PExport>> exports) {
		return new AModuleExports(exports);
	}

	public static AAllExport newAAllExport(LexLocation location) {
		AAllExport result = new AAllExport();
		result.setLocation(location);
		return result;
	}

	public static ATypeExport newATypeExport(LexNameToken name, boolean struct) {
		ATypeExport result = new ATypeExport();
		result.setLocation(name.location);
		result.setName(name);
		result.setStruct(struct);
		return result;
	}

	public static AValueExport newAValueExport(LexLocation location,
			List<LexNameToken> nameList, PType type) {
		AValueExport result = new AValueExport();
		result.setLocation(location);
		result.setNameList(nameList);
		result.setExportType(type);
		return result;
	}

	public static AFunctionExport newAFunctionExport(LexLocation location,
			List<LexNameToken> nameList, PType type) {
		AFunctionExport result = new AFunctionExport();
		result.setLocation(location);
		result.setNameList(nameList);
		result.setExportType(type);
		return result;
	}

	public static AOperationExport newAOperationExport(LexLocation location,
			List<LexNameToken> nameList, PType type) {
		AOperationExport result = new AOperationExport();
		result.setLocation(location);
		result.setNameList(nameList);
		result.setExportType(type);
		return result;
	}

	public static AModuleImports newAModuleImports(LexIdentifierToken name,
			List<AFromModuleImports> imports) {
		return new AModuleImports(name, imports);
	}

	public static ATypeImport newATypeImport(ATypeDefinition def,
			LexNameToken renamed) {
		ATypeImport result = new ATypeImport();
		result.setLocation(def.getName().location);
		result.setName(def.getName());
		result.setRenamed(renamed);
		result.setDef(def);
		return result;
	}

	public static ATypeImport newATypeImport(LexNameToken defname,
			LexNameToken renamed) {
		ATypeImport result = new ATypeImport();
		result.setLocation(defname.location);
		result.setName(defname);
		result.setRenamed(renamed);
		result.setDef(null);
		return result;
	}

	public static AValueValueImport newAValueValueImport(LexNameToken defname,
			PType type, LexNameToken renamed) {
		AValueValueImport result = new AValueValueImport();
		result.setLocation(defname.location);
		result.setName(defname);
		result.setRenamed(renamed);
		result.setImportType(type);
		return result;
	}

	public static AFunctionValueImport newAFunctionValueImport(
			LexNameToken defname, PType type, LexNameList typeParams,
			LexNameToken renamed) {
		AFunctionValueImport result = new AFunctionValueImport();
		result.setLocation(defname.location);
		result.setName(defname);
		result.setRenamed(renamed);
		result.setImportType(type);
		result.setTypeParams(typeParams);
		
		return result;
	}

	public static AOperationValueImport newAOperationValueImport(
			LexNameToken defname, PType type, LexNameToken renamed) {
		AOperationValueImport result = new AOperationValueImport();
		result.setLocation(defname.location);
		result.setName(defname);
		result.setRenamed(renamed);
		result.setImportType(type);
		
		return result;
	}

	public static AUnionPattern newAUnionPattern(PPattern left,
			LexLocation location, PPattern right) {
		AUnionPattern result = new AUnionPattern();
		result.setLocation(location);
		result.setLeft(left);
		result.setRight(right);
		return result;
	}

	public static AConcatenationPattern newAConcatenationPattern(PPattern left,
			LexLocation location, PPattern right) {
		AConcatenationPattern result = new AConcatenationPattern();
		result.setLocation(location);
		result.setLeft(left);
		result.setRight(right);
		return result;
	}

	public static AIntegerPattern newAIntegerPattern(LexIntegerToken token) {
		AIntegerPattern result =  new AIntegerPattern();
		result.setLocation(token.location);
		result.setValue(token);
		return result;
	}

	public static ARealPattern newARealPattern(LexRealToken token) {
		ARealPattern result =  new ARealPattern();
		result.setLocation(token.location);
		result.setValue(token);
		return result;
	}

	public static ACharacterPattern newACharacterPattern(LexCharacterToken token) {
		ACharacterPattern result =  new ACharacterPattern();
		result.setLocation(token.location);
		result.setValue(token);
		return result;
	}

	public static AStringPattern newAStringPattern(LexStringToken token) {
		AStringPattern result =  new AStringPattern();
		result.setLocation(token.location);
		result.setValue(token);
		return result;
	}

	public static AQuotePattern newAQuotePattern(LexQuoteToken token) {
		AQuotePattern result =  new AQuotePattern();
		result.setLocation(token.location);
		result.setValue(token);
		return result;
	}

	public static ABooleanPattern newABooleanPattern(LexBooleanToken token) {
		ABooleanPattern result =  new ABooleanPattern();
		result.setLocation(token.location);
		result.setValue(token);
		return result;
	}

	public static ANilPattern newANilPattern(LexKeywordToken token) {
		ANilPattern result =  new ANilPattern();
		result.setLocation(token.location);
		return result;
	}

	public static AExpressionPattern newAExpressionPattern(PExp expression) {
		AExpressionPattern result = new AExpressionPattern();
		result.setLocation(expression.getLocation());
		result.setExp(expression);
		return result;
	}

	public static ASetPattern newASetPattern(LexLocation location,
			List<PPattern> list) {
		ASetPattern result = new ASetPattern();
		result.setLocation(location);
		result.setPlist(list);
		return result;
	}

	public static ASeqPattern newASeqPattern(LexLocation location,
			List<PPattern> list) {
		ASeqPattern result = new ASeqPattern();
		result.setLocation(location);
		result.setPlist(list);
		return result;
	}

	public static ARecordPattern newARecordPattern(LexNameToken typename,
			List<PPattern> list) {
		ARecordPattern result = new ARecordPattern();
		result.setLocation(typename.location);
		result.setPlist(list);
		result.setTypename(typename);
		result.setType(AstFactory.getAUnresolvedType(typename));
		return result;
	}

	private static AUnresolvedType getAUnresolvedType(LexNameToken typename) {
		AUnresolvedType result = new AUnresolvedType();
		result.setLocation(typename.location);
		result.setName(typename);
		return result;
	}

	public static AIgnorePattern newAIgnorePattern(LexLocation location) {
		AIgnorePattern result = new AIgnorePattern();
		result.setLocation(location);
		return result;
	}

	public static ANotYetSpecifiedStm newANotYetSpecifiedStm(LexLocation location) {
		ANotYetSpecifiedStm result = new ANotYetSpecifiedStm();
		result.setLocation(location);
		location.executable(false); // ie. ignore coverage for these
		return result;
	}

	public static ASubclassResponsibilityStm newASubclassResponsibilityStm(LexLocation location) {
		ASubclassResponsibilityStm result = new ASubclassResponsibilityStm();
		result.setLocation(location);
		location.hit(); // ie. ignore coverage for these
		return result;
	}

	public static AExitStm newAExitStm(LexLocation token, PExp exp) {
		AExitStm result = new AExitStm();
		result.setLocation(token);
		token.executable(true);
		result.setExpression(exp);
		return result;
	}

	public static PStm newAExitStm(LexLocation token) {
		AExitStm result = new AExitStm();
		result.setLocation(token);
		token.executable(true);
		result.setExpression(null);
		return result;
	}

	public static ATixeStm newATixeStm(LexLocation token,
			List<ATixeStmtAlternative> traps, PStm body) {
		ATixeStm result = new ATixeStm();
		result.setLocation(token);
		token.executable(true);
		result.setTraps(traps);
		result.setBody(body);
		return result;
	}

	public static ATrapStm newATrapStm(LexLocation token,
			ADefPatternBind patternBind, PStm with, PStm body) {
		ATrapStm result = new ATrapStm();
		result.setLocation(token);
		token.executable(true);
		
		result.setPatternBind(patternBind);
		result.setWith(with);
		result.setBody(body);
		
		return result;
	}

	public static AAlwaysStm newAAlwaysStm(LexLocation token, PStm always, PStm body) {
		AAlwaysStm result = new AAlwaysStm();
		result.setLocation(token);
		token.executable(true);
		
		result.setAlways(always);
		result.setBody(body);
		
		return result;
	}

	public static ANonDeterministicSimpleBlockStm newANonDeterministicSimpleBlockStm(
			LexLocation token) {
		ANonDeterministicSimpleBlockStm result = new ANonDeterministicSimpleBlockStm();
		result.setLocation(token);
		token.executable(true);
		
		result.setStatements(new Vector<PStm>());
		
		return result;
	}

	public static AAtomicStm newAAtomicStm(LexLocation token,
			List<AAssignmentStm> assignments) {
		AAtomicStm result = new AAtomicStm();
		result.setLocation(token);
		token.executable(true);
		
		result.setAssignments(assignments);
		return result;
	}

	public static ACallStm newACallStm(LexNameToken name, List<PExp> args) {
		ACallStm result = new ACallStm();
		result.setLocation(name.location);
		name.location.executable(true);
		result.setName(name);
		result.setArgs(args);
		
		return result;
	}

	public static ACallObjectStm newACallObjectStm(PObjectDesignator designator,
			LexNameToken classname, List<PExp> args) {
		ACallObjectStm result = new ACallObjectStm();
		result.setLocation(designator.getLocation());
		designator.getLocation().executable(true);
		
		result.setDesignator(designator);
		result.setClassname(classname);
		result.setFieldname(null);
		result.setArgs(args);
		result.setExplicit(classname.explicit);
		
		return result;
	}

	public static ACallObjectStm newACallObjectStm(PObjectDesignator designator,
			LexIdentifierToken fieldname, List<PExp> args) {
		ACallObjectStm result = new ACallObjectStm();
		result.setLocation(designator.getLocation());
		designator.getLocation().executable(true);
		
		result.setDesignator(designator);
		result.setClassname(null);
		result.setFieldname(fieldname);
		result.setArgs(args);
		result.setExplicit(false);
		
		return result;
	}

	public static AFieldObjectDesignator newAFieldObjectDesignator(
			PObjectDesignator object, LexIdentifierToken fieldname) {
		AFieldObjectDesignator result = new AFieldObjectDesignator();
		result.setLocation(object.getLocation());
		result.setObject(object);
		result.setClassName(null);
		result.setFieldName(fieldname);
		
		return result;
	}

	public static PObjectDesignator newAFieldObjectDesignator(
			PObjectDesignator object, LexNameToken classname) {
		AFieldObjectDesignator result = new AFieldObjectDesignator();
		result.setLocation(object.getLocation());
		result.setObject(object);
		result.setClassName(classname);
		result.setFieldName(null);
		
		return result;
	}

	public static AApplyObjectDesignator newAApplyObjectDesignator(
			PObjectDesignator object, List<PExp> args) {
		
		AApplyObjectDesignator result = new AApplyObjectDesignator();
		result.setLocation(object.getLocation());
		result.setObject(object);
		result.setArgs(args);
		
		return result;
	}

	public static ASelfObjectDesignator newASelfObjectDesignator(
			LexLocation location) {
		
		ASelfObjectDesignator result = new ASelfObjectDesignator();
		result.setLocation(location);
		result.setSelf(new LexNameToken(location.module, "self", location));
		return result;
	}

	public static AIdentifierObjectDesignator newAIdentifierObjectDesignator(
			LexNameToken name) {
		AIdentifierObjectDesignator result = new AIdentifierObjectDesignator();
		result.setLocation(name.location);
		result.setName(name);
		result.setExpression(AstFactory.newAVariableExp(name.getExplicit(true)));
		return result;
	}

	public static ANewObjectDesignator newANewObjectDesignator(
			LexIdentifierToken classname, List<PExp> args) {
		ANewObjectDesignator result = new ANewObjectDesignator();
		result.setLocation(classname.location);
		result.setExpression(AstFactory.newANewExp(classname.location, classname, args));
		return result;
	}

	public static AWhileStm newAWhileStm(LexLocation token, PExp exp, PStm body) {
		AWhileStm result = new AWhileStm();
		initStatement(result,token);
		result.setExp(exp);
		result.setStatement(body);
		return result;
	}

	private static void initStatement(PStm result, LexLocation token) {
		result.setLocation(token);
		token.executable(true);
	}

	public static AForAllStm newAForAllStm(LexLocation token, PPattern pattern, PExp set,
			PStm stmt) {
		AForAllStm result = new AForAllStm();
		initStatement(result, token);
		
		result.setPattern(pattern);
		result.setSet(set);
		result.setStatement(stmt);
		return null;
	}

	public static AForPatternBindStm newAForPatternBindStm(LexLocation token,
			ADefPatternBind pb, boolean reverse, PExp exp, PStm body) {
		AForPatternBindStm result = new AForPatternBindStm();
		initStatement(result, token);
		
		result.setPatternBind(pb);
		result.setReverse(reverse);
		result.setExp(exp);
		result.setStatement(body);
		return result;
	}

	public static AForIndexStm newAForIndexStm(LexLocation token,
			LexNameToken var, PExp from, PExp to, PExp by, PStm body) {
		
		AForIndexStm result = new AForIndexStm();
		initStatement(result, token);
		
		result.setVar(var);
		result.setFrom(from);
		result.setTo(to);
		result.setBy(by);
		result.setStatement(body);
		
		return result;
	}

	public static AIfStm newAIfStm(LexLocation token, PExp ifExp, PStm thenStmt,
			List<AElseIfStm> elseIfList, PStm elseStmt) {
		
		AIfStm result = new AIfStm();
		initStatement(result, token);
		
		result.setIfExp(ifExp);
		result.setThenStm(thenStmt);
		result.setElseIf(elseIfList);
		result.setElseStm(elseStmt);
		
		return result;
	}

	public static AElseIfStm newAElseIfStm(LexLocation token, PExp elseIfExp,
			PStm thenStmt) {
		AElseIfStm result = new AElseIfStm();
		initStatement(result, token);
		
		result.setElseIf(elseIfExp);
		result.setThenStm(thenStmt);
		
		return result;
	}

	public static AAssignmentStm newAAssignmentStm(LexLocation token,
			PStateDesignator target, PExp exp) {
		AAssignmentStm result = new AAssignmentStm();
		initStatement(result, token);
		
		result.setExp(exp);
		result.setTarget(target);
		return result;
	}

	public static AFieldStateDesignator newAFieldStateDesignator(
			PStateDesignator object, LexIdentifierToken field) {
		AFieldStateDesignator result = new AFieldStateDesignator();
		initStateDesignator(result,object.getLocation());
		
		result.setObject(object);
		result.setField(field);
		
		return result;
	}

	private static void initStateDesignator(PStateDesignator result,
			LexLocation location) {
		result.setLocation(location);
	}

	public static AMapSeqStateDesignator newAMapSeqStateDesignator(
			PStateDesignator mapseq, PExp exp) {
		AMapSeqStateDesignator result = new AMapSeqStateDesignator();
		initStateDesignator(result, mapseq.getLocation());
		
		result.setMapseq(mapseq);
		result.setExp(exp);
		return result;
	}

	public static ABlockSimpleBlockStm newABlockSimpleBlockStm(
			LexLocation token, List<PDefinition> assignmentDefs) {
		ABlockSimpleBlockStm result = new ABlockSimpleBlockStm();
		initStatement(result, token);
		
		result.setAssignmentDefs(assignmentDefs);
		return result;
	}

	public static AAssignmentDefinition newAAssignmentDefinition(
			LexNameToken name, PType type, PExp exp) {
		AAssignmentDefinition result = new AAssignmentDefinition();
		initDefinition(result,Pass.VALUES,name.location,name,NameScope.STATE);
		
		result.setType(type);
		result.setExpression(exp);
		result.getLocation().executable(false);
		return result;
	}

	private static void initDefinition(PDefinition result,
			Pass values, LexLocation location, LexNameToken name, NameScope scope) {
		result.setPass(values);
		result.setLocation(location);
		result.setName(name);
		result.setNameScope(scope);
		result.setAccess(PAccessSpecifierAssistant.getDefault());
	}

	public static AReturnStm newAReturnStm(LexLocation token, PExp exp) {
		AReturnStm result = new AReturnStm();
		initStatement(result, token);
		
		result.setExpression(exp);
		return result;
	}

	public static PStm newAReturnStm(LexLocation token) {
		AReturnStm result = new AReturnStm();
		initStatement(result, token);
		
		result.setExpression(null);
		return result;
	}

	public static ADefLetDefStm newADefLetDefStm(LexLocation token,
			List<PDefinition> localDefs, PStm readStatement) {
		ADefLetDefStm result = new ADefLetDefStm();
		initStatement(result, token);
		
		result.setLocalDefs(localDefs);
		result.setStatement(readStatement);
		return result;
	}

	public static ALetBeStStm newALetBeStStm(LexLocation token,
			PMultipleBind bind, PExp stexp, PStm statement) {
		ALetBeStStm result = new ALetBeStStm();
		initStatement(result, token);
		
		result.setBind(bind);
		result.setSuchThat(stexp);
		result.setStatement(statement);
		
		return result;
	}

	public static ACasesStm newACasesStm(LexLocation token, PExp exp,
			List<ACaseAlternativeStm> cases, PStm others) {
		ACasesStm result = new ACasesStm();
		initStatement(result, token);
		
		result.setExp(exp);
		result.setCases(cases);
		result.setOthers(others);
		
		return result;
	}

	public static ACaseAlternativeStm newACaseAlternativeStm(PPattern pattern,
			PStm stmt) {
		ACaseAlternativeStm result = new ACaseAlternativeStm();
		result.setLocation(pattern.getLocation());
		result.setPattern(pattern);
		result.setResult(stmt);
		return result;	
	}

	public static AStartStm newAStartStm(LexLocation location, PExp obj) {
		AStartStm result = new AStartStm();
		initStatement(result, location);
		
		result.setObj(obj);
		return result;
	}

	public static ADurationStm newADurationStm(LexLocation location, PExp duration,
			PStm stmt) {
		ADurationStm result = new ADurationStm();
		initStatement(result, location);
		
		result.setDuration(duration);
		result.setStatement(stmt);
		return result;
	}

	public static ACyclesStm newACyclesStm(LexLocation location, PExp duration,
			PStm stmt) {
		ACyclesStm result = new ACyclesStm();
		initStatement(result, location);
		
		result.setCycles(duration);
		result.setStatement(stmt);
		
		return result;
	}

	public static AUnionType newAUnionType(LexLocation location, PType a,
			PType b) {
		AUnionType result = new AUnionType();
		result.setLocation(location);
		
		List<PType> list = new Vector<PType>();
		list.add(a);
		list.add(b);
		result.setTypes(list);
		result.setProdCard(-1);
		AUnionTypeAssistant.expand(result);
		return result;
	}

	public static AFieldField newAFieldField(LexNameToken tagname, String tag,
			PType type, boolean equalityAbstraction) {
		AFieldField result = new AFieldField();
		
		result.setAccess(null);
		result.setTagname(tagname);
		result.setTag(tag);
		result.setType(type);
		result.setEqualityAbstraction(equalityAbstraction);
		
		return result;
	}

	public static AMapMapType newAMapMapType(LexLocation location, PType from,
			PType to) {
		
		AMapMapType result = new AMapMapType();
		result.setLocation(location);
		result.setFrom(from);
		result.setTo(to);
		result.setEmpty(false);
		
		return result;
	}

	public static AInMapMapType newAInMapMapType(LexLocation location, PType from,
			PType to) {
		AInMapMapType result = new AInMapMapType();
		result.setLocation(location);
		result.setFrom(from);
		result.setTo(to);
		result.setEmpty(false);
		
		return result;
	}

	public static ASetType newASetType(LexLocation location, PType type) {
		ASetType result = new ASetType();
		
		result.setLocation(location);
		result.setSetof(type);
		result.setEmpty(false);
		
		return result;
	}

	public static ASeqSeqType newASeqSeqType(LexLocation location, PType type) {
		ASeqSeqType result = new ASeqSeqType();
		
		result.setLocation(location);
		result.setSeqof(type);
		result.setEmpty(false);
		
		return result;
	}

	public static ASeq1SeqType newASeq1SeqType(LexLocation location, PType type) {
		ASeq1SeqType result = new ASeq1SeqType();
		
		result.setLocation(location);
		result.setSeqof(type);
		result.setEmpty(false);
		
		return result;
	}

	public static AQuoteType newAQuoteType(LexQuoteToken token) {
		AQuoteType result = new AQuoteType();
		result.setLocation(token.location);
		result.setValue(token);
		
		return result;
	}

	public static ABracketType newABracketType(LexLocation location, PType type) {
		ABracketType result = new ABracketType();
		result.setLocation(location);
		result.setType(type);
		
		return result;
	}

	public static AOptionalType newAOptionalType(LexLocation location, PType type) {
		AOptionalType result = new AOptionalType();
		result.setLocation(location);
		
		while (type instanceof AOptionalType)
		{
			type = ((AOptionalType)type).getType();
		}

		result.setType(type);
		return result;
	}

	public static AUnresolvedType newAUnresolvedType(LexNameToken typename) {
		AUnresolvedType result = new AUnresolvedType();
		result.setLocation(typename.location);
		result.setName(typename);
		
		return result;
	}

	public static AParameterType newAParameterType(LexNameToken name) {
		AParameterType result = new AParameterType();
		result.setLocation(name.location);
		result.setName(name);
		return result;
	}

	public static AOperationType newAOperationType(LexLocation location) {
		AOperationType result = new AOperationType();
		result.setLocation(location);
		result.setParameters(new Vector<PType>());
		result.setResult(AstFactory.newAVoidType(location));
		
		return result;
	}

	public static AClassInvariantStm newAClassInvariantStm(LexNameToken name,
			List<PDefinition> invdefs) {
		AClassInvariantStm result = new AClassInvariantStm();
		initStatement(result, name.location);
		
		result.setName(name);
		result.setInvDefs(invdefs);
		name.location.executable(false);
		
		return result;
	}

	public static AInheritedDefinition newAInheritedDefinition(
			LexNameToken localname, PDefinition d) {
		AInheritedDefinition result = new AInheritedDefinition();
		initDefinition(result, d.getPass(), d.getLocation(), localname, d.getNameScope());
		
		result.setSuperdef(d);
		result.setOldname(localname.getOldName());
		
		result.setClassDefinition(d.getClassDefinition());
		result.setAccess(d.getAccess().clone());
		
		return result;
	}

	public static AImportedDefinition newAImportedDefinition(LexLocation location,
			PDefinition d) {
		AImportedDefinition result = new AImportedDefinition();
		initDefinition(result, Pass.DEFS, location, d.getName(), d.getNameScope());
		result.setDef(d);
		
		return result;
	}

	public static ARenamedDefinition newARenamedDefinition(LexNameToken name,
			PDefinition def) {
		ARenamedDefinition result = new ARenamedDefinition();
		initDefinition(result, def.getPass() , name.location, name, def.getNameScope());
		result.setDef(def);
		
		return result;
	}

	public static AClassClassDefinition newAClassClassDefinition() {
		AClassClassDefinition result = AstFactory.newAClassClassDefinition(
				new LexNameToken("CLASS", "DEFAULT", new LexLocation()),
				new LexNameList(), 
				new Vector<PDefinition>());
		//TODO: missing types in AClassClassDefinition
//		privateStaticValues = new NameValuePairMap();
//		publicStaticValues = new NameValuePairMap();
		return result;
	}

	public static AClassType newAClassType(LexLocation location,
			SClassDefinition classdef) {
		AClassType result = new AClassType();
		result.setLocation(location);
		
		result.setClassdef(classdef);
		result.setName(classdef.getName());
		
		return result;
	}

	public static AMapMapType newAMapMapType(LexLocation location) {
		AMapMapType result = new AMapMapType();
		result.setLocation(location);
		
		result.setFrom(AstFactory.newAUnknownType(location));
		result.setTo(AstFactory.newAUnknownType(location));
		result.setEmpty(true);
		
		return result;
	}

	public static ASetType newASetType(LexLocation location) {
		ASetType result = new ASetType();
		result.setLocation(location);
		
		result.setSetof(AstFactory.newAUnknownType(location));
		result.setEmpty(true);
		
		return result;
	}



	public static ASeqSeqType newASeqSeqType(LexLocation location) {
		ASeqSeqType result = new ASeqSeqType();
		result.setLocation(location);
		result.setSeqof(AstFactory.newAUnknownType(location));
		result.setEmpty(true);
		
		return result;
	}

	public static ARecordInvariantType newARecordInvariantType(LexLocation location,
			List<AFieldField> fields) {
		ARecordInvariantType result = new ARecordInvariantType();
		result.setLocation(location);
		
		result.setName(new LexNameToken("?", "?", location));
		result.setFields(fields);
		
		return result;
	}

	public static AExternalDefinition newAExternalDefinition(PDefinition state,
			LexToken mode) {
		AExternalDefinition result = new AExternalDefinition();
		initDefinition(result, Pass.DEFS, state.getLocation(), state.getName(), NameScope.STATE);
		
		result.setState(state);
		result.setReadOnly(mode.is(VDMToken.READ));
		result.setOldname(result.getReadOnly() ? null : state.getName().getOldName());
		
		return result;
	}

	public static AMultiBindListDefinition newAMultiBindListDefinition(
			LexLocation location, List<PMultipleBind> bindings) {
		AMultiBindListDefinition result = new AMultiBindListDefinition();
		initDefinition(result, Pass.DEFS, location, null, null);
		result.setBindings(bindings);
		return result;
	}

	public static AUndefinedType newAUndefinedType(LexLocation location) {
		AUndefinedType result = new AUndefinedType();
		result.setLocation(location);
		return result;
	}

	public static AVoidReturnType newAVoidReturnType(LexLocation location) {
		AVoidReturnType result = new AVoidReturnType();
		result.setLocation(location);
		return result;
	}

	public static AUnaryPlusUnaryExp newAUnaryPlusUnaryExp(LexLocation location,
			PExp exp) {
		AUnaryPlusUnaryExp result = new AUnaryPlusUnaryExp();
		initExpressionUnary(result,location,exp);
		return result;
	}

	
	
	private static void initExpressionUnary(SUnaryExp result,
			LexLocation location, PExp exp) {
		initExpression(result,location);
		result.setExp(exp);
	}

	private static void initExpression(PExp result, LexLocation location) {
		result.setLocation(location);
		location.executable(true);
	}

	public static AUnaryMinusUnaryExp newAUnaryMinusUnaryExp(LexLocation location,
			PExp exp) {
		AUnaryMinusUnaryExp result = new AUnaryMinusUnaryExp();
		initExpressionUnary(result,location,exp);
		return result;
	}

	public static ACardinalityUnaryExp newACardinalityUnaryExp(LexLocation location,
			PExp exp) {
		ACardinalityUnaryExp result = new ACardinalityUnaryExp();
		initExpressionUnary(result,location,exp);
		return result;
	}

	public static AMapDomainUnaryExp newAMapDomainUnaryExp(LexLocation location,
			PExp exp) {
		AMapDomainUnaryExp result = new AMapDomainUnaryExp();
		initExpressionUnary(result,location,exp);
		return result;
	}

	public static ALenUnaryExp newALenUnaryExp(LexLocation location,
			PExp exp) {
		ALenUnaryExp result = new ALenUnaryExp();
		initExpressionUnary(result,location,exp);
		return result;
	}

	public static APowerSetUnaryExp newAPowerSetUnaryExp(LexLocation location,
			PExp exp) {
		APowerSetUnaryExp result = new APowerSetUnaryExp();
		initExpressionUnary(result,location,exp);
		return result;
	}

	public static AMapRangeUnaryExp newAMapRangeUnaryExp(LexLocation location,
			PExp exp) {
		AMapRangeUnaryExp result = new AMapRangeUnaryExp();
		initExpressionUnary(result,location,exp);
		return result;
	}

	public static AElementsUnaryExp newAElementsUnaryExp(LexLocation location,
			PExp exp) {
		AElementsUnaryExp result = new AElementsUnaryExp();
		initExpressionUnary(result,location,exp);
		return result;
	}

	public static AAbsoluteUnaryExp newAAbsoluteUnaryExp(LexLocation location,
			PExp exp) {
		AAbsoluteUnaryExp result = new AAbsoluteUnaryExp();
		initExpressionUnary(result,location,exp);
		return result;
	}

	public static ADistIntersectUnaryExp newADistIntersectUnaryExp(LexLocation location,
			PExp exp) {
		ADistIntersectUnaryExp result = new ADistIntersectUnaryExp();
		initExpressionUnary(result,location,exp);
		return result;
	}

	public static ADistMergeUnaryExp newADistMergeUnaryExp(LexLocation location,
			PExp exp) {
		ADistMergeUnaryExp result = new ADistMergeUnaryExp();
		initExpressionUnary(result,location,exp);
		return result;
	}

	public static AHeadUnaryExp newAHeadUnaryExp(LexLocation location,
			PExp exp) {
		AHeadUnaryExp result = new AHeadUnaryExp();
		initExpressionUnary(result,location,exp);
		return result;
	}

	public static ATailUnaryExp newATailUnaryExp(LexLocation location,
			PExp exp) {
		ATailUnaryExp result = new ATailUnaryExp();
		initExpressionUnary(result,location,exp);
		return result;
	}

	public static AReverseUnaryExp newAReverseUnaryExp(LexLocation location,
			PExp exp) {
		AReverseUnaryExp result = new AReverseUnaryExp();
		initExpressionUnary(result,location,exp);
		return result;
	}

	public static AFloorUnaryExp newAFloorUnaryExp(LexLocation location,
			PExp exp) {
		AFloorUnaryExp result = new AFloorUnaryExp();
		initExpressionUnary(result,location,exp);
		return result;
	}

	public static ADistUnionUnaryExp newADistUnionUnaryExp(LexLocation location,
			PExp exp) {
		ADistUnionUnaryExp result = new ADistUnionUnaryExp();
		initExpressionUnary(result,location,exp);
		return result;
	}

	public static ADistConcatUnaryExp newADistConcatUnaryExp(LexLocation location,
			PExp exp) {
		ADistConcatUnaryExp result = new ADistConcatUnaryExp();
		initExpressionUnary(result,location,exp);
		return result;
	}

	public static AIndicesUnaryExp newAIndicesUnaryExp(LexLocation location,
			PExp exp) {
		AIndicesUnaryExp result = new AIndicesUnaryExp();
		initExpressionUnary(result,location,exp);
		return result;
	}

	public static ASetEnumSetExp newASetEnumSetExp(LexLocation start,
			List<PExp> members) {
		ASetEnumSetExp result = new ASetEnumSetExp();
		initExpression(result, start);
		result.setMembers(members);
		
		return result;
	}

	public static AIdentifierStateDesignator newAIdentifierStateDesignator(
			LexNameToken name) {
		AIdentifierStateDesignator result = new AIdentifierStateDesignator();
		initStateDesignator(result, name.location);
		result.setName(name);
		return result;
	}

	public static AErrorStm newAErrorStm(LexLocation location) {
		AErrorStm result = new AErrorStm();
		initStatement(result, location);
		return result;
	}

	public static ASkipStm newASkipStm(LexLocation location) {
		ASkipStm result = new ASkipStm();
		initStatement(result, location);
		return result;
	}

	public static ATixeStmtAlternative newATixeStmtAlternative(
			ADefPatternBind patternBind, PStm resultStm) {
		ATixeStmtAlternative result = new ATixeStmtAlternative();
		result.setPatternBind(patternBind);
		result.setStatement(resultStm);
		return result;
	}

	public static APostOpExp newAPostOpExp(LexNameToken opname,
			PExp preexpression, PExp postexpression, List<AErrorCase> errors,
			AStateDefinition state) {
		APostOpExp result = new APostOpExp();
		initExpression(result, postexpression.getLocation());
		
		result.setOpname(opname);
		result.setPreexpression(preexpression);
		result.setPostexpression(postexpression);
		result.setErrors(errors);
		result.setState(state);
		
		return result;
	}

	public static APreOpExp newAPreOpExp(LexNameToken opname, PExp expression,
			List<AErrorCase> errors, AStateDefinition state) {
		APreOpExp result = new APreOpExp();
		initExpression(result, expression);
		
		result.setOpname(opname);
		result.setExpression(expression);
		result.setErrors(errors);
		result.setState(state);
		
		return result;
	}

	private static void initExpression(PExp result, PExp expression) {
		initExpression(result, expression.getLocation());
		
	}

	public static AUnionType newAUnionType(LexLocation location, PTypeList types) {
		AUnionType result = new AUnionType();
		initType(result,location);
		result.setTypes(types);
		AUnionTypeAssistant.expand(result);
		return result;
	}

	private static void initType(PType result, LexLocation location) {
		result.setLocation(location);
	}

	public static AStateInitExp newAStateInitExp(AStateDefinition state) {
		AStateInitExp result = new AStateInitExp();
		initExpression(result, state.getLocation());
		result.setState(state);
		result.getLocation().executable(false);
		return result;
	}
	
	
	
	

	
}
