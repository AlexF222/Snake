/*snake segments as array of items in the Snake class - items are X and Y positions in the game board.
 The game grid can keep track of what is at each location*/

public class SnakePart
{
	protected int currCol;
	protected int currRow;
	protected SnakePart nextPart;
		
	public SnakePart(int row, int col, SnakePart next)
	{
		currCol = col;
		currRow = row;
		nextPart = next;
	}
	
	public int getCol()
	{
		return currCol;
	}
	
	public int getRow()
	{
		return currRow;
	}
		
	public void setCol(int col)
	{
		currCol = col;
		return;
	}
	
	public void setRow(int row)
	{
		currRow = row;
		return;
	}
	
	public SnakePart getNextPart()
	{
		return nextPart;
	}
	
	public void setNextPart(SnakePart next)
	{
		nextPart = next;
	}
}


