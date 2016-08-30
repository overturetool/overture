package org.overture.codegen.runtime;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class StaticSentinel
{

	static Map<String, Long> m;
	static StaticOperationsCounters counter;

	// public StaticSentinel(long fnr)
	// {
	// counter = new StaticOperationsCounters((int)fnr);
	// }

	public synchronized static void entering(String classname, long fnr)
	{
		// int fnr = (int) fnr2; //Here the long value is casted into in to be used as index to the arrays.
		requesting(classname, (int) fnr);// the #req counter is changed to add one to it.
		try
		{
			if (!evalPP(classname, fnr)) // the first evaluation of the permition predicate.
			{
				waiting(classname, (int) fnr, +1);// if the permission predicate is false. It add one to the #waiting
													// counter.
				while (!evalPP(classname, fnr))// reevaluation of the permission predicate.
				{
					Sentinel.class.wait(); // actual thread wait method. This freeze the thread waiting for the
											// execution of its method.
				}
				waiting(classname, (int) fnr, -1); // if predicate changes to true, #waiting is changed to remove one.
			}
		} catch (InterruptedException e)
		{
		}
		activating(classname, (int) fnr);// the #act and the #active counters are change to add one to them
	}

	// this method is registering the termination of a method.
	public synchronized static void leaving(String classname, int fnr2)
	{
		int fn = fnr2;
		m.put(classname, StaticOperationsCounters.fin[fn]++);// fin[fn]++; //changes the #fin counter adding one to it.
		m.put(classname, StaticOperationsCounters.active[fn]--); // changes the #active counter removing one to it.
		stateChanged();
	}

	// this method notifies the threads that a counter has be changed to reevaluate their permission predicates.
	public synchronized static void stateChanged()
	{
		StaticSentinel.class.notifyAll();
	}

	// The method that actually changes the #req history counter.
	private synchronized static void requesting(String classname, int fn)
	{
		m.put(classname, StaticOperationsCounters.req[fn]++);
		stateChanged();
	}

	// The method that actually changing the #act and #active history counters.
	private synchronized static void activating(String classname, int fn)
	{
		m.put(classname, StaticOperationsCounters.act[fn]++);
		m.put(classname, StaticOperationsCounters.active[fn]++);
		stateChanged();
	}

	// The method that actually changing the #waiting history counter.
	// The offset defines how many methods of the same name are waiting.
	private synchronized static void waiting(String classname, int fnr,
			int offset)
	{
		m.put(classname, StaticOperationsCounters.waiting[fnr] += offset);
		stateChanged();
	}

	public static boolean evalPP(String ClassName, Long fnr)
	{
		try
		{
			Class<?> c = Class.forName(ClassName);
			Method m = c.getDeclaredMethod("evaluatePP", Number.class);
			Object o = m.invoke(c.newInstance(), fnr);

			return (Boolean) o;
		} catch (ClassNotFoundException e)
		{
			System.out.println("class not found!!");
		} catch (NoSuchMethodException e)
		{
			System.out.println("method not found!!");
		} catch (SecurityException e)
		{
			System.out.println("alla not found!!");
		} catch (IllegalAccessException e)
		{
			System.out.println("invoke did not work!!");
		} catch (IllegalArgumentException e)
		{
			System.out.println("false arguments!!");
		} catch (InvocationTargetException e)
		{
			System.out.println("invocation target not found!!");
		} catch (InstantiationException e)
		{
			System.out.println("new instance not applied!!");
		}
		return true;
	}
}
