package main;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import piece.*;

public class GamePanel  extends JPanel implements Runnable{
    public static final  int WIDTH = 1100;
    public static final  int HEIGHT = 800;
    final int FPS = 60;
    Thread gameThread;
    Board board = new Board();
    Mouse mouse = new Mouse();

    // Pieces
    public static ArrayList<Piece> pieces = new ArrayList<>();
    public static ArrayList<Piece> simPieces = new ArrayList<>();
    ArrayList<Piece> promoPieces = new ArrayList<>();
    public static Piece castlingPiece;
    Piece activePiece, checkingPiece;

    // Color
    public static boolean currentColor = true;

    boolean canMove;
    boolean validSquare;
    boolean promotion;
    boolean gameOver;
    boolean stalemate;

    public GamePanel(){
        setPreferredSize(new Dimension(WIDTH,HEIGHT));
        setBackground(new Color(149, 137, 122));
        addMouseMotionListener(mouse);
        addMouseListener(mouse);

        setPiece();
        copyPieces(pieces, simPieces);
    }

    public void launchGame(){
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void setPiece(){
        for(int i = 0; i < 8; i++){
            pieces.add(new Pawn(true, i, 6));
            pieces.add(new Pawn(false, i, 1));
        }

        // White
        pieces.add(new Rook(true, 0, 7));
        pieces.add(new Rook(true, 7, 7));
        pieces.add(new Knight(true, 1, 7));
        pieces.add(new Knight(true, 6, 7));
        pieces.add(new Bishop(true, 2, 7));
        pieces.add(new Bishop(true, 5, 7));
        pieces.add(new Queen(true, 3, 7));
        pieces.add(new King(true, 4, 7));

        // Black
        pieces.add(new Rook(false, 0, 0));
        pieces.add(new Rook(false, 7, 0));
        pieces.add(new Knight(false, 1, 0));
        pieces.add(new Knight(false, 6, 0));
        pieces.add(new Bishop(false, 2, 0));
        pieces.add(new Bishop(false, 5, 0));
        pieces.add(new Queen(false, 3, 0));
        pieces.add(new King(false, 4, 0));
    }

    public void testIllegal(){

    }


    private void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target){
        target.clear();
        for(int i = 0; i < source.size(); i++)
            target.add(source.get(i));
    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while(gameThread != null){
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if(delta >= 1){
                update();
                repaint();
                delta--;
            }
        }
    }

    private void update(){
        if(promotion){
            promoting();
        } else if(!gameOver && !stalemate){

            if(mouse.pressed) {
                if (activePiece == null) {
                    for (Piece piece : simPieces) {
                        if (piece.color == currentColor &&
                                piece.col == mouse.x / Board.SQUARE_SIZE &&
                                piece.row == mouse.y / Board.SQUARE_SIZE)
                        {
                            activePiece = piece;
                        }
                    }
                } else {
                    simulate();
                }
            }
            if(!mouse.pressed){
                if(activePiece != null){

                    if(validSquare) {

                        copyPieces(simPieces, pieces);
                        activePiece.updatePosition();

                        if(castlingPiece != null){
                            castlingPiece.updatePosition();
                        }

                        if(isKingInCheck() && isCheckmate()){
                            gameOver = true;
                        }
                        else if(isStalemate() && !isKingInCheck()){
                            stalemate = true;
                        }
                        else {
                            if(canPromote()) {
                                promotion = true;
                            } else {
                                changePlayer();
                            }
                        }

                    } else {
                        copyPieces(pieces, simPieces);
                        activePiece.resetPosition();
                        activePiece = null;
                    }
                }
            }
        }
    }

    private void simulate(){
        canMove = false;
        validSquare = false;

        copyPieces(pieces, simPieces);

        if(castlingPiece != null){
            castlingPiece.col = castlingPiece.preCol;
            castlingPiece.x = castlingPiece.getCoordinate(castlingPiece.col);
            castlingPiece = null;
        }

        activePiece.x = mouse.x - Board.HALF_SQUARE;
        activePiece.y = mouse.y - Board.HALF_SQUARE;
        activePiece.col = activePiece.getCol(activePiece.x);
        activePiece.row = activePiece.getRow(activePiece.y);

        if(activePiece.canMove(activePiece.col, activePiece.row)){
            canMove = true;

            if(activePiece.hittingPiece != null){
                simPieces.remove(activePiece.hittingPiece.getIndex());
            }

            checkCasting();

            if(!isIllegal(activePiece) && !opponentCanCaptureKing() ){
                validSquare = true;
            }
        }
    }

    private boolean isIllegal(Piece king){
        if(king.type == Type.KING){
            for(Piece piece : simPieces){
                if(piece != king && piece.color != king.color && piece.canMove(king.col, king.row)){
                    return true;
                }
            }
        }
        return false;
    }

    private boolean opponentCanCaptureKing(){
        Piece king = getKing(false);

        for(Piece piece : simPieces){
            if(piece.color != king.color && piece.canMove(king.col, king.row)){
                return true;
            }
        }
        return false;
    }

    private boolean isKingInCheck(){

        Piece king = getKing(true);

        if(activePiece.canMove(king.col, king.row)){
            checkingPiece = activePiece;
            return true;
        } else {
            checkingPiece = null;
        }

        return false;
    }

    private Piece getKing(boolean opponent){
        Piece king = null;

        for(Piece piece : simPieces){
            if(opponent){
                if(piece.type == Type.KING && piece.color != currentColor){
                    king = piece;
                }
            }
            else {
                if(piece.type == Type.KING && piece.color == currentColor){
                    king = piece;
                }
            }
        }
        return king;
    }

    private boolean isCheckmate(){

        Piece king = getKing(true);

        if(kingCanMove(king)){
            return false;
        } else {
            int colDiff = Math.abs(checkingPiece.col - king.col);
            int rowDiff = Math.abs(checkingPiece.row - king.row);

            if(colDiff == 0){
                if(checkingPiece.row < king.row){
                    for(int row = checkingPiece.row; row < king.row; row++){
                        for(Piece piece : simPieces){
                            if(piece != king && piece.color != currentColor && piece.canMove(checkingPiece.col, row)){
                                return false;
                            }
                        }
                    }
                }
                if(checkingPiece.row > king.row){
                    for(int row = checkingPiece.row; row > king.row; row--){
                        for(Piece piece : simPieces){
                            if(piece != king && piece.color != currentColor && piece.canMove(checkingPiece.col, row)){
                                return false;
                            }
                        }
                    }
                }
            } else if(rowDiff == 0){
                if(checkingPiece.col < king.col){
                    for(int col = checkingPiece.col; col < king.row; col++){
                        for(Piece piece : simPieces){
                            if(piece != king && piece.color != currentColor && piece.canMove(col, checkingPiece.row)){
                                return false;
                            }
                        }
                    }
                }

                if(checkingPiece.col < king.col){
                    for(int col = checkingPiece.col; col > king.row; col--){
                        for(Piece piece : simPieces){
                            if(piece != king && piece.color != currentColor && piece.canMove(col, checkingPiece.row)){
                                return false;
                            }
                        }
                    }
                }

            } else if(colDiff == rowDiff){
                if(checkingPiece.row < king.row){
                    if(checkingPiece.col < king.col){
                        for(int col = checkingPiece.col, row = checkingPiece.row; col < king.col; col++, row++){
                            for(Piece piece : simPieces){
                                if(piece != king && piece.color != currentColor && piece.canMove(col, row)){
                                    return false;
                                }
                            }
                        }
                    }

                    if(checkingPiece.col > king.col){
                        for(int col = checkingPiece.col, row = checkingPiece.row; col > king.col; col--, row++){
                            for(Piece piece : simPieces){
                                if(piece != king && piece.color != currentColor && piece.canMove(col, row)){
                                    return false;
                                }
                            }
                        }
                    }
                }

                if(checkingPiece.row > king.row){
                    if(checkingPiece.col < king.col){
                        for(int col = checkingPiece.col, row = checkingPiece.row; col < king.col; col++, row--){
                            for(Piece piece : simPieces){
                                if(piece != king && piece.color != currentColor && piece.canMove(col, row)){
                                    return false;
                                }
                            }
                        }
                    }

                    if(checkingPiece.col > king.col){
                        for(int col = checkingPiece.col, row = checkingPiece.row; col > king.col; col--, row--){
                            for(Piece piece : simPieces){
                                if(piece != king && piece.color != currentColor && piece.canMove(col, row)){
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }

        return true;
    }

    private boolean kingCanMove(Piece king){

        if(isValidMove(king, -1, -1)) {return true;}
        if(isValidMove(king, 0, -1)) {return true;}
        if(isValidMove(king, 1, -1)) {return true;}
        if(isValidMove(king, -1, 0)) {return true;}
        if(isValidMove(king, 1, 0)) {return true;}
        if(isValidMove(king, -1, 1)) {return true;}
        if(isValidMove(king, 0, 1)) {return true;}
        if(isValidMove(king, 1, 1)) {return true;}

        return false;
    }

    private boolean isValidMove(Piece king, int colPlus, int rowPlus){
        boolean validMove = false;

        king.col += colPlus;
        king.row += rowPlus;

        if(king.canMove(king.col, king.row)){
            if(king.hittingPiece != null){
                simPieces.remove(king.hittingPiece.getIndex());
            }
            if(!isIllegal(king)){
                validMove = true;
            }
        }
        king.resetPosition();
        copyPieces(pieces, simPieces);

        return validMove;
    }

    private void checkCasting(){
        if(castlingPiece != null){
            if(castlingPiece.col == 0)
                castlingPiece.col += 3;
            else if(castlingPiece.col == 7)
                castlingPiece.col -= 2;

            castlingPiece.x = castlingPiece.getCoordinate(castlingPiece.col);
        }
    }

    private boolean isStalemate(){
        int count = 0;

        for(Piece piece : simPieces){
            if(piece.color != currentColor){
                count++;
            }
        }

        if(count == 1){
            if(!kingCanMove(getKing(true)))
                return true;
        }

        return false;
    }

    private void changePlayer(){
        if( currentColor) {
            currentColor = false;

            for(Piece piece : pieces){
                if(piece.color == false){
                    piece.twoStepped = false;
                }
            }
        } else {
            currentColor = true;

            for(Piece piece : pieces){
                if(piece.color == true){
                    piece.twoStepped = false;
                }
            }
        }

        activePiece = null;
    }

    private boolean canPromote(){

        if(activePiece.type == Type.PAWN){
            if(currentColor && activePiece.row == 0 || !currentColor && activePiece.row == 7){
                promoPieces.clear();
                promoPieces.add(new Rook(currentColor,9,2));
                promoPieces.add(new Knight(currentColor,9,3));
                promoPieces.add(new Bishop(currentColor,9,4));
                promoPieces.add(new Queen(currentColor,9,5));
                return true;
            }
        }

        return false;
    }

    private void promoting(){
        if(mouse.pressed){
            for(Piece piece : promoPieces){
                if(piece.col == mouse.x / Board.SQUARE_SIZE && piece.row == mouse.y / Board.SQUARE_SIZE){
                    switch (piece.type) {
                        case ROOK: simPieces.add(new Rook(currentColor, activePiece.col, activePiece.row)); break;
                        case KNIGHT: simPieces.add(new Knight(currentColor, activePiece.col, activePiece.row)); break;
                        case BISHOP: simPieces.add(new Bishop(currentColor, activePiece.col, activePiece.row)); break;
                        case QUEEN: simPieces.add(new Queen(currentColor, activePiece.col, activePiece.row)); break;
                        default: break;
                    }
                    simPieces.remove(activePiece.getIndex());
                    copyPieces(simPieces, pieces);
                    activePiece = null;
                    promotion = false;
                    changePlayer();
                }
            }
        }
    }

    // Drawing
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        // Board
        board.draw(g2);

        // Pieces
        for(Piece p : simPieces){
            p.draw(g2);
        }

        if(activePiece != null){
            if(canMove) {
                if(isIllegal(activePiece) || opponentCanCaptureKing()) {
                    g2.setColor(Color.red);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                    g2.fillRect(activePiece.col * Board.SQUARE_SIZE, activePiece.row * Board.SQUARE_SIZE,
                            Board.SQUARE_SIZE, Board.SQUARE_SIZE);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                }
                else {
                    g2.setColor(Color.green);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
                    g2.fillRect(activePiece.col * Board.SQUARE_SIZE, activePiece.row * Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                }
            }

            activePiece.draw(g2);
        }

        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(new Font("Times New Roman", Font.BOLD, 40));
        g2.setColor(Color.WHITE);

        if(promotion){
            g2.drawString("Promote to:", 840, 150);
            for(Piece piece : promoPieces){
                g2.drawImage(piece.image, piece.getCoordinate(piece.col), piece.getCoordinate(piece.row),
                             Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
            }
        } else {
            if(currentColor) {
                g2.drawString("White's turn", 840, 550);
                if(checkingPiece != null && !checkingPiece.color){
                    g2.setColor(Color.red);
                    g2.drawString("The King", 840, 650);
                    g2.drawString("is in check", 840, 700);
                }
            }
            else {
                g2.drawString("Black's turn", 840, 250);
                if(checkingPiece != null && checkingPiece.color){
                    g2.setColor(Color.red);
                    g2.drawString("The King", 840, 100);
                    g2.drawString("is in check", 840, 150);
                }
            }
        }
        if(gameOver){
            String s = "";
            if(currentColor){
                s = "White Wins";
            } else {
                s = "Black Wins";
            }

            g2.setFont(new Font("Times New Roman", Font.BOLD, 90));
            g2.setColor(Color.GREEN);
            g2.drawString(s, 200, 420);
        }
        if(stalemate){
            g2.setFont(new Font("Times New Roman", Font.BOLD, 90));
            g2.setColor(Color.RED);
            g2.drawString("Stalemate", 200, 420);
        }
    }
}