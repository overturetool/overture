package org.overture.core.tests;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.parser.lex.LexException;
import org.overture.parser.syntax.ParserException;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;



/**
 * Helper Class for the new test framework. Helps process test inputs. This
 * class handles I/O and interaction with the Overture TC and Parser.
 * 
 * @author ldc
 */
public class InputProcessor {

	
	/**
	 * Parse and type check a VDM source file and return the model's AST.
	 * @param sourcePath a {@link String} with the path to the VDM model source
	 * @return the AST of the model as a {@link List} of {@link INode}.
	 */
		public static List<INode> typedAst(String sourcePath) throws IOException,
				ParserException, LexException
		{
			String ext = sourcePath.split("\\.")[1];

			boolean switchRelease = false;
			try
			{
				if (sourcePath.contains("vdm10release"))
				{
					switchRelease = true;
					Settings.release = Release.VDM_10;
				}

				if (ext.equals("vdmsl"))
				{
					return parseTcSl(sourcePath);
				}

				else
				{
					if (ext.equals("vdmpp"))
					{
						return parseTcPp(sourcePath);
					} else
					{
						if (ext.equals("vdmrt"))
						{
							return parseTcRt(sourcePath);
						} else
						{
							fail("Unexpected extension in file " + sourcePath
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
		//FIXME unify parsing and TCing of VDM dialects
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
