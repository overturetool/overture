package gui;



import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

import javax.swing.JPanel;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ComponentEvent;


class View extends JPanel implements Observer {

	private static final long serialVersionUID = 1L;

    Model model;

    private List<Rectangle> cells;
    private List<Object> liveCells;
    
    private int sideCount;
    int sideSplit;
    int cellCount;
    int zerozero;

    int simulationRoundCount;
    
    int cellWidth;
    int cellHeight;

    int xOffset;
    int yOffset;
    
    View(Model model, int cellSideCount) throws IOException {
        this.model = model;
        this.setLayout(null);
        this.setBackground(Color.WHITE);
        
        sideCount = cellSideCount;
        cellCount = sideCount * sideCount;
        sideSplit = sideCount / 2;
        zerozero = (sideCount % 2 == 0 ? -sideSplit  + (cellCount /2) : (cellCount /2) ); 
        
        cells = new ArrayList<>(cellCount);
        liveCells = Collections.synchronizedList(new ArrayList<>(cellCount));
        
        simulationRoundCount = 0;    
        
        //resize event
  		this.addComponentListener(new java.awt.event.ComponentAdapter() {
  			@Override
  			public void componentResized(ComponentEvent e) {
  				
  		         int width = getWidth();
  		         int height = getHeight();

  		         cellWidth = width / sideCount;
  		         cellHeight = height / sideCount;

  		         xOffset = (width - (sideCount * cellWidth)) / 2;
  		         yOffset = (height - (sideCount * cellHeight)) / 2;
  		         
  		         rebuildCells();
  		         
  		         invalidate();
  			}
  		});
    }

    
    transient Graphics2D g2;
    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent( g );

        g2 = (Graphics2D) g.create();

        
    	synchronized(liveCells){
        	for (Object activeCell : liveCells) {
                Rectangle cell = cells.get((int) activeCell);
                g2.setColor(Color.BLACK);
                g2.fill(cell);
        	}
    
	        g2.setColor(Color.GRAY);
	        for (Rectangle cell : cells) {
	            g2.draw(cell);
	        }
	        
        }	
 
        Font font = new Font("Arial", Font.PLAIN, 18);
        g2.setFont(font);
        g2.setColor(Color.RED);
        g2.drawString("Round: " + simulationRoundCount , 8,20);
        
        g2.dispose();
    }   
    
    public void update(Observable obs, Object arg) {
    
    	if(arg == null)
    		return;
    	
        if(arg == "newRound")
        {
        	simulationRoundCount++;
        	liveCells.clear();
        	List<Point> list = model.getLivingCells();
        	
        	for (Point cord : list) {
        		addNewLiveCell(cord.x,cord.y);
			}
        }

        repaint();
    }
    
    private void addNewLiveCell(int x, int y) 
    {
    	int index = findIndexFromCoord(x,y);
    	
    	if(index > 0) 
    		liveCells.add(index);
    }
    
    private int findIndexFromCoord(int x, int y) 
    { 
    	if((x < sideSplit && x >= -sideSplit) && (y < sideSplit && y >= -sideSplit))
    	{
    	
	    	int index = zerozero +  (-y * sideCount) +x;
	
	    	if(0 <= index && index < cellCount)
	    		return index;
    	}
    	
    	return -1;
    }
    
	private void rebuildCells() {
		cells.clear();
        for (int row = 0; row < sideCount; row++) {
            for (int col = 0; col < sideCount; col++) {
                Rectangle cell = new Rectangle(
                        xOffset + (col * cellWidth),
                        yOffset + (row * cellHeight),
                        cellWidth,
                        cellHeight);
                cells.add(cell);
            }
        }
	}
}

    