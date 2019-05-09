package edu.asu.stratego.game.board;

import edu.asu.stratego.gui.board.BoardEventPane;
import edu.asu.stratego.gui.board.BoardPane;

public interface AbstractBoard{
    //Creamos una interfaz con metodos abstractos que usaran tanto ClientBoard como ServerBoard
    BoardPane getPiecePane();
    BoardEventPane getEventPane();
    AbstractSquare getSquare(int row, int col);
}
