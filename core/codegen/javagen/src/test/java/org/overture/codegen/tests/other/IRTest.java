package org.overture.codegen.tests.other;

import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.overture.ast.util.ClonableString;
import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.IRConstants;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.ACatchClauseDeclIR;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.AVarDeclIR;
import org.overture.codegen.ir.expressions.ATypeArgExpIR;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.ir.statements.AMetaStmIR;
import org.overture.codegen.ir.statements.AReturnStmIR;
import org.overture.codegen.ir.statements.ASkipStmIR;
import org.overture.codegen.ir.statements.ATryStmIR;
import org.overture.codegen.ir.types.ABoolBasicTypeIR;
import org.overture.codegen.ir.types.AClassTypeIR;
import org.overture.codegen.ir.types.AExternalTypeIR;
import org.overture.codegen.ir.types.AMethodTypeIR;
import org.overture.codegen.ir.types.ARealNumericBasicTypeIR;
import org.overture.codegen.ir.types.AVoidTypeIR;
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
		AFieldDeclIR fieldDecl = new AFieldDeclIR();
		fieldDecl.setAccess("public");
		fieldDecl.setFinal(false);
		fieldDecl.setInitial(javaCodeGen.getInfo().getExpAssistant().consBoolLiteral(true));
		fieldDecl.setVolatile(true);
		fieldDecl.setStatic(true);
		fieldDecl.setName("flag");
		fieldDecl.setType(new ABoolBasicTypeIR());

		String expected = "public static volatile Boolean flag = true;";

		compare(expected, fieldDecl);
	}

	@Test
	public void testTypeArg()
	{
		AClassTypeIR classA = new AClassTypeIR();
		classA.setName("A");

		ATypeArgExpIR typeArg = new ATypeArgExpIR();
		typeArg.setType(classA);

		String expected = "A.class";

		compare(expected, typeArg);
	}

	@Test
	public void testCatchClause()
	{
		ACatchClauseDeclIR catchClause = consCatchClause();

		String expected = "catch(Exception e1) { return 42L; }";

		compare(expected, catchClause);
	}

	@Test
	public void testTryNoCatch()
	{
		ATryStmIR tryStm = new ATryStmIR();
		tryStm.setStm(consReturnIntLit(4));
		tryStm.setFinally(consReturnIntLit(19));

		String expected = "try { return 4L; } finally { return 19L; }";

		compare(expected, tryStm);
	}

	@Test
	public void testTryNoFinal()
	{
		ATryStmIR tryStm = new ATryStmIR();
		tryStm.setStm(consReturnIntLit(5));

		for (int i = 0; i < 2; i++)
		{
			tryStm.getCatchClauses().add(consCatchClause());
		}

		String expected = "try { return 5L; } catch(Exception e1) { return 42L; } catch(Exception e1) { return 42L; }";

		compare(expected, tryStm);
	}

	@Test
	public void testOpRaises()
	{
		AMethodDeclIR method = new AMethodDeclIR();
		method.setAbstract(false);
		method.setAccess(IRConstants.PUBLIC);
		method.setAsync(false);
		method.setBody(new ASkipStmIR());
		AMethodTypeIR t = new AMethodTypeIR();
		t.setResult(new AVoidTypeIR());
		method.setName("op");
		method.setMethodType(t);
		method.setStatic(false);

		AExternalTypeIR runtimeExpType = new AExternalTypeIR();
		runtimeExpType.setName("RuntimeException");
		method.getRaises().add(runtimeExpType);

		// For one exception
		compare("public void op() throws RuntimeException { /* skip */ }", method);

		AExternalTypeIR npeType = new AExternalTypeIR();
		npeType.setName("NullPointerException");
		method.getRaises().add(npeType);

		compare("public void op() throws RuntimeException, NullPointerException { /* skip */ }", method);
	}

	@Test
	public void testFinalVarDecl()
	{
		AIdentifierPatternIR id = new AIdentifierPatternIR();
		id.setName("x");

		AVarDeclIR varDecl = javaCodeGen.getInfo().getDeclAssistant().consLocalVarDecl(new ARealNumericBasicTypeIR(), id, javaCodeGen.getInfo().getExpAssistant().consUndefinedExp());
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

		AMetaStmIR meta = new AMetaStmIR();
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

	private AReturnStmIR consReturnIntLit(long n)
	{
		AReturnStmIR returnStm = new AReturnStmIR();
		returnStm.setExp(javaCodeGen.getInfo().getExpAssistant().consIntLiteral(n));

		return returnStm;
	}

	private ACatchClauseDeclIR consCatchClause()
	{
		AExternalTypeIR externalType = new AExternalTypeIR();
		externalType.setName("Exception");

		ACatchClauseDeclIR catchClause = new ACatchClauseDeclIR();
		catchClause.setType(externalType);
		catchClause.setName("e1");
		catchClause.setStm(consReturnIntLit(42));

		return catchClause;
	}
}
