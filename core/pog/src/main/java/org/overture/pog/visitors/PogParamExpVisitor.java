package org.overture.pog.visitors;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SOperationDefinitionBase;
import org.overture.ast.expressions.*;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexIdentifierToken;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.*;
import org.overture.ast.types.*;
import org.overture.pog.contexts.*;
import org.overture.pog.obligation.*;
import org.overture.pog.pub.IPOContextStack;
import org.overture.pog.pub.IPogAssistantFactory;
import org.overture.pog.pub.IProofObligationList;
import org.overture.pog.utility.PogAssistantFactory;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class PogParamExpVisitor<Q extends IPOContextStack, A extends IProofObligationList>
		extends AbstractPogParamVisitor
{

	final private QuestionAnswerAdaptor<IPOContextStack, ? extends IProofObligationList> rootVisitor;
	final private QuestionAnswerAdaptor<IPOContextStack, ? extends IProofObligationList> mainVisitor;
	
	public final static LexNameToken NO_PRECONDITION = new LexNameToken("", "", null);

	final private IPogAssistantFactory aF;

	// Added a mainVisitor hack to enable use from the compassVisitors -ldc

	public PogParamExpVisitor(
			QuestionAnswerAdaptor<IPOContextStack, ? extends IProofObligationList> parentVisitor,
			QuestionAnswerAdaptor<IPOContextStack, ? extends IProofObligationList> mainVisitor,
			IPogAssistantFactory assistantFactory)
	{
		this.rootVisitor = parentVisitor;
		this.mainVisitor = mainVisitor;
		this.aF = assistantFactory;
	}

	/**
	 * <b>Warning!</b> This constructor is not for use with Overture extensions as it sets several customisable fields
	 * to Overture defaults. Use
	 * {@link #PogParamExpVisitor(QuestionAnswerAdaptor, QuestionAnswerAdaptor, IPogAssistantFactory)} instead
	 * 
	 * @param parentVisitor
	 */
	public PogParamExpVisitor(
			QuestionAnswerAdaptor<IPOContextStack, ? extends IProofObligationList> parentVisitor)
	{
		this.rootVisitor = parentVisitor;
		this.mainVisitor = this;
		this.aF = new PogAssistantFactory();
	}
	
	@Override
	public IProofObligationList caseAAnnotatedUnaryExp(AAnnotatedUnaryExp node, IPOContextStack question)
		throws AnalysisException
	{
		IProofObligationList obligations = beforeAnnotation(node.getAnnotation(), node, question);
		obligations.addAll(node.getExp().apply(this, question));
		return afterAnnotation(node.getAnnotation(), node, obligations, question);
	}

	@Override
	// RWL see [1] pg. 57: 6.12 Apply Expressions
	public IProofObligationList caseAApplyExp(AApplyExp node,
			IPOContextStack question) throws AnalysisException
	{
		IProofObligationList obligations = new ProofObligationList();

		PExp root = node.getRoot();

		// is it a map?

		PType type = root.getType();
		if (aF.createPTypeAssistant().isMap(type, null))
		{
			SMapType mapType = aF.createPTypeAssistant().getMap(type, null);
			obligations.add(new MapApplyObligation(node.getRoot(), node.getArgs().get(0), question, aF));
			PType aType = question.checkType(node.getArgs().get(0), node.getArgtypes().get(0));

			if (!aF.getTypeComparator().isSubType(aType, mapType.getFrom()))
			{
				TypeCompatibilityObligation sto = TypeCompatibilityObligation.newInstance(node.getArgs().get(0), mapType.getFrom(), aType, question, aF);
				if (sto != null)
				{
					obligations.add(sto);
				}
			}
		}

		if (!aF.createPTypeAssistant().isUnknown(type)
				&& aF.createPTypeAssistant().isFunction(type, null))
		{
			AFunctionType funcType = aF.createPTypeAssistant().getFunction(type);
			ILexNameToken prename = getPreName(root);
			if (prename == null
					|| !prename.equals(NO_PRECONDITION))
			{
				obligations.add(new FunctionApplyObligation(node.getRoot(), node.getArgs(), prename, question, aF));
			}

			int i = 0;
			List<PType> argTypes = node.getArgtypes();
			List<PExp> argList = node.getArgs();
			for (PType argType : argTypes)
			{
				argType = question.checkType(argList.get(i), argType);
				PType pt = funcType.getParameters().get(i);

				if (!aF.getTypeComparator().isSubType(argType, pt))
				{
					TypeCompatibilityObligation sto = TypeCompatibilityObligation.newInstance(argList.get(i), pt, argType, question, aF);
					if (sto != null)
					{
						obligations.add(sto);
					}
				}
				i++;
			}

			PDefinition recursive = node.getRecursive();
			if (recursive != null)
			{
				if (recursive instanceof AExplicitFunctionDefinition)
				{
					AExplicitFunctionDefinition def = (AExplicitFunctionDefinition) recursive;
					if (def.getMeasureName() != null)
					{
						obligations.add(new RecursiveObligation(def, node, question, aF));
					}
				} else if (recursive instanceof AImplicitFunctionDefinition)
				{
					AImplicitFunctionDefinition def = (AImplicitFunctionDefinition) recursive;
					if (def.getMeasureName() != null)
					{
						obligations.add(new RecursiveObligation(def, node, question, aF));
					}

				}
			}
		}

		if (aF.createPTypeAssistant().isSeq(type, null))
		{
			obligations.add(new SeqApplyObligation(node.getRoot(), node.getArgs().get(0), question, aF));
		}

		obligations.addAll(node.getRoot().apply(mainVisitor, question));

		for (PExp arg : node.getArgs())
		{
			obligations.addAll(arg.apply(mainVisitor, question));
		}

		// stick possible op post_condition in the context
		SOperationDefinitionBase calledOp = node.apply(new GetOpCallVisitor());
		if (calledOp != null)
		{
			new OpPostConditionContext(calledOp.getPostdef(), node, calledOp, aF, question);
		}
		return obligations;
	}

	@Override
	// see [1] pg. 179 unary expressions
	public IProofObligationList caseAHeadUnaryExp(AHeadUnaryExp node,
			IPOContextStack question) throws AnalysisException
	{

		IProofObligationList obligations = defaultSUnaryExp(node, question);
		PExp exp = node.getExp();

		// RWL This is a hack. The new ast LexNameToken's toString method
		// includes the module e.g. like Test`b for variables
		// which the old one did not. Hence proof obligations with variable
		// names are different as "Test`b" is just b with the old proof
		// obligations generator.
		PExp fake = exp.clone();
		if (exp instanceof AVariableExp)
		{
			AVariableExp var = (AVariableExp) fake;
			var.setName(new LexNameToken("", var.getName().getIdentifier().clone()));
		}

		if (!aF.createPTypeAssistant().isType(exp.getType(), ASeq1SeqType.class))
		{
			obligations.add(new NonEmptySeqObligation(fake, question, aF));
		}

		return obligations;
	}

	@Override
	// [1] pg. 46
	public IProofObligationList caseACasesExp(ACasesExp node,
			IPOContextStack question) throws AnalysisException
	{
		IProofObligationList obligations = new ProofObligationList();

		int count = 0;
		boolean hasIgnore = false;

		// handle each case
		for (ACaseAlternative alt : node.getCases())
		{

			if (alt.getPattern() instanceof AIgnorePattern)
			{
				hasIgnore = true;
			}

			PPattern pattern = alt.getPattern();
			PExp cexp = alt.getCexp();
			PType type = node.getExpression().getType();

			question.push(new POCaseContext(pattern, type, cexp.clone(), aF));
			obligations.addAll(alt.getResult().apply(rootVisitor, question));
			question.pop();
			question.push(new PONotCaseContext(pattern, type, cexp.clone(), aF));

			count++;
		}

		if (node.getOthers() != null)
		{
			obligations.addAll(node.getOthers().apply(mainVisitor, question));
		}

		for (int i = 0; i < count; i++)
		{
			question.pop();
		}

		if (node.getOthers() == null && !hasIgnore)
		{
			obligations.add(new CasesExhaustiveObligation(node, question, aF));
		}

		return obligations;
	}

	@Override
	public IProofObligationList caseAMapCompMapExp(AMapCompMapExp node,
			IPOContextStack question) throws AnalysisException
	{
		IProofObligationList obligations = new ProofObligationList();

		obligations.add(new MapInjectivityComp(node, question, aF));

		question.push(new POForAllPredicateContext(node));
		obligations.addAll(node.getFirst().apply(mainVisitor, question));
		question.pop();

		boolean finiteTest = false;

		for (PMultipleBind mb : node.getBindings())
		{
			obligations.addAll(mb.apply(rootVisitor, question));
			if (mb instanceof ATypeMultipleBind)
			{
				finiteTest = true;
			}
		}

		if (finiteTest)
		{
			obligations.add(new FiniteMapObligation(node, node.getType(), question, aF));
		}

		PExp predicate = node.getPredicate();
		if (predicate != null)
		{
			question.push(new POForAllContext(node));
			obligations.addAll(predicate.apply(mainVisitor, question));
			question.pop();
		}

		return obligations;
	}

	@Override
	// RWL see [1] pg. 179 A.5.4 Unary Expressions
	public IProofObligationList defaultSUnaryExp(SUnaryExp node,
			IPOContextStack question) throws AnalysisException
	{

		return node.getExp().apply(mainVisitor, question);
	}

	@Override
	// RWL
	public IProofObligationList defaultSBinaryExp(SBinaryExp node,
			IPOContextStack question) throws AnalysisException
	{
		IProofObligationList obligations = new ProofObligationList();
		obligations.addAll(node.getLeft().apply(mainVisitor, question));
		obligations.addAll(node.getRight().apply(mainVisitor, question));
		return obligations;
	}

	@Override
	public IProofObligationList caseABooleanConstExp(ABooleanConstExp node,
			IPOContextStack question)
	{

		return new ProofObligationList();
	}

	@Override
	public IProofObligationList caseACharLiteralExp(ACharLiteralExp node,
			IPOContextStack question)
	{
		return new ProofObligationList();
	}

	@Override
	public IProofObligationList caseAElseIfExp(AElseIfExp node,
			IPOContextStack question) throws AnalysisException
	{

		IProofObligationList obligations = new ProofObligationList();
		question.push(new POImpliesContext(node.getElseIf()));
		obligations.addAll(node.getThen().apply(mainVisitor, question));
		question.pop();

		return obligations;
	}

	@Override
	public IProofObligationList caseAExists1Exp(AExists1Exp node,
			IPOContextStack question) throws AnalysisException
	{
		IProofObligationList obligations = new ProofObligationList();
		question.push(new POForAllContext(aF, node));
		obligations.addAll(node.getPredicate().apply(mainVisitor, question));
		question.pop();
		return obligations;
	}

	@Override
	public IProofObligationList caseAExistsExp(AExistsExp node,
			IPOContextStack question) throws AnalysisException
	{
		IProofObligationList obligations = new ProofObligationList();

		for (PMultipleBind mb : node.getBindList())
		{
			obligations.addAll(mb.apply(rootVisitor, question));
		}

		question.push(new POForAllContext(node));
		obligations.addAll(node.getPredicate().apply(mainVisitor, question));
		question.pop();

		return obligations;
	}

	@Override
	public IProofObligationList caseAFieldExp(AFieldExp node,
			IPOContextStack question) throws AnalysisException
	{
		return node.getObject().apply(mainVisitor, question);
	}

	@Override
	public IProofObligationList caseAFieldNumberExp(AFieldNumberExp node,
			IPOContextStack question) throws AnalysisException
	{

		IProofObligationList obligations = node.getTuple().apply(mainVisitor, question);

		PType puType = node.getTuple().getType();

		if (aF.createPTypeAssistant().isUnion(puType, null))
		{
			AUnionType utype = (AUnionType) aF.createPTypeAssistant().getUnion(puType, null);

			for (PType t : utype.getTypes())
			{
				if (t instanceof AProductType)
				{
					AProductType aprodType = (AProductType) t;
					if (aprodType.getTypes().size() >= node.getField().getValue())
					{
						// tuple selection is fine. do nothing and check the
						// next one
						continue;
					}
				}
				obligations.add(new TupleSelectObligation(node.getTuple(), t, question, aF));
			}
		}

		return obligations;
	}

	@Override
	public IProofObligationList caseAForAllExp(AForAllExp node,
			IPOContextStack question) throws AnalysisException
	{

		IProofObligationList obligations = new ProofObligationList();

		for (PMultipleBind mb : node.getBindList())
		{
			obligations.addAll(mb.apply(rootVisitor, question));
		}

		question.push(new POForAllContext(node));
		obligations.addAll(node.getPredicate().apply(mainVisitor, question));
		question.pop();
		return obligations;
	}

	@Override
	public IProofObligationList caseAFuncInstatiationExp(
			AFuncInstatiationExp node, IPOContextStack question)
			throws AnalysisException
	{
		return node.getFunction().apply(mainVisitor, question);
	}

	@Override
	// RWL
	public IProofObligationList caseAHistoryExp(AHistoryExp node,
			IPOContextStack question)
	{
		// No getProofObligationMethod found on the HistoryExpression class of
		// VDMJ assuming we have the empty list.
		return new ProofObligationList();
	}

	@Override
	public IProofObligationList caseAIfExp(AIfExp node, IPOContextStack question)
			throws AnalysisException
	{
		IProofObligationList obligations = node.getTest().apply(mainVisitor, question);

		question.push(new POImpliesContext(node.getTest()));
		obligations.addAll(node.getThen().apply(mainVisitor, question));
		question.pop();

		question.push(new PONotImpliesContext(node.getTest()));

		for (AElseIfExp e : node.getElseList())
		{
			obligations.addAll(e.apply(mainVisitor, question));
			question.push(new PONotImpliesContext(e.getElseIf()));

		}

		int sizeBefore = question.size();
		obligations.addAll(node.getElse().apply(mainVisitor, question));
		assert sizeBefore <= question.size();

		for (int i = 0; i < node.getElseList().size(); i++)
		{
			question.pop();
		}

		question.pop();
		return obligations;
	}

	@Override
	public IProofObligationList caseAIntLiteralExp(AIntLiteralExp node,
			IPOContextStack question)
	{

		return new ProofObligationList();
	}

	@Override
	public IProofObligationList caseAIotaExp(AIotaExp node,
			IPOContextStack question) throws AnalysisException
	{
		IProofObligationList obligations = node.getBind().apply(rootVisitor, question);
		obligations.add(new UniqueExistenceObligation(node, question, aF));

		question.push(new POForAllContext(aF, node));
		obligations.addAll(node.getPredicate().apply(mainVisitor, question));
		question.pop();
		return obligations;
	}

	@Override
	public IProofObligationList caseAIsExp(AIsExp node, IPOContextStack question)
			throws AnalysisException
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
		return node.getTest().apply(mainVisitor, question);
	}

	@Override
	// RWL See [1] pg. 64-65
	public IProofObligationList caseAIsOfBaseClassExp(AIsOfBaseClassExp node,
			IPOContextStack question) throws AnalysisException
	{
		return node.getExp().apply(mainVisitor, question);
	}

	@Override
	// RWL See [1] pg. 64-65
	public IProofObligationList caseAIsOfClassExp(AIsOfClassExp node,
			IPOContextStack question) throws AnalysisException
	{

		question.noteType(node.getExp(), node.getClassType());

		return node.getExp().apply(mainVisitor, question);
	}

	@Override
	// RWL See [1] pg. 62
	public IProofObligationList caseALambdaExp(ALambdaExp node,
			IPOContextStack question) throws AnalysisException
	{

		IProofObligationList obligations = new ProofObligationList();

		for (ATypeBind tb : node.getBindList())
		{
			obligations.addAll(tb.apply(rootVisitor, question));
		}

		question.push(new POForAllContext(node));
		obligations.addAll(node.getExpression().apply(mainVisitor, question));
		question.pop();

		return obligations;
	}

	@Override
	// RWL See [1] pg.95
	public IProofObligationList caseALetBeStExp(ALetBeStExp node,
			IPOContextStack question) throws AnalysisException
	{
		IProofObligationList obligations = new ProofObligationList();
		obligations.add(new LetBeExistsObligation(node, question, aF));
		obligations.addAll(node.getBind().apply(rootVisitor, question));

		PExp suchThat = node.getSuchThat();
		if (suchThat != null)
		{
			question.push(new POForAllContext(node, aF));
			obligations.addAll(suchThat.apply(mainVisitor, question));
			question.pop();
		}

		question.push(new POForAllPredicateContext(node, aF));
		obligations.addAll(node.getValue().apply(mainVisitor, question));
		question.pop();

		return obligations;
	}

	@Override
	// RWL see [1] pg.
	public IProofObligationList caseALetDefExp(ALetDefExp node,
			IPOContextStack question) throws AnalysisException
	{
		IProofObligationList obligations = new ProofObligationList();

		for (PDefinition def : node.getLocalDefs())
		{
			question.push(new PONameContext(aF.createPDefinitionAssistant().getVariableNames(def)));
			obligations.addAll(def.apply(rootVisitor, question));
			question.pop();
		}

		question.push(new POLetDefContext(node));
		obligations.addAll(node.getExpression().apply(mainVisitor, question));
		question.pop();

		return obligations;
	}

	@Override
	public IProofObligationList caseADefExp(ADefExp node,
			IPOContextStack question) throws AnalysisException
	{
		IProofObligationList obligations = aF.createPDefinitionAssistant().getProofObligations(node.getLocalDefs(), rootVisitor, question);

		question.push(new PODefContext(node));
		obligations.addAll(node.getExpression().apply(mainVisitor, question));
		question.pop();

		return obligations;
	}

	@Override
	public IProofObligationList defaultSMapExp(SMapExp node,
			IPOContextStack question)
	{

		return new ProofObligationList();
	}

	@Override
	public IProofObligationList caseAMapletExp(AMapletExp node,
			IPOContextStack question) throws AnalysisException
	{

		IProofObligationList obligations = node.getLeft().apply(mainVisitor, question);
		obligations.addAll(node.getRight().apply(mainVisitor, question));
		return obligations;
	}

	@Override
	public IProofObligationList caseAMkBasicExp(AMkBasicExp node,
			IPOContextStack question) throws AnalysisException
	{
		return node.getArg().apply(mainVisitor, question);
	}

	@Override
	public IProofObligationList caseAMkTypeExp(AMkTypeExp node,
			IPOContextStack question) throws AnalysisException
	{

		IProofObligationList obligations = new ProofObligationList();
		@SuppressWarnings("unchecked")
		Queue<PExp> args = (Queue<PExp>) node.getArgs().clone();
		for (PExp arg : args)
		{
			obligations.addAll(arg.apply(mainVisitor, question));
		}

		@SuppressWarnings("unchecked")
		Queue<PType> argTypes = (Queue<PType>) node.getArgTypes().clone();

		ARecordInvariantType recordType = node.getRecordType();
		for (AFieldField f : recordType.getFields())
		{
			PType aType = argTypes.poll();
			PExp aExp = args.poll();

			if (!aF.getTypeComparator().isSubType(question.checkType(aExp, aType), f.getType()))
			{
				TypeCompatibilityObligation sto = TypeCompatibilityObligation.newInstance(aExp, f.getType(), aType, question, aF);
				if (sto != null)
				{
					obligations.add(sto);
				}
			}
		}

		PDefinition invDef = recordType.getInvDef();
		if (invDef != null)
		{
			TypeCompatibilityObligation sto = TypeCompatibilityObligation.newInstance(node, recordType, recordType, question, aF);
			if (sto != null)
			{
				obligations.add(sto);
			}
		}
		return obligations;
	}

	private static AFieldField findField(ARecordInvariantType ty,
			ILexIdentifierToken id)
	{

		List<AFieldField> fields = ty.getFields();
		for (AFieldField f : fields)
		{
			if (f.getTag().equals(id.getName()))
			{
				return f;
			}
		}

		return null;
	}

	@Override
	// RWL See [1] pg. 56
	public IProofObligationList caseAMuExp(AMuExp node, IPOContextStack question)
			throws AnalysisException
	{
		IProofObligationList obligations = node.getRecord().apply(rootVisitor, question);
		Queue<ARecordModifier> modifiers = node.getModifiers();
		ARecordInvariantType recordType = node.getRecordType();
		LinkedList<PType> mTypes = node.getModTypes();

		int i = 0;
		for (ARecordModifier mod : modifiers)
		{
			obligations.addAll(mod.getValue().apply(mainVisitor, question));
			AFieldField f = findField(recordType, mod.getTag());
			PType mType = mTypes.get(i++);
			if (f != null)
			{
				if (!aF.getTypeComparator().isSubType(mType, f.getType()))
				{
					TypeCompatibilityObligation sto = TypeCompatibilityObligation.newInstance(mod.getValue(), f.getType(), mType, question, aF);
					if (sto != null)
					{
						obligations.add(sto);
					}
				}
			}

		}

		return obligations;
	}

	@Override
	public IProofObligationList caseANarrowExp(ANarrowExp node,
			IPOContextStack question) throws AnalysisException
	{
		IProofObligationList obligations = new ProofObligationList();

		PType expected = node.getTypedef() == null ? node.getBasicType()
				: aF.createPDefinitionAssistant().getType(node.getTypedef());
		question.noteType(node.getTest(), expected);

		if (!aF.getTypeComparator().isSubType(node.getTest().getType(), expected))
		{
			TypeCompatibilityObligation sto = TypeCompatibilityObligation.newInstance(node.getTest(), expected, node.getTest().getType(), question, aF);
			if (sto != null)
			{
				obligations.add(sto);
			}
		}

		obligations.addAll(node.getTest().apply(rootVisitor, question));

		return obligations;
	}

	@Override
	public IProofObligationList caseANewExp(ANewExp node,
			IPOContextStack question) throws AnalysisException
	{

		IProofObligationList obligations = new ProofObligationList();

		for (PExp exp : node.getArgs())
		{
			obligations.addAll(exp.apply(mainVisitor, question));
		}

		return obligations;
	}

	@Override
	public IProofObligationList caseANilExp(ANilExp node,
			IPOContextStack question)
	{
		return new ProofObligationList();
	}

	@Override
	public IProofObligationList caseANotYetSpecifiedExp(
			ANotYetSpecifiedExp node, IPOContextStack question)
	{
		return new ProofObligationList();
	}

	@Override
	public IProofObligationList caseAPostOpExp(APostOpExp node,
			IPOContextStack question)
	{
		return new ProofObligationList();
	}

	@Override
	public IProofObligationList caseAPreExp(APreExp node,
			IPOContextStack question)
	{
		return new ProofObligationList();
	}

	@Override
	public IProofObligationList caseAPreOpExp(APreOpExp node,
			IPOContextStack question)
	{
		return new ProofObligationList();
	}

	@Override
	public IProofObligationList caseAQuoteLiteralExp(AQuoteLiteralExp node,
			IPOContextStack question)
	{
		return new ProofObligationList();
	}

	@Override
	public IProofObligationList caseARealLiteralExp(ARealLiteralExp node,
			IPOContextStack question)
	{
		return new ProofObligationList();
	}

	@Override
	public IProofObligationList caseASameBaseClassExp(ASameBaseClassExp node,
			IPOContextStack question) throws AnalysisException
	{
		IProofObligationList obligations = new ProofObligationList();

		obligations.addAll(node.getLeft().apply(mainVisitor, question));
		obligations.addAll(node.getRight().apply(mainVisitor, question));

		return obligations;
	}

	@Override
	public IProofObligationList caseASameClassExp(ASameClassExp node,
			IPOContextStack question) throws AnalysisException
	{
		IProofObligationList list = node.getLeft().apply(mainVisitor, question);
		list.addAll(node.getRight().apply(mainVisitor, question));
		return list;
	}

	@Override
	public IProofObligationList caseASelfExp(ASelfExp node,
			IPOContextStack question)
	{
		return new ProofObligationList();
	}

	@Override
	public IProofObligationList defaultSSeqExp(SSeqExp node,
			IPOContextStack question)
	{
		return new ProofObligationList();
	}

	@Override
	public IProofObligationList defaultSSetExp(SSetExp node,
			IPOContextStack question)
	{
		return new ProofObligationList();
	}

	@Override
	public IProofObligationList caseAStateInitExp(AStateInitExp node,
			IPOContextStack question)
	{
		return new ProofObligationList();
	}

	@Override
	public IProofObligationList caseAStringLiteralExp(AStringLiteralExp node,
			IPOContextStack question)
	{
		return new ProofObligationList();
	}

	@Override
	public IProofObligationList caseASubclassResponsibilityExp(
			ASubclassResponsibilityExp node, IPOContextStack question)
	{
		return new ProofObligationList();
	}

	@Override
	public IProofObligationList caseASubseqExp(ASubseqExp node,
			IPOContextStack question) throws AnalysisException
	{
		IProofObligationList list = node.getSeq().apply(mainVisitor, question);
		list.addAll(node.getFrom().apply(mainVisitor, question));
		list.addAll(node.getTo().apply(mainVisitor, question));
		return list;
	}

	@Override
	public IProofObligationList caseAThreadIdExp(AThreadIdExp node,
			IPOContextStack question)
	{
		return new ProofObligationList();
	}

	@Override
	public IProofObligationList caseATimeExp(ATimeExp node,
			IPOContextStack question)
	{
		return new ProofObligationList();
	}

	@Override
	public IProofObligationList caseATupleExp(ATupleExp node,
			IPOContextStack question) throws AnalysisException
	{
		IProofObligationList obligations = new ProofObligationList();
		for (PExp exp : node.getArgs())
		{
			obligations.addAll(exp.apply(mainVisitor, question));
		}
		return obligations;
	}

	@Override
	public IProofObligationList caseAUndefinedExp(AUndefinedExp node,
			IPOContextStack question)
	{
		return new ProofObligationList();
	}

	@Override
	public IProofObligationList caseAAbsoluteUnaryExp(AAbsoluteUnaryExp node,
			IPOContextStack question) throws AnalysisException
	{

		return node.getExp().apply(mainVisitor, question);
	}

	@Override
	public IProofObligationList caseACardinalityUnaryExp(
			ACardinalityUnaryExp node, IPOContextStack question)
			throws AnalysisException
	{

		return node.getExp().apply(mainVisitor, question);
	}

	@Override
	public IProofObligationList caseADistConcatUnaryExp(
			ADistConcatUnaryExp node, IPOContextStack question)
			throws AnalysisException
	{
		return node.getExp().apply(mainVisitor, question);
	}

	@Override
	public IProofObligationList caseADistIntersectUnaryExp(
			ADistIntersectUnaryExp node, IPOContextStack question)
			throws AnalysisException
	{
		IProofObligationList obligations = node.getExp().apply(mainVisitor, question);
		obligations.add(new org.overture.pog.obligation.NonEmptySetObligation(node.getExp(), question, aF));
		return obligations;
	}

	@Override
	public IProofObligationList caseADistMergeUnaryExp(ADistMergeUnaryExp node,
			IPOContextStack question) throws AnalysisException
	{
		IProofObligationList obligations = new ProofObligationList();
		obligations.add(new MapInjectivityComp(node.getExp(), question, aF));
		return obligations;
	}

	@Override
	public IProofObligationList caseADistUnionUnaryExp(ADistUnionUnaryExp node,
			IPOContextStack question) throws AnalysisException
	{
		return node.getExp().apply(mainVisitor, question);
	}

	@Override
	public IProofObligationList caseAElementsUnaryExp(AElementsUnaryExp node,
			IPOContextStack question) throws AnalysisException
	{

		return node.getExp().apply(mainVisitor, question);
	}

	@Override
	public IProofObligationList caseAFloorUnaryExp(AFloorUnaryExp node,
			IPOContextStack question) throws AnalysisException
	{

		return node.getExp().apply(mainVisitor, question);
	}

	@Override
	public IProofObligationList caseAIndicesUnaryExp(AIndicesUnaryExp node,
			IPOContextStack question) throws AnalysisException
	{
		return node.getExp().apply(mainVisitor, question);
	}

	@Override
	public IProofObligationList caseALenUnaryExp(ALenUnaryExp node,
			IPOContextStack question) throws AnalysisException
	{

		return node.getExp().apply(mainVisitor, question);
	}

	@Override
	public IProofObligationList caseAMapDomainUnaryExp(AMapDomainUnaryExp node,
			IPOContextStack question) throws AnalysisException
	{
		return node.getExp().apply(mainVisitor, question);
	}

	@Override
	public IProofObligationList caseAMapInverseUnaryExp(
			AMapInverseUnaryExp node, IPOContextStack question)
			throws AnalysisException
	{
		IProofObligationList obligations = node.getExp().apply(mainVisitor, question);
		if (!node.getMapType().getEmpty())
		{
			obligations.add(new org.overture.pog.obligation.MapInverseObligation(node, question, aF));
		}
		return obligations;
	}

	@Override
	public IProofObligationList caseAMapRangeUnaryExp(AMapRangeUnaryExp node,
			IPOContextStack question) throws AnalysisException
	{
		return node.getExp().apply(mainVisitor, question);
	}

	@Override
	public IProofObligationList caseANotUnaryExp(ANotUnaryExp node,
			IPOContextStack question) throws AnalysisException
	{
		return node.getExp().apply(mainVisitor, question);
	}

	@Override
	public IProofObligationList caseAPowerSetUnaryExp(APowerSetUnaryExp node,
			IPOContextStack question) throws AnalysisException
	{
		return node.getExp().apply(mainVisitor, question);
	}

	@Override
	public IProofObligationList caseAReverseUnaryExp(AReverseUnaryExp node,
			IPOContextStack question) throws AnalysisException
	{
		return node.getExp().apply(mainVisitor, question);
	}

	@Override
	public IProofObligationList caseATailUnaryExp(ATailUnaryExp node,
			IPOContextStack question) throws AnalysisException
	{
		IProofObligationList obligations = node.getExp().apply(mainVisitor, question);

		if (!aF.createPTypeAssistant().isType(node.getExp().getType(), ASeq1SeqType.class))
		{
			obligations.add(new NonEmptySeqObligation(node.getExp(), question, aF));
		}

		return obligations;
	}

	@Override
	public IProofObligationList caseAUnaryMinusUnaryExp(
			AUnaryMinusUnaryExp node, IPOContextStack question)
			throws AnalysisException
	{

		return node.getExp().apply(mainVisitor, question);
	}

	@Override
	public IProofObligationList caseAUnaryPlusUnaryExp(AUnaryPlusUnaryExp node,
			IPOContextStack question) throws AnalysisException
	{
		return node.getExp().apply(mainVisitor, question);
	}

	@Override
	public IProofObligationList defaultSBooleanBinaryExp(
			SBooleanBinaryExp node, IPOContextStack question)
			throws AnalysisException
	{
		IProofObligationList obligations = new ProofObligationList();
		PExp lExp = node.getLeft();
		PExp rExp = node.getRight();

		PType lType = lExp.getType();
		PType rType = rExp.getType();
		if (lType instanceof AUnionType)
		{
			TypeCompatibilityObligation sto = TypeCompatibilityObligation.newInstance(lExp, AstFactory.newABooleanBasicType(lExp.getLocation()), lType, question, aF);
			if (sto != null)
			{
				obligations.add(sto);
			}
		}

		if (rType instanceof AUnionType)
		{
			TypeCompatibilityObligation sto = TypeCompatibilityObligation.newInstance(rExp, AstFactory.newABooleanBasicType(rExp.getLocation()), rType, question, aF);
			if (sto != null)
			{
				obligations.add(sto);
			}
		}
		return obligations;
	}

	@Override
	public IProofObligationList caseACompBinaryExp(ACompBinaryExp node,
			IPOContextStack question) throws AnalysisException
	{

		IProofObligationList obligations = new ProofObligationList();
		PExp lExp = node.getLeft();
		PType lType = lExp.getType();
		PExp rExp = node.getRight();

		if (aF.createPTypeAssistant().isFunction(lType, null))
		{
			ILexNameToken pref1 = getPreName(lExp);
			ILexNameToken pref2 = getPreName(rExp);

			if (pref1 == null || !pref1.equals(NO_PRECONDITION))
			{
				obligations.add(new FuncComposeObligation(node, pref1, pref2, question, aF));// gkanos:add on more
																								// argument for the
																								// assistantFactory need
																								// it
			}
		}

		if (aF.createPTypeAssistant().isMap(lType, null))
		{
			obligations.add(new MapComposeObligation(node, question, aF));
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

	private <T> IProofObligationList handleBinaryExpression(T node,
			IPOContextStack question) throws AnalysisException
	{

		if (node == null)
		{
			return new ProofObligationList();
		}

		PExp[] leftRight = getLeftRight(node);
		PExp left = leftRight[LEFT];
		PExp right = leftRight[RIGHT];

		IProofObligationList obligations = new ProofObligationList();
		obligations.addAll(left.apply(mainVisitor, question));
		obligations.addAll(right.apply(mainVisitor, question));
		return obligations;
	}

	@Override
	public IProofObligationList caseADomainResByBinaryExp(
			ADomainResByBinaryExp node, IPOContextStack question)
			throws AnalysisException
	{
		return handleBinaryExpression(node, question);
	}

	@Override
	public IProofObligationList caseADomainResToBinaryExp(
			ADomainResToBinaryExp node, IPOContextStack question)
			throws AnalysisException
	{
		return handleBinaryExpression(node, question);
	}

	@Override
	public IProofObligationList caseAInSetBinaryExp(AInSetBinaryExp node,
			IPOContextStack question) throws AnalysisException
	{
		return handleBinaryExpression(node, question);
	}

	@Override
	public IProofObligationList caseAMapUnionBinaryExp(AMapUnionBinaryExp node,
			IPOContextStack question) throws AnalysisException
	{
		IProofObligationList obligations = handleBinaryExpression(node, question);
		obligations.add(new MapCompatibleObligation(node.getLeft(), node.getRight(), question, aF));
		return obligations;
	}

	@Override
	public IProofObligationList caseANotEqualBinaryExp(ANotEqualBinaryExp node,
			IPOContextStack question) throws AnalysisException
	{
		return handleBinaryExpression(node, question);
	}

	@Override
	public IProofObligationList caseANotInSetBinaryExp(ANotInSetBinaryExp node,
			IPOContextStack question) throws AnalysisException
	{
		return handleBinaryExpression(node, question);
	}

	@Override
	public IProofObligationList defaultSNumericBinaryExp(
			SNumericBinaryExp node, IPOContextStack question)
			throws AnalysisException
	{
		IProofObligationList obligations = new ProofObligationList();

		PExp left = node.getLeft();
		PExp right = node.getRight();
		PType lType = left.getType();
		PType rType = right.getType();

		if (lType instanceof AUnionType)
		{

			TypeCompatibilityObligation sto = TypeCompatibilityObligation.newInstance(left, AstFactory.newARealNumericBasicType(right.getLocation()), lType, question, aF);
			if (sto != null)
			{
				obligations.add(sto);
			}

		}

		if (rType instanceof AUnionType)
		{
			TypeCompatibilityObligation sto = TypeCompatibilityObligation.newInstance(right, AstFactory.newARealNumericBasicType(right.getLocation()), rType, question, aF);
			if (sto != null)
			{
				obligations.add(sto);
			}
		}

		obligations.addAll(left.apply(mainVisitor, question));
		obligations.addAll(right.apply(mainVisitor, question));
		return obligations;
	}

	@Override
	public IProofObligationList caseAPlusPlusBinaryExp(APlusPlusBinaryExp node,
			IPOContextStack question) throws AnalysisException
	{

		IProofObligationList obligations = handleBinaryExpression(node, question);
		PType lType = node.getLeft().getType();

		if (aF.createPTypeAssistant().isSeq(lType, null))
		{
			obligations.add(new org.overture.pog.obligation.SeqModificationObligation(node, question, aF));
		}

		return obligations;
	}

	@Override
	public IProofObligationList caseAProperSubsetBinaryExp(
			AProperSubsetBinaryExp node, IPOContextStack question)
			throws AnalysisException
	{
		return handleBinaryExpression(node, question);
	}

	@Override
	public IProofObligationList caseARangeResByBinaryExp(
			ARangeResByBinaryExp node, IPOContextStack question)
			throws AnalysisException
	{

		return handleBinaryExpression(node, question);
	}

	@Override
	public IProofObligationList caseARangeResToBinaryExp(
			ARangeResToBinaryExp node, IPOContextStack question)
			throws AnalysisException
	{
		return super.caseARangeResToBinaryExp(node, question);
	}

	@Override
	public IProofObligationList caseASeqConcatBinaryExp(
			ASeqConcatBinaryExp node, IPOContextStack question)
			throws AnalysisException
	{
		return handleBinaryExpression(node, question);
	}

	@Override
	public IProofObligationList caseASetDifferenceBinaryExp(
			ASetDifferenceBinaryExp node, IPOContextStack question)
			throws AnalysisException
	{
		return handleBinaryExpression(node, question);
	}

	@Override
	public IProofObligationList caseASetIntersectBinaryExp(
			ASetIntersectBinaryExp node, IPOContextStack question)
			throws AnalysisException
	{
		return handleBinaryExpression(node, question);
	}

	@Override
	public IProofObligationList caseASetUnionBinaryExp(ASetUnionBinaryExp node,
			IPOContextStack question) throws AnalysisException
	{
		return handleBinaryExpression(node, question);
	}

	@Override
	public IProofObligationList caseAStarStarBinaryExp(AStarStarBinaryExp node,
			IPOContextStack question) throws AnalysisException
	{
		IProofObligationList obligations = new ProofObligationList();

		PExp lExp = node.getLeft();
		PType lType = lExp.getType();

		if (aF.createPTypeAssistant().isFunction(lType, null))
		{
			ILexNameToken preName = getPreName(lExp);
			if (preName == null
					|| !preName.equals(NO_PRECONDITION))
			{
				obligations.add(new org.overture.pog.obligation.FuncIterationObligation(node, preName, question, aF));
			}
		}

		if (aF.createPTypeAssistant().isMap(lType, null))
		{
			obligations.add(new MapIterationObligation(node, question, aF));
		}

		return obligations;
	}

	@Override
	public IProofObligationList caseASubsetBinaryExp(ASubsetBinaryExp node,
			IPOContextStack question) throws AnalysisException
	{
		return handleBinaryExpression(node, question);
	}

	@Override
	public IProofObligationList caseAAndBooleanBinaryExp(
			AAndBooleanBinaryExp node, IPOContextStack question)
			throws AnalysisException
	{
		IProofObligationList obligations = new ProofObligationList();

		PExp lExp = node.getLeft();
		PType lType = lExp.getType();
		PExp rExp = node.getRight();
		PType rType = rExp.getType();

		if (aF.createPTypeAssistant().isUnion(lType, null))
		{
			TypeCompatibilityObligation sto = TypeCompatibilityObligation.newInstance(lExp, AstFactory.newABooleanBasicType(lExp.getLocation()), lType, question, aF);
			if (sto != null)
			{
				obligations.add(sto);
			}
		}

		if (aF.createPTypeAssistant().isUnion(rType, null))
		{
			question.push(new POImpliesContext(lExp));

			TypeCompatibilityObligation sto = TypeCompatibilityObligation.newInstance(rExp, AstFactory.newABooleanBasicType(rExp.getLocation()), rType, question, aF);
			if (sto != null)
			{
				obligations.add(sto);
			}
			question.pop();
		}

		obligations.addAll(lExp.apply(mainVisitor, question));

		question.push(new POImpliesContext(lExp));
		obligations.addAll(rExp.apply(mainVisitor, question));
		question.pop();

		return obligations;
	}

	private <T> IProofObligationList handleBinaryBooleanExp(T node,
			IPOContextStack question) throws AnalysisException
	{
		IProofObligationList obligations = new ProofObligationList();

		PExp[] leftRight = getLeftRight(node);
		PExp lExp = leftRight[LEFT];
		PType lType = lExp.getType();
		PExp rExp = leftRight[RIGHT];
		PType rType = rExp.getType();

		if (aF.createPTypeAssistant().isUnion(lType, null))
		{
			TypeCompatibilityObligation sto = TypeCompatibilityObligation.newInstance(lExp, AstFactory.newABooleanBasicType(lExp.getLocation()), lType, question, aF);
			if (sto != null)
			{
				obligations.add(sto);
			}
		}

		if (aF.createPTypeAssistant().isUnion(rType, null))
		{
			TypeCompatibilityObligation sto = TypeCompatibilityObligation.newInstance(rExp, AstFactory.newABooleanBasicType(rExp.getLocation()), rType, question, aF);
			if (sto != null)
			{
				obligations.add(sto);
			}
		}

		obligations.addAll(lExp.apply(mainVisitor, question));
		obligations.addAll(rExp.apply(mainVisitor, question));

		return obligations;
	}

	@Override
	public IProofObligationList caseAEquivalentBooleanBinaryExp(
			AEquivalentBooleanBinaryExp node, IPOContextStack question)
			throws AnalysisException
	{
		return handleBinaryBooleanExp(node, question);
	}

	@Override
	public IProofObligationList caseAImpliesBooleanBinaryExp(
			AImpliesBooleanBinaryExp node, IPOContextStack question)
			throws AnalysisException
	{
		ProofObligationList obligations = new ProofObligationList();

		PExp[] leftRight = getLeftRight(node);
		PExp lExp = leftRight[LEFT];
		PType lType = lExp.getType();
		PExp rExp = leftRight[RIGHT];
		PType rType = rExp.getType();

		if (aF.createPTypeAssistant().isUnion(lType, null))
		{
			obligations.add(TypeCompatibilityObligation.newInstance(lExp, AstFactory.newABooleanBasicType(lExp.getLocation()), lType, question, aF));
		}

		if (aF.createPTypeAssistant().isUnion(rType, null))
		{
			obligations.add(TypeCompatibilityObligation.newInstance(rExp, AstFactory.newABooleanBasicType(rExp.getLocation()), rType, question, aF));
		}

		obligations.addAll(lExp.apply(mainVisitor, question));
		question.push(new POImpliesContext(lExp));
		obligations.addAll(rExp.apply(mainVisitor, question));
		question.pop();

		return obligations;

	}

	@Override
	public IProofObligationList caseAOrBooleanBinaryExp(
			AOrBooleanBinaryExp node, IPOContextStack question)
			throws AnalysisException
	{

		IProofObligationList obligations = new ProofObligationList();

		PExp lExp = node.getLeft();
		PExp rExp = node.getRight();
		PType lType = lExp.getType();
		PType rType = rExp.getType();

		if (lType instanceof AUnionType)
		{
			TypeCompatibilityObligation sto = TypeCompatibilityObligation.newInstance(lExp, AstFactory.newABooleanBasicType(lExp.getLocation()), lType, question, aF);
			if (sto != null)
			{
				obligations.add(sto);
			}
		}

		if (rType instanceof AUnionType)
		{
			question.push(new PONotImpliesContext(lExp));
			TypeCompatibilityObligation sto = TypeCompatibilityObligation.newInstance(rExp, AstFactory.newABooleanBasicType(rExp.getLocation()), rType, question, aF);
			if (sto != null)
			{
				obligations.add(sto);
			}
			question.pop();
		}

		obligations.addAll(lExp.apply(mainVisitor, question));
		question.push(new PONotImpliesContext(lExp));
		obligations.addAll(rExp.apply(mainVisitor, question));
		question.pop();

		return obligations;
	}

	private <T extends PExp> IProofObligationList handleDivideNumericBinaryExp(
			T node, IPOContextStack question) throws AnalysisException
	{
		IProofObligationList obligations = new ProofObligationList();
		PExp[] leftRight = getLeftRight(node);
		PExp rExp = leftRight[RIGHT];

		obligations.addAll(defaultSNumericBinaryExp((SNumericBinaryExp) node, question));

		if (!neverZero(rExp))
		{
			obligations.add(new NonZeroObligation(node.getLocation(), rExp, question, aF));
		}

		return obligations;
	}

	private boolean neverZero(PExp exp)
	{
		if (exp instanceof AIntLiteralExp)
		{
			AIntLiteralExp il_Exp = (AIntLiteralExp) exp;
			if (il_Exp.getValue().getValue() != 0)
			{
				return true;
			}
		}
		if (exp instanceof ARealLiteralExp)
		{
			ARealLiteralExp rl_Exp = (ARealLiteralExp) exp;
			if (rl_Exp.getValue().getValue() != 0)
			{
				return true;
			}
		}
		return false;
	}

	@Override
	// RWL see [1] pg.
	public IProofObligationList caseADivNumericBinaryExp(
			ADivNumericBinaryExp node, IPOContextStack question)
			throws AnalysisException
	{
		return handleDivideNumericBinaryExp(node, question);
	}

	@Override
	public IProofObligationList caseADivideNumericBinaryExp(
			ADivideNumericBinaryExp node, IPOContextStack question)
			throws AnalysisException
	{
		return handleDivideNumericBinaryExp(node, question);
	}

	private IProofObligationList handleNumericBinaryExpression(SNumericBinaryExp node,
			IPOContextStack question) throws AnalysisException
	{
		IProofObligationList obligations = new ProofObligationList();

		PExp[] leftRight = getLeftRight(node);
		PExp left = leftRight[LEFT];
		PExp right = leftRight[RIGHT];
		PType ltype = left.getType();
		PType rtype = right.getType();

		if (left.getLocation().getStartLine() == 2792)
		{
			System.out.println("fd");
		}

		handleBinExpSubNode(question, obligations, left, ltype);
		handleBinExpSubNode(question, obligations, right , rtype);

		obligations.addAll(left.apply(mainVisitor, question));
		obligations.addAll(right.apply(mainVisitor, question));

		return obligations;

	}

	private void handleBinExpSubNode(IPOContextStack question,
			IProofObligationList obligations, PExp left, PType ltype)
			throws AnalysisException
	{
		PTypeAssistantTC pTA = aF.createPTypeAssistant();
		if (pTA.isUnion(ltype, null))
		{
			for (PType type : pTA.getUnion(ltype, null).getTypes()){
				if (!pTA.isNumeric(type, null)){
					TypeCompatibilityObligation sto = TypeCompatibilityObligation.newInstance(left, AstFactory.newARealNumericBasicType(left.getLocation()), ltype, question, aF);
					if (sto != null)
					{
						obligations.add(sto);
					}
				}
			}
		}
	}

	public IProofObligationList getCommonOrderedObligations(
			SNumericBinaryExp exp, IPOContextStack ctxt) throws AnalysisException
	{
		IProofObligationList obligations = new ProofObligationList();

		List<PType> lset = new LinkedList<>();
		List<PType> rset = new LinkedList<>();

		PTypeAssistantTC pta = aF.createPTypeAssistant();
		if (pta.isUnion(exp.getLeft().getType(), null))
		{
			lset.addAll(pta.getUnion(exp.getLeft().getType(), null).getTypes());
		} else
		{
			lset.add(exp.getLeft().getType());
		}

		if (pta.isUnion(exp.getRight().getType(), null))
		{
			rset.addAll(pta.getUnion(exp.getRight().getType(), null).getTypes());
		} else
		{
			rset.add(exp.getRight().getType());
		}

		// For each LHS type, if there is a RHS type that is compatible, we potentially
		// remember the type. If there is a RHS type that is incompatible, we note that
		// a PO is actually needed.

		boolean poNeeded = false;
		List<PType> poTypes = new LinkedList<>();

		for (PType lhs : lset)
		{
			if (lhs.apply(aF.getIsOrderedVisitor(), exp.getLocation()))
			{
				for (PType rhs : rset)
				{
					if (lhs.apply(aF.getIsOrderedVisitor(), exp.getLocation())
							&& aF.getTypeComparator().compatible(lhs, rhs))
					{
						poTypes.add(lhs);
					} else
					{
						poNeeded = true;
					}
				}
			} else
			{
				poNeeded = true;
			}
		}

		if (poNeeded && !poTypes.isEmpty())
		{
			obligations.add(new OrderedObligation(exp, poTypes, ctxt, aF));
		}

		return obligations;
	}



		@Override
	public IProofObligationList caseAGreaterEqualNumericBinaryExp(
			AGreaterEqualNumericBinaryExp node, IPOContextStack question)
			throws AnalysisException
	{
		IProofObligationList pol = getCommonOrderedObligations(node,question);
		pol.addAll(node.getLeft().apply(mainVisitor,question));
		pol.addAll(node.getRight().apply(mainVisitor,question));
		return pol;
	}

	@Override
	public IProofObligationList caseAGreaterNumericBinaryExp(
			AGreaterNumericBinaryExp node, IPOContextStack question)
			throws AnalysisException
	{
		IProofObligationList pol = getCommonOrderedObligations(node,question);
		pol.addAll(node.getLeft().apply(mainVisitor,question));
		pol.addAll(node.getRight().apply(mainVisitor,question));
		return pol;
	}

	@Override
	public IProofObligationList caseALessEqualNumericBinaryExp(
			ALessEqualNumericBinaryExp node, IPOContextStack question)
			throws AnalysisException
	{
		IProofObligationList pol = getCommonOrderedObligations(node,question);
		pol.addAll(node.getLeft().apply(mainVisitor,question));
		pol.addAll(node.getRight().apply(mainVisitor,question));
		return pol;
	}

	@Override
	public IProofObligationList caseALessNumericBinaryExp(
			ALessNumericBinaryExp node, IPOContextStack question)
			throws AnalysisException
	{
		IProofObligationList pol = getCommonOrderedObligations(node,question);
		pol.addAll(node.getLeft().apply(mainVisitor,question));
		pol.addAll(node.getRight().apply(mainVisitor,question));
		return pol;
	}

	@Override
	public IProofObligationList caseAModNumericBinaryExp(
			AModNumericBinaryExp node, IPOContextStack question)
			throws AnalysisException
	{
		return handleNumericBinaryExpression(node, question);
	}

	@Override
	public IProofObligationList caseAPlusNumericBinaryExp(
			APlusNumericBinaryExp node, IPOContextStack question)
			throws AnalysisException
	{
		return handleNumericBinaryExpression(node, question);
	}

	@Override
	public IProofObligationList caseARemNumericBinaryExp(
			ARemNumericBinaryExp node, IPOContextStack question)
			throws AnalysisException
	{
		return handleNumericBinaryExpression(node, question);
	}

	@Override
	public IProofObligationList caseASubtractNumericBinaryExp(
			ASubtractNumericBinaryExp node, IPOContextStack question)
			throws AnalysisException
	{
		return handleNumericBinaryExpression(node, question);
	}

	@Override
	public IProofObligationList caseATimesNumericBinaryExp(
			ATimesNumericBinaryExp node, IPOContextStack question)
			throws AnalysisException
	{
		return handleNumericBinaryExpression(node, question);
	}

	@Override
	public IProofObligationList caseAMapEnumMapExp(AMapEnumMapExp node,
			IPOContextStack question) throws AnalysisException
	{

		IProofObligationList obligations = new ProofObligationList();

		List<AMapletExp> members = node.getMembers();

		for (AMapletExp maplet : members)
		{
			obligations.addAll(maplet.apply(mainVisitor, question));
		}

		if (members.size() > 1)
		{
			obligations.add(new MapInjectivityEnum(node, question, aF));
		}

		return obligations;
	}

	@Override
	public IProofObligationList caseASeqCompSeqExp(ASeqCompSeqExp node,
			IPOContextStack question) throws AnalysisException
	{
		IProofObligationList obligations = new ProofObligationList();

		PExp first = node.getFirst();
		question.push(new POForAllPredicateContext(node, aF));
		obligations.addAll(first.apply(mainVisitor, question));
		question.pop();

		if (node.getSetBind() != null)
		{
			obligations.addAll(node.getSetBind().apply(rootVisitor, question));
		}
		else
		{
			obligations.addAll(node.getSeqBind().apply(rootVisitor, question));
		}

		PExp predicate = node.getPredicate();
		if (predicate != null)
		{
			question.push(new POForAllContext(node, aF));
			obligations.addAll(predicate.apply(mainVisitor, question));
			question.pop();
		}

		return obligations;
	}

	@Override
	public IProofObligationList caseASeqEnumSeqExp(ASeqEnumSeqExp node,
			IPOContextStack question) throws AnalysisException
	{

		IProofObligationList obligations = new ProofObligationList();

		for (PExp e : node.getMembers())
		{
			obligations.addAll(e.apply(mainVisitor, question));
		}

		return obligations;
	}

	@Override
	public IProofObligationList caseASetCompSetExp(ASetCompSetExp node,
			IPOContextStack question) throws AnalysisException

	{
		PExp first = node.getFirst();
		PExp predicate = node.getPredicate();

		IProofObligationList obligations = new ProofObligationList();
		question.push(new POForAllPredicateContext(node));
		obligations.addAll(first.apply(mainVisitor, question));
		question.pop();

		List<PMultipleBind> bindings = node.getBindings();

		boolean finiteTest = false;
		for (PMultipleBind b : bindings)
		{
			obligations.addAll(b.apply(rootVisitor, question));

			if (b instanceof ATypeMultipleBind)
			{
				finiteTest = true;
			}
		}

		if (finiteTest)
		{
			obligations.add(new org.overture.pog.obligation.FiniteSetObligation(node, node.getSetType(), question, aF));
		}

		if (predicate != null)
		{
			question.push(new POForAllContext(node));
			obligations.addAll(predicate.apply(mainVisitor, question));
			question.pop();
		}

		return obligations;
	}

	@Override
	public IProofObligationList caseASetEnumSetExp(ASetEnumSetExp node,
			IPOContextStack question) throws AnalysisException
	{
		IProofObligationList obligations = new ProofObligationList();

		for (PExp e : node.getMembers())
		{
			obligations.addAll(e.apply(mainVisitor, question));
		}

		return obligations;

	}

	@Override
	public IProofObligationList caseASetRangeSetExp(ASetRangeSetExp node,
			IPOContextStack question) throws AnalysisException
	{
		PExp last = node.getLast();
		PExp first = node.getFirst();
		IProofObligationList obligations = first.apply(mainVisitor, question);
		obligations.addAll(last.apply(mainVisitor, question));
		return obligations;

	}

	@Override
	public IProofObligationList defaultPExp(PExp node, IPOContextStack question)
	{
		return new ProofObligationList();
	}

	@Override
	public IProofObligationList createNewReturnValue(INode node,
			IPOContextStack question)
	{
		return new ProofObligationList();
	}

	@Override
	public IProofObligationList createNewReturnValue(Object node,
			IPOContextStack question)
	{
		return new ProofObligationList();
	}
	
	public ILexNameToken getPreName(PExp expression)
	{
		try
		{
			return expression.apply(aF.getPreNameFinder());
		} catch (AnalysisException e)
		{
			return null;
		}
	}
}
