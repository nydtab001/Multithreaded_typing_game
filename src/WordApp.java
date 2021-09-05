import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;


import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
//model is separate from the view.

public class WordApp {
//shared variables
	static int noWords=4;
	static int totalWords;

   	static int frameX=1000;
	static int frameY=600;
	static int yLimit=480;

	static WordDictionary dict = new WordDictionary(); //use default dictionary, to read from file eventually

	static WordRecord[] words;
	static volatile boolean done;  //must be volatile
	public static Score score = new Score();

	static WordPanel w;

	static boolean start = false;
	static boolean already_started = false;
	static boolean pause = true;
	static Thread[] array;

	/**
	 * This method setsup the GUI display of the program with all panels and button events
	 *
	 * @param frameX is the with of the display
	 * @param frameY is the height of the display
	 * @param yLimit is the maximum height at which words are visible
	 */
	
	public static void setupGUI(int frameX,int frameY,int yLimit) {
		// Frame init and dimensions
		array = new Thread[noWords];
		for (int i = 0; i < noWords; i++) {
			array[i] = new Thread(w);
		}
    	JFrame frame = new JFrame("WordGame"); 
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.setSize(frameX, frameY);
      JPanel g = new JPanel();
      g.setLayout(new BoxLayout(g, BoxLayout.PAGE_AXIS)); 
      g.setSize(frameX,frameY);

		w = new WordPanel(words,yLimit);
		w.setSize(frameX,yLimit+100);
	   g.add(w); 
	    
      JPanel txt = new JPanel();
      txt.setLayout(new BoxLayout(txt, BoxLayout.LINE_AXIS)); 
      JLabel caught =new JLabel("Caught: " + score.getCaught() + "    ");
      JLabel missed =new JLabel("Missed:" + score.getMissed()+ "    ");
      JLabel scr =new JLabel("Score:" + score.getScore()+ "    ");    
      txt.add(caught);
	   txt.add(missed);
	   txt.add(scr);
	   w.missed = missed;
	   w.caught = caught;
	   w.scr = scr;
	   w.totalwords=totalWords;
        for (int i = 0; i < noWords; i++) {
            array[i] = new Thread(w);
        }
    
	    //[snip]

		/**
		 *
		 */

		final JTextField textEntry = new JTextField("",20);
	   textEntry.addActionListener(new ActionListener()
	   {

		   /**
			* This method is called when text is entered in the textfeild
			*
			* @param evt is the event action event being listened for
			*/
		   public void actionPerformed(ActionEvent evt) {
			  //[snip]
			  w.text= textEntry.getText();
	         textEntry.setText("");
	         textEntry.requestFocus();
	      }
	   });
	   
	   txt.add(textEntry);
	   txt.setMaximumSize( txt.getPreferredSize() );
	   g.add(txt);
	 //  w.textentry=textEntry;
	    
	   JPanel b = new JPanel();
      b.setLayout(new BoxLayout(b, BoxLayout.LINE_AXIS)); 
	   JButton startB = new JButton("Start/Restart");;
		
			// add the listener to the jbutton to handle the "pressed" event
		startB.addActionListener(new ActionListener()
		{

			/**
			 * This method is called when the start/restart button is pressed.
			 * This button is used to start the game by starting the individual threads and restart the threads
			 * when the button is pressed again.
			 *
			 * @param e is the button interrupt used to trigger this method
			 */

		   public void actionPerformed(ActionEvent e)
		   {
		      //[snip]
			   if(!start) {
			       start();//starts the threads
			/*   }else if(!start & already_started){
			   		w.pause=true;*/
			   }else if(already_started){
			        start=false;
			        already_started=false;
			        w.reset=true;
			        for(int i=0;i<noWords;i++){
                        try {
                            array[i].join();
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
			        start();
			   		//w.pause=false;
			   }

		      textEntry.requestFocus();  //return focus to the text entry field
		   }
		});
		JButton endB = new JButton("End");;
			
				// add the listener to the jbutton to handle the "pressed" event
		endB.addActionListener(new ActionListener()
		{

			/**
			 * This is the method called when the End button is pressed. This button is used to halt the game.
			 * When this button is pressed the threads are blocked.
			 *
			 * @param e is the button interrupt used to trigger this method
			 */

		   public void actionPerformed(ActionEvent e)
		   {
		      //[snip]
			   start=false;
			   already_started=false;
			   w.reset=true;
			   for(int i=0;i<noWords;i++){
				   try {
					   array[i].join();
				   } catch (InterruptedException ex) {
					   ex.printStackTrace();
				   }
			   }
		   }
		});

		JButton quitB = new JButton("Quit");
		quitB.addActionListener(new ActionListener()
        {

			/**
			 * This is the method called when the Quit button is pressed. When the button is pressed, the program terminates.
			 *
			 * @param e is the button interrupt used to trigger this method
			 */

			@Override
            public void actionPerformed(ActionEvent e) {
				System.exit(0);
            }
        });
		
		b.add(startB);
		b.add(endB);
		b.add(quitB);
		
		g.add(b);
    	
      frame.setLocationRelativeTo(null);  // Center window on screen.
      frame.add(g); //add contents to window
      frame.setContentPane(g);     
       	//frame.pack();  // don't do this - packs it into small space
      frame.setVisible(true);
	}

	/**
	 * This is the method is used to start each thread and is called by the start/restart button.
	 *
	 */

	public static void start(){
	    w.reset=false;
        for (int i = 0; i < noWords; i++) {
            array[i] = new Thread(w);
        }
        for (int i = 0; i < noWords; i++) {
            array[i].start();
        }
        start=!start;
        already_started = true;
    }

	/**
	 * This method is used the fetch data from a file and use it to populate ann array of strings
	 *
	 * @param filename is the file input
	 * @return returns the string array output
	 */

	public static String[] getDictFromFile(String filename) {
		String [] dictStr = null;
		try {
			Scanner dictReader = new Scanner(new FileInputStream(filename));
			int dictLength = dictReader.nextInt();
			//System.out.println("read '" + dictLength+"'");

			dictStr=new String[dictLength];
			for (int i=0;i<dictLength;i++) {
				dictStr[i]=new String(dictReader.next());
				//System.out.println(i+ " read '" + dictStr[i]+"'"); //for checking
			}
			dictReader.close();
		} catch (IOException e) {
	        System.err.println("Problem reading file " + filename + " default dictionary will be used");
	    }
		return dictStr;
	}

	/**
	 * This is the main method that executes the program.
	 *
	 * @param args are the command ine parameters used for input where the first is the total number of words to fall
	 * the second is the number of words displayed each time and the third is the input file.
	 */

	public static void main(String[] args) {
    	
		//deal with command line arguments
		totalWords=Integer.parseInt(args[0]);  //total words to fall
		noWords=Integer.parseInt(args[1]); // total words falling at any point
		assert(totalWords>=noWords); // this could be done more neatly
		String[] tmpDict=getDictFromFile(args[2]); //file of words
		if (tmpDict!=null)
			dict= new WordDictionary(tmpDict);
		
		WordRecord.dict=dict; //set the class dictionary for the words.
		
		words = new WordRecord[noWords];  //shared array of current words
		
		//[snip]
		
		setupGUI(frameX, frameY, yLimit);  
    	//Start WordPanel thread - for redrawing animation

		int x_inc=(int)frameX/noWords;
	  	//initialize shared array of current words

		for (int i=0;i<noWords;i++) {
			words[i]=new WordRecord(dict.getNewWord(),i*x_inc,yLimit);
		}
	}
}