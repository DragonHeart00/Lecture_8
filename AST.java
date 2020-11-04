import java.util.HashMap;
import java.util.Map.Entry;
import java.util.List;
import java.util.ArrayList;

class faux{ // collection of non-OO auxiliary functions (currently just error)
    public static void error(String msg){
	System.err.println("Interpreter error: "+msg);
	System.exit(-1);
    }
}





abstract class AST{};

abstract class Expr extends AST{
    abstract public Double eval(Environment env);
    abstract public String compile(Symtab env, String place);
};

class Addition extends Expr{
    public Expr e1,e2;
    Addition(Expr e1,Expr e2){this.e1=e1; this.e2=e2;}
    public Double eval(Environment env){ return e1.eval(env)+e2.eval(env); }

    //compile Addition
    @Override
    public String compile(Symtab env, String place) {
        String p1=env.newvar();
        String p2=env.newvar();
        return e1.compile(env,p1)+
                e2.compile(env,p2)+
                place+":="+p1+ "+"+p2+"\n";
    }

    ;
}

class Subtraction extends Expr{
    public Expr e1,e2;
    Subtraction(Expr e1,Expr e2){this.e1=e1; this.e2=e2;}
    public Double eval(Environment env){ return e1.eval(env)-e2.eval(env); }

    @Override
    public String compile(Symtab env, String place) {
        String p1=env.newvar();
        String p2=env.newvar();
        return e1.compile(env,p1)+
                e2.compile(env,p2)+
                place+":="+p1+ "-"+p2+"\n";
    }

    ;
}

class Multiplication extends Expr{
    public Expr e1,e2;
    Multiplication(Expr e1,Expr e2){this.e1=e1; this.e2=e2;}
    public Double eval(Environment env){ return e1.eval(env)*e2.eval(env); }
    //example page 24
    @Override
    public String compile(Symtab env, String place) {
        String p1=env.newvar();
        String p2=env.newvar();
        return e1.compile(env,p1)+
               e2.compile(env,p2)+
               place+":="+p1+ "*"+p2+"\n";
    }

    ;
}

class Division extends Expr{
    public Expr e1,e2;
    Division(Expr e1,Expr e2){this.e1=e1; this.e2=e2;}
    public Double eval(Environment env){ return e1.eval(env)/e2.eval(env); }

    @Override
    public String compile(Symtab env, String place) {
        String p1=env.newvar();
        String p2=env.newvar();
        return e1.compile(env,p1)+
                e2.compile(env,p2)+
                place+":="+p1+ "/"+p2+"\n";
    }

    ;
}

class Constant extends Expr{
    public Double value;
    Constant(Double value){this.value=value;}
    public Double eval(Environment env){ return value; }

    //example page 20
    @Override
    public String compile(Symtab env, String place) {

        return place+":="+value+"\n";
    };
}

class Variable extends Expr{
    public String name;
    Variable(String name){this.name=name;}
    public Double eval(Environment env){
	return env.getVariable(name);
    }

    //example page 22
    @Override
    public String compile(Symtab env, String place) {
        return place+":="+name+"\n";
    };
}

//TODO: 
class Array extends  Expr{
    public String a;
    public Expr index;


    public Array(String a, Expr index) {
        this.a = a;
        this.index = index;
    }

    @Override
    public Double eval(Environment env) {
        return  env.getVariable(a+index.eval(env));

    }

    @Override
    public String compile(Symtab env, String place) {
        String i = env.newvar();
        return index.compile(env,i) + place+":="+a+"["+i+"]";
    }
}





abstract class Condition extends AST{
    abstract public Boolean eval(Environment env);
    abstract public String compile(Symtab env, String thenLabel, String elseLabel);
};

class Unequal extends Condition{
    public Expr e1,e2;
    Unequal(Expr e1,Expr e2){this.e1=e1; this.e2=e2;}
    public Boolean eval(Environment env){ return !e1.eval(env).equals(e2.eval(env)); }

    @Override
    public String compile(Symtab env, String thenLabel, String elseLabel) {
       String p1 = env.newvar();
       String p2 = env.newvar();

        return e1.compile(env,p1)+
               e2.compile(env,p2)+
               "IF " + p1+ "==" + p2 +
               " THEN" +
               elseLabel+ "ELSE" +
               thenLabel + "\n";

    }

    ;
}















abstract class Command extends AST{
    abstract public void eval(Environment env);
    abstract public String compile(Symtab env);
};

class Assignment extends Command{
    public String x;
    public Expr e;
    Assignment(String x, Expr e){this.x=x; this.e=e;}
    public void eval(Environment env){
	env.setVariable(x,e.eval(env));
    }

    @Override
    public String compile(Symtab env) {
        return e.compile(env,x);
    }
}

class ArrayAssignment extends  Command{

    public String x;
    public  Expr index;
    public  Expr e;

    ArrayAssignment(String x, Expr index, Expr e) {
        this.x = x;
        this.index = index;
        this.e = e;
    }

    @Override
    public void eval(Environment env) {
      env.setVariable(x+index.eval(env),e.eval(env));
    }

    @Override
    public String compile(Symtab env) {
        String i =env.newvar();
        return index.compile(env,i)+
               e.compile(env,x +"[" + i +"]") ;
    }
}

class Output extends Command{
    public Expr e;
    Output(Expr e){this.e=e;}
    public void eval(Environment env){
	System.out.println(e.eval(env));
    }

    @Override
    public String compile(Symtab env) {
        return "";//return nothing
    }
}

class While extends Command{
    public Condition c;
    public Command body;
    While(Condition c, Command body){this.c=c; this.body=body;}
    public void eval(Environment env){
	while(c.eval(env)){ body.eval(env); }
    }

    @Override
    public String compile(Symtab env) {
        String start=env.newlabel();
        String lbody =env.newlabel();
        String end=env.newlabel();

        return
                "LABEL " + start + ":\n" + c.compile(env,lbody,end) +
                "LABEL " + lbody + ":\n" + body.compile(env) +
                "GOTO" + start + "\n" +
                "LABEL" + end + ":\n";
        /*
        IF condition THEN BODY ELSE GOTO END
        BODY:
        CODE BODY
        GOTO start
        end
        */

    }
}

class Sequence extends Command{
    public Command c1,c2;
    Sequence(Command c1,Command c2){this.c1=c1; this.c2=c2;}
    public void eval(Environment env){
	c1.eval(env);
	c2.eval(env);
    }

    @Override
    public String compile(Symtab env) {
        return c1.compile(env)+c2.compile(env);
    }
}
//no operation, use it for i we have if with empty else
class Nop extends Command{
    Nop(){};
    public void eval(Environment env){}

    @Override
    public String compile(Symtab env) {
        return "";
    }

    ;
}
