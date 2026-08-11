package conexao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexaoSQLite {

    public static Connection conectar() {
        Connection conn = null;
        try {
            // O arquivo 'banco_senai.db' vai aparecer na raiz da pasta do seu projeto
            String url = "jdbc:sqlite:banco_senai.db";
            conn = DriverManager.getConnection(url);

            // Cria uma tabela de teste chamada 'alunos' se ela não existir
            // Substituir a string SQL antiga dentro do método conectar() por esta:
            String sql = "CREATE TABLE IF NOT EXISTS chamados ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "solicitante TEXT NOT NULL,"
                    + "equipamento TEXT NOT NULL,"
                    + "categoria TEXT NOT NULL,"
                    + "descricao TEXT NOT NULL,"
                    + "status TEXT NOT NULL"
                    + ");";

            Statement stmt = conn.createStatement();
            stmt.execute(sql);

        } catch (SQLException e) {
            System.out.println("Erro ao conectar ao SQLite: " + e.getMessage());
        }
        return conn;
    }
}