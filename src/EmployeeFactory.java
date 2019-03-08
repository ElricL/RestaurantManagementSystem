/**
 * Creates various Employees
 */
public abstract class EmployeeFactory {
        //use getShape method to get object of type shape
        public static Employee getEmployee(String employeeType, String id, Kitchen kitchen, String cookType, Menu menu){
            if (employeeType.equals("Manager")) {
                return new Manager(id, kitchen, menu);
            } else if (employeeType.equals("Cook")) {
                return new Cook(id, cookType, kitchen);
            } else if (employeeType.equals("Server")) {
                return new Server(id, kitchen, menu);
            }
            return null;
        }
    }
