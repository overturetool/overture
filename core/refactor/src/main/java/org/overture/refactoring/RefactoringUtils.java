package org.overture.refactoring;

import java.io.File;

import org.overture.ast.intf.lex.ILexLocation;
import org.overture.parser.messages.VDMError;
import org.overture.parser.messages.VDMWarning;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

public class RefactoringUtils {

	public static boolean isVdmSourceFile(File f)
	{
		return isVdmPpSourceFile(f) || isVdmSlSourceFile(f)
				|| isVdmRtSourceFile(f);
	}
	
	public static boolean isVdmRtSourceFile(File f)
	{
		return hasExtension(f, new String[] { ".vdmrt", ".vrt" });
	}

	public static boolean isVdmSlSourceFile(File f)
	{
		return hasExtension(f, new String[] { ".vsl", ".vdmsl" });
	}

	public static boolean isVdmPpSourceFile(File f)
	{
		return hasExtension(f, new String[] { ".vdmpp", ".vpp" });
	}

	private static boolean hasExtension(File f, String[] extensions)
	{
		for (String ext : extensions)
		{
			if (f.getName().endsWith(ext))
			{
				return true;
			}
		}

		return false;
	}
	
	public static boolean hasErrors(TypeCheckResult<?> tcResult)
	{
		return !tcResult.parserResult.errors.isEmpty()
				|| !tcResult.errors.isEmpty();
	}
	
	public static String errorStr(TypeCheckResult<?> tcResult)
	{
		if (tcResult == null)
		{
			return "No type check result found!";
		}

		StringBuilder sb = new StringBuilder();

		if (!tcResult.parserResult.warnings.isEmpty())
		{
			sb.append("Parser warnings:").append('\n');
			for (VDMWarning w : tcResult.parserResult.warnings)
			{
				sb.append(w).append('\n');
			}
			sb.append('\n');
		}

		if (!tcResult.parserResult.errors.isEmpty())
		{
			sb.append("Parser errors:").append('\n');
			for (VDMError e : tcResult.parserResult.errors)
			{
				sb.append(e).append('\n');
			}
			sb.append('\n');
		}

		if (!tcResult.warnings.isEmpty())
		{
			sb.append("Type check warnings:").append('\n');
			for (VDMWarning w : tcResult.warnings)
			{
				sb.append(w).append('\n');
			}
			sb.append('\n');
		}

		if (!tcResult.errors.isEmpty())
		{
			sb.append("Type check errors:").append('\n');
			for (VDMError w : tcResult.errors)
			{
				sb.append(w).append('\n');
			}
			sb.append('\n');
		}

		return sb.toString();
	}
	
	public static boolean compareNodeLocations(ILexLocation loc1, ILexLocation loc2){
		if(loc1.getStartLine() == loc2.getStartLine() && 
				loc1.getStartOffset() == loc2.getStartOffset()){
			return true;
		}else{
			return false;
		}
	}
}
