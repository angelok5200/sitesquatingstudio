package org.tafel.squating.ports.inbound;

import org.tafel.squating.domain.model.Brand;
import org.tafel.squating.domain.value.MonitoringPolicy;

import java.util.List;
import java.util.Optional;

public interface MonitorDomainUseCase {
    Brand registerBrand(String brandName, String primaryDomain, MonitoringPolicy policy);
    Optional<Brand> findBrandById(String id);
    List<Brand> listBrands();
    void updateBrandPolicy(String brandId, MonitoringPolicy policy);
}
