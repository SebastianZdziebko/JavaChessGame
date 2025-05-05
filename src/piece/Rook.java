package piece;

import main.GamePanel;

public class Rook extends Piece {
    public Rook(boolean color, int col, int row) {
        super(color, col, row);

        if (color == GamePanel.currentColor)
            image = getImage("/piece/rook-white");
        else
            image = getImage("/piece/rook-black");
    }

    public boolean canMove(int targetCol, int targetRow) {
        if(isWithinBoard(targetCol, targetRow) && !isSameSquare(targetCol, targetRow)){
            if(targetCol == preCol || targetRow == preRow){
                if(isValidSquare(targetCol, targetRow) && pieceIsOnStraightLine(targetCol, targetRow) == false){
                    return true;
                }
            }
        }
        return false;
    }
}