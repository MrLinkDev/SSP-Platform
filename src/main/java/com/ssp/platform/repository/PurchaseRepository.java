package com.ssp.platform.repository;

import com.ssp.platform.entity.Purchase;
import com.ssp.platform.entity.User;
import com.ssp.platform.entity.enums.PurchaseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Репозиторий для работы с сущностью закупки
 * @author Изначальный автор Рыжков Дмитрий, доработал Василий Воробьев
 */
@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, UUID>, JpaSpecificationExecutor<Purchase> {
    boolean existsById(UUID id);
    List<Purchase> findByStatusOrStatus(PurchaseStatus purchaseStatus, PurchaseStatus purchaseStatus2);
    Page<Purchase> findAllByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Purchase> findAllByStatus(PurchaseStatus status, Pageable pageable);
    Page<Purchase> findAllByNameContainingIgnoreCaseAndStatus(String name, PurchaseStatus status, Pageable pageable);
}
