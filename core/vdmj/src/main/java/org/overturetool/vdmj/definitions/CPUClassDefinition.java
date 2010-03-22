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
import org.overturetool.vdmj.runtime.ObjectContext;
import org.overturetool.vdmj.syntax.DefinitionReader;
import org.overturetool.vdmj.syntax.ParserException;
import org.overturetool.vdmj.types.ClassType;
import org.overturetool.vdmj.values.CPUValue;
import org.overturetool.vdmj.values.NameValuePairList;
import org.overturetool.vdmj.values.NameValuePairMap;
import org.overturetool.vdmj.values.NaturalValue;
import org.overturetool.vdmj.values.ObjectValue;
import org.overturetool.vdmj.values.SeqValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;
import org.overturetool.vdmj.values.VoidValue;

public class CPUClassDefinition extends ClassDefinition
{
	private static final long serialVersionUID = 1L;

	public CPUClassDefinition() throws ParserException, LexException
	{
		super(
			new LexNameToken("CLASS", "CPU", new LexLocation()),
			new LexNameList(),
			operationDefs());
	}

	private static String defs =
		"operations " +
		"public CPU:(<FP>|<FCFS>) * real ==> CPU " +
		"	CPU(policy, speed) == is not yet specified; " +
		"public deploy: ? ==> () " +
		"	deploy(obj) == is not yet specified; " +
		"public deploy: ? * seq of char ==> () " +
		"	deploy(obj, name) == is not yet specified; " +
		"public setPriority: ? * nat ==> () " +
		"	setPriority(opname, priority) == is not yet specified;";

	private static DefinitionList operationDefs()
		throws ParserException, LexException
	{
		LexTokenReader ltr = new LexTokenReader(defs, Dialect.VDM_PP);
		DefinitionReader dr = new DefinitionReader(ltr);
		dr.setCurrentModule("CPU");
		return dr.readDefinitions();
	}

	@Override
	public ObjectValue newInstance(
		Definition ctorDefinition, ValueList argvals, Context ctxt)
	{
		NameValuePairList nvpl = definitions.getNamedValues(ctxt);
		NameValuePairMap map = new NameValuePairMap();
		map.putAll(nvpl);

		return new CPUValue((ClassType)getType(), map, argvals);
	}

	public static Value deploy(Context ctxt)
	{
		try
		{
    		ObjectContext octxt = (ObjectContext)ctxt;
    		CPUValue cpu = (CPUValue)octxt.self;
    		ObjectValue obj = (ObjectValue)octxt.lookup(varName("obj"));

    		obj.setCPU(cpu);
    		cpu.deploy(obj);

  			return new VoidValue();
		}
		catch (Exception e)
		{
			throw new ContextException(4136, "Cannot deploy to CPU", ctxt.location, ctxt);
		}
	}

	public static Value setPriority(Context ctxt)
	{
		try
		{
    		ObjectContext octxt = (ObjectContext)ctxt;
    		CPUValue cpu = (CPUValue)octxt.self;
    		SeqValue opname = (SeqValue)octxt.lookup(varName("opname"));
    		NaturalValue priority = (NaturalValue)octxt.check(varName("priority"));

    		cpu.setPriority(opname.stringValue(ctxt), priority.intValue(ctxt));
   			return new VoidValue();
		}
		catch (Exception e)
		{
			throw new ContextException(
				4137, "Cannot set priority: " + e.getMessage(), ctxt.location, ctxt);
		}
	}

	private static LexNameToken varName(String name)
	{
		return new LexNameToken("CPU", name, new LexLocation());
	}
}
