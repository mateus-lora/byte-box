package br.edu.atitus.product_service.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.edu.atitus.product_service.clients.CurrencyClient;
import br.edu.atitus.product_service.clients.CurrencyResponse;
import br.edu.atitus.product_service.entities.ProductEntity;
import br.edu.atitus.product_service.repositories.ProductRepository;
import jakarta.transaction.Transactional;

@RestController
@RequestMapping("products")
public class OpenProductController {

	private final ProductRepository repository;
	private final CurrencyClient currencyClient;
	private final CacheManager cacheManager;

	public OpenProductController(ProductRepository repository, CurrencyClient currencyClient,
			CacheManager cacheManager) {
		super();
		this.repository = repository;
		this.currencyClient = currencyClient;
		this.cacheManager = cacheManager;
	}

	@Value("${server.port}")
	private int serverPort;

	@GetMapping("/{idProduct}/{targetCurrency}")
	public ResponseEntity<ProductEntity> getProduct(@PathVariable Long idProduct, @PathVariable String targetCurrency)
			throws Exception {

		targetCurrency = targetCurrency.toUpperCase();
		String nameCache = "Product";
		String keyCache = idProduct + targetCurrency;

		ProductEntity product = cacheManager.getCache(nameCache).get(keyCache, ProductEntity.class);

		if (product == null) {
			product = repository.findById(idProduct).orElseThrow(() -> new Exception("Product not found"));
			
			product.setEnvironment("Product-service running on Port: " + serverPort);

			if (product.getCurrency().equals(targetCurrency)) {
				product.setConvertedPrice(product.getPrice());
			} else {
				CurrencyResponse currency = currencyClient.getCurrency(product.getPrice(), product.getCurrency(),
						targetCurrency);
				if (currency != null) {
					product.setConvertedPrice(currency.getConvertedValue());
					product.setEnvironment(product.getEnvironment() + " - " + currency.getEnviroment());
					
					cacheManager.getCache(nameCache).put(keyCache, product);
				} else {
					product.setConvertedPrice(-1);
					product.setEnvironment(product.getEnvironment() + " - Currency unavalaible");
				}
			}
			
		} else {
			product.setEnvironment("Product-service running on Port: " + serverPort + " - Datasource: cache");
		}
		return ResponseEntity.ok(product);
	}
	
	@GetMapping
	public ResponseEntity<List<ProductEntity>> getAllProducts() {
		try {
			List<ProductEntity> products = repository.findAll();
			
			if (products.isEmpty()) {
				return ResponseEntity.noContent().build();
			}
			return ResponseEntity.ok(products);
		} catch (Exception e) {
			System.err.println("Erro ao buscar todos os produtos: " + e.getMessage());
			return ResponseEntity.internalServerError().build();
		}
	}

	@PostMapping
	public ResponseEntity<ProductEntity> createProduct(@RequestBody ProductEntity product) {
		try {
			ProductEntity savedProduct = repository.save(product);
			return ResponseEntity.status(201).body(savedProduct);
		} catch (Exception e) {
			System.err.println("Erro ao criar produto: " + e.getMessage());
			return ResponseEntity.internalServerError().build();
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<ProductEntity> updateProduct(@PathVariable Long id, @RequestBody ProductEntity product) {
		try {
			if (!repository.existsById(id)) {
				return ResponseEntity.notFound().build();
			}

			product.setId(id);

			ProductEntity updatedProduct = repository.save(product);
			return ResponseEntity.ok(updatedProduct);
		} catch (Exception e) {
			System.err.println("Erro ao atualizar produto: " + e.getMessage());
			return ResponseEntity.internalServerError().build();
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
		try {
			if (!repository.existsById(id)) {
				return ResponseEntity.notFound().build();
			}

			repository.deleteById(id);
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			System.err.println("Erro ao deletar produto: " + e.getMessage());
			return ResponseEntity.internalServerError().build();
		}
	}
	
    @GetMapping("/search/{theme}")
    public ResponseEntity<List<ProductEntity>> searchProductsByTheme(@PathVariable String theme) {
        try {
            List<ProductEntity> products = repository.findByThemeContainingIgnoreCase(theme);

            if (products.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            System.err.println("Erro ao buscar produtos pelo tema: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
	@PutMapping("/{id}/favorite")
	@Transactional
	public ResponseEntity<ProductEntity> toggleFavoriteStatus(@PathVariable Long id, @RequestParam boolean favorite) {
		try {
			ProductEntity product = repository.findById(id)
					.orElseThrow(() -> new Exception("Produto não encontrado com o ID: " + id));

			product.setFavorite(favorite);

			ProductEntity updatedProduct = repository.save(product);

			return ResponseEntity.ok(updatedProduct);

		} catch (Exception e) {
			System.err.println("Erro ao atualizar status de favorito do produto: " + e.getMessage());
			return ResponseEntity.internalServerError().body(null);
		}
	}

    @GetMapping("/favorites")
    public ResponseEntity<List<ProductEntity>> getFavoriteProducts() {
        try {
            List<ProductEntity> favoriteProducts = repository.findByIsFavoriteTrue();

            if (favoriteProducts.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(favoriteProducts);
        } catch (Exception e) {
            System.err.println("Erro ao buscar produtos favoritos: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/favorites/search/{theme}")
    public ResponseEntity<List<ProductEntity>> searchFavoriteProductsByTheme(@PathVariable String theme) {
        try {
            List<ProductEntity> products = repository.findByIsFavoriteTrueAndThemeContainingIgnoreCase(theme);

            if (products.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            System.err.println("Erro ao buscar produtos favoritos pelo tema: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
}
