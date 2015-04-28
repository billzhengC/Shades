package Shades;
import java.awt.Dimension;
/*
 * The Block class stores all information about each component's size, the block's color and the methods to get, set and clear a block's color.
 */
import java.awt.Color;
import java.lang.Math;
import java.util.Random; // get random number 
class Block {
	// data about five different shades of blue
	protected enum shadesColor {
		NoColor,Blue1,Blue2,Blue3,Blue4,Blue5; // no color and 5 shades of blue
		private static shadesColor[] vals = values(); // array of values 
		// method to get a darker color
		public shadesColor next() {
			return vals[(this.ordinal()+1)%vals.length];
		}
	}; 
	shadesColor[] allShadesColor = shadesColor.values(); // list of all enum members
	//RGB of all colors
	public static final Color[] blueList = {
			new Color(255,255,255), // white
			new Color(191,239,255), // blue1
			new Color(135,206,255), // blue2
			new Color(0,191,255), // blue3
			new Color(30,144,255), // blue4
			new Color(0,0,205) // blue 5
	};
	// data about the speed by which a block drops in different mode
	private static int[] modeSpeed = {600,400,150}; // three differen speed at different play mode 
	enum shadesMode {
		Easy,Medium,Hard; // three different mode
		// get the speed of the mode
		public int getSpeed() {
			return modeSpeed[this.ordinal()];
		}
	};
	public static final int FrameWidth = 360;  // frame's width
	public static final int FrameLength = 660; // frame's length
	public static final int PanelRowNum = 10; // number of rows in the main board
	public static final int PanelColumnNum = 4; // number of columns in the main board
	public static final int totalRowNum = PanelRowNum+1; // total number of rows (including the score bar)
	public static final int PanelWidth = FrameWidth; // the main board's width
	public static final int PanelLength = FrameLength*PanelRowNum/(totalRowNum); // the main board's length
	public static final int blockWidth = (int) PanelWidth/PanelColumnNum; // each block's width
	public static final int blockLength = (int) PanelLength/PanelRowNum; // each block's length
	
	public static final int ScorebarLength = blockLength ; // the length of the score bar
	public static final Dimension BlockSize = new Dimension(Block.blockWidth,Block.blockLength); // dimension of each block
	public static final Dimension PanelSize = new Dimension(Block.PanelWidth,Block.PanelLength); // dimension of the board
	public static final Dimension ScorebarSize = new Dimension(Block.FrameWidth,Block.ScorebarLength); // dimension of the score bar
	private shadesColor blockColor; // the color of the block
	// intialize the block's color to no color
	Block() {
		blockColor = shadesColor.NoColor;
	}
	// get the block's color, shadesColor type
	public shadesColor getColor() {
		return blockColor;
	}
	// get the block's color, Color type
	public Color getRGBColor() {
		return blueList[blockColor.ordinal()];
	}
	// assign a specific color to this block.
	public void setColor(shadesColor col) {
		blockColor = col;
	}
	// assign a color to this block at random.
	public void setRandomColor() {
		Random ran = new Random();
		int x = Math.abs(ran.nextInt())%5+1;
		blockColor = allShadesColor[x];
	}
	// clear the block's color
	public void clearColor() {
		blockColor = shadesColor.NoColor;
	}
}