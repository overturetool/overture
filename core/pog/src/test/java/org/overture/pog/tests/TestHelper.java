package org.overture.pog.tests;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;
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

public abstract class TestHelper {

	public static String getResultAsString(String resultPath)
			throws FileNotFoundException, IOException {
		String sResult = IOUtils.toString(new FileReader(resultPath));

		return sResult;
	}

	public static List<INode> getAstFromName(String name) throws IOException,
			ParserException, LexException {
		String ext = name.split("\\.")[1];

		boolean switchRelease = false;
		try {
			if (name.contains("vdm10release")) {
				switchRelease = true;
				Settings.release = Release.VDM_10;
			}

			if (ext.equals("vdmsl")) {
				return parseTcSl(name);
			}

			else {
				if (ext.equals("vdmpp")) {
					return parseTcPp(name);
				} else {
					if (ext.equals("vdmrt")) {
						return parseTcRt(name);
					} else {
						fail("Unexpected extension in file " + name
								+ ". Only .vdmpp, .vdmsl and .vdmrt allowed");
					}
				}
			}
		} finally {
			if (switchRelease) {
				Settings.release = Release.DEFAULT;
			}
		}
		// only needed to compile. will never it because of fail()
		return null;
	}

	// These 3 methods have so much duplicated code because we cannot
	// return the TC results since their types are all different.

	// FIXME find a way to clear return TCResult dupes
	private static List<INode> parseTcPp(String name) {
		Settings.dialect = Dialect.VDM_PP;
		File f = new File(name);

		TypeCheckResult<List<SClassDefinition>> TC = TypeCheckerUtil
				.typeCheckPp(f);

		assertTrue("Specification has parse errors",
				TC.parserResult.errors.isEmpty());
		assertTrue("Specification has type errors", TC.errors.isEmpty());

		List<INode> r = new LinkedList<INode>();
		r.addAll(TC.result);

		return r;
	}

	private static List<INode> parseTcSl(String name) {
		Settings.dialect = Dialect.VDM_SL;
		File f = new File(name);

		TypeCheckResult<List<AModuleModules>> TC = TypeCheckerUtil
				.typeCheckSl(f);

		assertTrue("Specification has parse errors",
				TC.parserResult.errors.isEmpty());
		assertTrue("Specification has type errors", TC.errors.isEmpty());

		List<INode> r = new LinkedList<INode>();
		r.addAll(TC.result);

		return r;
	}

	private static List<INode> parseTcRt(String name) throws ParserException,
			LexException {
		Settings.dialect = Dialect.VDM_RT;
		File f = new File(name);

		TypeCheckResult<List<SClassDefinition>> TC = TypeCheckerUtil
				.typeCheckRt(f);

		assertTrue("Specification has parse errors",
				TC.parserResult.errors.isEmpty());
		assertTrue("Specification has type errors", TC.errors.isEmpty());

		List<INode> r = new LinkedList<INode>();
		r.addAll(TC.result);

		return r;
	}

}
