package org.overture.typechecker;

import java.util.Collection;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.intf.lex.ILexIdentifierToken;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.typechecker.NameScope;

/**
 * Use with the @{code Environment} class. If the environment fails to find 
 * a type or a name and it has an instance of {@code this} class, this class 
 * if given a chance to find the content.
 * 
 * @author rwl
 *
 */
public interface EnvironmentSearchStrategy {

	/** Extended strategy for finding a type */
	abstract public PDefinition findType(ILexNameToken name, String fromModule,
			PDefinition enclosingDefinition, 
			Environment outer, 
			Collection<PDefinition> currentDefinitions);

	/** Extended strategy for finding a name */
	abstract public PDefinition findName(ILexNameToken name, NameScope scope,
			PDefinition enclosingDefinition, 
			Environment outer, 
			Collection<PDefinition> currentDefinitions);

	/** Extended strategy for find any definition with the given name */
	abstract public PDefinition find(ILexIdentifierToken name, 
			PDefinition enclosingDefinition, 
			Environment outer, 
			Collection<PDefinition> currentDefinitions);
}
