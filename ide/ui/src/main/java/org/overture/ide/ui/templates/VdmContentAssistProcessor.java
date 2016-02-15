/*
 * #%~
 * org.overture.ide.ui
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.ide.ui.templates;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.templates.TemplateContext;
import org.overture.ast.lex.VDMToken;
import org.overture.ide.ui.VdmUIPlugin;
import org.overture.ide.ui.editor.core.VdmDocument;
import org.overture.ide.ui.internal.viewsupport.VdmElementImageProvider;

public abstract class VdmContentAssistProcessor extends
		VdmTemplateAssistProcessor
{

	VdmElementImageProvider imgProvider = new VdmElementImageProvider();
	VdmCompleteProcesser processer = new VdmCompleteProcesser();

	public boolean enableTemplate()
	{
		return true;
	}

	/**
	 * @param offset an offset within the document for which completions should be computed
	 */
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
			int offset)
	{
		List<ICompletionProposal> modList = new ArrayList<ICompletionProposal>();
		ICompletionProposal[] completionProposals = null;

		// IEditorInput editorInput = editor.getEditorInput();
		// String text = viewer.getTextWidget().getText();
		
		//added here
		String prefix = extractPrefix(viewer, offset);
		Region region = new Region(offset - prefix.length(), prefix.length());
		
		if (viewer.getDocument() instanceof VdmDocument)
		{
			processer.computeCompletionProposals(computeVdmCompletionContext(viewer.getDocument(), offset), (VdmDocument) viewer.getDocument(), modList, offset,viewer, createContext(viewer, region));
		}
		
		if (enableTemplate())
		{
			ICompletionProposal[] templates = super.computeCompletionProposals(viewer, offset);
			
			TemplateContext context = createContext(viewer, region);
			if (context == null)
				
			if (templates != null)
			{
				for (int i = 0; i < templates.length; i++)
				{
					modList.add(templates[i]);
				}

			}
		}		
		
		if (modList.size() > 0)
		{
			return (ICompletionProposal[]) modList.toArray(new ICompletionProposal[modList.size()]);
		}

		return completionProposals;
	}

	VDMToken getToken(char c)
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

	private VdmCompletionContext computeVdmCompletionContext(IDocument doc,
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
				if ((token = getToken(c)) != null)// '`' == null
				{
					if (!(token == VDMToken.LT || token == VDMToken.POINT/* . */|| token == VDMToken.BRA /* ( */))
					{
						break;
					}
				}
				
				scanned.append(c);
				
				if(c=='n' && scanned.length()>3&& scanned.substring(scanned.length()-4, scanned.length()).matches("\\swen"))
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
