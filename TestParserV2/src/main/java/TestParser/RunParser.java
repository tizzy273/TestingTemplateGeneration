package TestParser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.resolution.MethodUsage;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedTypeDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedTypeParameterDeclaration;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.SourceFileInfoExtractor;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.model.resolution.SymbolReference;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.github.javaparser.utils.SourceRoot;
import org.apache.commons.io.FilenameUtils;
import  com.github.javaparser.ast.Modifier;


import java.io.File;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static TestParser.TestedClass.statistics;


//Ho utilizzato SourceRoot per analizzare intere cartelle anzichè un solo file e avere quindi una lista di CompilationUnit
public class RunParser {

    public static List<CompilationUnit> mainCompilations;
    public static List<CompilationUnit> testCompilations;
    public static List<String> methodsDeclarations = new ArrayList<String>();
    public static void main(String[] args) throws Exception {

        List<String> mainPaths = new ArrayList<String>();
        List<String> testPaths = new ArrayList<String>();


        mainPaths.add("C:/Users/Tizio/Desktop/Uni/Triennale/Tesi/Progetti Maven/repodriller-master/src/main/java");
        testPaths.add("C:/Users/Tizio/Desktop/Uni/Triennale/Tesi/Progetti Maven/repodriller-master/src/test/java");

        mainPaths.add("C:/Users/Tizio/Desktop/Uni/Triennale/Tesi/Progetti Maven/commons-codec-master/src/main/java");
        testPaths.add("C:/Users/Tizio/Desktop/Uni/Triennale/Tesi/Progetti Maven/commons-codec-master/src/test/java");



        for (int i = 0; i < mainPaths.size() ; i++) {


            File main = new File(mainPaths.get(i));
            File test = new File(testPaths.get(i));

            CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();

            Path m2 = Paths.get("C:/Users/Tizio/.m2");

            Stream<Path> jarFiles = Files.find(m2, Integer.MAX_VALUE, ((path, basicFileAttributes) ->
                    FilenameUtils.getExtension(path.getFileName().toString()).equals("jar")));

            List<Path> jars = jarFiles.collect(Collectors.toList());

            for (Path jarfile : jars) {
                JarTypeSolver jarTypeSolver = new JarTypeSolver(jarfile);
                combinedTypeSolver.add(jarTypeSolver);
            }

            TypeSolver reflectionTypeSolver = new ReflectionTypeSolver();


            TypeSolver javaParserTypeSolverMain = new JavaParserTypeSolver(main);
            TypeSolver javaParserTypeSolverTest = new JavaParserTypeSolver(test);

            combinedTypeSolver.add(reflectionTypeSolver);
            combinedTypeSolver.add(javaParserTypeSolverMain);
            combinedTypeSolver.add(javaParserTypeSolverTest);


            JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);

            ParserConfiguration parserConfiguration = new ParserConfiguration();
            parserConfiguration.setSymbolResolver(symbolSolver);


            Path pathToMain = Paths.get(mainPaths.get(i));
            Path pathToTest = Paths.get(testPaths.get(i)); // Per la maggior parte dei progetti va bene questo path, altrimenti va cambiato

            SourceRoot testDirectory = new SourceRoot(pathToTest);
            SourceRoot mainDirectory = new SourceRoot(pathToMain);

            testDirectory.setParserConfiguration(parserConfiguration);
            mainDirectory.setParserConfiguration(parserConfiguration);

            mainDirectory.tryToParseParallelized();
            testDirectory.tryToParseParallelized();


            //Liste di compilation unit, classi progetto e classi di test
            testCompilations = testDirectory.getCompilationUnits();
            mainCompilations = mainDirectory.getCompilationUnits();


            //Lista delle classi testate, verrà riempita nel ciclo for
            List<TestedClass> testedClasses = new ArrayList<TestedClass>();

            // TestRecognizer TestVisitor = new TestRecognizer();

            MethodVisitor methodVisitor = new MethodVisitor();


            //testTemplateGenerator.();

            //ArrayList<TestedClass> testedClasses= new ArrayList<>();


            for (CompilationUnit maincompilation : mainCompilations) {
                // System.out.println("Sto inizializzando...");
                methodVisitor.visit(maincompilation, null);
                methodsDeclarations.addAll(methodVisitor.getDeclarations());

                TestedClass testedClass = new TestedClass(maincompilation); // Inizializzo testedClass  con parametro classe main iterata nel ciclo for
                testedClasses.add(testedClass); //


            }

            methodsDeclarations = methodsDeclarations.stream().distinct().collect(Collectors.toList());


            // testedClasses.removeIf(list->list.getTestingClasses().isEmpty());

            System.out.println("Classe testata\tTest\tSignature Metodo\tTipoAssert\t#Volte Metodo testato\tTipo Parametro Assert");  // Stampa voci categorizzazione excel

            //Stampa nomi classi e statistiche classi

            for (TestedClass testedClass : testedClasses) {
                testedClass.print();
                // testedClass.visit_tested_methods();

            }

            for (Statistic statistic : statistics) {
                statistic.printStatistic();
            }
            methodsDeclarations.clear();
            mainCompilations.clear();
            testCompilations.clear();

        }
        //statistics.get(0);



    }
}