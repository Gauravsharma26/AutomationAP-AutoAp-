package com.gaurav.autoap.model;

import java.util.List;

public record ValidationResult (
    boolean poMatch,
    boolean duplicate,
    List<String> issues){

}
