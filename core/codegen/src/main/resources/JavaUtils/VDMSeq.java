
import java.util.*;

public class VDMSeq extends Vector implements ValueType, VDMCollection
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
}
