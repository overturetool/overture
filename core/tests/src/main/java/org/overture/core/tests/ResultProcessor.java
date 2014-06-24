package org.overture.core.tests;

import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.node.INode;
import org.overture.pog.pub.IProofObligation;
import org.overture.pog.pub.IProofObligationList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import eu.compassresearch.core.analysis.pog.utility.PogPubUtil;
import eu.compassresearch.pog.tests.PoResult;

/**
 * Helper Class for the POG test framework. Helps compare test outputs and results.
 * 
 * @author ldc
 */
public class ResultProcessor
{

	public <A> void compare(A actual, Result<A> resut){
		
	}

	public static void testCompare(String input, String result)
			throws IOException, AnalysisException, FileNotFoundException
	{
		List<INode> ast = InputProcessor.getAstFromName(input);
		IProofObligationList ipol = PogPubUtil.generateProofObligations(ast);

		Gson gson = new Gson();
		String json = IOUtils.toString(new FileReader(result));
		Type datasetListType = new TypeToken<Collection<PoResult>>()
		{
		}.getType();
		List<PoResult> results = gson.fromJson(json, datasetListType);

		ResultProcessor.checkSameElements(results, ipol);
	}
	
	public static void testUpdate(String input, String result)
			throws IOException, AnalysisException, FileNotFoundException
	{
		List<INode> ast = InputProcessor.getAstFromName(input);
		IProofObligationList ipol = PogPubUtil.generateProofObligations(ast);

		List<PoResult> prl = new LinkedList<PoResult>();

		for (IProofObligation po : ipol) {
			prl.add(new PoResult(po.getKindString(), po.getFullPredString()));
		}

		Gson gson = new Gson();
		String json = gson.toJson(prl);

		IOUtils.write(json, new FileOutputStream(result));
		
		System.out.println("\n" +result + " file updated \n");
		
	}

	private static void checkSameElements(List<PoResult> pRL,
			IProofObligationList ipol)
	{

		List<PoResult> prl_actual = new LinkedList<PoResult>();
		for (IProofObligation ipo : ipol)
		{
			prl_actual.add(new PoResult(ipo.getKindString(), ipo.getFullPredString()));
		}

		Collection<PoResult> stored_notfound = CollectionUtils.removeAll(pRL, prl_actual);
		Collection<PoResult> found_notstored = CollectionUtils.removeAll(prl_actual, pRL);

		if (stored_notfound.isEmpty() && found_notstored.isEmpty())
		{
			// do nothing
		} else
		{
			StringBuilder sb = new StringBuilder();
			if (!stored_notfound.isEmpty())
			{
				sb.append("Expected (but not found) POS: " + "\n");
				for (PoResult pr : stored_notfound)
				{
					sb.append(pr.toString() + "\n");
				}
			}
			if (!found_notstored.isEmpty())
			{
				sb.append("\n Found (but not expected) POS: " + "\n");
				for (PoResult pr : found_notstored)
				{
					sb.append(pr.toString() + "\n");
				}
			}
			fail(sb.toString());
		}
	}

}
