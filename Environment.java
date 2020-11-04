import java.util.HashMap;
import java.util.Map.Entry;

class Symtab {
    private HashMap<String,Integer> variableValues = new HashMap<String,Integer>();
    int placecounter=0;
    public String newvar(){ return "%"+placecounter++; }
    int labelcounter=0;
    public String newlabel(){ return "$"+labelcounter++; }
    public Symtab() { }	
    public void setVariable(String name, Integer value) {
	variableValues.put(name, value);
    }
    
    public Integer getVariable(String name){
	Integer value = variableValues.get(name); 
	if (value == null) faux.error("Variable not defined: "+name); 
	return value;
    }
}


class Environment {
    private HashMap<String,Double> variableValues = new HashMap<String,Double>();

    public Environment() { }	
    public void setVariable(String name, Double value) {
	variableValues.put(name, value);
    }
    
    public Double getVariable(String name){
	Double value = variableValues.get(name); 
	if (value == null) { System.err.println("Variable not defined: "+name); System.exit(-1); }
	return value;
    }
    
    public String toString() {
	String table = "";
	for (Entry<String,Double> entry : variableValues.entrySet()) {
	    table += entry.getKey() + "\t-> " + entry.getValue() + "\n";
	}
	return table;
    }   
}

