package org.overture.codegen.analysis.vdm;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.statements.AIdentifierStateDesignator;
import org.overture.codegen.logging.Logger;

public class IdDesignatorOccurencesCollector extends DepthFirstAnalysisAdaptor 
{
	private ILexLocation defLoc;
	private Set<AIdentifierStateDesignator> idOccurences;
	private Map<AIdentifierStateDesignator, PDefinition> idDefs;
	
	public IdDesignatorOccurencesCollector(ILexLocation defLoc, Map<AIdentifierStateDesignator, PDefinition> idDefs)
	{
		this.defLoc = defLoc;
		this.idOccurences = new HashSet<AIdentifierStateDesignator>();
		this.idDefs = idDefs;
	}

	public Set<AIdentifierStateDesignator> getIds()
	{
		return idOccurences;
	}
	
	@Override
	public void caseAIdentifierStateDesignator(
			AIdentifierStateDesignator node) throws AnalysisException
	{
		PDefinition def = idDefs.get(node);
		
		if(def == null)
		{
			Logger.getLog().printErrorln("Could not find definition for " + node + " in '" + this.getClass().getSimpleName() + "'");
		}
		else
		{
			if(def.getLocation().equals(defLoc))
			{
				this.idOccurences.add(node);
			}
		}
	}
}
