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
package org.overturetool.interpreter.vdmj.lex;

import java.io.Serializable;
import java.util.Map;

import org.overture.interpreter.ast.analysis.IAnalysisInterpreter;
import org.overture.interpreter.ast.analysis.IAnswerInterpreter;
import org.overture.interpreter.ast.analysis.IQuestionAnswerInterpreter;
import org.overture.interpreter.ast.analysis.IQuestionInterpreter;
import org.overture.interpreter.ast.node.NodeEnumInterpreter;
import org.overture.interpreter.ast.node.NodeInterpreter;
import org.overturetool.vdmj.lex.VDMToken;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ContextException;



/**
 * The parent class for all lexical token types.
 */

 public  class LexToken extends NodeInterpreter implements Serializable
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

	/**
	 * @see org.overturetool.vdmj.definitions.Definition#abort
	 */

	public void abort(int number, String msg, Context ctxt)
	{
		throw new ContextException(number, msg, location, ctxt);
	}
	
	@Override
	public NodeInterpreter clone(Map<NodeInterpreter, NodeInterpreter> oldToNewMap) {
		NodeInterpreter newNode = (NodeInterpreter) clone();
		oldToNewMap.put(this, newNode);
		return newNode;
	}

	@Override
	public void apply(IAnalysisInterpreter analysis) {
		//Do not visit
	}

	@Override
	public <A> A apply(IAnswerInterpreter<A> caller) {
		//Do not visit
		return null;
	}

	@Override
	public <Q> void apply(IQuestionInterpreter<Q> caller, Q question) {
		//Do not visit
	}

	@Override
	public <Q, A> A apply(IQuestionAnswerInterpreter<Q, A> caller, Q question) {
		//Do not visit
		return null;
	}

	@Override
	public NodeEnumInterpreter kindNode() {
		return null;
	}

	@Override
	public void removeChild(NodeInterpreter child) {
				
	}

}