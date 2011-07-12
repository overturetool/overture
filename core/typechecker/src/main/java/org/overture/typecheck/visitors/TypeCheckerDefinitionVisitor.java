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
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.assistants.AExplicitFunctionDefinitionAssistant;
import org.overture.ast.definitions.assistants.AExplicitOperationDefinitionAssistant;
import org.overture.ast.definitions.assistants.AImplicitFunctionDefinitionAssistant;
import org.overture.ast.definitions.assistants.PAccessSpecifierAssistant;
import org.overture.ast.definitions.assistants.PDefinitionAssistant;
import org.overture.ast.expressions.ANotYetSpecifiedExp;
import org.overture.ast.expressions.ASubclassResponsibilityExp;
import org.overture.ast.expressions.AUndefinedExp;
import org.overture.ast.node.NodeList;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.APatternListTypePair;
import org.overture.ast.patterns.ATypeBind;
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
import org.overture.ast.types.ANatNumericBasicType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.AVoidType;
import org.overture.ast.types.EType;
import org.overture.ast.types.PType;
import org.overture.ast.types.assistants.APatternListTypePairAssistant;
import org.overture.ast.types.assistants.PTypeAssistant;
import org.overture.runtime.Environment;
import org.overture.runtime.FlatCheckedEnvironment;
import org.overture.runtime.FlatEnvironment;
import org.overture.runtime.TypeChecker;
import org.overture.runtime.TypeComparator;
import org.overture.typecheck.PrivateClassEnvironment;
import org.overture.typecheck.TypeCheckInfo;
import org.overture.typecheck.TypeCheckerErrors;
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

		PDefinitionAssistant.typeCheck(node.getDefs(), rootVisitor, question);
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

		PType expectedResult = AExplicitFunctionDefinitionAssistant.checkParams(node,node.getParamPatternList().listIterator(), node.getFunctionType());

		List<List<PDefinition>> paramDefinitionList = AExplicitFunctionDefinitionAssistant.getParamDefinitions(node,node.getFunctionType(), node.getParamPatternList(),node.getLocation());

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
		PDefinitionAssistant.typeCheck(defs,this,info); //can be this because its a definition list

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
			if (question.env.isVDMPP()) node.getMeasure().setTypeQualifier(node.getFunctionType().getParameters());
			node.setMeasureDef(question.env.findName(node.getMeasure(), question.scope));

			if (node.getMeasureDef() == null)
			{
				node.getMeasure().report(3270, "Measure " + node.getMeasure() + " is not in scope");
			}
			else if (!(node.getMeasureDef() instanceof AExplicitFunctionDefinition))
			{
				node.getMeasure().report(3271, "Measure " + node.getMeasure() + " is not an explicit function");
			}
			else if (node.getMeasureDef() == node)
			{
				node.getMeasure().report(3304, "Recursive function cannot be its own measure");
			}
			else
			{
				AExplicitFunctionDefinition efd = (AExplicitFunctionDefinition)node.getMeasureDef();
				
				if (node.getTypeParams() == null && efd.getTypeParams() != null)
				{
					node.getMeasure().report(3309, "Measure must not be polymorphic");
				}
				else if (node.getTypeParams() != null && efd.getTypeParams() == null)
				{
					node.getMeasure().report(3310, "Measure must also be polymorphic");
				}
				
				AFunctionType mtype = (AFunctionType)efd.getFunctionType();

				if (!TypeComparator.compatible(mtype.getParameters(), node.getFunctionType().getParameters()))
				{
					node.getMeasure().report(3303, "Measure parameters different to function");
					TypeChecker.detail2(node.getMeasure().getName(), mtype.getParameters(), node.getName().getName(), node.getFunctionType().getParameters());
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
								node.getMeasure().report(3272,
									"Measure range is not a nat, or a nat tuple");
								node.getMeasure().detail("Actual", mtype.getResult());
								break;
							}
						}

						node.setMeasureLexical(pt.getTypes().size());
					}
					else
					{
						node.getMeasure().report(3272,
							"Measure range is not a nat, or a nat tuple");
						node.getMeasure().detail("Actual", mtype.getResult());
					}
				}
			}
		}

		if (!(node.getBody() instanceof ANotYetSpecifiedExp) &&
			!(node.getBody() instanceof ASubclassResponsibilityExp))
		{
			local.unusedCheck();
		}
		
		node.setType(node.getFunctionType());
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

		for (APatternListTypePair pltp: node.getParamPatternList())
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
		
		PDefinitionAssistant.typeCheck(defs, rootVisitor, newInfo); 

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
			if (question.env.isVDMPP()) node.getMeasure().setTypeQualifier(node.getFunctionType().getParameters());
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

				if (!TypeComparator.compatible(mtype.getParameters(),node.getFunctionType().getParameters()))
				{
					TypeCheckerErrors.report(3303, "Measure parameters different to function",node.getMeasure().getLocation(),node.getMeasure());
					TypeCheckerErrors.detail2(node.getMeasure().name, mtype.getParameters(), node.getName().name,node.getFunctionType().getParameters());
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
		
		node.setType(node.getFunctionType());
		return node.getType();
	}
	
	
	@Override
	public PType caseAExplicitOperationDefinition(
			AExplicitOperationDefinition node, TypeCheckInfo question) {
		
		question.scope = NameScope.NAMESANDSTATE;
		List<PType> ptypes = node.getOpType().getParameters();

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
		PDefinitionAssistant.typeCheck(node.getParamDefinitions(), rootVisitor, question); 

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

				if (PTypeAssistant.isClass(node.getOpType().getResult()))
				{
					AClassType ctype = PTypeAssistant.getClassType(node.getOpType().getResult());

					if (ctype.getClassdef() != node.getClassDefinition())
					{
						TypeCheckerErrors.report(3025,
							"Constructor operation must have return type " + node.getClassDefinition().getName().name,node.getOpType().getResult().getLocation(),node.getOpType().getResult());
					}
				}
				else
				{
					TypeCheckerErrors.report(3026,
						"Constructor operation must have return type " + node.getClassDefinition().getName().name,node.getOpType().getResult().getLocation(),node.getOpType().getResult());
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
			List<PDefinition> rdefs = PPatternAssistant.getDefinitions(rp,node.getOpType().getResult(), NameScope.NAMESANDANYSTATE);
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
		boolean compatible = TypeComparator.compatible(node.getOpType().getResult(), node.getActualResult());

		if ((node.getIsConstructor() && !PTypeAssistant.isType(node.getActualResult(), AVoidType.class)  && !compatible) ||
			(!node.getIsConstructor() && !compatible))
		{
			TypeCheckerErrors.report(3027, "Operation returns unexpected type",node.getLocation(),node);
			TypeCheckerErrors.detail2("Actual", node.getActualResult(), "Expected", node.getOpType().getResult());
		}

		if (PAccessSpecifierAssistant.isAsync(node.getAccess()) && ! PTypeAssistant.isType(node.getOpType().getResult(), AVoidType.class))
		{
			TypeCheckerErrors.report(3293, "Asynchronous operation " + node.getName() + " cannot return a value",node.getLocation(),node);
		}

		if (PTypeAssistant.narrowerThan(node.getOpType(), node.getAccess()))
		{
			TypeCheckerErrors.report(3028, "Operation parameter visibility less than operation definition",node.getLocation(),node);
		}

		if (!(node.getBody() instanceof ANotYetSpecifiedStm) &&
			!(node.getBody() instanceof ASubclassResponsibilityStm))
		{
			local.unusedCheck();
		}
		node.setType(node.getOpType());
		return node.getOpType();
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

		for (APatternListTypePair ptp: node.getParameterPatternList())
		{
			argdefs.addAll(APatternListTypePairAssistant.getDefinitions(ptp, NameScope.LOCAL));
		}

		defs.addAll(new Vector<PDefinition>(argdefs));

		if (node.getResult() != null)
		{
			defs.addAll(
					PPatternAssistant.getDefinitions(node.getResult().getPattern(), node.getOpType().getResult(), NameScope.LOCAL));
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

		PDefinitionAssistant.typeCheck(defs, rootVisitor, question);

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

    				if (PTypeAssistant.isClass(node.getOpType().getResult()))
    				{
    					AClassType ctype = PTypeAssistant.getClassType(node.getOpType().getResult());

    					if (ctype.getClassdef() != node.getClassDefinition())
    					{
    						TypeCheckerErrors.report(3025,
    							"Constructor operation must have return type " + node.getClassDefinition().getName().name,node.getOpType().getResult().getLocation(),node.getOpType().getResult());
    					}
    				}
    				else
    				{
    					TypeCheckerErrors.report(3026,
    						"Constructor operation must have return type " + node.getClassDefinition().getName().name,node.getOpType().getLocation(),node.getOpType());
    				}
    			}
			}
			question.env = local;
			question.qualifiers = null;
			question.scope = NameScope.NAMESANDSTATE;
			node.setActualResult( node.getBody().apply(rootVisitor,question));
			
			boolean compatible = TypeComparator.compatible(node.getOpType().getResult(), node.getActualResult());

			if ((node.getIsConstructor() && !PTypeAssistant.isType(node.getActualResult(),AVoidType.class) && !compatible) ||
				(!node.getIsConstructor() && !compatible))
			{
				TypeCheckerErrors.report(3035, "Operation returns unexpected type",node.getLocation(),node);
				TypeCheckerErrors.detail2("Actual", node.getActualResult(), "Expected", node.getOpType().getResult());
			}
		}

		if (PAccessSpecifierAssistant.isAsync(node.getAccess()) && !PTypeAssistant.isType(node.getOpType().getResult(), AVoidType.class))
		{
			TypeCheckerErrors.report(3293, "Asynchronous operation " + node.getName() + " cannot return a value",node.getLocation(),node);
		}

		if (PTypeAssistant.narrowerThan(node.getOpType(), node.getAccess()))
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
}
