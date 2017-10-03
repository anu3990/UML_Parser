import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.ReferenceType;

public class Get_info {

	public  ArrayList<String> getJava_Files(File input) {				//To extract Java Files. For example - A.java, B.java etc.
        ArrayList javafiles = new ArrayList();
        for (File files : input.listFiles()) {
            String file_path = files.getPath();
            if (file_path.toLowerCase().endsWith("java")) {
            	javafiles.add(file_path);
            }
        }
        return javafiles;
    }
	
	public static ArrayList<String> getJavaClasses(CompilationUnit compunit)    // To extract Java Classes
    {
        ArrayList classes = new ArrayList();
        for (TypeDeclaration td : compunit.getTypes()) {
            if(td instanceof ClassOrInterfaceDeclaration && !((ClassOrInterfaceDeclaration) td).isInterface())
            {
            	classes.add(td.getName());
            }
        }
        return classes;
    }
 
	public HashMap<String, ArrayList> getMethod_details(BodyDeclaration member) {
        HashMap<String, ArrayList> method_details = new HashMap<String, ArrayList>();
        MethodDeclaration med = (MethodDeclaration) member;
        String method_name = med.getName();
        String method_class = "";

        if (med.getParentNode() instanceof ClassOrInterfaceDeclaration) {
        	method_class = ((ClassOrInterfaceDeclaration) med.getParentNode()).getName().toString();
        }

        HashMap<String, String> class_instance = new HashMap<String, String>();
        if (med.getBody() != null) {
            for (Statement statement: med.getBody().getStmts()) {
                if (statement instanceof ExpressionStmt) {
                    Expression expression = ((ExpressionStmt) statement).getExpression();
                    if (expression instanceof VariableDeclarationExpr) {
                        if (((VariableDeclarationExpr) expression).getType() instanceof ReferenceType) {
                            if (((ReferenceType) ((VariableDeclarationExpr) expression).getType()).getType() instanceof ClassOrInterfaceType) {
                                String class_name = ((ReferenceType) ((VariableDeclarationExpr) expression).getType()).getType().toString();
                                String instance_name = ((VariableDeclarationExpr) expression).getVars().get(0).getId().toString();
                                class_instance.put(instance_name, class_name);
                            }
                        }
                    } else if (expression instanceof MethodCallExpr) {
                        String class_scope;
                        try {
                        	class_scope = ((MethodCallExpr) expression).getScope().toString();
                        } catch (NullPointerException e) {
                        	class_scope = "";
                        }

                        
                        
                        String argument = "";

                        if (class_instance.containsKey(class_scope)) {
                            String name = class_instance.get(class_scope);
                            String name_2 = ((MethodCallExpr) expression).getName().toString();

                            for (Expression exp: ((MethodCallExpr) expression).getArgs()) {

                                String argument_2 = exp.toString();

                                if (argument!=null && argument.length() > 0) {
                                	argument += ", " + argument_2;
                                } else {
                                	argument += argument_2;
                                }

                            }

                            if (method_details!=null && method_details.containsKey(method_class + "-" + method_name)) {
                                ArrayList a = method_details.get(method_class + "-" + method_name);
                                a.add(name + "-" + name_2 + "-" + argument);
                                method_details.put(method_class + "-" + method_name, a);
                            } else {
                                ArrayList b = new ArrayList();
                                b.add(name + "-" + name_2 + "-" + argument);
                                method_details.put(method_class + "-" + method_name, b);
                            }

                        } else if (class_scope!=null && class_scope.length() == 0) {
                            String str = ((MethodCallExpr) expression).getName().toString();
                            if (method_details.containsKey(method_class + "-" + method_name)) {
                                ArrayList c = method_details.get(method_class + "-" + method_name);
                                c.add(method_class + "-" + str + "-" + argument);
                                method_details.put(method_class + "-" + method_name, c);
                            } else {
                                ArrayList curVal = new ArrayList();
                                curVal.add(method_class + "-" + str + "-" + argument);
                                method_details.put(method_class + "-" + method_name, curVal);
                            }
                        }
                    }
                }


            }
        }


        return method_details;

    }
}
