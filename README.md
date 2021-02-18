# JavaToMips
CS179E Compiler design project - Java to Mips compiler

## Phase 1: Type-checking

### Requirements and Specifications

The requirements for phase 1 were to implment the type system described in the MiniJava specification. These rules were defined using several helper functions, logical consequences, and custom data structures (Symbol tables in the form of type environments). This was to be completed using the provided grammar file for MiniJava, as well as the Java Tree Builder, which provided us with visitor interfaces and a Syntax tree to traverse that is produced from a MiniJava file. 

We primarily used GJVoidDepthFirst and GJDepthFirst to build our visitors from, encompassing multiple classes of visitors with methods that serve different purposes. Our program extended these visitors to traverse and create appropriate structures to store contextual information in one pass of the visitor, then perform actual typechecking in the second pass. 

On a successful type check, the program would print "Program type checked successfully" while on any failure it would throw and print an error with the string "Type error" then exit.

### Design

Our design focused around a single class, ContextType, which held all of our contextual information and helper functions to assist in type checking a MiniJava program. In a first pass, we would go down to the method level for each class, while recording the class variables, method names, types, and parameters in static maps in the ContextType class. When done, we would return to the top node and begin the second traversal, this time creating method specific variables in distinct ContextType objects. We would then call down into the statement and expression level visitors, which would call methods to retrieve data from the specific ContextType object that was passed down. 

All of the helper functions and type environment details were handled behind the scenes by ContextType, except for the existence of MethodDescriptors, which were created to represent a method inside any given class. The MethodDescriptor class was small, only holding space for its return type and types of its parameters in order, used for handling method calls in expressions and noOverloading while type checking class declarations.

![alt text](./Phase1/img/hw1_uml.png?raw=true)

### Testing and Verification

Tests were written using pre filled out data structures in ContextClass to perform the type checking for lower level statements and expressions, as well as test the helper methods in ContextType itself to ensure behavior was what we expected. We ran our program with the phase 1 tester provided and were able to pass all of the test cases. Along with that, we also wrote a few of our own test cases for all of the edge cases we could think of, including declaring variables of nonexisting types, and overloading with different return types.


## Phase 2: Intermediate Code Generation for Object-oriented Languages

### Requirements and specifications

In this part of the project, we were required to translate a MiniJava program into vapor, an intermediate code language that only deals with functions and memory, no instance of objects. The generated code should have the same functionality as the compiled java program. In order to correctly manage this, we broke the phase down into 3 parts: v-tables, function/object mappings, and actual code generation. 

V-Tables were created by first performing a topological sort on the classes and their parents. Once this ordering is created, we use the mapping created from phase 1 to build v-tables on the premise of "Inherit all the methods from the parent, unless the class itself contains said method", then include all of the methods from the class itself.

In order to generate vapor code for java, a language with classes, objects, and inheritance among other things, we needed much background work to be done in order to correctly manage which methods and variables would be associated with each object, since in vapor an object is just a block of memory, pointing to its variables and class methods.

Class methods were distinguished by listing Classname.methodname(args) to have a standardized naming scheme. Every method for a class would take first an object "this" as an argument, which would be a pointer to the base memory address of the object, for access to other methods, and class data members. 

### Design

We utilized many of the resources that we created from phase 1, through ContextType. We had already created mappings that included methodnames under their respective classes, so all we needed to do was to run the UpperLevelVisitor without the type checking. Once that information is filled out, we use VTableCreator to create and print our VTables whilest mapping the offsets in our ContextType. We also run a method in VTableCreator to map the variables to their proper offset, and use the topological ordering to handle variable inheritance.

The code generation then happens through our visitors, in a mixed tree traversal. We have a tabs variable in ContextType to control the tabbing, and recursively call down into statements and expressions. The expressions are handled in another class "CodeIdGenerator" that is a visitor that returns CodeIdContainer objects, used to store code and ids from expressions. This allows us to assume that the id containing the value of an expression is contained in the "id" field, while its code is contained in "code".

We utilize more temporary variables than are used in some of the examples, to avoid complicated if statement logic in our statements and other expressions, as we wanted to avoid a situation where we have "Add (Sub(1 x) y)" as that would be invalid syntax. So to simplify things, we have it universally where expressions contain a temp that stores the result.

The UML for this phase is as follows:

![alt text](./Phase2/img/hw2_uml.png?raw=true)

### Testing and Verification

To test this phase of the project, we first ran individual tests on our expression level visitors to ensure that the correct snippets of code were generated, then gradually expanding onto statements, making sure to check the control flow.

After individual module testing was done, we put it all together and ran our program against the tester. The initial run lead to a number of errors, from which running our programs vapor output allowed us to see the individual issues. This was usually small things, such as Add([this+4] 2) being a syntax error, or typos in our label generation leading to duplicated labels. We fixed these individually until our program would pass all test cases in the Phase2Tester script given.

We then created a few edge cases that we believed the given tests didnt cover, such as multi layered inheritance, same method argument and local variable names, among others. Patching where necessary, we were able to fix these and complete this phase of the project.
