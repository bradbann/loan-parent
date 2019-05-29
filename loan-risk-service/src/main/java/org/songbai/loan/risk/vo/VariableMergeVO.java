package org.songbai.loan.risk.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VariableMergeVO {
    String userId;
    String sources;

    Set<Integer> catalogs;
}
