package com.lausdahl;

import org.antlr.runtime.tree.CommonTree;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import com.lausdahl.ast.creator.CreateOnParse;

public class OutlineLabelProvider implements ILabelProvider
{

	public void addListener(ILabelProviderListener listener)
	{
		// TODO Auto-generated method stub

	}

	public void dispose()
	{
		// TODO Auto-generated method stub

	}

	public boolean isLabelProperty(Object element, String property)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public void removeListener(ILabelProviderListener listener)
	{
		// TODO Auto-generated method stub

	}

	public Image getImage(Object element)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getText(Object element)
	{
		if (element instanceof CommonTree)
		{
			CommonTree top = (CommonTree) element;
			if (top.getParent() != null && top.getParent().getText() != null
					&& top.getParent().getText().equals("Tokens"))
			{

				CommonTree ch = (CommonTree) top;
				String name = ch.getText(); 
				String tmp="";
				for (int i = 0; i < ch.getChildCount(); i++)
				{
					tmp+= ch.getChild(i).getText();
				}
				return name+(tmp.contains("java:")?": external":"");
			}if(top.getText()==null)
			{
				return "null";
			}
			if (top.getText().equals("P"))
			{
				if (top.getChild(0).getText().equals("#"))
				{
					return "#" + top.getChild(0).getChild(0).getText();
				} else
				{
					return top.getChild(0).getText();
				}
			}
			if (top.getText().endsWith("ALTERNATIVE_SUB_ROOT"))
			{
				return "#" + top.getChild(0).getText();
			}
			if (top.getText().equals("%"))
			{
				String tmp = "%";
				if (top.getChildCount() > 0 && top.getChild(0).getChildCount() > 0)
				{
					CommonTree n = (CommonTree) top.getChild(0).getChild(0);
					tmp += CreateOnParse.unfoldName(n);

				}

				return tmp;
			}
		}
		return element.toString();
	}

}
