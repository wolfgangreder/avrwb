grammar Atmelasm;
init: stat + ;

stat: directive NEWLINE
    | directive COMMENT
    | instruction NEWLINE
    | instruction COMMENT
    | COMMENT 
    | NEWLINE 
    ;

directive: '.include' STRING 
    | '.cseg'
    | '.org' exp
    ;

instruction: MNEMONIC  exp ',' exp 
    | MNEMONIC exp
    | MNEMONIC
    ;


exp: exp ('+'|'-') exp
   | NAME
   | INT
   ;

COMMENT: ';' .*? NEWLINE;


INT: [0-9]+ ;
MNEMONIC:      'adc' |
      'add' |
      'adiw' |
      'and' |
      'andi' |
      'asr' |
      'bclr' |
      'bld' |
      'brbc' |
      'brbs' |
      'brcc' |
      'brcs' |
      'break' |
      'breq' |
      'brge' |
      'brhc' |
      'brhs' |
      'brid' |
      'brie' |
      'brlo' |
      'brlt' |
      'brmi' |
      'brne' |
      'brpl' |
      'brsh' |
      'brtc' |
      'brts' |
      'brvc' |
      'brvs' |
      'bset' |
      'bst' |
      'call' |
      'cbi' |
      'cbr' |
      'clc' |
      'clh' |
      'cli' |
      'cln' |
      'clr' |
      'cls' |
      'clt' |
      'clv' |
      'clz' |
      'com' |
      'cp' |
      'cpc' |
      'cpi' |
      'cpse' |
      'dec' |
      'des' |
      'eicall' |
      'eijmp' |
      'elpm' |
      'eor' |
      'fmul' |
      'fmuls' |
      'fmulsu' |
      'icall' |
      'ijmp' |
      'in' |
      'inc' |
      'jmp' |
      'lac' |
      'las' |
      'lat' |
      'ld' |
      'ldd' |
      'ldi' |
      'lds' |
      'lpm' |
      'lsl' |
      'lsr' |
      'mov' |
      'movw' |
      'mul' |
      'muls' |
      'mulsu' |
      'neg' |
      'nop' |
      'or' |
      'ori' |
      'out' |
      'pop' |
      'push' |
      'rcall' |
      'ret' |
      'reti' |
      'rjmp' |
      'rol' |
      'ror' |
      'sbc' |
      'sbci' |
      'sbi' |
      'sbic' |
      'sbis' |
      'sbiw' |
      'sbr' |
      'sbrc' |
      'sbrs' |
      'sec' |
      'seh' |
      'sei' |
      'sen' |
      'ser' |
      'ses' |
      'set' |
      'sev' |
      'sez' |
      'sleep' |
      'spm' |
      'st' |
      'std' |
      'sts' |
      'sub' |
      'subi' |
      'swap' |
      'tst' |
      'wdr' |
      'xch' ;

NAME: [a-z_][a-z0-9_]* ;

STRING: '"' .*? '"' ;

NEWLINE: '\r'? '\n' ;

WS: [ \t]+ -> skip;

/*


init: ((directive ) NEWLINE 
    | (instruction) NEWLINE 
    | COMMENT NEWLINE 
    | NEWLINE )*
    ;

 
directive: label_directive 
    | SIMPLE_DIRECTIVE 
    | '.def' NAME '=' REGISTER 
    | '.device' NAME 
    | '.equ' NAME '=' exp 
    | '.include' STRING 
    ;

label_directive: LABEL_DIRECTIVE exp
    ;

label: NAME ':' ;

instruction: MNEMONIC exp ',' exp 
           | MNEMONIC exp 
           | MNEMONIC 
           ;

exp: FUNCTION '(' exp ')'
   | ('!'|'~'|'-') exp
   | exp ('*'|'/') exp
   | exp ('+'|'-') exp
   | exp ('<<'|'>>') exp
   | exp ('<'|'>'|'>='|'<=') exp
   | exp ('=='|'!=') exp
   | exp '&' exp
   | exp '^' exp
   | exp '|' exp
   | exp '&&' exp
   | exp '||' exp
   | INT
   | CHAR
   | STRING
   | 'PC'
   | '(' exp ')'
   ;

explist: exp (',' exp)*       ;

SIMPLE_DIRECTIVE: '.cseg' |'.dseg' |'.eseg' | '.exit' | '.list'|'.nolist';
LABEL_DIRECTIVE : '.byte' | '.db' | '.dw' | '.org' ;

REGISTER: 'r' DEZ_INT       ;    

FILENAME: NAME ;

FUNCTION: NAME ;

COMMENT: ';' .*? NEWLINE;

MNEMONIC: [a-z]+ ;

NAME: [a-z_] [a-z0-9_]* ;

DEZ_INT: [0-9]+ ;

HEX_INT: ('$'|'0x')+ [a-f0-9]*;

BIN_INT: '0b' [01]*;

INT: DEZ_INT | HEX_INT | BIN_INT;


CHAR: '\'' . '\'' ;

STRING: '"' .*? '"' ;

NEWLINE: '\r'? '\n' ;

WS: [ \t]+ -> skip;
*/