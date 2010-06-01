package org.overture.ide.debug.core.model;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IVariable;

public class CollectionVdmType extends AtomicVdmType {

	protected CollectionVdmType(String name) {
		super(name);
	}

	public boolean isAtomic() {
		return false;
	}

	public boolean isCollection() {
		return true;
	}

	public String formatDetails(IVdmValue value) {
		final StringBuffer sb = new StringBuffer();
		try {
			IVariable[] variables2 = value.getVariables();
			if (variables2.length > 0) {
				sb.append(getOpenBrace());
				for (int i = 0; i < variables2.length; i++) {
					String details = buildDetailString(variables2[i]);
					sb.append(details);
					sb.append(","); //$NON-NLS-1$
				}
				sb.setLength(sb.length() - 1);
				sb.append(getCloseBrace());
			}
		} catch (DebugException ex) {
			ex.printStackTrace();
		}

		return sb.toString();
	}

	public String formatValue(IVdmValue value) {
		StringBuffer sb = new StringBuffer();

		sb.append(getName());

		try {
			sb.append("[" + value.getVariables().length + "]"); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (DebugException e) {
			sb.append("[]"); //$NON-NLS-1$
		}

		appendInstanceId(value, sb);

		return sb.toString();
	}

	/**
	 * Build the detail string for the given variable.
	 * 
	 * <p>
	 * Default implementation just returns the value of the specified variable.
	 * Subclasses may override if they wish to return something different. For
	 * example, a hash collection may wish to return key/value pairs.
	 * </p>
	 */
	protected String buildDetailString(IVariable variable)
			throws DebugException {
		return variable.getValue().getValueString();
	}

	/**
	 * Returns the brace that will be used to close the collection.
	 * 
	 * <p>
	 * Default implementation returns <code>[</code>. Subclasses may override if
	 * they wish to use something different.
	 * </p>
	 */
	protected char getCloseBrace() {
		return ']';
	}

	/**
	 * Returns the brace that will be used to close the collection.
	 * 
	 * <p>
	 * Default implementation returns <code>]</code>. Subclasses may override if
	 * they wish to use something different.
	 * </p>
	 */
	protected char getOpenBrace() {
		return '[';
	}

}
