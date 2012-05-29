package org.overture.ast.definitions.assistants;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;

public class AUnionTypeAssistant {
	public static void expand(AUnionType type)
	{

		if (type.getExpanded())
			return;

		Set<PType> exptypes = new HashSet<PType>();

		for (PType t : type.getTypes())
		{
			if (t instanceof AUnionType)
			{
				AUnionType ut = (AUnionType) t;
				ut.setExpanded(false);
				expand(ut);
				exptypes.addAll(ut.getTypes());
			} else
			{
				exptypes.add(t);
			}
		}

		Vector<PType> v = new Vector<PType>(exptypes);
		type.setTypes(v);
		type.setExpanded(true);
		List<PDefinition> definitions = type.getDefinitions();

		for (PType t : type.getTypes())
		{
			if (t.getDefinitions() != null)
			{
				definitions.addAll(t.getDefinitions());
			}
		}

	}

}
