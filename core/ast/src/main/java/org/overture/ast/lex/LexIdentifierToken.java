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
package org.overture.ast.lex;

import java.util.HashMap;
import java.util.Map;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.intf.IAnalysis;
import org.overture.ast.analysis.intf.IAnswer;
import org.overture.ast.analysis.intf.IQuestion;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.intf.lex.ILexIdentifierToken;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.intf.lex.ILexNameToken;

public class LexIdentifierToken extends LexToken implements ILexIdentifierToken
{
	private static final long serialVersionUID = 1L;
	public final String name;
	public final boolean old;

	public LexIdentifierToken(String name, boolean old, ILexLocation location)
	{
		super(location, VDMToken.IDENTIFIER);
		this.name = name;
		this.old = old;
	}

	@Override
	public boolean getOld()
	{
		return old;
	}

	public ILexNameToken getClassName()
	{
		// We don't know the class name of the name of a class until we've
		// read the name. So create a new location with the right module.

		LexLocation loc = new LexLocation(location.getFile(), name, location.getStartLine(), location.getStartPos(), location.getEndLine(), location.getEndPos(), location.getStartOffset(), location.getEndOffset());

		return new LexNameToken("CLASS", name, loc);
	}

	@Override
	public boolean equals(Object other)
	{
		if (other instanceof LexIdentifierToken)
		{
			LexIdentifierToken tother = (LexIdentifierToken) other;
			return this.name.equals(tother.getName())
					&& this.old == tother.isOld();
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		return name.hashCode() + (old ? 1 : 0);
	}

	@Override
	public String toString()
	{
		return name + (old ? "~" : "");
	}

	public boolean isOld()
	{
		return old;
	}

	public String getName()
	{
		return name;
	}

	public ILexLocation getLocation()
	{
		return location;
	}

	@Override
	public LexIdentifierToken clone()
	{
		return new LexIdentifierToken(name, old, location);
	}

	@Override
	public void apply(IAnalysis analysis) throws AnalysisException
	{
		analysis.caseILexIdentifierToken(this);
	}

	@Override
	public <A> A apply(IAnswer<A> caller) throws AnalysisException
	{
		return caller.caseILexIdentifierToken(this);
	}

	@Override
	public <Q> void apply(IQuestion<Q> caller, Q question)
			throws AnalysisException
	{
		caller.caseILexIdentifierToken(this, question);
	}

	@Override
	public <Q, A> A apply(IQuestionAnswer<Q, A> caller, Q question)
			throws AnalysisException
	{
		return caller.caseILexIdentifierToken(this, question);
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
		Map<String, Object> fields = new HashMap<String, Object>();
		if (includeInheritedFields)
		{
			fields.putAll(super.getChildren(includeInheritedFields));
		}
		fields.put("name", this.name);
		fields.put("old", this.old);
		return fields;
	}
}
