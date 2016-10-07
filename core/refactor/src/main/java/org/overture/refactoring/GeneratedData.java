package org.overture.refactoring;

import java.util.List;

import org.overture.codegen.analysis.violations.InvalidNamesResult;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.extract.Extraction;
import org.overture.signature.SignatureChange;
import org.overture.rename.Renaming;

public class GeneratedData
{
	private List<GeneratedModule> classes;
	private List<GeneratedModule> quoteValues;
	private InvalidNamesResult invalidNamesResult;
	private List<String> skippedClasses;
	private List<Renaming> allRenamings;
	private List<Extraction> allExtractions;
	private List<SignatureChange> allSignatureChanges;
	private List<String> warnings;

	public GeneratedData()
	{
	}

	public GeneratedData(List<GeneratedModule> classes,
			List<GeneratedModule> quoteValues,
			InvalidNamesResult invalidNamesResult, List<String> skippedClasses)
	{
		super();
		this.classes = classes;
		this.quoteValues = quoteValues;
		this.invalidNamesResult = invalidNamesResult;
		this.skippedClasses = skippedClasses;
	}

	public boolean hasErrors()
	{
		return hasErrors(classes) || hasErrors(quoteValues);
	}

	public List<GeneratedModule> getClasses()
	{
		return classes;
	}

	public void setClasses(List<GeneratedModule> classes)
	{
		this.classes = classes;
	}

	public List<GeneratedModule> getQuoteValues()
	{
		return quoteValues;
	}

	public void setQuoteValues(List<GeneratedModule> quoteValues)
	{
		this.quoteValues = quoteValues;
	}

	public InvalidNamesResult getInvalidNamesResult()
	{
		return invalidNamesResult;
	}

	public void setInvalidNamesResult(InvalidNamesResult invalidNamesResult)
	{
		this.invalidNamesResult = invalidNamesResult;
	}

	public List<String> getSkippedClasses()
	{
		return skippedClasses;
	}

	public void setSkippedClasses(List<String> skippedClasses)
	{
		this.skippedClasses = skippedClasses;
	}

	public List<Renaming> getAllRenamings()
	{
		return allRenamings;
	}

	public void setAllRenamings(List<Renaming> allRenamings)
	{
		this.allRenamings = allRenamings;
	}

	public List<Extraction> getAllExtractions()
	{
		return allExtractions;
	}

	public void setAllExtractions(List<Extraction> allExtractions)
	{
		this.allExtractions = allExtractions;
	}
	
	public List<SignatureChange> getAllSignatureChanges()
	{
		return allSignatureChanges;
	}

	public void setAllSignatureChanges(List<SignatureChange> allSignatureChanges)
	{
		this.allSignatureChanges = allSignatureChanges;
	}
	
	public List<String> getWarnings()
	{
		return warnings;
	}

	public void setWarnings(List<String> warnings)
	{
		this.warnings = warnings;
	}

	private boolean hasErrors(List<GeneratedModule> modules)
	{
		if (modules != null)
		{
			for (GeneratedModule clazz : modules)
			{
				if (clazz.hasErrors())
				{
					return true;
				}
			}
		}
		return false;
	}
}

