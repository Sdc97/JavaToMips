# Phase 4: Activation Records and Instruction Selection

## Requirements and specifications

In this part of the project, we were required to map the Vapor-M registrs and stacks to MIPS registers and runtime stack, in which the Vapor-M instructions maps to MIPS intructions.

This phase is use the same concept as phase 3 in regards of registers name with new common instructions for load immediate, load address, load word, store word, jump and link, jump and .etc

Our stack grows from higher addresses down to lower addresser, which means when anything calls to be load (In) we use $fp, and in other hand when it used(out) we use $sp.

## Design

Our design contains one packages; Printer which we only have one file named CodeGenerator.java where we handle all instructions in there.

We are using same visitor as phase3, and we hanle each instructions, VAssign, VCall, VBuiltIn, VMemWrite, VMemRead, VBranch, VGoto, VReturn and print the instructions according to the MIPS snippet, and intructions provided in project page.   

![alt text](./img/hw4_flow.png?raw=true)

## Testing and Verification

Extensive testing was done on this section. We created unit tests for our package, to ensure that the output for instructions was correct. However, in order to confirm that outputs were correct, we needed to manually compute the instructions. Testing the printing was done as an integration of the entire project, and run on the MIPS interpreter. 

Despite the troubles, we were able to pass all of the sample test cases, and a few of our own to stress test when we use all the available registers in multiple functions.

