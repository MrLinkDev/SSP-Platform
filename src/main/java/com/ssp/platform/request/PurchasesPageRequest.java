package com.ssp.platform.request;

import com.ssp.platform.entity.enums.PurchaseStatus;
import lombok.Data;

/**
 * Промежуточная сущность с данными запроса по получению списка закупки
 * @author Василий Воробьев
 */
@Data
public class PurchasesPageRequest {

	private Integer requestPage = 0;
	private Integer numberOfElements = 10;
	private String filterName;
	private PurchaseStatus filterStatus;

	public PurchasesPageRequest(Integer requestPage, Integer numberOfElements, String filterName, PurchaseStatus filterStatus)
	{
		this.requestPage = requestPage;
		this.numberOfElements = numberOfElements;
		this.filterName = filterName;
		this.filterStatus = filterStatus;
	}
}
