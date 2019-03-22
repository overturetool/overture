import org.junit.Assert;
import org.junit.Test;
import org.overture.codegen.runtime.Quote;
import org.overture.codegen.runtime.SetUtil;
import org.overture.codegen.runtime.Token;
import org.overture.codegen.runtime.Tuple;
import org.overture.codegen.runtime.Utils;
import org.overture.codegen.runtime.VDMSet;
import org.overture.codegen.runtime.VDMUtil;
import org.overture.codegen.runtime.ValueType;

public class VDMUtilTest {
	
	@Test(expected = UnsupportedOperationException.class)
	public void testUnsupportedType()
	{
		VDMUtil.seq_of_char2val("mk_token(\"sdadada\")", Token.class);
	}
	
	@Test
	public void natSuccess()
	{
		Tuple res = VDMUtil.seq_of_char2val("0", Utils.NAT);
		assertSuccessfulConversion(res, 0);
		
	}
	
	@Test
	public void natUnsuccessful()
	{
		Tuple res = VDMUtil.seq_of_char2val("-1", Utils.NAT);
		assertUnsuccessfulConversion(res);
	}
	
	@Test
	public void nat1Success()
	{
		Tuple res = VDMUtil.seq_of_char2val("42", Utils.NAT1);
		assertSuccessfulConversion(res, 42);
		
	}
	
	@Test
	public void nat1Unsuccessful()
	{
		Tuple res = VDMUtil.seq_of_char2val("0", Utils.NAT1);
		assertUnsuccessfulConversion(res);
	}
	
	@Test
	public void intSuccess()
	{
		Tuple res = VDMUtil.seq_of_char2val("-4", Utils.INT);
		assertSuccessfulConversion(res, -4);
		
	}
	
	@Test
	public void intUnsuccessful()
	{
		Tuple res = VDMUtil.seq_of_char2val("2.5", Utils.INT);
		assertUnsuccessfulConversion(res);
	}
	
	@Test
	public void ratSuccess()
	{
		Tuple res = VDMUtil.seq_of_char2val("-3.5", Utils.RAT);
		assertSuccessfulConversion(res, -3.5);
	}
	
	@Test
	public void ratUnsuccessful()
	{
		Tuple res = VDMUtil.seq_of_char2val("true", Utils.RAT);
		assertUnsuccessfulConversion(res);
	}
	
	@Test
	public void realSuccess()
	{
		Tuple res = VDMUtil.seq_of_char2val("1.2345", Utils.REAL);
		assertSuccessfulConversion(res, 1.2345);
	}
	
	@Test
	public void realUnsuccessful()
	{
		Tuple res = VDMUtil.seq_of_char2val("nil", Utils.REAL);
		assertUnsuccessfulConversion(res);
	}
	
	@Test
	public void boolSuccess()
	{
		Tuple res = VDMUtil.seq_of_char2val("true", Utils.BOOL);
		assertSuccessfulConversion(res, true);
	}
	
	@Test
	public void boolUnsuccessful()
	{
		Tuple res = VDMUtil.seq_of_char2val("nil", Utils.BOOL);
		assertUnsuccessfulConversion(res);
	}
	
	@Test
	public void charSuccess()
	{
		Tuple res = VDMUtil.seq_of_char2val("'x'", Utils.CHAR);
		assertSuccessfulConversion(res, 'x');
	}
	
	@Test
	public void charUnsuccessful()
	{
		Tuple res = VDMUtil.seq_of_char2val("'xx'", Utils.CHAR);
		assertUnsuccessfulConversion(res);
	}
	
	@Test
	public void quoteSuccess()
	{
		Tuple res = VDMUtil.seq_of_char2val("<A>", AQuote.getInstance());
		assertSuccessfulConversion(res, AQuote.getInstance());
	}
	
	@Test
	public void quoteUnsuccessful()
	{
		Tuple res = VDMUtil.seq_of_char2val("<B>", AQuote.getInstance());
		assertUnsuccessfulConversion(res);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void unionOfQuotesSuccessful()
	{
		VDMSet set = SetUtil.set();
		set.add(AQuote.getInstance());
		set.add(BQuote.getInstance());
		
		Tuple res = VDMUtil.seq_of_char2val("<B>", set);
		assertSuccessfulConversion(res, BQuote.getInstance());
		
		res = VDMUtil.seq_of_char2val("<A>", set);
		assertSuccessfulConversion(res, AQuote.getInstance());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void unionOfQuotesUnsuccessful()
	{
		VDMSet set = SetUtil.set();
		set.add(AQuote.getInstance());
		set.add(BQuote.getInstance());
		
		Tuple res = VDMUtil.seq_of_char2val("<C>", set);
		assertUnsuccessfulConversion(res);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void unionOfBoolNatSuccessful()
	{
		VDMSet set = SetUtil.set();
		set.add(Utils.BOOL);
		set.add(Utils.NAT);
		
		Tuple res = VDMUtil.seq_of_char2val("true", set);
		assertSuccessfulConversion(res, true);
		
		res = VDMUtil.seq_of_char2val("55", set);
		assertSuccessfulConversion(res, 55);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void unionOfBoolNatUnsuccessful()
	{
		VDMSet set = SetUtil.set();
		set.add(Utils.BOOL);
		set.add(Utils.NAT);
		
		Tuple res = VDMUtil.seq_of_char2val("2.5", set);
		assertUnsuccessfulConversion(res);
		
		res = VDMUtil.seq_of_char2val("nil", set);
		assertUnsuccessfulConversion(res);
	}
	
	private void assertSuccessfulConversion(Tuple res, Object expected)
	{
		checkResult(res, expected,  true);
	}

	private void assertUnsuccessfulConversion(Tuple res)
	{
		checkResult(res, null, false);
	}
	
	private void checkResult(Tuple res, Object expected, boolean expectSuccess) {
		
		Assert.assertTrue("Expected tuple to be of size 2", res.size() == 2);
		Object first = res.get(0);
		
		if(expectSuccess)
		{
			Assert.assertTrue("Expected conversion to be successful", first instanceof Boolean && (Boolean) first);
		}
		else
		{
			Assert.assertFalse("Expected conversion to be unsuccessful", first instanceof Boolean && (Boolean) first);
		}
		
		Object second = res.get(1);
		Assert.assertEquals("Got unexpected value", second, expected);
	}

	static class AQuote implements Quote
	{
		private static final long serialVersionUID = 1L;
		
		private static AQuote instance = null;
		
		public static AQuote getInstance() {
			
			if(instance == null)
			{
				instance = new AQuote();
			}
			
			return instance;
		}

		@Override
		public ValueType copy() {
			return this;
		}
	}
	
	static class BQuote implements Quote
	{
		private static final long serialVersionUID = 1L;
		
		private static BQuote instance = null;
		
		public static BQuote getInstance() {
			
			if(instance == null)
			{
				instance = new BQuote();
			}
			
			return instance;
		}

		@Override
		public ValueType copy() {
			return this;
		}
	}
}
