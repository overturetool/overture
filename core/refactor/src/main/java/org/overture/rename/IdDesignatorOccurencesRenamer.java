package org.overture.rename;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.log4j.Logger;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.statements.AIdentifierStateDesignator;

public class IdDesignatorOccurencesRenamer extends DepthFirstAnalysisAdaptor
{
	private ILexLocation defLoc;
	private Set<AIdentifierStateDesignator> idOccurences;
	private Map<AIdentifierStateDesignator, PDefinition> idDefs;
	private Consumer<RenameObject> function;
	private String newName;
	private Logger log = Logger.getLogger(this.getClass().getName());

	public IdDesignatorOccurencesRenamer(ILexLocation defLoc,
			Map<AIdentifierStateDesignator, PDefinition> idDefs, Consumer<RenameObject> f, String newName)
	{
		this.defLoc = defLoc;
		this.idOccurences = new HashSet<AIdentifierStateDesignator>();
		this.idDefs = idDefs;
		this.function = f;
		this.newName = newName;
	}

	public Set<AIdentifierStateDesignator> getIds()
	{
		return idOccurences;
	}

	@Override
	public void caseAIdentifierStateDesignator(AIdentifierStateDesignator node)
			throws AnalysisException
	{
		PDefinition def = idDefs.get(node);

		if (def == null)
		{
			log.error("Could not find definition for " + node);
		} else
		{
			if (def.getLocation().equals(defLoc))
			{
				function.accept(new RenameObject(node.getName(), newName, node::setName));
				this.idOccurences.add(node);
			}
		}
	}
}
