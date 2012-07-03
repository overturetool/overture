package org.overture.interpreter.tests;

import java.io.File;
import java.util.List;
import java.util.Vector;

import org.overture.vdmjUtils.VdmjCompatibilityUtils;
import org.overturetool.test.framework.results.IMessage;
import org.overturetool.test.framework.results.Message;
import org.overturetool.test.framework.results.Result;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.definitions.BUSClassDefinition;
import org.overturetool.vdmj.definitions.CPUClassDefinition;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.messages.VDMError;
import org.overturetool.vdmj.messages.VDMWarning;
import org.overturetool.vdmj.modules.Module;
import org.overturetool.vdmj.modules.ModuleList;
import org.overturetool.vdmj.runtime.ClassInterpreter;
import org.overturetool.vdmj.runtime.ModuleInterpreter;
import org.overturetool.vdmj.syntax.ClassReader;
import org.overturetool.vdmj.syntax.ModuleReader;
import org.overturetool.vdmj.syntax.ParserException;
import org.overturetool.vdmj.typechecker.ClassTypeChecker;
import org.overturetool.vdmj.typechecker.ModuleTypeChecker;
import org.overturetool.vdmj.typechecker.TypeChecker;
import org.overturetool.vdmj.values.Value;

public class OvertureTestHelper
{
	
	
	public  Value interpret(Dialect dialect, String entry, File file)
			throws Exception
	{
		switch (dialect)
		{

			case VDM_SL:
			{
				Result<List<Module>> parserResult = parseCheckSl(file);
				if (parserResult.errors.isEmpty())
				{
					ModuleList moduleList = new ModuleList(parserResult.result);
					ModuleTypeChecker mtc = new ModuleTypeChecker(moduleList);
					mtc.typeCheck();
				

					if (TypeChecker.getErrors().isEmpty())
					{
						ModuleInterpreter interpreter = new ModuleInterpreter(moduleList);												
						interpreter.init(null);
						interpreter.setDefaultName(moduleList.get(0).getName());
						Value val = interpreter.execute(entry, null);
						return val;
					}
				}
			}
			case VDM_PP:
			{
				Result<ClassList> parserResult = parseCheckPp(file);

				if (parserResult.errors.isEmpty())
				{
					
					ClassTypeChecker mtc = new ClassTypeChecker(parserResult.result);
					mtc.typeCheck();
					
					ClassInterpreter interpreter = new ClassInterpreter(parserResult.result);
					interpreter.init(null);
					interpreter.setDefaultName(parserResult.result.get(0).getName());
					Value val = interpreter.execute(entry, null);
					return val;
				}
			}
			case VDM_RT:
			{
				Result<ClassList> parserResult = parseCheckPp(file);
				ClassList classes = parserResult.result;
				if (parserResult.errors.isEmpty())
				{
					try {
						classes.add(new CPUClassDefinition());
						classes.add(new BUSClassDefinition());
						ClassTypeChecker mtc = new ClassTypeChecker(classes);
						
						mtc.typeCheck();
						
						if(TypeChecker.getErrorCount() > 0 )
						{
							for (VDMError err : TypeChecker.getErrors())
							{
								System.out.println(err);
							}
							return null;
						}
						
						ClassInterpreter interpreter = new ClassInterpreter(parserResult.result);
						interpreter.init(null);
						interpreter.setDefaultName(parserResult.result.get(0).getName());
						Value val = interpreter.execute(entry, null);
						return val;
					}catch (ParserException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (LexException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
								
			}
		}
		return null;
	}
	
	public Result<Boolean> typeCheckSl(File file)
	{
		Result<List<Module>> parserResult = parseCheckSl(file);
		if (parserResult.errors.isEmpty())
		{
			ModuleTypeChecker mtc = new ModuleTypeChecker(new ModuleList(parserResult.result));
			mtc.typeCheck();
			return collectTypeResults(mtc);
		}
		return new Result<Boolean>(false, parserResult.warnings,parserResult.errors);
	}

	public Result<Boolean> typeCheckPp(File file)
	{
		Result<ClassList> parserResult = parseCheckPp(file);
		if (parserResult.errors.isEmpty())
		{			
			ClassTypeChecker mtc = new ClassTypeChecker(parserResult.result);
			mtc.typeCheck();
			return collectTypeResults(mtc);
		}
		return new Result<Boolean>(false, parserResult.warnings,parserResult.errors);
	}
	
	
	public Result<Boolean> typeCheckRt(File file)
	{
		Result<ClassList> parserResult = parseCheckPp(file);
		if (parserResult.errors.isEmpty())
		{
			ClassList classes = new ClassList();
			classes.addAll(parserResult.result);
			try {
				classes.add(new CPUClassDefinition());
				classes.add(new BUSClassDefinition());
				ClassTypeChecker mtc = new ClassTypeChecker(classes);
				
				mtc.typeCheck();
				return collectTypeResults(mtc);
			} catch (ParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (LexException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return new Result<Boolean>(false, parserResult.warnings,parserResult.errors);
	}


	

	public Result<ClassList> parseCheckPp(File file)
	{
		LexTokenReader ltr=new LexTokenReader(file, Settings.dialect);
		ClassReader reader = null;
		ClassList result = null;

		List<IMessage> errors = new Vector<IMessage>();
		List<IMessage> warnings = new Vector<IMessage>();

		try
		{
			reader = new ClassReader(ltr);
			result = reader.readClasses();
			VdmjCompatibilityUtils.collectParserErrorsAndWarnings(reader, errors, warnings);
		} catch (Exception e)
		{
			errors.add(new Message("Internal Parser", -1, -1, -1, e.getMessage()));
		}
		return new Result<ClassList>(result, warnings, errors);
	}
	
	

	public Result<List<Module>> parseCheckSl(File file)
	{
		LexTokenReader ltr=new LexTokenReader(file, Settings.dialect);
		ModuleReader reader = null;
		List<Module> result = null;

		List<IMessage> errors = new Vector<IMessage>();
		List<IMessage> warnings = new Vector<IMessage>();

		try
		{
			reader = new ModuleReader(ltr);
			result = reader.readModules();
			VdmjCompatibilityUtils.collectParserErrorsAndWarnings(reader, errors, warnings);
		} catch (Exception e)
		{
			errors.add(new Message("Internal Parser", -1, -1, -1, e.getMessage()));
		}
		return new Result<List<Module>>(result, warnings, errors);
	}
	
	
	protected Result<Boolean> collectTypeResults(TypeChecker mtc)
	{
		List<IMessage> errors = new Vector<IMessage>();
		List<IMessage> warnings = new Vector<IMessage>();
		if (mtc != null && TypeChecker.getErrorCount() > 0)
		{

			for (VDMError msg : TypeChecker.getErrors())
			{
				errors.add(new Message(msg.location.file.getName(), msg.number, msg.location.startLine, msg.location.startPos, msg.message));
			}
		}

		if (mtc != null && TypeChecker.getWarningCount() > 0)
		{
			for (VDMWarning msg : TypeChecker.getWarnings())
			{
				warnings.add(new Message(msg.location.file.getName(), msg.number, msg.location.startLine, msg.location.startPos, msg.message));
			}
		}
		return new Result<Boolean>(true, warnings, errors);
	}

	
}
