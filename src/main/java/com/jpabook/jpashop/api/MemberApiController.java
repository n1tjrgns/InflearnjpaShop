package com.jpabook.jpashop.api;

import com.jpabook.jpashop.domain.Member;
import com.jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    //회원 조회 v1 -> 엔티티 직접 노출, @jsonignore 사용시 프레젠테이션 계층을 위한 로직이 추가됨
    @GetMapping("/api/v1/members")
    public List<Member> membersV1(){
        return memberService.findMembers();
    }

    //회원 조회 v2
    //현재  api 구조가 [ ] 안에 들어있기 때문에 유연성이 없어서, 한 번 감싸준다.
    @GetMapping("/api/v2/members")
    public Result memberV2(){
        List<Member> findMembers = memberService.findMembers();
        List<MemberDto> collect = new ArrayList<>();
        for (Member m : findMembers) {
            MemberDto memberDto = new MemberDto(m.getName());
            collect.add(memberDto);
        }

        return new Result(collect);
    }
    
    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto{
        private String name;
    }
    


    //api 같이 데이터 변경이 빈번한 상황에 엔티티를 외부로 노출시키면안됨
    //엔티티 칼럼에 NotEmpty 옵션을 주면 상황에 따라 많이 깨질 수 있어
    //이 부분도 dto 클래스의 필드로 옮겨야해
   @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member){
        Long id = memberService.join(member);

        return new CreateMemberResponse(id);
    }

    //dto를 사용해야함.
    /**
     * 등록 V1: 요청 값으로 Member 엔티티를 직접 받는다.
     * 문제점
     * - 엔티티에 프레젠테이션 계층을 위한 로직이 추가된다.
     * - 엔티티에 API 검증을 위한 로직이 들어간다. (@NotEmpty 등등)
     * - 실무에서는 회원 엔티티를 위한 API가 다양하게 만들어지는데, 한 엔티티에 각각의 API를 위
     한 모든 요청 요구사항을 담기는 어렵다.
     * - 엔티티가 변경되면 API 스펙이 변한다.
     * 결론
     * - API 요청 스펙에 맞추어 별도의 DTO를 파라미터로 받는다.
     */
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request){
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);

    }

    //수정 api
    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(@PathVariable("id") Long id,
                                               @RequestBody @Valid UpdateMemberRequest request){

        memberService.update(id, request.getName());
        //커맨드와 쿼리를 분리
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    @Data
    static class UpdateMemberRequest{
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse{
        private Long id;
        private String name;
    }

    @Data
    static class CreateMemberRequest{

       @NotEmpty
        private String name;
    }

    @Data
    static class CreateMemberResponse{
        private Long id;

        public CreateMemberResponse(Long id){
            this.id = id;
        }
    }
}
