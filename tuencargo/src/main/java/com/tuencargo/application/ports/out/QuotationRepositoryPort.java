package com.tuencargo.application.ports.out;

import com.tuencargo.domain.model.Quotation;

import java.util.List;
import java.util.Optional;

/** Puerto de salida: persistencia de cotizaciones. */
public interface QuotationRepositoryPort {

    Quotation save(Quotation quotation);

    Optional<Quotation> findById(Long id);

    List<Quotation> findByUserId(Long userId);
}
