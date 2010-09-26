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

package org.overturetool.vdmj.definitions;

import java.util.HashMap;

import org.overturetool.vdmj.debug.DBGPReader;
import org.overturetool.vdmj.expressions.UndefinedExpression;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.messages.RTLogger;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ContextException;
import org.overturetool.vdmj.runtime.StateContext;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.scheduler.ResourceScheduler;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.types.ClassType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.UndefinedType;
import org.overturetool.vdmj.types.UnresolvedType;
import org.overturetool.vdmj.values.BUSValue;
import org.overturetool.vdmj.values.CPUValue;
import org.overturetool.vdmj.values.NameValuePairList;
import org.overturetool.vdmj.values.ObjectValue;
import org.overturetool.vdmj.values.QuoteValue;
import org.overturetool.vdmj.values.RealValue;
import org.overturetool.vdmj.values.UpdatableValue;
import org.overturetool.vdmj.values.ValueList;
import org.overturetool.vdmj.values.ValueSet;

public class SystemDefinition extends ClassDefinition
{
	private static final long serialVersionUID = 1L;

	private static Context systemContext = null;
	
	/**
	 * Experimental DESTECS extension. 
	 * Allowing access at runtime to the instances variables of the system.
	 */
	private static ObjectValue system = null;
	
	public static NameValuePairList getSystemMembers()
	{
		if(system!=null)
		{
			return system.members.asList();
		}
		return null;
	}

	public SystemDefinition(LexNameToken className, DefinitionList members)
	{
		super(className, new LexNameList(), members);
	}

	@Override
	public void implicitDefinitions(Environment publicClasses)
	{
		super.implicitDefinitions(publicClasses);

		for (Definition d: definitions)
		{
			if (d instanceof InstanceVariableDefinition)
			{
				InstanceVariableDefinition iv = (InstanceVariableDefinition)d;

				if (iv.type instanceof UnresolvedType &&
					iv.expression instanceof UndefinedExpression)
				{
					UnresolvedType ut = (UnresolvedType)iv.type;

					if (ut.typename.getName().equals("BUS"))
					{
						d.warning(5014, "Uninitialized BUS ignored");
					}
				}
			}
			else if (d instanceof ExplicitOperationDefinition)
			{
				ExplicitOperationDefinition edef = (ExplicitOperationDefinition)d;

				if (!edef.name.name.equals(name.name) ||
					!edef.parameterPatterns.isEmpty())
				{
					d.report(3285, "System class can only define a default constructor");
				}
			}
			else if (d instanceof ImplicitOperationDefinition)
			{
				ImplicitOperationDefinition idef = (ImplicitOperationDefinition)d;

				if (!d.name.name.equals(name.name))
				{
					d.report(3285, "System class can only define a default constructor");
				}

				if (idef.body == null)
				{
					d.report(3283, "System class constructor cannot be implicit");
				}
			}
			else
			{
				d.report(3284, "System class can only define instance variables and a constructor");
			}
		}
	}

	public void systemInit(ResourceScheduler scheduler, DBGPReader dbgp)
	{
		systemContext = new StateContext(location, "RT system environment");
		systemContext.setThreadState(dbgp, CPUValue.vCPU);

		try
		{
			// First go through the definitions, looking for CPUs to decl
			// before we can deploy to them in the constructor. We have to
			// predict the CPU numbers at this point.

			DefinitionList cpudefs = new DefinitionList();
			int cpuNumber = 1;
			CPUClassDefinition instance = null;

			for (Definition d: definitions)
			{
				Type t = d.getType();

				if (t instanceof ClassType)
				{
					InstanceVariableDefinition ivd = (InstanceVariableDefinition)d;
					ClassType ct = (ClassType)t;

					if (ct.classdef instanceof CPUClassDefinition)
					{
						cpudefs.add(d);
						instance = (CPUClassDefinition)ct.classdef;

	    				RTLogger.log(
	    					"CPUdecl -> id: " + (cpuNumber++) +
	    					" expl: " + !(ivd.expType instanceof UndefinedType) +
	    					" sys: \"" + name.name + "\"" +
	    					" name: \"" + d.name.name + "\"");
					}
				}
			}

			// Run the constructor to do any deploys etc.

			system = makeNewInstance(null, new ValueList(),
					systemContext, new HashMap<LexNameToken, ObjectValue>());

			// Do CPUs first so that default BUSses can connect all CPUs.

			ValueSet cpus = new ValueSet();

			for (Definition d: cpudefs)
			{
    			UpdatableValue v = (UpdatableValue)system.members.get(d.name);
    			CPUValue cpu = null;

    			if (v.isUndefined())
    			{
    				ValueList args = new ValueList();

    				args.add(new QuoteValue("FCFS"));	// Default policy
    				args.add(new RealValue(0));			// Default speed

    				cpu = (CPUValue)instance.newInstance(null, args, systemContext);
    				v.set(location, cpu, systemContext);
    			}
    			else
    			{
    				cpu = (CPUValue)v.deref();
    			}

    			// Set the name and scheduler for the CPU resource, and
    			// associate the resource with the scheduler.

    			cpu.setup(scheduler, d.name.name);
    			cpus.add(cpu);
			}

			// We can create vBUS now that all the CPUs have been created
			// This must be first, to ensure it's bus number 0.

			BUSValue.vBUS = BUSClassDefinition.makeVirtualBUS(cpus);
			BUSValue.vBUS.setup(scheduler, "vBUS");

			for (Definition d: definitions)
			{
				Type t = d.getType();

				if (t instanceof ClassType)
				{
					ClassType ct = (ClassType)t;

					if (ct.classdef instanceof BUSClassDefinition)
					{
						UpdatableValue v = (UpdatableValue)system.members.get(d.name);
	    				BUSValue bus = null;

						if (!v.isUndefined())
						{
							bus = (BUSValue)v.deref();

							// Set the name and scheduler for the BUS resource, and
							// associate the resource with the scheduler.

							bus.setup(scheduler, d.name.name);
						}
					}
				}
			}

			// For efficiency, we create a 2D array of CPU-to-CPU bus links
			BUSValue.createMap(systemContext, cpus);
		}
		catch (ContextException e)
		{
			throw e;
		}
		catch (ValueException e)
		{
			throw new ContextException(e, location);
		}
    	catch (Exception e)
    	{
    		throw new ContextException(
    			4135, "Cannot instantiate a system class", location, systemContext);
    	}
	}

	@Override
	public ObjectValue newInstance(
		Definition ctorDefinition, ValueList argvals, Context ctxt)
	{
		abort(4135, "Cannot instantiate system class " + name, ctxt);
		return null;
	}
}
