package org.overture.ide.ui.templates;

import java.util.HashSet;
import java.util.Set;

/**
 *TODO Temp class which has to be removed and replaced with the NameGen from package org.overture.codegen.vdm2jml.util; since it duplicates functionality
 * 
 * @author mlp
 */
public class NameGen
{
	private Set<String> toAvoid;
	
	public NameGen()
	{
		this.toAvoid = new HashSet<String>();
	}
	
	public String getName(String suggestion)
	{
		if (!toAvoid.contains(suggestion))
		{
			toAvoid.add(suggestion);
			return suggestion;
		} else
		{
			int counter = 1;

			String prefix = suggestion + "_";
			
			String newSuggestion = prefix + counter;

			while (toAvoid.contains(newSuggestion))
			{
				counter++;
				newSuggestion = prefix + counter;
			}
			
			toAvoid.add(newSuggestion);
			return newSuggestion;
		}
	}
}