package chess.engine;

import chess.pieces.*;

/**
 * Rutgers New Brunswick
 * CSC-213 (Software Methodology)
 *
 * @author Danilo Navas (den34)
 * @version 1.0
 */
public class Board {
	/**
	 * Holds instance of the Engine class
	 */
	Engine engine;

	/**
	 * Holds all valid file letters
	 */
	public static final String fileLetters = "abcdefgh";

	/**
	 * Holds all valid rank numbers
	 */
	public static final String rankNumbers = "87654321";

	/**
	 * 2D array that holds all pieces for the game
	 */
	Piece[][] board;

	/**
	 * Constructor
	 * @param e Instance of Engine
	 */
	public Board(Engine e) {
		this.engine = e;
		board = new Piece[8][8];

		for (int i = 0; i < board.length; ++i) {
			if (i == 0) {
				// Black pieces King row
				board[i][0] = new Rook  ("bR", false, true, i, 0, e);
				board[i][1] = new Knight("bN", false, true, i, 1, e);
				board[i][2] = new Bishop("bB", false, true, i, 2, e);
				board[i][3] = new Queen ("bQ", false, true, i, 3, e);
				board[i][4] = new King  ("bK", false, true, i, 4, e);
				board[i][5] = new Bishop("bB", false, true, i, 5, e);
				board[i][6] = new Knight("bN", false, true, i, 6, e);
				board[i][7] = new Rook  ("bR", false, true, i, 7, e);
			} else if (i == 1) {
				// Black pieces pawns row
				board[i][0] = new Pawn("bp", false, true, i, 0, e);
				board[i][1] = new Pawn("bp", false, true, i, 1, e);
				board[i][2] = new Pawn("bp", false, true, i, 2, e);
				board[i][3] = new Pawn("bp", false, true, i, 3, e);
				board[i][4] = new Pawn("bp", false, true, i, 4, e);
				board[i][5] = new Pawn("bp", false, true, i, 5, e);
				board[i][6] = new Pawn("bp", false, true, i, 6, e);
				board[i][7] = new Pawn("bp", false, true, i, 7, e);
			} else if (i == 6) {
				// White pieces pawns row
				board[i][0] = new Pawn("wp", true, false, i, 0, e);
				board[i][1] = new Pawn("wp", true, false, i, 1, e);
				board[i][2] = new Pawn("wp", true, false, i, 2, e);
				board[i][3] = new Pawn("wp", true, false, i, 3, e);
				board[i][4] = new Pawn("wp", true, false, i, 4, e);
				board[i][5] = new Pawn("wp", true, false, i, 5, e);
				board[i][6] = new Pawn("wp", true, false, i, 6, e);
				board[i][7] = new Pawn("wp", true, false, i, 7, e);
			} else if (i == 7) {
				// White pieces King row
				board[i][0] = new Rook  ("wR", true, false, i, 0, e);
				board[i][1] = new Knight("wN", true, false, i, 1, e);
				board[i][2] = new Bishop("wB", true, false, i, 2, e);
				board[i][3] = new Queen ("wQ", true, false, i, 3, e);
				board[i][4] = new King  ("wK", true, false, i, 4, e);
				board[i][5] = new Bishop("wB", true, false, i, 5, e);
				board[i][6] = new Knight("wN", true, false, i, 6, e);
				board[i][7] = new Rook  ("wR", true, false, i, 7, e);
			}
		}
	}	
	
	/**
	 * @param fileRank 2 letter string that contains "[file][rank] respectively
	 * @return The piece found at "fileRank"
	 */
	public Piece getPiece(String fileRank) {
		char f = fileRank.charAt(0);
		char r = fileRank.charAt(1);
		return board[mapRankToX(r)][mapFileToY(f)];
	}
	
	/**
	 * @return The 2D board array
	 */
	public Piece[][] getBoard() {
		return board;
	}
	
	/**
	 * @param source The piece that needs to be moved
	 * @param sourcePos "[file][rank]" string that contains the source position
	 * @param targetPos "[file][rank]" string that contains the target position
	 */
	public void movePiece(Piece source, String sourcePos, String targetPos) {
		replacePiece(targetPos, source);
		replacePiece(sourcePos, null);
		source.updatePosition();

		// Check if source is pawn and if it just captured enpassant pawn
		if (engine.getVals().isPawn(source)) {
			Pawn enpassantPawn = ((Pawn)source).getEnpassantPawn();
			if (enpassantPawn != null) {
				board[enpassantPawn.getX()][enpassantPawn.getY()] = null;
			}
		}	
		// Check if source is King and it's castling
		if (source.isKing()) {
			Piece rook = ((King)source).getRook();
			if (rook != null) {
				// Keep in mind King was already moved above
				int x = source.getX();
				int y = source.getY();
				if (y == 6) {
					board[x][7] = null;
					board[x][5] = rook;
				} else if (y == 2) {
					board[x][0] = null;
					board[x][3] = rook;
				}
			}
		}
	}
	
	/**
	 * @param fileRank The position of the square on the board to replace
	 * @param p The piece that will be placed at "fileRank"
	 */
	private void replacePiece(String fileRank, Piece p) {
		char f = fileRank.charAt(0);
		char r = fileRank.charAt(1);
		board[mapRankToX(r)][mapFileToY(f)] = p;
	}
	
	/**
	 * @param r The character that represents "rank" that needs to be mapped to int
	 * @return The integer resulting from the mapping of "r" to int
	 */
	private int mapRankToX(char r) {
		int x = rankNumbers.indexOf(r);
		return x;
	}

	/**
	 * @param f The character that represents "file" that needs to be mapped to int
	 * @return The integer resulting from the mapping of "f" to int
	 */
	private int mapFileToY(char f) {
		int y = fileLetters.indexOf(f);
		return y;
	}
	
	/**
	 * @return The white king instance
	 */
	public Piece getWhiteKing() {
		Piece k = null;
		for (Piece[] ps : board) {
			for (Piece p : ps) {
				if (p != null && p.isKing() && p.isWhite()) {
					return p;
				}
			}
		}
		return k;
	}

	/**
	 * @return The black king instance
	 */
	public Piece getBlackKing() {
		Piece k = null;
		for (Piece[] ps : board) {
			for (Piece p : ps) {
				if (p != null && p.isKing() && p.isBlack()) {
					return p;
				}
			}
		}
		return k;
	}
}
