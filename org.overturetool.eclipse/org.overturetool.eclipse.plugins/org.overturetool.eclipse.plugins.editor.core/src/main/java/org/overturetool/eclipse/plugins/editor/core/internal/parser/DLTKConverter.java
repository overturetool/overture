package org.overturetool.eclipse.plugins.editor.core.internal.parser;

public class DLTKConverter {
	String content;
	CodeModel model;
	private static class CodeModel {
		private String[] codeLines;

		private int[] codeLineLengths;

		
		public CodeModel(String code) {
			this.codeLines = code.split("\n");
			int count = this.codeLines.length;

			this.codeLineLengths = new int[count];

			int sum = 0;
			for (int i = 0; i < count; ++i) {
				this.codeLineLengths[i] = sum;
				sum += this.codeLines[i].length() + 1;
			}
		}

		public int[] getBounds(int lineNumber) {
			String codeLine = codeLines[lineNumber];
			char[] charLine = codeLine.toCharArray();
			int start = codeLineLengths[lineNumber];
			int end = start + codeLine.length();
			
			// TODO if tab is not 4 characters. 
			for (char c : charLine) {
				if (c == '\t'){
					start -= 3;
					end -= 3;
				}
			}
			return new int[] { start, end };
		}
	}
	

	public DLTKConverter(char[] content0) {
		this.content = new String( content0 );
		this.model = new CodeModel(content);
	}
	
	public int convert( int line, int offset ) {
		int[] bounds = this.model.getBounds(line-1);
		return bounds[0] + offset;
	}

	public int length() {
		return this.content.length();
	}
}
