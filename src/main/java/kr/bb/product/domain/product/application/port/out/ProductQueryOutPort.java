package kr.bb.product.domain.product.application.port.out;

import java.util.List;
import kr.bb.product.domain.product.entity.Product;
import kr.bb.product.domain.product.entity.ProductSaleStatus;
import org.springframework.data.domain.Pageable;

public interface ProductQueryOutPort {
  List<Product> findProductByStoreId(Long storeId);

  List<Product> findStoreProducts(
      Long soreId, Long categoryId, Long flowerId, ProductSaleStatus saleStatus, Pageable pageable);
}
