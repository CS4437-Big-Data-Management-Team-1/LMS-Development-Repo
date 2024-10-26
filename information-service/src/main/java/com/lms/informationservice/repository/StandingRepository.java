package com.lms.informationservice.repository;

import com.lms.informationservice.standing.Standing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StandingRepository extends JpaRepository<Standing, Integer> {
}
