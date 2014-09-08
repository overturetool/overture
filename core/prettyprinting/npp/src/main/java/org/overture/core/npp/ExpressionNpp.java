package org.overture.core.npp;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.expressions.AAbsoluteUnaryExp;
import org.overture.ast.expressions.AAndBooleanBinaryExp;
import org.overture.ast.expressions.AApplyExp;
import org.overture.ast.expressions.ABooleanConstExp;
import org.overture.ast.expressions.ACardinalityUnaryExp;
import org.overture.ast.expressions.ACaseAlternative;
import org.overture.ast.expressions.ACasesExp;
import org.overture.ast.expressions.ACharLiteralExp;
import org.overture.ast.expressions.ACompBinaryExp;
import org.overture.ast.expressions.ADefExp;
import org.overture.ast.expressions.ADistConcatUnaryExp;
import org.overture.ast.expressions.ADistIntersectUnaryExp;
import org.overture.ast.expressions.ADistMergeUnaryExp;
import org.overture.ast.expressions.ADistUnionUnaryExp;
import org.overture.ast.expressions.ADivNumericBinaryExp;
import org.overture.ast.expressions.ADivideNumericBinaryExp;
import org.overture.ast.expressions.ADomainResByBinaryExp;
import org.overture.ast.expressions.ADomainResToBinaryExp;
import org.overture.ast.expressions.AElementsUnaryExp;
import org.overture.ast.expressions.AElseIfExp;
import org.overture.ast.expressions.AEqualsBinaryExp;
import org.overture.ast.expressions.AEquivalentBooleanBinaryExp;
import org.overture.ast.expressions.AExists1Exp;
import org.overture.ast.expressions.AExistsExp;
import org.overture.ast.expressions.AFieldExp;
import org.overture.ast.expressions.AFieldNumberExp;
import org.overture.ast.expressions.AFloorUnaryExp;
import org.overture.ast.expressions.AForAllExp;
import org.overture.ast.expressions.AGreaterEqualNumericBinaryExp;
import org.overture.ast.expressions.AGreaterNumericBinaryExp;
import org.overture.ast.expressions.AHeadUnaryExp;
import org.overture.ast.expressions.AHistoryExp;
import org.overture.ast.expressions.AIfExp;
import org.overture.ast.expressions.AImpliesBooleanBinaryExp;
import org.overture.ast.expressions.AInSetBinaryExp;
import org.overture.ast.expressions.AIndicesUnaryExp;
import org.overture.ast.expressions.AIntLiteralExp;
import org.overture.ast.expressions.AIotaExp;
import org.overture.ast.expressions.AIsExp;
import org.overture.ast.expressions.AIsOfBaseClassExp;
import org.overture.ast.expressions.AIsOfClassExp;
import org.overture.ast.expressions.ALambdaExp;
import org.overture.ast.expressions.ALenUnaryExp;
import org.overture.ast.expressions.ALessEqualNumericBinaryExp;
import org.overture.ast.expressions.ALessNumericBinaryExp;
import org.overture.ast.expressions.ALetBeStExp;
import org.overture.ast.expressions.ALetDefExp;
import org.overture.ast.expressions.AMapCompMapExp;
import org.overture.ast.expressions.AMapDomainUnaryExp;
import org.overture.ast.expressions.AMapEnumMapExp;
import org.overture.ast.expressions.AMapInverseUnaryExp;
import org.overture.ast.expressions.AMapRangeUnaryExp;
import org.overture.ast.expressions.AMapUnionBinaryExp;
import org.overture.ast.expressions.AMapletExp;
import org.overture.ast.expressions.AModNumericBinaryExp;
import org.overture.ast.expressions.AMuExp;
import org.overture.ast.expressions.ANarrowExp;
import org.overture.ast.expressions.ANotEqualBinaryExp;
import org.overture.ast.expressions.ANotInSetBinaryExp;
import org.overture.ast.expressions.AOrBooleanBinaryExp;
import org.overture.ast.expressions.APlusNumericBinaryExp;
import org.overture.ast.expressions.APlusPlusBinaryExp;
import org.overture.ast.expressions.APostOpExp;
import org.overture.ast.expressions.APowerSetUnaryExp;
import org.overture.ast.expressions.APreExp;
import org.overture.ast.expressions.APreOpExp;
import org.overture.ast.expressions.AProperSubsetBinaryExp;
import org.overture.ast.expressions.AQuoteLiteralExp;
import org.overture.ast.expressions.ARangeResByBinaryExp;
import org.overture.ast.expressions.ARangeResToBinaryExp;
import org.overture.ast.expressions.ARealLiteralExp;
import org.overture.ast.expressions.ARecordModifier;
import org.overture.ast.expressions.ARemNumericBinaryExp;
import org.overture.ast.expressions.ASameBaseClassExp;
import org.overture.ast.expressions.ASameClassExp;
import org.overture.ast.expressions.ASeqCompSeqExp;
import org.overture.ast.expressions.ASeqConcatBinaryExp;
import org.overture.ast.expressions.ASeqEnumSeqExp;
import org.overture.ast.expressions.ASetCompSetExp;
import org.overture.ast.expressions.ASetDifferenceBinaryExp;
import org.overture.ast.expressions.ASetEnumSetExp;
import org.overture.ast.expressions.ASetIntersectBinaryExp;
import org.overture.ast.expressions.ASetRangeSetExp;
import org.overture.ast.expressions.ASetUnionBinaryExp;
import org.overture.ast.expressions.AStarStarBinaryExp;
import org.overture.ast.expressions.ASubsetBinaryExp;
import org.overture.ast.expressions.ASubtractNumericBinaryExp;
import org.overture.ast.expressions.ATailUnaryExp;
import org.overture.ast.expressions.AThreadIdExp;
import org.overture.ast.expressions.ATimeExp;
import org.overture.ast.expressions.ATimesNumericBinaryExp;
import org.overture.ast.expressions.ATupleExp;
import org.overture.ast.expressions.AUndefinedExp;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.ASetBind;
import org.overture.ast.patterns.ASetMultipleBind;
import org.overture.ast.patterns.ATypeBind;
import org.overture.ast.types.ACharBasicType;
import org.overture.ast.types.ANatNumericBasicType;
import org.overture.ast.types.ANatOneNumericBasicType;

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
	private static String leftpar = "(";
	private static String rightpar = ")";
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


		return Utilities.wrap(Utilities.append(l, r, op));
	}
	
	@Override
	public String caseASubtractNumericBinaryExp(ASubtractNumericBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS, question);
		String op = mytable.getMINUS();
		
		
		return Utilities.wrap(Utilities.append(l, r, op));
	}
	
	@Override
	public String caseATimesNumericBinaryExp(ATimesNumericBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS,question);
		String op = mytable.getTIMES();
		
		
		return Utilities.wrap(Utilities.append(l, r, op));
	}
	
	@Override
	public String caseADivideNumericBinaryExp(ADivideNumericBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS,question);
		String op = mytable.getDIVIDE();
		
		
		return Utilities.wrap(Utilities.append(l, r, op));
	}
	
	@Override
	public String caseAModNumericBinaryExp(AModNumericBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS,question);
		String op = mytable.getMOD();
		
		
		return Utilities.wrap(Utilities.append(l, r, op));
	}
	
	@Override
	public String caseADivNumericBinaryExp(ADivNumericBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS,question);
		String op = mytable.getDIV();
		
		
		return Utilities.wrap(Utilities.append(l, r, op));
	}
	
	@Override
	public String caseARemNumericBinaryExp(ARemNumericBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS,question);
		String op = mytable.getREM();
		
		return	Utilities.wrap(Utilities.append(l, r, op));
	}
	
	@Override
	public String caseAImpliesBooleanBinaryExp(AImpliesBooleanBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS,question);
		String op = mytable.getIMPLIES();
		
		
		return	Utilities.wrap(Utilities.append(l, r, op));
	}
	
	@Override
	public String caseAEquivalentBooleanBinaryExp(
			AEquivalentBooleanBinaryExp node, IndentTracker question)
			throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS, question);
		String op = mytable.getEQUIV();
		
		return Utilities.wrap(Utilities.append(l, r, op));
	}
	
	@Override
	public String caseAAbsoluteUnaryExp(AAbsoluteUnaryExp node,
			IndentTracker question) throws AnalysisException
	{
		
		String r = node.getExp().apply(THIS,question);
		String op = mytable.getABSOLUTE();
		
		
		return Utilities.wrap(Utilities.unaryappend(r, op));
	}
	
	@Override
	public String caseAAndBooleanBinaryExp(AAndBooleanBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS,question);
		String r = node.getRight().apply(THIS, question);
		String op = mytable.getAND();
		
		
		return Utilities.wrap(Utilities.append(l, r, op));
		
	}
	
	@Override
	public String caseAOrBooleanBinaryExp(AOrBooleanBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS,question);
		String op = mytable.getOR();
	
		return Utilities.wrap(Utilities.append(l, r, op));
	}
	
	@Override
	public String caseAFloorUnaryExp(AFloorUnaryExp node, IndentTracker question)
			throws AnalysisException
	{
		String r = node.getExp().apply(THIS, question);
		String op = mytable.getFLOOR();
		
		return Utilities.wrap(Utilities.unaryappend(r, op));
	}
	
	@Override
	public String caseAStarStarBinaryExp(AStarStarBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS, question);
		String op = mytable.getSTARSTAR();
		
		return Utilities.wrap(Utilities.append(l, r, op));
	}
	
	@Override
	public String caseALambdaExp(ALambdaExp node, IndentTracker question)
			throws AnalysisException
	{
		String exp = node.getExpression().apply(THIS, question);
		//System.out.print(exp + "\n");
		String op = mytable.getLAMBDA();
		String pred = mytable.getPRED();
		String bind;
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(op);
		sb.append(space);
		
		while (node.getBindList().size() != 0){
			if(node.getBindList().size() > 1){
				//bind = node.getBindList().poll().apply(THIS, question);
				bind = rootNpp.defaultPBind(node.getBindList().poll(), question);
				
				sb.append(bind);
				sb.append(mytable.getCOMMA());
				sb.append(space);
			}
			else{
				//bind = node.getBindList().poll().apply(THIS, question);
				bind = rootNpp.defaultPBind(node.getBindList().poll(), question);
				sb.append(bind);
				sb.append(space);
			}
		}
		
		sb.append(pred);
		sb.append(space);
		
		sb.append(exp);
		
		return sb.toString();
	}
	
	
//	@Override
//	public String caseANotUnaryExp(ANotUnaryExp node, IndentTracker question)
//			throws AnalysisException
//	{
//		String r = node.getExp().apply(THIS, question);
//		String op = mytable.getMINUS();
//		
//		StringBuilder sb = new StringBuilder();
//		
//		sb.append(op);
//		sb.append(r);
//		return sb.toString();
//		
//		//return Utilities.wrap(Utilities.unaryappend(r, op));
//	}
	

	@Override
	public String caseAEqualsBinaryExp(AEqualsBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS, question);

		String op = mytable.getEQUALS();
		
		
		return Utilities.wrap(Utilities.append(l, r, op));
	}
	
	@Override
	public String caseAGreaterNumericBinaryExp(AGreaterNumericBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS, question);
		String op = mytable.getGT();
		
		
		return Utilities.wrap(Utilities.append(l, r, op));
	}
	
	@Override
	public String caseALessNumericBinaryExp(ALessNumericBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS, question);
		String op = mytable.getLT();
		

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
		
		
		return Utilities.wrap(Utilities.append(l, r, op));
	}
	
	@Override
	public String caseANotEqualBinaryExp(ANotEqualBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS, question);
		String op = mytable.getNE();
		

		return Utilities.wrap(Utilities.append(l, r, op));
	}
	
	@Override
	public String caseACompBinaryExp(ACompBinaryExp node, IndentTracker question)
			throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS, question);
		String op = mytable.getCOMPOSITION();
		
		
		return Utilities.wrap(Utilities.append(l, r, op));
	}
	
	@Override
	public String caseAPowerSetUnaryExp(APowerSetUnaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String exp = node.getExp().apply(THIS, question);
		String op = mytable.getPOWER();
		
		
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
		
		
		return Utilities.wrap(Utilities.unaryappend(s, op));
	
	}
	
	@Override
	public String caseAInSetBinaryExp(AInSetBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS, question);
		String op = mytable.getINSET();
		
		
		return Utilities.wrap(Utilities.append(l, r, op));
	}
	
	@Override
	public String caseANotInSetBinaryExp(ANotInSetBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS, question);
		String op = mytable.getNOTINSET();
		
		
		return Utilities.wrap(Utilities.append(l, r, op));
	}
	
	@Override
	public String caseASetUnionBinaryExp(ASetUnionBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS, question);
		String op = mytable.getUNION();
		
		return Utilities.wrap(Utilities.append(l, r, op));
	}
	
	@Override
	public String caseASetIntersectBinaryExp(ASetIntersectBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS, question);
		String op = mytable.getINTER();
		
		return Utilities.wrap(Utilities.append(l, r, op));
	}
	
	@Override
	public String caseASetDifferenceBinaryExp(ASetDifferenceBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS, question);
		String op = mytable.getSETDIFF();
		
		return Utilities.wrap(Utilities.append(l, r, op));
	}
	
	@Override
	public String caseASubsetBinaryExp(ASubsetBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS, question);
		String op = mytable.getSUBSET();
		
		return Utilities.wrap(Utilities.append(l, r, op));
	}
	
	@Override
	public String caseAProperSubsetBinaryExp(AProperSubsetBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS, question);
		String op = mytable.getPSUBSET();
		

		return Utilities.wrap(Utilities.append(l, r, op));
	}
	
	@Override
	public String caseADistUnionUnaryExp(ADistUnionUnaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String r = node.getExp().apply(THIS, question);
		String op = mytable.getDUNION();
		

		return Utilities.wrap(Utilities.unaryappend(r, op));
	}
	
	@Override
	public String caseADistIntersectUnaryExp(ADistIntersectUnaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String r = node.getExp().apply(THIS, question);
		String op = mytable.getDINTER();
		
		
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
//	public String caseASetBind(ASetBind node, IndentTracker question)
//			throws AnalysisException
//	{
//		StringBuilder sb = new StringBuilder();
//		
//		sb.append(node.getPattern().toString());
//		sb.append(space);
//		sb.append(mytable.getINSET());
//		sb.append(space);
//		sb.append(node.getSet().apply(THIS, question));
//		
//		return sb.toString();
//	}
	
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
		String binding = rootNpp.defaultPBind(node.getBind(), question);//node.getBind().apply(THIS, question);
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
		String binding = rootNpp.defaultPBind(node.getBind(), question);//node.getBind().apply(THIS, question);
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
		String bind = rootNpp.defaultPBind(node.getSetBind(), question);//node.getSetBind().apply(THIS, question);
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
		
		
		return Utilities.wrap(Utilities.unaryappend(r, op));
	}
	
	@Override
	public String caseATailUnaryExp(ATailUnaryExp node, IndentTracker question)
			throws AnalysisException
	{
		String r = node.getExp().apply(THIS, question);
		String op = mytable.getTAIL();
		

		return Utilities.wrap(Utilities.unaryappend(r, op));
	}
	
	@Override
	public String caseALenUnaryExp(ALenUnaryExp node, IndentTracker question)
			throws AnalysisException
	{
		String r = node.getExp().apply(THIS, question);
		String op = mytable.getLEN();
		

		return Utilities.wrap(Utilities.unaryappend(r, op));
	}
	
	@Override
	public String caseAElementsUnaryExp(AElementsUnaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String r = node.getExp().apply(THIS, question);
		String op = mytable.getELEMS();
		

		return Utilities.wrap(Utilities.unaryappend(r, op));
	}
	
	@Override
	public String caseAIndicesUnaryExp(AIndicesUnaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String r = node.getExp().apply(THIS, question);
		String op = mytable.getINDS();
		

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
		
		
		return Utilities.append(l, r, op);
	}
	
	@Override
	public String caseAMapEnumMapExp(AMapEnumMapExp node, IndentTracker question)
			throws AnalysisException
	{
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
		String bind ;
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
	public String caseAMapUnionBinaryExp(AMapUnionBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS,question);
		String op = mytable.getMUNION();
		
		return Utilities.append(l, r, op);
	}
	
	@Override
	public String caseADistMergeUnaryExp(ADistMergeUnaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String r = node.getExp().apply(THIS, question);
		String op = mytable.getMERGE();
		
		return Utilities.wrap(Utilities.unaryappend(r, op));
	}
	
	@Override
	public String caseADomainResToBinaryExp(ADomainResToBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS,question);
		String op = mytable.getDOMRESTO();
		
		return Utilities.append(l, r, op);
	}
	
	@Override
	public String caseADomainResByBinaryExp(ADomainResByBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS,question);
		String op = mytable.getDOMRESBY();
		
		return Utilities.append(l, r, op);
	}
	
	@Override
	public String caseARangeResToBinaryExp(ARangeResToBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS,question);
		String op = mytable.getRANGERESTO();
		
		return Utilities.append(l, r, op);
	}
	
	@Override
	public String caseARangeResByBinaryExp(ARangeResByBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS,question);
		String op = mytable.getRANGERESBY();
		
		return Utilities.append(l, r, op);
	}
	
	@Override
	public String caseAMapInverseUnaryExp(AMapInverseUnaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String r = node.getExp().apply(THIS, question);
		String op = mytable.getINVERSE();
		
		return Utilities.wrap(Utilities.unaryappend(r, op));
	}
	
	@Override
	public String caseACasesExp(ACasesExp node, IndentTracker question)
			throws AnalysisException
	{
		String exp = node.getExpression().apply(THIS, question);
		
		//System.out.print(exp);
		String op  = mytable.getCASES();
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(op);
		sb.append(space);
		
		sb.append(exp);
		
		sb.append(mytable.getCOLON());
		sb.append(space); 
		
		//System.out.print(node.getCases().getFirst().toString());
		
		while(node.getCases().size() !=0){
			
			String caselist = node.getCases().poll().apply(THIS, question);
			
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
	
	@Override
	public String caseACaseAlternative(ACaseAlternative node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getPattern().toString();
		String r = node.getResult().apply(THIS, question);
		String op = mytable.getARROW();
		
		return Utilities.append(l, r, op);
	}
	
	
	@Override
	public String caseAIfExp(AIfExp node, IndentTracker question)
			throws AnalysisException
	{
		String test = node.getTest().apply(THIS, question);
		String exp1 = node.getThen().apply(THIS, question);
		String exp2 = node.getElse().apply(THIS, question);
		String elseif;
		
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(mytable.getIF());
		sb.append(space);
		sb.append(test);
		sb.append(brl);
		sb.append(space);
		sb.append(mytable.getTHEN());
		sb.append(space);
		sb.append(exp1);
		sb.append(brl);
		sb.append(space);
		while (node.getElseList().size() != 0){
			
			elseif = node.getElseList().poll().apply(THIS, question);
			sb.append(elseif);
			sb.append(brl);
			sb.append(space);
		}
		sb.append(mytable.getELSE());
		sb.append(space);
		sb.append(exp2);
		//System.out.print(sb.toString()+"\n");
		return sb.toString();
	}
	
	@Override
	public String caseAElseIfExp(AElseIfExp node, IndentTracker question)
			throws AnalysisException
	{
		String test = node.getElseIf().apply(THIS, question);
		String exp1 = node.getThen().apply(THIS, question);
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(mytable.getELSEIF());
		sb.append(space);
		sb.append(test);
		sb.append(brl);
		sb.append(space);
		sb.append(mytable.getTHEN());
		sb.append(space);
		sb.append(exp1);
		
		return sb.toString();
	}
	
	@Override
	public String caseAMuExp(AMuExp node, IndentTracker question)
			throws AnalysisException
	{
		String exp = node.getRecord().apply(THIS, question);
		String mod;
		
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		
		sb1.append("mu");
		//sb1.append(space);
		
		sb2.append(exp);
		sb2.append(mytable.getCOMMA());
		sb2.append(space);
	
		while (node.getModifiers().size() != 0){
			if (node.getModifiers().size() > 1){
				
				mod = node.getModifiers().poll().apply(THIS, question);//.toString();
				
				sb2.append(mod);
				sb2.append(mytable.getCOMMA());
				sb2.append(space);
			}
			else
			{
				mod = node.getModifiers().poll().apply(THIS, question);//.toString();//.apply(THIS, question);
				
				sb2.append(mod);
			}
			
		}
		
		sb1.append(Utilities.wrap(sb2.toString()));
		
		return sb1.toString();
	}
	
	@Override
	public String caseALetDefExp(ALetDefExp node, IndentTracker question)
			throws AnalysisException
	{
		String exp = node.getExpression().apply(THIS, question);
		String def;
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(mytable.getLET());
		sb.append(space);
		
		while (node.getLocalDefs().size() != 0){
			

			if (node.getLocalDefs().size() > 1){
				
				//def = node.getLocalDefs().poll().apply(THIS,question);
				def = rootNpp.defaultPDefinition(node.getLocalDefs().poll(), question);
				sb.append(def);
				sb.append(mytable.getCOMMA());
				sb.append(space);
			}
			else
			{
				//def = node.getLocalDefs().poll().apply(THIS, question);
				def = rootNpp.defaultPDefinition(node.getLocalDefs().poll(), question);
				sb.append(def);
				sb.append(space);
			}
		}
		
		sb.append(mytable.getIN());
		sb.append(space);
		
		sb.append(exp);
	
		return sb.toString();
	}
	
	@Override
	public String caseALetBeStExp(ALetBeStExp node, IndentTracker question)
			throws AnalysisException
	{
		StringBuilder sb = new StringBuilder();
		String exp_value = node.getValue().apply(THIS, question);
		String def;
		
		try{
			String exp1 = node.getSuchThat().apply(THIS, question);
			
			sb.append(mytable.getLET());
			sb.append(space);
			
			def = node.getBind().apply(THIS, question);
			sb.append(def);
			sb.append(space);
			
			sb.append(mytable.getBESUCH());
			sb.append(space);
			sb.append(exp1);
		}
		catch(NullPointerException e)
		{
			sb.append(mytable.getLET());
			sb.append(space);
			def = node.getBind().apply(THIS, question);
			sb.append(def);
		}
		
		sb.append(brl);
		
		sb.append(mytable.getIN());
		sb.append(brl);
		sb.append(space);
		sb.append(exp_value);
		
		return sb.toString();
	}
	
	@Override
	public String caseADefExp(ADefExp node, IndentTracker question)
			throws AnalysisException
	{
		String def;
		String exp = node.getExpression().apply(THIS, question);
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(mytable.getDEFINE());
		sb.append(space);
		
		
		while(node.getLocalDefs().size() != 0){
			if(node.getLocalDefs().size() >1)
			{
				//def = node.getLocalDefs().poll().apply(THIS, question);
				def = rootNpp.defaultPDefinition(node.getLocalDefs().poll(), question);
				sb.append(def);
				sb.append(mytable.getSEP());
				sb.append(space);
			}
			else
			{
				//def = node.getLocalDefs().poll().apply(THIS, question);
				def = rootNpp.defaultPDefinition(node.getLocalDefs().poll(), question);
				
				sb.append(def);
			}
		}
		
		sb.append(brl);
		sb.append(mytable.getIN());
		sb.append(brl);
		sb.append(space);
		sb.append(exp);
		
		return Utilities.wrap(sb.toString());
		
	}

	@Override
	public String caseAIsExp(AIsExp node, IndentTracker question)
			throws AnalysisException
	{
		String exp = node.getTest().apply(THIS, question);
		String type;
		if (node.getBasicType() == null)
		{
			//System.out.print(node.getTypeName().getClass().toString());
			type = node.getTypeName().toString();
		}
		else
		{	//System.out.print(node.getBasicType().getClass() + "\n");
			type = node.getBasicType().toString();
		}
		
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(mytable.getISTYPE());
		sb.append(leftpar);
		sb.append(exp);
		sb.append(mytable.getCOMMA());
		sb.append(type);
		sb.append(rightpar);
		//System.out.print(sb.toString()+"\n");
		return sb.toString();
	}
	
	@Override
	public String caseAIsOfBaseClassExp(AIsOfBaseClassExp node,
			IndentTracker question) throws AnalysisException
	{
		String exp = node.getExp().apply(THIS, question);
		String op = mytable.getISBASECLASS();
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(op);
		sb.append(leftpar);
		sb.append(exp);
		sb.append(rightpar);
	
		return sb.toString();
	}
	
	@Override
	public String caseAIsOfClassExp(AIsOfClassExp node, IndentTracker question)
			throws AnalysisException
	{
		String exp = node.getExp().apply(THIS, question);
		String op = mytable.getISCLASS();
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(op);
		sb.append(leftpar);
		sb.append(exp);
		sb.append(rightpar);
		
		return sb.toString();
	}
	
	@Override
	public String caseASameClassExp(ASameClassExp node, IndentTracker question)
			throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS,question);
		String op = mytable.getSAMECLASS();
	
		StringBuilder sb = new StringBuilder();
	
		sb.append(op);
		sb.append(leftpar);
		sb.append(l);
		sb.append(mytable.getCOMMA());
		sb.append(space);
		sb.append(r);
		sb.append(rightpar);
	
		return sb.toString();
	}
	
	@Override
	public String caseASameBaseClassExp(ASameBaseClassExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS,question);
		String op = mytable.getSAMEBASECLASS();
	
		StringBuilder sb = new StringBuilder();
	
		sb.append(op);
		sb.append(leftpar);
		sb.append(l);
		sb.append(mytable.getCOMMA());
		sb.append(space);
		sb.append(r);
		sb.append(rightpar);
	
		return sb.toString();
	}
	
	@Override
	public String caseAPreExp(APreExp node, IndentTracker question)
			throws AnalysisException
	{
		String op = mytable.getPRE();
		
		String func = node.getFunction().apply(THIS, question);
		String arg;
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(op);
		sb.append(leftpar);
		sb.append(func);
		
		while (node.getArgs().size() != 0){
			if(node.getArgs().size() > 1){
				arg = node.getArgs().poll().apply(THIS, question);
				sb.append(mytable.getCOMMA());
				sb.append(space);
				sb.append(arg);
				sb.append(mytable.getCOMMA());
				sb.append(space);
			}
			else
			{
				arg = node.getArgs().poll().apply(THIS, question);
				sb.append(arg);
			}
		}
		
		sb.append(rightpar);
		
		return sb.toString();
	}
	
	
	
	@Override
	public String caseAHistoryExp(AHistoryExp node, IndentTracker question)
			throws AnalysisException
	{
		String exp = node.getHop().apply(THIS, question);
		StringBuilder sb = new StringBuilder();
		
		sb.append(exp);
		return sb.toString();
		
	}
	
	@Override
	public String caseATupleExp(ATupleExp node, IndentTracker question)
			throws AnalysisException
	{
		String exp;
		String op = mytable.getTUPLE();
		StringBuilder sb = new StringBuilder();
		
		
		sb.append(op);
		sb.append(leftpar);
		
		while(node.getArgs().size() != 1){
			exp = node.getArgs().poll().apply(THIS, question);
			sb.append(exp);
			sb.append(mytable.getCOMMA());
			sb.append(space);
		}
		exp = node.getArgs().poll().apply(THIS, question);
		sb.append(exp);
		
		sb.append(rightpar);
		
		return sb.toString();
	}
	
	@Override
	public String caseAThreadIdExp(AThreadIdExp node, IndentTracker question)
			throws AnalysisException
	{
		return node.toString();
	}
	
	@Override
	public String caseAFieldExp(AFieldExp node, IndentTracker question)
			throws AnalysisException
	{
		String exp = node.getObject().apply(THIS, question);
		String field = node.getField().getClassName().toString();//.apply(THIS, question);
		
		String op = mytable.getPOINT();
		
		StringBuilder sb = new StringBuilder();
		sb.append(exp);
		sb.append(op);
		sb.append(field);
		//System.out.print(node.getField().getClassName());
		
		return sb.toString();//Utilities.append(exp, field, op);
	}
	
	@Override
	public String caseAFieldNumberExp(AFieldNumberExp node,
			IndentTracker question) throws AnalysisException
	{
		String tuple = node.getTuple().apply(THIS, question);
		String field = node.getField().toString();
		
		return tuple+"."+field;
	}
	
	@Override
	public String caseANarrowExp(ANarrowExp node, IndentTracker question)
			throws AnalysisException
	{
		String exp = node.getTest().apply(THIS, question);
		
		String type = node.getTypeName().toString();//.apply(THIS,question);
		//System.out.print(type);
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(mytable.getNARROW());
		sb.append(leftpar);
		sb.append(exp);
		sb.append(mytable.getCOMMA());
		sb.append(type);
		sb.append(rightpar);
		
		return sb.toString();
	}
	
	@Override
	public String caseAPostOpExp(APostOpExp node, IndentTracker question)
			throws AnalysisException
	{
		String exp = node.getPostexpression().apply(THIS, question);
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(mytable.getPOSTOP());
		sb.append(space);
		sb.append(exp);
		
		return sb.toString();
	}
	
	@Override
	public String caseAPreOpExp(APreOpExp node, IndentTracker question)
			throws AnalysisException
	{
		String exp = node.getExpression().apply(THIS, question);
		
		System.out.print(exp);
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(mytable.getPREOP());
		sb.append(space);
		sb.append(exp);
		
		return sb.toString();
	}
	
//	@Override
//	public String caseAFuncInstatiationExp(AFuncInstatiationExp node,
//			IndentTracker question) throws AnalysisException
//	{
//		String exp = node.getFunction().apply(THIS, question);
//		
//		System.out.print(exp);
//		
//		return exp;
//	}
	
//	@Override
//	public String caseAFieldField(AFieldField node, IndentTracker question)
//			throws AnalysisException
//	{
//		return node.getTag().toString();
//	}
	
	
//	@Override
//	public String caseILexIdentifierToken(ILexIdentifierToken node,
//			IndentTracker question) throws AnalysisException
//	{
//		String fieldname = node.getClassName().apply(THIS, question);
//		
//		return fieldname;
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
	public String caseANatOneNumericBasicType(ANatOneNumericBasicType node,
			IndentTracker question) throws AnalysisException
	{
		return node.toString();
	}
	
	@Override
	public String caseANatNumericBasicType(ANatNumericBasicType node,
			IndentTracker question) throws AnalysisException
	{
		return node.toString();
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
	public String caseACharBasicType(ACharBasicType node, IndentTracker question)
			throws AnalysisException
	{
		return node.getDefinitions().toString();
	}
	
	@Override
	public String caseATimeExp(ATimeExp node, IndentTracker question)
			throws AnalysisException
	{
		return node.toString();
	}
	
	@Override
	public String caseABooleanConstExp(ABooleanConstExp node,
			IndentTracker question) throws AnalysisException
	{
		return Boolean.toString(node.getValue().getValue());
	}
	
	@Override
	public String caseARecordModifier(ARecordModifier node,
			IndentTracker question) throws AnalysisException
	{
		return node.toString();
	}

	
	@Override
	public String caseAUndefinedExp(AUndefinedExp node, IndentTracker question)
			throws AnalysisException
	{
		return node.toString();
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
