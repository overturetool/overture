package org.overture.ide.debug.core.model;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IVariable;

/**
 * Represents an 'array' script type
 */
public class ArrayVdmType extends CollectionVdmType {

	private static String ARRAY = "sequence"; //$NON-NLS-1$

	public ArrayVdmType() {
		super(ARRAY);
	}

	/**
	 * @see org.eclipse.dltk.debug.core.model.CollectionScriptType#buildDetailString(org.eclipse.debug.core.model.IVariable)
	 */
	protected String buildDetailString(IVariable variable)
			throws DebugException {
		String name = variable.getName();
		if (name != null && name.length() > 0) {
			int counter = 0;
			if (name.startsWith("-"))
				counter++;
			while (counter < name.length()) {
				if (!Character.isDigit(name.charAt(counter++))) {
					return name + "=" + super.buildDetailString(variable);
				}
			}
		}
		return super.buildDetailString(variable);
	}
}
