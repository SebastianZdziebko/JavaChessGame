package piece;

import main.Board;
import main.GamePanel;
import main.Type;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Piece {
    public Type type;
    public BufferedImage image;
    public int x, y;
    public int col, row, preCol, preRow;
    public boolean color;
    public Piece hittingPiece;
    public boolean moved, twoStepped;

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

    public int getIndex(){
        for( int i = 0; i < GamePanel.simPieces.size(); i++ ){
            if(GamePanel.simPieces.get(i) == this){
                return i;
            }
        }
        return 0;
    }

    public void updatePosition(){
        if(type == Type.PAWN){
            if(Math.abs(row - preRow) == 2){
                twoStepped = true;
            }
        }

        x = getCoordinate(col);
        y = getCoordinate(row);
        preCol = getCol(x);
        preRow = getRow(y);
        moved = true ;
    }

    public void resetPosition(){
        col = preCol;;
        row = preRow;
        x = getCoordinate(col);
        y = getCoordinate(row);
    }

    public boolean canMove(int targetCol, int targetRow){
        return false;
    }

    public boolean isWithinBoard(int targetCol, int targetRow){
        if(targetCol >= 0 && targetCol <= 7 && targetRow >= 0 && targetRow <= 7)
            return true;
        return false;
    }

    public boolean isSameSquare(int targetCol, int targetRow){
        if(targetCol == preCol && targetRow == preRow)
            return true;
        return false;
    }

    public Piece getHittingPiece(int targetCol, int targetRow){
        for(Piece piece : GamePanel.simPieces){
            if(piece.col == targetCol && piece.row == targetRow && piece != this){
                return piece;
            }
        }
        return null;
    }

    public boolean isValidSquare(int targetCol, int targetRow){
        hittingPiece = getHittingPiece(targetCol, targetRow);

        if(hittingPiece == null) {
            return true;
        } else {
            if (hittingPiece.color != this.color)
                return true;
            else
                hittingPiece = null;
        }
        return false;
    }

    public boolean pieceIsOnStraightLine(int targetCol, int targetRow){
        // Left
        for(int i = preCol - 1; i > targetCol; i--){
            for(Piece piece : GamePanel.simPieces){
                if(piece.col == i && piece.row == targetRow){
                    hittingPiece = piece;
                    return true;
                }
            }
        }

        // Right
        for(int i = preCol + 1; i < targetCol; i++){
            for(Piece piece : GamePanel.simPieces){
                if(piece.col == i && piece.row == targetRow){
                    hittingPiece = piece;
                    return true;
                }
            }
        }

        // Up
        for(int i = preRow - 1; i > targetRow; i--){
            for(Piece piece : GamePanel.simPieces){
                if(piece.col == targetCol && piece.row == i){
                    hittingPiece = piece;
                    return true;
                }
            }
        }

        // Down
        for(int i = preRow + 1; i < targetRow; i++){
            for(Piece piece : GamePanel.simPieces){
                if(piece.col == targetCol && piece.row == i){
                    hittingPiece = piece;
                    return true;
                }
            }
        }

        return false;
    }

    public boolean pieceIsOnDiagonalLine(int targetCol, int targetRow){
        if(targetRow < preRow) {
            // Up left
            for (int i = preCol - 1; i > targetCol; i--) {
                int diff = Math.abs(i - preCol);
                for(Piece piece : GamePanel.simPieces){
                    if(piece.col == i && piece.row == preRow - diff){
                        hittingPiece = piece;
                        return true;
                    }
                }
            }

            // Up right
            for (int i = preCol + 1; i < targetCol; i++) {
                int diff = Math.abs(i - preCol);
                for(Piece piece : GamePanel.simPieces){
                    if(piece.col == i && piece.row == preRow - diff){
                        hittingPiece = piece;
                        return true;
                    }
                }
            }
        }

        if(targetRow > preRow) {
            // Down Left
            for (int i = preCol - 1; i > targetCol; i--) {
                int diff = Math.abs(i - preCol);
                for(Piece piece : GamePanel.simPieces){
                    if(piece.col == i && piece.row == preRow + diff){
                        hittingPiece = piece;
                        return true;
                    }
                }
            }

            // Down right
            for (int i = preCol + 1; i < targetCol; i++) {
                int diff = Math.abs(i - preCol);
                for(Piece piece : GamePanel.simPieces){
                    if(piece.col == i && piece.row == preRow + diff){
                        hittingPiece = piece;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void draw(Graphics2D g2) {
        g2.drawImage(image, x, y, Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
    }
}