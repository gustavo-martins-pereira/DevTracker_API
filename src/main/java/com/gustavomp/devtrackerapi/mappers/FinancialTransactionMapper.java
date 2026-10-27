package com.gustavomp.devtrackerapi.mappers;

import com.gustavomp.devtrackerapi.dtos.requests.financialtransaction.CreateFTRequestDto;
import com.gustavomp.devtrackerapi.dtos.responses.financialtransaction.CreateFTResponseDto;
import com.gustavomp.devtrackerapi.dtos.responses.financialtransaction.GetAllFTByProjectIdResponseDto;
import com.gustavomp.devtrackerapi.dtos.responses.financialtransaction.GetFTByIdResponseDto;
import com.gustavomp.devtrackerapi.models.FinancialTransaction;
import com.gustavomp.devtrackerapi.models.FixedPriceProject;
import com.gustavomp.devtrackerapi.models.HourlyRateProject;
import com.gustavomp.devtrackerapi.models.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.math.BigDecimal;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FinancialTransactionMapper {

    // Create Financial Transaction
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "project", ignore = true)
    FinancialTransaction toEntity(CreateFTRequestDto dto);
    @Mapping(target = "projectId", source = "project.id")
    CreateFTResponseDto toCreateFTResponseDto(FinancialTransaction entity);

    // Get Financial Transaction by ID
    @Mapping(target = "amount", source = "responseAmount")
    @Mapping(target = "projectId", source = "entity.project.id")
    @Mapping(target = "clientId", source = "entity.project.client.id")
    @Mapping(target = "projectType", source = "entity.project")
    GetFTByIdResponseDto toGetFTByIdResponseDto(FinancialTransaction entity, BigDecimal responseAmount);

    // Get Financial Transactions by Project ID
    @Mapping(target = "amount", source = "responseAmount")
    GetAllFTByProjectIdResponseDto toGetAllFTByProjectIdResponseDto(FinancialTransaction entity, BigDecimal responseAmount);

    default String mapProjectType(Project project) {
        return switch (project) {
            case null -> null;
            case FixedPriceProject _ -> "FIXED_PRICE";
            case HourlyRateProject _ -> "HOURLY_RATE";
            default ->
                    throw new IllegalArgumentException("Unsupported project subclass: " + project.getClass().getName());
        };

    }

}

