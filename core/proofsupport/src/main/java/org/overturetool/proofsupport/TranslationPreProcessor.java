package org.overturetool.proofsupport;

import java.util.ArrayList;
import java.util.List;

import org.overturetool.ast.itf.IOmlDocument;
import org.overturetool.ast.itf.IOmlExpression;
import org.overturetool.proofsupport.external_tools.omlparser.OmlAstGenerator;
import org.overturetool.proofsupport.external_tools.omlparser.OmlAstGeneratorFactory;
import org.overturetool.proofsupport.external_tools.omlparser.ParserException;
import org.overturetool.proofsupport.external_tools.pog.PoGenerator;
import org.overturetool.proofsupport.external_tools.pog.PoGeneratorException;
import org.overturetool.proofsupport.external_tools.pog.PoProcessor;
import org.overturetool.proofsupport.external_tools.pog.PoProcessorException;

public class TranslationPreProcessor {

	private final PoGenerator pogGen;
	private final PoProcessor pogProc;
	private final OmlAstGenerator omlAstGen;

	public TranslationPreProcessor(PoGenerator pogGen, PoProcessor pogProc) {
		this.pogGen = pogGen;
		this.pogProc = pogProc;
		this.omlAstGen = OmlAstGeneratorFactory.newOmlAstGenertorInstance();
	}

	public PreparationData prepareVdmFiles(String vdmModelFile,
			List<String> vdmContextFiles) throws PoGeneratorException,
			PoProcessorException, ParserException {
		String pogFilePath = generatePogFile(vdmModelFile, vdmContextFiles);
		List<String> poExpressions = processPogFile(pogFilePath);
		return generateOmlAst(vdmModelFile, vdmContextFiles, poExpressions);
	}
	
	public PreparationData prepareVdmFiles(String vdmModelFile,
			List<String> vdmContextFiles, String pogFile) throws PoGeneratorException,
			PoProcessorException, ParserException {
		List<String> poExpressions = processPogFile(pogFile);
		return generateOmlAst(vdmModelFile, vdmContextFiles, poExpressions);
	}
	
	public PreparationData prepareVdmModels(IOmlDocument vdmModel,
			List<IOmlDocument> vdmContext) {
		return new PreparationData(vdmModel, vdmContext);
	}
	
	public IOmlExpression prepareVdmExpression(String vdmExpression) throws ParserException {
		return omlAstGen.getOmlExpression(vdmExpression);
	}
	
	protected PreparationData generateOmlAst(String vdmModelFile,
			List<String> vdmContextFiles, List<String> poExpressions)
			throws ParserException {
		IOmlDocument omlModel = omlAstGen.getOmlDocument(vdmModelFile);
		List<IOmlDocument> omlContextDocuments = parseContext(vdmContextFiles);
		List<IOmlExpression> omlPos = parsePos(poExpressions);
		return new PreparationData(poExpressions, omlModel, omlContextDocuments, omlPos);
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
			throws PoProcessorException {
		List<String[]> pos = pogProc.extractPosFromFile(pogFilePath);
		return pogProc.extractPoExpressions(pos);
	}

	protected String generatePogFile(String vdmModelFile,
			List<String> vdmContextFiles) throws PoGeneratorException {
		List<String> tmpList = new ArrayList<String>(vdmContextFiles.size() + 1);
		tmpList.add(0, vdmModelFile);
		for (String ctxFile : vdmContextFiles)
			tmpList.add(ctxFile);
		return pogGen.generatePogFile(tmpList.toArray(new String[] {}));
	}

}
