package piece;

import main.GamePanel;

public class Pawn extends Piece {
    public Pawn(boolean color, int col, int row) {
        super(color, col, row);

        if (color == GamePanel.currentColor)
            image = getImage("/piece/pawn-white");
        else
            image = getImage("/piece/pawn-black");
    }
}