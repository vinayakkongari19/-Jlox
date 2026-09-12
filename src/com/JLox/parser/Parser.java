package com.JLox.parser;

import com.JLox.Main;
import com.JLox.ast.Expr;
import com.JLox.ast.Stmt;
import com.JLox.scanner.Token;
import com.JLox.scanner.TokenType;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;
    public Parser(List<Token> tokens){this.tokens=tokens;}

    public List<Stmt> parse(){
        List<Stmt> statements=new ArrayList<>();
        while(!isAtEnd()) statements.add(declaration());
        return statements;
    }
    private Stmt declaration(){
        try { if(match(TokenType.VAR)) return varDeclaration(); return statement(); }
        catch(ParseError error){synchronize(); return null;}
    }
    private Stmt varDeclaration(){
        Token name=consume(TokenType.IDENTIFIER,"Expect variable name.");
        Expr initializer=null;
        if(match(TokenType.EQUAL)) initializer=expression();
        consume(TokenType.SEMICOLON,"Expect ';' after variable declaration.");
        return new Stmt.Var(name,initializer);
    }
    private Stmt statement(){
        if(match(TokenType.FOR)) return forStatement();
        if(match(TokenType.IF)) return ifStatement();
        if(match(TokenType.PRINT)) return printStatement();
        if(match(TokenType.WHILE)) return whileStatement();
        if(match(TokenType.LEFT_BRACE)) return new Stmt.Block(block());
        return expressionStatement();
    }
    private Stmt forStatement(){
        consume(TokenType.LEFT_PAREN,"Expect '(' after 'for'.");
        Stmt initializer;
        if(match(TokenType.SEMICOLON)) initializer=null;
        else if(match(TokenType.VAR)) initializer=varDeclaration();
        else initializer=expressionStatement();
        Expr condition=null;
        if(!check(TokenType.SEMICOLON)) condition=expression();
        consume(TokenType.SEMICOLON,"Expect ';' after loop condition.");
        Expr increment=null;
        if(!check(TokenType.RIGHT_PAREN)) increment=expression();
        consume(TokenType.RIGHT_PAREN,"Expect ')' after for clauses.");
        Stmt body=statement();
        if(increment!=null){List<Stmt> list=new ArrayList<>(); list.add(body); list.add(new Stmt.Expression(increment)); body=new Stmt.Block(list);}
        if(condition==null) condition=new Expr.Literal(true);
        body=new Stmt.While(condition,body);
        if(initializer!=null){List<Stmt> list=new ArrayList<>(); list.add(initializer); list.add(body); body=new Stmt.Block(list);}
        return body;
    }
    private Stmt ifStatement(){
        consume(TokenType.LEFT_PAREN,"Expect '(' after 'if'."); Expr condition=expression(); consume(TokenType.RIGHT_PAREN,"Expect ')' after if condition.");
        Stmt thenBranch=statement(); Stmt elseBranch=null; if(match(TokenType.ELSE)) elseBranch=statement();
        return new Stmt.If(condition,thenBranch,elseBranch);
    }
    private Stmt whileStatement(){
        consume(TokenType.LEFT_PAREN,"Expect '(' after 'while'."); Expr condition=expression(); consume(TokenType.RIGHT_PAREN,"Expect ')' after condition.");
        return new Stmt.While(condition,statement());
    }
    private Stmt printStatement(){Expr value=expression(); consume(TokenType.SEMICOLON,"Expect ';' after value."); return new Stmt.Print(value);}
    private Stmt expressionStatement(){Expr value=expression(); consume(TokenType.SEMICOLON,"Expect ';' after expression."); return new Stmt.Expression(value);}
    private List<Stmt> block(){
        List<Stmt> statements=new ArrayList<>();
        while(!check(TokenType.RIGHT_BRACE)&&!isAtEnd()) statements.add(declaration());
        consume(TokenType.RIGHT_BRACE,"Expect '}' after block."); return statements;
    }
    private Expr expression(){return assignment();}
    private Expr assignment(){
        Expr expr=or();
        if(match(TokenType.EQUAL)){
            Token equals=previous(); Expr value=assignment();
            if(expr instanceof Expr.Variable) return new Expr.Assign(((Expr.Variable)expr).name,value);
            Main.error(equals,"Invalid assignment target.");
        }
        return expr;
    }
    private Expr or(){Expr expr=and(); while(match(TokenType.OR)){Token op=previous(); expr=new Expr.Logical(expr,op,and());} return expr;}
    private Expr and(){Expr expr=equality(); while(match(TokenType.AND)){Token op=previous(); expr=new Expr.Logical(expr,op,equality());} return expr;}
    private Expr equality(){Expr expr=comparison(); while(match(TokenType.BANG_EQUAL,TokenType.EQUAL_EQUAL)){Token op=previous(); expr=new Expr.Binary(expr,op,comparison());} return expr;}
    private Expr comparison(){Expr expr=term(); while(match(TokenType.GREATER,TokenType.GREATER_EQUAL,TokenType.LESS,TokenType.LESS_EQUAL)){Token op=previous(); expr=new Expr.Binary(expr,op,term());} return expr;}
    private Expr term(){Expr expr=factor(); while(match(TokenType.MINUS,TokenType.PLUS)){Token op=previous(); expr=new Expr.Binary(expr,op,factor());} return expr;}
    private Expr factor(){Expr expr=unary(); while(match(TokenType.SLASH,TokenType.STAR)){Token op=previous(); expr=new Expr.Binary(expr,op,unary());} return expr;}
    private Expr unary(){if(match(TokenType.BANG,TokenType.MINUS)) return new Expr.Unary(previous(),unary()); return primary();}
    private Expr primary(){
        if(match(TokenType.FALSE)) return new Expr.Literal(false); if(match(TokenType.TRUE)) return new Expr.Literal(true); if(match(TokenType.NIL)) return new Expr.Literal(null);
        if(match(TokenType.NUMBER,TokenType.STRING)) return new Expr.Literal(previous().literal);
        if(match(TokenType.IDENTIFIER)) return new Expr.Variable(previous());
        if(match(TokenType.LEFT_PAREN)){Expr expr=expression(); consume(TokenType.RIGHT_PAREN,"Expect ')' after expression."); return new Expr.Grouping(expr);}
        throw error(peek(),"Expect expression.");
    }
    private boolean match(TokenType... types){for(TokenType type:types) if(check(type)){advance();return true;} return false;}
    private Token consume(TokenType type,String message){if(check(type)) return advance(); throw error(peek(),message);}
    private ParseError error(Token token,String message){Main.error(token,message);return new ParseError();}
    private void synchronize(){advance(); while(!isAtEnd()){if(previous().type==TokenType.SEMICOLON)return; switch(peek().type){case VAR: case FOR: case IF: case WHILE: case PRINT: return; default:;} advance();}}
    private boolean check(TokenType type){return !isAtEnd()&&peek().type==type;} private Token advance(){if(!isAtEnd())current++;return previous();}
    private boolean isAtEnd(){return peek().type==TokenType.EOF;} private Token peek(){return tokens.get(current);} private Token previous(){return tokens.get(current-1);}
    private static class ParseError extends RuntimeException{}
}
