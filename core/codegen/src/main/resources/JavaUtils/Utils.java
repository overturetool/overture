import java.util.List;
import java.util.Vector;

public class Utils
{

	public static <T> List<T> seq()
	{
		return new Vector<T>();
	}
	
	public static <T> List<T> seq(T... elements)
	{
		Vector<T> vector = new Vector<T>();
		
		for (T element : elements)
		{
			vector.add(element);
		}
		
		return vector;
	}
	
	public static <T> List<T> seqConc(List<T> left, List<T> right)
	{
		List<T> result = new Vector<T>();
		
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
