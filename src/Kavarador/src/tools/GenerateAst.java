/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tools;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author guilherme.tonetti
 */
public class GenerateAst {
    public static void main(String args[]) throws IOException {
        String outputDir = System.getProperty("user.dir") + "/src/model";
        defineAst(outputDir, "Expressao", Arrays.asList(
        "Binaria   : Expressao esquerda, Token operador, Expressao direita",
        "Agrupamento : Expressao expressao",
        "Literal : Object valor",
        "Unaria : Token operador, Expressao direita"
        ));
    }
    
    private static void defineAst(
      String outputDir, String baseName, List<String> types)
      throws IOException {
        String path = outputDir + "/" + baseName + ".java";
        try (PrintWriter writer = new PrintWriter(path, "UTF-8")) {
            writer.println("package model;");
            writer.println();
            writer.println("import java.util.List;");
            writer.println();
            writer.println("abstract class " + baseName + " {");
            
            defineVisitor(writer, baseName, types);
            
            // The AST classes.
            for (String type : types) {
                String className = type.split(":")[0].trim();
                String fields = type.split(":")[1].trim();
                defineType(writer, baseName, className, fields);
            }
            
            writer.println("}");
        }
    }
    
    private static void defineType(PrintWriter writer, String baseName, String className, String fieldList) {
        writer.println("    static class " + className + " extends " + baseName + " {");

        // Constructor.
        writer.println("    " + className + "(" + fieldList + ") {");

        // Store parameters in fields.
        String[] fields = fieldList.split(", ");
        for (String field : fields) {
            String name = field.split(" ")[1];
            writer.println("      this." + name + " = " + name + ";");
        }

        writer.println("    }");
        
        // Visitor pattern.
        writer.println();
        writer.println("    @Override");
        writer.println("        <R> R accept(Visitor<R> visitor) {");
        writer.println("      return visitor.visit" +
            baseName + className + "(this);");
        writer.println("    }");

        // Fields.
        writer.println();
        for (String field : fields) {
            writer.println("    final " + field + ";");
        }

        writer.println("  }");
        writer.println();
    }

    private static void defineVisitor(PrintWriter writer, String baseName, List<String> types) {
        writer.println("    interface Visitor<R> {");
        
        for (String type : types) {
            String typeName = type.split(":")[0].trim();
            writer.println("        R visit" + baseName + typeName + "(" +
                typeName + " " + baseName.toLowerCase() + ");");
        }
        
        writer.println("    }");
        
        // The base accept() method.
        writer.println();
        writer.println("    abstract <R> R accept(Visitor<R> visitor);\n");
    }
}