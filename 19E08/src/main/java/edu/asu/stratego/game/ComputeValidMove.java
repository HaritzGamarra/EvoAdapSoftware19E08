package edu.asu.stratego.game;

import edu.asu.stratego.game.board.AbstractBoard;

import java.awt.*;
import java.util.ArrayList;

//Con esta clase tratamos de solucionar codigo duplicado (estos 5 metodos) que antes encontrabamos en ServerGameManager y en BoardSquareEventPane
public class ComputeValidMove {

    public ArrayList<Point> computeValidMoves(int row, int col, PieceColor inColor, AbstractBoard board) {
        // Set the max distance of a valid move to 1
        int max = 1;

        // Set the max distance of a valid move to the board width if the piece is a scout
        PieceType pieceType = board.getSquare(row, col).getPiece().getPieceType();
        if (pieceType == PieceType.SCOUT)
            max = 8;

        ArrayList<Point> validMoves = new ArrayList<>();

        // Iterate through each direction and add valid moves based on if:
        // 1) The square is in bounds (inside the board)
        // 2) If the square is not a lake
        // 3) If the square has no piece on it OR there is a piece, but it is an opponent piece

        if (pieceType != PieceType.BOMB && pieceType != PieceType.FLAG) {
            // Negative Row (UP)
            for (int i = -1; i >= -max; --i) {
                if (isInBounds(row + i, col) && (!isLake(row + i, col) || (!isNullPiece(row + i, col, board) && !isOpponentPiece(row + i, col, inColor, board)))) {
                    if (isNullPiece(row + i, col, board) || isOpponentPiece(row + i, col, inColor, board)) {
                        validMoves.add(new Point(row + i, col));

                        if (!isNullPiece(row + i, col, board) && isOpponentPiece(row + i, col, inColor, board))
                            break;
                    } else
                        break;
                } else
                    break;
            }
            // Positive Col (RIGHT)
            for (int i = 1; i <= max; ++i) {
                if (isInBounds(row, col + i) && (!isLake(row, col + i) || (!isNullPiece(row, col + i, board) && !isOpponentPiece(row, col + i, inColor, board)))) {
                    if (isNullPiece(row, col + i, board) || isOpponentPiece(row, col + i, inColor, board)) {
                        validMoves.add(new Point(row, col + i));

                        if (!isNullPiece(row, col + i, board) && isOpponentPiece(row, col + i, inColor, board))
                            break;
                    } else
                        break;
                } else
                    break;
            }
            // Positive Row (DOWN)
            for (int i = 1; i <= max; ++i) {
                if (isInBounds(row + i, col) && (!isLake(row + i, col) || (!isNullPiece(row + i, col, board) && !isOpponentPiece(row + i, col, inColor, board)))) {
                    if (isNullPiece(row + i, col, board) || isOpponentPiece(row + i, col, inColor, board)) {
                        validMoves.add(new Point(row + i, col));

                        if (!isNullPiece(row + i, col, board) && isOpponentPiece(row + i, col, inColor, board))
                            break;
                    } else
                        break;
                } else
                    break;
            }
            // Negative Col (LEFT)
            for (int i = -1; i >= -max; --i) {
                if (isInBounds(row, col + i) && (!isLake(row, col + i) || (!isNullPiece(row, col + i, board) && !isOpponentPiece(row, col + i, inColor, board)))) {
                    if (isNullPiece(row, col + i, board) || isOpponentPiece(row, col + i, inColor, board)) {
                        validMoves.add(new Point(row, col + i));

                        if (!isNullPiece(row, col + i, board) && isOpponentPiece(row, col + i, inColor, board))
                            break;
                    } else
                        break;
                } else
                    break;
            }
        }

        return validMoves;
    }

    public boolean isLake(int row, int col) {
        return ((col == 2 || col == 3 || col == 6 || col == 7) && (row == 4 || row == 5));
    }

    private static boolean isInBounds(int row, int col) {
        if (row < 0 || row > 9) {
            return false;
        }
        return !(col < 0 || col > 9);

    }

    //Este es el unico metodo que es diferente en las dos clases en las que aparece.
    //En un metodo lo que se devuelve es si el color de la pieza es igual al color dado y en el otro se compara directamente elcolor de la pieza con el del jugador.
    //Lo que haremos es que si el color introducido como parametro es null, compararemos la pieza con el jugador, si no, compararemos con el color introducido
    private boolean isOpponentPiece(int row, int col, PieceColor inColor, AbstractBoard board) {
        if (inColor == null) {
            return Game.getBoard().getSquare(row, col).getPiece().getPieceColor() != Game.getPlayer().getColor();
        } else {
            return board.getSquare(row, col).getPiece().getPieceColor() != inColor;
        }
    }

    public boolean isNullPiece(int row, int col, AbstractBoard board) {
        return board.getSquare(row, col).getPiece() == null;
    }

}
