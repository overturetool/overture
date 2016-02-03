package org.overture.codegen.tests;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.codegen.ir.PCG;
import org.overture.codegen.ir.expressions.AIdentifierVarExpCG;
import org.overture.codegen.ir.IRGenerator;
import org.overture.codegen.ir.IRStatus;
import org.overture.codegen.tests.util.FirstVarFinder;
import org.overture.config.Settings;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

/**
 * Tests if the {@link AIdentifierVarExpCG#getIsLocal()} works as intended. For simplicity each test specification
 * includes a single variable occurrence in some context.
 * 
 * @author pvj
 */
public class LocalVarTest
{
	public static final String ROOT = "src" + File.separatorChar + "test"
			+ File.separatorChar + "resources" + File.separatorChar
			+ "local_var_test_specs";
	
	public static boolean LOCAL = true;
	public static boolean NOT_LOCAL = false;
	
	@Test
	public void letVarInOp()
	{
		checkLocal("LetVarInOp.vdmsl", "v", LOCAL);
	}
	
	@Test
	public void opParam()
	{
		checkLocal("OpParam.vdmsl", "x", LOCAL);
	}
	
	@Test
	public void valueReadInOp()
	{
		checkLocal("ValueReadInOp.vdmsl", "q", NOT_LOCAL);
	}
	
	@Test
	public void stateRead()
	{
		checkLocal("StateRead.vdmsl", "h", NOT_LOCAL);
	}

	@Test
	public void dcl()
	{
		checkLocal("Dcl.vdmsl", "m", LOCAL);
	}
	
	@Test
	public void tupPatternInFunc()
	{
		checkLocal("TupPatternInFunc.vdmsl", "a", LOCAL);
	}
	
	@Test
	public void tupPatternVal()
	{
		checkLocal("TupPatternVal.vdmsl", "p", NOT_LOCAL);
	}
	
	@Test
	public void lambdaParamReturn()
	{
		checkLocal("LambdaParamReturn.vdmsl", "o", LOCAL);
	}
	
	@Test
	public void lambdaValReturn()
	{
		checkLocal("LambdaValReturn.vdmsl", "val", NOT_LOCAL);
	}

	@Test
	public void instanceVar()
	{
		checkLocal("InstanceVar.vdmpp", "iv", NOT_LOCAL);
	}
	
	@Test
	public void instanceVarParent()
	{
		checkLocal("InstanceVarParent.vdmpp", "field", NOT_LOCAL);
	}
	
	protected void checkLocal(String spec, String expectedVarName, boolean expectedLocalVal)
	{
		AIdentifierVarExpCG var = findVar(consIrModule(spec));
		
		Assert.assertTrue("Could not find variable occurrence in module", var != null);
		Assert.assertTrue("Expected name of variable occurence to be " + expectedVarName + " but it was " + var.getName(), var.getName().equals(expectedVarName));
		Assert.assertTrue("Expected 'getIsLocal()' to be " + expectedLocalVal + " but it was not", var.getIsLocal() == expectedLocalVal);
	}


	protected AIdentifierVarExpCG findVar(List<PCG> irNodes)
	{
		try
		{
			FirstVarFinder finder = new FirstVarFinder();
			
			for(PCG n : irNodes)
			{
				n.apply(finder);
				
				if(finder.getVar() != null)
				{
					// As soon as we find an occurrence return it.
					return finder.getVar();
				}
			}
			
		} catch (org.overture.codegen.ir.analysis.AnalysisException e)
		{
		}
		
		return null;
	}


	protected List<PCG> consIrModule(String fileName)
	{
		File file = new File(ROOT + File.separatorChar + fileName);
		
		String tcErrMsg = "Expected VDM specification to parse and type check";
		
		List<? extends INode> nodes;
		
		if(fileName.endsWith(".vdmsl"))
		{
			Settings.dialect = Dialect.VDM_SL;
			TypeCheckResult<List<AModuleModules>> tcRes = TypeCheckerUtil.typeCheckSl(file);
			Assert.assertTrue(tcErrMsg, tcRes.parserResult.errors.isEmpty() && tcRes.errors.isEmpty());
			nodes = tcRes.result;
		}
		else
		{
			Settings.dialect = Dialect.VDM_PP;
			TypeCheckResult<List<SClassDefinition>> tcRes = TypeCheckerUtil.typeCheckPp(file);
			Assert.assertTrue(tcErrMsg, tcRes.parserResult.errors.isEmpty() && tcRes.errors.isEmpty());
			nodes = tcRes.result;
		}
		
		IRGenerator irGen = new IRGenerator();
		
		try
		{
			List<PCG> irRes = new LinkedList<>();
			
			for(INode n : nodes)
			{
				IRStatus<PCG> res = irGen.generateFrom(n);
				
				Assert.assertTrue("Expected IR node to generate without problems", irRes != null
						&& res.canBeGenerated());
				irRes.add(res.getIrNode());
			}
			
			return irRes;
		} catch (AnalysisException e)
		{
			Assert.fail("Problems encountered when trying to generate IR node: " + e.getMessage());
		}
		
		return null;
	}
}
