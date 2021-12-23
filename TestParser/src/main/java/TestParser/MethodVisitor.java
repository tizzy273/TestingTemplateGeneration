package TestParser;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.MethodUsage;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedMethodLikeDeclaration;
import com.github.javaparser.resolution.types.ResolvedType;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static TestParser.RunParser.mainCompilations;
import static TestParser.RunParser.methodsDeclarations;

public class MethodVisitor extends VoidVisitorAdapter<Void> {

    List<String> declarationsCollector;
    List<ResolvedMethodDeclaration> resolvedMethods;

    public MethodVisitor()
    {
        declarationsCollector = new ArrayList<String>();
        resolvedMethods = new ArrayList<ResolvedMethodDeclaration>();

    }


    public boolean isMainMethod(Node method)
    {

        if(method instanceof MethodCallExpr)
        {

            if(methodsDeclarations.contains(((MethodCallExpr) method).getName().asString())) {

                return true;
            }
            return false;
        }
        return false;
    }

    public boolean isMainMethod(String method) //Per chiamarlo dobbiamo essere sicuri che method sia effettivamente il nome di un metodo
    {
            if(methodsDeclarations.contains(method)) {
                return true;
            }
            // System.out.println("METODO" + method);
            return false;
    }

    public  List<String> getDeclarations()
    {
        return declarationsCollector;
    }

    public List<ResolvedMethodDeclaration> getResolvedMethods()
    {
        return resolvedMethods;
    }

    @Override
    public void visit(MethodDeclaration methodDeclaration, Void arg)
    {
        super.visit(methodDeclaration,arg);

        List<AnnotationExpr> annotations = methodDeclaration.getAnnotations();
        List<String> annotationsToString = new ArrayList<String>();

        for(AnnotationExpr annotationExpr : annotations)
            annotationsToString.add(annotationExpr.toString());

        if(!annotationsToString.contains("@Override")) //Escludiamo gli overriding
        {
            declarationsCollector.add(methodDeclaration.getNameAsString());
            resolvedMethods.add(methodDeclaration.resolve());
        }

    }
}