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

package org.overture.interpreter.runtime;

import java.io.PrintWriter;

import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.debug.DBGPReader;
import org.overture.interpreter.values.CPUValue;
import org.overture.interpreter.values.NameValuePair;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.interpreter.values.ObjectValue;
import org.overture.interpreter.values.OperationValue;
import org.overture.interpreter.values.Value;
import org.overture.typechecker.util.LexNameTokenMap;

/**
 * A class to hold runtime name/value context information.
 * 
 * @author Nick, modified by Kenneth Lausdahl
 */

@SuppressWarnings("serial")
public class Context extends LexNameTokenMap<Value>
{
	/** Debug flag used to include extra information in toString */
	public final static boolean DEBUG = true;
	/** The running debug id of contexts */
	private static int nextid = 0;
	/** cache for transient serialization */
	private static final CloneTrancientMemoryCache<ThreadState> serilizationCache = new CloneTrancientMemoryCache<ThreadState>();
	/** The debug if of this context */
	protected final int id;
	/** The Assistant factory used by this context */
	public final IInterpreterAssistantFactory assistantFactory;
	/** The location of the context. */
	public final ILexLocation location;
	/** The name of the location. */
	public final String title;
	/** A link to a lower level context, if present. */
	public final Context outer;
	/** The thread state associated with this context. */
	public transient ThreadState threadState = null;
	/** Serialization key for the transient threadState **/
	private Integer threadStateSerilizationHashedId = null;
	/** Non-zero if this is a pre or postcondition call. */
	public int prepost = 0;
	/** Set to the error message if prepost is set. */
	public String prepostMsg = null;
	/** Set to the operation being guarded, if any. */
	public OperationValue guardOp = null;

	/**
	 * Create a context at the given location.
	 * 
	 * @param af
	 * @param location
	 * @param title
	 * @param outer
	 */

	public Context(IInterpreterAssistantFactory af, ILexLocation location,
			String title, Context outer)
	{
		id = nextid++;
		this.assistantFactory = af;
		this.location = location;
		this.outer = outer;
		this.title = title;

		if (outer != null)
		{
			this.threadState = outer.threadState;
		}
	}

	/**
	 * Set the current thread state. Note this must be called from the thread where the context will run, which may not
	 * be where the thread is created. And it must be called before any context chaining is performed.
	 * 
	 * @param dbgp
	 * @param cpu
	 *            TODO
	 */

	public void setThreadState(DBGPReader dbgp, CPUValue cpu)
	{
		threadState = new ThreadState(dbgp, cpu);
	}

	/**
	 * Find the outermost context from this one.
	 * 
	 * @return The outermost context.
	 */

	public Context getGlobal()
	{
		Context op = this;

		while (op.outer != null)
		{
			op = op.outer;
		}

		return op;
	}

	/**
	 * Find the nearest RootContext in the context chain.
	 * 
	 * @return
	 */

	public RootContext getRoot()
	{
		assert outer != null : "Root context is wrong type";
		return outer.getRoot(); // RootContext overrides this!
	}

	/**
	 * Make a deep copy of the context, using Value.deepCopy. Every concrete subclass must implements its own version of
	 * this method.
	 * 
	 * @return
	 */

	public Context deepCopy()
	{
		Context below = null;

		if (outer != null)
		{
			below = outer.deepCopy();
		}

		Context result = new Context(assistantFactory, location, title, below);
		result.threadState = threadState;

		for (ILexNameToken var : keySet())
		{
			Value v = get(var);
			result.put(var, v.deepCopy());
		}

		return result;
	}

	/**
	 * Add a list of name/value pairs to this context.
	 * 
	 * @param nvl
	 *            A list of name/value pairs.
	 */

	public void putList(NameValuePairList nvl)
	{
		for (NameValuePair nv : nvl)
		{
			put(nv.name, nv.value);
		}
	}

	public void putNew(NameValuePair nvp)
	{
		if (get(nvp.name) == null)
		{
			put(nvp.name, nvp.value);
		}
	}

	public void putAllNew(NameValuePairList list)
	{
		for (NameValuePair nvp : list)
		{
			putNew(nvp);
		}
	}

	/**
	 * Get a name, taking type overloading into account. If we use the superclass method, different names are considered
	 * different, because the map is driven by the names' hashCodes. The equals method of LexNameToken makes a
	 * TypeComparator check, which is what we need. But we try a simple super.get() first. TODO Slow though.
	 */

	@Override
	public Value get(Object name)
	{
		Value rv = super.get(name);

		if (rv == null)
		{
			for (ILexNameToken var : keySet())
			{
				if (assistantFactory.getLexNameTokenAssistant().isEqual(var, name))
				{
					rv = super.get(var);
					break;
				}
			}
		}

		return rv;
	}

	/**
	 * Get all visible names from this Context, with more visible values overriding those below.
	 * 
	 * @return A new Context with all visible names.
	 */

	public Context getVisibleVariables()
	{
		Context visible = new Context(assistantFactory, location, title, null);

		if (outer != null)
		{
			visible.putAll(outer.getVisibleVariables());
		}

		visible.putAll(this); // Overriding anything below here
		return visible;
	}

	/**
	 * Get the value for a given name. This searches outer contexts, if any are present.
	 * 
	 * @param name
	 *            The name to look for.
	 * @return The value of the name, or null.
	 */

	public Value check(ILexNameToken name)
	{
		Value v = get(name);

		if (v == null)
		{
			if (outer != null)
			{
				return outer.check(name);
			}
		}

		return v;
	}

	/**
	 * Locate the Context in a chain that contains a name, if any.
	 * 
	 * @param name
	 * @return
	 */

	public Context locate(ILexNameToken name)
	{
		Value v = get(name);

		if (v == null)
		{
			if (outer != null)
			{
				return outer.locate(name);
			} else
			{
				return null;
			}
		} else
		{
			return this;
		}
	}

	/**
	 * Return the value of a name, else fail. If the name is not present, a {@link ContextException} is thrown.
	 * 
	 * @param name
	 *            The name to look for.
	 * @return The value of the name.
	 */

	public Value lookup(ILexNameToken name)
	{
		Value v = check(name);

		if (v == null)
		{
			VdmRuntimeError.abort(name.getLocation(), 4034, "Name '" + name
					+ "' not in scope", this);
		}

		return v;
	}

	@Override
	public String toString()
	{
		return (DEBUG ? "#" + id + " " : "") + format("", this)
				+ "-------------------\n" + (outer == null ? "" : outer.toString());
	}

	protected String format(String indent, Context what)
	{
		StringBuilder sb = new StringBuilder();

		for (ILexNameToken name : what.keySet())
		{
			sb.append(indent + name + " = " + what.get(name).toShortString(100)
					+ "\n");
		}

		return sb.toString();
	}

	/**
	 * This is used by the stack overflow processing in Function/OperationValue.
	 * It is intended to print the frame titles without recursing.
	 * @param out
	 */
	
	private static final int FRAMES_LIMIT = 100;
	
	public void printStackFrames(PrintWriter out)
	{
		Context frame = this;
		out.print(format("\t", frame));
		int count = 0;

		while (frame.outer != null)
		{
			if (++count < FRAMES_LIMIT)
			{
				out.println("In context of " + frame.title + " " + frame.location);
			}
			else if (count == FRAMES_LIMIT)
			{
				out.println("...");
			}
			
			frame = frame.outer;	// NB. DON'T RECURSE!
		}

		out.println("In context of " + frame.title);
	}

	public void printStackTrace(PrintWriter out, boolean variables)
	{
		if (outer == null) // Don't expand initial context
		{
			out.println("In context of " + title);
		} else
		{
			if (variables)
			{
				out.print(this.format("\t", this));
			}

			out.println("In context of " + title + " " + location);
			outer.printStackTrace(out, variables);
		}
	}

	public int getDepth()
	{
		return outer == null ? 0 : outer.getDepth(); // NB only roots count
	}

	public Context getFrame(int depth)
	{
		return outer == null ? this : outer.getFrame(depth);
	}

	public ObjectValue getSelf()
	{
		return outer == null ? null : outer.getSelf();
	}

	public void setPrepost(int prepost, String prepostMsg)
	{
		this.prepost = prepost;
		this.prepostMsg = prepostMsg;
	}

	/**
	 * Custom serialization method handling the transient values
	 * 
	 * @param stream
	 * @throws java.io.IOException
	 */
	private synchronized void writeObject(java.io.ObjectOutputStream stream)
			throws java.io.IOException
	{
		ThreadState tmp = this.threadState;
		this.threadStateSerilizationHashedId = serilizationCache.store(this.threadState);
		this.threadState = null;
		stream.defaultWriteObject();
		this.threadState = tmp;
	}

	/**
	 * Custom de-serialization method handling the transient values
	 * 
	 * @param stream
	 * @throws java.io.IOException
	 * @throws ClassNotFoundException
	 */
	private synchronized void readObject(java.io.ObjectInputStream stream)
			throws java.io.IOException, ClassNotFoundException
	{
		stream.defaultReadObject();
		this.threadState = (ThreadState) serilizationCache.load(this.threadStateSerilizationHashedId);
		this.threadStateSerilizationHashedId = null;
	}
}
