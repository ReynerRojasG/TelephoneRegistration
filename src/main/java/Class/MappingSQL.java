package Class;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Reyner
 */
public class MappingSQL {
    private final Connection connection;
    
    public MappingSQL(){
    this.connection = ConnectionSingleTon.getInstance().conn;
    }
    
    public int insertUserInfo(UserInfo user){
        String query = "INSERT INTO user_data (user_name, description) VALUES (?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getDirection());
            pstmt.executeUpdate();
            
             try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    user.setUserId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    public List<UserInfo> loadUserInfo(){
        List<UserInfo> userInformation = new ArrayList<>();
        String sql = "SELECT user_id, user_name, description FROM user_data";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    int id = rs.getInt("user_id");
                    String name = rs.getString("user_name");
                    String description = rs.getString("description");
                    UserInfo user = new UserInfo(id, name, description);
                    userInformation.add(user);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        return userInformation;
    }
    public boolean insertNumber(PhoneNumber number){
        String query = "INSERT INTO number (number_type, telephone, user_id) VALUES (?,?,?)"; 
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, number.getType());
            pstmt.setString(2, number.getNumber());
            pstmt.setInt(3, number.getNumberUserId());
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0; 
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public List<PhoneNumber> loadNumbersByUserId(int userId) {
        List<PhoneNumber> phoneNumbers = new ArrayList<>();
        String query = "SELECT number_type, telephone, number_id FROM number WHERE user_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String numberType = rs.getString("number_type");
                    String telephone = rs.getString("telephone");
                    int numberId = rs.getInt("number_id"); // Ahora debería funcionar porque number_id está en la consulta
                    PhoneNumber number = new PhoneNumber(numberType, telephone, userId, numberId);
                    phoneNumbers.add(number);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return phoneNumbers;
    }

    
    
    public boolean deletePhoneNumber(int numberId) {
        String query = "DELETE FROM number WHERE number_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, numberId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }   
    }
    
    public boolean deleteUser(int userId){
        String deleteQuery = "DELETE FROM number WHERE user_id = ?";
        try(PreparedStatement pstmt = connection.prepareStatement(deleteQuery)){
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        }catch(SQLException e){
         e.printStackTrace();
         return false;
        }
        
        
        String deleteUserQuery = "DELETE FROM user_data WHERE user_id = ?";
        try(PreparedStatement pstmtUser = connection.prepareStatement(deleteUserQuery)){
            pstmtUser.setInt(1, userId);
            int rowsAffected = pstmtUser.executeUpdate();
            return rowsAffected > 0;  
        }catch(SQLException e){
         e.printStackTrace();
         return false;
        } 
    }
    
}

    
