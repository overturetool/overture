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
import org.overture.ast.expressions.ADivNumericBinaryExp;
import org.overture.ast.expressions.ADivideNumericBinaryExp;
import org.overture.ast.expressions.AImpliesBooleanBinaryExp;
import org.overture.ast.expressions.AIntLiteralExp;
import org.overture.ast.expressions.AModNumericBinaryExp;
import org.overture.ast.expressions.APlusNumericBinaryExp;
import org.overture.ast.expressions.ARealLiteralExp;
import org.overture.ast.expressions.ASubtractNumericBinaryExp;
import org.overture.ast.expressions.ATimesNumericBinaryExp;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.node.INode;

class ExpressionNpp extends QuestionAnswerAdaptor<IndentTracker, String>
		implements IPrettyPrinter
{

	ISymbolTable mytable;
	IPrettyPrinter rootNpp;

	private static String EXPRESSION_NOT_FOUND = "ERROR: Expression Node not found";
	private static String space = " ";

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

		StringBuilder sb = new StringBuilder();

		sb.append(l);
		sb.append(space);
		sb.append(op);
		sb.append(space);
		sb.append(r);

		return Utilities.wrap(sb.toString());
	}
	
	@Override
	public String caseASubtractNumericBinaryExp(ASubtractNumericBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS, question);
		String op = mytable.getMINUS();
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(l);
		sb.append(space);
		sb.append(op);
		sb.append(space);
		sb.append(r);
		
		return Utilities.wrap(sb.toString());
	}
	
	@Override
	public String caseATimesNumericBinaryExp(ATimesNumericBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS,question);
		String op = mytable.getTIMES();
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(l);
		sb.append(space);
		sb.append(op);
		sb.append(space);
		sb.append(r);
		
		return Utilities.wrap(sb.toString());
	}
	
	@Override
	public String caseADivideNumericBinaryExp(ADivideNumericBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS,question);
		String op = mytable.getDIVIDE();
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(l);
		sb.append(space);
		sb.append(op);
		sb.append(space);
		sb.append(r);
		
		return Utilities.wrap(sb.toString());
	}
	
	@Override
	public String caseAModNumericBinaryExp(AModNumericBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS,question);
		String op = mytable.getMOD();
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(l);
		sb.append(space);
		sb.append(op);
		sb.append(space);
		sb.append(r);
		
		return Utilities.wrap(sb.toString());
	}
	
	@Override
	public String caseADivNumericBinaryExp(ADivNumericBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS,question);
		String op = mytable.getDIV();
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(l);
		sb.append(space);
		sb.append(op);
		sb.append(space);
		sb.append(r);
		
		return Utilities.wrap(sb.toString());
	}
	
	@Override
	public String caseAImpliesBooleanBinaryExp(AImpliesBooleanBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS,question);
		String op = mytable.getIMPLIES();
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(l);
		sb.append(space);
		sb.append(op);
		sb.append(space);
		sb.append(r);
		
		return	Utilities.wrap(sb.toString());
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
