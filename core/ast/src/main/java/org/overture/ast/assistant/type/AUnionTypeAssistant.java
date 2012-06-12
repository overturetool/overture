package org.overture.ast.assistant.type;

import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SNumericBasicType;
import org.overture.ast.util.PTypeSet;

public class AUnionTypeAssistant {
	public static void expand(AUnionType type)
	{

		if (type.getExpanded())
			return;

		PTypeSet exptypes = new PTypeSet();

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

	public static boolean isNumeric(AUnionType type)
	{
		return getNumeric(type) != null;
	}
	
	public static SNumericBasicType getNumeric(AUnionType type)
	{
		if (!type.getNumDone())
		{
			type.setNumDone(true);
			type.setNumType(AstFactory.newANatNumericBasicType(type.getLocation())); // lightest default
			boolean found = false;

			for (PType t : type.getTypes())
			{
				if (PTypeAssistant.isNumeric(t))
				{
					SNumericBasicType nt = PTypeAssistant.getNumeric(t);

					if (SNumericBasicTypeAssistant.getWeight(nt) > SNumericBasicTypeAssistant.getWeight(type.getNumType()))
					{
						type.setNumType(nt);
					}

					found = true;
				}
			}

			if (!found)
				type.setNumType(null);
		}

		return type.getNumType();
	}
	
}
