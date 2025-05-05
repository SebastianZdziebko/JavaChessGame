package piece;

import main.GamePanel;
import main.Type;

public class Pawn extends Piece {
    public Pawn(boolean color, int col, int row) {
        super(color, col, row);

        type = Type.PAWN;

        if (color)
            image = getImage("/piece/pawn-white");
        else
            image = getImage("/piece/pawn-black");
    }

    public boolean canMove(int targetCol, int targetRow) {
        if(isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol, targetRow) == false){

            int moveValue;
            if(color)
                moveValue = -1;
            else
                moveValue = 1;

            hittingPiece = getHittingPiece(targetCol, targetRow);
            if(targetCol == preCol && targetRow == preRow + moveValue && hittingPiece == null){
                return true;
            }

            if(targetCol == preCol && targetRow == preRow + moveValue * 2 && moved == false &&
                pieceIsOnStraightLine(targetCol, targetRow) == false && hittingPiece == null){
                return true;
            }

            if(Math.abs(targetCol - preCol) == 1 && targetRow == preRow + moveValue && hittingPiece != null &&
            hittingPiece.color != color){
                return true;
            }

            if(Math.abs(targetCol - preCol) == 1 && targetRow == preRow + moveValue){
                for(Piece piece : GamePanel.simPieces)
                if(piece.col == targetCol && piece.row == preRow && piece.twoStepped == true){
                    hittingPiece = piece;
                    return true;
                }
            }

        }
        return false;
    }
}