startlabel: 
.equ spl = 22
rjmp pushseq
rjmp init
adc r23,r31
mov r0,r1

mov r20,r0
push r0 ; push hot gfeugelt
nop
nop
.org 0x21
init:
mov r22,r23
pop r22
pushseq:
push r0
push r1
push r2
in r0,spl
push r0
out spl,r1
rjmp init
