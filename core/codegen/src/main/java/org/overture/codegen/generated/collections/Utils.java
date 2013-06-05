package org.overture.codegen.generated.collections;

import java.util.List;
import java.util.Vector;

public class Utils
{

	public static <T> List<T> seq()
	{
		return new Vector<>();
	}
	
	public static <T> List<T> seq(T... elements)
	{
		Vector<T> vector = new Vector<>();
		
		for (T element : elements)
		{
			vector.add(element);
		}
		
		return vector;
	}
	
	public static <T> List<T> seqConc(List<T> left, List<T> right)
	{
		List<T> result = new Vector<>();
		
		result.addAll(left);
		result.addAll(right);
		
		return result;
	}
}
