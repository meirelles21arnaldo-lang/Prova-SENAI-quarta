import com.sun.net.httpserver.HttpServer;
import conexao.ConexaoSQLite;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class Main {

    public static void main(String[] args) throws IOException {
        // Força a criação do banco de dados e da tabela ao iniciar o programa
        ConexaoSQLite.conectar();

        // Cria o servidor HTTP na porta 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Define a rota de cadastro (Exemplo: http://localhost:8080/cadastrar?nome=Carlos)
        server.createContext("/cadastrar", exchange -> {
            String query = exchange.getRequestURI().getQuery();

            // Criamos um mapa simples para organizar as variáveis que chegam da internet
            java.util.Map<String, String> params = new java.util.HashMap<>();

            if (query != null) {
                // Quebra a URL no símbolo "&" (ex: solicitante=Eric&equipamento=PC05)
                for (String param : query.split("&")) {
                    String[] par = param.split("=");
                    if (par.length > 1) {
                        // Guarda a chave e decodifica o texto tirando os símbolos da internet
                        params.put(par[0], java.net.URLDecoder.decode(par[1], "UTF-8"));
                    }
                }
            }

            // Resgata os valores coletados ou define um padrão caso venha vazio
            String solicitante = params.getOrDefault("solicitante", "Anônimo");
            String equipamento = params.getOrDefault("equipamento", "Geral");
            String categoria = params.getOrDefault("categoria", "Outros");
            String descricao = params.getOrDefault("descricao", "Sem descrição");
            String statusInicial = "Aberto";

            String jsonResposta;

            // GRAVAÇÃO NO BANCO VIA JDBC
            try (Connection conn = ConexaoSQLite.conectar()) {
                String sql = "INSERT INTO chamados (solicitante, equipamento, categoria, descricao, status) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, solicitante);
                pstmt.setString(2, equipamento);
                pstmt.setString(3, categoria);
                pstmt.setString(4, descricao);
                pstmt.setString(5, statusInicial);
                pstmt.executeUpdate();

                // Resposta em JSON puro
                jsonResposta = "{\"status\":\"sucesso\",\"mensagem\":\"Chamado do " + equipamento + " registrado com sucesso!\"}";
            } catch (Exception e) {
                jsonResposta = "{\"status\":\"erro\",\"mensagem\":\"" + e.getMessage() + "\"}";
            }

            // Cabeçalhos de resposta HTTP e CORS
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");

            exchange.sendResponseHeaders(200, jsonResposta.getBytes("UTF-8").length);
            OutputStream os = exchange.getResponseBody();
            os.write(jsonResposta.getBytes("UTF-8"));
            os.close();
        });

        System.out.println("Servidor do SENAI iniciado com sucesso!");
        System.out.println("Disponivel em: http://localhost:8080");
        server.start();
    }
}