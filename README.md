# JLox

This is the official repository for JLox.
**JLox is a programming language interpreter written in Java.**

## Installation

### Windows
Build and run the project using Maven.

### Linux
Make sure Java 17+ and Maven are installed before building the project.

### macOS
Make sure Java 17+ and Maven are installed before building the project.

## Documentation

### General
JLox supports single-line and multiline comments.

```lox
// This is a single-line comment

/*
   This is a
   multiline comment
*/
```

### Variables
Variables can be declared using the `var` keyword.

```lox
var a = 5;
var b = 6.9;
var c = "Hello World";
var d = true;
var e = false;
var f; // nil is assigned by default
```

### Types
JLox currently supports numbers, strings, booleans, and nil.

```lox
var a = 5;
var b = 6.9;
var c = "Hello World";
var d = true;
var e = false;
var f;
```

### Built-ins
Use `print` to display values in the console. The `print` statement automatically adds a new line after the output.

```lox
var a = 5;
print a;
```

### Conditionals
JLox supports `if` and `else` conditional statements. The `if` block executes when the condition evaluates to true. The `else` block is optional and executes when the condition evaluates to false. JLox also supports the logical operators `and` and `or`.

```lox
var a = 5;

if (a == 5) {
    print a;
} else {
    print "Condition is false";
}
```

Logical operators can be used to combine conditions:

```lox
var age = 20;

if (age >= 18 and age <= 60) {
    print "Eligible";
}
```

### Loops
JLox supports `while` and `for` loops.

#### While Loop
Statements inside a `while` block are executed as long as the specified condition evaluates to true. When the condition becomes false, the loop stops executing and control passes to the statement following the loop.

```lox
var a = 0;

while (a < 10) {
    a = a + 1;

    if (a == 5) {
        print a;
    }
}
```

#### For Loop
JLox also supports `for` loops.

```lox
for (var i = 0; i < 5; i = i + 1) {
    print i;
}
```
