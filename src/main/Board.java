package main;

import javax.swing.*;
import java.awt.*;

public class Board  extends JPanel {
    public static final  int WIDTH = 1100;
    public static final  int HEIGHT = 800;

    public Board(){
        setPreferredSize(new Dimension(WIDTH,HEIGHT));
        setBackground(Color.BLACK);
    }

    private void update(){

    }

    // Drawing
    public void paintComponent(Graphics g){
        super.paintComponent(g);
    }
}