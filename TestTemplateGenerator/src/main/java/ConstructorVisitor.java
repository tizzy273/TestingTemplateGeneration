
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.List;

public class ConstructorVisitor extends VoidVisitorAdapter<Void> {

    String name;
    List<Parameter> parameters = new ArrayList<Parameter>();

    @Override
    public void visit(ConstructorDeclaration cid, Void arg) {
        super.visit(cid, arg);
        parameters =  cid.getParameters();
        name = cid.getNameAsString();
        //  System.out.println("\n\n\n Class name printed: " + cid.getName());
    }

    String getName() {
        return name;
    }

    List<Parameter> getParameters()
    {
        return parameters;
    }
}