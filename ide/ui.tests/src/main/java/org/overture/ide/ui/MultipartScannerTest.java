/*
 * #%~
 * org.overture.ide.tests.ui
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
package org.overture.ide.ui;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;
import org.junit.Assert;
import org.junit.Test;
import org.overture.ide.ui.editor.syntax.MultipleWordsWordRule;
import org.overture.ide.ui.editor.syntax.VdmWordDetector;

public class MultipartScannerTest
{
	public static final String[] multipleKeywords = { "is not yet specified",
			"for all", "in set", "be st", "not in set", "is subclass of",
			"instance variables", "is subclass responsibility" };

	public class TestScanner implements ICharacterScanner
	{
		String data;
		public int index;

		public TestScanner(String data)
		{
			this.data = data;
		}

		@Override
		public char[][] getLegalLineDelimiters()
		{
			return null;
		}

		@Override
		public int getColumn()
		{
			return 0;
		}

		@Override
		public int read()
		{
			if (index > data.length())
			{
				return -1;// EOF
			}
			return (int)data.codePointAt(index++);
		}

		@Override
		public void unread()
		{
			index--;
		}

		@Override
		public String toString()
		{
			return "Visible data : '" + data.substring(index)
					+ "'\nScanned data    : '" + data.substring(0, index)
					+ "'\nFull data    : '" + data + "'";
		}

	}

	public WordRule getMultupartRule()
	{
		IToken other = new Token("other");
		IToken keyword = new Token("kw");
		MultipleWordsWordRule multipleWordRule = new MultipleWordsWordRule(new VdmWordDetector(), other, false);
		for (int i = 0; i < multipleKeywords.length; i++)
		{
			multipleWordRule.addWord(multipleKeywords[i], keyword);
		}

		return multipleWordRule;
	}

	public WordRule getWordRule()
	{
		IToken other = new Token("other");
		IToken keyword = new Token("kw");
		WordRule multipleWordRule = new WordRule(new VdmWordDetector(), other);
		for (int i = 0; i < new VdmPpKeywords().getAllSingleWordKeywords().length; i++)
		{
			multipleWordRule.addWord(new VdmPpKeywords().getAllSingleWordKeywords()[i], keyword);
		}

		return multipleWordRule;
	}

	@Test
	public void testforallScanner()
	{
		TestScanner scanner = new TestScanner("for all\n\nend Test");

		IToken token = getMultupartRule().evaluate(scanner);
		System.out.println("Token : "+token.getData());

		System.out.println(scanner);
		Assert.assertEquals("for all".length(), scanner.index);//7 for 'for all'
	}
	
	@Test
	public void testScanner()
	{
		TestScanner scanner = new TestScanner(" class Test \n\n\nend Test");

		IToken token = getMultupartRule().evaluate(scanner);
		System.out.println("Token : "+token.getData());

		System.out.println(scanner);
		Assert.assertEquals(0, scanner.index);//0 since '  class...' doesn't match a word
	}

	@Test
	public void testWordScanner()
	{
		TestScanner scanner = new TestScanner("class Test \n\n\nend Test");

		IToken token = getWordRule().evaluate(scanner);
		System.out.println("Token : "+token.getData());

		System.out.println(scanner);
		Assert.assertEquals("class".length(), scanner.index);//5 for 'class'
	}
}
