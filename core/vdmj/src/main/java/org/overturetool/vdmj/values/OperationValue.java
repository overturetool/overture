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
import org.overturetool.vdmj.definitions.SystemDefinition;
import org.overturetool.vdmj.expressions.AndExpression;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexKeywordToken;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.lex.Token;
import org.overturetool.vdmj.messages.RTLogger;
import org.overturetool.vdmj.patterns.Pattern;
import org.overturetool.vdmj.patterns.PatternList;
import org.overturetool.vdmj.runtime.AsyncThread;
import org.overturetool.vdmj.runtime.Breakpoint;
import org.overturetool.vdmj.runtime.CPUThread;
import org.overturetool.vdmj.runtime.ClassContext;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.Holder;
import org.overturetool.vdmj.runtime.MessageRequest;
import org.overturetool.vdmj.runtime.MessageResponse;
import org.overturetool.vdmj.runtime.ObjectContext;
import org.overturetool.vdmj.runtime.PatternMatchException;
import org.overturetool.vdmj.runtime.RootContext;
import org.overturetool.vdmj.runtime.RunState;
import org.overturetool.vdmj.runtime.StateContext;
import org.overturetool.vdmj.runtime.SystemClock;
import org.overturetool.vdmj.runtime.VDMThreadSet;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.statements.Statement;
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
	private static final int DEADLOCK_TIMEOUT_MS = 10000;

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
			!name.name.equals("thread") &&
			!name.name.startsWith("inv_");
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

	public ObjectValue getSelf()
	{
		return self;
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

	public void prepareGuard(ObjectContext ctxt)
	{
		if (guard != null)
		{
			ValueListener vl = new GuardValueListener<ObjectValue>(self);

			for (Value v: guard.getValues(ctxt))
			{
				UpdatableValue uv = (UpdatableValue)v;
				uv.addListener(vl);
			}
		}
	}

	public Value eval(LexLocation from, ValueList argValues, Context ctxt)
		throws ValueException
	{
		VDMThreadSet.stopIfDebugged(from, ctxt);

		if (Settings.dialect == Dialect.VDM_RT)
		{
			if (!isStatic && (ctxt.threadState.CPU != self.getCPU() || isAsync))
			{
				return asyncEval(argValues, ctxt);
			}
			else
			{
				return localEval(from, argValues, ctxt, true);
			}
		}
		else
		{
			return localEval(from, argValues, ctxt, true);
		}
	}

	public Value localEval(
		LexLocation from, ValueList argValues, Context ctxt, boolean logreq)
		throws ValueException
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
			argContext = new ObjectContext(from, toTitle(), ctxt, self);
		}
		else if (classdef != null)
		{
			argContext = new ClassContext(from, toTitle(), ctxt, classdef);
		}
		else
		{
			argContext = new StateContext(from, toTitle(), ctxt, stateContext);
		}

		req(logreq);
		notifySelf();

		if (guard != null)
		{
			if (Settings.dialect == Dialect.VDM_RT)
			{
				guardRT(argContext);
			}
			else
			{
				guardPP(argContext);
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

	private void guardPP(Context ctxt) throws ValueException
	{
		synchronized (self)		// So that test and act() are atomic
		{
			debug("TEST " + guard);
			long expires = System.currentTimeMillis() + DEADLOCK_TIMEOUT_MS;

			while (!guard.eval(ctxt).boolValue(ctxt))
			{
				try
				{
					debug("WAITING on " + guard);
					self.wait(DEADLOCK_TIMEOUT_MS);
					debug("RESUME on " + guard);

					long now = System.currentTimeMillis();

					if (!VDMThreadSet.isDebugStopped() && now >= expires)
					{
						abort(4067, "Deadlock detected", ctxt);
					}
					else
					{
						expires = now + DEADLOCK_TIMEOUT_MS;
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

	private void guardRT(Context ctxt) throws ValueException
	{
		// RT deadlocks are detected by time rather than a wait(t) for
		// a certain number of retries. This is because other threads
		// on the same CPU have to be allowed to run immediately; we
		// can't pause the whole CPU with a wait(t) and starve the others.

		long expires = System.currentTimeMillis() + DEADLOCK_TIMEOUT_MS;
		CPUValue cpu = self.getCPU();
		CPUThread me = new CPUThread(cpu);

		while (true)
		{
			synchronized (self)		// So that test and act() are atomic
			{
				// For the purpose of doing durations, guard tests
				// are not really swapped-in threads. So we cheat and
				// mark the CPU as not-running at this point, which
				// is cleared again when (below) we pass the guard test.

				SystemClock.cpuRunning(self.getCPU().cpuNumber, false);

				// We have to suspend thread swapping round the guard,
				// else we will reschedule another CPU thread while
				// having self locked, and that locks up everything!

				ctxt.threadState.setAtomic(true);
    			boolean ok = guard.eval(ctxt).boolValue(ctxt);
    			ctxt.threadState.setAtomic(false);

    			if (ok)
    			{
    				act();
    				SystemClock.cpuRunning(self.getCPU().cpuNumber, true);
    				break;	// Out of while loop
    			}
			}

			// The waiters list is processed by the GuardValueListener
			// and by notifySelf.

			synchronized (self.guardWaiters)
			{
				self.guardWaiters.add(me);
			}

			cpu.yield(RunState.WAITING);

			synchronized (self.guardWaiters)
			{
				self.guardWaiters.remove(me);
			}

			long now = System.currentTimeMillis();

			if (!VDMThreadSet.isDebugStopped() && now > expires)
			{
				abort(4067, "Deadlock detected", ctxt);
			}
			else
			{
				expires = now + DEADLOCK_TIMEOUT_MS;
			}
		}
	}

	private Value asyncEval(ValueList argValues, Context ctxt) throws ValueException
	{
		// Spawn a thread, send a message, wait for a reply...

		CPUValue from = ctxt.threadState.CPU;
		CPUValue to = self.getCPU();
		boolean stepping = ctxt.threadState.isStepping();

		// Async calls have the OpRequest made by the caller using the
		// "from" CPU, whereas the OpActivate and OpComplete are made
		// by the called object, using self's CPU (see trace(msg)).

		RTLogger.log(
			"OpRequest -> id: " + Thread.currentThread().getId() +
			" opname: \"" + name + "\"" +
			" objref: " + self.objectReference +
			" clnm: \"" + self.type.name.name + "\"" +
			" cpunm: " + from.cpuNumber +
			" async: " + isAsync
			);

		if (from != to)		// Remote CPU call
		{
    		BUSValue bus = BUSClassDefinition.findBUS(from, to);

    		if (bus == null)
    		{
    			abort(4140,
    				"No BUS between CPUs " + from.name + " and " + to.name, ctxt);
    		}

    		if (isAsync)	// Don't wait
    		{
        		MessageRequest request = new MessageRequest(
        			bus, from, to, self, this, argValues, null, stepping);

        		bus.transmit(request);
        		return new VoidValue();
    		}
    		else
    		{
        		Holder<MessageResponse> result = new Holder<MessageResponse>();
        		MessageRequest request = new MessageRequest(
        			bus, from, to, self, this, argValues, result, stepping);

        		bus.transmit(request);
        		MessageResponse reply = result.get(from);
        		return reply.getValue();	// Can throw a returned exception
    		}
		}
		else	// local, must be async so don't wait
		{
    		MessageRequest request = new MessageRequest(
    			null, from, to, self, this, argValues, null, stepping);

    		new AsyncThread(request).start();
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
	public OperationValue operationValue(Context ctxt)
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

	private synchronized void req(boolean logreq)
	{
		hashReq++;

		if (logreq)		// Async OpRequests are made in asyncEval
		{
			trace("OpRequest");
		}
	}

	private synchronized void act()
	{
		hashAct++;

		if (!AsyncThread.stopping())	// else makes a mess of shutdown trace
		{
			trace("OpActivate");
		}
	}

	private synchronized void fin()
	{
		hashFin++;

		if (!AsyncThread.stopping())	// else makes a mess of shutdown trace
		{
			trace("OpCompleted");
		}
	}

	private void notifySelf()
	{
		if (self != null)
		{
			if (Settings.dialect == Dialect.VDM_RT)
			{
				synchronized (self.guardWaiters)
				{
    				for (CPUThread th: self.guardWaiters)
    				{
    					th.setState(RunState.RUNNABLE);
    				}
				}
			}
			else
			{
    			synchronized (self)
    			{
    				self.notifyAll();
    			}
			}
		}
	}

	private void trace(String kind)
	{
		if (traceRT)
		{
			if (isStatic)
			{
	    		RTLogger.log(
	    			kind + " -> id: " + Thread.currentThread().getId() +
	    			" opname: \"" + name + "\"" +
	    			" objref: nil" +
	    			" clnm: \"" + classdef.name.name + "\"" +
	    			" cpunm: 0" +
	    			" async: " + isAsync
	    			);
			}
			else
			{
        		RTLogger.log(
        			kind + " -> id: " + Thread.currentThread().getId() +
        			" opname: \"" + name + "\"" +
        			" objref: " + self.objectReference +
        			" clnm: \"" + self.type.name.name + "\"" +
        			" cpunm: " + self.getCPU().cpuNumber +
        			" async: " + isAsync
        			);
			}
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

	public String toTitle()
	{
		return name.name + Utils.listToString("(", paramPatterns, ", ", ")");
	}
}
