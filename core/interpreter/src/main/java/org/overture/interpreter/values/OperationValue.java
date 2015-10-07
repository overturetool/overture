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

package org.overture.interpreter.values;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.ASystemClassDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.Dialect;
import org.overture.ast.lex.LexKeywordToken;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.lex.VDMToken;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.patterns.APatternListTypePair;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.statements.PStm;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.PType;
import org.overture.ast.util.Utils;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.assistant.definition.SClassDefinitionAssistantInterpreter;
import org.overture.interpreter.messages.Console;
import org.overture.interpreter.messages.rtlog.RTExtendedTextMessage;
import org.overture.interpreter.messages.rtlog.RTLogger;
import org.overture.interpreter.messages.rtlog.RTMessage.MessageType;
import org.overture.interpreter.messages.rtlog.RTOperationMessage;
import org.overture.interpreter.runtime.ClassContext;
import org.overture.interpreter.runtime.ClassInterpreter;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.Interpreter;
import org.overture.interpreter.runtime.ModuleInterpreter;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.runtime.PatternMatchException;
import org.overture.interpreter.runtime.RootContext;
import org.overture.interpreter.runtime.RuntimeValidator;
import org.overture.interpreter.runtime.StateContext;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.runtime.VdmRuntime;
import org.overture.interpreter.scheduler.AsyncThread;
import org.overture.interpreter.scheduler.BasicSchedulableThread;
import org.overture.interpreter.scheduler.CPUResource;
import org.overture.interpreter.scheduler.Holder;
import org.overture.interpreter.scheduler.ISchedulableThread;
import org.overture.interpreter.scheduler.InitThread;
import org.overture.interpreter.scheduler.Lock;
import org.overture.interpreter.scheduler.MessageRequest;
import org.overture.interpreter.scheduler.MessageResponse;
import org.overture.interpreter.scheduler.ResourceScheduler;
import org.overture.interpreter.solver.IConstraintSolver;
import org.overture.interpreter.solver.SolverFactory;
import org.overture.parser.config.Properties;

public class OperationValue extends Value
{
	private static final long serialVersionUID = 1L;
	public final AExplicitOperationDefinition expldef;
	public final AImplicitOperationDefinition impldef;
	public final ILexNameToken name;
	public final AOperationType type;
	public final List<PPattern> paramPatterns;
	public final PStm body;
	public final FunctionValue precondition;
	public final FunctionValue postcondition;
	public final AStateDefinition state;
	public final SClassDefinition classdef;

	private ILexNameToken stateName = null;
	private Context stateContext = null;
	private ObjectValue self = null;

	public boolean isConstructor = false;
	public boolean isStatic = false;
	public boolean isAsync = false;

	private PExp guard = null;

	private int hashAct = 0; // Number of activations
	private int hashFin = 0; // Number of finishes
	private int hashReq = 0; // Number of requests

	public int getHashAct()
	{
		return this.hashAct;
	}

	public int getHashFin()
	{
		return this.hashFin;
	}

	public int getHashReq()
	{
		return this.hashReq;
	}

	private long priority = 0;
	private boolean traceRT = true;

	public OperationValue(AExplicitOperationDefinition def,
			FunctionValue precondition, FunctionValue postcondition,
			AStateDefinition state,
			IInterpreterAssistantFactory assistantFactory)
	{
		this(def, precondition, postcondition, state, assistantFactory.createPAccessSpecifierAssistant().isAsync(def.getAccess()));
	}

	private OperationValue(AExplicitOperationDefinition def,
			FunctionValue precondition, FunctionValue postcondition,
			AStateDefinition state, boolean async)
	{
		this.expldef = def;
		this.impldef = null;
		this.name = def.getName();
		this.type = (AOperationType) def.getType();
		this.paramPatterns = def.getParameterPatterns();
		this.body = def.getBody();
		this.precondition = precondition;
		this.postcondition = postcondition;
		this.state = state;
		this.classdef = def.getClassDefinition();
		this.isAsync = async;

		traceRT = Settings.dialect == Dialect.VDM_RT && classdef != null
				&& !(classdef instanceof ASystemClassDefinition)
				&& !classdef.getName().getName().equals("CPU")
				&& !classdef.getName().getName().equals("BUS")
				&& !name.getName().equals("thread")
				&& !name.getName().startsWith("inv_");
	}

	public OperationValue(AImplicitOperationDefinition def,
			FunctionValue precondition, FunctionValue postcondition,
			AStateDefinition state,
			IInterpreterAssistantFactory assistantFactory)
	{
		this(def, precondition, postcondition, state, assistantFactory.createPAccessSpecifierAssistant().isAsync(def.getAccess()));
	}

	private OperationValue(AImplicitOperationDefinition def,
			FunctionValue precondition, FunctionValue postcondition,
			AStateDefinition state, boolean async)
	{
		this.impldef = def;
		this.expldef = null;
		this.name = def.getName();
		this.type = (AOperationType) def.getType();
		this.paramPatterns = new Vector<PPattern>();

		for (APatternListTypePair ptp : def.getParameterPatterns())
		{
			paramPatterns.addAll(ptp.getPatterns());
		}

		this.body = def.getBody();
		this.precondition = precondition;
		this.postcondition = postcondition;
		this.state = state;
		this.classdef = def.getClassDefinition();
		this.isAsync = async;

		traceRT = Settings.dialect == Dialect.VDM_RT && classdef != null
				&& !(classdef instanceof ASystemClassDefinition)
				&& !classdef.getName().getName().equals("CPU")
				&& !classdef.getName().getName().equals("BUS")
				&& !name.getName().equals("thread");
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

	public void setGuard(PExp add, boolean isMutex)
	{
		if (guard == null)
		{
			guard = add;
		} else
		{
			// Create "old and new" expression

			ILexLocation where = isMutex ? guard.getLocation()
					: add.getLocation();

			guard = AstFactory.newAAndBooleanBinaryExp(guard.clone(), new LexKeywordToken(VDMToken.AND, where), add.clone());
		}
	}

	public void prepareGuard(ObjectContext ctxt)
	{
		if (guard != null)
		{
			ValueListener vl = new GuardValueListener(getGuardLock(ctxt.assistantFactory));

			for (Value v : ctxt.assistantFactory.createPExpAssistant().getValues(guard, ctxt))
			{
				UpdatableValue uv = (UpdatableValue) v;
				uv.addListener(vl);
			}
		}
	}

	public Value eval(ILexLocation from, ValueList argValues, Context ctxt)
			throws AnalysisException
	{
		// Note args cannot be Updateable, so we convert them here. This means
		// that TransactionValues pass the local "new" value to the far end.
		ValueList constValues = argValues.getConstant();

		if (Settings.dialect == Dialect.VDM_RT)
		{
			if (!isStatic && (ctxt.threadState.CPU != self.getCPU() || isAsync))
			{
				return asyncEval(constValues, ctxt);
			} else
			{
				return localEval(from, constValues, ctxt, true);
			}
		} else
		{
			return localEval(from, constValues, ctxt, true);
		}
	}

	public Value localEval(ILexLocation from, ValueList argValues,
			Context ctxt, boolean logreq) throws AnalysisException
	{
		if (state != null && stateName == null)
		{
			stateName = state.getName();
			stateContext = ctxt.assistantFactory.createAStateDefinitionAssistant().getStateContext(state);
		}

		RootContext argContext = newContext(from, toTitle(), ctxt);

		req(logreq);
		notifySelf(ctxt.assistantFactory);

		if (guard != null)
		{
			guard(argContext);
		} else
		{
			act(); // Still activated, even if no guard
		}

		notifySelf(ctxt.assistantFactory);

		if (argValues.size() != paramPatterns.size())
		{
			abort(4068, "Wrong number of arguments passed to " + name.getName(), ctxt);
		}

		ListIterator<Value> valIter = argValues.listIterator();
		Iterator<PType> typeIter = type.getParameters().iterator();
		NameValuePairMap args = new NameValuePairMap();

		for (PPattern p : paramPatterns)
		{
			try
			{
				// Note values are assumed to be constant, as enforced by eval()
				Value pv = valIter.next().convertTo(typeIter.next(), ctxt);

				for (NameValuePair nvp : ctxt.assistantFactory.createPPatternAssistant().getNamedValues(p, pv, ctxt))
				{
					Value v = args.get(nvp.name);

					if (v == null)
					{
						args.put(nvp);
					} else
					// Names match, so values must also
					{
						if (!v.equals(nvp.value))
						{
							abort(4069, "Parameter patterns do not match arguments", ctxt);
						}
					}
				}
			} catch (PatternMatchException e)
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
		MapValue originalValues = null;

		if (postcondition != null)
		{
			if (stateName != null)
			{
				Value sigma = argContext.lookup(stateName);
				originalSigma = (Value) sigma.clone();
			} else if (self != null)
			{
				// originalSelf = self.shallowCopy();
				LexNameList oldnames = ctxt.assistantFactory.createPExpAssistant().getOldNames(postcondition.body);
				originalValues = self.getOldValues(oldnames);
			} else if (classdef != null)
			{
				LexNameList oldnames = ctxt.assistantFactory.createPExpAssistant().getOldNames(postcondition.body);
				SClassDefinitionAssistantInterpreter assistant = ctxt.assistantFactory.createSClassDefinitionAssistant();
				originalValues = assistant.getOldValues(classdef, oldnames);
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
				} else if (self != null)
				{
					preArgs.add(self);
				}

				// We disable the swapping and time (RT) as precondition checks should be "free".

				try
				{
					ctxt.threadState.setAtomic(true);
					ctxt.setPrepost(4071, "Precondition failure: ");
					precondition.eval(from, preArgs, ctxt);
				} finally
				{
					ctxt.setPrepost(0, null);
					ctxt.threadState.setAtomic(false);
				}
			}

			if (body == null)
			{

				IConstraintSolver solver = SolverFactory.getSolver(Settings.dialect);

				if (solver != null)
				{
					rv = invokeSolver(ctxt, argContext, args, rv, solver);

				} else
				{
					abort(4066, "Cannot call implicit operation: " + name, ctxt);
				}

			} else
			{
	    		if (Settings.release == Release.VDM_10 && !type.getPure() && ctxt.threadState.isPure())
	    		{
	    			abort(4166, "Cannot call impure operation: " + name, ctxt);
	    		}

	    		rv = body.apply(VdmRuntime.getStatementEvaluator(), argContext);
			}

			if (isConstructor)
			{
				rv = self;
			} else
			{
				rv = rv.convertTo(type.getResult(), argContext);
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
				} else if (self != null)
				{
					postArgs.add(originalValues);
					postArgs.add(self);
				} else if (classdef != null)
				{
					postArgs.add(originalValues);
				}

				// We disable the swapping and time (RT) as postcondition checks should be "free".

				try
				{
					ctxt.threadState.setAtomic(true);
					ctxt.setPrepost(4072, "Postcondition failure: ");
					postcondition.eval(from, postArgs, ctxt);
				} finally
				{
					ctxt.setPrepost(0, null);
					ctxt.threadState.setAtomic(false);
				}
			}

		} catch (AnalysisException e)
		{
			if (e instanceof ValueException)
			{
				throw (ValueException) e;
			}
			e.printStackTrace();
		} finally
		{
			fin();
			notifySelf(ctxt.assistantFactory);
		}

		return rv;
	}

	public Value invokeSolver(Context ctxt, RootContext argContext,
			NameValuePairMap args, Value rv, IConstraintSolver solver)
			throws ValueException
	{
		try
		{
			Map<String, String> argExps = new HashMap<String, String>();
			for (Entry<ILexNameToken, Value> argVal : args.entrySet())
			{
				argExps.put(argVal.getKey().getName(), argVal.getValue().toString());
			}

			Map<String, String> stateExps = new HashMap<String, String>();

			if (stateContext != null)
			{
				for (Entry<ILexNameToken, Value> argVal : stateContext.entrySet())
				{
					if (argVal.getKey().parent() instanceof AFieldField)
					{
						stateExps.put(argVal.getKey().getName(), argVal.getValue().toString());
					}
				}
			} else
			{
				// TODO
				if (self != null)
				{
					for (Entry<ILexNameToken, Value> argVal : self.getMemberValues().entrySet())
					{
						if (argVal.getValue() instanceof FunctionValue
								|| argVal.getValue() instanceof OperationValue)
						{
							continue;
						}
						if (argVal.getValue() instanceof UpdatableValue)
						{
							stateExps.put(argVal.getKey().getName(), argVal.getValue().toString());
						}
					}
				}
			}

			Interpreter interpreter = Interpreter.getInstance();
			List<PDefinition> allDefs = new Vector<PDefinition>();
			if (interpreter instanceof ClassInterpreter)
			{
				for (SClassDefinition c : ((ClassInterpreter) interpreter).getClasses())
				{
					allDefs.addAll(c.getDefinitions());
				}
			} else if (interpreter instanceof ModuleInterpreter)
			{
				for (AModuleModules c : ((ModuleInterpreter) interpreter).getModules())
				{
					allDefs.addAll(c.getDefs());
				}
			}

			PStm res = solver.solve(allDefs, name.getName(), this.impldef, stateExps, argExps, Console.out, Console.err);

			rv = res.apply(VdmRuntime.getStatementEvaluator(), argContext);
		} catch (Exception e)
		{
			e.printStackTrace();
			abort(4066, "Cannot call implicit operation: " + name, ctxt);
		}
		return rv;
	}

	private RootContext newContext(ILexLocation from, String title, Context ctxt)
	{
		RootContext argContext;

		if (self != null)
		{
			argContext = new ObjectContext(Interpreter.getInstance().getAssistantFactory(), from, title, ctxt, self);
		} else if (classdef != null)
		{
			argContext = new ClassContext(Interpreter.getInstance().getAssistantFactory(), from, title, ctxt, classdef);
		} else
		{
			argContext = new StateContext(Interpreter.getInstance().getAssistantFactory(), from, title, ctxt, stateContext);
		}

		return argContext;
	}

	private Lock getGuardLock(IInterpreterAssistantFactory assistantFactory)
	{
		if (classdef != null)
		{
			return VdmRuntime.getNodeState(assistantFactory, classdef).guardLock;
		} else if (self != null)
		{
			return self.guardLock;
		} else
		{
			return null;
		}
	}

	private Object getGuardObject(Context ctxt)
	{
		if (ctxt instanceof ClassContext)
		{
			ClassContext cctxt = (ClassContext) ctxt;
			return cctxt.classdef;
		} else
		{
			return self;
		}
	}

	private void guard(Context ctxt) throws ValueException
	{
		ISchedulableThread th = BasicSchedulableThread.getThread(Thread.currentThread());
		if (th == null || th instanceof InitThread)
		{
			act();
			return; // Probably during initialization.
		}

		Lock lock = getGuardLock(ctxt.assistantFactory);
		lock.lock(ctxt, guard.getLocation());

		while (true)
		{
			synchronized (getGuardObject(ctxt)) // So that test and act() are atomic
			{
				// We have to suspend thread swapping round the guard,
				// else we will reschedule another CPU thread while
				// having self locked, and that locks up everything!
				boolean ok = false;

				try
				{
					debug("guard TEST");
					ctxt.threadState.setAtomic(true);

					try
					{
						ok = guard.apply(VdmRuntime.getExpressionEvaluator(), ctxt).boolValue(ctxt);
					} catch (AnalysisException e)
					{
						if (e instanceof ValueException)
						{
							throw (ValueException) e;
						}
						e.printStackTrace();
					}
				} finally
				{
					ctxt.threadState.setAtomic(false);
				}

				if (ok)
				{
					debug("guard OK");
					act();
					break; // Out of while loop
				}
			}

			// The guardLock list is signalled by the GuardValueListener
			// and by notifySelf when something changes. The guardOp
			// is set to indicate the guard state to any breakpoints.

			debug("guard WAIT");
			ctxt.guardOp = this;
			lock.block(ctxt, guard.getLocation());
			ctxt.guardOp = null;
			debug("guard WAKE");
		}

		lock.unlock();
	}

	private void notifySelf(IInterpreterAssistantFactory assistantFactory)
	{
		Lock lock = getGuardLock(assistantFactory);

		if (lock != null)
		{
			debug("Signal guard");
			lock.signal();
		}
	}

	private Value asyncEval(ValueList argValues, Context ctxt)
			throws ValueException
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

		if (from != to) // Remote CPU call
		{
			BUSValue bus = BUSValue.lookupBUS(from, to);

			if (bus == null)
			{
				abort(4140, "No BUS between CPUs " + from.getName() + " and "
						+ to.getName(), ctxt);
			}

			if (isAsync) // Don't wait
			{
				MessageRequest request = new MessageRequest(ctxt.threadState.dbgp, bus, from, to, self, this, argValues, null, stepping);

				bus.transmit(request);
				return new VoidValue();
			} else
			{
				Holder<MessageResponse> result = new Holder<MessageResponse>();
				MessageRequest request = new MessageRequest(ctxt.threadState.dbgp, bus, from, to, self, this, argValues, result, stepping);

				bus.transmit(request);
				MessageResponse reply = result.get(ctxt, name.getLocation());
				return reply.getValue(); // Can throw a returned exception
			}
		} else
		// local, must be async so don't wait
		{
			MessageRequest request = new MessageRequest(ctxt.threadState.dbgp, null, from, to, self, this, argValues, null, stepping);

			AsyncThread t = new AsyncThread(request);
			t.start();
			RuntimeValidator.validateAsync(this, t);
			return new VoidValue();
		}
	}

	@Override
	public boolean equals(Object other)
	{
		if (other instanceof Value)
		{
			Value val = ((Value) other).deref();

			if (val instanceof OperationValue)
			{
				OperationValue ov = (OperationValue) val;
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
	protected Value convertValueTo(PType to, Context ctxt, Set<PType> done)
			throws AnalysisException
	{
		if (ctxt.assistantFactory.createPTypeAssistant().isType(to, AOperationType.class))
		{
			return this;
		} else
		{
			return super.convertValueTo(to, ctxt, done);
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
			return new OperationValue(expldef, precondition, postcondition, state, isAsync);
		} else
		{
			return new OperationValue(impldef, precondition, postcondition, state, isAsync);
		}
	}

	private synchronized void req(boolean logreq)
	{
		hashReq++;

		MessageType type = MessageType.Request;
		RuntimeValidator.validate(this, type);

		if (logreq) // Async OpRequests are made in asyncEval
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
			RuntimeValidator.validate(this, type);
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
					cpu = CPUValue.vCPU.resource; // Initialization on vCPU
				} else
				{
					cpu = ct.getCPUResource();
				}

				RTLogger.log(new RTOperationMessage(kind, this, cpu, ct.getId()));
			} else
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
				System.err.println(String.format("%s %s %s", BasicSchedulableThread.getThread(Thread.currentThread()), name, string));
			} else
			{
				RTLogger.log(new RTExtendedTextMessage(String.format("-- %s %s %s", BasicSchedulableThread.getThread(Thread.currentThread()), name, string)));
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
		return name.getName()
				+ Utils.listToString("(", paramPatterns, ", ", ")");
	}
}
