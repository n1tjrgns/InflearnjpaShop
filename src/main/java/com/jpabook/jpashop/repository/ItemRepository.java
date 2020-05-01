package com.jpabook.jpashop.repository;

import com.jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    //jpa에 저장하기전까지 item의 id는 없기 때문에 save와 update를 구분함
    public void save(Item item){
        if(item.getId() == null){
            em.persist(item);
        }else{
            //이걸 그래서 merge말고 dirty checking해서 사용하도록 해야한다.
            em.merge(item);
        }
    }

    public Item findOne(Long id){
        return em.find(Item.class, id);
    }

    public List<Item> findAll(){
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }
}
