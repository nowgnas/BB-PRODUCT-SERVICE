package kr.bb.product.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableReactiveMongoAuditing
@EnableMongoRepositories(basePackages = "kr.bb.product.repository.mongo")
@RequiredArgsConstructor
public class MongoDBConfiguration {
//  private final MongoMappingContext mongoMappingContext;
//
//  @Bean
//  public MappingMongoConverter mappingMongoConverter(
//      MongoDatabaseFactory mongoDatabaseFactory, MongoMappingContext mongoMappingContext) {
//    DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDatabaseFactory);
//    MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, mongoMappingContext);
//    converter.setTypeMapper(new DefaultMongoTypeMapper(null));
//    return converter;
//  }
}
