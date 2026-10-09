package dss.example.pr1.service;

import dss.example.pr1.model.Product;
import dss.example.pr1.repository.ProductRepo;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class DatabaseExportService {

    private final ProductRepo productRepo;

    public DatabaseExportService(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }

    public byte[] exportDatabaseToSql() {

        StringBuilder sql = new StringBuilder();

        sql.append("CREATE TABLE IF NOT EXISTS product (")
                .append("id BIGINT AUTO_INCREMENT PRIMARY KEY, ")
                .append("name VARCHAR(255), price DOUBLE, image_url VARCHAR(1000));\n\n");

        for (Product product : productRepo.findAll()) {

            sql.append("INSERT INTO product (id, name, price, image_url) VALUES (")
                    .append(product.getId())
                    .append(", ")
                    .append(literal(product.getName()))
                    .append(", ")
                    .append(product.getPrice())
                    .append(", ")
                    .append(literal(product.getImageUrl()))
                    .append(");\n");
        }

        return sql.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String literal(String texto) {

        if (texto == null) {
            return "NULL";
        }

        // En SQL, una comilla simple dentro de un texto se representa duplicándola.
        return "'" + texto.replace("'", "''") + "'";
    }
}