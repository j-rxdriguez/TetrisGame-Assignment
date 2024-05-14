package com.mycompany.tetris.master;

import javafx.scene.shape.Rectangle;

public class Controller {
    
    // Using constants from Tetris class
    public static final int MOVE = Tetris.MOVE;
    public static final int SIZE = Tetris.SIZE;
    public static int XMAX = Tetris.XMAX;
    public static int YMAX = Tetris.YMAX;
    public static int[][] MESH = Tetris.MESH;

    public static void MoveRight(Shapes shape) {
        if (shape.a.getX() + MOVE <= XMAX - SIZE && shape.b.getX() + MOVE <= XMAX - SIZE
                && shape.c.getX() + MOVE <= XMAX - SIZE && shape.d.getX() + MOVE <= XMAX - SIZE) {
            int movea = MESH[((int) shape.a.getX() / SIZE) + 1][((int) shape.a.getY() / SIZE)];
            int moveb = MESH[((int) shape.b.getX() / SIZE) + 1][((int) shape.b.getY() / SIZE)];
            int movec = MESH[((int) shape.c.getX() / SIZE) + 1][((int) shape.c.getY() / SIZE)];
            int moved = MESH[((int) shape.d.getX() / SIZE) + 1][((int) shape.d.getY() / SIZE)];
            if (movea == 0 && movea == moveb && moveb == movec && movec == moved) {
                shape.a.setX(shape.a.getX() + MOVE);
                shape.b.setX(shape.b.getX() + MOVE);
                shape.c.setX(shape.c.getX() + MOVE);
                shape.d.setX(shape.d.getX() + MOVE);
            }
        }
    }

    public static void MoveLeft(Shapes shape) {
        if (shape.a.getX() - MOVE >= 0 && shape.b.getX() - MOVE >= 0 && shape.c.getX() - MOVE >= 0
                && shape.d.getX() - MOVE >= 0) {
            int movea = MESH[((int) shape.a.getX() / SIZE) - 1][((int) shape.a.getY() / SIZE)];
            int moveb = MESH[((int) shape.b.getX() / SIZE) - 1][((int) shape.b.getY() / SIZE)];
            int movec = MESH[((int) shape.c.getX() / SIZE) - 1][((int) shape.c.getY() / SIZE)];
            int moved = MESH[((int) shape.d.getX() / SIZE) - 1][((int) shape.d.getY() / SIZE)];
            if (movea == 0 && movea == moveb && moveb == movec && movec == moved) {
                shape.a.setX(shape.a.getX() - MOVE);
                shape.b.setX(shape.b.getX() - MOVE);
                shape.c.setX(shape.c.getX() - MOVE);
                shape.d.setX(shape.d.getX() - MOVE);
            }
        }
    }

        public static Shapes makeRect() {
        int block = (int) (Math.random() * 100);
        String name;
        Rectangle a = new Rectangle(SIZE-1, SIZE-1), b = new Rectangle(SIZE-1, SIZE-1),
                 c = new Rectangle(SIZE-1, SIZE-1), d = new Rectangle(SIZE-1, SIZE-1);

        if (block < 15) {
            name = "JShape";
            a.setX(XMAX / 2 - SIZE);
            b.setX(XMAX / 2 - SIZE);
            b.setY(SIZE);
            c.setX(XMAX / 2);
            c.setY(SIZE);
            d.setX(XMAX / 2 + SIZE);
            d.setY(SIZE);
        } else if (block < 30) {
            name = "LShape";
            a.setX(XMAX / 2 + SIZE);
            b.setX(XMAX / 2 - SIZE);
            b.setY(SIZE);
            c.setX(XMAX / 2);
            c.setY(SIZE);
            d.setX(XMAX / 2 + SIZE);
            d.setY(SIZE);
        } else if (block < 45) {
            name = "SquareShape";
            a.setX(XMAX / 2 - SIZE);
            b.setX(XMAX / 2);
            c.setX(XMAX / 2 - SIZE);
            c.setY(SIZE);
            d.setX(XMAX / 2);
            d.setY(SIZE);
        } else if (block < 60) {
            name = "SShape";
            a.setX(XMAX / 2 + SIZE);
            b.setX(XMAX / 2);
            c.setX(XMAX / 2);
            c.setY(SIZE);
            d.setX(XMAX / 2 - SIZE);
            d.setY(SIZE);
        } else if (block < 75) {
            name = "TShape";
            a.setX(XMAX / 2 - SIZE);
            b.setX(XMAX / 2);
            c.setX(XMAX / 2);
            c.setY(SIZE);
            d.setX(XMAX / 2 + SIZE);
        } else if (block < 90) {
            name = "ZShape";
            a.setX(XMAX / 2 + SIZE);
            b.setX(XMAX / 2);
            c.setX(XMAX / 2 + SIZE);
            c.setY(SIZE);
            d.setX(XMAX / 2 + SIZE + SIZE);
            d.setY(SIZE);
        } else {
            name = "IShape";
            a.setX(XMAX / 2 - SIZE - SIZE);
            b.setX(XMAX / 2 - SIZE);
            c.setX(XMAX / 2);
            d.setX(XMAX / 2 + SIZE);
        }
        return new Shapes(a, b, c, d, name);
    }
}