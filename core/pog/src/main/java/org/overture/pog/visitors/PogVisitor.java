
package org.overture.pog.visitors;


import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.ACaseAlternative;
import org.overture.ast.expressions.ACasesExp;
import org.overture.ast.expressions.AHeadUnaryExp;
import org.overture.ast.expressions.AMapCompMapExp;
import org.overture.ast.expressions.AMapUnionBinaryExp;
import org.overture.ast.expressions.PAlternative;
import org.overture.ast.expressions.PExp;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.patterns.AIgnorePattern;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.patterns.assistants.PatternList;
import org.overture.ast.statements.PCase;
import org.overture.ast.types.assistants.PTypeAssistant;
import org.overture.pog.assistants.PDefinitionAssistantPOG;
import org.overture.pog.obligations.CasesExhaustiveObligation;
import org.overture.pog.obligations.FiniteMapObligation;
import org.overture.pog.obligations.FuncPostConditionObligation;
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
import org.overture.pog.obligations.SubTypeObligation;
import org.overture.typecheck.TypeComparator;
import org.overturetool.vdmj.lex.LexNameList;

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


	
	
}
