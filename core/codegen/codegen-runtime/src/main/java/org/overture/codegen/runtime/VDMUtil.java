package org.overture.codegen.runtime;

public class VDMUtil
{
	private static final String NOT_SUPPORTED_MSG = "Function is currently not supported";

	@SuppressWarnings("unchecked")
	public static VDMSeq set2seq(Object obj)
	{
		if (!(obj instanceof VDMSet))
		{
			throw new IllegalArgumentException("Expected a set but got: " + Utils.toString(obj));
		}
		
		VDMSet set = (VDMSet) obj;
		
		VDMSeq seq = SeqUtil.seq();
		seq.addAll(set);
		return seq;
	}

	public static Object get_file_pos()
	{
		throw new UnsupportedOperationException(NOT_SUPPORTED_MSG);
	}

	public static String val2seq_of_char(Object value)
	{
		return Utils.toString(value);
	}

	// This method has been updated to also receive the type argument
	public static Tuple seq_of_char2val(Object s, Object type)
	{
		return seq_of_char2val_(s, type);
	}

	private static Tuple seq_of_char2val_(Object s, Object type)
	{
		if(type == null)
		{
			throw new IllegalArgumentException("null is not a valid type");
		}
		
		Tuple NO_CONVERSION = Tuple.mk_(false, null);
		
		String str = null;
		
		if(s instanceof String)
		{
			str = (String) s;
		}
		else if(s instanceof VDMSeq)
		{
			VDMSeq seq = (VDMSeq) s;

			// Check if the sequence is indeed a sequence of characters
			for(Object o : seq)
			{
				if(!(o instanceof Character))
				{
					return NO_CONVERSION;
				}
			}
			
			str = VDMSeq.toStr(seq.iterator());
		}
		else
		{
			return NO_CONVERSION;
		}
		
		str = str.trim();
		
		if(type == Utils.NAT || type == Utils.NAT1 || type == Utils.INT)
		{
			try
			{
				Integer intRes = Integer.valueOf(str);
				
				if((type == Utils.NAT && Utils.is_nat(intRes)) ||
					(type == Utils.NAT1 && Utils.is_nat1(intRes)) ||
					(type == Utils.INT && Utils.is_int(intRes)))
				{
					return Tuple.mk_(true, intRes); 
				}
				else
				{
					return NO_CONVERSION;
				}
				
			} catch (NumberFormatException e)
			{
				return NO_CONVERSION;
			}
		}
		else if(type == Utils.RAT || type == Utils.REAL)
		{
			try
			{
				return Tuple.mk_(true, Double.valueOf(str));
			} catch(NumberFormatException e)
			{
				return NO_CONVERSION;
			}
		}
		else if(type == Utils.BOOL)
		{
			if (str.equals("true")) 
			{
				return Tuple.mk_(true, true);
			} else if (str.equals("false"))
			{
				return Tuple.mk_(true, false);
			} else 
			{
				return NO_CONVERSION;
			}
		}
		else if(type == Utils.CHAR)
		{
			if(str.length() == 3)
			{
				return Tuple.mk_(true, new Character(str.charAt(1)));
			}
			else
			{
				return NO_CONVERSION;
			}
		}
		else if(type instanceof Quote)
		{
			// <A> -> A
			String quoteStr = str.replaceAll("<|>", "");
			
			// A -> AQuote
			quoteStr += "Quote";
			
			if(quoteStr.equals(type.getClass().getSimpleName()))
			{
				return Tuple.mk_(true, type);
			}
			else
			{
				return NO_CONVERSION;
			}
		}
		else if(type instanceof VDMSet)
		{
			VDMSet types = (VDMSet) type;
			// The union type case
			for(Object t : types)
			{
				Tuple conversionAttempt = seq_of_char2val_(str, t);
				
				if(conversionAttempt.size() == 2)
				{
					Object first = conversionAttempt.get(0);
					
					if(first instanceof Boolean)
					{
						Boolean success = (Boolean) first;
						
						if(success)
						{
							return conversionAttempt;
						}
						else
						{
							// Try to convert to the next type
							continue;
						}
					}
				}
				else
				{
					return NO_CONVERSION;
				}
			}
			
			return NO_CONVERSION;
		}
		else
		{
			throw new UnsupportedOperationException("seq_of_char2val currently only supports" + 
					" the following types: nat, nat1, int, rat, real, bool, char and quotes as" +
					" well as unions of these types");
		}
	}

	public static String classname(Object obj)
	{
		if (obj != null && obj.getClass().getEnclosingClass() == null)
		{
			return obj.getClass().getSimpleName();
		} else
		{
			return null;
		}
	}
}
