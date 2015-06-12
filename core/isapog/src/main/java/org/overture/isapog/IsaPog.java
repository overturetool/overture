package org.overture.isapog;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.modules.AModuleModules;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.pog.pub.IProofObligation;
import org.overture.pog.pub.IProofObligationList;
import org.overture.pog.pub.ProofObligationGenerator;
import org.overturetool.cgisa.IsaGen;

/**
 * Main class for Isabelle Translation and POG integration. <br>
 * <br>
 * Usage: instantiate the class with the module you wish to analyse. THen use {@link #getModelThyString()} and
 * {@link #getPosThyString()} to obtain the Strings for the model and POs translations respectively. <br>
 * You can also use {@link #writeThyFiles(String)} to write the theory files directly.
 * 
 * @author ldc
 */
public class IsaPog
{

	private static final String POS_THY = "_POs";
	private static final String ISA_THEORY = "theory";
	private static final String ISA_IMPORTS = "imports";
	private static final String SINGLE_SPACE = " ";
	private static final String LINEBREAK = "\n";
	private static final String ISA_BEGIN = "begin\n";
	private static final String ISA_END = "end";
	private static final String ISA_LEMMA = "lemma";
	private static final String BY_TAC = " by vdm_auto_tac";
	private static final String THY_EXT = ".thy";
	private static final String ISA_OPEN_COMMENT = "(*";
	private static final String ISA_CLOSE_COMMENT = "*)";

	private GeneratedModule modelThy;
	private String modelThyName;
	private String posThy;
	private String posThyName;

	public IsaPog(AModuleModules ast) throws AnalysisException,
			org.overture.codegen.cgast.analysis.AnalysisException
	{
		IProofObligationList pos = ProofObligationGenerator.generateProofObligations(ast);
		pos.renumber();

		modelThy = IsaGen.vdmModule2IsaTheory(ast);
		modelThyName = modelThy.getName() + THY_EXT;
		posThy = makePosThy(pos, modelThy.getName());
		posThyName = modelThy.getName() + POS_THY + THY_EXT;
	}

	public String getModelThyString()
	{
		return modelThy.getContent();
	}

	public String getPosThyString()
	{
		return posThy;
	}

	public String getModelThyName()
	{
		return modelThyName;
	}

	public String getPosThyName()
	{
		return posThyName;
	}

	public boolean hasErrors()
	{
		return modelThy.hasMergeErrors() || modelThy.hasUnsupportedIrNodes()
				|| modelThy.hasUnsupportedTargLangNodes();
	}

	public String getErrorMessage()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(modelThy.getMergeErrors().toString());
		sb.append(LINEBREAK);
		sb.append(modelThy.getUnsupportedInIr().toString());
		sb.append(LINEBREAK);
		sb.append(modelThy.getUnsupportedInTargLang().toString());
		return sb.toString();
	}

	/**
	 * Write Isabelle theory files to disk for the model and proof obligations
	 * 
	 * @param path
	 *            Path to the directory to write the files to. Must end with the {@link File#separatorChar}
	 * @return true if write is successful
	 * @throws IOException
	 */
	public Boolean writeThyFiles(String path) throws IOException
	{
		File modelThyFile = new File(path + modelThyName);
		FileUtils.writeStringToFile(modelThyFile, modelThy.getContent());

		File posThyFile = new File(path + posThyName);
		FileUtils.writeStringToFile(posThyFile, posThy);

		return true;
	}

	private String makePosThyHeader(String moduleName)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(ISA_THEORY);
		sb.append(SINGLE_SPACE);
		sb.append(moduleName);
		sb.append(POS_THY);
		sb.append(LINEBREAK);
		sb.append(ISA_IMPORTS);
		sb.append(SINGLE_SPACE);
		sb.append(moduleName);
		sb.append(LINEBREAK);
		sb.append(ISA_BEGIN);
		return sb.toString();
	}

	private String makePoLemma(IProofObligation po) throws AnalysisException,
			org.overture.codegen.cgast.analysis.AnalysisException
	{
		StringBuilder sb = new StringBuilder();
		sb.append(ISA_OPEN_COMMENT);
		sb.append(" ");
		sb.append(po.toString());
		sb.append(" ");
		sb.append(ISA_CLOSE_COMMENT);
		sb.append(LINEBREAK);
		sb.append(ISA_LEMMA);
		sb.append(SINGLE_SPACE);
		sb.append(po.getIsaName());
		sb.append(": ");
		sb.append("\"+|");
		sb.append(IsaGen.vdmExp2IsaString(po.getValueTree().getPredicate()));
		sb.append("|+\"");
		sb.append(BY_TAC);
		sb.append(LINEBREAK);
		return sb.toString();
	}

	private String makePosThy(IProofObligationList pos, String moduleName)
			throws AnalysisException,
			org.overture.codegen.cgast.analysis.AnalysisException
	{
		StringBuilder sb = new StringBuilder();
		sb.append(makePosThyHeader(moduleName));
		sb.append(LINEBREAK);
		Iterator<IProofObligation> iter = pos.iterator();
		IProofObligation po;

		while (iter.hasNext())
		{
			po = iter.next();
			sb.append(makePoLemma(po));
			sb.append(LINEBREAK);
		}
		sb.append(ISA_END);
		return sb.toString();
	}

}
