package org.overture.ide.ui.partitioning;

import org.eclipse.jface.text.rules.IWhitespaceDetector;

public class VdmWhitespaceDetector implements IWhitespaceDetector {

	/* (non-Javadoc)
	 * Method declared on IWhitespaceDetector
	 */
	public boolean isWhitespace(char character) {
		return Character.isWhitespace(character);
	}
}