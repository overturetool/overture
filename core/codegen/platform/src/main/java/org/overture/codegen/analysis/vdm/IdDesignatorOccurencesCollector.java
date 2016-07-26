package org.overture.codegen.analysis.vdm;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.statements.AIdentifierStateDesignator;

public class IdDesignatorOccurencesCollector extends DepthFirstAnalysisAdaptor 
{
	private ILexLocation defLoc;
	private Set<AIdentifierStateDesignator> idOccurences;
	private Map<AIdentifierStateDesignator, PDefinition> idDefs;
	
	private Logger log = Logger.getLogger(this.getClass().getName());
	
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
			log.error("Could not find definition for " + node);
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
