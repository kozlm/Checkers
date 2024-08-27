package gui;

import gamelogic.*;
import gamelogic.pieces.Colour;
import gamelogic.pieces.Coordinates;
import gamelogic.pieces.King;
import gamelogic.pieces.Piece;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

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
        whiteAIService = new Service<>() {
            @Override
            protected Task<Pair<Coordinates, Coordinates>> createTask() {
                return new Task<>() {
                    @Override
                    protected Pair<Coordinates, Coordinates> call() {
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

        blackAIService = new Service<>() {
            @Override
            protected Task<Pair<Coordinates, Coordinates>> createTask() {
                return new Task<>() {
                    @Override
                    protected Pair<Coordinates, Coordinates> call() {
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
        alert.setTitle("Game information");
        alert.setHeaderText("The game is over!");

        if (game.whoWon() == 'w') {
            alert.setContentText("White won!");
        } else if (game.whoWon() == 'b') {
            alert.setContentText("Black won!");
        } else if (game.whoWon() == 'd') {
            alert.setContentText("Draw!");
        } else return false;

        ButtonType buttonBack = new ButtonType("Back to MainPage", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType buttonAgain = new ButtonType("Play again");
        ButtonType buttonPDN = new ButtonType("Get PDN");

        alert.getButtonTypes().setAll(buttonBack, buttonAgain, buttonPDN);
        
        Button pdnB = (Button) alert.getDialogPane().lookupButton(buttonPDN);
        Tooltip tooltip = new Tooltip("Copied to clipboard!");
        pdnB.setOnAction(event -> {
            try {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();
                content.putString(game.getPDN());
                clipboard.setContent(content);

                double screenX = pdnB.localToScreen(pdnB.getBoundsInLocal()).getMinX();
                double screenY = pdnB.localToScreen(pdnB.getBoundsInLocal()).getMinY();
                tooltip.show(pdnB, screenX, screenY);
                PauseTransition trans = new PauseTransition(Duration.seconds(1));
                trans.setOnFinished(e -> tooltip.hide());
                trans.play();
            } catch (NullPointerException ignored) {
            }
        });
        AtomicInteger consumer = new AtomicInteger(0);

        alert.setOnCloseRequest(event -> {
            if (alert.getResult() == buttonPDN) {
                if (consumer.get() == 0) {
                    consumer.getAndIncrement();
                    event.consume();
                } else {
                    try {
                        backToMainPage();
                    } catch (IOException e) {
                        System.out.println("Can't go to MainPage.");
                    }
                }
            }
        });

        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == buttonBack) {
                try {
                    backToMainPage();
                } catch (IOException e) {
                    System.out.println("Can't go to MainPage.");
                }
            } else if (buttonType == buttonAgain) {
                try {
                    playAgain();
                } catch (IOException e) {
                    System.out.println("Can't play again.");
                }
            }
        });
        return true;
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

    private void playAgain() throws IOException {
        Stage stage = (Stage) boardGrid.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("boardScene.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private void backToMainPage() throws IOException {
        Stage stage = (Stage) boardGrid.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("mainPageScene.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}