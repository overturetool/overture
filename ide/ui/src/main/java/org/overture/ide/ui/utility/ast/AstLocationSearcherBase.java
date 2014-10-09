/*
 * #%~
 * org.overture.ide.ui
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
package org.overture.ide.ui.utility.ast;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.traces.PTraceCoreDefinition;
import org.overture.ast.definitions.traces.PTraceDefinition;
import org.overture.ast.expressions.ACaseAlternative;
import org.overture.ast.expressions.APostOpExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.intf.lex.ILexBooleanToken;
import org.overture.ast.intf.lex.ILexCharacterToken;
import org.overture.ast.intf.lex.ILexIdentifierToken;
import org.overture.ast.intf.lex.ILexIntegerToken;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.intf.lex.ILexQuoteToken;
import org.overture.ast.intf.lex.ILexRealToken;
import org.overture.ast.intf.lex.ILexStringToken;
import org.overture.ast.intf.lex.ILexToken;
import org.overture.ast.modules.PExport;
import org.overture.ast.modules.PImport;
import org.overture.ast.patterns.ADefPatternBind;
import org.overture.ast.patterns.PBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.statements.ACaseAlternativeStm;
import org.overture.ast.statements.PObjectDesignator;
import org.overture.ast.statements.PStateDesignator;
import org.overture.ast.statements.PStm;
import org.overture.ast.types.PType;

public class AstLocationSearcherBase extends DepthFirstAnalysisAdaptor
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// /**
	// * Never called by analysis from super
	// */
	// @Override
	// public void caseILexLocation(ILexLocation node) throws AnalysisException
	// {
	// }

	/* First all tokens */
	@Override
	public void caseILexToken(ILexToken node) throws AnalysisException
	{
		caseILexLocation(node.getLocation());
	}

	@Override
	public void caseILexNameToken(ILexNameToken node) throws AnalysisException
	{
		caseILexLocation(node.getLocation());
	}

	@Override
	public void caseILexIdentifierToken(ILexIdentifierToken node)
			throws AnalysisException
	{
		caseILexLocation(node.getLocation());
	}

	@Override
	public void caseILexBooleanToken(ILexBooleanToken node)
			throws AnalysisException
	{
		caseILexLocation(node.getLocation());
	}

	@Override
	public void caseILexCharacterToken(ILexCharacterToken node)
			throws AnalysisException
	{
		caseILexLocation(node.getLocation());
	}

	@Override
	public void caseILexIntegerToken(ILexIntegerToken node)
			throws AnalysisException
	{
		caseILexLocation(node.getLocation());
	}

	@Override
	public void caseILexQuoteToken(ILexQuoteToken node)
			throws AnalysisException
	{
		caseILexLocation(node.getLocation());
	}

	@Override
	public void caseILexRealToken(ILexRealToken node) throws AnalysisException
	{
		caseILexLocation(node.getLocation());
	}

	@Override
	public void caseILexStringToken(ILexStringToken node)
			throws AnalysisException
	{
		caseILexLocation(node.getLocation());
	}

	/* All nodes that have a location */
	@Override
	public void caseAPostOpExp(APostOpExp node) throws AnalysisException
	{
		caseILexLocation(node.getLocation());
	}

	@Override
	public void caseACaseAlternative(ACaseAlternative node)
			throws AnalysisException
	{
		caseILexLocation(node.getLocation());
	}

	@Override
	public void caseADefPatternBind(ADefPatternBind node)
			throws AnalysisException
	{
		caseILexLocation(node.getLocation());
	}

	@Override
	public void caseACaseAlternativeStm(ACaseAlternativeStm node)
			throws AnalysisException
	{
		caseILexLocation(node.getLocation());
	}

	@Override
	public void defaultOutPExp(PExp node) throws AnalysisException
	{
		caseILexLocation(node.getLocation());
	}

	@Override
	public void defaultOutPType(PType node) throws AnalysisException
	{
		caseILexLocation(node.getLocation());
	}

	@Override
	public void defaultOutPPattern(PPattern node) throws AnalysisException
	{
		caseILexLocation(node.getLocation());
	}

	
	@Override
	public void defaultOutPBind(PBind node) throws AnalysisException
	{
		caseILexLocation(node.getLocation());
	}
	

	@Override
	public void defaultOutPMultipleBind(PMultipleBind node)
			throws AnalysisException
	{
		caseILexLocation(node.getLocation());
	}

	@Override
	public void defaultOutPDefinition(PDefinition node)
			throws AnalysisException
	{
		caseILexLocation(node.getLocation());
	}

	@Override
	public void defaultOutPStm(PStm node) throws AnalysisException
	{
		caseILexLocation(node.getLocation());
	}

	@Override
	public void defaultOutPImport(PImport node) throws AnalysisException
	{
		caseILexLocation(node.getLocation());
	}

	@Override
	public void defaultOutPExport(PExport node) throws AnalysisException
	{
		caseILexLocation(node.getLocation());
	}

	@Override
	public void defaultOutPTraceDefinition(PTraceDefinition node)
			throws AnalysisException
	{
		caseILexLocation(node.getLocation());
	}

	@Override
	public void defaultOutPTraceCoreDefinition(PTraceCoreDefinition node)
			throws AnalysisException
	{
		caseILexLocation(node.getLocation());
	}

	@Override
	public void defaultOutPStateDesignator(PStateDesignator node)
			throws AnalysisException
	{
		caseILexLocation(node.getLocation());
	}

	@Override
	public void defaultOutPObjectDesignator(PObjectDesignator node)
			throws AnalysisException
	{
		caseILexLocation(node.getLocation());
	}
}
