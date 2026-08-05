package com.devsuperior.dscatalog.services;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dtos.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DataBaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoudException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ProductService {

	@Autowired
	private ProductRepository repository;

	@Transactional(readOnly = true)
	public List<ProductDTO> findAll() {
		List<Product> list = repository.findAll();
		List<ProductDTO> dtos = list.stream().map(prod -> new ProductDTO(prod)).toList();
		return dtos;
	}

	@Transactional(readOnly = true)
	public Page<ProductDTO> findAllPaged(PageRequest pageRequest) {
		Page<Product> list = repository.findAll(pageRequest);
		Page<ProductDTO> dtos = list.map(prod -> (new ProductDTO(prod, prod.getCategories())));
		return dtos;
	}

	@Transactional(readOnly = true)
	public ProductDTO findById(Long id) {
		Optional<Product> obj = repository.findById(id);
		Product entity = obj.orElseThrow(() -> new ResourceNotFoudException("Entity not found!"));
		ProductDTO dto = new ProductDTO(entity, entity.getCategories());
		return dto;

	}

	@Transactional(readOnly = false)
	public ProductDTO insert(ProductDTO dto) {
		Product entity = repository.save(dto.dtoToEntity());
		Set<Category> categories = entity.getCategories();
		return new ProductDTO(entity, categories);
	}

	@Transactional(readOnly = false)
	public ProductDTO update(Long id, ProductDTO dto) {
		try {
			Product entity = repository.getReferenceById(id);
			dto.setId(id);
			entity = repository.save(dto.dtoToEntity());
			Set<Category> categories = entity.getCategories();
			return new ProductDTO(entity, categories);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoudException("Id not found: " + id);
		}
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	public void deleteById(Long id) {
		if (!repository.existsById(id)) {
			throw new ResourceNotFoudException("Id not found: " + id);
		}
		try {
			repository.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new DataBaseException("Falha de integridade referencial.");
		}
	}

}
