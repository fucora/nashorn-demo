package com.experiments.crypto;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;

public class NashornHelper {

   public static void main(String[] args) throws Exception {
      ObjectMapper mapper = new ObjectMapper();
      mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
      mapper.setDateFormat(new StdDateFormat().withColonInTimeZone(true));
      String jsonString = mapper
         .writeValueAsString(new Pojo("hariram",
                                      BigDecimal.valueOf(300000.55),
                                      new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                                         .parse("1980-05-23 18:30:05")));
      System.out.println("POJO : " + jsonString);
      ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
      engine.getContext().setAttribute("jsonStr", jsonString, ScriptContext.ENGINE_SCOPE);
      StringBuilder ruleBuilder = new StringBuilder();
      // Need to convert the POJO's stringified JSON version into a proper JSON object
      // inside the script engine runtime.
      ruleBuilder.append("var o = JSON.parse(jsonStr);");
      ruleBuilder.append("\n");
      // Here's the actual rule
      ruleBuilder
         .append("o.name.startsWith('hari') && o.salary > 300000 && new Date(o.doj).getTime() > new Date('1980-01-01T18:30:00')");
      String rule = ruleBuilder.toString();
      System.out.println("RULE : " + rule);
      Object resp = engine.eval(rule);
      System.out.println("EXECUTION RESULT : " + resp);
   }
}

class Pojo {

   private String     name;

   private BigDecimal salary;

   private Date       doj;

   public Pojo() {}

   public Pojo(String name, BigDecimal salary, Date doj) {
      super();
      this.name = name;
      this.salary = salary;
      this.doj = doj;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public BigDecimal getSalary() {
      return salary;
   }

   public void setSalary(BigDecimal salary) {
      this.salary = salary;
   }

   public Date getDoj() {
      return doj;
   }

   public void setDoj(Date doj) {
      this.doj = doj;
   }
}

/* Ouput */

// POJO : {"name":"hariram","salary":300000.55,"doj":"1980-05-23T13:00:05.000+00:00"}
// RULE : var o = JSON.parse(jsonStr);
// o.name.startsWith('hari') && o.salary > 300000 && new Date(o.doj).getTime() > new Date('1980-01-01T18:30:00')
// EXECUTION RESULT : true
