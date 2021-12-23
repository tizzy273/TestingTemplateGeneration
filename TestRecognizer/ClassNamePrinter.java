package TestParser;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class ClassNamePrinter extends VoidVisitorAdapter<Void> {

    @Override
    public void visit(ClassOrInterfaceDeclaration cid, Void arg) {
        super.visit(cid, arg);
        System.out.println("\n Class name printed: " + cid.getName());
    }
}
