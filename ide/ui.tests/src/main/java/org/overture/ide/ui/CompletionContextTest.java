package org.overture.ide.ui;

import org.eclipse.jface.text.Document;
import org.junit.Assert;
import org.junit.Test;
import org.overture.ide.ui.completion.CompletionUtil;
import org.overture.ide.ui.templates.SearchType;
import org.overture.ide.ui.templates.VdmCompletionContext;

public class CompletionTest
{
	private static final String SEARCH_TYPE_ERROR_MSG = "Incorrect search type";
	private static final String PROPOSAL_PREFIX_ERROR_MSG = "Incorrect proposal prefix";
	private static final String OFFSET_ERROR_MSG = "Incorrect offset";

	@Test
	public void testScanQuote()
	{
		String docContent = "let a <q";
		int documentOffset = 8;
		
		VdmCompletionContext ctxt = consVdmCompletionCtxt(docContent, documentOffset);

		// Search for quotes
		Assert.assertEquals(SEARCH_TYPE_ERROR_MSG, SearchType.Quote,ctxt.type);
		
		// The prefix of the proposal
		Assert.assertEquals(PROPOSAL_PREFIX_ERROR_MSG, "<q", ctxt.proposalPrefix);
		
		// The index before '<q'
		Assert.assertEquals(OFFSET_ERROR_MSG, -2/*index before <q*/, ctxt.offset);
	}
	
	@Test
	public void testScanNew_NoName(){
		String docContent = "let x = new ";
		int documentOffset = 12;
		
		VdmCompletionContext ctxt = consVdmCompletionCtxt(docContent, documentOffset);
		
		// search for constructors
		Assert.assertEquals(SEARCH_TYPE_ERROR_MSG, SearchType.New, ctxt.type);
		
		// check the prefix of the proposal
		Assert.assertEquals(PROPOSAL_PREFIX_ERROR_MSG, "", ctxt.proposalPrefix);
		
		// The index before proposal
		Assert.assertEquals(OFFSET_ERROR_MSG, 0, ctxt.offset);
	}
	
	@Test
	public void testScanNew_NoSpace(){
		String docContent = "let x = new";
		int documentOffset = 11;
		
		// 'new' without space should not propose constructor. Only types
		VdmCompletionContext ctxt = consVdmCompletionCtxt(docContent, documentOffset);
		
		// search for constructors
		Assert.assertEquals(SEARCH_TYPE_ERROR_MSG, SearchType.Types, ctxt.type);
		
		// check the prefix of the proposal
		Assert.assertEquals(PROPOSAL_PREFIX_ERROR_MSG, "", ctxt.proposalPrefix);
		
		// The index before proposal
		Assert.assertEquals(OFFSET_ERROR_MSG, 0, ctxt.offset);
	}
	
	
	@Test
	public void testScanNew_ClassPrefix(){
		String docContent = "let x = new Foo";
		int documentOffset = 15;
		
		// 'new' with some text should add it to the prefix
		VdmCompletionContext ctxt = consVdmCompletionCtxt(docContent, documentOffset);
		
		// search for constructors
		Assert.assertEquals(SEARCH_TYPE_ERROR_MSG, SearchType.New, ctxt.type);
		
		// check the prefix of the proposal
		Assert.assertEquals(PROPOSAL_PREFIX_ERROR_MSG, "Foo", ctxt.proposalPrefix);
		
		// The index before proposal
		Assert.assertEquals(OFFSET_ERROR_MSG, -3, ctxt.offset);
	}
	
	
	

	private VdmCompletionContext consVdmCompletionCtxt(String docContent, int documentOffset)
	{
		Document doc = new Document(docContent);
		VdmCompletionContext ctxt = CompletionUtil.computeVdmCompletionContext(doc, documentOffset);
		return ctxt;
	}
}
