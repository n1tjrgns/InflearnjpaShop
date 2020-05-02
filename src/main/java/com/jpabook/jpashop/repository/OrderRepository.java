package com.jpabook.jpashop.repository;

import com.jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order){
        em.persist(order);
    }

    public Order findOne(Long id){
        return em.find(Order.class, id);
    }

    /*public List<Order> findAll(OrderSearch orderSearch){

        //그런데 동적 쿼리니까 조건이 아무것도 없고 전체가 다 출력되어야하는 형태라면?
        String jpql = "select o from Order o join o.member m";
        //위의 기본 쿼리에 대한 처리를 추가적으로 해줘야하는데 매우 복잡하다.

        //order를 조회후 order와 member를 조인 -> JPQL 작성 방법 확인
        return em.createQuery("select o from Order o join o.member m" +
                 " where o.status = :status " +
                 " and m.name like :name", Order.class)
                .setParameter("status", orderSearch.getOrderStatus())
                .setParameter("name", orderSearch.getMemberName())
                //.setFirstResult(100) 페이징 처리 할 경우
                .setMaxResults(1000) //row 제한 최대 1000건
                .getResultList();
    }*/


    //JPA Criteria
    public List<Order> findAllByCriteria(OrderSearch orderSearch){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Object, Object> m = o.join("member", JoinType.INNER);

        List<Predicate> criteria = new ArrayList<>();

        //주문 상태 검색
        if(orderSearch.getOrderStatus() != null){
            Predicate name = cb.like(m.<String>get("name"), "%" + orderSearch.getMemberName() + "%");
            criteria.add(name);
        }

        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000);
        return query.getResultList();
    }

    //v3 한 번에 필요한 쿼리를 fetch join으로 다 가져옴.
    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery(
                "select o from Order o" +
                        " join fetch o.member" +
                        " join fetch o.delivery", Order.class
        ).getResultList();
    }

    //v4 반환 클래스를 새로 생성한 DTO클래스로 해줌
    //하지만 Order를 매핑해야하는데 DTO 클래스를 매핑 할 수가 없어
    // 엔티티랑, 임베더블 클래스만 가능해
    // 매핑 해주려면 new 를 사용해줘야함
    public List<OrderSimpleQueryDto> findOrderDtos() {
        return em.createQuery(
                "select new com.jpabook.jpashop.repository.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                " from Order o" +
                " join o.member m" +
                " join o.delivery d", OrderSimpleQueryDto.class)
                .getResultList();
    }
}
