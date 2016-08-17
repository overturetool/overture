package org.overture.codegen.runtime.traces;

import java.lang.reflect.Field;

public class TraceUtil
{
	public static <T> T readState(Class<?> module, Class<T> stateType)
	{
		Field[] fields = module.getDeclaredFields();

		for (Field f : fields)
		{
			if (!ModuleCopy.isFinal(f))
			{
				// It' the state component
				f.setAccessible(true);
				try
				{
					Object stateVal = f.get(null);
					return stateType.cast(stateVal);
				} catch (IllegalArgumentException | IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}
		}

		return null;
	}
}
