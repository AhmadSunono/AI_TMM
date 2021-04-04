package application;

import java.net.URL;
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
			
			if(gameOver) return;
			
			Button current = (Button) event.getTarget();
			int row = ((GameCell) current.getUserData()).getRow();
			int col = ((GameCell) current.getUserData()).getCol();

			boolean tempMoveFlag = tempMove.getRow() != -1 && tempMove.getCol() != -1;

			// If there was a piece moving //
			if (tempMoveFlag) {
				board[row][col] = player;
				current.setText(player == 1 ? "1" : "2");
				current.setStyle(player == 1 ? player1Color : player2Color);

				int tmpRow = tempMove.getRow();
				int tmpCol = tempMove.getCol();

				board[tmpRow][tmpCol] = 0;
				buttons[tmpRow][tmpCol].setText("");

				tempMove.setCol(-1);
				tempMove.setRow(-1);

				if (checkWin())
					handleWin();

				player *= -1;

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
						
						if (checkWin())
							handleWin();

						decrementPieces();
						player *= -1;
					}

				} else {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setHeaderText("Invalid Move!");
					alert.show();
				}
			}
			if (checkWin())
				handleWin();

		}
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

	private boolean checkValidMove(int row, int col) {
		int cellValue = board[row][col];
		if (cellValue == 0 && piecesLeft == 0) {
			return false;
		}
		if (cellValue == 1 && player == -1 || cellValue == -1 && player == 1) {
			return false;
		}

		return true;
	}

	private Button[][] buttons;
	private int[][] board = new int[3][3];
	private GameCell tempMove = new GameCell();
	private boolean gameOver = false;
	private boolean[][] legalMove = new boolean[3][3];
	private int piecesLeft = 6;
	private int player = 1;
	private String player1Color = "-fx-text-fill: green";
	private String player2Color = "-fx-text-fill: red";
	private String tempMoveColor = "-fx-text-fill: black";
}
