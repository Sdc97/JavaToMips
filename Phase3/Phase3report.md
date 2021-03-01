# Phase 3: Register Allocation

## Requirements and specifications

In this part of the project, we were required to translate the Vapor language to the Vapor-M language, In this phase the diffrences to the Vapor in previus phases is vapor provides local variables, Vapor-M provides registers and stacks. The local variables should be mapped to registers and run-time stack elements with the difrences in which a Vapor-M uses registers and stack memory instead of local variables.

We have 23 register to use instead of local variables which they are global to all functions. In order to following MIPS calling convention, we using following registrs: using $s0..$s7 registers for general use callee-saved, $t0..$t8 for general use caller-saved, $a0..$a3 for reserved for argument passing, $v0 for returning a result from a call, and temporary registers for loading values from the stack.

Our functions contains three stack arrays; The in and out arrays are for passing arguments between functions, and local array for function-local storage which we use to spilled registers.

## Design

Our design contains three packages; Liveness, printcode, and registeralloc. In Liveness, we have two file in which graph.java that extends VInstr.VisitorP visitor and handle to generates the flow graph. And IntervalCreation.java which handles the label mappings in our graph, controles the flow graph, and handles the in and out for passing arguements between functions. In other package called registeralloc, we have our linear scan algorithm in the java file named Algorithm.java, which LinearScanRegisterAllocation() function handles the to scan the registers, expireOldInterval(Interval interval), to remove the old register from active and add register to pool of free registers, and spillAtInterval(Interval interval) function to spill the interval that end last as it can contains longer.  In this package we have Interval.java which is a setter, getter, start and end point for intervals, and registers.java to get the register and the type which in this case either is "t" or "s".
And lastly printcode packege that contains Printvisitor.java and VarContainer.java where handles all printing and outputs tasks. 


The UML for this phase is as follows:

![alt text](./img/hw2_uml.png?raw=true)

## Testing and Verification

After individual module testing was done, we put it all together and ran our program against the tester. The initial run lead to a number of errors, from which running our programs vapor output allowed us to see the individual issues. This was usually small things, such as Add([this+4] 2) being a syntax error, or typos in our label generation leading to duplicated labels. We fixed these individually until our program would pass all test cases in the Phase2Tester script given.
