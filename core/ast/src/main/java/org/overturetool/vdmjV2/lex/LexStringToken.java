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

import org.overture.ast.analysis.intf.IAnalysis;
import org.overture.ast.analysis.intf.IAnswer;
import org.overture.ast.analysis.intf.IQuestion;
import org.overture.ast.analysis.intf.IQuestionAnswer;

public class LexStringToken extends LexToken {
	private static final long serialVersionUID = 1L;
	public final String value;

	public LexStringToken(String value, LexLocation location) {
		super(location, VDMToken.STRING);
		this.value = value;
	}

	@Override
	public String toString() {
		return "\"" + value + "\"";
	}

	@Override
	public Object clone() {
		return new LexStringToken(value, location);
	}
	
	@Override
	public void apply(IAnalysis analysis) {
		analysis.caseLexStringToken(this);
	}

	@Override
	public <A> A apply(IAnswer<A> caller) {
		return caller.caseLexStringToken(this);
	}

	@Override
	public <Q> void apply(IQuestion<Q> caller, Q question) {
		caller.caseLexStringToken(this, question);
	}

	@Override
	public <Q, A> A apply(IQuestionAnswer<Q, A> caller, Q question) {
		return caller.caseLexStringToken(this, question);
	}
}
