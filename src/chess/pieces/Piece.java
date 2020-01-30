package chess.pieces;

/**
 * Rutgers New Brunswick
 * CSC-213 (Software Methodology)
 *
 * @author Danilo Navas (den34)
 * @version 1.0
 */
public interface Piece {
	/**
	 * Method that outputs the name of the piece to standard output
	 */
	void draw();

	/**
	 * @return The rank of the piece
	 */
	int getX();

	/**
	 * @return The file of the piece
	 */
	int getY();

	/**
	 * Determines if piece is black
	 * @return true|false
	 */
	boolean isBlack();

	/**
	 * Determines if piece is white
	 * @return true|false
	 */
	boolean isWhite();

	/**
	 * Updates the position of the piece with target position
	 */
	void updatePosition();

	/**
	 * Rollsback position of the piece to previous position
	 */
	void rollbackPosition();

	/**
	 * Method used to determine if piece is the king
	 * @return true|false
	 */
	boolean isKing();

	/**
	 * Method used to determine if piece has moved
	 * @return true|false
	 */
	boolean hasMoved();

	/**
	 * Method that updates the "hasMoved" flag to true
	 */
	void moved();

	/**
	 * Method to determine if next move for piece is legal
	 * @param target The target piece that this piece will replace
	 * @param targetX The rank of the target piece
	 * @param targetY The file of the target piece
	 * @return true|false
	 */
	boolean isLegalMove(Piece target, int targetX, int targetY);
}
