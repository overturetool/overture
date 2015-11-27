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
	protected Object val;
	protected Map<Field, Object> staticFields;
	
	public ModuleCopy(Class<?> clazz)
	{
		super();
		copyStaticFields(clazz);
	}
	
	public void reset()
	{
		resetStaticFields();
	}

	public static List<Field> getAllFields(Class<?> type)
	{
		return getAllFields(new LinkedList<Field>(), type);
	}

	public Object getValue()
	{
		return val;
	}

	public void resetStaticFields()
	{
		for (Field f : staticFields.keySet())
		{
			if (isFinal(f))
			{
				continue;
			}
	
			f.setAccessible(true);
	
			Object v = deepCopy(staticFields.get(f));
	
			try
			{
				f.set(val, v);
			} catch (IllegalArgumentException | IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}
	}

	public void copyStaticFields(Class<?> clazz)
	{
		staticFields = new HashMap<>();
	
		for (Field f : getAllFields(clazz))
		{
			if (isFinal(f))
			{
				continue;
			}
	
			f.setAccessible(true);
	
			try
			{
				if(isStatic(f))
				{
					staticFields.put(f, deepCopy(f.get(null)));
				}
			} catch (IllegalArgumentException | IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}
	}

	public boolean isFinal(Field f)
	{
		return Modifier.isFinal(f.getModifiers());
	}
	
	public boolean isStatic(Field f)
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
		if(orig == null)
		{
			return null;
		}
		else if(orig instanceof ValueType)
		{
			ValueType vt = (ValueType) orig;
			
			return vt.copy();
		}else if(orig instanceof Number || orig instanceof Character || orig instanceof Boolean)
		{
			return orig;
		}
		else
		{
			return DeepCopy.copy(orig);
		}
	}
}