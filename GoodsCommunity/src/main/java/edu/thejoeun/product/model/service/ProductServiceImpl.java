package edu.thejoeun.product.model.service;

import edu.thejoeun.product.model.dto.Product;
import edu.thejoeun.product.model.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;

    /*
    @Override
    public List<Product> getAllProducts() {
        log.info("전체 상품 조회 : {}", )
        return List.of();
    }
    getAllProducts 자료형이 List<Product>이기 때문에
    변수 자료형 타입 또한 List<Product> 설정한다.
     */
    @Override
    public List<Product> getAllProducts() {
        List<Product> p = productMapper.getAllProducts();
        log.info("전체 상품 조회 : {}", p);
        return p;
    }

    @Override
    public Product getProductById(int id) {
        Product p = productMapper.getProductById(id);
        if (p == null) {
            log.warn("상품 조회할 수 없습니다. ID : {}", id);
            throw new IllegalArgumentException("존재하지 않는 상품입니다.");
        }
        return p;
    }

    @Override
    public Product getProductByCode(String productCode) {
        Product p = productMapper.getProductByCode(productCode);
        if (p == null) {
            log.warn("상품 조회할 수 없습니다. productCode : {}", productCode);
            throw new IllegalArgumentException("존재하지 않는 상품입니다.");
        }
        return p;
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        List<Product> p = productMapper.getProductsByCategory(category);
        log.info("상품 카테고리 : {}", p);
        return p;
    }

    /**
     * 상품 조회 - 키워드 검색
     * @param keyword trim() 사용해서 공백 제거 및 유효성 검사 필요하다.
     * @return
     */
    @Override
    public List<Product> searchProducts(String keyword) {
        log.info("상품 검색 키워드 : {}", keyword);
        if (keyword == null || keyword.trim().isEmpty()) {
            log.warn("검색어가 비어있습니다.");
            return null;
        }
        // 한 글자 검색 방지 기능 추가하기
        return productMapper.searchProducts(keyword.trim());
    }

    @Override
    @Transactional
    public void insertProduct(Product product) {
        log.info("상품 등록 시작 : {}", product.getProductName());
        /*
        유효성 검사
        void validateProduct(product);
        메서드를 만들어서 데이터 저장하기 전에 백엔드에서 한 번 더 유효성 검사 진행한다.
         */
        Product existingProduct = productMapper.getProductByCode(product.getProductCode());
        if (existingProduct != null) {
            log.warn("상품 코드 중복 : {}", product.getProductCode());
            throw new IllegalArgumentException("이미 존재하는 상품입니다.");
        }
        int result = productMapper.insertProduct(product);
        if (result > 0) {
            log.info("상품 등록 완료 : {}, name ; {}", product.getId(), product.getProductName());
        } else {
            log.error("상품 등록 실패 : {}", product.getProductName());
            throw new RuntimeException("상품 등록 실패했습니다.");
        }

    }

    @Override
    @Transactional
    public void updateProduct(Product product) {
        log.info("상품 수정 시작 : {}", product.getProductName());
        /*
        유효성 검사
        void validateProduct(product);
        메서드를 만들어서 데이터 저장하기 전에 백엔드에서 한 번 더 유효성 검사 진행한다.
         */
        Product existingProduct = productMapper.getProductById(product.getId());
        if (existingProduct == null) {
            log.warn("상품 조회 불가 : {}", product.getId());
            throw new IllegalArgumentException("존재하지 않는 상품입니다.");
        }
        int result = productMapper.updateProduct(product);
        if (result > 0) {
            log.info("상품 수정 완료 : {}", product.getId());
        } else {
            log.error("상품 수정 실패 : {}", product.getId());
            throw new RuntimeException("상품 수정 실패했습니다.");
        }
    }

    @Override
    @Transactional
    public void deleteProduct(int id) {
        log.info("상품 삭제 시작 : {}", id);
        /*
        유효성 검사
        void validateProduct(product);
        메서드를 만들어서 데이터 저장하기 전에 백엔드에서 한 번 더 유효성 검사 진행한다.
         */
        Product existingProduct = productMapper.getProductById(id);
        if (existingProduct == null) {
            log.warn("상품 조회 불가 : {}", id);
            throw new IllegalArgumentException("존재하지 않는 상품입니다.");
        }
        int result = productMapper.deleteProduct(id);
        if (result > 0) {
            log.info("상품 삭제 완료 : {}", id);
        } else {
            log.error("상품 삭제 실패 : {}", id);
            throw new RuntimeException("상품 삭제 실패했습니다.");
        }
    }

    @Override
    @Transactional
    public void updateStock(int id, int quantity) {
        log.info("재고 업데이트 시작 : {}, {}", id, quantity);
        Product existingProduct = productMapper.getProductById(id);
        if (existingProduct == null) {
            log.warn("상품 조회 불가 : {}", id);
            throw new IllegalArgumentException("존재하지 않는 상품입니다.");
        }
        int newStock = existingProduct.getStockQuantity() + quantity;
        if (newStock < 0) {
            log.warn("재고는 음수가 될 수 없습니다. 현재 : {}, 수정 : {}", existingProduct.getStockQuantity(), quantity);
        }
        int result = productMapper.deleteProduct(id);
        if (result > 0) {
            log.info("재고 업데이트 완료 : {}", id);
        } else {
            log.error("재고 업데이트 실패 : {}", id);
            throw new RuntimeException("재고 업데이트 실패했습니다.");
        }
    }
}
