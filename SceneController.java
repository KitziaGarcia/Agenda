package com.example.agenda;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.event.ActionEvent;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SceneController {
    @FXML
    private TableView<Person> tableView;
    @FXML
    private ListView<String> listViewAddresses;
    @FXML
    private ListView<String> listViewPhones;
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
    private Button addNumberButton;
    @FXML
    private Button addAddressButton;
    @FXML
    private Button removeButton;
    @FXML
    private Button editButton;
    @FXML
    private Button saveButton;
    @FXML
    private TextField txtPhoneNumber;
    private ObservableList<Person> people;
    private AgendaDB agenda;
    private boolean insertSelected;
    private boolean extraPhoneNumber;
    private boolean extraAddress;
    private boolean scene2Showing = false;

    public SceneController() {
        people = FXCollections.observableArrayList();
        agenda = new AgendaDB();
        insertSelected = false;
        extraPhoneNumber = false;
        extraAddress = false;
        scene2Showing = false;
    }

    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colPhoneNumbers.setCellValueFactory(new PropertyValueFactory<>("phoneNumbers"));
        people.addAll(agenda.getPeople());
        tableView.setItems(people);
        if (!scene2Showing) {
            setElementsVisible(true);
        }
    }

    @FXML
    protected void insertClick(ActionEvent event) throws IOException {
        setElementsVisible(true);
        insertSelected = true;
    }

    @FXML
    protected void removeClick(ActionEvent event) throws IOException {
        TableView.TableViewSelectionModel<Person> selectionModel = tableView.getSelectionModel();
        Person selectedPerson = selectionModel.getSelectedItem();
        confirmDelete(selectedPerson);
    }

    @FXML
    protected void editClick(ActionEvent event) throws IOException {
        setElementsVisible(true);
        insertSelected = false;
        Person selectedPerson = tableView.getSelectionModel().getSelectedItem();
        saveButton.setDisable(false);
        if (selectedPerson != null) {
            scene2Showing = true;
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Scene2.fxml"));
            Parent root = loader.load();
            SceneController controller = loader.getController();
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

            txtName = controller.txtName;
            txtName.setDisable(false);
            txtName.setText(selectedPerson.getName());
            controller.loadPersonInfo(selectedPerson);
            listViewAddresses = controller.listViewAddresses;
            listViewPhones = controller.listViewPhones;

            listViewAddresses.setEditable(true);
            listViewAddresses.setCellFactory(TextFieldListCell.forListView());
            editPerson(selectedPerson);
        }
    }

    @FXML
    protected void addOtherPhoneNumber(ActionEvent event) throws IOException {
        Person selectedPerson = tableView.getSelectionModel().getSelectedItem();
        if (selectedPerson != null) {
            txtName.setText(selectedPerson.getName());
            txtName.setDisable(true);
            txtAddress.setDisable(true);
        }
        txtPhoneNumber.setDisable(false);
        extraPhoneNumber = true;
        saveButton.setDisable(false);
    }

    @FXML
    protected void addOtherAddress(ActionEvent event) throws IOException {
        Person selectedPerson = tableView.getSelectionModel().getSelectedItem();

        if (selectedPerson != null) {
            txtName.setText(selectedPerson.getName());
            txtName.setDisable(true);
            txtPhoneNumber.setDisable(true);
        }
        txtAddress.setDisable(false);
        extraAddress = true;
        saveButton.setDisable(false);
    }

    @FXML
    protected void saveClick(ActionEvent event) throws IOException, SQLException {
        Person selectedPerson = tableView.getSelectionModel().getSelectedItem();
        if (insertSelected && !extraPhoneNumber && !extraAddress) {
            String name = txtName.getText();
            String address = txtAddress.getText();
            String phone = txtPhoneNumber.getText();
            agenda.insertPerson(name, address, phone);
            updateTableView();
            setElementsVisible(true);
            clearTextFields();
        } else if (!insertSelected && !extraPhoneNumber && !extraAddress) {
            scene2Showing = false;
            FXMLLoader loader = new FXMLLoader(AgendaApp.class.getResource("Scene.fxml"));
            Parent root = loader.load();
            SceneController controller = loader.getController();
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            controller.updateTableView();
        } else if (!insertSelected && extraPhoneNumber && !extraAddress) {
            agenda.addPhone(selectedPerson.getId(), txtPhoneNumber.getText());
            updateTableView();
            setElementsVisible(true);
            clearTextFields();
        } else if (!insertSelected && !extraPhoneNumber && extraAddress) {
            agenda.addAddress(selectedPerson.getId(), txtAddress.getText());
            updateTableView();
            setElementsVisible(true);
            clearTextFields();
        }
        insertSelected = false;
        extraPhoneNumber = false;
        extraAddress = false;
    }

    private void setElementsVisible(boolean status) {
        if (agenda.getPeople().isEmpty()) {
            removeButton.setDisable(true);
            editButton.setDisable(true);
            addNumberButton.setDisable(true);
            addAddressButton.setDisable(true);
        } else {
            removeButton.setDisable(false);
            editButton.setDisable(false);
            addNumberButton.setDisable(false);
            addAddressButton.setDisable(false);
        }

        txtName.setDisable(!status);
        txtAddress.setDisable(!status);
        txtPhoneNumber.setDisable(!status);
        saveButton.setDisable(!status);
    }

    private void updateTableView() {
        people.clear();
        agenda.getData();
        people.addAll(agenda.getPeople());
        tableView.setItems(people);
    }

    private void clearTextFields() {
        txtName.clear();
        txtAddress.clear();
        txtPhoneNumber.clear();
    }

    public void loadPersonInfo(Person selectedPerson) {
        txtName.setText(selectedPerson.getName());

        listViewAddresses.getItems().clear();
        listViewPhones.getItems().clear();

        List<String> addresses = agenda.getAddressesByPerson(selectedPerson.getId());
        listViewAddresses.getItems().addAll(addresses);

        List<String> phones = agenda.getPhonesByPerson(selectedPerson.getId());
        listViewPhones.getItems().addAll(phones);
    }

    public void editPerson(Person selectedPerson) {
        listViewAddresses.setEditable(true);
        listViewAddresses.setCellFactory(TextFieldListCell.forListView());

        listViewAddresses.setOnEditCommit(event -> {
            int index = event.getIndex();
            String newValue = event.getNewValue();
            listViewAddresses.getItems().set(index, newValue);
            agenda.updatePerson(selectedPerson.getId(), txtName.getText(), new ArrayList<>(listViewAddresses.getItems()), new ArrayList<>(listViewPhones.getItems()));
        });

        listViewPhones.setEditable(true);
        listViewPhones.setCellFactory(TextFieldListCell.forListView());

        listViewPhones.setOnEditCommit(event -> {
            int index = event.getIndex();
            String newValue = event.getNewValue();
            listViewPhones.getItems().set(index, newValue);
            agenda.updatePerson(selectedPerson.getId(), txtName.getText(), new ArrayList<>(listViewAddresses.getItems()), new ArrayList<>(listViewPhones.getItems()));
        });
    }

    public void confirmDelete(Person person) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText(null);
        alert.setContentText("¿Estás seguro de que quieres eliminar a " + person.getName() + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            agenda.deletePerson(person.getId());
            updateTableView();
            setElementsVisible(false);
        }
    }

}