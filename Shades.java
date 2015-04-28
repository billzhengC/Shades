package Shades;
import javax.swing.*;
import java.awt.*;


/*
 * Shades is 
 */
public class Shades extends JFrame {
	JLabel scoreBar; // defines a score bar
	public Shades() {
		scoreBar = new JLabel("0"); // reset the score to 0
		scoreBar.setPreferredSize(Block.ScorebarSize); // set size of the score abr
		setLayout(new BorderLayout(0,0)); // use border layout 
		add(scoreBar,BorderLayout.CENTER); // put score bar at the top of window
		Board board = new Board(this); // create the main board
		board.setPreferredSize(Block.PanelSize); // set the board's size
		add(board,BorderLayout.SOUTH); // put the board below the score bar		
		setSize(Block.FrameWidth,Block.FrameLength); // set the window's size
		setTitle("Shades"); // set title
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // set close operation
		board.start(); // start the game
	}
	// get the score bar
	public JLabel getScoreBar() {
		return this.scoreBar;
	}
	public static void main(String args[]) {
		Shades frame = new Shades(); // create the window
		
		frame.setLocationRelativeTo(null); // set location relative to null
		frame.setVisible(true); // make it visible
	}
}