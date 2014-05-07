Feature: Trip Search feature

  Scenario: As a user, I want to search for a one way trip
    Given my app is running
    Then I should see "BusRide"
    When I touch the "From" text
    And I enter "New" into input field number 1
    And I wait
    Then I should see "New York"
    Then I touch the "New York, NY" text
    
    When I touch the "To" text
    And I enter "Wash" into input field number 2
    Then I should see "Washington"
    Then I touch the "Washington, DC" text
    
    When I touch the "Depart Date" text
    And I set the date to "06-05-2014" on DatePicker with index "1"
    And I press "Set"
    And I press "Search"
    
    And I should see "$"
    
  Scenario: As a user, I want to search for a round trip
    Given my app is running
    Then I should see "BusRide"
    But I should not see "Return Date"
    When I touch the "From" text
    And I enter "New" into input field number 1
    And I wait
    Then I should see "New York"
    Then I touch the "New York, NY" text
    
    When I touch the "To" text
    And I enter "Wash" into input field number 2
    Then I should see "Washington"
    Then I touch the "Washington, DC" text
    
    When I touch the "Depart Date" text
    And I set the date to "01-05-2014" on DatePicker with index "1"
    And I press "Set"
    
    When I toggle checkbox number 1
    Then I should see "Return Date"
    When I touch the "Return Date" text
    And I set the date to "06-05-2014" on DatePicker with index "1"
    And I press "Set"
    And I press "Search"
    
    And I should see "$"
    