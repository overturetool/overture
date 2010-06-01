package org.overture.ide.debug.core.model;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IVariable;

/**
 * Represents a 'hash' script type
 */
public class HashVdmType extends CollectionVdmType {

	private static String HASH = "map"; //$NON-NLS-1$

	public HashVdmType() {
		super(HASH);
	}

	protected String buildDetailString(IVariable variable)
			throws DebugException {
		StringBuffer sb = new StringBuffer();

		sb.append(getVariableName(variable));
		sb.append("|->"); //$NON-NLS-1$
		sb.append(variable.getValue().getValueString());

		return sb.toString();
	}

	protected char getCloseBrace() {
		return '}';
	}

	protected char getOpenBrace() {
		return '{';
	}

	/**
	 * Returns the variable name (key) for the hash element.
	 * 
	 * <p>
	 * Subclasses may override this method if they need to process the variable
	 * name before it is displayed.
	 * </p>
	 */
	protected String getVariableName(IVariable variable) throws DebugException {
		return variable.getName();
	}
	
	@Override
	public String formatValue(IVdmValue value) {
		return super.formatValue(value);
	}
}
