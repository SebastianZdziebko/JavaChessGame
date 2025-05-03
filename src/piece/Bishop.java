package piece;

import main.GamePanel;

public class Bishop extends Piece {
    public Bishop(boolean color, int col, int row) {
        super(color, col, row);

        if (color == GamePanel.currentColor)
            image = getImage("/piece/bishop-white");
        else
            image = getImage("/piece/bishop-black");
    }
}