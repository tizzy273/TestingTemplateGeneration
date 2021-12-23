package TestParser;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import java.util.*;
import java.util.stream.IntStream;

public class Statistic { //classe che permette di avere le statistiche dei progetti analizzati, in base al tipo di ritorno del metodo. Potrei modificarlo in seguito per avere la modifica in base a tutta la signature del metodo
   private String return_type;
   private List<Chain> chains; //tutte le catene con cui viene testato un metodo con un determinato tipo di ritorno

   double dirTestedMethods; //Directly Tested Methods.
   double chainTestedMethods; //Chain Tested Methods
   double testedMethods;


   public Statistic(String return_type)
   {
       this.return_type = return_type;
       chains = new ArrayList<Chain>();
   }


   String getReturn_type(){
      return return_type;
   }


   public void addChain(List<String> types, List<String> methods, String assertType) //viene aggiunta una nuova chain, se già presente, viene incrementato il numero di volte in cui è presente
   {
      int chainIndex = getChainIndex(assertType,methods);
      if(chainIndex == -1)
         chains.add(new Chain(types,methods,assertType));
      else
         chains.get(chainIndex).increment_time_present();
   }


 public void PrintDTMethodstatistics() //vengono stampate le statistiche dei metodi testati direttamente
 {


    List<String> asserts = new ArrayList<String>(); //Lista degli assert utilizzato per testare la chiamata
    for (Chain chain : chains) {
       if (chain.getSimplifiedChainMethods().size() == 1) { //Se la chain ha lunghezza uguale ad uno, siamo di fronte ad una chiamata diretta
          int i = 0;

          while (i < chain.getTimes_present()) {
             asserts.add(chain.getAssertType());
             i++;
          }
          dirTestedMethods += chain.getTimes_present();
       }
       else
          chainTestedMethods += chain.getTimes_present();

       testedMethods = dirTestedMethods + chainTestedMethods;

    }
    Set<String> assertsset = new HashSet<String>(asserts);
    System.out.println("\n\n I metodi che restituiscono " + return_type + " vengono testati direttamente " + dirTestedMethods + " volte ( " + (dirTestedMethods/testedMethods) *100  + "% del totale dei test su " + return_type + " ) di cui :");
    for (String a : assertsset)
       System.out.println( Collections.frequency(asserts, a) + " in " + a);
 }


   /*void PrintCMethodstatistics() {// PRIMA

      ListMultimap<String,List<String>> meth_ass = ArrayListMultimap.create(); // multimap, contiene ultimo metodo catena e tipo assert
      List<String> lastmethods = new ArrayList<String>();
      List<String> asserts = new ArrayList<String>();
      for (Chain chain : chains) {
         if (chain.getSimplifiedChainMethods().size() > 1) {  //La chain è effettiva se ha lunghezza maggiore di 1
            int i = 0;

            while (i < chain.getTimes_present()) {
               List<String> simplifiedChainMethods = chain.getSimplifiedChainMethods();

               meth_ass.put(chain.getAssertType(), simplifiedChainMethods);
               i++;
            }
         }
      }
      System.out.println("\n\n I metodi che restituscono " + return_type + " vengono testati tramite una chain" + chainTestedMethods + " volte ( " + chainTestedMethods/testedMethods *100 + "% del totale dei test su " + return_type + " ) di cui :");
      for (String a : meth_ass.keySet())
      {
         System.out.println(meth_ass.get(a).size() +  " volte " + "in " + a + " dove la catena è formata dai seguenti metodi ");
         //int i = 0;
         List<List<String>> methods  = meth_ass.get(a);
         Set<List<String>> methodset = new HashSet<List<String>>(methods);
         for(List<String> s : methodset) {
             int frequency = Collections.frequency(methods, s);
             System.out.println(s + " " +  frequency + " volte \n");
         }
      }

   }*/

   void PrintCMethodstatistics() {

      ListMultimap<String,List<String>> meth_ass = ArrayListMultimap.create();
      for (Chain chain : chains) {
         if (chain.getSize() > 1) {
            int i = 0;

            while (i < chain.getTimes_present()) {
               List<String> methods = chain.getMethods();

               meth_ass.put(chain.getAssertType(), methods);
               i++;
            }
         }
      }
      System.out.println("\n\n I metodi che restituscono " + return_type + " vengono testati tramite una chain" + chainTestedMethods + " volte ( "
              + chainTestedMethods/testedMethods *100 + "% del totale dei test su " + return_type + " ) di cui :");
      for (String a : meth_ass.keySet())
      {
         System.out.println(meth_ass.get(a).size() +  " volte " + "in " + a + " dove la catena è formata dai seguenti metodi ");
         List<List<String>> methods  = meth_ass.get(a);
         Set<List<String>> methodset = new HashSet<List<String>>(methods);
         for(List<String> s : methodset) {
            int frequency = Collections.frequency(methods, s);
            System.out.println(s + " " +  frequency + " volte \n");
         }
      }

   }


   public int getChainIndex(String assertType,List<String> methods)
   {
      return IntStream.range(0, chains.size())
              .filter(i ->((chains.get(i).getAssertType().equals(assertType)) && (chains.get(i).getMethods().equals(methods))))
             // .filter(i->((chains.get(i).getMethods().equals(methods))))//
              .findFirst().orElse(-1);
   }


   void printStatistic()
   {
      PrintDTMethodstatistics();
      PrintCMethodstatistics();
   }

}



