package com.jpabook.jpashop.domain.item;

import com.jpabook.jpashop.domain.Category;
import com.jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // 상속 전략
@DiscriminatorColumn(name = "dtype")
@Getter
//@Setter
public abstract class Item {

    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    //다대다 관계
    @ManyToMany
    @JoinTable(name = "items")
    private List<Category> categories = new ArrayList<>();

    /*비즈니스 로직*/

    //setter를 사용하지 않고 아래처럼 별도의 메소드로 작업을 해야한다.

    //해당 데이터를 가지고 있는 클래스에서 비즈니스 로직이 출발해야 좋다
    // -> 응집력 증가
    //재고 수량 증가
    public void addStock(int quantity){
        this.stockQuantity += quantity;
    }

    //재고 수량 감소
    public void removeStock(int ququantity){
        int restStock = this.stockQuantity - ququantity;
        if(restStock < 0){
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }
}
