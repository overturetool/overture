package org.overture.ide.plugins.rttraceviewer.view;

import java.io.InputStreamReader;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class ValidationConjecturesView extends ViewPart
{

	private ValidationTable theConjectures;

	public ValidationConjecturesView()
	{
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent)
	{
		theConjectures = new ValidationTable(parent);
	}

	@Override
	public void setFocus()
	{
		// TODO Auto-generated method stub

	}

	public void initializeLink(InputStreamReader inputStreamReader,
			IViewCallback theCallback)
	{
		theConjectures.setCallback(theCallback);
		theConjectures.parseValidationFile(inputStreamReader);
		
	}

	public void unlink(IViewCallback theCallback)
	{
		theConjectures.unlink(theCallback);
	}
	
	

}
