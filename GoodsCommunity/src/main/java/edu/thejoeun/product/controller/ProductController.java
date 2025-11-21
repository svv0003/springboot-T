package edu.thejoeun.product.controller;

import edu.thejoeun.product.model.dto.Product;
import edu.thejoeun.product.model.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * 전체 상품 조회
     * 조회 성공 시 ResponseEntity.ok = 200
     * 번호에 따른 상태 확인 방식이다.
     * @return
     *
     * ResponseEntity 사용 금지라고 하셨다.
     */
    @GetMapping("/all")
    public ResponseEntity<List<Product>> getAllProducts() {
        log.info("GET /api/product/all - 전체 상품 조회");
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    /**
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable int id) {
        log.info("GET /api/product/{}} - 상품 상세 조회", id);
        try {
            Product product = productService.getProductById(id);
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            Map<String, Object> res = new HashMap<>();
            res.put("success", false);
            res.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
        }
    }

    /**
     * PathVariable & RequestParam = Header에서 데이터 주고받기
     * PathVariable = {} 형태로 중괄호 내부 변수명에 해당하는 데이터로 접근한다.
     * RequestParam = ?categoty="카테고리명칭"과 같은 형태로 K:V 데이터 접근한다.
     * @param category 클라이언트가 클릭한 카테고리 값을 넣는다.
     * @return 카테고리에 해당하는 상품들을 조회한다.
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getProdctsByCategory(@PathVariable String category) {
        log.info("GET /api/product/category/{} - 카테고리별 조회", category);
        List<Product> products = productService.getProdctsByCategory(category);
        return ResponseEntity.ok(products);
    }

    /**
     * PathVariable & RequestParam = Header에서 데이터 주고받기
     * PathVariable = {} 형태로 중괄호 내부 변수명에 해당하는 데이터로 접근한다.
     * RequestParam = ?categoty="카테고리명칭"과 같은 형태로 K:V 데이터 접근한다.
     * @param keyword 키워드에 해당하는 DB에서 조회한다.
     * @return 검색 키워드로 조회된 모든 데이터를 목록 형태로 반환한다.
     */
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@PathVariable String keyword) {
        log.info("GET /api/product/search?keyword={} - 상품 검색", keyword);
        List<Product> products = productService.searchProducts(keyword);
        return ResponseEntity.ok(products);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> addProduct(@RequestBody Product product) {
        log.info("POST /api/product - 상품 등록", product.getProductName());
        Map<String, Object> res = new HashMap<>();
        try {
            productService.insertProduct(product);
            res.put("success", true);
            res.put("message", "상품이 성공적으로 완료되었습니다.");
            res.put("productId", product.getId());
            log.info("상품 등록 완료 - ID : {}", product.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        } catch (IllegalArgumentException e) {
            res.put("success", false);
            res.put("message", e.getMessage());
            log.warn("상품 등록 실패 - 유효성 검사 오류 : {}", e.getMessage());
            return ResponseEntity.badRequest().body(res);
        } catch (Exception e) {
            res.put("success", false);
            res.put("message", e.getMessage());
            log.error("상품 등록 실패 - 서버 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);

        }
    }

    /**
     * 상품 수정
     * @param id      수정할 제품의 id 가져오기
     * @param product 수정할 제품의 대하여 작성된 내용 모두 가져오기
     * @return        수정된 결과 클라이언트 전달
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> upDateProduct(@PathVariable int id, @RequestBody Product product) {
        log.info("Put /api/product/{} - 상품 수정",id);
        Map<String, Object> res = new HashMap<>();

        try{
            product.setId(id);
            productService.updateProduct(product);
            res.put("success",true);
            res.put("message","상품이 성공적으로 수정되었습니다.");
            res.put("productId", product.getId());
            log.info("상품 수정 성공 - ID : {} ", product.getId());
            return ResponseEntity.ok(res);

        }catch (IllegalArgumentException e){
            log.warn("상품 수정 실패 - 유효성 검사 오류 : {} ", e.getMessage());
            res.put("success",false);
            res.put("message",e.getMessage());
            return ResponseEntity.badRequest().body(res);

        } catch (Exception e) {
            log.error("상품 수정 실패 - 서버 오류", e);
            res.put("success",false);
            res.put("message","상품 수정 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);

        }
    }

    /**
     * 상품 삭제
     * @param id    id에 해당하는 상품 삭제 관련 기능 수행
     * @return      수행 결과 반환
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteProduct(@PathVariable int id) {
        log.info("Delete /api/product/{} - 상품 삭제",id);
        Map<String, Object> res = new HashMap<>();

        try{
            productService.deleteProduct(id);
            res.put("success",true);
            res.put("message","상품이 성공적으로 삭제되었습니다.");
            res.put("productId", id);
            log.info("상품 삭제 성공 - ID : {} ", id);
            return ResponseEntity.ok(res);

        }catch (IllegalArgumentException e){
            log.warn("상품 삭제 실패 {} ", e.getMessage());
            res.put("success",false);
            res.put("message",e.getMessage());
            return ResponseEntity.badRequest().body(res);

        } catch (Exception e) {
            log.error("상품 수정 실패 - 서버 오류", e);
            res.put("success",false);
            res.put("message","상품 수정 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);

        }
    }

    /**
     * 재고 업데이트
     * @param id        재고 업데이트할 상품 id 조회
     * @param quantity  프론트엔드에서 재고 업데이트 관련 수량 변경 요청
     * @return          수행 결과 반환
     */
    @PatchMapping("/{id}/stock")
    public ResponseEntity<Map<String, Object>> updateStock(@PathVariable int id, @RequestParam int quantity) {
        log.info("Patch /api/product/{}/stock?quantity={} - 재고 업데이트", id, quantity);
        Map<String, Object> res = new HashMap<>();

        try{
            productService.updateStock(id, quantity);
            res.put("success",true);
            res.put("message","재고가 성공적으로 업데이트되었습니다.");
            res.put("productId", id);
            log.info("재고 업데이트 성공 - ID : {} ", id);
            return ResponseEntity.ok(res);

        }catch (IllegalArgumentException e){
            log.warn("재고 업데이트 실패 {} ", e.getMessage());
            res.put("success",false);
            res.put("message",e.getMessage());
            return ResponseEntity.badRequest().body(res);

        } catch (Exception e) {
            log.error("재고 업데이트 실패 - 서버 오류", e);
            res.put("success",false);
            res.put("message","재고 업데이트 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);

        }
    }
}