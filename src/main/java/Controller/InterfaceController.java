package Controller;

import Class.MappingSQL;
import Class.UserInfo;
import Class.PhoneNumber;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * FXML Controller class
 *
 * @author Reyner
 */
public class InterfaceController implements Initializable {

    @FXML
    private TextField name_tf;
    @FXML
    private TextField direction_tf;
    @FXML
    private TextField number_tf;
    @FXML
    private TableView<UserInfo> personalData_tv;
    @FXML
    private Button add_btn;
    @FXML
    private TableColumn<UserInfo, String> nameColumn;
    @FXML
    private TableColumn<UserInfo, String> directionColumn;
    @FXML
    private TableColumn<UserInfo, String> idColumn;
    @FXML
    private ChoiceBox<String> telephone_cb;  
    @FXML
    private TableView<PhoneNumber> numberData_tv;
    @FXML
    private TableColumn<PhoneNumber, String> typeColumn;
    @FXML
    private TableColumn<PhoneNumber, String> numberColumn;
    @FXML
    private Button addNumber_btn;
    @FXML
    private Button deleteUser_btn;
    @FXML
    private Button deleteNumber_btn;
    private UserInfo selectedUser; 
    private MappingSQL mappingSQL;
    private String[] telephoneType = {"Telefono", "Celular"};
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeNumberColumn();
        initializeUserColumn();
        
        mappingSQL = new MappingSQL();
        reloadUserTableData();
        initializeChoiceBox();
        initializeUserSelectionListener();
        
    }          
    
    private void initializeNumberColumn(){
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        numberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
    }
    
    private void initializeUserColumn(){
        idColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        directionColumn.setCellValueFactory(new PropertyValueFactory<>("direction"));
    }
    
    private void initializeChoiceBox(){
        telephone_cb.getItems().addAll(telephoneType);
    } 
    
    private void initializeUserSelectionListener(){
        personalData_tv.getSelectionModel().selectedItemProperty().addListener((obs, oldUser, newUser) -> {
            selectedUser = newUser;
            if (selectedUser != null) {
                reloadPhoneNumbers(selectedUser.getUserId());
            } else {
                numberData_tv.getItems().clear();
            }
        });
    }
    
    private void reloadUserTableData() {
        personalData_tv.getItems().clear();
        personalData_tv.getItems().addAll(mappingSQL.loadUserInfo());
    }
    
    private void reloadPhoneNumbers(int userId) {
        numberData_tv.getItems().clear();
        numberData_tv.getItems().addAll(mappingSQL.loadNumbersByUserId(userId));
    }
    
    @FXML
    private void addPrincipalData(ActionEvent event) {
        String nameText = name_tf.getText();
        String descriptionText = direction_tf.getText();
        
        if(!nameText.isEmpty() && !descriptionText.isEmpty()){
            UserInfo info = new UserInfo(0, nameText, descriptionText);
            int id = mappingSQL.insertUserInfo(info);
            
            if (id != -1) {
                info.setUserId(id);
                personalData_tv.getItems().add(info);
                name_tf.clear();
                direction_tf.clear();
            } else {
                System.out.println("Error al insertar datos en la base de datos");
            }
        } else {
            Platform.runLater(() -> {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setHeaderText("Error al agregar");
                alert.setContentText("Alguno de los campos están vacíos.");
                alert.showAndWait();
            });
        }
    }

    @FXML
    private void addPhoneNumber(ActionEvent event) {
        String type = telephone_cb.getValue();
        String number = number_tf.getText();
        
        if (selectedUser == null || type == null || number.isEmpty()) {
            Platform.runLater(() -> {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setHeaderText("Error al agregar");
                alert.setContentText("Por favor seleccione un usuario, un tipo e ingrese el numero.");
                alert.showAndWait();
            });
            return;
        }

        PhoneNumber phoneNumber = new PhoneNumber(type, number, selectedUser.getUserId(),-1);

        boolean inserted = mappingSQL.insertNumber(phoneNumber);

        if (inserted) {
            reloadPhoneNumbers(selectedUser.getUserId());
            number_tf.clear();
            telephone_cb.setValue(null);
        } else {
            Platform.runLater(() -> {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setHeaderText("Error al agregar");
                alert.setContentText("Error al insertar el numero de telefono en la base de datos.");
                alert.showAndWait();
            });
        }   
    }

    @FXML
    private void deleteUser(ActionEvent event) {
        UserInfo selectedUser = personalData_tv.getSelectionModel().getSelectedItem();
        if(selectedUser != null){
            int userId = selectedUser.getUserId();
            boolean deletedUser = mappingSQL.deleteUser(userId);
            if(deletedUser){
            reloadUserTableData();
            numberData_tv.getItems().clear();
            }
        }else{
            Platform.runLater(() -> {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setHeaderText("Error al eliminar");
                alert.setContentText("No se pudo eliminar el usuario seleccionado.");
                alert.showAndWait();
                });
        }
    }

    @FXML
    private void deleteNumber(ActionEvent event) {
        PhoneNumber selectedNumber = numberData_tv.getSelectionModel().getSelectedItem();
            if(selectedNumber != null){
                boolean eliminated = mappingSQL.deletePhoneNumber(selectedNumber.getNumberId());
                if(eliminated){
                    reloadPhoneNumbers(selectedUser.getUserId());
                }else{
                    System.out.println("Error al eliminar");
                }
            }else{
                Platform.runLater(() -> {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setHeaderText("Error al eliminar");
                alert.setContentText("No se selecciono ningun telefono para eliminar.");
                alert.showAndWait();
                });
            }
    }
}
