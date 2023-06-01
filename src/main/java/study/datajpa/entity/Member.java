package study.datajpa.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "username", "age"})
@NamedQuery(
        name = "Member.findByUsername",
        query = "select m from Member m where m.username = :username"
)
@NamedEntityGraph(name = "Member.all", attributeNodes = @NamedAttributeNode("team"))
public class Member {

    @Id @GeneratedValue
    @Column(name="member_id")
    private Long id;

    private String username;

    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="team_id")
    private Team team;

//    protected Member(){
//        // 엔티티는 기본생성자가 하나 있어야된다.(도큐먼트상에서 무조건)
//        // 그리고 protected 이상이어야한다. 왜냐면 jpa에서 프록시 만들때 사용해서..
//    } => @NoArgsConstructor 로 대체

    public Member(String username) {
        this.username = username;
    }

    public Member(String username, int i, Team teamA) {
        this.username = username;
        this.age = i;
        if(team != null){
            chageTeam(teamA);
        }
    }

    public Member(String username, int age) {
        this.username = username;
        this.age = age;
    }

    // 연관관계 편의 메소드
    public void chageTeam(Team team){
        this.team = team;
        team.getMembers().add(this);
    }
}
