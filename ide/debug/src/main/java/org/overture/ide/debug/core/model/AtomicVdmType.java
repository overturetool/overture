package org.overture.ide.debug.core.model;

public class AtomicVdmType implements IVdmType {
	private String name;

	public AtomicVdmType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public boolean isAtomic() {
		return true;
	}

	public boolean isComplex() {
		return false;
	}

	public boolean isCollection() {
		return false;
	}

	public boolean isString() {
		return false;
	}

	public String formatDetails(IVdmValue value) {
		return formatValue(value);
	}

	public String formatValue(IVdmValue value) {
		return value.getRawValue();
	}

	protected void appendInstanceId(IVdmValue value, StringBuffer buffer) {
		String id = value.getInstanceId();
		if (id != null) {
			buffer.append(" ("); //$NON-NLS-1$
			buffer.append("id");
			buffer.append("="); //$NON-NLS-1$
			buffer.append(id);
			buffer.append(")"); //$NON-NLS-1$
		}
	}
}
