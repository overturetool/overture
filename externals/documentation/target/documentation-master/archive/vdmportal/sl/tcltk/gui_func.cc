//----------------------------------------------------------------------------
//  gui_func.cc
//
//  Eigentliche Funktionen des Grafical User Interface
//
//   makefile: Makefile                      created:       1996.02.15    
//    bf                                     last modified: 1996.02.20     
//-------------------------------------------------------------------------

#include<tcl.h>
#include<tk.h>
#include<stdio.h>
#include<string.h>

#include "tcl_init.h"
#include "gui_func.h"


int Menu()
{
  int choose;
  int err_code;

  if (MainWindow("DB-EXAMPLE", "db_example")) {
     fprintf(stderr," Could not generate Main Window! ");
     exit(1);
   }

  err_code = Tcl_EvalFile(interp, "menu.tcl");

  if (err_code != TCL_OK) {
    printf("%s", interp->result); 
    exit(1);
  }

 Tcl_LinkVar(interp, "choose", (char *) &choose  , TCL_LINK_INT); 

  char * donep = 0; 
  while ( donep==0 || strcmp(donep, "0")==0) {
    Tk_DoOneEvent(0);
    donep = Tcl_GetVar(interp, "Done", TCL_GLOBAL_ONLY); 
  }


 Tcl_UnlinkVar(interp,"choose");
 return choose;
}


int KeyInput()
{
  keytype key;  /* link variable to tcl */
  int err_code;

  if (MainWindow("DB-EXAMPLE", "db_example")) {
     fprintf(stderr," Could not generate Main Window! ");
     exit(1);
   }

  err_code = Tcl_EvalFile(interp, "getkey.tcl");

  if (err_code != TCL_OK) {
    exit(1);
  }

 Tcl_LinkVar(interp, "key", (char *) &key  , TCL_LINK_INT   ); 
  char * donep = 0; 
  while ( donep==0 || strcmp(donep, "0")==0) {
    Tk_DoOneEvent(0);
    donep = Tcl_GetVar(interp, "Done", TCL_GLOBAL_ONLY); 
  }

 Tcl_UnlinkVar(interp, "key");
 return key;
}



item ItemInput()
{
 
  item tp1;
  keytype key;
  datatype data;

  int err_code;

  if (MainWindow("DB-EXAMPLE", "db_example")) {
     fprintf(stderr," Could not generate Main Window! "); 
     exit(1);
   }

  err_code = Tcl_EvalFile(interp, "getdata.tcl");

  if (err_code != TCL_OK) {
    exit(1);
  }

 Tcl_LinkVar(interp, "key", (char *) &key  , TCL_LINK_INT   ); 
 Tcl_LinkVar(interp, "data", (char *) &data  , TCL_LINK_INT   ); 
  char * donep = 0; 
  while ( donep==0 || strcmp(donep, "0")==0) {
    Tk_DoOneEvent(0);
    donep = Tcl_GetVar(interp, "Done", TCL_GLOBAL_ONLY); 
  }

 Tcl_UnlinkVar(interp, "key");
 Tcl_UnlinkVar(interp,"data");

 tp1.key =key;
 tp1.data = data;
// strcpy(tp1.data, data);

 return tp1;
}


void ItemOutput(item tp)
{
  keytype k;
  datatype d;
  char string[21];
  int err_code;

  if (MainWindow("DB-EXAMPLE", "db_example")) {
     fprintf(stderr," Could not generate Main Window! ");  
     exit(1);
   }
  sprintf(string, "%d", tp.key);
  Tcl_SetVar(interp, "key", string, 0);
  sprintf(string, "%d", tp.data);
  Tcl_SetVar(interp, "data", string, 0); 

  err_code = Tcl_EvalFile(interp, "showdata.tcl");

   if (err_code != TCL_OK) {
    exit(1);
  }

  char * donep = 0; 
  while ( donep==0 || strcmp(donep, "0")==0) {
    Tk_DoOneEvent(0);
    donep = Tcl_GetVar(interp, "Done", TCL_GLOBAL_ONLY); 
  }
 return;
}

void MsgOutput(char * err_func, char *err_msg)
{
 
  int err_code;

  if (MainWindow("DB-EXAMPLE", "db_example")) {
     fprintf(stderr," Could not generate Main Window! ");
     exit(1);
   }

  Tcl_SetVar(interp, "err_func", err_func, 0);
  Tcl_SetVar(interp, "err_msg", err_msg, 0);

  err_code = Tcl_EvalFile(interp, "showmsg.tcl");

   if (err_code != TCL_OK) {
    exit(1);
  }

  char * donep = 0; 
  while ( donep==0 || strcmp(donep, "0")==0) {
    Tk_DoOneEvent(0);
    donep = Tcl_GetVar(interp, "Done", TCL_GLOBAL_ONLY); 
  }
 return;
}

void DefinedOutput(int key, int found)
{
  
  char string[21];
  char flag[2];
  int err_code;
 
  if (MainWindow("DB-EXAMPLE", "db_example")) {
     fprintf(stderr," Could not generate Main Window! "); 
     exit(1);
   }

  sprintf(string, "%d", key);
  Tcl_SetVar(interp, "key", string, 0);
  sprintf(flag, "%d", found);
  Tcl_SetVar(interp, "flag", flag, 0);

  
  err_code = Tcl_EvalFile(interp, "showdef.tcl");

   if (err_code != TCL_OK) {
    exit(1);
  }

  char * donep = 0; 
  while ( donep==0 || strcmp(donep, "0")==0) {
    Tk_DoOneEvent(0);
    donep = Tcl_GetVar(interp, "Done", TCL_GLOBAL_ONLY); 
  }
 return;
}

void ShowInfo()
{ 
  int err_code;

  Init_Tcl();

  if (MainWindow("DB-EXAMPLE", "db_example")) {
     fprintf(stderr," Could not generate Main Window! ");
     exit(1);
   }

   err_code = Tcl_EvalFile(interp, "showinfo.tcl");

   if (err_code != TCL_OK) {
    exit(1);
  }

  char * donep = 0; 
  while ( donep==0 || strcmp(donep, "0")==0) {
    Tk_DoOneEvent(0);
    donep = Tcl_GetVar(interp, "Done", TCL_GLOBAL_ONLY); 
  }
 return;
  
} 
