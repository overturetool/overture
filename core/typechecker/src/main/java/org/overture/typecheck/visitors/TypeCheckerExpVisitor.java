package org.overture.typecheck.visitors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.ABusClassDefinition;
import org.overture.ast.definitions.ACpuClassDefinition;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.AMultiBindListDefinition;
import org.overture.ast.definitions.APerSyncDefinition;
import org.overture.ast.definitions.ASystemClassDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.definitions.assistants.AExplicitFunctionDefinitionAssistant;
import org.overture.ast.definitions.assistants.AImplicitFunctionDefinitionAssistant;
import org.overture.ast.definitions.assistants.PAccessSpecifierAssistant;
import org.overture.ast.definitions.assistants.PDefinitionAssistant;
import org.overture.ast.definitions.assistants.PDefinitionListAssistant;
import org.overture.ast.definitions.assistants.PMultipleBindAssistant;
import org.overture.ast.definitions.assistants.SClassDefinitionAssistant;
import org.overture.ast.expressions.AApplyExp;
import org.overture.ast.expressions.ABooleanConstExp;
import org.overture.ast.expressions.ACaseAlternative;
import org.overture.ast.expressions.ACasesExp;
import org.overture.ast.expressions.ACharLiteralExp;
import org.overture.ast.expressions.ACompBinaryExp;
import org.overture.ast.expressions.ADefExp;
import org.overture.ast.expressions.ADivNumericBinaryExp;
import org.overture.ast.expressions.ADivideNumericBinaryExp;
import org.overture.ast.expressions.ADomainResByBinaryExp;
import org.overture.ast.expressions.ADomainResToBinaryExp;
import org.overture.ast.expressions.AElseIfExp;
import org.overture.ast.expressions.AEqualsBinaryExp;
import org.overture.ast.expressions.AExists1Exp;
import org.overture.ast.expressions.AExistsExp;
import org.overture.ast.expressions.AFieldExp;
import org.overture.ast.expressions.AFieldNumberExp;
import org.overture.ast.expressions.AForAllExp;
import org.overture.ast.expressions.AFuncInstatiationExp;
import org.overture.ast.expressions.AGreaterEqualNumericBinaryExp;
import org.overture.ast.expressions.AGreaterNumericBinaryExp;
import org.overture.ast.expressions.AHistoryExp;
import org.overture.ast.expressions.AIfExp;
import org.overture.ast.expressions.AInSetBinaryExp;
import org.overture.ast.expressions.AIntLiteralExp;
import org.overture.ast.expressions.AIotaExp;
import org.overture.ast.expressions.AIsExp;
import org.overture.ast.expressions.AIsOfBaseClassExp;
import org.overture.ast.expressions.AIsOfClassExp;
import org.overture.ast.expressions.ALambdaExp;
import org.overture.ast.expressions.ALetBeStExp;
import org.overture.ast.expressions.ALetDefExp;
import org.overture.ast.expressions.AMapCompMapExp;
import org.overture.ast.expressions.AMapEnumMapExp;
import org.overture.ast.expressions.AMapUnionBinaryExp;
import org.overture.ast.expressions.AMapletExp;
import org.overture.ast.expressions.AMkBasicExp;
import org.overture.ast.expressions.AMkTypeExp;
import org.overture.ast.expressions.AModNumericBinaryExp;
import org.overture.ast.expressions.AMuExp;
import org.overture.ast.expressions.ANewExp;
import org.overture.ast.expressions.ANilExp;
import org.overture.ast.expressions.ANotEqualBinaryExp;
import org.overture.ast.expressions.ANotInSetBinaryExp;
import org.overture.ast.expressions.ANotYetSpecifiedExp;
import org.overture.ast.expressions.APlusNumericBinaryExp;
import org.overture.ast.expressions.APlusPlusBinaryExp;
import org.overture.ast.expressions.APostOpExp;
import org.overture.ast.expressions.APreExp;
import org.overture.ast.expressions.APreOpExp;
import org.overture.ast.expressions.AProperSubsetBinaryExp;
import org.overture.ast.expressions.AQuoteLiteralExp;
import org.overture.ast.expressions.ARangeResByBinaryExp;
import org.overture.ast.expressions.ARangeResToBinaryExp;
import org.overture.ast.expressions.ARealLiteralExp;
import org.overture.ast.expressions.ARecordModifier;
import org.overture.ast.expressions.ARemNumericBinaryExp;
import org.overture.ast.expressions.ASameBaseClassExp;
import org.overture.ast.expressions.ASameClassExp;
import org.overture.ast.expressions.ASelfExp;
import org.overture.ast.expressions.ASeqCompSeqExp;
import org.overture.ast.expressions.ASeqConcatBinaryExp;
import org.overture.ast.expressions.ASeqEnumSeqExp;
import org.overture.ast.expressions.ASetCompSetExp;
import org.overture.ast.expressions.ASetDifferenceBinaryExp;
import org.overture.ast.expressions.ASetEnumSetExp;
import org.overture.ast.expressions.ASetIntersectBinaryExp;
import org.overture.ast.expressions.ASetRangeSetExp;
import org.overture.ast.expressions.ASetUnionBinaryExp;
import org.overture.ast.expressions.AStarStarBinaryExp;
import org.overture.ast.expressions.AStateInitExp;
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
import org.overture.ast.expressions.SBooleanBinaryExp;
import org.overture.ast.expressions.assistants.AApplyExpAssistant;
import org.overture.ast.expressions.assistants.ACaseAlternativeAssistant;
import org.overture.ast.expressions.assistants.SBinaryExpAssistant;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.ASetBind;
import org.overture.ast.patterns.ATypeBind;
import org.overture.ast.patterns.PBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.patterns.assistants.ASetBindAssistant;
import org.overture.ast.patterns.assistants.ATypeBindAssistant;
import org.overture.ast.patterns.assistants.PBindAssistant;
import org.overture.ast.patterns.assistants.PPatternAssistant;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.ACharBasicType;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AIntNumericBasicType;
import org.overture.ast.types.AMapMapType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.ANatNumericBasicType;
import org.overture.ast.types.ANatOneNumericBasicType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AParameterType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.AQuoteType;
import org.overture.ast.types.ARealNumericBasicType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.ASeq1SeqType;
import org.overture.ast.types.ASeqSeqType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.ATokenBasicType;
import org.overture.ast.types.AUndefinedType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SNumericBasicType;
import org.overture.ast.types.SSeqType;
import org.overture.ast.types.assistants.AClassTypeAssistant;
import org.overture.ast.types.assistants.AFunctionTypeAssistant;
import org.overture.ast.types.assistants.AOperationTypeAssistant;
import org.overture.ast.types.assistants.ARecordInvariantTypeAssistant;
import org.overture.ast.types.assistants.PTypeAssistant;
import org.overture.ast.types.assistants.PTypeSet;
import org.overture.ast.types.assistants.SNumericBasicTypeAssistant;
import org.overture.typecheck.Environment;
import org.overture.typecheck.FlatCheckedEnvironment;
import org.overture.typecheck.LexNameTokenAssistent;
import org.overture.typecheck.TypeCheckInfo;
import org.overture.typecheck.TypeChecker;
import org.overture.typecheck.TypeCheckerErrors;
import org.overture.typecheck.TypeComparator;
import org.overturetool.vdmj.Release;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.lex.LexRealToken;
import org.overturetool.vdmj.typechecker.NameScope;


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
		PTypeSet results = new PTypeSet();

		if (PTypeAssistant.isFunction(node.getType()))
		{
			AFunctionType ft = PTypeAssistant.getFunction(node.getType());
			AFunctionTypeAssistant.typeResolve(ft, null,rootVisitor,question);
			results.add(AApplyExpAssistant.functionApply(node,isSimple, ft));
		}

		if (PTypeAssistant.isOperation(node.getType()))
		{
			AOperationType ot = PTypeAssistant.getOperation(node.getType());
			AOperationTypeAssistant.typeResolve(ot, null,rootVisitor,question);

			if (inFunction && Settings.release == Release.VDM_10)
			{
				TypeCheckerErrors.report(3300, "Operation '" + node.getRoot() + "' cannot be called from a function",node.getLocation(),node);
				results.add(new AUnknownType(node.getLocation(),false,null));
			}
			else
			{
    			results.add(AApplyExpAssistant.operationApply(node,isSimple, ot));
			}
		}

		if (PTypeAssistant.isSeq(node.getType()))
		{
			SSeqType seq = PTypeAssistant.getSeq(node.getType());
			results.add(AApplyExpAssistant.sequenceApply(node,isSimple, seq));
		}

		if (PTypeAssistant.isMap(node.getType()))
		{
			SMapType map = PTypeAssistant.getMap(node.getType());
			results.add(AApplyExpAssistant.mapApply(node,isSimple, map));
		}

		if (results.isEmpty())
		{
			TypeCheckerErrors.report(3054, "Type " + node.getType() + " cannot be applied",node.getLocation(),node);
			return new AUnknownType(node.getLocation(),false,null);
		}

		node.setType(results.getType(node.getLocation()));
		return node.getType();	// Union of possible applications
	}
	
	
	@Override
	public PType caseSBooleanBinaryExp(SBooleanBinaryExp node,
			TypeCheckInfo question) {
		
		SBinaryExpAssistant.binaryCheck(node, new ABooleanBasicType(node.getLocation(), true,null),rootVisitor,question);
		return node.getType();
		
	}
	
	@Override
	public PType caseACompBinaryExp(ACompBinaryExp node, TypeCheckInfo question) {
		node.getLeft().apply(rootVisitor, question);
		node.getRight().apply(rootVisitor, question);
		
		PTypeSet results = new PTypeSet();

		if (PTypeAssistant.isMap(node.getLeft().getType()))
		{
    		if (!PTypeAssistant.isMap(node.getRight().getType()))
    		{
    			TypeCheckerErrors.report(3068, "Right hand of map 'comp' is not a map",node.getLocation(),node);
    			TypeCheckerErrors.detail("Type", node.getRight().getType());
    			node.setType(new AMapMapType(node.getLocation(),false, null, null, null,null));	// Unknown types
    			return node.getType(); 
    		}

    		SMapType lm = PTypeAssistant.getMap(node.getLeft().getType());
    		SMapType rm = PTypeAssistant.getMap(node.getRight().getType());

    		if (!TypeComparator.compatible(lm.getFrom(), rm.getTo()))
    		{
    			TypeCheckerErrors.report(3069, "Domain of left should equal range of right in map 'comp'",node.getLocation(),node);
    			TypeCheckerErrors.detail2("Dom", lm.getFrom(), "Rng", rm.getTo());
    		}

    		results.add(new AMapMapType(node.getLocation(), false, null, rm.getFrom(), lm.getTo(), null));
		}

		if (PTypeAssistant.isFunction(node.getLeft().getType()))
		{
    		if (!PTypeAssistant.isFunction(node.getRight().getType()))
    		{
    			TypeCheckerErrors.report(3070, "Right hand of function 'comp' is not a function",node.getLocation(),node);
    			TypeCheckerErrors.detail("Type", node.getRight().getType());
    			node.setType(new AUnknownType(node.getLocation(),false,null));
    			return node.getType();
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
        			new AFunctionType(node.getLocation(), false,null, true, rf.getParameters(), lf.getResult()));
    		}
		}

		if (results.isEmpty())
		{
			TypeCheckerErrors.report(3074, "Left hand of 'comp' is neither a map nor a function",node.getLocation(),node);
			TypeCheckerErrors.detail("Type", node.getLeft().getType());
			node.setType(new AUnknownType(node.getLocation(),false,null));
			return node.getType();
		}

		node.setType( results.getType(node.getLocation()));		
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
			SMapType map = PTypeAssistant.getMap(node.getRight().getType());

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
			SMapType map = PTypeAssistant.getMap(node.getRight().getType());

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

		node.setType(new ABooleanBasicType(node.getLocation(),false,null));
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

		node.setType(new ABooleanBasicType(node.getLocation(),false,null));
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
			node.setType(new AMapMapType(node.getLocation(),false,null,null, null, null ));	// Unknown types
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
			SMapType ml = PTypeAssistant.getMap(node.getLeft().getType());
			SMapType mr = PTypeAssistant.getMap(node.getRight().getType());

			PTypeSet from = new PTypeSet();
			from.add(ml.getFrom()); from.add(mr.getFrom());
			PTypeSet to =  new PTypeSet();
			to.add(ml.getTo()); to.add(mr.getTo());

			node.setType(new AMapMapType(node.getLocation(),false,null,
					from.getType(node.getLocation()), to.getType(node.getLocation()), null));
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

		node.setType(new ABooleanBasicType(node.getLocation(),false,null));
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

		node.setType(new ABooleanBasicType(node.getLocation(),false,null));
		return node.getType();
	}
	
	
	@Override
	public PType caseADivNumericBinaryExp(ADivNumericBinaryExp node,
			TypeCheckInfo question) {
		
		SNumericBasicTypeAssistant.checkNumeric(node,rootVisitor,question);
		node.setType(new AIntNumericBasicType(node.getLocation(),false,null));
		return node.getType() ;
	}
	
	@Override
	public PType caseADivideNumericBinaryExp(ADivideNumericBinaryExp node,
			TypeCheckInfo question) {
		
		SNumericBasicTypeAssistant.checkNumeric(node,rootVisitor,question);
		node.setType(new ARealNumericBasicType(node.getLocation(),false,null));
		return node.getType() ;
	}
	
	@Override
	public PType caseAGreaterEqualNumericBinaryExp(
			AGreaterEqualNumericBinaryExp node, TypeCheckInfo question) {
		
		SNumericBasicTypeAssistant.checkNumeric(node,rootVisitor,question);
		node.setType(new ABooleanBasicType(node.getLocation(),false,null));
		return node.getType() ;
	}
	
	@Override
	public PType caseAGreaterNumericBinaryExp(AGreaterNumericBinaryExp node,
			TypeCheckInfo question) {
		
		SNumericBasicTypeAssistant.checkNumeric(node,rootVisitor,question);
		node.setType(new ABooleanBasicType(node.getLocation(),false,null));
		return node.getType() ;
	}
	
	@Override
	public PType caseAModNumericBinaryExp(AModNumericBinaryExp node,
			TypeCheckInfo question) {
	
		SNumericBasicTypeAssistant.checkNumeric(node,rootVisitor,question);
		node.setType(new ANatNumericBasicType(node.getLocation(),false,null));
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
			node.setType(ln);
			return ln;
		}
		else if (rn instanceof ARealNumericBasicType)
		{
			node.setType(rn);
			return rn;
		}
		else if (ln instanceof AIntNumericBasicType)
		{
			node.setType(ln);
			return ln;
		}
		else if (rn instanceof AIntNumericBasicType)
		{
			node.setType(rn);
			return rn;
		}
		else if (ln instanceof ANatNumericBasicType && rn instanceof ANatNumericBasicType)
		{
			node.setType(ln);
			return ln;
		}
		else
		{
			node.setType(new ANatOneNumericBasicType(ln.getLocation(),false,null));
			return node.getType();
		}
	}
	
	
	@Override
	public PType caseARemNumericBinaryExp(ARemNumericBinaryExp node,
			TypeCheckInfo question) {
		
		SNumericBasicTypeAssistant.checkNumeric(node,rootVisitor,question);
		node.setType(new AIntNumericBasicType(node.getLocation(),false,null));
		return node.getType() ;
		
	}
	
	
	@Override
	public PType caseASubstractNumericBinaryExp(
			ASubstractNumericBinaryExp node, TypeCheckInfo question) {
		
		SNumericBasicTypeAssistant.checkNumeric(node,rootVisitor,question);

		if (node.getLeft().getType() instanceof ARealNumericBasicType || node.getRight().getType() instanceof ARealNumericBasicType)
		{
			node.setType(new ARealNumericBasicType(node.getLocation(),false,null));
			return node.getType();
		}
		else
		{
			node.setType(new AIntNumericBasicType(node.getLocation(), false,null));
			return node.getType();
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
			node.setType(ln);
			return ln;
		}
		else if (rn instanceof ARealNumericBasicType)
		{
			node.setType(rn);
			return rn;
		}
		else if (ln instanceof AIntNumericBasicType)
		{
			node.setType(ln);
			return ln;
		}
		else if (rn instanceof AIntNumericBasicType)
		{
			node.setType(rn);
			return rn;
		}
		else if (ln instanceof ANatNumericBasicType && rn instanceof ANatNumericBasicType)
		{
			node.setType(ln);
			return ln;
		}
		else
		{
			node.setType(new ANatOneNumericBasicType(ln.getLocation(),false,null));
			return node.getType();
		}
	}
	
	
	@Override
	public PType caseAPlusPlusBinaryExp(APlusPlusBinaryExp node,
			TypeCheckInfo question) {
		
		node.getLeft().apply(rootVisitor,question);
		node.getRight().apply(rootVisitor, question);

		PTypeSet result = new PTypeSet();
		
		boolean unique = (!PTypeAssistant.isUnion(node.getLeft().getType()) && !PTypeAssistant.isUnion(node.getRight().getType()));

		if (PTypeAssistant.isMap(node.getLeft().getType()))
		{
    		if (!PTypeAssistant.isMap(node.getRight().getType()))
    		{
    			TypeCheckerErrors.concern(unique, 3141, "Right hand of '++' is not a map",node.getLocation(),node);
    			TypeCheckerErrors.detail(unique, "Type", node.getRight().getType());
    			node.setType(new AMapMapType(node.getLocation(),false,null, null, null, null));	// Unknown types
    			return node.getType();
    		}

    		SMapType lm = PTypeAssistant.getMap(node.getLeft().getType());
    		SMapType rm = PTypeAssistant.getMap(node.getRight().getType());

    		PTypeSet domain = new PTypeSet();
    		domain.add(lm.getFrom()); domain.add(rm.getFrom());
    		PTypeSet range = new PTypeSet(); 
    		range.add(lm.getTo()); range.add(rm.getTo());

    		result.add(new AMapMapType(node.getLocation(),false,null,
    			domain.getType(node.getLocation()), range.getType(node.getLocation()),false));
		}
			
		if (PTypeAssistant.isSeq(node.getLeft().getType()))
		{
    		SSeqType st = PTypeAssistant.getSeq(node.getLeft().getType());

    		if (!PTypeAssistant.isMap(node.getRight().getType()))
    		{
    			TypeCheckerErrors.concern(unique, 3142, "Right hand of '++' is not a map",node.getLocation(),node);
    			TypeCheckerErrors.detail(unique, "Type", node.getRight().getType());
    		}
    		else
    		{
        		SMapType mr = PTypeAssistant.getMap(node.getRight().getType());

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
			node.setType(new AUnknownType(node.getLocation(), false,null));
			return node.getType();
		}

		node.setType(result.getType(node.getLocation()));
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

		node.setType(new ABooleanBasicType(node.getLocation(),false,null));
		return node.getType();
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
			SMapType map = PTypeAssistant.getMap(ltype);
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
			SMapType map = PTypeAssistant.getMap(ltype);
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
			ltype = new ASeqSeqType(node.getLocation(), false,null, new AUnknownType(node.getLocation(),false,null), null);
		}

		if (!PTypeAssistant.isSeq(rtype))
		{
			TypeCheckerErrors.report(3158, "Right hand of '^' is not a sequence",node.getLocation(),node);
			rtype = new ASeqSeqType(node.getLocation(), false,null, new AUnknownType(node.getLocation(),false,null), null);
		}

		PType lof = PTypeAssistant.getSeq(ltype);
		PType rof = PTypeAssistant.getSeq(rtype);
		boolean seq1 = (lof instanceof ASeq1SeqType) || (rof instanceof ASeq1SeqType);
		
		lof = ((SSeqType)lof).getSeqof();
		rof = ((SSeqType)rof).getSeqof();
		PTypeSet ts = new PTypeSet();
		ts.add(lof); ts.add(rof);
		
		node.setType(seq1 ?
			new ASeq1SeqType(node.getLocation(), false,null, ts.getType(node.getLocation()),null) :
			new ASeqSeqType(node.getLocation(), false,null, ts.getType(node.getLocation()),null));
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

		node.setType(ltype);
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

		PTypeSet result = new PTypeSet();
		result.add(ltype); result.add(rtype);
		node.setType(result.getType(node.getLocation()));
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
			node.setType(new AUnknownType(node.getLocation(), false,null));
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

		node.setType(new ABooleanBasicType(node.getLocation(),false,null));
		return node.getType();
	}
	
	
	@Override
	public PType caseABooleanConstExp(ABooleanConstExp node,
			TypeCheckInfo question) {
		node.setType(new ABooleanBasicType(node.getLocation(), false,null)); 
		return node.getType();
	}
	
	@Override
	public PType caseACasesExp(ACasesExp node, TypeCheckInfo question) {
		
		question.qualifiers = null;
		
		PType expType = node.getExpression().apply(rootVisitor, question);
		
		PTypeSet rtypes = new PTypeSet();

		for (ACaseAlternative c: node.getCases())
		{
			rtypes.add(ACaseAlternativeAssistant.typeCheck(c,rootVisitor,question, expType));
		}

		if (node.getOthers() != null)
		{
			rtypes.add(node.getOthers().apply(rootVisitor, question));
		}

		node.setType(rtypes.getType(node.getLocation()));
		return node.getType();
	}
	
	@Override
	public PType caseACharLiteralExp(ACharLiteralExp node, TypeCheckInfo question) {
		
		node.setType(new ACharBasicType(node.getLocation(), false,null));
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
		node.setDef(new AMultiBindListDefinition(node.getBind().getLocation(), null, null, null, null, null, null, PBindAssistant.getMultipleBindList(node.getBind()), null));
		node.getDef().apply(rootVisitor, question);
		Environment local = new FlatCheckedEnvironment(node.getDef(), question.env, question.scope);

		question.qualifiers = null;
		if (!PTypeAssistant.isType(node.getPredicate().apply(rootVisitor, question), ABooleanBasicType.class))
		{
			TypeCheckerErrors.report(3088, "Predicate is not boolean",node.getPredicate().getLocation(),node.getPredicate());
		}

		local.unusedCheck();
		node.setType(new ABooleanBasicType(node.getLocation(),false,null));
		return node.getType();
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
		node.setType(new ABooleanBasicType(node.getLocation(),false,null));
		return node.getType();
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

		PTypeSet results = new PTypeSet();
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

    		node.setType(new AUnknownType(node.getLocation(),false,null));
    		return node.getType();
		}

		node.setType(results.getType(node.getLocation()));
		return node.getType();
	}
	
	
	@Override
	public PType caseAFieldNumberExp(AFieldNumberExp node,
			TypeCheckInfo question) {
		
		PExp tuple = node.getTuple();
		question.qualifiers = null;
		PType type = tuple.apply(rootVisitor, question);		
		node.setType(type);

		if (!PTypeAssistant.isProduct(type))
		{
			TypeCheckerErrors.report(3094, "Field '#" + node.getField() + "' applied to non-tuple type",tuple.getLocation(),tuple);
			node.setType(new AUnknownType(node.getLocation(),false,null));
			return node.getType();
		}

		AProductType product = PTypeAssistant.getProduct(type);
		long fn = node.getField().value;

		if (fn > product.getTypes().size() || fn < 1)
		{
			TypeCheckerErrors.report(3095, "Field number does not match tuple size",node.getField().location,node.getField());
			node.setType(new AUnknownType(node.getLocation(),false,null));
			return node.getType();
		}
		
		node.setType(product.getTypes().get((int)fn - 1));
		return node.getType();
	}
	
	
	@Override
	public PType caseAForAllExp(AForAllExp node, TypeCheckInfo question) {
		PDefinition def = new AMultiBindListDefinition(node.getLocation(), null, null, null, null, null, null, node.getBindList(), null);
		def.apply(rootVisitor, question);		
		Environment local = new FlatCheckedEnvironment(def, question.env, question.scope);

		question.qualifiers = null;
		if (!PTypeAssistant.isType(node.getPredicate().apply(rootVisitor,question),ABooleanBasicType.class))
		{
			TypeCheckerErrors.report(3097, "Predicate is not boolean",node.getPredicate().getLocation(),node.getPredicate());
		}

		local.unusedCheck();
		node.setType(new ABooleanBasicType(node.getLocation(),false,null));
		return node.getType();
	}
	
	
	@Override
	public PType caseAFuncInstatiationExp(AFuncInstatiationExp node,
			TypeCheckInfo question) {

		// If there are no type qualifiers passed because the poly function value
		// is being accessed alone (not applied). In this case, the null qualifier
		// will cause VariableExpression to search for anything that matches the
		// name alone. If there is precisely one, it is selected; if there are
		// several, this is an ambiguity error.
		//
		// Note that a poly function is hard to identify from the actual types
		// passed here because the number of parameters may not equal the number
		// of type parameters.

		PType ftype = node.getFunction().apply(rootVisitor, question);

		if (PTypeAssistant.isUnknown(ftype))
		{
			node.setType(ftype);
			return ftype;
		}

		if (PTypeAssistant.isFunction(ftype))
		{
			AFunctionType t = PTypeAssistant.getFunction(ftype);
			PTypeSet set = new PTypeSet();

			if (t.getDefinitions() == null)
			{
				TypeCheckerErrors.report(3098, "Function value is not polymorphic",node.getLocation(),node);
				set.add(new AUnknownType(node.getLocation(),false, null));
			}
			else
			{
    			boolean serious = (t.getDefinitions().size() == 1);

    			for (PDefinition def: t.getDefinitions())		// Possibly a union of several
    			{
    				List<LexNameToken> typeParams = null;
    				def = PDefinitionAssistant.deref(def);

    				if (def instanceof AExplicitFunctionDefinition)
    				{
    					node.setExpdef((AExplicitFunctionDefinition)def);
    					typeParams = node.getExpdef().getTypeParams();
    				}
    				else if (def instanceof AImplicitFunctionDefinition)
    				{
    					node.setImpdef((AImplicitFunctionDefinition)def);
    					typeParams = node.getImpdef().getTypeParams();
    				}
    				else
    				{
    					TypeCheckerErrors.report(3099, "Polymorphic function is not in scope", node.getLocation(), node);
    					continue;
    				}

    				if (typeParams == null)
    				{
    					TypeCheckerErrors.concern(serious, 3100, "Function has no type parameters", node.getLocation(), node);
    					continue;
    				}

    				if (node.getActualTypes().size() != typeParams.size())
    				{
    					TypeCheckerErrors.concern(serious, 3101, "Expecting " + typeParams.size() + " type parameters", node.getLocation(), node);
    					continue;
    				}

    				List<PType> fixed = new Vector<PType>();

    				for (PType ptype: node.getActualTypes())
    				{
    					if (ptype instanceof AParameterType)		// Recursive polymorphism
    					{
    						AParameterType pt = (AParameterType)ptype;
    						PDefinition d = question.env.findName( pt.getName(), question.scope);

    						if (d == null)
    						{
    							TypeCheckerErrors.report(3102, "Parameter name " + pt + " not defined", node.getLocation(), node);
    							ptype = new AUnknownType(node.getLocation(),false,null);
    						}
    						else
    						{
    							ptype = d.getType();
    						}
    					}
    					
    					fixed.add(PTypeAssistant.typeResolve(ptype, null, rootVisitor, question));
    				}
    					
    				node.setActualTypes(fixed);

    				node.setType( node.getExpdef() == null ?
    						AImplicitFunctionDefinitionAssistant.getType(node.getImpdef(), node.getActualTypes()) : 
    							AExplicitFunctionDefinitionAssistant.getType(node.getExpdef(), node.getActualTypes()));
    				
//    				type = expdef == null ?
//    					impdef.getType(actualTypes) : expdef.getType(actualTypes);

    				set.add(node.getType());
    			}
			}

			if (!set.isEmpty())
			{
				node.setType(set.getType(node.getLocation()));
				return node.getType();
			}
		}
		else
		{
			TypeCheckerErrors.report(3103, "Function instantiation does not yield a function", node.getLocation(), node);
		}

		node.setType(new AUnknownType(node.getLocation(),false,null));
		return node.getType();
	}
	
	@Override
	public PType caseAHistoryExp(AHistoryExp node,  TypeCheckInfo question)
	{
		SClassDefinition classdef = question.env.findClassDefinition();

		for (LexNameToken opname: node.getOpnames())
		{
    		int found = 0;

    		for (PDefinition def: classdef.getDefinitions())
    		{
    			if (def.getName() != null && def.getName().matches(opname))
    			{
    				found++;
    				
    				if (!PDefinitionAssistant.isCallableOperation(def))
    				{
    					TypeCheckerErrors.report(3105, opname + " is not an explicit operation",opname.location,opname);
    				}
    			}
    		}

    		if (found == 0)
    		{
    			TypeCheckerErrors.report(3106, opname + " is not in scope",opname.location,opname);
    		}
    		else if (found > 1)
    		{
    			TypeCheckerErrors.warning(5004, "History expression of overloaded operation",opname.location,opname);
    		}

    		if (opname.name.equals(classdef.getName().name))
    		{
    			TypeCheckerErrors.report(3107, "Cannot use history of a constructor",opname.location,opname);
    		}
		}

		node.setType(new ANatNumericBasicType(node.getLocation(), false, null));
		
		return node.getType();
	}
	
	
	@Override
	public PType caseAIfExp(AIfExp node, TypeCheckInfo question) {
		
		if (!PTypeAssistant.isType(node.getTest().apply(this, question),ABooleanBasicType.class))
		{
			TypeChecker.report(3108, "If expression is not a boolean",node.getLocation());
		}

		PTypeSet rtypes = new PTypeSet();
		rtypes.add(node.getThen().apply(rootVisitor, question));

		for (AElseIfExp eie: node.getElseList())
		{
			rtypes.add(eie.apply(rootVisitor, question));
		}

		rtypes.add(node.getElse().apply(rootVisitor, question));

		node.setType(rtypes.getType(node.getLocation()));
		return node.getType();
	}
	
	
	
	
	@Override
	public PType caseAIntLiteralExp(AIntLiteralExp node, TypeCheckInfo question) {
		if (node.getValue().value < 0)
		{
			node.setType(new AIntNumericBasicType(node.getLocation(),true,null));
		}
		else if (node.getValue().value == 0)
		{
			node.setType(new ANatNumericBasicType(node.getLocation(),true,null));
		}
		else
		{
			node.setType(new ANatOneNumericBasicType(node.getLocation(),true,null));
		}
		
		return node.getType();
	}
	
	
	@Override
	public PType caseAIotaExp(AIotaExp node, TypeCheckInfo question)
	{
		PDefinition def = new AMultiBindListDefinition(node.getLocation(),
				null, //name
				null, //namescope
				false,
				null, //classdef
				null, //access specifier
				null, //type
				PBindAssistant.getMultipleBindList(node.getBind()),
				null //defs
				);
				
		
		def.apply(rootVisitor, question);
		
		PType rt = null;
		PBind bind = node.getBind();
		
		if (bind instanceof ASetBind)
		{
			ASetBind sb = (ASetBind)bind;
			question.qualifiers = null;
			rt = sb.getSet().apply(rootVisitor, question);

			if (PTypeAssistant.isSet(rt))
			{
				rt = PTypeAssistant.getSet(rt).getSetof();
			}
			else
			{
				TypeCheckerErrors.report(3112, "Iota set bind is not a set",node.getLocation(),node);
			}
		}
		else
		{
			ATypeBind tb = (ATypeBind)bind;
			rt = tb.getType();
		}

		Environment local = new FlatCheckedEnvironment(def, question.env, question.scope);
		question.env = local;
		question.qualifiers = null;
		node.getPredicate().apply(rootVisitor, question);		
		local.unusedCheck();
		node.setType(rt);
		return rt;
	}
	
	@Override
	public PType caseAIsExp(AIsExp node, TypeCheckInfo question) {
		
		question.qualifiers = null;
		node.getTest().apply(rootVisitor, question);

		PType basictype = node.getBasicType();
		
		if (basictype != null)
		{
			basictype = PTypeAssistant.typeResolve(basictype, null, rootVisitor, question);
		}
		
		LexNameToken typename = node.getTypeName();
		
		if (typename != null)
		{
			node.setTypedef(question.env.findType(typename, node.getLocation().module));

			if (node.getTypedef() == null)
			{
				TypeCheckerErrors.report(3113, "Unknown type name '" + typename + "'",node.getLocation(),node);
			}
		}

		node.setType(new ABooleanBasicType(node.getLocation(),false,null));
		return node.getType();
	}
	
	@Override
	public PType caseAIsOfBaseClassExp(AIsOfBaseClassExp node,
			TypeCheckInfo question) {
		
		if (question.env.findType(node.getBaseClass(), null) == null)
		{
			TypeCheckerErrors.report(3114, "Undefined base class type: " + node.getBaseClass().name,node.getLocation(),node);
		}

		question.qualifiers = null;
		PType rt = node.getExp().apply(rootVisitor, question);

		if (!PTypeAssistant.isClass(rt))
		{
			TypeCheckerErrors.report(3266, "Argument is not an object",node.getExp().getLocation(),node.getExp());
		}

		node.setType(new ABooleanBasicType(node.getLocation(),false,null));
		return node.getType();
	}
	
	@Override
	public PType caseAIsOfClassExp(AIsOfClassExp node, TypeCheckInfo question) {
		
		LexNameToken classname = node.getClassName();
		PDefinition cls = question.env.findType(classname, null);

		if (cls == null || !(cls instanceof SClassDefinition))
		{
			TypeCheckerErrors.report(3115, "Undefined class type: " + classname.name,node.getLocation(),node);
		}
		else
		{
			node.setClassType((AClassType)cls.getType());
		}

		question.qualifiers = null;
		PType rt = node.getExp().apply(rootVisitor, question);

		if (!PTypeAssistant.isClass(rt))
		{
			TypeCheckerErrors.report(3266, "Argument is not an object",node.getExp().getLocation(),node.getExp());
		}

		node.setType(new ABooleanBasicType(node.getLocation(),false,null));
		return node.getType();
	}
	
	@Override
	public PType caseALambdaExp(ALambdaExp node, TypeCheckInfo question) {
		List<PMultipleBind> mbinds = new Vector<PMultipleBind>();
		List<PType> ptypes = new Vector<PType>();

		List<PPattern> paramPatterns = new Vector<PPattern>();
		List<PDefinition >paramDefinitions = new Vector<PDefinition>();

		for (ATypeBind tb: node.getBindList())
		{
			mbinds.addAll(ATypeBindAssistant.getMultipleBindList(tb));
			paramDefinitions.addAll(PPatternAssistant.getDefinitions(tb.getPattern(), tb.getType(), NameScope.LOCAL));
			paramPatterns.add(tb.getPattern());
			ptypes.add(PTypeAssistant.typeResolve(tb.getType(), null, rootVisitor, question));
		}

		node.setParamPatterns(paramPatterns); 
		
		PDefinitionListAssistant.implicitDefinitions(paramDefinitions,question.env);
		PDefinitionListAssistant.typeCheck(paramDefinitions, rootVisitor, question);

		node.setParamDefinitions(paramDefinitions);
		
		
		PDefinition def = new AMultiBindListDefinition(node.getLocation(),null,null,false,null,null,null, mbinds,null);
		def.apply(rootVisitor, question);
		Environment local = new FlatCheckedEnvironment(def, question.env, question.scope);
		TypeCheckInfo newInfo = new TypeCheckInfo();
		newInfo.env = local;
		newInfo.qualifiers = null;
		newInfo.scope = question.scope;
		
		PType result = node.getExpression().apply(rootVisitor, newInfo);
		local.unusedCheck();

		
		node.setType(new AFunctionType(node.getLocation(), false, null, true, ptypes, result));
		return node.getType();
	}
	
	
	@Override
	public PType caseALetBeStExp(ALetBeStExp node, TypeCheckInfo question) {
		
		
		
		PDefinition def = new AMultiBindListDefinition(node.getLocation(), null, null, false, null, null, null, PMultipleBindAssistant.getMultipleBindList(node.getBind()), null);
		
		def.apply(rootVisitor, question);
		
		Environment local = new FlatCheckedEnvironment(def, question.env, question.scope);

		TypeCheckInfo newInfo = new TypeCheckInfo();
		newInfo.env = local;
		newInfo.qualifiers = question.qualifiers;
		newInfo.scope = question.scope;
		
		PExp suchThat = node.getSuchThat();
		
		if ( suchThat != null &&
			!PTypeAssistant.isType(suchThat.apply(rootVisitor, newInfo),ABooleanBasicType.class))
		{
			TypeCheckerErrors.report(3117, "Such that clause is not boolean",node.getLocation(),node);
		}

		newInfo.qualifiers = null;
		PType r = node.getValue().apply(rootVisitor, newInfo);
		local.unusedCheck();
		node.setType(r);
		return r;
	}
	
	@Override
	public PType caseALetDefExp(ALetDefExp node, TypeCheckInfo question) {
		// Each local definition is in scope for later local definitions...

		Environment local = question.env;

		for (PDefinition d: node.getLocalDefs())
		{
			if (d instanceof AExplicitFunctionDefinition)
			{
				// Functions' names are in scope in their bodies, whereas
				// simple variable declarations aren't

				local = new FlatCheckedEnvironment(d, local, question.scope);	// cumulative
				PDefinitionAssistant.implicitDefinitions(d, local);
				TypeCheckInfo newQuestion = new TypeCheckInfo();
				question.env = local;
				
				PDefinitionAssistant.typeResolve(d,rootVisitor,question);

				if (question.env.isVDMPP())
				{
					SClassDefinition cdef = question.env.findClassDefinition();
					d.setClassDefinition(cdef);
					d.setAccess( PAccessSpecifierAssistant.getStatic(d,true));
				}

				
				d.apply(rootVisitor, newQuestion);
			}
			else
			{
				PDefinitionAssistant.implicitDefinitions(d, local);
				question.env = local;
				PDefinitionAssistant.typeResolve(d, rootVisitor, question);
				d.apply(rootVisitor, question);
				local = new FlatCheckedEnvironment(d, local, question.scope);	// cumulative
			}
		}

		question.qualifiers = null;
		PType r = node.getExpression().apply(rootVisitor, question);
		local.unusedCheck(question.env);
		node.setType(r);
		return r;
	}

	
	@Override
	public PType caseADefExp(ADefExp node, TypeCheckInfo question) {
		// Does not have a type check
		return super.caseADefExp(node, question);
	}
	
	
	@Override
	public PType caseAMapCompMapExp(AMapCompMapExp node, TypeCheckInfo question) {
		
		PDefinition def = new AMultiBindListDefinition(node.getLocation(), null, null, false, null, null, null, node.getBindings(), null);
		def.apply(rootVisitor, question);
		Environment local = new FlatCheckedEnvironment(def, question.env, question.scope);

		PExp predicate = node.getPredicate();
		question.env = local;
		if (predicate != null && !PTypeAssistant.isType(predicate.apply(rootVisitor, question), ABooleanBasicType.class))
		{
			TypeCheckerErrors.report(3118, "Predicate is not boolean",predicate.getLocation(),predicate);
		}

		
		node.setType(node.getFirst().apply(rootVisitor, question));	// The map from/to type
		local.unusedCheck();
		return node.getType();
	}
	
	
	@Override
	public PType caseAMapEnumMapExp(AMapEnumMapExp node, TypeCheckInfo question) {
		
		node.setDomTypes(new Vector<PType>());
		node.setRngTypes(new Vector<PType>());

		if (node.getMembers().isEmpty())
		{
			return new AMapMapType(node.getLocation(), false, null, null, null, null);
		}

		PTypeSet dom = new PTypeSet();
		PTypeSet rng = new PTypeSet();

		for (AMapletExp ex: node.getMembers())
		{
			PType mt = ex.apply(rootVisitor, question);

			if (!PTypeAssistant.isMap(mt))
			{
				TypeCheckerErrors.report(3121, "Element is not of maplet type",node.getLocation(),node);
			}
			else
			{
				SMapType maplet = PTypeAssistant.getMap(mt);
				dom.add(maplet.getFrom());
				node.getDomTypes().add(maplet.getFrom());
				rng.add(maplet.getTo());
				node.getRngTypes().add(maplet.getTo());
			}
		}
		node.setType(new AMapMapType(node.getLocation(), false, null, dom.getType(node.getLocation()), rng.getType(node.getLocation()), null));
		return node.getType();
		
	}
	
	@Override
	public PType caseAMapletExp(AMapletExp node, TypeCheckInfo question) {
		
		PType ltype = node.getLeft().apply(rootVisitor, question);
		PType rtype = node.getRight().apply(rootVisitor, question);
		node.setType(new AMapMapType(node.getLocation(), false, null, ltype, rtype, null));
		return node.getType();
	}
	
	@Override
	public PType caseAMkBasicExp(AMkBasicExp node, TypeCheckInfo question) {
		PType argtype = node.getArg().apply(rootVisitor, question);

		if (!(node.getType() instanceof ATokenBasicType) && !PTypeAssistant.equals(argtype, node.getType()))
		{
			TypeCheckerErrors.report(3125, "Argument of mk_" + node.getType() + " is the wrong type",node.getLocation(),node);
		}

		return node.getType();
	}
	
	
	@Override
	public PType caseAMkTypeExp(AMkTypeExp node, TypeCheckInfo question) {
		
		PDefinition typeDef = question.env.findType(node.getTypeName(), node.getLocation().module);

		if (typeDef == null)
		{
			TypeCheckerErrors.report(3126, "Unknown type '" + node.getTypeName() + "' in constructor",node.getLocation(),node);
			node.setType(new AUnknownType(node.getLocation(), false, null));
			return node.getType();
		}

		PType rec = typeDef.getType();

		while (rec instanceof ANamedInvariantType)
		{
			ANamedInvariantType nrec = (ANamedInvariantType)rec;
			rec = nrec.getType();
		}

		if (!(rec instanceof ARecordInvariantType))
		{
			TypeCheckerErrors.report(3127, "Type '" + node.getTypeName() + "' is not a record type",node.getLocation(),node);
			node.setType(rec);
			return rec;
		}

		node.setRecordType((ARecordInvariantType)rec);

		if (node.getRecordType().getOpaque())
		{
			TypeCheckerErrors.report(3127, "Type '" + node.getTypeName() + "' is not a record type",node.getLocation(),node);
			node.setType(rec);
			return rec;
		}

		if (node.getTypeName().explicit)
		{
			// If the type name is explicit, the Type ought to have an explicit
			// name. This only really affects trace expansion.

			ARecordInvariantType recordType = node.getRecordType();
			
			AExplicitFunctionDefinition inv = recordType.getInvDef();
			
			
			recordType= new ARecordInvariantType(null, false,  recordType.getName().getExplicit(true), recordType.getFields());
			recordType.setInvDef(inv);
		}

		if (node.getRecordType().getFields().size() != node.getArgs().size())
		{
			TypeCheckerErrors.report(3128, "Record and constructor do not have same number of fields",node.getLocation(),node);
			node.setType(rec);
			return rec;
		}

		int i=0;
		Iterator<AFieldField> fiter = node.getRecordType().getFields().iterator();
		node.setArgTypes(new LinkedList<PType>());
		List<PType> argTypes = node.getArgTypes();
		
		
		for (PExp arg: node.getArgs())
		{
			PType fieldType = fiter.next().getType();
			PType argType = arg.apply(rootVisitor, question);
			i++;

			if (!TypeComparator.compatible(fieldType, argType))
			{
				TypeCheckerErrors.report(3129, "Constructor field " + i + " is of wrong type",node.getLocation(),node);
				TypeCheckerErrors.detail2("Expected", fieldType, "Actual", argType);
			}

			argTypes.add(argType);
		}

		node.setType(node.getRecordType());
		return node.getRecordType();
	}
	
	@Override
	public PType caseAMuExp(AMuExp node, TypeCheckInfo question) {
		
		PType rtype = node.getRecord().apply(rootVisitor, question);

		if (PTypeAssistant.isUnknown(rtype))
		{
			node.setType(rtype);
			return rtype;
		}

		if (PTypeAssistant.isRecord(rtype))
		{
			node.setRecordType(PTypeAssistant.getRecord(rtype));
			node.setModTypes(new LinkedList<PType>());

			List<PType> modTypes = node.getModTypes(); 
			
    		for (ARecordModifier rm: node.getModifiers())
    		{
    			PType mtype = rm.getValue().apply(rootVisitor, question);
    			modTypes.add(mtype);
    			AFieldField f = ARecordInvariantTypeAssistant.findField(node.getRecordType(), rm.getTag().name);

    			if (f != null)
    			{
					if (!TypeComparator.compatible(f.getType(), mtype))
					{
						TypeCheckerErrors.report(3130, "Modifier for " + f.getTag() + " should be " + f.getType(),node.getLocation(),node);
						TypeCheckerErrors.detail("Actual", mtype);
					}
    			}
    			else
    			{
    				TypeCheckerErrors.report(3131, "Modifier tag " + rm.getTag() + " not found in record",node.getLocation(),node);
    			}
    		}
		}
		else
		{
			TypeCheckerErrors.report(3132, "mu operation on non-record type",node.getLocation(),node);
		}
		node.setType(rtype);
		return rtype;
	}
	
	
	@Override
	public PType caseANewExp(ANewExp node, TypeCheckInfo question) {
		
		PDefinition cdef = question.env.findType(node.getClassName().getClassName(), null);

		if (cdef == null || !(cdef instanceof SClassDefinition))
		{
			TypeCheckerErrors.report(3133, "Class name " + node.getClassName() + " not in scope",node.getLocation(),node);
			node.setType(new AUnknownType(node.getLocation(),false));
			return node.getType();
		}

		node.setClassdef((SClassDefinition)cdef);

		SClassDefinition classdef = node.getClassdef();
		 
		if (classdef instanceof ASystemClassDefinition)
		{
			TypeCheckerErrors.report(3279, "Cannot instantiate system class " + classdef.getName(),node.getLocation(),node);
		}

		List<PType> argtypes = new LinkedList<PType>();

		for (PExp a: node.getArgs())
		{
			argtypes.add(a.apply(rootVisitor, question));
		}

		PDefinition opdef = SClassDefinitionAssistant.findConstructor(classdef,argtypes);

		if (opdef == null)
		{
			if (!node.getArgs().isEmpty())	// Not having a default ctor is OK
    		{
    			TypeCheckerErrors.report(3134, "Class has no constructor with these parameter types",node.getLocation(),node);
    			TypeCheckerErrors.detail("Called", SClassDefinitionAssistant.getCtorName(classdef, argtypes));
    		}
			else if (classdef instanceof ACpuClassDefinition ||
					 classdef instanceof ABusClassDefinition)
			{
				TypeCheckerErrors.report(3297, "Cannot use default constructor for this class",node.getLocation(),node);
			}
		}
		else
		{
			if (!PDefinitionAssistant.isCallableOperation(opdef))
    		{
    			TypeCheckerErrors.report(3135, "Class has no constructor with these parameter types",node.getLocation(),node);
    			TypeCheckerErrors.detail("Called", SClassDefinitionAssistant.getCtorName(classdef, argtypes));
    		}
			else if (!SClassDefinitionAssistant.isAccessible(question.env, opdef, false)) // (opdef.accessSpecifier.access == Token.PRIVATE)
			{
    			TypeCheckerErrors.report(3292, "Constructor is not accessible",node.getLocation(),node);
    			TypeCheckerErrors.detail("Called", SClassDefinitionAssistant.getCtorName(classdef, argtypes));
			}
			else
			{
				node.setCtorDefinition(opdef);
			}
		}

		node.setType(classdef.getType());
		return classdef.getType();
	}
	
	
	@Override
	public PType caseANilExp(ANilExp node, TypeCheckInfo question) {
		node.setType(new AOperationType(node.getLocation(), false, null, new AUnknownType(node.getLocation(), false)));
		return  node.getType();
	}
	
	@Override
	public PType caseANotYetSpecifiedExp(ANotYetSpecifiedExp node, TypeCheckInfo question)
	{
		node.setType(new AUnknownType(node.getLocation(),false,null));
		return node.getType();	// Because we terminate anyway
	}
			
	@Override
	public PType caseAPostOpExp(APostOpExp node, TypeCheckInfo question)
	{
		node.setType(node.getPostexpression().apply(this,question));
		return node.getType();		
	}
	
	@Override
	public PType caseAPreExp(APreExp node, TypeCheckInfo question) {
		
		question.qualifiers = null;
		node.getFunction().apply(rootVisitor, question);

		for (PExp a: node.getArgs())
		{
			question.qualifiers = null;
			a.apply(rootVisitor, question);
		}

		node.setType(new ABooleanBasicType(node.getLocation(),false));
		return node.getType();
	}
	
	@Override
	public PType caseAPreOpExp(APreOpExp node, TypeCheckInfo question) {
		question.qualifiers = null;
		node.setType(node.getExpression().apply(rootVisitor, question));
		return node.getType();
	}

	@Override
	public PType caseAQuoteLiteralExp(AQuoteLiteralExp node, TypeCheckInfo question) {
		node.setType(new AQuoteType(null, false, node.getValue()));
		return node.getType();
	}
	
	@Override
	public PType caseARealLiteralExp(ARealLiteralExp node,
			TypeCheckInfo question) {
		
		LexRealToken value = node.getValue();
		
		if (Math.round(value.value) == value.value)
		{
    		if (value.value < 0)
    		{
    			node.setType(new AIntNumericBasicType(node.getLocation(),false));
    			return node.getType();
    		}
    		else if (value.value == 0)
    		{
    			node.setType(new ANatNumericBasicType(node.getLocation(),false));
    			return node.getType();
    		}
    		else
    		{
    			node.setType(new ANatOneNumericBasicType(node.getLocation(),false));
    			return node.getType();
    		}
		}
		else
		{
			node.setType(new ARealNumericBasicType(node.getLocation(),false));
			return node.getType();
		}		
	}
	
	@Override
	public PType caseASameBaseClassExp(ASameBaseClassExp node,
			TypeCheckInfo question) {
		
		PExp left = node.getLeft();
		PExp right = node.getRight();
		
		question.qualifiers = null;
		PType lt = left.apply(rootVisitor, question);

		if (!PTypeAssistant.isClass(lt))
		{
			TypeCheckerErrors.report(3266, "Argument is not an object",left.getLocation(),left);
		}

		question.qualifiers = null;
		PType rt = right.apply(rootVisitor, question);

		if (!PTypeAssistant.isClass(rt))
		{
			TypeCheckerErrors.report(3266, "Argument is not an object",node.getLocation(),node);
		}

		node.setType(new ABooleanBasicType(node.getLocation(),false));
		return node.getType();
	}
	
	@Override
	public PType caseASameClassExp(ASameClassExp node, TypeCheckInfo question) {
		PExp left = node.getLeft();
		PExp right = node.getRight();
		
		question.qualifiers = null;
		PType lt = left.apply(rootVisitor, question);

		if (!PTypeAssistant.isClass(lt))
		{
			TypeCheckerErrors.report(3266, "Argument is not an object",left.getLocation(),left);
		}

		question.qualifiers = null;
		PType rt = right.apply(rootVisitor, question);

		if (!PTypeAssistant.isClass(rt))
		{
			TypeCheckerErrors.report(3266, "Argument is not an object",node.getLocation(),node);
		}

		node.setType(new ABooleanBasicType(node.getLocation(),false));
		return node.getType();
	}
	
	@Override
	public PType caseASelfExp(ASelfExp node, TypeCheckInfo question) {
		PDefinition cdef = question.env.findName(node.getName(), question.scope);

		if (cdef == null)
		{
			TypeCheckerErrors.report(3154, node.getName() + " not in scope",node.getLocation(),node);
			node.setType(new AUnknownType(node.getLocation(),false));
			return node.getType();
		}
		
		node.setType(cdef.getType());
		return cdef.getType();
	}
	
	@Override
	public PType caseASeqCompSeqExp(ASeqCompSeqExp node, TypeCheckInfo question) {
		
		PDefinition def = new AMultiBindListDefinition(node.getLocation(), null, null, null, null, null, null, ASetBindAssistant.getMultipleBindList(node.getSetBind()), null);
		def.apply(rootVisitor, question);

		if (!PTypeAssistant.isNumeric(def.getType()))
		{
			TypeCheckerErrors.report(3155, "List comprehension must define one numeric bind variable",node.getLocation(),node);
		}

		Environment local = new FlatCheckedEnvironment(def, question.env, question.scope);
		question.env = local;
		PType etype = node.getFirst().apply(rootVisitor, question);
		
		PExp predicate = node.getPredicate();
		
		if (predicate != null)
		{
			question.qualifiers = null;
			if (!PTypeAssistant.isType(predicate.apply(rootVisitor, question),ABooleanBasicType.class))
			{
				TypeCheckerErrors.report(3156, "Predicate is not boolean",predicate.getLocation(),predicate);
			}
		}

		local.unusedCheck();
		node.setType(new ASeqSeqType(node.getLocation(), null, null, etype, null));
		return node.getType();
	}
	
	@Override
	public PType caseASeqEnumSeqExp(ASeqEnumSeqExp node, TypeCheckInfo question) {
	
		PTypeSet ts = new PTypeSet();
		node.setTypes(new LinkedList<PType>());
		List<PType> types = node.getTypes();
		
		
		for (PExp ex: node.getMembers())
		{
			question.qualifiers = null;
			PType mt = ex.apply(rootVisitor, question);
  			ts.add(mt);
  			types.add(mt);
		}

		node.setType(ts.isEmpty() ? new ASeqSeqType(node.getLocation(), null, null, null, null) :
			new ASeq1SeqType(node.getLocation(), null, null, ts.getType(node.getLocation()), null ));
		
		return node.getType();
	}
	
	@Override
	public PType caseASetCompSetExp(ASetCompSetExp node, TypeCheckInfo question) {
		PDefinition def = new AMultiBindListDefinition(node.getFirst().getLocation(), null, null, null, null, null, null, node.getBindings(), null);
		def.apply(rootVisitor, question);
		
		Environment local = new FlatCheckedEnvironment(def, question.env, question.scope);
		question.env = local;
		PType etype = node.getFirst().apply(rootVisitor, question);
		PExp predicate = node.getPredicate();
		
		
		if (predicate != null)
		{
			if (!PTypeAssistant.isType(predicate.apply(rootVisitor, question),ABooleanBasicType.class))
			{
				TypeCheckerErrors.report(3159, "Predicate is not boolean",predicate.getLocation(),predicate);
			}
		}

		local.unusedCheck();
		ASetType setType = new ASetType(node.getLocation(), false, etype, null, false);
		node.setType(setType);
		node.setSetType(setType);
		return setType;
	}
	
	@Override
	public PType caseASetEnumSetExp(ASetEnumSetExp node, TypeCheckInfo question) {
		PTypeSet ts = new PTypeSet();
		node.setTypes(new LinkedList<PType>());
		List<PType> types = node.getTypes();

		for (PExp ex: node.getMembers())
		{
			question.qualifiers = null;
			PType mt = ex.apply(rootVisitor, question);
			ts.add(mt);
			types.add(mt);
		}
		
		node.setType(ts.isEmpty() ? new ASetType(node.getLocation(), null, null, null, null) :
			new ASetType(node.getLocation(), false, ts.getType(node.getLocation()), null, null));
		
		return node.getType();
	}
	
	@Override
	public PType caseASetRangeSetExp(ASetRangeSetExp node,
			TypeCheckInfo question) {

		PExp first = node.getFirst();
		PExp last = node.getLast();
		
		question.qualifiers = null;
		node.setFtype(first.apply(rootVisitor, question));
		question.qualifiers = null;
		node.setLtype(last.apply(rootVisitor,question));

		PType ftype = node.getFtype();
		PType ltype = node.getLtype();
		
		if (!PTypeAssistant.isNumeric(ftype))
		{
			TypeCheckerErrors.report(3166, "Set range type must be an number",ftype.getLocation(),ftype);
		}

		if (!PTypeAssistant.isNumeric(ltype))
		{
			TypeCheckerErrors.report(3167, "Set range type must be an number",ltype.getLocation(),ltype);
		}
		
		node.setType(new ASetType(first.getLocation(), null, new AIntNumericBasicType(node.getLocation(),false), null, null));
		return node.getType();
	}
	
	
	@Override
	public PType caseAStateInitExp(AStateInitExp node, TypeCheckInfo question) {
		
		PPattern pattern = node.getState().getInitPattern();
		PExp exp = node.getState().getInitExpression();
		boolean canBeExecuted = false;

		if (pattern instanceof AIdentifierPattern &&
			exp instanceof AEqualsBinaryExp)
		{
			AEqualsBinaryExp ee = (AEqualsBinaryExp)exp;
			question.qualifiers = null;
			ee.getLeft().apply(rootVisitor, question);

			if (ee.getLeft() instanceof AVariableExp)
			{
				question.qualifiers = null;
				PType rhs = ee.getRight().apply(rootVisitor, question);

				if (PTypeAssistant.isRecord(rhs))
				{
					ARecordInvariantType rt = PTypeAssistant.getRecord(rhs);
					canBeExecuted = rt.getName().equals(node.getState().getName().name);
				}
			}
		}
		else
		{
			question.qualifiers = null;
			exp.apply(rootVisitor, question);
		}

		if (!canBeExecuted)
		{
			TypeCheckerErrors.warning(5010, "State init expression cannot be executed",node.getLocation(),node);
			TypeCheckerErrors.detail("Expected", "p == p = mk_Record(...)");
		}

		node.getState().setCanBeExecuted(canBeExecuted);
		node.setType(new ABooleanBasicType(node.getLocation(), false));
		return node.getType();
	}
	
	@Override
	public PType caseAStringLiteralExp(AStringLiteralExp node,
			TypeCheckInfo question) {
		
		if (node.getValue().value.isEmpty())
		{
			node.setType(new ASeqSeqType(node.getLocation(), false, new ACharBasicType(node.getLocation(), false, null), null));
			return node.getType();
		}
		else
		{
			node.setType(new ASeq1SeqType(node.getLocation(),false, null, new ACharBasicType(node.getLocation(),false,null), null));
			return node.getType() ;
		}
	}
	
	@Override
	public PType caseASubclassResponsibilityExp(
			ASubclassResponsibilityExp node, TypeCheckInfo question) {
		node.setType(new AUnknownType(node.getLocation(),false));
		return node.getType();	// Because we terminate anyway
	}
	
	@Override
	public PType caseASubseqExp(ASubseqExp node, TypeCheckInfo question) {
		question.qualifiers = null;
		PType stype = node.getSeq().apply(rootVisitor, question);
		question.qualifiers = null;
		node.setFtype(node.getFrom().apply(rootVisitor,question));
		PType ftype = node.getFtype();
		question.qualifiers = null;
		node.setTtype(node.getTo().apply(rootVisitor,question));
		PType ttype = node.getTtype();		

		if (!PTypeAssistant.isSeq(stype))
		{
			TypeCheckerErrors.report(3174, "Subsequence is not of a sequence type",node.getLocation(),node);
		}

		if (!PTypeAssistant.isNumeric(ftype))
		{
			TypeCheckerErrors.report(3175, "Subsequence range start is not a number",node.getLocation(),node);
		}

		if (!PTypeAssistant.isNumeric(ttype))
		{
			TypeCheckerErrors.report(3176, "Subsequence range end is not a number",node.getLocation(),node);
		}
		node.setType(stype);
		return stype;
	}
	
	@Override
	public PType caseAThreadIdExp(AThreadIdExp node, TypeCheckInfo question) {
		node.setType(new ANatNumericBasicType(node.getLocation(),false));
		return node.getType();
	}
	
	@Override
	public PType caseATimeExp(ATimeExp node, TypeCheckInfo question) {
		node.setType(new ANatOneNumericBasicType(node.getLocation(),false));
		return node.getType();
	}
	
	@Override
	public PType caseATupleExp(ATupleExp node, TypeCheckInfo question) {
		
		node.setTypes(new LinkedList<PType>());
		List<PType> types = node.getTypes();
		
		for (PExp arg: node.getArgs())
		{
			question.qualifiers = null;
			types.add(arg.apply(rootVisitor, question));
		}

		node.setType(new AProductType(node.getLocation(), false, null, types));
		return node.getType();	// NB mk_() is a product
	}
	
	@Override
	public PType caseAUndefinedExp(AUndefinedExp node, TypeCheckInfo question) {
		node.setType(new AUndefinedType(node.getLocation(), false));
		return node.getType();
	}
	
	@Override
	public PType caseAVariableExp(AVariableExp node, TypeCheckInfo question) {		
		
		Environment env = question.env;
		LexNameToken name = node.getName();
				
		if (env.isVDMPP())
		{
			
			name.setTypeQualifier(question.qualifiers);
    		node.setVardef(env.findName(name, question.scope));
    		PDefinition vardef = node.getVardef();
    		
    		if (vardef != null)
    		{
    			if (vardef.getClassDefinition() != null)
    			{
        			if (!SClassDefinitionAssistant.isAccessible(env, vardef, true))
        			{
        				TypeCheckerErrors.report(3180, "Inaccessible member " + name + " of class " +
        					vardef.getClassDefinition().getName().name,node.getLocation(),node);
        				node.setType(new AUnknownType(node.getLocation(),false));
        				return node.getType();
        			}
        			else if (!PAccessSpecifierAssistant.isStatic(vardef.getAccess()) && env.isStatic())
            		{
        				TypeCheckerErrors.report(3181, "Cannot access " + name + " from a static context",node.getLocation(),node);
        				node.setType(new AUnknownType(node.getLocation(),false));
        				return node.getType();
            		}
    			}
    		}
    		else if (question.qualifiers != null)
    		{
    			// It may be an apply of a map or sequence, which would not
    			// have the type qualifier of its arguments in the name. Or
    			// it might be an apply of a function via a function variable
    			// which would not be qualified.

    			name.setTypeQualifier(null);
    			vardef = env.findName(name, question.scope);

    			if (vardef == null)
    			{
    				name.setTypeQualifier(question.qualifiers);	// Just for error text!
    			}
    		}
    		else
    		{
    			// We may be looking for a bare function/op "x", when in fact
    			// there is one with a qualified name "x(args)". So we check
    			// the possible matches - if there is precisely one, we pick it,
    			// else we raise an ambiguity error.

				for (PDefinition possible: env.findMatches(name))
				{
					if (PDefinitionAssistant.isFunctionOrOperation(possible))
					{
						if (vardef != null)
						{
							TypeCheckerErrors.report(3269, "Ambiguous function/operation name: " + name.name,node.getLocation(),node);
							env.listAlternatives(name);
							break;
						}

						vardef = possible;

						// Set the qualifier so that it will find it at runtime.

						PType pt = possible.getType();

						if (pt instanceof AFunctionType)
						{
							AFunctionType ft = (AFunctionType)pt;
							name.setTypeQualifier(ft.getParameters());
						}
						else
						{
							AOperationType ot = (AOperationType)pt;
							name.setTypeQualifier(ot.getParameters());
						}
					}
				}
    		}
    	}
    	else
    	{
    		node.setVardef(env.findName(name, question.scope));
    	}

		if (node.getVardef() == null)
		{
			TypeCheckerErrors.report(3182, "Name '" + name + "' is not in scope",node.getLocation(),node);
			env.listAlternatives(name);
			node.setType(new AUnknownType(node.getLocation(),false));
			return node.getType();
		}
		else
		{
			// Note that we perform an extra typeResolve here. This is
			// how forward referenced types are resolved, and is the reason
			// we don't need to retry at the top level (assuming all names
			// are in the environment).
			node.setType(PTypeAssistant.typeResolve(node.getVardef().getType(), null, rootVisitor, question));
			return node.getType();
		}
	}
}
