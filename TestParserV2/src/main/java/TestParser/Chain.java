package TestParser;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static TestParser.RunParser.mainCompilations;

public class Chain { //Nella Classe chain, troviamo la lista dei nome metodi, la lista dei tipi dei metodi, l'assert in cui vengono utilizzate ed il numero di volte in cui si trovano chain con queste precedenti caratteristiche
    private List<String> types;
    private List<String> methods;
    private String assertType;
    private int times_present;

    public  Chain(List<String> types,List<String> methods, String assertType)
    {
        this.types = types;// = new ArrayList<String>();
        this.methods = methods;// = new ArrayList<String>();
        this.assertType = assertType;
        times_present = 1;
    }

    String getAssertType()
    {
        return assertType;
    }

    int getSize()
    {
        return  methods.size();
    }

    List<String> getMethods()
    {
        return methods;
    }


    String getLastMethod()  //l'ultima chiamata della chain,questo metodo verrà modificato per ottenere l'ultima chiamata di metodo che appartiene al progetto.
    {
        return methods.get(methods.size()-1);
    }


    public String getLastProjectMethod()
    {
        MethodVisitor methodVisitor = new MethodVisitor();

        for(int i = methods.size()-1; i >= 0; i--)
        {
            if(methodVisitor.isMainMethod(methods.get(i)))
                return methods.get(i);
        }

        System.out.println(getLastMethod());
        return "no method found";
    }

    List<String> getSimplifiedChainMethods()
    {
        List<String> SimplifiedChainMethods = new ArrayList<String>();

        for(int i = getLastProjectMethodIndex(); i<methods.size() ; i++)
            SimplifiedChainMethods.add(methods.get(i));
        return SimplifiedChainMethods;
    }


    public int getLastProjectMethodIndex()
    {
        MethodVisitor methodVisitor = new MethodVisitor();
        for(int i = methods.size() - 1 ;i >= 0; i--)
            if(methodVisitor.isMainMethod(methods.get(i)))
                return i;

            return 0;
    }


String getFirstMethod()
{
    return methods.get(0);
}


    void increment_time_present()
    {
        times_present++;
    }

    int getTimes_present()
    {
        return times_present;
    }

}
