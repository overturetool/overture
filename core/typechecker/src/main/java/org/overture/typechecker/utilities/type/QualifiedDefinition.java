package org.overture.typechecker.utilities.type;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.types.PType;

public class QualifiedDefinition
{
	private final PDefinition def;
	private final PType type;
	private final PType original;
	
	public QualifiedDefinition(PDefinition qualifies, PType type)
	{
		this.def = qualifies;
		this.type = type;
		this.original = qualifies.getType();
	}
	
	public void qualifyType()
	{
		def.setType(type);
	}
	
	public void resetType()
	{
		def.setType(original);
	}
}
