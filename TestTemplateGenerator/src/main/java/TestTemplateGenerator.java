import com.github.javaparser.JavaParser;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedTypeParameterDeclaration;

import java.io.FileWriter;
import java.io.IOException;
import java.io.File;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Locale;

public class TestTemplateGenerator {

    CompilationUnit cu; //Compilation Unit della classe da testare
    String className; //nome classe da testare
    String param;//istanza;
    ClassNamePrinter classNamePrinter;
    MethodVisitor methodVisitor;
    ConstructorVisitor constructorVisitor;
    ClassOrInterfaceDeclaration testClass;
    String templatesPath;
    String expected_string;
    Object expected_value;
    int expected_int;
    public TestTemplateGenerator()
    {
        classNamePrinter = new ClassNamePrinter();
        expected_string = "\"expected_value\"";
        expected_value = null;
    }

    public void generateTestClass(CompilationUnit mainCU)
    {
        testClass = new ClassOrInterfaceDeclaration();

        methodVisitor = new MethodVisitor();
        constructorVisitor = new ConstructorVisitor();
        cu = new CompilationUnit();

        classNamePrinter.visit(mainCU,null);
        constructorVisitor.visit(mainCU,null);

        className = classNamePrinter.getName();
        param =  className.toLowerCase(Locale.ROOT);

        templatesPath =  System.getProperty("user.home") + "/Desktop/TestTemplateGenerated";



        cu.addImport("org.junit.Assert"); //Setting Imports
        cu.addImport("org.junit.Test");


        testClass = cu.addClass( className+ "Test").setPublic(true); //Setting TestClassName



        methodVisitor.visit(mainCU,null);
        List<MethodDeclaration> methodDeclarations =  methodVisitor.getDeclarations();


        String returnType;
        String methodName;



       // String param = className.toLowerCase(Locale.ROOT); //istanza;


        List<Parameter> constructorParameters = constructorVisitor.getParameters();


        Expression expression1 = StaticJavaParser.parseExpression(param + " = new " + className + getConstructorParamters(constructorParameters));


        testClass.addField(className, param);
         testClass.addConstructor(Modifier.Keyword.PUBLIC).setBody(
                 new BlockStmt().addStatement(expression1)
         );




        for(MethodDeclaration declaration : methodDeclarations)
        {
            ResolvedMethodDeclaration  resolvedMethod= declaration.resolve();

            returnType = resolvedMethod.getReturnType().describe();
            methodName = resolvedMethod.getName();




            List<Parameter> parameters =  declaration.getParameters();


            makeTest(methodName,parameters,returnType);
        }
      ;
        File testTemplate = new File(templatesPath);

        if(!testTemplate.exists())
            testTemplate.mkdirs();

        try {
            FileWriter testWriter = new FileWriter(templatesPath +"/" + className + "Test.java");


            testWriter.write(cu.toString());
            testWriter.close();
        }
        catch (IOException E)
        {
            System.out.println(E);
        }


      //  Expression statement2 = StaticJavaParser.parseExpression(returntype);

        System.out.println(cu.toString());
    }

    void makeTest(String methodName,List<Parameter> parameters, String returnType) {

        String params = getMethodParameters(parameters);

        if (returnType.equals("boolean")) {


            Expression expression1 = StaticJavaParser.parseExpression("Assert.assertTrue(" + param + "." + methodName + params + ")");

            testClass.addMethod(methodName + "Test1", Modifier.Keyword.PUBLIC).setBody(
                    new BlockStmt().addStatement(expression1)).addAnnotation("Test").setLineComment("Probabilità test: 50%");


            Expression expression2 = StaticJavaParser.parseExpression("Assert.assertFalse(" + param + "." + methodName + params + ")");

            testClass.addMethod(methodName + "Test2", Modifier.Keyword.PUBLIC).setBody(
                    new BlockStmt().addStatement(expression2)).addAnnotation("Test").setLineComment("Probabilità test: 50%");
        }
       if (returnType.equals("java.lang.String")) {
            Expression expression1 = StaticJavaParser.parseExpression("Assert.assertEquals(" + param + "." + methodName + params + "," + expected_value + ")");
            testClass.addMethod(methodName + "Test1", Modifier.Keyword.PUBLIC).setBody(
                    new BlockStmt().addStatement(expression1)).addAnnotation("Test").setLineComment("Probabilità test: 95%");

           Expression expression2 = StaticJavaParser.parseExpression("Assert.assertNotNull(" + param + "." + methodName + params + ")");
            testClass.addMethod(methodName + "Test2", Modifier.Keyword.PUBLIC).setBody(
                    new BlockStmt().addStatement(expression2)).addAnnotation("Test").setLineComment("Probabilità test: 0,8%");

            Expression expression3 = StaticJavaParser.parseExpression("Assert.assertNotEquals(" + param + "." + methodName + params + "," + expected_value + ")");
            testClass.addMethod(methodName + "Test3", Modifier.Keyword.PUBLIC).setBody(
                    new BlockStmt().addStatement(expression3)).addAnnotation("Test").setLineComment("Probabilità test: 1,5%");

            Expression expression4 = StaticJavaParser.parseExpression("Assert.assertNull(" + param + "." + methodName + params + ")");
            testClass.addMethod(methodName + "Test4", Modifier.Keyword.PUBLIC).setBody(
                    new BlockStmt().addStatement(expression4)).addAnnotation("Test").setLineComment("Probabilità test: 0,2%");

            Expression expression5 = StaticJavaParser.parseExpression("Assert.assertTrue(" + param + "." + methodName + params + ".startsWith("+ expected_value + "))");
            testClass.addMethod(methodName + "Test5", Modifier.Keyword.PUBLIC).setBody(
                    new BlockStmt().addStatement(expression5)).addAnnotation("Test").setLineComment("Probabilità test: 0,8%");

            Expression expression6 = StaticJavaParser.parseExpression("Assert.assertTrue(" + param + "." + methodName + params + ".contains("+ expected_value + "))");
            testClass.addMethod(methodName + "Test6", Modifier.Keyword.PUBLIC).setBody(
                    new BlockStmt().addStatement(expression6)).addAnnotation("Test").setLineComment("Probabilità test: 2%");

            Expression expression7 = StaticJavaParser.parseExpression("Assert.assertEquals(" + param + "." + methodName + params + ".toUpperCase(), " + expected_value + ")");
            testClass.addMethod(methodName + "Test7", Modifier.Keyword.PUBLIC).setBody(
                    new BlockStmt().addStatement(expression7)).addAnnotation("Test").setLineComment("Probabilità test: 0,8%");


        }

        if (returnType.contains("java.util.Set") || returnType.contains("java.util.List")) {
            Expression expression1 = StaticJavaParser.parseExpression("Assert.assertEquals(" + param + "." + methodName + params + "," + expected_value+ ")");
            testClass.addMethod(methodName + "Test1", Modifier.Keyword.PUBLIC).setBody(
                    new BlockStmt().addStatement(expression1)).addAnnotation("Test").setLineComment("Probabilità test: 70%");

            Expression expression2 = StaticJavaParser.parseExpression("Assert.assertEquals(" + param + "." + methodName + params + ".size()," + expected_value + ")");
            testClass.addMethod(methodName + "Test2", Modifier.Keyword.PUBLIC).setBody(
                    new BlockStmt().addStatement(expression2)).addAnnotation("Test").setLineComment("Probabilità test: 10%");

            Expression expression3 = StaticJavaParser.parseExpression("Assert.assertTrue(" + param + "." + methodName + params +  ".contains("+expected_value+"))");
            testClass.addMethod(methodName + "Test3", Modifier.Keyword.PUBLIC).setBody(
                    new BlockStmt().addStatement(expression3)).addAnnotation("Test").setLineComment("Probabilità test: 14%");

                if (returnType.contains("java.util.List"))
                {
                    Expression expression4 = StaticJavaParser.parseExpression("Assert.assertEquals(" + param + "." + methodName + params + ".get(" + expected_value + ")," + expected_value + ")");
                    testClass.addMethod(methodName + "Test4", Modifier.Keyword.PUBLIC).setBody(
                            new BlockStmt().addStatement(expression4)).addAnnotation("Test").setLineComment("Probabilità test: 6%");
                }
        }
        if(returnType.equals("int") || returnType.equals("java.lang.Integer")){
            Expression expression1 = StaticJavaParser.parseExpression("Assert.assertEquals(" + param + "." + methodName + params + ", " + expected_value + ")");
            testClass.addMethod(methodName + "Test1", Modifier.Keyword.PUBLIC).setBody(
                    new BlockStmt().addStatement(expression1)).addAnnotation("Test").setLineComment("Probabilità test: 94%");

            Expression expression2 = StaticJavaParser.parseExpression("Assert.assertNotEquals(" + param + "." + methodName + params + "," + expected_value+ ")");
            testClass.addMethod(methodName + "Test2", Modifier.Keyword.PUBLIC).setBody(
                    new BlockStmt().addStatement(expression2)).addAnnotation("Test").setLineComment("Probabilità test: 1%");

                if(returnType.equals("java.lang.Integer"))
                {
                    Expression expression3 = StaticJavaParser.parseExpression("Assert.assertEquals(" + param + "." + methodName + params + ".LongValue(), "+ 0 + ")");
                    testClass.addMethod(methodName + "Test3", Modifier.Keyword.PUBLIC).setBody(
                            new BlockStmt().addStatement(expression3)).addAnnotation("Test").setLineComment("Probabilità test: 4%");
                    Expression expression4 = StaticJavaParser.parseExpression("Assert.assertEquals(" + param + "." + methodName + params + ".toString(), "+ expected_value + ")");
                    testClass.addMethod(methodName + "Test4", Modifier.Keyword.PUBLIC).setBody(
                            new BlockStmt().addStatement(expression4)).addAnnotation("Test").setLineComment("Probabilità test: 1%");
                }


        }
    }


    String getMethodParameters(List<Parameter> parameters)
    {
        String inputs = "(";
        if(parameters.size()>0) {
            for (Parameter parameter : parameters) {

                if (parameter.getNameAsString().equals("int"))
                    inputs += "0,";
                else
                    inputs += "null,";
            }
            inputs = inputs.substring(0,inputs.length()-1);
        }
        inputs +=")";
        return inputs;
    }

    String getConstructorParamters(List<Parameter> parameters)
    {
        String inputs = "(";
        if(parameters.size()>0) {
            for (Parameter parameter : parameters) {
                System.out.println("Parameter" + parameter.resolve().describeType());
                if (parameter.resolve().describeType().equals("int"))
                    inputs += "0,";
                else
                    inputs += "null,";
            }
            inputs = inputs.substring(0,inputs.length()-1);
        }
        inputs +=")";
        return inputs;
    }
}
