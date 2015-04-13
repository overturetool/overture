/**
 * Copyright (C) 2011 Aarhus University
 *
 * This software is provided under the MIT-license:
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 * 
 * 
 * @author Rasmus W. Lauritsen (rala@iha.dk, rwl@post.au.dk, rwl@cs.au.dk)
 * @date 2011-12-28
 * 
 */
package dk.au.eng;


import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Panel;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.overture.interpreter.values.IntegerValue;
import org.overture.interpreter.values.NumericValue;
import org.overture.interpreter.values.SeqValue;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.VoidValue;


public class Radar extends Panel {

	private static final long serialVersionUID = 8425314445103581406L;


	private static class FlyingObject {
		int longtitude;
		int latitude;
		int showlong;
		int showlat;
		String transponder;
		boolean isShown;

		public FlyingObject(int lo, int la, int al, String transponder)
		{
			this.longtitude = lo;
			this.latitude = la;
			this.transponder = transponder;
		}

	}



	private int scanAngle;
	private int step = 5;
	private int scanWidth = 20; // degrees
	private Font font;
	private Graphics pen;
	private boolean isShowing;


	private Map<String,FlyingObject> fos = new HashMap<String,FlyingObject>();
	private long scanTime = 20; // Time to make a step in ms
	private JFrame window;
	private ImageIcon background;

	private void addFo(FlyingObject fo)
	{
		synchronized(fos)
		{
			fos.put(fo.transponder,fo);
		}
	}

	// Display a dot and transponder code for o if it is inside scan cone.
	private void putObject(FlyingObject o, Graphics pen)
	{

		if (isInScanCone(o.longtitude, o.latitude))
		{
			// The flying oject o, just entered the scan range
			if (!o.isShown)
			{
				o.isShown=true;
				o.showlat = o.latitude;
				o.showlong = o.longtitude;
			}
			int[] coords = radarCrdToScreenCrd(o.showlong, o.showlat);
			Color c = pen.getColor();
			pen.setColor(Color.BLUE);
			pen.fillOval(coords[0], coords[1], 8, 8);
			pen.setColor(Color.WHITE);
			pen.drawString(o.transponder, coords[0]+10, coords[1]);
			pen.setColor(c);		
		}
		else
			o.isShown = false;
	}

	private static int[] radarCrdToScreenCrd(int x, int y)
	{
		int[] res = new int[2];
		res[0] = x + 200;res[1] = 200-y;
		return res;
	}

	private void putLineOnRadar(int x, int y, int x1, int y1, Color c)
	{
		if (pen != null)
		{
			Color oc = pen.getColor();
			pen.setColor(c);
			int[] startCrds = radarCrdToScreenCrd(x, y);
			int[] stopCrds = radarCrdToScreenCrd(x1, y1);
			pen.drawLine(startCrds[0],startCrds[1],stopCrds[0],stopCrds[1]);
			pen.setColor(oc);
		}

	}


	private static double radianToDegrees(double rad) { return 57.2957795 * rad; }

	public static int crdToAngle(int x, int y)
	{
		double len = Math.sqrt(Math.pow(x,2) + Math.pow(y,2));

		double xAngle = Math.acos( x / len );

		// Q1 => No extra xAngle is the answer
		// Q2 => No extra xAngle is the answer
		// Q3 => x is negative and hence xAngle is 90 degrees off
		// on the vertical axis xAngle is 180 off
		// Q4 => No extra xAngle is 270 degrees off
		double extra = y < 0 ? 360 - radianToDegrees(xAngle) : radianToDegrees(xAngle);

		return (int)(Math.round(extra));
	}

	private boolean isInScanCone(int x, int y)
	{
		int fromAngle = scanAngle - scanWidth;
		int toAngle = scanAngle;
		return crdToAngle(x, y) > fromAngle && crdToAngle(x, y) < toAngle;
	}



	public Radar()
	{
		background = new ImageIcon(Radar.class.getResource("radardisc.jpg"));
		this.setBackground(Color.BLACK);
		font = new Font("Helvetica",Font.BOLD,12);
		setPreferredSize(new Dimension(400, 400));
		scanAngle = (90 % step == 0 ) ? 1 : 0;
		this.window = makeWindow(this);
	}

	public void stepScan()
	{
		scanAngle += step;
		scanAngle = scanAngle % 360;
		this.repaint();
	}


	@Override
	public void paint(Graphics arg0) {	
		drawRadarDisc(arg0);
	}

	private void drawRadarDisc(final Graphics pen)
	{

		if (SwingUtilities.isEventDispatchThread())
		{
			
			if ( (this.pen=pen) != null)
			{
				pen.setFont(font);
				//pen.setColor(new Color(0x3b,0x82,0x0c));

				// Draw disc
				pen.drawImage(background.getImage(),0,0,400,400,background.getImageObserver());
				
				// Draw center dot
				pen.setColor(Color.BLACK);
				pen.fillOval(195,195,10,10);

				// Draw rings
				for(int d = 10; d < 200;d+=10)
					pen.drawOval(200-d, 200-d, 2*d, 2*d);

				// Draw the flying objects in this radar
				synchronized(fos)
				{
					for(FlyingObject fo : fos.values())
						putObject(fo, pen);
				}

				// Draw the scan line
				putLineOnRadar(0, 0,
						(int)(200*Math.cos(degreesToRadians(scanAngle))), 
						(int)(200*Math.sin(degreesToRadians(scanAngle))), Color.YELLOW);

			}
			return;
		} 

		// Not the dispatcher thread lets delegate this invocation
		try {
			SwingUtilities.invokeAndWait(new Runnable() {

				@Override
				public void run() {
					drawRadarDisc(pen);
				}

			});
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static JFrame makeWindow(Component content)
	{
		JFrame window = new JFrame();
		window.getContentPane().add(content);
		window.setSize(content.getPreferredSize());
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setTitle("VDM Radar");
		window.pack();
		return window;
	}


	private static double degreesToRadians(int degree)
	{
		return (44.0 * degree) / 2520.0;
	}

	//------------------------------------------------------
	// VDM Interface 
	//------------------------------------------------------

	// -- AddFlyingObject: int * int * int * seq of char ==> ()
	public Value AddFlyingObject(Value longtitude, Value latitude, Value altitude, Value transponder)
	{
		synchronized(this.fos)
		{
			IntegerValue iv = (IntegerValue)longtitude;
			IntegerValue la = (IntegerValue)latitude;

			FlyingObject fo = new FlyingObject( (int)iv.value, (int)la.value, 10, transponder.toString());
			this.addFo(fo);
			return new VoidValue();
		}
	}

	// -- RemFlyingObject: seq of char ==> ()
	public Value RemFlyingObject(Value transponder)
	{
		synchronized(this.fos)
		{
			fos.remove(transponder);
			return new VoidValue();
		}
	}


	// -- UpdateFlyingObject: seq of char * int * int ==> ()
	public Value UpdateFlyingObject(Value transponder, Value longtitude, Value latitude)
	{
		int lon = (int)((IntegerValue)longtitude).value;
		int lat = (int)((IntegerValue)latitude).value;
		synchronized(this.fos)
		{
			FlyingObject o = fos.get(transponder);
			if (o != null)
			{
				o.longtitude = lon;
				o.latitude = lat;
			}
			return new VoidValue();
		}

	}

	// -- SetStepSize: int ==> ()
	public Value SetStepSize(Value newStep)
	{
		this.step = (int)((IntegerValue)newStep).value;
		return new VoidValue();
	}


	// -- StepRadar: () ==> ()
	public Value StepRadar()	
	{
		// If the window is not shown yet, show it!
		if (!isShowing)
		{
			isShowing = true;
			(window).setVisible(true);
		}

		// step the radar 
		this.stepScan();

		// wait a bit to simulate it takes time to scan
		try {
			Thread.sleep(scanTime);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		return new VoidValue();
	}

	// -- SetScanWidth: int ==> ()
	public Value SetScanWidth(Value width)
	{

		this.scanWidth = (int)((IntegerValue)width).value;
		return new VoidValue();
	}

	// -- SetScanTime: int ==> ()
	public Value SetScanTime(Value scanTime)
	{
		this.scanTime = (int)((IntegerValue)scanTime).value;
		return new VoidValue();
	}

	// -- SetWindowPosition: int * int ==> ()
	public Value SetWindowPosition(Value ix, Value iy)
	{
		int x = (int)((IntegerValue)ix).value;
		int y = (int)((IntegerValue)iy).value;
		this.window.setLocation(x, y);
		return new VoidValue();
	}

	// -- SetTitle: seq of char ==> ()
	public Value SetTitle(Value v)
	{
		window.setTitle(v.toString());
		return new VoidValue();
	}

	// -- SetScanAngle: int ==> ()
	public Value SetScanAngle(Value v)
	{
		scanAngle = (int)((IntegerValue)v).value;
		return new VoidValue();
	}

	// -- static ToCharSeq: token ==> seq of char
	public static Value ToCharSeq(Value v)
	{
		if (v != null)
		{
			System.out.println(v.getClass());

			if (v instanceof NumericValue)
			{
				NumericValue nv = (NumericValue)v;
				return new SeqValue("Number:"+Double.toString(nv.value));
			}

			if (v != null)
				return new SeqValue(v.toString());
		}
		else System.out.println("V is null, Why the face.");
		return new SeqValue();
	}

	public static void main(String[] args)
	{
		Radar r = new Radar();
		r.StepRadar();
	}
}
