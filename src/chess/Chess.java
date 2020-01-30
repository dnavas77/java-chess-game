package chess;

import chess.engine.Engine;

/**
 * Rutgers New Brunswick
 * CSC-213 (Software Methodology)
 *
 * @author Danilo Navas (den34)
 * @version 1.0
 */
public class Chess {

	/**
	 * Main method, chess game starts here
	 * @param args The command line arguments passed to main
	 */
	public static void main(String[] args) {
		new Engine().start();
	}

}
