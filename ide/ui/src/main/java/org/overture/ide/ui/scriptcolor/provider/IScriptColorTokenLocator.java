package org.overture.ide.ui.scriptcolor.provider;

import org.eclipse.jface.text.rules.IToken;

public interface IScriptColorTokenLocator
{
	public IToken getColorToken(String key);
}
