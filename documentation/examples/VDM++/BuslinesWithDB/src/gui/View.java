package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.io.IOException;
import java.util.*;
import javax.swing.JPanel;

class View extends JPanel implements Observer {

	private static final long serialVersionUID = 1L;
	public static final int GRID_SIZE  = 30;
	public static final int GRID_BORDER  = 2;
	
    Model model;
   
    int resFactor;
    Font font12, font16;
    
    transient Collection<Bus> buses;
    transient Collection<Waypoint> waypoints;
    transient Collection<Road> roads;
    transient Collection<Passenger> passengers;
    
    int inflow; 
    Object lock; 
    
    View(Model model) throws IOException {
    	
    	super();
        this.model = model;
        this.setBackground(Color.WHITE);
        font12 =  new Font("Lucida Sans Typewriter", Font.BOLD, 12);
        font16 = new Font("Lucida Sans Typewriter", Font.BOLD, 16);
        lock = new Object();
        inflow = 0;
        
        buses = model.getBuses();
        waypoints = model.getWaypoints();
        roads = model.getRoads();
        passengers = model.getPassengers();
        
		this.setLayout(null);
        this.setBackground(Color.WHITE);  
        resFactor = (int)(this.getHeight() / (GRID_SIZE));
    }

    transient Graphics2D g2;
    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(4));
        
        //draw road
        double y;
        for (Road r : roads) {
        	
            if(r.isHighspeed())
            	g2.setColor(Color.ORANGE);
            else
            	g2.setColor(Color.black);
        	
        	 for(Line2D rp : r.getRoadParts())
        	 {
        		 g2.drawLine(l(rp.getX1()), l(rp.getY1()), l(rp.getX2()), l(rp.getY2()));
        		 
        		 //center text on line
        		 y = rp.getY2() - rp.getY1() == 0.0 ? rp.getY1() : (rp.getY2() - rp.getY1()) / 2 + rp.getY1();
        		 if(rp.getX2() - rp.getX1() == 0.0 )
        			 g2.drawString(r.name(), l(rp.getX1()) + 5, l(y) );
        		 else
        			 g2.drawString(r.name(), l((rp.getX2() - rp.getX1()) / 2 + rp.getX1()) -7, l(y) - 5);
        	 }	
		} 
        
        //draw waypoints
        for (Waypoint wp : waypoints) {
        	
        	if(wp.IsStop())
            	g2.setColor(Color.orange);
            else
            	g2.setColor(Color.black);
        	
           	g2.drawRoundRect(l(wp.X()) -4 , l(wp.Y()) - 4, 8, 8, 10, 10);	
            g2.drawString(wp.Name(), l(wp.X()) + 5, l(wp.Y()) - 5);
		}
        
        //draw passengers waiting
        //these positions fit
        double h = 0, v = 0;
        
        synchronized (lock) {
	        for (Passenger p : passengers) {
				
	    		if(p.isAnnoyed())
	        		g2.setColor(Color.RED);
	        	else
	        		g2.setColor(Color.GREEN);
	        	
	    		if(p.isOnBus())
	    			g2.fillOval(l(21.4), l(2 + v), 10, 10);
	    		else
	    			g2.fillOval(l(22.5 + h), l(2 + v), 10, 10);
	        	 
	        	 //calc grid
	        	 v += 0.5;
	        	 //start new row a 5 vertical
	        	 if(v % 5 == 0){
	        		 h += 0.5;
	        		 v = 0;
	        		 
	        		 //limit to 5 horizontal rows
	        		 if(h >= 5) break;
	        	 }
			}
        }
        
        //draw buses
        int bX, bY;
        Point p;
        
		g2.setFont(font12);
        for (Bus b : buses) {
        	
        	g2.setColor(Color.RED);
        	p = b.getBusPosition();
        	bX = l(p.getX());
        	bY = l(p.getY());
            g2.fillRect(bX - 7, bY - 7, 15, 15);
            g2.setColor(Color.black);
            g2.drawString("Pass: " + b.passengerCount(), bX +5,bY -10); 
            g2.drawString(Integer.toString(b.lineNr()), bX - 5, bY +5); 
		}
        
        g2.setFont(font16);
        g2.drawString("Inflow: " + inflow, this.getWidth() / 2, this.getHeight() ); 
    }
    
    public void update(Observable obs, Object arg) {
    
		if(arg == null) return;
		
		//window resize
        if(arg == "limits"){
        	resFactor = (int) (this.getWidth() / (GRID_SIZE));
        } else if(arg == "passengerAdded"){
        	synchronized (lock) {
        		passengers = model.getPassengers();
        	}
    		repaint();
        } else if(arg == "passengerAnnoyed"){
            repaint();
        } else if(arg == "passengerGotOnBus"){
            repaint();
        } else if(arg == "inflowChanged"){
        	inflow = model.getInflow();	
    		repaint();
        } else if(arg == "move"){
        	buses = model.getBuses();	
    		repaint();
        } else if (arg == "busStopping"){
        	repaint();
        }  else if (arg == "busAdded"){
        	repaint();
        }  else if (arg == "busPassengerCount"){
        	repaint();
        }  else if (arg == "planUpdated"){
            waypoints = model.getWaypoints();
            roads = model.getRoads();
        	repaint();
        }
    }
	
    private int l(double steps)
    {
    	return (int) (GRID_BORDER * resFactor + resFactor * Math.abs(steps));
    }
}
    