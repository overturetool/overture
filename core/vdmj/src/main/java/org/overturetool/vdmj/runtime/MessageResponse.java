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

import org.overturetool.vdmj.values.CPUValue;
import org.overturetool.vdmj.values.Value;

public class MessageResponse extends MessagePacket
{
	public final long msgId;
	public final long threadId;
	public final CPUValue from;
	public final CPUValue to;
	public final Value result;
	public final ValueException exception;

	public MessageResponse(Value result, MessageRequest request)
	{
		this.msgId = nextId++;
		this.threadId = Thread.currentThread().getId();
		this.from = request.to;
		this.to = request.from;
		this.result = result;
		this.exception = null;
	}

	public MessageResponse(ValueException exception, MessageRequest request)
	{
		this.msgId = nextId++;
		this.threadId = Thread.currentThread().getId();
		this.from = request.to;
		this.to = request.from;
		this.result = null;
		this.exception = exception;
	}

	public Value getValue() throws ValueException
	{
		if (exception != null)
		{
			throw exception;
		}

		return result;
	}
}
