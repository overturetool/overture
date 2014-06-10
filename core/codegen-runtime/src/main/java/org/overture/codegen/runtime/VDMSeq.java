package org.overture.codegen.runtime;

import java.util.*;

@SuppressWarnings("rawtypes")
public class VDMSeq extends ArrayList implements ValueType
{	
	private static final long serialVersionUID = 5083307947808060044L;

	@SuppressWarnings("unchecked")
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
		
		if (!iterator.hasNext())
			return "[]";
		
		boolean seqOfChar = true;

		while (iterator.hasNext())
		{
			Object element = iterator.next();
			if (!(element instanceof Character))
			{
				seqOfChar = false;
				break;
			}
		}

		if (seqOfChar)
		{
			StringBuilder sb = new StringBuilder();
			
			iterator = this.iterator();
			
			while (iterator.hasNext())
			{
				Object element = iterator.next();
				sb.append(element);
			}
			
			return sb.toString();

		} else
		{
			iterator = this.iterator();

			StringBuilder sb = new StringBuilder();

			sb.append('[');

			Object element = iterator.next();
			sb.append(element == this ? "(this Collection)" : element);

			while (iterator.hasNext())
			{
				element = iterator.next();
				sb.append(", ");
				sb.append(element == this ? "(this Collection)" : element);
			}

			sb.append(']');

			return sb.toString();
		}
	}
}
