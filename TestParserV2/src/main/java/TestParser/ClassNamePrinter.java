package TestParser;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class ClassNamePrinter extends VoidVisitorAdapter<Void> {

    private String name;
    @Override
    public void visit(ClassOrInterfaceDeclaration cid, Void arg) {
        super.visit(cid, arg);
        name = cid.getNameAsString();
      //  System.out.println("\n\n\n Class name printed: " + cid.getName());
    }
    String getName()
    {
        return name;
    }
}
