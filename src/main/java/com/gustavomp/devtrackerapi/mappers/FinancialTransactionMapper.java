package com.gustavomp.devtrackerapi.mappers;

import com.gustavomp.devtrackerapi.dtos.requests.financialtransaction.Create_FT_RequestDto;
import com.gustavomp.devtrackerapi.dtos.responses.financialtransaction.Create_FT_ResponseDto;
import com.gustavomp.devtrackerapi.dtos.responses.financialtransaction.GetAll_FT_ByProjectIdResponseDto;
import com.gustavomp.devtrackerapi.models.FinancialTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FinancialTransactionMapper {

    // Create Financial Transaction
    FinancialTransaction toEntity(Create_FT_RequestDto dto);
    @Mapping(target = "projectId", source = "project.id")
    Create_FT_ResponseDto toCreate_FT_ResponseDto(FinancialTransaction entity);

    // Get All Financial Transaction by Project ID
    GetAll_FT_ByProjectIdResponseDto toGetAll_FT_ByProjectIdResponseDto(FinancialTransaction entity);

}
