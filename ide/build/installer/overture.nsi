 
;--------------------------------
;Include Modern UI

  !include "MUI2.nsh"
;--------------------------------
!include zipdll.nsh
!include LogicLib.nsh
!include x64.nsh

!include 'FileFunc.nsh'
!insertmacro Locate
 

!define PRODUCT_VERSION "0.0.0"
!define PRODUCT_REG_KEY "Overture"
!define PRODUCT_NAME "Overture"

!define OVERTUREIDE "OvertureIde-"
;!define OVERTUREFOLDER "${OVERTUREIDE}${PRODUCT_VERSION}I"
;!define DESTECSZIP "${DESTECSFOLDER}-win32.win32.x86.zip"
!define OVERTUREZIP "overture.zip"

!include "WordFunc.nsh"
  !insertmacro VersionCompare

Var UNINSTALL_OLD_VERSION


; The name of the installer
Name "Overture"

; The file to write
OutFile "OvertureInstaller-${PRODUCT_VERSION}.exe"

; The default installation directory
InstallDir $PROGRAMFILES\Overture

; Registry key to check for directory (so if you install again, it will 
; overwrite the old one automatically)
InstallDirRegKey HKLM "Software\Overture" "Install_Dir"

; Request application privileges for Windows Vista
RequestExecutionLevel admin
;--------------------------------
;Interface Configuration

  !define MUI_HEADERIMAGE 
  !define MUI_HEADERIMAGE_BITMAP "overture.bmp" ; optional
  !define MUI_ABORTWARNING
  !define MUI_LICENSEPAGE_TEXT_BOTTOM "This instalation includes Overture Ide ${PRODUCT_VERSION}."
;-------------------------------- 

;!define MUI_WELCOMEPAGE_TITLE "dsasda"
;!define MUI_WELCOMEPAGE_TITLE_3LINES
;!define MUI_WELCOMEPAGE_TEXT "dsadas sdadas dassd ada dsa dsadsa"


;--------------------------------

; Pages
;Page components
;Page directory
;Page instfiles
  ;!insertmacro MUI_PAGE_WELCOME   
  !insertmacro MUI_PAGE_LICENSE "license.txt"
  !insertmacro MUI_PAGE_COMPONENTS
   ; Var StartMenuFolder
;!insertmacro MUI_PAGE_STARTMENU "Application" $StartMenuFolder
  !insertmacro MUI_PAGE_DIRECTORY
  !insertmacro MUI_PAGE_INSTFILES
  

  
  !insertmacro MUI_UNPAGE_CONFIRM
  !insertmacro MUI_UNPAGE_INSTFILES

;--------------------------------

  !insertmacro MUI_LANGUAGE "English"


; The stuff to install
Section "Overture (required)" ;No components page, name is not important

  SectionIn RO

  ; Set output path to the installation directory.
  SetOutPath $INSTDIR

  ;
  StrCmp $UNINSTALL_OLD_VERSION "" core.files
  ExecWait '$UNINSTALL_OLD_VERSION'

  core.files:
  WriteRegStr HKLM "Software\${PRODUCT_REG_KEY}" "" $INSTDIR
  WriteRegStr HKLM "Software\${PRODUCT_REG_KEY}" "Version" "${PRODUCT_VERSION}"
  
  ; DESTECS Tool instalation file
  File "data\${OVERTUREZIP}"
  ; Calling the function that installs DESTECS  
  Call OvertureInstall
  
  
  ; Registry creation
  ; Write the installation path into the registry
  WriteRegStr HKLM SOFTWARE\${PRODUCT_REG_KEY} "Install_Dir" "$INSTDIR"
  ; Write the uninstall keys for Windows
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT_REG_KEY}" "DisplayName" "Overture"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT_REG_KEY}" "UninstallString" '"$INSTDIR\uninstall.exe"'
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT_REG_KEY}" "NoModify" 1
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT_REG_KEY}" "NoRepair" 1
  WriteUninstaller "uninstall.exe"
  
   AccessControl::GrantOnFile "$INSTDIR" "(BU)" "GenericRead + GenericWrite"
  
SectionEnd ; end the section

; Optional section (can be disabled by the user)
Section "Start Menu Shortcuts (Optional)"
  CreateDirectory "$SMPROGRAMS\${PRODUCT_REG_KEY}"
  CreateShortCut "$SMPROGRAMS\${PRODUCT_REG_KEY}\Uninstall.lnk" "$INSTDIR\uninstall.exe" "" "$INSTDIR\uninstall.exe" 0
  CreateShortCut "$SMPROGRAMS\${PRODUCT_REG_KEY}\Overture.lnk" "$INSTDIR\overture.exe" "" "$INSTDIR\overture.exe" 0
SectionEnd

; Uninstaller
Section "Uninstall"

  ;deleting the uninstall exe first is apparently normal
  Delete $INSTDIR\uninstall.exe 
  
  
  ; Remove registry keys
  DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT_REG_KEY}"
  DeleteRegKey HKLM SOFTWARE\${PRODUCT_REG_KEY}
  ; Remove files and uninstaller
  ;Delete $INSTDIR\example2.nsi
  DetailPrint "Deleting $INSTDIR\configuration"
  RMDir /r "$INSTDIR\configuration"
  DetailPrint "Deleting $INSTDIR\features"
  RMDir /r "$INSTDIR\features"
  DetailPrint "Deleting $INSTDIR\p2"
  RMDir /r "$INSTDIR\p2"
  DetailPrint "Deleting $INSTDIR\plugins"
  RMDir /r "$INSTDIR\plugins"
  DetailPrint "Deleting $INSTDIR\readme"
  RMDir /r "$INSTDIR\readme"
  DetailPrint "Deleting $INSTDIR\.eclipseproduct"
  Delete "$INSTDIR\.eclipseproduct"
  DetailPrint "Deleting $INSTDIR\artifacts.xml"
  Delete "$INSTDIR\artifacts.xml"
  DetailPrint "Deleting $INSTDIR\overture.exe"
  Delete "$INSTDIR\Overture.exe"
  DetailPrint "Deleting $INSTDIR\overture.ini"
  Delete "$INSTDIR\Overture.ini"
  DetailPrint "Deleting $INSTDIR\epl-v10.html"
  Delete "$INSTDIR\epl-v10.html"
  DetailPrint "Deleting $INSTDIR\notice.html"
  Delete "$INSTDIR\notice.html"
 
  ; Remove shortcuts, if any
  Delete "$SMPROGRAMS\${PRODUCT_REG_KEY}\*.*"
  ; Remove directories used
  RMDir "$SMPROGRAMS\${PRODUCT_REG_KEY}"
  
  
  RMDir "$INSTDIR"
SectionEnd


Function .onInit
  ;Check earlier installation
  ClearErrors
  ReadRegStr $0 HKLM "Software\${PRODUCT_REG_KEY}" "Version"
  IfErrors init.uninst ; older versions might not have "Version" string set
  ${VersionCompare} $0 ${PRODUCT_VERSION} $1
  IntCmp $1 2 init.uninst
    MessageBox MB_YESNO|MB_ICONQUESTION "${PRODUCT_NAME} version $0 seems to be already installed on your system.$\nWould you like to proceed with the installation of version ${PRODUCT_VERSION}?" \
        IDYES init.uninst
    Quit

init.uninst:
  ClearErrors
  ReadRegStr $0 HKLM "Software\${PRODUCT_REG_KEY}" ""
  IfErrors init.done
  StrCpy $UNINSTALL_OLD_VERSION '"$0\uninstall.exe" /S _?=$0'

init.done:
FunctionEnd

; Install Overture Tool
Function OvertureInstall
  ; Print to detail log 
  DetailPrint "Installing Overture"
  ; Unzip the file
  ZipDLL::extractall "${OVERTUREZIP}" $INSTDIR 
  ; Delete the zip
  Delete "${OVERTUREZIP}"
  ;Moving files from DESTECS folder to root of $INSTDIR
  ;!insertmacro MoveFolder "$INSTDIR\${DESTECSFOLDER}\" $INSTDIR "*.*"
FunctionEnd


