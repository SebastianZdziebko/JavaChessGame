package piece;

import main.Type;

public class Queen extends Piece {
    public Queen(boolean color, int col, int row) {
        super(color, col, row);

        type = Type.QUEEN;

        if (color)
            image = getImage("/piece/queen-white");
        else
            image = getImage("/piece/queen-black");
    }

    public boolean canMove(int targetCol, int targetRow) {
        if(isWithinBoard(targetCol, targetRow) && !isSameSquare(targetCol, targetRow)){

            if(targetCol == preCol || targetRow == preRow) {
                if (isValidSquare(targetCol, targetRow) && !pieceIsOnStraightLine(targetCol, targetRow))
                    return true;
            }

            if(Math.abs(targetCol - preCol) == Math.abs(targetRow - preRow)) {
                if (isValidSquare(targetCol, targetRow) && !pieceIsOnDiagonalLine(targetCol, targetRow))
                    return true;
            }
        }
        return false;
    }
}