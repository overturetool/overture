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

package org.overturetool.vdmj.runtime;

public class FPPolicy extends SchedulingPolicy
{
	public FPPolicy()
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void addThread(Thread thread)
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public Thread getThread()
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void removeThread(Thread thread)
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public boolean reschedule()
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void setState(Thread thread, RunState newstate)
	{
		throw new RuntimeException("Not implemented");
	}
}
