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

	// Used to pass type arguments for instantiated functions in the generated code
	public static final Object NAT = new Object();
	public static final Object NAT1 = new Object();
	public static final Object INT = new Object();
	public static final Object REAL = new Object();
	public static final Object RAT = new Object();
	public static final Object BOOL = new Object();
	public static final Object CHAR = new Object();
	public static final Object TOKEN = new Object();
	public static final Object STRING = new Object();
	public static final Object UNKNOWN = new Object();
	// Only basic types, quotes, union of quotes, strings, polymorphic types, the unknown type and records can currently be used as polymorphic type arguments
	public static final Object TYPE_NOT_SUPPORTED = new Object();

	public static boolean isVoidValue(Object value)
	{
		return value == VOID_VALUE;
	}

	public static boolean empty(Object col)
	{
		if (col instanceof VDMSet)
		{
			return ((VDMSet) col).isEmpty();
		} else if (col instanceof VDMSeq)
		{
			return ((VDMSeq) col).isEmpty();
		} else if (col instanceof VDMMap)
		{
			return ((VDMMap) col).isEmpty();
		} else
		{
			throw new IllegalArgumentException("Expected collection to be either a VDM set, map or sequence. Got: "
					+ col);
		}
	}

	public static int hashCode(Object... fields)
	{
		if (fields == null)
		{
			throw new IllegalArgumentException("Fields cannot be null");
		}

		int hashcode = 0;

		for (int i = 0; i < fields.length; i++)
		{
			Object currentField = fields[i];
			hashcode += currentField != null ? currentField.hashCode() : 0;
		}

		return hashcode;
	}

	public static Object get(Object col, Object index)
	{
		if (col instanceof VDMSeq)
		{
			VDMSeq seq = (VDMSeq) col;
			return seq.get(Utils.index(index));
		} else if (col instanceof VDMMap)
		{
			return MapUtil.get((VDMMap) col, index);
		} else
		{
			throw new IllegalArgumentException("Only a map or a sequence can be read");
		}
	}

	@SuppressWarnings("unchecked")
	public static void mapSeqUpdate(Object col, Object index, Object value)
	{
		if (col instanceof VDMSeq)
		{
			VDMSeq seq = (VDMSeq) col;
			seq.set(index(index), value);
		} else if (col instanceof VDMMap)
		{
			VDMMap map = (VDMMap) col;
			map.put(index, value);
		} else
		{
			throw new IllegalArgumentException("Only a map or a sequence can be updated");
		}
	}

	public static int index(Object value)
	{
		if (!(value instanceof Number))
		{
			throw new IllegalArgumentException("The value to be converted must be a java.lang.Number");
		}

		Number numberValue = (Number) value;

		if (numberValue.longValue() < 1)
		{
			throw new IllegalArgumentException("VDM subscripts must be >= 1");
		}

		return toInt(numberValue) - 1;
	}

	public static String formatFields(Object... fields)
	{
		if (fields == null)
		{
			throw new IllegalArgumentException("Fields cannot be null in formatFields");
		}

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
	public static <T> T copy(T t)
	{
		if (t instanceof ValueType)
		{
			return (T) ((ValueType) t).copy();
		} else
		{
			return t;
		}
	}

	public static String toString(Object obj)
	{
		if (obj == null)
		{
			return "nil";
		} else if (obj == VOID_VALUE)
		{
			return "()";
		} else if (obj instanceof Number)
		{
			Number n = (Number) obj;

			if (n.doubleValue() % 1 == 0)
			{
				return Long.toString(n.longValue());
			} else
			{
				return Double.toString(n.doubleValue());
			}
		} else if (obj instanceof Character)
		{
			return "'" + obj + "'";
		} else if (obj instanceof String)
		{
			return "\"" + obj.toString() + "\"";
		}

		return obj.toString();
	}

	public static boolean equals(Object left, Object right)
	{
		if (left instanceof Long && right instanceof Long)
		{
			Long leftLong = (Long) left;
			Long rightLong = (Long) right;

			return leftLong.compareTo(rightLong) == 0;
		}

		if (left instanceof Integer && right instanceof Integer)
		{
			Integer leftInt = (Integer) left;
			Integer rightInt = (Integer) right;

			return leftInt.compareTo(rightInt) == 0;
		}

		if (left instanceof Number && right instanceof Number)
		{
			Double leftNumber = ((Number) left).doubleValue();
			Double rightNumber = ((Number) right).doubleValue();

			return leftNumber.compareTo(rightNumber) == 0;
		}

		return left != null ? left.equals(right) : right == null;
	}

	public static <T> T postCheck(T returnValue, boolean postResult,
			String name)
	{
		if (postResult)
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
	public static boolean is_(Object exp, Object type)
	{
		// Handle polymorphic type arguments
		if(type == NAT)
		{
			return is_nat(exp);
		}
		else if(type == NAT1)
		{
			return is_nat1(exp);
		}
		else if(type == INT)
		{
			return is_int(exp);
		}
		else if(type == REAL)
		{
			return is_real(exp);
		}
		else if(type == RAT)
		{
			return is_rat(exp);
		}
		else if(type == BOOL)
		{
			return is_bool(exp);
		}
		else if(type == CHAR)
		{
			return is_char(exp);
		}
		else if(type == TOKEN)
		{
			return is_token(exp);
		}
		else if(type == TYPE_NOT_SUPPORTED)
		{
			throw new IllegalArgumentException("Only basic types, quotes, union of quotes, strings, polymorphic types,"
					+ " the unknown type and records can currently be used as polymorphic type arguments");
		}
		else if(type == STRING)
		{
			return exp instanceof String;
		}
		else if(type == UNKNOWN)
		{
			return true;
		}
		else if(type instanceof VDMSet)
		{
			// Special case; union of quotes

			for(Object o : ((VDMSet) type))
			{
				if(exp == o)
				{
					return true;
				}
			}

			return false;
		}
		else if(type instanceof Class)
		{
			return ((Class) type).isInstance(exp);
		}
		else
		{
      // If we are checking if a value is a quote
			return exp == type;
		}
	}

	public static double divide(Object left, Object right)
	{
		validateNumbers(left, right, "divide");

		double leftDouble = ((Number) left).doubleValue();
		double rightDouble = ((Number) right).doubleValue();

		if (rightDouble == 0L)
		{
			throw new ArithmeticException("Division by zero is undefined");
		}

		return leftDouble / rightDouble;
	}

	public static long div(Object left, Object right)
	{
		validateIntOperands(left, right);

		Number leftInt = (Number) left;
		Number rightInt = (Number) right;

		return computeDiv(leftInt.doubleValue(), rightInt.doubleValue());
	}

	public static long mod(Object left, Object right)
	{
		validateIntOperands(left, right);

		double leftInt = ((Number) left).doubleValue();
		double rightInt = ((Number) right).doubleValue();

		return (long) (leftInt
				- rightInt * (long) Math.floor(leftInt / rightInt));
	}

	public static long rem(Object left, Object right)
	{
		validateIntOperands(left, right);

		double leftInt = ((Number) left).doubleValue();
		double rightInt = ((Number) right).doubleValue();

		return (long) (leftInt - rightInt * computeDiv(leftInt, rightInt));
	}

	public static double floor(Object arg)
	{
		validateNumber(arg, "floor");

		Number number = (Number) arg;

		return Math.floor(number.doubleValue());
	}

	public static double abs(Object arg)
	{
		validateNumber(arg, "abs");

		Number number = (Number) arg;

		return Math.abs(number.doubleValue());
	}

	public static double pow(Object a, Object b)
	{
		validateNumbers(a, b, "pow");

		Number aNumber = (Number) a;
		Number bNumber = (Number) b;

		return Math.pow(aNumber.doubleValue(), bNumber.doubleValue());
	}

	/* @ pure @ */
	public static boolean report(String name, boolean res, Object... params)
	{
		if (res)
		{
			return true;
		}

		StringBuilder sb = new StringBuilder();
		sb.append(name);
		sb.append('(');

		if (params.length > 1)
		{
			String del = ", ";
			String eq = " = ";

			sb.append(params[0]);
			sb.append(eq);
			sb.append(Utils.toString(params[1]));

			for (int i = 2; i < params.length; i = i + 2)
			{
				sb.append(del);
				sb.append(params[i]);
				sb.append(eq);
				sb.append(Utils.toString(params[i + 1]));
			}
		}

		sb.append(')');
		sb.append(" is " + res);
		sb.append('\n');

		System.out.println(sb.toString());
		AssertionError error = new AssertionError();
		error.printStackTrace(System.out);
		throw error;
	}

	static int toInt(Number value)
	{
		long valueLong = value.longValue();

		if (valueLong < Integer.MIN_VALUE || valueLong > Integer.MAX_VALUE)
		{
			throw new IllegalArgumentException(valueLong
					+ " Casting the long to an int will change its value");
		}
		return (int) valueLong;
	}

	private static void validateIntOperands(Object left, Object right)
	{
		if (!(is_int(left) && is_int(right)))
		{
			throw new ArithmeticException("Operands must be integers. Got left "
					+ left + " and right" + right);
		}

		if (((Number) right).longValue() == 0L)
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
		return doubleValue != null && doubleValue == Math.floor(doubleValue)
				&& !Double.isInfinite(doubleValue);
	}

	private static boolean isIntWithinRange(Object value, int lowerLimit)
	{
		Double doubleValue = getDoubleValue(value);

		if (!is_int(doubleValue))
		{
			return false;
		}

		return doubleValue >= lowerLimit;
	}

	private static Double getDoubleValue(Object value)
	{
		if (!(value instanceof Number))
		{
			return null;
		}

		Double doubleValue = ((Number) value).doubleValue();

		return doubleValue;
	}

	static void validateNumbers(Object left, Object right, String operator)
	{
		if (!(left instanceof Number) || !(right instanceof Number))
		{
			throw new IllegalArgumentException(operator
					+ " is only supported for numbers. Got " + left + " and "
					+ right);
		}
	}

	private static void validateNumber(Object arg, String operator)
	{
		if (!(arg instanceof Number))
		{
			throw new IllegalArgumentException(operator
					+ " is only supported for numbers. Got " + arg);
		}
	}
}
