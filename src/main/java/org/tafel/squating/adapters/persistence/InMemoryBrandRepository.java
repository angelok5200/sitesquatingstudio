package org.tafel.squating.adapters.persistence;

import org.springframework.stereotype.Repository;
import org.tafel.squating.domain.model.Brand;
import org.tafel.squating.ports.outbound.BrandRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryBrandRepository implements BrandRepository {

    private final Map<String, Brand> brands = new ConcurrentHashMap<>();

    @Override
    public Brand save(Brand brand) {
        brands.put(brand.getId(), brand);
        return brand;
    }

    @Override
    public Optional<Brand> findById(String id) {
        return Optional.ofNullable(brands.get(id));
    }

    @Override
    public Optional<Brand> findByPrimaryDomain(String domain) {
        return brands.values().stream()
            .filter(b -> b.getPrimaryDomain().equalsIgnoreCase(domain))
            .findFirst();
    }

    @Override
    public List<Brand> findAll() {
        return new ArrayList<>(brands.values());
    }

    @Override
    public void deleteById(String id) {
        brands.remove(id);
    }
}
