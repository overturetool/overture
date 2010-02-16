package org.overture.ide.ui.editor.syntax;

import org.eclipse.jface.text.rules.IWordDetector;

public class VdmWordDetector implements IWordDetector {

	public boolean isWordPart(char c) {
		// TODO Auto-generated method stub
		return !Character.isWhitespace(c);
	}

	public boolean isWordStart(char c) {
		// TODO Auto-generated method stub
		return !Character.isWhitespace(c);
	}

}