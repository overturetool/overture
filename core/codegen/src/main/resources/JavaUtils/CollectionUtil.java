
public class CollectionUtil
{
	public static void addAll(VDMCollection to, Object... from)
	{
		if(to == null || from == null)
			throw new IllegalArgumentException("Arguments to addAll cannot be null");
		
		for(Object element : from)
		{
			if(element instanceof ValueType)
			{
				ValueType valueType = (ValueType) element;
				element = valueType.clone();
			}
			
			to.add(element);
		}
	}
}
