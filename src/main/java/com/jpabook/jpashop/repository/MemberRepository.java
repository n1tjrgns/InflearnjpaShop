package com.jpabook.jpashop.repository;

import com.jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    /*@PersistenceContext
    private EntityManager em;*/
    private final EntityManager em;

    public void save(Member member){
        em.persist(member);
    }

    public Member findOne(Long id){
        return em.find(Member.class, id);
    }

    public List<Member> findAll(){
        //JPQL 작성
        return em.createQuery("select m from Member m", Member.class) //쿼리, 반환 타입
                            .getResultList();
    }

    public List<Member> findByName(String name){
        //JQPL 이름으로 파라미터 바인딩해서 사용하는 방법
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }
}
