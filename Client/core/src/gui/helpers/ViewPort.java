package gui.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Is a 2d matrix of textures, used to draw/render the ships in the screen
 *
 */
public class ViewPort {
	int viewWidth, viewHeight, xCells, yCells, xPosition, yPosition, cellWidth, cellHeight;
	Texture[][] viewPortMap;

	/**
	 * Constructor for the class ViewPort. All necessary information is passed
	 * through the parameters
	 * 
	 * @param viewWidth
	 * @param viewHeight
	 * @param xCells
	 * @param yCells
	 * @param xPosition
	 * @param yPosition
	 */
	public ViewPort(int viewWidth, int viewHeight, int xCells, int yCells, int xPosition, int yPosition) {
		this.viewWidth = viewWidth;
		this.viewHeight = viewHeight;
		this.xCells = xCells;
		this.yCells = yCells;
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		this.cellWidth = (viewWidth / xCells);
		this.cellHeight = (viewHeight / yCells);
		viewPortMap = new Texture[xCells][yCells];
	}

	/**
	 * Sets the Cell with position (x, y) with the texture passed in cell
	 * 
	 * @param x
	 * @param y
	 * @param cell
	 */
	public void setCell(int x, int y, Texture cell) {
		viewPortMap[x][y] = cell;
	}

	/**
	 * Will return the texture associated to the cell with position (x, y)
	 * 
	 * @param x
	 * @param y
	 * @return Texture
	 */
	public Texture getCell(int x, int y) {
		return viewPortMap[x][y];
	}

	/**
	 * Will return the texture of the cell where the mouse is located
	 * 
	 * @param mouseX
	 * @param mouseY
	 * @return int[] mouse location
	 */
	public Texture getCellByMouseLocation(int mouseX, int mouseY) {
		int[] mouseLocation = this.returnMouseLocation(mouseX, mouseY);
		return this.getCell(mouseLocation[0], mouseLocation[1]);
	}

	/**
	 * Will set the cell with the coordinates of mouse location with the texture
	 * passed by the parameter cell
	 * 
	 * @param mouseX
	 * @param mouseY
	 * @param cell
	 */
	public void setCellByMouseLocation(int mouseX, int mouseY, Texture cell) {
		int[] mouseLocation = this.returnMouseLocation(mouseX, mouseY);
		viewPortMap[mouseLocation[0]][mouseLocation[1]] = cell;
	}

	/**
	 * We use the batch from BattleScreen to render the ViewPort
	 * 
	 * @param batch
	 */
	public void render(SpriteBatch batch, Texture background) {
		for (int i = 0; i < xCells; i++) {
			for (int j = 0; j < yCells; j++) {
				batch.draw(background, (i * cellWidth) + xPosition, (j * cellHeight) + yPosition, cellWidth,
						cellHeight);
				batch.draw(viewPortMap[i][j], (i * cellWidth) + xPosition, (j * cellHeight) + yPosition, cellWidth,
						cellHeight);
			}
		}
	}

	/**
	 * Returns the cell at the given mouse position. Useful for clicking on a space.
	 * cellClicked[0] is X, cellClicked[1] is Y
	 **/
	public int[] returnMouseLocation(int mouseX, int mouseY) {
		// MouseY is starts from top of screen(3D mode) and has to be flipped(2D mode).
		mouseY = Gdx.graphics.getHeight() - mouseY;
		int[] cellClicked = new int[2];
		cellClicked[0] = (mouseX - xPosition) / cellWidth;
		cellClicked[1] = (mouseY - yPosition) / cellHeight;
		if (cellClicked[0] >= xCells) {
			cellClicked[0] = -1;// Out of Bounds
		}
		if (cellClicked[1] >= yCells) {
			cellClicked[1] = -1;// Out of Bounds
		}
		return cellClicked;
	}

	/**
	 * Will return true or false depending if the mouse id located within the grid
	 * or not.
	 * 
	 * @param mouseX
	 * @param mouseY
	 * @return boolean :: true if in grid, false in any other case
	 */
	public boolean mouseInGrid(int mouseX, int mouseY) {
		mouseY = Gdx.graphics.getHeight() - mouseY;
		if (mouseX >= xPosition && mouseX < xPosition + viewWidth && mouseY >= yPosition
				&& mouseY < yPosition + viewHeight) {
			return true;
		}
		return false;
	}

	public void dispose() {
		for (int i = 0; i < xCells; i++) {
			for (int j = 0; j < yCells; j++) {
				viewPortMap[i][j].dispose();
			}
		}
	}

	public int getViewWidth() {
		return viewWidth;
	}

	public void setViewWidth(int viewWidth) {
		this.viewWidth = viewWidth;
	}

	public int getViewHeight() {
		return viewHeight;
	}

	public void setViewHeight(int viewHeight) {
		this.viewHeight = viewHeight;
	}

	public int getxCells() {
		return xCells;
	}

	public void setxCells(int xCells) {
		this.xCells = xCells;
	}

	public int getyCells() {
		return yCells;
	}

	public void setyCells(int yCells) {
		this.yCells = yCells;
	}

	public int getxPosition() {
		return xPosition;
	}

	public void setxPosition(int xPosition) {
		this.xPosition = xPosition;
	}

	public int getyPosition() {
		return yPosition;
	}

	public void setyPosition(int yPosition) {
		this.yPosition = yPosition;
	}

	public int getCellWidth() {
		return cellWidth;
	}

	public void setCellWidth(int cellWidth) {
		this.cellWidth = cellWidth;
	}

	public int getCellHeight() {
		return cellHeight;
	}

	public void setCellHeight(int cellHeight) {
		this.cellHeight = cellHeight;
	}

	public Texture[][] getViewPortMap() {
		return viewPortMap;
	}

	public void setViewPortMap(Texture[][] viewPortMap) {
		this.viewPortMap = viewPortMap;
	}

}
