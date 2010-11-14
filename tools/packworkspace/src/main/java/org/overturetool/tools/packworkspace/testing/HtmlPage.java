package org.overturetool.tools.packworkspace.testing;

public class HtmlPage
{
	public static String makePage(String body)
	{
		return "<html>"+
				"\n<head>"+
		"\n<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\">"+
		"</head>"+
		"\n<body>\n" + body + "\n</body>\n</html>";
	}
	
	public static String makeLink(String text,String href)
	{
		return "<a href=\""+href+"\">"+text+"</a>";
	}
	
	public static String makeH1(String text)
	{
		return makeH(1, text);//return "\n<h1>" + text+ "</h1>\n";
	}
	
	public static String makeStyleCss()
	{
		return styleCss;
	}
	
	public static String makeBr()
	{
		return "<br/>\n";
	}
	
	
	final static String styleCss="table.mytable"+
		"\n{"+
		"\n	width: 100%;"+
		"\npadding: 0px;"+
		"\nborder: none;"+
		"\nborder: 1px solid #000000;"+
		"\n}"+
		"\ntable.mytable td"+
		"\n{"+
		"\nfont-size: 11px;"+
		"\nborder: none;"+
		"\nbackground-color: #EBEBEB;"+
		"\nvertical-align: middle;"+
		"\npadding: 7px;"+
		"\nfont-weight: bold;"+
		"\nfont-family: 'Courier New' , Courier, monospace;"+
		"\n}"+
		"\n"+
		"\ntable.mytable td.faild"+
		"\n{"+
		"\nbackground-color: #F87676;"+
		"\n}"+
		"\ntable.mytable td.ok"+
		"\n{"+
		"\nbackground-color: #80F479;"+
		"\n}"+
		"\n"+
		"\ntable.mytable th"+
		"\n{"+
		"\nborder-style: none none double none;"+
		"\nborder-width: medium;"+
		"\nborder-color: inherit;"+
		"\nfont-size: 12px;"+
		"\nbackground-color: #EBEBEB;"+
		"\nvertical-align: middle;"+
		"\npadding: 7px;"+
		"\nfont-weight: bold;"+
		"\n}"+
		"\ntable.mytable tr.special td"+
		"\n{"+
		"\nborder-bottom: 1px solid #ff0000;"+
		"\nborder-bottom-color: #0033CC;"+
		"\n}"+
		"\n"+
		"\ntable.mytable a"+
		"\n{"+
		"\ncolor: #000000;"+
		"\n}"+
		"\n"+
		"\nbody"+
		"\n{"+
		"\nfont-family: Arial, Helvetica, sans-serif;"+
		"\n}"+
		"\ntable.mytable tr.total td"+
		"\n{"+
		"\n			border-style: double none none none;"+
		"\n			border-width: medium;"+
		"\n			border-color: inherit;"+
		"\n}"+
		"\ntable.mytable tr.total td.ok"+
		"\n{"+
		"\n			background-color: #80F479;"+
		"\n}"+
		"\ntable.mytable tr.total td.faild"+
		"\n{"+
		"\n			background-color: #F87676;"+
		"\n}";



	public static String makeH(Integer level,String text)
	{
		return "\n<h"+level+">" + text+ "</h"+level+">\n";
	}
}
		
