/**
 * 
 */
package org.overture.ast.assistant;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Joey Coleman <jwc@iha.dk>
 * 
 */
public final class InvocationAssistant {

	/**
	 * @param target
	 * @param name
	 * @param parameters
	 * @return
	 */
	public static final Object invokePreciseMethod(Object target, String name,
			Object... parameters) throws InvocationAssistantException {
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
			throw new InvocationAssistantException("IllegalAccessException on attempt to invoke "+method.getName(), e);
		} catch (InvocationTargetException e) {
			throw new InvocationAssistantException(e);
		}
	}
}
