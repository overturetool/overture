package org.overture.typechecker.visitor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.ABusClassDefinition;
import org.overture.ast.definitions.ACpuClassDefinition;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.AMultiBindListDefinition;
import org.overture.ast.definitions.APerSyncDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.ASystemClassDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.*;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.lex.LexRealToken;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.ASetBind;
import org.overture.ast.patterns.ATypeBind;
import org.overture.ast.patterns.PBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.ABooleanBasicType;
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
import org.overture.ast.types.ARealNumericBasicType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.ASeq1SeqType;
import org.overture.ast.types.ASeqSeqType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.ATokenBasicType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SNumericBasicType;
import org.overture.ast.types.SSeqType;
import org.overture.ast.util.PTypeSet;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.typechecker.Environment;
import org.overture.typechecker.FlatCheckedEnvironment;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.TypeChecker;
import org.overture.typechecker.TypeCheckerErrors;
import org.overture.typechecker.TypeComparator;
import org.overture.typechecker.assistant.definition.AExplicitFunctionDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AImplicitFunctionDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.PAccessSpecifierAssistantTC;
import org.overture.typechecker.assistant.definition.PDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.PDefinitionListAssistantTC;
import org.overture.typechecker.assistant.definition.SClassDefinitionAssistantTC;
import org.overture.typechecker.assistant.expression.AApplyExpAssistantTC;
import org.overture.typechecker.assistant.expression.ACaseAlternativeAssistantTC;
import org.overture.typechecker.assistant.expression.SBinaryExpAssistantTC;
import org.overture.typechecker.assistant.pattern.ATypeBindAssistantTC;
import org.overture.typechecker.assistant.pattern.PBindAssistantTC;
import org.overture.typechecker.assistant.pattern.PMultipleBindAssistantTC;
import org.overture.typechecker.assistant.pattern.PPatternAssistantTC;
import org.overture.typechecker.assistant.type.AClassTypeAssistantTC;
import org.overture.typechecker.assistant.type.AFunctionTypeAssistantTC;
import org.overture.typechecker.assistant.type.AOperationTypeAssistantTC;
import org.overture.typechecker.assistant.type.ARecordInvariantTypeAssistantTC;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;
import org.overture.typechecker.assistant.type.SNumericBasicTypeAssistantTC;

public class TypeCheckerExpVisitor extends
		QuestionAnswerAdaptor<TypeCheckInfo, PType> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final private QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor;

	public TypeCheckerExpVisitor(
			QuestionAnswerAdaptor<TypeCheckInfo, PType> typeCheckVisitor) {
		this.rootVisitor = typeCheckVisitor;
	}

	@Override
	public PType caseAApplyExp(AApplyExp node, TypeCheckInfo question)
			throws AnalysisException {
		node.setArgtypes(new ArrayList<PType>());

		for (PExp a : node.getArgs()) {
			question.qualifiers = null;
			node.getArgtypes().add(a.apply(rootVisitor, question));
		}

		node.setType(node.getRoot().apply(
				rootVisitor,
				new TypeCheckInfo(question.env, question.scope, node
						.getArgtypes())));

		if (PTypeAssistantTC.isUnknown(node.getType())) {
			return node.getType();
		}

		PDefinition func = question.env.getEnclosingDefinition();

		boolean inFunction = (func instanceof AExplicitFunctionDefinition
				|| func instanceof AImplicitFunctionDefinition || func instanceof APerSyncDefinition);

		if (inFunction) {
			PDefinition called = AApplyExpAssistantTC.getRecursiveDefinition(node, question);

			if (called instanceof AExplicitFunctionDefinition) {
				
				AExplicitFunctionDefinition def = (AExplicitFunctionDefinition)called;
				
				if (def.getIsCurried())
				{
					// Only recursive if this apply is the last - so our type is not a function.
					
					if (node.getType() instanceof AFunctionType && ((AFunctionType)node.getType()).getResult() instanceof AFunctionType)
					{
						called = null;
					}
				}
				
			}

			if (called != null) {
				if (func instanceof AExplicitFunctionDefinition)
				{
					AExplicitFunctionDefinition def = (AExplicitFunctionDefinition) func;

					if (called == def) {
						node.setRecursive(def);
						def.setRecursive(true);
					}
				} else if (func instanceof AImplicitFunctionDefinition) {
					AImplicitFunctionDefinition def = (AImplicitFunctionDefinition) func;

					if (called == def) {
						node.setRecursive(def);
						def.setRecursive(true);
					}
				}
			}
		}

		boolean isSimple = !PTypeAssistantTC.isUnion(node.getType());
		PTypeSet results = new PTypeSet();

		if (PTypeAssistantTC.isFunction(node.getType())) {
			AFunctionType ft = PTypeAssistantTC.getFunction(node.getType());
			AFunctionTypeAssistantTC.typeResolve(ft, null, rootVisitor,
					question);
			results.add(AApplyExpAssistantTC.functionApply(node, isSimple, ft));
		}

		if (PTypeAssistantTC.isOperation(node.getType())) {
			AOperationType ot = PTypeAssistantTC.getOperation(node.getType());
			AOperationTypeAssistantTC.typeResolve(ot, null, rootVisitor,
					question);

			if (inFunction && Settings.release == Release.VDM_10) {
				TypeCheckerErrors.report(3300, "Operation '" + node.getRoot()
						+ "' cannot be called from a function",
						node.getLocation(), node);
				results.add(AstFactory.newAUnknownType(node.getLocation()));
			} else {
				results.add(AApplyExpAssistantTC.operationApply(node, isSimple,
						ot));
			}
		}

		if (PTypeAssistantTC.isSeq(node.getType())) {
			SSeqType seq = PTypeAssistantTC.getSeq(node.getType());
			results.add(AApplyExpAssistantTC.sequenceApply(node, isSimple, seq));
		}

		if (PTypeAssistantTC.isMap(node.getType())) {
			SMapType map = PTypeAssistantTC.getMap(node.getType());
			results.add(AApplyExpAssistantTC.mapApply(node, isSimple, map));
		}

		if (results.isEmpty()) {
			TypeCheckerErrors.report(3054, "Type " + node.getType()
					+ " cannot be applied", node.getLocation(), node);
			return AstFactory.newAUnknownType(node.getLocation());
		}

		node.setType(results.getType(node.getLocation()));
		return node.getType(); // Union of possible applications
	}

	@Override
	public PType defaultSBooleanBinaryExp(SBooleanBinaryExp node,
			TypeCheckInfo question) throws AnalysisException {
		node.setType(SBinaryExpAssistantTC.binaryCheck(node,
				AstFactory.newABooleanBasicType(node.getLocation()),
				rootVisitor, question));
		return node.getType();
	}

	@Override
	public PType caseACompBinaryExp(ACompBinaryExp node, TypeCheckInfo question)
			throws AnalysisException {
		node.getLeft().apply(rootVisitor, question);
		node.getRight().apply(rootVisitor, question);

		PTypeSet results = new PTypeSet();

		if (PTypeAssistantTC.isMap(node.getLeft().getType())) {
			if (!PTypeAssistantTC.isMap(node.getRight().getType())) {
				TypeCheckerErrors.report(3068,
						"Right hand of map 'comp' is not a map",
						node.getLocation(), node);
				TypeCheckerErrors.detail("Type", node.getRight().getType());
				node.setType(AstFactory.newAMapMapType(node.getLocation())); // Unknown
																				// types
																				// types
				return node.getType();
			}

			SMapType lm = PTypeAssistantTC.getMap(node.getLeft().getType());
			SMapType rm = PTypeAssistantTC.getMap(node.getRight().getType());

			if (!TypeComparator.compatible(lm.getFrom(), rm.getTo())) {
				TypeCheckerErrors
						.report(3069,
								"Domain of left should equal range of right in map 'comp'",
								node.getLocation(), node);
				TypeCheckerErrors.detail2("Dom", lm.getFrom(), "Rng",
						rm.getTo());
			}

			results.add(AstFactory.newAMapMapType(node.getLocation(),
					rm.getFrom(), lm.getTo()));
		}

		if (PTypeAssistantTC.isFunction(node.getLeft().getType())) {
			if (!PTypeAssistantTC.isFunction(node.getRight().getType())) {
				TypeCheckerErrors.report(3070,
						"Right hand of function 'comp' is not a function",
						node.getLocation(), node);
				TypeCheckerErrors.detail("Type", node.getRight().getType());
				node.setType(AstFactory.newAUnknownType(node.getLocation()));
				return node.getType();
			} else {
				AFunctionType lf = PTypeAssistantTC.getFunction(node.getLeft()
						.getType());
				AFunctionType rf = PTypeAssistantTC.getFunction(node.getRight()
						.getType());

				if (lf.getParameters().size() != 1) {
					TypeCheckerErrors.report(3071,
							"Left hand function must have a single parameter",
							node.getLocation(), node);
					TypeCheckerErrors.detail("Type", lf);
				} else if (rf.getParameters().size() != 1) {
					TypeCheckerErrors.report(3072,
							"Right hand function must have a single parameter",
							node.getLocation(), node);
					TypeCheckerErrors.detail("Type", rf);
				} else if (!TypeComparator.compatible(
						lf.getParameters().get(0), rf.getResult())) {
					TypeCheckerErrors
							.report(3073,
									"Parameter of left should equal result of right in function 'comp'",
									node.getLocation(), node);
					TypeCheckerErrors.detail2("Parameter", lf.getParameters()
							.get(0), "Result", rf.getResult());
				}

				results.add(AstFactory.newAFunctionType(node.getLocation(),
						true, rf.getParameters(), lf.getResult()));

			}
		}

		if (results.isEmpty()) {
			TypeCheckerErrors.report(3074,
					"Left hand of 'comp' is neither a map nor a function",
					node.getLocation(), node);
			TypeCheckerErrors.detail("Type", node.getLeft().getType());
			node.setType(AstFactory.newAUnknownType(node.getLocation()));
			return node.getType();
		}

		node.setType(results.getType(node.getLocation()));
		return node.getType();
	}

	@Override
	public PType caseADomainResByBinaryExp(ADomainResByBinaryExp node,
			TypeCheckInfo question) throws AnalysisException {
		node.getLeft().apply(rootVisitor, question);
		node.getRight().apply(rootVisitor, question);

		if (!PTypeAssistantTC.isSet(node.getLeft().getType())) {
			TypeCheckerErrors.report(3079, "Left of '<-:' is not a set",
					node.getLocation(), node);
		} else if (!PTypeAssistantTC.isMap(node.getRight().getType())) {
			TypeCheckerErrors.report(3080, "Right of '<-:' is not a map",
					node.getLocation(), node);
		} else {
			ASetType set = PTypeAssistantTC.getSet(node.getLeft().getType());
			SMapType map = PTypeAssistantTC.getMap(node.getRight().getType());

			if (!TypeComparator.compatible(set.getSetof(), map.getFrom())) {
				TypeCheckerErrors.report(3081,
						"Restriction of map should be set of " + map.getFrom(),
						node.getLocation(), node);
			}
		}

		node.setType(node.getRight().getType());
		return node.getType();
	}

	@Override
	public PType caseADomainResToBinaryExp(ADomainResToBinaryExp node,
			TypeCheckInfo question) throws AnalysisException {
		node.getLeft().apply(rootVisitor, question);
		node.getRight().apply(rootVisitor, question);

		if (!PTypeAssistantTC.isSet(node.getLeft().getType())) {
			TypeCheckerErrors.report(3082, "Left of '<:' is not a set",
					node.getLocation(), node);
			TypeCheckerErrors.detail("Actual", node.getLeft().getType());
		} else if (!PTypeAssistantTC.isMap(node.getRight().getType())) {
			TypeCheckerErrors.report(3083, "Right of '<:' is not a map",
					node.getLocation(), node);
			TypeCheckerErrors.detail("Actual", node.getRight().getType());
		} else {
			ASetType set = PTypeAssistantTC.getSet(node.getLeft().getType());
			SMapType map = PTypeAssistantTC.getMap(node.getRight().getType());

			if (!TypeComparator.compatible(set.getSetof(), map.getFrom())) {
				TypeCheckerErrors.report(3084,
						"Restriction of map should be set of " + map.getFrom(),
						node.getLocation(), node);
			}
		}

		node.setType(node.getRight().getType());
		return node.getType();
	}

	@Override
	public PType caseAEqualsBinaryExp(AEqualsBinaryExp node,
			TypeCheckInfo question) throws AnalysisException {
		node.getLeft().apply(rootVisitor, question);
		node.getRight().apply(rootVisitor, question);

		if (!TypeComparator.compatible(node.getLeft().getType(), node
				.getRight().getType())) {
			TypeCheckerErrors.report(3087,
					"Left and right of '=' are incompatible types",
					node.getLocation(), node);
			TypeCheckerErrors.detail2("Left", node.getLeft().getType(),
					"Right", node.getRight().getType());
		}

		node.setType(AstFactory.newABooleanBasicType(node.getLocation()));
		return node.getType();
	}

	@Override
	public PType caseAInSetBinaryExp(AInSetBinaryExp node,
			TypeCheckInfo question) throws AnalysisException {
		node.getLeft().apply(rootVisitor, question);
		node.getRight().apply(rootVisitor, question);

		if (!PTypeAssistantTC.isSet(node.getRight().getType())) {
			TypeCheckerErrors.report(3110, "Argument of 'in set' is not a set",
					node.getLocation(), node);
		}

		node.setType(AstFactory.newABooleanBasicType(node.getLocation()));
		return node.getType();
	}

	@Override
	public PType caseAMapUnionBinaryExp(AMapUnionBinaryExp node,
			TypeCheckInfo question) throws AnalysisException {
		node.getLeft().apply(rootVisitor, question);
		node.getRight().apply(rootVisitor, question);

		if (!PTypeAssistantTC.isMap(node.getLeft().getType())) {
			TypeCheckerErrors.report(3123,
					"Left hand of 'munion' is not a map", node.getLocation(),
					node);
			TypeCheckerErrors.detail("Type", node.getLeft().getType());
			node.setType(AstFactory.newAMapMapType(node.getLocation())); // Unknown
																			// types
			return node.getType();
		} else if (!PTypeAssistantTC.isMap(node.getRight().getType())) {
			TypeCheckerErrors.report(3124,
					"Right hand of 'munion' is not a map", node.getLocation(),
					node);
			TypeCheckerErrors.detail("Type", node.getRight().getType());
			node.setType(node.getLeft().getType());
			return node.getType();
		} else {
			SMapType ml = PTypeAssistantTC.getMap(node.getLeft().getType());
			SMapType mr = PTypeAssistantTC.getMap(node.getRight().getType());

			PTypeSet from = new PTypeSet();
			from.add(ml.getFrom());
			from.add(mr.getFrom());
			PTypeSet to = new PTypeSet();
			to.add(ml.getTo());
			to.add(mr.getTo());

			node.setType(AstFactory.newAMapMapType(node.getLocation(),
					from.getType(node.getLocation()),
					to.getType(node.getLocation())));
			return node.getType();
		}
	}

	@Override
	public PType caseANotEqualBinaryExp(ANotEqualBinaryExp node,
			TypeCheckInfo question) throws AnalysisException {
		node.getLeft().apply(rootVisitor, question);
		node.getRight().apply(rootVisitor, question);

		if (!TypeComparator.compatible(node.getLeft().getType(), node
				.getRight().getType())) {
			TypeCheckerErrors.report(3136,
					"Left and right of '<>' different types",
					node.getLocation(), node);
			TypeCheckerErrors.detail2("Left", node.getLeft().getType(),
					"Right", node.getRight().getType());
		}

		node.setType(AstFactory.newABooleanBasicType(node.getLocation()));
		return node.getType();
	}

	@Override
	public PType caseANotInSetBinaryExp(ANotInSetBinaryExp node,
			TypeCheckInfo question) throws AnalysisException {
		node.getLeft().apply(rootVisitor, question);
		node.getRight().apply(rootVisitor, question);

		if (!PTypeAssistantTC.isSet(node.getRight().getType())) {
			TypeCheckerErrors.report(3138,
					"Argument of 'not in set' is not a set",
					node.getLocation(), node);
			TypeCheckerErrors.detail("Actual", node.getRight().getType());
		}

		node.setType(AstFactory.newABooleanBasicType(node.getLocation()));

		return node.getType();
	}

	@Override
	public PType caseADivNumericBinaryExp(ADivNumericBinaryExp node,
			TypeCheckInfo question) throws AnalysisException {

		SNumericBasicTypeAssistantTC.checkNumeric(node, rootVisitor, question);
		node.setType(AstFactory.newAIntNumericBasicType(node.getLocation()));
		return node.getType();
	}

	@Override
	public PType caseADivideNumericBinaryExp(ADivideNumericBinaryExp node,
			TypeCheckInfo question) throws AnalysisException {

		SNumericBasicTypeAssistantTC.checkNumeric(node, rootVisitor, question);
		node.setType(AstFactory.newARealNumericBasicType(node.getLocation()));
		return node.getType();
	}

	@Override
	public PType caseAGreaterEqualNumericBinaryExp(
			AGreaterEqualNumericBinaryExp node, TypeCheckInfo question)
			throws AnalysisException {

		SNumericBasicTypeAssistantTC.checkNumeric(node, rootVisitor, question);
		node.setType(AstFactory.newABooleanBasicType(node.getLocation()));
		return node.getType();
	}

	@Override
	public PType caseAGreaterNumericBinaryExp(AGreaterNumericBinaryExp node,
			TypeCheckInfo question) throws AnalysisException {

		SNumericBasicTypeAssistantTC.checkNumeric(node, rootVisitor, question);
		node.setType(AstFactory.newABooleanBasicType(node.getLocation()));
		return node.getType();
	}

	@Override
	public PType caseAModNumericBinaryExp(AModNumericBinaryExp node,
			TypeCheckInfo question) throws AnalysisException {

		SNumericBasicTypeAssistantTC.checkNumeric(node, rootVisitor, question);
		node.setType(AstFactory.newANatNumericBasicType(node.getLocation()));
		return node.getType();

	}

	@Override
	public PType caseAPlusNumericBinaryExp(APlusNumericBinaryExp node,
			TypeCheckInfo question) throws AnalysisException {
		SNumericBasicTypeAssistantTC.checkNumeric(node, rootVisitor, question);

		SNumericBasicType ln = PTypeAssistantTC.getNumeric(node.getLeft()
				.getType());
		SNumericBasicType rn = PTypeAssistantTC.getNumeric(node.getRight()
				.getType());

		if (ln instanceof ARealNumericBasicType) {
			node.setType(ln);
			return ln;
		} else if (rn instanceof ARealNumericBasicType) {
			node.setType(rn);
			return rn;
		} else if (ln instanceof AIntNumericBasicType) {
			node.setType(ln);
			return ln;
		} else if (rn instanceof AIntNumericBasicType) {
			node.setType(rn);
			return rn;
		} else if (ln instanceof ANatNumericBasicType
				&& rn instanceof ANatNumericBasicType) {
			node.setType(ln);
			return ln;
		} else {
			node.setType(AstFactory.newANatOneNumericBasicType(ln.getLocation()));
			return node.getType();
		}
	}

	@Override
	public PType caseARemNumericBinaryExp(ARemNumericBinaryExp node,
			TypeCheckInfo question) throws AnalysisException {

		SNumericBasicTypeAssistantTC.checkNumeric(node, rootVisitor, question);
		node.setType(AstFactory.newAIntNumericBasicType(node.getLocation()));
		return node.getType();

	}

	@Override
	public PType caseASubtractNumericBinaryExp(
			ASubtractNumericBinaryExp node, TypeCheckInfo question)
			throws AnalysisException {

		SNumericBasicTypeAssistantTC.checkNumeric(node, rootVisitor, question);

		if (node.getLeft().getType() instanceof ARealNumericBasicType
				|| node.getRight().getType() instanceof ARealNumericBasicType) {
			node.setType(AstFactory.newARealNumericBasicType(node.getLocation()));
			return node.getType();
		} else {
			node.setType(AstFactory.newAIntNumericBasicType(node.getLocation()));
			return node.getType();
		}
	}

	@Override
	public PType caseATimesNumericBinaryExp(ATimesNumericBinaryExp node,
			TypeCheckInfo question) throws AnalysisException {

		SNumericBasicTypeAssistantTC.checkNumeric(node, rootVisitor, question);

		SNumericBasicType ln = PTypeAssistantTC.getNumeric(node.getLeft()
				.getType());
		SNumericBasicType rn = PTypeAssistantTC.getNumeric(node.getRight()
				.getType());

		if (ln instanceof ARealNumericBasicType) {
			node.setType(ln);
			return ln;
		} else if (rn instanceof ARealNumericBasicType) {
			node.setType(rn);
			return rn;
		} else if (ln instanceof AIntNumericBasicType) {
			node.setType(ln);
			return ln;
		} else if (rn instanceof AIntNumericBasicType) {
			node.setType(rn);
			return rn;
		} else if (ln instanceof ANatNumericBasicType) {
			node.setType(ln);
			return ln;
		} else if (rn instanceof ANatNumericBasicType) {	
			node.setType(rn);
			return rn;
		} else {
			node.setType(AstFactory.newANatOneNumericBasicType(ln.getLocation()));
			return node.getType();
		}
	}

	@Override
	public PType caseAPlusPlusBinaryExp(APlusPlusBinaryExp node,
			TypeCheckInfo question) throws AnalysisException {

		node.getLeft().apply(rootVisitor, question);
		node.getRight().apply(rootVisitor, question);

		PTypeSet result = new PTypeSet();

		boolean unique = (!PTypeAssistantTC.isUnion(node.getLeft().getType()) && !PTypeAssistantTC
				.isUnion(node.getRight().getType()));

		if (PTypeAssistantTC.isMap(node.getLeft().getType())) {
			if (!PTypeAssistantTC.isMap(node.getRight().getType())) {
				TypeCheckerErrors.concern(unique, 3141,
						"Right hand of '++' is not a map", node.getLocation(),
						node);
				TypeCheckerErrors.detail(unique, "Type", node.getRight()
						.getType());
				node.setType(AstFactory.newAMapMapType(node.getLocation())); // Unknown
																				// types
				return node.getType();
			}

			SMapType lm = PTypeAssistantTC.getMap(node.getLeft().getType());
			SMapType rm = PTypeAssistantTC.getMap(node.getRight().getType());

			PTypeSet domain = new PTypeSet();
			domain.add(lm.getFrom());
			domain.add(rm.getFrom());
			PTypeSet range = new PTypeSet();
			range.add(lm.getTo());
			range.add(rm.getTo());

			result.add(AstFactory.newAMapMapType(node.getLocation(),
					domain.getType(node.getLocation()),
					range.getType(node.getLocation())));
		}

		if (PTypeAssistantTC.isSeq(node.getLeft().getType())) {
			SSeqType st = PTypeAssistantTC.getSeq(node.getLeft().getType());

			if (!PTypeAssistantTC.isMap(node.getRight().getType())) {
				TypeCheckerErrors.concern(unique, 3142,
						"Right hand of '++' is not a map", node.getLocation(),
						node);
				TypeCheckerErrors.detail(unique, "Type", node.getRight()
						.getType());
			} else {
				SMapType mr = PTypeAssistantTC
						.getMap(node.getRight().getType());

				if (!PTypeAssistantTC.isType(mr.getFrom(),
						SNumericBasicType.class)) {
					TypeCheckerErrors.concern(unique, 3143,
							"Domain of right hand of '++' must be nat1",
							node.getLocation(), node);
					TypeCheckerErrors.detail(unique, "Type", mr.getFrom());
				}
			}

			result.add(st);
		}

		if (result.isEmpty()) {
			TypeCheckerErrors.report(3144,
					"Left of '++' is neither a map nor a sequence",
					node.getLocation(), node);
			node.setType(AstFactory.newAUnknownType(node.getLocation()));
			return node.getType();
		}

		node.setType(result.getType(node.getLocation()));
		return node.getType();
	}

	@Override
	public PType caseAProperSubsetBinaryExp(AProperSubsetBinaryExp node,
			TypeCheckInfo question) throws AnalysisException {

		node.getLeft().apply(rootVisitor, question);
		node.getRight().apply(rootVisitor, question);

		PType ltype = node.getLeft().getType();
		PType rtype = node.getRight().getType();

		if (!PTypeAssistantTC.isSet(ltype)) {
			TypeCheckerErrors.report(3146, "Left hand of " + node.getOp()
					+ " is not a set", node.getLocation(), node);
		}

		if (!PTypeAssistantTC.isSet(rtype)) {
			TypeCheckerErrors.report(3147, "Right hand of " + node.getOp()
					+ " is not a set", node.getLocation(), node);
		}

		node.setType(AstFactory.newABooleanBasicType(node.getLocation()));
		return node.getType();
	}

	@Override
	public PType caseARangeResByBinaryExp(ARangeResByBinaryExp node,
			TypeCheckInfo question) throws AnalysisException {

		node.getLeft().apply(rootVisitor, question);
		node.getRight().apply(rootVisitor, question);

		PType ltype = node.getLeft().getType();
		PType rtype = node.getRight().getType();

		if (!PTypeAssistantTC.isMap(ltype)) {
			TypeCheckerErrors.report(3148, "Left of ':->' is not a map",
					node.getLocation(), node);
		} else if (!PTypeAssistantTC.isSet(rtype)) {
			TypeCheckerErrors.report(3149, "Right of ':->' is not a set",
					node.getLocation(), node);
		} else {
			SMapType map = PTypeAssistantTC.getMap(ltype);
			ASetType set = PTypeAssistantTC.getSet(rtype);

			if (!TypeComparator.compatible(set.getSetof(), map.getTo())) {
				TypeCheckerErrors.report(3150,
						"Restriction of map should be set of " + map.getTo(),
						node.getLocation(), node);
			}
		}

		node.setType(ltype);
		return ltype;
	}

	@Override
	public PType caseARangeResToBinaryExp(ARangeResToBinaryExp node,
			TypeCheckInfo question) throws AnalysisException {

		node.getLeft().apply(rootVisitor, question);
		node.getRight().apply(rootVisitor, question);

		PType ltype = node.getLeft().getType();
		PType rtype = node.getRight().getType();

		if (!PTypeAssistantTC.isMap(ltype)) {
			TypeCheckerErrors.report(3151, "Left of ':>' is not a map",
					node.getLocation(), node);
		} else if (!PTypeAssistantTC.isSet(rtype)) {
			TypeCheckerErrors.report(3152, "Right of ':>' is not a set",
					node.getLocation(), node);
		} else {
			SMapType map = PTypeAssistantTC.getMap(ltype);
			ASetType set = PTypeAssistantTC.getSet(rtype);

			if (!TypeComparator.compatible(set.getSetof(), map.getTo())) {
				TypeCheckerErrors.report(3153,
						"Restriction of map should be set of " + map.getTo(),
						node.getLocation(), node);
			}
		}
		node.setType(ltype);
		return ltype;
	}

	@Override
	public PType caseASeqConcatBinaryExp(ASeqConcatBinaryExp node,
			TypeCheckInfo question) throws AnalysisException {

		node.getLeft().apply(rootVisitor, question);
		node.getRight().apply(rootVisitor, question);

		PType ltype = node.getLeft().getType();
		PType rtype = node.getRight().getType();

		if (!PTypeAssistantTC.isSeq(ltype)) {
			TypeCheckerErrors.report(3157,
					"Left hand of '^' is not a sequence", node.getLocation(),
					node);
			ltype = AstFactory.newASeqSeqType(node.getLocation(),
					AstFactory.newAUnknownType(node.getLocation()));
		}

		if (!PTypeAssistantTC.isSeq(rtype)) {
			TypeCheckerErrors.report(3158,
					"Right hand of '^' is not a sequence", node.getLocation(),
					node);
			rtype = AstFactory.newASeqSeqType(node.getLocation(),
					AstFactory.newAUnknownType(node.getLocation()));
		}

		PType lof = PTypeAssistantTC.getSeq(ltype);
		PType rof = PTypeAssistantTC.getSeq(rtype);
		boolean seq1 = (lof instanceof ASeq1SeqType)
				|| (rof instanceof ASeq1SeqType);

		lof = ((SSeqType) lof).getSeqof();
		rof = ((SSeqType) rof).getSeqof();
		PTypeSet ts = new PTypeSet();
		ts.add(lof);
		ts.add(rof);

		node.setType(seq1 ? AstFactory.newASeq1SeqType(node.getLocation(),
				ts.getType(node.getLocation())) : AstFactory.newASeqSeqType(
				node.getLocation(), ts.getType(node.getLocation())));
		return node.getType();
	}

	@Override
	public PType caseASetDifferenceBinaryExp(ASetDifferenceBinaryExp node,
			TypeCheckInfo question) throws AnalysisException {

		node.getLeft().apply(rootVisitor, question);
		node.getRight().apply(rootVisitor, question);

		PType ltype = node.getLeft().getType();
		PType rtype = node.getRight().getType();

		if (!PTypeAssistantTC.isSet(ltype)) {
			TypeCheckerErrors.report(3160, "Left hand of '\\' is not a set",
					node.getLocation(), node);
		}

		if (!PTypeAssistantTC.isSet(rtype)) {
			TypeCheckerErrors.report(3161, "Right hand of '\\' is not a set",
					node.getLocation(), node);
		}

		if (!TypeComparator.compatible(ltype, rtype)) {
			TypeCheckerErrors.report(3162,
					"Left and right of '\\' are different types",
					node.getLocation(), node);
			TypeCheckerErrors.detail2("Left", ltype, "Right", rtype);
		}

		node.setType(ltype);
		return ltype;
	}

	@Override
	public PType caseASetIntersectBinaryExp(ASetIntersectBinaryExp node,
			TypeCheckInfo question) throws AnalysisException {

		node.getLeft().apply(rootVisitor, question);
		node.getRight().apply(rootVisitor, question);

		PType ltype = node.getLeft().getType();
		PType rtype = node.getRight().getType();

		if (!PTypeAssistantTC.isSet(ltype)) {
			TypeCheckerErrors.report(3163, "Left hand of " + node.getLocation()
					+ " is not a set", node.getLocation(), node);
		}

		if (!PTypeAssistantTC.isSet(rtype)) {
			TypeCheckerErrors.report(3164,
					"Right hand of " + node.getLocation() + " is not a set",
					node.getLocation(), node);
		}

		if (!TypeComparator.compatible(ltype, rtype)) {
			TypeCheckerErrors.report(3165,
					"Left and right of intersect are different types",
					node.getLocation(), node);
			TypeCheckerErrors.detail2("Left", ltype, "Right", rtype);
		}

		node.setType(ltype);
		return ltype;
	}

	@Override
	public PType caseASetUnionBinaryExp(ASetUnionBinaryExp node,
			TypeCheckInfo question) throws AnalysisException {

		node.getLeft().apply(rootVisitor, question);
		node.getRight().apply(rootVisitor, question);

		PType ltype = node.getLeft().getType();
		PType rtype = node.getRight().getType();

		if (!PTypeAssistantTC.isSet(ltype)) {
			TypeCheckerErrors.report(3168, "Left hand of " + node.getOp()
					+ " is not a set", node.getLocation(), node);
		}

		if (!PTypeAssistantTC.isSet(rtype)) {
			TypeCheckerErrors.report(3169, "Right hand of " + node.getOp()
					+ " is not a set", node.getLocation(), node);
		}

		PTypeSet result = new PTypeSet();
		result.add(ltype);
		result.add(rtype);
		node.setType(result.getType(node.getLocation()));
		return node.getType();
	}

	@Override
	public PType caseAStarStarBinaryExp(AStarStarBinaryExp node,
			TypeCheckInfo question) throws AnalysisException {

		node.getLeft().apply(rootVisitor, question);
		node.getRight().apply(rootVisitor, question);

		PType ltype = node.getLeft().getType();
		PType rtype = node.getRight().getType();

		if (PTypeAssistantTC.isMap(ltype)) {
			if (!PTypeAssistantTC.isNumeric(rtype)) {
				// rtype.report(3170,
				// "Map iterator expects nat as right hand arg");
				TypeCheckerErrors.report(3170,
						"Map iterator expects nat as right hand arg",
						rtype.getLocation(), rtype);
			}
		} else if (PTypeAssistantTC.isFunction(ltype)) {
			if (!PTypeAssistantTC.isNumeric(rtype)) {
				TypeCheckerErrors.report(3171,
						"Function iterator expects nat as right hand arg",
						rtype.getLocation(), rtype);
			}
		} else if (PTypeAssistantTC.isNumeric(ltype)) {
			if (!PTypeAssistantTC.isNumeric(rtype)) {
				TypeCheckerErrors.report(3172,
						"'**' expects number as right hand arg",
						rtype.getLocation(), rtype);
			}
		} else {
			TypeCheckerErrors.report(3173,
					"First arg of '**' must be a map, function or number",
					node.getLocation(), node);
			node.setType(AstFactory.newAUnknownType(node.getLocation()));
			return node.getType();
		}

		node.setType(ltype);
		return ltype;
	}

	@Override
	public PType caseASubsetBinaryExp(ASubsetBinaryExp node,
			TypeCheckInfo question) throws AnalysisException {

		node.getLeft().apply(rootVisitor, question);
		node.getRight().apply(rootVisitor, question);

		PType ltype = node.getLeft().getType();
		PType rtype = node.getRight().getType();

		if (!PTypeAssistantTC.isSet(ltype)) {
			TypeCheckerErrors.report(3177, "Left hand of " + node.getOp()
					+ " is not a set", node.getLocation(), node);
			TypeCheckerErrors.detail("Type", ltype);
		}

		if (!PTypeAssistantTC.isSet(rtype)) {
			TypeCheckerErrors.report(3178, "Right hand of " + node.getOp()
					+ " is not a set", node.getLocation(), node);
			TypeCheckerErrors.detail("Type", rtype);
		}

		node.setType(AstFactory.newABooleanBasicType(node.getLocation()));
		return node.getType();
	}

	@Override
	public PType caseABooleanConstExp(ABooleanConstExp node,
			TypeCheckInfo question) {
		node.setType(AstFactory.newABooleanBasicType(node.getLocation()));
		return node.getType();
	}

	@Override
	public PType caseACasesExp(ACasesExp node, TypeCheckInfo question)
			throws AnalysisException {

		question.qualifiers = null;

		PType expType = node.getExpression().apply(rootVisitor, question);

		PTypeSet rtypes = new PTypeSet();

		for (ACaseAlternative c : node.getCases()) {
			rtypes.add(ACaseAlternativeAssistantTC.typeCheck(c, rootVisitor,
					question, expType));
		}

		if (node.getOthers() != null) {
			rtypes.add(node.getOthers().apply(rootVisitor, question));
		}

		node.setType(rtypes.getType(node.getLocation()));
		return node.getType();
	}

	@Override
	public PType caseACharLiteralExp(ACharLiteralExp node,
			TypeCheckInfo question) {

		node.setType(AstFactory.newACharBasicType(node.getLocation()));
		return node.getType();
	}

	@Override
	public PType caseAElseIfExp(AElseIfExp node, TypeCheckInfo question)
			throws AnalysisException {

		if (!PTypeAssistantTC.isType(
				node.getElseIf().apply(rootVisitor, question),
				ABooleanBasicType.class)) {
			TypeCheckerErrors.report(3086, "Else clause is not a boolean",
					node.getLocation(), node);
		}

		node.setType(node.getThen().apply(rootVisitor, question));
		return node.getType();
	}

	@Override
	public PType caseAExists1Exp(AExists1Exp node, TypeCheckInfo question)
			throws AnalysisException {
		node.setDef(AstFactory.newAMultiBindListDefinition(node.getBind()
				.getLocation(), PBindAssistantTC.getMultipleBindList(node
				.getBind())));
		node.getDef().apply(rootVisitor, question);
		Environment local = new FlatCheckedEnvironment(node.getDef(),
				question.env, question.scope);

		if (node.getBind() instanceof ATypeBind) {
			ATypeBind tb = (ATypeBind) node.getBind();
			ATypeBindAssistantTC.typeResolve(tb, rootVisitor, question);
		}

		question.qualifiers = null;
		if (!PTypeAssistantTC.isType(
				node.getPredicate().apply(rootVisitor,
						new TypeCheckInfo(local, question.scope)),
				ABooleanBasicType.class)) {
			TypeCheckerErrors.report(3088, "Predicate is not boolean", node
					.getPredicate().getLocation(), node.getPredicate());
		}

		local.unusedCheck();
		node.setType(AstFactory.newABooleanBasicType(node.getLocation()));
		return node.getType();
	}

	@Override
	public PType caseAExistsExp(AExistsExp node, TypeCheckInfo question)
			throws AnalysisException {

		PDefinition def = AstFactory.newAMultiBindListDefinition(
				node.getLocation(), node.getBindList());
		def.apply(rootVisitor, question);

		Environment local = new FlatCheckedEnvironment(def, question.env,
				question.scope);
		question = new TypeCheckInfo(local, question.scope);
		if (!PTypeAssistantTC.isType(
				node.getPredicate().apply(rootVisitor, question),
				ABooleanBasicType.class)) {
			TypeCheckerErrors.report(3089, "Predicate is not boolean", node
					.getPredicate().getLocation(), node.getPredicate());
		}

		local.unusedCheck();
		node.setType(AstFactory.newABooleanBasicType(node.getLocation()));
		return node.getType();
	}

	@Override
	public PType caseAFieldExp(AFieldExp node, TypeCheckInfo question)
			throws AnalysisException {

		PType root = node.getObject().apply(rootVisitor,
				new TypeCheckInfo(question.env, question.scope));

		if (PTypeAssistantTC.isUnknown(root)) {
			node.setMemberName(new LexNameToken("?", node.getField()));
			node.setType(root);
			return root;
		}

		PTypeSet results = new PTypeSet();
		boolean recOrClass = false;
		boolean unique = !PTypeAssistantTC.isUnion(root);

		if (PTypeAssistantTC.isRecord(root)) {
			ARecordInvariantType rec = PTypeAssistantTC.getRecord(root);
			AFieldField cf = ARecordInvariantTypeAssistantTC.findField(rec,
					node.getField().name);

			if (cf != null) {
				results.add(cf.getType());
			} else {
				TypeCheckerErrors.concern(unique, 3090,
						"Unknown field " + node.getField().name + " in record "
								+ rec.getName(), node.getField().getLocation(),
						node.getField());
			}

			recOrClass = true;
		}

		if (question.env.isVDMPP() && PTypeAssistantTC.isClass(root)) {
			AClassType cls = PTypeAssistantTC.getClassType(root);
			LexNameToken memberName = node.getMemberName();

			if (memberName == null) {
				memberName = AClassTypeAssistantTC.getMemberName(cls,
						node.getField());
				node.setMemberName(memberName);
			}

			memberName.setTypeQualifier(question.qualifiers);
			PDefinition fdef = AClassTypeAssistantTC.findName(cls, memberName,
					question.scope);

			if (fdef == null) {
				// The field may be a map or sequence, which would not
				// have the type qualifier of its arguments in the name...

				List<PType> oldq = memberName.getTypeQualifier();
				memberName.setTypeQualifier(null);
				fdef = AClassTypeAssistantTC.findName(cls, memberName,
						question.scope);
				memberName.setTypeQualifier(oldq); // Just for error text!
			}

			if (fdef == null && memberName.typeQualifier == null) {
				// We might be selecting a bare function or operation, without
				// applying it (ie. no qualifiers). In this case, if there is
				// precisely one possibility, we choose it.

				for (PDefinition possible : question.env
						.findMatches(memberName)) {
					if (PDefinitionAssistantTC.isFunctionOrOperation(possible)) {
						if (fdef != null) {
							fdef = null; // Alas, more than one
							break;
						} else {
							fdef = possible;
						}
					}
				}
			}

			if (fdef == null) {
				TypeCheckerErrors.concern(unique, 3091, "Unknown member "
						+ memberName + " of class " + cls.getName().name, node
						.getField().getLocation(), node.getField());

				if (unique) {
					question.env.listAlternatives(memberName);
				}
			} else if (SClassDefinitionAssistantTC.isAccessible(question.env,
					fdef, false)) {
				// The following gives lots of warnings for self.value access
				// to values as though they are fields of self in the CSK test
				// suite, so commented out for now.

				if (PDefinitionAssistantTC.isStatic(fdef))// && !env.isStatic())
				{
					// warning(5005, "Should access member " + field +
					// " from a static context");
				}

				results.add(PDefinitionAssistantTC.getType(fdef));
				// At runtime, type qualifiers must match exactly
				memberName.setTypeQualifier(fdef.getName().typeQualifier);
			} else {
				TypeCheckerErrors.concern(unique, 3092, "Inaccessible member "
						+ memberName + " of class " + cls.getName().name, node
						.getField().getLocation(), node.getField());
			}

			recOrClass = true;
		}

		if (results.isEmpty()) {
			if (!recOrClass) {
				TypeCheckerErrors.report(3093, "Field '" + node.getField().name
						+ "' applied to non-aggregate type", node.getObject()
						.getLocation(), node.getObject());
			}

			node.setType(AstFactory.newAUnknownType(node.getLocation()));
			return node.getType();
		}

		PType resType = results.getType(node.getLocation());
		node.setType(resType);
		return node.getType();
	}

	@Override
	public PType caseAFieldNumberExp(AFieldNumberExp node,
			TypeCheckInfo question) throws AnalysisException {

		PExp tuple = node.getTuple();
		question.qualifiers = null;
		PType type = tuple.apply(rootVisitor, question);
		node.setType(type);

		if (!PTypeAssistantTC.isProduct(type)) {
			TypeCheckerErrors
					.report(3094, "Field '#" + node.getField()
							+ "' applied to non-tuple type",
							tuple.getLocation(), tuple);
			node.setType(AstFactory.newAUnknownType(node.getLocation()));
			return node.getType();
		}

		AProductType product = PTypeAssistantTC.getProduct(type);
		long fn = node.getField().value;

		if (fn > product.getTypes().size() || fn < 1) {
			TypeCheckerErrors.report(3095,
					"Field number does not match tuple size",
					node.getField().location, node.getField());
			node.setType(AstFactory.newAUnknownType(node.getLocation()));
			return node.getType();
		}

		node.setType(product.getTypes().get((int) fn - 1));
		return node.getType();
	}

	@Override
	public PType caseAForAllExp(AForAllExp node, TypeCheckInfo question)
			throws AnalysisException {
		PDefinition def = AstFactory.newAMultiBindListDefinition(
				node.getLocation(), node.getBindList());
		def.apply(rootVisitor, question);
		Environment local = new FlatCheckedEnvironment(def, question.env,
				question.scope);
		if (!PTypeAssistantTC.isType(
				node.getPredicate().apply(rootVisitor,
						new TypeCheckInfo(local, question.scope)),
				ABooleanBasicType.class)) {
			TypeCheckerErrors.report(3097, "Predicate is not boolean", node
					.getPredicate().getLocation(), node.getPredicate());
		}

		local.unusedCheck();
		node.setType(AstFactory.newABooleanBasicType(node.getLocation()));
		return node.getType();
	}

	@Override
	public PType caseAFuncInstatiationExp(AFuncInstatiationExp node,
			TypeCheckInfo question) throws AnalysisException {

		// If there are no type qualifiers passed because the poly function
		// value
		// is being accessed alone (not applied). In this case, the null
		// qualifier
		// will cause VariableExpression to search for anything that matches the
		// name alone. If there is precisely one, it is selected; if there are
		// several, this is an ambiguity error.
		//
		// Note that a poly function is hard to identify from the actual types
		// passed here because the number of parameters may not equal the number
		// of type parameters.

		PType ftype = node.getFunction().apply(rootVisitor, question);

		if (PTypeAssistantTC.isUnknown(ftype)) {
			node.setType(ftype);
			return ftype;
		}

		if (PTypeAssistantTC.isFunction(ftype)) {
			AFunctionType t = PTypeAssistantTC.getFunction(ftype);
			PTypeSet set = new PTypeSet();

			if (t.getDefinitions().size() == 0) {
				TypeCheckerErrors.report(3098,
						"Function value is not polymorphic",
						node.getLocation(), node);
				set.add(AstFactory.newAUnknownType(node.getLocation()));
			} else {
				boolean serious = (t.getDefinitions().size() == 1);

				for (PDefinition def : t.getDefinitions()) // Possibly a union
															// of several
				{
					List<LexNameToken> typeParams = null;
					def = PDefinitionAssistantTC.deref(def);

					if (def instanceof AExplicitFunctionDefinition) {
						node.setExpdef((AExplicitFunctionDefinition) def
								.clone());
						typeParams = node.getExpdef().getTypeParams();
					} else if (def instanceof AImplicitFunctionDefinition) {
						node.setImpdef((AImplicitFunctionDefinition) def);
						typeParams = node.getImpdef().getTypeParams();
					} else {
						TypeCheckerErrors.report(3099,
								"Polymorphic function is not in scope",
								node.getLocation(), node);
						continue;
					}

					if (typeParams.size() == 0) {
						TypeCheckerErrors.concern(serious, 3100,
								"Function has no type parameters",
								node.getLocation(), node);
						continue;
					}

					if (node.getActualTypes().size() != typeParams.size()) {
						TypeCheckerErrors.concern(serious, 3101, "Expecting "
								+ typeParams.size() + " type parameters",
								node.getLocation(), node);
						continue;
					}

					List<PType> fixed = new Vector<PType>();

					for (PType ptype : node.getActualTypes()) {
						if (ptype instanceof AParameterType) // Recursive
																// polymorphism
						{
							AParameterType pt = (AParameterType) ptype;
							PDefinition d = question.env.findName(pt.getName(),
									question.scope);

							if (d == null) {
								TypeCheckerErrors
										.report(3102, "Parameter name " + pt
												+ " not defined",
												node.getLocation(), node);
								ptype = AstFactory.newAUnknownType(node
										.getLocation());
							} else {
								ptype = d.getType();
							}
						}

						fixed.add(PTypeAssistantTC.typeResolve(ptype, null,
								rootVisitor, question));
					}

					node.setActualTypes(fixed);

					node.setType(node.getExpdef() == null ? AImplicitFunctionDefinitionAssistantTC
							.getType(node.getImpdef(), node.getActualTypes())
							: AExplicitFunctionDefinitionAssistantTC.getType(
									node.getExpdef(), node.getActualTypes()));

					// type = expdef == null ?
					// impdef.getType(actualTypes) :
					// expdef.getType(actualTypes);

					set.add(node.getType());
				}
			}

			if (!set.isEmpty()) {
				node.setType(set.getType(node.getLocation()));
				return node.getType();
			}
		} else {
			TypeCheckerErrors.report(3103,
					"Function instantiation does not yield a function",
					node.getLocation(), node);
		}

		node.setType(AstFactory.newAUnknownType(node.getLocation()));
		return node.getType();
	}

	@Override
	public PType caseAHistoryExp(AHistoryExp node, TypeCheckInfo question) {
		SClassDefinition classdef = question.env.findClassDefinition();

		for (LexNameToken opname : node.getOpnames()) {
			int found = 0;

			for (PDefinition def : classdef.getDefinitions()) {
				if (def.getName() != null && def.getName().matches(opname)) {
					found++;

					if (!PDefinitionAssistantTC.isCallableOperation(def)) {
						TypeCheckerErrors.report(3105, opname
								+ " is not an explicit operation",
								opname.location, opname);
					}
				}
			}

			if (found == 0) {
				TypeCheckerErrors.report(3106, opname + " is not in scope",
						opname.location, opname);
			} else if (found > 1) {
				TypeCheckerErrors.warning(5004,
						"History expression of overloaded operation",
						opname.location, opname);
			}

			if (opname.name.equals(classdef.getName().name)) {
				TypeCheckerErrors.report(3107,
						"Cannot use history of a constructor", opname.location,
						opname);
			}
		}

		node.setType(AstFactory.newANatNumericBasicType(node.getLocation()));

		return node.getType();
	}

	@Override
	public PType caseAIfExp(AIfExp node, TypeCheckInfo question)
			throws AnalysisException {

		question.qualifiers = null;
		if (!PTypeAssistantTC.isType(node.getTest()
				.apply(rootVisitor, question), ABooleanBasicType.class)) {
			TypeChecker.report(3108, "If expression is not a boolean",
					node.getLocation());
		}

		PTypeSet rtypes = new PTypeSet();
		question.qualifiers = null;
		rtypes.add(node.getThen().apply(rootVisitor, question));

		for (AElseIfExp eie : node.getElseList()) {
			question.qualifiers = null;
			rtypes.add(eie.apply(rootVisitor, question));
		}
		question.qualifiers = null;
		rtypes.add(node.getElse().apply(rootVisitor, question));

		node.setType(rtypes.getType(node.getLocation()));
		return node.getType();
	}

	@Override
	public PType caseAIntLiteralExp(AIntLiteralExp node, TypeCheckInfo question) {
		if (node.getValue().value < 0) {
			node.setType(AstFactory.newAIntNumericBasicType(node.getLocation()));
		} else if (node.getValue().value == 0) {
			node.setType(AstFactory.newANatNumericBasicType(node.getLocation()));
		} else {
			node.setType(AstFactory.newANatOneNumericBasicType(node
					.getLocation()));
		}

		return node.getType();
	}

	@Override
	public PType caseAIotaExp(AIotaExp node, TypeCheckInfo question)
			throws AnalysisException {

		PDefinition def = AstFactory.newAMultiBindListDefinition(
				node.getLocation(),
				PBindAssistantTC.getMultipleBindList(node.getBind()));

		def.apply(rootVisitor, question);

		PType rt = null;
		PBind bind = node.getBind();

		if (bind instanceof ASetBind) {
			ASetBind sb = (ASetBind) bind;
			question.qualifiers = null;
			rt = sb.getSet().apply(rootVisitor, question);

			if (PTypeAssistantTC.isSet(rt)) {
				rt = PTypeAssistantTC.getSet(rt).getSetof();
			} else {
				TypeCheckerErrors.report(3112, "Iota set bind is not a set",
						node.getLocation(), node);
			}
		} else {
			ATypeBind tb = (ATypeBind) bind;
			rt = tb.getType();
		}

		Environment local = new FlatCheckedEnvironment(def, question.env,
				question.scope);
		node.getPredicate().apply(rootVisitor,
				new TypeCheckInfo(local, question.scope));
		local.unusedCheck();
		node.setType(rt);
		return rt;
	}

	@Override
	public PType caseAIsExp(AIsExp node, TypeCheckInfo question)
			throws AnalysisException {

		question.qualifiers = null;
		node.getTest().apply(rootVisitor, question);

		PType basictype = node.getBasicType();

		if (basictype != null) {
			basictype = PTypeAssistantTC.typeResolve(basictype, null,
					rootVisitor, question);
		}

		LexNameToken typename = node.getTypeName();

		if (typename != null) {
			PDefinition typeFound = question.env.findType(typename,
					node.getLocation().module);
			if (typeFound == null) {
				TypeCheckerErrors.report(3113, "Unknown type name '" + typename
						+ "'", node.getLocation(), node);
				node.setType(node.getTest().getType());
				return node.getType();
			}
			node.setTypedef(typeFound.clone());

		}

		node.setType(AstFactory.newABooleanBasicType(node.getLocation()));
		return node.getType();
	}

	@Override
	public PType caseAIsOfBaseClassExp(AIsOfBaseClassExp node,
			TypeCheckInfo question) throws AnalysisException {

		if (question.env.findType(node.getBaseClass(), null) == null) {
			TypeCheckerErrors.report(3114,
					"Undefined base class type: " + node.getBaseClass().name,
					node.getLocation(), node);
		}

		question.qualifiers = null;
		PType rt = node.getExp().apply(rootVisitor, question);

		if (!PTypeAssistantTC.isClass(rt)) {
			TypeCheckerErrors.report(3266, "Argument is not an object", node
					.getExp().getLocation(), node.getExp());
		}

		node.setType(AstFactory.newABooleanBasicType(node.getLocation()));
		return node.getType();
	}

	@Override
	public PType caseAIsOfClassExp(AIsOfClassExp node, TypeCheckInfo question)
			throws AnalysisException {

		LexNameToken classname = node.getClassName();
		PDefinition cls = question.env.findType(classname, null);

		if (cls == null || !(cls instanceof SClassDefinition)) {
			TypeCheckerErrors.report(3115, "Undefined class type: "
					+ classname.name, node.getLocation(), node);
		} else {
			node.setClassType((AClassType) cls.getType());
		}

		question.qualifiers = null;
		PType rt = node.getExp().apply(rootVisitor, question);

		if (!PTypeAssistantTC.isClass(rt)) {
			TypeCheckerErrors.report(3266, "Argument is not an object", node
					.getExp().getLocation(), node.getExp());
		}

		node.setType(AstFactory.newABooleanBasicType(node.getLocation()));
		return node.getType();
	}

	@Override
	public PType caseALambdaExp(ALambdaExp node, TypeCheckInfo question)
			throws AnalysisException {
		List<PMultipleBind> mbinds = new Vector<PMultipleBind>();
		List<PType> ptypes = new Vector<PType>();

		List<PPattern> paramPatterns = new Vector<PPattern>();
		List<PDefinition> paramDefinitions = new Vector<PDefinition>();

		// node.setParamPatterns(paramPatterns);
		for (ATypeBind tb : node.getBindList()) {
			mbinds.addAll(ATypeBindAssistantTC.getMultipleBindList(tb));
			paramDefinitions.addAll(PPatternAssistantTC.getDefinitions(
					tb.getPattern(), tb.getType(), NameScope.LOCAL));
			paramPatterns.add(tb.getPattern());
			ptypes.add(PTypeAssistantTC.typeResolve(tb.getType(), null,
					rootVisitor, question));
		}

		node.setParamPatterns(paramPatterns);

		PDefinitionListAssistantTC.implicitDefinitions(paramDefinitions,
				question.env);
		PDefinitionListAssistantTC.typeCheck(paramDefinitions, rootVisitor,
				question);

		node.setParamDefinitions(paramDefinitions);

		PDefinition def = AstFactory.newAMultiBindListDefinition(
				node.getLocation(), mbinds);
		def.apply(rootVisitor, question);
		Environment local = new FlatCheckedEnvironment(def, question.env,
				question.scope);
		TypeCheckInfo newInfo = new TypeCheckInfo(local, question.scope);

		PType result = node.getExpression().apply(rootVisitor, newInfo);
		local.unusedCheck();

		node.setType(AstFactory.newAFunctionType(node.getLocation(), true,
				ptypes, result));
		return node.getType();
	}

	@Override
	public PType caseALetBeStExp(ALetBeStExp node, TypeCheckInfo question)
			throws AnalysisException {
		PDefinition def = AstFactory.newAMultiBindListDefinition(node
				.getLocation(), PMultipleBindAssistantTC
				.getMultipleBindList((PMultipleBind) node.getBind()));

		def.apply(rootVisitor, question);
		node.setDef((AMultiBindListDefinition) def);
		Environment local = new FlatCheckedEnvironment(def, question.env,
				question.scope);

		TypeCheckInfo newInfo = new TypeCheckInfo(local, question.scope,
				question.qualifiers);

		PExp suchThat = node.getSuchThat();

		if (suchThat != null
				&& !PTypeAssistantTC.isType(
						suchThat.apply(rootVisitor, newInfo),
						ABooleanBasicType.class)) {
			TypeCheckerErrors.report(3117, "Such that clause is not boolean",
					node.getLocation(), node);
		}

		newInfo.qualifiers = null;
		PType r = node.getValue().apply(rootVisitor, newInfo);
		local.unusedCheck();
		node.setType(r);
		return r;
	}

	@Override
	public PType caseALetDefExp(ALetDefExp node, TypeCheckInfo question)
			throws AnalysisException {
		// Each local definition is in scope for later local definitions...

		Environment local = question.env;

		for (PDefinition d : node.getLocalDefs()) {
			if (d instanceof AExplicitFunctionDefinition) {
				// Functions' names are in scope in their bodies, whereas
				// simple variable declarations aren't

				local = new FlatCheckedEnvironment(d, local, question.scope); // cumulative
				PDefinitionAssistantTC.implicitDefinitions(d, local);

				PDefinitionAssistantTC.typeResolve(d, rootVisitor,
						new TypeCheckInfo(local, question.scope,
								question.qualifiers));

				if (question.env.isVDMPP()) {
					SClassDefinition cdef = question.env.findClassDefinition();
					PDefinitionAssistantTC.setClassDefinition(d, cdef);
					d.setAccess(PAccessSpecifierAssistantTC.getStatic(d, true));
				}

				d.apply(rootVisitor, new TypeCheckInfo(local, question.scope,
						question.qualifiers));
			} else {
				PDefinitionAssistantTC.implicitDefinitions(d, local);
				PDefinitionAssistantTC.typeResolve(d, rootVisitor,
						new TypeCheckInfo(local, question.scope,
								question.qualifiers));
				d.apply(rootVisitor, new TypeCheckInfo(local, question.scope));
				local = new FlatCheckedEnvironment(d, local, question.scope); // cumulative
			}
		}

		PType r = node.getExpression().apply(rootVisitor,
				new TypeCheckInfo(local, question.scope));
		local.unusedCheck(question.env);
		node.setType(r);
		return r;
	}

	@Override
	public PType caseADefExp(ADefExp node, TypeCheckInfo question)
			throws AnalysisException {
		// Each local definition is in scope for later local definitions...

		Environment local = question.env;

		for (PDefinition d : node.getLocalDefs()) {
			if (d instanceof AExplicitFunctionDefinition) {
				// Functions' names are in scope in their bodies, whereas
				// simple variable declarations aren't

				local = new FlatCheckedEnvironment(d, local, question.scope); // cumulative
				PDefinitionAssistantTC.implicitDefinitions(d, local);
				TypeCheckInfo newQuestion = new TypeCheckInfo(local,
						question.scope);

				PDefinitionAssistantTC.typeResolve(d, rootVisitor, question);

				if (question.env.isVDMPP()) {
					SClassDefinition cdef = question.env.findClassDefinition();
					d.setClassDefinition(cdef);
					d.setAccess(PAccessSpecifierAssistantTC.getStatic(d, true));
				}

				d.apply(rootVisitor, newQuestion);
			} else {
				PDefinitionAssistantTC.implicitDefinitions(d, local);
				PDefinitionAssistantTC.typeResolve(d, rootVisitor,
						new TypeCheckInfo(local, question.scope,
								question.qualifiers));
				d.apply(rootVisitor, new TypeCheckInfo(local, question.scope,
						question.qualifiers));
				local = new FlatCheckedEnvironment(d, local, question.scope); // cumulative
			}
		}

		PType r = node.getExpression().apply(rootVisitor,
				new TypeCheckInfo(local, question.scope));
		local.unusedCheck(question.env);
		node.setType(r);
		return r;
	}

	@Override
	public PType caseAMapCompMapExp(AMapCompMapExp node, TypeCheckInfo question)
			throws AnalysisException {

		PDefinition def = AstFactory.newAMultiBindListDefinition(
				node.getLocation(), node.getBindings());
		def.apply(rootVisitor, question);
		Environment local = new FlatCheckedEnvironment(def, question.env,
				question.scope);

		PExp predicate = node.getPredicate();
		if (predicate != null
				&& !PTypeAssistantTC.isType(predicate.apply(rootVisitor,
						new TypeCheckInfo(local, question.scope,
								question.qualifiers)), ABooleanBasicType.class)) {
			TypeCheckerErrors.report(3118, "Predicate is not boolean",
					predicate.getLocation(), predicate);
		}

		node.setType(node.getFirst().apply(rootVisitor,
				new TypeCheckInfo(local, question.scope, question.qualifiers))); // The
																					// map
																					// from/to
																					// type
		local.unusedCheck();
		return node.getType();
	}

	@Override
	public PType caseAMapEnumMapExp(AMapEnumMapExp node, TypeCheckInfo question)
			throws AnalysisException {

		node.setDomTypes(new Vector<PType>());
		node.setRngTypes(new Vector<PType>());

		if (node.getMembers().isEmpty()) {
			node.setType(AstFactory.newAMapMapType(node.getLocation()));
			return node.getType();
		}

		PTypeSet dom = new PTypeSet();
		PTypeSet rng = new PTypeSet();

		for (AMapletExp ex : node.getMembers()) {
			PType mt = ex.apply(rootVisitor, question);

			if (!PTypeAssistantTC.isMap(mt)) {
				TypeCheckerErrors.report(3121, "Element is not of maplet type",
						node.getLocation(), node);
			} else {
				SMapType maplet = PTypeAssistantTC.getMap(mt);
				dom.add(maplet.getFrom());
				node.getDomTypes().add(maplet.getFrom());
				rng.add(maplet.getTo());
				node.getRngTypes().add(maplet.getTo());
			}
		}
		node.setType(AstFactory.newAMapMapType(node.getLocation(),
				dom.getType(node.getLocation()),
				rng.getType(node.getLocation())));
		return node.getType();

	}

	@Override
	public PType caseAMapletExp(AMapletExp node, TypeCheckInfo question)
			throws AnalysisException {

		PType ltype = node.getLeft().apply(rootVisitor, question);
		PType rtype = node.getRight().apply(rootVisitor, question);
		node.setType(AstFactory.newAMapMapType(node.getLocation(), ltype, rtype));
		return node.getType();
	}

	@Override
	public PType caseAMkBasicExp(AMkBasicExp node, TypeCheckInfo question)
			throws AnalysisException {
		PType argtype = node.getArg().apply(rootVisitor, question);

		if (!(node.getType() instanceof ATokenBasicType)
				&& !PTypeAssistantTC.equals(argtype, node.getType())) {
			TypeCheckerErrors.report(3125, "Argument of mk_" + node.getType()
					+ " is the wrong type", node.getLocation(), node);
		}

		return node.getType();
	}

	@Override
	public PType caseAMkTypeExp(AMkTypeExp node, TypeCheckInfo question)
			throws AnalysisException {

		PDefinition typeDef = question.env.findType(node.getTypeName(),
				node.getLocation().module);

		if (typeDef == null) {
			TypeCheckerErrors.report(3126,
					"Unknown type '" + node.getTypeName() + "' in constructor",
					node.getLocation(), node);
			node.setType(AstFactory.newAUnknownType(node.getLocation()));
			return node.getType();
		}

		PType rec = null;
		if (typeDef instanceof ATypeDefinition) {
			rec = ((ATypeDefinition) typeDef).getInvType();
		} else if (typeDef instanceof AStateDefinition) {
			rec = ((AStateDefinition) typeDef).getRecordType();
		} else {
			rec = PDefinitionAssistantTC.getType(typeDef);
		}

		while (rec instanceof ANamedInvariantType) {
			ANamedInvariantType nrec = (ANamedInvariantType) rec;
			rec = nrec.getType();
		}

		if (!(rec instanceof ARecordInvariantType)) {
			TypeCheckerErrors.report(3127, "Type '" + node.getTypeName()
					+ "' is not a record type", node.getLocation(), node);
			node.setType(rec);
			return rec;
		}

		node.setRecordType((ARecordInvariantType) rec);

		if (node.getRecordType().getOpaque()) {
			TypeCheckerErrors.report(3127, "Type '" + node.getTypeName()
					+ "' is not a record type", node.getLocation(), node);
			node.setType(rec);
			return rec;
		}

		if (node.getTypeName().explicit) {
			// If the type name is explicit, the Type ought to have an explicit
			// name. This only really affects trace expansion.

			ARecordInvariantType recordType = node.getRecordType();

			AExplicitFunctionDefinition inv = recordType.getInvDef();

			recordType = AstFactory.newARecordInvariantType(recordType
					.getName().getExplicit(true), recordType.getFields());
			recordType.setInvDef(inv);
			node.setRecordType(recordType);
		}

		if (node.getRecordType().getFields().size() != node.getArgs().size()) {
			TypeCheckerErrors.report(3128,
					"Record and constructor do not have same number of fields",
					node.getLocation(), node);
			node.setType(rec);
			return rec;
		}

		int i = 0;
		Iterator<AFieldField> fiter = node.getRecordType().getFields()
				.iterator();
		node.setArgTypes(new LinkedList<PType>());
		List<PType> argTypes = node.getArgTypes();

		for (PExp arg : node.getArgs()) {
			PType fieldType = fiter.next().getType();
			PType argType = arg.apply(rootVisitor, question);
			i++;

			if (!TypeComparator.compatible(fieldType, argType)) {
				TypeCheckerErrors.report(3129, "Constructor field " + i
						+ " is of wrong type", node.getLocation(), node);
				TypeCheckerErrors.detail2("Expected", fieldType, "Actual",
						argType);
			}

			argTypes.add(argType);
		}

		node.setType(node.getRecordType().clone());
		return node.getRecordType();
	}

	@Override
	public PType caseAMuExp(AMuExp node, TypeCheckInfo question)
			throws AnalysisException {

		PType rtype = node.getRecord().apply(rootVisitor, question);

		if (PTypeAssistantTC.isUnknown(rtype)) {
			node.setType(rtype);
			return rtype;
		}

		if (PTypeAssistantTC.isRecord(rtype)) {
			node.setRecordType(PTypeAssistantTC.getRecord(rtype));
			node.setModTypes(new LinkedList<PType>());

			List<PType> modTypes = node.getModTypes();

			for (ARecordModifier rm : node.getModifiers()) {
				PType mtype = rm.getValue().apply(rootVisitor, question);
				modTypes.add(mtype);
				AFieldField f = ARecordInvariantTypeAssistantTC.findField(
						node.getRecordType(), rm.getTag().name);

				if (f != null) {
					if (!TypeComparator.compatible(f.getType(), mtype)) {
						TypeCheckerErrors
								.report(3130, "Modifier for " + f.getTag()
										+ " should be " + f.getType(),
										node.getLocation(), node);
						TypeCheckerErrors.detail("Actual", mtype);
					}
				} else {
					TypeCheckerErrors.report(3131,
							"Modifier tag " + rm.getTag()
									+ " not found in record",
							node.getLocation(), node);
				}
			}
		} else {
			TypeCheckerErrors.report(3132, "mu operation on non-record type",
					node.getLocation(), node);
		}
		node.setType(rtype);
		return rtype;
	}
	
	@Override
	public PType caseANarrowExp(ANarrowExp node, TypeCheckInfo question) throws AnalysisException
	{

		node.getTest().setType(node.getTest().apply(rootVisitor, question));
		
		PType result = null;
				
		if(node.getBasicType() != null)
		{
			
			node.setBasicType(PTypeAssistantTC.typeResolve(node.getBasicType(), null, rootVisitor, question));	
			result = node.getBasicType();
		}
		else
		{		
			node.setTypedef(question.env.findType(node.getTypeName(), node.getLocation().module));
			
			if(node.getTypedef() == null)
			{	
				TypeCheckerErrors.report(3113, "Unknown type name '" + node.getTypeName() + "'", node.getLocation(), node);
				result = AstFactory.newAUnknownType(node.getLocation());
			}
			else
			{
				result = PDefinitionAssistantTC.getType(node.getTypedef());
			}
			
		}
		
		if(!TypeComparator.compatible(result, node.getTest().getType()))
		{
			TypeCheckerErrors.report(3317, "Expression can never match narrow type", node.getLocation(), node);
		}
		
		return result;
	}

	@Override
	public PType caseANewExp(ANewExp node, TypeCheckInfo question)
			throws AnalysisException {

		PDefinition cdef = question.env.findType(node.getClassName()
				.getClassName(), null);

		if (cdef == null || !(cdef instanceof SClassDefinition)) {
			TypeCheckerErrors.report(3133, "Class name " + node.getClassName()
					+ " not in scope", node.getLocation(), node);
			node.setType(AstFactory.newAUnknownType(node.getLocation()));
			return node.getType();
		}

		node.setClassdef((SClassDefinition) cdef);

		SClassDefinition classdef = node.getClassdef();

		if (classdef instanceof ASystemClassDefinition) {
			TypeCheckerErrors.report(3279, "Cannot instantiate system class "
					+ classdef.getName(), node.getLocation(), node);
		}

		List<PType> argtypes = new LinkedList<PType>();

		for (PExp a : node.getArgs()) {
			argtypes.add(a.apply(rootVisitor, question));
		}

		PDefinition opdef = SClassDefinitionAssistantTC.findConstructor(
				classdef, argtypes);

		if (opdef == null) {
			if (!node.getArgs().isEmpty()) // Not having a default ctor is OK
			{
				TypeCheckerErrors.report(3134,
						"Class has no constructor with these parameter types",
						node.getLocation(), node);
				TypeCheckerErrors.detail("Called", SClassDefinitionAssistantTC
						.getCtorName(classdef, argtypes));
			} else if (classdef instanceof ACpuClassDefinition
					|| classdef instanceof ABusClassDefinition) {
				TypeCheckerErrors.report(3297,
						"Cannot use default constructor for this class",
						node.getLocation(), node);
			}
		} else {
			if (!PDefinitionAssistantTC.isCallableOperation(opdef)) {
				TypeCheckerErrors.report(3135,
						"Class has no constructor with these parameter types",
						node.getLocation(), node);
				TypeCheckerErrors.detail("Called", SClassDefinitionAssistantTC
						.getCtorName(classdef, argtypes));
			} else if (!SClassDefinitionAssistantTC.isAccessible(question.env,
					opdef, false)) // (opdef.accessSpecifier.access
									// ==
									// Token.PRIVATE)
			{
				TypeCheckerErrors.report(3292, "Constructor is not accessible",
						node.getLocation(), node);
				TypeCheckerErrors.detail("Called", SClassDefinitionAssistantTC
						.getCtorName(classdef, argtypes));
			} else {
				node.setCtorDefinition(opdef);
			}
		}

		PType type = PDefinitionAssistantTC.getType(classdef);
		node.setType(type);
		return type;
	}

	@Override
	public PType caseANilExp(ANilExp node, TypeCheckInfo question) {
		node.setType(AstFactory.newAOptionalType(node.getLocation(),
				AstFactory.newAUnknownType(node.getLocation())));
		return node.getType();
	}

	@Override
	public PType caseANotYetSpecifiedExp(ANotYetSpecifiedExp node,
			TypeCheckInfo question) {
		node.setType(AstFactory.newAUnknownType(node.getLocation()));
		return node.getType(); // Because we terminate anyway
	}

	@Override
	public PType caseAPostOpExp(APostOpExp node, TypeCheckInfo question)
			throws AnalysisException {
		node.setType(node.getPostexpression().apply(rootVisitor, question));
		return node.getType();
	}

	@Override
	public PType caseAPreExp(APreExp node, TypeCheckInfo question)
			throws AnalysisException {

		question.qualifiers = null;
		node.getFunction().apply(rootVisitor, question);

		for (PExp a : node.getArgs()) {
			question.qualifiers = null;
			a.apply(rootVisitor, question);
		}

		node.setType(AstFactory.newABooleanBasicType(node.getLocation()));
		return node.getType();
	}

	@Override
	public PType caseAPreOpExp(APreOpExp node, TypeCheckInfo question)
			throws AnalysisException {
		question.qualifiers = null;
		node.setType(node.getExpression().apply(rootVisitor, question));
		return node.getType();
	}

	@Override
	public PType caseAQuoteLiteralExp(AQuoteLiteralExp node,
			TypeCheckInfo question) {
		node.setType(AstFactory.newAQuoteType(node.getValue().clone()));
		return node.getType();
	}

	@Override
	public PType caseARealLiteralExp(ARealLiteralExp node,
			TypeCheckInfo question) {

		LexRealToken value = node.getValue();

		if (Math.round(value.value) == value.value) {
			if (value.value < 0) {
				node.setType(AstFactory.newAIntNumericBasicType(node
						.getLocation()));
				return node.getType();
			} else if (value.value == 0) {
				node.setType(AstFactory.newANatNumericBasicType(node
						.getLocation()));
				return node.getType();
			} else {
				node.setType(AstFactory.newANatOneNumericBasicType(node
						.getLocation()));
				return node.getType();
			}
		} else {
			node.setType(AstFactory.newARealNumericBasicType(node.getLocation()));
			return node.getType();
		}
	}

	@Override
	public PType caseASameBaseClassExp(ASameBaseClassExp node,
			TypeCheckInfo question) throws AnalysisException {

		PExp left = node.getLeft();
		PExp right = node.getRight();

		question.qualifiers = null;
		PType lt = left.apply(rootVisitor, question);

		if (!PTypeAssistantTC.isClass(lt)) {
			TypeCheckerErrors.report(3266, "Argument is not an object",
					left.getLocation(), left);
		}

		question.qualifiers = null;
		PType rt = right.apply(rootVisitor, question);

		if (!PTypeAssistantTC.isClass(rt)) {
			TypeCheckerErrors.report(3266, "Argument is not an object",
					right.getLocation(), right);
		}

		node.setType(AstFactory.newABooleanBasicType(node.getLocation()));
		return node.getType();
	}

	@Override
	public PType caseASameClassExp(ASameClassExp node, TypeCheckInfo question)
			throws AnalysisException {
		PExp left = node.getLeft();
		PExp right = node.getRight();

		question.qualifiers = null;
		PType lt = left.apply(rootVisitor, question);

		if (!PTypeAssistantTC.isClass(lt)) {
			TypeCheckerErrors.report(3266, "Argument is not an object",
					left.getLocation(), left);
		}

		question.qualifiers = null;
		PType rt = right.apply(rootVisitor, question);

		if (!PTypeAssistantTC.isClass(rt)) {
			TypeCheckerErrors.report(3266, "Argument is not an object",
					right.getLocation(), right);
		}

		node.setType(AstFactory.newABooleanBasicType(node.getLocation()));
		return node.getType();
	}

	@Override
	public PType caseASelfExp(ASelfExp node, TypeCheckInfo question) {
		PDefinition cdef = question.env
				.findName(node.getName(), question.scope);

		if (cdef == null) {
			TypeCheckerErrors.report(3154, node.getName() + " not in scope",
					node.getLocation(), node);
			node.setType(AstFactory.newAUnknownType(node.getLocation()));
			return node.getType();
		}

		node.setType(cdef.getType());
		return cdef.getType();
	}

	@Override
	public PType caseASeqCompSeqExp(ASeqCompSeqExp node, TypeCheckInfo question)
			throws AnalysisException {
		// TODO: check if this is still needed?!
		// save these so we can clone them after they have been type checked
		// PExp setBindSet = node.getSetBind().getSet();
		// PPattern setBindPattern = node.getSetBind().getPattern();
		//
		// List<PPattern> plist = new ArrayList<PPattern>();
		// plist.add(setBindPattern);
		// List<PMultipleBind> mblist = new Vector<PMultipleBind>();
		// mblist.add(new ASetMultipleBind(plist.get(0).getLocation(), plist,
		// setBindSet));

		PDefinition def = AstFactory.newAMultiBindListDefinition(
				node.getLocation(),
				PBindAssistantTC.getMultipleBindList(node.getSetBind()));
		def.apply(rootVisitor, question);

		// now they are typechecked, add them again
		// node.getSetBind().setSet(setBindSet.clone());
		// node.getSetBind().setPattern(setBindPattern.clone());

		if (PPatternAssistantTC
				.getVariableNames(node.getSetBind().getPattern()).size() != 1
				|| !PTypeAssistantTC.isNumeric(PDefinitionAssistantTC
						.getType(def))) {
			TypeCheckerErrors.report(3155,
					"List comprehension must define one numeric bind variable",
					node.getLocation(), node);
		}

		Environment local = new FlatCheckedEnvironment(def, question.env,
				question.scope);
		PType etype = node.getFirst().apply(rootVisitor,
				new TypeCheckInfo(local, question.scope, question.qualifiers));

		PExp predicate = node.getPredicate();

		if (predicate != null) {
			question.qualifiers = null;
			if (!PTypeAssistantTC.isType(predicate.apply(rootVisitor,
					new TypeCheckInfo(local, question.scope,
							question.qualifiers)), ABooleanBasicType.class)) {
				TypeCheckerErrors.report(3156, "Predicate is not boolean",
						predicate.getLocation(), predicate);
			}
		}

		local.unusedCheck();
		node.setType(AstFactory.newASeqSeqType(node.getLocation(), etype));
		return node.getType();
	}

	@Override
	public PType caseASeqEnumSeqExp(ASeqEnumSeqExp node, TypeCheckInfo question)
			throws AnalysisException {

		PTypeSet ts = new PTypeSet();
		node.setTypes(new LinkedList<PType>());
		List<PType> types = node.getTypes();

		for (PExp ex : node.getMembers()) {
			question.qualifiers = null;
			PType mt = ex.apply(rootVisitor, question);
			ts.add(mt);
			types.add(mt);
		}

		node.setType(ts.isEmpty() ? AstFactory.newASeqSeqType(node
				.getLocation()) : AstFactory.newASeq1SeqType(
				node.getLocation(), ts.getType(node.getLocation())));

		return node.getType();
	}

	@Override
	public PType caseASetCompSetExp(ASetCompSetExp node, TypeCheckInfo question)
			throws AnalysisException {
		PDefinition def = AstFactory.newAMultiBindListDefinition(node
				.getFirst().getLocation(), node.getBindings());
		def.apply(rootVisitor, question);

		Environment local = new FlatCheckedEnvironment(def, question.env,
				question.scope);
		question = new TypeCheckInfo(local, question.scope);

		PType etype = node.getFirst().apply(rootVisitor, question);
		PExp predicate = node.getPredicate();

		if (predicate != null) {
			if (!PTypeAssistantTC.isType(
					predicate.apply(rootVisitor, question),
					ABooleanBasicType.class)) {
				TypeCheckerErrors.report(3159, "Predicate is not boolean",
						predicate.getLocation(), predicate);
			}
		}

		local.unusedCheck();
		ASetType setType = AstFactory.newASetType(node.getLocation(), etype);
		node.setType(setType);
		node.setSetType(setType);
		return setType;
	}

	@Override
	public PType caseASetEnumSetExp(ASetEnumSetExp node, TypeCheckInfo question)
			throws AnalysisException {
		PTypeSet ts = new PTypeSet();
		node.setTypes(new LinkedList<PType>());
		List<PType> types = node.getTypes();

		for (PExp ex : node.getMembers()) {
			question.qualifiers = null;
			PType mt = ex.apply(rootVisitor, question);
			ts.add(mt);
			types.add(mt);
		}

		node.setType(ts.isEmpty() ? AstFactory.newASetType(node.getLocation())
				: AstFactory.newASetType(node.getLocation(),
						ts.getType(node.getLocation())));

		return node.getType();
	}

	@Override
	public PType caseASetRangeSetExp(ASetRangeSetExp node,
			TypeCheckInfo question) throws AnalysisException {

		PExp first = node.getFirst();
		PExp last = node.getLast();

		question.qualifiers = null;
		node.setFtype(first.apply(rootVisitor, question));
		question.qualifiers = null;
		node.setLtype(last.apply(rootVisitor, question));

		PType ftype = node.getFtype();
		PType ltype = node.getLtype();

		if (!PTypeAssistantTC.isNumeric(ftype)) {
			TypeCheckerErrors.report(3166, "Set range type must be an number",
					ftype.getLocation(), ftype);
		}

		if (!PTypeAssistantTC.isNumeric(ltype)) {
			TypeCheckerErrors.report(3167, "Set range type must be an number",
					ltype.getLocation(), ltype);
		}

		node.setType(AstFactory.newASetType(first.getLocation(),
				AstFactory.newAIntNumericBasicType(node.getLocation())));
		return node.getType();
	}

	@Override
	public PType caseAStateInitExp(AStateInitExp node, TypeCheckInfo question)
			throws AnalysisException {

		PPattern pattern = node.getState().getInitPattern();
		PExp exp = node.getState().getInitExpression();
		boolean canBeExecuted = false;

		if (pattern instanceof AIdentifierPattern
				&& exp instanceof AEqualsBinaryExp) {
			AEqualsBinaryExp ee = (AEqualsBinaryExp) exp;
			question.qualifiers = null;
			ee.getLeft().apply(rootVisitor, question);

			if (ee.getLeft() instanceof AVariableExp) {
				question.qualifiers = null;
				PType rhs = ee.getRight().apply(rootVisitor, question);

				if (PTypeAssistantTC.isRecord(rhs)) {
					ARecordInvariantType rt = PTypeAssistantTC.getRecord(rhs);
					canBeExecuted = rt.getName().name.equals(node.getState()
							.getName().name);
				}
			}
		} else {
			question.qualifiers = null;
			exp.apply(rootVisitor, question);
		}

		if (!canBeExecuted) {
			TypeCheckerErrors.warning(5010,
					"State init expression cannot be executed",
					node.getLocation(), node);
			TypeCheckerErrors.detail("Expected", "p == p = mk_Record(...)");
		}

		node.getState().setCanBeExecuted(canBeExecuted);
		node.setType(AstFactory.newABooleanBasicType(node.getLocation()));
		return node.getType();
	}

	@Override
	public PType caseAStringLiteralExp(AStringLiteralExp node,
			TypeCheckInfo question) {

		if (node.getValue().value.isEmpty()) {
			ASeqSeqType tt = AstFactory.newASeqSeqType(node.getLocation(),
					AstFactory.newACharBasicType(node.getLocation()));
			node.setType(tt);
			return node.getType();
		} else {
			node.setType(AstFactory.newASeq1SeqType(node.getLocation(),
					AstFactory.newACharBasicType(node.getLocation())));
			return node.getType();
		}
	}

	@Override
	public PType caseASubclassResponsibilityExp(
			ASubclassResponsibilityExp node, TypeCheckInfo question) {
		node.setType(AstFactory.newAUnknownType(node.getLocation()));
		return node.getType(); // Because we terminate anyway
	}

	@Override
	public PType caseASubseqExp(ASubseqExp node, TypeCheckInfo question)
			throws AnalysisException {
		question.qualifiers = null;
		PType stype = node.getSeq().apply(rootVisitor, question);
		question.qualifiers = null;
		node.setFtype(node.getFrom().apply(rootVisitor, question));
		PType ftype = node.getFtype();
		question.qualifiers = null;
		node.setTtype(node.getTo().apply(rootVisitor, question));
		PType ttype = node.getTtype();

		if (!PTypeAssistantTC.isSeq(stype)) {
			TypeCheckerErrors.report(3174,
					"Subsequence is not of a sequence type",
					node.getLocation(), node);
		}

		if (!PTypeAssistantTC.isNumeric(ftype)) {
			TypeCheckerErrors.report(3175,
					"Subsequence range start is not a number",
					node.getLocation(), node);
		}

		if (!PTypeAssistantTC.isNumeric(ttype)) {
			TypeCheckerErrors.report(3176,
					"Subsequence range end is not a number",
					node.getLocation(), node);
		}
		node.setType(stype);
		return stype;
	}

	@Override
	public PType caseAThreadIdExp(AThreadIdExp node, TypeCheckInfo question) {
		node.setType(AstFactory.newANatNumericBasicType(node.getLocation()));
		return node.getType();
	}

	@Override
	public PType caseATimeExp(ATimeExp node, TypeCheckInfo question) {
		node.setType(AstFactory.newANatNumericBasicType(node.getLocation()));
		return node.getType();
	}

	@Override
	public PType caseATupleExp(ATupleExp node, TypeCheckInfo question)
			throws AnalysisException {

		node.setTypes(new LinkedList<PType>());
		List<PType> types = node.getTypes();

		for (PExp arg : node.getArgs()) {
			question.qualifiers = null;
			types.add(arg.apply(rootVisitor, question));
		}

		node.setType(AstFactory.newAProductType(node.getLocation(), types));
		return node.getType(); // NB mk_() is a product
	}

	@Override
	public PType caseAUndefinedExp(AUndefinedExp node, TypeCheckInfo question) {
		node.setType(AstFactory.newAUndefinedType(node.getLocation()));
		return node.getType();
	}

	@Override
	public PType caseAVariableExp(AVariableExp node, TypeCheckInfo question) {

		Environment env = question.env;
		LexNameToken name = node.getName();

		if (env.isVDMPP()) {

			name.setTypeQualifier(question.qualifiers);
			node.setVardef(env.findName(name, question.scope));
			PDefinition vardef = node.getVardef();

			if (vardef != null) {
				if (vardef.getClassDefinition() != null) {
					if (!SClassDefinitionAssistantTC.isAccessible(env, vardef,
							true)) {
						TypeCheckerErrors.report(3180, "Inaccessible member "
								+ name + " of class "
								+ vardef.getClassDefinition().getName().name,
								node.getLocation(), node);
						node.setType(AstFactory.newAUnknownType(node
								.getLocation()));
						return node.getType();
					} else if (!PAccessSpecifierAssistantTC.isStatic(vardef
							.getAccess()) && env.isStatic()) {
						TypeCheckerErrors.report(3181, "Cannot access " + name
								+ " from a static context", node.getLocation(),
								node);
						node.setType(AstFactory.newAUnknownType(node
								.getLocation()));
						return node.getType();
					}
				}
			} else if (question.qualifiers != null) {
				// It may be an apply of a map or sequence, which would not
				// have the type qualifier of its arguments in the name. Or
				// it might be an apply of a function via a function variable
				// which would not be qualified.

				name.setTypeQualifier(null);
				vardef = env.findName(name, question.scope);

				if (vardef == null) {
					name.setTypeQualifier(question.qualifiers); // Just for
																// error text!
				} else {
					node.setVardef(vardef);
				}

			} else {
				// We may be looking for a bare function/op "x", when in fact
				// there is one with a qualified name "x(args)". So we check
				// the possible matches - if there is precisely one, we pick it,
				// else we raise an ambiguity error.

				for (PDefinition possible : env.findMatches(name)) {
					if (PDefinitionAssistantTC.isFunctionOrOperation(possible)) {
						if (vardef != null) {
							TypeCheckerErrors.report(3269,
									"Ambiguous function/operation name: "
											+ name.name, node.getLocation(),
									node);
							env.listAlternatives(name);
							break;
						}

						vardef = possible;
						node.setVardef(vardef);
						// Set the qualifier so that it will find it at runtime.

						PType pt = possible.getType();

						if (pt instanceof AFunctionType) {
							AFunctionType ft = (AFunctionType) pt;
							name.setTypeQualifier(ft.getParameters());
						} else {
							AOperationType ot = (AOperationType) pt;
							name.setTypeQualifier(ot.getParameters());
						}
					}
				}
			}
		} else {
			PDefinition temp = env.findName(name, question.scope);
			node.setVardef(temp == null ? null : temp);
		}

		if (node.getVardef() == null) {
			TypeCheckerErrors.report(3182, "Name '" + name
					+ "' is not in scope", node.getLocation(), node);
			env.listAlternatives(name);
			node.setType(AstFactory.newAUnknownType(node.getLocation()));
			return node.getType();
		} else {
			// Note that we perform an extra typeResolve here. This is
			// how forward referenced types are resolved, and is the reason
			// we don't need to retry at the top level (assuming all names
			// are in the environment).
			node.setType(PTypeAssistantTC.typeResolve(
					PDefinitionAssistantTC.getType(node.getVardef()), null,
					rootVisitor, question));
			return node.getType();
		}
	}

	/**
	 * BINARY Expressions
	 * 
	 * @throws AnalysisException
	 */
	@Override
	public PType caseALessEqualNumericBinaryExp(
			ALessEqualNumericBinaryExp node, TypeCheckInfo question)
			throws AnalysisException {

		SNumericBasicTypeAssistantTC.checkNumeric(node, rootVisitor, question);
		node.setType(AstFactory.newABooleanBasicType(node.getLocation()));
		return node.getType();
	}

	@Override
	public PType caseALessNumericBinaryExp(ALessNumericBinaryExp node,
			TypeCheckInfo question) throws AnalysisException {
		SNumericBasicTypeAssistantTC.checkNumeric(node, rootVisitor, question);
		node.setType(AstFactory.newABooleanBasicType(node.getLocation()));
		return node.getType();
	}

	/**
	 * UNARY Expressions
	 * 
	 * @throws AnalysisException
	 */
	@Override
	public PType caseAAbsoluteUnaryExp(AAbsoluteUnaryExp node,
			TypeCheckInfo question) throws AnalysisException {
		question.qualifiers = null;
		PType t = node.getExp().apply(rootVisitor, question);

		if (!PTypeAssistantTC.isNumeric(t)) {
			TypeCheckerErrors.report(3053, "Argument of 'abs' is not numeric",
					node.getLocation(), node);
		} else if (t instanceof AIntNumericBasicType) {
			t = AstFactory.newANatNumericBasicType(t.getLocation());
		}

		node.setType(t);
		return t;
	}

	@Override
	public PType caseACardinalityUnaryExp(ACardinalityUnaryExp node,
			TypeCheckInfo question) throws AnalysisException {

		PExp exp = node.getExp();
		question.qualifiers = null;
		if (!PTypeAssistantTC.isSet(exp.apply(rootVisitor, question))) {
			TypeCheckerErrors.report(3067, "Argument of 'card' is not a set",
					exp.getLocation(), exp);
		}

		node.setType(AstFactory.newANatNumericBasicType(node.getLocation()));
		return node.getType();
	}

	@Override
	public PType caseADistConcatUnaryExp(ADistConcatUnaryExp node,
			TypeCheckInfo question) throws AnalysisException {
		PExp exp = node.getExp();
		question.qualifiers = null;
		PType result = exp.apply(rootVisitor, question);

		if (PTypeAssistantTC.isSeq(result)) {
			PType inner = PTypeAssistantTC.getSeq(result).getSeqof();

			if (PTypeAssistantTC.isSeq(inner)) {
				node.setType(PTypeAssistantTC.getSeq(inner));
				return node.getType();
			}
		}

		TypeCheckerErrors.report(3075,
				"Argument of 'conc' is not a seq of seq", node.getLocation(),
				node);
		node.setType(AstFactory.newAUnknownType(node.getLocation()));
		return node.getType();
	}

	@Override
	public PType caseADistIntersectUnaryExp(ADistIntersectUnaryExp node,
			TypeCheckInfo question) throws AnalysisException {

		PExp exp = node.getExp();
		question.qualifiers = null;

		PType arg = exp.apply(rootVisitor, question);

		if (PTypeAssistantTC.isSet(arg)) {
			ASetType set = PTypeAssistantTC.getSet(arg);

			if (set.getEmpty() || PTypeAssistantTC.isSet(set.getSetof())) {
				node.setType(set.getSetof());
				return set.getSetof();
			}
		}

		TypeCheckerErrors.report(3076,
				"Argument of 'dinter' is not a set of sets",
				node.getLocation(), node);
		node.setType(AstFactory.newAUnknownType(node.getLocation()));
		return node.getType();
	}

	@Override
	public PType caseADistMergeUnaryExp(ADistMergeUnaryExp node,
			TypeCheckInfo question) throws AnalysisException {

		PExp exp = node.getExp();
		question.qualifiers = null;

		PType arg = exp.apply(rootVisitor, question);

		if (PTypeAssistantTC.isSet(arg)) {
			ASetType set = PTypeAssistantTC.getSet(arg);

			if (!set.getEmpty() && PTypeAssistantTC.isMap(set.getSetof())) {
				node.setType(set.getSetof());
				return set.getSetof();
			}
		}

		TypeCheckerErrors.report(3077, "Merge argument is not a set of maps",
				node.getLocation(), node);
		return AstFactory.newAMapMapType(node.getLocation()); // Unknown types
	}

	@Override
	public PType caseADistUnionUnaryExp(ADistUnionUnaryExp node,
			TypeCheckInfo question) throws AnalysisException {

		PExp exp = node.getExp();
		question.qualifiers = null;

		PType type = exp.apply(rootVisitor, question);

		if (PTypeAssistantTC.isSet(type)) {
			ASetType set = PTypeAssistantTC.getSet(type);

			if (PTypeAssistantTC.isSet(set.getSetof())) {
				node.setType(set.getSetof());
				return set.getSetof();
			}
		}

		TypeCheckerErrors.report(3078, "dunion argument is not a set of sets",
				node.getLocation(), node);
		node.setType(AstFactory.newASetType(node.getLocation(),
				AstFactory.newAUnknownType(node.getLocation())));
		return node.getType();
	}

	@Override
	public PType caseAFloorUnaryExp(AFloorUnaryExp node, TypeCheckInfo question)
			throws AnalysisException {

		PExp exp = node.getExp();
		question.qualifiers = null;

		if (!PTypeAssistantTC.isNumeric(exp.apply(rootVisitor, question))) {
			TypeCheckerErrors.report(3096, "Argument to floor is not numeric",
					node.getLocation(), node);
		}

		node.setType(AstFactory.newAIntNumericBasicType(node.getLocation()));
		return node.getType();
	}

	@Override
	public PType caseAHeadUnaryExp(AHeadUnaryExp node, TypeCheckInfo question)
			throws AnalysisException {
		PExp exp = node.getExp();
		question.qualifiers = null;

		PType etype = exp.apply(rootVisitor, question);

		if (!PTypeAssistantTC.isSeq(etype)) {
			TypeCheckerErrors.report(3104,
					"Argument to 'hd' is not a sequence", node.getLocation(),
					node);
			node.setType(AstFactory.newAUnknownType(node.getLocation()));
			return node.getType();
		}

		node.setType(PTypeAssistantTC.getSeq(etype).getSeqof());
		return node.getType();
	}

	@Override
	public PType caseAIndicesUnaryExp(AIndicesUnaryExp node,
			TypeCheckInfo question) throws AnalysisException {

		PExp exp = node.getExp();
		question.qualifiers = null;

		PType etype = exp.apply(rootVisitor, question);

		if (!PTypeAssistantTC.isSeq(etype)) {
			TypeCheckerErrors.report(3109,
					"Argument to 'inds' is not a sequence", node.getLocation(),
					node);
			TypeCheckerErrors.detail("Actual type", etype);
		}

		node.setType(AstFactory.newASetType(node.getLocation(),
				AstFactory.newANatOneNumericBasicType(node.getLocation())));
		return node.getType();
	}

	@Override
	public PType caseALenUnaryExp(ALenUnaryExp node, TypeCheckInfo question)
			throws AnalysisException {

		PExp exp = node.getExp();
		question.qualifiers = null;

		PType etype = exp.apply(rootVisitor, question);

		if (!PTypeAssistantTC.isSeq(etype)) {
			TypeCheckerErrors.report(3116,
					"Argument to 'len' is not a sequence", node.getLocation(),
					node);
		}

		node.setType(AstFactory.newANatNumericBasicType(node.getLocation()));
		return node.getType();
	}

	@Override
	public PType caseAMapDomainUnaryExp(AMapDomainUnaryExp node,
			TypeCheckInfo question) throws AnalysisException {

		PExp exp = node.getExp();
		question.qualifiers = null;

		PType etype = exp.apply(rootVisitor, question);

		if (!PTypeAssistantTC.isMap(etype)) {
			TypeCheckerErrors.report(3120, "Argument to 'dom' is not a map",
					node.getLocation(), node);
			node.setType(AstFactory.newAUnknownType(node.getLocation()));
			return node.getType();
		}

		SMapType mt = PTypeAssistantTC.getMap(etype);
		node.setType(AstFactory.newASetType(node.getLocation(), mt.getFrom()));
		return node.getType();
	}

	@Override
	public PType caseAMapInverseUnaryExp(AMapInverseUnaryExp node,
			TypeCheckInfo question) throws AnalysisException {

		PExp exp = node.getExp();
		question.qualifiers = null;

		PType etype = exp.apply(rootVisitor, question);

		if (!PTypeAssistantTC.isMap(etype)) {
			TypeCheckerErrors.report(3111,
					"Argument to 'inverse' is not a map", node.getLocation(),
					node);
			node.setType(AstFactory.newAUnknownType(node.getLocation()));
			return node.getType();
		}

		node.setMapType(PTypeAssistantTC.getMap(etype));
		AMapMapType mm = AstFactory.newAMapMapType(node.getLocation(), node
				.getMapType().getTo(), node.getMapType().getFrom());
		node.setType(mm);

		return node.getType();
	}

	@Override
	public PType caseAMapRangeUnaryExp(AMapRangeUnaryExp node,
			TypeCheckInfo question) throws AnalysisException {
		PExp exp = node.getExp();
		question.qualifiers = null;

		PType etype = exp.apply(rootVisitor, question);

		if (!PTypeAssistantTC.isMap(etype)) {
			TypeCheckerErrors.report(3122, "Argument to 'rng' is not a map",
					node.getLocation(), node);
			node.setType(AstFactory.newAUnknownType(node.getLocation()));
			return node.getType();
		}

		SMapType mt = PTypeAssistantTC.getMap(etype);
		node.setType(AstFactory.newASetType(node.getLocation(), mt.getTo()));
		return node.getType();
	}

	@Override
	public PType caseANotUnaryExp(ANotUnaryExp node, TypeCheckInfo question)
			throws AnalysisException {
		PExp exp = node.getExp();
		question.qualifiers = null;

		PType t = exp.apply(rootVisitor, question);

		if (!PTypeAssistantTC.isType(t, ABooleanBasicType.class)) {
			TypeCheckerErrors.report(3137, "Not expression is not a boolean",
					node.getLocation(), node);
		}

		node.setType(AstFactory.newABooleanBasicType(node.getLocation()));
		return node.getType();
	}

	@Override
	public PType caseAPowerSetUnaryExp(APowerSetUnaryExp node,
			TypeCheckInfo question) throws AnalysisException {
		PExp exp = node.getExp();
		question.qualifiers = null;

		PType etype = exp.apply(rootVisitor, question);

		if (!PTypeAssistantTC.isSet(etype)) {
			TypeCheckerErrors.report(3145, "Argument to 'power' is not a set",
					node.getLocation(), node);
			node.setType(AstFactory.newAUnknownType(node.getLocation()));
			return node.getType();
		}

		node.setType(AstFactory.newASetType(node.getLocation(), etype));
		return node.getType();
	}

	@Override
	public PType caseAReverseUnaryExp(AReverseUnaryExp node,
			TypeCheckInfo question) throws AnalysisException {

		PExp exp = node.getExp();
		question.qualifiers = null;

		PType etype = exp.apply(rootVisitor, question);

		if (!PTypeAssistantTC.isSeq(etype)) {
			TypeCheckerErrors.report(3295,
					"Argument to 'reverse' is not a sequence",
					node.getLocation(), node);
			ASeqSeqType tt = AstFactory.newASeqSeqType(node.getLocation(),
					AstFactory.newAUnknownType(node.getLocation()));
			node.setType(tt);
			return node.getType();
		}

		node.setType(etype);
		return etype;
	}

	@Override
	public PType caseATailUnaryExp(ATailUnaryExp node, TypeCheckInfo question)
			throws AnalysisException {
		PExp exp = node.getExp();
		question.qualifiers = null;

		PType etype = exp.apply(rootVisitor, question);

		if (!PTypeAssistantTC.isSeq(etype)) {
			TypeCheckerErrors.report(3179,
					"Argument to 'tl' is not a sequence", node.getLocation(),
					node);
			node.setType(AstFactory.newASeqSeqType(node.getLocation(),
					AstFactory.newAUnknownType(node.getLocation())));
			return node.getType();
		}
		node.setType(etype);
		return etype;
	}

	@Override
	public PType caseAUnaryMinusUnaryExp(AUnaryMinusUnaryExp node,
			TypeCheckInfo question) throws AnalysisException {
		question.qualifiers = null;
		PType t = node.getExp().apply(rootVisitor, question);

		if (t instanceof ANatNumericBasicType
				|| t instanceof ANatOneNumericBasicType) {
			t = AstFactory.newAIntNumericBasicType(node.getLocation());
		}

		node.setType(t);
		return t;
	}

	@Override
	public PType caseAUnaryPlusUnaryExp(AUnaryPlusUnaryExp node,
			TypeCheckInfo question) throws AnalysisException {
		question.qualifiers = null;
		node.setType(node.getExp().apply(rootVisitor, question));
		return node.getType();
	}

	@Override
	public PType caseAElementsUnaryExp(AElementsUnaryExp node,
			TypeCheckInfo question) throws AnalysisException {

		PExp etype = node.getExp();
		question.qualifiers = null;

		PType arg = etype.apply(rootVisitor, question);

		if (!PTypeAssistantTC.isSeq(arg)) {
			TypeCheckerErrors.report(3085,
					"Argument of 'elems' is not a sequence",
					node.getLocation(), node);
			node.setType(AstFactory.newASetType(node.getLocation(),
					AstFactory.newAUnknownType(node.getLocation())));
			return node.getType();
		}

		SSeqType seq = PTypeAssistantTC.getSeq(arg);
		node.setType(seq.getEmpty() ? AstFactory.newASetType(node.getLocation())
				: AstFactory.newASetType(node.getLocation(), seq.getSeqof()));
		return node.getType();
	}
}
