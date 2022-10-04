# Level Up Changelog

This document tracks all notables changes to the Level Up app.

## 0.2.1

### Added

- Adds animated slide transitions when navigating between pages

### Changed

- Quest name field text color changed to black color

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
