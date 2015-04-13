


import org.overture.interpreter.values.IntegerValue;

import gui.Graphics;

//import org.overturetool.vdmj.runtime.ValueException;
//import org.overturetool.vdmj.values.IntegerValue;


public class Main {

    /**
     * @param args the command line arguments
     * @throws Exception 
     * @throws ValueException 
     */
    public static void main(String[] args) throws Exception {
    	Graphics g = new Graphics();
    	g.initialise( new IntegerValue(11),  new IntegerValue(10));
    	
    	IntegerValue x = new IntegerValue(0);
    	IntegerValue y = new IntegerValue(0);
    	g.newLivingCell(x,y);
    	x = new IntegerValue(-1);
    	y = new IntegerValue(0);
    	g.newLivingCell(x,y);
    	x = new IntegerValue(0);
    	y = new IntegerValue(-1);
    	g.newLivingCell(x,y);
    	x = new IntegerValue(-1);
    	y = new IntegerValue(-1);
    	g.newLivingCell(x,y);
    	x = new IntegerValue(1);
    	y = new IntegerValue(0);
    	g.newLivingCell(x,y);
    	Thread.sleep(200);
    	g.newRound();

    	x = new IntegerValue(0);
    	y = new IntegerValue(0);
    	g.newLivingCell(x,y);
    	x = new IntegerValue(-1);
    	y = new IntegerValue(0);
    	g.newLivingCell(x,y);
    	x = new IntegerValue(0);
    	y = new IntegerValue(-1);
    	g.newLivingCell(x,y);
    	x = new IntegerValue(-1);
    	y = new IntegerValue(-1);
    	g.newLivingCell(x,y);
    	x = new IntegerValue(1);
    	y = new IntegerValue(0);
    	g.newLivingCell(x,y);
    	Thread.sleep(200);
    	g.newRound();
    	
    	x = new IntegerValue(0);
    	y = new IntegerValue(0);
    	g.newLivingCell(x,y);
    	x = new IntegerValue(-1);
    	y = new IntegerValue(0);
    	g.newLivingCell(x,y);
    	x = new IntegerValue(0);
    	y = new IntegerValue(-1);
    	g.newLivingCell(x,y);
    	x = new IntegerValue(-1);
    	y = new IntegerValue(-1);
    	g.newLivingCell(x,y);
    	x = new IntegerValue(1);
    	y = new IntegerValue(0);
    	g.newLivingCell(x,y);
    	Thread.sleep(200);
    	g.newRound();
    	
    	x = new IntegerValue(0);
    	y = new IntegerValue(0);
    	g.newLivingCell(x,y);
    	x = new IntegerValue(-1);
    	y = new IntegerValue(0);
    	g.newLivingCell(x,y);
    	x = new IntegerValue(0);
    	y = new IntegerValue(-1);
    	g.newLivingCell(x,y);
    	x = new IntegerValue(-1);
    	y = new IntegerValue(-1);
    	g.newLivingCell(x,y);
    	x = new IntegerValue(1);
    	y = new IntegerValue(0);
    	g.newLivingCell(x,y);
    	Thread.sleep(200);
    	g.newRound();
    	
    	x = new IntegerValue(0);
    	y = new IntegerValue(0);
    	g.newLivingCell(x,y);
    	x = new IntegerValue(-1);
    	y = new IntegerValue(0);
    	g.newLivingCell(x,y);
    	x = new IntegerValue(1);
    	y = new IntegerValue(-1);
    	g.newLivingCell(x,y);
    	x = new IntegerValue(-1);
    	y = new IntegerValue(-1);
    	g.newLivingCell(x,y);
    	x = new IntegerValue(1);
    	y = new IntegerValue(0);
    	g.newLivingCell(x,y);
    	Thread.sleep(200);
    	
    	x = new IntegerValue(0);
    	y = new IntegerValue(0);
    	g.newLivingCell(x,y);
    	x = new IntegerValue(-1);
    	y = new IntegerValue(0);
    	g.newLivingCell(x,y);
    	x = new IntegerValue(0);
    	y = new IntegerValue(-1);
    	g.newLivingCell(x,y);
    	x = new IntegerValue(1);
    	y = new IntegerValue(-1);
    	g.newLivingCell(x,y);
    	x = new IntegerValue(1);
    	y = new IntegerValue(1);
    	g.newLivingCell(x,y);
    	Thread.sleep(200);
    	g.newRound();
    	
    	x = new IntegerValue(0);
    	y = new IntegerValue(0);
    	g.newLivingCell(x,y);
    	x = new IntegerValue(-1);
    	y = new IntegerValue(0);
    	g.newLivingCell(x,y);
    	x = new IntegerValue(0);
    	y = new IntegerValue(-1);
    	g.newLivingCell(x,y);
    	x = new IntegerValue(-1);
    	y = new IntegerValue(1);
    	g.newLivingCell(x,y);
    	x = new IntegerValue(1);
    	y = new IntegerValue(0);
    	g.newLivingCell(x,y);
    	Thread.sleep(200);
    	g.newRound();
    	
    	x = new IntegerValue(0);
    	y = new IntegerValue(0);
    	g.newLivingCell(x,y);
    	x = new IntegerValue(-1);
    	y = new IntegerValue(0);
    	g.newLivingCell(x,y);
    	x = new IntegerValue(0);
    	y = new IntegerValue(-1);
    	g.newLivingCell(x,y);
    	x = new IntegerValue(-1);
    	y = new IntegerValue(-1);
    	g.newLivingCell(x,y);
    	x = new IntegerValue(1);
    	y = new IntegerValue(0);
    	g.newLivingCell(x,y);
    	Thread.sleep(200);
    	g.newRound();
    	
    	x = new IntegerValue(0);
    	y = new IntegerValue(0);
    	g.newLivingCell(x,y);
    	x = new IntegerValue(-1);
    	y = new IntegerValue(0);
    	g.newLivingCell(x,y);
    	x = new IntegerValue(0);
    	y = new IntegerValue(-1);
    	g.newLivingCell(x,y);
    	x = new IntegerValue(1);
    	y = new IntegerValue(-1);
    	g.newLivingCell(x,y);
    	x = new IntegerValue(1);
    	y = new IntegerValue(0);
    	g.newLivingCell(x,y);
    	Thread.sleep(200);
    	g.newRound();
    	
    	x = new IntegerValue(0);
    	y = new IntegerValue(0);
    	g.newLivingCell(x,y);
    	x = new IntegerValue(-1);
    	y = new IntegerValue(0);
    	g.newLivingCell(x,y);
    	x = new IntegerValue(0);
    	y = new IntegerValue(-1);
    	g.newLivingCell(x,y);
    	x = new IntegerValue(1);
    	y = new IntegerValue(1);
    	g.newLivingCell(x,y);
    	x = new IntegerValue(1);
    	y = new IntegerValue(0);
    	g.newLivingCell(x,y);
    	Thread.sleep(200);
    	g.newRound();
    	
    	x = new IntegerValue(0);
    	y = new IntegerValue(0);
    	g.newLivingCell(x,y);
    	x = new IntegerValue(-1);
    	y = new IntegerValue(0);
    	g.newLivingCell(x,y);
    	x = new IntegerValue(0);
    	y = new IntegerValue(-1);
    	g.newLivingCell(x,y);
    	x = new IntegerValue(-1);
    	y = new IntegerValue(-1);
    	g.newLivingCell(x,y);
    	x = new IntegerValue(1);
    	y = new IntegerValue(0);
    	g.newLivingCell(x,y);
    	Thread.sleep(200);
    	g.newRound();
    	
    	

    	Thread.sleep(5000);
    	
    
    	x = new IntegerValue(5);
    	y = new IntegerValue(0);
    	g.newLivingCell(x,y);
    	Thread.sleep(500);
    	x = new IntegerValue(7);
    	y = new IntegerValue(0);
    	g.newLivingCell(x,y);
    	   	
    	g.newRound();
    	x = new IntegerValue(0);
    	y = new IntegerValue(0);
    	g.newLivingCell(x,y);
    	Thread.sleep(500);
    	x = new IntegerValue(0);
    	y = new IntegerValue(-1);
    	g.newLivingCell(x,y);
    	Thread.sleep(500);
    	x = new IntegerValue(0);
    	y = new IntegerValue(-2);
    	g.newLivingCell(x,y);
    	Thread.sleep(500);
    	x = new IntegerValue(0);
    	y = new IntegerValue(-3);
    	g.newLivingCell(x,y);
    	Thread.sleep(500);
    	x = new IntegerValue(0);
    	y = new IntegerValue(-4);
    	g.newLivingCell(x,y);
    	Thread.sleep(500);
    	x = new IntegerValue(0);
    	y = new IntegerValue(-5);
    	g.newLivingCell(x,y);
    	Thread.sleep(500);
    	x = new IntegerValue(0);
    	y = new IntegerValue(-6);
    	g.newLivingCell(x,y);
    	
    	g.newRound();
    	x = new IntegerValue(0);
    	y = new IntegerValue(0);
    	g.newLivingCell(x,y);
    	Thread.sleep(500);
    	x = new IntegerValue(0);
    	y = new IntegerValue(1);
    	g.newLivingCell(x,y);
    	Thread.sleep(500);
    	x = new IntegerValue(0);
    	y = new IntegerValue(2);
    	g.newLivingCell(x,y);
    	Thread.sleep(500);
    	x = new IntegerValue(0);
    	y = new IntegerValue(3);
    	g.newLivingCell(x,y);
    	Thread.sleep(500);
    	x = new IntegerValue(0);
    	y = new IntegerValue(4);
    	g.newLivingCell(x,y);
    	Thread.sleep(500);
    	x = new IntegerValue(0);
    	y = new IntegerValue(5);
    	g.newLivingCell(x,y);
    	Thread.sleep(500);
    	x = new IntegerValue(0);
    	y = new IntegerValue(6);
    	g.newLivingCell(x,y);
    }
}
