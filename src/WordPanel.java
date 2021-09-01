import java.awt.Color;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JButton;
import javax.swing.JPanel;

public class WordPanel extends JPanel implements Runnable{
	public static volatile boolean done;
	public int[] j;
	public static AtomicInteger k = new AtomicInteger(0);
	public WordRecord[] words;
	private int noWords;
	private int maxY;
	private WordDictionary d;

		
		public void paintComponent(Graphics g) {
		    int width = getWidth();
		    int height = getHeight();
		    g.clearRect(0,0,width,height);
		    g.setColor(Color.red);
		    g.fillRect(0,maxY-10,width,height);

		    g.setColor(Color.black);
		    g.setFont(new Font("Helvetica", Font.PLAIN, 26));
		   //draw the words
		   //animation must be added 
		    for (int i=0;i<noWords;i++){	    	
		    	//g.drawString(words[i].getWord(),words[i].getX(),words[i].getY());
				j[i]=i;
		    	g.drawString(words[i].getWord(),words[i].getX(),words[i].getY()+20);  //y-offset for skeleton so that you can see the words
		    }
		   
		  }
		
		WordPanel(WordRecord[] words, int maxY) {
			this.words=words; //will this work?
			noWords = words.length;
			done=false;
			this.maxY=maxY;
			j = new int[noWords];
		}
		
		public void run() {
			//add in code to animate this
			while(true) {
				int v = k.getAndIncrement();
				if (v >= noWords) {
					k = new AtomicInteger(0);
					v = k.getAndIncrement();
				}
				WordRecord temp = words[v];
				while (!temp.dropped()) {
					temp.drop(10);
					repaint();
					try {
						Thread.sleep(temp.getSpeed());
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				words[v].resetWord();
			}
		}

	}


