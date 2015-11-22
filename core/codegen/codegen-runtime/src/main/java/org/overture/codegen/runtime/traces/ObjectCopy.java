package org.overture.codegen.runtime.traces;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.overture.codegen.runtime.copying.DeepCopy;

public class ObjectCopy
{
	private Object val;

	private Object instance;
	private Map<Field, Object> staticFields;

	public ObjectCopy(Object orig)
	{
		// Copy instance
		instance = DeepCopy.copy(orig);

		copyStaticFields(orig);

		reset();
	}

	public Object getValue()
	{
		return val;
	}

	public void reset()
	{
		val = DeepCopy.copy(instance);

		for (Field f : staticFields.keySet())
		{
			if (isFinal(f))
			{
				continue;
			}

			f.setAccessible(true);

			Object v = DeepCopy.copy(staticFields.get(f));

			try
			{
				f.set(val, v);
			} catch (IllegalArgumentException | IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}
	}

	private void copyStaticFields(Object val)
	{
		staticFields = new HashMap<>();

		for (Field f : getAllFields(val.getClass()))
		{
			if (isFinal(f))
			{
				continue;
			}

			f.setAccessible(true);

			try
			{
				staticFields.put(f, DeepCopy.copy(f.get(val)));
			} catch (IllegalArgumentException | IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}
	}

	private boolean isFinal(Field f)
	{
		return Modifier.isFinal(f.getModifiers());
	}

	private static List<Field> getAllFields(Class<?> type)
	{
		return getAllFields(new LinkedList<Field>(), type);
	}

	private static List<Field> getAllFields(List<Field> fields, Class<?> type)
	{
		fields.addAll(Arrays.asList(type.getDeclaredFields()));

		if (type.getSuperclass() != null)
		{
			fields = getAllFields(fields, type.getSuperclass());
		}

		return fields;
	}
}
