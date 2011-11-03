
package org.overture.pog.visitors;


import java.util.LinkedList;
import java.util.List;

import javax.swing.RootPaneContainer;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AAssignmentDefinition;
import org.overture.ast.definitions.ABusClassDefinition;
import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.AClassInvariantDefinition;
import org.overture.ast.definitions.ACpuClassDefinition;
import org.overture.ast.definitions.AEqualsDefinition;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AExternalDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.AImportedDefinition;
import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.ALocalDefinition;
import org.overture.ast.definitions.AMultiBindListDefinition;
import org.overture.ast.definitions.AMutexSyncDefinition;
import org.overture.ast.definitions.ANamedTraceDefinition;
import org.overture.ast.definitions.APerSyncDefinition;
import org.overture.ast.definitions.APrivateAccess;
import org.overture.ast.definitions.AProtectedAccess;
import org.overture.ast.definitions.APublicAccess;
import org.overture.ast.definitions.ARenamedDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.ASystemClassDefinition;
import org.overture.ast.definitions.AThreadDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.AUntypedDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PAccess;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.definitions.traces.AApplyExpressionTraceCoreDefinition;
import org.overture.ast.definitions.traces.ABracketedExpressionTraceCoreDefinition;
import org.overture.ast.definitions.traces.AConcurrentExpressionTraceCoreDefinition;
import org.overture.ast.definitions.traces.AInstanceTraceDefinition;
import org.overture.ast.definitions.traces.ALetBeStBindingTraceDefinition;
import org.overture.ast.definitions.traces.ALetDefBindingTraceDefinition;
import org.overture.ast.definitions.traces.ARepeatTraceDefinition;
import org.overture.ast.definitions.traces.PTraceCoreDefinition;
import org.overture.ast.definitions.traces.PTraceDefinition;
import org.overture.ast.expressions.*;
import org.overture.ast.modules.AAllExport;
import org.overture.ast.modules.AAllImport;
import org.overture.ast.modules.AFromModuleImports;
import org.overture.ast.modules.AFunctionExport;
import org.overture.ast.modules.AFunctionValueImport;
import org.overture.ast.modules.AModuleExports;
import org.overture.ast.modules.AModuleImports;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.modules.AOperationExport;
import org.overture.ast.modules.AOperationValueImport;
import org.overture.ast.modules.ATypeExport;
import org.overture.ast.modules.ATypeImport;
import org.overture.ast.modules.AValueExport;
import org.overture.ast.modules.AValueValueImport;
import org.overture.ast.modules.PExport;
import org.overture.ast.modules.PExports;
import org.overture.ast.modules.PImport;
import org.overture.ast.modules.PImports;
import org.overture.ast.modules.PModules;
import org.overture.ast.modules.SValueImport;
import org.overture.ast.node.AFalseBooleanConst;
import org.overture.ast.node.ATrueBooleanConst;
import org.overture.ast.node.Node;
import org.overture.ast.node.PBooleanConst;
import org.overture.ast.node.Token;
import org.overture.ast.node.tokens.TAndAnd;
import org.overture.ast.node.tokens.TAsync;
import org.overture.ast.node.tokens.TBool;
import org.overture.ast.node.tokens.TBoolLiteral;
import org.overture.ast.node.tokens.TChar;
import org.overture.ast.node.tokens.TCharLiteral;
import org.overture.ast.node.tokens.TFalse;
import org.overture.ast.node.tokens.TInt;
import org.overture.ast.node.tokens.TNat;
import org.overture.ast.node.tokens.TNatOne;
import org.overture.ast.node.tokens.TNumbersLiteral;
import org.overture.ast.node.tokens.TOrOr;
import org.overture.ast.node.tokens.TPlus;
import org.overture.ast.node.tokens.TQuoteLiteral;
import org.overture.ast.node.tokens.TRat;
import org.overture.ast.node.tokens.TReal;
import org.overture.ast.node.tokens.TRealLiteral;
import org.overture.ast.node.tokens.TStatic;
import org.overture.ast.node.tokens.TStringLiteral;
import org.overture.ast.node.tokens.TTokenLiteral;
import org.overture.ast.node.tokens.TTrue;
import org.overture.ast.patterns.ABooleanPattern;
import org.overture.ast.patterns.ACharacterPattern;
import org.overture.ast.patterns.AConcatenationPattern;
import org.overture.ast.patterns.ADefPatternBind;
import org.overture.ast.patterns.AExpressionPattern;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.AIgnorePattern;
import org.overture.ast.patterns.AIntegerPattern;
import org.overture.ast.patterns.ANilPattern;
import org.overture.ast.patterns.APatternListTypePair;
import org.overture.ast.patterns.APatternTypePair;
import org.overture.ast.patterns.AQuotePattern;
import org.overture.ast.patterns.ARealPattern;
import org.overture.ast.patterns.ARecordPattern;
import org.overture.ast.patterns.ASeqPattern;
import org.overture.ast.patterns.ASetBind;
import org.overture.ast.patterns.ASetMultipleBind;
import org.overture.ast.patterns.ASetPattern;
import org.overture.ast.patterns.AStringPattern;
import org.overture.ast.patterns.ATuplePattern;
import org.overture.ast.patterns.ATypeBind;
import org.overture.ast.patterns.ATypeMultipleBind;
import org.overture.ast.patterns.AUnionPattern;
import org.overture.ast.patterns.PBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPair;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.patterns.PPatternBind;
import org.overture.ast.patterns.assistants.PatternList;
import org.overture.ast.statements.AAlwaysStm;
import org.overture.ast.statements.AApplyObjectDesignator;
import org.overture.ast.statements.AAssignmentStm;
import org.overture.ast.statements.AAtomicStm;
import org.overture.ast.statements.ABlockSimpleBlockStm;
import org.overture.ast.statements.ACallObjectStm;
import org.overture.ast.statements.ACallStm;
import org.overture.ast.statements.ACaseAlternativeStm;
import org.overture.ast.statements.ACasesStm;
import org.overture.ast.statements.AClassInvariantStm;
import org.overture.ast.statements.ACyclesStm;
import org.overture.ast.statements.ADefLetDefStm;
import org.overture.ast.statements.ADurationStm;
import org.overture.ast.statements.AElseIfStm;
import org.overture.ast.statements.AErrorCase;
import org.overture.ast.statements.AErrorStm;
import org.overture.ast.statements.AExitStm;
import org.overture.ast.statements.AExternalClause;
import org.overture.ast.statements.AFieldObjectDesignator;
import org.overture.ast.statements.AFieldStateDesignator;
import org.overture.ast.statements.AForAllStm;
import org.overture.ast.statements.AForIndexStm;
import org.overture.ast.statements.AForPatternBindStm;
import org.overture.ast.statements.AIdentifierObjectDesignator;
import org.overture.ast.statements.AIdentifierStateDesignator;
import org.overture.ast.statements.AIfStm;
import org.overture.ast.statements.ALetBeStStm;
import org.overture.ast.statements.AMapSeqStateDesignator;
import org.overture.ast.statements.ANewObjectDesignator;
import org.overture.ast.statements.ANonDeterministicSimpleBlockStm;
import org.overture.ast.statements.ANotYetSpecifiedStm;
import org.overture.ast.statements.APeriodicStm;
import org.overture.ast.statements.AReturnStm;
import org.overture.ast.statements.ASelfObjectDesignator;
import org.overture.ast.statements.ASkipStm;
import org.overture.ast.statements.ASpecificationStm;
import org.overture.ast.statements.AStartStm;
import org.overture.ast.statements.ASubclassResponsibilityStm;
import org.overture.ast.statements.ATixeStm;
import org.overture.ast.statements.ATixeStmtAlternative;
import org.overture.ast.statements.ATrapStm;
import org.overture.ast.statements.AWhileStm;
import org.overture.ast.statements.PAlternativeStm;
import org.overture.ast.statements.PCase;
import org.overture.ast.statements.PClause;
import org.overture.ast.statements.PObjectDesignator;
import org.overture.ast.statements.PStateDesignator;
import org.overture.ast.statements.PStm;
import org.overture.ast.statements.PStmtAlternative;
import org.overture.ast.statements.SLetDefStm;
import org.overture.ast.statements.SSimpleBlockStm;
import org.overture.ast.types.AAccessSpecifierAccessSpecifier;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.ABracketType;
import org.overture.ast.types.ACharBasicType;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AInMapMapType;
import org.overture.ast.types.AIntNumericBasicType;
import org.overture.ast.types.AMapMapType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.ANatNumericBasicType;
import org.overture.ast.types.ANatOneNumericBasicType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AParameterType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.AQuoteType;
import org.overture.ast.types.ARationalNumericBasicType;
import org.overture.ast.types.ARealNumericBasicType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.ASeq1SeqType;
import org.overture.ast.types.ASeqSeqType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.ATokenBasicType;
import org.overture.ast.types.AUndefinedType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.AUnresolvedType;
import org.overture.ast.types.AVoidReturnType;
import org.overture.ast.types.AVoidType;
import org.overture.ast.types.PAccessSpecifier;
import org.overture.ast.types.PField;
import org.overture.ast.types.PType;
import org.overture.ast.types.SBasicType;
import org.overture.ast.types.SInvariantType;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SNumericBasicType;
import org.overture.ast.types.SSeqType;
import org.overture.ast.types.assistants.PTypeAssistant;
import org.overture.pog.assistants.PDefinitionAssistantPOG;
import org.overture.pog.obligations.CasesExhaustiveObligation;
import org.overture.pog.obligations.FiniteMapObligation;
import org.overture.pog.obligations.FuncPostConditionObligation;
import org.overture.pog.obligations.FunctionApplyObligation;
import org.overture.pog.obligations.MapApplyObligation;
import org.overture.pog.obligations.MapSetOfCompatibleObligation;
import org.overture.pog.obligations.NonEmptySeqObligation;
import org.overture.pog.obligations.POContextStack;
import org.overture.pog.obligations.POForAllContext;
import org.overture.pog.obligations.POForAllPredicateContext;
import org.overture.pog.obligations.POFunctionDefinitionContext;
import org.overture.pog.obligations.POFunctionResultContext;
import org.overture.pog.obligations.ParameterPatternObligation;
import org.overture.pog.obligations.ProofObligation;
import org.overture.pog.obligations.ProofObligationList;
import org.overture.pog.obligations.RecursiveObligation;
import org.overture.pog.obligations.SeqApplyObligation;
import org.overture.pog.obligations.SubTypeObligation;
import org.overture.typecheck.TypeComparator;
import org.overturetool.util.ClonableFile;
import org.overturetool.vdmj.lex.LexBooleanToken;
import org.overturetool.vdmj.lex.LexCharacterToken;
import org.overturetool.vdmj.lex.LexIdentifierToken;
import org.overturetool.vdmj.lex.LexIntegerToken;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.lex.LexQuoteToken;
import org.overturetool.vdmj.lex.LexRealToken;
import org.overturetool.vdmj.lex.LexStringToken;
import org.overturetool.vdmj.lex.LexToken;
import org.overturetool.vdmj.typechecker.ClassDefinitionSettings;
import org.overturetool.vdmj.typechecker.NameScope;

/**
 * This is the proof obligation visitor climbs through the AST and
 * builds the list of proof obligations the given program exhibits.
 * 
 * References: 
 * 
 * [1] http://wiki.overturetool.org/images/9/95/VDM10_lang_man.pdf
 * for BNF definitions.
 * 
 * This work is based on previous work by Nick Battle in the VDMJ
 * package.
 * 
 * @author Overture team
 * @since 1.0
 */
public class PogVisitor extends QuestionAnswerAdaptor<POContextStack, ProofObligationList> {

	
	@Override
	// See [1] pg. 167 for the definition
	public ProofObligationList caseAModuleModules(AModuleModules node,
			POContextStack question) {
		
		return PDefinitionAssistantPOG.getProofObligations(node.getDefs(),this,question);
		
	}

	@Override
	// from [1] pg. 35 we have an:
	// explicit function definition = identifier,
	// [ type variable list ], ‘:’, function type,
	// identifier, parameters list, ‘==’,
	// function body,
	// [ ‘pre’, expression ],
	// [ ‘post’, expression ],
	// [ ‘measure’, name ] ;
	public ProofObligationList caseAExplicitFunctionDefinition(
			AExplicitFunctionDefinition node, POContextStack question) {
		
		ProofObligationList obligations = new ProofObligationList();
		LexNameList pids = new  LexNameList();

		
		// add all defined names from the function parameter list 
		for (List<PPattern> patterns : node.getParamPatternList())
			for(PPattern p : patterns)
				for(PDefinition def : p.getDefinitions())
					pids.add(def.getName());

		// check for duplicates 
		if (pids.hasDuplicates())
		{
			obligations.add(new ParameterPatternObligation(node,question));
		}
		
		// do proof obligations for the pre-condition
		PExp precondition  = node.getPrecondition(); 
		if(precondition != null)
		{
			question.push(new POFunctionDefinitionContext(node, false));
			obligations.addAll(precondition.apply(this, question));
			question.pop();
		}
		
		// do proof obligations for the post-condition
		PExp postcondition = node.getPostcondition();
		if (postcondition != null)
		{
			question.push(new POFunctionDefinitionContext(node,false));
			obligations.add(new FuncPostConditionObligation(node, question));
			question.push(new POFunctionResultContext(node));
			obligations.addAll(postcondition.apply(this, question));
			question.pop();
			question.pop();
		}

		// do proof obligations for the function body
		question.push(new POFunctionDefinitionContext(node,true));
		PExp body = node.getBody();
		obligations.addAll(body.apply(this,question));
		
		
		
		// do proof obligation for the return type
		if (node.getIsUndefined() || !TypeComparator.isSubType(node.getActualResult(), node.getExpectedResult()))
		{
			obligations.add(new SubTypeObligation(node, node.getExpectedResult(), node.getActualResult(), question));
		}
		question.pop();
		
		return obligations;
	}

	
	

	@Override
	// see [1] pg. 179 unary expressions
	public ProofObligationList caseAHeadUnaryExp(AHeadUnaryExp node,
			POContextStack question) {

		ProofObligation po =new NonEmptySeqObligation(node.getExp(), question); 
		
		LinkedList<PDefinition> defs = new LinkedList<PDefinition>();
		ProofObligationList obligations = PDefinitionAssistantPOG.getProofObligations(defs, this, question);
		obligations.add(po);
		
		return obligations;
	}

	@Override
	// [1] pg. 46 
	public ProofObligationList caseACasesExp(ACasesExp node,
			POContextStack question) {

		ProofObligationList obligations = new ProofObligationList();
		
		int count = 0;
		boolean hasIgnore = false;
		
		
		// handle each case
		for(ACaseAlternative alt : node.getCases())
		{
			
			if (alt.getPattern() instanceof AIgnorePattern)
				hasIgnore = true;
			
			obligations.addAll(alt.apply(this, question));
			count++;
		}
		
		if (node.getOthers() != null)
		{
			obligations.addAll(node.getOthers().apply(this, question));
		}
		
		for(int i = 0;i<count;i++) question.pop();
		
		if (node.getOthers() == null && !hasIgnore)
			obligations.add(new CasesExhaustiveObligation(node, question));
		
		return obligations;
	}

	@Override
	public ProofObligationList caseAMapCompMapExp(AMapCompMapExp node,
			POContextStack question) {
		ProofObligationList obligations = new ProofObligationList();
		
		obligations.add(new MapSetOfCompatibleObligation(node, question));
		
		question.push(new POForAllPredicateContext(node));
		obligations.addAll(node.getFirst().apply(this, question));
		question.pop();
		
		boolean finiteTest = false;
		
		for (PMultipleBind mb : node.getBindings())
		{
			obligations.addAll(mb.apply(this, question));
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
	public ProofObligationList caseLexToken(LexToken node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseLexToken(node, question);
	}

	@Override
	public ProofObligationList caseLexNameToken(LexNameToken node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseLexNameToken(node, question);
	}

	@Override
	public ProofObligationList caseLexIdentifierToken(LexIdentifierToken node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseLexIdentifierToken(node, question);
	}

	@Override
	public ProofObligationList caseLexBooleanToken(LexBooleanToken node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseLexBooleanToken(node, question);
	}

	@Override
	public ProofObligationList caseLexCharacterToken(LexCharacterToken node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseLexCharacterToken(node, question);
	}

	@Override
	public ProofObligationList caseLexIntegerToken(LexIntegerToken node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseLexIntegerToken(node, question);
	}

	@Override
	public ProofObligationList caseLexQuoteToken(LexQuoteToken node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseLexQuoteToken(node, question);
	}

	@Override
	public ProofObligationList caseLexRealToken(LexRealToken node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseLexRealToken(node, question);
	}

	@Override
	public ProofObligationList caseLexStringToken(LexStringToken node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseLexStringToken(node, question);
	}

	@Override
	public ProofObligationList caseClonableFile(ClonableFile node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseClonableFile(node, question);
	}

	@Override
	public ProofObligationList caseClassDefinitionSettings(
			ClassDefinitionSettings node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseClassDefinitionSettings(node, question);
	}

	@Override
	public ProofObligationList caseLexLocation(LexLocation node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseLexLocation(node, question);
	}

	@Override
	public ProofObligationList caseTPlus(TPlus node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseTPlus(node, question);
	}

	@Override
	public ProofObligationList caseBoolean(Boolean node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseBoolean(node, question);
	}

	@Override
	public ProofObligationList caseInteger(Integer node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseInteger(node, question);
	}

	@Override
	public ProofObligationList caseString(String node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseString(node, question);
	}

	@Override
	public ProofObligationList caseLong(Long node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseLong(node, question);
	}

	@Override
	public ProofObligationList caseNameScope(NameScope node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseNameScope(node, question);
	}

	@Override
	public ProofObligationList caseTBool(TBool node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseTBool(node, question);
	}

	@Override
	public ProofObligationList caseTChar(TChar node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseTChar(node, question);
	}

	@Override
	public ProofObligationList caseTInt(TInt node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseTInt(node, question);
	}

	@Override
	public ProofObligationList caseTNatOne(TNatOne node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseTNatOne(node, question);
	}

	@Override
	public ProofObligationList caseTNat(TNat node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseTNat(node, question);
	}

	@Override
	public ProofObligationList caseTRat(TRat node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseTRat(node, question);
	}

	@Override
	public ProofObligationList caseTReal(TReal node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseTReal(node, question);
	}

	@Override
	public ProofObligationList caseTTrue(TTrue node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseTTrue(node, question);
	}

	@Override
	public ProofObligationList caseTFalse(TFalse node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseTFalse(node, question);
	}

	@Override
	public ProofObligationList caseTAndAnd(TAndAnd node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseTAndAnd(node, question);
	}

	@Override
	public ProofObligationList caseTOrOr(TOrOr node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseTOrOr(node, question);
	}

	@Override
	public ProofObligationList caseTNumbersLiteral(TNumbersLiteral node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseTNumbersLiteral(node, question);
	}

	@Override
	public ProofObligationList caseTCharLiteral(TCharLiteral node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseTCharLiteral(node, question);
	}

	@Override
	public ProofObligationList caseTQuoteLiteral(TQuoteLiteral node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseTQuoteLiteral(node, question);
	}

	@Override
	public ProofObligationList caseTRealLiteral(TRealLiteral node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseTRealLiteral(node, question);
	}

	@Override
	public ProofObligationList caseTStringLiteral(TStringLiteral node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseTStringLiteral(node, question);
	}

	@Override
	public ProofObligationList caseTTokenLiteral(TTokenLiteral node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseTTokenLiteral(node, question);
	}

	@Override
	public ProofObligationList caseTBoolLiteral(TBoolLiteral node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseTBoolLiteral(node, question);
	}

	@Override
	public ProofObligationList caseTStatic(TStatic node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseTStatic(node, question);
	}

	@Override
	public ProofObligationList caseTAsync(TAsync node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseTAsync(node, question);
	}

	@Override
	public ProofObligationList defaultPExp(PExp node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultPExp(node, question);
	}

	
	@Override
	// RWL see [1] pg. 57: 6.12 Apply Expressions
	public ProofObligationList caseAApplyExp(AApplyExp node,
			POContextStack question) {

		ProofObligationList obligations = new ProofObligationList();
		
		PType type = node.getType();
		if (type instanceof SMapType )
		{
			SMapType mapType = (SMapType)type;
			obligations.add(new MapApplyObligation(node.getRoot(), node.getArgs().get(0), question));
			PType aType = question.checkType(node.getArgs().get(0), node.getArgtypes().get(0));
			
			if (!TypeComparator.isSubType(aType, mapType.getFrom()))
			{
				obligations.add(new SubTypeObligation(node.getArgs().get(0), mapType.getFrom(), aType, question));
			}
		}
		
		if (! (type instanceof AUnknownType) && (type instanceof AFunctionType) )
		{
			AFunctionType funcType = (AFunctionType)type;
			// TODO Fix this get the name of the precondition
			String prename = "Precond";
			if (prename == null || !prename.equals(""))
			{
				obligations.add(new FunctionApplyObligation(node.getRoot(), node.getArgs(), prename, question));
			}
			
			int i = 0;
			List<PType> argTypes = node.getArgtypes();
			List<PExp> argList = node.getArgs();
			for(PType argType : argTypes)
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
					AExplicitFunctionDefinition def = (AExplicitFunctionDefinition)recursive;
					if (def.getMeasure() != null)
					{
						obligations.add(new RecursiveObligation(def, node, question));
					}
				}
				else if (recursive instanceof AImplicitFunctionDefinition)
				{
					AImplicitFunctionDefinition def = (AImplicitFunctionDefinition)recursive;
					if (def.getMeasure() != null)
					{
						obligations.add(new RecursiveObligation(def,node, question));
					}
					
				}
			}
		}
		
		if (type instanceof SSeqType)
		{
			obligations.add(new SeqApplyObligation(node.getRoot(), node.getArgs().get(0), question));
		}
		
		obligations.addAll(node.getRoot().apply(this, question));
		
		for(PExp arg: node.getArgs())
		{
			obligations.addAll(arg.apply(this, question));
		}
		
		return obligations;
	}

	@Override
	// RWL 
	public ProofObligationList caseSUnaryExp(SUnaryExp node,
			POContextStack question) {
		return super.caseSUnaryExp(node, question);
	}

	@Override
	public ProofObligationList defaultSUnaryExp(SUnaryExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultSUnaryExp(node, question);
	}

	@Override
	public ProofObligationList caseSBinaryExp(SBinaryExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseSBinaryExp(node, question);
	}

	@Override
	public ProofObligationList defaultSBinaryExp(SBinaryExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultSBinaryExp(node, question);
	}

	@Override
	public ProofObligationList caseABooleanConstExp(ABooleanConstExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseABooleanConstExp(node, question);
	}

	@Override
	public ProofObligationList caseACharLiteralExp(ACharLiteralExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseACharLiteralExp(node, question);
	}

	@Override
	public ProofObligationList caseAElseIfExp(AElseIfExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAElseIfExp(node, question);
	}

	@Override
	public ProofObligationList caseAExists1Exp(AExists1Exp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAExists1Exp(node, question);
	}

	@Override
	public ProofObligationList caseAExistsExp(AExistsExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAExistsExp(node, question);
	}

	@Override
	public ProofObligationList caseAFieldExp(AFieldExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAFieldExp(node, question);
	}

	@Override
	public ProofObligationList caseAFieldNumberExp(AFieldNumberExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAFieldNumberExp(node, question);
	}

	@Override
	public ProofObligationList caseAForAllExp(AForAllExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAForAllExp(node, question);
	}

	@Override
	public ProofObligationList caseAFuncInstatiationExp(
			AFuncInstatiationExp node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAFuncInstatiationExp(node, question);
	}

	@Override
	public ProofObligationList caseAHistoryExp(AHistoryExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAHistoryExp(node, question);
	}

	@Override
	public ProofObligationList caseAIfExp(AIfExp node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAIfExp(node, question);
	}

	@Override
	public ProofObligationList caseAIntLiteralExp(AIntLiteralExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAIntLiteralExp(node, question);
	}

	@Override
	public ProofObligationList caseAIotaExp(AIotaExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAIotaExp(node, question);
	}

	@Override
	public ProofObligationList caseAIsExp(AIsExp node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAIsExp(node, question);
	}

	@Override
	public ProofObligationList caseAIsOfBaseClassExp(AIsOfBaseClassExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAIsOfBaseClassExp(node, question);
	}

	@Override
	public ProofObligationList caseAIsOfClassExp(AIsOfClassExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAIsOfClassExp(node, question);
	}

	@Override
	public ProofObligationList caseALambdaExp(ALambdaExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseALambdaExp(node, question);
	}

	@Override
	public ProofObligationList caseALetBeStExp(ALetBeStExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseALetBeStExp(node, question);
	}

	@Override
	public ProofObligationList caseALetDefExp(ALetDefExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseALetDefExp(node, question);
	}

	@Override
	public ProofObligationList caseADefExp(ADefExp node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseADefExp(node, question);
	}

	@Override
	public ProofObligationList caseSMapExp(SMapExp node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseSMapExp(node, question);
	}

	@Override
	public ProofObligationList defaultSMapExp(SMapExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultSMapExp(node, question);
	}

	@Override
	public ProofObligationList caseAMapletExp(AMapletExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAMapletExp(node, question);
	}

	@Override
	public ProofObligationList caseAMkBasicExp(AMkBasicExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAMkBasicExp(node, question);
	}

	@Override
	public ProofObligationList caseAMkTypeExp(AMkTypeExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAMkTypeExp(node, question);
	}

	@Override
	public ProofObligationList caseAMuExp(AMuExp node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAMuExp(node, question);
	}

	@Override
	public ProofObligationList caseANewExp(ANewExp node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseANewExp(node, question);
	}

	@Override
	public ProofObligationList caseANilExp(ANilExp node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseANilExp(node, question);
	}

	@Override
	public ProofObligationList caseANotYetSpecifiedExp(
			ANotYetSpecifiedExp node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseANotYetSpecifiedExp(node, question);
	}

	@Override
	public ProofObligationList caseAPostOpExp(APostOpExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAPostOpExp(node, question);
	}

	@Override
	public ProofObligationList caseAPreExp(APreExp node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAPreExp(node, question);
	}

	@Override
	public ProofObligationList caseAPreOpExp(APreOpExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAPreOpExp(node, question);
	}

	@Override
	public ProofObligationList caseAQuoteLiteralExp(AQuoteLiteralExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAQuoteLiteralExp(node, question);
	}

	@Override
	public ProofObligationList caseARealLiteralExp(ARealLiteralExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseARealLiteralExp(node, question);
	}

	@Override
	public ProofObligationList caseASameBaseClassExp(ASameBaseClassExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseASameBaseClassExp(node, question);
	}

	@Override
	public ProofObligationList caseASameClassExp(ASameClassExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseASameClassExp(node, question);
	}

	@Override
	public ProofObligationList caseASelfExp(ASelfExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseASelfExp(node, question);
	}

	@Override
	public ProofObligationList caseSSeqExp(SSeqExp node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseSSeqExp(node, question);
	}

	@Override
	public ProofObligationList defaultSSeqExp(SSeqExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultSSeqExp(node, question);
	}

	@Override
	public ProofObligationList caseSSetExp(SSetExp node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseSSetExp(node, question);
	}

	@Override
	public ProofObligationList defaultSSetExp(SSetExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultSSetExp(node, question);
	}

	@Override
	public ProofObligationList caseAStateInitExp(AStateInitExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAStateInitExp(node, question);
	}

	@Override
	public ProofObligationList caseAStringLiteralExp(AStringLiteralExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAStringLiteralExp(node, question);
	}

	@Override
	public ProofObligationList caseASubclassResponsibilityExp(
			ASubclassResponsibilityExp node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseASubclassResponsibilityExp(node, question);
	}

	@Override
	public ProofObligationList caseASubseqExp(ASubseqExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseASubseqExp(node, question);
	}

	@Override
	public ProofObligationList caseAThreadIdExp(AThreadIdExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAThreadIdExp(node, question);
	}

	@Override
	public ProofObligationList caseATimeExp(ATimeExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseATimeExp(node, question);
	}

	@Override
	public ProofObligationList caseATupleExp(ATupleExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseATupleExp(node, question);
	}

	@Override
	public ProofObligationList caseAUndefinedExp(AUndefinedExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAUndefinedExp(node, question);
	}

	@Override
	public ProofObligationList caseAVariableExp(AVariableExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAVariableExp(node, question);
	}

	@Override
	public ProofObligationList caseAAbsoluteUnaryExp(AAbsoluteUnaryExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAAbsoluteUnaryExp(node, question);
	}

	@Override
	public ProofObligationList caseACardinalityUnaryExp(
			ACardinalityUnaryExp node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseACardinalityUnaryExp(node, question);
	}

	@Override
	public ProofObligationList caseADistConcatUnaryExp(
			ADistConcatUnaryExp node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseADistConcatUnaryExp(node, question);
	}

	@Override
	public ProofObligationList caseADistIntersectUnaryExp(
			ADistIntersectUnaryExp node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseADistIntersectUnaryExp(node, question);
	}

	@Override
	public ProofObligationList caseADistMergeUnaryExp(ADistMergeUnaryExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseADistMergeUnaryExp(node, question);
	}

	@Override
	public ProofObligationList caseADistUnionUnaryExp(ADistUnionUnaryExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseADistUnionUnaryExp(node, question);
	}

	@Override
	public ProofObligationList caseAElementsUnaryExp(AElementsUnaryExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAElementsUnaryExp(node, question);
	}

	@Override
	public ProofObligationList caseAFloorUnaryExp(AFloorUnaryExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAFloorUnaryExp(node, question);
	}

	@Override
	public ProofObligationList caseAIndicesUnaryExp(AIndicesUnaryExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAIndicesUnaryExp(node, question);
	}

	@Override
	public ProofObligationList caseALenUnaryExp(ALenUnaryExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseALenUnaryExp(node, question);
	}

	@Override
	public ProofObligationList caseAMapDomainUnaryExp(AMapDomainUnaryExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAMapDomainUnaryExp(node, question);
	}

	@Override
	public ProofObligationList caseAMapInverseUnaryExp(
			AMapInverseUnaryExp node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAMapInverseUnaryExp(node, question);
	}

	@Override
	public ProofObligationList caseAMapRangeUnaryExp(AMapRangeUnaryExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAMapRangeUnaryExp(node, question);
	}

	@Override
	public ProofObligationList caseANotUnaryExp(ANotUnaryExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseANotUnaryExp(node, question);
	}

	@Override
	public ProofObligationList caseAPowerSetUnaryExp(APowerSetUnaryExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAPowerSetUnaryExp(node, question);
	}

	@Override
	public ProofObligationList caseAReverseUnaryExp(AReverseUnaryExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAReverseUnaryExp(node, question);
	}

	@Override
	public ProofObligationList caseATailUnaryExp(ATailUnaryExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseATailUnaryExp(node, question);
	}

	@Override
	public ProofObligationList caseAUnaryMinusUnaryExp(
			AUnaryMinusUnaryExp node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAUnaryMinusUnaryExp(node, question);
	}

	@Override
	public ProofObligationList caseAUnaryPlusUnaryExp(AUnaryPlusUnaryExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAUnaryPlusUnaryExp(node, question);
	}

	@Override
	public ProofObligationList caseSBooleanBinaryExp(SBooleanBinaryExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseSBooleanBinaryExp(node, question);
	}

	@Override
	public ProofObligationList defaultSBooleanBinaryExp(SBooleanBinaryExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultSBooleanBinaryExp(node, question);
	}

	@Override
	public ProofObligationList caseACompBinaryExp(ACompBinaryExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseACompBinaryExp(node, question);
	}

	@Override
	public ProofObligationList caseADomainResByBinaryExp(
			ADomainResByBinaryExp node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseADomainResByBinaryExp(node, question);
	}

	@Override
	public ProofObligationList caseADomainResToBinaryExp(
			ADomainResToBinaryExp node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseADomainResToBinaryExp(node, question);
	}

	@Override
	public ProofObligationList caseAEqualsBinaryExp(AEqualsBinaryExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAEqualsBinaryExp(node, question);
	}

	@Override
	public ProofObligationList caseAInSetBinaryExp(AInSetBinaryExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAInSetBinaryExp(node, question);
	}

	@Override
	public ProofObligationList caseAMapUnionBinaryExp(AMapUnionBinaryExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAMapUnionBinaryExp(node, question);
	}

	@Override
	public ProofObligationList caseANotEqualBinaryExp(ANotEqualBinaryExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseANotEqualBinaryExp(node, question);
	}

	@Override
	public ProofObligationList caseANotInSetBinaryExp(ANotInSetBinaryExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseANotInSetBinaryExp(node, question);
	}

	@Override
	public ProofObligationList caseSNumericBinaryExp(SNumericBinaryExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseSNumericBinaryExp(node, question);
	}

	@Override
	public ProofObligationList defaultSNumericBinaryExp(SNumericBinaryExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultSNumericBinaryExp(node, question);
	}

	@Override
	public ProofObligationList caseAPlusPlusBinaryExp(APlusPlusBinaryExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAPlusPlusBinaryExp(node, question);
	}

	@Override
	public ProofObligationList caseAProperSubsetBinaryExp(
			AProperSubsetBinaryExp node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAProperSubsetBinaryExp(node, question);
	}

	@Override
	public ProofObligationList caseARangeResByBinaryExp(
			ARangeResByBinaryExp node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseARangeResByBinaryExp(node, question);
	}

	@Override
	public ProofObligationList caseARangeResToBinaryExp(
			ARangeResToBinaryExp node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseARangeResToBinaryExp(node, question);
	}

	@Override
	public ProofObligationList caseASeqConcatBinaryExp(
			ASeqConcatBinaryExp node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseASeqConcatBinaryExp(node, question);
	}

	@Override
	public ProofObligationList caseASetDifferenceBinaryExp(
			ASetDifferenceBinaryExp node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseASetDifferenceBinaryExp(node, question);
	}

	@Override
	public ProofObligationList caseASetIntersectBinaryExp(
			ASetIntersectBinaryExp node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseASetIntersectBinaryExp(node, question);
	}

	@Override
	public ProofObligationList caseASetUnionBinaryExp(ASetUnionBinaryExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseASetUnionBinaryExp(node, question);
	}

	@Override
	public ProofObligationList caseAStarStarBinaryExp(AStarStarBinaryExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAStarStarBinaryExp(node, question);
	}

	@Override
	public ProofObligationList caseASubsetBinaryExp(ASubsetBinaryExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseASubsetBinaryExp(node, question);
	}

	@Override
	public ProofObligationList caseAAndBooleanBinaryExp(
			AAndBooleanBinaryExp node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAAndBooleanBinaryExp(node, question);
	}

	@Override
	public ProofObligationList caseAEquivalentBooleanBinaryExp(
			AEquivalentBooleanBinaryExp node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAEquivalentBooleanBinaryExp(node, question);
	}

	@Override
	public ProofObligationList caseAImpliesBooleanBinaryExp(
			AImpliesBooleanBinaryExp node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAImpliesBooleanBinaryExp(node, question);
	}

	@Override
	public ProofObligationList caseAOrBooleanBinaryExp(
			AOrBooleanBinaryExp node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAOrBooleanBinaryExp(node, question);
	}

	@Override
	public ProofObligationList caseADivNumericBinaryExp(
			ADivNumericBinaryExp node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseADivNumericBinaryExp(node, question);
	}

	@Override
	public ProofObligationList caseADivideNumericBinaryExp(
			ADivideNumericBinaryExp node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseADivideNumericBinaryExp(node, question);
	}

	@Override
	public ProofObligationList caseAGreaterEqualNumericBinaryExp(
			AGreaterEqualNumericBinaryExp node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAGreaterEqualNumericBinaryExp(node, question);
	}

	@Override
	public ProofObligationList caseAGreaterNumericBinaryExp(
			AGreaterNumericBinaryExp node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAGreaterNumericBinaryExp(node, question);
	}

	@Override
	public ProofObligationList caseALessEqualNumericBinaryExp(
			ALessEqualNumericBinaryExp node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseALessEqualNumericBinaryExp(node, question);
	}

	@Override
	public ProofObligationList caseALessNumericBinaryExp(
			ALessNumericBinaryExp node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseALessNumericBinaryExp(node, question);
	}

	@Override
	public ProofObligationList caseAModNumericBinaryExp(
			AModNumericBinaryExp node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAModNumericBinaryExp(node, question);
	}

	@Override
	public ProofObligationList caseAPlusNumericBinaryExp(
			APlusNumericBinaryExp node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAPlusNumericBinaryExp(node, question);
	}

	@Override
	public ProofObligationList caseARemNumericBinaryExp(
			ARemNumericBinaryExp node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseARemNumericBinaryExp(node, question);
	}

	@Override
	public ProofObligationList caseASubstractNumericBinaryExp(
			ASubstractNumericBinaryExp node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseASubstractNumericBinaryExp(node, question);
	}

	@Override
	public ProofObligationList caseATimesNumericBinaryExp(
			ATimesNumericBinaryExp node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseATimesNumericBinaryExp(node, question);
	}

	@Override
	public ProofObligationList caseAMapEnumMapExp(AMapEnumMapExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAMapEnumMapExp(node, question);
	}

	@Override
	public ProofObligationList caseASeqCompSeqExp(ASeqCompSeqExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseASeqCompSeqExp(node, question);
	}

	@Override
	public ProofObligationList caseASeqEnumSeqExp(ASeqEnumSeqExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseASeqEnumSeqExp(node, question);
	}

	@Override
	public ProofObligationList caseASetCompSetExp(ASetCompSetExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseASetCompSetExp(node, question);
	}

	@Override
	public ProofObligationList caseASetEnumSetExp(ASetEnumSetExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseASetEnumSetExp(node, question);
	}

	@Override
	public ProofObligationList caseASetRangeSetExp(ASetRangeSetExp node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseASetRangeSetExp(node, question);
	}

	@Override
	public ProofObligationList defaultPModifier(PModifier node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultPModifier(node, question);
	}

	@Override
	public ProofObligationList caseARecordModifier(ARecordModifier node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseARecordModifier(node, question);
	}

	@Override
	public ProofObligationList defaultPAlternative(PAlternative node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultPAlternative(node, question);
	}

	@Override
	public ProofObligationList caseACaseAlternative(ACaseAlternative node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseACaseAlternative(node, question);
	}

	@Override
	public ProofObligationList defaultPBooleanConst(PBooleanConst node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultPBooleanConst(node, question);
	}

	@Override
	public ProofObligationList caseATrueBooleanConst(ATrueBooleanConst node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseATrueBooleanConst(node, question);
	}

	@Override
	public ProofObligationList caseAFalseBooleanConst(AFalseBooleanConst node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAFalseBooleanConst(node, question);
	}

	@Override
	public ProofObligationList defaultPType(PType node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultPType(node, question);
	}

	@Override
	public ProofObligationList caseSBasicType(SBasicType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseSBasicType(node, question);
	}

	@Override
	public ProofObligationList defaultSBasicType(SBasicType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultSBasicType(node, question);
	}

	@Override
	public ProofObligationList caseABracketType(ABracketType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseABracketType(node, question);
	}

	@Override
	public ProofObligationList caseAClassType(AClassType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAClassType(node, question);
	}

	@Override
	public ProofObligationList caseAFunctionType(AFunctionType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAFunctionType(node, question);
	}

	@Override
	public ProofObligationList caseSInvariantType(SInvariantType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseSInvariantType(node, question);
	}

	@Override
	public ProofObligationList defaultSInvariantType(SInvariantType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultSInvariantType(node, question);
	}

	@Override
	public ProofObligationList caseSMapType(SMapType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseSMapType(node, question);
	}

	@Override
	public ProofObligationList defaultSMapType(SMapType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultSMapType(node, question);
	}

	@Override
	public ProofObligationList caseAOperationType(AOperationType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAOperationType(node, question);
	}

	@Override
	public ProofObligationList caseAOptionalType(AOptionalType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAOptionalType(node, question);
	}

	@Override
	public ProofObligationList caseAParameterType(AParameterType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAParameterType(node, question);
	}

	@Override
	public ProofObligationList caseAProductType(AProductType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAProductType(node, question);
	}

	@Override
	public ProofObligationList caseAQuoteType(AQuoteType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAQuoteType(node, question);
	}

	@Override
	public ProofObligationList caseSSeqType(SSeqType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseSSeqType(node, question);
	}

	@Override
	public ProofObligationList defaultSSeqType(SSeqType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultSSeqType(node, question);
	}

	@Override
	public ProofObligationList caseASetType(ASetType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseASetType(node, question);
	}

	@Override
	public ProofObligationList caseAUndefinedType(AUndefinedType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAUndefinedType(node, question);
	}

	@Override
	public ProofObligationList caseAUnionType(AUnionType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAUnionType(node, question);
	}

	@Override
	public ProofObligationList caseAUnknownType(AUnknownType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAUnknownType(node, question);
	}

	@Override
	public ProofObligationList caseAUnresolvedType(AUnresolvedType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAUnresolvedType(node, question);
	}

	@Override
	public ProofObligationList caseAVoidReturnType(AVoidReturnType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAVoidReturnType(node, question);
	}

	@Override
	public ProofObligationList caseAVoidType(AVoidType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAVoidType(node, question);
	}

	@Override
	public ProofObligationList caseASeqSeqType(ASeqSeqType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseASeqSeqType(node, question);
	}

	@Override
	public ProofObligationList caseASeq1SeqType(ASeq1SeqType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseASeq1SeqType(node, question);
	}

	@Override
	public ProofObligationList caseAInMapMapType(AInMapMapType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAInMapMapType(node, question);
	}

	@Override
	public ProofObligationList caseAMapMapType(AMapMapType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAMapMapType(node, question);
	}

	@Override
	public ProofObligationList caseANamedInvariantType(
			ANamedInvariantType node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseANamedInvariantType(node, question);
	}

	@Override
	public ProofObligationList caseARecordInvariantType(
			ARecordInvariantType node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseARecordInvariantType(node, question);
	}

	@Override
	public ProofObligationList caseABooleanBasicType(ABooleanBasicType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseABooleanBasicType(node, question);
	}

	@Override
	public ProofObligationList caseACharBasicType(ACharBasicType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseACharBasicType(node, question);
	}

	@Override
	public ProofObligationList caseSNumericBasicType(SNumericBasicType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseSNumericBasicType(node, question);
	}

	@Override
	public ProofObligationList defaultSNumericBasicType(SNumericBasicType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultSNumericBasicType(node, question);
	}

	@Override
	public ProofObligationList caseATokenBasicType(ATokenBasicType node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseATokenBasicType(node, question);
	}

	@Override
	public ProofObligationList caseAIntNumericBasicType(
			AIntNumericBasicType node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAIntNumericBasicType(node, question);
	}

	@Override
	public ProofObligationList caseANatOneNumericBasicType(
			ANatOneNumericBasicType node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseANatOneNumericBasicType(node, question);
	}

	@Override
	public ProofObligationList caseANatNumericBasicType(
			ANatNumericBasicType node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseANatNumericBasicType(node, question);
	}

	@Override
	public ProofObligationList caseARationalNumericBasicType(
			ARationalNumericBasicType node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseARationalNumericBasicType(node, question);
	}

	@Override
	public ProofObligationList caseARealNumericBasicType(
			ARealNumericBasicType node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseARealNumericBasicType(node, question);
	}

	@Override
	public ProofObligationList defaultPField(PField node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultPField(node, question);
	}

	@Override
	public ProofObligationList caseAFieldField(AFieldField node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAFieldField(node, question);
	}

	@Override
	public ProofObligationList defaultPAccessSpecifier(PAccessSpecifier node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultPAccessSpecifier(node, question);
	}

	@Override
	public ProofObligationList caseAAccessSpecifierAccessSpecifier(
			AAccessSpecifierAccessSpecifier node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAAccessSpecifierAccessSpecifier(node, question);
	}

	@Override
	public ProofObligationList defaultPAccess(PAccess node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultPAccess(node, question);
	}

	@Override
	public ProofObligationList caseAPublicAccess(APublicAccess node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAPublicAccess(node, question);
	}

	@Override
	public ProofObligationList caseAProtectedAccess(AProtectedAccess node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAProtectedAccess(node, question);
	}

	@Override
	public ProofObligationList caseAPrivateAccess(APrivateAccess node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAPrivateAccess(node, question);
	}

	@Override
	public ProofObligationList defaultPPattern(PPattern node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultPPattern(node, question);
	}

	@Override
	public ProofObligationList caseABooleanPattern(ABooleanPattern node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseABooleanPattern(node, question);
	}

	@Override
	public ProofObligationList caseACharacterPattern(ACharacterPattern node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseACharacterPattern(node, question);
	}

	@Override
	public ProofObligationList caseAConcatenationPattern(
			AConcatenationPattern node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAConcatenationPattern(node, question);
	}

	@Override
	public ProofObligationList caseAExpressionPattern(AExpressionPattern node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAExpressionPattern(node, question);
	}

	@Override
	public ProofObligationList caseAIdentifierPattern(AIdentifierPattern node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAIdentifierPattern(node, question);
	}

	@Override
	public ProofObligationList caseAIgnorePattern(AIgnorePattern node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAIgnorePattern(node, question);
	}

	@Override
	public ProofObligationList caseAIntegerPattern(AIntegerPattern node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAIntegerPattern(node, question);
	}

	@Override
	public ProofObligationList caseANilPattern(ANilPattern node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseANilPattern(node, question);
	}

	@Override
	public ProofObligationList caseAQuotePattern(AQuotePattern node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAQuotePattern(node, question);
	}

	@Override
	public ProofObligationList caseARealPattern(ARealPattern node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseARealPattern(node, question);
	}

	@Override
	public ProofObligationList caseARecordPattern(ARecordPattern node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseARecordPattern(node, question);
	}

	@Override
	public ProofObligationList caseASeqPattern(ASeqPattern node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseASeqPattern(node, question);
	}

	@Override
	public ProofObligationList caseASetPattern(ASetPattern node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseASetPattern(node, question);
	}

	@Override
	public ProofObligationList caseAStringPattern(AStringPattern node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAStringPattern(node, question);
	}

	@Override
	public ProofObligationList caseATuplePattern(ATuplePattern node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseATuplePattern(node, question);
	}

	@Override
	public ProofObligationList caseAUnionPattern(AUnionPattern node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAUnionPattern(node, question);
	}

	@Override
	public ProofObligationList defaultPPair(PPair node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultPPair(node, question);
	}

	@Override
	public ProofObligationList caseAPatternTypePair(APatternTypePair node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAPatternTypePair(node, question);
	}

	@Override
	public ProofObligationList caseAPatternListTypePair(
			APatternListTypePair node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAPatternListTypePair(node, question);
	}

	@Override
	public ProofObligationList defaultPBind(PBind node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultPBind(node, question);
	}

	@Override
	public ProofObligationList caseASetBind(ASetBind node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseASetBind(node, question);
	}

	@Override
	public ProofObligationList caseATypeBind(ATypeBind node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseATypeBind(node, question);
	}

	@Override
	public ProofObligationList defaultPMultipleBind(PMultipleBind node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultPMultipleBind(node, question);
	}

	@Override
	public ProofObligationList caseASetMultipleBind(ASetMultipleBind node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseASetMultipleBind(node, question);
	}

	@Override
	public ProofObligationList caseATypeMultipleBind(ATypeMultipleBind node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseATypeMultipleBind(node, question);
	}

	@Override
	public ProofObligationList defaultPPatternBind(PPatternBind node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultPPatternBind(node, question);
	}

	@Override
	public ProofObligationList caseADefPatternBind(ADefPatternBind node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseADefPatternBind(node, question);
	}

	@Override
	public ProofObligationList defaultPDefinition(PDefinition node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultPDefinition(node, question);
	}

	@Override
	public ProofObligationList caseAAssignmentDefinition(
			AAssignmentDefinition node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAAssignmentDefinition(node, question);
	}

	@Override
	public ProofObligationList caseAInstanceVariableDefinition(
			AInstanceVariableDefinition node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAInstanceVariableDefinition(node, question);
	}

	@Override
	public ProofObligationList caseSClassDefinition(SClassDefinition node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseSClassDefinition(node, question);
	}

	@Override
	public ProofObligationList defaultSClassDefinition(SClassDefinition node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultSClassDefinition(node, question);
	}

	@Override
	public ProofObligationList caseAClassInvariantDefinition(
			AClassInvariantDefinition node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAClassInvariantDefinition(node, question);
	}

	@Override
	public ProofObligationList caseAEqualsDefinition(AEqualsDefinition node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAEqualsDefinition(node, question);
	}

	@Override
	public ProofObligationList caseAExternalDefinition(
			AExternalDefinition node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAExternalDefinition(node, question);
	}

	@Override
	public ProofObligationList caseAImplicitFunctionDefinition(
			AImplicitFunctionDefinition node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAImplicitFunctionDefinition(node, question);
	}

	@Override
	public ProofObligationList caseAExplicitOperationDefinition(
			AExplicitOperationDefinition node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAExplicitOperationDefinition(node, question);
	}

	@Override
	public ProofObligationList caseAImplicitOperationDefinition(
			AImplicitOperationDefinition node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAImplicitOperationDefinition(node, question);
	}

	@Override
	public ProofObligationList caseAImportedDefinition(
			AImportedDefinition node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAImportedDefinition(node, question);
	}

	@Override
	public ProofObligationList caseAInheritedDefinition(
			AInheritedDefinition node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAInheritedDefinition(node, question);
	}

	@Override
	public ProofObligationList caseALocalDefinition(ALocalDefinition node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseALocalDefinition(node, question);
	}

	@Override
	public ProofObligationList caseAMultiBindListDefinition(
			AMultiBindListDefinition node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAMultiBindListDefinition(node, question);
	}

	@Override
	public ProofObligationList caseAMutexSyncDefinition(
			AMutexSyncDefinition node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAMutexSyncDefinition(node, question);
	}

	@Override
	public ProofObligationList caseANamedTraceDefinition(
			ANamedTraceDefinition node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseANamedTraceDefinition(node, question);
	}

	@Override
	public ProofObligationList caseAPerSyncDefinition(APerSyncDefinition node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAPerSyncDefinition(node, question);
	}

	@Override
	public ProofObligationList caseARenamedDefinition(ARenamedDefinition node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseARenamedDefinition(node, question);
	}

	@Override
	public ProofObligationList caseAStateDefinition(AStateDefinition node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAStateDefinition(node, question);
	}

	@Override
	public ProofObligationList caseAThreadDefinition(AThreadDefinition node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAThreadDefinition(node, question);
	}

	@Override
	public ProofObligationList caseATypeDefinition(ATypeDefinition node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseATypeDefinition(node, question);
	}

	@Override
	public ProofObligationList caseAUntypedDefinition(AUntypedDefinition node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAUntypedDefinition(node, question);
	}

	@Override
	public ProofObligationList caseAValueDefinition(AValueDefinition node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAValueDefinition(node, question);
	}

	@Override
	public ProofObligationList defaultPTraceDefinition(PTraceDefinition node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultPTraceDefinition(node, question);
	}

	@Override
	public ProofObligationList caseAInstanceTraceDefinition(
			AInstanceTraceDefinition node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAInstanceTraceDefinition(node, question);
	}

	@Override
	public ProofObligationList caseALetBeStBindingTraceDefinition(
			ALetBeStBindingTraceDefinition node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseALetBeStBindingTraceDefinition(node, question);
	}

	@Override
	public ProofObligationList caseALetDefBindingTraceDefinition(
			ALetDefBindingTraceDefinition node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseALetDefBindingTraceDefinition(node, question);
	}

	@Override
	public ProofObligationList caseARepeatTraceDefinition(
			ARepeatTraceDefinition node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseARepeatTraceDefinition(node, question);
	}

	@Override
	public ProofObligationList defaultPTraceCoreDefinition(
			PTraceCoreDefinition node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultPTraceCoreDefinition(node, question);
	}

	@Override
	public ProofObligationList caseAApplyExpressionTraceCoreDefinition(
			AApplyExpressionTraceCoreDefinition node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAApplyExpressionTraceCoreDefinition(node, question);
	}

	@Override
	public ProofObligationList caseABracketedExpressionTraceCoreDefinition(
			ABracketedExpressionTraceCoreDefinition node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseABracketedExpressionTraceCoreDefinition(node, question);
	}

	@Override
	public ProofObligationList caseAConcurrentExpressionTraceCoreDefinition(
			AConcurrentExpressionTraceCoreDefinition node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAConcurrentExpressionTraceCoreDefinition(node, question);
	}

	@Override
	public ProofObligationList caseABusClassDefinition(
			ABusClassDefinition node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseABusClassDefinition(node, question);
	}

	@Override
	public ProofObligationList caseACpuClassDefinition(
			ACpuClassDefinition node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseACpuClassDefinition(node, question);
	}

	@Override
	public ProofObligationList caseASystemClassDefinition(
			ASystemClassDefinition node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseASystemClassDefinition(node, question);
	}

	@Override
	public ProofObligationList caseAClassClassDefinition(
			AClassClassDefinition node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAClassClassDefinition(node, question);
	}

	@Override
	public ProofObligationList defaultPModules(PModules node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultPModules(node, question);
	}

	@Override
	public ProofObligationList defaultPImports(PImports node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultPImports(node, question);
	}

	@Override
	public ProofObligationList caseAModuleImports(AModuleImports node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAModuleImports(node, question);
	}

	@Override
	public ProofObligationList caseAFromModuleImports(AFromModuleImports node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAFromModuleImports(node, question);
	}

	@Override
	public ProofObligationList defaultPImport(PImport node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultPImport(node, question);
	}

	@Override
	public ProofObligationList caseAAllImport(AAllImport node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAAllImport(node, question);
	}

	@Override
	public ProofObligationList caseATypeImport(ATypeImport node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseATypeImport(node, question);
	}

	@Override
	public ProofObligationList caseSValueImport(SValueImport node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseSValueImport(node, question);
	}

	@Override
	public ProofObligationList defaultSValueImport(SValueImport node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultSValueImport(node, question);
	}

	@Override
	public ProofObligationList caseAValueValueImport(AValueValueImport node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAValueValueImport(node, question);
	}

	@Override
	public ProofObligationList caseAFunctionValueImport(
			AFunctionValueImport node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAFunctionValueImport(node, question);
	}

	@Override
	public ProofObligationList caseAOperationValueImport(
			AOperationValueImport node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAOperationValueImport(node, question);
	}

	@Override
	public ProofObligationList defaultPExports(PExports node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultPExports(node, question);
	}

	@Override
	public ProofObligationList caseAModuleExports(AModuleExports node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAModuleExports(node, question);
	}

	@Override
	public ProofObligationList defaultPExport(PExport node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultPExport(node, question);
	}

	@Override
	public ProofObligationList caseAAllExport(AAllExport node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAAllExport(node, question);
	}

	@Override
	public ProofObligationList caseAFunctionExport(AFunctionExport node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAFunctionExport(node, question);
	}

	@Override
	public ProofObligationList caseAOperationExport(AOperationExport node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAOperationExport(node, question);
	}

	@Override
	public ProofObligationList caseATypeExport(ATypeExport node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseATypeExport(node, question);
	}

	@Override
	public ProofObligationList caseAValueExport(AValueExport node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAValueExport(node, question);
	}

	@Override
	public ProofObligationList defaultPStm(PStm node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultPStm(node, question);
	}

	@Override
	public ProofObligationList caseAAlwaysStm(AAlwaysStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAAlwaysStm(node, question);
	}

	@Override
	public ProofObligationList caseAAssignmentStm(AAssignmentStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAAssignmentStm(node, question);
	}

	@Override
	public ProofObligationList caseAAtomicStm(AAtomicStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAAtomicStm(node, question);
	}

	@Override
	public ProofObligationList caseACallObjectStm(ACallObjectStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseACallObjectStm(node, question);
	}

	@Override
	public ProofObligationList caseACallStm(ACallStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseACallStm(node, question);
	}

	@Override
	public ProofObligationList caseACasesStm(ACasesStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseACasesStm(node, question);
	}

	@Override
	public ProofObligationList caseAClassInvariantStm(AClassInvariantStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAClassInvariantStm(node, question);
	}

	@Override
	public ProofObligationList caseACyclesStm(ACyclesStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseACyclesStm(node, question);
	}

	@Override
	public ProofObligationList caseADurationStm(ADurationStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseADurationStm(node, question);
	}

	@Override
	public ProofObligationList caseAElseIfStm(AElseIfStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAElseIfStm(node, question);
	}

	@Override
	public ProofObligationList caseAErrorStm(AErrorStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAErrorStm(node, question);
	}

	@Override
	public ProofObligationList caseAExitStm(AExitStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAExitStm(node, question);
	}

	@Override
	public ProofObligationList caseAForAllStm(AForAllStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAForAllStm(node, question);
	}

	@Override
	public ProofObligationList caseAForIndexStm(AForIndexStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAForIndexStm(node, question);
	}

	@Override
	public ProofObligationList caseAForPatternBindStm(AForPatternBindStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAForPatternBindStm(node, question);
	}

	@Override
	public ProofObligationList caseAIfStm(AIfStm node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAIfStm(node, question);
	}

	@Override
	public ProofObligationList caseALetBeStStm(ALetBeStStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseALetBeStStm(node, question);
	}

	@Override
	public ProofObligationList caseSLetDefStm(SLetDefStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseSLetDefStm(node, question);
	}

	@Override
	public ProofObligationList defaultSLetDefStm(SLetDefStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultSLetDefStm(node, question);
	}

	@Override
	public ProofObligationList caseANotYetSpecifiedStm(
			ANotYetSpecifiedStm node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseANotYetSpecifiedStm(node, question);
	}

	@Override
	public ProofObligationList caseAReturnStm(AReturnStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAReturnStm(node, question);
	}

	@Override
	public ProofObligationList caseSSimpleBlockStm(SSimpleBlockStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseSSimpleBlockStm(node, question);
	}

	@Override
	public ProofObligationList defaultSSimpleBlockStm(SSimpleBlockStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultSSimpleBlockStm(node, question);
	}

	@Override
	public ProofObligationList caseASkipStm(ASkipStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseASkipStm(node, question);
	}

	@Override
	public ProofObligationList caseASpecificationStm(ASpecificationStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseASpecificationStm(node, question);
	}

	@Override
	public ProofObligationList caseAStartStm(AStartStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAStartStm(node, question);
	}

	@Override
	public ProofObligationList caseASubclassResponsibilityStm(
			ASubclassResponsibilityStm node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseASubclassResponsibilityStm(node, question);
	}

	@Override
	public ProofObligationList caseATixeStm(ATixeStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseATixeStm(node, question);
	}

	@Override
	public ProofObligationList caseATrapStm(ATrapStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseATrapStm(node, question);
	}

	@Override
	public ProofObligationList caseAWhileStm(AWhileStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAWhileStm(node, question);
	}

	@Override
	public ProofObligationList caseAPeriodicStm(APeriodicStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAPeriodicStm(node, question);
	}

	@Override
	public ProofObligationList caseADefLetDefStm(ADefLetDefStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseADefLetDefStm(node, question);
	}

	@Override
	public ProofObligationList caseABlockSimpleBlockStm(
			ABlockSimpleBlockStm node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseABlockSimpleBlockStm(node, question);
	}

	@Override
	public ProofObligationList caseANonDeterministicSimpleBlockStm(
			ANonDeterministicSimpleBlockStm node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseANonDeterministicSimpleBlockStm(node, question);
	}

	@Override
	public ProofObligationList defaultPStateDesignator(PStateDesignator node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultPStateDesignator(node, question);
	}

	@Override
	public ProofObligationList caseAFieldStateDesignator(
			AFieldStateDesignator node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAFieldStateDesignator(node, question);
	}

	@Override
	public ProofObligationList caseAIdentifierStateDesignator(
			AIdentifierStateDesignator node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAIdentifierStateDesignator(node, question);
	}

	@Override
	public ProofObligationList caseAMapSeqStateDesignator(
			AMapSeqStateDesignator node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAMapSeqStateDesignator(node, question);
	}

	@Override
	public ProofObligationList defaultPObjectDesignator(PObjectDesignator node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultPObjectDesignator(node, question);
	}

	@Override
	public ProofObligationList caseAApplyObjectDesignator(
			AApplyObjectDesignator node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAApplyObjectDesignator(node, question);
	}

	@Override
	public ProofObligationList caseAFieldObjectDesignator(
			AFieldObjectDesignator node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAFieldObjectDesignator(node, question);
	}

	@Override
	public ProofObligationList caseAIdentifierObjectDesignator(
			AIdentifierObjectDesignator node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAIdentifierObjectDesignator(node, question);
	}

	@Override
	public ProofObligationList caseANewObjectDesignator(
			ANewObjectDesignator node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseANewObjectDesignator(node, question);
	}

	@Override
	public ProofObligationList caseASelfObjectDesignator(
			ASelfObjectDesignator node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseASelfObjectDesignator(node, question);
	}

	@Override
	public ProofObligationList defaultPAlternativeStm(PAlternativeStm node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultPAlternativeStm(node, question);
	}

	@Override
	public ProofObligationList caseACaseAlternativeStm(
			ACaseAlternativeStm node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseACaseAlternativeStm(node, question);
	}

	@Override
	public ProofObligationList defaultPStmtAlternative(PStmtAlternative node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultPStmtAlternative(node, question);
	}

	@Override
	public ProofObligationList caseATixeStmtAlternative(
			ATixeStmtAlternative node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseATixeStmtAlternative(node, question);
	}

	@Override
	public ProofObligationList defaultPClause(PClause node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultPClause(node, question);
	}

	@Override
	public ProofObligationList caseAExternalClause(AExternalClause node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAExternalClause(node, question);
	}

	@Override
	public ProofObligationList defaultPCase(PCase node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultPCase(node, question);
	}

	@Override
	public ProofObligationList caseAErrorCase(AErrorCase node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAErrorCase(node, question);
	}

	@Override
	public ProofObligationList defaultNode(Node node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultNode(node, question);
	}

	@Override
	public ProofObligationList defaultToken(Token node, POContextStack question) {
		// TODO Auto-generated method stub
		return super.defaultToken(node, question);
	}


	
	
}
