package study.inflearnquerydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.inflearnquerydsl.entity.Member;
import study.inflearnquerydsl.entity.QMember;
import study.inflearnquerydsl.entity.Team;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
public class QuerydslBasicTest {

	@Autowired
	EntityManager em;

	JPAQueryFactory queryFactory;

	@BeforeEach
	void setUp() {
		queryFactory = new JPAQueryFactory(em);

		Team teamA = new Team("teamA");
		Team teamB = new Team("teamB");

		em.persist(teamA);
		em.persist(teamB);

		Member member1 = new Member("member1", 10, teamA);
		Member member2 = new Member("member2", 20, teamA);

		Member member3 = new Member("member3", 30, teamB);
		Member member4 = new Member("member4", 40, teamB);

		em.persist(member1);
		em.persist(member2);
		em.persist(member3);
		em.persist(member4);

		em.flush();
		em.clear();

		List<Member> members = em.createQuery(
				"select m from Member m",
				Member.class
		).getResultList();

		members.forEach(member -> {
			System.out.println("member.getUsername() = " + member.getUsername());
			System.out.println("member.getTeam() = " + member.getTeam());
		});
	}

	@Test
	void startJPQL() {
		// member1을 찾아라.

		String ql = "select m from Member m where m.username = :username";
		Member findMember = em.createQuery(ql, Member.class)
				.setParameter("username", "member1")
				.getSingleResult();

		assertThat(findMember.getUsername()).isEqualTo("member1");
	}

	@Test
	void startQuerydsl() {
		QMember m = new QMember("m");

		Member findMember = queryFactory
				.select(m)
				.from(m)
				.where(m.username.eq("member1")) // 파라미터 바인딩 처리
				.fetchOne();

		assertThat(findMember.getUsername()).isEqualTo("member1");
	}
}
