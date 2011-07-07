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
import org.overture.ast.expressions.ACompBinaryExp;
import org.overture.ast.expressions.AElseIfExp;
import org.overture.ast.expressions.AFuncInstatiationExp;
import org.overture.ast.expressions.AIfExp;
import org.overture.ast.expressions.AIntConstExp;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.expressions.SBooleanBinaryExp;
import org.overture.ast.expressions.assistants.AApplyExpAssistant;
import org.overture.ast.expressions.assistants.PExpAssistant;
import org.overture.ast.expressions.assistants.SBinaryExpAssistant;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AIntNumericBasicType;
import org.overture.ast.types.AMapType;
import org.overture.ast.types.ANatNumericBasicType;
import org.overture.ast.types.ANatOneNumericBasicType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.ASeqType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overture.ast.types.assistants.AFunctionTypeAssistent;
import org.overture.ast.types.assistants.AOperationTypeAssistant;
import org.overture.ast.types.assistants.PTypeAssistant;
import org.overture.runtime.TypeChecker;
import org.overture.runtime.TypeComparator;
import org.overture.typecheck.LexNameTokenAssistent;
import org.overture.typecheck.TypeCheckInfo;
import org.overturetool.vdmj.Release;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.lex.LexNameToken;






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
			AFunctionTypeAssistent.typeResolve(ft,question.env, null,rootVisitor,question);
			results.add(AApplyExpAssistant.functionApply(node,isSimple, ft));
		}

		if (PTypeAssistant.isOperation(node.getType()))
		{
			AOperationType ot = PTypeAssistant.getOperation(node.getType());
			AOperationTypeAssistant.typeResolve(ot,question.env, null,rootVisitor,question);

			if (inFunction && Settings.release == Release.VDM_10)
			{
				PExpAssistant.report(3300, "Operation '" + node.getRoot() + "' cannot be called from a function",node);
				results.add(new AUnknownType(node.getLocation(),false));
			}
			else
			{
    			results.add(AApplyExpAssistant.operationApply(node,isSimple, ot));
			}
		}

		if (PTypeAssistant.isSeq(node.getType()))
		{
			ASeqType seq = PTypeAssistant.getSeq(node.getType());
			results.add(AApplyExpAssistant.sequenceApply(node,isSimple, seq));
		}

		if (PTypeAssistant.isMap(node.getType()))
		{
			AMapType map = PTypeAssistant.getMap(node.getType());
			results.add(AApplyExpAssistant.mapApply(node,isSimple, map));
		}

		if (results.isEmpty())
		{
			PExpAssistant.report(3054, "Type " + node.getType() + " cannot be applied",node);
			return new AUnknownType(node.getLocation(),false);
		}

		node.setType(PTypeAssistant.getType(results,node.getLocation()));
		return node.getType();	// Union of possible applications
	}
	
	
	@Override
	public PType caseSBooleanBinaryExp(SBooleanBinaryExp node,
			TypeCheckInfo question) {
		
		SBinaryExpAssistant.binaryCheck(node, new ABooleanBasicType(node.getLocation(), true),rootVisitor,question);
		return node.getType();
		
	}
	
	@Override
	public PType caseACompBinaryExp(ACompBinaryExp node, TypeCheckInfo question) {
		node.setLtype(node.getLeft().apply(rootVisitor, question));
		node.setRtype(node.getRight().apply(rootVisitor, question));
		
		Set<PType> results = new HashSet<PType>();

		if (PTypeAssistant.isMap(node.getLtype()))
		{
    		if (!PTypeAssistant.isMap(node.getRtype()))
    		{
    			PExpAssistant.report(3068, "Right hand of map 'comp' is not a map",node);
    			PExpAssistant.detail("Type", node.getRtype());
    			return new AMapType(node.getLocation(),false, null, null, null);	// Unknown types
    		}

    		AMapType lm = PTypeAssistant.getMap(node.getLtype());
    		AMapType rm = PTypeAssistant.getMap(node.getRtype());

    		if (!TypeComparator.compatible(lm.getFrom(), rm.getTo()))
    		{
    			PExpAssistant.report(3069, "Domain of left should equal range of right in map 'comp'",node);
    			PExpAssistant.detail2("Dom", lm.getFrom(), "Rng", rm.getTo());
    		}

    		results.add(new AMapType(node.getLocation(), false, rm.getFrom(), lm.getTo(), null));
		}

		if (PTypeAssistant.isFunction(node.getLtype()))
		{
    		if (!PTypeAssistant.isFunction(node.getRtype()))
    		{
    			PExpAssistant.report(3070, "Right hand of function 'comp' is not a function",node);
    			PExpAssistant.detail("Type", node.getRtype());
    			return new AUnknownType(node.getLocation(),false);
    		}
    		else
    		{
        		AFunctionType lf = PTypeAssistant.getFunction(node.getLtype());
        		AFunctionType rf = PTypeAssistant.getFunction(node.getRtype());

        		if (lf.getParameters().size() != 1)
        		{
        			PExpAssistant.report(3071, "Left hand function must have a single parameter",node);
        			PExpAssistant.detail("Type", lf);
        		}
        		else if (rf.getParameters().size() != 1)
        		{
        			PExpAssistant.report(3072, "Right hand function must have a single parameter",node);
        			PExpAssistant.detail("Type", rf);
        		}
        		else if (!TypeComparator.compatible(lf.getParameters().get(0), rf.getResult()))
        		{
        			PExpAssistant.report(3073, "Parameter of left should equal result of right in function 'comp'",node);
        			PExpAssistant.detail2("Parameter", lf.getParameters().get(0), "Result", rf.getResult());
        		}

        		results.add(
        			new AFunctionType(node.getLocation(), false, true, rf.getParameters(), lf.getResult()));
    		}
		}

		if (results.isEmpty())
		{
			PExpAssistant.report(3074, "Left hand of 'comp' is neither a map nor a function",node);
			PExpAssistant.detail("Type", node.getLtype());
			return new AUnknownType(node.getLocation(),false);
		}

		node.setType( PTypeAssistant.getType(results,node.getLocation()));		
		return node.getType();
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
			node.setType(new AIntNumericBasicType(node.getLocation(),true));
		}
		else if (node.getValue().value == 0)
		{
			node.setType(new ANatNumericBasicType(node.getLocation(),true));
		}
		else
		{
			node.setType(new ANatOneNumericBasicType(node.getLocation(),true));
		}
		
		return node.getType();
	}
	
	
	@Override
	public PType caseABooleanConstExp(ABooleanConstExp node,
			TypeCheckInfo question) {
		node.setType(new ABooleanBasicType(node.getLocation(), true)); 
		return node.getType();
	}
	
}
