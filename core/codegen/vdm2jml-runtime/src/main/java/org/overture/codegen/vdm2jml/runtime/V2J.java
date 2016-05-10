package org.overture.codegen.vdm2jml.runtime;

import java.util.Collection;
import java.util.HashSet;

import org.overture.codegen.runtime.SeqUtil;
import org.overture.codegen.runtime.Tuple;
import org.overture.codegen.runtime.VDMMap;
import org.overture.codegen.runtime.VDMSeq;
import org.overture.codegen.runtime.VDMSet;

/**
 * Runtime library used by the Java Modeling Language (JML) generator, which translates VDM-SL models into JML annotated
 * Java. The JML annotations generated by the VDM-SL to Java/JML generator uses the methods in this class.
 * 
 * @author pvj
 */
public class V2J
{

	public static final String METHOD_IS_ONLY_SUPPORTED_FOR = "Method is only supported for ";

	/*@ pure @*/
    /*@ helper @*/
	public static boolean isMap(Object subject)
	{
		return subject instanceof VDMMap;
	}
	
	/* @ pure @ */
	/* @ helper @ */
	public static boolean isInjMap(Object subject)
	{
		if (subject instanceof VDMMap)
		{
			VDMMap map = (VDMMap) subject;
			@SuppressWarnings("unchecked")
			HashSet<Object> values = new HashSet<>(map.values());
			
			return map.keySet().size() == values.size();
		} else
		{
			return false;
		}
	}
	
    /*@ pure @*/
    /*@ helper @*/
	public static boolean isSet(Object subject)
	{
		return subject instanceof VDMSet;
	}
	
    /*@ pure @*/
    /*@ helper @*/
	public static boolean isSeq(Object subject)
	{
		return subject instanceof VDMSeq;
	}
	
    /*@ pure @*/
    /*@ helper @*/
	public static boolean isSeq1(Object subject)
	{
		if (subject instanceof VDMSeq)
		{
			VDMSeq seq = (VDMSeq) subject;
			return !seq.isEmpty();
		} else
		{
			return false;
		}
	}
	
    /*@ pure @*/
    /*@ helper @*/
	public static boolean isTup(Object subject, int size)
	{
		if (subject instanceof Tuple)
		{
			Tuple t = (Tuple) subject;

			return t.size() == size;
		} else
		{
			return false;
		}
	}
	
    /*@ pure @*/
    /*@ helper @*/
	public static Object field(Object subject, int fieldNo)
	{
		if (subject instanceof Tuple)
		{
			Tuple tuple = (Tuple) subject;
			
			return tuple.get(fieldNo);
		} else
		{
			throw new IllegalArgumentException(METHOD_IS_ONLY_SUPPORTED_FOR + Tuple.class);
		}
	}
	
    /*@ pure @*/
    /*@ helper @*/
	public static Object get(Object col, int index)
	{
		if (col instanceof VDMSeq)
		{
			VDMSeq seq = (VDMSeq) col;
			
			return seq.get(index);
		} else if (col instanceof VDMSet)
		{
			VDMSeq seq = set2seq(col);
			
			return seq.get(index);
		}

		throw new IllegalArgumentException(METHOD_IS_ONLY_SUPPORTED_FOR + VDMSeq.class + " and " + VDMSet.class);
	}
	
    /*@ pure @*/
    /*@ helper @*/
	@SuppressWarnings("unchecked")
	public static Object getDom(Object subject, int index)
	{
		if (subject instanceof VDMMap)
		{
			VDMMap map = (VDMMap) subject;
			// http://stackoverflow.com/questions/2923856/is-the-order-guaranteed-for-the-return-of-keys-and-values-from-a-linkedhashmap-o

			VDMSeq seq = SeqUtil.seq();
			seq.addAll(map.keySet());
			
			return seq.get(index);
		}

		throw new IllegalArgumentException(METHOD_IS_ONLY_SUPPORTED_FOR + VDMMap.class);
	}
	
    /*@ pure @*/
    /*@ helper @*/
	@SuppressWarnings("unchecked")
	public static Object getRng(Object subject, int index)
	{
		if (subject instanceof VDMMap)
		{
			VDMMap map = (VDMMap) subject;

			VDMSeq seq = SeqUtil.seq();
			seq.addAll(map.values());
			
			return seq.get(index);
		}

		throw new IllegalArgumentException(METHOD_IS_ONLY_SUPPORTED_FOR + VDMMap.class);
	}
	
    /*@ pure @*/
    /*@ helper @*/
	public static int size(Object vdmCol)
	{
		// Covers sequences and sets
		if (vdmCol instanceof Collection)
		{
			Collection<?> setSeq = (Collection<?>) vdmCol;
			
			return setSeq.size();
		}

		// Covers maps
		if (vdmCol instanceof VDMMap)
		{
			VDMMap map = (VDMMap) vdmCol;
			
			return map.size();
		}

		throw new IllegalArgumentException(METHOD_IS_ONLY_SUPPORTED_FOR + Collection.class + " and " + VDMMap.class);
	}

    /*@ pure @*/
    /*@ helper @*/
	@SuppressWarnings("unchecked")
	public static VDMSeq set2seq(Object set)
	{
		if (set instanceof VDMSet)
		{
			VDMSeq seq = SeqUtil.seq();
			seq.addAll((VDMSet) set);
			
			return seq;
		}

		throw new IllegalArgumentException(METHOD_IS_ONLY_SUPPORTED_FOR + VDMSet.class);
	}
}
