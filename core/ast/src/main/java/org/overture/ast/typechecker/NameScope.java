package org.overture.ast.typechecker;

import java.io.Serializable;




public enum NameScope implements Serializable
{
	LOCAL(1),		// Let definitions and parameters
	GLOBAL(2),		// Eg. module and class func/ops/values
	STATE(4),		// Module state or object instance values
	OLDSTATE(8),	// State names with a "~" modifier
	TYPENAME(16),	// The names of types
	CLASSNAME(32),	// The names of classes
	PROCESSNAME(64), // dirty hack to remove split packaging. FIXME must be changed to propper extensible enums
	
	NAMES(3),
	NAMESANDSTATE(7),
	NAMESANDANYSTATE(15);

	private int mask;

	NameScope(int level)
	{
		this.mask = level;
	}

	public boolean matches(NameScope other)
	{
		return (mask & other.mask) != 0;
	}
	

}
