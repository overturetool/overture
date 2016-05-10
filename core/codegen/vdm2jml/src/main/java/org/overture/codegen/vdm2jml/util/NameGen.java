package org.overture.codegen.vdm2jml.util;

import java.util.HashSet;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.node.INode;
import org.overture.codegen.analysis.vdm.NameCollector;
import org.overture.codegen.assistant.AssistantBase;
import org.overture.codegen.ir.declarations.SClassDeclIR;
import org.overture.codegen.logging.Logger;

/**
 * Convenience class for generating parameter names that do not collide with other names used in a given class.
 * 
 * @author pvj
 */
public class NameGen
{
	private Set<String> toAvoid;
	
	public NameGen()
	{
		this.toAvoid = new HashSet<>();
	}
	
	public NameGen(INode vdmNode)
	{
		this();
		
		if(vdmNode != null)
		{
			NameCollector collector = new NameCollector();
			
			try
			{
				vdmNode.apply(collector);
				this.toAvoid.addAll(collector.namesToAvoid());
			} catch (AnalysisException e)
			{
				Logger.getLog().printErrorln("Problems encountered when trying to collect names from "
						+ vdmNode + " in '" + this.getClass().getSimpleName() + "'");
				e.printStackTrace();
			}
		}
	}
	
	public NameGen(SClassDeclIR classDecl)
	{
		this(AssistantBase.getVdmNode(classDecl));

	}
	
	public void addName(String name)
	{
		toAvoid.add(name);
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
