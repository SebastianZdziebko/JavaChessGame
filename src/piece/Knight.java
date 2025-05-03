package piece;

import main.GamePanel;

public class Knight extends Piece {
    public Knight(boolean color, int col, int row) {
        super(color, col, row);

        if (color == GamePanel.currentColor)
            image = getImage("/piece/knight-white");
        else
            image = getImage("/piece/knight-black");
    }
}