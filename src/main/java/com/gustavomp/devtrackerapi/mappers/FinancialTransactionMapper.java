package com.gustavomp.devtrackerapi.mappers;

import com.gustavomp.devtrackerapi.dtos.requests.financialtransaction.Create_FT_RequestDto;
import com.gustavomp.devtrackerapi.dtos.responses.financialtransaction.Create_FT_ResponseDto;
import com.gustavomp.devtrackerapi.dtos.responses.financialtransaction.GetAll_FT_ByProjectIdResponseDto;
import com.gustavomp.devtrackerapi.dtos.responses.financialtransaction.Get_FT_ByIdResponseDto;
import com.gustavomp.devtrackerapi.models.FinancialTransaction;
import com.gustavomp.devtrackerapi.models.FixedPriceProject;
import com.gustavomp.devtrackerapi.models.HourlyRateProject;
import com.gustavomp.devtrackerapi.models.Project;
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

    // Get Financial Transaction by ID
    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "clientId", source = "project.client.id")
    @Mapping(target = "projectType", source = "project")
    Get_FT_ByIdResponseDto toGet_FT_ByIdResponseDto(FinancialTransaction entity);

    default String mapProjectType(Project project) {
        if (project == null) return null;

        if (project instanceof FixedPriceProject) return "FIXED_PRICE";

        if (project instanceof HourlyRateProject) return "HOURLY_RATE";

        throw new IllegalArgumentException("Unsupported project subclass: " + project.getClass().getName());
    }

}
