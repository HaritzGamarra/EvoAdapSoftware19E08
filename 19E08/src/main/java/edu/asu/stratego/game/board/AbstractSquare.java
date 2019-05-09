package edu.asu.stratego.game.board;

import edu.asu.stratego.game.Piece;
import edu.asu.stratego.gui.board.BoardSquareEventPane;
import edu.asu.stratego.gui.board.BoardSquarePane;

public interface AbstractSquare {
    //Creamos una interfaz con metodos abstractos que usaran tanto ClientSquare como ServerSquare
    Piece getPiece();
    void setPiece(Piece piece);
    BoardSquarePane getPiecePane();
    BoardSquareEventPane getEventPane();
}
