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

import java.util.Queue;

import org.overturetool.vdmj.values.BUSValue;
import org.overturetool.vdmj.values.CPUValue;
import org.overturetool.vdmj.values.ValueList;

public class MessageRequest extends MessagePacket
{
	public final long msgId;
	public final Thread thread;
	public final CPUValue from;
	public final CPUValue to;
	public final BUSValue bus;
	public final ValueList args;
	public final Queue<MessageResponse> replyTo;

	public MessageRequest(
		BUSValue bus, CPUValue from, CPUValue to,
		ValueList args, Queue<MessageResponse> replyTo)
	{
		this.msgId = nextId++;
		this.thread = Thread.currentThread();
		this.bus = bus;
		this.from = from;
		this.to = to;
		this.args = args;
		this.replyTo = replyTo;
	}
}
