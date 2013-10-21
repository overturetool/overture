package org.overture.typechecker.utilities;

import org.overture.ast.analysis.AnalysisAdaptor;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AExternalDefinition;
import org.overture.ast.definitions.AImportedDefinition;
import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.ast.definitions.ARenamedDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * This class implements a way to Mark used nodes from the AST.
 * 
 * @author kel
 */

public class UsedMarker extends AnalysisAdaptor
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1831499172201136291L;
	
	protected ITypeCheckerAssistantFactory af;

	public UsedMarker(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	
	@Override
	public void caseAExternalDefinition(AExternalDefinition node)
			throws AnalysisException
	{
		
		node.setUsed(true);
		node.getState().apply(THIS);
	}
	
	@Override
	public void caseAImportedDefinition(AImportedDefinition node)
			throws AnalysisException
	{
		node.setUsed(true);
		node.getDef().apply(THIS);
	}

	@Override
	public void caseAInheritedDefinition(AInheritedDefinition node)
			throws AnalysisException
	{
		node.setUsed(true);
		node.getSuperdef().apply(THIS);
	}
	
	@Override
	public void caseARenamedDefinition(ARenamedDefinition node)
			throws AnalysisException
	{
		
		node.setUsed(true);
		node.getDef().apply(THIS);
		node.setUsed(true);
	}

	@Override
	public void defaultPDefinition(PDefinition node) throws AnalysisException
	{
		node.setUsed(true);
	}

}
