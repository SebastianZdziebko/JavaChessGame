package piece;

import main.GamePanel;

public class Queen extends Piece {
    public Queen(boolean color, int col, int row) {
        super(color, col, row);

        if (color == GamePanel.currentColor)
            image = getImage("/piece/queen-white");
        else
            image = getImage("/piece/queen-black");
    }
}