package org.overture.ide.vdmsl.ui.internal.completion;

import org.eclipse.dltk.codeassist.IAssistParser;
import org.eclipse.dltk.codeassist.ScriptCompletionEngine;
import org.eclipse.dltk.compiler.env.ISourceModule;
import org.eclipse.dltk.core.IField;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IType;

public class VdmSlCompletionEngine extends ScriptCompletionEngine {

	@Override
	protected int getEndOfEmptyToken() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected String processFieldName(IField field, String token) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String processMethodName(IMethod method, String token) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String processTypeName(IType method, String token) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IAssistParser getParser() {
		// TODO Auto-generated method stub
		return null;
	}

	public void complete(ISourceModule module, int position, int i) {
		// TODO Auto-generated method stub
		
	}

}
