package org.overture.vdmjUtils;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.overturetool.test.framework.results.IMessage;
import org.overturetool.test.framework.results.Message;
import org.overturetool.test.framework.results.Result;
import org.overturetool.vdmj.messages.VDMError;
import org.overturetool.vdmj.messages.VDMWarning;
import org.overturetool.vdmj.syntax.SyntaxReader;
import org.overturetool.vdmj.typechecker.ModuleTypeChecker;
import org.overturetool.vdmj.typechecker.TypeChecker;


public class VdmjCompatibilityUtils {

	@SuppressWarnings({"rawtypes" })
	public static Result convertToResult(SyntaxReader reader,File file, String name) {
		Set<IMessage> warnings = new HashSet<IMessage>();
		Set<IMessage> errors = new HashSet<IMessage>();
		
		for (VDMWarning warning : reader.getWarnings()) {
			warnings.add(new Message(file.getName(), warning.number, warning.location.startLine, warning.location.startPos, warning.message));
		}
		for (VDMError error : reader.getErrors()) {
			errors.add(new Message(file.getName(), error.number, error.location.startLine, error.location.startPos, error.message));
		}
		
		return  new Result<String>(name, warnings, errors);
		
	}

	public static Result convertToResult(ModuleTypeChecker module, File file,
			String name) {
		Set<IMessage> warnings = new HashSet<IMessage>();
		Set<IMessage> errors = new HashSet<IMessage>();
		
		for (VDMWarning warning : TypeChecker.getWarnings()) {
			warnings.add(new Message(file.getName(), warning.number, warning.location.startLine, warning.location.startPos, warning.message));
		}
		for (VDMError error : TypeChecker.getErrors()) {
			errors.add(new Message(file.getName(), error.number, error.location.startLine, error.location.startPos, error.message));
		}
		
		return  new Result<String>(name, warnings, errors);
	}
}
