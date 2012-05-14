package org.overture.ast.factory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.ALocalDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.ASystemClassDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.AUntypedDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.definitions.assistants.PAccessSpecifierAssistant;
import org.overture.ast.definitions.assistants.PDefinitionAssistant;
import org.overture.ast.expressions.PExp;
import org.overture.ast.patterns.ADefPatternBind;
import org.overture.ast.patterns.APatternListTypePair;
import org.overture.ast.patterns.APatternTypePair;
import org.overture.ast.patterns.ASetBind;
import org.overture.ast.patterns.ASetMultipleBind;
import org.overture.ast.patterns.ATypeBind;
import org.overture.ast.patterns.ATypeMultipleBind;
import org.overture.ast.patterns.PBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.patterns.assistants.PPatternAssistant;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SInvariantType;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.messages.InternalException;
import org.overturetool.vdmj.typechecker.ClassDefinitionSettings;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.typechecker.Pass;


public class AstFactory {

	public static ADefPatternBind createADefPatternBind(LexLocation location,
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

	public static ASetBind createASetBind(PPattern pattern, PExp readExpression) {
		ASetBind result = new ASetBind();
		
		result.setLocation(pattern.getLocation());
		result.setPattern(pattern);
		result.setSet(readExpression);
		
		return result;
	}

	public static ATypeBind createATypeBind(PPattern pattern, PType readType) {
		ATypeBind result = new ATypeBind();
		
		result.setLocation(pattern.getLocation());
		result.setPattern(pattern);
		result.setType(readType);
		
		return result;
	}

	public static ASetMultipleBind createASetMultipleBind(List<PPattern> plist,
			PExp readExpression) {
		ASetMultipleBind result = new ASetMultipleBind();
		
		result.setLocation(plist.get(0).getLocation());
		result.setPlist(plist);
		result.setSet(readExpression);
				
		return result;
	}

	public static ATypeMultipleBind createATypeMultipleBind(List<PPattern> plist,
			PType readType) {
		ATypeMultipleBind result = new ATypeMultipleBind();
		
		result.setLocation(plist.get(0).getLocation());
		result.setPlist(plist);
		result.setType(readType);
		
		return result;
	}

	public static AClassClassDefinition createAClassClassDefinition(
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

	public static ASystemClassDefinition createASystemClassDefinition(
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

	public static ANamedInvariantType createANamedInvariantType(
			LexNameToken typeName, PType type) {

		ANamedInvariantType result = new ANamedInvariantType();
		
		result.setLocation(typeName.location);
		result.setName(typeName);
		result.setType(type);
		
		return result;
	}

	public static ARecordInvariantType createARecordInvariantType(
			LexNameToken name, List<AFieldField> fields) {
		
		ARecordInvariantType result = new ARecordInvariantType();
		
		result.setLocation(name.location);
		result.setName(name);
		result.setFields(fields);
		
		return result;
	}

	public static ATypeDefinition createATypeDefinition(LexNameToken name,
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

	public static AExplicitFunctionDefinition createAExplicitFunctionDefinition(LexNameToken name,
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

	public static AImplicitFunctionDefinition createAImplicitFunctionDefinition(
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
		AFunctionType type = AstFactory.createAFunctionType(result.getLocation(), false, ptypes, resultPattern.getType());// AFunctionType(funcName.location, false, null, false, ptypes, (PType) resultPattern.getType());
		
		List<PDefinition> defs = new Vector<PDefinition>();
		defs.add(result);
		type.setDefinitions(defs);
		
		return result;
	}
	
	private static AFunctionType createAFunctionType(LexLocation location,
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
			// type.parent(null);//Create new type not in the tree yet.
			list.add(type);
		}

		return list;
	}

	public static AValueDefinition createAValueDefinition(PPattern p,
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
			defs.add(AstFactory.createAUntypedDefinition(result.getLocation(), var, scope));
		}
		
		result.setDefs(defs);
		
		return result;
	}

	public static PDefinition createAUntypedDefinition(LexLocation location,
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

	public static AStateDefinition createAStateDefinition(LexNameToken name,
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
			stateDefs.add(AstFactory.createALocalDefinition(f.getTagname().location, f.getTagname(), NameScope.STATE, f.getType()));
			ALocalDefinition ld = AstFactory.createALocalDefinition(f.getTagname().location,
					f.getTagname().getOldName(), NameScope.OLDSTATE, f.getType()); 

			ld.setUsed(true);  // Else we moan about unused ~x names
			stateDefs.add(ld);
		}
		
		result.setRecordType(AstFactory.createARecordInvariantType(name, fields));
		
		ALocalDefinition recordDefinition = null;
		
		recordDefinition = AstFactory.createALocalDefinition(result.getLocation(), name, NameScope.STATE, result.getRecordType());
		recordDefinition.setUsed(true);  // Can't be exported anyway
		stateDefs.add(recordDefinition);

		recordDefinition = AstFactory.createALocalDefinition(result.getLocation(), name.getOldName(), NameScope.OLDSTATE, result.getRecordType());
		recordDefinition.setUsed(true); // Can't be exported anyway
		stateDefs.add(recordDefinition);
		
		return result;
	}

	private static ALocalDefinition createALocalDefinition(LexLocation location,
			LexNameToken name, NameScope state, PType type) {
		
		ALocalDefinition result = new ALocalDefinition();
		// Definition initialization
		result.setPass(Pass.DEFS);
		result.setLocation(name.location);
		result.setName(name);
		result.setNameScope(NameScope.STATE);
		result.setAccess(PAccessSpecifierAssistant.getDefault());
		
		result.setType(type);
		// TODO Auto-generated method stub
		return result;
	}

}
