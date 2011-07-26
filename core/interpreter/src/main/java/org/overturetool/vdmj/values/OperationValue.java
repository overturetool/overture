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
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import org.overture.interpreter.ast.definitions.AExplicitOperationDefinitionInterpreter;
import org.overture.interpreter.ast.definitions.AImplicitOperationDefinitionInterpreter;
import org.overture.interpreter.ast.definitions.AStateDefinitionInterpreter;
import org.overture.interpreter.ast.definitions.ASystemClassDefinitionInterpreter;
import org.overture.interpreter.ast.definitions.SClassDefinitionInterpreter;
import org.overture.interpreter.ast.expressions.PExpInterpreter;
import org.overture.interpreter.ast.patterns.APatternListTypePairInterpreter;
import org.overture.interpreter.ast.patterns.PPatternInterpreter;
import org.overture.interpreter.ast.statements.PStmInterpreter;
import org.overture.interpreter.ast.types.AAccessSpecifierAccessSpecifierInterpreter;
import org.overture.interpreter.ast.types.AOperationTypeInterpreter;
import org.overture.interpreter.ast.types.PTypeInterpreter;
import org.overturetool.interpreter.vdmj.lex.LexKeywordToken;
import org.overturetool.interpreter.vdmj.lex.LexLocation;
import org.overturetool.interpreter.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.config.Properties;

import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.VDMToken;
import org.overturetool.vdmj.messages.rtlog.RTExtendedTextMessage;
import org.overturetool.vdmj.messages.rtlog.RTLogger;
import org.overturetool.vdmj.messages.rtlog.RTMessage.MessageType;
import org.overturetool.vdmj.messages.rtlog.RTOperationMessage;


import org.overturetool.vdmj.runtime.ClassContext;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ObjectContext;
import org.overturetool.vdmj.runtime.PatternMatchException;
import org.overturetool.vdmj.runtime.RootContext;
import org.overturetool.vdmj.runtime.RuntimeValidator;
import org.overturetool.vdmj.runtime.StateContext;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.scheduler.AsyncThread;
import org.overturetool.vdmj.scheduler.BasicSchedulableThread;
import org.overturetool.vdmj.scheduler.CPUResource;
import org.overturetool.vdmj.scheduler.Holder;
import org.overturetool.vdmj.scheduler.ISchedulableThread;
import org.overturetool.vdmj.scheduler.InitThread;
import org.overturetool.vdmj.scheduler.MessageRequest;
import org.overturetool.vdmj.scheduler.MessageResponse;
import org.overturetool.vdmj.scheduler.ResourceScheduler;

import org.overturetool.vdmj.util.Utils;


public class OperationValue extends Value
{
	private static final long serialVersionUID = 1L;
	public final AExplicitOperationDefinitionInterpreter expldef;
	public final AImplicitOperationDefinitionInterpreter impldef;
	public final LexNameToken name;
	public final AOperationTypeInterpreter type;
	public final List<PPatternInterpreter> paramPatterns;
	public final PStmInterpreter body;
	public final FunctionValue precondition;
	public final FunctionValue postcondition;
	public final AStateDefinitionInterpreter state;
	public final SClassDefinitionInterpreter classdef;

	private LexNameToken stateName = null;
	private Context stateContext = null;
	private ObjectValue self = null;

	public boolean isConstructor = false;
	public boolean isStatic = false;
	public boolean isAsync = false;

	private PExpInterpreter guard = null;

	public int hashAct = 0; // Number of activations
	public int hashFin = 0; // Number of finishes
	public int hashReq = 0; // Number of requests

	private long priority = 0;
	private boolean traceRT = true;

	public OperationValue(AExplicitOperationDefinitionInterpreter def,
		FunctionValue precondition, FunctionValue postcondition,
		AStateDefinitionInterpreter state)
	{
		this.expldef = def;
		this.impldef = null;
		this.name = def.getName();
		this.type = def.getType();
		this.paramPatterns = def.getParameterPatterns();
		this.body = def.getBody();
		this.precondition = precondition;
		this.postcondition = postcondition;
		this.state = state;
		this.classdef = def.getClassDefinition();
		this.isAsync = ((AAccessSpecifierAccessSpecifierInterpreter)def.getAccess()).getAsync()!=null;//TODO can we change this

		traceRT =
			Settings.dialect == Dialect.VDM_RT &&
			classdef != null &&
			!(classdef instanceof ASystemClassDefinitionInterpreter) &&
			!classdef.getName().name.equals("CPU") &&
			!classdef.getName().name.equals("BUS") &&
			!name.name.equals("thread") &&
			!name.name.startsWith("inv_");
	}

	public OperationValue(AImplicitOperationDefinitionInterpreter def,
		FunctionValue precondition, FunctionValue postcondition,
		AStateDefinitionInterpreter state)
	{
		this.impldef = def;
		this.expldef = null;
		this.name = def.getName();
		this.type = def.getType();
		this.paramPatterns = new Vector<PPatternInterpreter>();

		for (APatternListTypePairInterpreter ptp : def.getParameterPatterns())
		{
			paramPatterns.addAll(ptp.getPatterns());
		}

		this.body = def.getBody();
		this.precondition = precondition;
		this.postcondition = postcondition;
		this.state = state;
		this.classdef = def.getClassDefinition();
		this.isAsync = ((AAccessSpecifierAccessSpecifierInterpreter)def.getAccess()).getAsync()!=null;//TODO can we change this

		traceRT =
			Settings.dialect == Dialect.VDM_RT &&
			classdef != null &&
			!(classdef instanceof ASystemClassDefinitionInterpreter) &&
			!classdef.getName().name.equals("CPU") &&
			!classdef.getName().name.equals("BUS") &&
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

	public void setGuard(PExpInterpreter add, boolean isMutex)
	{
		if (guard == null)
		{
			guard = add;
		}
		else
		{
			// Create "old and new" expression
			
			LexLocation where = isMutex ? guard.getLocation() : add.getLocation();

			guard = new AndExpression(guard,
				new LexKeywordToken(VDMToken.AND, where), add);
		}
	}

	public void prepareGuard(ObjectContext ctxt)
	{
		if (guard != null)
		{
			ValueListener vl = new GuardValueListener(self);

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
			stateName = state.getName();
			stateContext = state.getStateContext();
		}

		RootContext argContext = newContext(from, toTitle(), ctxt);

		req(logreq);
		notifySelf();

		if (guard != null)
		{
			guard(argContext);
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
		Iterator<PTypeInterpreter> typeIter = type.getParameters().iterator();
		NameValuePairMap args = new NameValuePairMap();

		for (PPatternInterpreter p : paramPatterns)
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

		if (postcondition != null)
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
    				preArgs.add(argContext.lookup(stateName));
    			}
    			else if (self != null)
    			{
    				preArgs.add(self);
    			}

    			ctxt.setPrepost(4071, "Precondition failure: ");
    			precondition.eval(from, preArgs, ctxt);
    			ctxt.setPrepost(0, null);
    		}

    		rv = body.eval(argContext);

    		if (isConstructor)
    		{
    			rv = self;
    		}
    		else
    		{
    			rv = rv.convertValueTo(type.getResult(), argContext);
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

    			ctxt.setPrepost(4072, "Postcondition failure: ");
    			postcondition.eval(from, postArgs, ctxt);
    			ctxt.setPrepost(0, null);
    		}
		}
		finally
		{
    		fin();
    		notifySelf();
		}

		return rv;
	}

	private RootContext newContext(LexLocation from, String title, Context ctxt)
	{
		RootContext argContext;

		if (self != null)
		{
			argContext = new ObjectContext(from, title, ctxt, self);
		}
		else if (classdef != null)
		{
			argContext = new ClassContext(from, title, ctxt, classdef);
		}
		else
		{
			argContext = new StateContext(from, title, ctxt, stateContext);
		}

		return argContext;
	}

	private void guard(Context ctxt) throws ValueException
	{
		ISchedulableThread th = BasicSchedulableThread.getThread(Thread.currentThread());
		if (th == null || th instanceof InitThread )
		{
			return;		// Probably during initialization.
		}

		self.guardLock.lock(ctxt, guard.getLocation());

		while (true)
		{
			synchronized (self)		// So that test and act() are atomic
			{
				// We have to suspend thread swapping round the guard,
				// else we will reschedule another CPU thread while
				// having self locked, and that locks up everything!

				debug("guard TEST");
				ctxt.threadState.setAtomic(true);
    			boolean ok = guard.eval(ctxt).boolValue(ctxt);
    			ctxt.threadState.setAtomic(false);

    			if (ok)
    			{
    				debug("guard OK");
    				act();
    				break;	// Out of while loop
    			}
			}

			// The guardLock list is signalled by the GuardValueListener
			// and by notifySelf when something changes. The guardOp
			// is set to indicate the guard state to any breakpoints.

			debug("guard WAIT");
			ctxt.guardOp = this;
			self.guardLock.block(ctxt, guard.getLocation());
			ctxt.guardOp = null;
			debug("guard WAKE");
		}

		self.guardLock.unlock();
	}

	private void notifySelf()
	{
		if (self != null && self.guardLock != null)
		{
			debug("Signal guard");
			self.guardLock.signal();
		}
	}

	private Value asyncEval(ValueList argValues, Context ctxt) throws ValueException
	{
		// Spawn a thread, send a message, wait for a reply...

		CPUValue from = ctxt.threadState.CPU;
		CPUValue to = self.getCPU();
		boolean stepping = ctxt.threadState.isStepping();
		long threadId = BasicSchedulableThread.getThread(Thread.currentThread()).getId();
		// Async calls have the OpRequest made by the caller using the
		// "from" CPU, whereas the OpActivate and OpComplete are made
		// by the called object, using self's CPU (see trace(msg)).

		RTLogger.log(new RTOperationMessage(MessageType.Request, this, from.resource, threadId));

		if (from != to)		// Remote CPU call
		{
    		BUSValue bus = BUSValue.lookupBUS(from, to);

    		if (bus == null)
    		{
    			abort(4140,
    				"No BUS between CPUs " + from.getName() + " and " + to.getName(), ctxt);
    		}

    		if (isAsync)	// Don't wait
    		{
        		MessageRequest request = new MessageRequest(ctxt.threadState.dbgp,
        			bus, from, to, self, this, argValues, null, stepping);

        		bus.transmit(request);
        		return new VoidValue();
    		}
    		else
    		{
        		Holder<MessageResponse> result = new Holder<MessageResponse>();
        		MessageRequest request = new MessageRequest(ctxt.threadState.dbgp,
        			bus, from, to, self, this, argValues, result, stepping);

        		bus.transmit(request);
        		MessageResponse reply = result.get(ctxt, name.location);
        		return reply.getValue();	// Can throw a returned exception
    		}
		}
		else	// local, must be async so don't wait
		{
    		MessageRequest request = new MessageRequest(ctxt.threadState.dbgp,
    			null, from, to, self, this, argValues, null, stepping);

    		AsyncThread t = new AsyncThread(request);
    		t.start();
    		RuntimeValidator.validateAsync(this,t);
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
	public Value convertValueTo(PTypeInterpreter to, Context ctxt) throws ValueException
	{
		if (to.isType(AOperationTypeInterpreter.class))
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
		
		MessageType type = MessageType.Request;
		RuntimeValidator.validate(this, type);

		if (logreq)		// Async OpRequests are made in asyncEval
		{
			trace(type);
		}

		debug("#req = " + hashReq);
	}

	private synchronized void act()
	{
		hashAct++;

		if (!ResourceScheduler.isStopping())
		{
			MessageType type = MessageType.Activate; 
			RuntimeValidator.validate(this,type);
			trace(type);
			debug("#act = " + hashAct);
		}
	}

	private synchronized void fin()
	{
		hashFin++;

		if (!ResourceScheduler.isStopping())
		{
			MessageType type = MessageType.Completed;
			RuntimeValidator.validate(this, type);
			trace(type);
			debug("#fin = " + hashFin);
		}
	}

	private void trace(MessageType kind)
	{
		if (traceRT)
		{
			ISchedulableThread ct = BasicSchedulableThread.getThread(Thread.currentThread());

			if (isStatic)
			{
				CPUResource cpu = null;
				if (ct instanceof InitThread)
				{
					cpu = CPUValue.vCPU.resource;	// Initialization on vCPU
				}
				else
				{
					cpu = ct.getCPUResource();
				}

				RTLogger.log(new RTOperationMessage(kind, this, cpu, ct.getId()));
			}
			else
			{
				RTLogger.log(new RTOperationMessage(kind, this, self.getCPU().resource, ct.getId()));
			}
		}
	}

	/**
	 * @param string
	 */

	private void debug(String string)
	{
		if (Properties.diags_guards)
		{
			if (Settings.dialect == Dialect.VDM_PP)
			{
				System.err.println(String.format("%s %s %s",
						BasicSchedulableThread.getThread(Thread.currentThread()), name, string));
			}
			else
			{
				RTLogger.log(new RTExtendedTextMessage(String.format("-- %s %s %s",
						BasicSchedulableThread.getThread(Thread.currentThread()), name, string)));
			}
		}
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
		return self == null ? CPUValue.vCPU : self.getCPU();
	}

	public String toTitle()
	{
		return name.name + Utils.listToString("(", paramPatterns, ", ", ")");
	}
}
