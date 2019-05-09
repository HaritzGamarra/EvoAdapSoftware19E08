package edu.asu.stratego.game.board;

import edu.asu.stratego.gui.board.BoardEventPane;
import edu.asu.stratego.gui.board.BoardPane;

/**
 * Representation of a Stratego board.
 */
public class ServerBoard implements AbstractBoard{

    private final int SIZE = 10;
    private ServerSquare[][] squares;
    
    /**
     * Creates a new instance of Board.
     */
    public ServerBoard() {
        squares = new ServerSquare[SIZE][SIZE];
        
        for (int row = 0; row < SIZE; ++row) {
            for (int col = 0; col < SIZE; ++col) {
                squares[row][col] = new ServerSquare();
            }
        }
    }
    
    /**
     * Returns the board square located at (row, col).
     * 
     * @param row board square row
     * @param col board square column
     * @return the square located at (row, col)
     */
    public ServerSquare getSquare(int row, int col) {
        return squares[row][col];
    }

    //Como estamos haciendo que herede de una clase abstracta debemos implementar todos los metodos abstractos que haya en la interfaz
    //Otra opcion es poner en la interfaz unicamente los metodos que sean comunes a las dos clases. HABLAMOS PARA VER QUE HACEMOS
    public BoardPane getPiecePane(){ return null; }
    public BoardEventPane getEventPane(){ return null; }
}