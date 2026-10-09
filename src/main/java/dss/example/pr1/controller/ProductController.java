package dss.example.pr1.controller;

import dss.example.pr1.model.Product;
import dss.example.pr1.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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

    // Ver todos los productos (público)
    @GetMapping({"", "/"})
    public String listProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "products";
    }

    // El formulario de alta está en el panel de administración
    @GetMapping("/add")
    public String addProductForm() {
        return "redirect:/admin";
    }

    // Añadir producto (solo ADMIN). La imagen es opcional
    @PostMapping("/add")
    public String addProduct(@RequestParam String name,
                             @RequestParam double price,
                             @RequestParam(value = "imageFile", required = false) MultipartFile imageFile)
            throws IOException {
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setImageUrl(guardarImagen(imageFile));
        productService.saveProduct(product);
        return "redirect:/admin";
    }

    // Formulario de edición (solo ADMIN)
    @GetMapping("/edit/{id}")
    public String editProductForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return "redirect:/admin";
        }
        model.addAttribute("product", product);
        return "product_form";
    }

    // Guardar edición (solo ADMIN). Si no se sube imagen, se conserva la anterior
    @PostMapping("/edit/{id}")
    public String editProduct(@PathVariable Long id,
                              @RequestParam String name,
                              @RequestParam double price,
                              @RequestParam(value = "imageFile", required = false) MultipartFile imageFile)
            throws IOException {
        Product product = productService.getProductById(id);
        if (product != null) {
            product.setName(name);
            product.setPrice(price);
            String ruta = guardarImagen(imageFile);
            if (ruta != null) {
                product.setImageUrl(ruta);
            }
            productService.saveProduct(product);
        }
        return "redirect:/admin";
    }

    // Eliminar producto (solo ADMIN)
    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/admin";
    }

    // Guarda la imagen en la carpeta uploads y devuelve su ruta web (o null si no hay imagen válida)
    private String guardarImagen(MultipartFile archivo) throws IOException {

        if (archivo == null || archivo.isEmpty()) {
            return null;
        }

        String nombreOriginal = archivo.getOriginalFilename();

        if (nombreOriginal == null || !nombreOriginal.contains(".")) {
            return null;
        }

        String extension = nombreOriginal.substring(nombreOriginal.lastIndexOf('.')).toLowerCase();

        if (!EXTENSIONES_PERMITIDAS.contains(extension)) {
            return null;
        }

        Files.createDirectories(CARPETA_UPLOADS);

        String nombreArchivo = UUID.randomUUID() + extension;
        Files.write(CARPETA_UPLOADS.resolve(nombreArchivo), archivo.getBytes());

        return "/uploads/" + nombreArchivo;
    }
}
