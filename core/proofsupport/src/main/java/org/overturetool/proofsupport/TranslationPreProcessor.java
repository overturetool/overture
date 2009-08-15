package org.overturetool.proofsupport;

import java.util.ArrayList;
import java.util.List;

import org.overturetool.ast.itf.IOmlDocument;
import org.overturetool.ast.itf.IOmlExpression;
import org.overturetool.proofsupport.external_tools.omlparser.OmlAstGenerator;
import org.overturetool.proofsupport.external_tools.omlparser.OmlAstGeneratorFactory;
import org.overturetool.proofsupport.external_tools.omlparser.ParserException;
import org.overturetool.proofsupport.external_tools.pog.PogGenerator;
import org.overturetool.proofsupport.external_tools.pog.PogGeneratorException;
import org.overturetool.proofsupport.external_tools.pog.PogProcessor;
import org.overturetool.proofsupport.external_tools.pog.PogProcessorException;

public class TranslationPreProcessor {

	private final PogGenerator pogGen;
	private final PogProcessor pogProc;
	private final OmlAstGenerator omlAstGen;

	public TranslationPreProcessor(PogGenerator pogGen, PogProcessor pogProc) {
		this.pogGen = pogGen;
		this.pogProc = pogProc;
		this.omlAstGen = OmlAstGeneratorFactory.newOmlAstGenertorInstance();
	}

	public PreparationData prepareVdmFiles(String vdmModelFile,
			List<String> vdmContextFiles) throws PogGeneratorException,
			PogProcessorException, ParserException {
		String pogFilePath = generatePogFile(vdmModelFile, vdmContextFiles);
		List<String> poExpressions = processPogFile(pogFilePath);
		return generateOmlAst(vdmModelFile, vdmContextFiles, poExpressions);
	}

	protected PreparationData generateOmlAst(String vdmModelFile,
			List<String> vdmContextFiles, List<String> poExpressions)
			throws ParserException {
		IOmlDocument omlModel = omlAstGen.getOmlDocument(vdmModelFile);
		List<IOmlDocument> omlContextDocuments = parseContext(vdmContextFiles);
		List<IOmlExpression> omlPos = parsePos(poExpressions);
		return new PreparationData(omlModel, omlContextDocuments, omlPos);
	}

	private List<IOmlExpression> parsePos(List<String> poExpressions)
			throws ParserException {
		List<IOmlExpression> omlPos = new ArrayList<IOmlExpression>(
				poExpressions.size());
		for (String poExpression : poExpressions)
			omlPos.add(omlAstGen.getOmlExpression(poExpression));
		return omlPos;
	}

	protected List<IOmlDocument> parseContext(List<String> vdmContextFiles)
			throws ParserException {
		List<IOmlDocument> omlContextDocuments = new ArrayList<IOmlDocument>(
				vdmContextFiles.size());
		for (String vdmContextFile : vdmContextFiles)
			omlContextDocuments.add(omlAstGen.getOmlDocument(vdmContextFile));
		return omlContextDocuments;
	}

	protected List<String> processPogFile(String pogFilePath)
			throws PogProcessorException {
		List<String[]> pos = pogProc.extractPosFromFile(pogFilePath);
		return pogProc.extractPoExpressions(pos);
	}

	protected String generatePogFile(String vdmModelFile,
			List<String> vdmContextFiles) throws PogGeneratorException {
		List<String> tmpList = new ArrayList<String>(vdmContextFiles.size() + 1);
		tmpList.add(0, vdmModelFile);
		for (String ctxFile : vdmContextFiles)
			tmpList.add(ctxFile);
		return pogGen.generatePogFile(tmpList.toArray(new String[] {}));
	}

}
