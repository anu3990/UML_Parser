import java.io.BufferedReader;
import java.io.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.sun.deploy.util.StringUtils;

import net.sourceforge.plantuml.SourceStringReader;

public class Seq {
	public  void execute(File file, String output) throws Exception{
		Get_info get_info = new Get_info();
		
		 ArrayList<String> javaFiles = get_info.getJava_Files(file);
		 
		 StringBuilder input = new StringBuilder();
		 
		 for (String jfiles : javaFiles) {
	            FileInputStream file_input = new FileInputStream(jfiles);
	            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file_input, "UTF-8"));
	            String lines = bufferedReader.readLine();
	            while(lines != null){		//Read all the lines of the java files
	                input.append(lines);
	                input.append('\n');		//Append new line
	                lines = bufferedReader.readLine();
	            }
	        }
		 String iStream = input.toString();
		 
		 iStream = iStream.replace("package", "// package");     //Comment out the package and import statements.
		 iStream = iStream.replace("import", "// import");
		 
		 InputStream in = new ByteArrayInputStream(iStream.getBytes(StandardCharsets.UTF_8));
			CompilationUnit compunit ; 
			
			try{
			compunit = JavaParser.parse(in); //compunit contains the non-commented part of the code
			}
			finally{ 
				in.close();
			}
		 
			ArrayList java_classes = get_info.getJavaClasses(compunit);
			
			ArrayList<HashMap<String, ArrayList>> method_details = new ArrayList<HashMap<String, ArrayList>>();
	        for (TypeDeclaration td : compunit.getTypes()) {
	            String class_name = td.getName();
	            if(java_classes.contains(class_name))
	            {
	                List<BodyDeclaration> member_list = td.getMembers();
	                if (member_list != null && member_list.size() > 0) {
	                    for (BodyDeclaration participant : member_list) {
	                        if (participant instanceof MethodDeclaration) {
	                            HashMap<String, ArrayList> methods = get_info.getMethod_details(participant);
	                            if (!methods.isEmpty()) {
	                            	method_details.add(methods);
	                            }
	                        }
	                    }


	                }
	            }
	        }
	        
	        ArrayList<String> local = new ArrayList<String>();
	        
	        String main_grammar = "@startuml\n";     // initialize the grammer to be passed to plantuml
	        boolean value = true;
	        
	        for (HashMap<String, ArrayList> new1: method_details) {
	            for (Map.Entry<String, ArrayList> k: new1.entrySet()) {
	                String str_class = k.getKey().split("-")[0];
	                String str_method = k.getKey().split("-")[1];

	                for (Object obj: k.getValue()) {
	                    String obj_info = (String) obj;
	                    String obj_class = obj_info.split("-")[0];
	                    String obj_method = obj_info.split("-")[1];
	                    String obj_args = obj_info.split("-")[1];

	                    String str1 = str_class + " -->  " + obj_class + "\n";
	                    if (!local.contains(str1) && !str_method.equals("main")) {
	                    	local.add(str1);
	                    }
	                    if (str_method.equals("main") && value) {
	                        value = false;
	                        local.add(0, "Activate " + obj_class);
	                        local.add(0, str_class + " -> " + obj_class + ": " + obj_method + "(" + obj_args + ")");
	                        if (!local.contains(str1)) {
	                        	local.add(0, str1);
	                        }
	                        local.add("Deactivate " + obj_class);
	                    } else {

	                    	local.add(str_class + " -> " + obj_class + ": " + obj_method + "(" + obj_args + ")");
	                    	local.add("Activate " + obj_class);
	                    	local.add(obj_class + " -> " + str_class);
	                    	local.add("Deactivate " + obj_class);
	                    }

	                }
	            }
	        }
	        
	        
	        
	        for (String str_grammar: local) {
	        	main_grammar += str_grammar + "\n";
	        }
	        
	        main_grammar += "@enduml";
	        
	        OutputStream png = null;
	        try {
	            png = new FileOutputStream(output);
	            SourceStringReader reader = new SourceStringReader(main_grammar);
	            String desc = reader.generateImage(png);
	        }
	        catch (FileNotFoundException e) {
	            System.out.println("File Not Found");
	        }
	        catch (IOException e) {
	            System.out.println("IO Exception");
	        }

	        
	}
	
}
