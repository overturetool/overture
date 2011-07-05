package org.overture.typecheck;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.AElseIfExp;
import org.overture.ast.expressions.AIfExp;
import org.overture.ast.expressions.ANotYetSpecifiedExp;
import org.overture.ast.expressions.ASubclassResponsibilityExp;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.Node;
import org.overture.ast.node.NodeList;
import org.overture.ast.node.tokens.TNumbersLiteral;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.ANatNumericBasicType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.EType;
import org.overture.ast.types.PType;
import org.overture.runtime.FlatCheckedEnvironment;
import org.overture.runtime.HelperDefinition;
import org.overture.runtime.HelperPattern;
import org.overture.runtime.HelperType;
import org.overture.runtime.TypeChecker;
import org.overture.runtime.TypeComparator;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.lex.LexNameTokenImpl;
import org.overturetool.vdmj.typechecker.NameScope;








public class TypeCheckVisitor  extends QuestionAnswerAdaptor<TypeCheckInfo, PType> {

	@Override
	public PType defaultNode(Node node, TypeCheckInfo question) {
		
		return super.defaultNode(node, question);
	}
	
	
	@Override
	public PType caseAModuleModules(AModuleModules node, TypeCheckInfo question) {
		System.out.println("Visiting Module: "+ node.getName());
		for (PDefinition def : node.getDefs()) {
			def.apply(this, null);
		}
		
		return null;
	}
			
	
	@Override
	public PType caseAExplicitFunctionDefinition(
			AExplicitFunctionDefinition node, TypeCheckInfo question) {
		System.out.println("Visiting Explicit Function Def: " + node.getName());
		
		NodeList<PDefinition> defs = new NodeList<PDefinition>(node);

		if (node.getTypeParams() != null)
		{
			defs.addAll(HelperAExplicitFunctionDefinition.getTypeParamDefinitions(node));
		}

		PType expectedResult = HelperAExplicitFunctionDefinition.checkParams(node,node.getParamPatternList().listIterator(), node.getFunctionType());

		List<List<PDefinition>> paramDefinitionList = HelperAExplicitFunctionDefinition.getParamDefinitions(node,node.getFunctionType(), node.getParamPatternList(),node.getLocation());

		for (List<PDefinition> pdef: paramDefinitionList)
		{
			defs.addAll(pdef);	// All definitions of all parameter lists
		}

		FlatCheckedEnvironment local = new FlatCheckedEnvironment(defs,question.env, question.scope);
		
		//TODO: access specifier not defined
//		local.setStatic(accessSpecifier);
		local.setEnclosingDefinition(node);

		//building the new scope for subtypechecks
		TypeCheckInfo info = new TypeCheckInfo();
		info.env = local;
		info.scope = question.scope;
		info.qualifiers = question.qualifiers;
		HelperDefinition.typeCheck(defs,info,this);

		if (question.env.isVDMPP()) //TODO:Access specifier: && !accessSpecifier.isStatic)
		{
			local.add(HelperDefinition.getSelfDefinition(node));
		}
 
		if (node.getPredef() != null)
		{
			//building the new scope for subtypechecks
			
			info.env = local;
			info.scope = NameScope.NAMES;
			info.qualifiers = null;
			PType b = node.getPredef().getBody().apply(this, question);
			ABooleanBasicType expected = new ABooleanBasicType(node.getLocation(), null);

			if (!HelperType.isType(b,ABooleanBasicType.class))
			{
				TypeChecker.report(3018, "Precondition returns unexpected type",node.getLocation());
				TypeChecker.detail2("Actual", b, "Expected", expected);
			}
		}

		if (node.getPostdef() != null)
		{
			LexNameToken result = new LexNameTokenImpl(node.getName().getModule(), "RESULT", node.getLocation());
			PPattern rp = new AIdentifierPattern(null,null,result);
			List<PDefinition> rdefs = HelperPattern.getDefinitions(rp,expectedResult, NameScope.NAMES);
			FlatCheckedEnvironment post =
				new FlatCheckedEnvironment(rdefs, local, NameScope.NAMES);

			//building the new scope for subtypechecks
			info.env = post;
			info.scope = NameScope.NAMES;
			info.qualifiers = null;			
			PType b = node.getPostdef().getBody().apply(this, info);
			ABooleanBasicType expected = new ABooleanBasicType(node.getLocation(),null);

			if (!HelperType.isType(b,ABooleanBasicType.class))
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
		
		node.setActualResult(node.getBody().apply(this,question));

		if (!TypeComparator.compatible(expectedResult, node.getActualResult()))
		{
			TypeChecker.report(3018, "Function returns unexpected type",node.getLocation());
			TypeChecker.detail2("Actual", node.getActualResult(), "Expected", expectedResult);
		}

		//TODO:Access Specifier
//		if (node.getType().narrowerThan(accessSpecifier))
//		{
//			report(3019, "Function parameter visibility less than function definition");
//		}

		if (node.getMeasure() == null && node.getRecursive())
		{
			HelperDefinition.warning(node,5012, "Recursive function has no measure");
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
						AProductType pt = HelperType.getProduct(mtype.getResult());

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
		
		//TODO:What to return
		return null;
	}
	
	


	@Override
	public PType caseAIfExp(AIfExp node, TypeCheckInfo question) {
		
		if (!HelperType.isType(node.getTest().apply(this, question),ABooleanBasicType.class))
		{
			TypeChecker.report(3108, "If expression is not a boolean",node.getLocation());
		}

		Set<PType> rtypes = new HashSet<PType>();
		rtypes.add(node.getThen().apply(this, question));

		for (AElseIfExp eie: node.getElseList())
		{
			rtypes.add(eie.apply(this, question));
		}

		rtypes.add(node.getElse().apply(this, question));

		return HelperType.getType(rtypes,node.getLocation());		
	}
	
	
	
}


