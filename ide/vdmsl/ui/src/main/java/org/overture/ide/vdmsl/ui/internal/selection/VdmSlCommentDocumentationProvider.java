package org.overture.ide.vdmsl.ui.internal.selection;

import java.io.Reader;
import java.io.StringReader;

import org.eclipse.dltk.core.IBuffer;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.ui.documentation.IScriptDocumentationProvider;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;

public class VdmSlCommentDocumentationProvider implements
IScriptDocumentationProvider {

	protected String getLine(Document d, int line) throws BadLocationException {
		return d.get(d.getLineOffset(line), d.getLineLength(line));
	}
 
	protected String getHeaderComment(IMember member) {
		try {
			ISourceRange range = member.getSourceRange();
			if (range == null)
				return null;
 
			IBuffer buf = null;
 
			ISourceModule compilationUnit = member.getSourceModule();
			if (!compilationUnit.isConsistent()) {
				return null;
			}
 
			buf = compilationUnit.getBuffer();
 
			final int start = range.getOffset();
 
			String contents = buf.getContents();
 
			String result = "";
 
			Document doc = new Document(contents);
			try {
				int line = doc.getLineOfOffset(start);
				line--;
				if (line < 0)
					return null;
				boolean emptyEnd = true;
				while (line >= 0) {
					String curLine = getLine(doc, line);
					String curLineTrimmed = curLine.trim();
					if ((curLineTrimmed.length() == 0 && emptyEnd)
							|| curLineTrimmed.startsWith("--")) {
						if (curLineTrimmed.length() != 0)
							emptyEnd = false;
						result = curLine + result;
					} else
						break;
 
					line--;
				}
			} catch (BadLocationException e) {
				return null;
			}
 
			return result;
 
		} catch (ModelException e) {
		}
		return null;
	}
 
	public Reader getInfo(IMember member, boolean lookIntoParents,
			boolean lookIntoExternal) {
		String header = getHeaderComment(member);
		return new StringReader(convertToHTML(header));
	}
 
	protected String convertToHTML(String header) {
		StringBuffer result = new StringBuffer();
		// result.append("<p>\n");
		Document d = new Document(header);
		for (int line = 0;; line++) {
			try {
				String str = getLine(d, line).trim();
				if (str == null)
					break;
				while (str.length() > 0 && str.startsWith("#"))
					str = str.substring(1);
				while (str.length() > 0 && str.endsWith("#"))
					str = str.substring(0, str.length() - 1);
				if (str.length() == 0)
					result.append("<p>");
				else {
					if (str.trim().matches("\\w*:")) {
						result.append("<h4>");
						result.append(str);
						result.append("</h4>");
					} else
						result.append(str + "<br>");
				}
			} catch (BadLocationException e) {
				break;
			}
 
		}
		return result.toString();
	}
 
	public Reader getInfo(String content) {
		return null;
	}

}
