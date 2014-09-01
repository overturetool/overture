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

import org.overture.ast.intf.lex.ILexKeywordToken;
import org.overture.ast.intf.lex.ILexLocation;

public class LexKeywordToken extends LexToken implements ILexKeywordToken
{
	private static final long serialVersionUID = 1L;

	public LexKeywordToken(VDMToken type, ILexLocation location)
	{
		super(location, type);
	}

	@Override
	public ILexKeywordToken clone()
	{
		return new LexKeywordToken(type, location);
	}

	// @Override
	// public void apply(IAnalysis analysis) {
	// analysis.caselexk(this);
	// }
	//
	// @Override
	// public <A> A apply(IAnswer<A> caller) {
	// return caller.caseLexKeywordToken(this);
	// }
	//
	// @Override
	// public <Q> void apply(IQuestion<Q> caller, Q question) {
	// caller.caseLexKeywordToken(this, question);
	// }
	//
	// @Override
	// public <Q, A> A apply(IQuestionAnswer<Q, A> caller, Q question) {
	// return caller.caseLexKeywordToken(this, question);
	// }

}
