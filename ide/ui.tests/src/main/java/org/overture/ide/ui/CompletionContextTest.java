package org.overture.ide.ui;

import org.eclipse.jface.text.Document;
import org.junit.Assert;
import org.junit.Test;
import org.overture.ide.ui.completion.CompletionUtil;
import org.overture.ide.ui.templates.SearchType;
import org.overture.ide.ui.templates.VdmCompletionContext;

public class CompletionContextTest
{
	private static final String SEARCH_TYPE_ERROR_MSG = "Incorrect search type";
	private static final String PROPOSAL_PREFIX_ERROR_MSG = "Incorrect proposal prefix";
	private static final String OFFSET_ERROR_MSG = "Incorrect offset";
	
	@Test
	public void testMK_tuples()
	{
		commonTest("let x = mk_(", 12, "(", SearchType.Mk);
	}
	
	@Test
	public void testMk_RecName()
	{
		commonTest("let x = mk_F", 12, "F", SearchType.Mk);
	}
	
	@Test
	public void testMk_RecEmpty()
	{
		commonTest("let x = mk_", 11, "", SearchType.Mk);
	}
	
	@Test
	public void testDot_Field()
	{
		commonTest("foo.bar", 7, "bar", SearchType.Dot);
	}

	@Test
	public void testDot_Op()
	{
		commonTest("foo.bar(", 8, "bar(", SearchType.Dot);
	}

	@Test
	public void testScanQuote()
	{
		commonTest("let a <q", 8, "<q", SearchType.Quote);
	}

	@Test
	public void testScanNew_NoName()
	{
		commonTest("let x = new ", 12, "", SearchType.New);
	}

	@Test
	public void testScanNew_NoSpace()
	{
		// It is expected we get types because we have no space after "new"
		commonTest("let x = new", 11, "", SearchType.Types);
	}

	@Test
	public void testScanNew_ClassPrefix()
	{
		commonTest("let x = new Foo", 15, "Foo", SearchType.New);
	}

	/**
	 * Common method for running tests on completion proposal contexts. This method checks for the proposal type, prefix
	 * and offset and fails if any of them do not match what is expected
	 * 
	 * @param docContent
	 *            the string content of the document where the proposal is requested
	 * @param docOffset
	 *            the cursor position in the document
	 * @param prefix
	 *            the expected proposal prefix to be suggested
	 * @param type
	 *            the expected proposal type to be suggested
	 */
	private void commonTest(String docContent, int docOffset, String prefix,
			SearchType type)
	{
		VdmCompletionContext ctxt = consVdmCompletionCtxt(docContent, docOffset);

		Assert.assertEquals(SEARCH_TYPE_ERROR_MSG, type, ctxt.getType());

		Assert.assertEquals(PROPOSAL_PREFIX_ERROR_MSG, prefix, ctxt.getProposalPrefix());

		Assert.assertEquals(OFFSET_ERROR_MSG, -prefix.length(), ctxt.getReplacementOffset());

	}

	private VdmCompletionContext consVdmCompletionCtxt(String docContent,
			int documentOffset)
	{
		Document doc = new Document(docContent);
		VdmCompletionContext ctxt = CompletionUtil.computeVdmCompletionContext(doc, documentOffset);
		return ctxt;
	}
}
