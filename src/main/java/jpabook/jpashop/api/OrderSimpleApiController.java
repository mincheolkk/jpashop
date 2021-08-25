package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * xToOne (ManyToOne, OneToOne)
 * Order
 * Order -> Member
 * Order -> Delivery
 */

@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName();  // Lazy 강제 초기화
            order.getDelivery().getAddress();  // Lazy 강제 초기화
        }

        return all;
    }

    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        // 현재 데이터 상황에서 SQL 1번에 결과 주문수 2개.
        // 즉 아래 stream은 2번 루프가 돌게 됨.
        // 이게 1 + N 문제.
        // 첫 번째 쿼리에서 N 개를 가져옴.
        // N 에서도 가져올 쿼리들이 있기에 N의 갯수에 따라 막대한 양의 쿼리가 날라가게 됨.
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))  // == .map(SimpleOrderDto::new)
                .collect(Collectors.toList());

        return result;

    }

    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithMemberDelivery();

        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());
        // 엔티티를 조회하고, DTO로 변환

        return result;
    }

    // 결과 쿼리
//    select
//    order0_.order_id as order_id1_6_0_,
//    member1_.member_id as member_i1_4_1_,
//    delivery2_.delivery_id as delivery1_2_2_,
//    order0_.delivery_id as delivery4_6_0_,
//    order0_.member_id as member_i5_6_0_,
//    order0_.order_date as order_da2_6_0_,
//    order0_.status as status3_6_0_,
//    member1_.city as city2_4_1_,
//    member1_.street as street3_4_1_,
//    member1_.zipcode as zipcode4_4_1_,
//    member1_.name as name5_4_1_,
//    delivery2_.city as city2_2_2_,
//    delivery2_.street as street3_2_2_,
//    delivery2_.zipcode as zipcode4_2_2_,
//    delivery2_.status as status5_2_2_
//            from
//    orders order0_
//    inner join
//    member member1_
//    on order0_.member_id=member1_.member_id
//    inner join
//    delivery delivery2_
//    on order0_.delivery_id=delivery2_.delivery_id

    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> ordersV4() {
        return orderSimpleQueryRepository.findOrderDtos();
    }

    // 결과 쿼리
//    select
//    order0_.order_id as col_0_0_,
//    member1_.name as col_1_0_,
//    order0_.order_date as col_2_0_,
//    order0_.status as col_3_0_,
//    delivery2_.city as col_4_0_,
//    delivery2_.street as col_4_1_,
//    delivery2_.zipcode as col_4_2_
//            from
//    orders order0_
//    inner join
//    member member1_
//    on order0_.member_id=member1_.member_id
//    inner join
//    delivery delivery2_
//    on order0_.delivery_id=delivery2_.delivery_id

    // v3 와 v4 는 우열을 가리기 힘듬. 각각 트레이드 오프가 있음
    // v3 는 order 를 가져올때 내가 원하는 것만 fetch join 해서 가져옴.

    // v4 는 sql 짜듯이 jpql 을 짜서 가져옴 . 하나의 dto 에만 최적화
    // 재사용이 안 됨.

    // 쿼리에서 보면, select 절은 큰 영향을 못 미침, 주로 join, where 등이 성능에 영향

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        // Dto가 엔티티를 파라미터로 받는건 크게 문제가 되지 않음.
        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();  // LAZY 초기화
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();  // LAZY 초기화
        }
    }

}
