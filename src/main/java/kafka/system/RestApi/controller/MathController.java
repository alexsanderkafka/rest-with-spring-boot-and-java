package kafka.system.RestApi.controller;

import kafka.system.RestApi.exceptions.UnsupportedMathOperationException;
import kafka.system.RestApi.converters.NumberConverter;
import kafka.system.RestApi.math.Calculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/math")
public class MathController {

    @Autowired
    private NumberConverter convert;

    @Autowired
    private Calculator calculator;

    @GetMapping(value = "/sum/{numberOne}/{numberTwo}")
    public Double sum(@PathVariable(value = "numberOne") String numberOne,
                      @PathVariable(value = "numberTwo") String numberTwo) throws Exception {

        if(!convert.isNumeric(numberOne) || !convert.isNumeric(numberTwo)){
            throw new UnsupportedMathOperationException("Please, set a numeric value");
        }

        return calculator.sum(convert.convertToDouble(numberOne), convert.convertToDouble(numberTwo));
    }

    @GetMapping(value = "/sub/{numberOne}/{numberTwo}")
    public Double sub(@PathVariable(value = "numberOne") String numberOne,
                      @PathVariable(value = "numberTwo") String numberTwo) throws Exception {

        if(!convert.isNumeric(numberOne) || !convert.isNumeric(numberTwo)){
            throw new UnsupportedMathOperationException("Please, set a numeric value");
        }

        return calculator.subtraction(convert.convertToDouble(numberOne), convert.convertToDouble(numberTwo));
    }

    @GetMapping(value = "/mult/{numberOne}/{numberTwo}")
    public Double mult(@PathVariable(value = "numberOne") String numberOne,
                      @PathVariable(value = "numberTwo") String numberTwo) throws Exception {

        if(!convert.isNumeric(numberOne) || !convert.isNumeric(numberTwo)){
            throw new UnsupportedMathOperationException("Please, set a numeric value");
        }

        return calculator.multiplication(convert.convertToDouble(numberOne), convert.convertToDouble(numberTwo));
    }

    @GetMapping(value = "/div/{numberOne}/{numberTwo}")
    public Double div(@PathVariable(value = "numberOne") String numberOne,
                      @PathVariable(value = "numberTwo") String numberTwo) throws Exception {

        if(!convert.isNumeric(numberOne) || !convert.isNumeric(numberTwo)){
            throw new UnsupportedMathOperationException("Please, set a numeric value");
        }

        return calculator.division(convert.convertToDouble(numberOne), convert.convertToDouble(numberTwo));
    }

    @GetMapping(value = "/avg/{numberOne}/{numberTwo}")
    public Double avg(@PathVariable(value = "numberOne") String numberOne,
                      @PathVariable(value = "numberTwo") String numberTwo) throws Exception {

        if(!convert.isNumeric(numberOne) || !convert.isNumeric(numberTwo)){
            throw new UnsupportedMathOperationException("Please, set a numeric value");
        }

        return calculator.avarage(convert.convertToDouble(numberOne), convert.convertToDouble(numberTwo));
    }

    @GetMapping(value = "/sqrt/{number}")
    public Double sqrt(@PathVariable(value = "number") String number) throws Exception {

        if(!convert.isNumeric(number)){
            throw new UnsupportedMathOperationException("Please, set a numeric value");
        }

        return calculator.squareRoot(convert.convertToDouble(number));
    }

}
