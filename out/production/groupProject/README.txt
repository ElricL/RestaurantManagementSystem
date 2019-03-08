================================================== HOW PROGRAM WORKS ===================================================

- The program will be running in RestaurantManager's main method
- The program will read inputs in the Events.txt
- Menu info for Foods and Ingredient prices will not be present initially. Running the program for first time
  will create the configuration files for these. Food and Ingredient info will then be accessible to make any edits.
- Save file will also be created after first run.
- After each run, the Events processed will be recorded in the Save file. This gives the User the ability to start where
  they left off for their next run.


************************************************************************************************************************
**        CLEAR Events.txt BEFORE MODIFYING FOR EACH RUN: WILL DUPLICATE EVENTS IF RAN TWICE WITHOUT CLEARING        ***
************************************************************************************************************************


================================================== INPUT INSTRUCTIONS ==================================================
------------------------------------------------------------------------------------------------------------------------

Manager:
1. Change ingredient threshold
	- Manager | id | change threshold | ingredient name | new threshold |

2. Generate inventory list
	- Manager | id | generate inventory |

3. Read request file
	- Manager | id | read request |

4. Restock ingredient
	- Manager | id | restock ingredient | ingredient name | quantity |
	- Manager | id | restock ingredient | ingredient name |
	    * quantity not specified, restocks ingredient by 20 units.
	    * Manager does not delete requests upon requestiong.

------------------------------------------------------------------------------------------------------------------------

Cook:
1. Sees the Order
    - Cook | id | Sees Order | Order number |

2. Cooks the given order
    - Cook | id | Cooks Order | Order number |
        * A Cook can only Cook a Food of the same type, either one of "Main", "Appetizer", "Desert".

------------------------------------------------------------------------------------------------------------------------
Server:

1. Fill Table
    - Server | id | Fills Table | Table # | Num of Split bills | Num of Customers |
        * Num of split bills will be the number of paying customers
        * If Num of split bills is 1 then there only one bill for the Table

1. Record a food Order
    - Server | id | Record Food from | Table # | Food | Subtractions Here | Additions Here | Bill # |
        * Any additions or subtractions of nonexistent ingredients will not be added, when food is recorded
        * If no changes write "None"

2. Place recorded Orders
    - Server | id | Place Order(s) from | Table # |
        * Place recorded Orders to the Kitchen for Cooks to prepare

3. Deliver Order
    - Server | id | Deliver Order to | Table # | Order # |
        * Only the Server that initially recorded an Order may Deliver Order.
        * Cannot deliver an Order that is not completely filled (All food items have not been cooked)

************************************************************************************************************************
**                         Only the Server that initially recorded an Order may Deliver Order.                       ***
************************************************************************************************************************


4. Return Order
    - Server | id | Return Order from | Table # | Order # | Food | \
    Original Subtract | Original Add | New Subtract | New Add | Reason |
        * All must be in one line.
        * Order must have been delivered to the table to return any of the Order's food item
        * If no changes write "None"

5. Confirm Order
    - Server | id | Confirm Order in | Table # | Order # |
        * Order must have been delivered

6. Clear Table
    - Server | id | Clear Table | Table # |
        * Only confirmed Orders will be added to the table's final bill

7. Cancel Order
    - Server | id | Cancel Food item from | Table # | Order # | Food | Subtractions | Additions |
        * Can only cancel Food item if it exist and has not been prepared yet
        * If no changes write "None"

8. Delete Food request
    - Server | id | Delete Food from | Table # | Food | Subtractions Here | Additions Here | Bill # |

------------------------------------------------------------------------------------------------------------------------

===Configuration Format===

1. DefaultFood.txt (File for Menu information on list of foods that can be ordered)
    Food Name | price | Food Type (Main, Appetizer, Dessert) | List of Ingredients separated by ", " |

2. IngredientsToPrice.txt (File for Menu information on list of ingredient prices for additions)
    Ingredient Name | price