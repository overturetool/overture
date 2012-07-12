package org.overture.ide.plugins.uml2.vdm2uml;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.uml2.uml.Type;
import org.overture.ast.types.PType;

public class UmlTypeCreatorBase
{
	public final Map<String, Type> types = new HashMap<String, Type>();
	
	public String getName(PType type)
	{
		return "unknown";
	}
}
