package org.overture.ide.ui.completion.templates;

import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.ui.templates.ScriptTemplateContext;
import org.eclipse.dltk.ui.templates.ScriptTemplateContextType;
import org.eclipse.jface.text.IDocument;

public class VdmUniversalTemplateContextType extends ScriptTemplateContextType {

	
	@Override
	public ScriptTemplateContext createContext(IDocument document, int completionPosition, int length, ISourceModule sourceModule) {
		return new VdmTemplateContext(this, document, completionPosition, length, sourceModule);
	}
	
	public VdmUniversalTemplateContextType() {
	}
 
	public VdmUniversalTemplateContextType(String id, String name) {
		super(id, name);
	}
 
	public VdmUniversalTemplateContextType(String id) {
		super(id);
	}

}
