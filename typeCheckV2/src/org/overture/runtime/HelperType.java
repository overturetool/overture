package org.overture.runtime;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overturetool.vdmj.lex.LexLocation;




public class HelperType {

	public static boolean hasSupertype(AClassType cto, AClassType other) {
		return HelperDefinition.hasSupertype(cto.getClassdef(),other);
	}

	public static boolean isType(PType b, Class<ABooleanBasicType> typeclass) {
		return typeclass.isInstance(b);
	}

	public static AProductType getProduct(PType result) {
		if(result instanceof AProductType)
		{
			return (AProductType) result;
		}else
		{
			return null;
		}
	}

	public static PType getType(Set<PType> rtypes, LexLocation location) {
		// If there are any Optional(Unknowns) these are the result of
		// nil values, which set the overall type as optional. Other
		// optional types stay.

		Iterator<PType> tit = rtypes.iterator();
		boolean optional = false;

		while (tit.hasNext())
		{
			PType t = tit.next();

			if (t instanceof AOptionalType)
			{
				AOptionalType ot = (AOptionalType)t;

				if (ot.getType() instanceof AUnknownType)
				{
					if (rtypes.size() > 1)
    				{
    					tit.remove();
    					optional = true;
    				}
					else
					{
						optional = false;
					}
				}
			}
		}

		assert rtypes.size() > 0 : "Getting type of empty TypeSet";
		PType result = null;

		if (rtypes.size() == 1)
		{
			result = rtypes.iterator().next();
		}
		else
		{
			result = new AUnionType(location, new ArrayList<PType>(rtypes));
		}

		return (optional ? new AOptionalType(location, result) : result);
	}

}
