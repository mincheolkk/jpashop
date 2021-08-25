package jpabook.jpashop.repository.order.simplequery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {

    /**
     *  복잡한 조인 쿼리를 가지고 DTO 로 뽑아야 할 때가 많.
     *  이런 것들을 QueryService , QueryRepository 별도로 뽑아서 처리할 수도 있음.
     *  유지 보수성이 좋아짐.
     *  아래 findOrderDtos 는 조회 전용으로 api 스펙에 맞춰서 만든걸을 알 수 있음.
     */

    private final EntityManager em;

    // 화면에 박힌 API
    public List<OrderSimpleQueryDto> findOrderDtos() {
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d", OrderSimpleQueryDto.class)
                .getResultList();
    }
}
