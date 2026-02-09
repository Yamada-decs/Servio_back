package com.servio.app.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.servio.app.dto.CustomerDTO;
import com.servio.app.model.Customer;
import com.servio.app.model.CustomerState;
import com.servio.app.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerService {
	
	private final CustomerRepository customerRepository;
	
	public Page<CustomerDTO> searchCustomers(String searchParams, List<String> fields, PageRequest pageRequest){
		Page<Customer> customerPage = customerRepository.searchByFields(searchParams, fields, pageRequest);
		
		return customerPage.map(customer -> mapToDTO(customer));
	}
	
	public List<CustomerDTO> getAllCustomers(){
		List<Customer> activeCustomers = customerRepository.findByCustomerState(CustomerState.ACTIVO);
		return activeCustomers.stream()
				.map(this::mapToDTO)
				.collect(Collectors.toList());
	}
	
	public CustomerDTO createCustomer(CustomerDTO dto) {
		Customer customer = new Customer();
		customer.setName(dto.getName());
		customer.setComment(dto.getComment());
		customer.setCustomerState(CustomerState.valueOf(dto.getState()));
		Customer saved = customerRepository.save(customer);
		return mapToDTO(saved);
	}
	
	private CustomerDTO mapToDTO(Customer customer){
        return CustomerDTO.builder()
                .id(customer.getId())
                .name(customer.getName())
                .comment(customer.getComment())
                .state(customer.getCustomerState().name())
                .build();
    }
	
	public List<CustomerDTO> formatCustomerData(Page<CustomerDTO> customerPage){
		return customerPage.getContent().stream().map(this::formatCustomer).collect(Collectors.toList());
	}
	
	
	public CustomerDTO formatCustomer(CustomerDTO customerDTO) {
		return customerDTO;
	}

}
