package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;

    // 엔티티 직접 노출
    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());


        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();

            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName());
            // 아래와 같은 결과나옴
//            for (OrderItem orderItem : orderItems) {
//                orderItem.getItem().getName(); }
            // orderItems 와 그 안에 있는 orderItems 정보를 같이 출력하고 싶기에, 강제 초기화를 해줌.
            // 프록시(지연로딩은 프록시를 사용)는 강제 초기화 하게되면, 데이터가 있기에 데이터를 뿌림.
        }
        return all;
    }
    // api 에 엔티티를 직접 노출하는 것은 좋지 않음

    // 앤티티를 DTO 로 변환
    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        // orders 를 OrderDto 로 변환해야 함.
        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return result;
    }

    @Getter
    static class OrderDto {

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
//            order.getOrderItems().stream().forEach(o -> o.getItem().getName());
//            orderItems = order.getOrderItems();
            // OrderItem 엔티티를 직접 사용하던걸 OrderItemDto 로 변경하기에 위 두 줄은 이제 주석.
            orderItems = order.getOrderItems().stream()
                    .map(OrderItemDto::new)
//                    .map(orderItem -> new OrderItemDto(orderItem))
                    .collect(Collectors.toList());
        }
        // @Getter 를 처음엔 안 걸어줘서 500에러 발생. 에러문구에 no Properties 가 있는데 이건 대부분 @Getter 가 없어서 발생
    }
    // DTO 로 반환하려고 의도했을땐 DTO 안에 엔티티가 있으면 안됨. 래핑하는것도 안됨.
    // 왜냐면 orderItems 로 나간 데이터가 외부에 다 노출됨. (기존 엔티티가 바뀌면 api를 수정해야 되기에 엔티티를 노출하면 안됨)
    // 엔티티가 외부에 노출되면 안된다고해서 간단하게 dto 로 감싸서 보내라는 뜻이 아님.
    // 엔티티에 대한 의존을 완전히 끊어야 됨.
    // 해결책 -> OrderItem 을 DTO 로 바꿔야 함.

    @Getter
    static class OrderItemDto {

        // 필요한 정보들만 보냄
        private String itemName;  // 상품 명
        private int orderPrice;   // 주문 가격
        private int count;    // 주문 수량

        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();

        }
    }

}
