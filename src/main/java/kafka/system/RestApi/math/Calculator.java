package kafka.system.RestApi.math;

import org.springframework.stereotype.Component;

@Component
public class Calculator {

    public Double sum(Double numberOne, Double numberTwo){
        return numberOne + numberTwo;
    }

    public Double subtraction(Double numberOne, Double numberTwo){
        return numberOne - numberTwo;
    }

    public Double multiplication(Double numberOne, Double numberTwo){
        return numberOne * numberTwo;
    }

    public Double division(Double numberOne, Double numberTwo){
        return numberOne / numberTwo;
    }

    public Double squareRoot(Double number){
        return Math.sqrt(number);
    }

    public Double avarage(Double numberOne, Double numberTwo){
        return (numberOne + numberTwo) / 2;
    }

}
