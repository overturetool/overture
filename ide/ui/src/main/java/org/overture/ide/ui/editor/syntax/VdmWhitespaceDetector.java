package org.overture.ide.ui.editor.syntax;

import org.eclipse.jface.text.rules.IWhitespaceDetector;

public class VdmWhitespaceDetector implements IWhitespaceDetector {

	public boolean isWhitespace(char c) {

		return Character.isWhitespace(c);
	}

}