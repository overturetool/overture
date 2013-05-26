/*******************************************************************************
 * Copyright (c) 2013 Overture Team.
 *
 * Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The Overture Tool web-site: http://overturetool.org/
 *******************************************************************************/
package org.overture.ast.assistant;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * <b>Note!</b> The tests for this class have not yet been written, and the
 * methods herein should not be considered entirely reliable. Especially
 * {@link #invokeNarrowestMethod(Object, String, Class, Object...)
 * invokeNarrowestMethod()}.
 * <p>
 * The InvocationAssistant class contains some static methods that allow the
 * user to invoke a method on a type that is "closer" to the type than Java's
 * references and polymorphism would normally allow. Essentially this provides
 * "narrowing" or "downcasting" functionality on a general type.
 * <p>
 * It is recommended that the
 * {@link #invokePreciseMethod(Object, String, Object...) invokePreciseMethod()}
 * be used in most cases, as the
 * {@link #invokeNarrowestMethod(Object, String, Class, Object...)
 * invokeNarrowestMethod()} may give unexpected behaviour (especially in the
 * presence of objects that implement multiple distinct interfaces).
 * <p>
 * The standard usage pattern is via an {@code import static} of the required
 * invocation method.
 * 
 * @author Joey Coleman (jwc@iha.dk)
 */
public final class InvocationAssistant {

	/**
	 * This method uses reflection to invoke the method that precisely matches
	 * the number of parameters and type of the first parameter.
	 * <p>
	 * Unfortunately, Java does not have a mechanism to do automatic
	 * downcasting/narrowing of parameter references during method invocation.
	 * For example, in code such as the following
	 * 
	 * <pre>
	 * abstract class Test {...}
	 * class TestA extends Test {...}
	 * class Main {
	 *   public static String getSomeString(TestA a) {
	 *     return "got some string from a TestA class";
	 *   }
	 *   public static void main(String[] argv) {
	 *     TestA a = new TestA();
	 *     Test  t = new TestA();
	 *     getSomeString(a); // This is fine,
	 *     getSomeString(t); // but this is not
	 *   }
	 * }
	 * </pre>
	 * 
	 * the first call to {@code getSomeString()} in {@code main} will be fine as
	 * it is able to match the reference type of {@code a} (i.e. {@code TestA})
	 * to the type of {@code getSomeString(TestA)}. However, the second call to
	 * {@code getSomeString()} will fail, as the reference type of {@code t} is
	 * the class {@code Test}, not the actual type of the object.
	 * <p>
	 * So, we would add the following method to the {@code Main} class:
	 * 
	 * <pre>
	 * public static String getSomeString(Test t) throws InvocationAssistantException {
	 * 	return (String) invokePreciseMethod(new Main(), &quot;getSomeString&quot;, t);
	 * }
	 * </pre>
	 * 
	 * at which point the second call to {@code getSomeString()} will succeed,
	 * calling the specific {@code getSomeString(TestA)} by way of the
	 * {@link #invokePreciseMethod(Object, String, Object...)
	 * invokePreciseMethod()} call.
	 * <p>
	 * Note that the desired target method must be public, otherwise the
	 * invokePreciseMethod will not be able to find it. Also, note the cast of
	 * the return value of
	 * {@link #invokePreciseMethod(Object, String, Object...)
	 * invokePreciseMethod()}.
	 * 
	 * @param target
	 *            The object on which the named method will be called.
	 * @param name
	 *            The name of the method to be called.
	 * @param parameters
	 *            The parameters to be passed to the method to be called. There
	 *            must be at least one parameter, though there may be more. The
	 *            type of the first parameter must be an exact match of the
	 *            first parameter of the desired method.
	 * @return The return value must be cast from Object to the correct return
	 *         type.
	 * @throws InvocationAssistantExternalException
	 * @throws InvocationAssistantNotFoundException
	 */
	public static final Object invokePreciseMethod(Object target, String name,
			Object... parameters) throws InvocationAssistantExternalException,
			InvocationAssistantNotFoundException {
		if (parameters.length == 0)
			throw new InvocationAssistantNotFoundException(
					"invokePreciseMethod must be called with at least one parameter to give to the invoked method");
		Method method = null;
		Class<?> pClass = parameters[0].getClass();

		for (Method m : target.getClass().getMethods()) {
			if (!m.getName().equals(name))
				continue;

			Class<?>[] params = m.getParameterTypes();

			if (params.length != parameters.length)
				continue;

			/*
			 * So, pClass.isAssignableFrom(params[0]), if true, means that
			 * params[0] must have a subtype relationship to pClass (via
			 * inheritance or interfaces).
			 * 
			 * This particular if checks that pClass and params[0] are
			 * equivalent, meaning that we only want the method that is a
			 * *precise* match.
			 */
			if (pClass.isAssignableFrom(params[0])
					&& params[0].isAssignableFrom(pClass)) {
				method = m;
				break;
			}
		}

		try {
			if (method != null) {
				return method.invoke(target, parameters);
			} else {
				String errorString = "Attempt to invoke method " + name
						+ " in " + target.getClass().getName()
						+ " did not find method with " + parameters.length
						+ " parameters and first parameter of type "
						+ pClass.getName();
				throw new InvocationAssistantNotFoundException(errorString);
			}
		} catch (IllegalAccessException e) {
			throw new InvocationAssistantExternalException(
					"IllegalAccessException on attempt to invoke "
							+ method.getName(), e);
		} catch (InvocationTargetException e) {
			throw new InvocationAssistantExternalException(e);
		}
	}

	/**
	 * This method works essentially the same as
	 * {@link #invokePreciseMethod(Object, String, Object...)
	 * invokePreciseMethod()}, except that instead of searching for a method
	 * that exactly matches the type of the first element of {@code parameters},
	 * it searches for a method that matches the type as narrowly as possible,
	 * but is more specific than the type given by {@code bound}.
	 * <p>
	 * Modifying the example from
	 * {@link #invokePreciseMethod(Object, String, Object...)
	 * invokePreciseMethod()}, consider:
	 * 
	 * <pre>
	 * abstract class Test {...}
	 * class TestA extends Test {...}
	 * class TestB extends TestA {...}
	 * class Main {
	 *   public static String getSomeString(Test t) throws InvocationAssistantException {
	 *     return (String) invokeNarrowestMethod(new Main(), &quot;getSomeString&quot;, Test.class, t);
	 *   }
	 *   public static String getSomeString(TestA a) {
	 *     return "got some string from a TestA class";
	 *   }
	 *   public static void main(String[] argv) {
	 *     Test t = new TestB();
	 *     getSomeString(t);
	 *   }
	 * }
	 * </pre>
	 * 
	 * In this example, the call to {@code getSomeString()} will use
	 * {@link #invokeNarrowestMethod(Object, String, Class, Object...)
	 * invokeNarrowestMethod()} to ultimately call {@code getSomeString(TestA)}.
	 * If a {@code getSomeString(TestB)} method existed, it would be chosen in
	 * stead of the {@code TestA} version.
	 * 
	 * @param target
	 *            The object on which the named method will be called.
	 * @param name
	 *            The name of the method to be called.
	 * @param bound
	 *            A class that is an immediate superclass/interface of the
	 *            widest acceptable target for invocation.
	 * @param parameters
	 *            The parameters to be passed to the method to be called. There
	 *            must be at least one parameter, though there may be more. The
	 *            type of the first parameter must be an exact match of the
	 *            first parameter of the desired method.
	 * @return The return value must be cast from Object to the correct return
	 *         type.
	 * @throws InvocationAssistantExternalException
	 * @throws InvocationAssistantNotFoundException
	 * @see #invokePreciseMethod(Object, String, Object...)
	 *      invokePreciseMethod()
	 */
	public static final Object invokeNarrowestMethod(Object target,
			String name, Class<?> bound, Object... parameters)
			throws InvocationAssistantExternalException,
			InvocationAssistantNotFoundException {
		if (parameters.length == 0)
			throw new InvocationAssistantNotFoundException(
					"called without any target parameters to give to the invoked method");

		Method method = null;
		Class<?> pClass = parameters[0].getClass();

		if (pClass.isAssignableFrom(bound))
			throw new InvocationAssistantNotFoundException(
					"called with a first target parameter type that is the same or more general than the bound");

		for (Method m : target.getClass().getMethods()) {
			if (!m.getName().equals(name))
				continue;

			Class<?>[] params = m.getParameterTypes();

			if (params.length != parameters.length)
				continue;

			/*
			 * FIXME Document this
			 */
			if (params[0].isAssignableFrom(pClass) // candidate will accept our
													// first object
					&& !params[0].isAssignableFrom(bound)) { // candidate is a
																// strict
																// subtype of
																// the bound
				if (method == null // we have no prior candidate
						|| method.getParameterTypes()[0]
								.isAssignableFrom(params[0])) { // candidate is
																// a non-strict
																// subtype of
																// prior
																// candidate
					method = m;
					break;
				}
			}
		}

		try {
			if (method != null) {
				return method.invoke(target, parameters);
			} else {
				String errorString = "Attempt to invoke method " + name
						+ " in " + target.getClass().getName()
						+ " did not find method with " + parameters.length
						+ " parameters and first parameter of type "
						+ pClass.getName();
				throw new InvocationAssistantNotFoundException(errorString);
			}
		} catch (IllegalAccessException e) {
			throw new InvocationAssistantExternalException(
					"IllegalAccessException on attempt to invoke "
							+ method.getName(), e);
		} catch (InvocationTargetException e) {
			throw new InvocationAssistantExternalException(e);
		}
	}
}
