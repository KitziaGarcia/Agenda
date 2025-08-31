//package com.example.agenda;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Node;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.scene.control.*;
//import javafx.scene.control.cell.PropertyValueFactory;
//import javafx.event.ActionEvent;
//import javafx.scene.control.cell.TextFieldListCell;
//import javafx.stage.Stage;
//
//import java.io.IOException;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//public class SceneController2 {
//    @FXML
//    private TableView<Person> tableView;
//    @FXML
//    private ListView<String> listViewAddresses;
//    @FXML
//    private ListView<String> listViewPhones;
//    @FXML
//    private TableColumn<Person, Integer> colId;
//    @FXML
//    private TableColumn<Person, String> colName;
//    @FXML
//    private TableColumn<Person, String> colAddress;
//    @FXML
//    private TableColumn<Person, String> colPhoneNumbers;;
//    @FXML
//    private TextField txtName;
//    private ObservableList<Person> people;
//    private AgendaDB agenda;
//
//    public SceneController2() {
//        people = FXCollections.observableArrayList();
//    }
//
//    public void initialize() {
//        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
//        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
//        colAddress.setCellValueFactory(new PropertyValueFactory<>("addresses"));
//        colPhoneNumbers.setCellValueFactory(new PropertyValueFactory<>("phoneNumbers"));
//        people.addAll(agenda.getPeople());
//        tableView.setItems(people);
//    }
//
//    @FXML
//    protected void saveClick(ActionEvent event) throws IOException, SQLException {
//        updateTableView();
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("Scene.fxml"));
//        Parent root = loader.load();
//        SceneController controller = loader.getController();
//        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
//        Scene scene = new Scene(root);
//        stage.setScene(scene);
//        stage.show();
//    }
//
//    private void updateTableView() {
//        people.clear();
//        agenda.getData();
//        people.addAll(agenda.getPeople());
//        tableView.setItems(people);
//    }
//
//    public void loadPersonInfo(Person selectedPerson) {
//        txtName.setText(selectedPerson.getName());
//
//        listViewAddresses.getItems().clear();
//        listViewPhones.getItems().clear();
//
//        List<String> addresses = agenda.getAddressesByPerson(selectedPerson.getId());
//        listViewAddresses.getItems().addAll(addresses);
//
//        List<String> phones = agenda.getPhonesByPerson(selectedPerson.getId());
//        listViewPhones.getItems().addAll(phones);
//    }
//
//    public void editPerson(Person selectedPerson) {
//        listViewAddresses.setEditable(true);
//        listViewAddresses.setCellFactory(TextFieldListCell.forListView());
//
//        listViewAddresses.setOnEditCommit(event -> {
//            int index = event.getIndex();
//            String newValue = event.getNewValue();
//            listViewAddresses.getItems().set(index, newValue);
//            agenda.updatePerson(selectedPerson.getId(), txtName.getText(), new ArrayList<>(listViewAddresses.getItems()), new ArrayList<>(listViewPhones.getItems()));
//        });
//
//        listViewPhones.setEditable(true);
//        listViewPhones.setCellFactory(TextFieldListCell.forListView());
//
//        listViewPhones.setOnEditCommit(event -> {
//            int index = event.getIndex();
//            String newValue = event.getNewValue();
//            listViewPhones.getItems().set(index, newValue);
//            agenda.updatePerson(selectedPerson.getId(), txtName.getText(), new ArrayList<>(listViewAddresses.getItems()), new ArrayList<>(listViewPhones.getItems()));
//        });
//    }
//
//    public void setPerson(Person person) {
//        txtName.setText(person.getName());
//    }
//
//    public void setAddresses(List<String> addresses) {
//        listViewAddresses.getItems().setAll(addresses);
//        listViewAddresses.setEditable(true);
//        listViewAddresses.setCellFactory(TextFieldListCell.forListView());
//    }
//
//    public void setPhones(List<String> phones) {
//        listViewPhones.getItems().setAll(phones);
//    }
//
//}