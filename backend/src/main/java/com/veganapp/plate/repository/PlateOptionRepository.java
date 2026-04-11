package com.veganapp.plate.repository;

import com.veganapp.plate.entity.PlateOption;
import com.veganapp.plate.entity.PlateOption.PlateCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlateOptionRepository extends JpaRepository<PlateOption, Long> {

    /**
     * Obtiene todas las opciones activas de una categoría
     */
    List<PlateOption> findByCategoryAndActiveTrue(PlateCategory category);

    /**
     * Obtiene una opción activa por ID
     */
    Optional<PlateOption> findByIdAndActiveTrue(Long id);
}
