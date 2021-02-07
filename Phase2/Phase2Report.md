# Phase 2: Intermediate Code Generation for Object-oriented Languages

## Requirements and specifications

In this part of the project, we were required to translate a MiniJava program into vapor, an intermediate code language that only deals with functions and memory, no instance of objects. The generated code should have the same functionality as the compiled java program. In order to correctly manage this, we broke the phase down into 3 parts: v-tables, function/object mappings, and actual code generation. 

V-Tables were created by first performing a topological sort on the classes and their parents. Once this ordering is created, we use the mapping created from phase 1 to build v-tables on the premise of "Inherit all the methods from the parent, unless the class itself contains said method", then include all of the methods from the class itself.

In order to generate vapor code for java, a language with classes, objects, and inheritance among other things, we needed much background work to be done in order to correctly manage which methods and variables would be associated with each object, since in vapor an object is just a block of memory, pointing to its variables and class methods.

Class methods were distinguished by listing Classname.methodname(args) to have a standardized naming scheme. Every method for a class would take first an object "this" as an argument, which would be a pointer to the base memory address of the object, for access to other methods, and class data members. 

## Design

We utilized many of the resources that we created from phase 1, through ContextType. We had already created mappings that included methodnames under their respective classes, so all we needed to do was to run the UpperLevelVisitor without the type checking. Once that information is filled out, we use VTableCreator to create and print our VTables whilest mapping the offsets in our ContextType. We also run a method in VTableCreator to map the variables to their proper offset, and use the topological ordering to handle variable inheritance.

The code generation then happens through our visitors, in a mixed tree traversal. We have a tabs variable in ContextType to control the tabbing, and recursively call down into statements and expressions. The expressions are handled in another class "CodeIdGenerator" that is a visitor that returns CodeIdContainer objects, used to store code and ids from expressions. This allows us to assume that the id containing the value of an expression is contained in the "id" field, while its code is contained in "code".

We utilize more temporary variables than are used in some of the examples, to avoid complicated if statement logic in our statements and other expressions, as we wanted to avoid a situation where we have "Add (Sub(1 x) y)" as that would be invalid syntax. So to simplify things, we have it universally where expressions contain a temp that stores the result.

The UML for this phase is as follows:

![alt text](./img/hw2_uml.png?raw=true)

## Testing and Verification

To test this phase of the project, we first ran individual tests on our expression level visitors to ensure that the correct snippets of code were generated, then gradually expanding onto statements, making sure to check the control flow.

After individual module testing was done, we put it all together and ran our program against the tester. The initial run lead to a number of errors, from which running our programs vapor output allowed us to see the individual issues. This was usually small things, such as Add([this+4] 2) being a syntax error, or typos in our label generation leading to duplicated labels. We fixed these individually until our program would pass all test cases in the Phase2Tester script given.

We then created a few edge cases that we believed the given tests didnt cover, such as multi layered inheritance, same method argument and local variable names, among others. Patching where necessary, we were able to fix these and complete this phase of the project.