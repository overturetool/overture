package org.overture.core.npp;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import au.com.bytecode.opencsv.CSVReader;

/**
 * Test content (all symbols and keywords present) for {@link VdmNsTable}
 * 
 * @author ldc
 * 
 */
@RunWith(Parameterized.class)
public class VdmNsTableTestContent {

	public static final String ATTRIB_FILE = "src/test/resources/keyattrib/attribs";

	private String key;
	private String attrib;

	private VdmNsTable table;

	public VdmNsTableTestContent(String key, String attrib) {
		super();
		this.key = key;
		this.attrib = attrib;
	}

	@Parameters(name = "{index} : {0}")
	public static Collection<Object[]> testData() {

		List<Object[]> pairs = new Vector<Object[]>();

		pairs.addAll(readLines(ATTRIB_FILE));

		return pairs;
	}

	private static List<Object[]> readLines(String fileName) {
		List<Object[]> r = new Vector<Object[]>();
	
		CSVReader reader = null;

		try {

			reader = new CSVReader(new FileReader(fileName));
			r.addAll(reader.readAll());

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return r;
	}

	@Before
	public void setup() {
		table = VdmNsTable.getInstance();
	}

	@Test
	public void testKeyAttrib() {
		String actual = table.getAttribute(key);
		String expected = attrib;

		assertEquals(expected, actual);
	}

}
