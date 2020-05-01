package com.jpabook.jpashop.controller;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//상품을 뷰에 전달하기 위한 클래스
public class BookForm {

    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    private String author;
    private String isbn;
}
