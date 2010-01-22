/*******************************************************************************
 *
 *	Copyright (C) 2008, 2009 Fujitsu Services Ltd.
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

package org.overturetool.vdmj.definitions;

import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ContextException;
import org.overturetool.vdmj.syntax.DefinitionReader;
import org.overturetool.vdmj.syntax.ParserException;
import org.overturetool.vdmj.values.BUSValue;
import org.overturetool.vdmj.values.CPUValue;
import org.overturetool.vdmj.values.NameValuePairList;
import org.overturetool.vdmj.values.NameValuePairMap;
import org.overturetool.vdmj.values.ObjectValue;
import org.overturetool.vdmj.values.QuoteValue;
import org.overturetool.vdmj.values.RealValue;
import org.overturetool.vdmj.values.SetValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;
import org.overturetool.vdmj.values.ValueSet;

public class BUSClassDefinition extends ClassDefinition
{
	private static final long serialVersionUID = 1L;
	private static BUSClassDefinition instance = null;

	public static BUSValue virtualBUS = null;
	private static BUSValue[][] cpumap = null;

	public static void init()
	{
		BUSValue.init();
	}

	public BUSClassDefinition() throws ParserException, LexException
	{
		super(
			new LexNameToken("CLASS", "BUS", new LexLocation()),
			new LexNameList(),
			operationDefs());

		instance = this;
	}

	private static String defs =
		"operations " +
		"public BUS:(<FCFS>|<CSMACD>) * real * set of CPU ==> BUS " +
		"	BUS(policy, speed, cpus) == is not yet specified;";

	private static DefinitionList operationDefs()
		throws ParserException, LexException
	{
		LexTokenReader ltr = new LexTokenReader(defs, Dialect.VDM_PP);
		DefinitionReader dr = new DefinitionReader(ltr);
		dr.setCurrentModule("BUS");
		return dr.readDefinitions();
	}

	@Override
	public ObjectValue newInstance(
		Definition ctorDefinition, ValueList argvals, Context ctxt)
	{
		NameValuePairList nvpl = definitions.getNamedValues(ctxt);
		NameValuePairMap map = new NameValuePairMap();
		map.putAll(nvpl);

		return new BUSValue(classtype, map, argvals);
	}

	public static BUSValue newBUS()
	{
		ValueList args = new ValueList();

		args.add(new QuoteValue("FCFS"));
		args.add(new RealValue(0));
		ValueSet cpus = new ValueSet();
		cpus.addAll(CPUValue.allCPUs);
		args.add(new SetValue(cpus));

		return new BUSValue(instance.getType(), new NameValuePairMap(), args);
	}

	public static BUSValue newDefaultBUS()
	{
		ValueList args = new ValueList();

		args.add(new QuoteValue("FCFS"));
		args.add(new RealValue(0));
		ValueSet cpus = new ValueSet();
		cpus.addAll(CPUValue.allCPUs);
		args.add(new SetValue(cpus));

		BUSValue bv = new BUSValue(0, instance.getType(), new NameValuePairMap(), args);
		bv.setName("BUS:0");
		return bv;
	}

	public static BUSValue findBUS(CPUValue cpu1, CPUValue cpu2)
	{
		return cpumap[cpu1.cpuNumber][cpu2.cpuNumber];
	}

	public static void createMap(Context ctxt)
	{
		int max = CPUValue.allCPUs.size();
		cpumap = new BUSValue[max][max];

		for (int i=0; i<max; i++)
		{
			cpumap[i][0] = virtualBUS;
			cpumap[0][i] = virtualBUS;
		}

		for (BUSValue bus: BUSValue.allBUSSES)
		{
			if (bus == virtualBUS)
			{
				continue;
			}

			for (Value v1: bus.cpus)
			{
				CPUValue cpu1 = (CPUValue)v1.deref();
				int n1 = cpu1.cpuNumber;

				for (Value v2: bus.cpus)
				{
					CPUValue cpu2 = (CPUValue)v2.deref();
					int n2 = cpu2.cpuNumber;

					if (n1 == n2)
					{
						continue;
					}
					else if (cpumap[n1][n2] == null)
					{
						cpumap[n1][n2] = bus;
					}
					else if (cpumap[n1][n2] != bus)
					{
						throw new ContextException(4139,
							"CPUs " + cpu1.name + " and " + cpu2.name +
							" connected by " +
							bus.name + " and " + cpumap[n1][n2].name,
							ctxt.location, ctxt);
					}
				}
			}
 		}
	}
}
