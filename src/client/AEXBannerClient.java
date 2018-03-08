package client;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.rmi.RemoteException;

public class AEXBannerClient extends Application {

    private static final int WIDTH = 1000;
    private static final int HEIGHT = 100;
    private static final int NANO_TICKS = 20000000;

    private static final double TEXT_SPEED = 5.0;

    private Text text;
    private double textLength;
    private double textPosition;
    private BannerController controller;
    private AnimationTimer animationTimer;

    @Override
    public void start(Stage primaryStage) throws RemoteException {

        Font font = new Font("Arial", HEIGHT);
        text = new Text();
        text.setFont(font);
        text.setFill(Color.BLACK);

        Pane root = new Pane();
        root.getChildren().add(text);
        Scene scene = new Scene(root, WIDTH, HEIGHT + 20);

        primaryStage.setTitle("AEX banner");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.toFront();

        // Start animation: text moves from right to left
        animationTimer = new AnimationTimer() {
            private long prevUpdate;

            @Override
            public void handle(long now) {
                long lag = now - prevUpdate;
                if (lag >= NANO_TICKS) {
                    textPosition -= TEXT_SPEED;
                    text.relocate(textPosition,0);
                    prevUpdate = now;
                }

                if ((textPosition + textLength < 0)) {
                    textPosition = WIDTH;
                }
            }

            @Override
            public void start() {
                prevUpdate = System.nanoTime();
                textPosition = WIDTH;
                text.relocate(textPosition, 0);
                setKoersen("Nothing to display");
                super.start();
            }
        };
        animationTimer.start();

        controller = new BannerController(this);
    }

    public void setKoersen(final String koersen) {
        Platform.runLater(() -> {
            text.setText(koersen);
            textLength = text.getLayoutBounds().getWidth();
        });
    }

    @Override
    public void stop() {
        animationTimer.stop();
        controller.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
