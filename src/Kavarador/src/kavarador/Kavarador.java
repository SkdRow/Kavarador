/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kavarador;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import model.Token;

import static model.TokenType.*;

/**
 *
 * @author guillherme.tonetti
 */
public class Kavarador {
    private static boolean teveErro = false;

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Este modo aceita somente um script.");
        } else if (args.length == 1) {
            executarScript(args[0]);
        } else {
            executarTerminal();
        }
    }
    
    /**
     * Responsável por ler o script e delegar o tratamento do conteúdo para a
     * função run.
     * 
     * @param path Caminho (relativo) onde o script está localizado.
     */
    private static void executarScript(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        executar(new String(bytes, Charset.defaultCharset()));
    }
    
    /**
     * Esta função será utilizada quando deseja-se escrever códigos diretamente
     * do console linha a linha.
     * 
     * Útil para quando nenhum script for passado por parâmetro na inicialização
     * do projeto.
     */
    private static void executarTerminal() throws IOException {
        BufferedReader leitor = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
        System.out.println("Hello! This is the Kavarador's built-in prompt command."
                         + "\nStart typing or press Enter with a blank line to exit.");
        
        while (true) {
            System.out.println("> ");
            String linha = leitor.readLine();
            // Quando o usuário pressionar CTRL + D ou uma linha em branco, será
            // atribuída como null.
            if (linha == null || linha.isEmpty()) System.exit(0);
            
            executar(linha);
            
        //> Reseta o hadError, pois não houve erro nesta linha.
            teveErro = false;
        }
    }
    
    private static void executar(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.lerTokens();
        
        Parser parser = new Parser(tokens);
        Expressao expressao = parser.parse();
        
        if (teveErro) return;
        
        System.out.println(new AstPrinter().print(expressao));
    }
    
    /**
     * Esta função formata uma mensagem para exibir no console caso algum comando
     * esteja incorreto no script.
     * 
     * @param linha A linha onde o erro está localizado.
     * @param onde Qual o token, lexema, identificador, etc, que ocasionou o erro.
     * @param mensagem Mensagem amigável a ser exibida para o usuário
     */
    public static void reportarErro(int linha, String onde, String mensagem) {
        System.err.println("[line " + linha + "] Error " + onde + ": " + mensagem);
        
        teveErro = true;
    }
    
    public static void reportarErro(Token token, String mensagem) {
        if (token.getTipoToken() == EOF) {
            reportarErro(token.getLinha(), " no fim", mensagem);
        } else {
            reportarErro(token.getLinha(), " no " + token.getLexeme() + "'", mensagem);
        }
    }
}
