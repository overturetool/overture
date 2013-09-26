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
package org.overture.ide.core.utility;

import java.util.List;

import org.overture.ast.intf.lex.ILexLocation;


public class SourceLocationConverter
{
	
	CodeModel model;

	private static class CodeModel
	{
		public final String content;
		private String[] codeLines;

		private int[] codeLineLengths;

		public CodeModel(String code)
		{
			this.content = code;
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
//				char[] charLine = codeLine.toCharArray();
				int start = codeLineLengths[lineNumber];
				int end = start + codeLine.length();

				// TODO if tab is not 4 characters.
//				for (char c : charLine)
//				{
//					if (c == '\t')
//					{
//						start -= 3;
//						end -= 3;
//					}
//				}
				return new int[] { start, end };
			} else
				return new int[] { 0, 0 };
		}
	}

	
	@Deprecated
	public SourceLocationConverter(char[] content)
	{
//		this.content = new String(content);
		this.model = new CodeModel(new String(content));
	}
	
	@Deprecated
	public SourceLocationConverter(List<Character> content) {
//		this.content = new String(FileUtility.getCharContent(content));
		this.model = new CodeModel(new String(FileUtility.getCharContent(content)));
	}
	
	public SourceLocationConverter(String content)
	{
//		this.content = new String(content);
		this.model = new CodeModel(content);
	}

	
	public int getStartPos(ILexLocation location)
	{
		return this.convert(location.getStartLine(), location.getStartPos() -1);
	}
	
	public int getEndPos(ILexLocation location)
	{
		return this.convert(location.getEndLine(), location.getEndPos() -1);
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
		return this.model.content.length();
	}
}
