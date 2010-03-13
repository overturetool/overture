package org.overture.ide.ui.editor.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.views.contentoutline.ContentOutline;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.overture.ide.core.Activator;
import org.overture.ide.core.ast.NotAllowedException;
import org.overture.ide.core.parser.ISourceParser;
import org.overture.ide.core.parser.SourceParserManager;
import org.overture.ide.core.utility.VdmProject;
import org.overture.ide.ui.outline.VdmContentOutlinePage;

public class VdmReconcilingStrategy implements IReconcilingStrategy {

	private VdmDocument currentDocument;
	private ContentOutline outline =null;

	public VdmReconcilingStrategy() {
		IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();

		if (activeWorkbenchWindow != null) {
			IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
			if(activePage!=null)
			{
			IViewPart outlineCandidate = activePage.findView(IPageLayout.ID_OUTLINE);
			if(outlineCandidate instanceof ContentOutline)
			{
				outline = (ContentOutline) outlineCandidate;
			}
			}
		}
	}

	public void reconcile(IRegion partition) {
		if (Activator.DEBUG) {
			System.out.println("reconcile(IRegion partition)");
			System.out.println("File: "
					+ (currentDocument).getFile().toString());
			if(outline != null)
			{
				VdmContentOutlinePage page = (VdmContentOutlinePage) outline.getCurrentPage();
				
			}
		}
		try {

			if (VdmProject.isVdmProject(currentDocument.getProject())) {

				try {
					ISourceParser parser = SourceParserManager
							.getInstance()
							.getSourceParser(
									new VdmProject(currentDocument.getProject()));

					if (parser != null) {
						parser.parse(currentDocument.getFile(), currentDocument
								.get());
					}
				} catch (NotAllowedException e) {
					if (Activator.DEBUG) {
						e.printStackTrace();
					}
				}
			}

		} catch (CoreException e) {
			if (Activator.DEBUG)
				e.printStackTrace();
		}
	}

	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {

	}

	public void setDocument(IDocument document) {
		if (document instanceof VdmDocument)
			currentDocument = (VdmDocument) document;
	}

}
