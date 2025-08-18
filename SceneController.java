package com.example.agenda;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.sql.SQLOutput;

public class SceneController {
    @FXML
    private TableView<Person> tableView;
    @FXML
    private TableColumn<Person, Integer> colId;
    @FXML
    private TableColumn<Person, String> colName;
    @FXML
    private TableColumn<Person, String> colAddress;
    @FXML
    private TableColumn<Person, String> colPhoneNumbers;;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtAddress;
    @FXML
    private Button addNumber;
    @FXML
    private Button okButton;
    @FXML
    private Button saveButton;
    @FXML
    private TextField txtPhoneNumber;
    private ObservableList<Person> people;
    private AgendaDB agenda;
    private boolean insertSelected;
    private boolean extraPhoneNumber;

    public SceneController() {
        people = FXCollections.observableArrayList();
        agenda = new AgendaDB();
        insertSelected = true;
        extraPhoneNumber = false;
    }

    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colPhoneNumbers.setCellValueFactory(new PropertyValueFactory<>("phoneNumbers"));
        people.addAll(agenda.getPeople());
        tableView.setItems(people);
    }

    @FXML
    protected void insertClick(ActionEvent event) throws IOException {
        setElementsVisible(true);
        insertSelected = true;
    }

    @FXML
    protected void okClick(ActionEvent event) throws IOException {
        String name = txtName.getText();
        String address = txtAddress.getText();
        String phone = txtPhoneNumber.getText();
        agenda.insertPerson(name, address, phone);
        people.setAll(agenda.getPeople());

        Parent root = FXMLLoader.load(getClass().getResource("Scene2.fxml"));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        okButton.setVisible(false);
    }


    @FXML
    protected void removeClick(ActionEvent event) throws IOException {
        TableView.TableViewSelectionModel<Person> selectionModel = tableView.getSelectionModel();
        Person selectedPerson = selectionModel.getSelectedItem();

        if (selectedPerson != null) {
            agenda.deletePerson(selectedPerson.getId());
        }

        Parent root = FXMLLoader.load(getClass().getResource("Scene2.fxml"));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    protected void editClick(ActionEvent event) throws IOException {
        setElementsVisible(true);
        insertSelected = false;
        Person selectedPerson = tableView.getSelectionModel().getSelectedItem();
        if (selectedPerson != null) {
            txtName.setText(selectedPerson.getName());
            txtAddress.setText(selectedPerson.getAddress());
            txtPhoneNumber.setText(selectedPerson.getPhoneNumbers());
        }
    }

    @FXML
    protected void addOtherPhoneNumber(ActionEvent event) throws IOException {
        txtPhoneNumber.clear();
        extraPhoneNumber = true;
    }

    @FXML
    protected void saveClick(ActionEvent event) throws IOException {
        Person selectedPerson = tableView.getSelectionModel().getSelectedItem();
        if (insertSelected && !extraPhoneNumber) {
            String name = txtName.getText();
            String address = txtAddress.getText();
            String phone = txtPhoneNumber.getText();
            agenda.insertPerson(name, address, phone);
            people.setAll(agenda.getPeople());

            Parent root = FXMLLoader.load(getClass().getResource("Scene2.fxml"));
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } else if (!insertSelected && !extraPhoneNumber) {
            if (selectedPerson != null) {
                selectedPerson.setName(txtName.getText());
                selectedPerson.setAddress(txtAddress.getText());
                selectedPerson.setPhoneNumbers(txtPhoneNumber.getText());
                agenda.updatePerson(selectedPerson.getId(), selectedPerson.getName(), selectedPerson.getName());
                people.clear();
                people.addAll(agenda.getPeople());
                tableView.setItems(people);
            }
        } else if (!insertSelected && extraPhoneNumber) {
            agenda.addPhone(selectedPerson.getId(), txtPhoneNumber.getText());

            Parent root = FXMLLoader.load(getClass().getResource("Scene2.fxml"));
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
        insertSelected = false;
        setElementsVisible(false);
    }

    private void setElementsVisible(boolean status) {
        txtName.setDisable(!status);
        txtAddress.setDisable(!status);
        txtPhoneNumber.setDisable(!status);
        saveButton.setDisable(!status);
        addNumber.setDisable(!status);
    }


}
