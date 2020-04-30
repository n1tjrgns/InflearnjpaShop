package com.jpabook.jpashop.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable
@Getter
public class Address { //값 타입의 클래스는 immutable하게 설계되어야함

    private String city;
    private String street;
    private String zipcode;

    //new 로 새로 만들어서 값 타입 변경을 방지하기 위해서 jpa 스펙상 protected까지 가능
    protected Address() {
    }

    public Address(String city, String street, String zipcode){
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
