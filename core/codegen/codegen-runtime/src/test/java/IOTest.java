
import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.overture.codegen.runtime.IO;
import org.overture.codegen.runtime.SeqUtil;
import org.overture.codegen.runtime.Tuple;
import org.overture.codegen.runtime.VDMSeq;

public class IOTest
{
	@Test
	public void testSprintfStr()
	{
		VDMSeq args = SeqUtil.seq("tuple", Tuple.mk_(1, 2));
		String format = "A %s: %s";

		@SuppressWarnings("unchecked")
		String actual = IO.sprintf(format, args);
		String expected = "A tuple: mk_(1, 2)";

		Assert.assertEquals("Unexpected string returned from IO.sprintf", expected, actual);
	}

	@Test
	public void fechoNoParentDirs()
	{
		String filename = "target/path/to/file";

		new IO().fecho(filename, "Some message", null /* Works like passing <start> */);

		File file = new File(filename);
		Assert.assertTrue("Expected " + file.getAbsolutePath()
				+ " to exists", file.exists());
	}
}
