package org.overture.ide.debug.core.model;

public interface IVdmType {
	String getName();

	boolean isAtomic();

	boolean isCollection();

	boolean isString();

	boolean isComplex();

	String formatValue(IVdmValue value);

	String formatDetails(IVdmValue vdmValue);
}
