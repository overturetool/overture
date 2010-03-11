package org.overture.ide.ui.editor.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.overture.ide.core.Activator;
import org.overture.ide.core.ast.NotAllowedException;
import org.overture.ide.core.parser.ISourceParser;
import org.overture.ide.core.parser.SourceParserManager;
import org.overture.ide.core.utility.VdmProject;

public class VdmReconcilingStrategy implements IReconcilingStrategy
{

	private VdmDocument currentDocument;

	public void reconcile(IRegion partition)
	{
		if (Activator.DEBUG)
		{
			System.out.println("reconcile(IRegion partition)");
			System.out.println("File: "
					+ (currentDocument).getFile().toString());
		}
		try
		{

			if (VdmProject.isVdmProject(currentDocument.getProject()))
			{

				try
				{
					ISourceParser parser = SourceParserManager.getInstance()
							.getSourceParser(new VdmProject(currentDocument.getProject()));

					if (parser != null)
					{
						parser.parse(currentDocument.getFile(),
								currentDocument.get());
					}
				} catch (NotAllowedException e)
				{
					if (Activator.DEBUG)
					{
						e.printStackTrace();
					}
				}
			}

		} catch (CoreException e)
		{
			if (Activator.DEBUG)
				e.printStackTrace();
		}
	}

	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion)
	{

	}

	public void setDocument(IDocument document)
	{
		if (document instanceof VdmDocument)
			currentDocument = (VdmDocument) document;
	}

}
