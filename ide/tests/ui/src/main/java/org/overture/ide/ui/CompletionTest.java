package org.overture.ide.ui;

import org.eclipse.jface.text.Document;
import org.junit.Assert;
import org.junit.Test;
import org.overture.ide.ui.completion.CompletionUtil;
import org.overture.ide.ui.templates.SearchType;
import org.overture.ide.ui.templates.VdmCompletionContext;

public class CompletionTest
{
	@Test
	public void testScanQuote()
	{
		String docContent = "let a <q";
		int documentOffset = 8;
		
		VdmCompletionContext ctxt = consVdmCompletionCtxt(docContent, documentOffset);

		// Search for quotes
		Assert.assertEquals(SearchType.Quote,ctxt.type);
		
		// The prefix of the proposal
		Assert.assertEquals("<q", ctxt.proposalPrefix);
		
		// The index before '<q'
		Assert.assertEquals(-2/*index before <q*/, ctxt.offset);
	}

	private VdmCompletionContext consVdmCompletionCtxt(String docContent, int documentOffset)
	{
		Document doc = new Document(docContent);
		VdmCompletionContext ctxt = CompletionUtil.computeVdmCompletionContext(doc, documentOffset);
		return ctxt;
	}
}
