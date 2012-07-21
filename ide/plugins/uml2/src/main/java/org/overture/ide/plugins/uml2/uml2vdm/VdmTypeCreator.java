package org.overture.ide.plugins.uml2.uml2vdm;

import org.eclipse.uml2.uml.Type;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.types.PType;

public class VdmTypeCreator
{

	public PType convert(Type type)
	{
		return AstFactory.newAIntNumericBasicType(null);
	}

}
