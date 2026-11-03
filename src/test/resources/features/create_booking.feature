@RB-100 @create_booking
Feature: Create booking

  @RB-101
  Scenario Outline: Successfully create a new booking
    Given I am on the Restful booker platform page
    When I select a stay of <stay_nights> nights starting <starting_from_days_ahead> days from today
    And I select an available room
    And I land in the room page
    And I click the button to start the reservation process
    And I enter booking information:
      | FIRST_NAME | LAST_NAME  | EMAIL                      | PHONE           |
      | Oliver     | Hargreaves | oliver.hargreaves@mail.com | +44 7700 900123 |
    And I click the button to finish the reservation process
    Then I get the booking confirmation with the booking dates

    Examples:
      | stay_nights | starting_from_days_ahead |
      | 3           | 5                        |

  @RB-102
  Scenario Outline: Create a new booking with missing information - missing <missing_field>
    Given I am on the Restful booker platform page
    When I select a stay of 3 nights starting 2 days from today
    And I select an available room
    And I land in the room page
    And I click the button to start the reservation process
    And I enter booking information:
      | FIRST_NAME   | LAST_NAME   | EMAIL   | PHONE   |
      | <first_name> | <last_name> | <email> | <phone> |
    And I click the button to finish the reservation process
    Then I get an error message because of the missing fields

    Examples:
      | missing_field | first_name | last_name  | email                      | phone           |
      | first name    |            | Hargreaves | oliver.hargreaves@mail.com | +44 7700 900123 |
      | last name     | Oliver     |            | oliver.hargreaves@mail.com | +44 7700 900123 |
      | email         | Oliver     | Hargreaves |                            | +44 7700 900123 |
      | phone         | Oliver     | Hargreaves | oliver.hargreaves@mail.com |                 |
