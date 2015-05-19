package org.overture.interpreter.assistant.definition;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.overture.ast.assistant.IAstAssistant;
import org.overture.ast.definitions.ABusClassDefinition;
import org.overture.ast.definitions.ACpuClassDefinition;
import org.overture.ast.definitions.ASystemClassDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.factory.AstFactoryTC;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.PType;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.debug.DBGPReader;
import org.overture.interpreter.messages.rtlog.RTOperationMessage;
import org.overture.interpreter.runtime.ContextException;
import org.overture.interpreter.runtime.RootContext;
import org.overture.interpreter.runtime.RuntimeValidator;
import org.overture.interpreter.runtime.state.ASystemClassDefinitionRuntime;
import org.overture.interpreter.scheduler.ResourceScheduler;
import org.overture.interpreter.values.BUSValue;
import org.overture.interpreter.values.CPUValue;
import org.overture.interpreter.values.ObjectValue;
import org.overture.interpreter.values.QuoteValue;
import org.overture.interpreter.values.RealValue;
import org.overture.interpreter.values.UpdatableValue;
import org.overture.interpreter.values.ValueList;
import org.overture.interpreter.values.ValueSet;
import org.overture.parser.lex.LexException;
import org.overture.parser.syntax.ParserException;

public class ASystemClassDefinitionAssistantInterpreter implements IAstAssistant
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public ASystemClassDefinitionAssistantInterpreter(
			IInterpreterAssistantFactory af)
	{
		// super(af);
		this.af = af;
	}

	//FIXME: Only used once. Remove it
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
			ASystemClassDefinitionRuntime.system = af.createSClassDefinitionAssistant().makeNewInstance(systemClass, null, new ValueList(), initialContext, new HashMap<ILexNameToken, ObjectValue>(), false);

			// Bind system instances to runtime validator
			RuntimeValidator.bindSystemVariables(systemClass, af);

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

					cpu = (CPUValue) af.createACpuClassDefinitionAssistant().newInstance(instance, null, args, initialContext);
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

			BUSValue.vBUS = makeVirtualBUS(cpus);
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
	public BUSValue makeVirtualBUS(ValueSet cpus)
	{
		try
		{
			return new BUSValue((AClassType) AstFactoryTC.newABusClassDefinition(af).getType(), cpus);
		} catch (ParserException e)
		{

		} catch (LexException e)
		{

		}
		return null;
	}

}
