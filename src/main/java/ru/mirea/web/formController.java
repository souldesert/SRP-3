package ru.mirea.web;

import com.google.common.io.Resources;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpMethod;

import java.io.File;
import java.math.BigInteger;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Alex on 27.01.2017.
 */
public class formController {

    private WebMain mainApp;
    private HttpClient httpClient;
    private SRPClient user;

    public formController() {
//        httpClient = new HttpClient();
//        try {
//            httpClient.start();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


        user = new SRPClient();
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public void setMainApp(WebMain mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private ImageView imageView;


    @FXML
    private void initialize() {
    }

    @FXML
    private void handleLogIn() {
        Request request = httpClient.newRequest("http://localhost:8080/key");
        request.method(HttpMethod.POST);
        request.param("U", username.getText());
        user.setUsername(username.getText());
        user.setPassword(password.getText());
        request.param("A", user.A().toString());
        try {
            ContentResponse response = request.send();
            if (response.getHeaders().get("status").equals("FOUND")) {
                user.set_s(response.getHeaders().get("s"));
                user.set_B(new BigInteger(response.getHeaders().get("B")));
                user.set_u(new BigInteger(response.getHeaders().get("u")));
                user.K();

                request = httpClient.newRequest("http://localhost:8080/verify");
                request.method(HttpMethod.POST);
                request.param("M", user.M());
                response = request.send();
                if (response.getHeaders().get("status").equals("OK") & user.checkAMK(response.getHeaders().get("AMK"))) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Результат авторизации");
                    alert.setHeaderText("Авторизация пройдена успешно!");
                    alert.setContentText("Вы авторизовались в системе.");

                    request = httpClient.newRequest("http://localhost:8080/complete");
                    request.method(HttpMethod.POST);
                    request.param("isLoggedIn", "true");
                    request.send();

                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Результат авторизации");
                    alert.setHeaderText("Ошибка авторизации");
                    alert.setContentText("Неверный пароль.");

                    alert.showAndWait();

                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Результат авторизации");
                alert.setHeaderText("Ошибка авторизации");
                alert.setContentText("Пользователь не найден.");

                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRegister() {
        Request request = httpClient.newRequest("http://localhost:8080/signup");
        request.method(HttpMethod.GET);
        try {
            ContentResponse response = request.send();
            String s = response.getHeaders().get("salt");
            user.setUsername(username.getText());
            user.setPassword(password.getText());
            String v = user.v(s).toString();
            request.method(HttpMethod.POST);
            request.param("U", username.getText());
            request.param("v", v);
            response = request.send();
            if (response.getHeaders().get("status").equals("ERROR")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Результат регистрации");
                alert.setHeaderText("Ошибка регистрации");
                alert.setContentText("Пользователь уже существует.");

                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Результат регистрации");
                alert.setHeaderText("Регистрация пройдена успешно!");
                alert.setContentText("Вы зарегистрировались в системе.");

                alert.showAndWait();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDownload() {
        Request request = httpClient.newRequest("http://localhost:8080/complete");
        request.method(HttpMethod.GET);
        try {
            ContentResponse response = request.send();
            if (response.getHeaders().get("status").equals("OK")) {
                byte[] downloaded = response.getContent();
                String fileName = String.valueOf(new Date().getTime()) + ".jpg";
                File image = new File("src/main/resources/" + fileName);
                FileUtils.writeByteArrayToFile(image, downloaded);
                //Image sceneImage = new Image(Resources.getResource(fileName).toString());
                Image sceneImage = new Image(image.toURI().toString());
                imageView.setImage(sceneImage);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Результат скачивания");
                alert.setHeaderText("Ошибка скачивания");
                alert.setContentText("Вы не авторизовались.");

                alert.showAndWait();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
