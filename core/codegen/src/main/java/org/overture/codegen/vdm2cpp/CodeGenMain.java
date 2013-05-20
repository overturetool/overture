package org.overture.codegen.vdm2cpp;

import java.io.File;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.config.Settings;
import org.overture.parser.util.ParserUtil;
import org.overture.parser.util.ParserUtil.ParserResult;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

public class CodeGenMain
{
	public static void main(String[] args)
	{
		Settings.dialect = Dialect.VDM_RT;
		
		if (args.length <= 0)
			System.out.println("Wrong input!");

		for (String fileName : args)
		{
			File file = new File(fileName);

			if (!file.exists() || !file.isFile())
			{
				System.out.println("Could not find file: " + file.getAbsolutePath());
				continue;
			}

			ParserResult<List<SClassDefinition>> parseResult = ParserUtil.parseOo(file);

			if (parseResult.errors.size() > 0)
			{
				System.out.println("File did not parse: " + file.getAbsolutePath());
				continue;
			}
			
			TypeCheckResult<List<SClassDefinition>> typeCheckResult = TypeCheckerUtil.typeCheckPp(file);
			
			if(typeCheckResult.errors.size() > 0)
			{
				System.out.println("File did not pass the type check: " + fileName);
				continue;
			}
				
			Vdm2Cpp vdm2cpp = new Vdm2Cpp();
			try
			{
				vdm2cpp.generateCode(typeCheckResult.result);
				//vdm2cpp.save(contextMap);
			} catch (AnalysisException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}

	}

}
