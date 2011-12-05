package org.overture.pog.tests;

enum ExpressionsTests implements AbstractTests
{
	explicite_function_declaration_with_unary_head_exp$vdmsl(
			"expressions//explicite_function_declaration_with_unary_head_exp.vdmsl"), explicite_func_decl_with_unary_head_pre_cond$vdmsl(
			"expressions//explicite_func_decl_with_unary_head_pre_cond.vdmsl"), fromexample$vdmsl(
			"expressions//fromexample.vdmsl"), function_with_pre_on_param_and_post_on_result$vdmsl(
			"expressions//function_with_pre_on_param_and_post_on_result.vdmsl"), mapapply$vdmsl(
			"expressions//mapapply.vdmsl"), smallerfromexample$vdmsl(
			"expressions//smallerfromexample.vdmsl"), test$vdmsl(
			"expressions//test.vdmsl"), ;
	private String filePath;

	ExpressionsTests(String fp)
	{
		this.filePath = fp;
	}

	public String getFolder()
	{
		return this.filePath;
	}
};
