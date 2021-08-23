package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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

        return result;
    }

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
