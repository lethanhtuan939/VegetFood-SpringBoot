package vn.LeThanhTuan.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import vn.LeThanhTuan.entity.Category;
import vn.LeThanhTuan.entity.Product;
import vn.LeThanhTuan.entity.dto.ProductDto;
import vn.LeThanhTuan.repository.CategoryRepository;
import vn.LeThanhTuan.repository.ProductRepository;
import vn.LeThanhTuan.service.ProductService;
import vn.LeThanhTuan.util.FileUploadUtil;

@Service
public class ProductServiceImpl implements ProductService {
	String uploadDir = "src/main/resources/static/images";
	
	@Autowired
	ProductRepository productRepository;
	
	@Autowired
	CategoryRepository categoryRepository;
	
	@Autowired
	ModelMapper modelMapper;
	
	private ProductDto toDto(Product product) {
		return modelMapper.map(product, ProductDto.class);
	}
	
	private Product toProduct(ProductDto productDto) {
		return modelMapper.map(productDto, Product.class);
	}
	
	@Override
	public ProductDto createProduct(ProductDto productDto, MultipartFile file) throws IOException {
		
		String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
		productDto.setImage(fileName);
		productDto.setActive(true);
		
		Product product = toProduct(productDto);

		Optional<Category> optional = categoryRepository.findById(productDto.getCategory().getId());
		
		if(optional.isEmpty()) {
			return null;
		}
		
		Category category = optional.get();
		product.setCategory(category);
		
		FileUploadUtil.saveFile(uploadDir, product.getImage(), file);
		
		Product savedProduct = productRepository.save(product);
		
		return toDto(savedProduct);
		
	}
	
	@Override
	public Page<ProductDto> getAllProduct(String keyword, int pageNumber, int amountPage) {
		Sort sort = Sort.by("id");
		Pageable pageable = PageRequest.of(pageNumber-1, amountPage, sort);
		
		Page<Product> products;
		if(keyword.isEmpty()) {			
			products = productRepository.findAll(pageable);
		} else {
			products = productRepository.findAllActiveProduct(keyword, pageable);
		}
		
		return products.map(this::toDto);
	}

	@Override
	public ProductDto updateProduct(ProductDto productDto, MultipartFile file) throws IOException {
		
		Optional<Product> optionalProduct = productRepository.findById(productDto.getId());
		Optional<Category> optionalCategory = categoryRepository.findById(productDto.getCategory().getId());
		
		if(optionalProduct.isEmpty() || optionalCategory.isEmpty()) {
			return null;
		}
		
		Product product = optionalProduct.get();
		Category category = optionalCategory.get();
		
		String fileName = "";
		if(!file.isEmpty()) {
			fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
			product.setImage(fileName);
		}
		
		product.setName(productDto.getName());
		product.setDescription(productDto.getDescription());
		product.setActive(productDto.isActive());
		product.setPrice(productDto.getPrice());
		product.setCategory(category);
		
		
		Product updatedProduct = productRepository.save(product);
		
		return toDto(updatedProduct);
	}
	
	@Override
	public Page<ProductDto> getAllProductByCategoryId(String keyword, int pageNumber, int amountPage, Integer categoryId) {
		Sort sort = Sort.by("id");
		Pageable pageable = PageRequest.of(pageNumber-1, amountPage, sort);
		
		Page<Product> products;
		
		if(categoryId != 0) {
			Optional<Category> optional = categoryRepository.isActiveCategory(categoryId);
			
			if(optional.isEmpty()) {
				return null;
			}
			
			products = productRepository.findAllActiveProduct(categoryId, pageable);
		} else if(keyword.trim().isEmpty()) {			
			products = productRepository.findAllActiveProduct(pageable);
		} else {
			products = productRepository.findAllActiveProduct(keyword, pageable);
		}
		
		return products.map(this::toDto);
	}
	
	@Override
	public ProductDto getProductById(Integer id) {
		Optional<Product> optional = productRepository.findActiveProduct(id);
		
		if(optional.isEmpty()) {
			return null;
		}
		
		Product product = optional.get();
		
		return toDto(product);
	}
	
	@Override
	public List<ProductDto> findTop8Product() {
		List<Product> products = productRepository.findTop8ByOrderByIdDesc();
		
		return products.stream().map(this::toDto).collect(Collectors.toList());
	}

}
