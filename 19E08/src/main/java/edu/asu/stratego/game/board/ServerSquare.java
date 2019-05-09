package edu.asu.stratego.game.board;

import edu.asu.stratego.game.Piece;
import edu.asu.stratego.gui.board.BoardSquareEventPane;
import edu.asu.stratego.gui.board.BoardSquarePane;

/**
 * Represents an individual square of a Stratego board.
 */
public class ServerSquare implements AbstractSquare{

    private Piece piece = null;

    /**
     * @return the piece
     */
    public Piece getPiece() {
        return piece;
    }

    /**
     * @param piece the piece to set
     */
    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    //Como estamos haciendo que herede de una clase abstracta debemos implementar todos los metodos abstractos que haya en la interfaz
    //Otra opcion es poner en la interfaz unicamente los metodos que sean comunes a las dos clases. HABLAMOS PARA VER QUE HACEMOS
    public BoardSquarePane getPiecePane(){ return null; }
    public BoardSquareEventPane getEventPane(){ return null; }
}