package kr.bb.product.domain.product.adapter.out.mongo;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import kr.bb.product.domain.category.entity.Category;
import kr.bb.product.domain.product.application.port.out.ProductQueryOutPort;
import kr.bb.product.domain.product.entity.Product;
import kr.bb.product.domain.product.entity.ProductSaleStatus;
import kr.bb.product.domain.product.vo.ProductFlowers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ProductQueryRepositoryTest {
  @Autowired ProductCommandRepository productCommandRepository;
  @Autowired ProductRepository productRepository;
  @Autowired EntityManager em;
  @Autowired private ProductMongoRepository productMongoRepository;
  @Autowired private ProductQueryOutPort productQueryOutPort;

  @Test
  void createProduct() {
    productMongoRepository.deleteAll();
    Product build =
        Product.builder()
            .productName("name")
            .productSummary("summary")
            .productPrice(12L)
            .productDescriptionImage("images")
            .productThumbnail("thumbnail")
            .build();
    productCommandRepository.createProduct(build);
    List<Product> all = productMongoRepository.findAll();
    assertThat(all.get(0).getProductName()).isEqualTo(build.getProductName());
    assertThat(all.get(0).getCategory()).isNull();
  }

  @Test
  void findProductByStoreId() {
    productMongoRepository.deleteAll();
    for (int i = 0; i < 5; i++) {
      Product build =
          Product.builder()
              .productName("name")
              .productSummary("summary")
              .productPrice(12L)
              .storeId(1L)
              .productDescriptionImage("images")
              .productThumbnail("thumbnail")
              .build();
      productCommandRepository.createProduct(build);
    }
    PageRequest pageRequest = PageRequest.of(0, 5);
    List<Product> productByStoreId = productMongoRepository.findProductByStoreId(1L);
    assertThat(productByStoreId.size()).isEqualTo(5);
  }

  @Test
  @DisplayName("가게 사장 상품 리스트 조회")
  void findStoreProducts() {
    productMongoRepository.deleteAll();
    ProductFlowers build1 = ProductFlowers.builder().flowerId(1L).build();
    List<ProductFlowers> list = new ArrayList<>();
    list.add(build1);
    for (int i = 0; i < 10; i++) {
      Product build =
          Product.builder()
              .productThumbnail("thumbnail")
              .productName("product name")
              .productSummary("summary")
              .category(Category.builder().categoryName("ca").categoryId(1L + i).build())
              .productDescriptionImage("description image")
              .productFlowers(list)
              .productPrice(100000L)
              .storeId(1L)
              .isSubscription(true)
              .build();
      productMongoRepository.save(build);
    }
    PageRequest pageRequest = PageRequest.of(0, 5);
    List<Product> storeProducts =
        productQueryOutPort.findStoreProducts(1L, null, 1L, null, pageRequest);
    assertThat(storeProducts.size()).isEqualTo(5);
  }
}
