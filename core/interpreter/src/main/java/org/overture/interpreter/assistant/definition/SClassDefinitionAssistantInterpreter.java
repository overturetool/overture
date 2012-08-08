package org.overture.interpreter.assistant.definition;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import org.overture.ast.assistant.pattern.PTypeList;
import org.overture.ast.definitions.ABusClassDefinition;
import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.ACpuClassDefinition;
import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.ast.definitions.AMutexSyncDefinition;
import org.overture.ast.definitions.APerSyncDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.lex.Dialect;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.statements.PStm;
import org.overture.ast.types.AClassType;
import org.overture.config.Settings;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.runtime.VdmRuntimeError;
import org.overture.interpreter.runtime.StateContext;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.runtime.VdmRuntime;
import org.overture.interpreter.util.ClassListInterpreter;
import org.overture.interpreter.values.CPUValue;
import org.overture.interpreter.values.ClassInvariantListener;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.interpreter.values.NameValuePairMap;
import org.overture.interpreter.values.ObjectValue;
import org.overture.interpreter.values.OperationValue;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.ValueList;
import org.overture.interpreter.values.ValueListenerList;
import org.overture.pog.obligation.POContextStack;
import org.overture.pog.obligation.ProofObligationList;
import org.overture.typechecker.assistant.definition.SClassDefinitionAssistantTC;

public class SClassDefinitionAssistantInterpreter extends SClassDefinitionAssistantTC
{

	public static Value getStatic(SClassDefinition classdef, LexNameToken sought)
	{
		LexNameToken local = (sought.explicit) ? sought
				: sought.getModifiedName(classdef.getName().name);

		
		
		
		Value v = VdmRuntime.getNodeState(classdef).privateStaticValues.get(local);

		if (v == null)
		{
			v = VdmRuntime.getNodeState(classdef).publicStaticValues.get(local);

			if (v == null)
			{
				for (SClassDefinition sdef : classdef.getSuperDefs())
				{
					v = getStatic(sdef,local);

					if (v != null)
					{
						break;
					}
				}
			}
		}

		return v;
	}

	public static Context getStatics(SClassDefinition classdef)
	{
		Context ctxt = new Context(classdef.getLocation(), "Statics", null);
		ctxt.putAll(VdmRuntime.getNodeState(classdef).publicStaticValues);
		ctxt.putAll(VdmRuntime.getNodeState(classdef).privateStaticValues);
		return ctxt;
	}
	
	public static ObjectValue newInstance(SClassDefinition node,PDefinition ctorDefinition , ValueList argvals,
			Context ctxt) throws ValueException
	{
		switch (node.kindSClassDefinition())
		{
			case BUS:
				return ABusClassDefinitionAssitantInterpreter.newInstance((ABusClassDefinition)node,ctorDefinition,argvals,ctxt);
			case CLASS:
				return AClassClassDefinitionAssistantInterpreter.newInstance((AClassClassDefinition)node,ctorDefinition ,argvals,ctxt);
			case CPU:
				return ACpuClassDefinitionAssistantInterpreter.newInstance((ACpuClassDefinition)node, ctorDefinition, argvals, ctxt);
			case SYSTEM:
				VdmRuntimeError.abort(node.getLocation(),4135, "Cannot instantiate system class " + node.getName(), ctxt);
		}
		
		return null;
	}

	protected static ObjectValue makeNewInstance(SClassDefinition node,PDefinition ctorDefinition , ValueList argvals,
			Context ctxt, Map<LexNameToken, ObjectValue> done) throws ValueException
	{
		setStaticDefinitions(node,ctxt.getGlobal());		// When static member := new X()
		setStaticValues(node,ctxt.getGlobal());	// When static member := new X()

		List<ObjectValue> inherited = new Vector<ObjectValue>();
		NameValuePairMap members = new NameValuePairMap();

		for (SClassDefinition sdef: node.getSuperDefs())
		{
			// Check the "done" list for virtual inheritance
			ObjectValue obj = done.get(sdef.getName());

			if (obj == null)
			{
				obj = makeNewInstance(sdef,null, null, ctxt, done);
				done.put(sdef.getName(), obj);
			}

			inherited.add(obj);
		}

		// NB. we don't use localInheritedDefinitions because we're creating
		// the local definitions in this loop.

		for (PDefinition idef: node.getSuperInheritedDefinitions())
		{
			// Inherited definitions don't notice when their referenced
			// definition names are updated with type qualifiers.
			// FIXME This ought to be done at the end of type checking...

			if (idef instanceof AInheritedDefinition)
			{
				AInheritedDefinition i = (AInheritedDefinition)idef;
				i.getName().setTypeQualifier(i.getSuperdef().getName().typeQualifier);
			}

			if (PDefinitionAssistantInterpreter.isRuntime(idef))	// eg. TypeDefinitions aren't
			{
				Value v = null;

				for (ObjectValue sobj: inherited)
				{
					v = sobj.get(idef.getName(), true);

					if (v != null)
					{
						LexNameToken localname = idef.getName().getModifiedName(node.getName().name);

						// In a cascade of classes all overriding a name, we may
						// have already created the local name for the nearest
						// name - superInheritedDefinitions has the nearest first.

						if (members.get(localname) == null)
						{
							members.put(localname, v);
						}

						break;
					}
				}

				if (v == null)
				{
					VdmRuntimeError.abort(node.getLocation(),6, "Constructor for " + node.getName().name +
											" can't find " + idef.getName(), ctxt);
				}
			}
		}

		members.putAll(VdmRuntime.getNodeState(node).publicStaticValues);
		members.putAll(VdmRuntime.getNodeState(node).privateStaticValues);

		// We create a RootContext here so that the scope for member
		// initializations are restricted.

		Context initCtxt = new StateContext(node.getLocation(), "field initializers", ctxt, null);
		initCtxt.putList(members.asList());

		// We create an empty context to pass for function creation, so that
		// there are no spurious free variables created.

		Context empty = new Context(node.getLocation(), "empty", null);

		for (PDefinition d: node.getDefinitions())
		{
			if (!PDefinitionAssistantInterpreter.isStatic(d) && PDefinitionAssistantInterpreter.isFunctionOrOperation(d))
			{
				NameValuePairList nvpl = PDefinitionAssistantInterpreter.getNamedValues(d,empty);
				initCtxt.putList(nvpl);
				members.putAll(nvpl);
			}
		}

		// Object instances have their own listeners
		ValueListenerList listeners = null;
		ClassInvariantListener listener = null;

		if (node.getInvariant() != null)
		{
			OperationValue invop = new OperationValue(node.getInvariant(), null, null, null);
			listener = new ClassInvariantListener(invop);
			listener.doInvariantChecks = false;	// during construction
			listeners = new ValueListenerList(listener);
		}

		for (PDefinition d: node.getDefinitions())
		{
			if (!PDefinitionAssistantInterpreter.isStatic(d) && !PDefinitionAssistantInterpreter.isFunctionOrOperation(d))
			{
				NameValuePairList nvpl =
						PDefinitionAssistantInterpreter.getNamedValues(d,initCtxt).getUpdatable(listeners);

				initCtxt.putList(nvpl);
				members.putAll(nvpl);
			}
		}

		setPermissions(node,node.getDefinitions(), members, initCtxt);
		setPermissions(node,node.getSuperInheritedDefinitions(), members, initCtxt);

		ObjectValue creator = ctxt.outer == null ? null : ctxt.outer.getSelf();
		
		ObjectValue object =
			new ObjectValue((AClassType) SClassDefinitionAssistantTC.getType(node), members, inherited,
			ctxt.threadState.CPU, creator);

		if (listener != null)
		{
			object.setListener(listener);
		}

		Value ctor = null;

		if (ctorDefinition == null)
		{
			argvals = new ValueList();
			LexNameToken cname = getCtorName(node,new PTypeList());
     		ctor = object.get(cname, false);
		}
		else
		{
     		ctor = object.get(ctorDefinition.getName(), false);
		}

		if (Settings.dialect ==	Dialect.VDM_RT)
		{
			CPUValue cpu = object.getCPU();

			if (cpu != null)
			{
				cpu.deploy(object);
			}
		}

		if (ctor != null)	// Class may have no constructor defined
		{
     		OperationValue ov = ctor.operationValue(ctxt);

    		ObjectContext ctorCtxt = new ObjectContext(
    				node.getLocation(), node.getName().name + " constructor", ctxt, object);

       		ov.eval(ov.name.location, argvals, ctorCtxt);
		}

		if (VdmRuntime.getNodeState(node).hasPermissions)
		{
    		ObjectContext self = new ObjectContext(
    				node.getLocation(), node.getName().name + " guards", ctxt, object);

    		for (Entry<LexNameToken, Value> entry: members.entrySet())
			{
				Value v = entry.getValue();

				if (v instanceof OperationValue)
				{
					OperationValue opv = (OperationValue)v;
					opv.prepareGuard(self);
				}
			}
		}

		return object;
	}


	private static void setPermissions(SClassDefinition node,
			LinkedList<PDefinition> definitions, NameValuePairMap members,
			Context initCtxt) throws ValueException
	{
		for (PDefinition d: node.getDefinitions())
		{
			while (d instanceof AInheritedDefinition)
			{
				d = ((AInheritedDefinition)d).getSuperdef();
			}

    		if (d instanceof APerSyncDefinition)
    		{
    			APerSyncDefinition sync = (APerSyncDefinition)d;
    			ValueList overloads = members.getOverloads(sync.getOpname());
    			PExp exp = sync.getGuard();

    			for (Value op: overloads)
    			{
    				op.operationValue(initCtxt).setGuard(exp, false);
    			}

    		VdmRuntime.getNodeState(node).hasPermissions  = true;
    		}
    		else if (d instanceof AMutexSyncDefinition)
    		{
    			AMutexSyncDefinition sync = (AMutexSyncDefinition)d;
    			
    			for (LexNameToken opname: new LexNameList(sync.getOperations()))
    			{
    				PExp exp =AMutexSyncDefinitionAssistantInterpreter.getExpression( sync.clone(),opname);
    				ValueList overloads = members.getOverloads(opname);

    				for (Value op: overloads)
    				{
    					op.operationValue(initCtxt).setGuard(exp, true);
    				}
    			}

    			VdmRuntime.getNodeState(node).hasPermissions = true;
    		}
		}
	}

	private static void setStaticValues(SClassDefinition node,
			Context initCtxt)
	{
		if (!VdmRuntime.getNodeState(node).staticValuesInit)
		{
			VdmRuntime.getNodeState(node).staticValuesInit = true;

    		for (SClassDefinition sdef: node.getSuperDefs())
    		{
    			setStaticValues(sdef,initCtxt);
    		}

    		setStaticValues(node,node.getDefinitions(), initCtxt, false);
    		setStaticValues(node,node.getLocalInheritedDefinitions(), initCtxt, true);
		}
	}

	private static void setStaticValues(SClassDefinition node,
			LinkedList<PDefinition> defs, Context initCtxt, boolean inherit)
	{
		for (PDefinition d: defs)
		{
			NameValuePairList nvl = null;

			if (inherit)
			{
				AInheritedDefinition id = (AInheritedDefinition)d;
				LexNameList names =PDefinitionAssistantInterpreter.getVariableNames( d);
				nvl = new NameValuePairList();

				for (LexNameToken vname: names)
				{
					LexNameToken iname = vname.getModifiedName(id.getSuperdef().getName().module);
					Value v = initCtxt.check(iname);

					if (v != null)		// TypeDefinition names aren't values
					{
						nvl.add(vname, v);
					}
				}
			}
			else
			{
				if (PDefinitionAssistantInterpreter.isValueDefinition(d))
				{
					nvl =PDefinitionAssistantInterpreter.getNamedValues(d,initCtxt);
				}
				else if (PDefinitionAssistantInterpreter.isStatic(d) && PDefinitionAssistantInterpreter.isInstanceVariable(d))
				{
					nvl =PDefinitionAssistantInterpreter.getNamedValues(d,initCtxt).getUpdatable(VdmRuntime.getNodeState(node).invlistenerlist);
				}
			}

			if (PDefinitionAssistantInterpreter.isValueDefinition(d))
			{
				// Values are implicitly static, but NOT updatable

				switch (d.getAccess().getAccess().kindPAccess())
				{
					case PRIVATE:
					case PROTECTED:
					VdmRuntime.getNodeState(node).privateStaticValues.putAllNew(nvl);
						initCtxt.putAllNew(nvl);
						break;

					case PUBLIC:
						VdmRuntime.getNodeState(node).publicStaticValues.putAllNew(nvl);
						initCtxt.putAllNew(nvl);
						break;
					
				}
			}
			else if (PDefinitionAssistantInterpreter.isStatic(d) && PDefinitionAssistantInterpreter.isInstanceVariable(d))
			{
				// Static instance variables are updatable

				switch (d.getAccess().getAccess().kindPAccess())
				{
					case PRIVATE:
					case PROTECTED:
						VdmRuntime.getNodeState(node).privateStaticValues.putAllNew(nvl);
						initCtxt.putAllNew(nvl);
						break;

					case PUBLIC:
						VdmRuntime.getNodeState(node).publicStaticValues.putAllNew(nvl);
						initCtxt.putAllNew(nvl);
						break;
				}
			}
		}
	}

	private static void setStaticDefinitions(SClassDefinition node,
			Context initCtxt)
	{
		if (!VdmRuntime.getNodeState(node).staticInit)
		{
			VdmRuntime.getNodeState(node).staticInit = true;

    		for (SClassDefinition sdef: node.getSuperDefs())
    		{
    			setStaticDefinitions(sdef,initCtxt);
    		}

    		VdmRuntime.getNodeState(node).privateStaticValues = new NameValuePairMap();
    		VdmRuntime.getNodeState(node).publicStaticValues = new NameValuePairMap();

    		// We initialize function and operation definitions first as these
    		// can be called by variable initializations.

    		setStaticDefinitions(node,node.getDefinitions(), initCtxt);
    		setStaticDefinitions(node,node.getLocalInheritedDefinitions(), initCtxt);
		}
	}

	private static void setStaticDefinitions(SClassDefinition node,
			LinkedList<PDefinition> defs, Context initCtxt)
	{
		for (PDefinition d: defs)
		{
			if ((PDefinitionAssistantInterpreter.isStatic(d) && PDefinitionAssistantInterpreter.isFunctionOrOperation(d)) ||
					PDefinitionAssistantInterpreter.isTypeDefinition(d))
			{
				// Note function and operation values are not updatable.
				// Type invariants are implicitly static, but not updatable

				// The context here is just used for free variables, of
				// which there are none at static func/op creation...

				Context empty = new Context(node.getLocation(), "empty", null);
				NameValuePairList nvl = PDefinitionAssistantInterpreter.getNamedValues(d,empty);

				switch (d.getAccess().getAccess().kindPAccess())
				{
					case PRIVATE:
					case PROTECTED:
						VdmRuntime.getNodeState(node).privateStaticValues.putAllNew(nvl);
						initCtxt.putList(nvl);
						break;

					case PUBLIC:
						VdmRuntime.getNodeState(node).publicStaticValues.putAllNew(nvl);
						initCtxt.putList(nvl);
						break;
				}
			}
		}
	}

	public static void staticInit(SClassDefinition cdef,
			StateContext ctxt)
	{
		VdmRuntime.getNodeState(cdef).staticInit = false;				// Forced initialization
		VdmRuntime.getNodeState(cdef).staticValuesInit = false;		// Forced initialization

		VdmRuntime.getNodeState(cdef).privateStaticValues = new NameValuePairMap();
		VdmRuntime.getNodeState(cdef).publicStaticValues = new NameValuePairMap();

		setStaticDefinitions(cdef,ctxt);
	}

	public static ProofObligationList getProofObligations(
			SClassDefinition c, POContextStack ctxt)
	{
		return PDefinitionListAssistantInterpreter.getProofObligations(c.getDefinitions(), ctxt);
	}

	public static void staticValuesInit(SClassDefinition cdef,
			StateContext ctxt)
	{
		VdmRuntime.getNodeState(cdef).staticValuesInit = false;		// Forced initialization
		setStaticValues(cdef,ctxt);
	}

	public static boolean hasDelegate(SClassDefinition classdef)
	{
		return VdmRuntime.getNodeState(classdef).hasDelegate();
	}

	public static Object newInstance(SClassDefinition classdef)
	{
		return VdmRuntime.getNodeState(classdef).newInstance();
	}

	public static Value invokeDelegate(SClassDefinition classdef,
			Object delegateObject, Context ctxt)
	{
		return VdmRuntime.getNodeState(classdef).invokeDelegate(delegateObject, ctxt);
	}


	public static PExp findExpression(SClassDefinition d, int lineno)
	{
		return PDefinitionListAssistantInterpreter.findExpression(d.getDefinitions(), lineno);
	}

	public static boolean isTypeDefinition(SClassDefinition def)
	{
		return true;
	}

	public static PStm findStatement(ClassListInterpreter classes, File file,
			int lineno)
	{
		for (SClassDefinition c: classes)
		{
			if (c.getName().location.file.equals(file))
			{
    			PStm stmt = findStatement(c, lineno);

    			if (stmt != null)
    			{
    				return stmt;
    			}
			}
		}

		return null;
	}

	public static PStm findStatement(SClassDefinition c, int lineno)
	{
		return PDefinitionAssistantInterpreter.findStatement(c.getDefinitions(),lineno);
	}

	public static PExp findExpression(ClassListInterpreter classes, File file,
			int lineno)
	{
		for (SClassDefinition c: classes)
		{
			if (c.getName().location.file.equals(file))
			{
    			PExp exp = findExpression(c, lineno);

    			if (exp != null)
    			{
    				return exp;
    			}
			}
		}

		return null;
	}

	public static String getName(SClassDefinition classdef) {
		if (classdef.getName() != null)
		{
			return classdef.getName().name;
		}

		return null;
	}

	

	

}
