package gui;

import gamelogic.*;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class BoardController implements Initializable {

    Game game;
    Node chosenPiece;
    Color chosenSquare, darkSquare, lightSquare;

    @FXML
    private GridPane boardGrid;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        chosenSquare = Color.CHOCOLATE;
        darkSquare = Color.BURLYWOOD;
        lightSquare = Color.BISQUE;
        chosenPiece = null;
        game = new Game(0);
        showBoard();
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
                Piece pos = game.getPosition(i, 9 - j);
                if (pos != null) {
                    if (pos.getColour() == Colour.WHITE) circ.setFill(Color.WHITE);
                    else circ.setFill(Color.BLACK);
                    if (pos instanceof King) {
                        circ.setStroke(Color.RED);
                        circ.setRadius(20);
                        circ.setStrokeWidth(5);
                    }
                    boardGrid.add(circ, i, j);
                    circ.setOnMouseClicked(this::showMoves);
                }
            }
        }
    }

    @FXML
    private void move(MouseEvent event) {
        if (event.getSource() instanceof Rectangle && chosenPiece != null) {
            int xOrigin = GridPane.getColumnIndex(chosenPiece);
            int yOrigin = 9 - GridPane.getRowIndex(chosenPiece);
            int xDestination = GridPane.getColumnIndex((Node) event.getSource());
            int yDestination = 9 - GridPane.getRowIndex((Node) event.getSource());
            if (((Rectangle) event.getSource()).getFill() == chosenSquare) {
                game.makeMove(xOrigin,yOrigin,xDestination,yDestination);
                boardGrid.getChildren().clear();
                showBoard();
            }
        }
        if (game.whoWon()==Colour.WHITE) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("white");
            alert.showAndWait();
        }
        if (game.whoWon()==Colour.BLACK) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("black");
            alert.showAndWait();
        }
    }

    @FXML
    private void showMoves(MouseEvent event) {
        if (event.getSource() instanceof Circle) {
            int x = GridPane.getColumnIndex((Node) event.getSource());
            int y = GridPane.getRowIndex((Node) event.getSource());
            if (chosenPiece == event.getSource()) {
                chosenPiece = null;
                for (Node node : boardGrid.getChildren()) {
                    if (node instanceof Rectangle) {
                        if ((GridPane.getColumnIndex(node) + GridPane.getRowIndex(node)) % 2 == 0)
                            ((Rectangle) node).setFill(lightSquare);
                        else ((Rectangle) node).setFill(darkSquare);
                    }
                }
            } else {
                List<Coordinates> moves = game.getMovesForPosition(x, 9 - y);
                for (Node node : boardGrid.getChildren()) {
                    if (node instanceof Rectangle) {
                        if ((GridPane.getColumnIndex(node) + GridPane.getRowIndex(node)) % 2 == 0)
                            ((Rectangle) node).setFill(lightSquare);
                        else ((Rectangle) node).setFill(darkSquare);
                    }
                }
                for (Node node : boardGrid.getChildren()) {
                    if (node instanceof Rectangle
                            && moves.contains(new Coordinates(GridPane.getColumnIndex(node), 9 - GridPane.getRowIndex(node))))
                        ((Rectangle) node).setFill(chosenSquare);
                }
                chosenPiece = (Node) event.getSource();
            }
        }
    }
}