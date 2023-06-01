package study.datajpa.repository;

import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor // private final 필드인것을 스프링이 인젝션해줌
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final EntityManager em; // 생성자 이것 하나만 있는거 생성하면 인젝션된다. 아니면 @Persistcontext 해줘야함

    /*
        데이터베이스 커넥션 직접얻어서 네이티브 쿼리 쓰고 싶은경우 등 ..
        여기서 커스텀해서 써라
     */
    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m")
                .getResultList();
    }
}