/**
 * 
 */
package org.overturetool.eclipse.plugins.editor.internal.ui.text;

import org.eclipse.dltk.ui.editor.highlighting.AbstractSemanticHighlighter;


final class OverturePositionUpdater extends AbstractSemanticHighlighter {

	private static final int HL_XML_TAG = 0;
	private static final int HL_XML_ATTRIBUTE = 1;
	private static final int HL_XML_COMMENT = 1;


	//
	protected boolean doHighlighting(
			org.eclipse.dltk.compiler.env.ISourceModule code) throws Exception {
		char[] sourceAsCharArray = code.getContentsAsCharArray();
		
		return false;
		/***
		 * implementation from org.eclipse.dltk.overture 
		 */
		/*Parser p = new Parser(new CompilerEnvirons(), new ErrorReporter() {

			public void error(String message, String sourceName, int line,
					String lineSource, int offset) {
				// TODO Auto-generated method stub

			}

			//TODO: create EvaluatorException which properly should extend VDMJExeption
			public EvaluatorException runtimeError(String message,
					String sourceName, int line, String lineSource,
					int lineOffset) {
				// TODO Auto-generated method stub
				return null;
			}

			public void warning(String message, String sourceName, int line,
					String lineSource, int lineOffset) {
				// TODO Auto-generated method stub
			}

		});

		try {
			p.setXMLCallback(new IXMLCallback() {

				public void xmlTokenStart(int offset, String tagName, int cursor) {
					int i = cursor - offset + 1;
					if (tagName.length() != i) {
						StringBuffer copy = new StringBuffer();
						for (int a = 0; a < tagName.length(); a++) {
							char c = tagName.charAt(a);
							if (c == '\n')
								copy.append("  "); //$NON-NLS-1$
							else
								copy.append(c);
						}
						tagName = copy.toString();
					}

					XMLTokenizer r = new XMLTokenizer(new StringReader(tagName));
					List l = r.getRegions();
					for (int a = 0; a < l.size(); a++) {
						Token object = (Token) l.get(a);
						if (object.context == XMLTokenizer.XML_TAG_NAME) {
							addRange(offset - 1 + object.start,
									object.textLength, HL_XML_TAG);
						} else if (object.context == XMLTokenizer.XML_TAG_ATTRIBUTE_NAME) {
							addRange(offset - 1 + object.start,
									object.textLength, HL_XML_ATTRIBUTE);
						} else if (object.context == XMLTokenizer.XML_COMMENT_OPEN
								|| object.context == XMLTokenizer.XML_COMMENT_TEXT
								|| object.context == XMLTokenizer.XML_COMMENT_CLOSE) {
							addRange(offset - 1 + object.start,
									object.textLength, HL_XML_COMMENT);
						} else if (object.context == XMLTokenizer.XML_TAG_ATTRIBUTE_VALUE) {

						} else {
							// result.add(presenter
							// .createHighlightedPosition(
							// offset - 1
							// + object.start,
							// object.textLength,
							// highlightings[3]));
						}
					}
					// result.add(presenter.createHighlightedPosition(
					// offset - 1, i,
					// highlightings[0]));
				}

			});
			p.parse(new CharArrayReader(sourceAsCharArray), this.toString(), 0);
		} catch (IOException e) {
			return false;
		}
		// result.add(presenter.createHighlightedPosition(offset , length,
		// highlightings[0]));

		return true;
		*/
		
	}

	private void addRange(int start, int len, int highlightingIndex) {
		addPosition(start, start + len, highlightingIndex);
	}

}
