
package com.co.repository;

import com.co.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface AdminRepository extends JpaRepository<Admin, String> {

    // 🔥 REAL COLLISION QUERY (EDIT TABLE NAME IF NEEDED)
    @Query(value = """
        SELECT room, exam_date, time_slot, COUNT(*) as count
        FROM exam_schedule
        GROUP BY room, exam_date, time_slot
        HAVING COUNT(*) > 1
        """, nativeQuery = true)
    List<Map<String, Object>> findCollisions();
}