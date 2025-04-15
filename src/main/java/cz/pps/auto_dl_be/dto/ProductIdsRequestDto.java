package cz.pps.auto_dl_be.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ProductIdsRequestDto {
    private List<String> ids;
}