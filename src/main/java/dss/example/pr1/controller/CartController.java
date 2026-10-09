package dss.example.pr1.controller;

import dss.example.pr1.model.Product;
import dss.example.pr1.service.CartService;
import dss.example.pr1.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final ProductService productService;

    public CartController(CartService cartService, ProductService productService) {
        this.cartService = cartService;
        this.productService = productService;
    }

    // Ver el carrito (quita los productos eliminados del catálogo y avisa al usuario)
    @GetMapping({"", "/"})
    public String viewCart(Model model) {
        List<Product> removed = cartService.removeIf(
                product -> productService.getProductById(product.getId()) == null);

        List<String> removedNames = new ArrayList<>();
        for (Product product : removed) {
            if (!removedNames.contains(product.getName())) {
                removedNames.add(product.getName());
            }
        }

        model.addAttribute("removedNames", removedNames);
        model.addAttribute("cartItems", cartService.getItems());
        return "cart";
    }

    @PostMapping("/add/{id}")
    public String addToCart(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        if (product != null) {
            cartService.addItem(product);
        }
        return "redirect:/cart";
    }

    @PostMapping("/remove/{id}")
    public String removeFromCart(@PathVariable Long id) {
        cartService.removeItem(id);
        return "redirect:/cart";
    }
}
