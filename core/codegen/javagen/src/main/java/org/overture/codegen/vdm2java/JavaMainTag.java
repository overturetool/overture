package org.overture.codegen.vdm2java;

import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.types.AVoidTypeIR;

public class JavaMainTag
{
	private boolean isVoidRun;

	public JavaMainTag(ADefaultClassDeclIR classCg)
	{
		checkRunReturnType(classCg);
	}

	private void checkRunReturnType(ADefaultClassDeclIR classCg)
	{
		isVoidRun = false;
		for (AMethodDeclIR m : classCg.getMethods())
		{
			if (m.getName().equals("Run")
					&& m.getMethodType().getResult() instanceof AVoidTypeIR)
			{
				isVoidRun = true;
			}
		}
	}

	public String getMainMethod()
	{
		String body;

		if (isVoidRun)
		{
			body = "Run();IO.println(Utils.toString(Utils.VOID_VALUE));";
		} else
		{
			body = "IO.println(Utils.toString(Run()));";
		}

		return "public static void main(String[] args){ " + body + " }";
	}
}
