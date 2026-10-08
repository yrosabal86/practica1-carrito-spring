package dss.example.pr1.controller;

import dss.example.pr1.model.Product;
import dss.example.pr1.service.DatabaseExportService;
import dss.example.pr1.service.ProductService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ProductService productService;
    private final DatabaseExportService exportService;

    public AdminController(ProductService productService, DatabaseExportService exportService) {
        this.productService = productService;
        this.exportService = exportService;
    }

    @GetMapping
    public String adminPanel(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("product", new Product());
        return "admin";
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportSql() {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=products.sql")
                .contentType(MediaType.TEXT_PLAIN)
                .body(exportService.exportDatabaseToSql());
    }
}
