package com.example.agenda;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.event.ActionEvent;
import java.io.IOException;

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
    private Button addNumberButton;
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

    public SceneController() {
        people = FXCollections.observableArrayList();
        agenda = new AgendaDB();
        insertSelected = false;
        extraPhoneNumber = false;
    }

    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colPhoneNumbers.setCellValueFactory(new PropertyValueFactory<>("phoneNumbers"));
        people.addAll(agenda.getPeople());
        tableView.setItems(people);
        setElementsVisible(false);
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

        if (selectedPerson != null) {
            agenda.deletePerson(selectedPerson.getId());
        }

        people.clear();
        agenda.getData();
        people.addAll(agenda.getPeople());
        tableView.setItems(people);
        setElementsVisible(false);
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
        Person selectedPerson = tableView.getSelectionModel().getSelectedItem();
        if (selectedPerson != null) {
            txtName.setText(selectedPerson.getName());
            txtAddress.setText(selectedPerson.getAddress());
        }
        txtPhoneNumber.setDisable(false);
        extraPhoneNumber = true;
        saveButton.setDisable(false);
    }

    @FXML
    protected void saveClick(ActionEvent event) throws IOException {
        Person selectedPerson = tableView.getSelectionModel().getSelectedItem();
        System.out.println("INSERT: " + insertSelected + " EXTRA: " + extraPhoneNumber);
        if (insertSelected && !extraPhoneNumber) {
            String name = txtName.getText();
            String address = txtAddress.getText();
            String phone = txtPhoneNumber.getText();
            agenda.insertPerson(name, address, phone);
            people.clear();
            agenda.getData();
            people.addAll(agenda.getPeople());
            tableView.setItems(people);
        } else if (!insertSelected && !extraPhoneNumber) {
            if (selectedPerson != null) {
                selectedPerson.setName(txtName.getText());
                selectedPerson.setAddress(txtAddress.getText());
                selectedPerson.setPhoneNumbers(txtPhoneNumber.getText());
                agenda.updatePerson(selectedPerson.getId(), selectedPerson.getName(), selectedPerson.getAddress(), selectedPerson.getPhoneNumbers());
                people.clear();
                people.addAll(agenda.getPeople());
                tableView.setItems(people);
            }
        } else if (!insertSelected && extraPhoneNumber) {
            agenda.addPhone(selectedPerson.getId(), txtPhoneNumber.getText());
            people.clear();
            agenda.getData();
            people.addAll(agenda.getPeople());
            tableView.setItems(people);
        }
        insertSelected = false;
        extraPhoneNumber = false;
        setElementsVisible(false);
        clearTextFields();
    }

    private void setElementsVisible(boolean status) {
        if (agenda.getPeople().isEmpty()) {
            removeButton.setDisable(true);
            editButton.setDisable(true);
            addNumberButton.setDisable(true);
        } else {
            removeButton.setDisable(false);
            editButton.setDisable(false);
            addNumberButton.setDisable(false);
        }

        txtName.setDisable(!status);
        txtAddress.setDisable(!status);
        txtPhoneNumber.setDisable(!status);
        saveButton.setDisable(!status);
    }

    private void clearTextFields() {
        txtName.clear();
        txtAddress.clear();
        txtPhoneNumber.clear();
    }
}
