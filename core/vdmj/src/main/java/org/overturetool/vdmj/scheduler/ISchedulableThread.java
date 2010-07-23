/*******************************************************************************
 *
 *	Overture.
 *
 *	Author: Kenneth Lausdahl
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

package org.overturetool.vdmj.scheduler;

import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.values.ObjectValue;

public interface ISchedulableThread
{

	public abstract boolean equals(Object other);

	public abstract int hashCode();

	public abstract String toString();

	public abstract void start();

	public abstract void run();

	public abstract void step(Context ctxt, LexLocation location);

	public abstract RunState getRunState();

	public abstract void setState(RunState newstate);

	public abstract void waiting(Context ctxt, LexLocation location);

	public abstract void locking(Context ctxt, LexLocation location);

	public abstract void runslice(long slice);

	public abstract void duration(long pause, Context ctxt, LexLocation location);

	public abstract void suspendOthers();

	public abstract ObjectValue getObject();

	public abstract void setSwapInBy(long swapInBy);

	public abstract long getSwapInBy();

	public abstract boolean isPeriodic();

	public abstract boolean isActive();

	public abstract boolean isVirtual();

	public abstract void setTimestep(long step);

	public abstract long getTimestep();

	public abstract long getDurationEnd();

	public abstract void inOuterTimestep(boolean b);

	public abstract boolean inOuterTimestep();

	public abstract CPUResource getCPUResource();

	public abstract long getId();
	
	public abstract String getName();
	
	public abstract void setName(String name);
	
	public abstract boolean isAlive();
	
	void setSignal(Signal sig);
	
	public abstract Thread getThread();

}