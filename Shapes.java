package com.mycompany.tetris.master;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Shapes {
    Rectangle a;
    Rectangle b;
    Rectangle c;
    Rectangle d;
    Color color;
    private String name;
    public int shape = 1;

    public Shapes(Rectangle a, Rectangle b, Rectangle c, Rectangle d, String name) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.name = name;

        switch (name) {
            case "JShape":
                color = Color.BLUE;  // Piece colours
                break;
            case "LShape":
                color = Color.ORANGE;
                break;
            case "SquareShape":
                color = Color.YELLOW;
                break;
            case "SShape":
                color = Color.GREEN;
                break;
            case "TShape":
                color = Color.PURPLE;
                break;
            case "ZShape":
                color = Color.RED;
                break;
            case "IShape":
                color = Color.CYAN;
                break;
            default:
                color = Color.BLACK; // Default color if none of the names match
                break;
        }
        this.a.setFill(color);
        this.b.setFill(color);
        this.c.setFill(color);
        this.d.setFill(color);
    }

    public String getName() {
        return name;
    }

    public void changeShape() {
        if (shape != 4) {
            shape++;
        } else {
            shape = 1;
        }
    }
}
