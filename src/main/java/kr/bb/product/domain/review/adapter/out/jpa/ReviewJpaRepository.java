package kr.bb.product.domain.review.adapter.out.jpa;

import java.util.List;
import kr.bb.product.domain.review.entity.Review;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewJpaRepository extends JpaRepository<Review, Long> {
  @Query(
      "SELECT DISTINCT r FROM Review r LEFT JOIN FETCH r.reviewImages i WHERE r.productId IN :productIds")
  List<Review> getReviewByProductId(
      @Param("productIds") List<String> productIds, Pageable pageable);
}