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
import org.overture.ast.expressions.ACaseAlternative;
import org.overture.ast.expressions.ACasesExp;
import org.overture.ast.expressions.ACompBinaryExp;
import org.overture.ast.expressions.ADivNumericBinaryExp;
import org.overture.ast.expressions.ADivideNumericBinaryExp;
import org.overture.ast.expressions.ADomainResByBinaryExp;
import org.overture.ast.expressions.ADomainResToBinaryExp;
import org.overture.ast.expressions.AElseIfExp;
import org.overture.ast.expressions.AEqualsBinaryExp;
import org.overture.ast.expressions.AFuncInstatiationExp;
import org.overture.ast.expressions.AGreaterEqualNumericBinaryExp;
import org.overture.ast.expressions.AGreaterNumericBinaryExp;
import org.overture.ast.expressions.AIfExp;
import org.overture.ast.expressions.AInSetBinaryExp;
import org.overture.ast.expressions.AIntConstExp;
import org.overture.ast.expressions.AMapUnionBinaryExp;
import org.overture.ast.expressions.AModNumericBinaryExp;
import org.overture.ast.expressions.ANotEqualBinaryExp;
import org.overture.ast.expressions.ANotInSetBinaryExp;
import org.overture.ast.expressions.APlusNumericBinaryExp;
import org.overture.ast.expressions.APlusPlusBinaryExp;
import org.overture.ast.expressions.AProperSubsetBinaryExp;
import org.overture.ast.expressions.ARangeResByBinaryExp;
import org.overture.ast.expressions.ARangeResToBinaryExp;
import org.overture.ast.expressions.ARemNumericBinaryExp;
import org.overture.ast.expressions.ASeqConcatBinaryExp;
import org.overture.ast.expressions.ASetDifferenceBinaryExp;
import org.overture.ast.expressions.ASetIntersectBinaryExp;
import org.overture.ast.expressions.ASetUnionBinaryExp;
import org.overture.ast.expressions.AStarStarBinaryExp;
import org.overture.ast.expressions.ASubsetBinaryExp;
import org.overture.ast.expressions.ASubstractNumericBinaryExp;
import org.overture.ast.expressions.ATimesNumericBinaryExp;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.expressions.SBooleanBinaryExp;
import org.overture.ast.expressions.assistants.AApplyExpAssistant;
import org.overture.ast.expressions.assistants.ACaseAlternativeAssistant;
import org.overture.ast.expressions.assistants.PExpAssistant;
import org.overture.ast.expressions.assistants.SBinaryExpAssistant;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AIntNumericBasicType;
import org.overture.ast.types.AMapType;
import org.overture.ast.types.ANatNumericBasicType;
import org.overture.ast.types.ANatOneNumericBasicType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.ARealNumericBasicType;
import org.overture.ast.types.ASeq1Type;
import org.overture.ast.types.ASeqType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SNumericBasicType;
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
		node.getLeft().apply(rootVisitor, question);
		node.getRight().apply(rootVisitor, question);
		
		Set<PType> results = new HashSet<PType>();

		if (PTypeAssistant.isMap(node.getLeft().getType()))
		{
    		if (!PTypeAssistant.isMap(node.getRight().getType()))
    		{
    			PExpAssistant.report(3068, "Right hand of map 'comp' is not a map",node);
    			PExpAssistant.detail("Type", node.getRight().getType());
    			return new AMapType(node.getLocation(),false, null, null, null);	// Unknown types
    		}

    		AMapType lm = PTypeAssistant.getMap(node.getLeft().getType());
    		AMapType rm = PTypeAssistant.getMap(node.getRight().getType());

    		if (!TypeComparator.compatible(lm.getFrom(), rm.getTo()))
    		{
    			PExpAssistant.report(3069, "Domain of left should equal range of right in map 'comp'",node);
    			PExpAssistant.detail2("Dom", lm.getFrom(), "Rng", rm.getTo());
    		}

    		results.add(new AMapType(node.getLocation(), false, rm.getFrom(), lm.getTo(), null));
		}

		if (PTypeAssistant.isFunction(node.getLeft().getType()))
		{
    		if (!PTypeAssistant.isFunction(node.getRight().getType()))
    		{
    			PExpAssistant.report(3070, "Right hand of function 'comp' is not a function",node);
    			PExpAssistant.detail("Type", node.getRight().getType());
    			return new AUnknownType(node.getLocation(),false);
    		}
    		else
    		{
        		AFunctionType lf = PTypeAssistant.getFunction(node.getLeft().getType());
        		AFunctionType rf = PTypeAssistant.getFunction(node.getRight().getType());

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
			PExpAssistant.detail("Type", node.getLeft().getType());
			return new AUnknownType(node.getLocation(),false);
		}

		node.setType( PTypeAssistant.getType(results,node.getLocation()));		
		return node.getType();
	}
	
	
	@Override
	public PType caseADomainResByBinaryExp(ADomainResByBinaryExp node,
			TypeCheckInfo question) {
		node.getLeft().apply(rootVisitor,question);
		node.getRight().apply(rootVisitor, question);

		if (!PTypeAssistant.isSet(node.getLeft().getType()))
		{
			PExpAssistant.report(3079, "Left of '<-:' is not a set",node);
		}
		else if (!PTypeAssistant.isMap(node.getRight().getType()))
		{
			PExpAssistant.report(3080, "Right of '<-:' is not a map",node);
		}
		else
		{
			ASetType set = PTypeAssistant.getSet(node.getLeft().getType());
			AMapType map = PTypeAssistant.getMap(node.getRight().getType());

			if (!TypeComparator.compatible(set.getSetof(), map.getFrom()))
			{
				PExpAssistant.report(3081, "Restriction of map should be set of " + map.getFrom(),node);
			}
		}

		node.setType(node.getRight().getType());
		return node.getType();
	}
	
	@Override
	public PType caseADomainResToBinaryExp(ADomainResToBinaryExp node,
			TypeCheckInfo question) {
		node.getLeft().apply(rootVisitor,question);
		node.getRight().apply(rootVisitor, question);

		if (!PTypeAssistant.isSet(node.getLeft().getType()))
		{
			PExpAssistant.report(3082, "Left of '<:' is not a set",node);
			PExpAssistant.detail("Actual", node.getLeft().getType());
		}
		else if (!PTypeAssistant.isMap(node.getRight().getType()))
		{
			PExpAssistant.report(3083, "Right of '<:' is not a map",node);
			PExpAssistant.detail("Actual", node.getRight().getType());
		}
		else
		{
			ASetType set = PTypeAssistant.getSet(node.getLeft().getType());
			AMapType map = PTypeAssistant.getMap(node.getRight().getType());

			if (!TypeComparator.compatible(set.getSetof(), map.getFrom()))
			{
				PExpAssistant.report(3084, "Restriction of map should be set of " + map.getFrom(),node);
			}
		}

		node.setType(node.getRight().getType());
		return node.getType();
	}
	
	@Override
	public PType caseAEqualsBinaryExp(AEqualsBinaryExp node,
			TypeCheckInfo question) {
		node.getLeft().apply(rootVisitor,question);
		node.getRight().apply(rootVisitor, question);

		if (!TypeComparator.compatible(node.getLeft().getType(), node.getRight().getType()))
		{
			PExpAssistant.report(3087, "Left and right of '=' are incompatible types",node);
			PExpAssistant.detail2("Left", node.getLeft().getType(), "Right", node.getRight().getType());
		}

		node.setType(new ABooleanBasicType(node.getLocation(),true));
		return node.getType();
	}
	
	@Override
	public PType caseAInSetBinaryExp(AInSetBinaryExp node,
			TypeCheckInfo question) {
		node.getLeft().apply(rootVisitor,question);
		node.getRight().apply(rootVisitor, question);

		if (!PTypeAssistant.isSet(node.getRight().getType()))
		{
			PExpAssistant.report(3110, "Argument of 'in set' is not a set",node);
		}

		node.setType(new ABooleanBasicType(node.getLocation(),true));
		return node.getType();
	}
	
	
	@Override
	public PType caseAMapUnionBinaryExp(AMapUnionBinaryExp node,
			TypeCheckInfo question) {
		node.getLeft().apply(rootVisitor,question);
		node.getRight().apply(rootVisitor, question);


		if (!PTypeAssistant.isMap(node.getLeft().getType()))
		{
			PExpAssistant.report(3123, "Left hand of 'munion' is not a map",node);
			PExpAssistant.detail("Type", node.getLeft().getType());
			node.setType(new AMapType(node.getLocation(),false,null, null, null ));	// Unknown types
			return node.getType();
		}
		else if (!PTypeAssistant.isMap(node.getRight().getType()))
		{
			PExpAssistant.report(3124, "Right hand of 'munion' is not a map",node);
			PExpAssistant.detail("Type", node.getRight().getType());
			node.setType(node.getLeft().getType());
			return node.getType();
		}
		else
		{
			AMapType ml = PTypeAssistant.getMap(node.getLeft().getType());
			AMapType mr = PTypeAssistant.getMap(node.getRight().getType());

			Set<PType> from = new HashSet<PType>();
			from.add(ml.getFrom()); from.add(mr.getFrom());
			Set<PType> to =  new HashSet<PType>();
			to.add(ml.getTo()); to.add(mr.getTo());

			node.setType(new AMapType(node.getLocation(),false,
					PTypeAssistant.getType(from,node.getLocation()), PTypeAssistant.getType(to,node.getLocation()), null));
			return node.getType();
		}
	}
	
	@Override
	public PType caseANotEqualBinaryExp(ANotEqualBinaryExp node,
			TypeCheckInfo question) {
		node.getLeft().apply(rootVisitor,question);
		node.getRight().apply(rootVisitor, question);

		if (!TypeComparator.compatible(node.getLeft().getType(), node.getRight().getType()))
		{
			PExpAssistant.report(3136, "Left and right of '<>' different types",node);
			PExpAssistant.detail2("Left", node.getLeft().getType(), "Right", node.getRight().getType());
		}

		node.setType(new ABooleanBasicType(node.getLocation(),true));
		return node.getType();
	}
	
	@Override
	public PType caseANotInSetBinaryExp(ANotInSetBinaryExp node,
			TypeCheckInfo question) {
		node.getLeft().apply(rootVisitor,question);
		node.getRight().apply(rootVisitor, question);

		if (!PTypeAssistant.isSet(node.getRight().getType()))
		{
			PExpAssistant.report(3138, "Argument of 'not in set' is not a set",node);
			PExpAssistant.detail("Actual", node.getRight().getType());
		}

		node.setType(new ABooleanBasicType(node.getLocation(),true));
		return node.getType();
	}
	
	
	@Override
	public PType caseADivNumericBinaryExp(ADivNumericBinaryExp node,
			TypeCheckInfo question) {
		
		SNumericBasicTypeAssistant.checkNumeric(node,rootVisitor,question);
		node.setType(new AIntNumericBasicType(node.getLocation(),false));
		return node.getType() ;
	}
	
	@Override
	public PType caseADivideNumericBinaryExp(ADivideNumericBinaryExp node,
			TypeCheckInfo question) {
		
		SNumericBasicTypeAssistant.checkNumeric(node,rootVisitor,question);
		node.setType(new ARealNumericBasicType(node.getLocation(),false));
		return node.getType() ;
	}
	
	@Override
	public PType caseAGreaterEqualNumericBinaryExp(
			AGreaterEqualNumericBinaryExp node, TypeCheckInfo question) {
		
		SNumericBasicTypeAssistant.checkNumeric(node,rootVisitor,question);
		node.setType(new ABooleanBasicType(node.getLocation(),false));
		return node.getType() ;
	}
	
	@Override
	public PType caseAGreaterNumericBinaryExp(AGreaterNumericBinaryExp node,
			TypeCheckInfo question) {
		
		SNumericBasicTypeAssistant.checkNumeric(node,rootVisitor,question);
		node.setType(new ABooleanBasicType(node.getLocation(),false));
		return node.getType() ;
	}
	
	@Override
	public PType caseAModNumericBinaryExp(AModNumericBinaryExp node,
			TypeCheckInfo question) {
	
		SNumericBasicTypeAssistant.checkNumeric(node,rootVisitor,question);
		node.setType(new ANatNumericBasicType(node.getLocation(),false));
		return node.getType() ;
		
	}
	
	@Override
	public PType caseAPlusNumericBinaryExp(APlusNumericBinaryExp node,
			TypeCheckInfo question) {
		SNumericBasicTypeAssistant.checkNumeric(node,rootVisitor,question);

		SNumericBasicType ln = PTypeAssistant.getNumeric(node.getLeft().getType());
		SNumericBasicType rn = PTypeAssistant.getNumeric(node.getRight().getType());

		if (ln instanceof ARealNumericBasicType)
		{
			return ln;
		}
		else if (rn instanceof ARealNumericBasicType)
		{
			return rn;
		}
		else if (ln instanceof AIntNumericBasicType)
		{
			return ln;
		}
		else if (rn instanceof AIntNumericBasicType)
		{
			return rn;
		}
		else if (ln instanceof ANatNumericBasicType && rn instanceof ANatNumericBasicType)
		{
			return ln;
		}
		else
		{
			return new ANatOneNumericBasicType(ln.getLocation(),false);
		}
	}
	
	
	@Override
	public PType caseARemNumericBinaryExp(ARemNumericBinaryExp node,
			TypeCheckInfo question) {
		
		SNumericBasicTypeAssistant.checkNumeric(node,rootVisitor,question);
		node.setType(new AIntNumericBasicType(node.getLocation(),false));
		return node.getType() ;
		
	}
	
	
	@Override
	public PType caseASubstractNumericBinaryExp(
			ASubstractNumericBinaryExp node, TypeCheckInfo question) {
		
		SNumericBasicTypeAssistant.checkNumeric(node,rootVisitor,question);

		if (node.getLeft().getType() instanceof ARealNumericBasicType || node.getRight().getType() instanceof ARealNumericBasicType)
		{
			return new ARealNumericBasicType(node.getLocation(),false);
		}
		else
		{
			return new AIntNumericBasicType(node.getLocation(), false);
		}
	}
	
	@Override
	public PType caseATimesNumericBinaryExp(ATimesNumericBinaryExp node,
			TypeCheckInfo question) {

		SNumericBasicTypeAssistant.checkNumeric(node,rootVisitor,question);

		SNumericBasicType ln = PTypeAssistant.getNumeric(node.getLeft().getType());
		SNumericBasicType rn = PTypeAssistant.getNumeric(node.getRight().getType());

		if (ln instanceof ARealNumericBasicType)
		{
			return ln;
		}
		else if (rn instanceof ARealNumericBasicType)
		{
			return rn;
		}
		else if (ln instanceof AIntNumericBasicType)
		{
			return ln;
		}
		else if (rn instanceof AIntNumericBasicType)
		{
			return rn;
		}
		else if (ln instanceof ANatNumericBasicType && rn instanceof ANatNumericBasicType)
		{
			return ln;
		}
		else
		{
			return new ANatOneNumericBasicType(ln.getLocation(),false);
		}
	}
	
	
	@Override
	public PType caseAPlusPlusBinaryExp(APlusPlusBinaryExp node,
			TypeCheckInfo question) {
		
		node.getLeft().apply(rootVisitor,question);
		node.getRight().apply(rootVisitor, question);

		Set<PType> result = new HashSet<PType>();
		
		boolean unique = (!PTypeAssistant.isUnion(node.getLeft().getType()) && !PTypeAssistant.isUnion(node.getRight().getType()));

		if (PTypeAssistant.isMap(node.getLeft().getType()))
		{
    		if (!PTypeAssistant.isMap(node.getRight().getType()))
    		{
    			PExpAssistant.concern(unique, 3141, "Right hand of '++' is not a map",node);
    			PExpAssistant.detail(unique, "Type", node.getRight().getType());
    			return new AMapType(node.getLocation(),false, null, null, null );	// Unknown types
    		}

    		AMapType lm = PTypeAssistant.getMap(node.getLeft().getType());
    		AMapType rm = PTypeAssistant.getMap(node.getRight().getType());

    		Set<PType> domain = new HashSet<PType>();
    		domain.add(lm.getFrom()); domain.add(rm.getFrom());
    		Set<PType> range = new HashSet<PType>(); 
    		range.add(lm.getTo()); range.add(rm.getTo());

    		result.add(new AMapType(node.getLocation(),false,
    			PTypeAssistant.getType(domain,node.getLocation()), PTypeAssistant.getType(range,node.getLocation()),false));
		}
			
		if (PTypeAssistant.isSeq(node.getLeft().getType()))
		{
    		ASeqType st = PTypeAssistant.getSeq(node.getLeft().getType());

    		if (!PTypeAssistant.isMap(node.getRight().getType()))
    		{
    			PExpAssistant.concern(unique, 3142, "Right hand of '++' is not a map",node);
    			PExpAssistant.detail(unique, "Type", node.getRight().getType());
    		}
    		else
    		{
        		AMapType mr = PTypeAssistant.getMap(node.getRight().getType());

        		if (!PTypeAssistant.isType(mr.getFrom(),SNumericBasicType.class))
        		{
        			PExpAssistant.concern(unique, 3143, "Domain of right hand of '++' must be nat1",node);
        			PExpAssistant.detail(unique, "Type", mr.getFrom());
        		}
    		}

    		result.add(st);
		}

		if (result.isEmpty())
		{
			PExpAssistant.report(3144, "Left of '++' is neither a map nor a sequence",node);
			node.setType(new AUnknownType(node.getLocation(), false));
			return node.getType();
		}

		node.setType(PTypeAssistant.getType(result, node.getLocation()));
		return node.getType();
	}
	
	@Override
	public PType caseAProperSubsetBinaryExp(AProperSubsetBinaryExp node,
			TypeCheckInfo question) {
		
		node.getLeft().apply(rootVisitor,question);
		node.getRight().apply(rootVisitor, question);

		PType ltype = node.getLeft().getType();
		PType rtype = node.getRight().getType();
		
		if (!PTypeAssistant.isSet(ltype))
		{
			PExpAssistant.report(3146, "Left hand of " + node.getOp() + " is not a set",node);
		}

		if (!PTypeAssistant.isSet(rtype))
		{
			PExpAssistant.report(3147, "Right hand of " + node.getOp() + " is not a set", node);
		}

		return new ABooleanBasicType(node.getLocation(),false);
	}
	
	@Override
	public PType caseARangeResByBinaryExp(ARangeResByBinaryExp node,
			TypeCheckInfo question) {
		
		node.getLeft().apply(rootVisitor,question);
		node.getRight().apply(rootVisitor, question);

		PType ltype = node.getLeft().getType();
		PType rtype = node.getRight().getType();

		if (!PTypeAssistant.isMap(ltype))
		{
			PExpAssistant.report(3148, "Left of ':->' is not a map",node);
		}
		else if (!PTypeAssistant.isSet(rtype))
		{
			PExpAssistant.report(3149, "Right of ':->' is not a set",node);
		}
		else
		{
			AMapType map = PTypeAssistant.getMap(ltype);
			ASetType set = PTypeAssistant.getSet(rtype);

			if (!TypeComparator.compatible(set.getSetof(), map.getTo()))
			{
				PExpAssistant.report(3150, "Restriction of map should be set of " + map.getTo(),node);
			}
		}

		node.setType(ltype);
		return ltype;
	}
	
	
	@Override
	public PType caseARangeResToBinaryExp(ARangeResToBinaryExp node,
			TypeCheckInfo question) {

		node.getLeft().apply(rootVisitor,question);
		node.getRight().apply(rootVisitor, question);

		PType ltype = node.getLeft().getType();
		PType rtype = node.getRight().getType();

		if (!PTypeAssistant.isMap(ltype))
		{
			PExpAssistant.report(3151, "Left of ':>' is not a map",node);
		}
		else if (!PTypeAssistant.isSet(rtype))
		{
			PExpAssistant.report(3152, "Right of ':>' is not a set",node);
		}
		else
		{
			AMapType map = PTypeAssistant.getMap(ltype);
			ASetType set = PTypeAssistant.getSet(rtype);

			if (!TypeComparator.compatible(set.getSetof(), map.getTo()))
			{
				PExpAssistant.report(3153, "Restriction of map should be set of " + map.getTo(),node);
			}
		}
		node.setType(ltype);
		return ltype;
	}
	
	@Override
	public PType caseASeqConcatBinaryExp(ASeqConcatBinaryExp node,
			TypeCheckInfo question) {

		node.getLeft().apply(rootVisitor,question);
		node.getRight().apply(rootVisitor, question);

		PType ltype = node.getLeft().getType();
		PType rtype = node.getRight().getType();


		if (!PTypeAssistant.isSeq(ltype))
		{
			PExpAssistant.report(3157, "Left hand of '^' is not a sequence",node);
			ltype = new ASeqType(node.getLocation(), false, new AUnknownType(node.getLocation(),false), null);
		}

		if (!PTypeAssistant.isSeq(rtype))
		{
			PExpAssistant.report(3158, "Right hand of '^' is not a sequence",node);
			rtype = new ASeqType(node.getLocation(), false, new AUnknownType(node.getLocation(),false), null);
		}

		PType lof = PTypeAssistant.getSeq(ltype);
		PType rof = PTypeAssistant.getSeq(rtype);
		boolean seq1 = (lof instanceof ASeq1Type) || (rof instanceof ASeq1Type);
		
		lof = ((ASeqType)lof).getSeqof();
		rof = ((ASeqType)rof).getSeqof();
		Set<PType> ts = new HashSet<PType>();
		ts.add(lof); ts.add(rof);
		
		node.setType(seq1 ?
			new ASeq1Type(node.getLocation(), false, PTypeAssistant.getType(ts,node.getLocation()),null) :
			new ASeqType(node.getLocation(), false, PTypeAssistant.getType(ts,node.getLocation()),null));
		return node.getType();
	}
	
	
	@Override
	public PType caseASetDifferenceBinaryExp(ASetDifferenceBinaryExp node,
			TypeCheckInfo question) {

		node.getLeft().apply(rootVisitor,question);
		node.getRight().apply(rootVisitor, question);

		PType ltype = node.getLeft().getType();
		PType rtype = node.getRight().getType();


		if (!PTypeAssistant.isSet(ltype))
		{
			PExpAssistant.report(3160, "Left hand of '\\' is not a set",node);
		}

		if (!PTypeAssistant.isSet(rtype))
		{
			PExpAssistant.report(3161, "Right hand of '\\' is not a set",node);
		}

		if (!TypeComparator.compatible(ltype, rtype))
		{
			PExpAssistant.report(3162, "Left and right of '\\' are different types",node);
			PExpAssistant.detail2("Left", ltype, "Right", rtype);
		}

		return ltype;
	}
	
	@Override
	public PType caseASetIntersectBinaryExp(ASetIntersectBinaryExp node,
			TypeCheckInfo question) {

		node.getLeft().apply(rootVisitor,question);
		node.getRight().apply(rootVisitor, question);

		PType ltype = node.getLeft().getType();
		PType rtype = node.getRight().getType();

		if (!PTypeAssistant.isSet(ltype))
		{
			PExpAssistant.report(3163, "Left hand of " + node.getLocation() + " is not a set",node);
		}

		if (!PTypeAssistant.isSet(rtype))
		{
			PExpAssistant.report(3164, "Right hand of " + node.getLocation() + " is not a set",node);
		}


		if (!TypeComparator.compatible(ltype, rtype))
		{
			PExpAssistant.report(3165, "Left and right of intersect are different types",node);
			PExpAssistant.detail2("Left", ltype, "Right", rtype);
		}

		node.setType(ltype);
		return ltype;
	}
	
	@Override
	public PType caseASetUnionBinaryExp(ASetUnionBinaryExp node,
			TypeCheckInfo question) {

		node.getLeft().apply(rootVisitor,question);
		node.getRight().apply(rootVisitor, question);

		PType ltype = node.getLeft().getType();
		PType rtype = node.getRight().getType();

		if (!PTypeAssistant.isSet(ltype))
		{
			PExpAssistant.report(3168, "Left hand of " + node.getOp() + " is not a set",node);
		}

		if (!PTypeAssistant.isSet(rtype))
		{
			PExpAssistant.report(3169, "Right hand of " + node.getOp() + " is not a set",node);
		}

		Set<PType> result = new HashSet<PType>();
		result.add(ltype); result.add(rtype);
		node.setType(PTypeAssistant.getType(result, node.getLocation()));
		return node.getType();
	}
	
	@Override
	public PType caseAStarStarBinaryExp(AStarStarBinaryExp node,
			TypeCheckInfo question) {

		node.getLeft().apply(rootVisitor,question);
		node.getRight().apply(rootVisitor, question);

		PType ltype = node.getLeft().getType();
		PType rtype = node.getRight().getType();


		if (PTypeAssistant.isMap(ltype))
		{
			if (!PTypeAssistant.isNumeric(rtype))
			{
				//rtype.report(3170, "Map iterator expects nat as right hand arg");
				PExpAssistant.report(3170, "Map iterator expects nat as right hand arg", node);
			}
		}
		else if (PTypeAssistant.isFunction(ltype))
		{
			if (!PTypeAssistant.isNumeric(rtype))
			{
				PExpAssistant.report(3171, "Function iterator expects nat as right hand arg",node);
			}
		}
		else if (PTypeAssistant.isNumeric(ltype))
		{
			if (!PTypeAssistant.isNumeric(rtype))
			{
				PExpAssistant.report(3172, "'**' expects number as right hand arg",node);
			}
		}
		else
		{
			PExpAssistant.report(3173, "First arg of '**' must be a map, function or number",node);
			node.setType(new AUnknownType(node.getLocation(), false));
			return node.getType();
		}
		
		node.setType(ltype);
		return ltype;
	}
	
	
	@Override
	public PType caseASubsetBinaryExp(ASubsetBinaryExp node,
			TypeCheckInfo question) {

		node.getLeft().apply(rootVisitor,question);
		node.getRight().apply(rootVisitor, question);

		PType ltype = node.getLeft().getType();
		PType rtype = node.getRight().getType();

		if (!PTypeAssistant.isSet(ltype))
		{
			PExpAssistant.report(3177, "Left hand of " + node.getOp() + " is not a set",node);
			PExpAssistant.detail("Type", ltype);
		}

		if (!PTypeAssistant.isSet(rtype))
		{
			PExpAssistant.report(3178, "Right hand of " + node.getOp() + " is not a set",node);
			PExpAssistant.detail("Type", rtype);
		}

		node.setType(new ABooleanBasicType(node.getLocation(),false));
		return node.getType();
	}
	
	
	@Override
	public PType caseABooleanConstExp(ABooleanConstExp node,
			TypeCheckInfo question) {
		node.setType(new ABooleanBasicType(node.getLocation(), false)); 
		return node.getType();
	}
	
	@Override
	public PType caseACasesExp(ACasesExp node, TypeCheckInfo question) {
		
		question.qualifiers = null;
		
		PType expType = node.getExpression().apply(rootVisitor, question);
		
		Set<PType> rtypes = new HashSet<PType>();

		for (ACaseAlternative c: node.getCases())
		{
			rtypes.add(ACaseAlternativeAssistant.typeCheck(c,rootVisitor,question, expType));
		}

		if (node.getOthers() != null)
		{
			rtypes.add(node.getOthers().apply(rootVisitor, question));
		}

		node.setType(PTypeAssistant.getType(rtypes, node.getLocation()));
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
	
	
	
	
}
