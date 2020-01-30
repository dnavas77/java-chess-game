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
public class Pawn implements Piece {
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
	private Engine engine;

	/**
	 * Opponent's pawn that just moved 2 squares
	 */
	Pawn enpassantPawn;

	/**
	 * Holds the target piece instance
	 */
	private Piece target;

	/**
	 * Name of the piece
	 */
	private String name;

	/**
	 * Flag used to determine if this piece is black
	 */
	private boolean isBlack;

	/**
	 * Flag used to determine if this piece is white
	 */
	private boolean isWhite;

	/**
	 * Flag that can be used to know if this pawn just moved 2 squares
	 */
	private boolean justMoved2Squares;

	/**
	 * Holds flag to determine if this piece has moved
	 */
	boolean hasMoved = false;

	/**
	 * Constructor
	 * @param name The name of this piece
	 * @param isWhite Flag used to know if this piece is white
	 * @param isBlack Flag used to know if this piece is black
	 * @param x The starting rank of this piece
	 * @param y The starting file of this piece
	 * @param e The instance of Engine
	 */
	public Pawn(String name, boolean isWhite, boolean isBlack, int x, int y, Engine e) {
		this.x = x;
		this.y = y;
		this.engine = e;
		this.name = name;
		this.isWhite = isWhite;
		this.isBlack = isBlack;
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
		return false;
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
		return this.x;
	}

	/**
	 * @return The local "y" value
	 */
	public int getY() {
		return this.y;
	}

	/**
	 * @return the enemy pawn that just moved 2 squares in previous turn
	 */
	public Pawn getEnpassantPawn() {
		return this.enpassantPawn;
	}

	/**
	 * Method that returns "justMoved2Squares" flag
	 * @return true|false
	 */
	public boolean justMoved2Squares() {
		return this.justMoved2Squares;
	}

	/**
	 * Method used to clear "justMoved2Squares" flag (i.e. set to false)
	 */
	public void clearMove2SquaresFlag() {
		justMoved2Squares = false;
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
		this.enpassantPawn = null;
		this.target = target;
		this.targetX = targetX;
		this.targetY = targetY;
		boolean isRightDiagonal, isLeftDiagonal, move2Squares, isCaptureEnpassant;

		if (isWhite) {
			move2Squares = move2Squares(x == 6, targetX == 4, x-1);
			isRightDiagonal = (targetX == (x-1)) && (targetY == (y+1));
			isLeftDiagonal = (targetX == (x-1)) && (targetY == (y-1));
			isCaptureEnpassant = captureEnpassantEnemyPawn(isRightDiagonal, isLeftDiagonal);
			if (
				move2Squares ||
				move1Square(x - targetX == 1) ||
				captureEnemyPiece(isRightDiagonal, isLeftDiagonal) ||
				isCaptureEnpassant
			) {
				if (move2Squares) {
					justMoved2Squares = true;
				}
				if (!isCaptureEnpassant) {
					enpassantPawn = null;
				}
				return true;
			}
		} else {
			move2Squares = move2Squares(x == 1, targetX == 3, x+1);
			isRightDiagonal = (targetX == (x+1)) && (targetY == (y-1));
			isLeftDiagonal = (targetX == (x+1)) && (targetY == (y+1));
			isCaptureEnpassant = captureEnpassantEnemyPawn(isRightDiagonal, isLeftDiagonal);
			if (
				move2Squares ||
				move1Square(x - targetX == -1) ||
				captureEnemyPiece(isRightDiagonal, isLeftDiagonal) ||
				isCaptureEnpassant
			) {
				if (move2Squares) {
					justMoved2Squares = true;
				}
				if (!isCaptureEnpassant) {
					enpassantPawn = null;
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Method that determines of move is 2 squares forward for this pawn
	 * @param isInitialPos Flag to check if it this piece is in initial position
	 * @param targetIs2Away Flag to check if target position is 2 squares away
	 * @param nextSquareX Integer value that contains rank of next square
	 * @return true|false
	 */
	private boolean move2Squares(boolean isInitialPos, boolean targetIs2Away, int nextSquareX) {
		// It's first move and user wants to move 2 squares forward
		// all squares in path need to be empty
		if (!isInitialPos) { return false; }
		Board b = engine.getBoard();
		Piece[][] board = b.getBoard();
		boolean columnIsSame = y == targetY;
		boolean pathIsEmpty = board[nextSquareX][y] == null && board[targetX][y] == null;

		return columnIsSame && targetIs2Away && pathIsEmpty;
	}
	
	/**
	 * Method that determines if move is 1 square forward for this pawn
	 * @param targetIsNextSquare Flag to check if target is in the next square from this pawn
	 * @return true|false
	 */
	private boolean move1Square(boolean targetIsNextSquare) {
		// Move is one square forward, target square needs to be empty
		boolean targetIsEmpty = target == null;
		boolean columnIsSame = y == targetY;
		return columnIsSame && targetIsNextSquare && targetIsEmpty;
	}

	/**
	 * Method used to determine if move is going to capture an enemy piece
	 * @param isRightDiagonal Flag to check if move is right diagonal
	 * @param isLeftDiagonal Flag to check if move is left diagonal
	 * @return true|false
	 */
	private boolean captureEnemyPiece(boolean isRightDiagonal, boolean isLeftDiagonal) {
		// Move is diagonal 1 square to left or right and "target" is not null
		// meaning, pawn is capturing an enemy piece
		return (isRightDiagonal || isLeftDiagonal) && target != null;
	}

	/**
	 * Method used to determine if move is going to capture enemy enpassant pawn
	 * @param isRightDiagonal Flag to check if move is right diagonal
	 * @param isLeftDiagonal Flag to check if move is left diagonal
	 * @return true|false
	 */
	private boolean captureEnpassantEnemyPawn(boolean isRightDiagonal, boolean isLeftDiagonal) {
		if (target != null || (!isRightDiagonal && !isLeftDiagonal)) { return false; }
		Board b = engine.getBoard();
		Piece[][] board = b.getBoard();
		Piece adjacentPiece;

		if (isWhite) {
			// If this pawn is not fifth rank return false
			if (x != 3) { return false; }
			adjacentPiece = isRightDiagonal ? board[x][y+1] : board[x][y-1];
			// If adjacent piece is not pawn return false
			if (!engine.getVals().isPawn(adjacentPiece)) { return false; }
			enpassantPawn = (Pawn)adjacentPiece;

			return enpassantPawn.isBlack() && enpassantPawn.justMoved2Squares();
		} else {
			// If this pawn is not fifth rank return false
			if (x != 4) { return false; }
			adjacentPiece = isRightDiagonal ? board[x][y-1] : board[x][y+1];
			// If adjacent piece is not pawn return false
			if (!engine.getVals().isPawn(adjacentPiece)) { return false; }
			enpassantPawn = (Pawn)adjacentPiece;

			return enpassantPawn.isWhite() && enpassantPawn.justMoved2Squares();
		}
	}	
}
