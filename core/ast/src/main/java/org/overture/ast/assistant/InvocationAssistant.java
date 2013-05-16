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
 * The InvocationAssistant class contains some static methods that allow the
 * user to invoke a method on a type that is "closer" to the type than Java's
 * references and polymorphism would normally allow. Essentially this provides
 * "narrowing" or "downcasting" functionality on a general type.
 * 
 * It is recommended that the {@link invokePreciseMethod} be used in most cases,
 * as the invokeNarrowestMethod (which is not yet implemenented and may yet be
 * renamed) may give unexpected behaviour.
 * 
 * Standard usage pattern is via an <tt>import static</tt> of the required
 * invocation method.
 * 
 * @author Joey Coleman (jwc@iha.dk)
 */
public final class InvocationAssistant {

	/**
	 * This method uses reflection to invoke the method that precisely matches
	 * the number of parameters and type of the first parameter.
	 * 
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
	 * the first call to <tt>getSomeString()</tt> in <tt>main</tt> will be fine
	 * as it is able to match the reference type of <tt>a</tt> (i.e.
	 * <tt>TestA</tt>) to the type of <tt>getSomeString(TestA)</tt>. However,
	 * the second call to <tt>getSomeString()</tt> will fail, as the reference
	 * type of <tt>t</tt> is the class <tt>Test</tt>, not the actual type of the
	 * object.
	 * 
	 * So, we would add the following method to the <tt>Main</tt> class:
	 * 
	 * <pre>
	 * public static String getSomeString(Test t) throws InvocationAssistantException {
	 * 	return (String) invokePreciseMethod(new Main(), &quot;getSomeString&quot;, t);
	 * }
	 * </pre>
	 * 
	 * at which point the second call to <tt>getSomeString()</tt> will succeed,
	 * calling the specific <tt>getSomeString(TestA)</tt> by way of the
	 * <tt>invokePreciseMethod</tt> call.
	 * 
	 * Note that the desired target method must be public, otherwise the
	 * invokePreciseMethod will not be able to find it. Also, note the cast of
	 * the return value of <tt>invokePreciseMethod()</tt>.
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
	 * @return The return value must be cast from Object to the correct return type.
	 * @throws InvocationAssistantException 
	 */
	public static final Object invokePreciseMethod(Object target, String name,
			Object... parameters) throws InvocationAssistantException {
		if (parameters.length == 0)
			throw new InvocationAssistantException(
					"invokePreciseMethod must be called with at least one parameter to give to the invoked method; i.e. the number of thy parameters must be at least three.");
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
				throw new InvocationAssistantException(errorString);
			}
		} catch (IllegalAccessException e) {
			throw new InvocationAssistantException(
					"IllegalAccessException on attempt to invoke "
							+ method.getName(), e);
		} catch (InvocationTargetException e) {
			throw new InvocationAssistantException(e);
		}
	}
}
