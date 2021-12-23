package TestParser;

public class Category { //Categorie necessarie per le statistiche.

    private String assertType;
    private String secondParameterType;
    private int times_present;
    private  String chain_types;

    public Category(String assertType, String secondParameterType,String chain_types) {
        this.assertType = assertType;
        this.secondParameterType = secondParameterType;
        this.times_present = 1;
        this.chain_types = chain_types; //Stringa composta da tutti i tipi della chain separati da spazio. Create_chain_tipes() presente in tested class
    }

    public String getAssertType() {
        return assertType;
    }

    public String getChain_types(){return chain_types;}

    public void setAssertType(String assertType) {
        this.assertType = assertType;
    }

    public String getSecondParameterType() {
        return secondParameterType;
    }


    public void setSecondParameterType(String secondParameterType) {
        this.secondParameterType = secondParameterType;
    }

    public int getTimes_present() {
        return times_present;
    }

    public void incrementTimes_present() {
        this.times_present++;
    }

    public void print()
    {
        System.out.println("\t\t\t"+assertType+"\t"+times_present+"\t"+secondParameterType+"\t" + chain_types);
    }
}

