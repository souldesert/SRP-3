package ru.mirea.web;

import com.google.common.io.Resources;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;

import java.io.IOException;

/**
 * Created by Alex on 27.01.2017.
 */
public class WebMain extends Application {
    private Stage primaryStage;
    private AnchorPane RootWindow;
    private static Server server;
    private static HttpClient httpClient;

    public WebMain() {

    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        initRootWindow();
        this.getPrimaryStage().show();
        primaryStage.setOnCloseRequest(event -> {
            //System.out.println("Stage is closing");
            try {
                httpClient.stop();
                server.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Save file
        });
    }

    private void initRootWindow() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Resources.getResource("form.fxml"));
            RootWindow = loader.load();
            // Отображаем сцену, содержащую корневой макет.
            Scene scene = new Scene(RootWindow);
            primaryStage.setScene(scene);

            formController controller = loader.getController();
            controller.setMainApp(this);
            controller.setHttpClient(httpClient);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        server = new Server(8080);
        ServletContextHandler handler = new ServletContextHandler();
        //handler.addServlet(MyServlet.class, "/test1");
        handler.addServlet(SignUp.class, "/signup");
        handler.addServlet(Key.class, "/key");
        handler.addServlet(Verify.class, "/verify");
        handler.addServlet(Complete.class, "/complete");
        handler.setSessionHandler(new SessionHandler());

        server.setHandler(handler);

        httpClient = new HttpClient();

        try {
            server.start();
            httpClient.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        launch(args);
    }

}
