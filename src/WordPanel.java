import java.awt.Color;
import java.awt.Graphics;
import java.awt.Font;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.*;

public class WordPanel extends JPanel implements Runnable{
	public static volatile boolean done;
	public int[] j;
	public static AtomicInteger k = new AtomicInteger(0);
	public WordRecord[] words;
	public volatile String text = "";
	private int noWords;
	private int maxY;
	public volatile Score score = new Score();
	public volatile JLabel missed = new JLabel("Missed:" + score.getMissed() + "    ");
	public volatile JLabel caught = new JLabel("Caught:" + score.getCaught() + "    ");
	public volatile JLabel scr=new JLabel("Score:" + score.getScore()+ "    ");;


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
		    	g.drawString(words[i].getWord(),words[i].getX(),words[i].getY());  //y-offset for skeleton so that you can see the words
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
				int v;
				if (k.get() >= noWords) {
					k = new AtomicInteger(0);
				}
				v = k.getAndIncrement();
			while(true) {
				WordRecord temp = words[v];
				while (!temp.dropped()) {
					if(temp.matchWord(text)){
						this.score.caughtWord(text.length());
						caught.setText("Caught:" + score.getCaught() + "    ");
						scr.setText("Score:" + score.getScore()+ "    ");
					}
					temp.drop(1);
					repaint();
					try {
						Thread.sleep((long) (0.02*temp.getSpeed()));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
                this.score.missedWord();
				missed.setText("Missed: " + score.getMissed() + "    ");
				temp.resetWord();
				repaint();
			}
		}

	}


