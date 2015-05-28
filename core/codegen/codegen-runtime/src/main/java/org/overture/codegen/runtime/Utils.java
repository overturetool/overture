/*
 * #%~
 * VDM Code Generator Runtime
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.codegen.runtime;

public class Utils
{
	public static final Object VOID_VALUE = new Object();
	
	public static boolean isVoidValue(Object value)
	{
		return value == VOID_VALUE;
	}
	
	public static int hashCode(Object... fields)
	{
		if(fields == null)
			throw new IllegalArgumentException("Fields cannot be null");

		int hashcode = 0;
		
		for(int i = 0; i < fields.length; i++)
		{
			Object currentField = fields[i];
			hashcode += currentField != null ? currentField.hashCode() : 0;
		}
		
		return hashcode;
	}
	
	public static Object get(Object col, Object index)
	{
		if(col instanceof VDMSeq)
		{
			VDMSeq seq = (VDMSeq) col;
			return seq.get(Utils.index(index));
		}
		else if(col instanceof VDMMap)
		{
			return MapUtil.get((VDMMap) col, index);
		}
		else
		{
			throw new IllegalArgumentException("Only a map or a sequence can be read");
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void mapSeqUpdate(Object col, Object index, Object value)
	{
		if(col instanceof VDMSeq)
		{
			VDMSeq seq = (VDMSeq) col;
			seq.set(index(index), value);
		}
		else if(col instanceof VDMMap)
		{
			VDMMap map = (VDMMap) col;
			map.put(index, value);
		}
		else
		{
			throw new IllegalArgumentException("Only a map or a sequence can be updated");
		}
	}
	
	public static int index(Object value)
	{
		if(!(value instanceof Number))
		{
			throw new IllegalArgumentException("The value to be converted must be a java.lang.Number");
		}
		
		Number numberValue = (Number) value;
		
		if(numberValue.longValue() < 1)
			throw new IllegalArgumentException("VDM subscripts must be >= 1");
		
		return toInt(numberValue) - 1;
	}
	
	public static int toInt(Number value) {
		
		long valueLong = value.longValue();
		
	    if (valueLong < Integer.MIN_VALUE || valueLong > Integer.MAX_VALUE) {
	        throw new IllegalArgumentException
	            (valueLong + " Casting the long to an int will change its value");
	    }
	    return (int) valueLong;
	}
	
	public static String formatFields(Object... fields)
	{
		if(fields == null)
			throw new IllegalArgumentException("Fields cannot be null in formatFields");
		
		StringBuilder str = new StringBuilder();

		if (fields.length > 0)
		{
			str.append(Utils.toString(fields[0]));

			for (int i = 1; i < fields.length; i++)
			{
				str.append(", " + Utils.toString(fields[i]));
			}
		}
		return "(" + str.toString() + ")";
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends ValueType> T clone(T t)
	{
		return (T) (t != null ? t.clone() : t);
	}
	
	public static String toString(Object obj)
	{
		if(obj == null)
		{
			return "nil";
		}
		else if(obj == VOID_VALUE)
		{
			return "()";
		}
		else if(obj instanceof Number)
		{
			Number n = (Number) obj;
			
			if(n.doubleValue() % 1 == 0)
			{
				return Long.toString(n.longValue());
			}
			else 
			{
				return Double.toString(n.doubleValue());
			}
		}
		else if(obj instanceof Character)
		{
			return "'" + obj + "'";
		}
		else if(obj instanceof String)
		{
			return "\"" + obj.toString() + "\"";
		}
		
		return obj.toString();
	}
	
	public static boolean equals(Object left, Object right)
	{
		if(left instanceof Long && right instanceof Long)
		{
			Long leftLong = (Long) left;
			Long rightLong = (Long) right;
			
			return leftLong.compareTo(rightLong) == 0;
		}
		
		if(left instanceof Integer && right instanceof Integer)
		{
			Integer leftInt = (Integer) left;
			Integer rightInt = (Integer) right;
			
			return leftInt.compareTo(rightInt) == 0;
		}
		
		if(left instanceof Number && right instanceof Number)
		{
			Double leftNumber = ((Number) left).doubleValue();
			Double rightNumber = ((Number) right).doubleValue();
			
			return leftNumber.compareTo(rightNumber) == 0;
		}
		
		return left != null ? left.equals(right) : right == null; 
	}
	
	public static <T> T postCheck(T returnValue, boolean postResult, String name)
	{
		if(postResult)
		{
			return returnValue;
		}
		
		throw new RuntimeException("Postcondition failure: post_" + name);
	}
	
	public static boolean is_bool(Object value)
	{
		return value instanceof Boolean;
	}
	
	public static boolean is_nat(Object value)
	{
		return isIntWithinRange(value, 0);
	}	
	
	public static boolean is_nat1(Object value)
	{
		return isIntWithinRange(value, 1);
	}

	public static boolean is_int(Object value)
	{
		Double doubleValue = getDoubleValue(value);
		
		return is_int(doubleValue);
	}

	public static boolean is_rat(Object value)
	{
		return value instanceof Number;
	}
	
	public static boolean is_real(Object value)
	{
		return value instanceof Number;
	}
	
	public static boolean is_char(Object value)
	{
		return value instanceof Character;
	}
	
	public static boolean is_token(Object value)
	{
		return value instanceof Token;
	}

	@SuppressWarnings("rawtypes")
	public static boolean is_Tuple(Object exp, Class... types)
	{
		return exp instanceof Tuple && ((Tuple) exp).compatible(types);
	}
	
	@SuppressWarnings("rawtypes")
	public static boolean is_(Object exp, Class type)
	{
		return exp != null && exp.getClass() == type;
	}
	
	public static double divide(double left, double right)
	{
		if(right == 0L)
		{
			throw new ArithmeticException("Division by zero is undefined");
		}
		
		return left/right;
	}

	public static long div(double left, double right)
	{
		validateInput(left, right);
		
		return computeDiv(left, right);
	}
	
	public static long mod(double left, double right)
	{
		validateInput(left, right);
		
		return (long) (left - right * (long) Math.floor(left / right));
	}
	
	public static long rem(double left, double right)
	{
		validateInput(left, right);

		return (long) (left - right * computeDiv(left, right));
	}

	private static void validateInput(double left, double right)
	{
		if(!(is_int(left) && is_int(right)))
		{
			throw new ArithmeticException("Operands must be integers. Got left " + left + " and right" + right);
		}
		
		if(right == 0L)
		{
			throw new ArithmeticException("Division by zero is undefined");
		}
	}
	
	private static long computeDiv(double lv, double rv)
	{
		if (lv / rv < 0)
		{
			return (long) -Math.floor(Math.abs(lv / rv));
		} else
		{
			return (long) Math.floor(Math.abs(-lv / rv));
		}
	}
	
	private static boolean is_int(Double doubleValue)
	{
		return doubleValue != null && (doubleValue == Math.floor(doubleValue)) && !Double.isInfinite(doubleValue);
	}
	
	private static boolean isIntWithinRange(Object value, int lowerLimit)
	{
		Double doubleValue = getDoubleValue(value);
		
		if(!is_int(doubleValue))
		{
			return false;
		}
		
		return doubleValue >= lowerLimit;
	}
	
	private static Double getDoubleValue(Object value)
	{
		if(!(value instanceof Number))
		{
			return null;
		}
		
		Double doubleValue = ((Number) value).doubleValue();
		
		return doubleValue;
	}
	
	public static double floor(Number n)
	{
		if(n == null)
		{
			throw new IllegalArgumentException("The 'floor' operator only works for numbers. Got null");
		}
		
		return Math.floor(n.doubleValue());
	}
	
	public static double abs(Number n)
	{
		if(n == null)
		{
			throw new IllegalArgumentException("The 'abs' operator only works for numbers. Got null");
		}
		
		return Math.abs(n.doubleValue());
	}
	
	public static double pow(Number a, Number b)
	{
		if(a == null || b == null)
		{
			throw new IllegalArgumentException("The power operator only works for numbers. Got arguments: '" + a + "' and '" + b + "'");
		}
		
		return Math.pow(a.doubleValue(), b.doubleValue());	
	}
}
