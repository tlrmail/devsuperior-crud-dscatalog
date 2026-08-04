package com.devsuperior.dscatalog.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dtos.CategoryDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoudException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository repository;

	@Transactional(readOnly = true)
	public List<CategoryDTO> findAll() {
		List<Category> list = repository.findAll();
		List<CategoryDTO> dtos = list.stream().map(category -> new CategoryDTO(category)).toList();
		return dtos;
	}

	@Transactional(readOnly = true)
	public CategoryDTO findById(Long id) {
		Optional<Category> obj = repository.findById(id);
		Category entity = obj.orElseThrow(() -> new ResourceNotFoudException("Entity not found!"));
		CategoryDTO dto = new CategoryDTO(entity);
		return dto;
	}

	@Transactional(readOnly = false)
	public CategoryDTO insert(CategoryDTO dto) {
		Category entity = repository.save(dto.dtoToEntity());
		return new CategoryDTO(entity);
	}

	@Transactional(readOnly = false)
	public CategoryDTO update(CategoryDTO dto, Long id) {
		try {
			Category entity = repository.getReferenceById(id);
			entity.setName(dto.getName());
			entity = repository.save(entity);
			return new CategoryDTO(entity);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoudException("Id not found: " + id);
		}
	}
}
