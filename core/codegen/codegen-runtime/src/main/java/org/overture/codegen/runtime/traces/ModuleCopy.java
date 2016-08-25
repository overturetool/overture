package org.overture.codegen.runtime.traces;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.overture.codegen.runtime.ValueType;
import org.overture.codegen.runtime.copying.DeepCopy;

public class ModuleCopy
{
	private static final String MODIFIERS_FIELD = "modifiers";
	private static final String JAVA_LANG = "java.lang";

	protected Map<Field, Object> staticFields;

	public ModuleCopy(Class<?> clazz)
	{
		super();

		if (!isJavaLangClass(clazz))
		{
			copyStaticFields(clazz);
		}
	}

	private boolean isJavaLangClass(Class<?> clazz)
	{
		return clazz.getName().startsWith(JAVA_LANG);
	}

	public void reset()
	{
		resetStaticFields();
	}

	public static List<Field> getAllFields(Class<?> type)
	{
		return getAllFields(new LinkedList<Field>(), type);
	}

	public void resetStaticFields()
	{
		if (staticFields == null)
		{
			return;
		}

		for (Field f : staticFields.keySet())
		{
			f.setAccessible(true);

			Object v = deepCopy(staticFields.get(f));

			try
			{
				f.set(null, v);
			} catch (IllegalArgumentException | IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}
	}

	public void copyStaticFields(Class<?> clazz)
	{
		this.staticFields = new HashMap<>();

		for (Field f : getAllFields(clazz))
		{
			try
			{
				/**
				 * The field may be 'private'. Make it accessible so we can set it later
				 */
				f.setAccessible(true);
				if (isFinal(f))
				{
					/**
					 * Remove the 'final' modifier so we can set it later
					 */
					unfinal(f);

					/**
					 * The Java code generator also makes 'final' fields 'static'
					 */
					staticFields.put(f, deepCopy(f.get(null)));
				} else if (isStatic(f))
				{
					staticFields.put(f, deepCopy(f.get(null)));
				}
			} catch (NoSuchFieldException | SecurityException
					| IllegalArgumentException | IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}
	}

	private static void unfinal(Field f)
			throws NoSuchFieldException, IllegalAccessException
	{
		f.setAccessible(true);
		Field modifiersField = Field.class.getDeclaredField(MODIFIERS_FIELD);
		modifiersField.setAccessible(true);
		modifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
	}

	public static boolean isFinal(Field f)
	{
		return Modifier.isFinal(f.getModifiers());
	}

	public static boolean isStatic(Field f)
	{
		return Modifier.isStatic(f.getModifiers());
	}

	public static List<Field> getAllFields(List<Field> fields, Class<?> type)
	{
		fields.addAll(Arrays.asList(type.getDeclaredFields()));

		if (type.getSuperclass() != null)
		{
			fields = getAllFields(fields, type.getSuperclass());
		}

		return fields;
	}

	public static Object deepCopy(Object orig)
	{
		if (orig == null)
		{
			return null;
		} else if (orig instanceof ValueType)
		{
			ValueType vt = (ValueType) orig;

			return vt.copy();
		} else if (orig instanceof Number || orig instanceof Character
				|| orig instanceof Boolean)
		{
			return orig;
		} else
		{
			return DeepCopy.copy(orig);
		}
	}

	public Object getValue()
	{
		// A module cannot be instantiated so it has no value
		return null;
	}
}