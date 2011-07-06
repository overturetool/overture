package org.overture.typecheck.visitors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.APerSyncDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.AApplyExp;
import org.overture.ast.expressions.ABooleanConstExp;
import org.overture.ast.expressions.AElseIfExp;
import org.overture.ast.expressions.AFuncInstatiationExp;
import org.overture.ast.expressions.AIfExp;
import org.overture.ast.expressions.AIntConstExp;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AIntNumericBasicType;
import org.overture.ast.types.ANatNumericBasicType;
import org.overture.ast.types.ANatOneNumericBasicType;
import org.overture.ast.types.PType;
import org.overture.ast.types.assistants.AFunctionTypeAssistent;
import org.overture.ast.types.assistants.PTypeAssistant;
import org.overture.runtime.TypeChecker;
import org.overture.typecheck.LexNameTokenAssistent;
import org.overture.typecheck.TypeCheckInfo;
import org.overturetool.vdmj.Release;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.ExplicitFunctionDefinition;
import org.overturetool.vdmj.definitions.ImplicitFunctionDefinition;
import org.overturetool.vdmj.definitions.PerSyncDefinition;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.expressions.FuncInstantiationExpression;
import org.overturetool.vdmj.expressions.VariableExpression;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.types.FunctionType;
import org.overturetool.vdmj.types.MapType;
import org.overturetool.vdmj.types.OperationType;
import org.overturetool.vdmj.types.SeqType;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.types.TypeSet;
import org.overturetool.vdmj.types.UnknownType;

public class TypeCheckerExpVisitor extends
		QuestionAnswerAdaptor<TypeCheckInfo, PType> {
	
	private QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor;

	public TypeCheckerExpVisitor(TypeCheckVisitor typeCheckVisitor) {
		this.rootVisitor = typeCheckVisitor;
	}

	
	@Override
	public PType caseAApplyExp(AApplyExp node, TypeCheckInfo question) {
		node.setArgtypes(new ArrayList<PType>());
		
		for (PExp a: node.getArgs())
		{
			node.getArgtypes().add(a.apply(rootVisitor, question));
		}

		question.qualifiers = node.getArgtypes();
		node.setType(node.getRoot().apply(rootVisitor, question));

		if (PTypeAssistant.isUnknown(node.getType()))
		{
			return node.getType();
		}

		PDefinition func = question.env.getEnclosingDefinition();

		boolean inFunction =
			(func instanceof AExplicitFunctionDefinition ||
			 func instanceof AImplicitFunctionDefinition ||
			 func instanceof APerSyncDefinition);

		if (inFunction)
		{
			LexNameToken called = null;

			if (node.getRoot() instanceof AVariableExp)
    		{
    			AVariableExp var = (AVariableExp)node.getRoot();
    			called = var.getName();
    		}
			else if (node.getRoot() instanceof AFuncInstatiationExp)
			{
				AFuncInstatiationExp fie = (AFuncInstatiationExp)node.getRoot();

				if (fie.getExpdef() != null)
				{
					called = fie.getExpdef().getName();
				}
				else if (fie.getImpdef() != null)
				{
					called = fie.getImpdef().getName();
				}
			}

			if (called != null)
			{
    			if (func instanceof AExplicitFunctionDefinition)
    			{
    				AExplicitFunctionDefinition def = (AExplicitFunctionDefinition)func;

        			if (LexNameTokenAssistent.isEqual(called,def.getName()))
        			{
        				node.setRecursive(def);
        				def.setRecursive(true);
        			}
    			}
    			else if (func instanceof AImplicitFunctionDefinition)
    			{
    				AImplicitFunctionDefinition def = (AImplicitFunctionDefinition)func;

        			if (LexNameTokenAssistent.isEqual(called,def.getName()))
        			{
        				node.setRecursive(def);
        				def.setRecursive(true);
        			}
    			}
			}
		}

		boolean isSimple = !PTypeAssistant.isUnion(node.getType());
		Set<PType> results = new HashSet<PType>();

		if (PTypeAssistant.isFunction(node.getType()))
		{
			AFunctionType ft = PTypeAssistant.getFunction(node.getType());
			AFunctionTypeAssistent.typeResolve(ft,question.env, null);
			results.add(functionApply(isSimple, ft));
		}

		if (type.isOperation())
		{
			OperationType ot = type.getOperation();
			ot.typeResolve(env, null);

			if (inFunction && Settings.release == Release.VDM_10)
			{
				report(3300, "Operation '" + root + "' cannot be called from a function");
				results.add(new UnknownType(location));
			}
			else
			{
    			results.add(operationApply(isSimple, ot));
			}
		}

		if (type.isSeq())
		{
			SeqType seq = type.getSeq();
			results.add(sequenceApply(isSimple, seq));
		}

		if (type.isMap())
		{
			MapType map = type.getMap();
			results.add(mapApply(isSimple, map));
		}

		if (results.isEmpty())
		{
			report(3054, "Type " + type + " cannot be applied");
			return new UnknownType(location);
		}

		return results.getType(location);	// Union of possible applications
	}
	
	@Override
	public PType caseAIfExp(AIfExp node, TypeCheckInfo question) {
		
		if (!PTypeAssistant.isType(node.getTest().apply(this, question),ABooleanBasicType.class))
		{
			TypeChecker.report(3108, "If expression is not a boolean",node.getLocation());
		}

		Set<PType> rtypes = new HashSet<PType>();
		rtypes.add(node.getThen().apply(rootVisitor, question));

		for (AElseIfExp eie: node.getElseList())
		{
			rtypes.add(eie.apply(rootVisitor, question));
		}

		rtypes.add(node.getElse().apply(rootVisitor, question));

		node.setType(PTypeAssistant.getType(rtypes,node.getLocation()));
		return node.getType();
	}
	
	
	@Override
	public PType caseAIntConstExp(AIntConstExp node, TypeCheckInfo question) {
		if (node.getValue().value < 0)
		{
			node.setType(new AIntNumericBasicType(node.getLocation()));
		}
		else if (node.getValue().value == 0)
		{
			node.setType(new ANatNumericBasicType(node.getLocation()));
		}
		else
		{
			node.setType(new ANatOneNumericBasicType(node.getLocation()));
		}
		
		return node.getType();
	}
	
	
	@Override
	public PType caseABooleanConstExp(ABooleanConstExp node,
			TypeCheckInfo question) {
		node.setType(new ABooleanBasicType(node.getLocation())); 
		return node.getType();
	}
	
}
