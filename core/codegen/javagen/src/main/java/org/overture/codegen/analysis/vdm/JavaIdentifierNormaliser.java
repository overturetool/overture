package org.overture.codegen.analysis.vdm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.codegen.ir.ITempVarGen;
import org.overture.codegen.utils.GeneralCodeGenUtils;

public class JavaIdentifierNormaliser extends DepthFirstAnalysisAdaptor
{
	private Set<String> allNames;
	private Map<String, String> renamingsSoFar;
	private ITempVarGen nameGen;
	private Set<Renaming> renamings;
	
	public JavaIdentifierNormaliser(Set<String> allNames, ITempVarGen nameGen)
	{
		this.allNames = allNames;
		this.renamingsSoFar = new HashMap<String, String>();
		this.nameGen = nameGen;
		this.renamings = new HashSet<Renaming>();
	}
	
	@Override
	public void inILexNameToken(ILexNameToken node) throws AnalysisException
	{
		// "?" is used for implicitly named things
		if(node.getName().equals("?"))
		{
			return;
		}
		
		if(!GeneralCodeGenUtils.isValidJavaIdentifier(node.getName()))
		{
			String newName = getReplacementName(node.getName());
			
			if (!contains(node.getLocation()))
			{
				this.renamings.add(new Renaming(node.getLocation(), node.getName(), newName));
			}
		}
	}
	
	private boolean contains(ILexLocation loc)
	{
		for (Renaming r : renamings)
		{
			if (r.getLoc().equals(loc))
			{
				return true;
			}
		}

		return false;
	}
	
	public Set<Renaming> getRenamings()
	{
		return renamings;
	}
	
	public String getReplacementName(String invalidName)
	{
		String name = renamingsSoFar.get(invalidName);
		
		if(name != null)
		{
			// A replacement name has previously been computed for 'invalidName' just use that
			return name;
		}
		
		String suggestion = "";
		
		if (GeneralCodeGenUtils.isJavaKeyword(invalidName))
		{
			// appending '_' to a Java keyword makes it a valid identifier
			suggestion = invalidName + "_";
		} else
		{
			suggestion = patchName(invalidName);
		}
		
		// Now it is important that the suggestion does not collide with a name in the model
		
		if(allNames.contains(suggestion))
		{
			// Okay the name is already used so we need to compute a new one (e.g. <suggestion>_42)
			String prefix = suggestion + "_";
			suggestion = nameGen.nextVarName(prefix);

			while (allNames.contains(suggestion))
			{
				suggestion = nameGen.nextVarName(prefix);
			}
		}
		// else {the suggestion is valid and does not collide with another name in the model}
		
		// By now we should have computed a name that does not appear in the model
		
		// Register the name we are about to use to replace 'invalidName'
		renamingsSoFar.put(invalidName, suggestion);
		allNames.add(suggestion);
		
		return suggestion;
	}

	private String patchName(String invalidName)
	{
		// Say we have an invalid name such as s'
		final String PATCH = "_X_";
		List<Integer> correctionIndices = GeneralCodeGenUtils.computeJavaIdentifierCorrections(invalidName);
		
		String tmp = "";
		char[] chars = invalidName.toCharArray();
		for(int i = 0; i < chars.length; i++)
		{
			if(correctionIndices.contains(i))
			{
				tmp += PATCH;
			}
			else
			{
				tmp += chars[i];
			}
		}
		
		// Return the patch named (e.g. s_X_)
		return tmp;
	}
}
