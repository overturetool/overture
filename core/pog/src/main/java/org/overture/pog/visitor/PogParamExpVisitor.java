package org.overture.pog.visitor;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.*;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.lex.LexIdentifierToken;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.patterns.AIgnorePattern;
import org.overture.ast.patterns.ATypeBind;
import org.overture.ast.patterns.ATypeMultipleBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SMapType;
import org.overture.pog.assistant.ACaseAlternativeAssistantPOG;
import org.overture.pog.assistant.PDefinitionAssistantPOG;
import org.overture.pog.obligation.CasesExhaustiveObligation;
import org.overture.pog.obligation.FiniteMapObligation;
import org.overture.pog.obligation.FuncComposeObligation;
import org.overture.pog.obligation.FunctionApplyObligation;
import org.overture.pog.obligation.LetBeExistsObligation;
import org.overture.pog.obligation.MapApplyObligation;
import org.overture.pog.obligation.MapCompatibleObligation;
import org.overture.pog.obligation.MapComposeObligation;
import org.overture.pog.obligation.MapIterationObligation;
import org.overture.pog.obligation.MapSeqOfCompatibleObligation;
import org.overture.pog.obligation.MapSetOfCompatibleObligation;
import org.overture.pog.obligation.NonEmptySeqObligation;
import org.overture.pog.obligation.NonZeroObligation;
import org.overture.pog.obligation.POContextStack;
import org.overture.pog.obligation.PODefContext;
import org.overture.pog.obligation.POForAllContext;
import org.overture.pog.obligation.POForAllPredicateContext;
import org.overture.pog.obligation.POImpliesContext;
import org.overture.pog.obligation.POLetDefContext;
import org.overture.pog.obligation.PONameContext;
import org.overture.pog.obligation.PONotImpliesContext;
import org.overture.pog.obligation.ProofObligation;
import org.overture.pog.obligation.ProofObligationList;
import org.overture.pog.obligation.RecursiveObligation;
import org.overture.pog.obligation.SeqApplyObligation;
import org.overture.pog.obligation.SubTypeObligation;
import org.overture.pog.obligation.TupleSelectObligation;
import org.overture.pog.obligation.UniqueExistenceObligation;
import org.overture.typechecker.TypeComparator;
import org.overture.typechecker.assistant.definition.PDefinitionAssistantTC;
import org.overture.typechecker.assistant.expression.PExpAssistantTC;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;

public class PogParamExpVisitor<Q extends POContextStack, A extends ProofObligationList>
	extends QuestionAnswerAdaptor<POContextStack,ProofObligationList> {

    /**
	 * 
	 */
    private static final long serialVersionUID = 7899640121529246521L;
    final private QuestionAnswerAdaptor<POContextStack, ProofObligationList> rootVisitor;
    final private QuestionAnswerAdaptor<POContextStack, ProofObligationList> mainVisitor;
    
    // Added a mainVisitor hack to enable use from the compassVisitors -ldc
    
    public PogParamExpVisitor(
	    QuestionAnswerAdaptor<POContextStack, ProofObligationList> parentVisitor, 
	    QuestionAnswerAdaptor<POContextStack, ProofObligationList> mainVisitor) {
	this.rootVisitor=parentVisitor;
	this.mainVisitor=mainVisitor;
	}

    public PogParamExpVisitor(
	    QuestionAnswerAdaptor<POContextStack, ProofObligationList> parentVisitor) {
	this.rootVisitor=parentVisitor;
	this.mainVisitor=this;
	}
    
    

    @Override
    // RWL see [1] pg. 57: 6.12 Apply Expressions
    public ProofObligationList caseAApplyExp(AApplyExp node,
	    POContextStack question) throws AnalysisException {

	ProofObligationList obligations = new ProofObligationList();

	PExp root = node.getRoot();

	// is it a map?

	PType type = root.getType();
	if (PTypeAssistantTC.isMap(type)) {
	    SMapType mapType = PTypeAssistantTC.getMap(type);
	    obligations.add(new MapApplyObligation(node.getRoot(), node
		    .getArgs().get(0), question));
	    PType aType = question.checkType(node.getArgs().get(0), node
		    .getArgtypes().get(0));

	    if (!TypeComparator.isSubType(aType, mapType.getFrom())) {
		obligations.add(new SubTypeObligation(node.getArgs().get(0),
			mapType.getFrom(), aType, question));
	    }
	}

	if (!PTypeAssistantTC.isUnknown(type)
		&& PTypeAssistantTC.isFunction(type)) {
	    AFunctionType funcType = PTypeAssistantTC.getFunction(type);
	    String prename = PExpAssistantTC.getPreName(root);
	    if (prename == null || !prename.equals("")) {
		obligations.add(new FunctionApplyObligation(node.getRoot(),
			node.getArgs(), prename, question));
	    }

	    int i = 0;
	    List<PType> argTypes = node.getArgtypes();
	    List<PExp> argList = node.getArgs();
	    for (PType argType : argTypes) {
		argType = question.checkType(argList.get(i), argType);
		PType pt = funcType.getParameters().get(i);

		if (!TypeComparator.isSubType(argType, pt))
		    obligations.add(new SubTypeObligation(argList.get(i), pt,
			    argType, question));
		i++;
	    }

	    PDefinition recursive = node.getRecursive();
	    if (recursive != null) {
		if (recursive instanceof AExplicitFunctionDefinition) {
		    AExplicitFunctionDefinition def = (AExplicitFunctionDefinition) recursive;
		    if (def.getMeasure() != null) {
			obligations.add(new RecursiveObligation(def, node,
				question));
		    }
		} else if (recursive instanceof AImplicitFunctionDefinition) {
		    AImplicitFunctionDefinition def = (AImplicitFunctionDefinition) recursive;
		    if (def.getMeasure() != null) {
			obligations.add(new RecursiveObligation(def, node,
				question));
		    }

		}
	    }
	}

	if (PTypeAssistantTC.isSeq(type)) {
	    obligations.add(new SeqApplyObligation(node.getRoot(), node
		    .getArgs().get(0), question));
	}

	obligations.addAll(node.getRoot().apply(mainVisitor, question));

	for (PExp arg : node.getArgs()) {
	    obligations.addAll(arg.apply(mainVisitor, question));
	}

	return obligations;
    }

    @Override
    // see [1] pg. 179 unary expressions
    public ProofObligationList caseAHeadUnaryExp(AHeadUnaryExp node,
	    POContextStack question) throws AnalysisException {

	ProofObligationList obligations = defaultSUnaryExp(node, question);
	PExp exp = node.getExp();

	// TODO RWL This is a hack. The new ast LexNameToken's toString method
	// includes the module e.g. like Test`b for variables
	// which the old one did not. Hence proof obligations with variable
	// names are different as "Test`b" is just b with the old proof
	// obligations generator.
	PExp fake = exp.clone();
	if (exp instanceof AVariableExp) {
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
	    POContextStack question) throws AnalysisException {
	ProofObligationList obligations = new ProofObligationList();

	int count = 0;
	boolean hasIgnore = false;

	// handle each case
	for (ACaseAlternative alt : node.getCases()) {

	    if (alt.getPattern() instanceof AIgnorePattern)
		hasIgnore = true;

	    obligations.addAll(ACaseAlternativeAssistantPOG
		    .getProofObligations(alt, rootVisitor, question, node
			    .getExpression().getType()));
	    /*
	     * obligations.addAll(alt.apply(rootVisitor, question));
	     */
	    count++;
	}

	if (node.getOthers() != null) {
	    obligations.addAll(node.getOthers().apply(mainVisitor, question));
	}

	for (int i = 0; i < count; i++)
	    question.pop();

	if (node.getOthers() == null && !hasIgnore)
	    obligations.add(new CasesExhaustiveObligation(node, question));

	return obligations;
    }

    @Override
    public ProofObligationList caseAMapCompMapExp(AMapCompMapExp node,
	    POContextStack question) throws AnalysisException {
	ProofObligationList obligations = new ProofObligationList();

	obligations.add(new MapSetOfCompatibleObligation(node, question));

	question.push(new POForAllPredicateContext(node));
	obligations.addAll(node.getFirst().apply(mainVisitor, question));
	question.pop();

	boolean finiteTest = false;

	for (PMultipleBind mb : node.getBindings()) {
	    obligations.addAll(mb.apply(rootVisitor, question));
	    if (mb instanceof ATypeMultipleBind)
		finiteTest = true;
	}

	if (finiteTest)
	    obligations.add(new FiniteMapObligation(node, node.getType(),
		    question));

	PExp predicate = node.getPredicate();
	if (predicate != null) {
	    question.push(new POForAllContext(node));
	    obligations.addAll(predicate.apply(mainVisitor, question));
	    question.pop();
	}

	return obligations;
    }

    @Override
    // RWL see [1] pg. 179 A.5.4 Unary Expressions
    public ProofObligationList defaultSUnaryExp(SUnaryExp node,
	    POContextStack question) throws AnalysisException {

	return node.getExp().apply(mainVisitor, question);
    }

    @Override
    // RWL
    public ProofObligationList defaultSBinaryExp(SBinaryExp node,
	    POContextStack question) throws AnalysisException {
	ProofObligationList obligations = new ProofObligationList();
	obligations.addAll(node.getLeft().apply(mainVisitor, question));
	obligations.addAll(node.getRight().apply(mainVisitor, question));
	return obligations;
    }

    @Override
    public ProofObligationList caseABooleanConstExp(ABooleanConstExp node,
	    POContextStack question) {

	return new ProofObligationList();
    }

    @Override
    public ProofObligationList caseACharLiteralExp(ACharLiteralExp node,
	    POContextStack question) {
	return new ProofObligationList();
    }

    @Override
    public ProofObligationList caseAElseIfExp(AElseIfExp node,
	    POContextStack question) throws AnalysisException {

	ProofObligationList obligations = new ProofObligationList();
	question.push(new POImpliesContext(node.getElseIf()));
	obligations.addAll(node.getThen().apply(mainVisitor, question));
	question.pop();

	return obligations;
    }

    @Override
    public ProofObligationList caseAExists1Exp(AExists1Exp node,
	    POContextStack question) throws AnalysisException {
	ProofObligationList obligations = new ProofObligationList();
	question.push(new POForAllContext(node));
	obligations.addAll(node.getPredicate().apply(mainVisitor, question));
	question.pop();
	return obligations;
    }

    @Override
    public ProofObligationList caseAExistsExp(AExistsExp node,
	    POContextStack question) throws AnalysisException {
	ProofObligationList obligations = new ProofObligationList();

	for (PMultipleBind mb : node.getBindList()) {
	    obligations.addAll(mb.apply(rootVisitor, question));
	}

	question.push(new POForAllContext(node));
	obligations.addAll(node.getPredicate().apply(mainVisitor, question));
	question.pop();

	return obligations;
    }

    @Override
    public ProofObligationList caseAFieldExp(AFieldExp node,
	    POContextStack question) throws AnalysisException {
	return node.getObject().apply(mainVisitor, question);
    }

    @Override
    public ProofObligationList caseAFieldNumberExp(AFieldNumberExp node,
	    POContextStack question) throws AnalysisException {

	ProofObligationList obligations = node.getTuple().apply(mainVisitor, question);

	PType type = node.getType();

	if (type instanceof AUnionType) {
	    AUnionType utype = (AUnionType) type;
	    for (PType t : utype.getTypes()) {
		if (t instanceof AProductType) {
		    AProductType aprodType = (AProductType) t;
		    if (aprodType.getTypes().size() < node.getField().value) {
			obligations.add(new TupleSelectObligation(node,
				aprodType, question));
		    }
		}
	    }
	}

	return obligations;
    }

    @Override
    public ProofObligationList caseAForAllExp(AForAllExp node,
	    POContextStack question) throws AnalysisException {

	ProofObligationList obligations = new ProofObligationList();

	for (PMultipleBind mb : node.getBindList()) {
	    obligations.addAll(mb.apply(rootVisitor, question));
	}

	question.push(new POForAllContext(node));
	obligations.addAll(node.getPredicate().apply(mainVisitor, question));
	question.pop();
	return obligations;
    }

    @Override
    public ProofObligationList caseAFuncInstatiationExp(
	    AFuncInstatiationExp node, POContextStack question)
	    throws AnalysisException {
	return node.getFunction().apply(mainVisitor, question);
    }

    @Override
    // RWL
    public ProofObligationList caseAHistoryExp(AHistoryExp node,
	    POContextStack question) {
	// No getProofObligationMethod found on the HistoryExpression class of
	// VDMJ assuming we have the empty list.
	return new ProofObligationList();
    }

    @Override
    public ProofObligationList caseAIfExp(AIfExp node, POContextStack question)
	    throws AnalysisException {
	ProofObligationList obligations = node.getTest().apply(mainVisitor, question);

	question.push(new POImpliesContext(node.getTest()));
	obligations.addAll(node.getThen().apply(mainVisitor, question));
	question.pop();

	question.push(new PONotImpliesContext(node.getTest()));

	for (AElseIfExp e : node.getElseList()) {
	    obligations.addAll(e.apply(mainVisitor, question));
	    question.push(new PONotImpliesContext(e.getElseIf()));

	}

	int sizeBefore = question.size();
	obligations.addAll(node.getElse().apply(mainVisitor, question));
	assert sizeBefore <= question.size();

	for (int i = 0; i < node.getElseList().size(); i++)
	    question.pop();

	question.pop();
	return obligations;
    }

    @Override
    public ProofObligationList caseAIntLiteralExp(AIntLiteralExp node,
	    POContextStack question) {

	return new ProofObligationList();
    }

    @Override
    public ProofObligationList caseAIotaExp(AIotaExp node,
	    POContextStack question) throws AnalysisException {
	ProofObligationList obligations = node.getBind().apply(rootVisitor,
		question);
	obligations.add(new UniqueExistenceObligation(node, question));

	question.push(new POForAllContext(node));
	obligations.addAll(node.getPredicate().apply(mainVisitor, question));
	question.pop();
	return obligations;
    }

    @Override
    public ProofObligationList caseAIsExp(AIsExp node, POContextStack question)
	    throws AnalysisException {
	PDefinition typeDef = node.getTypedef();
	PType basicType = node.getBasicType();
	if (typeDef != null) {
	    question.noteType(node.getTest(), typeDef.getType());
	} else if (basicType != null) {
	    question.noteType(node.getTest(), basicType);
	}
	return node.getTest().apply(mainVisitor, question);
    }

    @Override
    // RWL See [1] pg. 64-65
    public ProofObligationList caseAIsOfBaseClassExp(AIsOfBaseClassExp node,
	    POContextStack question) throws AnalysisException {
	return node.getExp().apply(mainVisitor, question);
    }

    @Override
    // RWL See [1] pg. 64-65
    public ProofObligationList caseAIsOfClassExp(AIsOfClassExp node,
	    POContextStack question) throws AnalysisException {

	question.noteType(node.getExp(), node.getClassType());

	return node.getExp().apply(mainVisitor, question);
    }

    @Override
    // RWL See [1] pg. 62
    public ProofObligationList caseALambdaExp(ALambdaExp node,
	    POContextStack question) throws AnalysisException {

	ProofObligationList obligations = new ProofObligationList();

	for (ATypeBind tb : node.getBindList()) {
	    obligations.addAll(tb.apply(rootVisitor, question));
	}

	question.push(new POForAllContext(node));
	obligations.addAll(node.getExpression().apply(mainVisitor, question));
	question.pop();

	return obligations;
    }

    @Override
    // RWL See [1] pg.95
    public ProofObligationList caseALetBeStExp(ALetBeStExp node,
	    POContextStack question) throws AnalysisException {
	ProofObligationList obligations = new ProofObligationList();
	obligations.add(new LetBeExistsObligation(node, question));
	obligations.addAll(node.getBind().apply(rootVisitor, question));

	PExp suchThat = node.getSuchThat();
	if (suchThat != null) {
	    question.push(new POForAllContext(node));
	    obligations.addAll(suchThat.apply(mainVisitor, question));
	    question.pop();
	}

	question.push(new POForAllPredicateContext(node));
	obligations.addAll(node.getValue().apply(mainVisitor, question));
	question.pop();

	return obligations;
    }

    @Override
    // RWL see [1] pg.
    public ProofObligationList caseALetDefExp(ALetDefExp node,
	    POContextStack question) throws AnalysisException {
	ProofObligationList obligations = new ProofObligationList();

	for (PDefinition def : node.getLocalDefs()) {
	    question.push(new PONameContext(PDefinitionAssistantTC
		    .getVariableNames(def)));
	    obligations.addAll(def.apply(rootVisitor, question));
	    question.pop();
	}

	question.push(new POLetDefContext(node));
	obligations.addAll(node.getExpression().apply(mainVisitor, question));
	question.pop();

	return obligations;
    }

    @Override
    public ProofObligationList caseADefExp(ADefExp node, POContextStack question)
	    throws AnalysisException {
	ProofObligationList obligations = PDefinitionAssistantPOG
		.getProofObligations(node.getLocalDefs(), rootVisitor, question);

	// RWL Question, are we going
	question.push(new PODefContext(node));
	obligations.addAll(node.getExpression().apply(mainVisitor, question));
	question.pop();

	return obligations;
    }

    @Override
    public ProofObligationList defaultSMapExp(SMapExp node,
	    POContextStack question) {

	return new ProofObligationList();
    }

    @Override
    public ProofObligationList caseAMapletExp(AMapletExp node,
	    POContextStack question) throws AnalysisException {

	ProofObligationList obligations = node.getLeft().apply(mainVisitor, question);
	obligations.addAll(node.getRight().apply(mainVisitor, question));
	return obligations;
    }

    @Override
    public ProofObligationList caseAMkBasicExp(AMkBasicExp node,
	    POContextStack question) throws AnalysisException {
	return node.getArg().apply(mainVisitor, question);
    }

    @Override
    public ProofObligationList caseAMkTypeExp(AMkTypeExp node,
	    POContextStack question) throws AnalysisException {

	ProofObligationList obligations = new ProofObligationList();
	@SuppressWarnings("unchecked")
	Queue<PExp> args = (Queue<PExp>) node.getArgs().clone();
	for (PExp arg : args)
	    obligations.addAll(arg.apply(mainVisitor, question));

	@SuppressWarnings("unchecked")
	Queue<PType> argTypes = (Queue<PType>) node.getArgTypes().clone();

	ARecordInvariantType recordType = node.getRecordType();
	for (AFieldField f : recordType.getFields()) {
	    PType aType = argTypes.poll();
	    PExp aExp = args.poll();

	    if (!TypeComparator.isSubType(question.checkType(aExp, aType),
		    f.getType()))
		obligations.add(new SubTypeObligation(aExp, f.getType(), aType,
			question));
	}

	PDefinition invDef = recordType.getInvDef();
	if (invDef != null)
	    obligations.add(new SubTypeObligation(node, recordType, recordType,
		    question));

	return obligations;
    }

    private static AFieldField findField(ARecordInvariantType ty,
	    LexIdentifierToken id) {

	List<AFieldField> fields = ty.getFields();
	for (AFieldField f : fields)
	    if (f.getTag().equals(id.name))
		return f;

	return null;
    }

    @Override
    // RWL See [1] pg. 56
    public ProofObligationList caseAMuExp(AMuExp node, POContextStack question)
	    throws AnalysisException {
	ProofObligationList obligations = node.getRecord().apply(rootVisitor,
		question);
	Queue<ARecordModifier> modifiers = node.getModifiers();
	ARecordInvariantType recordType = node.getRecordType();
	LinkedList<PType> mTypes = node.getModTypes();

	int i = 0;
	for (ARecordModifier mod : modifiers) {
	    obligations.addAll(mod.getValue().apply(mainVisitor, question));
	    AFieldField f = findField(recordType, mod.getTag());
	    PType mType = mTypes.get(i++);
	    if (f != null)
		if (!TypeComparator.isSubType(mType, f.getType()))
		    obligations.add(new SubTypeObligation(mod.getValue(), f
			    .getType(), mType, question));

	}

	return obligations;
    }

    @Override
    public ProofObligationList caseANewExp(ANewExp node, POContextStack question)
	    throws AnalysisException {

	ProofObligationList obligations = new ProofObligationList();

	for (PExp exp : node.getArgs())
	    obligations.addAll(exp.apply(mainVisitor, question));

	return obligations;
    }

    @Override
    public ProofObligationList caseANilExp(ANilExp node, POContextStack question) {
	return new ProofObligationList();
    }

    @Override
    public ProofObligationList caseANotYetSpecifiedExp(
	    ANotYetSpecifiedExp node, POContextStack question) {
	return new ProofObligationList();
    }

    @Override
    public ProofObligationList caseAPostOpExp(APostOpExp node,
	    POContextStack question) {
	return new ProofObligationList();
    }

    @Override
    public ProofObligationList caseAPreExp(APreExp node, POContextStack question) {
	return new ProofObligationList();
    }

    @Override
    public ProofObligationList caseAPreOpExp(APreOpExp node,
	    POContextStack question) {
	return new ProofObligationList();
    }

    @Override
    public ProofObligationList caseAQuoteLiteralExp(AQuoteLiteralExp node,
	    POContextStack question) {
	return new ProofObligationList();
    }

    @Override
    public ProofObligationList caseARealLiteralExp(ARealLiteralExp node,
	    POContextStack question) {
	return new ProofObligationList();
    }

    @Override
    public ProofObligationList caseASameBaseClassExp(ASameBaseClassExp node,
	    POContextStack question) throws AnalysisException {
	ProofObligationList obligations = new ProofObligationList();

	obligations.addAll(node.getLeft().apply(mainVisitor, question));
	obligations.addAll(node.getRight().apply(mainVisitor, question));

	return obligations;
    }

    @Override
    public ProofObligationList caseASameClassExp(ASameClassExp node,
	    POContextStack question) throws AnalysisException {
	ProofObligationList list = node.getLeft().apply(mainVisitor, question);
	list.addAll(node.getRight().apply(mainVisitor, question));
	return list;
    }

    @Override
    public ProofObligationList caseASelfExp(ASelfExp node,
	    POContextStack question) {
	return new ProofObligationList();
    }

    @Override
    public ProofObligationList defaultSSeqExp(SSeqExp node,
	    POContextStack question) {
	return new ProofObligationList();
    }

    @Override
    public ProofObligationList defaultSSetExp(SSetExp node,
	    POContextStack question) {
	return new ProofObligationList();
    }

    @Override
    public ProofObligationList caseAStateInitExp(AStateInitExp node,
	    POContextStack question) {
	return new ProofObligationList();
    }

    @Override
    public ProofObligationList caseAStringLiteralExp(AStringLiteralExp node,
	    POContextStack question) {
	return new ProofObligationList();
    }

    @Override
    public ProofObligationList caseASubclassResponsibilityExp(
	    ASubclassResponsibilityExp node, POContextStack question) {
	return new ProofObligationList();
    }

    @Override
    public ProofObligationList caseASubseqExp(ASubseqExp node,
	    POContextStack question) throws AnalysisException {
	ProofObligationList list = node.getSeq().apply(mainVisitor, question);
	list.addAll(node.getFrom().apply(mainVisitor, question));
	list.addAll(node.getTo().apply(mainVisitor, question));
	return list;
    }

    @Override
    public ProofObligationList caseAThreadIdExp(AThreadIdExp node,
	    POContextStack question) {
	return new ProofObligationList();
    }

    @Override
    public ProofObligationList caseATimeExp(ATimeExp node,
	    POContextStack question) {
	return new ProofObligationList();
    }

    @Override
    public ProofObligationList caseATupleExp(ATupleExp node,
	    POContextStack question) throws AnalysisException {
	ProofObligationList obligations = new ProofObligationList();
	for (PExp exp : node.getArgs())
	    obligations.addAll(exp.apply(mainVisitor, question));
	return obligations;
    }

    @Override
    public ProofObligationList caseAUndefinedExp(AUndefinedExp node,
	    POContextStack question) {
	return new ProofObligationList();
    }

    @Override
    public ProofObligationList caseAAbsoluteUnaryExp(AAbsoluteUnaryExp node,
	    POContextStack question) throws AnalysisException {

	return node.getExp().apply(mainVisitor, question);
    }

    @Override
    public ProofObligationList caseACardinalityUnaryExp(
	    ACardinalityUnaryExp node, POContextStack question)
	    throws AnalysisException {

	return node.getExp().apply(mainVisitor, question);
    }

    @Override
    public ProofObligationList caseADistConcatUnaryExp(
	    ADistConcatUnaryExp node, POContextStack question)
	    throws AnalysisException {
	return node.getExp().apply(mainVisitor, question);
    }

    @Override
    public ProofObligationList caseADistIntersectUnaryExp(
	    ADistIntersectUnaryExp node, POContextStack question)
	    throws AnalysisException {
	ProofObligationList obligations = node.getExp().apply(mainVisitor, question);
	obligations.add(new org.overture.pog.obligation.NonEmptySetObligation(
		node.getExp(), question));
	return obligations;
    }

    @Override
    public ProofObligationList caseADistMergeUnaryExp(ADistMergeUnaryExp node,
	    POContextStack question) {
	ProofObligationList obligations = new ProofObligationList();
	obligations.add(new MapSetOfCompatibleObligation(node.getExp(),
		question));
	return obligations;
    }

    @Override
    public ProofObligationList caseADistUnionUnaryExp(ADistUnionUnaryExp node,
	    POContextStack question) throws AnalysisException {
	return node.getExp().apply(mainVisitor, question);
    }

    @Override
    public ProofObligationList caseAElementsUnaryExp(AElementsUnaryExp node,
	    POContextStack question) throws AnalysisException {

	return node.getExp().apply(mainVisitor, question);
    }

    @Override
    public ProofObligationList caseAFloorUnaryExp(AFloorUnaryExp node,
	    POContextStack question) throws AnalysisException {

	return node.getExp().apply(mainVisitor, question);
    }

    @Override
    public ProofObligationList caseAIndicesUnaryExp(AIndicesUnaryExp node,
	    POContextStack question) throws AnalysisException {
	return node.getExp().apply(mainVisitor, question);
    }

    @Override
    public ProofObligationList caseALenUnaryExp(ALenUnaryExp node,
	    POContextStack question) throws AnalysisException {

	return node.getExp().apply(mainVisitor, question);
    }

    @Override
    public ProofObligationList caseAMapDomainUnaryExp(AMapDomainUnaryExp node,
	    POContextStack question) throws AnalysisException {
	return node.getExp().apply(mainVisitor, question);
    }

    @Override
    public ProofObligationList caseAMapInverseUnaryExp(
	    AMapInverseUnaryExp node, POContextStack question)
	    throws AnalysisException {
	ProofObligationList obligations = node.getExp().apply(mainVisitor, question);
	if (!node.getMapType().getEmpty()) {
	    obligations
		    .add(new org.overture.pog.obligation.InvariantObligation(
			    node, question));
	}
	return obligations;
    }

    @Override
    public ProofObligationList caseAMapRangeUnaryExp(AMapRangeUnaryExp node,
	    POContextStack question) throws AnalysisException {
	return node.getExp().apply(mainVisitor, question);
    }

    @Override
    public ProofObligationList caseANotUnaryExp(ANotUnaryExp node,
	    POContextStack question) throws AnalysisException {
	return node.getExp().apply(mainVisitor, question);
    }

    @Override
    public ProofObligationList caseAPowerSetUnaryExp(APowerSetUnaryExp node,
	    POContextStack question) throws AnalysisException {
	return node.getExp().apply(mainVisitor, question);
    }

    @Override
    public ProofObligationList caseAReverseUnaryExp(AReverseUnaryExp node,
	    POContextStack question) throws AnalysisException {
	return node.getExp().apply(mainVisitor, question);
    }

    @Override
    public ProofObligationList caseATailUnaryExp(ATailUnaryExp node,
	    POContextStack question) throws AnalysisException {
	ProofObligationList obligations = node.getExp().apply(mainVisitor, question);
	obligations.add(new NonEmptySeqObligation(node.getExp(), question));

	return obligations;
    }

    @Override
    public ProofObligationList caseAUnaryMinusUnaryExp(
	    AUnaryMinusUnaryExp node, POContextStack question)
	    throws AnalysisException {

	return node.getExp().apply(mainVisitor, question);
    }

    @Override
    public ProofObligationList caseAUnaryPlusUnaryExp(AUnaryPlusUnaryExp node,
	    POContextStack question) throws AnalysisException {
	return node.getExp().apply(mainVisitor, question);
    }

    @Override
    public ProofObligationList defaultSBooleanBinaryExp(SBooleanBinaryExp node,
	    POContextStack question) {
	ProofObligationList obligations = new ProofObligationList();
	PExp lExp = node.getLeft();
	PExp rExp = node.getRight();

	PType lType = lExp.getType();
	PType rType = rExp.getType();
	if (lType instanceof AUnionType) {
	    obligations
		    .add(new SubTypeObligation(lExp, AstFactory
			    .newABooleanBasicType(lExp.getLocation()), lType,
			    question));
	}

	if (rType instanceof AUnionType) {

	    obligations
		    .add(new SubTypeObligation(rExp, AstFactory
			    .newABooleanBasicType(rExp.getLocation()), rType,
			    question));
	}
	return obligations;
    }

    @Override
    public ProofObligationList caseACompBinaryExp(ACompBinaryExp node,
	    POContextStack question) {

	ProofObligationList obligations = new ProofObligationList();
	PExp lExp = node.getLeft();
	PType lType = lExp.getType();
	PExp rExp = node.getRight();

	if (PTypeAssistantTC.isFunction(lType)) {
	    String pref1 = PExpAssistantTC.getPreName(lExp);
	    String pref2 = PExpAssistantTC.getPreName(rExp);

	    if (pref1 == null || !pref1.equals(""))
		obligations.add(new FuncComposeObligation(node, pref1, pref2,
			question));
	}

	if (PTypeAssistantTC.isMap(lType)) {
	    obligations.add(new MapComposeObligation(node, question));
	}

	return obligations;
    }

    final static int LEFT = 0;
    final static int RIGHT = 1;

    private <T> PExp[] getLeftRight(T node) {
	PExp[] res = new PExp[2];
	try {
	    Class<?> clz = node.getClass();
	    Method getLeft = clz.getMethod("getLeft", new Class<?>[] {});
	    Method getRight = clz.getMethod("getRight", new Class<?>[] {});
	    res[LEFT] = (PExp) getLeft.invoke(node, new Object[0]);
	    res[RIGHT] = (PExp) getRight.invoke(node, new Object[0]);

	} catch (Exception k) {
	    throw new RuntimeException(k);
	}
	return res;
    }

    private <T> ProofObligationList handleBinaryExpression(T node,
	    POContextStack question) throws AnalysisException {

	if (node == null)
	    return new ProofObligationList();

	PExp[] leftRight = getLeftRight(node);
	PExp left = leftRight[LEFT];
	PExp right = leftRight[RIGHT];

	ProofObligationList obligations = new ProofObligationList();
	obligations.addAll(left.apply(mainVisitor, question));
	obligations.addAll(right.apply(mainVisitor, question));
	return obligations;
    }

    @Override
    public ProofObligationList caseADomainResByBinaryExp(
	    ADomainResByBinaryExp node, POContextStack question)
	    throws AnalysisException {
	return handleBinaryExpression(node, question);
    }

    @Override
    public ProofObligationList caseADomainResToBinaryExp(
	    ADomainResToBinaryExp node, POContextStack question)
	    throws AnalysisException {
	return handleBinaryExpression(node, question);
    }

    @Override
    public ProofObligationList caseAInSetBinaryExp(AInSetBinaryExp node,
	    POContextStack question) throws AnalysisException {
	return handleBinaryExpression(node, question);
    }

    @Override
    public ProofObligationList caseAMapUnionBinaryExp(AMapUnionBinaryExp node,
	    POContextStack question) throws AnalysisException {
	ProofObligationList obligations = handleBinaryExpression(node, question);
	obligations.add(new MapCompatibleObligation(node.getLeft(), node
		.getRight(), question));
	return obligations;
    }

    @Override
    public ProofObligationList caseANotEqualBinaryExp(ANotEqualBinaryExp node,
	    POContextStack question) throws AnalysisException {
	return handleBinaryExpression(node, question);
    }

    @Override
    public ProofObligationList caseANotInSetBinaryExp(ANotInSetBinaryExp node,
	    POContextStack question) throws AnalysisException {
	return handleBinaryExpression(node, question);
    }

    @Override
    public ProofObligationList defaultSNumericBinaryExp(SNumericBinaryExp node,
	    POContextStack question) throws AnalysisException {
	ProofObligationList obligations = new ProofObligationList();

	PExp left = node.getLeft();
	PExp right = node.getRight();
	PType lType = left.getType();
	PType rType = right.getType();

	if (lType instanceof AUnionType) {

	    obligations.add(new SubTypeObligation(left, AstFactory
		    .newARealNumericBasicType(right.getLocation()), lType,
		    question));
	}

	if (rType instanceof AUnionType) {
	    obligations.add(new SubTypeObligation(right, AstFactory
		    .newARealNumericBasicType(right.getLocation()), rType,
		    question));
	}

	obligations.addAll(left.apply(mainVisitor, question));
	obligations.addAll(right.apply(mainVisitor, question));
	return obligations;
    }

    @Override
    public ProofObligationList caseAPlusPlusBinaryExp(APlusPlusBinaryExp node,
	    POContextStack question) throws AnalysisException {

	ProofObligationList obligations = handleBinaryExpression(node, question);
	PType lType = node.getLeft().getType();

	if (PTypeAssistantTC.isSeq(lType)) {
	    obligations
		    .add(new org.overture.pog.obligation.SeqModificationObligation(
			    node, question));
	}

	return obligations;
    }

    @Override
    public ProofObligationList caseAProperSubsetBinaryExp(
	    AProperSubsetBinaryExp node, POContextStack question)
	    throws AnalysisException {
	return handleBinaryExpression(node, question);
    }

    @Override
    public ProofObligationList caseARangeResByBinaryExp(
	    ARangeResByBinaryExp node, POContextStack question)
	    throws AnalysisException {

	return handleBinaryExpression(node, question);
    }

    @Override
    public ProofObligationList caseARangeResToBinaryExp(
	    ARangeResToBinaryExp node, POContextStack question)
	    throws AnalysisException {
	return super.caseARangeResToBinaryExp(node, question);
    }

    @Override
    public ProofObligationList caseASeqConcatBinaryExp(
	    ASeqConcatBinaryExp node, POContextStack question)
	    throws AnalysisException {
	return handleBinaryExpression(node, question);
    }

    @Override
    public ProofObligationList caseASetDifferenceBinaryExp(
	    ASetDifferenceBinaryExp node, POContextStack question)
	    throws AnalysisException {
	return handleBinaryExpression(node, question);
    }

    @Override
    public ProofObligationList caseASetIntersectBinaryExp(
	    ASetIntersectBinaryExp node, POContextStack question)
	    throws AnalysisException {
	return handleBinaryExpression(node, question);
    }

    @Override
    public ProofObligationList caseASetUnionBinaryExp(ASetUnionBinaryExp node,
	    POContextStack question) throws AnalysisException {
	return handleBinaryExpression(node, question);
    }

    @Override
    public ProofObligationList caseAStarStarBinaryExp(AStarStarBinaryExp node,
	    POContextStack question) {
	ProofObligationList obligations = new ProofObligationList();

	PExp lExp = node.getLeft();
	PType lType = lExp.getType();

	if (PTypeAssistantTC.isFunction(lType)) {
	    String preName = PExpAssistantTC.getPreName(lExp);
	    if (preName == null || !preName.equals("")) {
		obligations
			.add(new org.overture.pog.obligation.FuncIterationObligation(
				node, preName, question));
	    }
	}

	if (PTypeAssistantTC.isMap(lType)) {
	    obligations.add(new MapIterationObligation(node, question));
	}

	return obligations;
    }

    @Override
    public ProofObligationList caseASubsetBinaryExp(ASubsetBinaryExp node,
	    POContextStack question) throws AnalysisException {
	return handleBinaryExpression(node, question);
    }

    @Override
    public ProofObligationList caseAAndBooleanBinaryExp(
	    AAndBooleanBinaryExp node, POContextStack question)
	    throws AnalysisException {
	ProofObligationList obligations = new ProofObligationList();

	PExp lExp = node.getLeft();
	PType lType = lExp.getType();
	PExp rExp = node.getRight();
	PType rType = rExp.getType();

	if (PTypeAssistantTC.isUnion(lType)) {

	    obligations
		    .add(new SubTypeObligation(lExp, AstFactory
			    .newABooleanBasicType(lExp.getLocation()), lType,
			    question));
	}

	if (PTypeAssistantTC.isUnion(rType)) {
	    question.push(new POImpliesContext(lExp));
	    obligations
		    .add(new SubTypeObligation(rExp, AstFactory
			    .newABooleanBasicType(rExp.getLocation()), rType,
			    question));
	    question.pop();
	}

	obligations.addAll(lExp.apply(mainVisitor, question));

	question.push(new POImpliesContext(lExp));
	obligations.addAll(rExp.apply(mainVisitor, question));
	question.pop();

	return obligations;
    }

    private <T> ProofObligationList handleBinaryBooleanExp(T node,
	    POContextStack question) throws AnalysisException {
	ProofObligationList obligations = new ProofObligationList();

	PExp[] leftRight = getLeftRight(node);
	PExp lExp = leftRight[LEFT];
	PType lType = lExp.getType();
	PExp rExp = leftRight[RIGHT];
	PType rType = rExp.getType();

	if (PTypeAssistantTC.isUnion(lType)) {
	    obligations
		    .add(new SubTypeObligation(lExp, AstFactory
			    .newABooleanBasicType(lExp.getLocation()), lType,
			    question));
	}

	if (PTypeAssistantTC.isUnion(rType)) {
	    obligations
		    .add(new SubTypeObligation(rExp, AstFactory
			    .newABooleanBasicType(rExp.getLocation()), rType,
			    question));
	}

	obligations.addAll(lExp.apply(mainVisitor, question));
	obligations.addAll(rExp.apply(mainVisitor, question));

	return obligations;
    }

    @Override
    public ProofObligationList caseAEquivalentBooleanBinaryExp(
	    AEquivalentBooleanBinaryExp node, POContextStack question)
	    throws AnalysisException {
	return handleBinaryBooleanExp(node, question);
    }

    @Override
    public ProofObligationList caseAImpliesBooleanBinaryExp(
	    AImpliesBooleanBinaryExp node, POContextStack question)
	    throws AnalysisException {
	return handleBinaryBooleanExp(node, question);
    }

    @Override
    public ProofObligationList caseAOrBooleanBinaryExp(
	    AOrBooleanBinaryExp node, POContextStack question)
	    throws AnalysisException {

	ProofObligationList obligations = new ProofObligationList();

	PExp lExp = node.getLeft();
	PExp rExp = node.getRight();
	PType lType = lExp.getType();
	PType rType = rExp.getType();

	if (lType instanceof AUnionType) {
	    obligations
		    .add(new SubTypeObligation(lExp, AstFactory
			    .newABooleanBasicType(lExp.getLocation()), lType,
			    question));
	}

	if (rType instanceof AUnionType) {
	    question.push(new PONotImpliesContext(lExp));
	    obligations
		    .add(new SubTypeObligation(rExp, AstFactory
			    .newABooleanBasicType(rExp.getLocation()), rType,
			    question));
	    question.pop();
	}

	obligations.addAll(lExp.apply(mainVisitor, question));
	question.push(new PONotImpliesContext(lExp));
	obligations.addAll(rExp.apply(mainVisitor, question));
	question.pop();

	return obligations;
    }

    private <T extends PExp> ProofObligationList handleDivideNumericBinaryExp(
	    T node, POContextStack question) throws AnalysisException {
	ProofObligationList obligations = new ProofObligationList();
	PExp[] leftRight = getLeftRight(node);
	PExp rExp = leftRight[RIGHT];

	obligations.addAll(defaultSNumericBinaryExp((SNumericBinaryExp) node,
		question));

	if (!(rExp instanceof AIntLiteralExp)
		&& !(rExp instanceof ARealLiteralExp)) {
	    obligations.add(new NonZeroObligation(node.getLocation(), rExp,
		    question));
	}

	return obligations;
    }

    @Override
    // RWL see [1] pg.
    public ProofObligationList caseADivNumericBinaryExp(
	    ADivNumericBinaryExp node, POContextStack question)
	    throws AnalysisException {
	return handleDivideNumericBinaryExp(node, question);
    }

    @Override
    public ProofObligationList caseADivideNumericBinaryExp(
	    ADivideNumericBinaryExp node, POContextStack question)
	    throws AnalysisException {
	return handleDivideNumericBinaryExp(node, question);
    }

    private <T> ProofObligationList handleNumericBinaryExpression(T node,
	    POContextStack question) throws AnalysisException {
	ProofObligationList obligations = new ProofObligationList();

	PExp[] leftRight = getLeftRight(node);
	PExp left = leftRight[LEFT];
	PExp right = leftRight[RIGHT];
	PType ltype = left.getType();
	PType rtype = right.getType();

	if (left.getLocation().startLine == 2792)
	    System.out.println("fd");

	if (PTypeAssistantTC.isUnion(ltype)) {
	    obligations.add(new SubTypeObligation(left, AstFactory
		    .newARealNumericBasicType(left.getLocation()), ltype,
		    question));
	}

	if (PTypeAssistantTC.isUnion(rtype)) {
	    obligations.add(new SubTypeObligation(right, AstFactory
		    .newARealNumericBasicType(right.getLocation()), rtype,
		    question));
	}

	obligations.addAll(left.apply(mainVisitor, question));
	obligations.addAll(right.apply(mainVisitor, question));
	return obligations;

    }

    @Override
    public ProofObligationList caseAGreaterEqualNumericBinaryExp(
	    AGreaterEqualNumericBinaryExp node, POContextStack question)
	    throws AnalysisException {
	return handleNumericBinaryExpression(node, question);
    }

    @Override
    public ProofObligationList caseAGreaterNumericBinaryExp(
	    AGreaterNumericBinaryExp node, POContextStack question)
	    throws AnalysisException {
	return handleNumericBinaryExpression(node, question);
    }

    @Override
    public ProofObligationList caseALessEqualNumericBinaryExp(
	    ALessEqualNumericBinaryExp node, POContextStack question)
	    throws AnalysisException {
	return handleNumericBinaryExpression(node, question);
    }

    @Override
    public ProofObligationList caseALessNumericBinaryExp(
	    ALessNumericBinaryExp node, POContextStack question)
	    throws AnalysisException {
	return handleNumericBinaryExpression(node, question);
    }

    @Override
    public ProofObligationList caseAModNumericBinaryExp(
	    AModNumericBinaryExp node, POContextStack question)
	    throws AnalysisException {
	return handleNumericBinaryExpression(node, question);
    }

    @Override
    public ProofObligationList caseAPlusNumericBinaryExp(
	    APlusNumericBinaryExp node, POContextStack question)
	    throws AnalysisException {
	return handleNumericBinaryExpression(node, question);
    }

    @Override
    public ProofObligationList caseARemNumericBinaryExp(
	    ARemNumericBinaryExp node, POContextStack question)
	    throws AnalysisException {
	return handleNumericBinaryExpression(node, question);
    }

    @Override
    public ProofObligationList caseASubtractNumericBinaryExp(
	    ASubtractNumericBinaryExp node, POContextStack question)
	    throws AnalysisException {
	return handleNumericBinaryExpression(node, question);
    }

    @Override
    public ProofObligationList caseATimesNumericBinaryExp(
	    ATimesNumericBinaryExp node, POContextStack question)
	    throws AnalysisException {
	return handleNumericBinaryExpression(node, question);
    }

    @Override
    public ProofObligationList caseAMapEnumMapExp(AMapEnumMapExp node,
	    POContextStack question) throws AnalysisException {

	ProofObligationList obligations = new ProofObligationList();

	List<AMapletExp> members = node.getMembers();

	for (AMapletExp maplet : members) {
	    obligations.addAll(maplet.apply(mainVisitor, question));
	}

	if (members.size() > 1)
	    obligations.add(new MapSeqOfCompatibleObligation(node, question));

	return obligations;
    }

    @Override
    public ProofObligationList caseASeqCompSeqExp(ASeqCompSeqExp node,
	    POContextStack question) throws AnalysisException {
	ProofObligationList obligations = new ProofObligationList();

	PExp first = node.getFirst();
	question.push(new POForAllPredicateContext(node));
	obligations.addAll(first.apply(mainVisitor, question));
	question.pop();

	obligations.addAll(node.getSetBind().apply(rootVisitor, question));

	PExp predicate = node.getPredicate();
	if (predicate != null) {
	    question.push(new POForAllContext(node));
	    obligations.addAll(predicate.apply(mainVisitor, question));
	    question.pop();
	}

	return obligations;
    }

    @Override
    public ProofObligationList caseASeqEnumSeqExp(ASeqEnumSeqExp node,
	    POContextStack question) throws AnalysisException {

	ProofObligationList obligations = new ProofObligationList();

	for (PExp e : node.getMembers())
	    obligations.addAll(e.apply(mainVisitor, question));

	return obligations;
    }

    @Override
    public ProofObligationList caseASetCompSetExp(ASetCompSetExp node,
	    POContextStack question) throws AnalysisException

    {
	PExp first = node.getFirst();
	PExp predicate = node.getPredicate();

	ProofObligationList obligations = new ProofObligationList();
	question.push(new POForAllPredicateContext(node));
	obligations.addAll(first.apply(mainVisitor, question));
	question.pop();

	List<PMultipleBind> bindings = node.getBindings();

	boolean finiteTest = false;
	for (PMultipleBind b : bindings) {
	    obligations.addAll(b.apply(rootVisitor, question));

	    if (b instanceof ATypeMultipleBind) {
		finiteTest = true;
	    }
	}

	if (finiteTest) {
	    obligations
		    .add(new org.overture.pog.obligation.FiniteSetObligation(
			    node, node.getSetType(), question));
	}

	if (predicate != null) {
	    question.push(new POForAllContext(node));
	    obligations.addAll(predicate.apply(mainVisitor, question));
	    question.pop();
	}

	return obligations;
    }

    @Override
    public ProofObligationList caseASetEnumSetExp(ASetEnumSetExp node,
	    POContextStack question) throws AnalysisException {
	ProofObligationList obligations = new ProofObligationList();

	for (PExp e : node.getMembers())
	    obligations.addAll(e.apply(mainVisitor, question));

	return obligations;

    }

    @Override
    public ProofObligationList caseASetRangeSetExp(ASetRangeSetExp node,
	    POContextStack question) throws AnalysisException {
	PExp last = node.getLast();
	PExp first = node.getFirst();
	ProofObligationList obligations = first.apply(mainVisitor, question);
	obligations.addAll(last.apply(mainVisitor, question));
	return obligations;

    }

    @Override
    public ProofObligationList defaultPExp(PExp node, POContextStack question) {
	return new ProofObligationList();
    }

}
