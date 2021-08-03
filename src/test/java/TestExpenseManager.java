import model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.ExpenseManager;

import java.util.LinkedHashMap;
import java.util.List;

public class TestExpenseManager {

    @Test
    public void checkIfSingleTon() {
        ExpenseManager instance1 = ExpenseManager.getInstance();
        ExpenseManager instance2 = ExpenseManager.getInstance();
        Assertions.assertEquals(instance1,instance2);
    }

    @Test
    public void testCreateUser() {
        User testUser = new User("User1", "u1", "user1@test", "123451",0.0, new LinkedHashMap<>());
        User createdUser = ExpenseManager.getInstance().createNewUser("u1");
        Assertions.assertEquals(testUser.getName(), createdUser.getName());
    }
}
