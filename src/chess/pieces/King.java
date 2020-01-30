package chess.pieces;

import chess.engine.Board;
import chess.engine.Engine;

/**
 * Rutgers New Brunswick
 * CSC-213 (Software Methodology)
 *
 * @author Danilo Navas (den34)
 * @version 1.0
 */
public class King implements Piece {
	/**
	 * Row number on board (rank)
	 */
	private int x;

	/**
	 * Column number on board (file)
	 */
	private int y;

	/**
	 * Holds previous rank position
	 */
	private int prevX;

	/**
	 * Holds previous file position
	 */
	private int prevY;

	/**
	 * Holds rank of target position
	 */
	private int targetX;

	/**
	 * Holds file of target position
	 */
	private int targetY;

	/**
	 * Holds instance of Engine
	 */
	Engine engine;

	/**
	 * Holds the target piece instance
	 */
	Piece target;

	/**
	 * Holds the rook that will be used for castling
	 */
	Piece rook;

	/**
	 * Holds flag to determine if this piece has moved
	 */
	boolean hasMoved = false;

	/**
	 * Flag used to determine if this piece is black
	 */
	boolean isBlack;
	
	/**
	 * Flag used to determine if this piece is white
	 */
	boolean isWhite;
	
	/**
	 * Name of the piece
	 */
	String name;
	
	/**
	 * Constructor
	 * @param name The name of this piece
	 * @param isWhite Flag used to know if this piece is white
	 * @param isBlack Flag used to know if this piece is black
	 * @param x The starting rank of this piece
	 * @param y The starting file of this piece
	 * @param e The instance of Engine
	 */
	public King(String name, boolean isWhite, boolean isBlack, int x, int y, Engine e) {
		this.x = x;
		this.y = y;
		this.engine = e;
		this.name = name;
		this.isWhite = isWhite;
		this.isBlack = isBlack;
	}
	
	/**
	 * @return The rook that will potentially be used for castling
	 */
	public Piece getRook() {
		return this.rook;
	}

	/**
	 * Updates the position of this piece with the target rank and file
	 * Also creates a backup of current position
	 */
	public void updatePosition() {
		prevX = x;
		prevY = y;
		x = targetX;
		y = targetY;
	}

	/**
	 * Rollsback the position of this piece to previous position
	 */
	public void rollbackPosition() {
		x = prevX;
		y = prevY;
	}

	/**
	 * Method that can be used to determine if this piece is white
	 * @return true|false
	 */
	public boolean isWhite() {
		return this.isWhite;
	}

	/**
	 * Method that can be used to determine if this piece is black
	 * @return true|false
	 */
	public boolean isBlack() {
		return this.isBlack;
	}

	/**
	 * Method that can be used to determine if this piece is King
	 * @return true|false
	 */
	public boolean isKing() {
		return true;
	}

	/**
	 * @return The name of this piece
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * @return The local "x" value
	 */
	public int getX() {
		return x;
	}

	/**
	 * @return The local "y" value
	 */
	public int getY() {
		return y;
	}

	/**
	 * Method that outputs the name of this piece to standard output
	 */
	public void draw() {
		System.out.print(this.name);
	}

	/**
	 * Method that sets local "hasMoved" flag to true
	 */
	public void moved() {
		this.hasMoved = true;
	}

	/**
	 * Method that can be used to determined if this piece has moved
	 * @return true|false
	 */
	public boolean hasMoved() {
		return this.hasMoved;
	}

	/**
	 * Method that determines if the next move for this piece is legal
	 * @param target The target piece
	 * @param targetX The rank of the target piece
	 * @param targetY The file of the target piece
	 * @return true|false
	 */
	public boolean isLegalMove(Piece target, int targetX, int targetY) {
		this.target = target;
		this.targetX = targetX;
		this.targetY = targetY;
		boolean up = targetY == y && targetX == (x-1);
		boolean down = targetY == y && targetX == (x+1);
		boolean left = targetX == x && targetY == (y-1);
		boolean right = targetX == x && targetY == (y+1);
		boolean topRight = targetX == (x-1) && targetY == (y+1);
		boolean bottomRight = targetX == (x+1) && targetY == (y+1);
		boolean topLeft = targetX == (x-1) && targetY == (y-1);
		boolean bottomLeft = targetX == (x+1) && targetY == (y-1);
		if (isCastlingMove()) {
			return true;
		} else {
			rook = null;
		}
		if (topLeft || up || topRight || left || right || bottomLeft || down || bottomRight) {
			return true;
		}
		return false;
	}
	
	/**
	 * Method that determines if this move will castle the king
	 * @return true|false
	 */
	private boolean isCastlingMove() {
		// 1. Only valid targetY values are 6 and 2 for both kings
		if (targetY != 6 && targetY != 2) {
			return false;
		}
		// 2. If the selected target position is not in the same row
		// as the King return false
		if ((isWhite && targetX != 7) || (isBlack && targetX != 0)) {
			return false;
		}
		// 3. King is not in check
		if ((isWhite && engine.whiteKingInCheck()) || (isBlack && engine.blackKingInCheck())) {
			return false;
		}
		// 4. If King or Rook have moved can't castle
		rook = findRookForCastling();
		if (this.hasMoved || rook.hasMoved()) {
			return false;
		}
		// 5. Space between King and Rook is empty
		if (!isSpaceBetweenEmpty(rook)) {
			return false;
		}
		// 6. Can't castle if an enemy piece is attacking the next square
		// in the direction the king wants to move
		if (nextSquareInCastlingDirectionIsAttacked()) {
			return false;
		}
		return true;
	}
	
	/**
	 * @return The Rook that the King might potentially castle with
	 */
	private Piece findRookForCastling() {
		Board b = engine.getBoard();
		Piece[][] board = b.getBoard();
		if (targetX == 7) {
			return targetY == 6 ? board[7][7] : board[7][0];
		} else if (targetX == 0) {
			return targetY == 6 ? board[0][7] : board[0][0];
		}
		return null;
	}
	
	/**
	 * Method used to determine if space between King and Rook is empty for castling
	 * @param rook The rook corresponding to castling position
	 * @return true|false
	 */
	private boolean isSpaceBetweenEmpty(Piece rook) {
		Board b = engine.getBoard();
		Piece[][] board = b.getBoard();
		int rookY = rook.getY();
		if (y < rookY) {
			int _y = y + 1;
			while (_y < rookY) {
				if (board[x][_y] != null) {
					return false;
				}
				++_y;
			}
			return true;
		} else if (y > rookY) {
			int _rookY = rookY + 1;
			while (_rookY < y) {
				if (board[x][_rookY] != null) {
					return false;
				}
				++_rookY;
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Method used to determine if the immediate square in castling direction
	 * is being attacked. If it is, king can't castle
	 * @return true|false
	 */
	private boolean nextSquareInCastlingDirectionIsAttacked() {
		Board b = engine.getBoard();
		Piece[][] board = b.getBoard();
		int _y = targetY < y ? y - 1 : y + 1;
		for (Piece[] ps : board) {
			for (Piece p : ps) {
				if (p == null) {
					continue;
				}
				if (this.isWhite) {
					if (p.isBlack() && p.isLegalMove(null, x, _y)) {
						return true;
					}
				} else {
					if (p.isWhite() && p.isLegalMove(null, x, _y)) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
