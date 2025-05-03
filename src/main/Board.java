package main;

import java.awt.*;

public class Board {
    final int MAX_COLUMNS = 8;
    final int MAX_ROWS = 8;
    public static final int SQUARE_SIZE = 100;
    public static final int HALF_SQUARE = SQUARE_SIZE / 2;

    public void draw(Graphics2D g2) {
        for (int row = 0; row < MAX_ROWS; row++) {
            for (int col = 0; col < MAX_COLUMNS; col++) {
                if((row + col) % 2 == 0) {
                    g2.setColor(new Color(235, 235, 210));
                } else {
                    g2.setColor(new Color(120, 150, 85));
                }
                g2.fillRect(col * SQUARE_SIZE, row * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
            }
        }
    }
}