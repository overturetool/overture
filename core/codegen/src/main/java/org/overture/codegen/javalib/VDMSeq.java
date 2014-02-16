package org.overture.codegen.javalib;

import java.util.*;

public class VDMSeq extends ArrayList implements ValueType
{	
	public VDMSeq clone()
	{
		VDMSeq seqClone = new VDMSeq();

		for (Object element: this)
		{
			if (element instanceof ValueType)
				element = ((ValueType)element).clone();
			
			seqClone.add(element);
		}

		return seqClone;
	}
	
	@Override
	public synchronized String toString()
	{
		Iterator iterator = this.iterator();
		
		if(!iterator.hasNext())
			return "[]";
		
		StringBuilder sb = new StringBuilder();
		
		sb.append('[');
		
		Object element = iterator.next();
		sb.append(element == this ? "(this Collection)" : element);
		
		while(iterator.hasNext())
		{
			element = iterator.next();
			sb.append(", ");
			sb.append(element == this ? "(this Collection)" : element);
		}
		
		sb.append(']');
		
		return sb.toString();
	}
}
