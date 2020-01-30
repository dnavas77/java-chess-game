package chess.engine;

import chess.pieces.King;
import chess.pieces.Pawn;
import chess.pieces.Piece;

/**
 * Rutgers New Brunswick
 * CSC-213 (Software Methodology)
 *
 * @author Danilo Navas (den34)
 * @version 1.0
 */
public class Validations {
	/**
	 * String that holds the only valid letters for pawn promotion
	 */
	private static final String validPromotionLetters = "RNBQK";

	/**
	 * Field that holds the instance of Engine
	 */
	Engine engine;
	
	/**
	 * Field that holds the instance of Board 
	 */
	Board board;

	/**
	 * String field that holds the source position from user input
	 */
	String sourcePos;

	/**
	 * String field that holds the target position from user input
	 */
	String targetPos;
	
	/**
	 * Holds the instance of the source piece
	 */
	Piece sourcePiece;

	/**
	 * Holds the instance of the target piece
	 */
	Piece targetPiece;
	
	/**
	 * Constructor
	 * @param e The instance of the Engine class
	 * @param board The instance of the Board class
	 */
	public Validations(Engine e, Board board) {
		this.engine = e;
		this.board = board;
	}

	/**
	 * Method used to determine if "p" is empty
	 * @param p The Piece to check
	 * @return true|false
	 */
	public boolean isEmptySquare(Piece p) {
		return p == null;
	}
	
	/**
	 * Method that determines if move is legal within the rules of chess
	 * @param whiteTurn Value to determine if it's white's turn
	 * @param source The source piece
	 * @param target The target piece
	 * @param targetX Rank of the target piece
	 * @param targetY File of the target piece
	 * @return true|false
	 */
	public boolean isLegalMove(
		boolean whiteTurn,
		Piece source,
		Piece target,
		int targetX,
		int targetY
	) {
		if (source == null) { return false; }
		if (whiteTurn) {
			if (source.isBlack() || (target != null && target.isWhite())) { 
				return false; 
			}
		} else {
			if (source.isWhite() || (target != null && target.isBlack())) { 
				return false; 
			}
		}
		int _targetX = targetX != -1 ? targetX : Board.rankNumbers.indexOf(targetPos.charAt(1));
		int _targetY = targetY != -1 ? targetY : Board.fileLetters.indexOf(targetPos.charAt(0));
		// Check if move is legal for Piece
		if (!source.isLegalMove(target, _targetX, _targetY)) {
			return false;
		}
		// 1. Check if this move puts king in check.
		// 2. This also makes sure the move can't be made if the King is in check 
		// this won't save him by capturing or blocking the threatening piece
		if (moveWouldPutKingInCheck(source, _targetX, _targetY)) {
			return false;
		}
		return true;
	}

	/**
	 * Method that determines if player's move would put his king in check
	 * @param source The source piece
	 * @param targetX Rank of the target
	 * @param targetY File of the target
	 * @return true|false
	 */
	private boolean moveWouldPutKingInCheck(Piece source, int targetX, int targetY) {
		int x = source.getX();
		int y = source.getY();
		boolean r = false;
		Piece[][] b = board.board;
		King king;
		
		// Move piece temporarily to see if it puts the king in check
		Piece backup = b[targetX][targetY];
		b[x][y] = null;
		b[targetX][targetY] = source;
		source.updatePosition();
		
		for (Piece[] ps : b) {
			for (Piece p : ps) {
				if (p == null) {
					continue;
				}
				if (source.isWhite()) {
					king = (King)board.getWhiteKing();
					if (p.isBlack() && p.isLegalMove(king, king.getX(), king.getY())) {
						r = true; break;
					}
				} else {
					king = (King)board.getBlackKing();
					if (p.isWhite() && p.isLegalMove(king, king.getX(), king.getY())) {
						r = true; break;
					}
				}
			}
		}
		// Undo moving the piece
		b[targetX][targetY] = backup;
		b[x][y] = source;
		source.rollbackPosition();
		
		return r;
	}
		
	/**
	 * Method that determines if user input is valid
	 * @param input Holds the user input
	 * @return true|false
	 */
	public boolean isValidInput(String input) {
		int inputLength = input.length();
		if (inputLength < 4) {
			return false;
		}
		if (inputLength > 4 && !input.contains("draw?") && !isPawnPromotion(input)) {
			return false;
		}
		sourcePos = input.substring(0, 2);
		targetPos = input.substring(2, 4);
		if (
			sourceAndTargetAreValid(Board.fileLetters, Board.rankNumbers) && 
			!sourcePos.equals(targetPos)
		) {
			return true;
		}
		return false;
	}
	
	/**
	 * Method that determines if source and target position from user input are valid
	 * @param fileLetters The valid file letters
	 * @param rankNumbers The valid rank numbers
	 * @return true|false
	 */
	public boolean sourceAndTargetAreValid(String fileLetters, String rankNumbers) {
		char sFile = sourcePos.charAt(0);
		char sRank = sourcePos.charAt(1);
		char tFile = targetPos.charAt(0);
		char tRank = targetPos.charAt(1);	
		if (
			fileLetters.indexOf(sFile) > -1 &&
			fileLetters.indexOf(tFile) > -1 &&
			rankNumbers.indexOf(sRank) > -1 &&
			rankNumbers.indexOf(tRank) > -1
		) {
			return true;
		}	
		return false;
	}
	
	/**
	 * Method that determines if user input includes pawn promotion letter
	 * @param input The user input string
	 * @return true|false
	 */
	private boolean isPawnPromotion(String input) {
		for (int i = 0; i < input.length(); ++i) {
			char c = input.charAt(i);
			if (validPromotionLetters.indexOf(c) > -1) {
				engine.promotionLetter = c;
				return true;
			}
		}
		return false;
	}

	/**
	 * Method that determines if piece "p" is a Pawn
	 * @param p The piece to check
	 * @return true|false
	 */
	public boolean isPawn(Piece p) {
		return p != null && p.getClass().toString().contains("Pawn");
	}

	/**
	 * Method that determines if piece "p" is a Knight
	 * @param p The piece to check
	 * @return true|false
	 */
	public boolean isKnight(Piece p) {
		return p != null && p.getClass().toString().contains("Knight");
	}

	/**
	 * Method that determines if pawn can be promoted by checking it reached eight rank
	 * @param pawn The pawn to check
	 * @return true|false
	 */
	public boolean canPawnBePromoted(Piece pawn) {
		Pawn p = (Pawn)pawn;
		if (pawn.isWhite()) {
			return p.getX() == 0;
		} else {
			return p.getX() == 7;
		}
	}
}
