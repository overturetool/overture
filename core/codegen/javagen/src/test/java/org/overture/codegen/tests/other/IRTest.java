package org.overture.codegen.tests.other;

import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.overture.ast.util.ClonableString;
import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.ACatchClauseDeclCG;
import org.overture.codegen.ir.declarations.AFieldDeclCG;
import org.overture.codegen.ir.declarations.AMethodDeclCG;
import org.overture.codegen.ir.declarations.AVarDeclCG;
import org.overture.codegen.ir.expressions.ATypeArgExpCG;
import org.overture.codegen.ir.patterns.AIdentifierPatternCG;
import org.overture.codegen.ir.statements.AMetaStmCG;
import org.overture.codegen.ir.statements.AReturnStmCG;
import org.overture.codegen.ir.statements.ASkipStmCG;
import org.overture.codegen.ir.statements.ATryStmCG;
import org.overture.codegen.ir.types.ABoolBasicTypeCG;
import org.overture.codegen.ir.types.AClassTypeCG;
import org.overture.codegen.ir.types.AExternalTypeCG;
import org.overture.codegen.ir.types.AMethodTypeCG;
import org.overture.codegen.ir.types.ARealNumericBasicTypeCG;
import org.overture.codegen.ir.types.AVoidTypeCG;
import org.overture.codegen.ir.IRConstants;
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
	public void testOpRaises()
	{
		AMethodDeclCG method = new AMethodDeclCG();
		method.setAbstract(false);
		method.setAccess(IRConstants.PUBLIC);
		method.setAsync(false);
		method.setBody(new ASkipStmCG());
		AMethodTypeCG t = new AMethodTypeCG();
		t.setResult(new AVoidTypeCG());
		method.setName("op");
		method.setMethodType(t);
		method.setStatic(false);
		
		AExternalTypeCG runtimeExpType = new AExternalTypeCG();
		runtimeExpType.setName("RuntimeException");
		method.getRaises().add(runtimeExpType);
		
		// For one exception
		compare("public void op() throws RuntimeException { /* skip */ }", method);
		
		AExternalTypeCG npeType = new AExternalTypeCG();
		npeType.setName("NullPointerException");
		method.getRaises().add(npeType);

		compare("public void op() throws RuntimeException, NullPointerException { /* skip */ }", method);
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
			javaFormat.getMergeVisitor().init();
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
