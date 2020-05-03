package com.jpabook.jpashop.api;

import com.jpabook.jpashop.domain.Address;
import com.jpabook.jpashop.domain.Order;
import com.jpabook.jpashop.domain.OrderItem;
import com.jpabook.jpashop.domain.OrderStatus;
import com.jpabook.jpashop.repository.OrderRepository;
import com.jpabook.jpashop.repository.OrderSearch;
import com.jpabook.jpashop.repository.order.query.OrderQueryDto;
import com.jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController {
    
    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;
    
    @GetMapping("/api/v1/orders")
    public List<Order> orderV1(){
        List<Order> all = orderRepository.findAllByCriteria(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();

            //강제 초기화
            List<OrderItem> orderItems = order.getOrderItems();
            for (OrderItem orderItem : orderItems) {
                orderItems.stream().forEach(o ->orderItem.getItem().getName());
            }
        }
            return all;
    }

    //V2 -> DTO로 변환
    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2(){
        List<Order> orders = orderRepository.findAllByCriteria(new OrderSearch());

        List<OrderDto> collect = new ArrayList<>();
        for (Order o : orders) {
            OrderDto orderDto = new OrderDto(o);
            collect.add(orderDto);
        }

        return collect;
    }

    @Getter
    static class OrderDto{

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        //래핑된 엔티티가 존재하니까 안된다.
        //private List<OrderItem> orderItems; //추가된 부분
        private List<OrderItemDto> orderItems; // 이렇게 수정

        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName(); //LAZY 초기화
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); //LAZY 초기화
            //orderitems가 출력되지않음, 프록시 초기화를 해준 후 돌리자
            //order.getOrderItems().stream().forEach(o -> o.getItem().getName());
            //orderItems = order.getOrderItems();
            orderItems = order.getOrderItems().stream()
                    .map(orderItem -> new OrderItemDto(orderItem))
                    .collect(Collectors.toList());
        }

        @Getter
        static class OrderItemDto{

            private String itemName; //상품 명
            private int orderPrice; //주문 가격
            private int count; //주문 수량

            public OrderItemDto(OrderItem orderItem) {
                itemName = orderItem.getItem().getName();
                orderPrice = orderItem.getOrderPrice();
                count = orderItem.getCount();
            }
        }
    }

    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3(){
        List<Order> orders = orderRepository.findAllWithItem();

        List<OrderDto> result = new ArrayList<>();
        for (Order o : orders) {
            OrderDto orderDto = new OrderDto(o);
            result.add(orderDto);
        }

        return result;
    }

    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "100") int limit){
        //member와 delivery는 fetchjoin으로 한 번에 가져왔지만
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);

        //다시 아이템을 가져올 때는 order_id 만큼 가져오기 때문에 이 부분에서 반복 쿼리가 발생함
        List<OrderDto> result = new ArrayList<>();
        for (Order o : orders) {
            OrderDto orderDto = new OrderDto(o);
            result.add(orderDto);
        }

        return result;
    }

    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4(){
        return orderQueryRepository.findOrderQueryDtos();
    }
}
