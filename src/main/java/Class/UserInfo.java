package Class;

/**
 *
 * @author Reyner
 */
public class UserInfo {
    private int userId;
    private String name;
    private String direction;

    public UserInfo(int userId, String name, String direction) {
        this.userId = userId;
        this.name = name;
        this.direction = direction;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public int getUserId() {
        return userId;
    }
    
    
    public String getName() {
        return name;
    }

    public String getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return "UserInfo{" + "name=" + name + ", direction=" + direction + '}';
    }
    
}
