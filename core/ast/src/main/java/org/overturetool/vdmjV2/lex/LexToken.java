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
package org.overturetool.vdmj.lex;





import java.io.Serializable;
import java.util.Map;

import org.overture.ast.analysis.intf.IAnalysis;
import org.overture.ast.analysis.intf.IAnswer;
import org.overture.ast.analysis.intf.IQuestion;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.node.INode;
import org.overture.ast.node.Node;
import org.overture.ast.node.NodeEnum;


/**
 * The parent class for all lexical token types.
 */

 public  class LexToken extends Node implements Serializable
{
	private static final long serialVersionUID = 1L;

	/** The textual location of the token. */
	public final LexLocation location;
	/** The basic type of the token. */
	public final VDMToken type;

	/**
	 * Create a token of the given type at the given location.
	 *
	 * @param location	The location of the token.
	 * @param type		The basic type of the token.
	 */

	public LexToken(LexLocation location, VDMToken type)
	{
		this.location = location;
		this.type = type;
	}

	/**
	 * Test whether this token is a given basic type.
	 *
	 * @param ttype	The type to test.
	 * @return	True if this is of that type.
	 */

	public boolean is(VDMToken ttype)
	{
		return this.type == ttype;
	}

	/**
	 * Test whether this token is not a given basic type.
	 *
	 * @param ttype	The type to test.
	 * @return	True if this is not of that type.
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
	public Object clone()
	{
		return new LexToken(location,type);
	}

	@Override
	public INode clone(Map<INode, INode> oldToNewMap) {
		Node newNode = (Node) clone();
		oldToNewMap.put(this, newNode);
		return newNode;
	}

	@Override
	public void apply(IAnalysis analysis) {
		analysis.caseLexToken(this);
	}

	@Override
	public <A> A apply(IAnswer<A> caller) {
		return caller.caseLexToken(this);
	}

	@Override
	public <Q> void apply(IQuestion<Q> caller, Q question) {
		caller.caseLexToken(this, question);
	}

	@Override
	public <Q, A> A apply(IQuestionAnswer<Q, A> caller, Q question) {
		return caller.caseLexToken(this, question);
	}

	@Override
	public NodeEnum kindNode() {
		return NodeEnum.ExternalDefined;
	}

	@Override
	public void removeChild(INode child) {
				
	}

}
