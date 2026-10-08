package dss.example.pr1.controller;

import dss.example.pr1.model.Product;
import dss.example.pr1.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/products")
public class ProductController {

    private static final List<String> EXTENSIONES_PERMITIDAS =
            List.of(".jpg", ".jpeg", ".png", ".gif", ".webp");

    private static final Path CARPETA_UPLOADS = Paths.get("uploads");

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String listProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "productos";
    }

    @GetMapping("/add")
    public String newProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "formulario-producto";
    }

    @GetMapping("/edit/{id}")
    public String editProductForm(@PathVariable Long id, Model model) {

        Product product = productService.getProductById(id);

        if (product == null) {
            return "redirect:/admin";
        }

        model.addAttribute("product", product);
        return "formulario-producto";
    }

    @PostMapping("/save")
    public String saveProduct(
            @ModelAttribute Product product,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile)
            throws IOException {

        String ruta = guardarImagen(imageFile);

        if (ruta != null) {
            product.setImageUrl(ruta);
        }

        productService.saveProduct(product);

        return "redirect:/admin";
    }

    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/admin";
    }

    private String guardarImagen(MultipartFile archivo) throws IOException {

        if (archivo == null || archivo.isEmpty()) {
            return null;
        }

        String nombreOriginal = archivo.getOriginalFilename();

        if (nombreOriginal == null || !nombreOriginal.contains(".")) {
            return null;
        }

        String extension =
                nombreOriginal.substring(nombreOriginal.lastIndexOf('.')).toLowerCase();

        if (!EXTENSIONES_PERMITIDAS.contains(extension)) {
            return null;
        }

        Files.createDirectories(CARPETA_UPLOADS);

        String nombreArchivo = UUID.randomUUID() + extension;

        Path destino = CARPETA_UPLOADS.resolve(nombreArchivo);

        Files.write(destino, archivo.getBytes());

        return "/uploads/" + nombreArchivo;
    }
}