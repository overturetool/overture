/*
 * #%~
 * New Pretty Printer
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
package org.overture.core.npp;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

public class IndentTrackerTest {

	IndentTracker it;

	@Before
	public void setUp() throws Exception {
		it = new IndentTracker();
		it.incrIndent();
	}

	@Test
	public void testGetIndentation() {
		String expected = "\t";
		String actual = it.getIndentation();
		assertEquals(expected, actual);
	}

	@Test
	public void testIndent_1Level_SingleLine() {
		String expected = "\t1 line string";
		String input = "1 line string";
		String actual = it.indent(input);
		assertEquals(expected, actual);
	}
	
	@Test public void testIndent_0Level(){
		String expected = "string";
		String input = "string";
		it.resetIndent(); // we increment the level once in setup so must reset here
		String actual = it.indent(input);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testIndent_1Level_MultiLine() {
		String expected = "\tmulti\n\tline\n\tstring";
		String input = "multi\nline\nstring";
		String actual = it.indent(input);
		assertEquals(expected, actual);
	}

	@Test
	public void testIncrIndent() {
		String expected = "\t\t";
		it.incrIndent();
		String actual = it.getIndentation();
		assertEquals(expected, actual);
	}

	@Test
	public void testIndentTracker() {
		IndentTracker it2 = new IndentTracker();
		assertNotNull(it2);
	}

	@Test
	public void testResetIndent() {
		String expected = "";
		it.resetIndent();
		String actual = it.getIndentation();
		assertEquals(expected, actual);
	}

}
