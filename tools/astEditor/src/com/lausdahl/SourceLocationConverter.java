package com.lausdahl;

import java.util.List;

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

		private void init(String code)
		{
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
			lineNumber -= 1;
			if (lineNumber > 0 && codeLines.length > lineNumber)
			{
				String codeLine = codeLines[lineNumber];
				int start = codeLineLengths[lineNumber];
				int end = start + codeLine.length();

				return new int[] { start, end };
			} else
				return new int[] { 0, 0 };
		}

		public int getLineCount()
		{
			return this.codeLines.length;
		}
	}

	public SourceLocationConverter(char[] content)
	{
		this.content = new String(content).replaceAll("\r\n", "\n");
		this.model = new CodeModel(this.content);
	}

	public SourceLocationConverter(List<Character> content)
	{
		this.content = new String(FileUtility.getCharContent(content));
		this.model = new CodeModel(this.content);
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
		int[] bounds = this.model.getBounds(line);
		return bounds[0] + offset;
	}

	public int getLineCount()
	{
		return this.model.getLineCount();
	}

	public int length()
	{
		return this.content.length();
	}
}
