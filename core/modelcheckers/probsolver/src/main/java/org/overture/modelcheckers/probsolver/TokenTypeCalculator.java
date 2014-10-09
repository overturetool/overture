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

import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.expressions.AMkBasicExp;
import org.overture.ast.types.AIntNumericBasicType;
import org.overture.ast.types.ATokenBasicType;
import org.overture.ast.types.PType;
import org.overture.ast.util.PTypeSet;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * Utility class capable of extracting all token types from a node tree<b> If no mk_token's can be found to extract the
 * type from then a {@link AIntNumericBasicType} is returned
 * 
 * @author kel
 */
public class TokenTypeCalculator extends DepthFirstAnalysisAdaptor
{
	final List<PType> types = new Vector<PType>();

	protected ITypeCheckerAssistantFactory af;

	public TokenTypeCalculator(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public void caseAMkBasicExp(AMkBasicExp node) throws AnalysisException
	{
		if (node.getType() instanceof ATokenBasicType
				&& node.getArg().getType() != null /*
													 * the pre/post expressions are cloned so the original ones are
													 * never type checked
													 */)
		{
			types.add(node.getArg().getType().clone());
		}
	}

	public PType getTokenType()
	{
		if (types.isEmpty())
		{
			return new AIntNumericBasicType();
		}
		PTypeSet ts = new PTypeSet(types, af);
		return ts.getType(null);
	}
}
