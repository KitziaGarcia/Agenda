package com.example.agenda;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class AgendaApp extends Application {
    private IPersonData personData;
    private IPhoneNumbersData phoneData;
    private IAddressesData addressesData;
    private AgendaDB agenda;

    @Override
    public void start(Stage stage) throws IOException {
        personData = new PersonData();
        phoneData = new PhoneNumbersData();
        addressesData = new AddressesData();
        agenda = new AgendaDB(personData, phoneData, addressesData);
        FXMLLoader fxmlLoader = new FXMLLoader(AgendaApp.class.getResource("Scene.fxml"));
        fxmlLoader.setControllerFactory(param -> new SceneController(agenda));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.sizeToScene();
        stage.setTitle("Agenda");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}