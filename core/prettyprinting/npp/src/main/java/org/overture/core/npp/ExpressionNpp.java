/*
 * #%~
 * New Pretty Printer
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.core.npp;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.*;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.AConcatenationPattern;
import org.overture.ast.patterns.AExpressionPattern;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.AIgnorePattern;
import org.overture.ast.patterns.AIntegerPattern;
import org.overture.ast.patterns.AMapPattern;
import org.overture.ast.patterns.AMapUnionPattern;
import org.overture.ast.patterns.AMapletPatternMaplet;
import org.overture.ast.patterns.ARecordPattern;
import org.overture.ast.patterns.ASeqPattern;
import org.overture.ast.patterns.ASetBind;
import org.overture.ast.patterns.ASetMultipleBind;
import org.overture.ast.patterns.ASetPattern;
import org.overture.ast.patterns.ATuplePattern;
import org.overture.ast.patterns.ATypeBind;
import org.overture.ast.patterns.ATypeMultipleBind;
import org.overture.ast.patterns.AUnionPattern;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.ABracketType;
import org.overture.ast.types.ACharBasicType;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AInMapMapType;
import org.overture.ast.types.AIntNumericBasicType;
import org.overture.ast.types.AMapMapType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.ANatNumericBasicType;
import org.overture.ast.types.ANatOneNumericBasicType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AParameterType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.ARealNumericBasicType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.ASeq1SeqType;
import org.overture.ast.types.ASeqSeqType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;

class ExpressionNpp extends QuestionAnswerAdaptor<IndentTracker, String>
		implements IPrettyPrinter
{

	ISymbolTable mytable;
	IPrettyPrinter rootNpp;

	private static String EXPRESSION_NOT_FOUND = "ERROR: Expression Node not found: ";
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
		String r = node.getRight().apply(THIS, question);
		String op = mytable.getTIMES();

		return Utilities.wrap(Utilities.append(l, r, op));
	}

	@Override
	public String caseADivideNumericBinaryExp(ADivideNumericBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS, question);
		String op = mytable.getDIVIDE();

		return Utilities.wrap(Utilities.append(l, r, op));
	}

	@Override
	public String caseAModNumericBinaryExp(AModNumericBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS, question);
		String op = mytable.getMOD();

		return Utilities.wrap(Utilities.append(l, r, op));
	}

	@Override
	public String caseADivNumericBinaryExp(ADivNumericBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS, question);
		String op = mytable.getDIV();

		return Utilities.wrap(Utilities.append(l, r, op));
	}

	@Override
	public String caseARemNumericBinaryExp(ARemNumericBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS, question);
		String op = mytable.getREM();

		return Utilities.wrap(Utilities.append(l, r, op));
	}

	@Override
	public String caseAImpliesBooleanBinaryExp(AImpliesBooleanBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS, question);
		String op = mytable.getIMPLIES();

		return Utilities.wrap(Utilities.append(l, r, op));
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

		String r = node.getExp().apply(THIS, question);
		String op = mytable.getABSOLUTE();

		return Utilities.wrap(Utilities.unaryappend(r, op));
	}

	@Override
	public String caseAAndBooleanBinaryExp(AAndBooleanBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS, question);
		String op = mytable.getAND();

		return Utilities.wrap(Utilities.append(l, r, op));

	}

	@Override
	public String caseAOrBooleanBinaryExp(AOrBooleanBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS, question);
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

		String op = mytable.getLAMBDA();
		String pred = mytable.getPRED();
		String bind;

		StringBuilder sb = new StringBuilder();

		sb.append(op);
		sb.append(space);
		int n = 0;
		for (ATypeBind x : node.getBindList())
		{
			n++;
			if (node.getBindList().size() != n)
			{
				bind = x.apply(THIS, question);//rootNpp.defaultPBind(x, question);

				sb.append(bind);
				sb.append(mytable.getCOMMA());
				sb.append(space);
			} else
			{
				bind = x.apply(THIS, question);//rootNpp.defaultPBind(x, question);
				sb.append(bind);
				sb.append(space);
			}
		}
		// while (node.getBindList().size() != 0){
		// if(node.getBindList().size() > 1){
		// //bind = node.getBindList().poll().apply(THIS, question);
		// bind = rootNpp.defaultPBind(node.getBindList().poll(), question);
		//
		// sb.append(bind);
		// sb.append(mytable.getCOMMA());
		// sb.append(space);
		// }
		// else{
		// //bind = node.getBindList().poll().apply(THIS, question);
		// bind = rootNpp.defaultPBind(node.getBindList().poll(), question);
		// sb.append(bind);
		// sb.append(space);
		// }
		// }

		sb.append(pred);
		sb.append(space);

		sb.append(exp);

		return sb.toString();
	}

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
		StringBuilder sb = new StringBuilder();
		sb.append(leftcurly);
		int n = 0;
		for(PExp x:node.getMembers()){
			n++;
			if(node.getMembers().size() != n)
			{
				sb.append(x.apply(THIS, question));
				sb.append(mytable.getCOMMA());
				sb.append(space);
			}
			else
			{
				sb.append(x.apply(THIS, question));
			}
		}
		sb.append(rightcurly);
		
		return sb.toString();
		//return node.toString();
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

		String pred = "";
		if (node.getPredicate() != null)
		{
			pred = node.getPredicate().apply(THIS, question);
		}
		StringBuilder sb = new StringBuilder();

		sb.append(leftcurly);
		sb.append(exp);
		sb.append(bar);
		for (PMultipleBind x : node.getBindings())
		{
			sb.append(x.apply(THIS, question));
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
		for (PPattern x : node.getPlist())
		{
			sb.append(x.apply(THIS, question));
		}
	
		sb.append(space);
		sb.append(mytable.getINSET());
		sb.append(space);
		sb.append(node.getSet().apply(THIS, question));

		return sb.toString();
	}

	@Override
	public String caseAExistsExp(AExistsExp node, IndentTracker question)
			throws AnalysisException
	{
		String op = mytable.getEXISTS();
		String binding;

		StringBuilder sb = new StringBuilder();

		sb.append(op);
		sb.append(space);
		int n = 0;
		for (PMultipleBind x : node.getBindList())
		{
			n++;
			if (node.getBindList().size() != n)
			{
				binding = x.apply(THIS, question);
				sb.append(binding);
				sb.append(mytable.getCOMMA());
				sb.append(space);
			} else
			{
				binding = x.apply(THIS, question);
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
		String binding = node.getBind().apply(THIS, question);//rootNpp.defaultPBind(node.getBind(), question);// node.getBind().apply(THIS, question);
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
		int n = 0;
		for (PMultipleBind x : node.getBindList())
		{
			n++;
			if (node.getBindList().size() != n)
			{
				sb.append(x.apply(THIS, question));
				sb.append(mytable.getCOMMA());
				sb.append(space);
			} else
			{
				sb.append(x.apply(THIS, question));
				sb.append(space);
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
		String binding = node.getBind().apply(THIS, question);//rootNpp.defaultPBind(node.getBind(), question);// node.getBind().apply(THIS, question);
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
		StringBuilder sb = new StringBuilder();
		sb.append(leftsq);
		int n = 0;
		for(PExp x:node.getMembers()){
			n++;
			if(node.getMembers().size() != n)
			{
				sb.append(x.apply(THIS, question));
				sb.append(mytable.getCOMMA());
				sb.append(space);
			}
			else
			{
				sb.append(x.apply(THIS, question));
			}
		}
		sb.append(rightsq);
		
		return sb.toString();
	}

	@Override
	public String caseASeqCompSeqExp(ASeqCompSeqExp node, IndentTracker question)
			throws AnalysisException
	{
		String exp = node.getFirst().apply(THIS, question);
		String bind = node.getSetBind().apply(THIS, question);
		String pred = "";
		if (node.getPredicate() != null){
			pred = node.getPredicate().apply(THIS, question);
		}
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
		// System.out.print(sb.toString()+"\n");
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
	public String caseAMapletPatternMaplet(AMapletPatternMaplet node,
			IndentTracker question) throws AnalysisException
	{
		return Utilities.append(node.getFrom().apply(THIS, question), node.getTo().apply(THIS, question), mytable.getMAPLET());
	}

	@Override
	public String caseAMapEnumMapExp(AMapEnumMapExp node, IndentTracker question)
			throws AnalysisException
	{
		StringBuilder sb = new StringBuilder();
		sb.append(leftcurly);
		int n = 0;
		for (AMapletExp x : node.getMembers())
		{
			n++;
			if (node.getMembers().size() != n)
			{
				sb.append(x.apply(THIS, question));
				sb.append(mytable.getCOMMA());
				sb.append(space);
			} else
			{
				sb.append(x.apply(THIS, question));
			}
		}
		sb.append(rightcurly);
		return sb.toString();
	}
	
	@Override
	public String caseAMapPattern(AMapPattern node, IndentTracker question)
			throws AnalysisException
	{
		StringBuilder sb = new StringBuilder();
		sb.append(leftcurly);
		int n = 0;
		for (AMapletPatternMaplet x : node.getMaplets())
		{
			n++;
			if (node.getMaplets().size() != n)
			{
				sb.append(x.apply(THIS, question));
				sb.append(mytable.getCOMMA());
				sb.append(space);
			} else
			{
				sb.append(x.apply(THIS, question));
			}
		}
		sb.append(rightcurly);
		return sb.toString();
	}
	
	@Override
	public String caseAMapUnionPattern(AMapUnionPattern node,
			IndentTracker question) throws AnalysisException
	{
		return Utilities.append(node.getLeft().apply(THIS, question), node.getRight().apply(THIS, question), mytable.getMUNION());
	}
	

	@Override
	public String caseAMapCompMapExp(AMapCompMapExp node, IndentTracker question)
			throws AnalysisException
	{
		String exp = node.getFirst().apply(THIS, question);
		String pred = "";

		if (node.getPredicate() != null)
		{
			pred = node.getPredicate().apply(THIS, question);
		}
		StringBuilder sb = new StringBuilder();

		sb.append(leftcurly);
		sb.append(exp);
		sb.append(space);
		sb.append(bar);

		int n = 0;
		for (PMultipleBind x : node.getBindings())
		{
			n++;
			if (node.getBindings().size() != n)
			{
				sb.append(x.apply(THIS, question));
				sb.append(mytable.getCOMMA());
				sb.append(space);
			} else
			{
				sb.append(x.apply(THIS, question));
				sb.append(space);
			}
		}

		// while (node.getBindings().size() != 0)
		// {
		// if (node.getBindings().size() >1)
		// {
		// bind = node.getBindings().poll().apply(THIS, question);
		// sb.append(bind);
		// sb.append(mytable.getCOMMA());
		// sb.append(space);
		// }
		// else
		// {
		// bind = node.getBindings().poll().apply(THIS, question);
		// sb.append(bind);
		// sb.append(space);
		// }
		// }

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
		String r = node.getRight().apply(THIS, question);
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
		String r = node.getRight().apply(THIS, question);
		String op = mytable.getDOMRESTO();

		return Utilities.append(l, r, op);
	}

	@Override
	public String caseADomainResByBinaryExp(ADomainResByBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS, question);
		String op = mytable.getDOMRESBY();

		return Utilities.append(l, r, op);
	}

	@Override
	public String caseARangeResToBinaryExp(ARangeResToBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS, question);
		String op = mytable.getRANGERESTO();

		return Utilities.append(l, r, op);
	}

	@Override
	public String caseARangeResByBinaryExp(ARangeResByBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS, question);
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

		String op = mytable.getCASES();

		StringBuilder sb = new StringBuilder();

		sb.append(op);
		sb.append(space);

		sb.append(exp);

		sb.append(mytable.getCOLON());
		sb.append(space);
		int n = 0;
		
		for(ACaseAlternative x : node.getCases()){
			n++;
			String caselist = x.apply(THIS, question);
			
			sb.append(brtab);
			sb.append(caselist);
			if (node.getCases().size() != n)
			{
				sb.append(mytable.getCOMMA());
			}
			
		}

//		while (node.getCases().size() != 0)
//		{
//
//			String caselist = node.getCases().poll().apply(THIS, question);
//
//			sb.append(brtab);
//			sb.append(caselist);
//			if (node.getCases().size() > 0)
//			{
//				sb.append(mytable.getCOMMA());
//			}
//
//		}
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
		String l = node.getPattern().apply(THIS, question);
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
		for(AElseIfExp x : node.getElseList())
		{
			elseif = x.apply(THIS, question);
			sb.append(elseif);
			sb.append(brl);
			sb.append(space);
		}
//		while (node.getElseList().size() != 0)
//		{
//
//			elseif = node.getElseList().poll().apply(THIS, question);
//			sb.append(elseif);
//			sb.append(brl);
//			sb.append(space);
//		}
		sb.append(mytable.getELSE());
		sb.append(space);
		sb.append(exp2);
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

		sb1.append(mytable.getMU());

		sb2.append(exp);
		sb2.append(mytable.getCOMMA());
		sb2.append(space);
		int n = 0;
		for(ARecordModifier x:node.getModifiers())
		{
			n++;
			if(node.getModifiers().size() != n){
				mod = x.apply(THIS, question);
				sb2.append(mod);
				sb2.append(mytable.getCOMMA());
				sb2.append(space);
			}
			else
			{
				mod = x.apply(THIS, question);

				sb2.append(mod);
			}
		}
		

//		while (node.getModifiers().size() != 0)
//		{
//			if (node.getModifiers().size() > 1)
//			{
//
//				mod = node.getModifiers().poll().apply(THIS, question);
//
//				sb2.append(mod);
//				sb2.append(mytable.getCOMMA());
//				sb2.append(space);
//			} else
//			{
//				mod = node.getModifiers().poll().apply(THIS, question);
//
//				sb2.append(mod);
//			}
//
//		}

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

		int n = 0;
		for(PDefinition x:node.getLocalDefs())
		{
			n++;
			if(node.getLocalDefs().size() != n){
				def = rootNpp.defaultPDefinition(x , question);
				sb.append(def);
				sb.append(mytable.getCOMMA());
				sb.append(space);
			}
			else
			{
				def = rootNpp.defaultPDefinition(x , question);
				sb.append(def);
				sb.append(space);
			}
		}
//		while (node.getLocalDefs().size() != 0)
//		{
//
//			if (node.getLocalDefs().size() > 1)
//			{
//
//				def = rootNpp.defaultPDefinition(node.getLocalDefs().poll(), question);
//				sb.append(def);
//				sb.append(mytable.getCOMMA());
//				sb.append(space);
//			} else
//			{
//				def = rootNpp.defaultPDefinition(node.getLocalDefs().poll(), question);
//				sb.append(def);
//				sb.append(space);
//			}
//		}

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

		try
		{
			String exp1 = node.getSuchThat().apply(THIS, question);

			sb.append(mytable.getLET());
			sb.append(space);

			def = node.getBind().apply(THIS, question);
			sb.append(def);
			sb.append(space);

			sb.append(mytable.getBESUCH());
			sb.append(space);
			sb.append(exp1);
		} catch (NullPointerException e)
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
		int n = 0;
		for(PDefinition x:node.getLocalDefs())
		{
			n++;
			if(node.getLocalDefs().size() != n){
				def = rootNpp.defaultPDefinition(x, question);
				sb.append(def);
				sb.append(mytable.getSEP());
				sb.append(space);
			}
			else
			{
				def = rootNpp.defaultPDefinition(x , question);
				sb.append(def);
			}
		}
		
//		while (node.getLocalDefs().size() != 0)
//		{
//			if (node.getLocalDefs().size() > 1)
//			{
//				def = rootNpp.defaultPDefinition(node.getLocalDefs().poll(), question);
//				sb.append(def);
//				sb.append(mytable.getSEP());
//				sb.append(space);
//			} else
//			{
//
//				def = rootNpp.defaultPDefinition(node.getLocalDefs().poll(), question);
//
//				sb.append(def);
//			}
//		}

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
			type = node.getTypeName().toString();
		} else
		{
			type = node.getBasicType().toString();
		}

		StringBuilder sb = new StringBuilder();

		sb.append(mytable.getISTYPE());
		sb.append(leftpar);
		sb.append(exp);
		sb.append(mytable.getCOMMA());
		sb.append(type);
		sb.append(rightpar);

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
		String r = node.getRight().apply(THIS, question);
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
		String r = node.getRight().apply(THIS, question);
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
		int n = 0;
		
		for(PExp x : node.getArgs())
		{
			n++;
			if(node.getArgs().size() != n)
			{
				arg = x.apply(THIS, question);
				sb.append(mytable.getCOMMA());
				sb.append(space);
				sb.append(arg);
				sb.append(mytable.getCOMMA());
				sb.append(space);
			}
			else
			{
				arg = x.apply(THIS, question);
				sb.append(arg);
			}
		}

//		while (node.getArgs().size() != 0)
//		{
//			if (node.getArgs().size() > 1)
//			{
//				arg = node.getArgs().poll().apply(THIS, question);
//				sb.append(mytable.getCOMMA());
//				sb.append(space);
//				sb.append(arg);
//				sb.append(mytable.getCOMMA());
//				sb.append(space);
//			} else
//			{
//				arg = node.getArgs().poll().apply(THIS, question);
//				sb.append(arg);
//			}
//		}

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
		String exp = null;
		String op = mytable.getTUPLE();
		StringBuilder sb = new StringBuilder();

		sb.append(op);
		sb.append(leftpar);
		int n = 0;

		for(PExp x:node.getArgs())
		{	n++;
			if(node.getArgs().size() != n){
				exp = x.apply(THIS, question);
				sb.append(exp);
				sb.append(mytable.getCOMMA());
				sb.append(space);
			}
			else
			{
				exp = x.apply(THIS, question);
				sb.append(exp);
			}
		}
//		while (node.getArgs().size() != 1)
//		{
//			exp = node.getArgs().poll().apply(THIS, question);
//			sb.append(exp);
//			sb.append(mytable.getCOMMA());
//			sb.append(space);
//		}
		//exp = node.getArgs().poll().apply(THIS, question);
		//sb.append(exp);

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
		String field = node.getField().getClassName().toString();

		String op = mytable.getPOINT();

		StringBuilder sb = new StringBuilder();
		sb.append(exp);
		sb.append(op);
		sb.append(field);

		return sb.toString();
	}

	@Override
	public String caseAFieldNumberExp(AFieldNumberExp node,
			IndentTracker question) throws AnalysisException
	{
		String tuple = node.getTuple().apply(THIS, question);
		String field = node.getField().toString();

		return tuple + "." + field;
	}

	@Override
	public String caseANarrowExp(ANarrowExp node, IndentTracker question)
			throws AnalysisException
	{
		String exp = node.getTest().apply(THIS, question);

		String type = node.getTypeName().toString();

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

		StringBuilder sb = new StringBuilder();

		sb.append(mytable.getPREOP());
		sb.append(space);
		sb.append(exp);

		return sb.toString();
	}

	@Override
	public String caseAFuncInstatiationExp(AFuncInstatiationExp node,
			IndentTracker question) throws AnalysisException
	{
		String func = node.getFunction().toString();
		StringBuilder sb = new StringBuilder();

		sb.append(func);
		sb.append(node.getActualTypes());
		
		return sb.toString();
	}

	@Override
	public String caseACharLiteralExp(ACharLiteralExp node,
			IndentTracker question) throws AnalysisException
	{
		return mytable.getCHARDELIM()
				+ Character.toString(node.getValue().getValue())
				+ mytable.getCHARDELIM();
	}

	@Override
	public String caseAQuoteLiteralExp(AQuoteLiteralExp node,
			IndentTracker question) throws AnalysisException
	{
		return mytable.getOPENQUOTE() + node.getValue().getValue()//.toString()
				+ mytable.getCLOSEQUOTE();
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
		return "nat1";//node.toString();
	}

	@Override
	public String caseANatNumericBasicType(ANatNumericBasicType node,
			IndentTracker question) throws AnalysisException
	{
		return "nat";//node.toString();
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
	public String caseANotUnaryExp(ANotUnaryExp node, IndentTracker question)
			throws AnalysisException
	{
		String exp = node.getExp().apply(this, question);
		String op = mytable.getNOT();

		StringBuilder sb = new StringBuilder();

		sb.append(op);
		sb.append(space);
		sb.append(exp);

		return sb.toString();
	}

	@Override
	public String caseATypeMultipleBind(ATypeMultipleBind node,
			IndentTracker question) throws AnalysisException
	{
		StringBuilder sb = new StringBuilder();
		String type = node.getType().apply(THIS, question);//.toString();

		for (PPattern x : node.getPlist())
		{
			sb.append(x.apply(THIS, question));
		}
		sb.append(space);
		sb.append(mytable.getCOLON());
		sb.append(space);
		sb.append(type);

		return sb.toString();
	}

	@Override
	public String caseATypeBind(ATypeBind node, IndentTracker question)
			throws AnalysisException
	{
		String pattern = node.getPattern().toString();
		String type = node.getType().apply(this, question);

		StringBuilder sb = new StringBuilder();

		sb.append(pattern);
		sb.append(space);
		sb.append(mytable.getCOLON());
		sb.append(space);
		sb.append(type);

		return sb.toString();
	}
	
	@Override
	public String caseASubseqExp(ASubseqExp node, IndentTracker question)
			throws AnalysisException
	{
		StringBuilder sb = new StringBuilder();
		sb.append(node.getSeq().apply(THIS, question));
		sb.append(leftpar);
		sb.append(node.getFrom().apply(THIS, question));
		sb.append(mytable.getCOMMA());
		sb.append(mytable.getRANGE());
		sb.append(mytable.getCOMMA());
		sb.append(node.getTo().apply(THIS, question));
		sb.append(rightpar);
		
		return sb.toString();
	}
	
	@Override
	public String caseAParameterType(AParameterType node, IndentTracker question)
			throws AnalysisException
	{
		return node.getName().toString();
	}
	
	@Override
	public String caseAInMapMapType(AInMapMapType node, IndentTracker question)
			throws AnalysisException
	{
		StringBuilder sb = new StringBuilder();
		sb.append("inmap");
		sb.append(space);
		sb.append(node.getFrom().apply(THIS, question));
		sb.append(space);
		sb.append("to");
		sb.append(space);
		sb.append(node.getTo().apply(THIS, question));
		
		return sb.toString();
	}
	
	@Override
	public String caseAUnionType(AUnionType node, IndentTracker question)
			throws AnalysisException
	{
		return node.toString();
	}
	
	@Override
	public String caseAUnaryMinusUnaryExp(AUnaryMinusUnaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String exp = node.getExp().apply(THIS,question);
		String minus = mytable.getMINUS();
		
		return Utilities.unaryappend(exp, minus);
		
	}
	
	@Override
	public String caseATuplePattern(ATuplePattern node, IndentTracker question)
			throws AnalysisException
	{
		String exp = null;
		String op = mytable.getTUPLE();
		StringBuilder sb = new StringBuilder();

		sb.append(op);
		sb.append(leftpar);
		int n = 0;

		for(PPattern x:node.getPlist())
		{	n++;
			if(node.getPlist().size() != n){
				exp = x.apply(THIS, question);
				sb.append(exp);
				sb.append(mytable.getCOMMA());
				sb.append(space);
			}
			else
			{
				exp = x.apply(THIS, question);
				sb.append(exp);
			}
		}

		sb.append(rightpar);

		return sb.toString();
	}
	
	@Override
	public String caseAExpressionPattern(AExpressionPattern node,
			IndentTracker question) throws AnalysisException
	{
		String exp = node.getExp().apply(THIS, question);
		return exp;
	}
	
	@Override
	public String caseAUnionPattern(AUnionPattern node, IndentTracker question)
			throws AnalysisException
	{
		String left = node.getLeft().apply(THIS, question);
		String right = node.getRight().apply(THIS, question);
		String op = mytable.getUNION();
		
		return Utilities.append(left, right, op);
	}

	@Override
	public String caseANilExp(ANilExp node, IndentTracker question)
			throws AnalysisException
	{
		return node.toString();
	}

	@Override
	public String caseAStringLiteralExp(AStringLiteralExp node,
			IndentTracker question) throws AnalysisException
	{
		return node.getValue().toString();
	}

	@Override
	public String caseACharBasicType(ACharBasicType node, IndentTracker question)
			throws AnalysisException
	{
		return "char";
	}
	
	@Override
	public String caseASeqPattern(ASeqPattern node, IndentTracker question)
			throws AnalysisException
	{
		StringBuilder sb = new StringBuilder();
		sb.append(leftcurly);
		int n = 0;
		for(PPattern x:node.getPlist()){
			n++;
			if(node.getPlist().size() != n)
			{
				sb.append(x.apply(THIS, question));
				sb.append(mytable.getCOMMA());
				sb.append(space);
			}
			else
			{
				sb.append(x.apply(THIS, question));
			}
		}
		sb.append(rightcurly);
		
		return sb.toString();
	}
	@Override
	public String caseAProductType(AProductType node, IndentTracker question)
			throws AnalysisException
	{
		StringBuilder sb = new StringBuilder();
		int n = 0;
		for(PType x:node.getTypes())
		{	n++;
			if(node.getTypes().size() != n){
				sb.append(x.apply(THIS,question));
				sb.append(mytable.getTIMES());
			}
			else
			{
				sb.append(x.apply(THIS, question));
			}
		}
		return Utilities.wrap(sb.toString());
	}
	
	@Override
	public String caseAFunctionType(AFunctionType node, IndentTracker question)
			throws AnalysisException
	{
		return node.toString();
	}
	@Override
	public String caseABracketType(ABracketType node, IndentTracker question)
			throws AnalysisException
	{
		StringBuilder sb = new StringBuilder();
		sb.append(leftpar);
		sb.append(node.getType());
		sb.append(rightpar);
		
		return sb.toString();
	}
	
	@Override
	public String caseASeq1SeqType(ASeq1SeqType node, IndentTracker question)
			throws AnalysisException
	{
		return "seq1 of" + node.getSeqof().apply(THIS, question);
	}
	
	@Override
	public String caseAConcatenationPattern(AConcatenationPattern node,
			IndentTracker question) throws AnalysisException
	{
		return Utilities.append(node.getLeft().apply(THIS, question), node.getRight().apply(THIS, question), mytable.getCONCATENATE());
		
	}
	@Override
	public String caseAOptionalType(AOptionalType node, IndentTracker question)
			throws AnalysisException
	{
		StringBuilder sb = new StringBuilder();
		sb.append(leftsq);
		sb.append(node.getType().apply(THIS, question));
		sb.append(rightsq);
		
		return sb.toString();
 	}

	@Override
	public String caseATimeExp(ATimeExp node, IndentTracker question)
			throws AnalysisException
	{
		return "time";
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
		StringBuilder sb = new StringBuilder();
		sb.append(node.getTag());
		sb.append(space);
		sb.append(mytable.getMAPLET());
		sb.append(space);
		sb.append(node.getValue().apply(THIS, question));
	
		return sb.toString();
	}
	
	

	@Override
	public String caseAUndefinedExp(AUndefinedExp node, IndentTracker question)
			throws AnalysisException
	{
		return Utilities.wrap("undefined");
	}

	public String caseASetBind(ASetBind node, IndentTracker question)
			throws AnalysisException
	{
		StringBuilder sb = new StringBuilder();

		sb.append(node.getPattern().apply(THIS, question));
		sb.append(space);
		sb.append(mytable.getINSET());
		sb.append(space);

		sb.append(node.getSet().apply(THIS, question));
		return sb.toString();
	}

	@Override
	public String caseAIdentifierPattern(AIdentifierPattern node,
			IndentTracker question) throws AnalysisException
	{
		return node.getName().toString();
	}

	@Override
	public String caseAIntegerPattern(AIntegerPattern node,
			IndentTracker question) throws AnalysisException
	{
		return node.getValue().toString();
	}
	
	@Override
	public String caseARealNumericBasicType(ARealNumericBasicType node,
			IndentTracker question) throws AnalysisException
	{
		return "real";
	}
	
	@Override
	public String caseAIntNumericBasicType(AIntNumericBasicType node,
			IndentTracker question) throws AnalysisException
	{
		return "int";
	}
	
	@Override
	public String caseARecordInvariantType(ARecordInvariantType node,
			IndentTracker question) throws AnalysisException
	{
		return node.toString();
	}
	
	@Override
	public String caseARecordPattern(ARecordPattern node, IndentTracker question)
			throws AnalysisException
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(mytable.getMK());
		sb.append(node.getTypename());
		sb.append(leftpar);
		int n = 0;
		for (PPattern x : node.getPlist())
		{
			n++;
			if(node.getDefinitions().size() != n){
				sb.append(x.apply(THIS, question));
				sb.append(mytable.getCOMMA());
				sb.append(space);
			}
			else
			{
				sb.append(x.apply(THIS, question));
			}
		}
		sb.append(rightpar);
		
		return sb.toString();
	}
	
	@Override
	public String caseASetPattern(ASetPattern node, IndentTracker question)
			throws AnalysisException
	{
		StringBuilder sb = new StringBuilder();
		sb.append(leftcurly);
		int n = 0;
		for(PPattern x:node.getPlist()){
			n++;
			if(node.getPlist().size() != n)
			{
				sb.append(x.apply(THIS, question));
				sb.append(mytable.getCOMMA());
				sb.append(space);
			}
			else
			{
				sb.append(x.apply(THIS, question));
			}
		}
		sb.append(rightcurly);
		
		return sb.toString();
	}
	
	@Override
	public String caseAMkTypeExp(AMkTypeExp node, IndentTracker question)
			throws AnalysisException
	{
		int n = 0;
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(mytable.getMK());
		if(node.getTypeName() != null){
			sb.append(node.getTypeName());
		}
		sb.append(leftpar);
		for(PExp x : node.getArgs())
		{
			
			n++;
			if(node.getArgs().size() != n){
				sb.append(x.apply(THIS, question));
				sb.append(mytable.getCOMMA());
				sb.append(space);
			}
			else
			{
				sb.append(x.apply(THIS, question));
			}
			
		}
		sb.append(rightpar);
		return sb.toString();
	}
	
	@Override
	public String caseASetType(ASetType node, IndentTracker question)
			throws AnalysisException
	{
		return "set of" + node.getSetof().apply(THIS, question);
	}
	
	@Override
	public String caseASeqSeqType(ASeqSeqType node, IndentTracker question)
			throws AnalysisException
	{
		return "seq of" + node.getSeqof().apply(THIS, question);
	}
	
	
	@Override
	public String caseAClassType(AClassType node, IndentTracker question)
			throws AnalysisException
	{
		return node.getName().toString();
	}
	
	@Override
	public String caseABooleanBasicType(ABooleanBasicType node,
			IndentTracker question) throws AnalysisException
	{
		return "bool";
	}
	
	@Override
	public String caseAIgnorePattern(AIgnorePattern node, IndentTracker question)
			throws AnalysisException
	{
		return node.toString();
	}
	
	@Override
	public String caseAMapMapType(AMapMapType node, IndentTracker question)
			throws AnalysisException
	{
		StringBuilder sb = new StringBuilder();
		sb.append("map");
		sb.append(space);
		sb.append(node.getFrom().apply(THIS, question));
		sb.append(space);
		sb.append("to");
		sb.append(space);
		sb.append(node.getTo().apply(THIS, question));
		
		return sb.toString();
	}
	
	@Override
	public String caseANamedInvariantType(ANamedInvariantType node,
			IndentTracker question) throws AnalysisException
	{
		return node.toString();
	}

	@Override
	public String createNewReturnValue(INode node, IndentTracker question)
			throws AnalysisException
	{
		return EXPRESSION_NOT_FOUND + node.getClass().getSimpleName();
	}

	@Override
	public String createNewReturnValue(Object node, IndentTracker question)
			throws AnalysisException
	{
		return EXPRESSION_NOT_FOUND + node.getClass().getSimpleName();
	}

}
