import java.awt.event.KeyEvent;
import java.awt.Image;

public class Arena extends GameEngine {

	protected enum ArenaItem {Empty, Head, Body, Tail, Apple, Fox, Wall, RedApple};
	protected int rows, columns, scale, windowW, windowH, arenaX, arenaY, rectH;

	
	protected ArenaItem[][] grid;
	
	private Image head, body, tail, apple, wall;
	private int dirCol, dirRow, newDirC, newDirR;
	private SnakePart snakeHead;
	private SnakePart snakeTail;
	private int imgWidth, imgHeight, appleR, appleC, appleCount, score, numWall, totalWalls, wallC, wallR, lives; 
	private boolean addApple, addWall, incSpeed, menu = true;
	private double dtLast;
	private double snakeSpeed;
	

	public Arena(int arenaRows, int arenaCols, int scaleFactor) {
		rows = arenaRows;
		columns = arenaCols;
		grid = new ArenaItem[rows][columns];
		scale = scaleFactor;
	}
	
	//Converts a double to a string
    public String doubleToString(double d)
    {
        return new Double(d).toString();
    }

    //Converts an int to a string
    public String intToString(int i)
    {
        return new Integer(i).toString();
    }

	public boolean isInArenaBounds(int headR, int headC)
	{
		if(headC < 0 || headC >= columns || headR < 0 || headR >= rows)     //not in bounds
		{
			return false;
		}
		return true;
	}

    //Places new apple randomly
	public void placeApple()
	{
		appleR = rand(rows);
		appleC = rand(columns);
		while(grid[appleR][appleC] != ArenaItem.Empty)
		{
			appleR = rand(rows);
			appleC = rand(columns);
		}
		grid[appleR][appleC] = ArenaItem.Apple;
		addApple = false;
		return;
	}

    //place wall obstacles randomly
	public void placeWalls()
	{
		for(numWall = 0; numWall < 2; numWall++)    //add 2 walls at a time
		{
			wallR = rand(rows);
			wallC = rand(columns);
			while(grid[wallR][wallC] != ArenaItem.Empty)    //check random wall location is empty
			{
				wallR = rand(rows);
				wallC = rand(columns);
			}
			grid[wallR][wallC] = ArenaItem.Wall;    //add wall to random location
		}
		addWall = false;
		return;
	}

	//adjusts snake movement after crash (if lives left)
	public void adjustSnakeDir(int HeadRow, int HeadCol)
	{
		if(dirCol == 1 || dirCol == -1)     //if snake is travelling right or left when crashed
		{
			if(isInArenaBounds(HeadRow - 1, HeadCol + 0) && grid[HeadRow-1][HeadCol] == ArenaItem.Empty)     //try up
			{
				newDirR = dirRow = -1;
				newDirC = dirCol = 0;
			} else
			{
				if(isInArenaBounds(HeadRow + 1, HeadCol + 0) && grid[HeadRow+1][HeadCol] == ArenaItem.Empty) {  //try down
					newDirR = dirRow = 1;
					newDirC = dirCol = 0;
				}
			}
			
		} else if(dirRow == 1 || dirRow == -1)  //if snake is travelling down or up when crashed
		{
			if(isInArenaBounds(HeadRow + 0, HeadCol - 1) && grid[HeadRow][HeadCol-1] == ArenaItem.Empty)   //try left
			{
				newDirR = dirRow = 0;
				newDirC = dirCol = -1;
			} else {
				if(isInArenaBounds(HeadRow + 0, HeadCol + 1) && grid[HeadRow][HeadCol+1] == ArenaItem.Empty)   //try right
				{
					newDirR = dirRow = 0;
					newDirC = dirCol = 1;
				}
			}
		}
	}


	public void init()
	{
		//load images
		head = loadImage("src/images/head.png");
		body = loadImage("src/images/dot.png");
		tail = loadImage("src/images/dot.png");
		apple = loadImage("src/images/apple.png");
		wall = loadImage("src/images/wall.png");
		
		// assume all of the images are the same size
		imgWidth = head.getWidth(null);
		imgHeight = head.getHeight(null);
		rectH = 30;
		windowW = columns*imgWidth * scale; //+ size of bar at top and bottom
		windowH = (rows*imgHeight + rectH) * scale;
		setWindowSize(windowW, windowH);
		snakeHead = new SnakePart(columns/2, rows/2, null);
		SnakePart bodyInit = new SnakePart(snakeHead.getRow(), snakeHead.getCol()-1, snakeHead);
		snakeTail = new SnakePart(bodyInit.getRow(), bodyInit.getCol()-1, bodyInit);
		score = 0;
		lives = 3;
		appleCount = 0;
		snakeSpeed = 0.25;
		dirCol = newDirC = 1; //initial movement to the right
		dirRow = newDirR = 0;
		dtLast = 0;
		
		// Create an empty grid and place the snake 
		for(int r = 0; r < rows; r++)
			for(int c = 0; c < columns; c++)
				grid[r][c] = ArenaItem.Empty;

		grid[snakeHead.getRow()][snakeHead.getCol()] = ArenaItem.Head;
		grid[bodyInit.getRow()][bodyInit.getCol()] = ArenaItem.Body;
		grid[snakeTail.getRow()][snakeTail.getCol()] = ArenaItem.Tail;
		placeApple();
	}

	
	//update movement
	public void update(double dt)
	{
		if(dirCol == 0 && dirRow == 0 || menu) { //if not moving or menu is up
			return;
		}
		dtLast = dtLast + dt;
		
		if(dtLast < snakeSpeed)
		{
			return; 
		}
		
		//increase snake speed as apples are collected (every 2 apples)
		if(appleCount%2 == 0 && appleCount != 0 && incSpeed && snakeSpeed > 0.03) {
			snakeSpeed = snakeSpeed - 0.01;
			incSpeed = false;
		}
		
		dtLast = 0; 
		dirRow = newDirR;
		dirCol = newDirC;

		//etbhee + h
		SnakePart newHead = new SnakePart(snakeHead.getRow() + dirRow, snakeHead.getCol() + dirCol, null);

		// The snake has crashed into the arena edge
		if(!(isInArenaBounds(snakeHead.getRow()+dirRow, snakeHead.getCol()+dirCol)))
		{
			lives--;
			if(lives > 0)
			{
				adjustSnakeDir(snakeHead.getRow(), snakeHead.getCol());	
				newHead.setCol(snakeHead.getCol() + dirCol);
				newHead.setRow(snakeHead.getRow() + dirRow);
			}
			else
			{
				dirCol = dirRow = 0;
				newDirC = newDirR = 0;
				return;
			}
		}
		
		//MOVE SNAKE
			//Snake Linked List: etbhee => etbhee + h => etbhhe => etbbhe => eebbhe => eetbhe

		// If the snake has crashed into wall or itself
		if(grid[newHead.getRow()][newHead.getCol()] == ArenaItem.Wall || grid[newHead.getRow()][newHead.getCol()] == ArenaItem.Tail || grid[newHead.getRow()][newHead.getCol()] == ArenaItem.Body)
		{
			lives--;
			if(lives > 0)
			{
				adjustSnakeDir(snakeHead.getRow(), snakeHead.getCol());	
				newHead.setCol(snakeHead.getCol() + dirCol);
				newHead.setRow(snakeHead.getRow() + dirRow);
			}
			else
			{
				dirCol = dirRow = 0;
				newDirC = newDirR = 0;
				return;
			}
		} 

		//if apple is at next head location, add new apple, adjust score, increase count of apples eaten 
		if(grid[newHead.getRow()][newHead.getCol()] == ArenaItem.Apple)
		{
			appleCount ++;
			incSpeed = true;
			score = score + 50;
			addApple = true;
			if(appleCount % 5 == 0) {
				addWall = true;
			}
		}
		else
		{	//if no apple at next head location, old tail becomes empty, next body part becomes tail
			//eebbhe
			grid[snakeTail.getRow()][snakeTail.getCol()] = ArenaItem.Empty;
			snakeTail = snakeTail.getNextPart();
			//eetbhe
			grid[snakeTail.getRow()][snakeTail.getCol()] = ArenaItem.Tail;
		}
		
		//new head location becomes current head, old head becomes body part
		//etbhhe
		grid[newHead.getRow()][newHead.getCol()] = ArenaItem.Head; //update location of head
		snakeHead.setNextPart(newHead);
		//etbbhe
		grid[snakeHead.getRow()][snakeHead.getCol()] = ArenaItem.Body;
		snakeHead = newHead;
		
		//if apple was eaten, place a new apple
		if(addApple == true)
		{
			placeApple();
		}
				
		if(addWall == true && totalWalls < (rows*columns)*0.05)     //maximum 10% of grid tiles contain walls
		{
			placeWalls();
			totalWalls = totalWalls + 5;
		}
	}
//________________________________________________________________________________________________________________
	
	public void paintComponent()
	{
		scale(scale, scale); 	//if scale factor passed into main adjusts size of all components
		
		if(menu || (dirCol == 0 && dirRow == 0))
		{
			changeBackgroundColor(white);
			clearBackground(windowW, windowH);
			
			changeColor(black);
			String strPlay = new String ("Press 'Spacebar' to play, or 'esc' to quit.");
			drawBoldText((columns*imgHeight)*6/47, (rows*imgHeight)/2, strPlay , "Arial", (columns/2)-5); 
			
			if(dirCol == 0 && dirRow == 0)
			{
				changeColor(red);
				String strGO = new String("GAME OVER!");
				
				drawBoldText((columns*imgHeight)*17/47, (rows*imgHeight)/3, strGO, "Arial", columns/2); 
			
				//score
				changeColor(blue);
				drawBoldText(((columns*imgHeight)*3/16), (rows*imgHeight)*2/3, "Score: ", "Arial", 14);
				drawText(((columns*imgHeight)*6/16), (rows*imgHeight)*2/3, intToString(score), "Arial", 14); 
				
				//number apples collected
				drawBoldText(((columns*imgHeight)*10/16), (rows*imgHeight)*2/3, "Apples: ", "Arial", 14);
				drawText(((columns*imgHeight)*13/16), (rows*imgHeight)*2/3, intToString(appleCount), "Arial", 14);
			}
			else
			{
				changeColor(blue);
				drawBoldText((columns*imgHeight)*10/47, (3*imgHeight), "Welcome to Snake!" , "Arial", (columns/2)+5); 
			}
		}
		else
		{
			changeBackgroundColor(black);
			clearBackground(windowW, windowH);
		
			for(int r = 0, imgY = 0; r < rows; r++, imgY += imgHeight)  //go through grid row by row
			{
				for(int c = 0, imgX = 0; c < columns; c++, imgX += imgWidth)    //go through grid column by column
				{
					switch(grid[r][c])  //draw ArenaItem Image for each case
					{
						case Head:
							drawImage(head, imgX, imgY); 
							break;
						case Body:
							drawImage(body, imgX, imgY); 
							break;
						case Tail:
							drawImage(tail, imgX, imgY); 
							break;
						case Apple:
							drawImage(apple, imgX, imgY);
							break;
						case Wall:
							drawImage(wall, imgX, imgY);
							break;
						default:
							break;
					}
				}
			}
		//Stats Bar
			//draw white rectangle
			changeColor(white);
			drawSolidRectangle(0, rows*imgHeight, columns*imgHeight, rectH*imgHeight);
	
			//score
			changeColor(blue);
			drawBoldText(((columns*imgHeight)*1/16), rows*imgHeight+rectH*2/3, "Score: ", "Arial", 14);
			drawText(((columns*imgHeight)*3/16), rows*imgHeight+rectH*2/3, intToString(score), "Arial", 14); 
			
			
			//number apples collected
			drawBoldText(((columns*imgHeight)*12/16), rows*imgHeight+rectH*2/3, "Apples: ", "Arial", 14);
			drawText(((columns*imgHeight)*15/16), rows*imgHeight+rectH*2/3, intToString(appleCount), "Arial", 14); 
			
			
			//lives (text turns red if one life left)
			drawBoldText(((columns*imgHeight)*6/16), rows*imgHeight+rectH*2/3, "Lives: ", "Arial", 14);
			if(lives < 2)
			{
				changeColor(red);
			}
				drawText(((columns*imgHeight)*8/16), rows*imgHeight+rectH*2/3, intToString(lives), "Arial", 16); 
		}
	}
		

	public void keyPressed(KeyEvent event)
	{
		// Spacebar to restart game from menu.
		if(menu || (dirRow == 0 && dirCol == 0))
		{
			if(event.getKeyCode() == KeyEvent.VK_SPACE)
			{
				menu = false;
				init();
			}
			if(event.getKeyCode() == KeyEvent.VK_ESCAPE)
			{
				System.exit(2);
			}
		}

		if(event.getKeyCode() == KeyEvent.VK_UP)
		{
			if(dirRow <= 0)     //stops snake going over itself to change direction
			{
				newDirR = -1;
				newDirC = 0;
			}
		}
		else if(event.getKeyCode() == KeyEvent.VK_DOWN)   //stops snake going over itself to change direction
		{
			if(dirRow >= 0)
			{
				newDirR = 1;
				newDirC = 0;
			}
		}
		else if(event.getKeyCode() == KeyEvent.VK_LEFT)
		{
			if(dirCol <= 0)     //stops snake going over itself to change direction
			{
				newDirR = 0;
				newDirC = -1;
			}
		}
		else if(event.getKeyCode() == KeyEvent.VK_RIGHT)
		{
			if(dirCol >= 0) //stops snake going over itself to change direction
			{
				newDirR = 0;
				newDirC = 1;
			}
		}
	}
}



