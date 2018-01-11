package com.cera.chain.entity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PageEntity {

	public final static String TOTAL = "total" ;
	public final static Long EXPIRE = 30L ;
	
	public transient Integer total ;
	public transient Integer page ;
	public transient Integer pageSize = 3000;
	public transient Integer index = 0;
	


	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}
	
	public String toUnderLine(String fieldName){
        Pattern  p=Pattern.compile("[A-Z]");  
        
        StringBuilder builder=new StringBuilder(fieldName);  
        Matcher mc=p.matcher(fieldName);  
        int i=0;  
        while(mc.find()){  
            builder.replace(mc.start()+i, mc.end()+i, "_"+mc.group().toLowerCase());  
            i++;  
        }  
        if('_' == builder.charAt(0)){  
            builder.deleteCharAt(0);  
        }  
        return builder.toString();  
	}
	
	public String toCamel(String fieldName){
		StringBuilder sb = new StringBuilder() ;
	    String camels[] = fieldName.split("_");
	    for (String camel :  camels) {
	        if (camel.isEmpty()) {
	            continue;
	        }
	        if (sb.length() == 0) {
	        	sb.append(camel.toLowerCase());
	        } else {
	        	sb.append(camel.substring(0, 1).toUpperCase());
	        	sb.append(camel.substring(1).toLowerCase());
	        }
	    }
	    return sb.toString();
	}
	
}


