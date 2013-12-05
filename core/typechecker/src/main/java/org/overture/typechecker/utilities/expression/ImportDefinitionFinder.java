package org.overture.typechecker.utilities.expression;

import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.modules.AAllImport;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.modules.ATypeImport;
import org.overture.ast.modules.SValueImport;
import org.overture.ast.node.INode;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.module.AAllImportAssistantTC;
import org.overture.typechecker.assistant.module.ATypeImportAssistantTC;
import org.overture.typechecker.assistant.module.SValueImportAssistantTC;

/**
 * Used to find the definitions of an imported object from a module.
 *  
 * @author kel
 */
public class ImportDefinitionFinder extends QuestionAnswerAdaptor<AModuleModules, List<PDefinition>>
{
	private static final long serialVersionUID = 1L;
	protected ITypeCheckerAssistantFactory af;
	
	public ImportDefinitionFinder(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	
	@Override
	public List<PDefinition> caseAAllImport(AAllImport imp,
			AModuleModules from) throws AnalysisException
	{
		return AAllImportAssistantTC.getDefinitions(imp,from);
	}
	
	@Override
	public List<PDefinition> caseATypeImport(ATypeImport imp,
			AModuleModules from) throws AnalysisException
	{
		return ATypeImportAssistantTC.getDefinitions((ATypeImport)imp,from);
	}
	
	@Override
	public List<PDefinition> defaultSValueImport(SValueImport imp,
			AModuleModules from) throws AnalysisException
	{
		return SValueImportAssistantTC.getDefinitions(imp,from);
	}

//	if (imp instanceof AAllImport) {
//	
//} else if (imp instanceof ATypeImport) {
//	
//} else if (imp instanceof SValueImport) {
//	
//} else {
//	
//	return null;
//}
	@Override
	public List<PDefinition> createNewReturnValue(INode node,
			AModuleModules question) throws AnalysisException
	{
		assert false : "PImport.getDefinitions should never hit this case";
		return null;
	}

	@Override
	public List<PDefinition> createNewReturnValue(Object node,
			AModuleModules question) throws AnalysisException
	{
		assert false : "PImport.getDefinitions should never hit this case";
		return null;
	}

}
