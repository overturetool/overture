package org.overture.refactoring;

import java.util.ArrayList;
import java.util.List;

import org.overture.add.remove.parameter.SignatureChange;
import org.overture.codegen.analysis.violations.InvalidNamesResult;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.convert.function.to.operation.ConversionFromFuncToOp;
import org.overture.dead.model.part.removal.Removal;
import org.overture.extract.Extraction;

import org.overture.rename.Renaming;

public class GeneratedData
{
	private List<GeneratedModule> classes;
	private List<GeneratedModule> quoteValues;
	private List<Renaming> allRenamings;
	private List<Extraction> allExtractions;
	private List<SignatureChange> allSignatureChanges;
	private List<Removal> allRemovals;
	private List<ConversionFromFuncToOp> allConversionFromFuncToOp;
	private List<String> warnings;

	public GeneratedData()
	{
	}

	public GeneratedData(List<GeneratedModule> classes,
			List<GeneratedModule> quoteValues,
			InvalidNamesResult invalidNamesResult, List<String> skippedClasses)
	{
		super();
		warnings = new ArrayList<String>();
	}

	public boolean hasErrors()
	{
		return hasErrors(classes) || hasErrors(quoteValues);
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
	
	public List<Removal> getAllRemovals()
	{
		return allRemovals;
	}
	
	public void setAllRemovals(List<Removal> allRemovals) {
		this.allRemovals = allRemovals;
	}
	
	public List<String> getWarnings()
	{
		return warnings;
	}

	public void addAllWarnings(List<String> warnings)
	{
		if(warnings != null && !warnings.isEmpty()){
			this.warnings.addAll(warnings);
		}
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

	public void setAllConversionFromFuncToOp(List<ConversionFromFuncToOp> allConversionFromFuncToOp) {
		this.allConversionFromFuncToOp = allConversionFromFuncToOp;
	}
	
	public List<ConversionFromFuncToOp> getAllConversionFromFuncToOp() {
		return allConversionFromFuncToOp;
	}
}

