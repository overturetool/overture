package com.lausdahl;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import com.lausdahl.ast.creator.parser.AstParserWrapper;
import com.lausdahl.ast.creator.parser.ParserWrapper;

public class AstEditor extends TextEditor
{

	public static class AstSourceViewerConfiguration extends
			DestecsBaseSourceViewerConfiguration
	{

		@Override
		protected ITokenScanner getCodeScaner(ColorProvider colorProvider)
		{
			return new AstCodeScanner(colorProvider);
		}

		@Override
		protected IReconcilingStrategy getReconcilingStrategy()
		{
			return new ContractReconcilingStrategy();
		}

	}

	public static class ContractReconcilingStrategy extends
			BaseReconcilingStrategy
	{
		@SuppressWarnings("rawtypes")
		@Override
		protected ParserWrapper getParser()
		{
			return new AstParserWrapper();
		}
		@Override
		public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion)
		{
			super.reconcile(dirtyRegion, subRegion);
		}
	}

	public static class AstCodeScanner extends BaseCodeScanner
	{
		
		public AstCodeScanner(ColorProvider provider)
		{
			super(provider);
		}

		@Override
		protected String[] getCommentWords()
		{
			return new String[] {};
		}

		@Override
		protected String[] getKeywords()
		{
			return new String[] { "=", ":", "Abstract",
					"Syntax", "Tree", "Tokens",
					"Aspect", "Declaration", "->","Packages" };
		}

		@Override
		protected String[] getTypeWords()
		{
			return new String[] { "->","%" };
		}

	}

	private EditorContentOutlinePage outlinePage;
	private IEditorInput input;

	public AstEditor()
	{
		super();
		setDocumentProvider(new DestecsDocumentProvider());
	}

	@Override
	protected void initializeEditor()
	{
		super.initializeEditor();
		setSourceViewerConfiguration(getContractSourceViewerConfiguration());
	}

	public AstSourceViewerConfiguration getContractSourceViewerConfiguration()
	{
		return new AstSourceViewerConfiguration();
	}

	public Object getAdapter(Class required)
	{
	    if (IContentOutlinePage.class.equals(required))
	    {
	        if (outlinePage == null)
	        {
	            outlinePage = new EditorContentOutlinePage(this);
	            if (getEditorInput() != null)
	                outlinePage.setInput(getEditorInput());
	        }
	        return outlinePage;
	    }
	    return super.getAdapter(required);
	}
	
	public void dispose()
	{
		if (outlinePage != null)
			outlinePage.setInput(null);
		super.dispose();
	}
	
	protected void doSetInput(IEditorInput newInput) throws CoreException
	{
		super.doSetInput(newInput);
		this.input = newInput;

		if (outlinePage != null)
			outlinePage.setInput(input);
		
		
	}
	protected void editorSaved()
	{
		super.editorSaved();


		if (outlinePage != null)
			outlinePage.update();	
	
		//we validate and mark document here
//		validateAndMark();

	}
	

}
