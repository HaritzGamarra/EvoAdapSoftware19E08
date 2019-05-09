package edu.asu.stratego.game;

import java.io.Serializable;

/**
 * Represents a single game piece.
 */
public class Piece implements Serializable {

    private static final long serialVersionUID = 7193334048398155856L;
    
    private PieceColor color;
    private PieceType  type;
    
    private boolean isOpponentPiece;
    private String   spriteKey;
    
    /**
     * Creates a new instance of Piece.
     * 
     * @param type PieceType of the piece
     * @param color color of the piece
     * @param isOpponentPiece whether or not the piece belongs to the opponent
     */
    public Piece(PieceType type, PieceColor color, boolean isOpponentPiece) {
        this.isOpponentPiece = isOpponentPiece;
        this.color = color;
        this.type  = type;
        setPieceImage();
    }
    
    /**
     * Sets the Piece's image sprite according to the type of the piece, the 
     * player's color, and whether or not the piece belongs to the opponent.
     */
    private void setPieceImage() {
        //ELIMINAMOS UN SWITCH-CASE CON 12 CASES COMPROBANDO ANTES CON UN UNICO IF-ELSE EL COLOR
        String colorValue;

        if(this.color == PieceColor.BLUE){
            colorValue = "BLUE";
        }else{
            colorValue = "RED";
        }

        switch (type) {
            case SCOUT:      this.spriteKey = colorValue + "_02";   break;
            case MINER:      this.spriteKey = colorValue + "_03";   break;
            case SERGEANT:   this.spriteKey = colorValue + "_04";   break;
            case LIEUTENANT: this.spriteKey = colorValue + "_05";   break;
            case CAPTAIN:    this.spriteKey = colorValue + "_06";   break;
            case MAJOR:      this.spriteKey = colorValue + "_07";   break;
            case COLONEL:    this.spriteKey = colorValue + "_08";   break;
            case GENERAL:    this.spriteKey = colorValue + "_09";   break;
            case MARSHAL:    this.spriteKey = colorValue + "_10";   break;
            case BOMB:       this.spriteKey = colorValue + "_BOMB"; break;
            case FLAG:       this.spriteKey = colorValue + "_FLAG"; break;
            case SPY:        this.spriteKey = colorValue + "_SPY";  break;
            default:                                      break;
        }
            
        if (this.isOpponentPiece)
            this.spriteKey = colorValue + "_BACK";

    }
    
    /**
     * @return the piece type of the piece.
     */
    public PieceType getPieceType() {
        return type;
    }
    
    public PieceColor getPieceColor() {
        return color;
    }
    
    /**
     * @return the sprite associated with the type of the piece.
     */
    public String getPieceSpriteKey() {
        return spriteKey;
    }
}