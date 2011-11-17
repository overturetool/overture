package org.overture.pog.visitors;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Queue;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.*;
import org.overture.ast.expressions.assistants.PExpAssistant;
import org.overture.ast.patterns.AIgnorePattern;
import org.overture.ast.patterns.ATypeBind;
import org.overture.ast.patterns.ATypeMultipleBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.ARealNumericBasicType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SSeqType;
import org.overture.ast.types.assistants.PTypeAssistant;
import org.overture.pog.obligations.CasesExhaustiveObligation;
import org.overture.pog.obligations.FiniteMapObligation;
import org.overture.pog.obligations.FuncComposeObligation;
import org.overture.pog.obligations.FunctionApplyObligation;
import org.overture.pog.obligations.LetBeExistsObligation;
import org.overture.pog.obligations.MapApplyObligation;
import org.overture.pog.obligations.MapCompatibleObligation;
import org.overture.pog.obligations.MapComposeObligation;
import org.overture.pog.obligations.MapIterationObligation;
import org.overture.pog.obligations.MapSeqOfCompatibleObligation;
import org.overture.pog.obligations.MapSetOfCompatibleObligation;
import org.overture.pog.obligations.NonEmptySeqObligation;
import org.overture.pog.obligations.NonZeroObligation;
import org.overture.pog.obligations.POContextStack;
import org.overture.pog.obligations.PODefContext;
import org.overture.pog.obligations.POForAllContext;
import org.overture.pog.obligations.POForAllPredicateContext;
import org.overture.pog.obligations.POImpliesContext;
import org.overture.pog.obligations.POLetDefContext;
import org.overture.pog.obligations.PONotImpliesContext;
import org.overture.pog.obligations.ProofObligation;
import org.overture.pog.obligations.ProofObligationList;
import org.overture.pog.obligations.RecursiveObligation;
import org.overture.pog.obligations.SeqApplyObligation;
import org.overture.pog.obligations.SubTypeObligation;
import org.overture.pog.obligations.TupleSelectObligation;
import org.overture.typecheck.TypeComparator;
import org.overturetool.vdmj.lex.LexIdentifierToken;
import org.overturetool.vdmj.lex.LexNameToken;

public class PogExpVisitor extends
		QuestionAnswerAdaptor<POContextStack, ProofObligationList>
{

	final private QuestionAnswerAdaptor<POContextStack, ProofObligationList> rootVisitor;

	public PogExpVisitor(PogVisitor pogVisitor)
	{
		this.rootVisitor = pogVisitor;

	}

	@Override
	// RWL see [1] pg. 57: 6.12 Apply Expressions
	public ProofObligationList caseAApplyExp(AApplyExp node,
			POContextStack question)
	{

		ProofObligationList obligations = new ProofObligationList();

		PExp root = node.getRoot();

		// is it a map?

		PType type = root.getType();
		if (PTypeAssistant.isMap(type))
		{
			SMapType mapType = PTypeAssistant.getMap(type);
			obligations.add(new MapApplyObligation(node.getRoot(), node.getArgs().get(0), question));
			PType aType = question.checkType(node.getArgs().get(0), node.getArgtypes().get(0));

			if (!TypeComparator.isSubType(aType, mapType.getFrom()))
			{
				obligations.add(new SubTypeObligation(node.getArgs().get(0), mapType.getFrom(), aType, question));
			}
		}

		if (!PTypeAssistant.isUnknown(type) && PTypeAssistant.isFunction(type))
		{
			AFunctionType funcType = PTypeAssistant.getFunction(type);
			String prename = PExpAssistant.getPreName(root);
			if (prename == null || !prename.equals(""))
			{
				obligations.add(new FunctionApplyObligation(node.getRoot(), node.getArgs(), prename, question));
			}

			int i = 0;
			List<PType> argTypes = node.getArgtypes();
			List<PExp> argList = node.getArgs();
			for (PType argType : argTypes)
			{
				argType = question.checkType(argList.get(i), argType);
				PType pt = funcType.getParameters().get(i);

				if (!TypeComparator.isSubType(argType, pt))
					obligations.add(new SubTypeObligation(argList.get(i), pt, argType, question));
				i++;
			}

			PDefinition recursive = node.getRecursive();
			if (recursive != null)
			{
				if (recursive instanceof AExplicitFunctionDefinition)
				{
					AExplicitFunctionDefinition def = (AExplicitFunctionDefinition) recursive;
					if (def.getMeasure() != null)
					{
						obligations.add(new RecursiveObligation(def, node, question));
					}
				} else if (recursive instanceof AImplicitFunctionDefinition)
				{
					AImplicitFunctionDefinition def = (AImplicitFunctionDefinition) recursive;
					if (def.getMeasure() != null)
					{
						obligations.add(new RecursiveObligation(def, node, question));
					}

				}
			}
		}

		if (PTypeAssistant.isSeq(type))
		{
			obligations.add(new SeqApplyObligation(node.getRoot(), node.getArgs().get(0), question));
		}

		obligations.addAll(node.getRoot().apply(this, question));

		for (PExp arg : node.getArgs())
		{
			obligations.addAll(arg.apply(this, question));
		}

		return obligations;
	}

	@Override
	// see [1] pg. 179 unary expressions
	public ProofObligationList caseAHeadUnaryExp(AHeadUnaryExp node,
			POContextStack question)
	{

		ProofObligationList obligations = new ProofObligationList();
		PExp exp = node.getExp();

		// TODO RWL This is a hack. The new ast LexNameToken's toString method
		// includes the module e.g. like Test`b for variables
		// which the old one did not. Hence proof obligations with variable
		// names are different as "Test`b" is just b with the old proof
		// obligations generator.
		PExp fake = exp.clone();
		if (exp instanceof AVariableExp)
		{
			AVariableExp var = (AVariableExp) fake;
			var.setName(new LexNameToken("", var.getName().getIdentifier()));
		}

		ProofObligation po = new NonEmptySeqObligation(fake, question);
		obligations.add(po);

		return obligations;
	}

	@Override
	// [1] pg. 46
	public ProofObligationList caseACasesExp(ACasesExp node,
			POContextStack question)
	{

		ProofObligationList obligations = new ProofObligationList();

		int count = 0;
		boolean hasIgnore = false;

		// handle each case
		for (ACaseAlternative alt : node.getCases())
		{

			if (alt.getPattern() instanceof AIgnorePattern)
				hasIgnore = true;

			obligations.addAll(alt.apply(rootVisitor, question));
			count++;
		}

		if (node.getOthers() != null)
		{
			obligations.addAll(node.getOthers().apply(this, question));
		}

		for (int i = 0; i < count; i++)
			question.pop();

		if (node.getOthers() == null && !hasIgnore)
			obligations.add(new CasesExhaustiveObligation(node, question));

		return obligations;
	}

	@Override
	public ProofObligationList caseAMapCompMapExp(AMapCompMapExp node,
			POContextStack question)
	{
		ProofObligationList obligations = new ProofObligationList();

		obligations.add(new MapSetOfCompatibleObligation(node, question));

		question.push(new POForAllPredicateContext(node));
		obligations.addAll(node.getFirst().apply(this, question));
		question.pop();

		boolean finiteTest = false;

		for (PMultipleBind mb : node.getBindings())
		{
			obligations.addAll(mb.apply(rootVisitor, question));
			if (mb instanceof PMultipleBind)
				finiteTest = true;
		}

		if (finiteTest)
			obligations.add(new FiniteMapObligation(node, node.getType(), question));

		PExp predicate = node.getPredicate();
		if (predicate != null)
		{
			question.push(new POForAllContext(node));
		}

		return obligations;
	}

	@Override
	// RWL see [1] pg. 179 A.5.4 Unary Expressions
	public ProofObligationList caseSUnaryExp(SUnaryExp node,
			POContextStack question)
	{

		return node.getExp().apply(this, question);
	}

	@Override
	// RWL
	public ProofObligationList defaultSUnaryExp(SUnaryExp node,
			POContextStack question)
	{
		return node.getExp().apply(this, question);
	}

	@Override
	// RWL
	public ProofObligationList caseSBinaryExp(SBinaryExp node,
			POContextStack question)
	{
		ProofObligationList obligations = new ProofObligationList();
		obligations.addAll(node.getLeft().apply(this, question));
		obligations.addAll(node.getRight().apply(this, question));
		return obligations;
	}

	@Override
	public ProofObligationList defaultSBinaryExp(SBinaryExp node,
			POContextStack question)
	{

		ProofObligationList obligations = new ProofObligationList();
		obligations.addAll(node.getLeft().apply(this, question));
		obligations.addAll(node.getRight().apply(this, question));
		return obligations;
	}

	@Override
	public ProofObligationList caseABooleanConstExp(ABooleanConstExp node,
			POContextStack question)
	{

		return new ProofObligationList();
	}

	@Override
	public ProofObligationList caseACharLiteralExp(ACharLiteralExp node,
			POContextStack question)
	{
		return new ProofObligationList();
	}

	@Override
	public ProofObligationList caseAElseIfExp(AElseIfExp node,
			POContextStack question)
	{

		ProofObligationList obligations = new ProofObligationList();
		question.push(new POImpliesContext(node.getElseIf()));
		node.getThen().apply(this, question);
		question.pop();

		return obligations;
	}

	@Override
	public ProofObligationList caseAExists1Exp(AExists1Exp node,
			POContextStack question)
	{
		ProofObligationList obligations = new ProofObligationList();
		question.push(new POForAllContext(node));
		obligations.addAll(node.getPredicate().apply(this, question));
		question.pop();
		return obligations;
	}

	@Override
	public ProofObligationList caseAExistsExp(AExistsExp node,
			POContextStack question)
	{
		ProofObligationList obligations = new ProofObligationList();

		for (PMultipleBind mb : node.getBindList())
		{
			obligations.addAll(mb.apply(rootVisitor, question));
		}

		question.push(new POForAllContext(node));
		obligations.addAll(node.getPredicate().apply(this, question));
		question.pop();

		return obligations;
	}

	@Override
	public ProofObligationList caseAFieldExp(AFieldExp node,
			POContextStack question)
	{
		return node.getObject().apply(this, question);
	}

	@Override
	public ProofObligationList caseAFieldNumberExp(AFieldNumberExp node,
			POContextStack question)
	{

		ProofObligationList obligations = node.getTuple().apply(this, question);

		PType type = node.getType();

		if (type instanceof AUnionType)
		{
			AUnionType utype = (AUnionType) type;
			for (PType t : utype.getTypes())
			{
				if (t instanceof AProductType)
				{
					AProductType aprodType = (AProductType) t;
					if (aprodType.getTypes().size() < node.getField().value)
					{
						obligations.add(new TupleSelectObligation(node, aprodType, question));
					}
				}
			}
		}

		return obligations;
	}

	@Override
	public ProofObligationList caseAForAllExp(AForAllExp node,
			POContextStack question)
	{

		ProofObligationList obligations = new ProofObligationList();

		for (PMultipleBind mb : node.getBindList())
		{
			obligations.addAll(mb.apply(rootVisitor, question));
		}

		question.push(new POForAllContext(node));
		obligations.addAll(node.getPredicate().apply(this, question));
		question.pop();
		return obligations;
	}

	@Override
	public ProofObligationList caseAFuncInstatiationExp(
			AFuncInstatiationExp node, POContextStack question)
	{
		// TODO RWL Hmm, what to do here?
		throw new RuntimeException("I did'nt know what to do there.");
		// return super.caseAFuncInstatiationExp(node, question);
	}

	@Override
	// RWL
	public ProofObligationList caseAHistoryExp(AHistoryExp node,
			POContextStack question)
	{
		// No getProofObligationMethod found on the HistoryExpression class of
		// VDMJ assuming we have the empty list.
		return new ProofObligationList();
	}

	@Override
	public ProofObligationList caseAIfExp(AIfExp node, POContextStack question)
	{

		ProofObligationList obligations = new ProofObligationList();

		question.push(new POImpliesContext(node.getTest()));
		obligations.addAll(node.getThen().apply(this, question));
		question.pop();

		question.push(new PONotImpliesContext(node.getTest()));

		for (AElseIfExp e : node.getElseList())
		{
			obligations.addAll(e.apply(this, question));
			question.push(new PONotImpliesContext(e.getElseIf()));

		}

		int sizeBefore = question.size();
		obligations.addAll(node.getElse().apply(this, question));
		assert sizeBefore <= question.size();

		for (int i = 0; i < node.getElseList().size(); i++)
			question.pop();

		question.pop();
		return obligations;
	}

	@Override
	public ProofObligationList caseAIntLiteralExp(AIntLiteralExp node,
			POContextStack question)
	{

		return new ProofObligationList();
	}

	@Override
	public ProofObligationList caseAIotaExp(AIotaExp node,
			POContextStack question)
	{
		return new ProofObligationList();
	}

	@Override
	public ProofObligationList caseAIsExp(AIsExp node, POContextStack question)
	{
		PDefinition typeDef = node.getTypedef();
		PType basicType = node.getBasicType();
		if (typeDef != null)
		{
			question.noteType(node.getTest(), typeDef.getType());
		} else if (basicType != null)
		{
			question.noteType(node.getTest(), basicType);
		}
		return node.getTest().apply(this, question);
	}

	@Override
	// RWL See [1] pg. 64-65
	public ProofObligationList caseAIsOfBaseClassExp(AIsOfBaseClassExp node,
			POContextStack question)
	{
		return node.getExp().apply(this, question);
	}

	@Override
	// RWL See [1] pg. 64-65
	public ProofObligationList caseAIsOfClassExp(AIsOfClassExp node,
			POContextStack question)
	{

		question.noteType(node.getExp(), node.getClassType());

		return node.getExp().apply(this, question);
	}

	@Override
	// RWL See [1] pg. 62
	public ProofObligationList caseALambdaExp(ALambdaExp node,
			POContextStack question)
	{

		ProofObligationList obligations = new ProofObligationList();

		for (ATypeBind tb : node.getBindList())
		{
			obligations.addAll(tb.apply(rootVisitor, question));
		}

		question.push(new POForAllContext(node));
		obligations.addAll(node.getExpression().apply(this, question));
		question.pop();

		return obligations;
	}

	@Override
	// RWL See [1] pg.95
	public ProofObligationList caseALetBeStExp(ALetBeStExp node,
			POContextStack question)
	{
		ProofObligationList obligations = new ProofObligationList();
		obligations.add(new LetBeExistsObligation(node, question));
		obligations.addAll(node.getBind().apply(rootVisitor, question));

		PExp suchThat = node.getSuchThat();
		if (suchThat != null)
		{
			question.push(new POForAllContext(node));
			obligations.addAll(suchThat.apply(this, question));
			question.pop();
		}

		question.push(new POForAllPredicateContext(node));
		obligations.addAll(node.getValue().apply(this, question));
		question.pop();

		return obligations;
	}

	@Override
	// RWL see [1] pg.
	public ProofObligationList caseALetDefExp(ALetDefExp node,
			POContextStack question)
	{

		ProofObligationList obligations = new ProofObligationList();

		for (PDefinition def : node.getLocalDefs())
			obligations.addAll(def.apply(rootVisitor, question));

		question.push(new POLetDefContext(node));
		obligations.addAll(node.getExpression().apply(this, question));
		question.pop();

		return obligations;
	}

	@Override
	public ProofObligationList caseADefExp(ADefExp node, POContextStack question)
	{

		ProofObligationList obligations = new ProofObligationList();
		List<PDefinition> localDefs = node.getLocalDefs();
		for (PDefinition def : localDefs)
		{
			obligations.addAll(def.apply(rootVisitor, question));
		}

		// RWL Question, are we going
		question.push(new PODefContext(node));
		obligations.addAll(node.getExpression().apply(this, question));
		question.pop();

		return obligations;
	}

	@Override
	public ProofObligationList caseSMapExp(SMapExp node, POContextStack question)
	{

		return new ProofObligationList();
	}

	@Override
	public ProofObligationList defaultSMapExp(SMapExp node,
			POContextStack question)
	{

		return new ProofObligationList();
	}

	@Override
	public ProofObligationList caseAMapletExp(AMapletExp node,
			POContextStack question)
	{
		return super.caseAMapletExp(node, question);
	}

	@Override
	public ProofObligationList caseAMkBasicExp(AMkBasicExp node,
			POContextStack question)
	{
		return node.getArg().apply(this, question);
	}

	@Override
	public ProofObligationList caseAMkTypeExp(AMkTypeExp node,
			POContextStack question)
	{

		ProofObligationList obligations = new ProofObligationList();
		Queue<PExp> args = node.getArgs();
		for (PExp arg : args)
			obligations.addAll(arg.apply(this, question));

		Queue<PType> argTypes = node.getArgTypes();

		ARecordInvariantType recordType = node.getRecordType();
		for (AFieldField f : recordType.getFields())
		{
			PType aType = argTypes.poll();
			PExp aExp = args.poll();

			if (!TypeComparator.isSubType(question.checkType(aExp, aType), f.getType()))
				obligations.add(new SubTypeObligation(aExp, f.getType(), aType, question));
		}

		PDefinition invDef = recordType.getInvDef();
		if (invDef != null)
			obligations.add(new SubTypeObligation(node, recordType, recordType, question));

		return obligations;
	}

	private static AFieldField findField(ARecordInvariantType ty,
			LexIdentifierToken id)
	{

		List<AFieldField> fields = ty.getFields();
		for (AFieldField f : fields)
			if (f.getTag().equals(id.name))
				return f;

		return null;
	}

	@Override
	// RWL See [1] pg. 56
	public ProofObligationList caseAMuExp(AMuExp node, POContextStack question)
	{

		ProofObligationList obligations = new ProofObligationList();

		Queue<ARecordModifier> modifiers = node.getModifiers();
		ARecordInvariantType recordType = node.getRecordType();
		Queue<PType> mTypes = node.getModTypes();

		for (ARecordModifier mod : modifiers)
		{
			obligations.addAll(mod.getValue().apply(this, question));
			AFieldField f = findField(recordType, mod.getTag());
			PType mType = mTypes.poll();
			if (f != null)
				if (!TypeComparator.isSubType(mType, f.getType()))
					obligations.add(new SubTypeObligation(mod.getValue(), f.getType(), mType, question));

		}

		return super.caseAMuExp(node, question);
	}

	@Override
	public ProofObligationList caseANewExp(ANewExp node, POContextStack question)
	{

		ProofObligationList obligations = new ProofObligationList();

		for (PExp exp : node.getArgs())
			obligations.addAll(exp.apply(this, question));

		return obligations;
	}

	@Override
	public ProofObligationList caseANilExp(ANilExp node, POContextStack question)
	{
		return new ProofObligationList();
	}

	@Override
	public ProofObligationList caseANotYetSpecifiedExp(
			ANotYetSpecifiedExp node, POContextStack question)
	{
		return new ProofObligationList();
	}

	@Override
	public ProofObligationList caseAPostOpExp(APostOpExp node,
			POContextStack question)
	{
		return new ProofObligationList();
	}

	@Override
	public ProofObligationList caseAPreExp(APreExp node, POContextStack question)
	{
		return new ProofObligationList();
	}

	@Override
	public ProofObligationList caseAPreOpExp(APreOpExp node,
			POContextStack question)
	{
		return new ProofObligationList();
	}

	@Override
	public ProofObligationList caseAQuoteLiteralExp(AQuoteLiteralExp node,
			POContextStack question)
	{
		return new ProofObligationList();
	}

	@Override
	public ProofObligationList caseARealLiteralExp(ARealLiteralExp node,
			POContextStack question)
	{
		return new ProofObligationList();
	}

	@Override
	public ProofObligationList caseASameBaseClassExp(ASameBaseClassExp node,
			POContextStack question)
	{
		ProofObligationList obligations = new ProofObligationList();

		obligations.addAll(node.getLeft().apply(this, question));
		obligations.addAll(node.getRight().apply(this, question));

		return obligations;
	}

	@Override
	public ProofObligationList caseASameClassExp(ASameClassExp node,
			POContextStack question)
	{
		ProofObligationList list = node.getLeft().apply(this, question);
		list.addAll(node.getRight().apply(this, question));
		return list;
	}

	@Override
	public ProofObligationList caseASelfExp(ASelfExp node,
			POContextStack question)
	{
		return new ProofObligationList();
	}

	@Override
	public ProofObligationList caseSSeqExp(SSeqExp node, POContextStack question)
	{

		return new ProofObligationList();
	}

	@Override
	public ProofObligationList defaultSSeqExp(SSeqExp node,
			POContextStack question)
	{
		return new ProofObligationList();
	}

	@Override
	public ProofObligationList caseSSetExp(SSetExp node, POContextStack question)
	{

		return new ProofObligationList();
	}

	@Override
	public ProofObligationList defaultSSetExp(SSetExp node,
			POContextStack question)
	{
		return new ProofObligationList();
	}

	@Override
	public ProofObligationList caseAStateInitExp(AStateInitExp node,
			POContextStack question)
	{
		return new ProofObligationList();
	}

	@Override
	public ProofObligationList caseAStringLiteralExp(AStringLiteralExp node,
			POContextStack question)
	{
		return new ProofObligationList();
	}

	@Override
	public ProofObligationList caseASubclassResponsibilityExp(
			ASubclassResponsibilityExp node, POContextStack question)
	{
		return new ProofObligationList();
	}

	@Override
	public ProofObligationList caseASubseqExp(ASubseqExp node,
			POContextStack question)
	{
		ProofObligationList list = node.getSeq().apply(this, question);
		list.addAll(node.getFrom().apply(this, question));
		list.addAll(node.getTo().apply(this, question));
		return list;
	}

	@Override
	public ProofObligationList caseAThreadIdExp(AThreadIdExp node,
			POContextStack question)
	{
		return new ProofObligationList();
	}

	@Override
	public ProofObligationList caseATimeExp(ATimeExp node,
			POContextStack question)
	{
		return new ProofObligationList();
	}

	@Override
	public ProofObligationList caseATupleExp(ATupleExp node,
			POContextStack question)
	{
		ProofObligationList obligations = new ProofObligationList();
		for (PExp exp : node.getArgs())
			obligations.addAll(exp.apply(this, question));
		return obligations;
	}

	@Override
	public ProofObligationList caseAUndefinedExp(AUndefinedExp node,
			POContextStack question)
	{
		return new ProofObligationList();
	}

	@Override
	public ProofObligationList caseAAbsoluteUnaryExp(AAbsoluteUnaryExp node,
			POContextStack question)
	{

		return node.getExp().apply(this, question);
	}

	@Override
	public ProofObligationList caseACardinalityUnaryExp(
			ACardinalityUnaryExp node, POContextStack question)
	{

		return node.getExp().apply(this, question);
	}

	@Override
	public ProofObligationList caseADistConcatUnaryExp(
			ADistConcatUnaryExp node, POContextStack question)
	{
		return node.getExp().apply(this, question);
	}

	@Override
	public ProofObligationList caseADistIntersectUnaryExp(
			ADistIntersectUnaryExp node, POContextStack question)
	{
		ProofObligationList obligations = node.getExp().apply(this, question);
		obligations.add(new org.overture.pog.obligations.NonEmptySetObligation(node.getExp(), question));
		return obligations;
	}

	@Override
	public ProofObligationList caseADistMergeUnaryExp(ADistMergeUnaryExp node,
			POContextStack question)
	{
		ProofObligationList obligations = new ProofObligationList();
		obligations.add(new MapSetOfCompatibleObligation(node.getExp(), question));
		return obligations;
	}

	@Override
	public ProofObligationList caseADistUnionUnaryExp(ADistUnionUnaryExp node,
			POContextStack question)
	{
		return node.getExp().apply(this, question);
	}

	@Override
	public ProofObligationList caseAElementsUnaryExp(AElementsUnaryExp node,
			POContextStack question)
	{

		return node.getExp().apply(this, question);
	}

	@Override
	public ProofObligationList caseAFloorUnaryExp(AFloorUnaryExp node,
			POContextStack question)
	{

		return node.getExp().apply(this, question);
	}

	@Override
	public ProofObligationList caseAIndicesUnaryExp(AIndicesUnaryExp node,
			POContextStack question)
	{
		return node.getExp().apply(this, question);
	}

	@Override
	public ProofObligationList caseALenUnaryExp(ALenUnaryExp node,
			POContextStack question)
	{

		return node.getExp().apply(this, question);
	}

	@Override
	public ProofObligationList caseAMapDomainUnaryExp(AMapDomainUnaryExp node,
			POContextStack question)
	{
		return node.getExp().apply(this, question);
	}

	@Override
	public ProofObligationList caseAMapInverseUnaryExp(
			AMapInverseUnaryExp node, POContextStack question)
	{
		return node.getExp().apply(this, question);
	}

	@Override
	public ProofObligationList caseAMapRangeUnaryExp(AMapRangeUnaryExp node,
			POContextStack question)
	{
		return node.getExp().apply(this, question);
	}

	@Override
	public ProofObligationList caseANotUnaryExp(ANotUnaryExp node,
			POContextStack question)
	{
		return node.getExp().apply(this, question);
	}

	@Override
	public ProofObligationList caseAPowerSetUnaryExp(APowerSetUnaryExp node,
			POContextStack question)
	{
		return node.getExp().apply(this, question);
	}

	@Override
	public ProofObligationList caseAReverseUnaryExp(AReverseUnaryExp node,
			POContextStack question)
	{
		return node.getExp().apply(this, question);
	}

	@Override
	public ProofObligationList caseATailUnaryExp(ATailUnaryExp node,
			POContextStack question)
	{
		return node.getExp().apply(this, question);
	}

	@Override
	public ProofObligationList caseAUnaryMinusUnaryExp(
			AUnaryMinusUnaryExp node, POContextStack question)
	{

		return node.getExp().apply(this, question);
	}

	@Override
	public ProofObligationList caseAUnaryPlusUnaryExp(AUnaryPlusUnaryExp node,
			POContextStack question)
	{
		return node.getExp().apply(this, question);
	}

	@Override
	public ProofObligationList caseSBooleanBinaryExp(SBooleanBinaryExp node,
			POContextStack question)
	{
		ProofObligationList obligations = new ProofObligationList();
		PExp lExp = node.getLeft();
		PExp rExp = node.getRight();

		PType lType = lExp.getType();
		PType rType = rExp.getType();
		if (lType instanceof AUnionType)
		{
			obligations.add(new SubTypeObligation(lExp, new ABooleanBasicType(lExp.getLocation(), false), lType, question));
		}

		if (rType instanceof AUnionType)
		{

			obligations.add(new SubTypeObligation(rExp, new ABooleanBasicType(rExp.getLocation(), false), rType, question));
		}
		return super.caseSBooleanBinaryExp(node, question);
	}

	@Override
	public ProofObligationList defaultSBooleanBinaryExp(SBooleanBinaryExp node,
			POContextStack question)
	{
		return caseSBooleanBinaryExp(node, question);
	}

	@Override
	public ProofObligationList caseACompBinaryExp(ACompBinaryExp node,
			POContextStack question)
	{

		ProofObligationList obligations = new ProofObligationList();
		PExp lExp = node.getLeft();
		PType lType = lExp.getType();
		PExp rExp = node.getRight();

		if (PTypeAssistant.isFunction(lType))
		{
			String pref1 = PExpAssistant.getPreName(lExp);
			String pref2 = PExpAssistant.getPreName(rExp);

			if (pref1 == null || !pref1.equals(""))
				obligations.add(new FuncComposeObligation(node, pref1, pref2, question));
		}

		if (PTypeAssistant.isMap(lType))
		{
			obligations.add(new MapComposeObligation(node, question));
		}

		return obligations;
	}

	final static int LEFT = 0;
	final static int RIGHT = 1;

	private <T> PExp[] getLeftRight(T node)
	{
		PExp[] res = new PExp[2];
		try
		{
			Class<?> clz = node.getClass();
			Method getLeft = clz.getMethod("getLeft", new Class<?>[] {});
			Method getRight = clz.getMethod("getRight", new Class<?>[] {});
			res[LEFT] = (PExp) getLeft.invoke(node, new Object[0]);
			res[RIGHT] = (PExp) getRight.invoke(node, new Object[0]);

		} catch (Exception k)
		{
			throw new RuntimeException(k);
		}
		return res;
	}

	private <T> ProofObligationList handleBinaryExpression(T node,
			POContextStack question)
	{

		if (node == null)
			return new ProofObligationList();

		PExp[] leftRight = getLeftRight(node);
		PExp left = leftRight[LEFT];
		PExp right = leftRight[RIGHT];

		ProofObligationList obligations = new ProofObligationList();
		obligations.addAll(left.apply(this, question));
		obligations.addAll(right.apply(this, question));
		return obligations;
	}

	@Override
	public ProofObligationList caseADomainResByBinaryExp(
			ADomainResByBinaryExp node, POContextStack question)
	{
		return handleBinaryExpression(node, question);
	}

	@Override
	public ProofObligationList caseADomainResToBinaryExp(
			ADomainResToBinaryExp node, POContextStack question)
	{
		return handleBinaryExpression(node, question);
	}

	@Override
	public ProofObligationList caseAInSetBinaryExp(AInSetBinaryExp node,
			POContextStack question)
	{
		return handleBinaryExpression(node, question);
	}

	@Override
	public ProofObligationList caseAMapUnionBinaryExp(AMapUnionBinaryExp node,
			POContextStack question)
	{
		ProofObligationList obligations = handleBinaryExpression(node, question);
		obligations.add(new MapCompatibleObligation(node.getLeft(), node.getRight(), question));
		return obligations;
	}

	@Override
	public ProofObligationList caseANotEqualBinaryExp(ANotEqualBinaryExp node,
			POContextStack question)
	{
		return handleBinaryExpression(node, question);
	}

	@Override
	public ProofObligationList caseANotInSetBinaryExp(ANotInSetBinaryExp node,
			POContextStack question)
	{
		return handleBinaryExpression(node, question);
	}

	@Override
	public ProofObligationList caseSNumericBinaryExp(SNumericBinaryExp node,
			POContextStack question)
	{

		ProofObligationList obligations = new ProofObligationList();

		PExp left = node.getLeft();
		PExp right = node.getRight();
		PType lType = left.getType();
		PType rType = right.getType();

		if (lType instanceof AUnionType)
		{

			obligations.add(new SubTypeObligation(left, new ARealNumericBasicType(right.getLocation(), false), lType, question));
		}

		if (rType instanceof AUnionType)
		{
			obligations.add(new SubTypeObligation(right, new ARealNumericBasicType(right.getLocation(), false), rType, question));
		}

		obligations.addAll(left.apply(this, question));
		obligations.addAll(right.apply(this, question));
		return obligations;
	}

	@Override
	public ProofObligationList defaultSNumericBinaryExp(SNumericBinaryExp node,
			POContextStack question)
	{
		return caseSNumericBinaryExp(node, question);
	}

	@Override
	public ProofObligationList caseAPlusPlusBinaryExp(APlusPlusBinaryExp node,
			POContextStack question)
	{

		ProofObligationList obligations = handleBinaryExpression(node, question);
		PType lType = node.getLeft().getType();

		if (lType instanceof SSeqType)
		{
			obligations.add(new org.overture.pog.obligations.SeqModificationObligation(node, question));
		}

		return obligations;
	}

	@Override
	public ProofObligationList caseAProperSubsetBinaryExp(
			AProperSubsetBinaryExp node, POContextStack question)
	{
		return handleBinaryExpression(node, question);
	}

	@Override
	public ProofObligationList caseARangeResByBinaryExp(
			ARangeResByBinaryExp node, POContextStack question)
	{

		return handleBinaryExpression(node, question);
	}

	@Override
	public ProofObligationList caseARangeResToBinaryExp(
			ARangeResToBinaryExp node, POContextStack question)
	{
		return super.caseARangeResToBinaryExp(node, question);
	}

	@Override
	public ProofObligationList caseASeqConcatBinaryExp(
			ASeqConcatBinaryExp node, POContextStack question)
	{
		return handleBinaryExpression(node, question);
	}

	@Override
	public ProofObligationList caseASetDifferenceBinaryExp(
			ASetDifferenceBinaryExp node, POContextStack question)
	{
		return handleBinaryExpression(node, question);
	}

	@Override
	public ProofObligationList caseASetIntersectBinaryExp(
			ASetIntersectBinaryExp node, POContextStack question)
	{
		return handleBinaryExpression(node, question);
	}

	@Override
	public ProofObligationList caseASetUnionBinaryExp(ASetUnionBinaryExp node,
			POContextStack question)
	{
		return handleBinaryExpression(node, question);
	}

	@Override
	public ProofObligationList caseAStarStarBinaryExp(AStarStarBinaryExp node,
			POContextStack question)
	{
		ProofObligationList obligations = new ProofObligationList();

		PExp lExp = node.getLeft();
		PType lType = lExp.getType();

		if (lType instanceof AFunctionType)
		{

			throw new RuntimeException("How do I get the left.preName (see StarStarExpression in vdmj) ?");
			// String preName = lExp.getPre
		}

		if (lType instanceof SMapType)
		{
			obligations.add(new MapIterationObligation(node, question));
		}

		return obligations;
	}

	@Override
	public ProofObligationList caseASubsetBinaryExp(ASubsetBinaryExp node,
			POContextStack question)
	{
		return handleBinaryExpression(node, question);
	}

	@Override
	public ProofObligationList caseAAndBooleanBinaryExp(
			AAndBooleanBinaryExp node, POContextStack question)
	{

		ProofObligationList obligations = new ProofObligationList();

		PExp lExp = node.getLeft();
		PType lType = lExp.getType();
		PExp rExp = node.getRight();
		PType rType = rExp.getType();

		if (lType instanceof AUnionType)
		{

			obligations.add(new SubTypeObligation(lExp, new ABooleanBasicType(lExp.getLocation(), false), lType, question));
		}

		if (rType instanceof AUnionType)
		{
			question.push(new POImpliesContext(lExp));
			obligations.add(new SubTypeObligation(rExp, new ABooleanBasicType(rExp.getLocation(), false), rType, question));
			question.pop();
		}

		obligations.addAll(lExp.apply(this, question));

		question.push(new POImpliesContext(lExp));
		obligations.addAll(rExp.apply(this, question));
		question.pop();

		return obligations;
	}

	private <T> ProofObligationList handleBinaryBooleanExp(T node,
			POContextStack question)
	{
		ProofObligationList obligations = new ProofObligationList();

		PExp[] leftRight = getLeftRight(node);
		PExp lExp = leftRight[LEFT];
		PType lType = lExp.getType();
		PExp rExp = leftRight[RIGHT];
		PType rType = rExp.getType();

		if (lType instanceof AUnionType)
		{

			obligations.add(new SubTypeObligation(lExp, new ABooleanBasicType(lExp.getLocation(), false), lType, question));
		}

		if (rType instanceof AUnionType)
		{
			obligations.add(new SubTypeObligation(rExp, new ABooleanBasicType(rExp.getLocation(), false), rType, question));
		}

		obligations.addAll(lExp.apply(this, question));

		question.push(new POImpliesContext(lExp));
		obligations.addAll(rExp.apply(this, question));
		question.pop();

		return obligations;

	}

	@Override
	public ProofObligationList caseAEquivalentBooleanBinaryExp(
			AEquivalentBooleanBinaryExp node, POContextStack question)
	{
		return handleBinaryBooleanExp(node, question);
	}

	@Override
	public ProofObligationList caseAImpliesBooleanBinaryExp(
			AImpliesBooleanBinaryExp node, POContextStack question)
	{
		return handleBinaryBooleanExp(node, question);
	}

	@Override
	public ProofObligationList caseAOrBooleanBinaryExp(
			AOrBooleanBinaryExp node, POContextStack question)
	{

		ProofObligationList obligations = new ProofObligationList();

		PExp lExp = node.getLeft();
		PExp rExp = node.getRight();
		PType lType = lExp.getType();
		PType rType = rExp.getType();

		if (lType instanceof AUnionType)
		{
			obligations.add(new SubTypeObligation(lExp, new ABooleanBasicType(lExp.getLocation(), false), lType, question));
		}

		if (rType instanceof AUnionType)
		{
			question.push(new PONotImpliesContext(lExp));
			obligations.add(new SubTypeObligation(rExp, new ABooleanBasicType(rExp.getLocation(), false), rType, question));
			question.pop();
		}

		obligations.addAll(lExp.apply(this, question));
		question.push(new PONotImpliesContext(lExp));
		obligations.addAll(rExp.apply(this, question));
		question.pop();

		return obligations;
	}

	private <T extends PExp> ProofObligationList handleDivideNumericBinaryExp(
			T node, POContextStack question)
	{
		ProofObligationList obligations = new ProofObligationList();
		PExp[] leftRight = getLeftRight(node);
		PExp rExp = leftRight[RIGHT];
		if (!(rExp instanceof AIntLiteralExp)
				&& !(rExp instanceof ARealLiteralExp))
		{
			obligations.add(new NonZeroObligation(node.getLocation(), rExp, question));
		}

		return obligations;
	}

	@Override
	// RWL see [1] pg.
	public ProofObligationList caseADivNumericBinaryExp(
			ADivNumericBinaryExp node, POContextStack question)
	{
		return handleDivideNumericBinaryExp(node, question);
	}

	@Override
	public ProofObligationList caseADivideNumericBinaryExp(
			ADivideNumericBinaryExp node, POContextStack question)
	{
		return handleDivideNumericBinaryExp(node, question);
	}

	private <T> ProofObligationList handleNumericBinaryExpression(T node,
			POContextStack question)
	{
		ProofObligationList obligations = new ProofObligationList();

		PExp[] leftRight = getLeftRight(node);
		PExp left = leftRight[LEFT];
		PExp right = leftRight[RIGHT];
		PType ltype = left.getType();
		PType rtype = right.getType();

		if (ltype instanceof AUnionType)
		{
			obligations.add(new SubTypeObligation(left, new ARealNumericBasicType(left.getLocation(), false), ltype, question));
		}

		if (rtype instanceof AUnionType)
		{
			obligations.add(new SubTypeObligation(right, new ARealNumericBasicType(right.getLocation(), false), rtype, question));
		}

		obligations.addAll(left.apply(this, question));
		obligations.addAll(right.apply(this, question));
		return obligations;

	}

	@Override
	public ProofObligationList caseAGreaterEqualNumericBinaryExp(
			AGreaterEqualNumericBinaryExp node, POContextStack question)
	{
		return handleNumericBinaryExpression(node, question);
	}

	@Override
	public ProofObligationList caseAGreaterNumericBinaryExp(
			AGreaterNumericBinaryExp node, POContextStack question)
	{
		return handleNumericBinaryExpression(node, question);
	}

	@Override
	public ProofObligationList caseALessEqualNumericBinaryExp(
			ALessEqualNumericBinaryExp node, POContextStack question)
	{
		return handleNumericBinaryExpression(node, question);
	}

	@Override
	public ProofObligationList caseALessNumericBinaryExp(
			ALessNumericBinaryExp node, POContextStack question)
	{
		return handleNumericBinaryExpression(node, question);
	}

	@Override
	public ProofObligationList caseAModNumericBinaryExp(
			AModNumericBinaryExp node, POContextStack question)
	{
		return handleNumericBinaryExpression(node, question);
	}

	@Override
	public ProofObligationList caseAPlusNumericBinaryExp(
			APlusNumericBinaryExp node, POContextStack question)
	{
		return handleNumericBinaryExpression(node, question);
	}

	@Override
	public ProofObligationList caseARemNumericBinaryExp(
			ARemNumericBinaryExp node, POContextStack question)
	{
		return handleNumericBinaryExpression(node, question);
	}

	@Override
	public ProofObligationList caseASubstractNumericBinaryExp(
			ASubstractNumericBinaryExp node, POContextStack question)
	{
		return handleNumericBinaryExpression(node, question);
	}

	@Override
	public ProofObligationList caseATimesNumericBinaryExp(
			ATimesNumericBinaryExp node, POContextStack question)
	{
		return handleNumericBinaryExpression(node, question);
	}

	@Override
	public ProofObligationList caseAMapEnumMapExp(AMapEnumMapExp node,
			POContextStack question)
	{

		ProofObligationList obligations = new ProofObligationList();

		List<AMapletExp> members = node.getMembers();

		for (AMapletExp maplet : members)
		{
			obligations.addAll(maplet.apply(this, question));
		}

		if (members.size() > 1)
			obligations.add(new MapSeqOfCompatibleObligation(node, question));

		return obligations;
	}

	@Override
	public ProofObligationList caseASeqCompSeqExp(ASeqCompSeqExp node,
			POContextStack question)
	{

		ProofObligationList obligations = new ProofObligationList();

		PExp first = node.getFirst();

		question.push(new POForAllPredicateContext(node));
		obligations.addAll(first.apply(this, question));
		question.pop();

		PExp predicate = node.getPredicate();
		if (predicate != null)
		{
			question.push(new POForAllContext(node));
			obligations.addAll(predicate.apply(this, question));
			question.pop();
		}

		return obligations;
	}

	@Override
	public ProofObligationList caseASeqEnumSeqExp(ASeqEnumSeqExp node,
			POContextStack question)
	{

		ProofObligationList obligations = new ProofObligationList();

		for (PExp e : node.getMembers())
			obligations.addAll(e.apply(this, question));

		return obligations;
	}

	@Override
	public ProofObligationList caseASetCompSetExp(ASetCompSetExp node,
			POContextStack question)

	{
		PExp first = node.getFirst();
		PExp predicate = node.getPredicate();

		ProofObligationList obligations = new ProofObligationList();
		question.push(new POForAllPredicateContext(node));
		obligations.addAll(first.apply(this, question));
		question.pop();

		List<PMultipleBind> bindings = node.getBindings();

		boolean finiteTest = false;
		for (PMultipleBind b : bindings)
		{
			if (b instanceof ATypeMultipleBind)
			{
				finiteTest = true;
			}
		}

		if (finiteTest)
		{
			obligations.addAll(predicate.apply(this, question));
		}

		if (predicate != null)
		{
			question.push(new POForAllContext(node));
			obligations.addAll(predicate.apply(this, question));
			question.pop();
		}

		return obligations;
	}

	@Override
	public ProofObligationList caseASetEnumSetExp(ASetEnumSetExp node,
			POContextStack question)
	{
		ProofObligationList obligations = new ProofObligationList();

		for (PExp e : node.getMembers())
			obligations.addAll(e.apply(this, question));

		return obligations;

	}

	@Override
	public ProofObligationList caseASetRangeSetExp(ASetRangeSetExp node,
			POContextStack question)
	{
		PExp last = node.getLast();
		PExp first = node.getFirst();
		ProofObligationList obligations = first.apply(this, question);
		obligations.addAll(last.apply(this, question));
		return obligations;

	}

	@Override
	public ProofObligationList defaultPExp(PExp node, POContextStack question)
	{
		return new ProofObligationList();
	}

}
