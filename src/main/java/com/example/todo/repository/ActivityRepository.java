package com.example.todo.repository;

import com.example.todo.entity.ActivityEntry;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<ActivityEntry, Long> {

    /** Most recent activity entries, newest first. */
    List<ActivityEntry> findByOrderByIdDesc(Limit limit);
}
