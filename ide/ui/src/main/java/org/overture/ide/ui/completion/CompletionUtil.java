package org.overture.ide.ui.completion;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.overture.ast.lex.VDMToken;
import org.overture.ide.ui.VdmUIPlugin;
import org.overture.ide.ui.templates.VdmCompletionContext;

public class CompletionUtil
{
	private static VDMToken getToken(char c)
	{
		String name = "" + c;
		for (VDMToken token : VDMToken.values())
		{
			if (token.toString() != null && token.toString().equals(name))
			{
				return token;
			}
		}
		return null;
	}

	public static VdmCompletionContext computeVdmCompletionContext(IDocument doc,
			int documentOffset)
	{
		// Use string buffer to collect characters
		StringBuffer scanned = new StringBuffer();
		while (true)
		{
			try
			{
				if(documentOffset-1==-1)
				{
					//EOF
					break;
				}
				// Read character backwards
				char c = doc.getChar(--documentOffset);

				VDMToken token = null;
				
				// We stop if it is a VDM token unless it is a '<', a '.' or a '('
				if ((token = getToken(c)) != null)// '`' == null
				{
					if (!(token == VDMToken.LT /* < */ || token == VDMToken.POINT/* . */|| token == VDMToken.BRA /* ( */))
					{
						break;
					}
				}
				
				scanned.append(c);
				
				// The 'new' keyword
				if(c=='n' && scanned.length()>3&& scanned.substring(scanned.length()-4, scanned.length()).matches("\\swen"))
				{
					
					break;
				}
//				//The 'mk_token'
//				else if (c == 't' && scanned.length() >= 4 && scanned.substring(scanned.length() - 4, scanned.length()).matches("t_km"))
//				{
//					break;
//				}
				// The 'mk_"
				else if(c == 'm' && scanned.length() >= 3 && scanned.substring(scanned.length() - 3, scanned.length()).matches("_km"))
				{
					break;
				}
				
				

			} catch (BadLocationException e)
			{
				e.printStackTrace();
				VdmUIPlugin.log("completion failed", e);
				// Document start reached, no tag found
				break;
			}
		}
		return new VdmCompletionContext(scanned.reverse());

	}
}
