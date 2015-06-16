Feature: Initial Screen

  Scenario: The screen is set up initially
	When I press the menu key
	And I touch the "Set Current Steps" text
	And I enter "555" into "StepsEditText"
	And I touch the "Ok" text

	When I press the menu key
	And I touch the "Set Goal Steps" text
	And I enter "10000" into "GoalEditText"
	And I touch the "Ok" text

	Then I expect atmost "1%" difference when comparing with "initial_screen.png"
