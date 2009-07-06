/*******************************************************************************
 *
 *	Copyright (C) 2008 Fujitsu Services Ltd.
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

import java.util.Iterator;
import java.util.ListIterator;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.definitions.BUSClassDefinition;
import org.overturetool.vdmj.definitions.CPUClassDefinition;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.ExplicitOperationDefinition;
import org.overturetool.vdmj.definitions.ImplicitOperationDefinition;
import org.overturetool.vdmj.definitions.StateDefinition;
import org.overturetool.vdmj.expressions.AndExpression;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexKeywordToken;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.lex.Token;
import org.overturetool.vdmj.messages.Console;
import org.overturetool.vdmj.patterns.Pattern;
import org.overturetool.vdmj.patterns.PatternList;
import org.overturetool.vdmj.runtime.AsyncThread;
import org.overturetool.vdmj.runtime.Breakpoint;
import org.overturetool.vdmj.runtime.ClassContext;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.MessageQueue;
import org.overturetool.vdmj.runtime.MessageRequest;
import org.overturetool.vdmj.runtime.MessageResponse;
import org.overturetool.vdmj.runtime.ObjectContext;
import org.overturetool.vdmj.runtime.PatternMatchException;
import org.overturetool.vdmj.runtime.RootContext;
import org.overturetool.vdmj.runtime.StateContext;
import org.overturetool.vdmj.runtime.VDMThreadSet;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.statements.Statement;
import org.overturetool.vdmj.syntax.SystemDefinition;
import org.overturetool.vdmj.types.OperationType;
import org.overturetool.vdmj.types.PatternListTypePair;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.util.Utils;


public class OperationValue extends Value
{
	private static final long serialVersionUID = 1L;
	public final ExplicitOperationDefinition expldef;
	public final ImplicitOperationDefinition impldef;
	public final LexNameToken name;
	public final OperationType type;
	public final PatternList paramPatterns;
	public final Statement body;
	public final FunctionValue precondition;
	public final FunctionValue postcondition;
	public final StateDefinition state;
	public final ClassDefinition classdef;

	private LexNameToken stateName = null;
	private Context stateContext = null;
	private ObjectValue self = null;

	public boolean isConstructor = false;
	public boolean isStatic = false;
	public boolean isAsync = false;

	private Expression guard = null;
	private static long guardPasses = 0;
	private static final int DEADLOCK_DELAY_MS = 10;
	private static final int DEADLOCK_RETRIES = 500;

	public int hashAct = 0; // Number of activations
	public int hashFin = 0; // Number of finishes
	public int hashReq = 0; // Number of requests

	private long priority = 0;
	private boolean traceRT = true;

	public OperationValue(ExplicitOperationDefinition def,
		FunctionValue precondition, FunctionValue postcondition,
		StateDefinition state)
	{
		this.expldef = def;
		this.impldef = null;
		this.name = def.name;
		this.type = (OperationType)def.getType();
		this.paramPatterns = def.parameterPatterns;
		this.body = def.body;
		this.precondition = precondition;
		this.postcondition = postcondition;
		this.state = state;
		this.classdef = def.classDefinition;
		this.isAsync = def.accessSpecifier.isAsync;

		traceRT =
			Settings.dialect == Dialect.VDM_RT &&
			classdef != null &&
			!(classdef instanceof SystemDefinition) &&
			!classdef.name.name.equals("CPU") &&
			!classdef.name.name.equals("BUS") &&
			!name.name.equals("thread");
	}

	public OperationValue(ImplicitOperationDefinition def,
		FunctionValue precondition, FunctionValue postcondition,
		StateDefinition state)
	{
		this.impldef = def;
		this.expldef = null;
		this.name = def.name;
		this.type = (OperationType)def.getType();
		this.paramPatterns = new PatternList();

		for (PatternListTypePair ptp : def.parameterPatterns)
		{
			paramPatterns.addAll(ptp.patterns);
		}

		this.body = def.body;
		this.precondition = precondition;
		this.postcondition = postcondition;
		this.state = state;
		this.classdef = def.classDefinition;
		this.isAsync = def.accessSpecifier.isAsync;

		traceRT =
			Settings.dialect == Dialect.VDM_RT &&
			classdef != null &&
			!(classdef instanceof SystemDefinition) &&
			!classdef.name.name.equals("CPU") &&
			!classdef.name.name.equals("BUS") &&
			!name.name.equals("thread");
	}

	@Override
	public String toString()
	{
		return type.toString();
	}

	public void setSelf(ObjectValue self)
	{
		if (!isStatic)
		{
			this.self = self;
		}
	}

	public void setGuard(Expression add)
	{
		if (guard == null)
		{
			guard = add;
		}
		else
		{
			// Create "old and new" expression

			guard = new AndExpression(guard,
				new LexKeywordToken(Token.AND, guard.location), add);
		}
	}

	public Value eval(ValueList argValues, Context ctxt) throws ValueException
	{
		if (Settings.dialect == Dialect.VDM_RT)
		{
			if (ctxt.threadState.CPU != self.getCPU() || isAsync)
			{
				return asyncEval(argValues, ctxt);
			}
			else
			{
				return localEval(argValues, ctxt);
			}
		}
		else
		{
			return localEval(argValues, ctxt);
		}
	}

	public Value localEval(ValueList argValues, Context ctxt) throws ValueException
	{
		if (body == null)
		{
			abort(4066, "Cannot call implicit operation: " + name, ctxt);
		}

		if (state != null && stateName == null)
		{
			stateName = state.name;
			stateContext = state.getStateContext();
		}

		RootContext argContext = null;

		if (self != null)
		{
			argContext = new ObjectContext(name.location, name.name
				+ Utils.listToString("(", paramPatterns, ", ", ")"), ctxt,
				self);
		}
		else if (classdef != null)
		{
			argContext = new ClassContext(name.location, name.name
				+ Utils.listToString("(", paramPatterns, ", ", ")"), ctxt,
				classdef);
		}
		else
		{
			argContext = new StateContext(name.location, name.name
				+ Utils.listToString("(", paramPatterns, ", ", ")"), ctxt,
				stateContext);
		}

		req();
		notifySelf();

		if (guard != null)
		{
			synchronized (self)		// So that test and act() are atomic
			{
    			debug("TEST " + guard);
    			int retries = DEADLOCK_RETRIES;
    			long previousPasses = guardPasses;

    			while (!guard.eval(argContext).boolValue(ctxt))
    			{
    				try
    				{
    					debug("WAITING on " + guard);
						self.wait(DEADLOCK_DELAY_MS);
						debug("RESUME on " + guard);

						if (guardPasses == previousPasses &&
							!VDMThreadSet.isDebugStopped())
						{
    						if (--retries <= 0)
    						{
    							Console.out.print(VDMThreadSet.dump());
    							abort(4067, "Deadlock detected", argContext);
    						}
						}
						else
						{
							previousPasses = guardPasses;
							retries = DEADLOCK_RETRIES;
						}
    				}
    				catch (InterruptedException e)
    				{
    					debug("INTERRUPT on " + guard);
    					Breakpoint.handleInterrupt(guard.location, ctxt);
    				}
    			}

    			act();
			}
		}
		else
		{
			act();		// Still activated, even if no guard
		}

		notifySelf();

		if (argValues.size() != paramPatterns.size())
		{
			abort(4068, "Wrong number of arguments passed to " + name.name, ctxt);
		}

		ListIterator<Value> valIter = argValues.listIterator();
		Iterator<Type> typeIter = type.parameters.iterator();
		NameValuePairMap args = new NameValuePairMap();

		for (Pattern p : paramPatterns)
		{
			try
			{
				// Note args cannot be Updateable, so we deref them here
				Value pv = valIter.next().deref().convertValueTo(typeIter.next(), ctxt);

				for (NameValuePair nvp : p.getNamedValues(pv, ctxt))
				{
					Value v = args.get(nvp.name);

					if (v == null)
					{
						args.put(nvp);
					}
					else	// Names match, so values must also
					{
						if (!v.equals(nvp.value))
						{
							abort(4069,	"Parameter patterns do not match arguments", ctxt);
						}
					}
				}
			}
			catch (PatternMatchException e)
			{
				abort(e.number, e, ctxt);
			}
		}

		if (self != null)
		{
			argContext.put(name.getSelfName(), self);
		}

		// Note: arg name/values hide member values
		argContext.putAll(args);

		Value originalSigma = null;
		ObjectValue originalSelf = null;

		if (precondition != null || postcondition != null)
		{
			if (stateName != null)
			{
				Value sigma = argContext.lookup(stateName);
				originalSigma = (Value)sigma.clone();
			}

			if (self != null)
			{
				originalSelf = self.shallowCopy();	// self.deepCopy();
			}
		}

		// Make sure the #fin is updated with ErrorExceptions, using the
		// finally clause...

		Value rv = null;

		try
		{
    		if (precondition != null && Settings.prechecks)
    		{
    			ValueList preArgs = new ValueList(argValues);

    			if (stateName != null)
    			{
    				preArgs.add(originalSigma);
    			}
    			else if (self != null)
    			{
    				preArgs.add(self);
    			}

    			if (!precondition.eval(
    				precondition.location, preArgs, ctxt).boolValue(ctxt))
    			{
    				abort(4071,
    					"Precondition failure: " + precondition.name, argContext);
    			}
    		}

    		rv = body.eval(argContext);

    		if (isConstructor)
    		{
    			rv = self;
    		}
    		else
    		{
    			rv = rv.convertValueTo(type.result, argContext);
    		}

    		if (postcondition != null && Settings.postchecks)
    		{
    			ValueList postArgs = new ValueList(argValues);

    			if (!(rv instanceof VoidValue))
    			{
    				postArgs.add(rv);
    			}

    			if (stateName != null)
    			{
    				postArgs.add(originalSigma);
    				Value sigma = argContext.lookup(stateName);
    				postArgs.add(sigma);
    			}
    			else if (self != null)
    			{
    				postArgs.add(originalSelf);
    				postArgs.add(self);
    			}

    			if (!postcondition.eval(
    				postcondition.location, postArgs, ctxt).boolValue(ctxt))
    			{
    				abort(4072,
    					"Postcondition failure: " + postcondition.name, argContext);
    			}
    		}
		}
		finally
		{
    		fin();
    		notifySelf();
		}

		return rv;
	}

	private Value asyncEval(ValueList argValues, Context ctxt) throws ValueException
	{
		// Spawn a thread, send a message, wait for a reply...

		AsyncThread thread = new AsyncThread(self, this);
		thread.start();
		CPUValue from = ctxt.threadState.CPU;
		CPUValue to = self.getCPU();

		if (from != to)
		{
    		trace("OpRequest");
    		BUSValue bus = BUSClassDefinition.findBUS(from, to);
    		MessageQueue<MessageResponse> queue = new MessageQueue<MessageResponse>(from);
    		MessageRequest request = new MessageRequest(bus, from, to, argValues, queue);
    		bus.send(request, thread);
    		MessageResponse reply = queue.take(self);
    		return reply.getValue();	// Can throw a returned exception
		}
		else	// local async
		{
    		MessageRequest request = new MessageRequest(null, from, to, argValues, null);
    		thread.send(request);
    		from.yield(self);
    		return new VoidValue();
		}
	}

	@Override
	public boolean equals(Object other)
	{
		if (other instanceof Value)
		{
			Value val = ((Value)other).deref();

			if (val instanceof OperationValue)
			{
				OperationValue ov = (OperationValue)val;
				return ov.type.equals(type);
			}
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		return type.hashCode();
	}

	@Override
	public String kind()
	{
		return "operation";
	}

	@Override
	public Value convertValueTo(Type to, Context ctxt) throws ValueException
	{
		if (to.isType(OperationType.class))
		{
			return this;
		}
		else
		{
			return super.convertValueTo(to, ctxt);
		}
	}

	@Override
	public OperationValue operationValue(Context ctxt) throws ValueException
	{
		return this;
	}

	@Override
	public Object clone()
	{
		if (expldef != null)
		{
			return new OperationValue(expldef, precondition, postcondition,
				state);
		}
		else
		{
			return new OperationValue(impldef, precondition, postcondition,
				state);
		}
	}

	private synchronized void req()
	{
		hashReq++;
		guardPasses++;
		trace("OpRequest");
	}

	private synchronized void act()
	{
		hashAct++;
		guardPasses++;
		trace("OpActivate");
	}

	private synchronized void fin()
	{
		hashFin++;
		guardPasses++;
		trace("OpComplete");
	}

	private void notifySelf()
	{
		if (self != null)
		{
			synchronized (self)
			{
				self.notifyAll();
			}
		}
	}

	private void trace(String kind)
	{
		if (traceRT)
		{
    		Console.out.println(
    			kind + " -> id: " + Thread.currentThread().getId() +
    			" opname: \"" + name + "\"" +
    			" objref: " + self.objectReference +
    			" clnm: \"" + self.type.name.name + "\"" +
    			" cpunm: " + self.getCPU().cpuNumber +
    			" async: " + isAsync +
    			" time: " + VDMThreadSet.getWallTime()
    			);
		}
	}

	private void debug(@SuppressWarnings("unused") String string)
	{
		// Put useful diags here, like print hashReq, hashAct, hashFin...
	}

	public synchronized void setPriority(long priority)
	{
		this.priority = priority;
	}

	public synchronized long getPriority()
	{
		return priority;
	}

	public synchronized CPUValue getCPU()
	{
		return self == null ? CPUClassDefinition.virtualCPU : self.getCPU();
	}
}
