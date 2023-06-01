package study.datajpa.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.print.attribute.standard.PageRanges;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext
    EntityManager entityManager;

    @Test
    public void testMember(){
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        //단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get(); // Optional은 value를 그대로반환
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        org.assertj.core.api.Assertions.assertThat(findMember1).isEqualTo(member1);
        org.assertj.core.api.Assertions.assertThat(findMember2).isEqualTo(member2);

        //리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        org.assertj.core.api.Assertions.assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        long count = memberRepository.count();
        org.assertj.core.api.Assertions.assertThat(count).isEqualTo(2);

        //삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        org.assertj.core.api.Assertions.assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThen(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        org.assertj.core.api.Assertions.assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        org.assertj.core.api.Assertions.assertThat(result.get(0).getAge()).isEqualTo(20);
    }

    @Test
    public void testNamedQuery(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        org.assertj.core.api.Assertions.assertThat(findMember).isEqualTo(m1);
    }

    @Test
    public void testQuery(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    public void findUsernameList(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        Member m3 = new Member("AAA", 10);
        Member m4 = new Member("AAA", 10);
        memberRepository.save(m1);
        memberRepository.save(m2);
        memberRepository.save(m3);
        memberRepository.save(m4);

        List<String> result = memberRepository.findUsernameList();
        assertThat(result.size()).isEqualTo(4);
    }

    @Test
    public void findMemberDto(){
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA", 10);
        m1.setTeam(team);
        memberRepository.save(m1);

        List<MemberDto> result = memberRepository.findMemberDto();
        for(MemberDto dto : result){
            System.out.println("dto = " +  dto);
        }
    }

    @Test
    public void findByNames(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        Member m3 = new Member("AAA", 10);
        Member m4 = new Member("AAA", 10);
        memberRepository.save(m1);
        memberRepository.save(m2);
        memberRepository.save(m3);
        memberRepository.save(m4);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        for(Member member : result ){
            System.out.println("mmm : " + member);
        }
    }

    @Test
    public void returnTypeTest(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        Optional<Member> findMember = memberRepository.findOptionalByUsername("AAA");
        // db에서 데이터조회시 데이터 있을수도있고 없을수도 있으면 optional로 조회해라
        // List는 빈컬렉션을 반환해주고 if(list != null) => 이건 매우 안좋은 코드다.
        // 단건인경우 스프링데이터JPA는 지가 trycatch해서 null이나오고 순수jpa라면 NoResult익셉션을 발생시킨다.

        System.out.println("findMember = " + findMember);
    }

    @Test
    public void paging(){
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username")); //  0페이지부터 시작함
            // PageRequest의 부모가 Pageable이다.

        //when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        Page<MemberDto> toMap = page.map(m -> new MemberDto(m.getId(), m.getUsername(), null));

        //then
        List<Member> content = page.getContent(); // 데이터 꺼내기
        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue(); // 첫번째페이지냐
        assertThat(page.hasNext()).isTrue();
    }

    @Test
    public void bulkUpdate(){
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        int resultCount = memberRepository.bulkAgePlus(20);
//        entityManager.flush();
//        entityManager.clear();

        List<Member> result = memberRepository.findByUsername("member5");
        Member member5 = result.get(0);
        System.out.println("member5 = " + member5);
        /*
            벌크연산을 하면 영속성컨텍스트를 무시하고 db를 업데이트 치기 때문에
            db는 업데이트가 되지만 영속성 컨텍스트에는 반영이 안되있다.
            그래서 위에서 같은 트랜잭션 안에서 벌크연산후 가져올때는 영속성컨텍스트에 member5가 남아있기때문에
            db에서 안가져오고 영속성컨텍스트에서 가져와 age가 40인거다.

            그래서 항상 벌크연산 이후에는 최신화된 값을 가져오기위해
            flush와 clear로 영속성컨텍스트를 날려줘야한다.

            허나 spring data jpa 에서는 auto clear 기능이 있어서 안해줘도 된다.
         */


        org.assertj.core.api.Assertions.assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void findMemberLazy(){
        // given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        Member member1 = new Member("member1",10, teamA);
        Member member2 = new Member("member2",10, teamB);
        member1.setTeam(teamA);
        member2.setTeam(teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        entityManager.flush();
        entityManager.clear();// 영속성컨텍스트 클리어

        //when
        List<Member> members = memberRepository.findEntityGraphByUsername("member1");

        for(Member member : members){
            System.out.println("member : " + member.getUsername());
            System.out.println("member.team : " + member.getTeam().getName());
        }
    }

    @Test
    public void queryHint(){
        //given
        Member member1 = memberRepository.save(new Member("member1", 10));
        entityManager.flush();
        entityManager.clear();
        //when
        Member findMember = memberRepository.findById(member1.getId()).get();
        findMember.setUsername("member2"); // 변경감지

        entityManager.flush(); // 변경감지된거 반영
        /*
            변경감지의 단점은 '원본'이 있어야한다.
            객체를 두개이상 가지고 있어야 되서 메모리 비용이 더 든다.

         */
    }

    @Test
    public void queryHint1(){
        //given
        Member member1 = memberRepository.save(new Member("member1", 10));
        entityManager.flush();
        entityManager.clear();
        //when
        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        findMember.setUsername("member2"); // findMember가 readOnly라 수정안됨

        entityManager.flush();
    }

    @Test
    public void lock() {
        //given
        Member member1 = memberRepository.save(new Member("member1", 10));
        entityManager.flush();
        entityManager.clear();
        //when
        Member findMember = memberRepository.findLockByUsername("member1");
        findMember.setUsername("member2");

        entityManager.flush();
    }

    @Test
    public void callCustom(){
        List<Member> result = memberRepository.findMemberCustom();
    }

    @Test
    public void specBasic(){
        // given
        Team teamA = new Team("teamA");
        entityManager.persist(teamA);

        Member m1 = new Member("m1", 0 , teamA);
        Member m2 = new Member("m2", 0, teamA);
        entityManager.persist(m1);
        entityManager.persist(m2);

        entityManager.flush();
        entityManager.clear();

        //when
        Specification<Member> spec = MemberSpec.username("m1").and(MemberSpec.teamName("teamA"));
        List<Member> result = memberRepository.findAll(spec);

        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void queryByExample(){
        //given
        Team teamA = new Team("teamA");
        entityManager.persist(teamA);

        Member m1 = new Member("m1", 0 , teamA);
        Member m2 = new Member("m2", 0, teamA);
        entityManager.persist(m1);
        entityManager.persist(m2);

        entityManager.flush();
        entityManager.clear();

        //when
        //Probe
        Member member = new Member("m1"); // 엔티티를 가지고 검색조건으로 만들어버림
        Team team = new Team("teamA");
        member.setTeam(team);

        ExampleMatcher matcher = ExampleMatcher.matching().withIgnorePaths("age"); // 값 null이거나 0인거 조건에서 제외 시킴
        Example<Member> example = Example.of(member, matcher);// 저장용도가 아닌 뭔가 찾을때 엔티티객체로 찾아버림

        List<Member> result = memberRepository.findAll(example);

        assertThat(result.get(0).getUsername()).isEqualTo("m1");

    }

    @Test
    public void projections(){
        //given
        Team teamA = new Team("teamA");
        entityManager.persist(teamA);

        Member m1 = new Member("m1", 0 , teamA);
        Member m2 = new Member("m2", 0, teamA);
        entityManager.persist(m1);
        entityManager.persist(m2);

        entityManager.flush();
        entityManager.clear();

        //when
        List<NestedClosedProjections> result = memberRepository.findProjectionsByUsername("m1", NestedClosedProjections.class);

        for(NestedClosedProjections nestedClosedProjections : result){
            System.out.println("usernameOnly = " + nestedClosedProjections.getUsername());
        }
    }

    @Test
    public void natevieQuery(){
        //given
        Team teamA = new Team("teamA");
        entityManager.persist(teamA);

        Member m1 = new Member("m1", 0 , teamA);
        Member m2 = new Member("m2", 0, teamA);
        entityManager.persist(m1);
        entityManager.persist(m2);

        entityManager.flush();
        entityManager.clear();

        //when
        Page<MemberProjection> result = memberRepository.findByNativeProjection(PageRequest.of(0, 10));
        List<MemberProjection> content = result.getContent();
        for(MemberProjection memberProjection : content) {
            System.out.println("dd :" + memberProjection.getUsername());
            System.out.println("ss :" + memberProjection.getTeamname());
        }
    }
}