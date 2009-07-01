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

import java.util.Vector;

import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.runtime.BUSPolicy;
import org.overturetool.vdmj.types.ClassType;
import org.overturetool.vdmj.types.Type;

public class BUSValue extends ObjectValue
{
	private static final long serialVersionUID = 1L;
	private static int nextBUS = 1;

	public final int busNumber;
	public final BUSPolicy policy;
	public final double speed;
	public final ValueSet cpus;

	public String name;

	public BUSValue(Type classtype, NameValuePairMap map, ValueList argvals)
	{
		super((ClassType)classtype, map, new Vector<ObjectValue>());

		this.busNumber = nextBUS++;

		QuoteValue parg = (QuoteValue)argvals.get(0);
		this.policy = BUSPolicy.valueOf(parg.value.toUpperCase());

		RealValue sarg = (RealValue)argvals.get(1);
		this.speed = sarg.value;

		SetValue set = (SetValue)argvals.get(2);
		cpus = set.values;
	}

	public BUSValue(
		int number, Type classtype, NameValuePairMap map, ValueList argvals)
	{
		super((ClassType)classtype, map, new Vector<ObjectValue>());

		this.busNumber = number;

		QuoteValue parg = (QuoteValue)argvals.get(0);
		this.policy = BUSPolicy.valueOf(parg.value.toUpperCase());

		RealValue sarg = (RealValue)argvals.get(1);
		this.speed = sarg.value;

		SetValue set = (SetValue)argvals.get(2);
		cpus = set.values;
	}

	public void setName(LexNameToken m)
	{
		this.name = m.name;
	}

	public String declString() throws Exception
	{
		ValueSet set = new ValueSet();

		for (Value v: cpus)
		{
			CPUValue cpu = (CPUValue)v.deref();
			set.add(new NaturalValue(cpu.cpuNumber));
		}

		return
			"BUSdecl -> id: " + busNumber +
			" topo: " + set +
			" name: \"" + name + "\"";
	}
}
