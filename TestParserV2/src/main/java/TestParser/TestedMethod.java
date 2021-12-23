package TestParser;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.utils.SourceRoot;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TestedMethod { //Metodo testato, può appartenere a diverse categorie, identificato da nome e tipo di ritorno
    List<Category> categories;

   private String return_type;
   private String name;
   public TestedMethod(String return_type, String name) {
       this.return_type = return_type;
       this.name = name;
       categories = new ArrayList<Category>();
   }

    public String getName() {
        return name;
    }
    public String getReturn_type(){return return_type;}

    public void addCategory(String assertType, String secondParameterType,String chain_types) //Viene chiamato in testedclass
    {
        int categoryIndex = getCategoryIndex(assertType,secondParameterType);

        if(categoryIndex== -1) //Se già esiste la categoria, incrementiamo il contatore di tale categoria, altrimenti la creiamo.
        {
            categories.add(new Category(assertType,secondParameterType,chain_types));
        }
        else
            categories.get(categoryIndex).incrementTimes_present();
    }


   public int getCategoryIndex(String assertType,String secondParameterType)
    {
        return IntStream.range(0, categories.size())
                .filter(i ->((categories.get(i).getAssertType().equals(assertType)) && (categories.get(i).getSecondParameterType().equals(secondParameterType))))
                .findFirst().orElse(-1);
    }

    public void print()
    {
        System.out.println("\t\t"+return_type+ " " + name);
        for(Category category : categories)
            category.print();

    }








}

