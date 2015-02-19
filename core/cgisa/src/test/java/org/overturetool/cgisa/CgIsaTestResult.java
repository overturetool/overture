package org.overturetool.cgisa;

import java.util.List;

import org.overture.codegen.utils.GeneratedModule;

public class CgIsaTestResult
{
	String translation;
	boolean errors;

	public CgIsaTestResult()
	{
	}

	private CgIsaTestResult(String translation, boolean errors)
	{
		super();
		this.translation = translation;
		this.errors = errors;
	}
	
	

	@Override
	public String toString()
	{
		return "CgIsaTestResult [translation=" + translation + ", errors="
				+ errors + "]";
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (errors ? 1231 : 1237);
		result = prime * result
				+ ((translation == null) ? 0 : translation.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CgIsaTestResult other = (CgIsaTestResult) obj;
		if (errors != other.errors)
			return false;
		if (translation == null)
		{
			if (other.translation != null)
				return false;
		} else if (!translation.equals(other.translation))
			return false;
		return true;
	}

	public boolean compare(CgIsaTestResult other){
		if (errors != other.errors){
			return false;
		}
		if (!translation.replaceAll("\\s",  "").equals(other.translation.replaceAll("\\s", ""))){
			return false;
		}
		
		return true;
	}
	
	public static CgIsaTestResult convert(List<GeneratedModule> result)
	{
		StringBuilder trans = new StringBuilder();
		boolean err = false;

		for (GeneratedModule g : result)
		{
			if (g.hasMergeErrors())
			{
				err = true;

			} else if (!g.canBeGenerated())
			{
				err = true;
			} else if (g.hasUnsupportedIrNodes())
			{
				err = true;
			}

			else
			{
				trans.append(g.getContent());

			}

		}
		return new CgIsaTestResult(trans.toString(), err);
	}
}
