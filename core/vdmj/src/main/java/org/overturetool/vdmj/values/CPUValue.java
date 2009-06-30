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

package org.overturetool.vdmj.values;

import java.util.List;
import java.util.Vector;

import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.messages.Console;
import org.overturetool.vdmj.runtime.CPUPolicy;
import org.overturetool.vdmj.types.ClassType;
import org.overturetool.vdmj.types.Type;

public class CPUValue extends ObjectValue
{
	private static final long serialVersionUID = 1L;

	public final int cpuNumber;
	public final CPUPolicy policy;
	public final double cyclesPerSec;
	public final List<ObjectValue> deployed;

	public CPUValue(
		Type classtype, NameValuePairMap map, ValueList argvals, int cpuNumber)
	{
		super((ClassType)classtype, map, new Vector<ObjectValue>());

		this.cpuNumber = cpuNumber;

		QuoteValue parg = (QuoteValue)argvals.get(0);
		this.policy = CPUPolicy.valueOf(parg.value.toUpperCase());

		RealValue sarg = (RealValue)argvals.get(1);
		this.cyclesPerSec = sarg.value;

		deployed = new Vector<ObjectValue>();

		if (cpuNumber > 0)
		{
			Console.out.println(
				"CPUdecl -> id: " + cpuNumber +
				" expl: true sys: \"" + ClassList.systemClass.name.name + "\"" +
				" name : \"?\"");
		}
	}

	public void addDeployed(ObjectValue obj)
	{
		deployed.add(obj);
	}

	public boolean setPriority(String opname, long priority)
	{
		boolean found = false;

		for (ObjectValue obj: deployed)
		{
			for (LexNameToken name: obj.members.keySet())
			{
				if (name.getExplicit(true).getName().equals(opname))
				{
					OperationValue op = (OperationValue)obj.members.get(name);
					op.setPriority(priority);
					found = true;
				}
			}
		}

		return found;
	}

	public long getDuration(long cycles)
	{
		return (long)(cycles/cyclesPerSec * 1000);		// millisecs
	}
}
