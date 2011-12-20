package org.overture.ide.ui.editor.syntax;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;

public class VdmStringScanner extends RuleBasedScanner
{

	public VdmStringScanner(VdmColorProvider provider)
	{
		final IToken string = new Token(new TextAttribute(provider.getColor(VdmColorProvider.STRING)));
		setDefaultReturnToken(string);
	}

}
