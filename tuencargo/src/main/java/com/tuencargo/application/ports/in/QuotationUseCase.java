package com.tuencargo.application.ports.in;

import com.tuencargo.domain.model.PriceOption;
import com.tuencargo.domain.model.Quotation;

import java.util.List;

/** Caso de uso: cotizar un producto desde su link y comparar precios entre tiendas. */
public interface QuotationUseCase {

    Quotation create(Long userId, String productUrl);

    Quotation get(Long quotationId);

    List<Quotation> listByUser(Long userId);

    List<PriceOption> compare(Long quotationId);
}
