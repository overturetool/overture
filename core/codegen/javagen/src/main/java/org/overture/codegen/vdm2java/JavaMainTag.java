package org.overture.codegen.vdm2java;

import org.overture.codegen.cgast.declarations.ADefaultClassDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.types.AVoidTypeCG;


public class JavaMainTag
{
	private boolean isVoidRun;
	
	public JavaMainTag(ADefaultClassDeclCG classCg)
	{
		checkRunReturnType(classCg);
	}

	private void checkRunReturnType(ADefaultClassDeclCG classCg)
	{
		isVoidRun = false;
		for(AMethodDeclCG m : classCg.getMethods())
		{
			if(m.getName().equals("Run") && m.getMethodType().getResult() instanceof AVoidTypeCG)
			{
				isVoidRun = true;
			}
		}
	}

	public String getMainMethod()
	{
		String body;
		
		if(isVoidRun)
		{
			body = "Run();IO.println(Utils.toString(Utils.VOID_VALUE));";
		}
		else
		{
			body = "IO.println(Utils.toString(Run()));";
		}
		
		return "public static void main(String[] args){ " + body + " }";
	}
}
