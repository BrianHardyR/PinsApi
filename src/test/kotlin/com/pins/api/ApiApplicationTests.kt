package com.pins.api

import com.pins.api.utils.clean
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder

@SpringBootTest
class ApiApplicationTests {

	@Autowired
	lateinit var passwordEncoder: PasswordEncoder



	@Test
	fun passwordEncoderTest(){

		val password = "qwertyuiop"
		val encoded = passwordEncoder.encode(password)
		val otherEncoded = passwordEncoder.encode(password)

		assert( passwordEncoder.matches(password, encoded) )
		assert( passwordEncoder.matches(password, otherEncoded) )

	}


	@Test
	fun cleanTest(){
		val text = "Brian Elias ?"
		val cleaned = text.clean()
		println(cleaned)
		assert(cleaned == "brianelias")
	}


}
