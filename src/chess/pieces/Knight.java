package chess.pieces;

import chess.engine.Engine;

/**
 * Rutgers New Brunswick
 * CSC-213 (Software Methodology)
 *
 * @author Danilo Navas (den34)
 * @version 1.0
 */
public class Knight implements Piece {
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
	public Knight(String name, boolean isWhite, boolean isBlack, int x, int y, Engine e) {
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
		// Check if move is legal for Knight
		this.target = target;
		this.targetX = targetX;
		this.targetY = targetY;
		if (
			(targetX == x - 1 && targetY == y - 2) ||
			(targetX == x - 2 && targetY == y - 1) ||
			(targetX == x - 2 && targetY == y + 1) ||
			(targetX == x - 1 && targetY == y + 2) ||
			(targetX == x + 1 && targetY == y + 2) ||
			(targetX == x + 2 && targetY == y + 1) ||
			(targetX == x + 2 && targetY == y - 1) ||
			(targetX == x + 1 && targetY == y - 2)
		) {
			return true;
		}
		return false;
	}
}
