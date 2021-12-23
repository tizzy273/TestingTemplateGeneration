package TestParser;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.AssertStmt;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static TestParser.RunParser.testCompilations;

public class TestedClass{
    private CompilationUnit testedClass;
    private List<TestedMethod> tested_methods; //Metodi testati
    public static  List<Statistic> statistics;
    List<CompilationUnit> testing_classes;


    public TestedClass(CompilationUnit testedClass) { //Tested class, la classe di cui si vogliono conoscere i test ( che potrebbero anche non esistere). Test Compilations, tutte le classi di test.
        this.testedClass = testedClass; //classi testate

        tested_methods = new ArrayList<TestedMethod>();
        statistics = new ArrayList<Statistic>();
    }


    public void addTested_method(String return_type,String name,String assertType,String secondParamerType,String chain_types)
    {
        int methodIndex = getMethodIndex(name);
        if(methodIndex == -1)
        {
            TestedMethod testedMethod = new TestedMethod(return_type,name);
            testedMethod.addCategory(assertType,secondParamerType,chain_types);
            tested_methods.add(testedMethod);
        }
        else
        {
            tested_methods.get(methodIndex).addCategory(assertType,secondParamerType,chain_types);
        }
    }


    public CompilationUnit getTestedClass() {
        return testedClass;
    }


    public String getName(CompilationUnit cu)
    {
        ClassNamePrinter classNamePrinter = new ClassNamePrinter();
        classNamePrinter.visit(cu,null);
        return classNamePrinter.getName();
    }


    public List<CompilationUnit> getTestingClasses() // //il metodo restituisce le classi che testano, "testing_classes": ad ogni classe quindi vengono associate tutte le classi di test relative, se esistono
    {
        //System.out.println("Sto filtrando...");
        List<CompilationUnit> testing_classes = new ArrayList<CompilationUnit>();
        String class_name = getName(testedClass);
        for(CompilationUnit testcompilation : testCompilations)
        {

            testcompilation.findAll(MethodCallExpr.class).forEach(methodCallExpr -> {

                if(methodCallExpr.getName().asString().contains("assert")) { // si cercano tutti gli assert all'interno della class di test iterata
                    NodeList<Expression> arguments = methodCallExpr.getArguments(); // argomenti dell'assert

                    for(Expression argument : arguments) {
                        if(argument instanceof  MethodCallExpr) { // se l'argomento è a sua volta una chiamata a funzione, si analizza se è una chiamata diretta o una chain grazie al ciclo do while sottostante

                            Node leftChild = argument;
                            do {  //Analisi argomento assert:
                                List<MethodCallExpr>  argumentCalls = leftChild.findAll(MethodCallExpr.class);
                                for(MethodCallExpr argumentCall : argumentCalls) {
                                  try
                                   {String chain_types = new String();
                                    ResolvedMethodDeclaration resolvedMethod = argumentCall.resolve();
                                    if (resolvedMethod.getClassName().equals(class_name)) { //Se la classe del metodo chiamato all'interno dell'assert è uguale al nome della classe testata, allora la class di test viene aggiunta alla lista testing classes
                                        testing_classes.add(testcompilation);
                                        //  if(!resolvedMethod.getReturnType().describe().equals(argument.calculateResolvedType().describe()))
                                        chain_types = create_chain_types(arguments); //Stringa contenente tutti i tipi della catena, separati da uno spazio
                                        makeStats(arguments, methodCallExpr.getName().asString(), resolvedMethod.getReturnType().describe());
                                        addTested_method(resolvedMethod.getReturnType().describe(), resolvedMethod.getSignature(), methodCallExpr.toString(), argument.calculateResolvedType().describe(), chain_types);
                                    }

                                    }
                                   catch (Exception e)
                                   {
                                       System.out.println(e+"\n");
                                   }
                                }



                                leftChild = getLeft(leftChild);//nell'albero costruito da javaparser, il figlio sinistro della method call expression è ancora una method call expression se siamo di fronte ad una catena.
                            }  while( getLeft(leftChild) instanceof  MethodCallExpr ); //Il ciclo viene fermato quando il figlio sinistro non è più una chiamata a metodo, se avviene una sola iterazione, non è una catena.
                        }
                    }
                }
            });
        }
        tested_methods = tested_methods.stream().distinct().collect(Collectors.toList()); //vengono eliminati i doppioni
        return testing_classes.stream().distinct().collect(Collectors.toList());
    }


    Node getLeft(Node x)
    {


        if(x.getChildNodes().size() >0)
            return x.getChildNodes().get(0);
        return null;
    }


    int getMethodIndex(String name) //Se restituisce meno uno, il metodo te
    {
        return IntStream.range(0,tested_methods.size())
                .filter(i->(tested_methods.get(i).getName().equals(name))).
                        findFirst().
                        orElse(-1);
    }


    public void print_tested_methods()
    {
        for(TestedMethod testedMethod : tested_methods)
        {
            testedMethod.print();
        }
    }


   public void print()
   {
       testing_classes = getTestingClasses();//lect(Collectors.toList());

       if(!testing_classes.isEmpty()) {
        //   System.out.println("Sto stampando...");
           System.out.println(getName(testedClass));
           for (CompilationUnit testing_class : testing_classes)
               System.out.println("\t"+getName(testing_class));
           print_tested_methods();
       }

   }


public void makeStats(NodeList<Expression> arguments, String assertType,String returnType)
{

        List<String> types = new ArrayList<String>();
        List<String> methods = new ArrayList<String>();
        for (Expression argument : arguments) //
        {
            if (argument instanceof MethodCallExpr) {
                Node leftChild = argument;
                do {
                    List<MethodCallExpr>  argumentCalls = leftChild.findAll(MethodCallExpr.class);
                    for(MethodCallExpr argumentCall : argumentCalls)  {
                                types.add(0, argumentCall.resolve().getReturnType().describe());
                                methods.add(0, argumentCall.getNameAsString());

                            }


                    leftChild = getLeft(leftChild);
                }
                while (getLeft(leftChild) instanceof MethodCallExpr);

            }
        }
        addSelectiveStatistic(types, methods, assertType, returnType);

}


public String create_chain_types(NodeList<Expression> arguments) //Tipi della catena
{

       final String[] chain_types = {new String()};
    for(Expression argument : arguments) {
        if(argument instanceof  MethodCallExpr ) {
            Node leftChild = argument;

            do{
                List<MethodCallExpr>  argumentCalls = leftChild.findAll(MethodCallExpr.class);
                for(MethodCallExpr argumentCall : argumentCalls) {
                    chain_types[0] = "      " + argumentCall.resolve().getReturnType().describe() + chain_types[0];
                }


                if(! (getLeft(leftChild) instanceof MethodCallExpr))
                    chain_types[0] = "";
                else
                    leftChild = getLeft(leftChild);


            }
            while (getLeft(leftChild) instanceof  MethodCallExpr ) ;

        }
    }
    return chain_types[0];
}


        public void addSelectiveStatistic(List<String> types,List<String> methods,String assertType,String returntype)
        {
            if(returntype.equals("java.lang.String"))
               addStatistic(types,methods,assertType,"String");
            if(returntype.contains("java.util.Set") || returntype.contains("java.util.List"))
                addStatistic(types,methods,assertType,"Collection");
            if(returntype.equals("boolean"))
                addStatistic(types,methods,assertType,"boolean");
            if(returntype.equals("int") || returntype.equals("java.lang.Integer"))
                addStatistic(types,methods,assertType,"int");
        }


        public void addStatistic(List<String> types,List<String> methods,String assertType,String returntype) {

                int statisticIndex = getStatisticIndex(returntype);
                if (statisticIndex == -1)
                {

                    Statistic statistic = new Statistic(returntype);
                    statistic.addChain(types, methods, assertType);
                    statistics.add(statistic);

                }
                else
                {
                    statistics.get(statisticIndex).addChain(types, methods, assertType);

                }
            }


int getStatisticIndex(String returntype)
{
        return IntStream.range(0, statistics.size())
                .filter(i ->((statistics.get(i).getReturn_type().equals(returntype))))
                .findFirst().orElse(-1);
}


List<Statistic> getStatistics()
{
    return statistics;
}


}