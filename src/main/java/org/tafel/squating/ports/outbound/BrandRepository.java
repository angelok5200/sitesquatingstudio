package org.tafel.squating.ports.outbound;

import org.tafel.squating.domain.model.Brand;

import java.util.List;
import java.util.Optional;

public interface BrandRepository {
    Brand save(Brand brand);
    Optional<Brand> findById(String id);
    Optional<Brand> findByPrimaryDomain(String domain);
    List<Brand> findAll();
    void deleteById(String id);
}
