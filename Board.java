package Shades;

import javax.swing.*;
import javax.swing.JOptionPane; // show dialog
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
// keyboard listener
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

// create Tuple class to store coordinates (x,y)
class Tuple<X, Y> { 
	  public final X x; // x coordinate
	  public final Y y; // x coordinate
	  // constructor for (x,y)
	  public Tuple(X x, Y y) { 
	    this.x = x; 
	    this.y = y; 
	  } 
} 

/*
 * Board contains the logic behind this game by using a Swing timer to create a game cycle. 
 * In each cycle, it displays the score, receives the keyboard input from the user, generates and moves a block, 
 * merges two blocks of the same color, clear a row when necessary.
 */

class Board extends JPanel implements ActionListener {
	/* 10 by 4 table, each element is Block type. 
	 * It contains the information about each block's position and color,
	 *  which is the base of painting.
	 * */
	Block[][] blockList = new Block[Block.PanelColumnNum][Block.PanelRowNum];
	Block curBlock; // the current moving block
	Tuple<Integer,Integer> curBlockIndex; // width, length
	Block.shadesColor curColor; // the block's color
	Color curRGBColor = Block.blueList[0]; // the block's RGB color
	boolean isPaused = true; // flag of paused
	boolean isFallingDone = true; // flag of finishing falling down
	int lineRemoved = 0; // number of line cleared
	int blockGenerated = 0; // number of block generated
	int blockMerged = 0; // number of pairs of blocks merged
	int score = 0; // the score the user achieved
	Timer timer; // the timer which control the game cycle
	Timer timerMove; // timer which makes block move more smoothly
	JLabel scoreBar; // the score bar
	// construtor: intialize the scor and start the game. Also, it starts the keyboard listener
	public Board(Shades parent) {
		scoreBar = parent.getScoreBar(); // get the score bar
		scoreBar.setForeground(Color.RED); // set color to red
		scoreBar.setHorizontalAlignment(JLabel.CENTER); // make text center
		scoreBar.setFont(new Font("Courier New", Font.BOLD, 20)); // set font 
		timer = new Timer(400, this); // create the timer
		initializeGame(); // intialize the game
		addKeyListener(new ShadesAdapter()); // add the keyboard listener to receive keybord input
	}
	
	// Game initialization: choose mode, reset score
	private void initializeGame() {
		int modeSpeed = chooseMode(); // prompt user to choose play mode and get the result
		timer.setDelay(modeSpeed); // set the delay according the play mode
		timer.setInitialDelay(0); // set initial delay to 0
		isFallingDone = true; // intialize fallingDone flag
		lineRemoved = 0; // inialize number of line cleared
		blockGenerated = 0; // initialize number of block generated
		blockMerged = 0; // intialize number of pairs of blocks merged
		// intialize blockList
		for (int i=0;i<Block.PanelColumnNum;i++) {
			for (int j=0;j<Block.PanelRowNum;j++) {
				blockList[i][j] = new Block(); // initialize all blocks
				blockList[i][j].clearColor(); // set them to NoColor
			}
		}
		updateScore(); // initialize score
		setFocusable(true); // set focusable to true
		requestFocusInWindow(); //request focus
	}
	
	/* Every certain milliseconds, it updates the position of the current moving block. 
	 * When this block stops moving, it checks calls tryMerge and tryRemoveLine, 
	 * and also calls newRec to create a new block.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// test if the block is still dropping
		if (isFallingDone) { 
			isFallingDone = false; // if not, reset flag 
			// TODO: bug here: does not function well
			tryMerge();
			tryRemoveLine(); // try merging blocks and clearing the line.
			newRec(); // generate new block
		}
		else {
			oneLineDown(); // if so, move it down by one line
		}
	}
	// let the user choose game mode
	private int chooseMode() {
		int speed = 0;
		String[] modes = {"Easy", "Medium", "Hard"}; // three play modes: easy, medium and hard
		// use dialog to let user choose play mode
		String s = (String)JOptionPane.showInputDialog(
		                    this, // in this JPane
		                    "Choose Your Mode:", // set message
		                    "Mode", // set title
		                    JOptionPane.PLAIN_MESSAGE, //  use plain messge
		                    null, // not use icon
		                    modes, // use array modes to display buttons info
		                    "Medium"); // set default choice
		if (s == null)
			exit(); // if the user close the dialog, exit
		else if (s.equals("Easy")) 
			speed = Block.shadesMode.Easy.getSpeed(); // choose easy mode
		else if (s.equals("Medium"))
			speed = Block.shadesMode.Medium.getSpeed(); //choose medium mode
		else if (s.equals("Hard"))
			speed = Block.shadesMode.Hard.getSpeed(); // choose hard mode	    
		return speed;
	}
	// create a new block
	private synchronized void newRec() { 
		updateScore(); // update and display current score
		blockGenerated++; // update the number of blocks generated
		curBlock = new Block(); // reset the current block 
		curBlock.setRandomColor(); // assign a color to the block randomly
		curBlockIndex = new Tuple<Integer,Integer>(1,0); // set the block at the top of the window
		curColor = curBlock.getColor(); // set the block's color
		curRGBColor = curBlock.getRGBColor(); // set the block's RGB color
		// test if the new generated button is able to move down 
		if (blockList[curBlockIndex.x][curBlockIndex.y].getColor()==Block.shadesColor.NoColor)
			blockList[curBlockIndex.x][curBlockIndex.y] = curBlock; // if so, move down 
		else {
			// if not, it means the stack of the blocks has hit the top. Call gameOver
			timer.stop(); // stop timer
			gameOver(); // call gameOver to handle it
		}
	}
	// move the block down by a line
	private synchronized void oneLineDown() {
		// move the block down by one line
		if (!tryMoveRec(0,1)) {
			isFallingDone = true; // if it cannot be moved down, set the fallingdone flag to true.
			}
	}
	// directly drop down the block
	private void dropDown() {
		int distance = Block.PanelRowNum-1-curBlockIndex.y; // calculate the distance betweenthe curent block and the bottom of the board.
		while (!tryMoveRec(0,distance)&&(distance>0)) --distance; // get the correct the distance that block will go.
		isFallingDone = true; // set the fallingdone flag to true
	}
	// calculate the score and update the score bar
	private void updateScore() {
		score = 10*lineRemoved + 2*blockGenerated + 4*blockMerged; //calculate the score
		scoreBar.setText("Score: "+String.valueOf(score)); // display the score
	}
	// try move the block in the given direction, return false if it fails.
	private boolean tryMoveRec(int x, int y) {
		 // return false when it hits the boundary
		if ((curBlockIndex.y+y+1>Block.PanelRowNum) || (curBlockIndex.x+x+1>Block.PanelColumnNum)) return false;
		if ((curBlockIndex.y+y<0) || (curBlockIndex.x+x<0)) return false; 
		// return false when it hits an exiting block
		if ((blockList[curBlockIndex.x+x][curBlockIndex.y+y].getColor() != Block.shadesColor.NoColor)) return false;
		Block.shadesColor thisColor = blockList[curBlockIndex.x][curBlockIndex.y].getColor(); // get the current block's color
		blockList[curBlockIndex.x][curBlockIndex.y].clearColor(); // clear the current block's color
		blockList[curBlockIndex.x+x][curBlockIndex.y+y].setColor(thisColor); // set the new color
		curBlockIndex = new Tuple<Integer,Integer>(curBlockIndex.x+x,curBlockIndex.y+y); // update the block's index
		repaint(); // call paint
		return true; // move succeed, return true
	}
	// remove lines consisting blocks of the same shades, if any
	private synchronized boolean tryRemoveLine() {
		boolean flagRemoved = false;
		for (int j=Block.PanelRowNum-1;j>=0;j--) {
			Block.shadesColor rowColor = blockList[0][j].getColor(); // get the color of the first block in a row
			boolean removeFlag = true; // initialize the flag
			if (rowColor==Block.shadesColor.NoColor) continue; // if the row is not full, go to next row
			// if the blocks are not of the same color, go the next row
			for (int i=0;i<Block.PanelColumnNum;i++) {
				Block.shadesColor thisColor = blockList[i][j].getColor();
				if (thisColor!=rowColor) {removeFlag = false; break;}
			}
			// if the row can be removed, remove it and update the score
			if (removeFlag) {
				// move the line above it down by one line
				for (int j1=j;j1>0;j1--) {
					for (int i1=0;i1<Block.PanelColumnNum;i1++) {
						blockList[i1][j1].setColor(blockList[i1][j1-1].getColor());
					}
				}
				// clear the first line
				for (int i1=0;i1<Block.PanelColumnNum;i1++) {
					blockList[i1][0].clearColor();
				}
				repaint(); // call paint 
				flagRemoved = true; // update flag
				lineRemoved++; // update number of line removed
				updateScore(); // update score
			}
		}
		return flagRemoved;
	}
	// merge blocks, if any
	private boolean tryMerge() {
		boolean flagMerged = false;
		// search all blocks
		for (int i=0;i<Block.PanelColumnNum;i++) {
			for (int j=0;j<Block.PanelRowNum;j++) {
				// get this block's color
				Block.shadesColor thisColor = blockList[i][j].getColor();
				// if the block's color is the deepest or no color, or it is in the buttom, move on
				if (thisColor==Block.shadesColor.Blue5 || blockList[i][j].getColor()==Block.shadesColor.NoColor
						|| j ==Block.PanelRowNum-1) continue;
				// found two blocks that can be merged
				if (thisColor == blockList[i][j+1].getColor()) {
					blockList[i][j].clearColor(); // clear the color of the one above
					blockList[i][j+1].setColor(thisColor.next()); // make the color darker
					flagMerged =true; // update flag
					blockMerged++; // update the number of block merged
					updateScore(); // update score
					repaint(); // call paint
				}
				
			}
		}
		return flagMerged;
	}
	// override paint method: draw rectangle according to info stored in blockList
	@Override
	public void paint(Graphics g) {
		super.paint(g); // call super
		// step over every block
		for (int i=0;i<Block.PanelColumnNum;i++) {
			for (int j=0;j<Block.PanelRowNum;j++) {
				// paint the block if it has color
				if (blockList[i][j].getColor()!=Block.shadesColor.NoColor) {
					g.setColor(blockList[i][j].getRGBColor()); // set the color as the block's
					g.fillRect(i*Block.blockWidth, j*Block.blockLength, Block.blockWidth, Block.blockLength); // draw rectangle
				}
			}
		}
	}
	
	// start the game cycle
	public void start() {
		if (!isPaused) return; // if already started, return
		timer.start(); // start the timer
		isPaused = false; // reset paused flag
		updateScore(); // update score
	}
	// pause the game cycle
	private void pause() {
		if (isPaused) return; // if already paused, return
		isPaused = true; // set paused flag
		timer.stop(); // stop timer
		scoreBar.setText("Paused"); // display paused info in the score bar
		repaint(); // paint again
	}
	// restart the game
	private void restart() {
		initializeGame(); // intialize the game
		timer.start(); // start timer
		repaint(); // paint again
	}
	// exit the program
	private void exit() {
		System.exit(0); // call exit 
	}
	// game over
	private void gameOver() {
		scoreBar.setText("Game Over"); // display Game over in the score bar
		String[] options = {"Restart","Exit"}; // two options
		// prompt score and let user choose to exit or restart
		int n = JOptionPane.showOptionDialog( // get user's choice of exit or restart
				this, // use this JPanel
				"Game Over. Your score is " + String.valueOf(score) + ". Restart?", // set message
				"Game Over", // set title
				JOptionPane.YES_NO_OPTION, // set it to yes/no option
				JOptionPane.PLAIN_MESSAGE, // use plain message
				null, // no icon
				options, // use the above message of button
				options[0]); // set the default choice as restart
		switch (n) {
		case JOptionPane.YES_OPTION:
			restart(); // restart the game when chosed
			break;
		case JOptionPane.NO_OPTION:
			exit(); // exit the game when chosed
			break;
		case JOptionPane.CLOSED_OPTION:
			exit(); // when the use closes the window, exit the game
			break;
		}
	
	}
	// inner class: receive keyboard input when the user presses the left, right or down arrow key. Move the block accordingly. 
	class ShadesAdapter implements KeyListener {
		public void keyPressed(KeyEvent e) {
			int keycode = e.getKeyCode(); // get keyboard input
			// move the block according to keyboard input
			switch (keycode) {
			case KeyEvent.VK_LEFT:
				if (!isPaused && !isFallingDone) tryMoveRec(-1,0); // move the block left, when left arrow key is pressed
				break;
			case KeyEvent.VK_RIGHT:
				if (!isPaused && !isFallingDone) tryMoveRec(1, 0); // move the block right, when right arrow key is pressed
				break;
			case KeyEvent.VK_DOWN:
				if (!isPaused && !isFallingDone) dropDown(); // make the block drop down to the bottom, when down arrow key is pressed
				break;
			case KeyEvent.VK_SPACE:
				// pause or start the game when the user presses the space
				if (!isPaused) pause();
				else start();
				break;
			}
		}
		public void keyTyped(KeyEvent e) {
			// pass. not used
		}
		public void keyReleased(KeyEvent e) {
			// pass. not used			
		}
	}
}