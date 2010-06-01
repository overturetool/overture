package org.overture.ide.debug.core.model;

/**
 * Represents an 'complex' script type
 */
public class ComplexVdmType extends AtomicVdmType {

	public ComplexVdmType(String name) {
		super(name);
	}

	public boolean isAtomic() {
		return false;
	}

	public boolean isComplex() {
		return true;
	}

	public String formatDetails(IVdmValue value) {
		StringBuffer sb = new StringBuffer();
		sb.append(getName());

		String address = value.getMemoryAddress();
		if (address == null) {
			address = "unknown";
		}

		sb.append("@" + address); //$NON-NLS-1$

		return sb.toString();
	}

	public String formatValue(IVdmValue value) {
		StringBuffer sb = new StringBuffer();
		sb.append(getName());

		appendInstanceId(value, sb);

		return sb.toString();
	}
}
