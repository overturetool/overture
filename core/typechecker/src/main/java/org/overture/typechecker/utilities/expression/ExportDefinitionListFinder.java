package org.overture.typechecker.utilities.expression;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.modules.AAllExport;
import org.overture.ast.modules.AFunctionExport;
import org.overture.ast.modules.AOperationExport;
import org.overture.ast.modules.ATypeExport;
import org.overture.ast.modules.AValueExport;
import org.overture.ast.node.INode;
import org.overture.ast.typechecker.NameScope;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * Used to find the definitions of an exported expression 
 * and return a linked list with them.
 *  
 * @author kel
 */
public class ExportDefinitionListFinder extends AnswerAdaptor<Collection<? extends PDefinition>>
{
	private static final long serialVersionUID = 1L;
	protected ITypeCheckerAssistantFactory af;
	
	public ExportDefinitionListFinder(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	
	@Override
	public Collection<? extends PDefinition> caseAAllExport(AAllExport exp)
			throws AnalysisException
	{
		return new LinkedList<PDefinition>(); // Nothing <shrug>
	}
	
	@Override
	public Collection<? extends PDefinition> caseAFunctionExport(
			AFunctionExport exp) throws AnalysisException
	{
		List<PDefinition> list = new Vector<PDefinition>();
		// AAccessSpecifierAccessSpecifier
		for (ILexNameToken name :  exp.getNameList())
		{
			list.add(AstFactory.newALocalDefinition(name.getLocation(), name.clone(), NameScope.GLOBAL, exp.getExportType()));
			// new ALocalDefinition(name.location, NameScope.GLOBAL,true,null,
			// new AAccessSpecifierAccessSpecifier(new APublicAccess(),new TStatic(),null),
			// ((AFunctionExport)exp).getExportType(),false,name.clone()));
		}
		return list;
	}
	
	@Override
	public Collection<? extends PDefinition> caseAOperationExport(
			AOperationExport exp) throws AnalysisException
	{
		List<PDefinition> list = new Vector<PDefinition>();
		for (ILexNameToken name : exp.getNameList())
		{
			list.add(AstFactory.newALocalDefinition(name.getLocation(), name.clone(), NameScope.GLOBAL, exp.getExportType()));
			// new ALocalDefinition(name.location, NameScope.GLOBAL,true,null,
			// new AAccessSpecifierAccessSpecifier(new APublicAccess(),new TStatic(),null),
			// ((AOperationExport)exp).getExportType(),false,name.clone()));
		}
		return list;
	}
	
	@Override
	public Collection<? extends PDefinition> caseATypeExport(ATypeExport exp)
			throws AnalysisException
	{
		return new LinkedList<PDefinition>();
	}
	
	@Override
	public Collection<? extends PDefinition> caseAValueExport(AValueExport exp)
			throws AnalysisException
	{
		List<PDefinition> list = new Vector<PDefinition>();
		for (ILexNameToken name : exp.getNameList())
		{
			list.add(AstFactory.newALocalDefinition(name.getLocation(), name.clone(), NameScope.GLOBAL, exp.getExportType()));
			// new ALocalDefinition(name.location, NameScope.GLOBAL,true,null,
			// new AAccessSpecifierAccessSpecifier(new APublicAccess(),new TStatic(),null),
			// ((AValueExport)exp).getExportType(),true,name.clone()));
		}
		return list;
	}

	@Override
	public Collection<? extends PDefinition> createNewReturnValue(INode node)
			throws AnalysisException
	{
		assert false;// "No match in switch";
		return null;
	}

	@Override
	public Collection<? extends PDefinition> createNewReturnValue(Object node)
			throws AnalysisException
	{
		assert false;// "No match in switch";
		return null;
	}

}
