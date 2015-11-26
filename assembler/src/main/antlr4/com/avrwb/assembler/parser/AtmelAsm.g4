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

byte_dir: label? '.byte' exp ;

cseg_dir: '.cseg' ;

db_dir: label? '.db' explist ;

def_dir: '.def' NAME '=' NAME;

device_dir: '.device' NAME ;

dseg_dir: '.dseg' ;

dw_dir: label? '.dw' explist ;

equ_dir: '.equ' NAME '=' exp ;

eseg_dir: '.eseg' ;

org_dir: label? '.org' exp ;

set_dir: '.set' NAME '=' exp ;

include_dir : '.include' STRING ;

instruction: label? MNEMONIC  exp ',' exp 
    | label? MNEMONIC exp
    | label? MNEMONIC
    ;

explist: exp (',' exp)* ;

exp: '(' exp ')' # Group
   | FUNCTION '(' exp ')' # Func
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
   | NAME # Name
   | STRING # String
   | CHAR # Char
   | INT # Int
   ;

label: NAME ':' ;

FUNCTION: 'low'
   | 'high'
   | 'byte2'
   | 'byte3'
   | 'byte4'
   | 'lwrd'
   | 'hwrd'
   | 'page'
   | 'exp2'
   | 'log2'
   ;

COMMENT: ';' .*? NEWLINE
    ;

CHAR: '\'' . '\'' 
    ;

INT: [0-9]+ 
   | ('0x'|'$')[0-9a-f]+
   | '0b'[01]+ 
   ;

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
