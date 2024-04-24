package gui;

import gamelogic.*;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class BoardController implements Initializable {

    private Game game;
    private Node chosenPiece;
    private Color chosenSquare, darkSquare, lightSquare;
    private int mode, perspectiveIndicator;
    private Colour whichColour;
    private String whiteName, blackName;
    private CheckersAI blackAI, whiteAI;
    private Service<Pair<Coordinates, Coordinates>> whiteAIService, blackAIService;
    @FXML
    private GridPane boardGrid;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        chosenSquare = Color.CHOCOLATE;
        darkSquare = Color.BURLYWOOD;
        lightSquare = Color.BISQUE;
        chosenPiece = null;
        mode = GameData.getMode();
        whiteName = GameData.getWhiteName();
        blackName = GameData.getBlackName();
        whichColour = GameData.whichColour();
        perspectiveIndicator = whichColour == Colour.WHITE ? 0 : 9;
        game = new Game();
        blackAI = new CheckersAI(Colour.BLACK, 7, game.getBoard());
        whiteAI = new CheckersAI(Colour.WHITE, 7, game.getBoard());
        showBoard();
        initializeAI();
        if (mode == 2 || (mode == 1 && whichColour == Colour.BLACK)) whiteAIService.restart();
    }

    private void showBoard() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Rectangle rec = new Rectangle(50, 50);
                if ((i + j) % 2 == 0) rec.setFill(lightSquare);
                else rec.setFill(darkSquare);
                rec.setOnMouseClicked(this::move);
                boardGrid.add(rec, i, j);
                Circle circ = new Circle(25);
                Piece pos = game.getPosition(
                        Math.abs(perspectiveIndicator - i),
                        Math.abs(perspectiveIndicator - (9 - j))
                );
                if (pos != null) {
                    if (pos.getColour() == Colour.WHITE) circ.setFill(Color.WHITE);
                    else circ.setFill(Color.BLACK);
                    if (pos instanceof King) {
                        circ.setStroke(Color.RED);
                        circ.setRadius(22.5);
                        circ.setStrokeWidth(5);
                    }
                    boardGrid.add(circ, i, j);
                    circ.setOnMouseClicked(this::showMoves);
                }
            }
        }
    }

    private void initializeAI() {
        whiteAIService = new Service<Pair<Coordinates, Coordinates>>() {
            @Override
            protected Task createTask() {
                return new Task<Pair<Coordinates, Coordinates>>() {
                    @Override
                    protected Pair<Coordinates, Coordinates> call() throws Exception {
                        return whiteAI.findBestMove();
                    }
                };
            }
        };


        whiteAIService.setOnSucceeded(workerStateEvent -> {
            Pair<Coordinates, Coordinates> generatedMove = whiteAIService.getValue();
            Platform.runLater(() -> {
                game.makeMove(generatedMove.getKey().getX(), generatedMove.getKey().getY(), generatedMove.getValue().getX(), generatedMove.getValue().getY());
                boardGrid.getChildren().clear();
                showBoard();
                if (checkWinner()) stopAI();
                else if (game.whoseTurn() == Colour.WHITE) whiteAIService.restart();
                else if (mode == 2) blackAIService.restart();
            });
        });

        blackAIService = new Service<Pair<Coordinates, Coordinates>>() {
            @Override
            protected Task createTask() {
                return new Task<Pair<Coordinates, Coordinates>>() {
                    @Override
                    protected Pair<Coordinates, Coordinates> call() throws Exception {
                        return blackAI.findBestMove();
                    }
                };
            }
        };


        blackAIService.setOnSucceeded(workerStateEvent -> {
            Pair<Coordinates, Coordinates> generatedMove = blackAIService.getValue();
            Platform.runLater(() -> {
                game.makeMove(generatedMove.getKey().getX(), generatedMove.getKey().getY(), generatedMove.getValue().getX(), generatedMove.getValue().getY());
                boardGrid.getChildren().clear();
                showBoard();
                if (checkWinner()) stopAI();
                else if (game.whoseTurn() == Colour.BLACK) blackAIService.restart();
                else if (mode == 2) whiteAIService.restart();
            });
        });
    }

    private void stopAI() {
        if (blackAIService.isRunning()) {
            blackAIService.cancel();
        }
        if (whiteAIService.isRunning()) {
            whiteAIService.cancel();
        }

    }

    @FXML
    private void move(MouseEvent event) {
        if (event.getSource() instanceof Rectangle && chosenPiece != null) {
            int xOrigin = Math.abs(perspectiveIndicator - GridPane.getColumnIndex(chosenPiece));
            int yOrigin = Math.abs(perspectiveIndicator - (9 - GridPane.getRowIndex(chosenPiece)));
            int xDestination = Math.abs(perspectiveIndicator - GridPane.getColumnIndex((Node) event.getSource()));
            int yDestination = Math.abs(perspectiveIndicator - (9 - GridPane.getRowIndex((Node) event.getSource())));
            if (((Rectangle) event.getSource()).getFill() == chosenSquare) {
                game.makeMove(xOrigin, yOrigin, xDestination, yDestination);
                boardGrid.getChildren().clear();
                showBoard();
                checkWinner();
                if (mode == 1) {
                    if (game.whoseTurn() == Colour.BLACK && whichColour == Colour.WHITE) blackAIService.restart();
                    else if (game.whoseTurn() == Colour.WHITE && whichColour == Colour.BLACK) whiteAIService.restart();
                }
            }
        }
    }

    private boolean checkWinner() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Three Button Alert");
        alert.setHeaderText("This is a three button alert");

        // Add buttons to the alert
        alert.getButtonTypes().addAll(ButtonType.CLOSE, ButtonType.YES, ButtonType.NEXT);

        // Show the alert and wait for user action
        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == ButtonType.YES) {
                System.out.println("Yes button clicked");
            } else if (buttonType == ButtonType.NO) {
                System.out.println("No button clicked");
            } else if (buttonType == ButtonType.CANCEL) {
                System.out.println("Cancel button clicked");
            }
        });

        if (game.whoWon() == 'w') {
            alert.setContentText("White won!");
            alert.showAndWait();
            return true;
        }
        else if (game.whoWon() == 'b') {
            alert.setContentText("Black won!");
            alert.showAndWait();
            return true;
        }
        else if (game.whoWon() == 'd') {
            alert.setContentText("Draw!");
            alert.showAndWait();
            return true;
        }
        return false;
    }

    @FXML
    private void showMoves(MouseEvent event) {
        if (event.getSource() instanceof Circle) {
            int x = Math.abs(perspectiveIndicator - GridPane.getColumnIndex((Node) event.getSource()));
            int y = Math.abs(perspectiveIndicator - (9 - GridPane.getRowIndex((Node) event.getSource())));
            for (Node node : boardGrid.getChildren()) {
                if (node instanceof Rectangle) {
                    if ((GridPane.getColumnIndex(node) + GridPane.getRowIndex(node)) % 2 == 0)
                        ((Rectangle) node).setFill(lightSquare);
                    else ((Rectangle) node).setFill(darkSquare);
                }
            }
            if (chosenPiece == event.getSource()) chosenPiece = null;
            else {
                List<Coordinates> moves = game.getMovesForPosition(x, y);
                for (Node node : boardGrid.getChildren()) {
                    if (node instanceof Rectangle && moves.contains(new Coordinates(
                            Math.abs(perspectiveIndicator - GridPane.getColumnIndex(node)),
                            Math.abs(perspectiveIndicator - (9 - GridPane.getRowIndex(node)))
                    )))
                        ((Rectangle) node).setFill(chosenSquare);
                }
                chosenPiece = (Node) event.getSource();
            }
        }
    }
}