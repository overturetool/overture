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
	
	public static int hashcode(Object... fields)
	{
		int hashcode = 0;
		
		for(int i = 0; i < fields.length; i++)
		{
			Object currentField = fields[i];
			hashcode += currentField != null ? currentField.hashCode() : 0;
		}
		
		return hashcode;
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
	
//	public static <T extends Object> List<T> reverse(List<T> list)
//	{
//		LinkedList<T> reversed = new LinkedList<T>();
//		
//		for(int i = list.size() - 1; i >= 0; i--)
//		{
//			T currentElement = list.get(i);
//			
//			
//		}
//		
//		return null;
//			
//	}
}
