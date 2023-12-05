package kr.bb.product.domain.product.application.port.in;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import kr.bb.product.domain.category.entity.Category;
import kr.bb.product.domain.product.adapter.out.mongo.ProductMongoRepository;
import kr.bb.product.domain.product.entity.Product;
import kr.bb.product.domain.product.entity.ProductCommand;
import kr.bb.product.domain.product.entity.ProductCommand.BestSellerTopTen;
import kr.bb.product.domain.product.entity.ProductCommand.ProductList;
import kr.bb.product.domain.product.entity.ProductCommand.ProductListItem;
import kr.bb.product.domain.product.entity.ProductCommand.ProductRegister;
import kr.bb.product.domain.product.entity.ProductCommand.ProductsGroupByCategory;
import kr.bb.product.domain.product.entity.ProductCommand.SortOption;
import kr.bb.product.domain.product.entity.ProductCommand.StoreProductDetail;
import kr.bb.product.domain.product.entity.ProductCommand.StoreProductList;
import kr.bb.product.domain.product.entity.ProductSaleStatus;
import kr.bb.product.domain.product.infrastructure.client.WishlistServiceClient;
import kr.bb.product.domain.product.vo.ProductFlowers;
import kr.bb.product.domain.product.vo.ProductFlowersRequestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.aws.messaging.listener.SimpleMessageListenerContainer;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@AutoConfigureWebTestClient
@ExtendWith(SpringExtension.class)
class ProductQueryInputPortTest {
  @MockBean SimpleMessageListenerContainer simpleMessageListenerContainer;
  @Autowired ProductCommandInputPort productCommandInputPort;
  @Autowired private ProductCommandInputPort productStoreInputPort;
  @Autowired private ProductMongoRepository productMongoRepository;
  @Autowired private ProductQueryInputPort productQueryInputPort;
  private WebTestClient webTestClient;
  @Mock private WishlistServiceClient wishlistServiceClient;

  @BeforeEach
  void beforeEach() {
    this.webTestClient = WebTestClient.bindToController(this.wishlistServiceClient).build();
  }

  private void extracted() {
    for (int i = 0; i < 10; i++) {
      List<Long> tagList = new ArrayList<>();
      tagList.add(1L);
      tagList.add(2L);

      List<ProductFlowersRequestData> list = new ArrayList<>();
      list.add(ProductFlowersRequestData.builder().flowerId(1L).flowerCount(2L).build());
      list.add(ProductFlowersRequestData.builder().flowerId(3L).flowerCount(3L).build());
      list.add(ProductFlowersRequestData.builder().flowerId(2L).flowerCount(2L).build());

      ProductCommand.ProductRegister product =
          ProductCommand.ProductRegister.builder()
              .categoryId(1L)
              .productTag(tagList)
              .representativeFlower(
                  ProductFlowersRequestData.builder().flowerCount(3L).flowerId(1L).build())
              .flowers(list)
              .productName("Example Product")
              .productSummary("Product Summary")
              .productDescriptionImage("image")
              .storeId(1L)
              .productThumbnail("thumbnail")
              .productPrice(100L + i)
              .productDescriptionImage("image_url")
              .build();
      productStoreInputPort.createProduct(product);
    }
  }

  @Test
  @DisplayName("가게 사장 상품 상세 조회")
  void getStoreProductDetail() {
    productMongoRepository.deleteAll();
    extracted();
    Product product = productMongoRepository.findAll().get(0);

    StoreProductDetail storeProductDetail =
        productQueryInputPort.getStoreProductDetail(1L, product.getProductId());
    System.out.println(storeProductDetail.getRepresentativeFlower().getFlowerName());
    assertThat(storeProductDetail.getRepresentativeFlower().getFlowerName()).isNotNull();
  }

  @Test
  @DisplayName("가게 사장 상품 리스트 조회 ")
  void getStoreProducts() {
    productMongoRepository.deleteAll();
    ProductFlowers build1 = ProductFlowers.builder().flowerId(1L).isRepresentative(true).build();
    ProductFlowersRequestData flowers =
        ProductFlowersRequestData.builder().flowerId(1L).flowerCount(4L).build();
    List<Long> list = new ArrayList<>();
    List<ProductFlowersRequestData> lst = new ArrayList<>();
    lst.add(flowers);
    list.add(1L);
    for (int i = 0; i < 10; i++) {
      ProductRegister build =
          ProductRegister.builder()
              .productName("name")
              .storeId(1L)
              .categoryId(1L)
              .flowers(lst)
              .productPrice(1L + i)
              .productTag(list)
              .representativeFlower(
                  ProductFlowersRequestData.builder().flowerCount(3L).flowerId(1L).build())
              .build();
      productCommandInputPort.createProduct(build);
    }
    productMongoRepository
        .findAll()
        .forEach(item -> System.out.println(item.getProductFlowers().toString()));

    PageRequest pageRequest = PageRequest.of(0, 5);

    ProductList productsByCategory =
        productQueryInputPort.getProductsByCategory(1L, 1L, 1L, SortOption.SALE, pageRequest);
    StoreProductList storeProducts =
        productQueryInputPort.getStoreProducts(1L, 1L, null, ProductSaleStatus.SALE, pageRequest);
    assertThat(storeProducts.getProducts().size()).isGreaterThan(0);
  }

  @Test
  @DisplayName("카테고리별 상품 리스트 조회")
  void getProductsByCategory() {
    productMongoRepository.deleteAll();
    extracted();
    PageRequest pageRequest = PageRequest.of(0, 5);
    ProductList productsByCategory =
        productQueryInputPort.getProductsByCategory(1L, null, SortOption.LOW, pageRequest);
    List<ProductListItem> products = productsByCategory.getProducts();
    assertThat(products.get(0).getProductPrice() < products.get(1).getProductPrice()).isTrue();
  }

  @Test
  @DisplayName("카테고리별 상품 리스트 조회 - login ")
  void getProductsByCategoryLogin() {
    productMongoRepository.deleteAll();
    extracted();
    PageRequest pageRequest = PageRequest.of(0, 5);
    ProductList productsByCategory =
        productQueryInputPort.getProductsByCategory(1L, 1L, SortOption.LOW, pageRequest);
    List<ProductListItem> products = productsByCategory.getProducts();
    assertThat(products.get(0).getProductPrice() < products.get(1).getProductPrice()).isTrue();
  }

  @Test
  @DisplayName("태그별 상품 리스트 조회 - login ")
  void getProductsByTagLogin() {
    productMongoRepository.deleteAll();
    extracted();
    PageRequest pageRequest = PageRequest.of(0, 5);
    ProductsGroupByCategory productsByTag =
        productQueryInputPort.getProductsByTag(1L, 1L, 1L, SortOption.SALE, pageRequest);
    assertThat(productsByTag.getProducts().size()).isEqualTo(5);
    assertThat(productsByTag.getProducts().get(1L).getProducts().size()).isEqualTo(5);
  }

  @Test
  @DisplayName("태그별 상품 리스트 조회 - 비로그인  ")
  void getProductsByTag() {
    productMongoRepository.deleteAll();
    extracted();
    PageRequest pageRequest = PageRequest.of(0, 5);
    ProductsGroupByCategory productsByTag =
        productQueryInputPort.getProductsByTag(null, 1L, 1L, SortOption.SALE, pageRequest);
    assertThat(productsByTag.getProducts().size()).isEqualTo(5);
    assertThat(productsByTag.getProducts().get(1L).getProducts().size()).isEqualTo(5);
  }

  @Test
  @DisplayName("태그별 상품 리스트 조회 - 비로그인  ")
  void getProductsByTagNotLogin() {
    productMongoRepository.deleteAll();
    extracted();
    PageRequest pageRequest = PageRequest.of(0, 5);
    ProductsGroupByCategory productsByTag =
        productQueryInputPort.getProductsByTag(1L, 1L, SortOption.SALE, pageRequest);
    assertThat(productsByTag.getProducts().size()).isEqualTo(5);
    assertThat(productsByTag.getProducts().get(1L).getProducts().size()).isEqualTo(5);
  }

  @DisplayName("베스트 셀러 10개 정보")
  void getBestSellerTopTen() {
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
              .productPrice(100000L + i)
              .productSaleAmount(10L + i)
              .storeId(1L)
              .isSubscription(false)
              .build();
      productMongoRepository.save(build);
    }
    BestSellerTopTen bestSellerTopTen = productQueryInputPort.getBestSellerTopTen(1L);
    assertThat(bestSellerTopTen.getProducts().size()).isEqualTo(10);
    assertThat(bestSellerTopTen.getProducts().get(0).getData().get(0)).isEqualTo(19L);
  }
}