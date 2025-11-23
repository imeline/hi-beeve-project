package beeve.biz.fitness.repository

import beeve.biz.fitness.entity.RankStandard
import beeve.biz.member.enum.Gender
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface RankStandardRepository : JpaRepository<RankStandard, Long> {

    @Query(
        """
        select r
        from RankStandard r
        where r.gender = :gender
          and :age between r.ageMin and r.ageMax
          and r.deletedYn = 'N'
        """
    )
    fun findByGenderAndAge(
        @Param("gender") gender: Gender,
        @Param("age") age: Int,
    ): List<RankStandard>
}