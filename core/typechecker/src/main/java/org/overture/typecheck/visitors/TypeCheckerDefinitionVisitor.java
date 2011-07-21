package org.overture.typecheck.visitors;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import org.overture.ast.definitions.assistants.AExplicitFunctionDefinitionAssistant;
import org.overture.ast.definitions.assistants.AExplicitOperationDefinitionAssistant;
import org.overture.ast.definitions.assistants.AImplicitFunctionDefinitionAssistant;
import org.overture.ast.definitions.assistants.ALocalDefinitionAssistant;
import org.overture.ast.definitions.assistants.PAccessSpecifierAssistant;
import org.overture.ast.definitions.assistants.PDefinitionAssistant;
import org.overture.ast.definitions.assistants.PDefinitionListAssistant;
import org.overture.ast.definitions.assistants.PMultipleBindAssistant;
import org.overture.ast.definitions.assistants.PTraceDefinitionAssistant;
import org.overture.ast.definitions.assistants.SClassDefinitionAssistant;
import org.overture.ast.definitions.traces.PTraceDefinition;
import org.overture.ast.expressions.ANotYetSpecifiedExp;
import org.overture.ast.expressions.ASubclassResponsibilityExp;
import org.overture.ast.expressions.AUndefinedExp;
import org.overture.ast.node.NodeList;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.APatternListTypePair;
import org.overture.ast.patterns.ATypeBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.patterns.assistants.APatternTypePairAssistant;
import org.overture.ast.patterns.assistants.ATypeBindAssistant;
import org.overture.ast.patterns.assistants.PPatternAssistant;
import org.overture.ast.statements.AErrorCase;
import org.overture.ast.statements.AExternalClause;
import org.overture.ast.statements.ANotYetSpecifiedStm;
import org.overture.ast.statements.ASubclassResponsibilityStm;
import org.overture.ast.statements.assistants.AExternalClauseAssistant;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.ANatNumericBasicType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.AVoidType;
import org.overture.ast.types.EType;
import org.overture.ast.types.PType;
import org.overture.ast.types.assistants.APatternListTypePairAssistant;
import org.overture.ast.types.assistants.PTypeAssistant;
import org.overture.typecheck.Environment;
import org.overture.typecheck.FlatCheckedEnvironment;
import org.overture.typecheck.FlatEnvironment;
import org.overture.typecheck.PrivateClassEnvironment;
import org.overture.typecheck.TypeCheckInfo;
import org.overture.typecheck.TypeChecker;
import org.overture.typecheck.TypeCheckerErrors;
import org.overture.typecheck.TypeComparator;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.lex.VDMToken;
import org.overturetool.vdmj.typechecker.NameScope;



public class TypeCheckerDefinitionVisitor extends
		QuestionAnswerAdaptor<TypeCheckInfo, PType> {

	
	private QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor;
	
	public TypeCheckerDefinitionVisitor(TypeCheckVisitor typeCheckVisitor) {
		this.rootVisitor = typeCheckVisitor;
	}
	
	
	@Override
	public PType caseAAssignmentDefinition(AAssignmentDefinition node,
			TypeCheckInfo question) {
		
		question.qualifiers = null;
		node.setExpType(node.getExpression().apply(rootVisitor, question));
		node.setType(PTypeAssistant.typeResolve(node.getType(), null, rootVisitor, question));

		if (node.getExpType() instanceof AVoidType)
		{
			TypeCheckerErrors.report(3048, "Expression does not return a value",node.getExpression().getLocation(),node.getExpression());
		}

		if (!TypeComparator.compatible(node.getType(), node.getExpType()))
		{
			TypeCheckerErrors.report(3000, "Expression does not match declared type",node.getLocation(),node);
			TypeCheckerErrors.detail2("Declared", node.getType(), "Expression", node.getExpType());
		}
		
		return node.getType();
	}
	
	@Override
	public PType caseAInstanceVariableDefinition(
			AInstanceVariableDefinition node, TypeCheckInfo question) {

		if (node.getExpression() instanceof AUndefinedExp)
		{
			if (PAccessSpecifierAssistant.isStatic(node.getAccess()))
			{
				TypeCheckerErrors.report(3037, "Static instance variable is not initialized: " + node.getName(),node.getLocation(),node);
			}
		}

		// Initializers can reference class members, so create a new env.
		// We set the type qualifier to unknown so that type-based name
		// resolution will succeed.

		Environment cenv = new PrivateClassEnvironment(node.getClassDefinition(), question.env);
		//TODO: I can see this could cause problems
		TypeCheckInfo newQuestion = new TypeCheckInfo(); 
		newQuestion.env = cenv;
		newQuestion.qualifiers = null;
		newQuestion.scope = question.scope;
		//TODO: This should be a call to the assignment definition typecheck but instance is not an subclass of assignment in our tree 		
		node.setExpType(node.getExpression().apply(rootVisitor, newQuestion));
		node.setType(PTypeAssistant.typeResolve(node.getType(), null, rootVisitor, newQuestion));

		if (node.getExpType() instanceof AVoidType)
		{
			TypeCheckerErrors.report(3048, "Expression does not return a value",node.getExpression().getLocation(),node.getExpression());
		}

		if (!TypeComparator.compatible(node.getType(), node.getExpType()))
		{
			TypeCheckerErrors.report(3000, "Expression does not match declared type",node.getLocation(),node);
			TypeCheckerErrors.detail2("Declared", node.getType(), "Expression", node.getExpType());
		}
		
		return node.getType();
		
	}
	
	//TODO: No type check for classes
	
	@Override
	public PType caseAClassInvariantDefinition(AClassInvariantDefinition node,
			TypeCheckInfo question) {
		
		question.qualifiers = null;
		question.scope =  NameScope.NAMESANDSTATE;
		PType type = node.getExpression().apply(rootVisitor, question);

		if (!PTypeAssistant.isType(type,ABooleanBasicType.class))
		{
			TypeCheckerErrors.report(3013, "Class invariant is not a boolean expression",node.getLocation(),node);
		}
		
		node.setType(type);
		return node.getType();
	}
	
	@Override
	public PType caseAEqualsDefinition(AEqualsDefinition node,
			TypeCheckInfo question) {
		
		question.qualifiers = null;
		
		node.setExpType(node.getTest().apply(rootVisitor,question));
		PPattern pattern = node.getPattern();
		
		if (pattern != null)
		{
			PPatternAssistant.typeResolve(pattern, rootVisitor,question);
			node.setDefs(PPatternAssistant.getDefinitions(pattern,node.getExpType(), question.scope));
			node.setDefType(node.getExpType());
		}
		else if (node.getTypebind() != null)
		{
			ATypeBindAssistant.typeResolve(node.getTypebind(),rootVisitor,question);
			ATypeBind typebind = node.getTypebind();
			
			if (!TypeComparator.compatible(typebind.getType(), node.getExpType()))
			{
				TypeCheckerErrors.report(3014, "Expression is not compatible with type bind",typebind.getLocation(),typebind);
			}

			node.setDefType(typebind.getType());	// Effectively a cast
			node.setDefs(PPatternAssistant.getDefinitions(typebind.getPattern(), node.getDefType(), question.scope));
		}
		else
		{
			question.qualifiers = null;
			PType st = node.getSetbind().getSet().apply(rootVisitor, question);

			if (!PTypeAssistant.isSet(st))
			{
				TypeCheckerErrors.report(3015, "Set bind is not a set type?",node.getLocation(),node);
				node.setDefType(node.getExpType());
			}
			else
			{
    			PType setof = PTypeAssistant.getSet(st).getSetof();

    			if (!TypeComparator.compatible(node.getExpType(), setof))
    			{
    				TypeCheckerErrors.report(3016, "Expression is not compatible with set bind",node.getSetbind().getLocation(),node.getSetbind());
    			}

    			node.setDefType(setof);	// Effectively a cast
			}

			PPatternAssistant.typeResolve(node.getSetbind().getPattern(), rootVisitor, question);
			node.setDefs(PPatternAssistant.getDefinitions(node.getSetbind().getPattern(), node.getDefType(), question.scope));
		}

		PDefinitionListAssistant.typeCheck(node.getDefs(), rootVisitor, question);
		return node.getType();
	}
	
	
	@Override
	public PType caseAExplicitFunctionDefinition(
			AExplicitFunctionDefinition node, TypeCheckInfo question) {
		System.out.println("Visiting Explicit Function Def: " + node.getName());
		
		NodeList<PDefinition> defs = new NodeList<PDefinition>(node);

		if (node.getTypeParams() != null)
		{
			defs.addAll(AExplicitFunctionDefinitionAssistant.getTypeParamDefinitions(node));
		}

		PType expectedResult = AExplicitFunctionDefinitionAssistant.checkParams(node,node.getParamPatternList().listIterator(), node.getType());

		List<List<PDefinition>> paramDefinitionList = AExplicitFunctionDefinitionAssistant.getParamDefinitions(node,node.getType(), node.getParamPatternList(),node.getLocation());

		for (List<PDefinition> pdef: paramDefinitionList)
		{
			defs.addAll(pdef);	// All definitions of all parameter lists
		}

		FlatCheckedEnvironment local = new FlatCheckedEnvironment(defs,question.env, question.scope);
		
		local.setStatic(PAccessSpecifierAssistant.isStatic(node.getAccess()));
		local.setEnclosingDefinition(node);

		//building the new scope for subtypechecks
		TypeCheckInfo info = new TypeCheckInfo();
		info.env = local;
		info.scope = question.scope;
		info.qualifiers = question.qualifiers;
		PDefinitionListAssistant.typeCheck(defs,this,info); //can be this because its a definition list

		if (question.env.isVDMPP() && !PAccessSpecifierAssistant.isStatic(node.getAccess())) 
		{
			local.add(PDefinitionAssistant.getSelfDefinition(node));
		}
 
		if (node.getPredef() != null)
		{
			//building the new scope for subtypechecks			
			info.env = local;
			info.scope = NameScope.NAMES;
			info.qualifiers = null;
			PType b = node.getPredef().getBody().apply(rootVisitor, question);
			ABooleanBasicType expected = new ABooleanBasicType(node.getLocation(),false,null);

			if (!PTypeAssistant.isType(b,ABooleanBasicType.class))
			{
				TypeChecker.report(3018, "Precondition returns unexpected type",node.getLocation());
				TypeChecker.detail2("Actual", b, "Expected", expected);
			}
		}

		if (node.getPostdef() != null)
		{
			LexNameToken result = new LexNameToken(node.getName().getModule(), "RESULT", node.getLocation());
			PPattern rp = new AIdentifierPattern(null,null,null, result);
			List<PDefinition> rdefs = PPatternAssistant.getDefinitions(rp,expectedResult, NameScope.NAMES);
			FlatCheckedEnvironment post =
				new FlatCheckedEnvironment(rdefs, local, NameScope.NAMES);

			//building the new scope for subtypechecks
			info.env = post;
			info.scope = NameScope.NAMES;
			info.qualifiers = null;			
			PType b = node.getPostdef().getBody().apply(rootVisitor, info);
			ABooleanBasicType expected = new ABooleanBasicType(node.getLocation(),false,null);

			if (!PTypeAssistant.isType(b,ABooleanBasicType.class))
			{
				TypeChecker.report(3018, "Postcondition returns unexpected type",node.getLocation());
				TypeChecker.detail2("Actual", b, "Expected", expected);
			}
		}

		// This check returns the type of the function body in the case where
		// all of the curried parameter sets are provided.

		info.env = local;
		info.scope = question.scope;
		info.qualifiers = null;		
		
		node.setActualResult(node.getBody().apply(rootVisitor,question));

		if (!TypeComparator.compatible(expectedResult, node.getActualResult()))
		{
			TypeChecker.report(3018, "Function returns unexpected type",node.getLocation());
			TypeChecker.detail2("Actual", node.getActualResult(), "Expected", expectedResult);
		}
		
		if (PTypeAssistant.narrowerThan(node.getType(),node.getAccess()))
		{
			TypeCheckerErrors.report(3019, "Function parameter visibility less than function definition",node.getLocation(),node);
		}

		if (node.getMeasure() == null && node.getRecursive())
		{
			TypeCheckerErrors.warning(5012, "Recursive function has no measure",node.getLocation(),node);
		}
		else if (node.getMeasure() != null)
		{
			if (question.env.isVDMPP()) node.getMeasure().setTypeQualifier(node.getType().getParameters());
			node.setMeasureDef(question.env.findName(node.getMeasure(), question.scope));

			if (node.getMeasureDef() == null)
			{
				TypeCheckerErrors.report(3270, "Measure " + node.getMeasure() + " is not in scope",node.getMeasure().getLocation(),node.getMeasure());
			}
			else if (!(node.getMeasureDef() instanceof AExplicitFunctionDefinition))
			{
				TypeCheckerErrors.report(3271, "Measure " + node.getMeasure() + " is not an explicit function",node.getMeasure().getLocation(),node.getMeasure());
			}
			else if (node.getMeasureDef() == node)
			{
				TypeCheckerErrors.report(3304, "Recursive function cannot be its own measure",node.getMeasure().getLocation(),node.getMeasure());
			}
			else
			{
				AExplicitFunctionDefinition efd = (AExplicitFunctionDefinition)node.getMeasureDef();
				
				if (node.getTypeParams() == null && efd.getTypeParams() != null)
				{
					TypeCheckerErrors.report(3309, "Measure must not be polymorphic",node.getMeasure().getLocation(),node.getMeasure());
				}
				else if (node.getTypeParams() != null && efd.getTypeParams() == null)
				{
					TypeCheckerErrors.report(3310, "Measure must also be polymorphic",node.getMeasure().getLocation(),node.getMeasure());
				}
				
				AFunctionType mtype = (AFunctionType)efd.getType();

				if (!TypeComparator.compatible(mtype.getParameters(), node.getType().getParameters()))
				{
					TypeCheckerErrors.report(3303, "Measure parameters different to function",node.getMeasure().getLocation(),node.getMeasure());
					TypeChecker.detail2(node.getMeasure().getName(), mtype.getParameters(), node.getName().getName(), node.getType().getParameters());
				}

				if (!(mtype.getResult() instanceof ANatNumericBasicType))
				{
					if (mtype.getResult().kindPType() == EType.PRODUCT)
					{
						AProductType pt = PTypeAssistant.getProduct(mtype.getResult());

						for (PType t: pt.getTypes())
						{
							if (!(t instanceof ANatNumericBasicType))
							{
								TypeCheckerErrors.report(3272,
									"Measure range is not a nat, or a nat tuple",node.getMeasure().getLocation(),node.getMeasure());
								TypeCheckerErrors.detail("Actual", mtype.getResult());
								break;
							}
						}

						node.setMeasureLexical(pt.getTypes().size());
					}
					else
					{
						TypeCheckerErrors.report(3272,
							"Measure range is not a nat, or a nat tuple",node.getMeasure().getLocation(),node.getMeasure());
						TypeCheckerErrors.detail("Actual", mtype.getResult());
					}
				}
			}
		}

		if (!(node.getBody() instanceof ANotYetSpecifiedExp) &&
			!(node.getBody() instanceof ASubclassResponsibilityExp))
		{
			local.unusedCheck();
		}
		
		node.setType(node.getType());
		return node.getType();
	}

	@Override
	public PType caseAExternalDefinition(AExternalDefinition node,
			TypeCheckInfo question) {
		// Nothing to do - state is type checked separately
		return null;
	}

	@Override
	public PType caseAImplicitFunctionDefinition(
			AImplicitFunctionDefinition node, TypeCheckInfo question) {
		
		List<PDefinition> defs = new Vector<PDefinition>();

		if (node.getTypeParams() != null)
		{
			defs.addAll(AImplicitFunctionDefinitionAssistant.getTypeParamDefinitions(node));
		}

		Set<PDefinition> argdefs = new HashSet<PDefinition>();

		for (APatternListTypePair pltp: node.getParamPatterns())
		{
			argdefs.addAll(APatternListTypePairAssistant.getDefinitions(pltp,NameScope.LOCAL));
		}

		defs.addAll(new Vector<PDefinition>(argdefs));
		FlatCheckedEnvironment local = new FlatCheckedEnvironment(defs, question.env, question.scope);
		local.setStatic(PAccessSpecifierAssistant.isStatic(node.getAccess()));
		local.setEnclosingDefinition(node);

		TypeCheckInfo newInfo = new TypeCheckInfo();
		newInfo.env = local;
		newInfo.scope = question.scope;
		newInfo.qualifiers = null;
		
		PDefinitionListAssistant.typeCheck(defs, rootVisitor, newInfo); 

		if (node.getBody() != null)
		{
			if (node.getClassDefinition() != null && !PAccessSpecifierAssistant.isStatic(node.getAccess()))
			{
				local.add(PDefinitionAssistant.getSelfDefinition(node));
			}

			node.setActualResult(node.getBody().apply(rootVisitor, newInfo));

			if (!TypeComparator.compatible(node.getResult().getType(), node.getActualResult()))
			{
				TypeCheckerErrors.report(3029, "Function returns unexpected type",node.getLocation(),node);
				TypeCheckerErrors.detail2("Actual", node.getActualResult(), "Expected",  node.getResult().getType());
			}
		}

		if (PTypeAssistant.narrowerThan(node.getType(),node.getAccess()))
		{
			TypeCheckerErrors.report(3030, "Function parameter visibility less than function definition",node.getLocation(),node);
		}

		if (node.getPredef() != null)
		{
			newInfo.qualifiers = null;
			newInfo.scope = NameScope.NAMES;
			PType b = node.getPredef().getBody().apply(rootVisitor, question);
			ABooleanBasicType expected = new ABooleanBasicType(node.getLocation(),false, null);

			if (!PTypeAssistant.isType(b, ABooleanBasicType.class))
			{
				TypeCheckerErrors.report(3018, "Precondition returns unexpected type",node.getLocation(),node);
				TypeCheckerErrors.detail2("Actual", b, "Expected", expected);
			}
		}

		// The result variables are in scope for the post condition

		if (node.getPostdef() != null)
		{
			PType b = null;

			if (node.getResult() != null)
			{
	    		List<PDefinition> postdefs = APatternTypePairAssistant.getDefinitions(node.getResult());
	    		FlatCheckedEnvironment post =
	    			new FlatCheckedEnvironment(postdefs, local, NameScope.NAMES);
	    		post.setStatic(PAccessSpecifierAssistant.isStatic(node.getAccess()));
	    		post.setEnclosingDefinition(node);
	    		newInfo.env = post;
	    		newInfo.qualifiers = null;
	    		newInfo.scope = NameScope.NAMES;
				b = node.getPostdef().getBody().apply(rootVisitor, question);
				post.unusedCheck();
			}
			else
			{
				newInfo.qualifiers = null;
				newInfo.scope = NameScope.NAMES;
				b = node.getPostdef().getBody().apply(rootVisitor, newInfo);
			}

			ABooleanBasicType expected = new ABooleanBasicType(node.getLocation(),false,null);

			if (!PTypeAssistant.isType(b, ABooleanBasicType.class))
			{
				TypeCheckerErrors.report(3018, "Postcondition returns unexpected type",node.getLocation(),node);
				TypeCheckerErrors.detail2("Actual", b, "Expected", expected);
			}
		}

		if (node.getMeasure() == null && node.getRecursive())
		{
			TypeCheckerErrors.warning(5012, "Recursive function has no measure",node.getLocation(),node);
		}
		else if (node.getMeasure() != null)
		{
			if (question.env.isVDMPP()) node.getMeasure().setTypeQualifier(node.getType().getParameters());
			node.setMeasureDef(question.env.findName(node.getMeasure(), question.scope));

			if (node.getBody() == null)
			{
				TypeCheckerErrors.report(3273, "Measure not allowed for an implicit function",node.getMeasure().getLocation(),node);
			}
			else if (node.getMeasureDef() == null)
			{
				TypeCheckerErrors.report(3270, "Measure " + node.getMeasure() + " is not in scope",node.getMeasure().getLocation(),node.getMeasure());
			}
			else if (!(node.getMeasureDef() instanceof AExplicitFunctionDefinition))
			{
				TypeCheckerErrors.report(3271, "Measure " + node.getMeasure() + " is not an explicit function",node.getMeasure().getLocation(),node.getMeasure());
			}
			else
			{
				AExplicitFunctionDefinition efd = (AExplicitFunctionDefinition)node.getMeasureDef();
				
				if (node.getTypeParams() == null && efd.getTypeParams() != null)
				{
					TypeCheckerErrors.report(3309, "Measure must not be polymorphic",node.getMeasure().getLocation(),node.getMeasure());
				}
				else if (node.getTypeParams() != null && efd.getTypeParams() == null)
				{
					TypeCheckerErrors.report(3310, "Measure must also be polymorphic",node.getMeasure().getLocation(),node.getMeasure());
				}
				
				AFunctionType mtype = (AFunctionType)node.getMeasureDef().getType();

				if (!TypeComparator.compatible(mtype.getParameters(),node.getType().getParameters()))
				{
					TypeCheckerErrors.report(3303, "Measure parameters different to function",node.getMeasure().getLocation(),node.getMeasure());
					TypeCheckerErrors.detail2(node.getMeasure().name, mtype.getParameters(), node.getName().name,node.getType().getParameters());
				}

				if (!(mtype.getResult() instanceof ANatNumericBasicType))
				{
					if (PTypeAssistant.isProduct(mtype.getResult()))
					{
						AProductType pt = PTypeAssistant.getProduct(mtype.getResult());

						for (PType t: pt.getTypes())
						{
							if (!(t instanceof ANatNumericBasicType))
							{
								TypeCheckerErrors.report(3272,
									"Measure range is not a nat, or a nat tuple",node.getMeasure().getLocation(),node.getMeasure());
								TypeCheckerErrors.detail("Actual", mtype.getResult());
							}
						}

						node.setMeasureLexical(pt.getTypes().size());
					}
					else
					{
						TypeCheckerErrors.report(3272,
							"Measure range is not a nat, or a nat tuple",node.getMeasure().getLocation(),node.getMeasure());
						TypeCheckerErrors.detail("Actual", mtype.getResult());
					}
				}
			}
		}

		if (!(node.getBody() instanceof ANotYetSpecifiedExp) &&
			!(node.getBody() instanceof ASubclassResponsibilityExp))
		{
			local.unusedCheck();
		}
		
		node.setType(node.getType());
		return node.getType();
	}
	
	
	@Override
	public PType caseAExplicitOperationDefinition(
			AExplicitOperationDefinition node, TypeCheckInfo question) {
		
		question.scope = NameScope.NAMESANDSTATE;
		List<PType> ptypes = node.getType().getParameters();

		if (node.getParameterPatterns().size() > ptypes.size())
		{
			TypeCheckerErrors.report(3023, "Too many parameter patterns",node.getLocation(),node);
			TypeCheckerErrors.detail2("Type params", ptypes.size(),
				"Patterns", node.getParameterPatterns().size());
			return null;
		}
		else if (node.getParameterPatterns().size() < ptypes.size())
		{
			TypeCheckerErrors.report(3024, "Too few parameter patterns",node.getLocation(),node);
			TypeCheckerErrors.detail2("Type params", ptypes.size(),
				"Patterns", node.getParameterPatterns().size());
			return null;
		}

		node.setParamDefinitions(AExplicitOperationDefinitionAssistant.getParamDefinitions(node));
		PDefinitionListAssistant.typeCheck(node.getParamDefinitions(), rootVisitor, question); 

		FlatCheckedEnvironment local =
			new FlatCheckedEnvironment(node.getParamDefinitions(), question.env, question.scope);
		local.setStatic(PAccessSpecifierAssistant.isStatic(node.getAccess()));
		local.setEnclosingDefinition(node);

		if (question.env.isVDMPP())
		{
			if (!PAccessSpecifierAssistant.isStatic(node.getAccess()))
			{
				local.add(PDefinitionAssistant.getSelfDefinition(node));
			}

			if (node.getName().name.equals(node.getClassDefinition().getName().name))
			{
				node.setIsConstructor(true);
				node.getClassDefinition().setHasContructors(true);

				if (PAccessSpecifierAssistant.isAsync(node.getAccess()))
				{
					TypeCheckerErrors.report(3286, "Constructor cannot be 'async'",node.getLocation(),node);
				}

				if (PTypeAssistant.isClass(node.getType().getResult()))
				{
					AClassType ctype = PTypeAssistant.getClassType(node.getType().getResult());

					if (ctype.getClassdef() != node.getClassDefinition())
					{
						TypeCheckerErrors.report(3025,
							"Constructor operation must have return type " + node.getClassDefinition().getName().name,node.getType().getResult().getLocation(),node.getType().getResult());
					}
				}
				else
				{
					TypeCheckerErrors.report(3026,
						"Constructor operation must have return type " + node.getClassDefinition().getName().name,node.getType().getResult().getLocation(),node.getType().getResult());
				}
			}
		}

		if (node.getPredef() != null)
		{
			FlatEnvironment pre = new FlatEnvironment(new Vector<PDefinition>(), local);
			pre.setEnclosingDefinition(node.getPredef());
			
			question.env = pre;
			question.qualifiers = null;
			question.scope = NameScope.NAMESANDSTATE;
			PType b = node.getPredef().getBody().apply(rootVisitor, question);
			
			ABooleanBasicType expected = new ABooleanBasicType(node.getLocation(),false,null);

			if (!PTypeAssistant.isType(b, ABooleanBasicType.class))
			{
				TypeCheckerErrors.report(3018, "Precondition returns unexpected type",node.getLocation(),node);
				TypeCheckerErrors.detail2("Actual", b, "Expected", expected);
			}
		}

		if (node.getPostdef() != null)
		{
			LexNameToken result = new LexNameToken(node.getName().module, "RESULT", node.getLocation());
			PPattern rp = new AIdentifierPattern(null, null, false, result);
			List<PDefinition> rdefs = PPatternAssistant.getDefinitions(rp,node.getType().getResult(), NameScope.NAMESANDANYSTATE);
			FlatEnvironment post = new FlatEnvironment(rdefs, local);
			post.setEnclosingDefinition(node.getPostdef());
			question.env = post;
			question.qualifiers = null;
			question.scope = NameScope.NAMESANDANYSTATE;
			PType b = node.getPostdef().getBody().apply(rootVisitor, question);
			ABooleanBasicType expected = new ABooleanBasicType(node.getLocation(),false,null);

			if (!PTypeAssistant.isType(b, ABooleanBasicType.class))
			{
				TypeCheckerErrors.report(3018, "Postcondition returns unexpected type",node.getLocation(),node);
				TypeCheckerErrors.detail2("Actual", b, "Expected", expected);
			}
		}
		question.env = local;
		question.qualifiers = null;
		question.scope = NameScope.NAMESANDSTATE;
		node.setActualResult( node.getBody().apply(rootVisitor,question));
		boolean compatible = TypeComparator.compatible(node.getType().getResult(), node.getActualResult());

		if ((node.getIsConstructor() && !PTypeAssistant.isType(node.getActualResult(), AVoidType.class)  && !compatible) ||
			(!node.getIsConstructor() && !compatible))
		{
			TypeCheckerErrors.report(3027, "Operation returns unexpected type",node.getLocation(),node);
			TypeCheckerErrors.detail2("Actual", node.getActualResult(), "Expected", node.getType().getResult());
		}

		if (PAccessSpecifierAssistant.isAsync(node.getAccess()) && ! PTypeAssistant.isType(node.getType().getResult(), AVoidType.class))
		{
			TypeCheckerErrors.report(3293, "Asynchronous operation " + node.getName() + " cannot return a value",node.getLocation(),node);
		}

		if (PTypeAssistant.narrowerThan(node.getType(), node.getAccess()))
		{
			TypeCheckerErrors.report(3028, "Operation parameter visibility less than operation definition",node.getLocation(),node);
		}

		if (!(node.getBody() instanceof ANotYetSpecifiedStm) &&
			!(node.getBody() instanceof ASubclassResponsibilityStm))
		{
			local.unusedCheck();
		}
		node.setType(node.getType());
		return node.getType();
	}
	
	@Override
	public PType caseAImplicitOperationDefinition(
			AImplicitOperationDefinition node, TypeCheckInfo question) {

		question.scope = NameScope.NAMESANDSTATE;
		List<PDefinition> defs = new Vector<PDefinition>();
		Set<PDefinition> argdefs = new HashSet<PDefinition>();

		if (question.env.isVDMPP())
		{
			node.setStateDefinition(question.env.findClassDefinition());
		}
		else
		{
			node.setStateDefinition(question.env.findStateDefinition());
		}

		for (APatternListTypePair ptp: node.getParameterPatterns())
		{
			argdefs.addAll(APatternListTypePairAssistant.getDefinitions(ptp, NameScope.LOCAL));
		}

		defs.addAll(new Vector<PDefinition>(argdefs));

		if (node.getResult() != null)
		{
			defs.addAll(
					PPatternAssistant.getDefinitions(node.getResult().getPattern(), node.getType().getResult(), NameScope.LOCAL));
		}

		// Now we build local definitions for each of the externals, so
		// that they can be added to the local environment, while the
		// global state is made inaccessible - but only if we have
		// an "ext" clause

		boolean limitStateScope = false;

		if (node.getExternals() != null)
		{
    		for (AExternalClause clause: node.getExternals())
    		{
    			for (LexNameToken exname: clause.getIdentifiers())
    			{
    				PDefinition sdef = question.env.findName(exname, NameScope.STATE);
    				AExternalClauseAssistant.typeResolve(clause,rootVisitor,question);

    				if (sdef == null)
    				{
    					TypeCheckerErrors.report(3031, "Unknown state variable " + exname,exname.getLocation(),exname);
    				}
    				else
    				{
    					if (!(clause.getType() instanceof AUnknownType) &&
    						!sdef.getType().equals(clause.getType()))
        				{
        					TypeCheckerErrors.report(3032, "State variable " + exname + " is not this type",node.getLocation(),node);
        					TypeCheckerErrors.detail2("Declared", sdef.getType(), "ext type", clause.getType());
        				}
        				else
        				{
        					LexNameToken oldname = clause.getMode().type==VDMToken.READ ? null : sdef.getName().getOldName();
        					
            				defs.add(new AExternalDefinition(sdef.getLocation(), sdef.getName(), NameScope.STATE, false, null, null, null, sdef, clause.getMode().type==VDMToken.READ, oldname));

            				// VDM++ "ext wr" clauses in a constructor effectively
            				// initialize the instance variable concerned.

            				if ((clause.getMode().type==VDMToken.WRITE) &&
            					sdef instanceof AInstanceVariableDefinition &&
            					node.getName().name.equals(node.getClassDefinition().getName().name))
            				{
            					AInstanceVariableDefinition iv = (AInstanceVariableDefinition)sdef;
            					iv.setInitialized(true);
            				}
        				}
    				}
    			}
    		}

    		// All relevant globals are now in defs (local), so we
    		// limit the state searching scope

    		limitStateScope = true;
		}

		PDefinitionListAssistant.typeCheck(defs, rootVisitor, question);

		FlatCheckedEnvironment local = new FlatCheckedEnvironment(defs, question.env, question.scope);
		local.setLimitStateScope(limitStateScope);
		local.setStatic(PAccessSpecifierAssistant.isStatic(node.getAccess()));
		local.setEnclosingDefinition(node);

		if (node.getBody() != null)
		{
			if (node.getClassDefinition() != null && !PAccessSpecifierAssistant.isStatic(node.getAccess()))
			{
				local.add(PDefinitionAssistant.getSelfDefinition(node));
			}

			if (question.env.isVDMPP())
			{
				if (node.getName().name.equals(node.getClassDefinition().getName().name))
    			{
    				node.setIsConstructor(true);
    				node.getClassDefinition().setHasContructors(true);

    				if (PAccessSpecifierAssistant.isAsync(node.getAccess()))
    				{
    					TypeCheckerErrors.report(3286, "Constructor cannot be 'async'",node.getLocation(),node);
    				}

    				if (PTypeAssistant.isClass(node.getType().getResult()))
    				{
    					AClassType ctype = PTypeAssistant.getClassType(node.getType().getResult());

    					if (ctype.getClassdef() != node.getClassDefinition())
    					{
    						TypeCheckerErrors.report(3025,
    							"Constructor operation must have return type " + node.getClassDefinition().getName().name,node.getType().getResult().getLocation(),node.getType().getResult());
    					}
    				}
    				else
    				{
    					TypeCheckerErrors.report(3026,
    						"Constructor operation must have return type " + node.getClassDefinition().getName().name,node.getType().getLocation(),node.getType());
    				}
    			}
			}
			question.env = local;
			question.qualifiers = null;
			question.scope = NameScope.NAMESANDSTATE;
			node.setActualResult( node.getBody().apply(rootVisitor,question));
			
			boolean compatible = TypeComparator.compatible(node.getType().getResult(), node.getActualResult());

			if ((node.getIsConstructor() && !PTypeAssistant.isType(node.getActualResult(),AVoidType.class) && !compatible) ||
				(!node.getIsConstructor() && !compatible))
			{
				TypeCheckerErrors.report(3035, "Operation returns unexpected type",node.getLocation(),node);
				TypeCheckerErrors.detail2("Actual", node.getActualResult(), "Expected", node.getType().getResult());
			}
		}

		if (PAccessSpecifierAssistant.isAsync(node.getAccess()) && !PTypeAssistant.isType(node.getType().getResult(), AVoidType.class))
		{
			TypeCheckerErrors.report(3293, "Asynchronous operation " + node.getName() + " cannot return a value",node.getLocation(),node);
		}

		if (PTypeAssistant.narrowerThan(node.getType(), node.getAccess()))
		{
			TypeCheckerErrors.report(3036, "Operation parameter visibility less than operation definition",node.getLocation(),node);
		}

		if (node.getPredef() != null)
		{
			FlatEnvironment pre = new FlatEnvironment(new Vector<PDefinition>(), local);
			pre.setEnclosingDefinition(node.getPredef());
			question.env = pre;
			question.qualifiers = null;
			question.scope = NameScope.NAMESANDSTATE;
			PType b = node.getPredef().getBody().apply(rootVisitor, question); 
			ABooleanBasicType expected = new ABooleanBasicType(node.getLocation(),false,null);

			if (!PTypeAssistant.isType(b,ABooleanBasicType.class))
			{
				TypeCheckerErrors.report(3018, "Precondition returns unexpected type",node.getLocation(),node);
				TypeCheckerErrors.detail2("Actual", b, "Expected", expected);
			}
		}

		// The result variables are in scope for the post condition

		if (node.getPostdef() != null)
		{
			PType b = null;

			if (node.getResult() != null)
			{
	    		List<PDefinition> postdefs = APatternTypePairAssistant.getDefinitions(node.getResult());
	    		FlatCheckedEnvironment post =
	    			new FlatCheckedEnvironment(postdefs, local, NameScope.NAMESANDANYSTATE);
	    		post.setStatic(PAccessSpecifierAssistant.isStatic(node.getAccess()));
	    		post.setEnclosingDefinition(node.getPostdef());	    		
	    		question.env = post;
	    		question.qualifiers = null;
	    		question.scope = NameScope.NAMESANDANYSTATE;
				b = node.getPostdef().getBody().apply(rootVisitor,question);
				post.unusedCheck();
			}
			else
			{
	    		FlatEnvironment post = new FlatEnvironment(new Vector<PDefinition>(), local);
	    		post.setEnclosingDefinition(node.getPostdef());
	    		question.env = post;
	    		question.qualifiers = null;
	    		question.scope = NameScope.NAMESANDANYSTATE;
	    		b = node.getPostdef().getBody().apply(rootVisitor,question);
			}

			ABooleanBasicType expected = new ABooleanBasicType(node.getLocation(),false,null);

			if (!PTypeAssistant.isType(b,ABooleanBasicType.class))
			{
				TypeCheckerErrors.report(3018, "Postcondition returns unexpected type",node.getLocation(),node);
				TypeCheckerErrors.detail2("Actual", b, "Expected", expected);
			}
		}

		if (node.getErrors() != null)
		{
			for (AErrorCase error: node.getErrors())
			{
				question.env = local;
				question.qualifiers = null;
				question.scope = NameScope.NAMESANDSTATE;
				
				PType a = error.getLeft().apply(rootVisitor,question);

				if (!PTypeAssistant.isType(a,ABooleanBasicType.class))
				{
					TypeCheckerErrors.report(3307, "Errs clause is not bool -> bool",error.getLeft().getLocation(),error.getLeft());
				}

				question.scope = NameScope.NAMESANDANYSTATE;
				PType b = error.getRight().apply(rootVisitor,question);

				if (!PTypeAssistant.isType(b,ABooleanBasicType.class))
				{
					TypeCheckerErrors.report(3307, "Errs clause is not bool -> bool",error.getRight().getLocation(),error.getRight());
				}
			}
		}

		if (!(node.getBody() instanceof ANotYetSpecifiedStm) &&
			!(node.getBody() instanceof ASubclassResponsibilityStm))
		{
			local.unusedCheck();
		}
		node.setType(node.getActualResult());
		return node.getType();
	}
	
	@Override
	public PType caseAImportedDefinition(AImportedDefinition node,
			TypeCheckInfo question) {
		node.setType(node.getDef().apply(rootVisitor, question));
		
		return node.getType();
	}
	
	@Override
	public PType caseAInheritedDefinition(AInheritedDefinition node,
			TypeCheckInfo question) {
		node.setType(node.getSuperdef().apply(rootVisitor, question));
		return node.getType();
	}
	
	@Override
	public PType caseALocalDefinition(ALocalDefinition node,
			TypeCheckInfo question) {
		if (node.getType() != null)
   		{
   			node.setType(PTypeAssistant.typeResolve(node.getType(), null, rootVisitor, question));
   		}
		
		return node.getType();
	}
	
	@Override
	public PType caseAMultiBindListDefinition(AMultiBindListDefinition node,
			TypeCheckInfo question) {
	
		List<PDefinition> defs = new Vector<PDefinition>();

		for (PMultipleBind mb: node.getBindings())
		{
			PType type = mb.apply(rootVisitor, question);
			defs.addAll(PMultipleBindAssistant.getDefinitions(mb,type, question));
		}

		PDefinitionListAssistant.typeCheck(defs, rootVisitor, question);
		node.setDefs(defs);
		return null;
	}
	@Override
	public PType caseAMutexSyncDefinition(AMutexSyncDefinition node,
			TypeCheckInfo question) {

		SClassDefinition classdef = question.env.findClassDefinition();

		if (node.getOperations().isEmpty())
		{
			// Add all locally visibly callable operations for mutex(all)

			for (PDefinition def: SClassDefinitionAssistant.getLocalDefinitions(node.getClassDefinition()))
			{
				if (PDefinitionAssistant.isCallableOperation(def) &&
					!def.getName().name.equals(classdef.getName().name))
				{
					node.getOperations().add(def.getName());
				}
			}
		}

		for (LexNameToken opname: node.getOperations())
		{
			int found = 0;

			for (PDefinition def: classdef.getDefinitions())
			{
				if (def.getName() != null && def.getName().matches(opname))
				{
					found++;

					if (!PDefinitionAssistant.isCallableOperation(def))
					{
						TypeCheckerErrors.report(3038, opname + " is not an explicit operation",opname.location,opname);
					}
				}
			}

    		if (found == 0)
    		{
    			TypeCheckerErrors.report(3039, opname + " is not in scope",opname.location,opname);
    		}
    		else if (found > 1)
    		{
    			TypeCheckerErrors.warning(5002, "Mutex of overloaded operation",opname.location,opname);
    		}

    		if (opname.name.equals(classdef.getName().name))
    		{
    			TypeCheckerErrors.report(3040, "Cannot put mutex on a constructor",opname.location,opname);
    		}

    		for (LexNameToken other: node.getOperations())
    		{
    			if (opname != other && opname.equals(other))
    			{
    				TypeCheckerErrors.report(3041, "Duplicate mutex name",opname.location,opname);
    			}
    		}
    		    		
		}
		return null;
	}
	
	
	@Override
	public PType caseANamedTraceDefinition(ANamedTraceDefinition node,
			TypeCheckInfo question) {
		
		if (question.env.isVDMPP())
		{
			question.env = new FlatEnvironment(PDefinitionAssistant.getSelfDefinition(node), question.env);
		}

		question.scope = NameScope.NAMESANDSTATE;
		for (List<PTraceDefinition> term: node.getTerms())
		{			
			PTraceDefinitionAssistant.typeCheck(term, rootVisitor, question);
		}
		
		return null;
	}
	
	@Override
	public PType caseAPerSyncDefinition(APerSyncDefinition node,
			TypeCheckInfo question) {
		
		Environment base = question.env;
		
		SClassDefinition classdef = base.findClassDefinition();
		int opfound = 0;
		int perfound = 0;

		for (PDefinition def: classdef.getDefinitions())
		{
			if (def.getName() != null && def.getName().matches(node.getOpname()))
			{
				opfound++;

				if (!PDefinitionAssistant.isCallableOperation(def))
				{
					TypeCheckerErrors.report(3042, node.getOpname() + " is not an explicit operation",node.getOpname().location,node.getOpname());
				}
			}

			if (def instanceof APerSyncDefinition)
			{
				APerSyncDefinition psd = (APerSyncDefinition)def;

				if (psd.getOpname().equals(node.getOpname()))
				{
					perfound++;
				}
			}
		}

		LexNameToken opname = node.getOpname();
		
		if (opfound == 0)
		{
			TypeCheckerErrors.report(3043, opname + " is not in scope",opname.location,opname);
		}
		else if (opfound > 1)
		{
			TypeCheckerErrors.warning(5003, "Permission guard of overloaded operation",opname.location,opname);
		}

		if (perfound != 1)
		{
			TypeCheckerErrors.report(3044, "Duplicate permission guard found for " + opname, opname.location,opname);
		}

		if (opname.name.equals(classdef.getName().name))
		{
			TypeCheckerErrors.report(3045, "Cannot put guard on a constructor", opname.location,opname);
		}

		Environment local = new FlatEnvironment(node, base);
		local.setEnclosingDefinition(node);	// Prevent op calls
		question.env = local;
		question.scope = NameScope.NAMESANDSTATE;
		PType rt = node.getGuard().apply(rootVisitor, question);

		if (!PTypeAssistant.isType(rt, ABooleanBasicType.class))
		{
			TypeCheckerErrors.report(3046, "Guard is not a boolean expression",node.getGuard().getLocation(),node.getGuard());
		}
		
		node.setType(rt);
		return node.getType();
	}
	
	@Override
	public PType caseARenamedDefinition(ARenamedDefinition node,
			TypeCheckInfo question) {

		node.setType(node.getDef().apply(rootVisitor, question));		
		return node.getType();			
	}
	
	@Override
	public PType caseAStateDefinition(AStateDefinition node,
			TypeCheckInfo question) {
		
		Environment base = question.env;
		
		if (base.findStateDefinition() != node)
		{
			TypeCheckerErrors.report(3047, "Only one state definition allowed per module",node.getLocation(),node);
			return null;
		}

		PDefinitionListAssistant.typeCheck(node.getStateDefs(),rootVisitor, question);

		if (node.getInvdef() != null)
		{
			node.getInvdef().apply(rootVisitor, question);
		}

		if (node.getInitdef() != null)
		{
			node.getInitdef().apply(rootVisitor, question);
		}
		
		return null;
	}
	
	@Override
	public PType caseAThreadDefinition(AThreadDefinition node,
			TypeCheckInfo question) {
		question.scope = NameScope.NAMESANDSTATE;
		
		PType rt = node.getStatement().apply(rootVisitor, question);

		if (!(rt instanceof AVoidType) && !(rt instanceof AUnknownType))
		{
			TypeCheckerErrors.report(3049, "Thread statement/operation must not return a value",node.getLocation(),node);
		}
		
		node.setType(rt);
		return rt;
	}
	
	@Override
	public PType caseATypeDefinition(ATypeDefinition node,
			TypeCheckInfo question) {
		
		if (node.getInvdef() != null)
		{
			question.scope = NameScope.NAMES;
			node.setType(node.getInvdef().apply(rootVisitor, question));
		}
		return node.getType();
		
	}
	
	@Override
	public PType caseAUntypedDefinition(AUntypedDefinition node,
			TypeCheckInfo question) {
		
		assert false: "Can't type check untyped definition?";
		return null;
	}
	
	@Override
	public PType caseAValueDefinition(AValueDefinition node,
			TypeCheckInfo question) {

		question.qualifiers = null;
		PType expType = node.getExpression().apply(rootVisitor, question);
		node.setExpType(expType);
		PType type = node.getType();
		if (expType instanceof AVoidType)
		{
			TypeCheckerErrors.report(3048, "Expression does not return a value",node.getExpression().getLocation(),node.getExpression());
		}
		else if (type != null)
		{
			if (!TypeComparator.compatible(type, expType))
			{
				TypeCheckerErrors.report(3051, "Expression does not match declared type",node.getLocation(),node);
				TypeCheckerErrors.detail2("Declared", type, "Expression", expType);
			}
		}
		else
		{
			type = expType;
			node.setType(expType);
		}

		Environment base = question.env;
		
		if (base.isVDMPP() && type instanceof ANamedInvariantType)
		{
			ANamedInvariantType named = (ANamedInvariantType)type;
    		PDefinition typedef = base.findType(named.getName(), node.getLocation().module);

    		if (PAccessSpecifierAssistant.narrowerThan(typedef.getAccess(), node.getAccess()))
    		{
    			TypeCheckerErrors.report(3052, "Value type visibility less than value definition",node.getLocation(),node);
    		}
		}

		PPattern pattern = node.getPattern();
		pattern.apply(rootVisitor, question);
		List<PDefinition> newdefs = PPatternAssistant.getDefinitions(pattern, type, question.scope);

		// The untyped definitions may have had "used" markers, so we copy
		// those into the new typed definitions, lest we get warnings. We
		// also mark the local definitions as "ValueDefintions" (proxies),
		// so that classes can be constructed correctly (values are statics).

		for (PDefinition d: newdefs)
		{
			for (PDefinition u: node.getDefs())
			{
				if (u.getName().equals(d.getName()))
				{
					if (PDefinitionAssistant.isUsed(u))
					{
						PDefinitionAssistant.markUsed(d);
					}

					break;
				}
			}

			ALocalDefinition ld = (ALocalDefinition)d;
			ALocalDefinitionAssistant.setValueDefinition(ld);
		}

		node.setDefs(newdefs);
		List<PDefinition> defs = node.getDefs();
		PDefinitionListAssistant.setAccessibility(defs,node.getAccess());
		PDefinitionListAssistant.setClassDefinition(defs,node.getClassDefinition());
		question.qualifiers = null;
		PDefinitionListAssistant.typeCheck(defs, rootVisitor, question);
		return null;
	}
		
	
}
