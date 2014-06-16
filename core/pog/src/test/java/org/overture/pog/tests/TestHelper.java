package org.overture.pog.tests;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.parser.lex.LexException;
import org.overture.parser.syntax.ParserException;
import org.overture.pog.pub.IProofObligation;
import org.overture.pog.pub.IProofObligationList;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

public abstract class TestHelper
{

	/**
	 * Compare a proof obligation list and a list of po result holders. This method checks if both lists hold the same
	 * elements, regardless of order. The comparison of elements is done with
	 * {@link #samePO(PoResult, IProofObligation)}. <br>
	 * <br>
	 * This method will trigger a test failure in case the lists are different (the difference will be described in the
	 * failure message).
	 * 
	 * @param pRL
	 *            a stored list of {@link PoResult}
	 * @param ipol
	 *            the generated {@link IProofObligationList}
	 *            
	 * @return true if lists hold same elements, false otherwise
	 */
	public static void checkSameElements(List<PoResult> pRL,
			IProofObligationList ipol)
	{

		List<PoResult> prl_actual = new LinkedList<PoResult>();
		for (IProofObligation ipo : ipol)
		{
			prl_actual.add(new PoResult(ipo.getKindString(), ipo.getValue()));
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
				sb.append("Found (but not expected) POS: " + "\n");
				for (PoResult pr : found_notstored)
				{
					sb.append(pr.toString() + "\n");
				}
			}
			fail(sb.toString());
		}
	}

	/**
	 * Compare a proof obligation and stored po result to check if they represent the same PO. This comparison is
	 * implemented as a string comparison of the kinds and expression values.
	 * 
	 * @param pR
	 *            the stored {@link PoResult}
	 * @param ipo
	 *            the generated {@link IProofObligation}
	 * @return true if same PO represented. false otherwise
	 */
	public static boolean samePO(PoResult pR, IProofObligation ipo)
	{
		if (pR.getPoExp().equals(ipo.getValue()))
		{
			return pR.getPoKind().endsWith(ipo.getKindString());
		} else
		{
			return false;
		}
	}
/**
 * Parse and type check a vdm source file and return the model AST.
 * @param name the filename of the VDM model
 * @return the AST of the model
 */
	public static List<INode> getAstFromName(String name) throws IOException,
			ParserException, LexException
	{
		String ext = name.split("\\.")[1];

		boolean switchRelease = false;
		try
		{
			if (name.contains("vdm10release"))
			{
				switchRelease = true;
				Settings.release = Release.VDM_10;
			}

			if (ext.equals("vdmsl"))
			{
				return parseTcSl(name);
			}

			else
			{
				if (ext.equals("vdmpp"))
				{
					return parseTcPp(name);
				} else
				{
					if (ext.equals("vdmrt"))
					{
						return parseTcRt(name);
					} else
					{
						fail("Unexpected extension in file " + name
								+ ". Only .vdmpp, .vdmsl and .vdmrt allowed");
					}
				}
			}
		} finally
		{
			if (switchRelease)
			{
				Settings.release = Release.DEFAULT;
			}
		}
		// only needed to compile. will never hit because of fail()
		return null;
	}

	// These 3 methods have so much duplicated code because we cannot
	// return the TC results since their types are all different.
	private static List<INode> parseTcPp(String name)
	{
		Settings.dialect = Dialect.VDM_PP;
		File f = new File(name);

		TypeCheckResult<List<SClassDefinition>> TC = TypeCheckerUtil.typeCheckPp(f);

		assertTrue("Specification has parse errors", TC.parserResult.errors.isEmpty());
		assertTrue("Specification has type errors", TC.errors.isEmpty());

		List<INode> r = new LinkedList<INode>();
		r.addAll(TC.result);

		return r;
	}

	private static List<INode> parseTcSl(String name)
	{
		Settings.dialect = Dialect.VDM_SL;
		File f = new File(name);

		TypeCheckResult<List<AModuleModules>> TC = TypeCheckerUtil.typeCheckSl(f);

		assertTrue("Specification has parse errors", TC.parserResult.errors.isEmpty());
		assertTrue("Specification has type errors", TC.errors.isEmpty());

		List<INode> r = new LinkedList<INode>();
		r.addAll(TC.result);

		return r;
	}

	private static List<INode> parseTcRt(String name) throws ParserException,
			LexException
	{
		Settings.dialect = Dialect.VDM_RT;
		File f = new File(name);

		TypeCheckResult<List<SClassDefinition>> TC = TypeCheckerUtil.typeCheckRt(f);

		assertTrue("Specification has parse errors", TC.parserResult.errors.isEmpty());
		assertTrue("Specification has type errors", TC.errors.isEmpty());

		List<INode> r = new LinkedList<INode>();
		r.addAll(TC.result);

		return r;
	}

}
