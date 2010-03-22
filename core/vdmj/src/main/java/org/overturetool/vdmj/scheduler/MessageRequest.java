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

package org.overturetool.vdmj.scheduler;

import org.overturetool.vdmj.debug.DBGPReader;
import org.overturetool.vdmj.values.BUSValue;
import org.overturetool.vdmj.values.CPUValue;
import org.overturetool.vdmj.values.ObjectValue;
import org.overturetool.vdmj.values.OperationValue;
import org.overturetool.vdmj.values.ValueList;

public class MessageRequest extends MessagePacket
{
	private static final long serialVersionUID = 1L;
	public final DBGPReader dbgp;
	public final boolean breakAtStart;
	public final ValueList args;
	public final Holder<MessageResponse> replyTo;

	public MessageRequest(
		DBGPReader dbgp, BUSValue bus, CPUValue from, CPUValue to,
		ObjectValue target,	OperationValue operation,
		ValueList args, Holder<MessageResponse> replyTo, boolean breakAtStart)
	{
		super(bus, from, to, target, operation);

		this.dbgp = dbgp;
		this.breakAtStart = breakAtStart;
		this.args = args;
		this.replyTo = replyTo;
	}

	public int getSize()
	{
		return args.toString().length();
	}
}
