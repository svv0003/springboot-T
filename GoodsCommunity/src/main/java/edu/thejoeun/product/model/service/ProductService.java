package edu.thejoeun.product.model.service;

import edu.thejoeun.product.model.dto.Product;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProductService {

    /**
     * 전체 상품 조회
     * @return
     */
    List<Product> getAllProducts();

    /**
     * 상품 id로 상세 조회
     */
    Product getProductById(int id);

    /**
     * 상품 코드로 상세 조회
     * @param productCode
     * @return
     */
    Product getProductByCode(String productCode);

    /**
     * 카테고리별 조회
     * @param category
     * @return
     */
    List<Product> getProductsByCategory(String category);

    /**
     * 상품명으로 조회
     * @param keyword
     * @return
     */
    List<Product> searchProducts(String keyword);

    /*
    int  : 등록 데이터가 0부터 n개까지 저장 확인 가능하며, 다수 상품 등록 시 사용한다.
    void : 등록 데이터의 유무만 확인 가능하다. (성공/실패)
    수정, 삭제도 동일하다.
     */
    // 상품 등록
    void insertProduct(Product product);

    // 상품 수정
    void updateProduct(Product product);

    // 상품 삭제
    void deleteProduct(int id);

    // 재고 업데이트
    void updateStock(int id, int quantity);

}
