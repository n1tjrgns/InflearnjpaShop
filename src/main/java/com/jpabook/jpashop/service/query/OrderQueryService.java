package com.jpabook.jpashop.service.query;

import com.jpabook.jpashop.domain.Order;
import com.jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderQueryService {  //OSIV false 예제

    private final OrderRepository orderRepository;
    //DTO도 별도로 분리해서 다 들고와야한다.
    public List<OrderDto> ordersV3_Osiv() {
        List<Order> orders = orderRepository.findAllWithItem();

        List<OrderDto> result = new ArrayList<>();
        for (Order o : orders) {
            OrderDto orderDto = new OrderDto(o);
            result.add(orderDto);
        }

        return result;
    }
}
