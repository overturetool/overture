package org.overture.interpreter.assistant.definition;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.ABusClassDefinition;
import org.overture.ast.definitions.ACpuClassDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.ASystemClassDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.factory.ABusClassDefinitionAssitantParser;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.AUndefinedType;
import org.overture.ast.types.PType;
import org.overture.interpreter.debug.DBGPReader;
import org.overture.interpreter.messages.rtlog.RTDeclareCPUMessage;
import org.overture.interpreter.messages.rtlog.RTLogger;
import org.overture.interpreter.messages.rtlog.RTOperationMessage;
import org.overture.interpreter.runtime.ContextException;
import org.overture.interpreter.runtime.RootContext;
import org.overture.interpreter.runtime.RuntimeValidator;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.runtime.VdmRuntime;
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

public class ASystemClassDefinitionAssistantInterpreter
{

	public static void systemInit(ASystemClassDefinition systemClass,
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
			int cpuNumber = 1;
			ACpuClassDefinition instance = null;

			for (PDefinition d : systemClass.getDefinitions())
			{
				PType t = d.getType();

				if (t instanceof AClassType)
				{
					AInstanceVariableDefinition ivd = (AInstanceVariableDefinition) d;
					AClassType ct = (AClassType) t;

					if (ct.getClassdef() instanceof ACpuClassDefinition)
					{
						cpudefs.add(d);
						instance = (ACpuClassDefinition) ct.getClassdef();

						RTLogger.log(new RTDeclareCPUMessage(cpuNumber++, !(ivd.getExpType() instanceof AUndefinedType), systemClass.getName().name, d.getName().name));
					}
				}
			}

			
			// Run the constructor to do any deploys etc.
			ASystemClassDefinitionRuntime.system = SClassDefinitionAssistantInterpreter.makeNewInstance(systemClass, null, new ValueList(), initialContext, new HashMap<LexNameToken, ObjectValue>());

			// Bind system instances to runtime validator
			RuntimeValidator.bindSystemVariables(systemClass);

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

					cpu = (CPUValue) ACpuClassDefinitionAssistantInterpreter.newInstance(instance,null, args, initialContext);
					v.set(systemClass.getLocation(), cpu, initialContext);
				} else
				{
					cpu = (CPUValue) v.deref();
				}

				// Set the name and scheduler for the CPU resource, and
				// associate the resource with the scheduler.

				cpu.setup(scheduler, d.getName().name);
				cpus.add(cpu);
			}

			// We can create vBUS now that all the CPUs have been created
			// This must be first, to ensure it's bus number 0.

			BUSValue.vBUS = ABusClassDefinitionAssitantInterpreter.makeVirtualBUS(cpus);
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

							bus.setup(scheduler, d.getName().name);
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
			//FIXME:this exception should be thrown
//		} catch (ValueException e)
//		{
//			throw new ContextException(e, systemClass.getLocation());
		} catch (Exception e)
		{
			throw new ContextException(4135, "Cannot instantiate a system class", systemClass.getLocation(), initialContext);
		}

	}

}
