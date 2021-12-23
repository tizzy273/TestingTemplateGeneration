import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.sun.org.apache.xpath.internal.operations.Mod;

import java.util.ArrayList;
import java.util.List;


public class MethodVisitor extends VoidVisitorAdapter<Void> {

    List<MethodDeclaration> declarationsCollector;
    List<ResolvedMethodDeclaration> resolvedMethods;

    public MethodVisitor()
    {
        declarationsCollector = new ArrayList<MethodDeclaration>();
        resolvedMethods = new ArrayList<ResolvedMethodDeclaration>();

    }


    public  List<MethodDeclaration> getDeclarations()
    {
        return declarationsCollector;
    }

    public List<ResolvedMethodDeclaration> getResolvedMethods()
    {
        return resolvedMethods;
    }

    public List<MethodDeclaration> collector;

    @Override
    public void visit(MethodDeclaration methodDeclaration, Void arg)
    {
        super.visit(methodDeclaration,arg);

        List<AnnotationExpr> annotations = methodDeclaration.getAnnotations();
        List<Modifier> modifiers = methodDeclaration.getModifiers();
        modifiers = methodDeclaration.getModifiers();
        List<String> annotationsToString = new ArrayList<String>();
        List<String> modifiersToString = new ArrayList<String>();

        for(AnnotationExpr annotationExpr : annotations)
            annotationsToString.add(annotationExpr.toString());

        for(Modifier modifier : modifiers) {
            modifiersToString.add(modifier.toString());
            System.out.println(modifier.toString());
        }





        if(!annotationsToString.contains("@Override") && !modifiersToString.contains("private ")) //Escludiamo gli overriding
        {

            declarationsCollector.add(methodDeclaration);
            resolvedMethods.add(methodDeclaration.resolve());
        }

    }
}