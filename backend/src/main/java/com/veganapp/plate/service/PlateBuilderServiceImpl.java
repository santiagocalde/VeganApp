package com.veganapp.plate.service;

import com.veganapp.plate.dto.PlateCalculationResult;
import com.veganapp.plate.dto.PlateOptionResponse;
import com.veganapp.plate.dto.PlateRequest;
import com.veganapp.plate.entity.PlateOption;
import com.veganapp.plate.entity.PlateOption.PlateCategory;
import com.veganapp.plate.repository.PlateOptionRepository;
import com.veganapp.user.entity.User;
import com.veganapp.user.repository.UserRepository;
import com.veganapp.common.exception.AppException;
import com.veganapp.common.exception.ErrorCode;
import com.veganapp.common.config.AppProperties;
import com.veganapp.pippin.domain.PippinContext;
import com.veganapp.pippin.dto.PippinReactionResponse;
import com.veganapp.pippin.service.PippinService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Implementación del servicio de Armado de Platos.
 *
 * FIX #5: Validación exhaustiva
 * 1. Fail Fast: validar IDs existencia y integridad de entrada
 * 2. Validar categoría: asegurar que baseId=BASE, proteinId=PROTEIN, etc.
 * 3. Validación de rango: IDs positivos y razonables
 * 4. Cálculo nutricional: sumar calorías, proteína, grasa
 * 5. Evaluar balance: comparar con umbrales de AppProperties
 * 6. Generar reacción de Pippin: PLATE_BUILT context
 *
 * GRASP: Information Expert — este service conoce los detalles del cálculo nutricional
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PlateBuilderServiceImpl implements PlateBuilderService {

    // Umbrales nutricionales para determinar si el plato es balanceado
    // (pueden ser sobrescrividos desde AppProperties)
    private final PlateOptionRepository plateOptionRepository;
    private final UserRepository userRepository;
    private final PippinService pippinService;
    private final AppProperties appProperties;

    @Override
    @Transactional(readOnly = true)
    public List<PlateOptionResponse> getOptionsByCategory(PlateCategory category) {
        return plateOptionRepository.findByCategoryAndActiveTrue(category)
                .stream()
                .map(this::toOptionResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PlateCalculationResult calculatePlate(PlateRequest request, Long userId) {
        // FIX #5: Fail Fast — validar todo antes de consultar BD
        validatePlateRequest(request);

        // Cargar usuario para contexto de Pippin
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // FIX #5: Fail Fast — cargar opciones y validar categorías
        PlateOption base = findAndValidateOption(request.baseId(), PlateCategory.BASE);
        PlateOption protein = findAndValidateOption(request.proteinId(), PlateCategory.PROTEIN);
        PlateOption vegetable = findAndValidateOption(request.vegetableId(), PlateCategory.VEGETABLE);
        PlateOption sauce = findAndValidateOption(request.sauceId(), PlateCategory.SAUCE);

        // Calcular macros
        int totalCalories = sumCalories(base, protein, vegetable, sauce);
        BigDecimal totalProteinG = sumNutrient(base.getProteinG(), protein.getProteinG(), vegetable.getProteinG(), sauce.getProteinG());
        BigDecimal totalFatG = sumNutrient(base.getFatG(), protein.getFatG(), vegetable.getFatG(), sauce.getFatG());

        // Evaluar balance
        BalanceEvaluation balance = evaluateBalance(totalCalories, totalProteinG, totalFatG);

        // Generar reacción de Pippin
        PippinContext pippinContext = PippinContext.forPlateBuilt(user);
        PippinReactionResponse pippinReaction = pippinService.getReaction(pippinContext);

        log.info("Plate calculated: userId={}, calories={}, protein={}g, fat={}g, balanced={}",
                userId, totalCalories, totalProteinG, totalFatG, balance.isBalanced());

        return new PlateCalculationResult(
                toOptionResponse(base),
                toOptionResponse(protein),
                toOptionResponse(vegetable),
                toOptionResponse(sauce),
                totalCalories,
                totalProteinG,
                totalFatG,
                balance.isBalanced(),
                balance.message(),
                pippinReaction
        );
    }

    // ────────────────────────────────────────────────────────────────────────────
    // PRIVADOS
    // ────────────────────────────────────────────────────────────────────────────

    /**
     * FIX #5: Validación de request antes de consultas BD
     */
    private void validatePlateRequest(PlateRequest request) {
        if (request.baseId() == null || request.baseId() <= 0) {
            throw new AppException(ErrorCode.VALIDATION_ERROR, "Base ID debe ser válido");
        }
        if (request.proteinId() == null || request.proteinId() <= 0) {
            throw new AppException(ErrorCode.VALIDATION_ERROR, "Protein ID debe ser válido");
        }
        if (request.vegetableId() == null || request.vegetableId() <= 0) {
            throw new AppException(ErrorCode.VALIDATION_ERROR, "Vegetable ID debe ser válido");
        }
        if (request.sauceId() == null || request.sauceId() <= 0) {
            throw new AppException(ErrorCode.VALIDATION_ERROR, "Sauce ID debe ser válido");
        }
    }

    /**
     * FIX #5: Obtener y validar categoría de la opción
     * Asegura que baseId realmente pertenece a BASE, proteinId a PROTEIN, etc.
     */
    private PlateOption findAndValidateOption(Long id, PlateCategory expectedCategory) {
        PlateOption option = plateOptionRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new AppException(
                    ErrorCode.VALIDATION_ERROR,
                    "Plate option not found: id=" + id
                ));

        // FIX #5: Validar categoría
        if (!option.getCategory().equals(expectedCategory)) {
            throw new AppException(
                ErrorCode.VALIDATION_ERROR,
                "Invalid category for option id=" + id + ". Expected: " + expectedCategory + ", Found:" + option.getCategory()
            );
        }

        return option;
    }

    /**
     * Suma calorías de las 4 opciones
     */
    private int sumCalories(PlateOption... options) {
        int total = 0;
        for (PlateOption option : options) {
            if (option.getCalories() != null) {
                total += option.getCalories();
            }
        }
        return total;
    }

    /**
     * Suma nutrientes (proteína, grasa) evitando nulls
     */
    private BigDecimal sumNutrient(BigDecimal... values) {
        BigDecimal total = BigDecimal.ZERO;
        for (BigDecimal value : values) {
            if (value != null) {
                total = total.add(value);
            }
        }
        return total.setScale(1, RoundingMode.HALF_UP);
    }

    /**
     * Evalúa si el plato es nutricionalmente balanceado.
     * Los umbrales vienen de AppProperties.
     */
    private BalanceEvaluation evaluateBalance(int calories, BigDecimal protein, BigDecimal fat) {
        AppProperties.Plate plateConfig = appProperties.plate();
        
        StringBuilder message = new StringBuilder();
        boolean balanced = true;

        if (calories < plateConfig.minCalories()) {
            message.append(String.format("Pocas calorías (%d, mínimo %d). ", 
                    calories, plateConfig.minCalories()));
            balanced = false;
        } else if (calories > plateConfig.maxCalories()) {
            message.append(String.format("Muchas calorías (%d, máximo %d). ", 
                    calories, plateConfig.maxCalories()));
            balanced = false;
        }

        if (protein.compareTo(BigDecimal.valueOf(plateConfig.minProteinG())) < 0) {
            message.append(String.format("Poca proteína (%.1fg, mínimo %dg). ", 
                    protein, plateConfig.minProteinG()));
            balanced = false;
        }

        if (fat.compareTo(BigDecimal.valueOf(plateConfig.maxFatG())) > 0) {
            message.append(String.format("Mucha grasa (%.1fg, máximo %dg). ", 
                    fat, plateConfig.maxFatG()));
            balanced = false;
        }

        if (balanced) {
            message.append("¡Plato balanceado y saludable!");
        }

        return new BalanceEvaluation(balanced, message.toString());
    }

    /**
     * Convierte PlateOption entity a DTO
     */
    private PlateOptionResponse toOptionResponse(PlateOption option) {
        return new PlateOptionResponse(
                option.getId(),
                option.getCategory(),
                option.getName(),
                option.getCalories(),
                option.getProteinG(),
                option.getFatG(),
                option.getIconUrl()
        );
    }

    /**
     * Value Object privado para el resultado de la evaluación de balance
     */
    private record BalanceEvaluation(boolean isBalanced, String message) {}
}
