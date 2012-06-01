package org.overture.vdmj.typechecker.tests;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.messages.VDMError;
import org.overturetool.vdmj.messages.VDMWarning;
import org.overturetool.vdmj.modules.Module;
import org.overturetool.vdmj.modules.ModuleList;
import org.overturetool.vdmj.syntax.ClassReader;
import org.overturetool.vdmj.syntax.ModuleReader;
import org.overturetool.vdmj.syntax.ParserException;
import org.overturetool.vdmj.syntax.SyntaxReader;
import org.overturetool.vdmj.typechecker.ClassTypeChecker;
import org.overturetool.vdmj.typechecker.ModuleTypeChecker;
import org.overturetool.vdmj.typechecker.TypeChecker;

public class OvertureTestHelper
{
	@SuppressWarnings({ "rawtypes" })
	public Result typeCheckSl(File file)
	{
		Result<List<Module>> parserResult = parseCheckSl(file);
		if (parserResult.errors.isEmpty())
		{
			ModuleTypeChecker mtc = new ModuleTypeChecker(new ModuleList(parserResult.result));
			mtc.typeCheck();
			return collectTypeResults(mtc);
		}
		return parserResult;
	}
	@SuppressWarnings({ "rawtypes" })
	public Result typeCheckPp(File file)
	{
		Result<List<ClassDefinition>> parserResult = parseCheckPp(file);
		if (parserResult.errors.isEmpty())
		{
			ClassList classes = new ClassList();
			classes.addAll(parserResult.result);
			ClassTypeChecker mtc = new ClassTypeChecker(classes);
			mtc.typeCheck();
			return collectTypeResults(mtc);
		}
		return parserResult;
	}
	
	@SuppressWarnings({ "rawtypes" })
	public Result typeCheckRt(File file)
	{
		Result<List<ClassDefinition>> parserResult = parseCheckPp(file);
		if (parserResult.errors.isEmpty())
		{
			ClassList classes = new ClassList();
			classes.addAll(parserResult.result);
			//			classes.add(new ACpuClassDefinition(location_, name_, nameScope_, used_, access_, type_, supernames_, hasContructors_, settingHierarchy_, gettingInheritable_, gettingInvDefs_, isAbstract_, isUndefined_))
//			classes.add(new ASystemClassDefinition(location_, name_, nameScope_, used_, access_, type_, supernames_, hasContructors_, settingHierarchy_, gettingInheritable_, gettingInvDefs_, isAbstract_, isUndefined_))
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
		return parserResult;
	}


	

	public Result<List<ClassDefinition>> parseCheckPp(File file)
	{
		LexTokenReader ltr=new LexTokenReader(file, Settings.dialect);
		ClassReader reader = null;
		List<ClassDefinition> result = null;

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
		return new Result<List<ClassDefinition>>(result, warnings, errors,null);
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
		return new Result<List<Module>>(result, warnings, errors,null);
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Result collectTypeResults(TypeChecker mtc)
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
		return new Result("some result", warnings, errors,null);
	}

	
}
