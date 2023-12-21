package kr.bb.product.domain.flower.application.port.in;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import kr.bb.product.common.dto.FlowerInformation;
import kr.bb.product.domain.flower.application.usecase.FlowerQueryUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.aws.messaging.listener.SimpleMessageListenerContainer;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class FlowerQueryInputPortTest {
  @MockBean SimpleMessageListenerContainer simpleMessageListenerContainer;
  @Autowired private FlowerQueryUseCase flowerQueryUseCase;

  @Test
  @DisplayName("전체 꽃 조회 ")
  void getAllFlowers() {
    List<FlowerInformation> allFlowers = flowerQueryUseCase.getAllFlowers();
    assertThat(allFlowers.size()).isEqualTo(4);
  }
}