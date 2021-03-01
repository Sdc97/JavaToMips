# Phase 3: Register Allocation

## Requirements and specifications

In this part of the project, we were required to translate the Vapor language to the Vapor-M language, In this phase the differnces to the Vapor in previus phases is vapor provides local variables, Vapor-M provides registers and stacks. The local variables should be mapped to registers and run-time stack elements with the difference being, Vapor-M uses registers and stack memory instead of local variables.

We have 23 register to use instead of local variables which they are global to all functions. In order to following MIPS calling convention, we using following registrs: using $s0..$s7 registers for general use callee-saved, $t0..$t8 for general use caller-saved, $a0..$a3 for reserved for argument passing, $v0 for returning a result from a call, and temporary registers for loading values from the stack.

Our functions contains three stack arrays; The in and out arrays are for passing arguments between functions, and local array for function-local storage which we use to spilled registers.

## Design

Our design contains three packages; Liveness, printcode, and registeralloc. In Liveness, we have two files in which we have a Graph object, that is our representation of the control flow graph, as a visitor for VInstr objects. IntervalCreation then handles the creation of live intervals, using the method described in the book with slight modifications. We utilize the control flow graph to create in/out sets for each line, and from there establish the intervals that variables are live. 

Next in registeralloc, we have the linear register scan allocation algorithm, which adds on to each interval object in our list a register or stack location. Small changes were made to account for differences in our interval objects. We also created a decorator VarContainer which handles access to the list of intervals, providing a variety of methods to return a specified interval for a variable on a line number, find active registers, etc.

Our last package printcode handles the actual printing of the Vapor-M code, utilizing the groundwork done by the previous two packages, and utilizing the helper functions of VarContainer. 

Rather than an extensive UML, we have provided a program flow map which shows which packages are used and in what order.

![alt text](./img/hw3_flow.png?raw=true)

## Testing and Verification

Extensive testing was done on this section. We created numerous unit tests for each package, to ensure that the output for intervals was correct. However, in order to confirm that intervals were correct, we needed to manually compute the intervals which was time consuming and troublesome. Testing the second phase was easy enough, we just ensured that no register or stack location was used during the same timeframe. Testing the printing was done as an integration of the entire project, and run on the vapor interpreter. 

There are many points we could have improved on, mainly using many *many* debug text blocks which led to bloated code. This could have been done with a debugger, but neither of us were that familiar with debugging java. So as a goal for the next phase, we hope to become more familiar with java debugging tools.

Despite the troubles, we were able to pass all of the sample test cases, and a few of our own to stress test when we use all the available registers in multiple functions.
