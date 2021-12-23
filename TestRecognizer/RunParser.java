package TestParser;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.utils.SourceRoot;


import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
//Ho utilizzato SourceRoot per analizzare intere cartelle anzichè un solo file e avere quindi una lista di CompilationUnit
public class RunParser {
    public static void main(String[] args) throws Exception{

        String testsPath = "C:/Users/Tizio/Desktop/Uni/Triennale/Tesi/Progetti Maven/slf4j-master/slf4j-simple/src/test";

        Path tests = Paths.get(testsPath);
        SourceRoot testDirectory = new SourceRoot(tests);

        testDirectory.tryToParse();
        List<CompilationUnit> compilations = testDirectory.getCompilationUnits();

        TestRecognizer testVisitor = new TestRecognizer();
        ClassNamePrinter classNameVisitor = new ClassNamePrinter();

        for(CompilationUnit cu : compilations) { // per ogni classe di test stampiamo il nome della classe e la versione di test utilizzata
            classNameVisitor.visit(cu,null);
            testVisitor.visit(cu, null);
            testVisitor.printTestVersion();
        }

    }
}
