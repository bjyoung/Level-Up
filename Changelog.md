# Level Up Changelog

This document tracks all notables changes to the Level Up app.

---

## 0.7.9

### Added

- Add Item History page
  - Accessible via the clock button on the Shop
  - Displays items the user bought from latest to oldest
  - Only keeps track of the latest 200 items purchased

### Fixed

- Make the app work in landscape mode

---

## 0.7.8

### Added

- Add Advanced Settings page which is reachable from Settings
- Add more icons

### Changed

- Limit total points earned to 999,999
- Limit how much exp and how many points can be earned in one Complete action to prevent overflows
  - Exp and points are both limited to 999,999 in one go
- Limit how many points can be spent in one purchase in the shop to prevent overflows
- Instead of jumping to the new value instantly, the points displayed now increments up to or decrements down to the actual value
- Increase default rewards for medium, hard and expert difficulties

### Fixed

- Purchasing items in the shop with a total cost that is zero or negative is now allowed
- Fix blurry icons
- Fix floating text in action buttons

---

## 0.7.7

### Added

- Add Quest History page
  - Accessible via the clock button on Quest List
  - Displays quests the user completed from most recently completed to the oldest
  - Card backgrounds are darker to distinguish from Quest List cards
  - Only keeps track of the latest 200 quests completed

### Changed

- Acronym labels in the Settings page now change in real-time based on what is entered in the acronym field
- Item cost allows empty values which defaults to zero points

### Fixed

- Fix some pop-up messages getting cut-off at the bottom
- Fix save and cancel buttons being pushed up by the virtual keyboard sometimes
- Fix spacing issues when using a 3-letter acronym
- Fix screen jitter on initial app start when displaying the Name Entry page

---

## 0.7.6

### Added

- Add delete icon functionality to Icon Select
  - Like with moving icons, Icon Select must be in Edit mode and at least one icon must be selected
  - All quests that use deleted icons fall back to the default question mark icon
- When pressed, action button colors darken to improve app responsiveness
  - Action buttons are the rectangular white buttons on some pages, usually for confirming or cancelling
- Show when quests were created in the Edit Quest page

---

## 0.7.5

### Added

- All of the changes below are for Icon Select
- Tapping the pointer arrow button switches the page to Edit mode, where multiple icons can be selected at once
- In Edit mode...
  - The Add New Icon button turns into a Move button
  - The center-bottom button can be pressed to turn back to the default select mode
  - Users can tap icons to select and de-select them
  - Switching between icon groups de-selects selected icons
- Pressing the move button during Edit mode triggers Move mode which allows users to choose an icon group to move selected icons to
  - Users are not allowed to press the move button if no icons are selected
  - Users are not allowed to choose the current icon group, since that would be redundant
- In Move mode...
  - The icon group buttons enlarge and move towards the center of the screen to make them easier to press
  - The move button turns into a cancel button
  - Pressing the cancel button or selecting an icon group changes the mode back to Edit
  - The rest of the buttons are disabled and become more transparent
  - The page label turns into "Select an Icon Group"
  - Scrolling through the icon grid is disabled
- When switching from Move mode back to Edit mode...
  - All disabled buttons and icon grid scrolling are enabled
  - Icon group buttons go back to their original size and positions and resume group-switching functionality
  - The cancel icon turns back to the move icon

---

## 0.7.4

### Added

- Add Name Entry page where the player can enter their name
  - Page is only shown an app startup and until player enters a valid name
  - Only allows names up to 15 alphanumeric characters long or empty names
  - After the name is submitted, the app starts on the Quest List page from then on
  - Player is not allowed to go back from Quest List to Name Entry or from Name Entry to Quest List by navigating back

---

## 0.7.3

### Added

- In Icon Select, tap on one of the card suite icons to switch to that icon group
- Add more initial icons to Icon Select
- Sort icons in icon groups in alphabetical order
- Show a "No icons found" message if an icon group has no icons

### Changed

- Spread icons in Icon Select across the icon groups roughly according to the criteria below
  - Spades = tech
  - Diamonds = abstract
  - Hearts = home
  - Clubs = hobbies and games

---

## 0.7.2

### Added

- On press, navigation buttons darken slightly to indicate that it is being pressed

### Changed

- Change the navigation buttons from a black-on-white rounded button to a classic arcade-style white-on-black design
- Adjust icon margins in Icon Select

### Fixed

- Fix floating text issue

---

## 0.7.1

### Added

- Completing quests in Quest List displays a snackbar showing how much experience and points the player earned
  - Display a slightly different message when the player levels up
- Buying items in the Shop displays a message showing how much the player spent or a warning if they do not have enough points

### Changed

- Replaces the "Not implemented" toast in Settings with a snackbar

---

## 0.7.0

### Added

- Add Icon Select page where the player can tap on an icon to select it for the quest they are creating or editing
- New Quest stores the selected icon on save
- Quest List shows each quest's icon or the default question mark icon if there is no icon

### Changed

- Swap shop nav button's star icon with a shopping bag icon

### Fixed

- Fix player input not being preserved properly when navigating from New Quest to Icon Select and back
- Fix quest creation date being updated when editing a quest
- Fix bug where editing a quest and selecting an icon

---

## 0.6.1

### Added

- Player can edit quests in Quest List by pressing and holding anywhere on the quest card

### Changed

- Items are allowed to have a negative cost which can be as low as -9999
- Settings are allowed to have negative number values
  - Exp and point input fields allow values down to -999
  - Level up bonus allow values down to -99

---

## 0.6.0

### Added

- Tapping on the quest card border in Quest List lets the player edit the selected quest
  - If any quests are selected, then the player cannot edit quests until they de-select all quests
- Pressing and holding on an item in the Shop lets the player edit the selected item
  - If any items are selected, then the player cannot edit quests until they de-select all items

---

## 0.5.1

### Added

- Shop table displays items in the Item database, sorted by date created
- Highlight selected items in blue

### Fixed

- Fix Settings not leading back to the previous page (aka the Shop) on cancel/save
- Shorten item name width in the shop table

### Changed

- Darken confirm button green color for clarity
- Remove acronym from price column in the shop table

---

## 0.5.0

### Added

- Add Shop page
  - Displays player's current points on the top-left
  - Has buttons that lead to the Quest List, Settings and New Item pages
  - A table header shows where the item details are going to go
- Add New Item page
  - Has an optional name field and a required cost field
  - Player can create new items that are displayed in the shop
  - Has a cancel button that leads back to the Shop
  - Cost must be between 0 and 99999

### Fixed

- Fix white slivers/backgrounds that appear sometimes between pages during navigation animations

---

## 0.4.3

### Added

- Adds UI to Quest List that displays the experience the player needs to level up
- The settings button on the Quest List page becomes a cancel button when at least one quest is selected
  - Selecting the cancel button de-selects all currently selected quests
- Link the points acronym in Quest list to the acronym setting

### Changed

- Move player's points below the points label in Quest List

### Fixed

- The points acronym in Quest List now correctly matches the acronym set in the Settings page

---

## 0.4.2

### Added

- Adds save functionality to Settings page
- Add validation to Settings save
  - All fields must have a value
  - The exp and point reward fields must be a number between 0 and 9999 inclusive
  - The points acronym must be 1-3 alphabet characters, no spaces
  - The level up bonus must be a number between 0 and 999 inclusive
  - If any of the inputs are not valid, then save does not go through

---

## 0.4.1

### Added

- In Settings page
  - Quest exp and points earned are restricted to numbers and allow up to 4 digits
  - Points acronym is restricted to uppercase characters and allow up to 3 characters
  - Level up bonus is restricted to numbers and up to 3 digits

---

## 0.4.0

### Added

- Add UI and input fields in Settings page
  - For the exp and points granted per quest difficulty
  - For the points rewarded on level up
  - For for the points acronym used across the map

---

## 0.3.2

### Added

- Limit the player's max level to 99
  - At the max level, the player's progress bar stops tracking exp earned, but their total exp earned is still recorded
- Limits total exp earned to 999,999,999,999,999,999
- Progress bar smoothly animates when the player earns exp
- Sort quests in Quest List by when they are created

---

## 0.3.1

### Added

- Completing quests grants player exp and rewards
- If the player earns enough exp, they level up and gain 5 bonus RT points
  - The level up formula is: expToNextLvl = 2500 + (1750 * currLvl - 1)

---

## 0.3.0

### Added

- In Quest List page
  - Initialize player stats on app install
  - Link player stats to the UI
  - User can now select quests and complete or delete them
    - The list is updated accordingly on quest complete/delete
    - No exp or points are rewarded to the player on quest completion yet
    - Selecting a quest changes the Shop and New Quest buttons to Confirm and Delete buttons
    - If no quests are selected then the Shop and New Quest buttons return

---

## 0.2.1

### Added

- Adds animated slide transitions when navigating between pages

### Changed

- Quest name field text color changed to black color

---

## 0.2.0

### Added

- In New Quest page
  - Pressing checkmark button saves quest data to a database and sends user to Quest List
  - A new quest is not created if the name is not valid and an error message appears next to the field instead
    - Name must be less than 40 characters
    - Name must only use alphanumeric characters and some punctuation: !#$%&:?()@_+/,.-
    - Empty names are allowed
- In Quest List page
  - Display quest data in a scrollable list of cards

### Changed

- Adjust UI alignment in Quest List and New Quest pages

---

## 0.1.0

### Added

- In New Quest page
  - Add name, difficulty selector and placeholder icon selector fields
  - Add confirm and cancel buttons
  - Update cancel button to bring user back to Quest List
  - Selected difficulty is shown with a white rectangle
  - Select default difficulty (easy) when creating a new quest
- In Quest List page
  - Add username, user level and experience bar UI
  - Add New Quest, Settings and Shop nav buttons
- Create basic pages for Quest List, New Quest, Settings and Shop
- Add README
- Add changelog

---
