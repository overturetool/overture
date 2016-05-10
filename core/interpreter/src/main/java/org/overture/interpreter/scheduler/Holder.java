/*******************************************************************************
 *
 *	Copyright (c) 2009 Fujitsu Services Ltd.
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

package org.overture.interpreter.scheduler;

import java.io.Serializable;

import org.overture.ast.intf.lex.ILexLocation;
import org.overture.interpreter.runtime.Context;

public class Holder<T> implements Serializable
{
	private static final long serialVersionUID = 1L;
	private ControlQueue cq = new ControlQueue();
	private T contents = null;

	public synchronized void set(T object)
	{
		contents = object;
		cq.stim();
	}

	public T get(Context ctxt, ILexLocation location)
	{
		cq.join(ctxt, location);

		while (contents == null)
		{
			cq.block(ctxt, location);
		}

		T result;

		synchronized (this)
		{
			result = contents;
		}

		cq.leave();

		return result;
	}
}
