package dss.example.pr1.controller;

import dss.example.pr1.service.DatabaseExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private DatabaseExportService exportService;

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportSql() {
        byte[] data = exportService.exportDatabaseToSql();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=products.sql")
                .contentType(MediaType.TEXT_PLAIN)
                .body(data);
    }
}