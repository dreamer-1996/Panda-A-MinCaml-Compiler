package org.panda.backend;

import org.panda.backend.ast.*;
import java_cup.runtime.*;
import org.panda.ast.*;

%%

/* -----------------Options and Declarations Section----------------- */

%public
%class BackendLexer

/*
  The current line number can be accessed with the variable yyline
  and the current column number with the variable yycolumn.
*/
%line
%column

/*
   Will switch to a CUP compatibility mode to interface with a CUP
   generated parser.
*/
%cup

/*
  Declarations

  Code between %{ and %}, both of which must be at the beginning of a
  line, will be copied letter to letter into the lexer class source.
  Here you declare member variables and functions that are used inside
  scanner actions.
*/
%{
    /* To create a new java_cup.runtime.Symbol with information about
       the current token, the token will have no value in this
       case. */
    private Symbol symbol(int type) {
        return new Symbol(type, yyline, yycolumn);
    }

    /* Also creates a new java_cup.runtime.Symbol with information
       about the current token, but this object has a value. */
    private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline, yycolumn, value);
    }
%}


/*
  Macro Declarations

  These declarations are regular expressions that will be used latter
  in the Lexical Rules Section.
*/

space = [ \t\n\r]
digit = [0-9]
lower = [a-z]
upper = [A-Z]
comment =  "(*" [^*] ~"*)"
%%
/* ------------------------Lexical Rules Section---------------------- */

/*
   This section contains regular expressions and actions, i.e. Java
   code, that will be executed when the scanner matches the associated
   regular expression. */

   /* YYINITIAL is the state at which the lexer begins scanning.  So
   these regular expressions will only be matched if the scanner is in
   the start state YYINITIAL. */

<YYINITIAL> {

{space}+  { }
{comment}   {  }
("-")? {digit}+  { return symbol(sym.INT, new Integer(yytext())); }
("-")? {digit}+ ("." {digit}*)? (["e" "E"] ["+" "-"]? digit+)?
        { return symbol(sym.FLOAT, new java.lang.Float(yytext())); }

"("     { return symbol(sym.LPAREN); }
")"     { return symbol(sym.RPAREN); }
"+"     { return symbol(sym.PLUS); }
"="     { return symbol(sym.EQUAL); }
"=."     { return symbol(sym.FEQUAL); }
"<="    { return symbol(sym.LE); }
"<=."    { return symbol(sym.FLE); }
">="    { return symbol(sym.GE); }
"if"    { return symbol(sym.IF); }
"then"  { return symbol(sym.THEN); }
"else"  { return symbol(sym.ELSE); }
"let"   { return symbol(sym.LET); }
"in"    { return symbol(sym.IN); }
"."     { return symbol(sym.DOT); }
"neg"     { return symbol(sym.NEG); }
"fneg"     { return symbol(sym.FNEG); }
"mem"     { return symbol(sym.MEM); }
"fmul"     { return symbol(sym.FMUL); }
"fdiv"     { return symbol(sym.FDIV); }
"fsub"     { return symbol(sym.FSUB); }
"fadd"     { return symbol(sym.FADD); }
"<-"     { return symbol(sym.ASSIGN); }
"add"     { return symbol(sym.ADD); }
"sub"     { return symbol(sym.SUB); }
"call"     { return symbol(sym.CALL); }
"new"     { return symbol(sym.NEW); }

"nop"     { return symbol(sym.NOP); }
"call_closure"     { return symbol(sym.APPCLO); }
"_"     { return symbol(sym.UNDERSC); }

/* ------ Not sure if eof is needed --------- */
eof     { return symbol(sym.EOF); }
/* ------ Not sure if eof is needed --------- */

"%self"  { return symbol(sym.IDENT, Var.get("%self") ); }
{lower} ({digit}|{lower}|{upper}|"_")*        { return symbol(sym.IDENT, Var.get(yytext()) ); }
("_" {lower}) ({digit}|{lower}|{upper}|"_")*  { return symbol(sym.LABEL, Label.get(yytext()) ); }



}
[^]                    { throw new Error("Illegal character <"+yytext()+">"); }



