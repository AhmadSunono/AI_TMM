package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class MainController implements Initializable {

	private class GameCell {

		public GameCell(int row, int col) {
			this.row = row;
			this.col = col;
		}

		public GameCell() {
			this.row = -1;
			this.col = -1;
		}

		public int row;

		public int getRow() {
			return row;
		}

		public void setRow(int row) {
			this.row = row;
		}

		public int getCol() {
			return col;
		}

		public void setCol(int col) {
			this.col = col;
		}

		public int col;
	}

	@FXML
	private GridPane grid;

	@FXML
	private Button btn0;

	@FXML
	private Button btn1;

	@FXML
	private Button btn2;

	@FXML
	private Button btn3;

	@FXML
	private Button btn4;

	@FXML
	private Button btn5;

	@FXML
	private Button btn6;

	@FXML
	private Button btn7;

	@FXML
	private Button btn8;

	@FXML
	private Button newGameBtn;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		newGameHandler();

	}

	private class CellClickHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent event) {

			if (gameOver) {

				System.out.println(player);

				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setHeaderText("Player " + (player == 1 ? "ONE" : "TWO") + " already won!");
				alert.show();

				return;
			}

			Button current = (Button) event.getTarget();
			int row = ((GameCell) current.getUserData()).getRow();
			int col = ((GameCell) current.getUserData()).getCol();

			boolean tempMoveFlag = tempMove.getRow() != -1 && tempMove.getCol() != -1;

			// If there was a piece moving //
			if (tempMoveFlag) {

				int tmpRow = tempMove.getRow();
				int tmpCol = tempMove.getCol();

				// If moving a piece in top of another piece
				if (board[row][col] != 0 || diagonalMove(row, col, tmpRow, tmpCol)) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setHeaderText("Invalid Move!!");
					alert.show();

					tempMove.setCol(-1);
					tempMove.setRow(-1);
					buttons[tmpRow][tmpCol].setStyle(player == 1 ? player1Color : player2Color);
					return;
				}

				board[row][col] = player;
				current.setText(player == 1 ? "1" : "2");
				current.setStyle(player == 1 ? player1Color : player2Color);

				board[tmpRow][tmpCol] = 0;
				buttons[tmpRow][tmpCol].setText("");

				tempMove.setCol(-1);
				tempMove.setRow(-1);
//				System.out.println(calculateHueValue(board, player));
				if (checkWin()) {
					handleWin();
					return;
				}

				if (checkBlocked()) {
					handleBlocked();
					return;
				}

				player *= -1;
//				RuleExtractor(board, player);
				if (player == -1) {
//					printBoard(alphaBetaCode(board, depth, -100000, 100000, -1));
					reflectBoard(alphaBetaCode(board, depth, -100000, 100000, -1));
				}

			}

			// Normal Move //
			else {
				if (checkValidMove(row, col)) {

					// If the player is moving his piece //
					if (player == board[row][col]) {
						current.setStyle(tempMoveColor);
						tempMove.setRow(row);
						tempMove.setCol(col);

					} else {

						board[row][col] = player;

						current.setText(player == 1 ? "1" : "2");
						current.setStyle(player == 1 ? player1Color : player2Color);
						// System.out.println(calculateHueValue(board, player));
						if (checkWin()) {
							handleWin();
							return;
						}

						if (checkBlocked()) {
							handleBlocked();
							return;
						}

						decrementPieces();
						player *= -1;
//						RuleExtractor(board, player);
						if (player == -1) {
//							printBoard(alphaBetaCode(board, depth, -100000, 100000, -1));
							reflectBoard(alphaBetaCode(board, depth, -100000, 100000, -1));
						}

					}

				} else {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setHeaderText("Invalid Move!");
					alert.show();
				}
			}

			if (checkWin()) {
				handleWin();
				return;
			}
			if (checkBlocked()) {
				handleBlocked();
				return;
			}
		}

	}

	private void handleBlocked() {
		handleWin();
	}

	private boolean diagonalMove(int row, int col, int tmpRow, int tmpCol) {

		return !(row == tmpRow || col == tmpCol);
	}

	private void handleWin() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setHeaderText("Player " + (player == 1 ? "One" : "Two") + " Won!");
		alert.show();
		gameOver = true;
	}

	private void newGameHandler() {
		board = new int[3][3];
		piecesLeft = 6;
		player = 1;
		buttons = new Button[3][3];
		buttons[0][0] = btn0;
		buttons[0][1] = btn1;
		buttons[0][2] = btn2;
		buttons[1][0] = btn3;
		buttons[1][1] = btn4;
		buttons[1][2] = btn5;
		buttons[2][0] = btn6;
		buttons[2][1] = btn7;
		buttons[2][2] = btn8;

		gameOver = false;

		newGameBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				newGameHandler();

			}
		});

		int count = 0;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				Button current = buttons[i][j];
				current.setId("" + count++);
				current.setUserData(new GameCell(i, j));
				current.setText("");

				buttons[i][j].setOnAction(new CellClickHandler());
			}
		}

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				board[i][j] = 0;
			}
		}
	}

	private void decrementPieces() {
		if (piecesLeft == 0)
			return;
		piecesLeft--;
	}

	private void incrementPieces() {
		if (piecesLeft == 6)
			return;
		piecesLeft++;
	}

	private boolean checkWin() {
		return board[0][0] == player && board[0][1] == player && board[0][2] == player
				|| board[1][0] == player && board[1][1] == player && board[1][2] == player
				|| board[2][0] == player && board[2][1] == player && board[2][2] == player
				|| board[0][0] == player && board[1][0] == player && board[2][0] == player
				|| board[0][1] == player && board[1][1] == player && board[2][1] == player
				|| board[0][2] == player && board[1][2] == player && board[2][2] == player;
	}

	private boolean checkBlocked() {

		if (piecesLeft != 0)
			return false;

		boolean result = false;

		int otherPlayer = player * -1;

		// Block by main diagonal
		if ((board[0][0] == player && board[1][1] == player && board[2][2] == player)
				&& ((board[0][1] == otherPlayer && board[0][2] == otherPlayer && board[1][2] == otherPlayer)
						|| (board[1][0] == otherPlayer && board[2][0] == otherPlayer && board[2][1] == otherPlayer))) {
			return true;
		}

		// Block by aux diagonal
		if ((board[0][2] == player && board[1][1] == player && board[2][0] == player)
				&& ((board[0][0] == otherPlayer && board[0][1] == otherPlayer && board[1][0] == otherPlayer)
						|| (board[1][2] == otherPlayer && board[2][1] == otherPlayer && board[2][2] == otherPlayer))) {
			return true;
		}

		player *= -1;
		otherPlayer *= -1;

		// Block by main diagonal
		if (board[0][0] == player && board[1][1] == player && board[2][2] == player
				&& ((board[0][1] == otherPlayer && board[0][2] == otherPlayer && board[1][2] == otherPlayer)
						|| (board[1][0] == otherPlayer && board[2][0] == otherPlayer && board[2][0] == otherPlayer))) {
			return true;
		}

		// Block by aux diagonal
		if (board[0][2] == player && board[1][1] == player && board[2][0] == player
				&& ((board[0][0] == otherPlayer && board[0][1] == otherPlayer && board[1][0] == otherPlayer)
						|| (board[1][2] == otherPlayer && board[2][1] == otherPlayer && board[2][2] == otherPlayer))) {
			return true;
		}

		player *= -1;

		return result;
	}

	private boolean checkValidMove(int row, int col) {
		if (row < 0 || row > 2)
			return false;
		if (col < 0 || col > 2)
			return false;
		int cellValue = board[row][col];

		if (cellValue == 0 && piecesLeft == 0) {
			return false;
		}
		if (cellValue == 1 && player == -1 || cellValue == -1 && player == 1) {
			return false;
		}

		if (cellValue != 0 && piecesLeft != 0) {
			return false;
		}

		return true;
	}

	public int[][] copy(int[][] src) {
		int tmp[][] = new int[3][3];
		for (int k = 0; k < 3; k++) {
			for (int r = 0; r < 3; r++) {

				tmp[k][r] = src[k][r];

			}

		}
		return tmp;

	}

	public boolean checkMoving(int row1, int col1, int row2, int col2) {
		if (row1 < 0 || row1 > 2)
			return false;
		if (row2 < 0 || row2 > 2)
			return false;
		if (col1 < 0 || col1 > 2)
			return false;
		if (col2 < 0 || col2 > 2)
			return false;
		if (board[row2][col2] != 0)
			return false;
		if (board[row1][col1] != player)
			return false;
		return true;

	}

	public void printBoard(int board[][]) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				System.out.print(board[i][j] + " ");
			}
			System.out.println();
		}
	}

	public ArrayList<int[][]> RuleExtractor(int board[][], int player) {
		list = new ArrayList<int[][]>();
		int count = 0;
		if (piecesLeft != 0) {
//			if (player == AIPlayer) {
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					if (checkValidMove(i, j)) {
						int tmpBoard[][] = copy(board);
						tmpBoard[i][j] = player;

						list.add(copy(tmpBoard));

						count++;

						tmpBoard[i][j] = 0;
					}
					if (count == 0)
						continue;

//					int boardToPrint[][] = list.get(count - 1);
				}
			}

			for (int i = 0; i < list.size(); i++) {
				printBoard(list.get(i));
				System.out.println(calculateHueValue(list.get(i), player));
				System.out.println("----");
			}
//			System.out.println("#########");
			// }

		} else {
//			if (player == AIPlayer) {
			int[][] tmpBorad = copy(board);

			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					if (tmpBorad[i][j] == player) {
						if (checkMoving(i, j, i + 1, j)) {
							tmpBorad[i][j] = 0;
							tmpBorad[i + 1][j] = player;
							list.add(copy(tmpBorad));
							tmpBorad = copy(board);
						}
						if (checkMoving(i, j, i, j + 1)) {
							tmpBorad[i][j] = 0;
							tmpBorad[i][j + 1] = player;
							list.add(copy(tmpBorad));
							tmpBorad = copy(board);

						}
						if (checkMoving(i, j, i - 1, j)) {
							tmpBorad[i][j] = 0;
							tmpBorad[i - 1][j] = player;
							list.add(copy(tmpBorad));
							tmpBorad = copy(board);

						}
						if (checkMoving(i, j, i, j - 1)) {
							tmpBorad[i][j] = 0;
							tmpBorad[i][j - 1] = player;
							list.add(copy(tmpBorad));
							tmpBorad = copy(board);

						}

					}
				}
			}

// print options :
			for (int i = 0; i < list.size(); i++) {
				printBoard(list.get(i));
				System.out.println(calculateHueValue(list.get(i), player));
				System.out.println("----");
			}
//			}

		}
		return list;

	}

	public int calculateHueValue(int[][] board, int AIPlayer) {

		int otherPlayer = AIPlayer * -1;

		if (checkBlocked()) {
			if (board[1][1] == AIPlayer)
				return 10000;
			else
				return -10000;
		}

		if (board[0][0] == AIPlayer && board[0][1] == AIPlayer && board[0][2] == AIPlayer
				|| board[1][0] == AIPlayer && board[1][1] == AIPlayer && board[1][2] == AIPlayer
				|| board[2][0] == AIPlayer && board[2][1] == AIPlayer && board[2][2] == AIPlayer
				|| board[0][0] == AIPlayer && board[1][0] == AIPlayer && board[2][0] == AIPlayer
				|| board[0][1] == AIPlayer && board[1][1] == AIPlayer && board[2][1] == AIPlayer
				|| board[0][2] == AIPlayer && board[1][2] == AIPlayer && board[2][2] == AIPlayer)
			return 10000;

		if (board[0][0] == otherPlayer && board[0][1] == otherPlayer && board[0][2] == otherPlayer
				|| board[1][0] == otherPlayer && board[1][1] == otherPlayer && board[1][2] == otherPlayer
				|| board[2][0] == otherPlayer && board[2][1] == otherPlayer && board[2][2] == otherPlayer
				|| board[0][0] == otherPlayer && board[1][0] == otherPlayer && board[2][0] == otherPlayer
				|| board[0][1] == otherPlayer && board[1][1] == otherPlayer && board[2][1] == otherPlayer
				|| board[0][2] == otherPlayer && board[1][2] == otherPlayer && board[2][2] == otherPlayer)
			return -10000;

		int sum = 0;
		if (board[0][0] == AIPlayer && board[0][1] == AIPlayer)
			sum += 100;
		if (board[0][1] == AIPlayer && board[0][2] == AIPlayer)
			sum += 100;
		if (board[1][0] == AIPlayer && board[1][1] == AIPlayer)
			sum += 100;
		if (board[1][1] == AIPlayer && board[1][2] == AIPlayer)
			sum += 100;
		if (board[2][0] == AIPlayer && board[2][1] == AIPlayer)
			sum += 100;
		if (board[2][1] == AIPlayer && board[2][2] == AIPlayer)
			sum += 100;

		if (board[0][0] == AIPlayer && board[1][0] == AIPlayer)
			sum += 100;
		if (board[1][0] == AIPlayer && board[2][0] == AIPlayer)
			sum += 100;
		if (board[0][1] == AIPlayer && board[1][1] == AIPlayer)
			sum += 100;
		if (board[1][1] == AIPlayer && board[2][1] == AIPlayer)
			sum += 100;
		if (board[0][2] == AIPlayer && board[1][2] == AIPlayer)
			sum += 100;
		if (board[1][2] == AIPlayer && board[2][2] == AIPlayer)
			sum += 100;

		return sum;
	}

	public int[][] alphaBetaCode(int[][] board, int depth, int alpha, int beta, int player) {
		if (depth == 0 || calculateHueValue(board, player) == 10000) {
//			return 10000;
			return board;
		}

		int[][] bestResult = null;

		// Maximizing (APLPHA)
		if (player == AIPlayer) {
			int v = -100000;
			ArrayList<int[][]> nodes = RuleExtractor(board, player);
			int i;
			for (i = 0; i < nodes.size(); i++) {
				int[][] temp = alphaBetaCode(nodes.get(i), depth - 1, alpha, beta, player * -1);
				int hueValue = calculateHueValue(temp, player);
				if (hueValue > v) {
					v = hueValue;
					bestResult = temp;
				}
//				v = Math.max(v,
//						calculateHueValue(alphaBetaCode(nodes.get(i), depth - 1, alpha, beta, player * -1), player));
				alpha = Math.max(alpha, v);
				if (beta <= alpha)
					break;

			}
			return bestResult;
//			return nodes.get(i == nodes.size() ? nodes.size() - 1 : i);
			// Minimizing (BETA)
		} else {
			int v = 100000;
			ArrayList<int[][]> nodes = RuleExtractor(board, player);
			int i;
			for (i = 0; i < nodes.size(); i++) {
				int[][] temp = alphaBetaCode(nodes.get(i), depth - 1, alpha, beta, player * -1);
				int hueValue = calculateHueValue(temp, player);
				if (hueValue < v) {
					v = hueValue;
					bestResult = temp;
				}
//				v = Math.min(v,
//						calculateHueValue(alphaBetaCode(nodes.get(i), depth - 1, alpha, beta, player * -1), player));
				beta = Math.min(beta, v);
				if (beta <= alpha)
					break;
			}
			return bestResult;
//			return nodes.get(i == nodes.size() ? nodes.size() - 1 : i);
		}

	}

	public void reflectBoard(int[][] newBoard) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				int currentPlayer = newBoard[i][j];
				Button currentButton = buttons[i][j];
				board[i][j] = currentPlayer;
				currentButton.setStyle(currentPlayer == 1 ? player1Color : player2Color);
				currentButton.setText(currentPlayer == 1 ? "1" : currentPlayer == -1 ? "2" : "");
			}
		}

		if (checkWin()) {
			handleWin();
			return;
		}

		if (checkBlocked()) {
			handleBlocked();
			return;
		}

		tempMove.setCol(-1);
		tempMove.setRow(-1);
		player *= -1;
		decrementPieces();
//		printBoard(board);
	}

	private Button[][] buttons; // 2D Array of Buttons
	private int[][] board = new int[3][3]; // Board 2D Array OF [-1 , 0, 1]
	private GameCell tempMove = new GameCell(); // When player want to move a piece
	private boolean gameOver = false;
	private int piecesLeft = 6;
	private int player = 1; // Current Player [-1, 1]
	private final int AIPlayer = -1;
	private String player1Color = "-fx-text-fill: green";
	private String player2Color = "-fx-text-fill: red";
	private String tempMoveColor = "-fx-text-fill: black";
	private ArrayList<int[][]> list;
	private final int depth = 5;

}
