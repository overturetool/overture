package org.overture.interpreter.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.ABusClassDefinition;
import org.overture.ast.definitions.ACpuClassDefinition;
import org.overture.ast.definitions.ASystemClassDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.factory.AstFactoryTC;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.PType;
import org.overture.ast.util.definitions.ClassList;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.debug.DBGPReader;
import org.overture.interpreter.messages.Console;
import org.overture.interpreter.messages.rtlog.RTOperationMessage;
import org.overture.interpreter.runtime.ClassInterpreter;
import org.overture.interpreter.runtime.ContextException;
import org.overture.interpreter.runtime.RootContext;
import org.overture.interpreter.runtime.RuntimeValidator;
import org.overture.interpreter.runtime.StateContext;
import org.overture.interpreter.runtime.state.ASystemClassDefinitionRuntime;
import org.overture.interpreter.scheduler.ResourceScheduler;
import org.overture.interpreter.values.BUSValue;
import org.overture.interpreter.values.CPUValue;
import org.overture.interpreter.values.ObjectValue;
import org.overture.interpreter.values.QuoteValue;
import org.overture.interpreter.values.RealValue;
import org.overture.interpreter.values.TransactionValue;
import org.overture.interpreter.values.UpdatableValue;
import org.overture.interpreter.values.ValueList;
import org.overture.interpreter.values.ValueSet;
import org.overture.parser.lex.LexException;
import org.overture.parser.syntax.ParserException;
import org.overture.pog.obligation.ProofObligationList;
import org.overture.pog.pub.ProofObligationGenerator;

public class ClassListInterpreter extends ClassList
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6070028869925906992L;

	public ClassListInterpreter(ClassList classList)
	{
		super();
		addAll(classList);
	}

	public ClassListInterpreter()
	{
		super();
	}

	public void systemInit(ResourceScheduler scheduler, DBGPReader dbgp,
			RootContext initialContext)
	{
		ASystemClassDefinition systemClass = null;

		for (SClassDefinition cdef : this)
		{
			if (cdef instanceof ASystemClassDefinition)
			{
				systemClass = (ASystemClassDefinition) cdef;
				systemInit(systemClass, scheduler, dbgp, initialContext);
				TransactionValue.commitAll();
			}
		}
	}

	public RootContext initialize(IInterpreterAssistantFactory af,
			DBGPReader dbgp)
	{
		StateContext globalContext = null;

		if (isEmpty())
		{
			globalContext = new StateContext(af, new LexLocation(), "global environment");
		} else
		{
			globalContext = new StateContext(af, this.get(0).getLocation(), "public static environment");
		}

		globalContext.setThreadState(dbgp, CPUValue.vCPU);

		// Initialize all the functions/operations first because the values
		// "statics" can call them.

		for (SClassDefinition cdef : this)
		{
			af.createSClassDefinitionAssistant().staticInit(cdef, globalContext);
		}

		// Values can forward reference each other, which means that we don't
		// know what order to initialize the classes in. So we have a crude
		// retry mechanism, looking for "forward reference" like exceptions.

		ContextException failed = null;
		int retries = 3; // Potentially not enough.
		Set<ContextException> trouble = new HashSet<ContextException>();

		do
		{
			failed = null;
			trouble.clear();

			for (SClassDefinition cdef : this)
			{
				try
				{
					af.createSClassDefinitionAssistant().staticValuesInit(cdef, globalContext);
				} catch (ContextException e)
				{
					trouble.add(e);
					// These two exceptions mean that a member could not be
					// found, which may be a forward reference, so we retry...

					if (e.number == 4034 || e.number == 6)
					{
						failed = e;
					} else
					{
						throw e;
					}
				}
			}
		} while (--retries > 0 && failed != null);

		if (!trouble.isEmpty())
		{
			ContextException toThrow = trouble.iterator().next();

			for (ContextException e : trouble)
			{
				Console.err.println(e);

				if (e.number != 4034) // Not in scope err
				{
					toThrow = e;
				}
			}

			throw toThrow;
		}

		return globalContext;
	}

	public ProofObligationList getProofObligations(
			IInterpreterAssistantFactory assistantFactory)
			throws AnalysisException
	{
		// TODO: Check this method, where it is used.
		ProofObligationList obligations = new ProofObligationList();

		for (SClassDefinition c : this)
		{

			obligations.addAll(ProofObligationGenerator.generateProofObligations(c));
		}
		obligations.trivialCheck();
		return obligations;
	}

	public void setLoaded()
	{
		for (SClassDefinition d : this)
		{
			d.setTypeChecked(true);
		}
	}

	public int notLoaded()
	{
		int count = 0;

		for (SClassDefinition d : this)
		{
			if (!d.getTypeChecked())
			{
				count++;
			}
		}

		return count;
	}

	public PDefinition findName(LexNameToken name, NameScope scope)
	{
		SClassDefinition d = map.get(name.module);

		if (d != null)
		{
			PDefinition def = ClassInterpreter.getInstance().getAssistantFactory().createPDefinitionAssistant().findName(d, name, scope);

			if (def != null)
			{
				return def;
			}
		}

		return null;
	}
	
	public void systemInit(ASystemClassDefinition systemClass,
			ResourceScheduler scheduler, DBGPReader dbgp,
			RootContext initialContext)
	{
		// systemContext = new StateContext(location, "RT system environment");
		initialContext.setThreadState(dbgp, CPUValue.vCPU);

		try
		{
			// First go through the definitions, looking for CPUs to decl
			// before we can deploy to them in the constructor. We have to
			// predict the CPU numbers at this point.

			List<PDefinition> cpudefs = new Vector<PDefinition>();
			ACpuClassDefinition instance = null;

			for (PDefinition d : systemClass.getDefinitions())
			{
				PType t = d.getType();

				if (t instanceof AClassType)
				{
					AClassType ct = (AClassType) t;

					if (ct.getClassdef() instanceof ACpuClassDefinition)
					{
						cpudefs.add(d);
						instance = (ACpuClassDefinition) ct.getClassdef();
					}
				}
			}

			// Run the constructor to do any deploys etc.
			ASystemClassDefinitionRuntime.system = initialContext.assistantFactory.createSClassDefinitionAssistant().makeNewInstance(systemClass, null, new ValueList(), initialContext, new HashMap<ILexNameToken, ObjectValue>(), false);

			// Bind system instances to runtime validator
			RuntimeValidator.bindSystemVariables(systemClass, initialContext.assistantFactory);

			// Do CPUs first so that default BUSses can connect all CPUs.

			ValueSet cpus = new ValueSet();

			for (PDefinition d : cpudefs)
			{
				UpdatableValue v = (UpdatableValue) ASystemClassDefinitionRuntime.system.members.get(d.getName());
				CPUValue cpu = null;

				if (v.isUndefined())
				{
					ValueList args = new ValueList();

					args.add(new QuoteValue("FCFS")); // Default policy
					args.add(new RealValue(0)); // Default speed

					cpu = (CPUValue) initialContext.assistantFactory.createACpuClassDefinitionAssistant().newInstance(instance, null, args, initialContext);
					v.set(systemClass.getLocation(), cpu, initialContext);
				} else
				{
					cpu = (CPUValue) v.deref();
				}

				// RTLogger.log(new RTDeclareCPUMessage(cpu.resource.getNumber(), !v.isUndefined(),
				// systemClass.getName().getName(), d.getName().getName()));

				// Set the name and scheduler for the CPU resource, and
				// associate the resource with the scheduler.

				cpu.setup(scheduler, d.getName().getName());
				cpus.add(cpu);
			}

			// We can create vBUS now that all the CPUs have been created
			// This must be first, to ensure it's bus number 0.

			BUSValue.vBUS = makeVirtualBUS(cpus,initialContext);
			BUSValue.vBUS.setup(scheduler, "vBUS");

			for (PDefinition d : systemClass.getDefinitions())
			{
				PType t = d.getType();

				if (t instanceof AClassType)
				{
					AClassType ct = (AClassType) t;

					if (ct.getClassdef() instanceof ABusClassDefinition)
					{
						UpdatableValue v = (UpdatableValue) ASystemClassDefinitionRuntime.system.members.get(d.getName());
						BUSValue bus = null;

						if (!v.isUndefined())
						{
							bus = (BUSValue) v.deref();

							// Set the name and scheduler for the BUS resource, and
							// associate the resource with the scheduler.

							bus.setup(scheduler, d.getName().getName());
						}
					}
				}
			}

			// For efficiency, we create a 2D array of CPU-to-CPU bus links
			BUSValue.createMap(initialContext, cpus);

			// Disable the system construction - all objects have not been created and deployed.
			RTOperationMessage.inSystemConstruction = false;
		} catch (ContextException e)
		{
			throw e;
			// FIXME:this exception should be thrown
			// } catch (ValueException e)
			// {
			// throw new ContextException(e, systemClass.getLocation());
		} catch (Exception e)
		{
			throw new ContextException(4135, "Cannot instantiate a system class", systemClass.getLocation(), initialContext);
		}

	}
	public BUSValue makeVirtualBUS(ValueSet cpus,RootContext initialContext)
	{
		try
		{
			return new BUSValue((AClassType) AstFactoryTC.newABusClassDefinition(initialContext.assistantFactory).getType(), cpus);
		} catch (ParserException e)
		{

		} catch (LexException e)
		{

		}
		return null;
	}
}
