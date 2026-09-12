package com.JLox.interpreter;

import com.JLox.Main;
import com.JLox.ast.Expr;
import com.JLox.ast.Stmt;
import com.JLox.scanner.Token;
import com.JLox.scanner.TokenType;
import java.util.List;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
    private Environment environment=new Environment();
    public void interpret(List<Stmt> statements){try{for(Stmt statement:statements) if(statement!=null)execute(statement);}catch(RuntimeError error){Main.runtimeError(error);}}
    private void execute(Stmt stmt){stmt.accept(this);} private Object evaluate(Expr expr){return expr.accept(this);}
    public Void visitExpressionStmt(Stmt.Expression s){evaluate(s.expression);return null;}
    public Void visitPrintStmt(Stmt.Print s){System.out.println(stringify(evaluate(s.expression)));return null;}
    public Void visitVarStmt(Stmt.Var s){Object value=null;if(s.initializer!=null)value=evaluate(s.initializer);environment.define(s.name.lexeme,value);return null;}
    public Void visitBlockStmt(Stmt.Block s){executeBlock(s.statements,new Environment(environment));return null;}
    public Void visitIfStmt(Stmt.If s){if(isTruthy(evaluate(s.condition)))execute(s.thenBranch);else if(s.elseBranch!=null)execute(s.elseBranch);return null;}
    public Void visitWhileStmt(Stmt.While s){while(isTruthy(evaluate(s.condition)))execute(s.body);return null;}
    public Object visitLiteralExpr(Expr.Literal e){return e.value;}
    public Object visitGroupingExpr(Expr.Grouping e){return evaluate(e.expression);}
    public Object visitUnaryExpr(Expr.Unary e){Object right=evaluate(e.right);switch(e.operator.type){case MINUS:checkNumberOperand(e.operator,right);return -(double)right;case BANG:return !isTruthy(right);default:return null;}}
    public Object visitVariableExpr(Expr.Variable e){return environment.get(e.name);}
    public Object visitAssignExpr(Expr.Assign e){Object value=evaluate(e.value);environment.assign(e.name,value);return value;}
    public Object visitLogicalExpr(Expr.Logical e){Object left=evaluate(e.left);if(e.operator.type==TokenType.OR){if(isTruthy(left))return left;}else{if(!isTruthy(left))return left;}return evaluate(e.right);}
    public Object visitBinaryExpr(Expr.Binary e){Object left=evaluate(e.left),right=evaluate(e.right);switch(e.operator.type){case MINUS:checkNumberOperands(e.operator,left,right);return(double)left-(double)right;case SLASH:checkNumberOperands(e.operator,left,right);return(double)left/(double)right;case STAR:checkNumberOperands(e.operator,left,right);return(double)left*(double)right;case PLUS:if(left instanceof Double&&right instanceof Double)return(double)left+(double)right;if(left instanceof String&&right instanceof String)return(String)left+(String)right;throw new RuntimeError(e.operator,"Operands must be two numbers or two strings.");case GREATER:checkNumberOperands(e.operator,left,right);return(double)left>(double)right;case GREATER_EQUAL:checkNumberOperands(e.operator,left,right);return(double)left>=(double)right;case LESS:checkNumberOperands(e.operator,left,right);return(double)left<(double)right;case LESS_EQUAL:checkNumberOperands(e.operator,left,right);return(double)left<=(double)right;case EQUAL_EQUAL:return isEqual(left,right);case BANG_EQUAL:return !isEqual(left,right);default:return null;}}
    private void executeBlock(List<Stmt> statements,Environment newEnv){Environment previous=environment;try{environment=newEnv;for(Stmt statement:statements)if(statement!=null)execute(statement);}finally{environment=previous;}}
    private boolean isTruthy(Object object){if(object==null)return false;if(object instanceof Boolean)return(boolean)object;return true;}
    private boolean isEqual(Object a,Object b){if(a==null&&b==null)return true;if(a==null)return false;return a.equals(b);}
    private void checkNumberOperand(Token op,Object operand){if(operand instanceof Double)return;throw new RuntimeError(op,"Operand must be a number.");}
    private void checkNumberOperands(Token op,Object a,Object b){if(a instanceof Double&&b instanceof Double)return;throw new RuntimeError(op,"Operands must be numbers.");}
    private String stringify(Object object){if(object==null)return "nil";if(object instanceof Double){String text=object.toString();if(text.endsWith(".0"))text=text.substring(0,text.length()-2);return text;}return object.toString();}
}
