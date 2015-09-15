package org.overture.interpreter.assistant.definition;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.assistant.IAstAssistant;
import org.overture.ast.assistant.pattern.PTypeList;
import org.overture.ast.definitions.ABusClassDefinition;
import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.AClassInvariantDefinition;
import org.overture.ast.definitions.ACpuClassDefinition;
import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.ast.definitions.AMutexSyncDefinition;
import org.overture.ast.definitions.APerSyncDefinition;
import org.overture.ast.definitions.APrivateAccess;
import org.overture.ast.definitions.AProtectedAccess;
import org.overture.ast.definitions.APublicAccess;
import org.overture.ast.definitions.ASystemClassDefinition;
import org.overture.ast.definitions.PAccess;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.Dialect;
import org.overture.ast.lex.LexIntegerToken;
import org.overture.ast.lex.LexKeywordToken;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.lex.LexToken;
import org.overture.ast.lex.VDMToken;
import org.overture.ast.statements.PStm;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.AClassType;
import org.overture.config.Settings;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.runtime.StateContext;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.runtime.VdmRuntime;
import org.overture.interpreter.runtime.VdmRuntimeError;
import org.overture.interpreter.util.ClassListInterpreter;
import org.overture.interpreter.values.BUSValue;
import org.overture.interpreter.values.CPUValue;
import org.overture.interpreter.values.ClassInvariantListener;
import org.overture.interpreter.values.MapValue;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.interpreter.values.NameValuePairMap;
import org.overture.interpreter.values.ObjectValue;
import org.overture.interpreter.values.OperationValue;
import org.overture.interpreter.values.SeqValue;
import org.overture.interpreter.values.UpdatableValue;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.ValueList;
import org.overture.interpreter.values.ValueMap;
import org.overture.pog.contexts.POContextStack;
import org.overture.pog.obligation.ProofObligationList;
import org.overture.typechecker.assistant.definition.SClassDefinitionAssistantTC;

public class SClassDefinitionAssistantInterpreter extends
		SClassDefinitionAssistantTC implements IAstAssistant
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public SClassDefinitionAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		super(af);
		this.af = af;
	}

	public Value getStatic(SClassDefinition classdef, ILexNameToken sought)
	{
		ILexNameToken local = sought.getExplicit() ? sought
				: sought.getModifiedName(classdef.getName().getName());

		Value v = VdmRuntime.getNodeState(af, classdef).privateStaticValues.get(local);

		if (v == null)
		{
			v = VdmRuntime.getNodeState(af, classdef).publicStaticValues.get(local);

			if (v == null)
			{
				for (SClassDefinition sdef : classdef.getSuperDefs())
				{
					v = getStatic(sdef, local);

					if (v != null)
					{
						break;
					}
				}
			}
		}

		return v;
	}

	public Context getStatics(SClassDefinition classdef)
	{
		Context ctxt = new Context(af, classdef.getLocation(), "Statics", null);
		ctxt.putAll(VdmRuntime.getNodeState(af, classdef).publicStaticValues);
		ctxt.putAll(VdmRuntime.getNodeState(af, classdef).privateStaticValues);
		return ctxt;
	}

	public MapValue getOldValues(SClassDefinition classdef, LexNameList oldnames)
	{
		ValueMap values = new ValueMap();

		for (ILexNameToken name : oldnames)
		{
			Value mv = getStatic(classdef, name.getNewName()).deref();
			SeqValue sname = new SeqValue(name.getName());

			if (mv instanceof ObjectValue)
			{
				ObjectValue om = (ObjectValue) mv;
				values.put(sname, om.deepCopy());
			} else
			{
				values.put(sname, (Value) mv.clone());
			}
		}

		return new MapValue(values);
	}

	// TODO:rewrite this???
	public ObjectValue newInstance(SClassDefinition node,
			PDefinition ctorDefinition, ValueList argvals, Context ctxt)
			throws AnalysisException
	{
		if (node instanceof ABusClassDefinition)
		{
			return newInstance((ABusClassDefinition) node, ctorDefinition, argvals, ctxt);
		} else if (node instanceof AClassClassDefinition)
		{
			if (node.getIsAbstract())
			{
				VdmRuntimeError.abort(node.getLocation(), 4000, "Cannot instantiate abstract class "
						+ node.getName(), ctxt);
			}

			return af.createSClassDefinitionAssistant().makeNewInstance(node, ctorDefinition, argvals, ctxt, new HashMap<ILexNameToken, ObjectValue>(), false);
			//return af.createAClassClassDefinitionAssistant().newInstance((AClassClassDefinition) node, ctorDefinition, argvals, ctxt);
		} else if (node instanceof ACpuClassDefinition)
		{
			return af.createACpuClassDefinitionAssistant().newInstance((ACpuClassDefinition) node, ctorDefinition, argvals, ctxt);
		} else if (node instanceof ASystemClassDefinition)
		{
			VdmRuntimeError.abort(node.getLocation(), 4135, "Cannot instantiate system class "
					+ node.getName(), ctxt);
		}

		return null;
	}

	public ObjectValue makeNewInstance(SClassDefinition node,
			PDefinition ctorDefinition, ValueList argvals, Context ctxt,

			Map<ILexNameToken, ObjectValue> done, boolean nested)
			throws AnalysisException

	{
		setStaticDefinitions(node, ctxt.getGlobal()); // When static member := new X()
		setStaticValues(node, ctxt.getGlobal()); // When static member := new X()

		List<ObjectValue> inherited = new Vector<ObjectValue>();
		NameValuePairMap members = new NameValuePairMap();

		for (SClassDefinition sdef : node.getSuperDefs())
		{
			// Check the "done" list for virtual inheritance
			ObjectValue obj = done.get(sdef.getName());

			if (obj == null)
			{
				obj = makeNewInstance(sdef, null, null, ctxt, done, true);
				done.put(sdef.getName(), obj);
			}

			inherited.add(obj);
		}

		// NB. we don't use localInheritedDefinitions because we're creating
		// the local definitions in this loop.

		for (PDefinition idef : node.getSuperInheritedDefinitions())
		{
			// Inherited definitions don't notice when their referenced
			// definition names are updated with type qualifiers.
			// FIXME This ought to be done at the end of type checking...

			if (idef instanceof AInheritedDefinition)
			{
				AInheritedDefinition i = (AInheritedDefinition) idef;
				i.getName().setTypeQualifier(i.getSuperdef().getName().getTypeQualifier());
			}

			if (af.createPDefinitionAssistant().isRuntime(idef)) // eg. TypeDefinitions aren't
			{
				Value v = null;

				for (ObjectValue sobj : inherited)
				{
					v = sobj.get(idef.getName(), true);

					if (v != null)
					{
						ILexNameToken localname = idef.getName().getModifiedName(node.getName().getName());

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
					VdmRuntimeError.abort(node.getLocation(), 6, "Constructor for "
							+ node.getName().getName()
							+ " can't find "
							+ idef.getName(), ctxt);
				}
			}
		}

		members.putAll(VdmRuntime.getNodeState(af, node).publicStaticValues);
		members.putAll(VdmRuntime.getNodeState(af, node).privateStaticValues);

		// We create a RootContext here so that the scope for member
		// initializations are restricted.

		Context initCtxt = new StateContext(af, node.getLocation(), "field initializers", ctxt, null);
		initCtxt.putList(members.asList());

		// We create an empty context to pass for function creation, so that
		// there are no spurious free variables created.

		Context empty = new Context(af, node.getLocation(), "empty", null);

		for (PDefinition d : node.getDefinitions())
		{
			if (!af.createPDefinitionAssistant().isStatic(d)
					&& af.createPDefinitionAssistant().isFunctionOrOperation(d))
			{
				NameValuePairList nvpl = af.createPDefinitionAssistant().getNamedValues(d, empty);
				initCtxt.putList(nvpl);
				members.putAll(nvpl);
			}
		}

		for (PDefinition d : node.getDefinitions())
		{
			if (!af.createPDefinitionAssistant().isStatic(d)
					&& !af.createPDefinitionAssistant().isFunctionOrOperation(d))
			{
				NameValuePairList nvpl = af.createPDefinitionAssistant().getNamedValues(d, initCtxt).getUpdatable(null);

				initCtxt.putList(nvpl);
				members.putAll(nvpl);
			}
		}

		setPermissions(node, node.getDefinitions(), members, initCtxt);
		setPermissions(node, node.getSuperInheritedDefinitions(), members, initCtxt);

		ObjectValue creator = ctxt.outer == null ? null : ctxt.outer.getSelf();

		ObjectValue object = new ObjectValue((AClassType) af.createSClassDefinitionAssistant().getType(node), members, inherited, ctxt.threadState.CPU, creator);

		Value ctor = null;

		if (ctorDefinition == null)
		{
			argvals = new ValueList();
			LexNameToken constructor = getCtorName(node, new PTypeList());
			ctorDefinition = af.createPDefinitionAssistant().findName(node, constructor, NameScope.NAMES);

			if (ctorDefinition != null)
			{
				ctor = object.get(ctorDefinition.getName(), false);
			}
		} else
		{
			ctor = object.get(ctorDefinition.getName(), false);
		}

		if (Settings.dialect == Dialect.VDM_RT)
		{
			CPUValue cpu = object.getCPU();

			if (cpu != null)
			{
				cpu.deploy(object);
			}
		}

		if (ctor != null) // Class may have no constructor defined
		{
			OperationValue ov = ctor.operationValue(ctxt);

			ObjectContext ctorCtxt = new ObjectContext(af, ov.name.getLocation(), node.getName().getName()
					+ " constructor", ctxt, object);

			if (ctorDefinition.getAccess().getAccess() instanceof APrivateAccess
					&& nested)
			{
				VdmRuntimeError.abort(ctorDefinition.getLocation(), 4163, "Cannot inherit private constructor", ctorCtxt);
			}

			ov.eval(ov.name.getLocation(), argvals, ctorCtxt);
		}

		// Do invariants and guards after construction, so values fields are set. The
		// invariant does not apply during construction anyway.

		if (node.getInvariant() != null)
		{

			OperationValue invop = new OperationValue(node.getInvariant(), null, null, null, af);
			ClassInvariantListener listener = new ClassInvariantListener(invop);

			for (PDefinition d : af.createSClassDefinitionAssistant().getInvDefs(node))
			{
				AClassInvariantDefinition inv = (AClassInvariantDefinition) d;

				// Is this correct?
				ValueList values = af.createPExpAssistant().getValues(inv.getExpression(), new ObjectContext(af, node.getLocation(), node.getName().getName()
						+ " object context", initCtxt, object));
				for (Value v : values)
				{
					UpdatableValue uv = (UpdatableValue) v;
					uv.addListener(listener);
				}
			}

			object.setListener(listener);
		}

		if (VdmRuntime.getNodeState(af, node).hasPermissions)
		{
			ObjectContext self = new ObjectContext(af, node.getLocation(), node.getName().getName()
					+ " guards", ctxt, object);

			for (Entry<ILexNameToken, Value> entry : members.entrySet())
			{
				Value v = entry.getValue();

				if (v instanceof OperationValue)
				{
					OperationValue opv = (OperationValue) v;
					opv.prepareGuard(self);
				}
			}
		}

		return object;
	}

	private void setPermissions(SClassDefinition node,
			LinkedList<PDefinition> definitions, NameValuePairMap members,
			Context initCtxt) throws ValueException
	{
		for (PDefinition d : node.getDefinitions())
		{
			while (d instanceof AInheritedDefinition)
			{
				d = ((AInheritedDefinition) d).getSuperdef();
			}

			if (d instanceof APerSyncDefinition)
			{
				APerSyncDefinition sync = (APerSyncDefinition) d;
				ValueList overloads = members.getOverloads(sync.getOpname());
				PExp exp = sync.getGuard();

				for (Value op : overloads)
				{
					op.operationValue(initCtxt).setGuard(exp, false);
				}

				VdmRuntime.getNodeState(af, node).hasPermissions = true;
			} else if (d instanceof AMutexSyncDefinition)
			{
				AMutexSyncDefinition sync = (AMutexSyncDefinition) d;

				for (ILexNameToken opname : new LexNameList(sync.getOperations()))
				{
					PExp exp = getExpression(sync.clone(), opname);
					ValueList overloads = members.getOverloads(opname);

					for (Value op : overloads)
					{
						op.operationValue(initCtxt).setGuard(exp, true);
					}
				}

				VdmRuntime.getNodeState(af, node).hasPermissions = true;
			}
		}
	}

	private void setStaticValues(SClassDefinition node, Context initCtxt)
	{
		if (!VdmRuntime.getNodeState(af, node).staticValuesInit)
		{
			VdmRuntime.getNodeState(af, node).staticValuesInit = true;

			for (SClassDefinition sdef : node.getSuperDefs())
			{
				setStaticValues(sdef, initCtxt);
			}

			setStaticValues(node, node.getLocalInheritedDefinitions(), initCtxt, true);
			setStaticValues(node, node.getDefinitions(), initCtxt, false);
		}
	}

	private void setStaticValues(SClassDefinition node,
			LinkedList<PDefinition> defs, Context initCtxt, boolean inherit)
	{
		for (PDefinition d : defs)
		{
			NameValuePairList nvl = null;

			if (inherit)
			{
				AInheritedDefinition id = (AInheritedDefinition) d;
				LexNameList names = af.createPDefinitionAssistant().getVariableNames(d);
				nvl = new NameValuePairList();

				for (ILexNameToken vname : names)
				{
					ILexNameToken iname = vname.getModifiedName(id.getSuperdef().getName().getModule());
					Value v = initCtxt.check(iname);

					if (v != null) // TypeDefinition names aren't values
					{
						nvl.add(vname, v);
					}
				}
			} else
			{
				if (af.createPDefinitionAssistant().isValueDefinition(d))
				{
					nvl = af.createPDefinitionAssistant().getNamedValues(d, initCtxt);
				} else if (af.createPDefinitionAssistant().isStatic(d)
						&& af.createPDefinitionAssistant().isInstanceVariable(d))
				{
					nvl = af.createPDefinitionAssistant().getNamedValues(d, initCtxt);
				}
			}

			if (af.createPDefinitionAssistant().isValueDefinition(d))
			{
				// Values are implicitly static, but NOT updatable

				PAccess pAccess = d.getAccess().getAccess();
				if (pAccess instanceof APrivateAccess
						|| pAccess instanceof AProtectedAccess)
				{
					VdmRuntime.getNodeState(af, node).privateStaticValues.putAllNew(nvl);
					initCtxt.putAllNew(nvl);
				} else if (pAccess instanceof APublicAccess)
				{
					VdmRuntime.getNodeState(af, node).publicStaticValues.putAllNew(nvl);
					initCtxt.putAllNew(nvl);
				}
			} else if (af.createPDefinitionAssistant().isStatic(d)
					&& af.createPDefinitionAssistant().isInstanceVariable(d))
			{
				// Static instance variables are updatable

				PAccess pAccess = d.getAccess().getAccess();
				if (pAccess instanceof APrivateAccess
						|| pAccess instanceof AProtectedAccess)
				{
					VdmRuntime.getNodeState(af, node).privateStaticValues.putAllNew(nvl);
					initCtxt.putAllNew(nvl);
				} else if (pAccess instanceof APublicAccess)
				{
					VdmRuntime.getNodeState(af, node).publicStaticValues.putAllNew(nvl);
					initCtxt.putAllNew(nvl);
				}
			}
		}
	}

	private void setStaticDefinitions(SClassDefinition node, Context initCtxt)
	{
		if (!VdmRuntime.getNodeState(af, node).staticInit)
		{
			VdmRuntime.getNodeState(af, node).staticInit = true;

			for (SClassDefinition sdef : node.getSuperDefs())
			{
				setStaticDefinitions(sdef, initCtxt);
			}

			VdmRuntime.getNodeState(af, node).privateStaticValues = new NameValuePairMap();
			VdmRuntime.getNodeState(af, node).publicStaticValues = new NameValuePairMap();

			// We initialize function and operation definitions first as these
			// can be called by variable initializations.

			setStaticDefinitions(node, node.getDefinitions(), initCtxt);
			setStaticDefinitions(node, node.getLocalInheritedDefinitions(), initCtxt);

			try
			{
				NameValuePairMap members = new NameValuePairMap();
				members.putAll(VdmRuntime.getNodeState(af, node).privateStaticValues);
				members.putAll(VdmRuntime.getNodeState(af, node).publicStaticValues);
				af.createSClassDefinitionAssistant().setPermissions(node, node.getDefinitions(), members, initCtxt);
			} catch (ValueException e)
			{
				VdmRuntimeError.abort(node.getLocation(), e);
			}
		}
	}

	private void setStaticDefinitions(SClassDefinition node,
			LinkedList<PDefinition> defs, Context initCtxt)
	{
		for (PDefinition d : defs)
		{
			if (af.createPDefinitionAssistant().isStatic(d)
					&& af.createPDefinitionAssistant().isFunctionOrOperation(d)
					|| af.createPDefinitionAssistant().isTypeDefinition(d))
			{
				// Note function and operation values are not updatable.
				// Type invariants are implicitly static, but not updatable

				// The context here is just used for free variables, of
				// which there are none at static func/op creation...

				Context empty = new Context(af, node.getLocation(), "empty", null);
				NameValuePairList nvl = af.createPDefinitionAssistant().getNamedValues(d, empty);

				PAccess pAccess = d.getAccess().getAccess();
				if (pAccess instanceof APrivateAccess
						|| pAccess instanceof AProtectedAccess)
				{
					VdmRuntime.getNodeState(af, node).privateStaticValues.putAllNew(nvl);
					initCtxt.putList(nvl);
				} else if (pAccess instanceof APublicAccess)
				{
					VdmRuntime.getNodeState(af, node).publicStaticValues.putAllNew(nvl);
					initCtxt.putList(nvl);
				}
			}
		}
	}

	public void staticInit(SClassDefinition cdef, StateContext ctxt)
	{
		VdmRuntime.getNodeState(af, cdef).staticInit = false; // Forced initialization
		VdmRuntime.getNodeState(af, cdef).staticValuesInit = false; // Forced initialization

		VdmRuntime.getNodeState(af, cdef).privateStaticValues = new NameValuePairMap();
		VdmRuntime.getNodeState(af, cdef).publicStaticValues = new NameValuePairMap();

		setStaticDefinitions(cdef, ctxt);
	}

	public ProofObligationList getProofObligations(SClassDefinition c,
			POContextStack ctxt)
	{
		return af.createPDefinitionListAssistant().getProofObligations(c.getDefinitions(), ctxt);
	}

	public void staticValuesInit(SClassDefinition cdef, StateContext ctxt)
	{
		VdmRuntime.getNodeState(af, cdef).staticValuesInit = false; // Forced initialization
		setStaticValues(cdef, ctxt);
	}

	public boolean hasDelegate(SClassDefinition classdef)
	{
		return VdmRuntime.getNodeState(af, classdef).hasDelegate();
	}

	public Object newInstance(SClassDefinition classdef)
	{
		return VdmRuntime.getNodeState(af, classdef).newInstance();
	}

	public Value invokeDelegate(SClassDefinition classdef,
			Object delegateObject, Context ctxt)
	{
		return VdmRuntime.getNodeState(af, classdef).invokeDelegate(delegateObject, ctxt);
	}

	public PExp findExpression(SClassDefinition d, int lineno)
	{
		return af.createPDefinitionListAssistant().findExpression(d.getDefinitions(), lineno);
	}

	// public static boolean isTypeDefinition(SClassDefinition def)
	// {
	// return true;
	// }

	public PStm findStatement(ClassListInterpreter classes, File file,
			int lineno)
	{
		for (SClassDefinition c : classes)
		{
			if (c.getName().getLocation().getFile().equals(file))
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

	public PStm findStatement(SClassDefinition c, int lineno)
	{
		return af.createPDefinitionAssistant().findStatement(c.getDefinitions(), lineno);
	}

	public PExp findExpression(ClassListInterpreter classes, File file,
			int lineno)
	{
		for (SClassDefinition c : classes)
		{
			if (c.getName().getLocation().getFile().equals(file))
			{
				PExp exp = af.createSClassDefinitionAssistant().findExpression(c, lineno);

				if (exp != null)
				{
					return exp;
				}
			}
		}

		return null;
	}
	
	public ObjectValue newInstance(ABusClassDefinition node,
			PDefinition ctorDefinition, ValueList argvals, Context ctxt)
	{
		NameValuePairList nvpl = af.createPDefinitionListAssistant().getNamedValues(node.getDefinitions(), ctxt);
		NameValuePairMap map = new NameValuePairMap();
		map.putAll(nvpl);

		return new BUSValue((AClassType) node.getClasstype(), map, argvals);
	}
	
	public PExp getExpression(AMutexSyncDefinition sync, ILexNameToken excluding)
	{
		LexNameList list = null;

		if (sync.getOperations().size() == 1)
		{
			list = new LexNameList();
			list.addAll(sync.getOperations());
		} else
		{
			list = new LexNameList();
			list.addAll(sync.getOperations());
			list.remove(excluding);
		}

		return AstFactory.newAEqualsBinaryExp(AstFactory.newAHistoryExp(sync.getLocation(), new LexToken(sync.getLocation(), VDMToken.ACTIVE), list), new LexKeywordToken(VDMToken.EQUALS, sync.getLocation()), AstFactory.newAIntLiteralExp(new LexIntegerToken(0, sync.getLocation())));
	}

}
