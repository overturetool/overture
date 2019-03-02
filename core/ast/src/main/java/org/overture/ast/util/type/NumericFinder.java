/*
 * #%~
 * The Overture Abstract Syntax Tree
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
package org.overture.ast.util.type;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.assistant.IAstAssistantFactory;
import org.overture.ast.node.INode;
import org.overture.ast.types.ABracketType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SBasicType;
import org.overture.ast.types.SInvariantType;
import org.overture.ast.types.SNumericBasicType;

/**
 * Used to find if a type is Numeric.
 * 
 * @author gkanos
 */

public class NumericFinder extends AnswerAdaptor<Boolean>
{
	protected final IAstAssistantFactory af;
	protected final String fromModule;

	public NumericFinder(IAstAssistantFactory af, String fromModule)
	{
		this.af = af;
		this.fromModule = fromModule;
	}

	@Override
	public Boolean defaultSBasicType(SBasicType type) throws AnalysisException
	{
		SBasicType bType = type;
		return bType instanceof SNumericBasicType;
	}

	@Override
	public Boolean caseABracketType(ABracketType type) throws AnalysisException
	{
		return type.getType().apply(THIS);
	}

	@Override
	public Boolean caseANamedInvariantType(ANamedInvariantType type)
			throws AnalysisException
	{
		if (isOpaque(type)) return false;
		return type.getType().apply(THIS);
	}

	@Override
	public Boolean defaultSInvariantType(SInvariantType type)
			throws AnalysisException
	{
		return false;
	}

	@Override
	public Boolean caseAOptionalType(AOptionalType type)
			throws AnalysisException
	{
		return type.getType().apply(THIS);
	}

	@Override
	public Boolean caseAUnionType(AUnionType type) throws AnalysisException
	{
		return af.createPTypeAssistant().getNumeric(type, fromModule) != null;
	}

	@Override
	public Boolean caseAUnknownType(AUnknownType type) throws AnalysisException
	{
		return true;
	}

	@Override
	public Boolean defaultPType(PType type) throws AnalysisException
	{
		return false;
	}

	@Override
	public Boolean createNewReturnValue(INode node) throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean createNewReturnValue(Object node) throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * An invariant type is opaque to the caller if it is opaque (not struct exported) and
	 * the module being used from is not the type's defining module.  
	 */
	protected boolean isOpaque(SInvariantType type)
	{
		return type.getOpaque() && fromModule != null && !type.getLocation().getModule().equals(fromModule);
	}
}
