package org.overture.codegen.generated.collections;

import java.util.Vector;

public class Utils
{

	public static <T> Vector<T> seq(T... elements)
	{
		Vector<T> vector = new Vector<>();
		
		for (T element : elements)
		{
			vector.add(element);
		}
		
		return vector;
	}
	
}
