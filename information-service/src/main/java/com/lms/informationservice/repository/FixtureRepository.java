package com.lms.informationservice.repository;

import com.lms.informationservice.fixture.Fixture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FixtureRepository extends JpaRepository<Fixture, Integer> {
}
