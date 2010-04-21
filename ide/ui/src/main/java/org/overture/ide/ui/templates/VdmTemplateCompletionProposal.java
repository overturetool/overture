package org.overture.ide.ui.templates;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateProposal;
import org.eclipse.swt.graphics.Image;

public class VdmTemplateCompletionProposal extends TemplateProposal {

	private final Template fTemplate;
	private final TemplateContext fContext;
	private final Image fImage;
	private final IRegion fRegion;
	private int fRelevance;
	private String fDisplayString;
	
	public VdmTemplateCompletionProposal(Template template,
			TemplateContext context, IRegion region, Image image, int relevance) {
		super(template, context, region, image, relevance);
		fTemplate=template;
		 fContext = context;
		 fImage=image;
		 fRegion=region;
		 fRelevance=relevance;
		 fDisplayString=null;
	}

//	@Override
//
//	public String getAdditionalProposalInfo() {
//	 return StringUtils.convertToHTMLContent(fTemplate.getPattern());
//
//	 }

}
