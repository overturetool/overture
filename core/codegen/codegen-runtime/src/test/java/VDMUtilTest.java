import org.junit.Assert;
import org.junit.Test;
import org.overture.codegen.runtime.SeqUtil;
import org.overture.codegen.runtime.SetUtil;
import org.overture.codegen.runtime.VDMSeq;
import org.overture.codegen.runtime.VDMUtil;

public class VDMUtilTest
{
	@Test(expected = IllegalArgumentException.class)
	public void setToSeqWrongArg()
	{
		VDMUtil.set2seq(SeqUtil.seq());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void setToSeqNats()
	{
		VDMSeq seq = VDMUtil.set2seq(SetUtil.set(1L, 2L, 3L));
		
		Assert.assertTrue("Expected sequence size 3", seq.size() == 3);
		Assert.assertTrue("Expected members 1,2,3", seq.containsAll(SeqUtil.seq(1L, 2L, 3L)));
	}
}
