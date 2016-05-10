/*******************************************************************************
 *
 *	Copyright (c) 2008 Fujitsu Services Ltd.
 *
 *	Author: Nick Battle
 *
 *	This file is part of VDMJ.
 *
 *	VDMJ is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	VDMJ is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with VDMJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

package org.overture.ast.lex;

import java.util.HashMap;
import java.util.Map;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.intf.IAnalysis;
import org.overture.ast.analysis.intf.IAnswer;
import org.overture.ast.analysis.intf.IQuestion;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.intf.lex.ILexCharacterToken;
import org.overture.ast.intf.lex.ILexLocation;

public class LexCharacterToken extends LexToken implements ILexCharacterToken
{
	private static final long serialVersionUID = 1L;
	public final char unicode;

	public LexCharacterToken(char value, ILexLocation location)
	{
		super(location, VDMToken.CHARACTER);
		this.unicode = value;
	}

	@Override
	public String toString()
	{
		return super.toString()
				+ " value "
				+ (Character.isISOControl(unicode) ? Integer.toString(unicode)
						+ " decimal" : "[" + unicode + "]");
	}

	@Override
	public char getValue()
	{
		return unicode;
	}

	@Override
	public ILexCharacterToken clone()
	{
		return new LexCharacterToken(unicode, location);
	}

	@Override
	public void apply(IAnalysis analysis) throws AnalysisException
	{
		analysis.caseILexCharacterToken(this);
	}

	@Override
	public <A> A apply(IAnswer<A> caller) throws AnalysisException
	{
		return caller.caseILexCharacterToken(this);
	}

	@Override
	public <Q> void apply(IQuestion<Q> caller, Q question)
			throws AnalysisException
	{
		caller.caseILexCharacterToken(this, question);
	}

	@Override
	public <Q, A> A apply(IQuestionAnswer<Q, A> caller, Q question)
			throws AnalysisException
	{
		return caller.caseILexCharacterToken(this, question);
	}

	/**
	 * Creates a map of all field names and their value
	 * 
	 * @param includeInheritedFields
	 *            if true all inherited fields are included
	 * @return a a map of names to values of all fields
	 */
	@Override
	public Map<String, Object> getChildren(Boolean includeInheritedFields)
	{
		Map<String, Object> fields = new HashMap<>();
		if (includeInheritedFields)
		{
			fields.putAll(super.getChildren(includeInheritedFields));
		}
		fields.put("unicode", this.unicode);
		return fields;
	}
}
