;.include "m8def.inc"
; kommentar 1
   ; kommentar 2
.dseg
var1: .db 23, (1<<4)+0x75*20
.db 15

.cseg
reset: .org 0
 rjmp label1
 mov r0,r31  ;kommentar 3
.org 23
label1: neg r2
xch Z,erf
ldi r1,low(23+34<<2)
nop
