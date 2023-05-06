package com.ssp.platform.service;

import com.ssp.platform.entity.Purchase;
import com.ssp.platform.exceptions.*;
import com.ssp.platform.request.PurchasesPageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

public interface PurchaseService {

	Purchase save(Purchase purchase);

	Purchase get(UUID id);

	Page<Purchase> getAll(PurchasesPageRequest purchasesPageRequest);

	boolean deletePurchase(Purchase purchase) throws IOException, FileServiceException, SupplyServiceException;

	Optional<Purchase> findById(UUID id);

	void sendEmail(Purchase purchase);
}
