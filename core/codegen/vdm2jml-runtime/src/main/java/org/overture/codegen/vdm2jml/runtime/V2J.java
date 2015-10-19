package org.overture.codegen.vdm2jml.runtime;

import java.util.Collection;
import java.util.HashSet;

import org.overture.codegen.runtime.SeqUtil;
import org.overture.codegen.runtime.Tuple;
import org.overture.codegen.runtime.VDMMap;
import org.overture.codegen.runtime.VDMSeq;
import org.overture.codegen.runtime.VDMSet;

public class V2J
{
	public static boolean isInjMap(Object obj)
	{
		if(isMap(obj))
		{
			VDMMap map = (VDMMap) obj;
			@SuppressWarnings("unchecked")
			HashSet<Object> values = new HashSet<>(map.values());
			return map.keySet().size() == values.size();
		}
		else
		{
			return false;
		}
	}
	
	public static boolean isMap(Object obj)
	{
		return obj instanceof VDMMap;
	}
	
	public static boolean isSet(Object obj)
	{
		return obj instanceof VDMSet;
	}
	
	public static boolean isSeq(Object obj)
	{
		return obj instanceof VDMSeq;
	}
	
	public static boolean isSeq1(Object obj)
	{
		if(obj instanceof VDMSeq)
		{
			VDMSeq seq = (VDMSeq) obj;
			return !seq.isEmpty();
		}
		else
		{
			return false;
		}
	}
	
	/*@ pure @*/
	public static VDMSeq toSeq(Object o)
	{
		if(o instanceof VDMSeq)
		{
			return (VDMSeq) o;
		}
		
		throw new IllegalArgumentException("Method is only supported for " + VDMSeq.class);
	}
	
	/*@ pure @*/
	public static Object get(Object col, int i)
	{
		if(col instanceof VDMSeq)
		{
			return ((VDMSeq) col).get(i);
		}
		else if(col instanceof VDMSet)
		{
			VDMSeq seq = set2seq(col);
			return seq.get(i);
		}
		
		throw new IllegalArgumentException("Method is only supported for " + VDMSeq.class + " and " + VDMSet.class);
	}
	
	@SuppressWarnings("unchecked")
	public static Object getDom(Object map, int i)
	{
		if(map instanceof VDMMap)
		{
			VDMMap cMap = (VDMMap) map;
			//http://stackoverflow.com/questions/2923856/is-the-order-guaranteed-for-the-return-of-keys-and-values-from-a-linkedhashmap-o
			
			VDMSeq seq = SeqUtil.seq();
			seq.addAll(cMap.keySet());
			return seq.get(i);
		}
		
		throw new IllegalArgumentException("Method is only supported for " + VDMMap.class);
	}
	
	@SuppressWarnings("unchecked")
	public static Object getRng(Object map, int i)
	{
		if(map instanceof VDMMap)
		{
			VDMMap cMap = (VDMMap) map;
			
			VDMSeq seq = SeqUtil.seq();
			seq.addAll(cMap.values());
			return seq.get(i);
		}
		
		throw new IllegalArgumentException("Method is only supported for " + VDMMap.class);
	}
	
	/*@ pure @*/
	public static int size(Object o)
	{
		// Covers sequences and sets
		if(o instanceof Collection)
		{
			return ((Collection<?>) o).size();
		}
		
		// Covers maps
		if(o instanceof VDMMap)
		{
			return ((VDMMap) o).size();
		}
		
		throw new IllegalArgumentException("Method is only supported for " + Collection.class);
	}
	
	@SuppressWarnings("unchecked")
	/*@ pure @*/
	public static VDMSeq set2seq(Object set)
	{
		if(set instanceof VDMSet)
		{
			VDMSeq seq = SeqUtil.seq();
			seq.addAll((VDMSet) set);
			return seq;
		}
		
		throw new IllegalArgumentException("Method is only supported for " + VDMSet.class);
	}
	
	/*@ pure @*/
	public static boolean isTup(Object subject, int size)
	{
		if(subject instanceof Tuple)
		{
			Tuple t = (Tuple) subject;
			
			return t.size() == size;
		}
		else
		{
			return false;
		}
	}
	
	/*@ pure @*/
	public static Object field(Object tup, int fieldNumber)
	{
		if(tup instanceof Tuple)
		{
			return ((Tuple) tup).get(fieldNumber);
		}
		else
		{
			throw new IllegalArgumentException("Method is only supported for " + Tuple.class);
		}
	}
}
