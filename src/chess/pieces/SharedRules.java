package chess.pieces;

import java.util.ArrayList;
import java.util.List;

/**
 * Rutgers New Brunswick
 * CSC-213 (Software Methodology)
 *
 * @author Danilo Navas (den34)
 * @version 1.0
 */
public class SharedRules {
	/**
	 * Row position
	 */
	private static int x;

	/**
	 * Column position
	 */
	private static int y;

	/**
	 * Target piece row
	 */
	private static int targetX;

	/**
	 * Target piece column
	 */
	private static int targetY;

	/**
	 * 2D array of Piece types that represents the chess board
	 */
	private static Piece[][] board;

	/**
	 * Value to determine if path must be traced. Used in checkmate checks.
	 */
	private static boolean tracePath = false;

	/**
	 * The list the holds the coordinates of the squares between the source piece
	 * and the target piece (normally used in checkmate rules)
	 */
	public static List<Coord> path = new ArrayList<Coord>();
	
	/**
	 * Method that determines if move is horizontal or vertical
	 * @param x The row position of the source piece
	 * @param y The column position of the source piece
	 * @param targetX The row position of the target piece
	 * @param targetY The column position of the target piece
	 * @param board The 2D array that holds the chess board and all pieces
	 * @param tracePath Flag used to determine if squares in between should be traced
	 * @return true|false
	 */
	static public boolean isVerticalOrHorizontalMove(
		int x, int y, int targetX, int targetY, Piece[][] board, boolean tracePath
	) {
		if (tracePath) {
			path.clear();
		}
		SharedRules.tracePath = tracePath;
		SharedRules.x = x;
		SharedRules.y = y;
		SharedRules.targetX = targetX;
		SharedRules.targetY = targetY;
		SharedRules.board = board;
		if (x == targetX && isHorizontalPathEmpty()) {
			// Horizontal move (same row, different column)
			return true;
		} else if (y == targetY && isVerticalPathEmpty()) {
			// Vertical move (same column, different row)
			return true;
		}
		return false;
	}

	/**
	 * Method used to determine if horizontal path between source and target is empty
	 * @return true|false
	 */
	static private boolean isHorizontalPathEmpty() {
		// Check if all squares in between source and target are empty
		int i;
		if (y - targetY == -1 || y - targetY == 1) {
			// Move is 1 square, no need to check if empty since doesn't matter
			return true;
		}
		if (y - targetY < -1) {
			// Moving to the right for white (left for black)
			i = y + 1;
			while (i < targetY) {
				if (board[x][i] != null) {
					if (tracePath) {
						path.clear();
					}
					return false;
				}
				if (tracePath) {
					path.add(new Coord(x, i));
				}
				++i;
			}
			return true;
		} else if (y - targetY > 1) {
			// moving to the left for white (right for black)
			i = y - 1;
			while (i > targetY) {
				if (board[x][i] != null) {
					if (tracePath) {
						path.clear();
					}
					return false;
				}
				if (tracePath) {
					path.add(new Coord(x, i));
				}
				--i;
			}
			return true;
		}
		return false;
	}

	/**
	 * Method used to determine if vertical path between source and target is empty
	 * @return true|false
	 */
	static private boolean isVerticalPathEmpty() {
		// Check if all squares in between source and target are empty
		int i;
		if (x - targetX == -1 || x - targetX == 1) {
			// Move is 1 square, no need to check if empty since doesn't matter
			return true;
		}
		if (x - targetX < -1) {
			// Moving down (for white), up (for black)
			i = x + 1;
			while (i < targetX) {
				if (board[i][y] != null) {
					if (tracePath) {
						path.clear();
					}
					return false;
				}
				if (tracePath) {
					path.add(new Coord(i, y));
				}
				++i;
			}
			return true;
		} else if (x - targetX > 1) {
			// Moving up (for white), down (for black)
			i = x - 1;
			while (i > targetX) {
				if (board[i][y] != null) {
					if (tracePath) {
						path.clear();
					}
					return false;
				}
				if (tracePath) {
					path.add(new Coord(i, y));
				}
				--i;
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Method used to determine if source move is diagonal in any direction
	 * @param x The row of the source piece
	 * @param y The column of the source piece
	 * @param targetX The row of the target piece
	 * @param targetY The column of the target piece
	 * @param board The 2D array that holds the chess board and all pieces
	 * @param tracePath Flag used to determine if squares in between should be traced
	 * @return true|false
	 */
	static public boolean isDiagonalMove(
		int x, int y, int targetX, int targetY, Piece[][] board, boolean tracePath
	) {
		if (tracePath) {
			path.clear();
		}
		SharedRules.tracePath = tracePath;
		SharedRules.x = x;
		SharedRules.y = y;
		SharedRules.targetX = targetX;
		SharedRules.targetY = targetY;
		SharedRules.board = board;
		if (
			isTopRightDiagonal(board) ||
			isTopLeftDiagonal(board) ||
			isBottomRightDiagonal(board) ||
			isBottomLeftDiagonal(board)
		) {
			return true;
		}
		return false;
	}

	/**
	 * Method used to decide if move is top right diagonal
	 * @param board The 2D array that holds the chess board and all pieces
	 * @return true|false
	 */
	static private boolean isTopRightDiagonal(Piece[][] board) {
		boolean pieceInPath = false;
		boolean targetIsHere = false;
		int _x = x - 1;
		int _y = y + 1;
		while (_x >= 0 && _y <= 7) {
			if (_x == targetX && _y == targetY) {
				targetIsHere = true;
				break;
			}
			if (board[_x][_y] != null) {
				pieceInPath = true;
			} else {
				if (tracePath) {
					path.add(new Coord(_x, _y));
				}
			}
			--_x;
			++_y;
		}
		boolean r = targetIsHere && !pieceInPath;
		if (!r && tracePath) {
			path.clear();
		}
		return r;
	}
	
	/**
	 * Method used to decide if move is top left diagonal
	 * @param board The 2D array that holds the chess board and all pieces
	 * @return true|false
	 */
	static private boolean isTopLeftDiagonal(Piece[][] board) {
		boolean pieceInPath = false;
		boolean targetIsHere = false;
		int _x = x - 1;
		int _y = y - 1;
		while (_x >= 0 && _y >= 0) {
			if (_x == targetX && _y == targetY) {
				targetIsHere = true;
				break;
			}
			if (board[_x][_y] != null) {
				pieceInPath = true;
			} else {
				if (tracePath) {
					path.add(new Coord(_x, _y));
				}
			}
			--_x;
			--_y;
		}
		boolean r = targetIsHere && !pieceInPath;
		if (!r && tracePath) {
			path.clear();
		}
		return r;
	}
	
	/**
	 * Method used to decide if move is bottom right diagonal
	 * @param board The 2D array that holds the chess board and all pieces
	 * @return true|false
	 */
	static private boolean isBottomRightDiagonal(Piece[][] board) {
		boolean pieceInPath = false;
		boolean targetIsHere = false;
		int _x = x + 1;
		int _y = y + 1;
		while (_x <= 7 && _y <= 7) {
			if (_x == targetX && _y == targetY) {
				targetIsHere = true;
				break;
			}
			if (board[_x][_y] != null) {
				pieceInPath = true;
			} else {
				if (tracePath) {
					path.add(new Coord(_x, _y));
				}
			}
			++_x;
			++_y;
		}
		boolean r = targetIsHere && !pieceInPath;
		if (!r && tracePath) {
			path.clear();
		}
		return r;
	}

	/**
	 * Method used to decide if move is bottom left diagonal
	 * @param board The 2D array that holds the chess board and all pieces
	 * @return true|false
	 */
	static private boolean isBottomLeftDiagonal(Piece[][] board) {
		boolean pieceInPath = false;
		boolean targetIsHere = false;
		int _x = x + 1;
		int _y = y - 1;
		while (_x <= 7 && _y >= 0 ) {
			if (_x == targetX && _y == targetY) {
				targetIsHere = true;
				break;
			}
			if (board[_x][_y] != null) {
				pieceInPath = true;
			} else {
				if (tracePath) {
					path.add(new Coord(_x, _y));
				}
			}
			++_x;
			--_y;
		}
		boolean r = targetIsHere && !pieceInPath;
		if (!r && tracePath) {
			path.clear();
		}
		return r;
	}
	
	/**
	 * Method that returns the list of coordinates of all squares between source and target
	 * @param x The row of the source piece
	 * @param y The column of the source piece
	 * @param targetX The row of the target piece
	 * @param targetY The column of the target piece
	 * @param board The 2D array that holds the chess board and all pieces
	 * @return List of Coord instances
	 */
	static public List<Coord> tracePath(int x, int y, int targetX, int targetY, Piece[][] board) {
		if (
			SharedRules.isVerticalOrHorizontalMove(x, y, targetX, targetY, board, true) ||
			SharedRules.isDiagonalMove(x, y, targetX, targetY, board, true)
		) {
			return path;
		}
		return null;
	}
}
