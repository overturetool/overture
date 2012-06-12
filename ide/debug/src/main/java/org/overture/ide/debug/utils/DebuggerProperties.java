package org.overture.ide.debug.utils;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import org.overture.parser.config.Properties;


public class DebuggerProperties
{
	public static class DebuggerProperty implements Comparable<DebuggerProperty>
	{
		public final String name;
		public Boolean bValue;
		public Integer iValue;

		public enum ValueType
		{
			Bool, Int
		};

		public final ValueType type;

		public DebuggerProperty(String name, Boolean value)
		{
			this.name = name;
			this.type = ValueType.Bool;
			this.bValue = value;
		}

		public DebuggerProperty(String name, Integer value)
		{
			this.name = name;
			this.type = ValueType.Int;
			this.iValue = value;
		}
		
		public static DebuggerProperty load(String propertyString) throws Exception
		{
			try{
				String[] parts=propertyString.split("=");
				
				String name = parts[0].trim().replace('.', '_');
				parts[1]=parts[1].trim();
				
				if(parts[1].equals("true")||parts[1].equals("false"))
				{
					return new DebuggerProperty(name, Boolean.valueOf(parts[1]));
				}
				return new DebuggerProperty(name, Integer.valueOf(parts[1]));
			}catch(Exception e)
			{
				throw new Exception("Invalid property string");
			}
		}

		@Override
		public String toString()
		{
			String tmp = name.replace('_', '.');
			switch (type)
			{
				case Bool:
					return tmp + " = " + bValue;
				case Int:
					return tmp + " = " + iValue;
				default:
					return super.toString();
			}
		}

		public int compareTo(DebuggerProperty o)
		{
			return this.name.compareTo(o.name);
		}
	}

	public static Set<DebuggerProperty> getDefaults() throws IllegalArgumentException,
			IllegalAccessException
	{
		Object propertiesInstance = new Properties();
		Set<DebuggerProperty> props = new HashSet<DebuggerProperty>();

		for (Field f : Properties.class.getFields())
		{
			if (f.getType().getName().equals(Boolean.class.getName())|| f.getType().getName().equals("boolean"))
			{
				DebuggerProperty p = new DebuggerProperty(f.getName(), f.getBoolean(propertiesInstance));
				props.add(p);
			} else if (f.getType().getName().equals(Integer.class.getName())|| f.getType().getName().equals("int"))
			{
				DebuggerProperty p = new DebuggerProperty(f.getName(), f.getInt(propertiesInstance));
				props.add(p);
			}

		}

		return props;
	}
}
