package org.overture.codegen.javalib;

import java.util.HashSet;
import java.util.Iterator;

@SuppressWarnings("rawtypes")
public class VDMSet extends HashSet implements ValueType
{
	private static final long serialVersionUID = 2984495719595419443L;

	@SuppressWarnings("unchecked")
	public VDMSet clone()
	{
		VDMSet setClone = new VDMSet();

		for (Object element: this)
		{
			if (element instanceof ValueType)
				element = ((ValueType)element).clone();
			
			setClone.add(element);
		}

		return setClone;
	}
	
	@Override
	public String toString()
	{
		Iterator iterator = this.iterator();
		
		if(!iterator.hasNext())
			return "{}";

		StringBuilder sb = new StringBuilder();
		
		sb.append('{');
		
		for(;;)
		{
            Object element = iterator.next();
            
            sb.append(element == this ? "(this Collection)" : element);
            
            if (! iterator.hasNext())
            {
                return sb.append('}').toString();
            }
            
            sb.append(',').append(' ');
		}
	}
}
