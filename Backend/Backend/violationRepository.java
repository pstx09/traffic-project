package com.example.traffic.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.traffic.model.violation;

public interface violationRepository extends JpaRepository<violation, Long> {

    List<violation> findByVehicleNumber(String vehicleNumber);

    List<violation> findByVehicleNumberContaining(String keyword);

    List<violation> findByPaidFalse();

}