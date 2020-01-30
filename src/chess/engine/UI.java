package chess.engine;

import java.util.Scanner;
import chess.pieces.Piece;

/**
 * Rutgers New Brunswick
 * CSC-213 (Software Methodology)
 *
 * @author Danilo Navas (den34)
 * @version 1.0
 */
public class UI {
	/**
	 * Holds the instance of Engine
	 */
	Engine engine;

	/**
	 * Java utility to read user input
	 */
	Scanner reader;

	/**
	 * Holds instance of Validations
	 */
	Validations vals;

	/**
	 * 2D Piece array that represents the chess board with all pieces in it
	 */
	Piece[][] board;
	
	/**
	 * Constructor
	 * @param board The instance of Board
	 * @param vals The instance of Validations
	 * @param e The instance of Engine
	 */
	public UI(Piece[][] board, Validations vals, Engine e) {
		this.reader = new Scanner(System.in);
		this.board = board;
		this.vals = vals;
		this.engine = e;
	}

	/**
	 * Method that draws the board to the screen at the beginning of game and
	 * after every move.
	 */
	public void drawBoard() {
		for (int i = 0; i < board.length; ++i) {
			for (int j = 0; j < board[i].length; ++j) {
				if (vals.isEmptySquare(board[i][j])) {
					drawEmptySquare(i, j);
				} else {
					board[i][j].draw();
				}
				System.out.print(" ");
			}
			drawYLabel(i);
			System.out.println();
		}
		drawXLabels();
	}

	/**
	 * Method that draws the respective character for an empty square on the board
	 * @param i The row number
	 * @param j The column number
	 */
	private void drawEmptySquare(int i, int j) {
		String r = (i + j) % 2 == 0 ? "  " : "##";
		System.out.print(r);
	}
	
	/**
	 * Draws the respective rank label to the right of row
	 * @param i The row number used to draw label
	 */
	private void drawYLabel(int i) {
		System.out.print(8 - i);
	}

	/**
	 * Method that draws the file labels under the board
	 */
	private void drawXLabels() {
		System.out.print(" a");
		System.out.print("  b");
		System.out.print("  c");
		System.out.print("  d");
		System.out.print("  e");
		System.out.print("  f");
		System.out.print("  g");
		System.out.print("  h");
	}
	
	/**
	 * Method that reads the user input for next move
	 * @param whiteTurn Flag to determine if it's white's turn
	 * @return String that contains the user input
	 */
	public String getNextMove(boolean whiteTurn) {
		System.out.println();
		System.out.println();
		if (engine.whiteKingInCheck || engine.blackKingInCheck) {
			System.out.println("Check");
		}
		if (whiteTurn) {
			System.out.print("White's move: ");
		} else {
			System.out.print("Black's move: ");
		}
		String s = reader.nextLine();
		s = s.replaceAll("\\s+", "");
		System.out.println();

		return s;
	}
	
	/**
	 * Method that outputs the outcome of the game at the end
	 */
	public void drawOutcome() {
		System.out.println();
		if (engine.whiteWinsByCheckmate) {
			System.out.println();
			System.out.println("Checkmate");
			System.out.println("White wins");
		} else if (engine.blackWinsByCheckmate) {
			System.out.println();
			System.out.println("Checkmate");
			System.out.println("Black wins");
		} else if (engine.whiteResigned) {
			System.out.println("Black wins");
		} else if (engine.blackResigned) {
			System.out.println("White wins");
		} else if (engine.whiteAcceptedDraw || engine.blackAcceptedDraw) {
			System.out.println("Draw");
		}
	}
}
