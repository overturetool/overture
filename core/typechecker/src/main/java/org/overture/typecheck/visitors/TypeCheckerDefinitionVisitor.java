package org.overture.typecheck.visitors;

import java.util.List;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AAssignmentDefinition;
import org.overture.ast.definitions.ABusClassDefinition;
import org.overture.ast.definitions.AClassInvariantDefinition;
import org.overture.ast.definitions.AEqualsDefinition;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.assistants.AExplicitFunctionDefinitionAssistant;
import org.overture.ast.definitions.assistants.PAccessSpecifierAssistant;
import org.overture.ast.definitions.assistants.PDefinitionAssistant;
import org.overture.ast.expressions.ANotYetSpecifiedExp;
import org.overture.ast.expressions.ASubclassResponsibilityExp;
import org.overture.ast.expressions.AUndefinedExp;
import org.overture.ast.node.NodeList;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.ATypeBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.patterns.assistants.ATypeBindAssistant;
import org.overture.ast.patterns.assistants.PPatternAssistant;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.ANatNumericBasicType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.AVoidType;
import org.overture.ast.types.EType;
import org.overture.ast.types.PType;
import org.overture.ast.types.assistants.PTypeAssistant;
import org.overture.runtime.Environment;
import org.overture.runtime.FlatCheckedEnvironment;
import org.overture.runtime.TypeChecker;
import org.overture.runtime.TypeComparator;
import org.overture.typecheck.PrivateClassEnvironment;
import org.overture.typecheck.TypeCheckInfo;
import org.overture.typecheck.TypeCheckerErrors;
import org.overturetool.vdmj.lex.LexNameToken;
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
			TypeCheckerErrors.warning(node,5012, "Recursive function has no measure");
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
	

}
