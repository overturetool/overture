/*
 * #%~
 * Integration of the ProB Solver for VDM
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
package org.overture.modelcheckers.probsolver;

import java.util.HashSet;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.expressions.AQuoteLiteralExp;
import org.overture.ast.types.AQuoteType;

/**
 * Utility class capable of extracting all quote names from a node tree
 * 
 * @author kel
 */
public class QuoteLiteralFinder extends DepthFirstAnalysisAdaptor
{
	final Set<String> quotes = new HashSet<String>();

	@Override
	public void caseAQuoteLiteralExp(AQuoteLiteralExp node)
			throws AnalysisException
	{
		quotes.add(node.getValue().getValue());
	}

	@Override
	public void caseAQuoteType(AQuoteType node) throws AnalysisException
	{
		quotes.add(node.getValue().getValue());
	}

	public Set<String> getQuoteLiterals()
	{
		return this.quotes;
	}
}
