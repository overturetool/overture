package org.overture.ide.core.utility;

import java.util.List;

import org.overturetool.vdmj.lex.LexLocation;

public class SourceLocationConverter
{
	String content;
	CodeModel model;

	private static class CodeModel
	{
		private String[] codeLines;

		private int[] codeLineLengths;

		public CodeModel(String code)
		{
			init(code);
		}
		 
		
		
		private void init(String code){
			this.codeLines = code.split("\n");
			int count = this.codeLines.length;

			this.codeLineLengths = new int[count];

			int sum = 0;
			for (int i = 0; i < count; ++i)
			{
				this.codeLineLengths[i] = sum;
				sum += this.codeLines[i].length() + 1;
			}
		}

		public int[] getBounds(int lineNumber)
		{
			if (lineNumber > 0 && codeLines.length > lineNumber)
			{
				String codeLine = codeLines[lineNumber];
				char[] charLine = codeLine.toCharArray();
				int start = codeLineLengths[lineNumber];
				int end = start + codeLine.length();

				// TODO if tab is not 4 characters.
				for (char c : charLine)
				{
					if (c == '\t')
					{
						start -= 3;
						end -= 3;
					}
				}
				return new int[] { start, end };
			} else
				return new int[] { 0, 0 };
		}
	}

	
	
	public SourceLocationConverter(char[] content)
	{
		this.content = new String(content);
		this.model = new CodeModel(this.content);
	}
	
	public SourceLocationConverter(List<Character> content) {
		this.content = new String(FileUtility.getCharContent(content));
		this.model = new CodeModel(this.content);
	}

	
	public int getStartPos(LexLocation location)
	{
		return this.convert(location.startLine, location.startPos -1);
	}
	
	public int getEndPos(LexLocation location)
	{
		return this.convert(location.endLine, location.endPos -1);
	}
	
	
	public int getStartPos(int line, int col)
	{
		return this.convert(line, col);
	}
	
	public int getEndPos(int line, int col)
	{
		return this.convert(line, col);
	}
	
	
	public int convert(int line, int offset)
	{
		int[] bounds = this.model.getBounds(line - 1);
		return bounds[0] + offset;
	}

	public int length()
	{
		return this.content.length();
	}
}
