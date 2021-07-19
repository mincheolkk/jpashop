package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm(Model model) {
        model.addAttribute("memberForm", new MemberForm());
        // controller에서 뷰로 넘어갈때 데이터를 실어서 넘김
        // 화면을 만들때 MemberForm 이라는 빈 껍데기 멤버객체를 가지고 감
        // 간단한 밸리데이션 등의 이점이 있음
        return "members/createMemberForm";
    }

    @PostMapping("/members/new")
    public String create(@Valid MemberForm form, BindingResult result) {

        if (result.hasErrors()) {
            return "members/createMemberForm";
        }

        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());

        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);

        memberService.join(member);
        return "redirect:/";
    }

    @GetMapping("/members")
    public String list(Model model) {
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);
        // memberService.findMembers() 로 JPA에서 멤버들을 가져오고, model에 담아 화면에 넘김
        // 한 줄로 합친 코드 -> model.addAttribute("members", memberService.findMembers());

        // 멤버 Entity 를 그대로 뿌리기 보다는, DTO 로 변환해서 화면에 꼭 필요한 데이터들만 출력하는 것을 권장
        // 템플릿 엔진에서 랜더링해서 화면에 보낼때는, 서버에서 내가 원하는 부분만 내보내기에 Entity를 써도 괜찮음
        // But, Api를 만들때는 절때 Entity 를 반환하면 안 됨. (왜냐면, Entity 를 변경하면 API 스펙이 변경되고 불안전해짐)

        return "members/memberList";
    }
}
