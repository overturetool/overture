package org.overturetool.potrans.proof_system;

import java.util.ArrayList;
import java.util.List;

import org.overturetool.ast.itf.IOmlDocument;
import org.overturetool.ast.itf.IOmlExpression;
import org.overturetool.potrans.external_tools.OvertureParserException;
import org.overturetool.potrans.external_tools.OvertureParserWrapper;
import org.overturetool.potrans.external_tools.PogGenerator;
import org.overturetool.potrans.external_tools.PogGeneratorException;
import org.overturetool.potrans.external_tools.PogProcessor;
import org.overturetool.potrans.external_tools.PogProcessorException;

public class TranslationPreProcessor {

	private final PogGenerator pogGen;
	private final PogProcessor pogProc;

	public TranslationPreProcessor(PogGenerator pogGen, PogProcessor pogProc) {
		this.pogGen = pogGen;
		this.pogProc = pogProc;
	}

	public PreparationData prepareVdmFiles(String vdmModelFile, List<String> vdmContextFiles) throws PogGeneratorException, PogProcessorException, OvertureParserException {
		String pogFilePath = generatePogFile(vdmModelFile, vdmContextFiles);
		List<String> poExpressions = processPogFile(pogFilePath);
		return generateOmlAst(vdmModelFile, vdmContextFiles, poExpressions);
	}

	protected PreparationData generateOmlAst(String vdmModelFile, List<String> vdmContextFiles,
			List<String> poExpressions) throws OvertureParserException  {
		IOmlDocument omlModel = OvertureParserWrapper.getOmlDocument(vdmModelFile);
		List<IOmlDocument> omlContextDocuments = parseContext(vdmContextFiles);
		List<IOmlExpression> omlPos = parsePos(poExpressions);
		return new PreparationData(omlModel, omlContextDocuments, omlPos);
	}

	private List<IOmlExpression> parsePos(List<String> poExpressions) throws OvertureParserException  {
		List<IOmlExpression> omlPos = new ArrayList<IOmlExpression>(poExpressions.size());
		for (String poExpression : poExpressions)
			omlPos.add(OvertureParserWrapper.getOmlExpression(poExpression));
		return omlPos;
	}

	protected List<IOmlDocument> parseContext(List<String> vdmContextFiles) throws OvertureParserException {
		List<IOmlDocument> omlContextDocuments = new ArrayList<IOmlDocument>(vdmContextFiles.size());
		// TODO check if this works or if all files have to be parsed together
		for (String vdmContextFile : vdmContextFiles)
			omlContextDocuments.add(OvertureParserWrapper.getOmlDocument(vdmContextFile));
		return omlContextDocuments;
	}

	protected List<String> processPogFile(String pogFilePath) throws PogProcessorException {
		List<String[]> pos = pogProc.extractPosFromFile(pogFilePath);
		return pogProc.extractPoExpressions(pos);
	}

	protected String generatePogFile(String vdmModelFile, List<String> vdmContextFiles) throws PogGeneratorException {
		List<String> tmpList = new ArrayList<String>(vdmContextFiles.size() + 1);
		tmpList.add(0, vdmModelFile);
		for(String ctxFile : vdmContextFiles)
			tmpList.add(ctxFile);
		return pogGen.generatePogFile(tmpList.toArray(new String[] {}));
	}

}
