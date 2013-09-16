<%@LANGUAGE="VBSCRIPT"%>
<%
   Option Explicit
   On Error Resume Next

   ' this section is optional - it just denies anonymous access
  ' If Request.ServerVariables("LOGON_USER")="" Then
   '   Response.Status = "401 Access Denied"
   'End If

   ' declare variables
   Dim objFSO, objFolder
   Dim objCollection, objItem

   Dim strPhysicalPath, strTitle, strServerName
   Dim strPath, strTemp
   Dim strName, strFile, strExt, strAttr
   Dim intSizeB, intSizeK, intAttr, dtmDate

   ' declare constants
   Const vbReadOnly = 1
   Const vbHidden = 2
   Const vbSystem = 4
   Const vbVolume = 8
   Const vbDirectory = 16
   Const vbArchive = 32
   Const vbAlias = 64
   Const vbCompressed = 128

   ' don't cache the page
   Response.AddHeader "Pragma", "No-Cache"
   Response.CacheControl = "Private"

   ' get the current folder URL path
   strTemp = Mid(Request.ServerVariables("URL"),2)
   strPath = ""

   Do While Instr(strTemp,"/")
      strPath = strPath & Left(strTemp,Instr(strTemp,"/"))
      strTemp = Mid(strTemp,Instr(strTemp,"/")+1)      
   Loop

   strPath = "/" & strPath

   ' build the page title
   strServerName = UCase(Request.ServerVariables("SERVER_NAME"))
   strTitle = "Contents of the " & strPath & " folder"

   ' create the file system objects
   strPhysicalPath = Server.MapPath(strPath)
   Set objFSO = Server.CreateObject("Scripting.FileSystemObject")
   Set objFolder = objFSO.GetFolder(strPhysicalPath)
%>
<html>
<head>
<title><%=strServerName%> - <%=strTitle%></title>
<meta name="GENERATOR" content="The Mighty Hand of Bob">
<style>
BODY  { BACKGROUND: #cccccc; COLOR: #000000;
        FONT-FAMILY: Arial; FONT-SIZE: 10pt; }
TABLE { BACKGROUND: #000000; COLOR: #ffffff; }
TH    { BACKGROUND: #0000ff; COLOR: #ffffff; }
TD    { BACKGROUND: #ffffff; COLOR: #000000; }
TT    { FONT-FAMILY: Courier; FONT-SIZE: 11pt; }
</style>
</head>
<body>

<h1 align="center"><%=strServerName%><br><%=strTitle%></h1>
<h4 align="center">Please choose a file/folder to view.</h4>

<div align="center"><center>
<table width="100%" border="0" cellspacing="1" cellpadding="2">
<tr>
   <th align="left">Name</th>
   <th align="left">Bytes</th>
   <th align="left">KB</th>
   <th align="left">Attributes</th>
   <th align="left">Ext</th>
   <th align="left">Type</th>
   <th align="left">Date</th>
   <th align="left">Time</th>
</tr>

<%
   ''''''''''''''''''''''''''''''''''''''''
   ' output the folder list
   ''''''''''''''''''''''''''''''''''''''''

   Set objCollection = objFolder.SubFolders

   For Each objItem in objCollection
      strName = objItem.Name
      strAttr = MakeAttr(objItem.Attributes)      
      dtmDate = CDate(objItem.DateLastModified)
%>
<tr>
   <td align="left"><b><a href="<%=strName%>"><%=strName%></a></b></td>
   <td align="right">N/A</td>
   <td align="right">N/A</td>
   <td align="left"><tt><%=strAttr%></tt></td>
   <td align="left"><b><DIR></b></td>
   <td align="left"><b>Directory</b></td>
   <td align="left"><%=FormatDateTime(dtmDate,vbShortDate)%></td>
   <td align="left"><%=FormatDateTime(dtmDate,vbLongTime)%></td>
</tr>
<% Next %>

<%
   ''''''''''''''''''''''''''''''''''''''''
   ' output the file list
   ''''''''''''''''''''''''''''''''''''''''

   Set objCollection = objFolder.Files

   For Each objItem in objCollection
      strName = objItem.Name
      strFile = Server.HTMLEncode(Lcase(strName))

      intSizeB = objItem.Size
      intSizeK = Int((intSizeB/1024) + .5)
      If intSizeK = 0 Then intSizeK = 1

      strAttr = MakeAttr(objItem.Attributes)
      strName = Ucase(objItem.ShortName)
      If Instr(strName,".") Then strExt = Right(strName,Len(strName)-Instr(strName,".")) Else strExt = ""
      dtmDate = CDate(objItem.DateLastModified)
%>
<tr>
   <td align="left"><a href="<%=strFile%>"><%=strFile%></a></td>
   <td align="right"><%=FormatNumber(intSizeB,0)%></td>
   <td align="right"><%=intSizeK%>K</td>
   <td align="left"><tt><%=strAttr%></tt></td>
   <td align="left"><%=strExt%></td>
   <td align="left"><%=objItem.Type%></td>
   <td align="left"><%=FormatDateTime(dtmDate,vbShortDate)%></td>
   <td align="left"><%=FormatDateTime(dtmDate,vbLongTime)%></td>
</tr>
<% Next %>

</table>
</center></div>

</body>
</html>
<%
   Set objFSO = Nothing
   Set objFolder = Nothing

   ' this adds the IIf() function to VBScript
   Function IIf(i,j,k)
      If i Then IIf = j Else IIf = k
   End Function

   ' this function creates a string from the file atttributes
   Function MakeAttr(intAttr)
      MakeAttr = MakeAttr & IIf(intAttr And vbArchive,"A","-")
      MakeAttr = MakeAttr & IIf(intAttr And vbSystem,"S","-")
      MakeAttr = MakeAttr & IIf(intAttr And vbHidden,"H","-")
      MakeAttr = MakeAttr & IIf(intAttr And vbReadOnly,"R","-")
   End Function
%>