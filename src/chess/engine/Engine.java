package chess.engine;

import java.util.List;

import chess.pieces.Bishop;
import chess.pieces.Coord;
import chess.pieces.King;
import chess.pieces.Knight;
import chess.pieces.Pawn;
import chess.pieces.Piece;
import chess.pieces.Queen;
import chess.pieces.Rook;
import chess.pieces.SharedRules;

/**
 * Rutgers New Brunswick
 * CSC-213 (Software Methodology)
 *
 * @author Danilo Navas (den34)
 * @version 1.0
 */
public class Engine {
	/**
	 * Holds the instance of Validations
	 */
	Validations vals;

	/**
	 * Holds the instance of Board
	 */
	Board board;

	/**
	 * Holds the instance of UI
	 */
	UI ui;
	
	/**
	 * Holds the letter that represents the Piece the pawn must be promoted to
	 * 'x' means default promotion (i.e. Queen)
	 */
	char promotionLetter = 'x';
	
	/**
	 * Value that represents if it's white's turn or not
	 */
	boolean whiteTurn = true;
	
	/**
	 * Value that represents if white asked for draw
	 */
	boolean whiteAskedForDraw;

	/**
	 * Value that represents if white accepted draw request from black
	 */
	boolean whiteAcceptedDraw;

	/**
	 * Value that can be used to determine if white resigned
	 */
	boolean whiteResigned;

	/**
	 * Value that can be used to determine if white king is in check
	 */
	boolean whiteKingInCheck;

	/**
	 * Value that represents if white wins by checkmate
	 */
	boolean whiteWinsByCheckmate;

	/**
	 * Value that represents if black asked for draw
	 */
	boolean blackAskedForDraw;

	/**
	 * Value that represents if black accepted draw request from white
	 */
	boolean blackAcceptedDraw;

	/**
	 * Value that can be used to determine if black resigned
	 */
	boolean blackResigned;

	/**
	 * Value that can be used to determine if black king is in check
	 */
	boolean blackKingInCheck;

	/**
	 * Value that represents if black wins by checkmate
	 */
	boolean blackWinsByCheckmate;
	
	/**
	 * Holds instance of white Piece attacking black King
	 */
	Piece whiteAttacker;

	/**
	 * Holds instance of black Piece attacking white King
	 */
	Piece blackAttacker;
	
	/**
	 * Constructor
	 */
	public Engine() {
		this.board = new Board(this);
		this.vals = new Validations(this, board);
		this.ui = new UI(board.board, vals, this);
	}
	
	/**
	 * Game execution begins here
	 */
	public void start() {
		ui.drawBoard();
		
		// Game loop
		while (true) {
			promotionLetter = 'x';
			boolean terminate;
			String input, sourcePos, targetPos;
			Piece source, target;

			// Check if checkmate
			if (whiteTurn) {
				if (whiteKingInCheck && !canKingBeSaved()) {
					blackWinsByCheckmate = true;
					break;
				}
			} else {
				if (blackKingInCheck && !canKingBeSaved()) {
					whiteWinsByCheckmate = true;
					break;
				}
			}

			// TODO check if stalemate:
			
			// Clear "justMoved2Squares" on all opposite pawns
			clearPawnsMove2SquaresFlag();

			// Check if input is valid
			input = ui.getNextMove(whiteTurn);

			// Check if player resigned
			terminate = didPlayerResign(input);
			if (terminate) { break; }

			// Check if player accepted draw
			terminate = didPlayerAcceptDraw(input);
			if (terminate) { break; }	
				
			// Check if input is valid
			if (vals.isValidInput(input)) {
				// Check if Legal move for source
				sourcePos = input.substring(0, 2);
				targetPos = input.substring(2, 4);
				source = board.getPiece(sourcePos);
				target = board.getPiece(targetPos);
				if (!vals.isLegalMove(whiteTurn, source, target, -1, -1)) {
					System.out.print("Illegal move, try again");
					continue;
				}
			} else {
				System.out.print("Illegal move, try again");
				continue;
			}

			// Perform move and terminate program if winner exists
			terminate = performMove(source, sourcePos, targetPos, input);
			if (terminate) { break; }	

			// Process draw request and reset flags if needed
			checkIfPlayerOfferedDraw(input);

			ui.drawBoard();
			whiteTurn = !whiteTurn;
		}
		// Game ended, display outcome (draw or winner)
		ui.drawOutcome();
	}
	
	/**
	 * Method that determines if any player resigned
	 * @param input The input from user
	 * @return boolean value used to determine if player resigned
	 */
	private boolean didPlayerResign(String input) {
		if (input.equals("resign")) {
			if (whiteTurn) {
				whiteResigned = true;
			} else {
				blackResigned = true;
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Method that determines if any player accepted draw request from opponent
	 * @param input The input from user
	 * @return boolean value used to determine if player accepted draw
	 */
	private boolean didPlayerAcceptDraw(String input) {
		return whiteTurn ? didWhiteAccept(input) : didBlackAccept(input);
	}
	
	/**
	 * Value used to determine if white player accepted draw
	 * @param input The input from user
	 * @return true|false
	 */
	private boolean didWhiteAccept(String input) {
		if (blackAskedForDraw && input.equals("draw")) {
			whiteAcceptedDraw = true;
			return true;
		}
		return false;
	}

	/**
	 * Method that determines if black player accepted draw
	 * @param input The user input
	 * @return true|false
	 */
	private boolean didBlackAccept(String input) {
		if (whiteAskedForDraw && input.equals("draw")) {
			blackAcceptedDraw = true;
			return true;
		}
		return false;
	}
		
	/**
	 * Method that performs the moving of the piece. Also checks if pawn needs to be promoted
	 * and processes if this move puts opponent king in check
	 * @param source The source piece
	 * @param sourcePos "fileRank" source position
	 * @param targetPos "fileRank" target position
	 * @param input The user input
	 * @return true|false
	 */
	private boolean performMove(Piece source, String sourcePos, String targetPos, String input) {
		blackAttacker = null;
		whiteAttacker = null;
		blackKingInCheck = false;
		whiteKingInCheck = false;

		// Move piece at "sourcePos" to "targetPos"
		// the move was determined to be legal, simple as that
		board.movePiece(source, sourcePos, targetPos);	
		source.moved();
			
		// Check if Piece is pawn and has a promoteMe flag and has what piece
		// it wants to be promoted to
		if (vals.isPawn(source) && vals.canPawnBePromoted(source)) {
			source = promotePawn(source);
		}
		
		// Check if this move puts the opponent king in check, if so
		// set proper flags
		if (source.isWhite()) {
			King bK = (King)board.getBlackKing();
			if (source.isLegalMove(bK, bK.getX(), bK.getY())) {
				whiteAttacker = source;
				blackKingInCheck = true;
			}
		} else {
			King wK = (King)board.getWhiteKing();
			if (source.isLegalMove(wK, wK.getX(), wK.getY())) {
				blackAttacker = source;
				whiteKingInCheck = true;
			}
		}
		return false;
	}

	/**
	 * Method that determines if king in check can be saved
	 * Check if King has valid moves (including capturing attacker)
	 * Check if any piece can capture attacking piece
	 * Check if any piece can block the attack
	 * @return Value that can be used to see if king can be saved
	 */
	private boolean canKingBeSaved() {
		if (kingHasValidMove()) {
			return true;
		}
		if (canAnyPieceCaptureAttacker()) {
			return true;
		}
		if (canAnyPieceBlockAttacker()) {
			return true;
		}
		return false;
	}
	
	/**
	 * Method that determines if any ally piece can block opponent's attack to king
	 * @return true|false
	 * 
	 */
	private boolean canAnyPieceBlockAttacker() {
		Piece K, attacker;
		if (whiteTurn) {
			K = board.getWhiteKing();
			attacker = blackAttacker;
		} else {
			K = board.getBlackKing();
			attacker = whiteAttacker;
		}
		// If attacker is Knight or Pawn it can't be blocked
		if (vals.isPawn(attacker) || vals.isKnight(attacker)) {
			return false;
		}
		// If attacker is right next to king there's no in between
		// squares to check
		if (K.isLegalMove(attacker, attacker.getX(), attacker.getY())) {
			return false;
		}

		// Identify squares between attacker and the king.
		// See if any piece can move to any of those squares to block attack
		Piece[][] b = board.board;
		for (Piece[] ps : b) {
			for (Piece p : ps) {
				if (p == null || p.isKing()) {
					continue;
				}
				List<Coord> path = SharedRules.tracePath(
					attacker.getX(), attacker.getY(), K.getX(), K.getY(), b
				);
				for (Coord c : path) {
					if (vals.isLegalMove(whiteTurn, p, null, c.x, c.y)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * Method that determines if any ally piece can capture opponent that has king in check
	 * @return true|false
	 */
	private boolean canAnyPieceCaptureAttacker() {
		Piece[][] b = board.board;
		for (Piece[] ps : b) {
			for (Piece p : ps) {
				if (p == null || p.isKing()) {
					continue;
				}
				Piece target = whiteTurn ? blackAttacker : whiteAttacker;
				int targetX = target.getX(), targetY = target.getY();
				if (vals.isLegalMove(whiteTurn, p, target, targetX, targetY)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Method that determines if king in check has valid moves
	 * @return true|false
	 */
	private boolean kingHasValidMove() {
		Piece[][] b = board.board;
		int[][] positions = new int[8][2];
		Piece K = whiteTurn ? board.getWhiteKing() : board.getBlackKing();
		int x = K.getX(), y = K.getY();
		// Top left
		positions[0][0] = x - 1;
		positions[0][1] = y - 1;
		// Top
		positions[1][0] = x - 1;
		positions[1][1] = y;
		// Top right
		positions[2][0] = x - 1;
		positions[2][1] = y + 1;
		// Left
		positions[3][0] = x;
		positions[3][1] = y - 1;
		// Right
		positions[4][0] = x;
		positions[4][1] = y + 1;
		// Bottom left
		positions[5][0] = x + 1;
		positions[5][1] = y - 1;
		// Down
		positions[5][0] = x + 1;
		positions[5][1] = y;
		// Bottom right
		positions[5][0] = x + 1;
		positions[5][1] = y + 1;
		for (int[] pair : positions) {
			// If X and Y are out of bounds go to next pair
			if (pair[0] < 0 || pair[0] > 7 || pair[1] < 0 || pair[1] > 7) {
				continue;
			}
			if (vals.isLegalMove(whiteTurn, K, b[pair[0]][pair[1]], pair[0], pair[1])) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Method that returns flag that determines if white king is in check
	 * @return true|false
	 */
	public boolean whiteKingInCheck() {
		return whiteKingInCheck;
	}

	/**
	 * Method that returns flag that determines if black king is in check
	 * @return true|false
	 */
	public boolean blackKingInCheck() {
		return blackKingInCheck;
	}
	
	/**
	 * Method that process input, determines if any player offered a draw
	 * and sets respective flags
	 * @param input The user input
	 */
	private void checkIfPlayerOfferedDraw(String input) {
		if (input.contains("draw?")) {
			if (whiteTurn) {
				whiteAskedForDraw = true;
			} else {
				blackAskedForDraw = true;
			}
		} else {
			whiteAskedForDraw = false;
			blackAskedForDraw = false;
		}
	}
	
	/**
	 * @param pawn The instance of the pawn that needs to be promoted
	 * @return the new Piece based on the request from user input
	 */
	private Piece promotePawn(Piece pawn) {
		String name;
		boolean isWhite = pawn.isWhite();
		boolean isBlack = pawn.isBlack();
		Pawn p = (Pawn)pawn;
		int _x = p.getX(), _y = p.getY();
		Piece[][] b = board.board;

		if (promotionLetter == 'R') {
			name = p.isWhite() ? "wR" : "bR";
			b[_x][_y] = new Rook(name, isWhite, isBlack, _x, _y, this);
		} else if (promotionLetter == 'N') {
			name = p.isWhite() ? "wN" : "bN";
			b[_x][_y] = new Knight(name, isWhite, isBlack, _x, _y, this);
		} else if (promotionLetter == 'B') {
			name = p.isWhite() ? "wB" : "bB";
			b[_x][_y] = new Bishop(name, isWhite, isBlack, _x, _y, this);
		} else if (promotionLetter == 'Q') {
			name = p.isWhite() ? "wQ" : "bQ";
			b[_x][_y] = new Queen(name, isWhite, isBlack, _x, _y, this);
		} else {
			name = p.isWhite() ? "wQ" : "bQ";
			b[_x][_y] = new Queen(name, isWhite, isBlack, _x, _y, this);
		}
		return b[_x][_y];
	}

	/**
	 * Method that clears flag on all pawns. This flag is used when processing
	 * enpassant checks
	 */
	private void clearPawnsMove2SquaresFlag() {
		Piece[][] b = board.board;
		for (Piece[] ps : b) {
			for (Piece p : ps) {
				if (p == null || !vals.isPawn(p)) {
					continue;
				}
				Pawn _p = (Pawn)p;
				if (whiteTurn && _p.isWhite()) {
					_p.clearMove2SquaresFlag();
				} else if (!whiteTurn && _p.isBlack()) {
					_p.clearMove2SquaresFlag();
				}
			}
		}//end:for
	}

	/**
	 * @return The local instance of Board
	 */
	public Board getBoard() {
		return board;
	}

	/**
	 * @return The local instance of Validations
	 */
	public Validations getVals() {
		return vals;
	}	
}
