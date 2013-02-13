package com.lausdahl;

import org.antlr.runtime.tree.CommonTree;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

public class EditorContentOutlinePage extends ContentOutlinePage
{
	protected ITextEditor editor;
	private IEditorInput input;
	protected OutlineContentProvider outlineContentProvider;
	private OutlineLabelProvider outlineLabelProvider;

	public EditorContentOutlinePage(ITextEditor editor)
	{
		super();
		this.editor = editor;
	}

	public void createControl(Composite parent)
	{
		super.createControl(parent);
		TreeViewer viewer = getTreeViewer();
//		outlineContentProvider = new OutlineContentProvider(editor.getDocumentProvider());
//		viewer.setContentProvider(outlineContentProvider);
		setContentProvider(viewer);
		viewer.setContentProvider(outlineContentProvider);
		outlineLabelProvider = new OutlineLabelProvider();
		viewer.setLabelProvider(outlineLabelProvider);
		viewer.addSelectionChangedListener(this);
		// control is created after input is set
		if (input != null)
			viewer.setInput(input);
	}
	
	protected void setContentProvider(TreeViewer viewer)
	{
		outlineContentProvider = new OutlineContentProvider(editor.getDocumentProvider());
	}

	/**
	 * Sets the input of the outline page
	 */
	public void setInput(Object input)
	{
		this.input = (IEditorInput) input;
		update();
	}

	/*
	 * Change in selection
	 */
	public void selectionChanged(SelectionChangedEvent event)
	{
		super.selectionChanged(event);

		// find out which item in tree viewer we have selected, and set highlight range accordingly

		ISelection selection = event.getSelection();
		if (selection.isEmpty())
			editor.resetHighlightRange();
		else
		{
			Object element = ((IStructuredSelection) selection).getFirstElement();
			if (element instanceof CommonTree && converter != null)
			{
				CommonTree tree = (CommonTree) element;
				// int start = element.getPosition().getOffset();
				//
				// int length = element.getPosition().getLength();
				int start = 0;
				if (tree.token.getLine() == 0
						&& tree.token.getCharPositionInLine() == -1
						&& tree.getChildCount() > 0)
				{
					int charPos = tree.getChild(0).getCharPositionInLine();
					// if(charPos>0)
					{
						charPos++;
					}
					start = converter.convert(tree.getChild(0).getLine() + 1, charPos);
				} else
				{
					int charPos = tree.getCharPositionInLine();
					// if(charPos>0)
					{
						charPos++;
					}
					start = converter.convert(tree.getLine() + 1, charPos);
				}
				int length = ((CommonTree) element).getText().length();
				try
				{
					editor.setHighlightRange(start, length, true);

				} catch (IllegalArgumentException x)
				{
					editor.resetHighlightRange();
				}
			}

		}
	}

	SourceLocationConverter converter = null;

	/**
	 * The editor is saved, so we should refresh representation
	 * 
	 * @param tableNamePositions
	 */
	public void update()
	{
		try
		{
			IDocument document = editor.getDocumentProvider().getDocument(input);
			if (document != null)
			{
				converter = new SourceLocationConverter(document.get().toCharArray());
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		// set the input so that the outlines parse can be called
		// update the tree viewer state
		TreeViewer viewer = getTreeViewer();
		if (viewer != null)
		{
			Control control = viewer.getControl();
			if (control != null && !control.isDisposed())
			{
				control.setRedraw(false);
				viewer.setInput(input);
//				viewer.expandAll();
				viewer.expandToLevel(2);
				control.setRedraw(true);
			}
		}
	}
}
