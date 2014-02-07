
import java.util.*;

public class VDMSet extends HashSet implements ValueType
{
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
}
