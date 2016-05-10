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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.intf.IAnalysis;
import org.overture.ast.analysis.intf.IAnswer;
import org.overture.ast.analysis.intf.IQuestion;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.intf.lex.ILexToken;
import org.overture.ast.node.INode;
import org.overture.ast.node.Node;

/**
 * The parent class for all lexical token types.
 */

public class LexToken extends Node implements ILexToken, Serializable
{
	private static final long serialVersionUID = 1L;

	/** The textual location of the token. */
	public final ILexLocation location;
	/** The basic type of the token. */
	public final VDMToken type;

	@Override
	public VDMToken getType()
	{
		return type;
	}

	public ILexLocation getLocation()
	{
		return location;
	}

	/**
	 * Create a token of the given type at the given location.
	 * 
	 * @param location
	 *            The location of the token.
	 * @param type
	 *            The basic type of the token.
	 */

	public LexToken(ILexLocation location, VDMToken type)
	{
		this.location = location;
		this.type = type;
	}

	/**
	 * Test whether this token is a given basic type.
	 * 
	 * @param ttype
	 *            The type to test.
	 * @return True if this is of that type.
	 */

	public boolean is(VDMToken ttype)
	{
		return this.type == ttype;
	}

	/**
	 * Test whether this token is not a given basic type.
	 * 
	 * @param ttype
	 *            The type to test.
	 * @return True if this is not of that type.
	 */

	public boolean isNot(VDMToken ttype)
	{
		return this.type != ttype;
	}

	@Override
	public String toString()
	{
		return type.toString();
	}

	@Override
	public ILexToken clone()
	{
		return new LexToken(location, type);
	}

	@Override
	public INode clone(Map<INode, INode> oldToNewMap)
	{
		Node newNode = (Node) clone();
		oldToNewMap.put(this, newNode);
		return newNode;
	}

	@Override
	public void apply(IAnalysis analysis) throws AnalysisException
	{
		analysis.caseILexToken(this);
	}

	@Override
	public <A> A apply(IAnswer<A> caller) throws AnalysisException
	{
		return caller.caseILexToken(this);
	}

	@Override
	public <Q> void apply(IQuestion<Q> caller, Q question)
			throws AnalysisException
	{
		caller.caseILexToken(this, question);
	}

	@Override
	public <Q, A> A apply(IQuestionAnswer<Q, A> caller, Q question)
			throws AnalysisException
	{
		return caller.caseILexToken(this, question);
	}

	@Override
	public void removeChild(INode child)
	{

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
		fields.put("location", this.location);
		fields.put("type", this.type);
		return fields;
	}

}
