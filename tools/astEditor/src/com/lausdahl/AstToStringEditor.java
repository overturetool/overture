package com.lausdahl;

import java.util.ArrayList;
import java.util.List;

import org.antlr.runtime.tree.CommonTree;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import com.lausdahl.ast.creator.parser.AstToStringParserWrapper;
import com.lausdahl.ast.creator.parser.ParserWrapper;

public class AstToStringEditor extends TextEditor
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
			return new AstToStringParserWrapper();
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
			return new String[] { "=", ":", "To",
					"String", "Extensions", "->","import" };
		}

		@Override
		protected String[] getTypeWords()
		{
			return new String[] { "->","%" };
		}

		protected void setup(ColorProvider provider)
		{
			IToken type = new Token(new TextAttribute(provider.getColor(new RGB(0, 0, 192)), null, SWT.BOLD));
			IToken keyword = new Token(new TextAttribute(provider.getColor(ColorProvider.KEYWORD), null, SWT.BOLD));
			IToken string = new Token(new TextAttribute(provider.getColor(ColorProvider.STRING)));
			IToken comment = new Token(new TextAttribute(provider.getColor(ColorProvider.SINGLE_LINE_COMMENT)));
			IToken other = new Token(new TextAttribute(provider.getColor(ColorProvider.DEFAULT)));
			IToken java = new Token(new TextAttribute(provider.getColor(ColorProvider.VDMDOC_DEFAULT)));
			IToken graph = new Token(new TextAttribute(provider.getColor(ColorProvider.GRAPH)));
			

			List<IRule> rules = new ArrayList<IRule>();
			// Add rule for single line comments.
//			rules.add(new EndOfLineRule("--", comment));
			rules.add(new EndOfLineRule("//", comment));
			// Multi line comment
			rules.add(new MultiLineRule("/*", "*/", comment));

			// Add rule for strings.
			rules.add(new SingleLineRule("\"", "\"", string, '\\'));
			rules.add(new MultiLineRule("$", "$", java));
			rules.add(new SingleLineRule("(", ")", graph, '\\'));
			rules.add(new SingleLineRule("[", "]", keyword, '\\'));
//			rules.add(new SingleLineRule("{", "}", type, '\\'));
			// rules.add(new SingleLineRule("{->", "}", comment, '\\'));
			// Add generic whitespace rule.
			rules.add(new WhitespaceRule(new WhitespaceDetector()));
			// Add word rule for keywords.
			WordRule wordRule = new WordRule(new ContractWordDetector(), other);

			for (String keywd : getKeywords())
			{
				wordRule.addWord(keywd, keyword);
			}

			for (String typeName : getTypeWords())
			{
				wordRule.addWord(typeName, type);
			}

			for (String word : getCommentWords())
			{
				wordRule.addWord(word, comment);
			}

			rules.add(wordRule);

			IRule[] result = new IRule[rules.size()];
			rules.toArray(result);
			setRules(result);
		}
	}

	private EditorContentOutlinePage outlinePage;
	private IEditorInput input;

	public AstToStringEditor()
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
	            outlinePage = new EditorToStringContentOutlinePage(this);
	            if (getEditorInput() != null)
	                outlinePage.setInput(getEditorInput());
	        }
	        return outlinePage;
	    }
	    return super.getAdapter(required);
	}
	
	public static class EditorToStringContentOutlinePage extends EditorContentOutlinePage
	{

		public EditorToStringContentOutlinePage(ITextEditor editor)
		{
			super(editor);
		}
		
		@Override
		protected void setContentProvider(TreeViewer viewer)
		{
			outlineContentProvider = new ToStringContentOutlineProvider(editor.getDocumentProvider());
		}
		public static class ToStringContentOutlineProvider extends OutlineContentProvider
		{

			public ToStringContentOutlineProvider(IDocumentProvider provider)
			{
				super(provider);
			}
			
			@Override
			protected CommonTree parseRootElement(IDocument document)
			{
				AstToStringParserWrapper parser = new AstToStringParserWrapper();

				if (document != null && document instanceof AstcDocument)
				{
					AstcDocument currentDocument = (AstcDocument) document;
					try
					{
						return (CommonTree) parser.parse(currentDocument.getFile().getLocation().toFile(), currentDocument.get()).getTree();

					} catch (Exception e)
					{
						e.printStackTrace();
					}
				}
				return null;
			}
			
		}
		
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
