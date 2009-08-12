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

import org.overturetool.vdmj.values.BUSValue;
import org.overturetool.vdmj.values.CPUValue;
import org.overturetool.vdmj.values.ObjectValue;
import org.overturetool.vdmj.values.OperationValue;

public abstract class MessagePacket
{
	protected static long nextId = 1;

	public final long msgId;
	public final BUSValue bus;
	public final Thread thread;
	public final CPUValue from;
	public final CPUValue to;
	public final ObjectValue target;
	public final OperationValue operation;

	public MessagePacket(
		BUSValue bus, CPUValue from, CPUValue to, ObjectValue target, OperationValue operation)
	{
		this.msgId = nextId++;
		this.thread = Thread.currentThread();
		this.bus = bus;
		this.from = from;
		this.to = to;
		this.target = target;
		this.operation = operation;
	}

	public MessagePacket()
	{
		this.msgId = 0;
		this.thread = null;
		this.bus = null;
		this.from = null;
		this.to = null;
		this.target = null;
		this.operation = null;
	}
}
