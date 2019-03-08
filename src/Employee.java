import java.io.Serializable;

/**
 * The Abstract Employee class. An Employee has an Id and can access the Restaurant's Kitchen and
 * its functions.
 */
public abstract class Employee implements Serializable {

  private String job;
  private String id; // Employee's Id
  private String type;
  private Kitchen kitchen; // The Kitchen the Employee has access to
  public String attendance;
  private String password;

  /**
   * Constructor for an Employee class.
   *
   * @param id id for this employee
   * @param kitchen kitchen this employee has access to
   */
  Employee(String id, Kitchen kitchen) {
    this.id = id;
    this.kitchen = kitchen;
    this.attendance = "Present";
    this.password = "password";
  }

  /**
   * Get job
   * @return return the job
   */
  public String getJob() {
    return job;
  }

  /**
   * Set the job
   * @param job job
   */
  public void setJob(String job) {
    this.job = job;
  }

  /**
   * Return true if the new password is set
   * @param oldPass old password
   * @param newPass new password
   * @return return true if the password is set
   */
  public boolean setPassword(String oldPass, String newPass){
    if (oldPass.equals(this.password)){
      this.password = newPass;
      return true;
    } else {
      return false;
    }
  }

  /**
   * Return true if entered the correct password
   * @param password password
   * @return return true if entered the correct password
   */
  public boolean checkPass(String password){
    if (password.equals(this.password)){
      return true;
    } else {
      return false;
    }
  }

  /**
   * Get attendance
   * @return the String attendance
   */
  public String getAttendance() {
    return attendance;
  }

  /**
   * Set attendance
   */
  public void setAttendance() {
    if (this.attendance.equals("Present")){
      attendance = "Absent";
    } else {
      attendance = "Present";
    }
  }

  /**
   * Each employee has a specific ID.
   *
   * @return id associated with this employee
   */
  public String getId() {
    return this.id;
  }

  /**
   * Set the Employee's id
   * @param id Employee's id
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Each employee has a specific type
   * @return Employee type
   */
  public String getType() {
    return type;
  }

  /**
   * Set the Employee's type
   * @param type Employee's type
   */
  public void setType(String type) {
    this.type = type;
  }

  /**
   * Each employee has access to the kitchen of a restaurant.
   *
   * @return kitchen associated with the restaurant this employee works in
   */
  public Kitchen getKitchen() {
    return this.kitchen;
  }
}
