# Level Up Changelog

This document tracks all notables changes to the Level Up app.

---

## 0.7.0

### Added

- Add Icon Select page where the player can tap on an icon to select it for the quest they are creating or editing
- New Quest now stores the selected icon on save
- Quest List now shows each quest's icon or the default question mark icon if there is no icon

## 0.6.1

### Added

- Player can edit quests in Quest List by pressing and holding anywhere on the quest card

### Changed

- Items are allowed to have a negative cost which can be as low as -9999
- Settings are allowed to have negative number values
  - Exp and point input fields allow values down to -999
  - Level up bonus allow values down to -99

## 0.6.0

### Added

- Tapping on the quest card border in Quest List lets the player edit the selected quest
  - If any quests are selected, then the player cannot edit quests until they de-select all quests
- Pressing and holding on an item in the Shop lets the player edit the selected item
  - If any items are selected, then the player cannot edit quests until they de-select all items

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
