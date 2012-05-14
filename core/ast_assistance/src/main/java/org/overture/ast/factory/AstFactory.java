package org.overture.ast.factory;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.AClassInvariantDefinition;
import org.overture.ast.definitions.AEqualsDefinition;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.ALocalDefinition;
import org.overture.ast.definitions.AMutexSyncDefinition;
import org.overture.ast.definitions.ANamedTraceDefinition;
import org.overture.ast.definitions.APerSyncDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.ASystemClassDefinition;
import org.overture.ast.definitions.AThreadDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.AUntypedDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
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
import org.overture.ast.expressions.AAndBooleanBinaryExp;
import org.overture.ast.expressions.AApplyExp;
import org.overture.ast.expressions.ABooleanConstExp;
import org.overture.ast.expressions.ACharLiteralExp;
import org.overture.ast.expressions.ACompBinaryExp;
import org.overture.ast.expressions.ADivNumericBinaryExp;
import org.overture.ast.expressions.ADivideNumericBinaryExp;
import org.overture.ast.expressions.ADomainResByBinaryExp;
import org.overture.ast.expressions.ADomainResToBinaryExp;
import org.overture.ast.expressions.AEqualsBinaryExp;
import org.overture.ast.expressions.AEquivalentBooleanBinaryExp;
import org.overture.ast.expressions.AFieldExp;
import org.overture.ast.expressions.AFieldNumberExp;
import org.overture.ast.expressions.AFuncInstatiationExp;
import org.overture.ast.expressions.AGreaterEqualNumericBinaryExp;
import org.overture.ast.expressions.AGreaterNumericBinaryExp;
import org.overture.ast.expressions.AImpliesBooleanBinaryExp;
import org.overture.ast.expressions.AInSetBinaryExp;
import org.overture.ast.expressions.AIntLiteralExp;
import org.overture.ast.expressions.AIsExp;
import org.overture.ast.expressions.ALessEqualNumericBinaryExp;
import org.overture.ast.expressions.ALessNumericBinaryExp;
import org.overture.ast.expressions.AMapInverseUnaryExp;
import org.overture.ast.expressions.AMapUnionBinaryExp;
import org.overture.ast.expressions.AMkBasicExp;
import org.overture.ast.expressions.AMkTypeExp;
import org.overture.ast.expressions.AModNumericBinaryExp;
import org.overture.ast.expressions.AMuExp;
import org.overture.ast.expressions.ANilExp;
import org.overture.ast.expressions.ANotEqualBinaryExp;
import org.overture.ast.expressions.ANotInSetBinaryExp;
import org.overture.ast.expressions.ANotUnaryExp;
import org.overture.ast.expressions.ANotYetSpecifiedExp;
import org.overture.ast.expressions.AOrBooleanBinaryExp;
import org.overture.ast.expressions.APlusNumericBinaryExp;
import org.overture.ast.expressions.APlusPlusBinaryExp;
import org.overture.ast.expressions.AProperSubsetBinaryExp;
import org.overture.ast.expressions.AQuoteLiteralExp;
import org.overture.ast.expressions.ARangeResByBinaryExp;
import org.overture.ast.expressions.ARangeResToBinaryExp;
import org.overture.ast.expressions.ARealLiteralExp;
import org.overture.ast.expressions.ARecordModifier;
import org.overture.ast.expressions.ARemNumericBinaryExp;
import org.overture.ast.expressions.ASelfExp;
import org.overture.ast.expressions.ASeqConcatBinaryExp;
import org.overture.ast.expressions.ASetDifferenceBinaryExp;
import org.overture.ast.expressions.ASetIntersectBinaryExp;
import org.overture.ast.expressions.ASetUnionBinaryExp;
import org.overture.ast.expressions.AStarStarBinaryExp;
import org.overture.ast.expressions.AStringLiteralExp;
import org.overture.ast.expressions.ASubclassResponsibilityExp;
import org.overture.ast.expressions.ASubseqExp;
import org.overture.ast.expressions.ASubsetBinaryExp;
import org.overture.ast.expressions.ASubstractNumericBinaryExp;
import org.overture.ast.expressions.AThreadIdExp;
import org.overture.ast.expressions.ATimeExp;
import org.overture.ast.expressions.ATimesNumericBinaryExp;
import org.overture.ast.expressions.ATupleExp;
import org.overture.ast.expressions.AUndefinedExp;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.patterns.ADefPatternBind;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.APatternListTypePair;
import org.overture.ast.patterns.APatternTypePair;
import org.overture.ast.patterns.ASetBind;
import org.overture.ast.patterns.ASetMultipleBind;
import org.overture.ast.patterns.ATuplePattern;
import org.overture.ast.patterns.ATypeBind;
import org.overture.ast.patterns.ATypeMultipleBind;
import org.overture.ast.patterns.PBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.patterns.assistants.PPatternAssistant;
import org.overture.ast.statements.AErrorCase;
import org.overture.ast.statements.AExternalClause;
import org.overture.ast.statements.APeriodicStm;
import org.overture.ast.statements.ASpecificationStm;
import org.overture.ast.statements.PStm;
import org.overture.ast.types.AAccessSpecifierAccessSpecifier;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.ACharBasicType;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AIntNumericBasicType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.ANatNumericBasicType;
import org.overture.ast.types.ANatOneNumericBasicType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.ARationalNumericBasicType;
import org.overture.ast.types.ARealNumericBasicType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.ATokenBasicType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.AVoidType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SBasicType;
import org.overture.ast.types.SInvariantType;
import org.overturetool.util.ClonableString;
import org.overturetool.vdmj.lex.LexBooleanToken;
import org.overturetool.vdmj.lex.LexCharacterToken;
import org.overturetool.vdmj.lex.LexIdentifierToken;
import org.overturetool.vdmj.lex.LexIntegerToken;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.lex.LexQuoteToken;
import org.overturetool.vdmj.lex.LexRealToken;
import org.overturetool.vdmj.lex.LexStringToken;
import org.overturetool.vdmj.lex.LexToken;
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
		result.setPass(Pass.DEFS);
		result.setLocation(className.location);
		result.setName(className);
		result.setNameScope(NameScope.CLASSNAME);
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
		PDefinitionAssistant.setClassDefinition(result,result);
		
		//others
		result.setSettingHierarchy(ClassDefinitionSettings.UNSET);
		
		return result;
	}

	public static ASystemClassDefinition newASystemClassDefinition(
			LexNameToken className, List<PDefinition> members) {
		ASystemClassDefinition result = new ASystemClassDefinition();
		
		result.setPass(Pass.DEFS);
		result.setLocation(className.location);
		result.setName(className);
		result.setNameScope(NameScope.CLASSNAME);
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
		
		result.setPass(Pass.TYPES);
		result.setLocation(name.location);
		result.setName(name);
		result.setNameScope(NameScope.TYPENAME);
		result.setAccess(PAccessSpecifierAssistant.getDefault());
		
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
		result.setPass(Pass.DEFS);
		result.setLocation(name.location);
		result.setName(name);
		result.setNameScope(scope);
		result.setAccess(PAccessSpecifierAssistant.getPublic());
		
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
		result.setPass(Pass.DEFS);
		result.setLocation(name.location);
		result.setName(name);
		result.setNameScope(scope);
		result.setAccess(PAccessSpecifierAssistant.getPublic());
		
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
	
	private static AFunctionType newAFunctionType(LexLocation location,
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
		result.setPass(Pass.VALUES);
		result.setLocation(p.getLocation());
		result.setName(null);
		result.setNameScope(scope);
		result.setAccess(PAccessSpecifierAssistant.getDefault());
		
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
		result.setPass(Pass.DEFS);
		result.setLocation(location);
		result.setName(name);
		result.setNameScope(scope);
		result.setAccess(PAccessSpecifierAssistant.getDefault());
		
		return result;
	}

	public static AStateDefinition newAStateDefinition(LexNameToken name,
			List<AFieldField> fields, PPattern invPattern,
			PExp invExpression, PPattern initPattern, PExp initExpression) {
		
		AStateDefinition result = new AStateDefinition();
		// Definition initialization
		result.setPass(Pass.TYPES);
		result.setLocation(name.location);
		result.setName(name);
		result.setNameScope(NameScope.STATE);
		result.setAccess(PAccessSpecifierAssistant.getDefault());
		
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

	private static ALocalDefinition newALocalDefinition(LexLocation location,
			LexNameToken name, NameScope state, PType type) {
		
		ALocalDefinition result = new ALocalDefinition();
		// Definition initialization
		result.setPass(Pass.DEFS);
		result.setLocation(name.location);
		result.setName(name);
		result.setNameScope(NameScope.STATE);
		result.setAccess(PAccessSpecifierAssistant.getDefault());
		
		result.setType(type);
		
		return result;
	}

	public static PDefinition newAExplicitOperationDefinition(
			LexNameToken name, AOperationType type,
			List<PPattern> parameters, PExp precondition, PExp postcondition,
			PStm body) {
		
		AExplicitOperationDefinition result = new AExplicitOperationDefinition();
		// Definition initialization
		result.setPass(Pass.DEFS);
		result.setLocation(name.location);
		result.setName(name);
		result.setNameScope(NameScope.GLOBAL);
		result.setAccess(PAccessSpecifierAssistant.getDefault());
		
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
		result.setPass(Pass.DEFS);
		result.setLocation(name.location);
		result.setName(name);
		result.setNameScope(NameScope.GLOBAL);
		result.setAccess(PAccessSpecifierAssistant.getDefault());
		
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

	private static AOperationType newAOperationType(LexLocation location,
			List<PType> parameters, PType resultType) {
		AOperationType result = new AOperationType();
		
		result.setLocation(location);
		result.setParameters(parameters);
		result.setResult(resultType);
		
		return result;
	}

	private static AVoidType newAVoidType(LexLocation location) {
		AVoidType result = new AVoidType();
		result.setLocation(location);
		return result;
	}

	public static ASpecificationStm newASpecificationStm(
			LexLocation location, List<AExternalClause> externals,
			PExp precondition, PExp postcondition, List<AErrorCase> errors) {
		
		ASpecificationStm result = new ASpecificationStm();
		
		result.setLocation(location);
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

	private static PType newAUnknownType(LexLocation location) {
		AUnknownType result = new AUnknownType();
		result.setLocation(location);
		return result;
	}

	private static AEqualsDefinition newAEqualsDefinition(LexLocation location) {
		AEqualsDefinition result = new AEqualsDefinition();
		// Definition initialization
		result.setPass(Pass.DEFS);
		result.setLocation(location);
		result.setName(null);
		result.setNameScope(NameScope.LOCAL);
		result.setAccess(PAccessSpecifierAssistant.getDefault());
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
		result.setPass(Pass.DEFS);
		result.setLocation(name.location);
		result.setName(name);
		result.setNameScope(NameScope.GLOBAL);
		result.setAccess(PAccessSpecifierAssistant.getDefault());
		
		result.setExpression(expression);
		
		return result;
	}

	public static AInstanceVariableDefinition newAInstanceVariableDefinition(
			LexNameToken name, PType type, PExp expression) {
		AInstanceVariableDefinition result = new AInstanceVariableDefinition();
		
		// Definition initialization
		result.setPass(Pass.VALUES);
		result.setLocation(name.location);
		result.setName(name);
		result.setNameScope(NameScope.STATE);
		result.setAccess(PAccessSpecifierAssistant.getDefault());
		
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
		result.setPass(Pass.DEFS);
		result.setLocation(statement.getLocation());
		result.setName(null);
		result.setNameScope(NameScope.GLOBAL);
		result.setAccess(PAccessSpecifierAssistant.getDefault());
		
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
		result.setLocation(opname.location);
		//this.breakpoint = new Breakpoint(location);
		result.getLocation().executable(true);
		
		result.setOpname(opname);
		result.setArgs(args);
		
		return result;
	}

	public static APerSyncDefinition newAPerSyncDefinition(LexLocation location,
			LexNameToken opname, PExp guard) {
		APerSyncDefinition result = new APerSyncDefinition();
		// Definition initialization
		result.setPass(Pass.DEFS);
		result.setLocation(location);
		result.setName(opname.getPerName(location));
		result.setNameScope(NameScope.GLOBAL);
		result.setAccess(PAccessSpecifierAssistant.getDefault());
		
		result.setOpname(opname);
		result.setGuard(guard);
		
		return result;
	}

	public static AMutexSyncDefinition newAMutexSyncDefinition(LexLocation location,
			LexNameList operations) {
		AMutexSyncDefinition result = new AMutexSyncDefinition();
		// Definition initialization
		result.setPass(Pass.DEFS);
		result.setLocation(location);
		result.setName(null);
		result.setNameScope(NameScope.GLOBAL);
		result.setAccess(PAccessSpecifierAssistant.getDefault());
		
		result.setOperations(operations);
		
		return result;
	}

	public static ANamedTraceDefinition newANamedTraceDefinition(LexLocation location,
			List<String> pathname, List<List<PTraceDefinition>> terms) {
		ANamedTraceDefinition result = new ANamedTraceDefinition();
		// Definition initialization
		result.setPass(Pass.DEFS);
		result.setLocation(location);
		result.setName(new LexNameToken(
				location.module, Utils.listToString(pathname, "_"), location));
		result.setNameScope(NameScope.GLOBAL);
		result.setAccess(PAccessSpecifierAssistant.getDefault());
		
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
	
	
	
	
	
	

	
}
