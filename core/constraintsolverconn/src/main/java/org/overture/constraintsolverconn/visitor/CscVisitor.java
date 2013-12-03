package org.overture.constraintsolverconn.visitor;

import java.util.LinkedList;
import java.util.LinkedHashSet;
import java.util.Map;

import org.overture.ast.patterns.ATypeBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.ACaseAlternative;
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
import org.overture.ast.expressions.AExists1Exp;
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
import org.overture.ast.expressions.ADistConcatUnaryExp;
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
import org.overture.ast.expressions.ALetDefExp;
import org.overture.ast.expressions.ACasesExp;
import org.overture.ast.expressions.ATupleExp;
import org.overture.ast.expressions.AMkBasicExp;
import org.overture.ast.patterns.ASetPattern;
import org.overture.ast.expressions.AFieldNumberExp;

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
		    String left = node.getLeft().apply(this,"");
		    String right = node.getRight().apply(this,"");
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
		    String left = node.getLeft().apply(this, "");
		    String right = node.getRight().apply(this, "");
			answer = "(" + left + ")"+ (MyDebug ? " *or* " : " or ") + right;
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
		    String left = node.getLeft().apply(this, "");
		    String right = node.getRight().apply(this, "");
			answer = "(" + left + ")" + (MyDebug ? " *=>* " : " => ") + right;
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
		    String left = node.getLeft().apply(this, "");
		    String right = node.getRight().apply(this,"");
			answer = "(" + left + ")" + (MyDebug ? " *<=>* " : " <=> ") + "(" + right + ")";
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
		
		String answer = (MyDebug ? " u-plus " : "") + right;
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
		
		String answer = "(" + left + ")" + (MyDebug ? " times " : " * ") + "(" + right + ")";
		if(MyDebug) answer = "(" + answer + ")";
		
		return answer;
	}

	@Override
	public String caseADivideNumericBinaryExp(ADivideNumericBinaryExp node,
			String question) throws AnalysisException {
		
		String left = node.getLeft().apply(this, "Some information #9");
		String right = node.getRight().apply(this, "Some information #10");
		
		String answer = "(" + left + ")" + (MyDebug ? " divedes " : " / ") + "(" + right + ")";
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
		    answer = left + " - (" + right + ") * ( (" + left + ") / (" + right + ") )";
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
		    // iterate for map
		    answer = "iterate(" + left + ", " + right+")";

		} else {
		    // power for number
		    answer = "(" + left + ")" + (MyDebug ? " starstar " : " ** ") + "(" + right + ")";
		}
		if(MyDebug) answer = "(" + answer + ")";
				
		return answer;
	}

	
	@Override
	public String caseACharLiteralExp(ACharLiteralExp node,
			String question) throws AnalysisException {
		String answer="not support ";
		if(node.getType().toString().equals("char")) {
			answer+= node.toString();
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
		
	    //System.out.println("Question is: " + question);	
	    String answer = node.getValue().toString(); 
		if(MyDebug) answer = "(" + answer + ")";
		
		return answer;
	}

	@Override
	public String caseAStringLiteralExp(AStringLiteralExp node, String question)
			throws AnalysisException {
		
	    //System.out.println("Question is: " + question);	
		String answer = node.getValue().toString(); 
		if(MyDebug) answer = "(" + answer + ")";
		
		return answer;
	}
	
	@Override
	public String caseASetEnumSetExp(ASetEnumSetExp node, String question)
			throws AnalysisException {
		
	    String answer = "{";
	    LinkedList<PExp> memList = node.getMembers();

	    for(PExp mem : memList) {
		answer += (mem.apply(this, "") + ", ");
	    }
	    if(answer.length()>=2)
		answer = answer.substring(0, answer.length()-2);
	    answer+="}";
		if(MyDebug) answer = "(" + answer + ")";
		
		return answer;
	}

	@Override
	public String caseASetUnionBinaryExp(ASetUnionBinaryExp node, String question)
			throws AnalysisException {

		String left = node.getLeft().apply(this,"Some information #1");
		String right = node.getRight().apply(this,"Some information #2");
		
		String answer = MyDebug ? ("("+left +")"+ " *union* " + "("+right+")")
				: ("("+left +")"+ " \\/ " + "("+right+")");
		if(MyDebug) answer = "(" + answer + ")";
		
		return answer;
	}

	@Override
	public String caseASetIntersectBinaryExp(ASetIntersectBinaryExp node, String question)
			throws AnalysisException {

		String left = node.getLeft().apply(this,"Some information #27");
		String right = node.getRight().apply(this,"Some information #28");
		
		String answer = MyDebug ? ("("+left +")"+ " *inter* " + "("+right+")")
				: ("("+left+")" + " /\\ " + "("+right+")");

		if(MyDebug) answer = "(" + answer + ")";
		
		return answer;
	}

	@Override
	public String caseASetDifferenceBinaryExp(ASetDifferenceBinaryExp node, String question)
			throws AnalysisException {

		String left = node.getLeft().apply(this,"Some information #29");
		String right = node.getRight().apply(this,"Some information #30");
		
		String answer = MyDebug ? ("("+left+")" + " *\\* " + "("+right+")")
				: ("("+left +")"+ " - " + "("+right+")");
		
		if(MyDebug) answer = "(" + answer + ")";
		return answer;
	}

	@Override
	public String caseASubsetBinaryExp(ASubsetBinaryExp node, String question)
			throws AnalysisException {

		String left = node.getLeft().apply(this,"#31");
		String right = node.getRight().apply(this,"#32");
		
		String answer = MyDebug ? ("("+left+")" + " *subset* " + "("+right+")")
				: ("("+left +")"+ " <: " + "("+right+")");
		if(MyDebug) answer = "(" + answer + ")";
		
		return answer;
	}

	@Override
	public String caseAProperSubsetBinaryExp(AProperSubsetBinaryExp node, String question)
			throws AnalysisException {

		String left = node.getLeft().apply(this,"#33");
		String right = node.getRight().apply(this,"#34");
		
		String answer = MyDebug ? ("("+left+")" + " *psubset* " + "("+right+")")
				: ("("+left+")" + " <<: " + "("+right+")");
		
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

	// under construction

	private void getVars(PExp node, LinkedHashSet<String> vars) {
	  if(node instanceof APlusNumericBinaryExp) {
	    Object temp = ((APlusNumericBinaryExp)node).getLeft().getChildren(true).get("_original");
	    if(temp!=null) {
	      vars.add(temp.toString());
	    } else {
	      getVars((PExp)((APlusNumericBinaryExp)node).getLeft().getChildren(true).get("_left"), vars);
	      getVars((PExp)((APlusNumericBinaryExp)node).getLeft().getChildren(true).get("_right"), vars);
            }
	    temp = ((APlusNumericBinaryExp)node).getRight().getChildren(true).get("_original");
	    if(temp!=null) {
	      vars.add(temp.toString());
	    } else {
	      getVars((PExp)((APlusNumericBinaryExp)node).getRight().getChildren(true).get("_left"), vars);
	      getVars((PExp)((APlusNumericBinaryExp)node).getRight().getChildren(true).get("_right"), vars);
            }
	  } else if(node instanceof ASubtractNumericBinaryExp) {
	    Object temp = ((ASubtractNumericBinaryExp)node).getLeft().getChildren(true).get("_original");
	    if(temp!=null) {
	      vars.add(temp.toString());
	    } else {
	      getVars((PExp)((ASubtractNumericBinaryExp)node).getLeft().getChildren(true).get("_left"), vars);
	      getVars((PExp)((ASubtractNumericBinaryExp)node).getLeft().getChildren(true).get("_right"), vars);
            }
	    temp = ((ASubtractNumericBinaryExp)node).getRight().getChildren(true).get("_original");
	    if(temp!=null) {
	      vars.add(temp.toString());
	    } else {
	      getVars((PExp)((ASubtractNumericBinaryExp)node).getRight().getChildren(true).get("_left"), vars);
	      getVars((PExp)((ASubtractNumericBinaryExp)node).getRight().getChildren(true).get("_right"), vars);
            }
	  } else if(node instanceof ATimesNumericBinaryExp) {
	    Object temp = ((ATimesNumericBinaryExp)node).getLeft().getChildren(true).get("_original");
	    if(temp!=null) {
	      vars.add(temp.toString());
	    } else {
	      getVars((PExp)((ATimesNumericBinaryExp)node).getLeft().getChildren(true).get("_left"), vars);
	      getVars((PExp)((ATimesNumericBinaryExp)node).getLeft().getChildren(true).get("_right"), vars);
            }
	    temp = ((ATimesNumericBinaryExp)node).getRight().getChildren(true).get("_original");
	    if(temp!=null) {
	      vars.add(temp.toString());
	    } else {
	      getVars((PExp)((ATimesNumericBinaryExp)node).getRight().getChildren(true).get("_left"), vars);
	      getVars((PExp)((ATimesNumericBinaryExp)node).getRight().getChildren(true).get("_right"), vars);
            }
	  } else if(node instanceof ADivideNumericBinaryExp) {
	    Object temp = ((ADivideNumericBinaryExp)node).getLeft().getChildren(true).get("_original");
	    //System.out.println(((ADivideNumericBinaryExp)node).getLeft().getChildren(true).get("_original"));
	    //System.out.println(((ADivideNumericBinaryExp)node).getLeft().getChildren(true));
	    if(temp!=null) {
	      vars.add(temp.toString());
	    } else {
	      getVars((PExp)((ADivideNumericBinaryExp)node).getLeft().getChildren(true).get("_left"), vars);
	      getVars((PExp)((ADivideNumericBinaryExp)node).getLeft().getChildren(true).get("_right"), vars);
            }
	    temp = ((ADivideNumericBinaryExp)node).getRight().getChildren(true).get("_original");
	    //System.out.println(((ADivideNumericBinaryExp)node).getRight().getChildren(true).get("_original"));
	    //System.out.println(((ADivideNumericBinaryExp)node).getRight().getChildren(true));
	    if(temp!=null) {
	      vars.add(temp.toString());
	    } else {
	      getVars((PExp)((ADivideNumericBinaryExp)node).getRight().getChildren(true).get("_left"), vars);
	      getVars((PExp)((ADivideNumericBinaryExp)node).getRight().getChildren(true).get("_right"), vars);
            }
	  } else if(node instanceof AUnaryMinusUnaryExp) {
	    Object temp = ((AUnaryMinusUnaryExp)node).getExp().getChildren(true).get("_original");
	    //System.out.println(((AUnaryMinusUnaryExp)node).getExp().getChildren(true));
	    if(temp!=null) {
	      vars.add(temp.toString());
	    } else {
	      getVars((PExp)((AUnaryMinusUnaryExp)node).getExp(), vars);
            }
	  } else if(node instanceof AVariableExp) {
	    vars.add(((AVariableExp)node).getName().toString());
	  }
	}

	@Override
	public String caseASetCompSetExp(ASetCompSetExp node, String question)
			throws AnalysisException {

	  LinkedHashSet<String> vars = new LinkedHashSet<String>();
	  PExp fst = node.getFirst();
	  getVars(fst, vars);
	    String doms="";
	    String ends="";
	    String varList="";
	    String first="";

	    varList="target,";
	    first = " & target = " + node.getFirst().toString();
	    for(String mem : vars) {
	      doms+="dom(";
	      ends+=")";
	      varList+=(mem+", ");
	    }

	    varList=varList.substring(0, varList.length()-2);
	
	    LinkedList<PMultipleBind> blist = node.getBindings();

	    String bind="";
	    for(int i=0;i<blist.size();i++) {
		String temp = blist.get(i).apply(this, "compset");
		for(int j=0;j<blist.get(i).getPlist().size();j++) {
		    bind+=(blist.get(i).getPlist().get(j).toString() + " : " + temp + " & ");
		}
	    }

	    bind = bind.substring(0, bind.length()-3);
		
	    String pred = node.getPredicate().apply(this,"#42");

	    String answer = doms + "{" + varList + " | " + bind + " & " + pred + first + "}" + ends;
	    
	    if(MyDebug) answer = "(" + answer + ")";

	    return answer;
	}

	/*
	@Override
	public String caseASetBind(ASetBind node, String question)
			throws AnalysisException {
	    // return set
	    //System.out.println("left?: " + node.toString());
	    String answer=node.getSet().toString();
	    return answer;
	}
	*/


	@Override
	public String caseASetMultipleBind(ASetMultipleBind node, String question)
			throws AnalysisException {
	    // return set
	    //String var = node.getPlist().getFirst().apply(this, "var");
	    //String answer=node.getSet().toString();
	    String answer=node.getSet().apply(this,"");
	    return answer;
	}


	// not yet check
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

	// not yet check
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
		vars = "target={"  + vars + "|";
		bindings = bindings.substring(0,bindings.length()-3);

		String pred = node.getPredicate().apply(this,"#43");

		String answer = MyDebug ? ( vars + "???" + bindings + "*???*" + pred)
		    : (vars + bindings + " & " + pred + "} & card(target)>0");

		if(MyDebug) answer = "(" + answer + ")";
		return answer;
	}
	
	
	// not yet check
	@Override
	public String caseAExists1Exp(AExists1Exp node, String question)
			throws AnalysisException {

	    String answer = "";
	    String var = node.getBind().getPattern().toString();
	    String bind = node.getBind().toString().replace("in set", " : ");
	    String pred = node.getPredicate().apply(this,"");

	    answer = "target={" + var + " | " + bind + " & " + pred + "} & card(target)=1";

	    return answer;
	}

	/*
	@Override
	public String caseASeqEnumSeqExp(ASeqEnumSeqExp node, String question)
			throws AnalysisException {
	    String answer = node.getMembers().toString();

		if(MyDebug) answer = "(" + answer + ")";
		return answer;
	}
	*/


	@Override
	public String caseASeqEnumSeqExp(ASeqEnumSeqExp node, String question)
			throws AnalysisException {
	    String answer="[";
	    LinkedList<PExp> seqList = node.getMembers();
	    for(PExp mem : seqList) {
		answer+=(mem.apply(this,"")+", ");
	    }
	    answer = answer.substring(0, answer.length()-2);
	    answer+="]";

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
	    System.out.println(root);

		LinkedList<PExp> argList = node.getArgs();
		String args="";
		String answer="";

		//System.out.println("!!!Type: " + node.getRoot().getType());

		for(PExp elem : argList) {
		    args+=(elem.apply(this,"#apply")+", ");
		}
		args = args.substring(0, args.length()-2);

		if(node.getRoot().getType().toString().indexOf("map") == 0) {
		    answer = MyDebug ? (root + "*map apply* " + args)
			: ( "{ target } = " + root + "[ {" + args + "} ] "); // tricky
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
		    : ("("+left+")" + " ^ " + "("+right+")");
		
		if(MyDebug) answer = "(" + answer + ")";
		return answer;
	}

	@Override
	public String caseADistConcatUnaryExp(ADistConcatUnaryExp node, String question)
			throws AnalysisException {

	    String answer="";
	    //System.out.println("DistConcatU getExp: " + node.getExp());
	    LinkedList<PExp> aseq = ((ASeqEnumSeqExp)node.getExp()).getMembers();
	    answer = aseq.get(0).toString();
	    for(int i=1;i<aseq.size();i++) {
		if(answer.indexOf(aseq.get(i).apply(this,""))==-1) {
		    answer+=(", " + aseq.get(i).toString());
		}
	    }
	    //answer = "target = conc( [" + answer + "]) & " + aseq.toString() + " = [" + answer + "]";
	    answer = "conc( [" + answer + "])";
	    return answer;
	}

	@Override
	public String caseAMapEnumMapExp(AMapEnumMapExp node, String question)
			throws AnalysisException {
		String answer = node.toString();
		//System.out.println("!!! map enum map: " + answer);
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
		    : ("("+left+")" + " \\/ " + "("+right+")");
		
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

		//System.out.println("Type: " + node.getLeft().getType());

		// map ++ map
		if(node.getLeft().getType().toString().indexOf("map") == 0) {
		    answer = MyDebug ? (left + "*++* " + right)
			: ("("+left+")" +  " <+ " + "("+right+")");
		} else {
                // seq ++ map
		    answer = MyDebug ? (left + "*++* " + right)
			: ( "("+left+")" + " <+ " + "("+right+")" );
		    
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
				: ("("+ left +")"+ " <| " + "("+right+")" );
		
		if(MyDebug) answer = "(" + answer + ")";
		return answer;
	}

	@Override
	public String caseADomainResByBinaryExp(ADomainResByBinaryExp node, String question)
			throws AnalysisException {

	        String left = node.getLeft().apply(this,"#51-5");
		String right = node.getRight().apply(this,"#51-6");
		
		String answer = MyDebug ? (left + "*<-:* " + right)
				: ( "("+left +")"+ " <<| " + "("+right+")" );
		
		if(MyDebug) answer = "(" + answer + ")";
		return answer;
	}

	@Override
	public String caseARangeResToBinaryExp(ARangeResToBinaryExp node, String question)
			throws AnalysisException {

	        String left = node.getLeft().apply(this,"#51-5");
		String right = node.getRight().apply(this,"#51-6");
		
		String answer = MyDebug ? (left + "*:>* " + right)
				: ( "("+left +")"+ " |> " + "("+right+")" );
		
		if(MyDebug) answer = "(" + answer + ")";
		return answer;
	}

	@Override
	public String caseARangeResByBinaryExp(ARangeResByBinaryExp node, String question)
			throws AnalysisException {

	        String left = node.getLeft().apply(this,"#51-5");
		String right = node.getRight().apply(this,"#51-6");
		
		String answer = MyDebug ? (left + "*:->* " + right)
				: ( "("+left +")"+ " |>> " + "("+right+")" );
		
		if(MyDebug) answer = "(" + answer + ")";
		return answer;
	}

	@Override
	public String caseACompBinaryExp(ACompBinaryExp node, String question)
			throws AnalysisException {

	        String left = node.getLeft().apply(this,"#51-7");
		String right = node.getRight().apply(this,"#51-8");
		
		String answer = MyDebug ? (left + "*comp* " + right)
		    : ( "( " + "("+right +")"+ " ; " + "("+left+")" + " )");
		
		if(MyDebug) answer = "(" + answer + ")";
		return answer;
	}

	@Override
	public String caseAMapInverseUnaryExp(AMapInverseUnaryExp node, String question)
			throws AnalysisException {

		String map = node.getExp().apply(this,"#51-9");
		
		String answer = MyDebug ? ("*inverse* " + map)
				: ( "("+map + ")~");
		
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
		String elsepart="";
		String elseifpart = "";
	        String ifpart = node.getTest().apply(this,"test");
	        String thenpart = node.getThen().apply(this,"then");
		if(!node.getThen().getType().equals("bool")) {
		    thenpart = "target="+thenpart;  //tricky
		}

		//System.out.println("Type of then part: " + node.getThen().getType().toString());
		LinkedList<AElseIfExp> elselist= node.getElseList();

		int countElseIf=0;
		for(AElseIfExp subnode : elselist) {
		    elseifpart += caseAElseIfExp(subnode, question);
		    countElseIf++;
		}

	        //System.out.println("Type of else part: " + node.getElse().getType());
		if(!node.getElse().getType().toString().equals("bool")) {
		    elsepart = "target=";    //tricky
		}
	        elsepart += node.getElse().apply(this,"else");
		//System.out.println("In caseAIfExp");
		answer = "((" + ifpart + ") & (" + thenpart + ")) or ((not(" + ifpart + ")) & (" + elseifpart;
		answer+=((elseifpart.equals("") ? "" : "(") + elsepart + (elseifpart.equals("") ? "" : ")" ) + "))");
		for(int i=0;i<countElseIf;i++) {
		    answer+=")";
		}
		return answer;
	}

	@Override
	public String caseAElseIfExp(AElseIfExp node, String question)
			throws AnalysisException {
	        String answer="";
		String thenpart="";
	        //String ifpart = node.getTest().apply(this,"test");
	        String elseifpart = node.getElseIf().apply(this,"else");

		if(!node.getThen().getType().equals("bool")) {
		    thenpart = "target=";    //tricky
		}
	        thenpart += node.getThen().apply(this,"then");
		//System.out.println("In caseAIfExp");
		answer = "((" + elseifpart + ") & (" + thenpart + ")) or ((not(" + elseifpart + ")) & ";
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
		pdef = pdef.replace("int", "INT").replace("nat", "NAT").replace("int1", "INT1");
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
	public String caseALetDefExp(ALetDefExp node, String question)
			throws AnalysisException {
			LinkedList<PDefinition> localdefs = node.getLocalDefs();
		for(PDefinition pdef: localdefs) {
		    System.out.println("Under construction");
			System.out.println(pdef.toString());
			System.out.println("\t" + pdef.getType().toString());
			//System.out.println("\t" + pdef.getPattern().toString());
			//System.out.println("\t" + pdef.getName().toString());
			
		}
	        String answer = node.getExpression().apply(this,"let");

		return answer;
	}

	@Override
	public String caseATupleExp(ATupleExp node, String question)
			throws AnalysisException {

		String answer="";
		LinkedList<PExp> list = node.getArgs();

		answer+=(list.get(0).apply(this,"arg") + "|->");
		for(int i=1;i<list.size();i++) {
			String temp = list.get(i).apply(this, "arg");
			answer = "("+ answer + temp + ")|->";
		}

		answer = answer.substring(0,answer.length()-3);
		return answer;
	}

	@Override
	public String caseAFieldNumberExp(AFieldNumberExp node, String question)
			throws AnalysisException {
	        //System.out.println(node.toString());
		String answer="";
		String tuple = node.getTuple().apply(this,"");
		String[] boo = tuple.split("\\|->");
		int args = boo.length; // args>=2
		//System.out.println(args);

		String field = node.getField().toString();
		int pos = Integer.parseInt(field);

		if(args==pos) {
		    answer = "ran({" + tuple + "})";
		} else if(pos==1 && args==2) {
		    answer = "dom({" + tuple + "})";
		} else {
		    String end=")";
		    answer="dom(";
		    for(int i=pos;i<args-1;i++) {
			answer+="dom(";
			end+=")";
		    }
		    if(pos!=1) {
			answer = "ran(" + answer;
			end+=")";
		    }
		    answer+=("{" + tuple + "}" + end);
		}
		answer = "{target} = " + answer; // tricky

		return answer;
	}

	@Override
	public String caseAMkBasicExp(AMkBasicExp node, String question)
			throws AnalysisException {
		String answer=node.getArg().apply(this, "mk_token");
		answer = "\"" + node.toString() + "\"";	
		return answer;
	}

	@Override
	public String caseACasesExp(ACasesExp node, String question)
			throws AnalysisException {
		//String answer = node.toString();
		String answer="";
		String exp = node.getExpression().apply(this, "cases exp");

		LinkedList<ACaseAlternative> list = node.getCases();

		for(int i=0;i<list.size();i++) {
		    String[] cs = list.get(i).toString().split("->");
		    answer+=("((" + exp + "=" + cs[0] + ") & ( target = " + cs[1] + ")) or ");
		}
		
		if(node.getOthers()!=null) {
		    answer+= ("(target = " + node.getOthers().apply(this,"") + ")");
		} else {
		    answer+="TRUE";
		}
		return answer;
	}
	
	@Override
	public String caseANotYetSpecifiedStm(ANotYetSpecifiedStm node,
			String question) throws AnalysisException {
		
	    //System.out.println("Got here..");
		
		return "by caseANotYetSpecifiedStm";
	}
	
}
//
// end of file
//
