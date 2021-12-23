package TestParser;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
//Classe che riconosce la versione del test analizzando i package importati
public class TestRecognizer extends VoidVisitorAdapter<Void> {
    private String TestVersion;
    boolean junit_3,junit_4,junit_5,mockito;
    @Override
    public void visit(ImportDeclaration md, Void arg) {
        super.visit(md, arg);
        String imported_package = md.getName().asString();

        if (imported_package.contains("org.junit")) {
            if (imported_package.contains("jupiter") || imported_package.contains("platform") || imported_package.contains("vintage"))
                junit_5 = true;
            else
                junit_4 = true;
        }
        else if (imported_package.contains("junit.awtui") || imported_package.contains("junit.extensions") || imported_package.contains("junit.framework") || imported_package.contains("junit.runner") ||
                imported_package.contains("junit.samples") || imported_package.contains("junit.swingui") || imported_package.contains("junit.tests") || imported_package.contains("junit.textui"))
            junit_3 = true;
        if (imported_package.contains("mockito"))
            mockito = true;
    }

    public void printTestVersion() {
        if(junit_3) {
            TestVersion = "Junit3 ";
            junit_3 = false;
        }
        if(junit_4) {
            TestVersion += "Junit4 ";
            junit_4 = false;
        }
        if(junit_5) {
            TestVersion += "Junit5 ";
            junit_5 = false;
        }
        if(mockito) {
            TestVersion += "mockito";
            mockito = false;
        }
        System.out.println(TestVersion);
        TestVersion = "";
    }
}