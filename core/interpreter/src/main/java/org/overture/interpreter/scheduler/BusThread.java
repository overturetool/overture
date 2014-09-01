/*******************************************************************************
 *
 *	Copyright (c) 2010 Fujitsu Services Ltd.
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

import org.overture.ast.intf.lex.ILexLocation;
import org.overture.interpreter.runtime.Context;

public class BusThread extends SchedulablePoolThread
{
	private static final long serialVersionUID = 1L;

	public BusThread(Resource resource, long priority)
	{
		super(resource, null, priority, false, 0);
		setName("BusThread-" + getId());
	}

	@Override
	protected void body()
	{
		BUSResource bus = (BUSResource) resource;
		bus.process(this);
	}

	@Override
	protected void handleSignal(Signal sig, Context ctxt, ILexLocation location)
	{
		switch (sig)
		{
			case TERMINATE:
				throw new ThreadDeath();

			case SUSPEND: // Ignore
				break;

			default:
				break;
		}
	}
}
