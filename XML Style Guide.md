# XML Style Guide

This guide describes the XML code styling practices used to develop the Level Up app.

## General Recommendation

When in doubt, follow existing code base conventions.

## Naming

- Names only use ASCII letters, numbers and underscores
- For files names, use lowercase snake_case and the `.xml` extension

## Formatting

- 100 character column limit
- Use a line break after each element

### Indentation

- Indent by four spaces
- Indent contents inside of an element
- Return to the previous indent level when closing an element

```XML
<!-- Good -->
<resources>
    <color name="purple_200">#FFBB86FC</color>
</resources>

<!-- Bad -->
<resources>
<color name="purple_200">#FFBB86FC</color>
</resources>
```

## Whitespace

### Vertical

- No empty lines at the end of the file
- No consecutive empty lines
- Use to group logical elements togetehr

### Horizontal

- No whitespace at the end of elements
