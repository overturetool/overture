/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Andrei Sobolev)
 *     xored software, Inc. - RubyDocumentation display improvements (Alex Panchenko <alex@xored.com>)
 *******************************************************************************/
package org.overturetool.eclipse.plugins.editor.internal.ui.documentation;

import java.io.Reader;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.dltk.ast.Modifiers;
import org.eclipse.dltk.core.IField;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.core.BuiltinProjectFragment;
import org.eclipse.dltk.ui.documentation.IScriptDocumentationProvider;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.overturetool.eclipse.plugins.editor.core.model.FakeField;
import org.overturetool.eclipse.plugins.editor.core.utils.PredefinedVariables;
import org.overturetool.eclipse.plugins.editor.internal.ui.text.OverturePartitionScanner;
import org.overturetool.eclipse.plugins.editor.ui.text.IOverturePartitions;

public class OvertureDocumentationProvider implements IScriptDocumentationProvider {

	private static final String PUBLIC = "public"; //$NON-NLS-1$

	private static final String PROTECTED = "protected"; //$NON-NLS-1$

	private static final String PRIVATE = "private"; //$NON-NLS-1$

	protected String getLine(Document d, int line) throws BadLocationException {
		return d.get(d.getLineOffset(line), d.getLineLength(line));
	}

	/**
	 * Installs a partitioner with <code>document</code>.
	 * 
	 * @param document
	 *            the document
	 */
	private static void installStuff(Document document) {
		String[] types = new String[] { 
				IOverturePartitions.OVERTURE_STRING,
				IOverturePartitions.OVERTURE_COMMENT,
				IOverturePartitions.OVERTURE_DOC,
				IDocument.DEFAULT_CONTENT_TYPE };
		
		FastPartitioner partitioner = new FastPartitioner(new OverturePartitionScanner(), types);
		partitioner.connect(document);
		document.setDocumentPartitioner(IOverturePartitions.OVERTURE_PARTITIONING, partitioner);
	}

	/**
	 * Removes partitioner with <code>document</code>.
	 * 
	 * @param document
	 *            the document
	 */
	private static void removeStuff(Document document) {
		document.setDocumentPartitioner(IOverturePartitions.OVERTURE_PARTITIONING, null);
	}

	private static int findOffsetBeforeMethod(Document doc, int start)
			throws BadLocationException {
		int line = doc.getLineOfOffset(start);
		for (;;) {
			if (--line < 0) {
				throw new BadLocationException();
			}
			final IRegion r = doc.getLineInformation(line);
			if (r.getLength() == 0) {
				continue;
			}
			String s = doc.get(r.getOffset(), r.getLength());
			if (isBlank(s)) {
				continue;
			}
			s = s.trim();
			if (PUBLIC.equals(s) || PROTECTED.equals(s) || PRIVATE.equals(s)) {
				/**
				 * skip access modifiers between method and comment, e.g.
				 * 
				 * <code>
				 * # foo-method	documentation 
				 * public 
				 * def foo 
				 * end
				 * </code>
				 */
				continue;
			}
			return r.getOffset() + r.getLength() - 1;
		}
	}

	public static String getHeaderComment(String contents, int offset) {
		int start = offset;
		int end = start;

		Document doc = new Document(contents);
		installStuff(doc);

		try {
			int pos = 0;
			if (start > 0) {
				pos = findOffsetBeforeMethod(doc, start);
			}
			while (pos >= 0 && pos <= doc.getLength()) {
				ITypedRegion region = TextUtilities.getPartition(doc,
						IOverturePartitions.OVERTURE_PARTITIONING, pos, true);
				if (
						region.getType().equals(IOverturePartitions.OVERTURE_DOC)
						|| region.getType().equals(IOverturePartitions.OVERTURE_COMMENT)
					) {
					start = region.getOffset();
				}
				if (region.getType().equals(IDocument.DEFAULT_CONTENT_TYPE)) {
					String content = doc.get(region.getOffset(), region.getLength()).trim();
					if (content.length() > 0 && !content.startsWith("public") //$NON-NLS-1$
							&& !content.startsWith("protected") //$NON-NLS-1$
							&& !content.startsWith("private")) //$NON-NLS-1$
						break;
				}
				pos = region.getOffset() - 1;
			}

			pos = start + 1;

			while (pos <= doc.getLength()) {
				ITypedRegion region = TextUtilities.getPartition(doc,
						IOverturePartitions.OVERTURE_PARTITIONING, pos, true);
				if (region.getType().equals(IOverturePartitions.OVERTURE_DOC)
						|| region.getType().equals(IOverturePartitions.OVERTURE_COMMENT)) {
					end = region.getOffset() + region.getLength();
				}
				if (region.getType().equals(IDocument.DEFAULT_CONTENT_TYPE)) {
					String content = doc.get(region.getOffset(), region.getLength()).trim();
					if (content.length() > 0 && !content.startsWith("public") //$NON-NLS-1$
							&& !content.startsWith("protected") //$NON-NLS-1$
							&& !content.startsWith("private")) //$NON-NLS-1$
						break;
				}
				pos = region.getOffset() + region.getLength() + 1;
			}

			if (end >= doc.getLength())
				end = doc.getLength() - 1;

			return doc.get(start, end - start);

		} catch (BadLocationException e1) {
			return null;
		} finally {
			removeStuff(doc);
		}
	}

	protected String getHeaderComment(IMember member) {
		if (member instanceof IField) {
			try {
				if ((member.getFlags() & Modifiers.AccConstant) == 0) {
					return null;
				}
			} catch (ModelException e) {
				return null;
			}
		}
		try {
			ISourceRange range = member.getSourceRange();
			if (range == null)
				return null;
			int offset = range.getOffset();

			ISourceModule sourceModule = member.getSourceModule();
			if (!sourceModule.isConsistent()) {
				return null;
			}
			String contents = sourceModule.getSource();

			return OvertureDocumentationProvider.getHeaderComment(contents, offset);

		} catch (ModelException e) {
		}
		return null;
	}

	private Reader proccessBuiltinType(IType type) {
//		String keyword = type.getElementName();
//		RiHelper helper = RiHelper.getInstance();
//		String doc = helper.getDocFor(keyword);
//		if (doc != null)
//			return new StringReader(doc);
		return null;
	}

	private static final String NOTHING_KNOWN_ABOUT = "Nothing known about"; //$NON-NLS-1$

	private Reader proccessBuiltinMethod(IMethod method) {
//		final String divider = "#"; //$NON-NLS-1$
//		IModelElement pp = method.getAncestor(IModelElement.TYPE);
//		if (pp.getElementName().startsWith("<<")) //$NON-NLS-1$
//			pp = pp.getAncestor(IModelElement.TYPE);
//		String keyword = pp.getElementName() + divider
//				+ method.getElementName();
//		RiHelper helper = RiHelper.getInstance();
//		String doc = helper.getDocFor(keyword);
//		if (doc != null
//				&& (doc.indexOf(NOTHING_KNOWN_ABOUT) >= 0 || isBlank(doc))) {
//			// XXX megafix: some Kernel methods are documented in Object
//			if (pp.getElementName().equals("Kernel")) { //$NON-NLS-1$
//				keyword = "Object" + divider + method.getElementName(); //$NON-NLS-1$
//				doc = helper.getDocFor(keyword);
//				if (doc != null && doc.indexOf(NOTHING_KNOWN_ABOUT) >= 0) {
//					doc = null;
//				}
//			} else {
//				doc = null;
//			}
//
//		}
//		if (doc != null)
//			return new StringReader(doc);
		return null;
	}

	public Reader getInfo(IMember member, boolean lookIntoParents,
			boolean lookIntoExternal) {
		boolean isBuiltin = member.getAncestor(IModelElement.PROJECT_FRAGMENT) instanceof BuiltinProjectFragment;
		if (isBuiltin && member instanceof IMethod) {
			IMethod method = (IMethod) member;
			return proccessBuiltinMethod(method);
		} else if (isBuiltin && member instanceof IType) {
			IType type = (IType) member;
			return proccessBuiltinType(type);
		} else if (member instanceof FakeField) {
			FakeField field = (FakeField) member;
			String doc = PredefinedVariables.getDocOf(field.getElementName());
			if (doc != null)
				return new StringReader(doc);
		}
		String header = getHeaderComment(member);
		if (header == null || header.length() == 0)
			return null;
		return new StringReader(convertToHTML(header));
	}

	private static String replaceSpecTag(String original, String sc, String tag) {
		String filtered = original;
		if (sc.equals("*") || sc.equals("+")) //$NON-NLS-1$ //$NON-NLS-2$
			sc = "\\" + sc; //$NON-NLS-1$
		Pattern bold = Pattern.compile(sc + "[_a-zA-Z0-9]+" + sc); //$NON-NLS-1$
		while (true) {
			Matcher matcher = bold.matcher(filtered);
			if (matcher.find()) {
				String startStr = filtered.substring(0, matcher.start());
				String endStr = filtered.substring(matcher.end());
				String grp = matcher.group();
				filtered = startStr + "<" + tag + ">" //$NON-NLS-1$ //$NON-NLS-2$
						+ grp.substring(1, grp.length() - 1) + "</" + tag + ">" //$NON-NLS-1$ //$NON-NLS-2$
						+ endStr;
			} else
				break;
		}

		return filtered;
	}

	protected String convertToHTML(String header) {
		if (header == null)
			return ""; //$NON-NLS-1$
		StringBuffer result = new StringBuffer();
		Document d = new Document(header);
		boolean enabled = true;
		for (int line = 0;; line++) {
			try {
				String str = getLine(d, line).trim();
				if (str == null)
					break;
				if (!enabled)
					continue;

				if (str.startsWith("=begin")) //$NON-NLS-1$
					continue;

				if (str.startsWith("=end")) //$NON-NLS-1$
					continue;

				while (str.length() > 0 && str.startsWith("--")) //$NON-NLS-1$
					str = str.substring(2);
				while (str.length() > 0 && str.startsWith("/*"))
					str = str.substring(2);
				while (str.length() > 0 && str.startsWith("*"))
					str = str.substring(1);
				while (str.length() > 0 && str.startsWith("/"))
					str = str.substring(1);
				
				str = replaceSpecTag(str, "*", "b"); //$NON-NLS-1$ //$NON-NLS-2$
				str = replaceSpecTag(str, "+", "tt"); //$NON-NLS-1$ //$NON-NLS-2$
				str = replaceSpecTag(str, "_", "em"); //$NON-NLS-1$ //$NON-NLS-2$

				str.replaceAll("\\*[_a-zA-Z0-9]+\\*", ""); //$NON-NLS-1$ //$NON-NLS-2$

				if (str.length() == 0)
					result.append("<p>"); //$NON-NLS-1$
				else {
					if (str.trim().startsWith("== ")) { //$NON-NLS-1$
						result.append("<h2>"); //$NON-NLS-1$
						result.append(str.substring(3));
						result.append("</h2>"); //$NON-NLS-1$
					} else if (str.trim().startsWith("= ")) { //$NON-NLS-1$
						result.append("<h1>"); //$NON-NLS-1$
						result.append(str.substring(2));
						result.append("</h1>"); //$NON-NLS-1$
					} else if (str.trim().startsWith("---")) { //$NON-NLS-1$
						result.append("<hr>"); //$NON-NLS-1$
					} else {
						result.append(str + "<br>"); //$NON-NLS-1$
					}
				}
			} catch (BadLocationException e) {
				break;
			}

		}
		// result.append("</p>\n");
		return result.toString();
	}

	public Reader getInfo(String content) {
		return null;
	}

	/**
	 * Checks if a String is whitespace, empty ("") or null.
	 * 
	 * @param str
	 *            the String to check, may be null
	 * @return <code>true</code> if the String is null, empty or whitespace
	 */
	private static boolean isBlank(String s) {
		if (s != null) {
			final int len = s.length();
			if (len != 0) {
				for (int i = 0; i < len; i++) {
					if (!Character.isWhitespace(s.charAt(i))) {
						return false;
					}
				}
			}
		}
		return true;
	}

}
