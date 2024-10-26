package com.lms.informationservice.repository;

import com.lms.informationservice.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Integer> {
}
