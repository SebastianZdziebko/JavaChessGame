package piece;

import main.Board;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Piece {
    public BufferedImage image;
    public int x, y;
    public int col, row, preCol, preRow;
    public boolean color;

    public Piece(boolean color, int col, int row) {
        this.color = color;
        this.col = col;
        this.row = row;
        x = getCoordinate(col);
        y = getCoordinate(row);
        preCol = col;
        preRow = row;
    }

    public BufferedImage getImage( String imagePath ) {
        BufferedImage image = null;

        try {
            image = ImageIO.read(getClass().getResourceAsStream(imagePath + ".png"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return image;
    }

    public int getCoordinate(int value){
        return value * Board.SQUARE_SIZE;
    }

    public int getCol(int x){
        return (x + Board.HALF_SQUARE) / Board.SQUARE_SIZE;
    }

    public int getRow(int y){
        return (y + Board.HALF_SQUARE) / Board.SQUARE_SIZE;
    }

    public void updatePosition(){
        x = getCoordinate(col);
        y = getCoordinate(row);
        preCol = getCol(x);
        preRow = getRow(y);
    }


    public void draw(Graphics2D g2) {
        g2.drawImage(image, x, y, Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
    }
}