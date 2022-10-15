# Kotlin Style Guide

This guide describes the Kotlin code styling practices used to develop the Level Up app.

## General Recommendation

If a style is not covered here, then follow:

- [Google's Kotlin Style Guide](https://developer.android.com/kotlin/style-guide?authuser=1)

When in doubt, follow existing code base conventions.

## Naming

- Names only use ASCII letters and numbers
- Prefer the full spelling of a word over abbreviations, unless the word is used often and the abbreviation is obvious
- Avoid redundant names

```kotlin
// BAD!
Quest.questId

// Good
Quest.Id 
```

- Only capitalize the first letter in acronyms when following camelCase or PascalCase conventions

```kotlin
// BAD!
Shop.itemID

// Good
Shop.itemId
```

### Source File Names

- Use PascalCase and the `.kt` extension
  - If there is only one top-level class in the file, then name the file after the class
  - If there are multiple top-level declarations, then choose an applicable name

### Package Names

- All lowercase, no underscores

```kotlin
// Good
package com.example.deepspace

// BAD!
package com.example.deepSpace

// BAD!
package com.example.deep_space
```

### Class & Interface Names

- Class names use PascalCase and are nouns
- Interface names use PascalCase and may be nouns or adjectives
- Test classes use the name of the class they are testing plus a `Test` suffix

### Function Names

- Use camelCase
- Usually verbs
- Underscores are allowed in test function names
- No spaces

```kotlin
// BAD!
fun `test every possible case`() {}

// Good
fun testEveryPossibleCase() {}
```

### Constant Names

- Use UPPER_SNAKE_CASE and nouns
- Constant means `val` properties with no custom `get` function and immutable contents
- Constants that are scalar values must use `const` modifier

```kotlin
const val NUMBER = 5
val NAMES = listOf("Alice", "Bob")
val AGES = mapOf("Alice" to 35, "Bob" to 32)
val COMMA_JOINER = Joiner.on(',') // Joiner is immutable
val EMPTY_ARRAY = arrayOf()
```

### Variable Names

- Use camelCase and nouns

```kotlin
val variable = "var"
val mutableCollection: MutableSet = HashSet()
val nonEmptyArray = arrayOf("these", "can", "change")
```

### Type Variable Names

- Either:
  - A single capital letter
  - A name with a `T` suffix (for example `RequestT`)

### Field Names

- Use camelCase and nouns

```kotlin
class Player(
    val name: String? = null,
    val lvl: Int = 1,
    val currentLvlExp: Long = 0,
)
```

## File Structure

- A `.kt` file should be comprised of the following, in order and separated by one blank line:
  - Copyright and/or license header (optional)
  - File-level annotations
  - Package statement
  - Import statements
  - Top-level declarations

### Copyright/License

- Should be placed at the immediate top in a multi-line comment

```kotlin
/*
 * Copyright 2022 Brandon Young
 *
 * ...
 */
```

### Package Statement

- Has no column limit
- Never line-wrapped

### Import Statements

- Group import statements in a single, ASCII-sorted list
- Never use wildcard imports
- Has no column limit
- Never line-wrapped

### Top-Level Declarations

- Includes types, functions, properties or type aliases at the top-level
- File contents should serve one purpose
- Minimize public declarations in a single file
- Declarations that appear earlier in the file should help explain those farther down
- Declarations should follow some logical order (not chronological)

### Class Member Ordering

- Ordering class members follow the same rules as top-level declarations

## Formatting

### Braces

- Not required for `when` branches and `if` statements which have no more than one `else` branch and which fit on a single line

```kotlin
if (condition()) return

val value = if (condition()) 0 else 1
```

- In any other case, use braces

```kotlin
// BAD!
if (condition()) 
    return

// Good
if (condition()){ 
    return
}

// BAD!
val value = if (string.isEmpty())  
    0
else
    1

// Good
val value = if (string.isEmpty()) { 
    0
} else {
    1
}
```

- Braces follow the Kernighan and Richie (K&R) style for blocks and block-like constructs
  - No line break before the opening brace
  - Line break after the opening brace
  - Line break before the closing brace
  - Line break after the closing brace, only if that brace terminates a statement, body of a function, constructor or named class

```kotlin
return object : MyClass() {
    override fun foo() {
        if (condition()) {
            try {
                something()
            } catch (e: ProblemException) {
                recover()
            }
        } else {
            lastThing()
        }
    }
}

try {
    doSomething()
} catch (e: Exception) {} // BAD!

try {
    doSomething()
} catch (e: Exception) {
} // Good
```

### Indentation

- Indent by four spaces
- Indent every time a new block or block-like construct is opened
- Return to the previous indent level when the block ends

### One Statement Per Line

- Use a line break after each statement
- Don't use semicolons to end statements

### Line Wrapping

- Code has a column limit of 100 characters
  - Any line exceeding the limit, with some exceptions, must be line-wrapped
- Exceptions
  - Lines where obeying the column limit is not possible
  - `package` and `import` statements
  - Command line commands in a comment
- In general, line wrap at a higher syntactic level and in a way that makes code clearer

### Functions

- If a function signature is too long, put each parameter declaration on its own indented line
  - Also put the closing parenthesis (`)`) and return type on their own line with no indent

```kotlin
fun <T> Iterable<T>.joinToString(
    separator: CharSequence = ", ",
    prefix: CharSequence = "",
    postfix: CharSequence = ""
): String {
    // …
}
```

- Prefer if statements that have less code in them

```kotlin
// BAD!
fun add(x, y): Int {
    if (x != null && y != null) {
        // Other operations
        return x + y
    }

    return 0
} 

// Good
fun add(x, y): Int {
    if (x == null || y == null) {
        return 0
    }

    // Other operations
    return x + y
}
```

### Expression Functions

- Functions with only one expression can be an expression function

```kotlin
override fun toString(): String = "Hey"
```

### Properties

- If a property initializer does not fit on a singe line, break after the equals sign (`=`) and use an indent

```kotlin
private val defaultCharset: Charset? =
    EncodingRegistry.getInstance().getDefaultCharsetForPropertiesFiles(file)
```

- Properties declaring a `get` and/or `set` function should place each on their own line with normal indent and format them following function rules

```kotlin
var directory: File? = null
    set(value) {
        // …
    }
```

- Read-only properties can use shorter syntax

```kotlin
val defaultExtension: String get() = "kt"
```

## Whitespace

### Vertical

- Use a single blank line:
  - Between consecutive members of a class (properties, constructors, functions, etc.)
  - Between statements, as needed to organize code into logical subsections
- Avoid using multiple blank lines in a row
- No blank lines at the start or end of a file

### Horizontal

- No trailing spaces at the end of statements
- Separate keywords from an open parenthesis (`(`) or curly braces (`{`, `}`)

```kotlin
// BAD!
for(i in 0..1){
}

// Good
for (i in 0..1) {
}

// BAD!
}else{
}

// Good
} else {
}
```

- Use around math operators and lambda expression (`->`)

```kotlin
// BAD!
val two = 1+1

// Good
val two = 1 + 1
```

- After a comma (`,`) or colon (`:`)

```kotlin
// BAD!
class Foo: Runnable

// Good
class Foo : Runnable
```

- Around a comment starter (`//`)

## Database

### Queries

- Use all uppercase for keywords

```sql
-- BAD!
drop table `Player`

-- Good
DROP TABLE `Player`
```

- Put tick marks (``) around all table and column names

```sql
-- BAD!
ALTER TABLE PlayerTemp RENAME TO Player

-- Good
ALTER TABLE `PlayerTemp` RENAME TO `Player`
```
