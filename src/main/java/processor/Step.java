package main.java.processor;

import main.java.common.models.BaseParams;

public interface Step<T extends BaseParams> {
    
    public String execute(String previousOutput, T step);

}
