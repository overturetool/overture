package org.overture.ide.debug.core.model;

public interface IVdmTypeFactory {

	static String STRING = "string"; //$NON-NLS-1$
	static String ARRAY = "array"; //$NON-NLS-1$
	static String HASH = "hash"; //$NON-NLS-1$

	IVdmType buildType(String type);
}
