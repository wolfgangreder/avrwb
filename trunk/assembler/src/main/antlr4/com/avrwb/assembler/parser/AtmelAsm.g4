grammar AtmelAsm;
/*
 * $Id$
 *
 * Copyright (C) 2015 Wolfgang Reder <wolfgang.reder@aon.at>.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 *
 */

init: stat + ;

stat: directive NEWLINE
    | directive COMMENT
    | instruction NEWLINE
    | instruction COMMENT
    | label? COMMENT 
    | label? NEWLINE 
    ;

directive: 
      byte_dir 
    | cseg_dir
    | db_dir
    | def_dir
    | device_dir
    | dseg_dir
    | dw_dir
    | equ_dir
    | eseg_dir
    | '.exit'
    | include_dir
    | '.list'
    | '.nolist'
    | org_dir
    | set_dir
    ;

byte_dir: label? DIR_BYTE exp ;

cseg_dir: DIR_CSEG ;

db_dir: label? DIR_DB explist ;

def_dir: DIR_DEF NAME EQU REG_NAME;

device_dir: DIR_DEVICE NAME ;

dseg_dir: DIR_DSEG ;

dw_dir: label? DIR_DW explist ;

equ_dir: DIR_EQU NAME EQU exp ;

eseg_dir: DIR_ESEG ;

org_dir: label? DIR_ORG exp ;

set_dir: DIR_SET NAME EQU exp ;

include_dir : DIR_INCLUDE STRING ;

instruction: label? mnemonic
    ;

mnemonic: adc
    | add
    | adiw
    | and
    | andi
    | asr 
    | bclr
    | bld
    | brbc
    | brbs
    | brcc
    | brcs
    | asm_break
    | breq
    | brge
    | brhc
    | brhs
    | brid
    | brie
    | brlo
    | brlt
    | brmi
    | brne
    | brpl
    | brsh
    | brtc
    | brts
    | brvc
    | brvs
    | bset
    | bst
    | call
    | cbi
    | cbr
    | clc
    | clh
    | cli
    | cln
    | clr
    | cls
    | clt
    | clv
    | clz
    | com
    | cp
    | cpc
    | cpi
    | cpse
    | dec
    | des
    | eicall
    | eijmp
    | elpm
    | eor
    | fmul
    | fmuls
    | fmulsu
    | icall
    | ijmp
    | in
    | inc
    | jmp
    | lac
    | las
    | lat
    | ld
    | ldd
    | ldi
    | lds
    | lpm
    | lsl
    | lsr
    | mov
    | movw
    | mul
    | muls
    | mulsu
    | neg
    | nop
    | or
    | ori
    | out
    | pop
    | push
    | rcall
    | ret
    | reti
    | rjmp
    | rol
    | ror
    | sbc
    | sbci
    | sbi
    | sbic
    | sbis
    | sbiw
    | sbr
    | sbrc
    | sbrs
    | sec
    | seh
    | sei
    | sen
    | ser
    | ses
    | set
    | sev
    | sez
    | sleep
    | spm
    | st
    | std
    | sts
    | sub 
    | subi
    | swap
    | tst
    | wdr
    | xch;
    
adc: ADC exp ',' exp;
add: ADD exp ',' exp;
adiw: ADIW exp ':' exp ',' exp;
and: AND exp ',' exp;
andi: ANDI exp ',' exp;
asr: ASR exp;
bclr: BCLR exp;
bld: BLD exp ',' exp;
brbc: BRBC exp ',' exp;
brbs: BRBS exp ',' exp;
brcc: BRCC exp;
brcs: BRCS exp;
asm_break: BREAK;
breq: BREQ exp;
brge: BRGE exp;
brhc: BRHC exp;
brhs: BRHS exp;
brid: BRID exp;
brie: BRIE exp;
brlo: BRLO exp;
brlt: BRLT exp;
brmi: BRMI exp;
brne: BRNE exp;
brpl: BRPL exp;
brsh: BRSH exp;
brtc: BRTC exp;
brts: BRTS exp;
brvc: BRVC exp;
brvs: BRVS exp;
bset: BSET exp;
bst: BST exp ',' exp;
call: CALL exp;
cbi: CBI exp ',' exp;
cbr: CBR exp ',' exp;
clc: CLC ;
clh: CLH ;
cli: CLI ;
cln: CLN ;
clr: CLR exp ;
cls: CLS ;
clt: CLT ;
clv: CLV ;
clz: CLZ ;
com: COM exp ;
cp: CP exp ',' exp ;
cpc: CPC exp ',' exp;
cpi: CPI exp ',' exp;
cpse: CPSE exp ',' exp;
dec: DEC exp;
des: DES exp;
eicall: EICALL ;
eijmp: EIJMP ;
elpm: ELPM  # Elpm_naked
  | ELPM exp ',' Z_PTR # Elpm_Z
  | ELPM exp ',' Z_P_PTR # Elpm_ZP
  ;
eor: EOR exp ',' exp ;
fmul: FMUL exp ',' exp ;
fmuls: FMULS exp ',' exp ;
fmulsu: FMULSU exp ',' exp ;
icall: ICALL ;
ijmp: IJMP ;
in: IN exp ',' exp;
inc: INC exp ;
jmp: JMP exp ;
lac: LAC  Z_PTR ',' exp;
las: LAS  Z_PTR ',' exp;
lat: LAT Z_PTR ',' exp;
ld: LD exp ',' ALL_PTR  # Ld_ALL
  | LD exp ',' M_ALL_PTR # Ld_M_ALL
  | LD exp ',' ALL_P_PTR # Ld_ALL_P
  ;
ldd: LDD exp ',' YZ_P_PTR exp; 
ldi: LDI exp ',' exp;
lds: LDS exp ',' exp;
lpm: LPM exp ',' Z_P_PTR # Lpm_ZP
  | LPM exp ',' Z_PTR  # Lpm_Z
  | LPM # Lpm_naked
  ;
lsl: LSL  exp;
lsr: LSR exp;
mov: MOV exp ',' exp;
movw: MOVW exp ':' exp ',' exp ':' exp;
mul: MUL exp ',' exp;
muls: MULS exp ',' exp;
mulsu:MULSU exp ',' exp;
neg: NEG exp;
nop: NOP;
or: OR exp ',' exp;
ori: ORI exp ',' exp;
out: OUT exp ',' exp;
pop: POP exp;
push: PUSH exp;
rcall: RCALL exp;
ret: RET;
reti: RETI;
rjmp: RJMP exp;
rol: ROL exp;
ror: ROR exp;
sbc: SBC exp ',' exp;
sbci: SBCI exp ',' exp;
sbi: SBI exp ',' exp;
sbic: SBIC exp ',' exp;
sbis: SBIS exp ',' exp;
sbiw: SBIW exp ':' exp ',' exp;
sbr: SBR exp ',' exp;
sbrc: SBRC exp ',' exp;
sbrs: SBRS exp ',' exp;
sec: SEC ;
seh: SEH ;
sei: SEI ;
sen: SEN ;
ser: SER exp ;
ses: SES;
set: SET;
sev: SEV;
sez: SEZ;
sleep: SLEEP;
spm: SPM  Z_P_PTR # Spm_ZP
  | SPM # Spm_naked
  ;
st: ST  ALL_PTR ',' exp # St_ALL
  | ST ALL_P_PTR ',' exp # St_ALL_P
  | ST M_ALL_PTR ',' exp # St_M_ALL
  ;
std: STD YZ_P_PTR exp ',' exp;
sts: STS exp ',' exp;
sub: SUB exp ',' exp;
subi: SUBI exp ',' exp;
swap: SWAP exp;
tst: TST exp;
wdr: WDR;
xch: XCH Z_PTR ',' exp;

explist: exp (',' exp)* ;

exp: '(' exp ')' # Group
   | FUNC_LOW '(' exp ')' # Func_low
   | FUNC_HIGH '(' exp ')' # Func_high
   | FUNC_BYTE2 '(' exp ')' # Func_high
   | FUNC_BYTE3 '(' exp ')' # Func_byte3
   | FUNC_BYTE4 '(' exp ')' # Func_byte4
   | FUNC_LWRD '(' exp ')' # Func_lwrd
   | FUNC_HWRD '(' exp ')' # Func_hwrd
   | FUNC_PAGE '(' exp ')' # Func_page
   | FUNC_EXP2 '(' exp ')' # Func_exp2
   | FUNC_LOG2 '(' exp ')' # Func_log2
   | FUNC_BITM '(' exp ')' # Func_exp2
   | ('!'|'~'|'-') exp    # Unary
   | exp ('*'|'/') exp    # Product
   | exp ('+'|'-') exp    # Sum
   | exp ('<<'|'>>') exp  # Shift
   | exp ('<'|'>'|'>='|'<=') exp # Compare
   | exp ('=='|'!=') exp # Equal
   | exp '&' exp # BitAnd
   | exp '^' exp # BitXor
   | exp '|' exp # BitOr
   | exp '&&' exp # LogAnd
   | exp '||' exp # LogOr
   | REG_NAME # RegName
   | NAME # Name
   | STRING # String
   | CHAR # Char
   | INT # Int   
   ;

label: NAME ':' ;


/*
 * LEXER 
 */


REG_NAME: [Rr](('0'?(''|'0'|'1'|'2'|'3'|'4'|'5'|'6'|'7'|'8'|'9'))|
('1'[0-9])|'2'[0-9]|'3'[0-1]);

ADC: [Aa][Dd][Cc]; 
ADD: [Aa][Dd][Dd];
ADIW: [Aa][Dd][Ii][Ww];
AND: [Aa][Nn][Dd];
ANDI: [Aa][Nn][Dd][Ii];
ASR: [Aa][Ss][Rr];
BCLR: [Bb][Cc][Ll][Rr];
BLD: [Bb][Ll][Dd];
BRBC: [Bb][Rr][Bb][Cc];
BRBS: [Bb][Rr][Bb][Ss];
BRCC: [Bb][Rr][Cc][Cc];
BRCS: [Bb][Rr][Cc][Ss];
BREAK: [Bb][Rr][Ee][Aa][Kk];
BREQ: [Bb][Rr][Ee][Qq];
BRGE: [Bb][Rr][Gg][Ee];
BRHC: [Bb][Rr][Hh][Cc];
BRHS: [Bb][Rr][Hh][Ss];
BRID: [Bb][Rr][Ii][Dd];
BRIE: [Bb][Rr][Ii][Ee];
BRLO: [Bb][Rr][Ll][Oo];
BRLT: [Bb][Rr][Ll][Tt];
BRMI: [Bb][Rr][Mm][Ii];
BRNE: [Bb][Rr][Nn][Ee];
BRPL: [Bb][Rr][Pp][Ll];
BRSH: [Bb][Rr][Ss][Hh];
BRTC: [Bb][Rr][Tt][Cc];
BRTS: [Bb][Rr][Tt][Ss];
BRVC: [Bb][Rr][Vv][Cc];
BRVS: [Bb][Rr][Vv][Ss];
BSET: [Bb][Ss][Ee][Tt];
BST: [Bb][Ss][Tt];
CALL: [Cc][Aa][Ll][Ll];
CBI: [Cc][Bb][Ii];
CBR: [Cc][Bb][Rr];
CLC: [Cc][Ll][Cc] ;
CLH: [Cc][Ll][Hh] ;
CLI: [Cc][Ll][Ii] ;
CLN: [Cc][Ll][Nn] ;
CLR: [Cc][Ll][Rr] ;
CLS: [Cc][Ll][Ss] ;
CLT: [Cc][Ll][Tt] ;
CLV: [Cc][Ll][Vv] ;
CLZ: [Cc][Ll][Zz] ;
COM: [Cc][Oo][Mm] ;
CP: [Cc][Pp] ;
CPC: [Cc][Pp][Cc] ;
CPI: [Cc][Pp][Ii] ;
CPSE: [Cc][Pp][Ss][Ee];
DEC: [Dd][Ee][Cc] ;
DES: [Dd][Ee][Ss] ;
EICALL: [Ee][Ii][Cc][Aa][Ll][Ll] ;
EIJMP: [Ee][Ii][Jj][Mm][Pp] ;
ELPM: [Ee][Ll][Pp][Mm] ;
EOR: [Ee][Oo][Rr] ;
FMUL: [Ff][Mm][Uu][Ll] ;
FMULS: [Ff][Mm][Uu][Ll][Ss] ;
FMULSU: [Ff][Mm][Uu][Ll][Ss][Uu] ;
ICALL: [Ii][Cc][Aa][Ll][Ll] ;
IJMP: [Ii][Jj][Mm][Pp] ;
IN: [Ii][Nn] ;
INC: [Ii][Nn][Cc] ;
JMP: [Jj][Mm][Pp] ;
LAC: [Ll][Aa][Cc] ;
LAS: [Ll][Aa][Ss] ;
LAT: [Ll][Aa][Tt] ;
LD: [Ll][Dd] ;
LDD: [Ll][Dd][Dd] ;
LDI: [Ll][Dd][Ii] ;
LDS: [Ll][Dd][Ss] ;
LPM: [Ll][Pp][Mm] ;
LSL: [LL][Ss][Ll] ;
LSR: [Ll][Ss][Rr] ;
MOV: [Mm][Oo][Vv] ;
MOVW: [Mm][Oo][Vv][Ww] ;
MUL: [Mm][Uu][Ll] ;
MULS: [Mu][Uu][Ll][Ss] ;
MULSU:[Mu][Uu][Ll][Ss][Uu] ;
NEG: [Nn][Ee][Gg] ;
NOP: [Nn][Oo][Pp] ;
OR: [Oo][Rr] ;
ORI: [Oo][Rr][Ii] ;
OUT: [Oo][Uu][Tt] ;
POP: [Pp][Oo][Pp] ;
PUSH: [Pp][Uu][Ss][Hh] ;
RCALL: [Rr][Cc][Aa][Ll][Ll] ;
RET: [Rr][Ee][Tt];
RETI: [Rr][Ee][Tt][Ii];
RJMP: [Rr][Jj][Mm][Pp] ;
ROL: [Rr][Oo][Ll] ;
ROR: [Rr][Oo][Rr] ;
SBC: [Ss][Bb][Cc] ;
SBCI: [Ss][Bb][Cc][Ii] ;
SBI: [Ss][Bb][Ii] ;
SBIC: [Ss][Bb][Ii][Cc] ;
SBIS: [Ss][Bb][Ii][Ss] ;
SBIW: [Ss][Bb][Ii][Ww] ;
SBR: [Ss][Bb][Rr] ;
SBRC: [Ss][Bb][Rr][Cc] ;
SBRS: [Ss][Bb][Rr][Ss] ;
SEC: [Ss][Ee][Cc] ;
SEH: [Ss][Ee][Hh] ;
SEI: [Ss][Ee][Ii] ;
SEN: [Ss][Ee][Nn] ;
SER: [Ss][Ee][Rr] ;
SES: [Ss][Ee][Ss];
SET: [Ss][Ee][Tt];
SEV: [Ss][Ee][Vv];
SEZ: [Ss][Ee][Zz];
SLEEP: [Ss][Ll][Ee][Ee][Pp];
SPM: [Ss][Pp][Mm] ;
ST: [Ss][Tt] ;
STD: [Ss][Tt][Dd] ;
STS: [Ss][Tt][Ss] ;
SUB: [Ss][Uu][Bb] ;
SUBI: [Ss][Uu][Bb][Ii] ;
SWAP: [Ss][Ww][Aa][Pp] ;
TST: [Tt][Ss][Tt] ;
WDR: [Ww][Dd][Rr] ;
XCH: [Xx][Cc][Hh] ;

X_PTR: [Xx] ;
Y_PTR: [Yy] ;
Z_PTR: [Zz] ;

ALL_PTR: [XxYyZz] ;
ALL_P_PTR : ALL_PTR'+';
M_ALL_PTR: '-'ALL_PTR;
YZ_PTR: [Yy][Zz] ;
YZ_P_PTR: YZ_PTR'+';
Z_P_PTR: Z_PTR'+';

DIR_BYTE: '.'[Bb][Yy][Tt][Ee];
DIR_CSEG: '.'[Cc][Ss][Ee][Gg];
DIR_DB: '.'[Dd][Bb];
DIR_DW: '.'[Dd][Ww];
DIR_DEF: '.'[Dd][Ee][Ff];
DIR_DEVICE: '.'[Dd][Ee][Vv][Ii][Cc][Ee];
DIR_DSEG: '.'[Dd][Ss][Ee][Gg];
DIR_ESEG: '.'[Ee][Ss][Ee][Gg];
DIR_EQU: '.'[Ee][Qq][Uu];
DIR_ORG: '.'[Oo][Rr][Gg];
DIR_SET: '.'[Ss][Ee][Tt];
DIR_INCLUDE: '.'[Ii][Nn][Cc][Ll][Uu][Dd][Ee];

FUNC_LOW: [Ll][Oo][Ww];
FUNC_HIGH: [Hh][Ii][Gg][Hh];
FUNC_BYTE2: [Bb][Yy][Tt][Ee]'2';
FUNC_BYTE3: [Bb][Yy][Tt][Ee]'3';
FUNC_BYTE4: [Bb][Yy][Tt][Ee]'4';
FUNC_LWRD: [Ll][Ww][Rr][Dd];
FUNC_HWRD: [Hh][Ww][Rr][Dd];
FUNC_PAGE: [Pp][Aa][Gg][Ee];
FUNC_EXP2: [Ee][Xx][Pp]'2';
FUNC_LOG2: [Ll][Oo][Gg]'2';
FUNC_BITM: [Bb][Ii][Tt][Mm];

COMMENT: ';' .*? NEWLINE
    ;

CHAR: '\'' . '\'' 
    ;

INT: [0-9]+ 
   | ('0x'|'$'|'0X')[0-9a-fA-F]+
   | ('0b'|'0B')[01]+ 
   ;

EQU: '=' ;

NAME: [a-zA-Z_][a-zA-Z0-9_]* ;

STRING: '"' .*? '"' ;

NEWLINE: ('\r'? '\n')|EOF ;

WS: [ \t]+ -> skip;
