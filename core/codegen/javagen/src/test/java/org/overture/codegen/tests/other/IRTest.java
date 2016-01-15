package org.overture.codegen.tests.other;

import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.overture.ast.util.ClonableString;
import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.ACatchClauseDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AVarDeclCG;
import org.overture.codegen.cgast.expressions.ATypeArgExpCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.AMetaStmCG;
import org.overture.codegen.cgast.statements.AReturnStmCG;
import org.overture.codegen.cgast.statements.ATryStmCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.AExternalTypeCG;
import org.overture.codegen.cgast.types.ARealNumericBasicTypeCG;
import org.overture.codegen.merging.MergeVisitor;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.codegen.vdm2java.JavaCodeGen;
import org.overture.codegen.vdm2java.JavaFormat;

public class IRTest
{
	private JavaCodeGen javaCodeGen;

	public IRTest()
	{
		this.javaCodeGen = new JavaCodeGen();
	}

	@Test
	public void testVolatileField()
	{
		AFieldDeclCG fieldDecl = new AFieldDeclCG();
		fieldDecl.setAccess("public");
		fieldDecl.setFinal(false);
		fieldDecl.setInitial(javaCodeGen.getInfo().getExpAssistant().consBoolLiteral(true));
		fieldDecl.setVolatile(true);
		fieldDecl.setStatic(true);
		fieldDecl.setName("flag");
		fieldDecl.setType(new ABoolBasicTypeCG());

		String expected = "public static volatile Boolean flag = true;";

		compare(expected, fieldDecl);
	}

	@Test
	public void testTypeArg()
	{
		AClassTypeCG classA = new AClassTypeCG();
		classA.setName("A");
		
		ATypeArgExpCG typeArg = new ATypeArgExpCG();
		typeArg.setType(classA);
		
		String expected = "A.class";
		
		compare(expected, typeArg);
	}
	
	@Test
	public void testCatchClause()
	{
		ACatchClauseDeclCG catchClause = consCatchClause();

		String expected = "catch(Exception e1) { return 42L; }";

		compare(expected, catchClause);
	}
	
	@Test
	public void testTryNoCatch()
	{
		ATryStmCG tryStm = new ATryStmCG();
		tryStm.setStm(consReturnIntLit(4));
		tryStm.setFinally(consReturnIntLit(19));
		
		String expected = "try { return 4L; } finally { return 19L; }";
		
		compare(expected, tryStm);
	}
	
	@Test
	public void testTryNoFinal()
	{
		ATryStmCG tryStm = new ATryStmCG();
		tryStm.setStm(consReturnIntLit(5));
		
		for(int i = 0; i < 2; i++)
		{
			tryStm.getCatchClauses().add(consCatchClause());
		}
		
		String expected = "try { return 5L; } catch(Exception e1) { return 42L; } catch(Exception e1) { return 42L; }";
		
		compare(expected, tryStm);
	}
	
	@Test
	public void testFinalVarDecl()
	{
		AIdentifierPatternCG id = new AIdentifierPatternCG();
		id.setName("x");
		
		AVarDeclCG varDecl = javaCodeGen.getInfo().getDeclAssistant().
				consLocalVarDecl(new ARealNumericBasicTypeCG(), id, javaCodeGen.getInfo().getExpAssistant().consUndefinedExp());
		varDecl.setFinal(true);
		
		String expected = "final Number x = null;";
		
		compare(expected, varDecl);
	}
	
	@Test
	public void testMetaStm()
	{
		String metaDataStr = "/*@ some meta data @*/";
		
		List<ClonableString> metaData = new LinkedList<ClonableString>();
		metaData.add(new ClonableString("/*@ some meta data @*/"));
		
		AMetaStmCG meta = new AMetaStmCG();
		meta.setMetaData(metaData);
	
		String expected = metaDataStr;
		
		compare(expected, meta);
	}

	private void compare(String expected, INode node)
	{
		StringWriter writer = new StringWriter();

		try
		{
			JavaFormat javaFormat = javaCodeGen.getJavaFormat();
			javaFormat.init();
			MergeVisitor mergeVisitor = javaFormat.getMergeVisitor();
			node.apply(mergeVisitor, writer);

			if (mergeVisitor.getMergeErrors().isEmpty())
			{
				String actual = GeneralUtils.cleanupWhiteSpaces(writer.toString());
				Assert.assertEquals("Got unexpected code generator output", expected, actual);
			} else
			{
				Assert.fail("Could print node: " + node);
			}

		} catch (AnalysisException e)
		{
			e.printStackTrace();
			Assert.fail("Could not print field declaration");
		}
	}
	
	private AReturnStmCG consReturnIntLit(long n)
	{
		AReturnStmCG returnStm = new AReturnStmCG();
		returnStm.setExp(javaCodeGen.getInfo().getExpAssistant().consIntLiteral(n));
		
		return returnStm;
	}
	
	private ACatchClauseDeclCG consCatchClause()
	{
		AExternalTypeCG externalType = new AExternalTypeCG();
		externalType.setName("Exception");

		ACatchClauseDeclCG catchClause = new ACatchClauseDeclCG();
		catchClause.setType(externalType);
		catchClause.setName("e1");
		catchClause.setStm(consReturnIntLit(42));
		
		return catchClause;
	}
}
