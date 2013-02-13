package com.lausdahl;

import java.io.IOException;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;

import com.lausdahl.ast.creator.parser.IError;
import com.lausdahl.ast.creator.parser.ParserWrapper;

public abstract class BaseReconcilingStrategy implements IReconcilingStrategy
{
	private AstcDocument currentDocument;

	public void reconcile(IRegion partition)
	{
		if (currentDocument != null)
		{
			@SuppressWarnings("rawtypes")
			ParserWrapper parser = getParser();
			FileUtility.deleteMarker(currentDocument.getFile(), IMarker.PROBLEM, IAstEditorConstants.PLUGIN_ID);
			try
			{
				parser.parse(currentDocument.getFile().getLocation().toFile(), currentDocument.get());

				if (parser.hasErrors())
				{
					for (Object err : parser.getErrors())
					{
						com.lausdahl.ast.creator.parser.IError e = (IError) err;						
						FileUtility.addMarker(currentDocument.getFile(), e.getMessage(), e.getLine(), e.getCharPositionInLine(), IMarker.SEVERITY_ERROR, currentDocument.get());
					}
				}

			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("rawtypes")
	protected abstract ParserWrapper getParser();

	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion)
	{

	}

	public void setDocument(IDocument document)
	{
		if (document instanceof AstcDocument)
		{
			currentDocument = (AstcDocument) document;
		}
	}

}
