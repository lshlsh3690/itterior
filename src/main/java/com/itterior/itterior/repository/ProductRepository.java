package com.itterior.itterior.repository;


import com.itterior.itterior.entity.Product;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @EntityGraph(attributePaths = "imageList")
    @Query("select p from Product p where p.pno=:pno")
    Optional<Product> selectOne(@Param("pno") Long pno);
    @Query("select p, pi from Product p join p.imageList pi  where pi.ord = 0 and p.delFlag = false and p.categoryNum = :categoryNum ")
    Page<Object[]> selectList(Pageable pageable, Long categoryNum);
    @Query("select p, pi from Product p left outer join fetch p.imageList pi  where pi.ord = 0 and p.delFlag = false and p.seller.userName = :username ")
    Page<Object[]> selectListWithUsername(Pageable pageable, String username);
    @Modifying
    @Query("update Product p set p.delFlag = :flag where p.pno = :pno")
    void updateToDelete(@Param("pno") Long pno, @Param("flag") boolean flag);

    @Transactional
    @Modifying
    @Query("UPDATE Product p SET p.viewCount = p.viewCount + 1 WHERE p.pno = :pno")
    void incrementProductViewCount(@Param("pno") Long pno);

    @Query("select p from Product p LEFT JOIN FETCH p.imageList order by p.viewCount desc")
    List<Product> findTop10ByOrderByViewCountDesc(Pageable pageable);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.imageList WHERE p.pname LIKE %:query%")
    List<Product> searchWithQuery(String query);

    @Query("select p from Product p where p.pno > :pno order by p.pno asc")
    Page<Object[]>findByPnoGreaterThanOOrderByPnoAsc(Pageable pageable, Long pno);
}
