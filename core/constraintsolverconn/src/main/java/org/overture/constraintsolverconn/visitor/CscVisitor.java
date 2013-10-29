package org.overture.constraintsolverconn.visitor;

import java.util.LinkedList;

import org.overture.ast.patterns.ATypeBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.expressions.AIntLiteralExp;
import org.overture.ast.expressions.AStringLiteralExp;
import org.overture.ast.expressions.ABooleanConstExp;
import org.overture.ast.expressions.ANotUnaryExp;
import org.overture.ast.expressions.AAndBooleanBinaryExp;
import org.overture.ast.expressions.AOrBooleanBinaryExp;
import org.overture.ast.expressions.AImpliesBooleanBinaryExp;
import org.overture.ast.expressions.AEquivalentBooleanBinaryExp;
import org.overture.ast.expressions.AUnaryMinusUnaryExp;
import org.overture.ast.expressions.AUnaryPlusUnaryExp;
import org.overture.ast.expressions.AAbsoluteUnaryExp;
import org.overture.ast.expressions.PExp;

import org.overture.ast.expressions.APlusNumericBinaryExp;
import org.overture.ast.expressions.ASubtractNumericBinaryExp;
import org.overture.ast.expressions.ATimesNumericBinaryExp;
import org.overture.ast.expressions.ADivideNumericBinaryExp;
import org.overture.ast.expressions.ALessNumericBinaryExp;
import org.overture.ast.expressions.ALessEqualNumericBinaryExp;
import org.overture.ast.expressions.AGreaterNumericBinaryExp;
import org.overture.ast.expressions.AGreaterEqualNumericBinaryExp;
import org.overture.ast.expressions.AEqualsBinaryExp;
import org.overture.ast.expressions.ANotEqualBinaryExp;
import org.overture.ast.expressions.AModNumericBinaryExp;
import org.overture.ast.expressions.AStarStarBinaryExp;
import org.overture.ast.expressions.ACharLiteralExp;
import org.overture.ast.expressions.ASetEnumSetExp;
import org.overture.ast.expressions.ASetUnionBinaryExp;
import org.overture.ast.expressions.ASetIntersectBinaryExp;
import org.overture.ast.expressions.ASetDifferenceBinaryExp;
import org.overture.ast.expressions.ASubsetBinaryExp;
import org.overture.ast.expressions.AProperSubsetBinaryExp;
import org.overture.ast.expressions.ACardinalityUnaryExp;
import org.overture.ast.expressions.ADistUnionUnaryExp;
import org.overture.ast.expressions.ADistIntersectUnaryExp;
import org.overture.ast.expressions.APowerSetUnaryExp;
import org.overture.ast.expressions.AInSetBinaryExp;
import org.overture.ast.expressions.ANotInSetBinaryExp;
import org.overture.ast.expressions.ASetCompSetExp;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.AForAllExp;
import org.overture.ast.expressions.AExistsExp;
//import org.overture.ast.patterns.ASetBind;
import org.overture.ast.patterns.ASetMultipleBind;

import org.overture.ast.expressions.ASeqEnumSeqExp;
import org.overture.ast.expressions.AHeadUnaryExp;
import org.overture.ast.expressions.ATailUnaryExp;
import org.overture.ast.expressions.AApplyExp;
import org.overture.ast.expressions.ALenUnaryExp;
import org.overture.ast.expressions.AElementsUnaryExp;
import org.overture.ast.expressions.AIndicesUnaryExp;
import org.overture.ast.expressions.AReverseUnaryExp;
import org.overture.ast.expressions.ASeqConcatBinaryExp;
import org.overture.ast.expressions.AMapEnumMapExp;
import org.overture.ast.expressions.AMapDomainUnaryExp;
import org.overture.ast.expressions.AMapRangeUnaryExp;
import org.overture.ast.expressions.AMapUnionBinaryExp;
import org.overture.ast.expressions.ADistMergeUnaryExp;
import org.overture.ast.expressions.ADomainResToBinaryExp;
import org.overture.ast.expressions.ADomainResByBinaryExp;
import org.overture.ast.expressions.ARangeResToBinaryExp;
import org.overture.ast.expressions.ARangeResByBinaryExp;
import org.overture.ast.expressions.APlusPlusBinaryExp;
import org.overture.ast.expressions.ACompBinaryExp;
import org.overture.ast.expressions.AMapInverseUnaryExp;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.statements.ANotYetSpecifiedStm;

import org.overture.ast.expressions.AIfExp;
import org.overture.ast.expressions.AElseIfExp;

import org.overture.ast.expressions.ALambdaExp;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.statements.AReturnStm;

public class CscVisitor extends QuestionAnswerAdaptor<String, String>{

	private static final long serialVersionUID = 8993841651356537402L;
	//private static boolean MyDebug = true;
	private static boolean MyDebug = false;
	
	@Override
	public String defaultINode(INode node, String question)
			throws AnalysisException {
		
		System.out.println("Construct not supported: " + node.getClass().getName());
		
		return "by defaultINode";
	}
	
	@Override
	public String caseABooleanConstExp(ABooleanConstExp node,
			String question) throws AnalysisException {
		String answer="";
		//System.out.println("type ? " + node.getType());
		//System.out.println("node ? " + node.toString());
		if(node.getType().toString().equals("bool"))
				if(node.toString().equals("true"))
				answer = "TRUE";
			else if(node.toString().equals("false"))
				answer = "FALSE";
	
		return answer;
	}

	@Override
	public String caseANotUnaryExp(ANotUnaryExp node,
			String question) throws AnalysisException {
		String answer="";

		if(node.getExp().getType().toString().equals("bool")) {
			answer = MyDebug ? " *not* " + node.getExp() : (" not( " + node.getExp()
					+ (MyDebug ? "" : " )"));
		if(MyDebug) answer = "(" + answer + ")";
		}
		return answer;
	}

	@Override
	public String caseAAndBooleanBinaryExp(AAndBooleanBinaryExp node,
			String question) throws AnalysisException {
		String answer="";
		if(node.getLeft().getType().toString().equals("bool") &&
				node.getRight().getType().toString().equals("bool")) {
			String left = node.getLeft().toString();
			String right = node.getRight().toString();
			// and -> &
			answer = left + (MyDebug ? " *and* " : " & ") + right;
		}

		if(MyDebug) answer = "(" + answer + ")";
		
		return answer;
	}

	@Override
	public String caseAOrBooleanBinaryExp(AOrBooleanBinaryExp node,
			String question) throws AnalysisException {
		String answer="";
		if(node.getLeft().getType().toString().equals("bool") &&
				node.getRight().getType().toString().equals("bool")) {
			String left = node.getLeft().toString();
			String right = node.getRight().toString();
			answer = left + (MyDebug ? " *or* " : " or ") + right;
		}
		
		if(MyDebug) answer = "(" + answer + ")";
		
		return answer;
	}

	
	@Override
	public String caseAImpliesBooleanBinaryExp(AImpliesBooleanBinaryExp node,
			String question) throws AnalysisException {
		String answer="";
		if(node.getLeft().getType().toString().equals("bool") &&
				node.getRight().getType().toString().equals("bool")) {
			String left = node.getLeft().toString();
			String right = node.getRight().toString();
			answer = left + (MyDebug ? " *=>* " : " => ") + right;
		}
		
		if(MyDebug) answer = "(" + answer + ")";
		
		return answer;
	}

	@Override
	public String caseAEquivalentBooleanBinaryExp(AEquivalentBooleanBinaryExp node,
			String question) throws AnalysisException {
		String answer="";
		if(node.getLeft().getType().toString().equals("bool") &&
				node.getRight().getType().toString().equals("bool")) {
			String left = node.getLeft().toString();
			String right = node.getRight().toString();
			answer = left + (MyDebug ? " *<=>* " : " <=> ") + right;
		}
		
		if(MyDebug) answer = "(" + answer + ")";
		
		return answer;
	}

	@Override
	public String caseAUnaryMinusUnaryExp(AUnaryMinusUnaryExp node,
			String question) throws AnalysisException {

		String right = node.getExp().apply(this, "Some information #1");
		
		String answer = (MyDebug ? " u-minus " : " - ") + right;
		if(MyDebug) answer = "(" + answer + ")";
		
		return answer;
	}

	
	@Override
	public String caseAUnaryPlusUnaryExp(AUnaryPlusUnaryExp node,
			String question) throws AnalysisException {

		String right = node.getExp().apply(this, "Some information #2");
		
		String answer = (MyDebug ? " u-plus " : " + ") + right;
		if(MyDebug) answer = "(" + answer + ")";
		
		return answer;
	}

	@Override
	public String caseAAbsoluteUnaryExp(AAbsoluteUnaryExp node,
			String question) throws AnalysisException {

		String right = node.getExp().apply(this, "Some information #2");
		
		String answer = MyDebug ? (" absolute " + right) : ("max( {" + "- (" + right + "), " + right + "} )");
		if(MyDebug) answer = "(" + answer + ")";
		
		return answer;
	}


	@Override
	public String caseAPlusNumericBinaryExp(APlusNumericBinaryExp node,
			String question) throws AnalysisException {

		String left = node.getLeft().apply(this, "Some information #3");
		String right = node.getRight().apply(this, "Some information #4");
		
		String answer = left + (MyDebug ? " plus " : " + ") + right;
		if(MyDebug) answer = "(" + answer + ")";
		
		return answer;
	}

	@Override
	public String caseASubtractNumericBinaryExp(ASubtractNumericBinaryExp node,
			String question) throws AnalysisException {
			
	        String left = node.getLeft().apply(this, "Some information #5");
	        String right = node.getRight().apply(this, "Some information #6");
		
		String answer = left + (MyDebug ? " minus " : " - ") + right;
		if(MyDebug) answer = "(" + answer + ")";
		
		return answer;
	}

	@Override
	public String caseATimesNumericBinaryExp(ATimesNumericBinaryExp node,
			String question) throws AnalysisException {
			
		String left = node.getLeft().apply(this, "Some information #7");
		String right = node.getRight().apply(this, "Some information #8");
		
		String answer = left + (MyDebug ? " times " : " * ") + right;
		if(MyDebug) answer = "(" + answer + ")";
		
		return answer;
	}

	@Override
	public String caseADivideNumericBinaryExp(ADivideNumericBinaryExp node,
			String question) throws AnalysisException {
		
		String left = node.getLeft().apply(this, "Some information #9");
		String right = node.getRight().apply(this, "Some information #10");
		
		String answer = left + (MyDebug ? " divedes " : " / ") + right;
		if(MyDebug) answer = "(" + answer + ")";
		
		return answer;
	}

	@Override
	public String caseALessNumericBinaryExp(ALessNumericBinaryExp node,
			String question) throws AnalysisException {
			
		String left = node.getLeft().apply(this, "Some information #11");
		String right = node.getRight().apply(this, "Some information #12");
		
		String answer = left + (MyDebug ? " less " : " < ") + right;
		if(MyDebug) answer = "(" + answer + ")";
				
		return answer;
	}

	@Override
	public String caseALessEqualNumericBinaryExp(ALessEqualNumericBinaryExp node,
			String question) throws AnalysisException {
			
		String left = node.getLeft().apply(this, "Some information #13");
		String right = node.getRight().apply(this, "Some information #14");
		
		String answer = left + (MyDebug ? " lesseq " : " <= ") + right;
		if(MyDebug) answer = "(" + answer + ")";
		
		return answer;
	}

	@Override
	public String caseAGreaterNumericBinaryExp(AGreaterNumericBinaryExp node,
			String question) throws AnalysisException {
			
		String left = node.getLeft().apply(this, "Some information #15");
		String right = node.getRight().apply(this, "Some information #16");
		
		String answer = left + (MyDebug ? " greater " : " > ") + right;
		if(MyDebug) answer = "(" + answer + ")";
				
		return answer;
	}

	@Override
	public String caseAGreaterEqualNumericBinaryExp(AGreaterEqualNumericBinaryExp node,
			String question) throws AnalysisException {
			
		String left = node.getLeft().apply(this, "Some information #17");
		String right = node.getRight().apply(this, "Some information #18");
		
		String answer = left + (MyDebug ? " greatereq " : " >= ") + right;
		if(MyDebug) answer = "(" + answer + ")";
		
		return answer;
	}

	@Override
	public String caseAEqualsBinaryExp(AEqualsBinaryExp node,
			String question) throws AnalysisException {
		String left = node.getLeft().apply(this, "Some information #19");
		String right = node.getRight().apply(this, "Some information #20");
		
		String answer = left + (MyDebug ? " equals " : " = ") + right;
		if(MyDebug) answer = "(" + answer + ")";
		
		return answer;
	}

	@Override
	public String caseANotEqualBinaryExp(ANotEqualBinaryExp node,
			String question) throws AnalysisException {
		String left = node.getLeft().apply(this, "Some information #21");
		String right = node.getRight().apply(this, "Some information #22");
		
		 // <> -> /=
		String answer = left + (MyDebug ? " noteq " : " /= ") + right;
		if(MyDebug) answer = "(" + answer + ")";
				
		return answer;
	}

	@Override
	public String caseAModNumericBinaryExp(AModNumericBinaryExp node,
			String question) throws AnalysisException {
		String left = node.getLeft().apply(this, "Some information #23");
		String right = node.getRight().apply(this, "Some information #24");
		String answer = "";
		if(MyDebug) {
		    answer = left + " modulo "  + right;
		} else {
		    // need to check in ProB again!!
		    answer = left + " - (" + right + ") * ( " + left + " / " + right + " )";
		}

		if(MyDebug) answer = "(" + answer + ")";
				
		return answer;
	}

	@Override
	public String caseAStarStarBinaryExp(AStarStarBinaryExp node,
			String question) throws AnalysisException {
	    String answer = "";
		String left = node.getLeft().apply(this, "Some information #25");
		String right = node.getRight().apply(this, "Some information #26");
		if(node.getLeft().getType().toString().indexOf("map")==0) {
		    answer = "iterate(" + left + ", " + right+")";

		} else {
		    answer = left + (MyDebug ? " starstar " : " ** ") + right;
		}
		if(MyDebug) answer = "(" + answer + ")";
				
		return answer;
	}

	
	@Override
	public String caseACharLiteralExp(ACharLiteralExp node,
			String question) throws AnalysisException {
		String answer="??";
		if(node.getType().toString().equals("char")) {
			answer = node.toString();
		}
		if(MyDebug) answer = "(" + answer + ")";
		return answer;
	}
	
	/*
	@Override
	public String ATimesNumericBinaryExp(ASubtractNumericBinaryExp node,
			String question) throws AnalysisException {
			
		String left = node.getLeft().apply(this, "Some information #1");
		String right = node.getRight().apply(this, "Some information #2");
		
		String answer = "(" + left + " times " + right + ")";
		
		return answer;
	}
*/
	
	@Override
	public String caseAIntLiteralExp(AIntLiteralExp node, String question)
			throws AnalysisException {
		
		System.out.println("Question is: " + question);	
		String answer = node.getValue().toString(); 
		if(MyDebug) answer = "(" + answer + ")";
		
		return answer;
	}

	@Override
	public String caseAStringLiteralExp(AStringLiteralExp node, String question)
			throws AnalysisException {
		
		System.out.println("Question is: " + question);	
		String answer = node.getValue().toString(); 
		if(MyDebug) answer = "(" + answer + ")";
		
		return answer;
	}
	
	@Override
	public String caseASetEnumSetExp(ASetEnumSetExp node, String question)
			throws AnalysisException {
		
		String answer = node.toString();
		if(MyDebug) answer = "(" + answer + ")";
		
		return answer;
	}

	@Override
	public String caseASetUnionBinaryExp(ASetUnionBinaryExp node, String question)
			throws AnalysisException {

		String left = node.getLeft().apply(this,"Some information #1");
		String right = node.getRight().apply(this,"Some information #2");
		
		String answer = MyDebug ? (left + " *union* " + right)
				: (left + " \\/ " + right);
		if(MyDebug) answer = "(" + answer + ")";
		
		return answer;
	}

	@Override
	public String caseASetIntersectBinaryExp(ASetIntersectBinaryExp node, String question)
			throws AnalysisException {

		String left = node.getLeft().apply(this,"Some information #27");
		String right = node.getRight().apply(this,"Some information #28");
		
		String answer = MyDebug ? (left + " *inter* " + right)
				: (left + " /\\ " + right);

		if(MyDebug) answer = "(" + answer + ")";
		
		return answer;
	}

	@Override
	public String caseASetDifferenceBinaryExp(ASetDifferenceBinaryExp node, String question)
			throws AnalysisException {

		String left = node.getLeft().apply(this,"Some information #29");
		String right = node.getRight().apply(this,"Some information #30");
		
		String answer = MyDebug ? (left + " *\\* " + right)
				: (left + " - " + right);
		
		if(MyDebug) answer = "(" + answer + ")";
		return answer;
	}

	@Override
	public String caseASubsetBinaryExp(ASubsetBinaryExp node, String question)
			throws AnalysisException {

		String left = node.getLeft().apply(this,"#31");
		String right = node.getRight().apply(this,"#32");
		
		String answer = MyDebug ? (left + " *subset* " + right)
				: (left + " <: " + right);
		if(MyDebug) answer = "(" + answer + ")";
		
		return answer;
	}

	@Override
	public String caseAProperSubsetBinaryExp(AProperSubsetBinaryExp node, String question)
			throws AnalysisException {

		String left = node.getLeft().apply(this,"#33");
		String right = node.getRight().apply(this,"#34");
		
		String answer = MyDebug ? (left + " *psubset* " + right)
				: (left + " <<: " + right);
		
		if(MyDebug) answer = "(" + answer + ")";
		return answer;
	}

	@Override
	public String caseACardinalityUnaryExp(ACardinalityUnaryExp node, String question)
			throws AnalysisException {

		String right = node.getExp().apply(this,"#35");
		
		String answer = MyDebug ? ("*card* " + right)
				: ("card( " + right + " )");
		
		if(MyDebug) answer = "(" + answer + ")";
		return answer;
	}

	@Override
	public String caseADistUnionUnaryExp(ADistUnionUnaryExp node, String question)
			throws AnalysisException {

		String right = node.getExp().apply(this,"#35-1");
		
		String answer = MyDebug ? ("*union* " + right)
				: ("union( " + right + " )");
		
		if(MyDebug) answer = "(" + answer + ")";
		return answer;
	}

	@Override
	public String caseADistIntersectUnaryExp(ADistIntersectUnaryExp node, String question)
			throws AnalysisException {

		String right = node.getExp().apply(this,"#35-1");
		
		String answer = MyDebug ? ("*inter* " + right)
				: ("inter( " + right + " )");
		
		if(MyDebug) answer = "(" + answer + ")";
		return answer;
	}

	@Override
	public String caseAPowerSetUnaryExp(APowerSetUnaryExp node, String question)
			throws AnalysisException {

		String right = node.getExp().apply(this,"#36");
		
		String answer = MyDebug ? ("*power* " + right)
				: ("POW( " + right + " )");
		
		if(MyDebug) answer = "(" + answer + ")";
		return answer;
	}

	@Override
	public String caseAInSetBinaryExp(AInSetBinaryExp node, String question)
			throws AnalysisException {

	        String left = node.getLeft().apply(this,"#37");
		String right = node.getRight().apply(this,"#38");
		
		String answer = MyDebug ? ( left + "*in set* " + right)
				: (left + " : " + right);
		
		if(MyDebug) answer = "(" + answer + ")";
		return answer;
	}

	@Override
	public String caseANotInSetBinaryExp(ANotInSetBinaryExp node, String question)
			throws AnalysisException {

		String left = node.getLeft().apply(this,"#39");
		String right = node.getRight().apply(this,"#40");
		
		String answer = MyDebug ? ( left + "*not in set* " + right)
				: (left + " /: " + right);
		
		if(MyDebug) answer = "(" + answer + ")";
		return answer;
	}

	@Override
	public String caseAVariableExp(AVariableExp node, String question)
			throws AnalysisException {

		String name = node.getName().toString();

		String answer = name;
		if(MyDebug) answer = "(" + answer + ")";
		return answer;
	}
	
	@Override
	public String caseASetCompSetExp(ASetCompSetExp node, String question)
			throws AnalysisException {
		
	    LinkedList<PMultipleBind> blist = node.getBindings();

		String bind="";
		for(int i=0;i<blist.size();i++) {
		    String temp = blist.get(i).apply(this, "compset");
		    for(int j=0;j<blist.get(i).getPlist().size();j++) {
			bind+=(blist.get(i).getPlist().get(j).toString() + " : " + temp + " & ");
		    }
		}
		bind = bind.substring(0, bind.length()-3);

		String first = node.getFirst().apply(this,"#41");
		
		String pred = node.getPredicate().apply(this,"#42");

		String answer = MyDebug ? ( "{ " + first + " | " + bind + " & " + pred + " }")
				: ( "{ " + first + " | " + bind + " & " + pred + " }");

		if(MyDebug) answer = "(" + answer + ")";
		return answer;
	}

	/*
	@Override
	public String caseASetBind(ASetBind node, String question)
			throws AnalysisException {
	    // return set
	    System.out.println("left?: " + node.toString());
	    String answer=node.getSet().toString();
	    return answer;
	}
	*/

	@Override
	public String caseASetMultipleBind(ASetMultipleBind node, String question)
			throws AnalysisException {
	    // return set
	    //String var = node.getPlist().getFirst().apply(this, "var");
	    String answer=node.getSet().toString();
	    return answer;
	}

	@Override
	public String caseAForAllExp(AForAllExp node, String question)
			throws AnalysisException {

		LinkedList<PMultipleBind> blist = node.getBindList();
		String vars="";
		String bindings="";
		
		for(int i=0;i<blist.size();i++) {
			for(int j=0;j<blist.get(i).getPlist().size();j++) {
			    vars+=(blist.get(i).getPlist().get(j).toString() + ", ");
			    String temp = blist.get(i).apply(this, "forall");
			    bindings+=(blist.get(i).getPlist().get(j).toString() + " : " + temp + " & ");
			}
		}
		vars = vars.substring(0, vars.length()-2);
		vars = "!" + (vars.indexOf(",")>0 ?("(" + vars + ")") :vars) + ".(";
		bindings = bindings.substring(0,bindings.length()-3);

		String pred = node.getPredicate().apply(this,"#43");

		String answer = MyDebug ? ( vars + "???" + bindings + "*???*" + pred)
		    : (vars + bindings + " => " + pred + ")");

		if(MyDebug) answer = "(" + answer + ")";
		return answer;
	}

	@Override
	public String caseAExistsExp(AExistsExp node, String question)
			throws AnalysisException {

		LinkedList<PMultipleBind> blist = node.getBindList();
		String vars="";
		String bindings="";
		
		for(int i=0;i<blist.size();i++) {
			for(int j=0;j<blist.get(i).getPlist().size();j++) {
			    vars+=(blist.get(i).getPlist().get(j).toString() + ", ");
			    String temp = blist.get(i).apply(this, "exists");
			    bindings+=(blist.get(i).getPlist().get(j).toString() + " : " + temp + " & ");
			}
		}
		vars = vars.substring(0, vars.length()-2);
		vars = "#" + (vars.indexOf(",")>0 ?("(" + vars + ")") :vars) + ".(";
		bindings = bindings.substring(0,bindings.length()-3);

		String pred = node.getPredicate().apply(this,"#43");

		String answer = MyDebug ? ( vars + "???" + bindings + "*???*" + pred)
		    : (vars + bindings + " => " + pred + ")");

		if(MyDebug) answer = "(" + answer + ")";
		return answer;
	}
	
	@Override
	public String caseASeqEnumSeqExp(ASeqEnumSeqExp node, String question)
			throws AnalysisException {
	    String answer = node.getMembers().toString();

		if(MyDebug) answer = "(" + answer + ")";
		return answer;
	}
	
	@Override
	public String caseAHeadUnaryExp(AHeadUnaryExp node, String question)
			throws AnalysisException {

		String right = node.getExp().apply(this,"#44");
		
		String answer = MyDebug ? ("*hd* " + right)
				: ("first( " + right + " )");
		
		if(MyDebug) answer = "(" + answer + ")";
		return answer;
	}
	
	@Override
	public String caseATailUnaryExp(ATailUnaryExp node, String question)
			throws AnalysisException {

		String right = node.getExp().apply(this,"#45");
		
		String answer = MyDebug ? ("*tl* " + right)
				: ("tail( " + right + " )");
		
		if(MyDebug) answer = "(" + answer + ")";
		return answer;
	}

	@Override
	public String caseAApplyExp(AApplyExp node, String question)
			throws AnalysisException {

		String root = node.getRoot().apply(this,"#applyexp");
		//check whether root is a sequence or a map

		LinkedList<PExp> argList = node.getArgs();
		String args="";
		String answer="";
		System.out.println("Type: " + node.getRoot().getType());

		for(int i=0;i<argList.size();i++) {
		    args+=(argList.get(i).apply(this,"#apply").toString()+", ");
		}
		args = args.substring(0, args.length()-2);

		if(node.getRoot().getType().toString().indexOf("map") == 0) {
		    answer = MyDebug ? (root + "*map apply* " + args)
			: ( "max( " + root + "[ {" + args + "} ] )");
		} else {
		    answer = MyDebug ? (root + "*seq apply* " + args)
			: ( root + "("  + args + ")");
		}
		if(MyDebug) answer = "(" + answer + ")";
		return answer;
	}
	
	@Override
	public String caseALenUnaryExp(ALenUnaryExp node, String question)
			throws AnalysisException {

		String right = node.getExp().apply(this,"#46");
		
		String answer = MyDebug ? ("*len* " + right)
				: ("size( " + right + " )");
		
		if(MyDebug) answer = "(" + answer + ")";
		return answer;
	}

	@Override
	public String caseAElementsUnaryExp(AElementsUnaryExp node, String question)
			throws AnalysisException {

		String right = node.getExp().apply(this,"#46");
		right = right.replaceAll("^.", "{").replaceAll(".$", "}");
		String answer = MyDebug ? ("*elems* " + right)
				: (right);
		
		if(MyDebug) answer = "(" + answer + ")";
		return answer;
	}	

	@Override
	public String caseAIndicesUnaryExp(AIndicesUnaryExp node, String question)
			throws AnalysisException {

		String right = node.getExp().apply(this,"#46");
		
		String answer = MyDebug ? ("*inds* " + right)
				: ("1..size( " + right + " )");
		
		if(MyDebug) answer = "(" + answer + ")";
		return answer;
	}
	
	@Override
	public String caseAReverseUnaryExp(AReverseUnaryExp node, String question)
			throws AnalysisException {

		String right = node.getExp().apply(this,"#47");
		
		String answer = MyDebug ? ("*reverse* " + right)
				: ("rev( " + right + " )");
		
		if(MyDebug) answer = "(" + answer + ")" ;
		return answer;
	}
	
	
	@Override
	public String caseASeqConcatBinaryExp(ASeqConcatBinaryExp node, String question)
			throws AnalysisException {

		String left = node.getLeft().apply(this,"#48");
		String right = node.getRight().apply(this,"#49");
		
		String answer = MyDebug ? ( left + "*^* " + right)
				: (left + " ^ " + right);
		
		if(MyDebug) answer = "(" + answer + ")";
		return answer;
	}

	@Override
	public String caseAMapEnumMapExp(AMapEnumMapExp node, String question)
			throws AnalysisException {

		String answer = node.toString();
		if(MyDebug) answer = "(" + answer + ")";
		return answer;
	}
	
	@Override
	public String caseAMapDomainUnaryExp(AMapDomainUnaryExp node, String question)
			throws AnalysisException {

		String right = node.getExp().apply(this,"#50");
		
		String answer = MyDebug ? ("*dom* " + right)
				: ("dom( " + right + " )");
		
		if(MyDebug) answer = "(" + answer + ")";
		return answer;
	}
	
	@Override
	public String caseAMapRangeUnaryExp(AMapRangeUnaryExp node, String question)
			throws AnalysisException {

		String right = node.getExp().apply(this,"#51");
		
		String answer = MyDebug ? ("*rng* " + right)
				: ("ran( " + right + " )");
		
		if(MyDebug) answer = "(" + answer + ")";
		return answer;
	}

	@Override
	public String caseAMapUnionBinaryExp(AMapUnionBinaryExp node, String question)
			throws AnalysisException {

		String left = node.getLeft().apply(this,"#51-1");
		String right = node.getRight().apply(this,"#51-2");
		
		String answer = MyDebug ? (left + "*munion* " + right)
				: (left + " \\/ " + right );
		
		if(MyDebug) answer = "(" + answer + ")";
		return answer;
	}

	@Override
	public String caseADistMergeUnaryExp(ADistMergeUnaryExp node, String question)
			throws AnalysisException {

		String right = node.getExp().apply(this,"#51-3");
		
		String answer = MyDebug ? ("*merge* " + right)
				: ("union( " + right + " )");
		
		if(MyDebug) answer = "(" + answer + ")";
		return answer;
	}

	@Override
	public String caseAPlusPlusBinaryExp(APlusPlusBinaryExp node, String question)
			throws AnalysisException {

	        String left = node.getLeft().apply(this,"#51-3");
		String right = node.getRight().apply(this,"#51-3");
		String answer="";
		System.out.println("Type: " + node.getLeft().getType());

		// map ++ map
		if(node.getLeft().getType().toString().indexOf("map") == 0) {
		    answer = MyDebug ? (left + "*++* " + right)
			: (left +  " <+ " + right);
		} else {
                // seq ++ map
		    answer = MyDebug ? (left + "*++* " + right)
			: ( left + " <+ " + right );
		    
		}
		
		if(MyDebug) answer = "(" + answer + ")";
		return answer;
	}

	@Override
	public String caseADomainResToBinaryExp(ADomainResToBinaryExp node, String question)
			throws AnalysisException {

	        String left = node.getLeft().apply(this,"#51-5");
		String right = node.getRight().apply(this,"#51-6");
		
		String answer = MyDebug ? (left + "*<:* " + right)
				: ( left + " <| " + right );
		
		if(MyDebug) answer = "(" + answer + ")";
		return answer;
	}

	@Override
	public String caseADomainResByBinaryExp(ADomainResByBinaryExp node, String question)
			throws AnalysisException {

	        String left = node.getLeft().apply(this,"#51-5");
		String right = node.getRight().apply(this,"#51-6");
		
		String answer = MyDebug ? (left + "*<-:* " + right)
				: ( left + " <<| " + right );
		
		if(MyDebug) answer = "(" + answer + ")";
		return answer;
	}

	@Override
	public String caseARangeResToBinaryExp(ARangeResToBinaryExp node, String question)
			throws AnalysisException {

	        String left = node.getLeft().apply(this,"#51-5");
		String right = node.getRight().apply(this,"#51-6");
		
		String answer = MyDebug ? (left + "*:>* " + right)
				: ( left + " |> " + right );
		
		if(MyDebug) answer = "(" + answer + ")";
		return answer;
	}

	@Override
	public String caseARangeResByBinaryExp(ARangeResByBinaryExp node, String question)
			throws AnalysisException {

	        String left = node.getLeft().apply(this,"#51-5");
		String right = node.getRight().apply(this,"#51-6");
		
		String answer = MyDebug ? (left + "*:->* " + right)
				: ( left + " |>> " + right );
		
		if(MyDebug) answer = "(" + answer + ")";
		return answer;
	}

	@Override
	public String caseACompBinaryExp(ACompBinaryExp node, String question)
			throws AnalysisException {

	        String left = node.getLeft().apply(this,"#51-7");
		String right = node.getRight().apply(this,"#51-8");
		
		String answer = MyDebug ? (left + "*comp* " + right)
				: ( "( " + right + " ; " + left + " )");
		
		if(MyDebug) answer = "(" + answer + ")";
		return answer;
	}

	@Override
	public String caseAMapInverseUnaryExp(AMapInverseUnaryExp node, String question)
			throws AnalysisException {

		String map = node.getExp().apply(this,"#51-9");
		
		String answer = MyDebug ? ("*inverse* " + map)
				: ( map + "~");
		
		if(MyDebug) answer = "(" + answer + ")";
		return answer;
	}

/*
	@Override
	public String caseAIfExp(AIfExp node, String question)
			throws AnalysisException {
	    //System.out.println("In caseAIfExp " + node.toString());
	    //System.out.println("In caseAIfExp " + node.kindPExp());
	        String answer="";
		String elseifpart = "";
	        String ifpart = node.getTest().apply(this,"test");
	        String thenpart = node.getThen().apply(this,"then");
		LinkedList<AElseIfExp> elselist= node.getElseList();

		for(AElseIfExp subnode : elselist) {
		    elseifpart += caseAElseIfExp(subnode, question);
		}

	        String elsepart = node.getElse().apply(this,"else");
		//System.out.println("In caseAIfExp");
		answer = "IF " + ifpart + " THEN " + thenpart
		    + (elseifpart.equals("")?"": elseifpart)
		    + " ELSE " +  elsepart + " END";
		return answer;
	}
*/
	
	@Override
	public String caseAIfExp(AIfExp node, String question)
			throws AnalysisException {
	    //System.out.println("In caseAIfExp " + node.toString());
	    //System.out.println("In caseAIfExp " + node.kindPExp());
	        String answer="";
		String elseifpart = "";
	        String ifpart = node.getTest().apply(this,"test");
	        String thenpart = node.getThen().apply(this,"then");
		LinkedList<AElseIfExp> elselist= node.getElseList();

		for(AElseIfExp subnode : elselist) {
		    elseifpart += caseAElseIfExp(subnode, question);
		}

	        String elsepart = node.getElse().apply(this,"else");
		//System.out.println("In caseAIfExp");
		answer = "((" + ifpart + ") & (" + thenpart + ")) or ( not(" + ifpart + ") & (" + elsepart + "))";
		return answer;
	}

	@Override
	public String caseAElseIfExp(AElseIfExp node, String question)
			throws AnalysisException {
	        String answer="";
	        //String ifpart = node.getTest().apply(this,"test");
	        String elseifpart = node.getElseIf().apply(this,"else");
	        String thenpart = node.getThen().apply(this,"then");
		//System.out.println("In caseAIfExp");
		answer = " ELSEIF " + elseifpart + " THEN " + thenpart;
		return answer;
	}

	@Override
	public String caseALambdaExp(ALambdaExp node, String question)
			throws AnalysisException {
	        String answer = "";
		String ptns = "";
		String pdef = "";
		String exp = "";

		LinkedList<PPattern> pptn = node.getParamPatterns();
		for(PPattern pp : pptn) {
		    ptns+=(pp.apply(this,"")+", ");
		}
		ptns=ptns.substring(0, ptns.length()-2);

		LinkedList<ATypeBind> pdfs = node.getBindList();
		for(ATypeBind pd : pdfs) {
		    pdef+=(pd.apply(this,"") + " & ");
		}
		pdef = pdef.substring(0, pdef.length()-3);

		exp = node.getExpression().apply(this, "");

		answer = "%" + (ptns.indexOf(",")>0 ? "(" : "") + ptns + (ptns.indexOf(",")>0 ? ")":"") + ".( " + pdef + " | " + exp + " )";
		return answer;
	}

	@Override
	public String caseAIdentifierPattern(AIdentifierPattern node, String question)
			throws AnalysisException {
	    String answer = node.getName().toString();
	    //String answer = node.getName().apply(this, ""); //consut not supported: org.overture.ast.lex.LexNameToken

		return answer;
	}

	@Override
	public String caseATypeBind(ATypeBind node, String question)
			throws AnalysisException {
	    String answer = node.toString();

		return answer;
	}

	/*
	@Override
	    public String caseILexNameToken(LexNameToken node, String question)
			throws AnalysisException {
	    String answer = node.getName().apply(this, "");

		return answer;
	}
	*/

	@Override
	public String caseAReturnStm(AReturnStm node, String question)
			throws AnalysisException {
	        String answer = node.getExpression().apply(this,"return");

		return answer;
	}

	@Override
	public String caseANotYetSpecifiedStm(ANotYetSpecifiedStm node,
			String question) throws AnalysisException {
		
		System.out.println("Got here..");
		
		return "by caseANotYetSpecifiedStm";
	}
	
}
//
// end of file
//