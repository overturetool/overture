package org.overture.pog.visitors;

import java.util.List;

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
import org.overture.ast.definitions.ARenamedDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.ASystemClassDefinition;
import org.overture.ast.definitions.AThreadDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.AUntypedDefinition;
import org.overture.ast.definitions.AValueDefinition;
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
import org.overture.ast.expressions.PExp;
import org.overture.ast.patterns.PPattern;
import org.overture.pog.obligations.FuncPostConditionObligation;
import org.overture.pog.obligations.POContextStack;
import org.overture.pog.obligations.POFunctionDefinitionContext;
import org.overture.pog.obligations.POFunctionResultContext;
import org.overture.pog.obligations.ParameterPatternObligation;
import org.overture.pog.obligations.ProofObligationList;
import org.overture.pog.obligations.SubTypeObligation;
import org.overture.typecheck.TypeComparator;
import org.overturetool.vdmj.lex.LexNameList;

public class PogDefinitionVisitor extends
		QuestionAnswerAdaptor<POContextStack, ProofObligationList> {

	final private QuestionAnswerAdaptor<POContextStack, ProofObligationList> rootVisitor;

	public PogDefinitionVisitor(PogVisitor pogVisitor) {
		this.rootVisitor = pogVisitor;
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
		LexNameList pids = new LexNameList();

		// add all defined names from the function parameter list
		for (List<PPattern> patterns : node.getParamPatternList())
			for (PPattern p : patterns)
				for (PDefinition def : p.getDefinitions())
					pids.add(def.getName());

		// check for duplicates
		if (pids.hasDuplicates()) {
			obligations.add(new ParameterPatternObligation(node, question));
		}

		// do proof obligations for the pre-condition
		PExp precondition = node.getPrecondition();
		if (precondition != null) {
			question.push(new POFunctionDefinitionContext(node, false));
			obligations.addAll(precondition.apply(this, question));
			question.pop();
		}

		// do proof obligations for the post-condition
		PExp postcondition = node.getPostcondition();
		if (postcondition != null) {
			question.push(new POFunctionDefinitionContext(node, false));
			obligations.add(new FuncPostConditionObligation(node, question));
			question.push(new POFunctionResultContext(node));
			obligations.addAll(postcondition.apply(this, question));
			question.pop();
			question.pop();
		}

		// do proof obligations for the function body
		question.push(new POFunctionDefinitionContext(node, true));
		PExp body = node.getBody();
		obligations.addAll(body.apply(rootVisitor, question));

		// do proof obligation for the return type
		if (node.getIsUndefined()
				|| !TypeComparator.isSubType(node.getActualResult(),
						node.getExpectedResult())) {
			obligations.add(new SubTypeObligation(node, node
					.getExpectedResult(), node.getActualResult(), question));
		}
		question.pop();

		return obligations;
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
		return super
				.caseABracketedExpressionTraceCoreDefinition(node, question);
	}

	@Override
	public ProofObligationList caseAConcurrentExpressionTraceCoreDefinition(
			AConcurrentExpressionTraceCoreDefinition node,
			POContextStack question) {
		// TODO Auto-generated method stub
		return super.caseAConcurrentExpressionTraceCoreDefinition(node,
				question);
	}
	
	@Override
	public ProofObligationList caseAClassClassDefinition(
			AClassClassDefinition node, POContextStack question) {

		ProofObligationList proofObligationList = new ProofObligationList();
		
		for(PDefinition def : node.getDefinitions())
		{
			proofObligationList.addAll(def.apply(this,question));
		}
		return proofObligationList;
	}

}
