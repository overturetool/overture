package org.overture.typechecker.tests;

import java.io.File;
import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.modules.AModuleModules;
import org.overture.typecheck.ClassTypeChecker;
import org.overture.typecheck.ModuleTypeChecker;
import org.overture.typecheck.TypeChecker;
import org.overturetool.ast.factory.AstFactoryPS;
import org.overturetool.test.framework.results.IMessage;
import org.overturetool.test.framework.results.Message;
import org.overturetool.test.framework.results.Result;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.messages.VDMError;
import org.overturetool.vdmj.messages.VDMWarning;
import org.overturetool.vdmj.syntax.ClassReader;
import org.overturetool.vdmj.syntax.ModuleReader;
import org.overturetool.vdmj.syntax.ParserException;
import org.overturetool.vdmj.syntax.SyntaxReader;

public class OvertureTestHelper
{
	public Result typeCheckSl(File file)
	{
		Result<List<AModuleModules>> parserResult = parseCheckSl(file);
		if (parserResult.errors.isEmpty())
		{
			ModuleTypeChecker mtc = new ModuleTypeChecker(parserResult.result);
			mtc.typeCheck();
			return collectTypeResults(mtc);
		}
		return parserResult;
	}
	public Result typeCheckPp(File file)
	{
		Result<List<SClassDefinition>> parserResult = parseCheckPp(file);
		if (parserResult.errors.isEmpty())
		{
			ClassTypeChecker mtc = new ClassTypeChecker(parserResult.result);
			mtc.typeCheck();
			return collectTypeResults(mtc);
		}
		return parserResult;
	}
	
	public Result typeCheckRt(File file)
	{
		Result<List<SClassDefinition>> parserResult = parseCheckPp(file);
		if (parserResult.errors.isEmpty())
		{
			List<SClassDefinition> classes = new Vector<SClassDefinition>();
			classes.addAll(parserResult.result);
			try {
				classes.add(AstFactoryPS.newACpuClassDefinition());
				classes.add(AstFactoryPS.newABusClassDefinition());
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
			
			//			classes.add(new ACpuClassDefinition(location_, name_, nameScope_, used_, access_, type_, supernames_, hasContructors_, settingHierarchy_, gettingInheritable_, gettingInvDefs_, isAbstract_, isUndefined_))
//			classes.add(new ASystemClassDefinition(location_, name_, nameScope_, used_, access_, type_, supernames_, hasContructors_, settingHierarchy_, gettingInheritable_, gettingInvDefs_, isAbstract_, isUndefined_))
			
		}
		return parserResult;
	}


	

	public Result<List<SClassDefinition>> parseCheckPp(File file)
	{
		LexTokenReader ltr=new LexTokenReader(file, Settings.dialect);
		ClassReader reader = null;
		List<SClassDefinition> result = null;

		List<IMessage> errors = new Vector<IMessage>();
		List<IMessage> warnings = new Vector<IMessage>();

		try
		{
			reader = new ClassReader(ltr);
			result = reader.readClasses();
			collectParserErrorsAndWarnings(reader, errors, warnings);
		} catch (Exception e)
		{
			errors.add(new Message("Internal Parser", -1, -1, -1, e.getMessage()));
		}
		return new Result<List<SClassDefinition>>(result, warnings, errors);
	}
	
	

	public Result<List<AModuleModules>> parseCheckSl(File file)
	{
		LexTokenReader ltr=new LexTokenReader(file, Settings.dialect);
		ModuleReader reader = null;
		List<AModuleModules> result = null;

		List<IMessage> errors = new Vector<IMessage>();
		List<IMessage> warnings = new Vector<IMessage>();

		try
		{
			reader = new ModuleReader(ltr);
			result = reader.readModules();
			collectParserErrorsAndWarnings(reader, errors, warnings);
		} catch (Exception e)
		{
			errors.add(new Message("Internal Parser", -1, -1, -1, e.getMessage()));
		}
		return new Result<List<AModuleModules>>(result, warnings, errors);
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

	private static void collectParserErrorsAndWarnings(SyntaxReader reader,
			List<IMessage> errors, List<IMessage> warnings)
	{
		if (reader != null && reader.getErrorCount() > 0)
		{
			for (VDMError msg : reader.getErrors())
			{
				errors.add(new Message(msg.location.file.getName(), msg.number, msg.location.startLine, msg.location.startPos, msg.message));
			}
		}

		if (reader != null && reader.getWarningCount() > 0)
		{
			for (VDMWarning msg : reader.getWarnings())
			{
				warnings.add(new Message(msg.location.file.getName(), msg.number, msg.location.startLine, msg.location.startPos, msg.message));
			}
		}
	}
	
}
