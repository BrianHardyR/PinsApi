package com.pins.api

import com.pins.api.entities.Credential
import com.pins.api.entities.UserModel
import com.pins.api.repo.UserRepo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ApiApplicationTests {

	@Autowired
	lateinit var userRepo : UserRepo

	@Test
	fun contextLoads() {
		assert(userRepo != null)
	}

	@Test
	fun saveUser(){
		val user = userRepo.saveAll(
				listOf(
						UserModel(
								userName = "testuser1",
								name = "Test1",
								otherNames = listOf("Middle","Last"),
								credentials = listOf(
										Credential(
												identifier = "testuser1@email.com",
												secret = "password"
										)
								)
						),
						UserModel(
								userName = "testuser2",
								name = "Test2",
								otherNames = listOf("Middle","Last"),
								credentials = listOf(
										Credential(
												identifier = "testuser2@email.com",
												secret = "password"
										)
								)
						),
						UserModel(
								userName = "testuser3",
								name = "Test3",
								otherNames = listOf("Middle","Last"),
								credentials = listOf(
										Credential(
												identifier = "testuser3@email.com",
												secret = "password"
										)
								)
						)
				)
		)
		System.out.print(user.toString())
	}

	@Test
	fun findUserByID(){
		val user = userRepo.findById(1)
		assert(user.isPresent)
	}

}
