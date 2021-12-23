import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.github.javaparser.utils.SourceRoot;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RunGenerator {

    public static List<CompilationUnit> mainCompilations;

    public static void main(String[] args) throws Exception {

        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();

        Path m2 = Paths.get("C:/Users/Tizio/.m2");

        Stream<Path> jarFiles = Files.find(m2, Integer.MAX_VALUE, ((path, basicFileAttributes) ->
                FilenameUtils.getExtension(path.getFileName().toString()).equals("jar")));

        List<Path> jars = jarFiles.collect(Collectors.toList());

        for (Path jarfile : jars) {
            //System.out.println(jarfile.toString());
            JarTypeSolver jarTypeSolver = new JarTypeSolver(jarfile);
            combinedTypeSolver.add(jarTypeSolver);
        }

        TypeSolver reflectionTypeSolver = new ReflectionTypeSolver();

        String mainPath = "C:/Users/Tizio/Desktop/Uni/Triennale/Tesi/Progetti Maven/repodriller-master/src/main/java";

        File main = new File(mainPath);

        TypeSolver javaParserTypeSolverMain = new JavaParserTypeSolver(main);


        combinedTypeSolver.add(reflectionTypeSolver);
        combinedTypeSolver.add(javaParserTypeSolverMain);



        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);

        ParserConfiguration parserConfiguration = new ParserConfiguration();
        parserConfiguration.setSymbolResolver(symbolSolver);


        Path pathToMain = Paths.get(mainPath);



        SourceRoot mainDirectory = new SourceRoot(pathToMain);


        mainDirectory.setParserConfiguration(parserConfiguration);

        mainDirectory.tryToParseParallelized();


        mainCompilations = mainDirectory.getCompilationUnits();


        //Lista delle classi testate, verrà riempita nel ciclo for


        // TestRecognizer TestVisitor = new TestRecognizer();


        TestTemplateGenerator testTemplateGenerator = new TestTemplateGenerator();

        for (CompilationUnit maincompilation : mainCompilations) {
            testTemplateGenerator.generateTestClass(maincompilation);
        }
    }

}
