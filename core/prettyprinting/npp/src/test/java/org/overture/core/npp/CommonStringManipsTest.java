package org.overture.core.npp;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class CommonStringManipsTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testWrap() {
		String input = "a string";
		String expected = "(a string)";
		String actual = Utilities.wrap(input);
		assertEquals(expected, actual);
	}

}
