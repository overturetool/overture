package org.overture.codegen.runtime;

import java.io.Serializable;

public interface ValueType extends Serializable
{
	public ValueType clone();
}
