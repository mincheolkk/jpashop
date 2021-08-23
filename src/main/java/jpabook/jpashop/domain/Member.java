package jpabook.jpashop.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Embedded  //내장 타입
    private Address address;

    @JsonIgnore // 양방향 연관관계에 있으면 한쪽은 JsonIgnore 해줘야 함.
    @OneToMany(mappedBy = "member") //  order 테이블에 있는 member 필드에 의해 매핑됐다는 의미
    private List<Order> orders = new ArrayList<>();

}
