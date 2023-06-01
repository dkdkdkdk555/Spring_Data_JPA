package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

import javax.annotation.PostConstruct;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id){
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    @GetMapping("/members2/{id}")
    public String findMember2(@PathVariable("id") Member member){ // 부트에서 id가지고 Member객체 가져옴
        return member.getUsername();
    }

    @GetMapping("/members")
    public Page<Member> list(Pageable pageable){
        return memberRepository.findAll(pageable);
    }

    @GetMapping("/members1")
    public Page<Member> list1(@PageableDefault(size = 5) Pageable pageable){
        return memberRepository.findAll(pageable);
    }

    @GetMapping("/members2")
    public Page<MemberDto> list2(Pageable pageable){
        Page<Member> page =  memberRepository.findAll(pageable);
        return page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));
    }


//    @PostConstruct
    public void init(){
        for(int i=0;i<100;i++){
            memberRepository.save(new Member("user" + i, i));
        }
    }
}
