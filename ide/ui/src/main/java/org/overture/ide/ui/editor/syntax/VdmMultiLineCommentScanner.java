package org.overture.ide.ui.editor.syntax;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;

public class VdmMultiLineCommentScanner extends RuleBasedScanner
{
		
	public VdmMultiLineCommentScanner(VdmColorProvider provider)
	{
		IToken comment = new Token(new TextAttribute(provider.getColor(VdmColorProvider.SINGLE_LINE_COMMENT)));
		
		setDefaultReturnToken(comment);
	}
	
	
}
