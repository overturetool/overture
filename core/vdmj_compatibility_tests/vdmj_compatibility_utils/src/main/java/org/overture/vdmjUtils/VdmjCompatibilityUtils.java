package org.overture.vdmjUtils;
import java.io.File;
import java.util.List;
import java.util.Vector;

import org.overturetool.test.framework.results.IMessage;
import org.overturetool.test.framework.results.Message;
import org.overturetool.test.framework.results.Result;
import org.overturetool.vdmj.messages.VDMError;
import org.overturetool.vdmj.messages.VDMWarning;
import org.overturetool.vdmj.pog.ProofObligation;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.syntax.SyntaxReader;
import org.overturetool.vdmj.typechecker.TypeChecker;


public class VdmjCompatibilityUtils {

	@SuppressWarnings({"rawtypes" })
	public static Result convertToResult(SyntaxReader reader,File file, String name) {
		List<IMessage> warnings = new Vector<IMessage>();
		List<IMessage> errors = new Vector<IMessage>();
		
		for (VDMWarning warning : reader.getWarnings()) {
			warnings.add(new Message(file.getName(), warning.number, warning.location.startLine, warning.location.startPos, warning.message));
		}
		for (VDMError error : reader.getErrors()) {
			errors.add(new Message(file.getName(), error.number, error.location.startLine, error.location.startPos, error.message));
		}
		
		return  new Result<String>(name, warnings, errors, null);
		
	}

	public static Result convertToResult( File file,
			String name) {
		List<IMessage> warnings = new Vector<IMessage>();
		List<IMessage> errors = new Vector<IMessage>();
		
		for (VDMWarning warning : TypeChecker.getWarnings()) {
			warnings.add(new Message(file.getName(), warning.number, warning.location.startLine, warning.location.startPos, warning.message));
		}
		for (VDMError error : TypeChecker.getErrors()) {
			errors.add(new Message(file.getName(), error.number, error.location.startLine, error.location.startPos, error.message));
		}
		
		return  new Result<String>(name, warnings, errors,null);
	}
	
	public static void collectParserErrorsAndWarnings(SyntaxReader reader,
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

	public static Result convertPOsToResult(ProofObligationList pos, File file,
			String name) {
		
		List<IMessage> messageList = new Vector<IMessage>();
		
		for (ProofObligation proofObligation : pos) {
			Message m = new Message(file.getName(), proofObligation.number, proofObligation.location.startLine, proofObligation.location.startPos, proofObligation.toString());
			messageList.add(m);
		}
		
		return new Result<String>(name, null, null, messageList);
	}
}
