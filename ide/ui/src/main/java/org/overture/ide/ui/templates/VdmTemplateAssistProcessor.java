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
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateCompletionProcessor;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.TemplateException;
import org.eclipse.swt.graphics.Image;
import org.overture.ide.ui.VdmPluginImages;


public abstract class VdmTemplateAssistProcessor extends TemplateCompletionProcessor {

	@Override
	protected String extractPrefix(ITextViewer viewer, int offset) {
		int i = offset;
		IDocument document = viewer.getDocument();
		if (i > document.getLength())
			return "";
		try {
			while (i > 0) {
				char ch = document.getChar(i - 1);
				if (!Character.isJavaIdentifierPart(ch))
					break;
				i--;
			}
			if (i > 0) {
				int j = i;
				if (document.getChar(j - 1) == '<')
					i--;
			}
			return document.get(i, offset - i);
		} catch (BadLocationException e) {
			return "";
		}
	}

	protected Template[] getTemplates(String contextTypeId) {
		VdmTemplateManager manager = VdmTemplateManager.getInstance();
		return manager.getTemplateStore().getTemplates();
	}

	protected TemplateContextType getContextType(ITextViewer viewer,
			IRegion region) {
		VdmTemplateManager manager = VdmTemplateManager.getInstance();
		
		return manager.getContextTypeRegistry().getContextType(getTempleteContextType());
//				VdmUniversalTemplateContextType.CONTEXT_TYPE);
	}

	abstract protected String getTempleteContextType();

	protected Image getImage(Template template) {
		return VdmPluginImages.get(VdmPluginImages.IMG_OBJS_TEMPLATE);
	}

	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
			int offset) {
		ITextSelection selection = (ITextSelection) viewer
				.getSelectionProvider().getSelection();
		// adjust offset to end of normalized selection
		if (selection.getOffset() == offset)
			offset = selection.getOffset() + selection.getLength();
		String prefix = extractPrefix(viewer, offset);
		Region region = new Region(offset - prefix.length(), prefix.length());
		TemplateContext context = createContext(viewer, region);
		if (context == null)
			return new ICompletionProposal[0];
		context.setVariable("selection", selection.getText()); // name of the selection variables {line, word_selection //$NON-NLS-1$
		Template[] templates = getTemplates(context.getContextType().getId());
		List<ICompletionProposal> matches = new ArrayList<ICompletionProposal>();
		for (int i = 0; i < templates.length; i++) {
			Template template = templates[i];
			try {
				context.getContextType().validate(template.getPattern());
			} catch (TemplateException e) {
				continue;
			}
			if (!prefix.equals("") && (template.getName().startsWith(prefix) && template.matches(prefix, context.getContextType().getId()))){				
				matches.add(createProposal(template, context, (IRegion) region,	getRelevance(template, prefix)));
				
			}
		}
		return matches.toArray(new ICompletionProposal[matches.size()]);
	}

}
