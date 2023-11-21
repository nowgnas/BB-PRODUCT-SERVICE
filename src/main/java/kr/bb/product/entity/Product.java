package kr.bb.product.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@DynamoDBTable(tableName = "product")
public class Product {
  @DynamoDBHashKey @DynamoDBAutoGeneratedKey private Long productId;

  @DynamoDBAttribute(attributeName = "category")
  private Category category;

  @DynamoDBAttribute(attributeName = "product_name")
  private String productName;

  @DynamoDBAttribute(attributeName = "product_summary")
  private String productSummary;

  @DynamoDBAttribute(attributeName = "product_price")
  private Long productPrice;

  @DynamoDBAttribute(attributeName = "product_sale_status")
  private String productSaleStatus;

  @DynamoDBAttribute(attributeName = "tag")
  private Tag tag;

  @DynamoDBAttribute(attributeName = "product_flowers")
  private Flowers productFlowers;

  @DynamoDBAttribute(attributeName = "product_description_image")
  private String productDescriptionImage;

  @DynamoDBAttribute(attributeName = "review_count")
  private Long reviewCount;

  @DynamoDBAttribute(attributeName = "product_sale_amount")
  private Long productSaleAmount;

  @DynamoDBAttribute(attributeName = "average_rating")
  private Double averageRating;

  @DynamoDBAttribute(attributeName = "store_id")
  private Long storeId;
}