package org.overture.typechecker.tests;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.modules.AModuleModules;
import org.overture.typecheck.ClassTypeChecker;
import org.overture.typecheck.ModuleTypeChecker;
import org.overture.typecheck.TypeChecker;
import org.overturetool.test.framework.results.IMessage;
import org.overturetool.test.framework.results.Message;
import org.overturetool.test.framework.results.Result;
import org.overturetool.vdmjV2.Settings;
import org.overturetool.vdmjV2.lex.LexTokenReader;
import org.overturetool.vdmjV2.messages.VDMError;
import org.overturetool.vdmjV2.messages.VDMWarning;
import org.overturetool.vdmjV2.syntax.ClassReader;
import org.overturetool.vdmjV2.syntax.ModuleReader;
import org.overturetool.vdmjV2.syntax.SyntaxReader;

public class OvertureTestHelper
{
	@SuppressWarnings({ "rawtypes" })
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
	@SuppressWarnings({ "rawtypes" })
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
	
	@SuppressWarnings({ "rawtypes" })
	public Result typeCheckRt(File file)
	{
		Result<List<SClassDefinition>> parserResult = parseCheckPp(file);
		if (parserResult.errors.isEmpty())
		{
			List<SClassDefinition> classes = new Vector<SClassDefinition>();
			classes.addAll(parserResult.result);
			//			classes.add(new ACpuClassDefinition(location_, name_, nameScope_, used_, access_, type_, supernames_, hasContructors_, settingHierarchy_, gettingInheritable_, gettingInvDefs_, isAbstract_, isUndefined_))
//			classes.add(new ASystemClassDefinition(location_, name_, nameScope_, used_, access_, type_, supernames_, hasContructors_, settingHierarchy_, gettingInheritable_, gettingInvDefs_, isAbstract_, isUndefined_))
			ClassTypeChecker mtc = new ClassTypeChecker(classes);
			mtc.typeCheck();
			return collectTypeResults(mtc);
		}
		return parserResult;
	}


	

	public Result<List<SClassDefinition>> parseCheckPp(File file)
	{
		LexTokenReader ltr=new LexTokenReader(file, Settings.dialect);
		ClassReader reader = null;
		List<SClassDefinition> result = null;

		Set<IMessage> errors = new HashSet<IMessage>();
		Set<IMessage> warnings = new HashSet<IMessage>();

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

		Set<IMessage> errors = new HashSet<IMessage>();
		Set<IMessage> warnings = new HashSet<IMessage>();

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
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Result collectTypeResults(TypeChecker mtc)
	{
		Set<IMessage> errors = new HashSet<IMessage>();
		Set<IMessage> warnings = new HashSet<IMessage>();
		if (mtc != null && TypeChecker.getErrorCount() > 0)
		{

			for (VDMError msg : TypeChecker.getErrors())
			{
				errors.add(new Message(msg.location.file.getName(), msg.number, msg.location.startLine, msg.location.endPos, msg.message));
			}
		}

		if (mtc != null && TypeChecker.getWarningCount() > 0)
		{
			for (VDMWarning msg : TypeChecker.getWarnings())
			{
				warnings.add(new Message(msg.location.file.getName(), msg.number, msg.location.startLine, msg.location.endPos, msg.message));
			}
		}
		return new Result("some result", warnings, errors);
	}

	protected void collectParserErrorsAndWarnings(SyntaxReader reader,
			Set<IMessage> errors, Set<IMessage> warnings)
	{
		if (reader != null && reader.getErrorCount() > 0)
		{
			for (VDMError msg : reader.getErrors())
			{
				errors.add(new Message(msg.location.file.getName(), msg.number, msg.location.startLine, msg.location.endPos, msg.message));
			}
		}

		if (reader != null && reader.getWarningCount() > 0)
		{
			for (VDMWarning msg : reader.getWarnings())
			{
				warnings.add(new Message(msg.location.file.getName(), msg.number, msg.location.startLine, msg.location.endPos, msg.message));
			}
		}
	}
}
