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
		String expected = "  ";
		String actual = it.getIndentation();
		assertEquals(expected, actual);
	}

	@Test
	public void testIndent_1Level_SingleLine() {
		String expected = "  1 line string";
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
		String expected = "  multi\n  line\n  string";
		String input = "multi\nline\nstring";
		String actual = it.indent(input);
		assertEquals(expected, actual);
	}

	@Test
	public void testIncrIndent() {
		String expected = "    ";
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
