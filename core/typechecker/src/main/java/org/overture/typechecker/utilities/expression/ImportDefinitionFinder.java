package org.overture.typechecker.utilities.expression;

import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.modules.AAllImport;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.modules.ATypeImport;
import org.overture.ast.modules.SValueImport;
import org.overture.ast.node.INode;
import org.overture.ast.typechecker.NameScope;
import org.overture.typechecker.TypeCheckerErrors;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

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
			AModuleModules module) throws AnalysisException
	{
		//return AAllImportAssistantTC.getDefinitions(imp,from);
		imp.setFrom(module);

		if (imp.getFrom().getExportdefs().isEmpty())
		{
			TypeCheckerErrors.report(3190, "Import all from module with no exports?",imp.getLocation(),imp);
		} 

		List<PDefinition> imported = new Vector<PDefinition>() ;

		for (PDefinition d: imp.getFrom().getExportdefs())
		{
			PDefinition id = AstFactory.newAImportedDefinition(
					imp.getLocation(), d);
			af.createPDefinitionAssistant().markUsed(id); // So imports all is quiet
			imported.add(id);

		}

		return imported;	// The lot!
	}
	
	@Override
	public List<PDefinition> caseATypeImport(ATypeImport imp,
			AModuleModules module) throws AnalysisException
	{
		List<PDefinition> list = new Vector<PDefinition>();
		imp.setFrom(module);
		PDefinition expdef = af.createPDefinitionListAssistant().findType(imp.getFrom().getExportdefs(),imp.getName(), null);

		if (expdef == null)
		{
			TypeCheckerErrors.report(3191, "No export declared for import of type " + imp.getName() + " from " + imp.getFrom().getName(),imp.getLocation(),imp);
		}
		else
		{
			if (imp.getRenamed() != null)
			{
				expdef = AstFactory.newARenamedDefinition(imp.getRenamed(),expdef);
			}
			else
			{
				expdef = AstFactory.newAImportedDefinition(imp.getName().getLocation(),expdef);
			}

			list.add(expdef);
		}

		return list;
	}
	
	@Override
	public List<PDefinition> defaultSValueImport(SValueImport imp,
			AModuleModules module) throws AnalysisException
	{
		List<PDefinition> list = new Vector<PDefinition>();
		imp.setFrom(module);
		ILexNameToken name = imp.getName();
		
		PDefinition expdef = af.createPDefinitionListAssistant().findName(module.getExportdefs(),name, NameScope.NAMES);

		if (expdef == null)
		{
			TypeCheckerErrors.report(3193, "No export declared for import of value " + name + " from " + module.getName(),imp.getLocation(),imp);
		}
		else
		{
			if (imp.getRenamed() != null)
			{
				expdef = AstFactory.newARenamedDefinition(imp.getRenamed(), expdef);
			}
			else
			{
				expdef = AstFactory.newAImportedDefinition(imp.getLocation(), expdef);
			}

			list.add(expdef);
		}

		return list;
	}
	
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
