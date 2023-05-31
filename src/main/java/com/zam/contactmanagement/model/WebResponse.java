package com.zam.contactmanagement.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Pageable;

@Data
@Builder
public class WebResponse<T> {

    //always have data is can change data type
    private T data;

    private String errors;

    private PagingResponse paging;

}
