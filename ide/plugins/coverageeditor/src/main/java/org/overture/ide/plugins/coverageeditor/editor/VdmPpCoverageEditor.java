package org.overture.ide.plugins.coverageeditor.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.overture.ide.vdmpp.ui.editor.core.VdmPpEditor;

public class VdmPpCoverageEditor extends VdmPpEditor
{
	private CoverageEditor covEditor = new CoverageEditor()
	{

		@Override
		protected ISourceViewer getEditorSourceViewer()
		{
			return getSourceViewer();
		}

		@Override
		protected void setEditorDocumentProvider(IDocumentProvider provider)
		{
			setDocumentProvider(provider);

		}

	};

	public VdmPpCoverageEditor()
	{
		super();
		covEditor.setEditorDocumentProvider();
	}

	@Override
	public void doSave(IProgressMonitor monitor)
	{
		monitor.done();
	}

	@Override
	public void doSaveAs()
	{

	}
 
	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException
	{
		super.init(site, input);

		covEditor.init(site, input);

	}

	@Override
	public boolean isDirty()
	{
		return false;
	}

	@Override
	public boolean isSaveAsAllowed()
	{
		return false;
	}

	@Override
	public void createPartControl(Composite parent)
	{
		super.createPartControl(parent);
		covEditor.createPartControl(parent);

	}

//	@Override
//	public void setFocus()
//	{
//	}

	@Override
	public boolean isEditable()
	{
		return false;
	}

}
