package com.mycompany.tetris.master;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Tetris extends Application {
    public static final int MOVE = 50;
    public static final int SIZE = 50;
    public static int XMAX = 500;
    public static int YMAX = 900;
    public static int PANE_WIDTH = 800;
    public static int PANE_HEIGHT = 900;
    public static int[][] MESH = new int[XMAX / SIZE][YMAX / SIZE];
    private static Pane group = new Pane();
    private static Shapes object;
    private static Scene scene = new Scene(group, PANE_WIDTH, PANE_HEIGHT, Color.BLACK);
    private static IntegerProperty score = new SimpleIntegerProperty(0);
    private static int top = 0;
    private static boolean game = true;
    private static Shapes nextObj = Controller.makeRect();
    private static IntegerProperty linesNo = new SimpleIntegerProperty(0);
    private String playerName = "Player";

    public static void main(String[] args) {
        launch(args);
    }
    private Object fullTables;

      @Override
    public void start(Stage stage) throws Exception {
        for (int[] a : MESH) {
            Arrays.fill(a, 0);
        }

        TextInputDialog dialog = new TextInputDialog("Player");
        dialog.setTitle("INSTRUCTIONS");
        dialog.setHeaderText("How to play:\n\n- Arrow key up changes piece orientation.\n- Arrow keys left and right move the pieces to the sides of the board.\n- Arrow key down accelerates the descending speed of the pieces.");
        dialog.setContentText("Please enter your player name:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> playerName = name);

        Text scoretext = new Text();
        scoretext.textProperty().bind(score.asString(playerName + "'s Score: %d"));
        scoretext.setStyle("-fx-font: 20 arial; -fx-fill: black;");
        scoretext.setY(50);
        scoretext.setX(XMAX + 20);

        Rectangle border = new Rectangle(XMAX, YMAX);
        border.setFill(null);
        border.setStroke(Color.BLUE);

        Text level = new Text();
        level.textProperty().bind(linesNo.asString("Lines: %d"));
        level.setStyle("-fx-font: 20 arial; -fx-fill: black;");
        level.setY(200);
        level.setX(XMAX + 20);
        level.setFill(Color.GREEN);

        group.getChildren().addAll(scoretext, level, border);

        Shapes a = nextObj;
        group.getChildren().addAll(a.a, a.b, a.c, a.d);
        moveOnKeyPress(a);
        object = a;
        nextObj = Controller.makeRect();

        stage.setScene(scene);
        stage.setTitle("JR's Tetris Game");
        stage.show();

        Timer fall = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (game) {
                        MoveDown(object);
                        checkGameOver();
                    }
                });
            }
        };
        fall.schedule(task, 0, 300); // This schedules the task to run every 300 milliseconds
            }
    private void checkGameOver() {
        for (int x = 0; x < MESH.length; x++) {
            if (MESH[x][0] == 1) {  // Checksif the top row has any blocks
                gameOver();
                break;
            }
        }
        }
    private void gameOver() {
        Text over = new Text("GAME OVER");
        over.setFill(Color.RED);
        over.setStyle("-fx-font: 70 arial;");
        over.setY(250);
        over.setX(10);
        Platform.runLater(() -> group.getChildren().add(over));
        game = false;
    }
       

   private void moveOnKeyPress(Shapes shape) {
    scene.setOnKeyPressed((KeyEvent event) -> {
        if (!game) return;  // Ignore key presses if the game is over
        
        switch (event.getCode()) {
            case RIGHT:
                Controller.MoveRight(shape);
                break;
            case DOWN:
                MoveDown(shape);
                if (game) {  // Check if game is still active after moving down
                    score.set(score.get() + 1);
                }
                break;
            case LEFT:
                Controller.MoveLeft(shape);
                break;
            case UP:
                MoveTurn(shape);
                break;
        }
    });
}


        private void MoveDown(Shapes shape) {
        boolean canMoveDown = true;
        // Check each part of the shape for collision below
        for (Rectangle rect : Arrays.asList(shape.a, shape.b, shape.c, shape.d)) {
            if (rect.getY() == YMAX - SIZE || checkCollision(rect, 0, 1)) {
                canMoveDown = false;
                break;
            }
        }
        

        if (canMoveDown) {
            // If no collision, move each rectangle of the shape down
            shape.a.setY(shape.a.getY() + MOVE);
            shape.b.setY(shape.b.getY() + MOVE);
            shape.c.setY(shape.c.getY() + MOVE);
            shape.d.setY(shape.d.getY() + MOVE);
        } else {
            // If there is a collision, update the mesh to lock the piece in place
            updateMesh(shape);
            // Attempt to clear any filled rows
            RemoveRows(group);
            // Setup the next piece
            setupNextObject();
        }
    }


    private void updateMesh(Shapes shape) {
        // Add current shape to the MESH grid
        MESH[(int) shape.a.getX() / SIZE][(int) shape.a.getY() / SIZE] = 1;
        MESH[(int) shape.b.getX() / SIZE][(int) shape.b.getY() / SIZE] = 1;
        MESH[(int) shape.c.getX() / SIZE][(int) shape.c.getY() / SIZE] = 1;
        MESH[(int) shape.d.getX() / SIZE][(int) shape.d.getY() / SIZE] = 1;
    }

    private void setupNextObject() {
        // Check potential overlap
        if (anyBlockAtSpawnLocation()) {
            gameOver();
            return;
        }

        object = nextObj;
        nextObj = Controller.makeRect(); // considers current grid state
        Platform.runLater(() -> {
            group.getChildren().addAll(object.a, object.b, object.c, object.d);
        });
        moveOnKeyPress(object);
    }

    private boolean anyBlockAtSpawnLocation() {
        //  pieces spawn around the center at the top
        int spawnY = 0; // Top of the grid
        int centerX = XMAX / 2 / SIZE;
        for (int x = centerX - 2; x <= centerX + 2; x++) {
            if (x >= 0 && x < MESH.length && MESH[x][spawnY] == 1) {
                return true; // There is a block where the next piece would spawn
            }
        }
        return false;
    }

    private void RemoveRows(Pane pane) {
    Platform.runLater(() -> {
        List<Integer> fullRows = new ArrayList<>();
        // Find all the full rows
        for (int y = 0; y < MESH[0].length; y++) {
            boolean full = true;
            for (int x = 0; x < MESH.length; x++) {
                if (MESH[x][y] == 0) {
                    full = false;
                    break;
                }
            }
            if (full) {
                fullRows.add(y);
            }
        }

        // Remove all nodes on these rows from the scene and update the mesh
        for (int y : fullRows) {
            for (Node node : new ArrayList<>(pane.getChildren())) {
                if (node instanceof Rectangle && ((Rectangle) node).getY() == y * SIZE) {
                    pane.getChildren().remove(node);
                    MESH[(int) ((Rectangle) node).getX() / SIZE][y] = 0;
                }
            }
        }

        // Move down all blocks above the cleared lines
        for (int i = 0; i < fullRows.size(); i++) {
            int y = fullRows.get(i) - i; // Adjust for already moved rows
            // Move all rows above this one down by one
            for (int row = y - 1; row >= 0; row--) {
                for (int x = 0; x < MESH.length; x++) {
                    if (MESH[x][row] == 1) {
                        MESH[x][row] = 0;
                        MESH[x][row + 1] = 1;
                        for (Node node : new ArrayList<>(pane.getChildren())) {
                            if (node instanceof Rectangle && ((Rectangle) node).getX() == x * SIZE && ((Rectangle) node).getY() == row * SIZE) {
                                ((Rectangle) node).setY(((Rectangle) node).getY() + SIZE);
                            }
                        }
                    }
                }
            }
        }

        // Updatesthe score based on the number of lines cleared
        if (!fullRows.isEmpty()) {
            int scoreIncrement = 100 * full​Rows.size() + 200 * (fullRows.size() - 1);
            score.set(score.get() + scoreIncrement);
            linesNo.set(linesNo.get() + fullRows.size());
        }
    });
}


	private void MoveTurn(Shapes shape) {
		int f = shape.shape;
		Rectangle a = shape.a;
		Rectangle b = shape.b;
		Rectangle c = shape.c;
		Rectangle d = shape.d;
		switch (shape.getName()) {
		case "JShape":
			if (f == 1 && cB(a, 1, -1) && cB(c, -1, -1) && cB(d, -2, -2)) {
				MoveRight(shape.a);
				MoveDown(shape.a);
				MoveDown(shape.c);
				MoveLeft(shape.c);
				MoveDown(shape.d);
				MoveDown(shape.d);
				MoveLeft(shape.d);
				MoveLeft(shape.d);
				shape.changeShape();
				break;
			}
			if (f == 2 && cB(a, -1, -1) && cB(c, -1, 1) && cB(d, -2, 2)) {
				MoveDown(shape.a);
				MoveLeft(shape.a);
				MoveLeft(shape.c);
				MoveUp(shape.c);
				MoveLeft(shape.d);
				MoveLeft(shape.d);
				MoveUp(shape.d);
				MoveUp(shape.d);
				shape.changeShape();
				break;
			}
			if (f == 3 && cB(a, -1, 1) && cB(c, 1, 1) && cB(d, 2, 2)) {
				MoveLeft(shape.a);
				MoveUp(shape.a);
				MoveUp(shape.c);
				MoveRight(shape.c);
				MoveUp(shape.d);
				MoveUp(shape.d);
				MoveRight(shape.d);
				MoveRight(shape.d);
				shape.changeShape();
				break;
			}
			if (f == 4 && cB(a, 1, 1) && cB(c, 1, -1) && cB(d, 2, -2)) {
				MoveUp(shape.a);
				MoveRight(shape.a);
				MoveRight(shape.c);
				MoveDown(shape.c);
				MoveRight(shape.d);
				MoveRight(shape.d);
				MoveDown(shape.d);
				MoveDown(shape.d);
				shape.changeShape();
				break;
			}
			break;
                case "LShape":
			if (f == 1 && cB(a, 1, -1) && cB(c, 1, 1) && cB(b, 2, 2)) {
				MoveRight(shape.a);
				MoveDown(shape.a);
				MoveUp(shape.c);
				MoveRight(shape.c);
				MoveUp(shape.b);
				MoveUp(shape.b);
				MoveRight(shape.b);
				MoveRight(shape.b);
				shape.changeShape();
				break;
			}
			if (f == 2 && cB(a, -1, -1) && cB(b, 2, -2) && cB(c, 1, -1)) {
				MoveDown(shape.a);
				MoveLeft(shape.a);
				MoveRight(shape.b);
				MoveRight(shape.b);
				MoveDown(shape.b);
				MoveDown(shape.b);
				MoveRight(shape.c);
				MoveDown(shape.c);
				shape.changeShape();
				break;
			}
			if (f == 3 && cB(a, -1, 1) && cB(c, -1, -1) && cB(b, -2, -2)) {
				MoveLeft(shape.a);
				MoveUp(shape.a);
				MoveDown(shape.c);
				MoveLeft(shape.c);
				MoveDown(shape.b);
				MoveDown(shape.b);
				MoveLeft(shape.b);
				MoveLeft(shape.b);
				shape.changeShape();
				break;
			}
			if (f == 4 && cB(a, 1, 1) && cB(b, -2, 2) && cB(c, -1, 1)) {
				MoveUp(shape.a);
				MoveRight(shape.a);
				MoveLeft(shape.b);
				MoveLeft(shape.b);
				MoveUp(shape.b);
				MoveUp(shape.b);
				MoveLeft(shape.c);
				MoveUp(shape.c);
				shape.changeShape();
				break;
			}
			break;
		case "SquareShape":
			break;
		case "SShape":
			if (f == 1 && cB(a, -1, -1) && cB(c, -1, 1) && cB(d, 0, 2)) {
				MoveDown(shape.a);
				MoveLeft(shape.a);
				MoveLeft(shape.c);
				MoveUp(shape.c);
				MoveUp(shape.d);
				MoveUp(shape.d);
				shape.changeShape();
				break;
			}
			if (f == 2 && cB(a, 1, 1) && cB(c, 1, -1) && cB(d, 0, -2)) {
				MoveUp(shape.a);
				MoveRight(shape.a);
				MoveRight(shape.c);
				MoveDown(shape.c);
				MoveDown(shape.d);
				MoveDown(shape.d);
				shape.changeShape();
				break;
			}
			if (f == 3 && cB(a, -1, -1) && cB(c, -1, 1) && cB(d, 0, 2)) {
				MoveDown(shape.a);
				MoveLeft(shape.a);
				MoveLeft(shape.c);
				MoveUp(shape.c);
				MoveUp(shape.d);
				MoveUp(shape.d);
				shape.changeShape();
				break;
			}
			if (f == 4 && cB(a, 1, 1) && cB(c, 1, -1) && cB(d, 0, -2)) {
				MoveUp(shape.a);
				MoveRight(shape.a);
				MoveRight(shape.c);
				MoveDown(shape.c);
				MoveDown(shape.d);
				MoveDown(shape.d);
				shape.changeShape();
				break;
			}
			break;
                case "TShape":
			if (f == 1 && cB(a, 1, 1) && cB(d, -1, -1) && cB(c, -1, 1)) {
				MoveUp(shape.a);
				MoveRight(shape.a);
				MoveDown(shape.d);
				MoveLeft(shape.d);
				MoveLeft(shape.c);
				MoveUp(shape.c);
				shape.changeShape();
				break;
			}
			if (f == 2 && cB(a, 1, -1) && cB(d, -1, 1) && cB(c, 1, 1)) {
				MoveRight(shape.a);
				MoveDown(shape.a);
				MoveLeft(shape.d);
				MoveUp(shape.d);
				MoveUp(shape.c);
				MoveRight(shape.c);
				shape.changeShape();
				break;
			}
			if (f == 3 && cB(a, -1, -1) && cB(d, 1, 1) && cB(c, 1, -1)) {
				MoveDown(shape.a);
				MoveLeft(shape.a);
				MoveUp(shape.d);
				MoveRight(shape.d);
				MoveRight(shape.c);
				MoveDown(shape.c);
				shape.changeShape();
				break;
			}
			if (f == 4 && cB(a, -1, 1) && cB(d, 1, -1) && cB(c, -1, -1)) {
				MoveLeft(shape.a);
				MoveUp(shape.a);
				MoveRight(shape.d);
				MoveDown(shape.d);
				MoveDown(shape.c);
				MoveLeft(shape.c);
				shape.changeShape();
				break;
			}
			break;
		case "ZShape":
			if (f == 1 && cB(b, 1, 1) && cB(c, -1, 1) && cB(d, -2, 0)) {
				MoveUp(shape.b);
				MoveRight(shape.b);
				MoveLeft(shape.c);
				MoveUp(shape.c);
				MoveLeft(shape.d);
				MoveLeft(shape.d);
				shape.changeShape();
				break;
			}
			if (f == 2 && cB(b, -1, -1) && cB(c, 1, -1) && cB(d, 2, 0)) {
				MoveDown(shape.b);
				MoveLeft(shape.b);
				MoveRight(shape.c);
				MoveDown(shape.c);
				MoveRight(shape.d);
				MoveRight(shape.d);
				shape.changeShape();
				break;
			}
			if (f == 3 && cB(b, 1, 1) && cB(c, -1, 1) && cB(d, -2, 0)) {
				MoveUp(shape.b);
				MoveRight(shape.b);
				MoveLeft(shape.c);
				MoveUp(shape.c);
				MoveLeft(shape.d);
				MoveLeft(shape.d);
				shape.changeShape();
				break;
			}
			if (f == 4 && cB(b, -1, -1) && cB(c, 1, -1) && cB(d, 2, 0)) {
				MoveDown(shape.b);
				MoveLeft(shape.b);
				MoveRight(shape.c);
				MoveDown(shape.c);
				MoveRight(shape.d);
				MoveRight(shape.d);
				shape.changeShape();
				break;
			}
			break;
		case "IShape":
			if (f == 1 && cB(a, 2, 2) && cB(b, 1, 1) && cB(d, -1, -1)) {
				MoveUp(shape.a);
				MoveUp(shape.a);
				MoveRight(shape.a);
				MoveRight(shape.a);
				MoveUp(shape.b);
				MoveRight(shape.b);
				MoveDown(shape.d);
				MoveLeft(shape.d);
				shape.changeShape();
				break;
			}
			if (f == 2 && cB(a, -2, -2) && cB(b, -1, -1) && cB(d, 1, 1)) {
				MoveDown(shape.a);
				MoveDown(shape.a);
				MoveLeft(shape.a);
				MoveLeft(shape.a);
				MoveDown(shape.b);
				MoveLeft(shape.b);
				MoveUp(shape.d);
				MoveRight(shape.d);
				shape.changeShape();
				break;
			}
			if (f == 3 && cB(a, 2, 2) && cB(b, 1, 1) && cB(d, -1, -1)) {
				MoveUp(shape.a);
				MoveUp(shape.a);
				MoveRight(shape.a);
				MoveRight(shape.a);
				MoveUp(shape.b);
				MoveRight(shape.b);
				MoveDown(shape.d);
				MoveLeft(shape.d);
				shape.changeShape();
				break;
			}
			if (f == 4 && cB(a, -2, -2) && cB(b, -1, -1) && cB(d, 1, 1)) {
				MoveDown(shape.a);
				MoveDown(shape.a);
				MoveLeft(shape.a);
				MoveLeft(shape.a);
				MoveDown(shape.b);
				MoveLeft(shape.b);
				MoveUp(shape.d);
				MoveRight(shape.d);
				shape.changeShape();
				break;
			}
			break;
		}
	}

	

	private void MoveDown(Rectangle rect) {
		if (rect.getY() + MOVE < YMAX)
			rect.setY(rect.getY() + MOVE);

	}

	private void MoveRight(Rectangle rect) {
		if (rect.getX() + MOVE <= XMAX - SIZE)
			rect.setX(rect.getX() + MOVE);
	}

	private void MoveLeft(Rectangle rect) {
		if (rect.getX() - MOVE >= 0)
			rect.setX(rect.getX() - MOVE);
	}

	private void MoveUp(Rectangle rect) {
		if (rect.getY() - MOVE > 0)
			rect.setY(rect.getY() - MOVE);
	}

	
	private boolean moveA(Shapes shape) {
		return (MESH[(int) shape.a.getX() / SIZE][((int) shape.a.getY() / SIZE) + 1] == 1);
	}

	private boolean moveB(Shapes shape) {
		return (MESH[(int) shape.b.getX() / SIZE][((int) shape.b.getY() / SIZE) + 1] == 1);
	}

	private boolean moveC(Shapes shape) {
		return (MESH[(int) shape.c.getX() / SIZE][((int) shape.c.getY() / SIZE) + 1] == 1);
	}

	private boolean moveD(Shapes shape) {
		return (MESH[(int) shape.d.getX() / SIZE][((int) shape.d.getY() / SIZE) + 1] == 1);
	}

	private boolean cB(Rectangle rect, int x, int y) {
		boolean xb = false;
		boolean yb = false;
		if (x >= 0)
			xb = rect.getX() + x * MOVE <= XMAX - SIZE;
		if (x < 0)
			xb = rect.getX() + x * MOVE >= 0;
		if (y >= 0)
			yb = rect.getY() - y * MOVE > 0;
		if (y < 0)
			yb = rect.getY() + y * MOVE < YMAX;
		return xb && yb && MESH[((int) rect.getX() / SIZE) + x][((int) rect.getY() / SIZE) - y] == 0;
	}

    private boolean checkCollision(Rectangle rect, int dx, int dy) {
    int newX = ((int) rect.getX() / SIZE) + dx;
    int newY = ((int) rect.getY() / SIZE) + dy;

    // Check boundaries
    if (newX < 0 || newX >= MESH.length || newY < 0 || newY >= MESH[0].length) {
        return true;
    }

    // Check for collision with existing blocks in MESH
    return MESH[newX][newY] == 1;
}

}
