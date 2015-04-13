//----------------------------------------------------------------------------
//  my_gui.cc
//
//  Typconversionfunctions
//
//  15.02.1996                            bf
//----------------------------------------------------------------------------
//old version with key and data are integer

#include <string.h>
#include <stdio.h>
#include "metaiv.h"

#include "gui_func.h"

extern "C" {
  Map NameMap();
  Generic GetData ();
  Generic GetKey ();
  void ShowData (Sequence sq1);
  void ShowMsg (Sequence sq1);
  void SqtoString(Sequence sq1, int sq_len, char * string);
  Generic SelectFunc();
  void GUI_Init();
  void ShowDefined(Sequence sq1);


void GUI_Init()
    {
    printf("GUI_Init start!\n"); 
      ShowInfo();
    printf("GUI_Init end!\n"); 
  }

Generic SelectFunc()
  {
    int auswahl;
    printf("SelectFunc start!\n"); 
    auswahl = Menu();
    printf("SelectFunc end!\n"); 
    
    return ((Int) auswahl);
   }

Generic GetData()
  {  
   item item;
   Tuple Tp1(2);
 
   item = ItemInput();
  
   Tp1.SetField(1, Int(item.key));
   Tp1.SetField(2, Int(item.data));

   return (Tp1);     
  }

Generic GetKey()
  {
    keytype key;
    key = KeyInput();
    return (Int) key;
  }
  
void ShowData(Sequence sq1)
{
     item tuple;

     tuple.key = (Int)sq1[1];
     tuple.data = (Int) sq1[2];

     ItemOutput(tuple);
     return;
}

void ShowMsg(Sequence sq1)
{
  int sel_func;
  sel_func = (Int)sq1[1];

  switch (sel_func) {
  case 1 : MsgOutput("Insert", "Key is already defined in database!");
           break;

  case 2: MsgOutput("Defined", "Unknown error in function!");
          break;

  case 3: MsgOutput("LookUp", "Key is not defined in database!");
          break;

  case 4: MsgOutput("Remove", "Key is not defined in database!");
          break;
	 }
 return;
}

void ShowDefined(Sequence sq1)
  {
    keytype key;
    Bool bval;
    int found;


    key = (Int)sq1[1];
    bval = (Bool) sq1[2];
    found = bval.GetValue();    

    DefinedOutput(key, found);  
}

}
