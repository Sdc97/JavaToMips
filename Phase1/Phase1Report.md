# Phase 1: Type-checking

## Requirements and Specifications

The requirements for phase 1 were to implment the type system described in the MiniJava specification. These rules were defined using several helper functions, logical consequences, and custom data structures (Symbol tables in the form of type environments). This was to be completed using the provided grammar file for MiniJava, as well as the Java Tree Builder, which provided us with visitor interfaces and a Syntax tree to traverse that is produced from a MiniJava file. 

We primarily used GJVoidDepthFirst and GJDepthFirst to build our visitors from, encompassing multiple classes of visitors with methods that serve different purposes. Our program extended these visitors to traverse and create appropriate structures to store contextual information in one pass of the visitor, then perform actual typechecking in the second pass. 

On a successful type check, the program would print "Program type checked successfully" while on any failure it would throw and print an error with the string "Type error" then exit.

## Design

Our design focused around a single class, ContextType, which held all of our contextual information and helper functions to assist in type checking a MiniJava program. In a first pass, we would go down to the method level for each class, while recording the class variables, method names, types, and parameters in static maps in the ContextType class. When done, we would return to the top node and begin the second traversal, this time creating method specific variables in distinct ContextType objects. We would then call down into the statement and expression level visitors, which would call methods to retrieve data from the specific ContextType object that was passed down. 

All of the helper functions and type environment details were handled behind the scenes by ContextType, except for the existence of MethodDescriptors, which were created to represent a method inside any given class. The MethodDescriptor class was small, only holding space for its return type and types of its parameters in order, used for handling method calls in expressions and noOverloading while type checking class declarations.

![alt text](./img/hw1_uml.png?raw=true)

## Testing and Verification

Tests were written using pre filled out data structures in ContextClass to perform the type checking for lower level statements and expressions, as well as test the helper methods in ContextType itself to ensure behavior was what we expected. We ran our program with the phase 1 tester provided and were able to pass all of the test cases. Along with that, we also wrote a few of our own test cases for all of the edge cases we could think of, including declaring variables of nonexisting types, and overloading with different return types.
