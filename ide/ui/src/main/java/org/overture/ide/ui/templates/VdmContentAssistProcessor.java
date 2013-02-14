/*******************************************************************************
 * Copyright (c) 2009, 2011 Overture Team and others.
 *
 * Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The Overture Tool web-site: http://overturetool.org/
 *******************************************************************************/
package org.overture.ide.ui.templates;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.overture.ide.ui.editor.core.VdmDocument;
import org.overture.ide.ui.internal.viewsupport.VdmElementImageProvider;
import org.overture.ide.ui.templates.VdmContentAssistProcessor.VdmCompletionContext.SearchType;

public abstract class VdmContentAssistProcessor extends
		VdmTemplateAssistProcessor
{

	VdmElementImageProvider imgProvider = new VdmElementImageProvider();
	VdmCompleteProcesser processer = new VdmCompleteProcesser();

	public boolean enableTemplate()
	{
		return true;
	}

	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
			int offset)
	{
		List<ICompletionProposal> modList = new ArrayList<ICompletionProposal>();
		ICompletionProposal[] completionProposals = null;

		// IEditorInput editorInput = editor.getEditorInput();
		// String text = viewer.getTextWidget().getText();

		if (enableTemplate())
		{
			ICompletionProposal[] templates = super.computeCompletionProposals(viewer, offset);
			if (templates != null)
			{
				for (int i = 0; i < templates.length; i++)
				{
					modList.add(templates[i]);
				}

			}
		}

		if (viewer.getDocument() instanceof VdmDocument)
		{
			processer.computeCompletionProposals(computeVdmCompletionContext(viewer.getDocument(), offset), (VdmDocument) viewer.getDocument(), modList, offset);
		}

		if (modList.size() > 0)
			return (ICompletionProposal[]) modList.toArray(new ICompletionProposal[modList.size()]);

		return completionProposals;
	}

	public static class VdmCompletionContext
	{
		enum SearchType
		{
			Proposal, Field, Unknown, Type
		};

		boolean isEmpty = false;
		SearchType type = SearchType.Proposal;
		StringBuffer proposal = new StringBuffer();
		StringBuffer field = new StringBuffer();
		StringBuffer fieldType = new StringBuffer();
		boolean afterNew = false;
		boolean afterMk = false;

		public void add(char c)
		{
			switch (type)
			{
				case Field:
					field.append(c);
					break;
				case Proposal:
					proposal.append(c);
					break;
				case Type:
					fieldType.append(c);
					break;
				case Unknown:
					break;

			}

		}

		public void reverse()
		{
			proposal = proposal.reverse();
			field = field.reverse();
			// fieldType = fieldType.reverse();
		}

		@Override
		public String toString()
		{
			return "Type: \"" + fieldType + "\" " + (afterMk ? "mk_" : "")
					+ (afterNew ? "new " : "") + "\""
					+ (field.length() != 0 ? field + "." : "") + proposal
					+ "\"";
		}
	}

	private VdmCompletionContext computeVdmCompletionContext(IDocument doc,
			int documentOffset)
	{

		// Use string buffer to collect characters
		StringBuffer buf = new StringBuffer();
		char lastChar = '\0';
		VdmCompletionContext info = new VdmCompletionContext();
		while (true)
		{
			try
			{

				// Read character backwards
				char c = doc.getChar(--documentOffset);

				if (c == '.' && info.type == SearchType.Proposal)
				{
					info.type = SearchType.Field;
					continue;
				}
				if (Character.isWhitespace(c)
						&& info.type == SearchType.Proposal)
				{
					// Ok maybe this is a field, lets try to search for it
					info.field = info.proposal;
					info.proposal = new StringBuffer();
					info.type = SearchType.Field;
					// break;
				}

				if (info.type == SearchType.Field && Character.isWhitespace(c))
				{
					info.type = SearchType.Type;
					continue;
				}

				if (info.type == SearchType.Type)
				{
					if (Character.isWhitespace(c))
					{
						continue;
					}
					buf.append(c);
					// System.out.println("Buf: \""+buf+"\t\t\""+info.field+"\"");
					if (buf.length() >= info.field.length()
							&& buf.substring(buf.length() - info.field.length()).equals(info.field.toString()))
					{
						StringBuffer tmp = new StringBuffer(buf).reverse();
						int index = tmp.indexOf("=");
						int index2 = tmp.indexOf(":=");
						int length = info.field.length() + 1;
						if (index2 != -1 && index > index2)
						{
							index = index2;

						}

						if (index > 0 && length < index)
						{

							String tmp2 = tmp.substring(length, index);
							info.fieldType.append(tmp2);
						}
						break;
					}

				} else
				{
					info.add(c);
				}

				if (c == '=' && lastChar == '=')
				{
					break;
				}
				lastChar = c;

			} catch (BadLocationException e)
			{

				// Document start reached, no tag found
				return info;
			}
		}

		if (buf.length() >= 3)
		{
			if (buf.substring(0, 3).equals("wen"))
			{
				info.afterNew = true;
			}
			if (buf.substring(0, 3).equals("_km"))
			{
				info.afterMk = true;
			}
		}

		info.reverse();

		if (buf.length() >= 3 && info.field.length() >= 3
				&& info.field.substring(0, 3).equals("mk_"))
		{
			info.afterMk = true;
			info.field = info.field.delete(0, 3);
		}

		if (info.field.toString().trim().length() == 0)
		{
			info.isEmpty = true;
		}

		System.out.println(info);

		return info;
	}
}
