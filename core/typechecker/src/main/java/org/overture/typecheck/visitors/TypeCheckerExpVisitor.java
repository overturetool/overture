package org.overture.typecheck.visitors;

import java.lang.instrument.ClassDefinition;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.AMultiBindListDefinition;
import org.overture.ast.definitions.APerSyncDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.definitions.assistants.PDefinitionAssistant;
import org.overture.ast.definitions.assistants.SClassDefinitionAssistant;
import org.overture.ast.expressions.AApplyExp;
import org.overture.ast.expressions.ABooleanConstExp;
import org.overture.ast.expressions.ACaseAlternative;
import org.overture.ast.expressions.ACasesExp;
import org.overture.ast.expressions.ACharConstExp;
import org.overture.ast.expressions.ACompBinaryExp;
import org.overture.ast.expressions.ADivNumericBinaryExp;
import org.overture.ast.expressions.ADivideNumericBinaryExp;
import org.overture.ast.expressions.ADomainResByBinaryExp;
import org.overture.ast.expressions.ADomainResToBinaryExp;
import org.overture.ast.expressions.AElseIfExp;
import org.overture.ast.expressions.AEqualsBinaryExp;
import org.overture.ast.expressions.AExists1Exp;
import org.overture.ast.expressions.AExistsExp;
import org.overture.ast.expressions.AFieldExp;
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
import org.overture.ast.expressions.assistants.SBinaryExpAssistant;
import org.overture.ast.patterns.assistants.PBindAssistant;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.ACharBasicType;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AIntNumericBasicType;
import org.overture.ast.types.AMapType;
import org.overture.ast.types.ANatNumericBasicType;
import org.overture.ast.types.ANatOneNumericBasicType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.ARealNumericBasicType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.ASeq1Type;
import org.overture.ast.types.ASeqType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SNumericBasicType;
import org.overture.ast.types.assistants.AClassTypeAssistant;
import org.overture.ast.types.assistants.AFunctionTypeAssistent;
import org.overture.ast.types.assistants.AOperationTypeAssistant;
import org.overture.ast.types.assistants.ARecordInvariantTypeAssistant;
import org.overture.ast.types.assistants.PTypeAssistant;
import org.overture.ast.types.assistants.SNumericBasicTypeAssistant;
import org.overture.runtime.Environment;
import org.overture.runtime.FlatCheckedEnvironment;
import org.overture.runtime.TypeChecker;
import org.overture.runtime.TypeComparator;
import org.overture.runtime.TypeList;
import org.overture.typecheck.LexNameTokenAssistent;
import org.overture.typecheck.TypeCheckInfo;
import org.overture.typecheck.TypeCheckerErrors;
import org.overturetool.vdmj.Release;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.lex.LexNameToken;









public class TypeCheckerExpVisitor extends
		QuestionAnswerAdaptor<TypeCheckInfo, PType> {
	
	final private QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor;
	
	
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
				TypeCheckerErrors.report(3300, "Operation '" + node.getRoot() + "' cannot be called from a function",node.getLocation(),node);
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
			TypeCheckerErrors.report(3054, "Type " + node.getType() + " cannot be applied",node.getLocation(),node);
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
    			TypeCheckerErrors.report(3068, "Right hand of map 'comp' is not a map",node.getLocation(),node);
    			TypeCheckerErrors.detail("Type", node.getRight().getType());
    			return new AMapType(node.getLocation(),false, null, null, null);	// Unknown types
    		}

    		AMapType lm = PTypeAssistant.getMap(node.getLeft().getType());
    		AMapType rm = PTypeAssistant.getMap(node.getRight().getType());

    		if (!TypeComparator.compatible(lm.getFrom(), rm.getTo()))
    		{
    			TypeCheckerErrors.report(3069, "Domain of left should equal range of right in map 'comp'",node.getLocation(),node);
    			TypeCheckerErrors.detail2("Dom", lm.getFrom(), "Rng", rm.getTo());
    		}

    		results.add(new AMapType(node.getLocation(), false, rm.getFrom(), lm.getTo(), null));
		}

		if (PTypeAssistant.isFunction(node.getLeft().getType()))
		{
    		if (!PTypeAssistant.isFunction(node.getRight().getType()))
    		{
    			TypeCheckerErrors.report(3070, "Right hand of function 'comp' is not a function",node.getLocation(),node);
    			TypeCheckerErrors.detail("Type", node.getRight().getType());
    			return new AUnknownType(node.getLocation(),false);
    		}
    		else
    		{
        		AFunctionType lf = PTypeAssistant.getFunction(node.getLeft().getType());
        		AFunctionType rf = PTypeAssistant.getFunction(node.getRight().getType());

        		if (lf.getParameters().size() != 1)
        		{
        			TypeCheckerErrors.report(3071, "Left hand function must have a single parameter",node.getLocation(),node);
        			TypeCheckerErrors.detail("Type", lf);
        		}
        		else if (rf.getParameters().size() != 1)
        		{
        			TypeCheckerErrors.report(3072, "Right hand function must have a single parameter",node.getLocation(),node);
        			TypeCheckerErrors.detail("Type", rf);
        		}
        		else if (!TypeComparator.compatible(lf.getParameters().get(0), rf.getResult()))
        		{
        			TypeCheckerErrors.report(3073, "Parameter of left should equal result of right in function 'comp'",node.getLocation(),node);
        			TypeCheckerErrors.detail2("Parameter", lf.getParameters().get(0), "Result", rf.getResult());
        		}

        		results.add(
        			new AFunctionType(node.getLocation(), false, true, rf.getParameters(), lf.getResult()));
    		}
		}

		if (results.isEmpty())
		{
			TypeCheckerErrors.report(3074, "Left hand of 'comp' is neither a map nor a function",node.getLocation(),node);
			TypeCheckerErrors.detail("Type", node.getLeft().getType());
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
			TypeCheckerErrors.report(3079, "Left of '<-:' is not a set",node.getLocation(),node);
		}
		else if (!PTypeAssistant.isMap(node.getRight().getType()))
		{
			TypeCheckerErrors.report(3080, "Right of '<-:' is not a map",node.getLocation(),node);
		}
		else
		{
			ASetType set = PTypeAssistant.getSet(node.getLeft().getType());
			AMapType map = PTypeAssistant.getMap(node.getRight().getType());

			if (!TypeComparator.compatible(set.getSetof(), map.getFrom()))
			{
				TypeCheckerErrors.report(3081, "Restriction of map should be set of " + map.getFrom(),node.getLocation(),node);
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
			TypeCheckerErrors.report(3082, "Left of '<:' is not a set",node.getLocation(),node);
			TypeCheckerErrors.detail("Actual", node.getLeft().getType());
		}
		else if (!PTypeAssistant.isMap(node.getRight().getType()))
		{
			TypeCheckerErrors.report(3083, "Right of '<:' is not a map",node.getLocation(),node);
			TypeCheckerErrors.detail("Actual", node.getRight().getType());
		}
		else
		{
			ASetType set = PTypeAssistant.getSet(node.getLeft().getType());
			AMapType map = PTypeAssistant.getMap(node.getRight().getType());

			if (!TypeComparator.compatible(set.getSetof(), map.getFrom()))
			{
				TypeCheckerErrors.report(3084, "Restriction of map should be set of " + map.getFrom(),node.getLocation(),node);
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
			TypeCheckerErrors.report(3087, "Left and right of '=' are incompatible types",node.getLocation(),node);
			TypeCheckerErrors.detail2("Left", node.getLeft().getType(), "Right", node.getRight().getType());
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
			TypeCheckerErrors.report(3110, "Argument of 'in set' is not a set",node.getLocation(),node);
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
			TypeCheckerErrors.report(3123, "Left hand of 'munion' is not a map",node.getLocation(),node);
			TypeCheckerErrors.detail("Type", node.getLeft().getType());
			node.setType(new AMapType(node.getLocation(),false,null, null, null ));	// Unknown types
			return node.getType();
		}
		else if (!PTypeAssistant.isMap(node.getRight().getType()))
		{
			TypeCheckerErrors.report(3124, "Right hand of 'munion' is not a map",node.getLocation(),node);
			TypeCheckerErrors.detail("Type", node.getRight().getType());
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
			TypeCheckerErrors.report(3136, "Left and right of '<>' different types",node.getLocation(),node);
			TypeCheckerErrors.detail2("Left", node.getLeft().getType(), "Right", node.getRight().getType());
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
			TypeCheckerErrors.report(3138, "Argument of 'not in set' is not a set",node.getLocation(),node);
			TypeCheckerErrors.detail("Actual", node.getRight().getType());
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
    			TypeCheckerErrors.concern(unique, 3141, "Right hand of '++' is not a map",node.getLocation(),node);
    			TypeCheckerErrors.detail(unique, "Type", node.getRight().getType());
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
    			TypeCheckerErrors.concern(unique, 3142, "Right hand of '++' is not a map",node.getLocation(),node);
    			TypeCheckerErrors.detail(unique, "Type", node.getRight().getType());
    		}
    		else
    		{
        		AMapType mr = PTypeAssistant.getMap(node.getRight().getType());

        		if (!PTypeAssistant.isType(mr.getFrom(),SNumericBasicType.class))
        		{
        			TypeCheckerErrors.concern(unique, 3143, "Domain of right hand of '++' must be nat1",node.getLocation(),node);
        			TypeCheckerErrors.detail(unique, "Type", mr.getFrom());
        		}
    		}

    		result.add(st);
		}

		if (result.isEmpty())
		{
			TypeCheckerErrors.report(3144, "Left of '++' is neither a map nor a sequence",node.getLocation(),node);
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
			TypeCheckerErrors.report(3146, "Left hand of " + node.getOp() + " is not a set",node.getLocation(),node);
		}

		if (!PTypeAssistant.isSet(rtype))
		{
			TypeCheckerErrors.report(3147, "Right hand of " + node.getOp() + " is not a set", node.getLocation(),node);
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
			TypeCheckerErrors.report(3148, "Left of ':->' is not a map",node.getLocation(),node);
		}
		else if (!PTypeAssistant.isSet(rtype))
		{
			TypeCheckerErrors.report(3149, "Right of ':->' is not a set",node.getLocation(),node);
		}
		else
		{
			AMapType map = PTypeAssistant.getMap(ltype);
			ASetType set = PTypeAssistant.getSet(rtype);

			if (!TypeComparator.compatible(set.getSetof(), map.getTo()))
			{
				TypeCheckerErrors.report(3150, "Restriction of map should be set of " + map.getTo(),node.getLocation(),node);
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
			TypeCheckerErrors.report(3151, "Left of ':>' is not a map",node.getLocation(),node);
		}
		else if (!PTypeAssistant.isSet(rtype))
		{
			TypeCheckerErrors.report(3152, "Right of ':>' is not a set",node.getLocation(),node);
		}
		else
		{
			AMapType map = PTypeAssistant.getMap(ltype);
			ASetType set = PTypeAssistant.getSet(rtype);

			if (!TypeComparator.compatible(set.getSetof(), map.getTo()))
			{
				TypeCheckerErrors.report(3153, "Restriction of map should be set of " + map.getTo(),node.getLocation(),node);
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
			TypeCheckerErrors.report(3157, "Left hand of '^' is not a sequence",node.getLocation(),node);
			ltype = new ASeqType(node.getLocation(), false, new AUnknownType(node.getLocation(),false), null);
		}

		if (!PTypeAssistant.isSeq(rtype))
		{
			TypeCheckerErrors.report(3158, "Right hand of '^' is not a sequence",node.getLocation(),node);
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
			TypeCheckerErrors.report(3160, "Left hand of '\\' is not a set",node.getLocation(),node);
		}

		if (!PTypeAssistant.isSet(rtype))
		{
			TypeCheckerErrors.report(3161, "Right hand of '\\' is not a set",node.getLocation(),node);
		}

		if (!TypeComparator.compatible(ltype, rtype))
		{
			TypeCheckerErrors.report(3162, "Left and right of '\\' are different types",node.getLocation(),node);
			TypeCheckerErrors.detail2("Left", ltype, "Right", rtype);
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
			TypeCheckerErrors.report(3163, "Left hand of " + node.getLocation() + " is not a set",node.getLocation(),node);
		}

		if (!PTypeAssistant.isSet(rtype))
		{
			TypeCheckerErrors.report(3164, "Right hand of " + node.getLocation() + " is not a set",node.getLocation(),node);
		}


		if (!TypeComparator.compatible(ltype, rtype))
		{
			TypeCheckerErrors.report(3165, "Left and right of intersect are different types",node.getLocation(),node);
			TypeCheckerErrors.detail2("Left", ltype, "Right", rtype);
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
			TypeCheckerErrors.report(3168, "Left hand of " + node.getOp() + " is not a set",node.getLocation(),node);
		}

		if (!PTypeAssistant.isSet(rtype))
		{
			TypeCheckerErrors.report(3169, "Right hand of " + node.getOp() + " is not a set",node.getLocation(),node);
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
				TypeCheckerErrors.report(3170, "Map iterator expects nat as right hand arg", node.getLocation(),node);
			}
		}
		else if (PTypeAssistant.isFunction(ltype))
		{
			if (!PTypeAssistant.isNumeric(rtype))
			{
				TypeCheckerErrors.report(3171, "Function iterator expects nat as right hand arg",node.getLocation(),node);
			}
		}
		else if (PTypeAssistant.isNumeric(ltype))
		{
			if (!PTypeAssistant.isNumeric(rtype))
			{
				TypeCheckerErrors.report(3172, "'**' expects number as right hand arg",node.getLocation(),node);
			}
		}
		else
		{
			TypeCheckerErrors.report(3173, "First arg of '**' must be a map, function or number",node.getLocation(),node);
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
			TypeCheckerErrors.report(3177, "Left hand of " + node.getOp() + " is not a set",node.getLocation(),node);
			TypeCheckerErrors.detail("Type", ltype);
		}

		if (!PTypeAssistant.isSet(rtype))
		{
			TypeCheckerErrors.report(3178, "Right hand of " + node.getOp() + " is not a set",node.getLocation(),node);
			TypeCheckerErrors.detail("Type", rtype);
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
	public PType caseACharConstExp(ACharConstExp node, TypeCheckInfo question) {
		
		node.setType(new ACharBasicType(node.getLocation(), false));
		return node.getType();
	}
	
	@Override
	public PType caseAElseIfExp(AElseIfExp node, TypeCheckInfo question) {
		
		if (!PTypeAssistant.isType(node.getElseIf().apply(this, question),ABooleanBasicType.class))
		{
			TypeCheckerErrors.report(3086, "Else clause is not a boolean",node.getLocation(),node);
		}

		node.setType(node.getThen().apply(this, question));
		return node.getType();
	}
	
	
	@Override
	public PType caseAExists1Exp(AExists1Exp node, TypeCheckInfo question) {
		node.setDef(new AMultiBindListDefinition(node.getBind().getLocation(), null, null, null, null, null, null, PBindAssistant.getMultipleBind(node.getBind()), null));
		node.getDef().apply(rootVisitor, question);
		Environment local = new FlatCheckedEnvironment(node.getDef(), question.env, question.scope);

		question.qualifiers = null;
		if (!PTypeAssistant.isType(node.getPredicate().apply(rootVisitor, question), ABooleanBasicType.class))
		{
			TypeCheckerErrors.report(3088, "Predicate is not boolean",node.getPredicate().getLocation(),node.getPredicate());
		}

		local.unusedCheck();
		return new ABooleanBasicType(node.getLocation(),false);
	}
	
	@Override
	public PType caseAExistsExp(AExistsExp node, TypeCheckInfo question) {
		
		PDefinition def = new AMultiBindListDefinition(node.getLocation(), null, null, null, null, null, null, node.getBindList(), null);
		def.apply(rootVisitor, question);

		Environment local = new FlatCheckedEnvironment(def, question.env, question.scope);

		if (!PTypeAssistant.isType(node.getPredicate().apply(rootVisitor, question),ABooleanBasicType.class))
		{
			TypeCheckerErrors.report(3089, "Predicate is not boolean",node.getPredicate().getLocation(),node.getPredicate());
		}

		local.unusedCheck();
		return new ABooleanBasicType(node.getLocation(),false);
	}
	
	@Override
	public PType caseAFieldExp(AFieldExp node, TypeCheckInfo question) {

		PType root = node.getObject().apply(rootVisitor,question);

		if (PTypeAssistant.isUnknown(root))
		{
			node.setMemberName(new LexNameToken("?", node.getField()));
			node.setType(root);
			return root;
		}

		Set<PType> results = new HashSet<PType>();
		boolean recOrClass = false;
		boolean unique = !PTypeAssistant.isUnion(root);

		if (PTypeAssistant.isRecord(root))
		{
    		ARecordInvariantType rec = PTypeAssistant.getRecord(root);
    		AFieldField cf = ARecordInvariantTypeAssistant.findField(rec,node.getField().name);
    		

   			if (cf != null)
   			{
   				results.add(cf.getType());
    		}
   			else
   			{
   				TypeCheckerErrors.concern(unique,
   					3090, "Unknown field " + node.getField().name + " in record " + rec.getName(),node.getLocation(),node);
   			}

   			recOrClass = true;
		}

		if (question.env.isVDMPP() && PTypeAssistant.isClass(root))
		{
    		AClassType cls = PTypeAssistant.getClassType(root);
    		LexNameToken memberName =node.getMemberName();
    		
    		if (memberName == null)
    		{
    			memberName = AClassTypeAssistant.getMemberName(cls,node.getField());
    		}

    		memberName.setTypeQualifier(question.qualifiers);
    		PDefinition fdef = AClassTypeAssistant.findName(cls,memberName);

   			if (fdef == null)
   			{
    			// The field may be a map or sequence, which would not
    			// have the type qualifier of its arguments in the name...

    			List<PType> oldq = memberName.getTypeQualifier();
    			memberName.setTypeQualifier(null);
    			fdef = AClassTypeAssistant.findName(cls,memberName);
    			memberName.setTypeQualifier(oldq);	// Just for error text!
    		}

			if (fdef == null && memberName.typeQualifier == null)
			{
				// We might be selecting a bare function or operation, without
				// applying it (ie. no qualifiers). In this case, if there is
				// precisely one possibility, we choose it.

				for (PDefinition possible: question.env.findMatches(memberName))
				{
					if (PDefinitionAssistant.isFunctionOrOperation(possible))
					{
						if (fdef != null)
						{
							fdef = null;	// Alas, more than one
							break;
						}
						else
						{
							fdef = possible;
						}
					}
				}
			}

			if (fdef == null)
			{
				TypeCheckerErrors.concern(unique,
					3091, "Unknown member " + memberName + " of class " + cls.getName().name,node.getField().getLocation(),node.getField());

				if (unique)
				{
					question.env.listAlternatives(memberName);
				}
			}
			else if ( SClassDefinitionAssistant.isAccessible(question.env, fdef, false))
   			{
				// The following gives lots of warnings for self.value access
				// to values as though they are fields of self in the CSK test
				// suite, so commented out for now.

				if (PDefinitionAssistant.isStatic(fdef))// && !env.isStatic())
				{
					// warning(5005, "Should access member " + field + " from a static context");
				}

   				results.add(fdef.getType());
   				// At runtime, type qualifiers must match exactly
   				memberName.setTypeQualifier(fdef.getName().typeQualifier);
    		}
   			else
   			{
   				TypeCheckerErrors.concern(unique,
   					3092, "Inaccessible member " + memberName + " of class " + cls.getName().name,node.getField().getLocation(),node.getField());
   			}

   			recOrClass = true;
		}

		if (results.isEmpty())
		{
    		if (!recOrClass)
    		{
    			TypeCheckerErrors.report(3093, "Field '" + node.getField().name + "' applied to non-aggregate type",node.getObject().getLocation(),node.getObject());
    		}

    		node.setType(new AUnknownType(node.getLocation(),false));
    		return node.getType();
		}

		node.setType(PTypeAssistant.getType(results, node.getLocation()));
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
