package com.lausdahl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import org.antlr.runtime.tree.CommonTree;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;

import com.lausdahl.ast.creator.parser.AstParserWrapper;

public class OutlineContentProvider implements ITreeContentProvider
{
	private CommonTree root = null;
	private IEditorInput input;
	private IDocumentProvider documentProvider;

	public OutlineContentProvider(IDocumentProvider provider)
	{
		super();
		this.documentProvider = provider;
	}

	public void dispose()
	{
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
		if (oldInput != null)
		{
			IDocument document = documentProvider.getDocument(oldInput);
			if (document != null)
			{

			}
		}
		input = (IEditorInput) newInput;
		if (newInput != null)
		{
			IDocument document = documentProvider.getDocument(newInput);
			if (document != null)
			{
				// document.addPositionCategory(TAG_POSITIONS);
				//
				// document.addPositionUpdater(positionUpdater);

				CommonTree rootElement = parseRootElement(document);

				if (rootElement != null)
				{
					root = rootElement;
				}
			}
		}
	}

	protected CommonTree parseRootElement(IDocument document)
	{
		AstParserWrapper parser = new AstParserWrapper();

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

	public Object[] getElements(Object inputElement)
	{
		if (root == null)

			return new Object[0];

		if (root instanceof CommonTree)
		{
			CommonTree top = (CommonTree) root;
			return removeNulls(((CommonTree) root).getChildren()).toArray();
		}
		return new Object[0];
	}

	public Object[] getChildren(Object parentElement)
	{
		if (parentElement instanceof CommonTree)
		{
			if (parentElement instanceof CommonTree
					&& ((CommonTree) parentElement).getText() != null
					&& (((CommonTree) parentElement).getText().equals("P") || ((CommonTree) parentElement).getText().equals("%")))
			{
				return sort( filter(((CommonTree) parentElement).getChildren().subList(1, ((CommonTree) parentElement).getChildren().size()))).toArray();
			}
			return sort(filter(((CommonTree) parentElement).getChildren())).toArray();
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	private List filter(List list)
	{
		List newList = new Vector();
		for (Object o : list)
		{
			if (o instanceof CommonTree)
			{
				if (!((CommonTree) o).getText().equals("->"))
				{
					newList.add(o);
				}
			}
		}
		return newList;
	}

	public Object getParent(Object element)
	{
		return null;
	}

	public boolean hasChildren(Object element)
	{
		if (element instanceof CommonTree)
		{
			CommonTree top = (CommonTree) element;
			if (top.getParent() != null
					&& top.getParent().getText() != null
					&& (top.getParent().getText().equals("Tokens") || top.getParent().getText().equals("To String Extensions")))
			{
				return false;
			}
			if (top.getParent() != null
					&& top.getParent().getText() != null
					&& (top.getParent().getText().equals("P") || top.getParent().getText().equals("%")))
			{
				return false;
			}
			return ((CommonTree) element).getChildCount() > 0;
		}
		return false;
	}

	protected List removeNulls(List list)
	{
		for (int i = 0; i < list.size(); i++)
		{
			if (list.get(i) == null
					|| (list.get(i) instanceof CommonTree && ((CommonTree) list.get(i)).getText() == null))
			{
				list.remove(i);
			}
		}
		return list;
	}

	protected List sort(List<CommonTree> list)
	{

		final OutlineLabelProvider pl = new OutlineLabelProvider();
		Collections.sort(list, new Comparator<CommonTree>()
		{

			
			public int compare(CommonTree o1, CommonTree o2)
			{
				String s1 = pl.getText(o1);
				String s2 = pl.getText(o2);
				return s1.compareTo(s2);
			}
		});
		return list;
	}
}
