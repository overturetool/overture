package org.overture.pog.tests;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.PExp;
import org.overture.ast.lex.Dialect;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.parser.util.ParserUtil;
import org.overture.parser.util.ParserUtil.ParserResult;
import org.overture.pof.AVdmPoTree;
import org.overture.pog.obligation.POContextStack;
import org.overture.pog.obligation.ProofObligationList;
import org.overture.pog.pub.IProofObligation;
import org.overture.pog.visitors.PogVisitor;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

/**
 * @author battlenc
 */
public class AdHocTest extends TestCase
{
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		Settings.dialect = Dialect.VDM_SL;
		Settings.release = Release.VDM_10;
	}

	private ProofObligationList pogSL(File file) throws AnalysisException
	{
		System.out.println("Processing " + file);
		
		TypeCheckResult<List<AModuleModules>> TC = TypeCheckerUtil.typeCheckSl(file);
		assertTrue("Specification has syntax errors", TC.parserResult.errors.isEmpty());
		assertTrue("Specification has type errors", TC.errors.isEmpty());
		ProofObligationList proofObligations = new ProofObligationList();

		for (INode aModule : TC.result)
		{
			proofObligations.addAll(aModule.apply(new PogVisitor(), new POContextStack()));
		}

		return proofObligations;
	}
	
	@Test
	public void testWithCompare() throws AnalysisException, IOException, URISyntaxException
	{
		ProofObligationList polist = pogSL(new File("src/test/resources/adhoc/sandbox.vdmsl"));
		String sResult = IOUtils.toString(new FileReader("src/test/resources/adhoc/sandbox.result"));
		ParserResult<PExp> pResult = ParserUtil.parseExpression(sResult);
		PExp result = pResult.result;
		
		for (IProofObligation po : polist)
		{
			
				String pretty = po.getValueTree().toString();
				System.out.println(pretty);
				assertTrue(comparePOtoExp(result, po.getValueTree()));
		}
		
		
	}
	

	private boolean comparePOtoExp(PExp result, AVdmPoTree valueTree)
	{
		// TODO need comparison logic now... 
		return true;
	}

	@Test
	public void test() throws AnalysisException
	{
		ProofObligationList polist = pogSL(new File("src/test/resources/adhoc/test.vdmsl"));

		for (IProofObligation po : polist)
		{
			
				String pretty = po.getValueTree().toString();
				System.out.println(pretty);
			
		}
	}
}
