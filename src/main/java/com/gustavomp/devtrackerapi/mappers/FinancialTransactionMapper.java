package com.gustavomp.devtrackerapi.mappers;

import com.gustavomp.devtrackerapi.dtos.requests.financialtransaction.CreateFinancialTransactionRequestDto;
import com.gustavomp.devtrackerapi.dtos.responses.financialtransaction.CreateFinancialTransactionResponseDto;
import com.gustavomp.devtrackerapi.models.FinancialTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FinancialTransactionMapper {

    // Create Financial Transaction
    FinancialTransaction toEntity(CreateFinancialTransactionRequestDto dto);
    @Mapping(target = "projectId", source = "project.id")
    CreateFinancialTransactionResponseDto toCreateFinancialTransactionResponseDto(FinancialTransaction entity);

}
