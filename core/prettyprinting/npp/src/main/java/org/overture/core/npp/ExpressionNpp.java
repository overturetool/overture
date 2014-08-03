package org.overture.core.npp;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.expressions.AAbsoluteUnaryExp;
import org.overture.ast.expressions.AAndBooleanBinaryExp;
import org.overture.ast.expressions.AApplyExp;
import org.overture.ast.expressions.ABooleanConstExp;
import org.overture.ast.expressions.ACardinalityUnaryExp;
import org.overture.ast.expressions.ACasesExp;
import org.overture.ast.expressions.ACharLiteralExp;
import org.overture.ast.expressions.ACompBinaryExp;
import org.overture.ast.expressions.ADistConcatUnaryExp;
import org.overture.ast.expressions.ADistIntersectUnaryExp;
import org.overture.ast.expressions.ADistUnionUnaryExp;
import org.overture.ast.expressions.ADivNumericBinaryExp;
import org.overture.ast.expressions.ADivideNumericBinaryExp;
import org.overture.ast.expressions.AElementsUnaryExp;
import org.overture.ast.expressions.AEqualsBinaryExp;
import org.overture.ast.expressions.AExists1Exp;
import org.overture.ast.expressions.AExistsExp;
import org.overture.ast.expressions.AForAllExp;
import org.overture.ast.expressions.AGreaterEqualNumericBinaryExp;
import org.overture.ast.expressions.AGreaterNumericBinaryExp;
import org.overture.ast.expressions.AHeadUnaryExp;
import org.overture.ast.expressions.AImpliesBooleanBinaryExp;
import org.overture.ast.expressions.AInSetBinaryExp;
import org.overture.ast.expressions.AIndicesUnaryExp;
import org.overture.ast.expressions.AIntLiteralExp;
import org.overture.ast.expressions.AIotaExp;
import org.overture.ast.expressions.ALenUnaryExp;
import org.overture.ast.expressions.ALessEqualNumericBinaryExp;
import org.overture.ast.expressions.ALessNumericBinaryExp;
import org.overture.ast.expressions.AMapCompMapExp;
import org.overture.ast.expressions.AMapDomainUnaryExp;
import org.overture.ast.expressions.AMapEnumMapExp;
import org.overture.ast.expressions.AMapRangeUnaryExp;
import org.overture.ast.expressions.AMapletExp;
import org.overture.ast.expressions.AModNumericBinaryExp;
import org.overture.ast.expressions.ANotEqualBinaryExp;
import org.overture.ast.expressions.ANotInSetBinaryExp;
import org.overture.ast.expressions.AOrBooleanBinaryExp;
import org.overture.ast.expressions.APlusNumericBinaryExp;
import org.overture.ast.expressions.APlusPlusBinaryExp;
import org.overture.ast.expressions.APowerSetUnaryExp;
import org.overture.ast.expressions.AProperSubsetBinaryExp;
import org.overture.ast.expressions.AQuoteLiteralExp;
import org.overture.ast.expressions.ARealLiteralExp;
import org.overture.ast.expressions.ARemNumericBinaryExp;
import org.overture.ast.expressions.ASeqCompSeqExp;
import org.overture.ast.expressions.ASeqConcatBinaryExp;
import org.overture.ast.expressions.ASeqEnumSeqExp;
import org.overture.ast.expressions.ASetCompSetExp;
import org.overture.ast.expressions.ASetDifferenceBinaryExp;
import org.overture.ast.expressions.ASetEnumSetExp;
import org.overture.ast.expressions.ASetIntersectBinaryExp;
import org.overture.ast.expressions.ASetRangeSetExp;
import org.overture.ast.expressions.ASetUnionBinaryExp;
import org.overture.ast.expressions.ASubsetBinaryExp;
import org.overture.ast.expressions.ASubtractNumericBinaryExp;
import org.overture.ast.expressions.ATailUnaryExp;
import org.overture.ast.expressions.ATimesNumericBinaryExp;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.ASetBind;
import org.overture.ast.patterns.ASetMultipleBind;
import org.overture.ast.types.AMapMapType;

class ExpressionNpp extends QuestionAnswerAdaptor<IndentTracker, String>
		implements IPrettyPrinter
{

	ISymbolTable mytable;
	IPrettyPrinter rootNpp;

	private static String EXPRESSION_NOT_FOUND = "ERROR: Expression Node not found";
	private static String space = " ";
	private static String leftcurly = "{";
	private static String rightcurly = "}";
	private static String leftsq = "[";
	private static String rightsq = "]";
	private static String bar = "|";
	private static String brtab = "\n\t";
	private static String brl = "\n";

	public ExpressionNpp(NewPrettyPrinter root, ISymbolTable nst)
	{
		rootNpp = root;
		mytable = nst;
	}

	@Override
	public void setInsTable(ISymbolTable it)
	{
		mytable = it;
	}

	@Override
	public String caseAPlusNumericBinaryExp(APlusNumericBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS, question);
		String op = mytable.getPLUS();

//		StringBuilder sb = new StringBuilder();
//		
//		sb.append(l);
//		sb.append(space);
//		sb.append(op);
//		sb.append(space);
//		sb.append(r);

		return Utilities.wrap(Utilities.append(l, r, op));
	}
	
	@Override
	public String caseASubtractNumericBinaryExp(ASubtractNumericBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS, question);
		String op = mytable.getMINUS();
		
//		StringBuilder sb = new StringBuilder();
//		
//		sb.append(l);
//		sb.append(space);
//		sb.append(op);
//		sb.append(space);
//		sb.append(r);
		
		return Utilities.wrap(Utilities.append(l, r, op));
	}
	
	@Override
	public String caseATimesNumericBinaryExp(ATimesNumericBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS,question);
		String op = mytable.getTIMES();
		
//		StringBuilder sb = new StringBuilder();
//		
//		sb.append(l);
//		sb.append(space);
//		sb.append(op);
//		sb.append(space);
//		sb.append(r);
		
		return Utilities.wrap(Utilities.append(l, r, op));
	}
	
	@Override
	public String caseADivideNumericBinaryExp(ADivideNumericBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS,question);
		String op = mytable.getDIVIDE();
		
//		StringBuilder sb = new StringBuilder();
//		
//		sb.append(l);
//		sb.append(space);
//		sb.append(op);
//		sb.append(space);
//		sb.append(r);
		
		return Utilities.wrap(Utilities.append(l, r, op));
	}
	
	@Override
	public String caseAModNumericBinaryExp(AModNumericBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS,question);
		String op = mytable.getMOD();
		
//		StringBuilder sb = new StringBuilder();
//		
//		sb.append(l);
//		sb.append(space);
//		sb.append(op);
//		sb.append(space);
//		sb.append(r);
		
		return Utilities.wrap(Utilities.append(l, r, op));
	}
	
	@Override
	public String caseADivNumericBinaryExp(ADivNumericBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS,question);
		String op = mytable.getDIV();
		
//		StringBuilder sb = new StringBuilder();
//		
//		sb.append(l);
//		sb.append(space);
//		sb.append(op);
//		sb.append(space);
//		sb.append(r);
		
		return Utilities.wrap(Utilities.append(l, r, op));
	}
	
	@Override
	public String caseARemNumericBinaryExp(ARemNumericBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS,question);
		String op = mytable.getREM();
		
//		StringBuilder sb = new StringBuilder();
//		
//		sb.append(l);
//		sb.append(space);
//		sb.append(op);
//		sb.append(space);
//		sb.append(r);
		
		return	Utilities.wrap(Utilities.append(l, r, op));
	}
	
	@Override
	public String caseAImpliesBooleanBinaryExp(AImpliesBooleanBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS,question);
		String op = mytable.getIMPLIES();
		
//		StringBuilder sb = new StringBuilder();
//		
//		sb.append(l);
//		sb.append(space);
//		sb.append(op);
//		sb.append(space);
//		sb.append(r);
		
		return	Utilities.wrap(Utilities.append(l, r, op));
	}
	
	@Override
	public String caseAAbsoluteUnaryExp(AAbsoluteUnaryExp node,
			IndentTracker question) throws AnalysisException
	{
		
		String r = node.getExp().apply(THIS,question);
		String op = mytable.getABSOLUTE();
		
//		StringBuilder sb = new StringBuilder();
//		
//		sb.append(op);
//		sb.append(space);
//		sb.append(l);
		
		return Utilities.wrap(Utilities.unaryappend(r, op));
	}
	
	@Override
	public String caseAAndBooleanBinaryExp(AAndBooleanBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS,question);
		String r = node.getRight().apply(THIS, question);
		String op = mytable.getAND();
		
//		StringBuilder sb = new StringBuilder();
//		
//		sb.append(l);
//		sb.append(space);
//		sb.append(op);
//		sb.append(space);
//		sb.append(r);
		
		return Utilities.wrap(Utilities.append(l, r, op));
		
	}
	
	@Override
	public String caseAOrBooleanBinaryExp(AOrBooleanBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS,question);
		String op = mytable.getOR();
		
//		StringBuilder sb = new StringBuilder();
//		
//		sb.append(l);
//		sb.append(space);
//		sb.append(op);
//		sb.append(space);
//		sb.append(r);
		
		return Utilities.wrap(Utilities.append(l, r, op));
	}
	
	@Override
	public String caseAEqualsBinaryExp(AEqualsBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS, question);
		String op = mytable.getEQUALS();
		
//		StringBuilder sb = new StringBuilder();
//		
//		sb.append(l);
//		sb.append(space);
//		sb.append(op);
//		sb.append(space);
//		sb.append(r);
		
		return Utilities.wrap(Utilities.append(l, r, op));
	}
	
	@Override
	public String caseAGreaterNumericBinaryExp(AGreaterNumericBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS, question);
		String op = mytable.getGT();
		
//		StringBuilder sb = new StringBuilder();
//		
//		sb.append(l);
//		sb.append(space);
//		sb.append(op);
//		sb.append(space);
//		sb.append(r);
		
		return Utilities.wrap(Utilities.append(l, r, op));
	}
	
	@Override
	public String caseALessNumericBinaryExp(ALessNumericBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS, question);
		String op = mytable.getLT();
		
//		StringBuilder sb = new StringBuilder();
//		
//		sb.append(l);
//		sb.append(space);
//		sb.append(op);
//		sb.append(space);
//		sb.append(r);
		
		return Utilities.wrap(Utilities.append(l, r, op));
	}
	
	@Override
	public String caseAGreaterEqualNumericBinaryExp(
			AGreaterEqualNumericBinaryExp node, IndentTracker question)
			throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS, question);
		String op = mytable.getGE();
		
//		StringBuilder sb = new StringBuilder();
//		
//		sb.append(l);
//		sb.append(space);
//		sb.append(op);
//		sb.append(space);
//		sb.append(r);
		
		return Utilities.wrap(Utilities.append(l, r, op));
	}
	
	@Override
	public String caseALessEqualNumericBinaryExp(
			ALessEqualNumericBinaryExp node, IndentTracker question)
			throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS, question);
		String op = mytable.getLE();
		
//		StringBuilder sb = new StringBuilder();
//		
//		sb.append(l);
//		sb.append(space);
//		sb.append(op);
//		sb.append(space);
//		sb.append(r);
		
		return Utilities.wrap(Utilities.append(l, r, op));
	}
	
	@Override
	public String caseANotEqualBinaryExp(ANotEqualBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS, question);
		String op = mytable.getNE();
		
//		StringBuilder sb = new StringBuilder();
//		
//		sb.append(l);
//		sb.append(space);
//		sb.append(op);
//		sb.append(space);
//		sb.append(r);
//		
		return Utilities.wrap(Utilities.append(l, r, op));
	}
	
	@Override
	public String caseACompBinaryExp(ACompBinaryExp node, IndentTracker question)
			throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS, question);
		String op = mytable.getCOMPOSITION();
		
//		StringBuilder sb = new StringBuilder();
//		
//		sb.append(l);
//		sb.append(space);
//		sb.append(op);
//		sb.append(space);
//		sb.append(r);
		
		return Utilities.wrap(Utilities.append(l, r, op));
	}
	
	@Override
	public String caseAPowerSetUnaryExp(APowerSetUnaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String exp = node.getExp().apply(THIS, question);
		String op = mytable.getPOWER();
		
//		StringBuilder sb = new StringBuilder();
//		
//		sb.append(op);
//		sb.append(space);
//		sb.append(exp);
		
		return Utilities.wrap(Utilities.unaryappend(exp, op));
	}
	
	@Override
	public String caseASetEnumSetExp(ASetEnumSetExp node, IndentTracker question)
			throws AnalysisException
	{
		return node.toString();
	}
	
	@Override
	public String caseACardinalityUnaryExp(ACardinalityUnaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String s = node.getExp().apply(THIS, question);
		String op = mytable.getCARD();
		
//		StringBuilder sb = new StringBuilder();
//		
//		sb.append(op);
//		sb.append(space);
//		sb.append(s);
		
		return Utilities.wrap(Utilities.unaryappend(s, op));
	
	}
	
	@Override
	public String caseAInSetBinaryExp(AInSetBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS, question);
		String op = mytable.getINSET();
		
//		StringBuilder sb = new StringBuilder();
//		
//		sb.append(l);
//		sb.append(space);
//		sb.append(op);
//		sb.append(space);
//		sb.append(r);
//		
		return Utilities.wrap(Utilities.append(l, r, op));
	}
	
	@Override
	public String caseANotInSetBinaryExp(ANotInSetBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS, question);
		String op = mytable.getNOTINSET();
		
//		StringBuilder sb = new StringBuilder();
//		
//		sb.append(l);
//		sb.append(space);
//		sb.append(op);
//		sb.append(space);
//		sb.append(r);
		
		return Utilities.wrap(Utilities.append(l, r, op));
	}
	
	@Override
	public String caseASetUnionBinaryExp(ASetUnionBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS, question);
		String op = mytable.getUNION();
		
//		StringBuilder sb = new StringBuilder();
//		
//		sb.append(l);
//		sb.append(space);
//		sb.append(op);
//		sb.append(space);
//		sb.append(r);
		
		return Utilities.wrap(Utilities.append(l, r, op));
	}
	
	@Override
	public String caseASetIntersectBinaryExp(ASetIntersectBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS, question);
		String op = mytable.getINTER();
		
//		StringBuilder sb = new StringBuilder();
//		
//		sb.append(l);
//		sb.append(space);
//		sb.append(op);
//		sb.append(space);
//		sb.append(r);
		
		return Utilities.wrap(Utilities.append(l, r, op));
	}
	
	@Override
	public String caseASetDifferenceBinaryExp(ASetDifferenceBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS, question);
		String op = mytable.getSETDIFF();
		
//		StringBuilder sb = new StringBuilder();
//		
//		sb.append(l);
//		sb.append(space);
//		sb.append(op);
//		sb.append(space);
//		sb.append(r);
		
		return Utilities.wrap(Utilities.append(l, r, op));
	}
	
	@Override
	public String caseASubsetBinaryExp(ASubsetBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS, question);
		String op = mytable.getSUBSET();
		
//		StringBuilder sb = new StringBuilder();
//		
//		sb.append(l);
//		sb.append(space);
//		sb.append(op);
//		sb.append(space);
//		sb.append(r);
		
		return Utilities.wrap(Utilities.append(l, r, op));
	}
	
	@Override
	public String caseAProperSubsetBinaryExp(AProperSubsetBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS, question);
		String op = mytable.getPSUBSET();
		
//		StringBuilder sb = new StringBuilder();
//		
//		sb.append(l);
//		sb.append(space);
//		sb.append(op);
//		sb.append(space);
//		sb.append(r);
		
		return Utilities.wrap(Utilities.append(l, r, op));
	}
	
	@Override
	public String caseADistUnionUnaryExp(ADistUnionUnaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String r = node.getExp().apply(THIS, question);
		String op = mytable.getDUNION();
		
//		StringBuilder sb = new StringBuilder();
//		
//		sb.append(op);
//		sb.append(space);
//		sb.append(l);
		
		return Utilities.wrap(Utilities.unaryappend(r, op));
	}
	
	@Override
	public String caseADistIntersectUnaryExp(ADistIntersectUnaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String r = node.getExp().apply(THIS, question);
		String op = mytable.getDINTER();
		
//		StringBuilder sb = new StringBuilder();
//		
//		sb.append(op);
//		sb.append(space);
//		sb.append(l);
		
		return Utilities.wrap(Utilities.unaryappend(r, op));
	}
	
	@Override
	public String caseASetCompSetExp(ASetCompSetExp node, IndentTracker question)
			throws AnalysisException
	{
		String exp = node.getFirst().apply(THIS, question);

		String pred = node.getPredicate().apply(THIS, question);
		StringBuilder sb = new StringBuilder();
		
		sb.append(leftcurly);
		sb.append(exp);
		sb.append(bar);
		while(node.getBindings().size() != 0){
			sb.append(node.getBindings().poll().apply(THIS, question));
		}
		sb.append(space);
		sb.append(mytable.getPRED());
		sb.append(space);
		sb.append(pred);
		sb.append(rightcurly);
		
		return sb.toString();
	}
	@Override
	public String caseASetRangeSetExp(ASetRangeSetExp node,
			IndentTracker question) throws AnalysisException
	{
		String start = node.getFirst().apply(THIS, question);
		String finish = node.getLast().apply(THIS, question);
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(leftcurly);
		sb.append(start);
		sb.append(mytable.getCOMMA());
		sb.append(space);
		sb.append(mytable.getRANGE());
		sb.append(mytable.getCOMMA());
		sb.append(space);
		sb.append(finish);
		sb.append(rightcurly);
		
		return sb.toString();
	}
	@Override
	public String caseASetMultipleBind(ASetMultipleBind node,
			IndentTracker question) throws AnalysisException
	{
		StringBuilder sb = new StringBuilder();
		while (node.getPlist().size() != 0){
			sb.append(node.getPlist().poll().toString());
		}
		sb.append(space);
		sb.append(mytable.getINSET());
		sb.append(space);
		sb.append(node.getSet().apply(THIS, question));
		
		return sb.toString();
	}
//	@Override
	public String caseASetBind(ASetBind node, IndentTracker question)
			throws AnalysisException
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(node.getPattern().toString());
		sb.append(space);
		sb.append(mytable.getINSET());
		sb.append(space);
		sb.append(node.getSet().apply(THIS, question));
		
		//System.out.print(sb.toString());
		return sb.toString();
	}
	
	@Override
	public String caseAExistsExp(AExistsExp node, IndentTracker question)
			throws AnalysisException
	{
		String op = mytable.getEXISTS();
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(op);
		sb.append(space);
		
		while (node.getBindList().size() != 0){
			if (node.getBindList().size() > 1){
				String binding = node.getBindList().getFirst().apply(THIS, question);
				sb.append(binding);
				sb.append(mytable.getCOMMA());
				sb.append(space);
				node.getBindList().removeFirst();
			}
			else{
				String binding = node.getBindList().getFirst().apply(THIS, question);
		
				sb.append(binding);
		
				node.getBindList().removeFirst();
			}
		}
		
		sb.append(space);
		sb.append(mytable.getPRED());
		
		String pred = node.getPredicate().apply(THIS, question);
		
		sb.append(space);
		
		sb.append(pred);
		
		return sb.toString();
	}
	
	@Override
	public String caseAExists1Exp(AExists1Exp node, IndentTracker question)
			throws AnalysisException
	{
		//String binding = node.getBind().toString();
		String binding = node.getBind().apply(THIS, question);
		String pred = node.getPredicate().apply(THIS, question);
		String op = mytable.getEXISTS1();
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(op);
		sb.append(space);
		sb.append(binding);
		sb.append(space);
		sb.append(mytable.getPRED());
		sb.append(space);
		sb.append(pred);
		
		return sb.toString();
	}
	
	@Override
	public String caseAForAllExp(AForAllExp node, IndentTracker question)
			throws AnalysisException
	{
		String op = mytable.getFORALL();
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(op);
		sb.append(space);
		
		while(node.getBindList().size() !=0){
			if(node.getBindList().size() >1){
				String binding = node.getBindList().getFirst().apply(THIS, question);
				sb.append(binding);
				sb.append(mytable.getCOMMA());
				sb.append(space);
				
				node.getBindList().removeFirst();
			}
			else{
				String binding = node.getBindList().getFirst().toString();
				
				sb.append(binding);
				
				sb.append(space);
				
				node.getBindList().removeFirst();
			}
			
		}
		
		sb.append(mytable.getPRED());
		
		String pred = node.getPredicate().apply(THIS, question);
		
		sb.append(space);
		
		sb.append(pred);
		
		return sb.toString();
	}
	
	@Override
	public String caseAIotaExp(AIotaExp node, IndentTracker question)
			throws AnalysisException
	{
		String binding = node.getBind().apply(THIS, question);
		String pred = node.getPredicate().apply(THIS, question);
		String op = mytable.getIOTA();
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(op);
		sb.append(space);
		
		sb.append(binding);
		sb.append(space);
		
		sb.append(mytable.getPRED());
		sb.append(space);
		
		sb.append(pred);
		
		return sb.toString();
	}
	
	@Override
	public String caseASeqEnumSeqExp(ASeqEnumSeqExp node, IndentTracker question)
			throws AnalysisException
	{
		return node.toString();
	}
	
	@Override
	public String caseASeqCompSeqExp(ASeqCompSeqExp node, IndentTracker question)
			throws AnalysisException
	{
		String exp = node.getFirst().apply(THIS, question);
		String bind = node.getSetBind().apply(THIS, question);
		String pred = node.getPredicate().apply(THIS, question);
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(leftsq);
		sb.append(exp);
		sb.append(space);
		sb.append(bar);
		
		sb.append(bind);
		sb.append(space);
		
		sb.append(mytable.getPRED());
		sb.append(space);
		sb.append(pred);
		sb.append(rightsq);
		//System.out.print(sb.toString()+"\n");
		return sb.toString();
		
	}
	
	@Override
	public String caseAHeadUnaryExp(AHeadUnaryExp node, IndentTracker question)
			throws AnalysisException
	{
		String r = node.getExp().apply(THIS, question);
		String op = mytable.getHEAD();
		
//		StringBuilder sb = new StringBuilder();
//		
//		sb.append(op);
//		sb.append(space);
//		sb.append(r);
		
		return Utilities.wrap(Utilities.unaryappend(r, op));
	}
	
	@Override
	public String caseATailUnaryExp(ATailUnaryExp node, IndentTracker question)
			throws AnalysisException
	{
		String r = node.getExp().apply(THIS, question);
		String op = mytable.getTAIL();
		
//		StringBuilder sb = new StringBuilder();
//		
//		sb.append(op);
//		sb.append(space);
//		sb.append(r);
		
		return Utilities.wrap(Utilities.unaryappend(r, op));
	}
	
	@Override
	public String caseALenUnaryExp(ALenUnaryExp node, IndentTracker question)
			throws AnalysisException
	{
		String r = node.getExp().apply(THIS, question);
		String op = mytable.getLEN();
		
//		StringBuilder sb = new StringBuilder();
//		
//		sb.append(op);
//		sb.append(space);
//		sb.append(r);
		
		return Utilities.wrap(Utilities.unaryappend(r, op));
	}
	
	@Override
	public String caseAElementsUnaryExp(AElementsUnaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String r = node.getExp().apply(THIS, question);
		String op = mytable.getELEMS();
		
//		StringBuilder sb = new StringBuilder();
//		
//		sb.append(op);
//		sb.append(space);
//		sb.append(r);
		
		return Utilities.wrap(Utilities.unaryappend(r, op));
	}
	
	@Override
	public String caseAIndicesUnaryExp(AIndicesUnaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String r = node.getExp().apply(THIS, question);
		String op = mytable.getINDS();
		
//		StringBuilder sb = new StringBuilder();
//		
//		sb.append(op);
//		sb.append(space);
//		sb.append(r);
		
		return Utilities.wrap(Utilities.unaryappend(r, op));
	}
	
	@Override
	public String caseASeqConcatBinaryExp(ASeqConcatBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS, question);
		String op = mytable.getCONCATENATE();
		
		return Utilities.append(l, r, op);
	}
	
	@Override
	public String caseADistConcatUnaryExp(ADistConcatUnaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String r = node.getExp().apply(THIS, question);
		String op = mytable.getDISTCONC();
		
//		StringBuilder sb = new StringBuilder();
//		
//		sb.append(op);
//		sb.append(space);
//		sb.append(r);
		
		return Utilities.wrap(Utilities.unaryappend(r, op));
	}
	
	@Override
	public String caseAPlusPlusBinaryExp(APlusPlusBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS, question);
		String op = mytable.getPLUSPLUS();
		
		return Utilities.append(l, r, op);
	}
	
	@Override
	public String caseAApplyExp(AApplyExp node, IndentTracker question)
			throws AnalysisException
	{
		return node.toString();
	}
	
	@Override
	public String caseAMapletExp(AMapletExp node, IndentTracker question)
			throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS, question);
		String op = mytable.getMAPLET();
		
		//System.out.print(Utilities.append(l, r, op));
		
		return Utilities.append(l, r, op);
	}
	
	@Override
	public String caseAMapEnumMapExp(AMapEnumMapExp node, IndentTracker question)
			throws AnalysisException
	{
		//System.out.print(node.getMembers().poll().apply(THIS, question));
		StringBuilder sb = new StringBuilder();
		sb.append(leftcurly);
		while(node.getMembers().size() != 0){
			if (node.getMembers().size() >1){
				sb.append(node.getMembers().poll().apply(THIS, question));
				sb.append(mytable.getCOMMA());
				sb.append(space);
			}
			else
			{
				sb.append(node.getMembers().poll().apply(THIS, question));
			}
			
		}
		sb.append(rightcurly);
		return sb.toString();
	}
	
	@Override
	public String caseAMapCompMapExp(AMapCompMapExp node, IndentTracker question)
			throws AnalysisException
	{
		String exp = node.getFirst().apply(THIS, question);
		String bind ;//= node.getBindings().poll().apply(THIS, question);
		String pred = node.getPredicate().apply(THIS, question);
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(leftcurly);
		sb.append(exp);
		sb.append(space);
		sb.append(bar);
		
		
		while (node.getBindings().size() != 0)
		{
			if (node.getBindings().size() >1)
			{
				bind = node.getBindings().poll().apply(THIS, question);
				sb.append(bind);
				sb.append(mytable.getCOMMA());
				sb.append(space);
			}
			else
			{
				bind = node.getBindings().poll().apply(THIS, question);
				sb.append(bind);
				sb.append(space);
			}
		}
		
		sb.append(mytable.getPRED());
		sb.append(space);
		sb.append(pred);
		sb.append(rightcurly);
		
		return sb.toString();
	}
	
	@Override
	public String caseAMapDomainUnaryExp(AMapDomainUnaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String r = node.getExp().apply(THIS, question);
		String op = mytable.getDOM();
		
		return Utilities.wrap(Utilities.unaryappend(r, op));
	}
	
	@Override
	public String caseAMapRangeUnaryExp(AMapRangeUnaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String r = node.getExp().apply(THIS, question);
		String op = mytable.getRNG();
		
		return Utilities.wrap(Utilities.unaryappend(r, op));
	}
	@Override
	public String caseACasesExp(ACasesExp node, IndentTracker question)
			throws AnalysisException
	{
		String exp = node.getExpression().apply(THIS, question);
		String op  = mytable.getCASES();
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(op);
		sb.append(space);
		
		sb.append(exp);
		
		sb.append(mytable.getCOLON());
		sb.append(space);
		
		while(node.getCases().size() !=0){
			
			String caselist = node.getCases().poll().toString();
			
			sb.append(brtab);
			sb.append(caselist);
			if (node.getCases().size() > 0){
				sb.append(mytable.getCOMMA());
			}
			
			//sb.append(space);
		}
		if (node.getOthers() != null)
		{
			
			sb.append(mytable.getCOMMA());
			sb.append(brtab);
			sb.append(mytable.getOTHERS());
			sb.append(space);
			
			sb.append(mytable.getMINUS());
			sb.append(mytable.getGT());
			sb.append(space);
			
			sb.append(node.getOthers().apply(THIS, question));
		}
		sb.append(brl);
		sb.append(mytable.getEND());
		
		
		return sb.toString();
	}
	
	
//	@Override
//	public String caseAIfExp(AIfExp node, IndentTracker question)
//			throws AnalysisException
//	{
//		String test = node.getTest().apply(THIS, question);
//		return test;
//	}
	
	@Override
	public String caseACharLiteralExp(ACharLiteralExp node,
			IndentTracker question) throws AnalysisException
	{
		return mytable.getCHARDELIM() + Character.toString(node.getValue().getValue()) + mytable.getCHARDELIM();
	}
	
	@Override
	public String caseAQuoteLiteralExp(AQuoteLiteralExp node,
			IndentTracker question) throws AnalysisException
	{
		return mytable.getOPENQUOTE() + node.getValue().getValue().toString() + mytable.getCLOSEQUOTE();
	}
	
	@Override
	public String caseAIntLiteralExp(AIntLiteralExp node, IndentTracker question)
			throws AnalysisException
	{
		return Long.toString(node.getValue().getValue());
	}
	
	@Override
	public String caseARealLiteralExp(ARealLiteralExp node,
			IndentTracker question) throws AnalysisException
	{
		return Double.toString(node.getValue().getValue());
	}
	
	@Override
	public String caseAVariableExp(AVariableExp node, IndentTracker question)
			throws AnalysisException
	{
		String var = node.getOriginal();
		
		return var;
		
	}
	
	@Override
	public String caseABooleanConstExp(ABooleanConstExp node,
			IndentTracker question) throws AnalysisException
	{
		return Boolean.toString(node.getValue().getValue());
	}

	@Override
	public String createNewReturnValue(INode node, IndentTracker question)
			throws AnalysisException
	{
		return EXPRESSION_NOT_FOUND;
	}

	@Override
	public String createNewReturnValue(Object node, IndentTracker question)
			throws AnalysisException
	{
		return EXPRESSION_NOT_FOUND;
	}

}
