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
    Piece activePiece;

    // Color
    public static boolean currentColor = true;

    boolean canMove;
    boolean validSquare;

    public GamePanel(){
        setPreferredSize(new Dimension(WIDTH,HEIGHT));
        setBackground(Color.BLACK);
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
        pieces.add(new King(true, 4, 4));

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
        if(mouse.pressed) {
            if (activePiece == null) {
                for (Piece piece : simPieces) {
                    if (piece.color == currentColor &&
                            piece.col == mouse.x / Board.SQUARE_SIZE &&
                            piece.row == mouse.y / Board.SQUARE_SIZE) {
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
                }
                else {
                    copyPieces(pieces, simPieces);
                    activePiece.resetPosition();
                    activePiece = null;
                }

                activePiece = null;
            }
        }
    }

    private void simulate(){
        canMove = false;
        validSquare = false;

        copyPieces(pieces, simPieces);

        activePiece.x = mouse.x - Board.HALF_SQUARE;
        activePiece.y = mouse.y - Board.HALF_SQUARE;
        activePiece.col = activePiece.getCol(activePiece.x);
        activePiece.row = activePiece.getRow(activePiece.y);

        if(activePiece.canMove(activePiece.col, activePiece.row)){
            canMove = true;

            if(activePiece.hittingPiece != null){
                simPieces.remove(activePiece.hittingPiece.getIndex());
            }

            validSquare = true;
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
                g2.setColor(Color.red);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                g2.fillRect(activePiece.col * Board.SQUARE_SIZE, activePiece.row * Board.SQUARE_SIZE,
                        Board.SQUARE_SIZE, Board.SQUARE_SIZE);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            }

            activePiece.draw(g2);
        }
    }
}