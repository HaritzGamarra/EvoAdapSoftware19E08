package edu.asu.stratego.game;

import java.awt.Point;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


import edu.asu.stratego.game.board.ServerBoard;


/**
 * Task to manage a Stratego game between two clients.
 */
public class ServerGameManager implements Runnable {

    private final String session;
    
    private ServerBoard board = new ServerBoard();
    
    private ObjectOutputStream toPlayerOne;
    private ObjectOutputStream toPlayerTwo;
    private ObjectInputStream  fromPlayerOne;
    private ObjectInputStream  fromPlayerTwo;
    
    private Player playerOne = new Player();
    private Player playerTwo = new Player();
    
    private Point playerOneFlag;
    private Point playerTwoFlag;
    
    private PieceColor turn;
    
    private Socket socketOne;
    private Socket socketTwo;

    private ComputeValidMove computeValidMove = new ComputeValidMove();
    
    /**
     * Creates a new instance of ServerGameManager.
     * 
     * @param sockOne socket connected to Player 1's client
     * @param sockTwo socket connected to Player 2's client
     * @param sessionNum the nth game session created by Server.
     * 
     * @see edu.asu.stratego.Server
     */
    public ServerGameManager(Socket sockOne, Socket sockTwo, int sessionNum) {
        this.session = "Session " + sessionNum + ": ";
        this.socketOne = sockOne;
        this.socketTwo = sockTwo;

        if (Math.random() < 0.5) 
        	this.turn = PieceColor.RED;
        else
        	this.turn = PieceColor.BLUE;
    }
    
    /**
     * See ClientGameManager's run() method to understand how the server 
     * interacts with the client.
     * 
     * @see edu.asu.stratego.game.ClientGameManager
     */
    @Override
    public void run() {
        createIOStreams();
        exchangePlayers();
        exchangeSetup();
        
        playGame();
    }

    /**
     * Establish IO object streams to facilitate communication between the 
     * client and server.
     */
    private void createIOStreams() {
        try {
            // NOTE: ObjectOutputStreams must be constructed before the 
            //       ObjectInputStreams so as to prevent a remote deadlock.
            toPlayerOne   = new ObjectOutputStream(socketOne.getOutputStream());
            fromPlayerOne = new ObjectInputStream(socketOne.getInputStream());
            toPlayerTwo   = new ObjectOutputStream(socketTwo.getOutputStream());
            fromPlayerTwo = new ObjectInputStream(socketTwo.getInputStream());
        }
        catch(IOException e) {
            // TODO Handle this exception somehow...
            e.printStackTrace();
        }
    }
    
    /**
     * Receive player information from the clients. Determines the players' 
     * colors, and sends the player information of the opponents back to the 
     * clients.
     */
    private void exchangePlayers() {
        try {
            playerOne = (Player) fromPlayerOne.readObject();
            playerTwo = (Player) fromPlayerTwo.readObject();

            
            if (Math.random() < 0.5) {
                playerOne.setColor(PieceColor.RED);
                playerTwo.setColor(PieceColor.BLUE);
            }
            else {
                playerOne.setColor(PieceColor.BLUE);
                playerTwo.setColor(PieceColor.RED);
            }
            
            toPlayerOne.writeObject(playerTwo);
            toPlayerTwo.writeObject(playerOne);
        }
        catch(ClassNotFoundException | IOException e) {
            // TODO Handle this exception somehow...
            e.printStackTrace();
        }
    }
    
    private void exchangeSetup() {
        try {
            SetupBoard setupBoardOne = (SetupBoard) fromPlayerOne.readObject();
            SetupBoard setupBoardTwo = (SetupBoard) fromPlayerTwo.readObject();
            
            // Register pieces on the server board.
            for (int row = 0; row < 4; ++row) {
                for (int col = 0; col < 10; ++col) {
                    board.getSquare(row, col).setPiece(setupBoardOne.getPiece(3 - row, 9 - col));
                    board.getSquare(row + 6, col).setPiece(setupBoardTwo.getPiece(row, col));
                    
                    if(setupBoardOne.getPiece(3 - row, 9 - col).getPieceType() == PieceType.FLAG)
                    	playerOneFlag = new Point(row, col);
                    if(setupBoardTwo.getPiece(row, col).getPieceType() == PieceType.FLAG)
                    	playerTwoFlag = new Point(row + 6, col);
                }
            }
            
            // Rotate pieces by 180 degrees.
            for (int row = 0; row < 2; ++row) {
                for (int col = 0; col < 10; ++col) {
                    // Player One
                    Piece temp = setupBoardOne.getPiece(row, col);
                    setupBoardOne.setPiece(setupBoardOne.getPiece(3 - row, 9 - col), row, col);
                    setupBoardOne.setPiece(temp, 3 - row, 9 - col);
                    
                    // Player Two
                    temp = setupBoardTwo.getPiece(row, col);
                    setupBoardTwo.setPiece(setupBoardTwo.getPiece(3 - row, 9 - col), row, col);
                    setupBoardTwo.setPiece(temp, 3 - row, 9 - col);
                }
            }

            /* ELIMINAMOS CODIGO REPETIDO (Extraemos el codigo repetido a un nuevo metodo aparte)

            //En el codigo original tenimos esto tanto aqui como en playGame()

            GameStatus winCondition = checkWinCondition();

            toPlayerOne.writeObject(setupBoardTwo);
            toPlayerTwo.writeObject(setupBoardOne);
            toPlayerOne.writeObject(winCondition);
            toPlayerTwo.writeObject(winCondition);
            */

			GameStatus winCondition = checkWinCondition();

			toPlayerOne.writeObject(setupBoardTwo);
			toPlayerTwo.writeObject(setupBoardOne);

			conditionAssign(winCondition);

        }
        catch (ClassNotFoundException | IOException e) {
            // TODO Handle this exception somehow...
            e.printStackTrace();
        }

    }
    
    private void playGame() {
    	Move move;
        while (true) {
            try {
                // Send player turn color to clients.
                toPlayerOne.writeObject(turn);
                toPlayerTwo.writeObject(turn);

                // Get turn from client.
                if (playerOne.getColor() == turn) {
                	move = (Move) fromPlayerOne.readObject();
                	move.setStart(9-move.getStart().x, 9-move.getStart().y);
                	move.setEnd(9-move.getEnd().x, 9-move.getEnd().y);
                }
                else{
                	move = (Move) fromPlayerTwo.readObject();
                }
                                
                Move moveToPlayerOne = new Move();
				Move moveToPlayerTwo = new Move();

				Point start1 = new Point(9 - move.getStart().x, 9 - move.getStart().y);
				Point start2 = new Point(move.getStart().x, move.getStart().y);
				Point end1 = new Point(9 - move.getEnd().x, 9 - move.getEnd().y);
				Point end2 = new Point(new Point(move.getEnd().x, move.getEnd().y));

				PieceColor color1 = move.getMoveColor();
				PieceColor color2 = move.getMoveColor();

                // Register move on the board.
                // If there is no piece at the end (normal move, no attack)
                if(board.getSquare(move.getEnd().x, move.getEnd().y).getPiece() == null) {
                	Piece piece = board.getSquare(move.getStart().x, move.getStart().y).getPiece();
                    
                	board.getSquare(move.getStart().x, move.getStart().y).setPiece(null);
                    board.getSquare(move.getEnd().x, move.getEnd().y).setPiece(piece);

                    moveToPlayerX(moveToPlayerOne, start1, end1, color1, null, piece);
                    moveToPlayerX(moveToPlayerTwo, start2, end2, color2, null, piece);
                    
                    /* CODIGO DUPLICADO ORIGINAL -> lo extraemos al metodo moveToPlayerX

                	moveToPlayerOne.setStart(new Point(9 - move.getStart().x, 9 - move.getStart().y));
                	moveToPlayerOne.setEnd(new Point(9 - move.getEnd().x, 9 - move.getEnd().y));
                	moveToPlayerOne.setMoveColor(move.getMoveColor());
                	moveToPlayerOne.setStartPiece(null);
                	moveToPlayerOne.setEndPiece(piece);

                	moveToPlayerTwo.setStart(new Point(move.getStart().x, move.getStart().y));
                	moveToPlayerTwo.setEnd(new Point(move.getEnd().x, move.getEnd().y));
                	moveToPlayerTwo.setMoveColor(move.getMoveColor());
                	moveToPlayerTwo.setStartPiece(null);
                	moveToPlayerTwo.setEndPiece(piece);
                	*/
                } 
                else {
                	Piece attackingPiece = board.getSquare(move.getStart().x, move.getStart().y).getPiece();
                    Piece defendingPiece = board.getSquare(move.getEnd().x, move.getEnd().y).getPiece();

                	BattleOutcome outcome = attackingPiece.getPieceType().attack(defendingPiece.getPieceType());
                	
                	moveToPlayerOne.setAttackMove(true);
                	moveToPlayerTwo.setAttackMove(true);
                	
                	if(outcome == BattleOutcome.WIN) {
                        board.getSquare(move.getEnd().x, move.getEnd().y).setPiece(board.getSquare(move.getStart().x, move.getStart().y).getPiece());
                        board.getSquare(move.getStart().x, move.getStart().y).setPiece(null);

                        moveToAttack(moveToPlayerOne, start1, end1, color1, attackingPiece, true, false);
                        moveToAttack(moveToPlayerTwo, start2, end2, color2, attackingPiece, true, false);

                        /* CODIGO DUPLICADO ORIGINAL (ASI EN CADA RAMA DEL IF) -> lo extraemos al metodo moveToAttack

                    	moveToPlayerOne.setStart(new Point(9 - move.getStart().x, 9 - move.getStart().y));
                    	moveToPlayerOne.setEnd(new Point(9 - move.getEnd().x, 9 - move.getEnd().y));
                    	moveToPlayerOne.setMoveColor(move.getMoveColor());
                    	moveToPlayerOne.setStartPiece(null);
                    	moveToPlayerOne.setEndPiece(attackingPiece);
                    	moveToPlayerOne.setAttackWin(true);
                    	moveToPlayerOne.setDefendWin(false);

                    	moveToPlayerTwo.setStart(new Point(move.getStart().x, move.getStart().y));
                    	moveToPlayerTwo.setEnd(new Point(move.getEnd().x, move.getEnd().y));
                    	moveToPlayerTwo.setMoveColor(move.getMoveColor());
                    	moveToPlayerTwo.setStartPiece(null);
                    	moveToPlayerTwo.setEndPiece(attackingPiece);
                    	moveToPlayerTwo.setAttackWin(true);
                    	moveToPlayerTwo.setDefendWin(false);
                    	*/
                	}
                	else if(outcome == BattleOutcome.LOSE) {

                        board.getSquare(move.getStart().x, move.getStart().y).setPiece(null);

                        moveToAttack(moveToPlayerOne, start1, end1, color1, defendingPiece, false, true);
                        moveToAttack(moveToPlayerTwo, start2, end2, color2, defendingPiece, false, true);

                	}
                	else if(outcome == BattleOutcome.DRAW) {

                        board.getSquare(move.getStart().x, move.getStart().y).setPiece(null);
                        board.getSquare(move.getEnd().x, move.getEnd().y).setPiece(null);

                        moveToAttack(moveToPlayerOne, start1, end1, color1, null, false, false);
                        moveToAttack(moveToPlayerTwo, start2, end2, color2, null, false, false);

                	}
                }


                /*ELIMINAMOS CODIGO REPETIDO

                //En el codigo original tenimos esto tanto aqui como en exchangeSetup()

                GameStatus winCondition = checkWinCondition();

            	toPlayerOne.writeObject(moveToPlayerOne);
            	toPlayerTwo.writeObject(moveToPlayerTwo);

            	toPlayerOne.writeObject(winCondition);
            	toPlayerTwo.writeObject(winCondition);
            	*/

				GameStatus winCondition = checkWinCondition();

				toPlayerOne.writeObject(winCondition);
				toPlayerTwo.writeObject(winCondition);

				conditionAssign(winCondition);

                // Change turn color.
                if (turn == PieceColor.RED)
                    turn = PieceColor.BLUE;
                else
                    turn = PieceColor.RED;
                
                // Check win conditions.
            }
            catch (IOException | ClassNotFoundException e) {
                System.out.println(session + "Error occured during network I/O");
                return;
            }
        }
    }
    
    private GameStatus checkWinCondition() {
    	if(!hasAvailableMoves(PieceColor.RED))
    		return GameStatus.RED_NO_MOVES;
    		
    	else if(isCaptured(PieceColor.RED))
    		return GameStatus.RED_CAPTURED;
    	
    	if(!hasAvailableMoves(PieceColor.BLUE))
    		return GameStatus.BLUE_NO_MOVES;
    		
    	else if(isCaptured(PieceColor.BLUE))
    		return GameStatus.BLUE_CAPTURED;
    	
    	return GameStatus.IN_PROGRESS;
    }
    
    private boolean isCaptured(PieceColor inColor) {
    	/*ELIMINAMOS IF ANIDADO

    	//codigo Original

    	if(playerOne.getColor() == inColor) {
    		if(board.getSquare(playerOneFlag.x, playerOneFlag.y).getPiece().getPieceType() != PieceType.FLAG) 
    			return true;
    	}
    	if(playerTwo.getColor() == inColor) {
    		if(board.getSquare(playerTwoFlag.x, playerTwoFlag.y).getPiece().getPieceType() != PieceType.FLAG)
    			return true;
    	}
    	return false;*/

		if(playerOne.getColor() == inColor){
			return board.getSquare(playerOneFlag.x, playerOneFlag.y).getPiece().getPieceType() != PieceType.FLAG;
		}
		if(playerTwo.getColor() == inColor){
			return board.getSquare(playerTwoFlag.x, playerTwoFlag.y).getPiece().getPieceType() != PieceType.FLAG;
		}
    	return false;
    }
    
    private boolean hasAvailableMoves(PieceColor inColor) {
    	//ELIMINAMOS IF ANIDADO (como antes)
    	for(int row = 0; row < 10; ++row) {
    		for(int col = 0; col < 10; ++col) {
    			if(board.getSquare(row, col).getPiece() != null && board.getSquare(row, col).getPiece().getPieceColor() == inColor) {
	    			 return computeValidMove.computeValidMoves(row, col, inColor, board).size() > 0;
    			}
    		}
    	}
    	return false;
    }

	//Metodo comun tanto para exchangeSetup como para playGame
	private void conditionAssign(GameStatus condition){
		try {
			toPlayerOne.writeObject(condition);
			toPlayerTwo.writeObject(condition);
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}

	//Metodo nuevo aparte para poder sustituir parte del codigo duplicado en playGame()
	private void moveToPlayerX(Move moveToPlayerX, Point start, Point end, PieceColor color, Piece starterPiece, Piece endPiece){
		moveToPlayerX.setStart(start);
		moveToPlayerX.setEnd(end);
		moveToPlayerX.setMoveColor(color);
		moveToPlayerX.setStartPiece(starterPiece);
		moveToPlayerX.setEndPiece(endPiece);


	}

	//Metodo nuevo aparte para poder sustituir el resto del codigo duplicado en playGame()
	private void moveToAttack(Move moveToPlayerX, Point start, Point end, PieceColor color, Piece attackingPiece, boolean attackWin, boolean defendWin){
		moveToPlayerX.setStart(start);
		moveToPlayerX.setEnd(end);
		moveToPlayerX.setMoveColor(color);
		moveToPlayerX.setStartPiece(null);
		moveToPlayerX.setEndPiece(attackingPiece);
		moveToPlayerX.setAttackWin(attackWin);
		moveToPlayerX.setDefendWin(defendWin);
	}
}