package com.enterpriseproject.enterpriseapi.APIControllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ValidationUtility {
//    private HttpServletResponse response;

    public static boolean parameterFound(HttpServletRequest request, String name){
        return request.getParameterMap().containsKey(name);
    }

    public static boolean validateParameters(HttpServletRequest request, List<String> arrayList){
        for(String name : arrayList){
            if(!request.getParameterMap().containsKey(name)){
                return false;
            }
        }
        return true;
    }

}
