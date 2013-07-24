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
	
	public static boolean seqEquals(List<? extends Object> left, List<? extends Object> right)
	{
		if(left.size() != right.size())
			return false;
		
		for(int i = 0; i < left.size(); i++)
			if(!left.get(i).equals(right.get(i)))
				return false;
				
		return true;
	}
}
