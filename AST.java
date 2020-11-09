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
//step 1 add abstract public String compile(Symtab env, String place);
abstract class Expr extends AST{
    abstract public Double eval(Environment env);
    abstract public String compile(Symtab env, String place);
};


//step 3 Addition, Subtraction, Multiplication and Division
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


//step 2: add compile to constant and Variable
class Constant extends Expr{
    public Double value;
    Constant(Double value){this.value=value;}
    public Double eval(Environment env){ return value; }

    @Override
    public String compile(Symtab env, String place) {
        //example page 20
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




//step 11 add  abstract public String compile(Symtab env, String thenLabel, String elseLabel);
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
               "IF " + p1+ "==" + p2 +"\n"+
               "THEN " +
               elseLabel+"\n"+"ELSE " +
               thenLabel + "\n";

    }

    ;
}


//TODO:
class Equal extends Condition{
    public Expr e1,e2;

    public Equal(Expr e1, Expr e2) {
        this.e1 = e1;
        this.e2 = e2;
    }

    @Override
    public Boolean eval(Environment env) {
        return e1.eval(env).equals(e2.eval(env));
    }

    @Override
    public String compile(Symtab env, String thenLabel, String elseLabel) {
        String p1= env.newvar();
        String p2= env.newvar();
        return
                e1.compile(env,p1)+
                e2.compile(env,p2)+
                "IF "+p1+"=="+p2+ " THEN "+ thenLabel+ " ELSE " + elseLabel+"\n";
    }
}

class Smaller extends Condition{
    public Expr e1,e2;

    public Smaller(Expr e1, Expr e2) {
        this.e1 = e1;
        this.e2 = e2;
    }

    @Override
    public Boolean eval(Environment env) {
        return e1.eval(env)<(e2.eval(env));
    }

    @Override
    public String compile(Symtab env, String thenLabel, String elseLabel) {
        String p1= env.newvar();
        String p2= env.newvar();
        return
                e1.compile(env,p1)+
                e2.compile(env,p2)+
                "IF "+p1+"<"+p2+ " THEN "+ thenLabel+ " ELSE " + elseLabel+"\n";

    }
}

class Conjunction extends Condition{
    public Condition c1,c2;

    public Conjunction(Condition c1, Condition c2) {
        this.c1 = c1;
        this.c2 = c2;
    }

    @Override
    public Boolean eval(Environment env) {
        return c1.eval(env) && c2.eval(env);
    }

    @Override
    public String compile(Symtab env, String thenLabel, String elseLabel) {
        String l=env.newlabel();

        return
               c1.compile(env,l,elseLabel)+
               "LABEL " +l+":\n"+
               c2.compile(env,thenLabel,elseLabel);
    }
}

class DisConjunction extends Condition{
    public Condition c1,c2;

    public DisConjunction(Condition c1, Condition c2) {
        this.c1 = c1;
        this.c2 = c2;
    }

    @Override
    public Boolean eval(Environment env) {
        return c1.eval(env) || c2.eval(env);
    }

    @Override
    public String compile(Symtab env, String thenLabel, String elseLabel) {
        String l=env.newlabel();
        return
                c1.compile(env,thenLabel,l)+
                "LABEL " +l+":\n"+
                c2.compile(env,thenLabel,elseLabel);
    }
}

class Negation extends Condition{
    public Condition c;

    public Negation(Condition c) {
        this.c = c;
    }

    @Override
    public Boolean eval(Environment env) {
        return !c.eval(env);
    }

    @Override
    public String compile(Symtab env, String thenLabel, String elseLabel) {
        return c.compile(env,elseLabel,thenLabel);
    }
}






//step 5 add abstract public String compile(Symtab env);
abstract class Command extends AST{
    abstract public void eval(Environment env);
    abstract public String compile(Symtab env);
};

// step 6 add compile to Assignment
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

//TODO
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

//step 7 add compile to Output
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
                "GOTO " + start + "\n" +
                "LABEL " + end + ":\n";
        /*
        IF condition THEN BODY ELSE GOTO END
        BODY:
        CODE BODY
        GOTO start
        end
        */

    }
}


//step 8 add compile to Sequence
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
//step 9 add compile to Nop, no operation, use it for i we have if with empty else
class Nop extends Command{
    Nop(){};
    public void eval(Environment env){}

    @Override
    public String compile(Symtab env) {
        return "";
    }

    ;
}

class If extends Command{
    public Condition c;
    public  Command p;

    public If(Condition c, Command p) {
        this.c = c;
        this.p = p;
    }

    @Override
    public void eval(Environment env) {
        if (c.eval(env))
            p.eval(env);
    }

    @Override
    public String compile(Symtab env) {
        String thenl=env.newlabel();
        String endl=env.newlabel();

        return c.compile(env,thenl,endl)+
               "LABEL " + thenl+ "\n"+
               p.compile(env)+
               "LABEL " + endl+ ":\n";
    }
}
