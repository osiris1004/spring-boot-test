package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.*;


//! FIRST JUNIT TEST
// @SpringBootTest //* for now we are going to comment the @SpringBootTest annotation*/
class DemoApplicationTests {

	Calculator underTest = new Calculator();

	@Test //*this tells that the below method is a test method */
	void itShouldAddTwoNumbers() {
		//!nb : give, when, then is the BDD test style, making super easy to understand the test
		//!give -> input
		int numberOne = 20;
		int numberTwo = 30;

		//!when -> result by executing the method
		int result = underTest.add(numberOne, numberTwo);

		//!then -> expected output
		int expected = 50;
		assertThat(result).isEqualTo(expected);
	}

	class Calculator{
		int add (int a, int b){
			return a+b;
		}
	}

}
