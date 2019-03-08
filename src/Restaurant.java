import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A Restaurant class. A Restaurant creates instances of Food, Menu, Kitchen, Employees, Tables, and
 * ingredients.
 */
public class Restaurant implements Serializable {
  private Kitchen kitchen;
  private Menu menu;
  private ArrayList<Server> servers = new ArrayList<>();
  private ArrayList<Cook> cooks = new ArrayList<>();
  private ArrayList<Table> tables = new ArrayList<>();
  private ArrayList<Manager> managers = new ArrayList<>();

  /** The constructor call methods that creates its own respective component of this Restaurant. */
  Restaurant() {
    this.menu = new Menu();
    buildMenu();
    buildKitchen();
    buildTables();
    buildIngredients();
  }

  /**
   * Return list of Servers
   *
   * @return Server[] servers
   */
  ArrayList<Server> getServers() {
    return this.servers;
  }

  /**
   * Return the Restaurants Menu
   *
   * @return menu
   */
  Menu getMenu() {
    return this.menu;
  }

  /**
   * Change this Restaurants menu to new Menu
   * @param menu the new menu
   */
  void updateMenu(Menu menu) {
    this.menu = menu;
  }

  /**
   * Return list of Cooks
   *
   * @return Cook[] cooks
   */
  ArrayList<Cook> getCooks() {
    return this.cooks;
  }

  /**
   * Return list of Tables
   *
   * @return Table[] tables
   */
  ArrayList<Table> getTables() {
    return this.tables;
  }

  /**
   * Return list of Managers
   *
   * @return Manager[] managers
   */
  ArrayList<Manager> getManagers() {
    return this.managers;
  }

  /**
   * Returns the Restaurant Kitchen
   * @return kitchen
   */
  public Kitchen getKitchen() {
    return kitchen;
  }

  /**
   * Return Table
   * @param tableNum Table number
   * @return the table
   */
  Table getTable(int  tableNum) {
    for (Table table : tables) {
      if (table.getTableNum() == tableNum) {
        return table;
      }
    }
    return null;
  }

  /**
   * Return the list of Tables in Restaurant
   * @return list of tables
   */
  ArrayList<String> getTablesString() {
    ArrayList<String> tablesList = new ArrayList<>();
    for (Table table : tables) {
      tablesList.add(table.toString());
    }
    return tablesList;
  }

  /**
   * Reads File DefaultFood.txt line by line, and creates instances of Food accordingly. Each Food
   * is then added into this Restaurant's Menu.
   */
  public String buildMenu() {
    Path currentRelativePath = Paths.get("");
    String fileName = currentRelativePath.toAbsolutePath().toString() + "/FoodItems.txt";
    FileInputStream in;
    in.

    StringBuilder menuBuilder = new StringBuilder();
    menuBuilder.append("---------------------------------- Menu -----------------------------------").append(String.format("%n"));

    try (BufferedReader fileReader = new BufferedReader((new FileReader(fileName)))) {
      // Refer one line at a time.
      String line = fileReader.readLine();

      while (line != null) {
        String[] rawLine = line.split("\\|");
        for (int i = 0; i < rawLine.length; i++) {
          rawLine[i] = rawLine[i].trim();
        }
        String foodName = rawLine[0];
        double price = Double.valueOf(rawLine[1]);
        String type = rawLine[2];
        String[] ingredients = rawLine[3].split(", ");
        ArrayList<String> arrayIngredients = new ArrayList<>(Arrays.asList(ingredients));

        Food newFood = new Food(foodName, price, type, arrayIngredients);
        menuBuilder.append(newFood.toString()).append("----" + newFood.getPrice()).append(String.format("%n"));

        this.menu.addFoodMenu(newFood);
        line = fileReader.readLine();
        String str;
        str.c
      }
      menuBuilder.append("---------------------------- End of Menu ---------------------------------");

    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
    return menuBuilder.toString();
  }

  /**
   * Reads IngredientsToPrice.txt line by line, then matches a String representing and ingredient to
   * its corresponding price accordingly.
   */
  private void buildIngredients() {
    Path currentRelativePath = Paths.get("");
    String fileName = currentRelativePath.toAbsolutePath().toString() + "/IngredientsToPrice.txt";

    try (BufferedReader fileReader = new BufferedReader((new FileReader(fileName)))) {
      // Refer one line at a time.
      String line = fileReader.readLine();

      while (line != null) {
        String[] rawLine = line.split("\\|");
        String ingredientName = rawLine[0].trim();
        double ingredientPrice = Double.valueOf(rawLine[1].trim());

        this.menu.setIngredientToPrice(ingredientName, ingredientPrice);

        line = fileReader.readLine();
      }

    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }

  /**
   * Checks whether an Employee's subclass matches with an existing id.
   *
   * @param job the subclass if Employee
   * @param id the id of Employee
   * @return true if this worker works at this restaurant
   */
  boolean isWorker(String job, String id) {
    if (!job.equals("Server") && !job.equals("Cook") && !job.equals("Manager")) {
      return false;
    }
    // If code gets here, occupation is valid. Now need to check if id is valid.
    ArrayList<String> idList = new ArrayList<>();

    switch (job) {
      case "Server":
        for (Server curServer : this.servers) {
          idList.add(curServer.getId());
        }
        break;
      case "Cook":
        for (Cook curCook : this.cooks) {
          idList.add(curCook.getId());
        }
        break;
      case "Manager":
        for (Manager curManager : this.managers) {
          idList.add(curManager.getId());
        }
        break;
      default:
        break;
    }

    return idList.contains(id);
  }

  /**
   * Builds a Kitchen, and fills it with ingredients with its initial quantity and default
   * threshold.
   */
  private void buildKitchen() {
    this.kitchen = new Kitchen();

    this.kitchen.addIngredients("cheese", 50, 20);
    this.kitchen.addIngredients("bun", 30, 10);
    this.kitchen.addIngredients("tomato", 25, 15);
    this.kitchen.addIngredients("flour", 100, 50);
    this.kitchen.addIngredients("egg", 80, 30);
    this.kitchen.addIngredients("beef patty", 80, 30);
    this.kitchen.addIngredients("lettuce", 50, 30);
    this.kitchen.addIngredients("egg", 80, 30);
    this.kitchen.addIngredients("gt scoop", 40, 25);
    this.kitchen.addIngredients("fries", 40, 20);
    this.kitchen.addIngredients("gravy", 35, 25);
    this.kitchen.addIngredients("ground beef", 45, 25);
  }

  /** Creates Tables and adds them to this Restaurant. */
  private void buildTables() {
    Table t1 = new Table(1);
    Table t2 = new Table(2);
    Table t3 = new Table(3);

    this.tables.add(t1);
    this.tables.add(t2);
    this.tables.add(t3);
  }

  /**
   * This method adds the Employee
   * @param employeeType Type of Employee
   * @param id Employee's id
   * @param cookType Type of Cook
   * @return the Employee
   */
  public Employee addEmployee(String employeeType, String id, String cookType) {
    Employee newWorker = EmployeeFactory.getEmployee(employeeType, id, kitchen, cookType, menu);
    if (employeeType.equals("Manager")) {
      this.managers.add((Manager) newWorker);
    } else if (employeeType.equals("Cook")) {
      this.cooks.add((Cook) newWorker);
    } else if (employeeType.equals("Server")) {
      this.servers.add((Server) newWorker);
    }
    return newWorker;
  }

  /**
   * This methods returns True once the Employee is deleted
   * @param employeeType Type of Employee
   * @param id Employee's id
   * @return true iff the employee is deleted
   */
  public Boolean deleteEmployee(String employeeType, String id) {
    if (isWorker(employeeType, id)) {
      if (employeeType.equals("Manager")) {
        ArrayList<Manager> employeeToDelete = new ArrayList<>();
        employeeToDelete.add((Manager) getWorker(employeeType, id));
        this.managers.removeAll(employeeToDelete);
        return true;
      }
      else if (employeeType.equals("Cook")) {
        ArrayList<Cook> employeeToDelete = new ArrayList<>();
        employeeToDelete.add((Cook) getWorker(employeeType, id));
        this.cooks.removeAll(employeeToDelete);
        return true;
      }
      else if (employeeType.equals("Server")) {
        ArrayList<Server> employeeToDelete = new ArrayList<>();
        employeeToDelete.add((Server) getWorker(employeeType, id));
        this.servers.removeAll(employeeToDelete);
        return true;
      }
    }
    return false;
  }

  /**
   * This method checks if the employee type matches and
   * return the type of Employee
   * @param employeeType Type of Employee
   * @param id  Employee's id
   * @return Type of Employee
   */
  public Employee getWorker(String employeeType, String id) {
    if (employeeType.equals("Manager")) {
      for (Manager manager : managers) {
        if (manager.getId().equals(id)) {
          return manager;
        }
      }
    } else if (employeeType.equals("Cook")) {
      for (Cook cook: cooks) {
        if (cook.getId().equals(id)) {
          return cook;
        }
      }
    } else if (employeeType.equals("Server")) {
      for (Server server : servers) {
        if (server.getId().equals(id)) {
          return server;
        }
      }
    }
    return null;
  }
}
