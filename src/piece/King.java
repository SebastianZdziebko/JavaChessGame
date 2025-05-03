package piece;

import main.GamePanel;

public class King extends Piece {
    public King(boolean color, int col, int row) {
        super(color, col, row);

        if (color == GamePanel.currentColor)
            image = getImage("/piece/king-white");
        else
            image = getImage("/piece/king-black");
    }
}