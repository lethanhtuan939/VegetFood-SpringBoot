package vn.LeThanhTuan.service;

import java.io.IOException;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import vn.LeThanhTuan.entity.dto.ProductDto;

public interface ProductService {

	Page<ProductDto> getAllProduct(String keyword, int pageNumber, int amountPage);

	ProductDto createProduct(ProductDto productDto, MultipartFile file) throws IOException;

	ProductDto updateProduct(ProductDto productDto, MultipartFile file) throws IOException;

	Page<ProductDto> getAllProductByCategoryId(String keyword, int pageNumber, int amountPage, Integer categoryId);

	ProductDto getProductById(Integer id);

}
