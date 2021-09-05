import java.awt.Color;
import java.awt.Graphics;
import java.awt.Font;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.*;

public class WordPanel extends JPanel implements Runnable {
	public volatile boolean done;
	public int[] j;
	public static AtomicInteger k = new AtomicInteger(0);
	public WordRecord[] words;
	public volatile String text = "";
	private int noWords;
	private int maxY;
	public volatile Score score = new Score();
	public volatile JLabel missed = new JLabel("Missed:" + score.getMissed() + "    ");
	public volatile JLabel caught = new JLabel("Caught:" + score.getCaught() + "    ");
	public volatile JLabel scr = new JLabel("Score:" + score.getScore() + "    ");
	public volatile boolean pause = false;
	public int totalwords;
	public AtomicInteger wordcount = new AtomicInteger(0);
	public AtomicInteger threadsfinished = new AtomicInteger(0);
	public volatile boolean reset = false;

	/**
	 * This is the method that dipslays components on the screen and is the method that displays the screen output
	 * atany given time
	 *
	 * @param g is the Graphics input
	 */

	public void paintComponent(Graphics g) {
		int width = getWidth();
		int height = getHeight();
		g.clearRect(0, 0, width, height);
		g.setColor(Color.red);
		g.fillRect(0, maxY - 10, width, height);

		g.setColor(Color.black);
		g.setFont(new Font("Helvetica", Font.PLAIN, 26));
		//draw the words
		//animation must be added
		for (int i = 0; i < noWords; i++) {
			//g.drawString(words[i].getWord(),words[i].getX(),words[i].getY());
			j[i] = i;
			g.drawString(words[i].getWord(), words[i].getX(), words[i].getY());  //y-offset for skeleton so that you can see the words
		}
		if((threadsfinished.get()>=noWords)& done){
			g.drawString("Caught: "+score.getCaught(),400,210);
			g.drawString("Missed: "+score.getMissed(),400,234);
			g.drawString("Your score: "+score.getScore(),400,258);
			threadsfinished= new AtomicInteger(0);
			wordcount = new AtomicInteger(0);
			done=false;
			score = new Score();
			missed.setText("Missed: " + score.getMissed() + "    ");
			caught.setText("Caught:" + score.getCaught() + "    ");
			scr.setText("Score:" + score.getScore() + "    ");
		}

	}

	/**
	 * This is the constructor of the class where the instance variables are initialized
	 *
	 * @param words is the input array of the words
	 * @param maxY is the input y_limit from the WordApp class
	 */

	WordPanel(WordRecord[] words, int maxY) {
		this.words = words; //will this work?
		noWords = words.length;
		done = false;
		this.maxY = maxY;
		j = new int[noWords];
	}

	/**
	 * This is the method that executes when each thread is started. It is also where the main computations
	 * for the animation are done.
	 *
	 */

	public void run() {
		//add in code to animate this
		int v;
		if (k.get() >= noWords) {
			k = new AtomicInteger(0);
		}
		v = k.getAndIncrement();
	//	wordcount = new AtomicInteger(0);
	//	threadsfinished = new AtomicInteger(0);
		while (true) {
			WordRecord temp = words[v];
			if(wordcount.incrementAndGet()>totalwords){
				done=true;
				int threads = threadsfinished.incrementAndGet();
				break;
			}else{done=false;}
			while (!temp.dropped()) {
				if(reset){
					break;
				}
				while (pause) {
					try {
						Thread.sleep(100);
						repaint();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if (temp.matchWord(text)) {
					this.score.caughtWord(text.length());
					caught.setText("Caught:" + score.getCaught() + "    ");
					scr.setText("Score:" + score.getScore() + "    ");
					if(wordcount.incrementAndGet()>totalwords){
						done=true;
						int threads = threadsfinished.incrementAndGet();
						break;
					}else{done=false;}
				}
				caught.setText("Caught:" + score.getCaught() + "    ");
				scr.setText("Score:" + score.getScore() + "    ");
				temp.drop(1);
				repaint();
				try {
					Thread.sleep((long) (0.02 * temp.getSpeed()));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if(temp.dropped()){
				this.score.missedWord();
			}
			if(reset){
				wordcount = new AtomicInteger(0);
				threadsfinished = new AtomicInteger(0);
				done=false;
				score = new Score();
				break;
			}
			missed.setText("Missed: " + score.getMissed() + "    ");
			caught.setText("Caught:" + score.getCaught() + "    ");
			scr.setText("Score:" + score.getScore() + "    ");
			temp.resetWord();
			repaint();
		}
		missed.setText("Missed: " + score.getMissed() + "    ");
		caught.setText("Caught:" + score.getCaught() + "    ");
		scr.setText("Score:" + score.getScore() + "    ");
		words[v].resetWord();
		repaint();
	}

}


