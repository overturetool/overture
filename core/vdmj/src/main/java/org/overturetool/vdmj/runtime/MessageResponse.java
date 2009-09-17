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

import org.overturetool.vdmj.values.Value;

public class MessageResponse extends MessagePacket
{
	public final Value result;
	public final ValueException exception;
	public final Holder<MessageResponse> replyTo;
	public final Thread caller;
	public final long originalId;

	public MessageResponse(Value result, MessageRequest request)
	{
		super(request.bus, request.to, request.from,	// NB to->from
			request.target, request.operation);

		this.result = result;
		this.exception = null;
		this.replyTo = request.replyTo;
		this.caller = request.thread;
		this.originalId = request.msgId;
	}

	public MessageResponse(ValueException exception, MessageRequest request)
	{
		super(request.bus, request.to, request.from,	// NB to->from
			request.target, request.operation);

		this.result = null;
		this.exception = exception;
		this.replyTo = request.replyTo;
		this.caller = request.thread;
		this.originalId = request.msgId;
	}

	public Value getValue() throws ValueException
	{
		if (exception != null)
		{
			throw exception;
		}

		return result;
	}

	@Override
	public String toString()
	{
		return result == null ? exception.getMessage() : result.toString();
	}

	public int getSize()
	{
		return result == null ?
			exception.toString().length() : result.toString().length();
	}
}
