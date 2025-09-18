package text.section_5;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnect_1 {
    public static void main(String[] args) {

        try {
            // データベースに接続
            Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost/springboot_nagoyameshi",
                "root",
                "dolphin@kick310"
            );

            System.out.println("データベース接続成功");
            System.out.println(con);

            // データベース接続を解除
            con.close();

        } catch(SQLException e) {
            System.out.println("データベース接続失敗：" + e.getMessage());
        }
    }
}